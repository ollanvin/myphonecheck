package app.myphonecheck.mobile.debug

import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import app.myphonecheck.mobile.core.util.PhoneNumberNormalizer
import app.myphonecheck.mobile.feature.callintercept.DeviceEvidenceProvider
import app.myphonecheck.mobile.feature.callintercept.SearchEvidenceProvider
import app.myphonecheck.mobile.feature.callintercept.presentation.OverlayPresenter
import app.myphonecheck.mobile.feature.countryconfig.CountryConfigProvider
import app.myphonecheck.mobile.feature.countryconfig.SupportedLanguage
import app.myphonecheck.mobile.feature.decisionengine.DecisionEngine
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

/**
 * v4.3 DEBUG-ONLY — 실제 파이프라인으로 오버레이를 검증하는 Activity.
 *
 * src/debug/ source set → release 빌드에 절대 포함되지 않음.
 *
 * 실행:
 *   adb shell am start -n app.myphonecheck.mobile/app.myphonecheck.mobile.debug.DebugSearchActivity --es number "+821012345678"
 *
 * 파이프라인:
 *   1. DeviceEvidenceProvider.gather()  → MPC_INTERNAL
 *   2. SearchEvidenceProvider.gather()  → MPC_EXTERNAL
 *   3. DecisionEngine.evaluate()        → MPC_DECISION
 *   4. OverlayPresenter.present()       → MPC_OVERLAY_BIND
 *
 * 검증:
 *   adb logcat -d | findstr "MPC_INTERNAL MPC_EXTERNAL MPC_DECISION MPC_OVERLAY_BIND MPC_OVERLAY"
 */
@AndroidEntryPoint
class DebugSearchActivity : ComponentActivity() {

    @Inject lateinit var deviceEvidenceProvider: DeviceEvidenceProvider
    @Inject lateinit var searchEvidenceProvider: SearchEvidenceProvider
    @Inject lateinit var decisionEngine: DecisionEngine
    @Inject lateinit var overlayPresenter: OverlayPresenter
    @Inject lateinit var countryConfigProvider: CountryConfigProvider

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rawNumber = intent.getStringExtra("number")
        if (rawNumber.isNullOrBlank()) {
            Log.e(TAG, "number extra missing — usage: adb shell am start ... --es number \"+821012345678\"")
            Toast.makeText(this, "[DEBUG] --es number 필수", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        if (!Settings.canDrawOverlays(this)) {
            Log.w(MPC_OVERLAY_BIND, "BLOCKED permission=SYSTEM_ALERT_WINDOW")
            Toast.makeText(this, "[DEBUG] SYSTEM_ALERT_WINDOW 권한 필요", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        Log.i(TAG, "DEBUG search pipeline START number=$rawNumber")
        runPipeline(rawNumber)
    }

    private fun runPipeline(rawNumber: String) {
        scope.launch {
            try {
                // ── 0. 번호 정규화 ──
                val country = countryConfigProvider.detectCountry(applicationContext)
                val normalized = PhoneNumberNormalizer.formatE164(rawNumber, country) ?: rawNumber
                Log.i(TAG, "Normalized: $normalized (country=$country)")

                // ── 1. 내부검색 (DeviceEvidence) ──
                val deviceEvidence = withTimeoutOrNull(3_000L) {
                    try {
                        deviceEvidenceProvider.gather(normalized)
                    } catch (e: Exception) {
                        Log.e(MPC_INTERNAL, "FAIL error=${e.message}")
                        null
                    }
                }
                if (deviceEvidence != null) {
                    Log.i(MPC_INTERNAL, "OK contact=${deviceEvidence.isSavedContact}" +
                        " name=${deviceEvidence.contactName}" +
                        " incoming=${deviceEvidence.incomingCount}" +
                        " outgoing=${deviceEvidence.outgoingCount}" +
                        " connected=${deviceEvidence.connectedCount}" +
                        " rejected=${deviceEvidence.rejectedCount}" +
                        " sms=${deviceEvidence.smsExists}" +
                        " hasHistory=${deviceEvidence.hasAnyHistory}")
                } else {
                    Log.w(MPC_INTERNAL, "NULL (timeout or error)")
                }

                // ── 2. 외부검색 (SearchEvidence) ──
                val searchEvidence = withTimeoutOrNull(8_000L) {
                    try {
                        searchEvidenceProvider.gather(normalized, country)
                    } catch (e: Exception) {
                        Log.e(MPC_EXTERNAL, "FAIL error=${e.message}")
                        null
                    }
                }
                if (searchEvidence != null) {
                    Log.i(MPC_EXTERNAL, "OK empty=${searchEvidence.isEmpty}" +
                        " keywords=${searchEvidence.keywordClusters}" +
                        " entities=${searchEvidence.repeatedEntities}" +
                        " snippets=${searchEvidence.topSnippets.size}" +
                        " signals=${searchEvidence.signalSummaries.size}" +
                        " scam=${searchEvidence.hasScamSignal}" +
                        " spam=${searchEvidence.hasSpamSignal}" +
                        " delivery=${searchEvidence.hasDeliverySignal}" +
                        " institution=${searchEvidence.hasInstitutionSignal}" +
                        " business=${searchEvidence.hasBusinessSignal}" +
                        " trend=${searchEvidence.searchTrend}" +
                        " 30d=${searchEvidence.recent30dSearchIntensity}")
                    searchEvidence.signalSummaries.forEachIndexed { i, s ->
                        Log.i(MPC_EXTERNAL, "  signal[$i] type=${s.signalType}" +
                            " desc=${s.signalDescription}" +
                            " count=${s.resultCount}" +
                            " snippet=${s.topSnippet}")
                    }
                    searchEvidence.adjacentNumberHint?.let { hint ->
                        Log.i(MPC_EXTERNAL, "  adjacent entity=${hint.matchedEntity}" +
                            " count=${hint.resultCount}" +
                            " range=${hint.rangeDescription}")
                    }
                } else {
                    Log.w(MPC_EXTERNAL, "NULL (timeout or error)")
                }

                // ── 3. 판단엔진 ──
                val result = decisionEngine.evaluate(
                    deviceEvidence = deviceEvidence,
                    searchEvidence = searchEvidence,
                )
                Log.i(MPC_DECISION, "OK risk=${result.riskLevel}" +
                    " category=${result.category}" +
                    " action=${result.action}" +
                    " confidence=${result.confidence}" +
                    " summary=${result.summary}" +
                    " reasons=${result.reasons}" +
                    " importance=${result.importanceLevel}")

                // ── 4. Overlay 바인딩 ──
                // 헌법 §9-1: 영문 단일. 다국어 표시는 OS Locale + ICU 가 처리.
                val overlayLang = SupportedLanguage.EN

                overlayPresenter.present(
                    context = applicationContext,
                    result = result,
                    phoneNumber = normalized,
                    language = overlayLang,
                )
                Log.i(MPC_OVERLAY_BIND, "PRESENT number=$normalized" +
                    " risk=${result.riskLevel}" +
                    " category=${result.category}" +
                    " action=${result.action}" +
                    " summary=${result.summary}")

                Log.i(TAG, "DEBUG search pipeline COMPLETE")

            } catch (e: Exception) {
                Log.e(TAG, "Pipeline FATAL error", e)
            }

            // 5초 후 자동 종료 (오버레이는 유지)
            window.decorView.postDelayed({ finish() }, 5_000)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    private companion object {
        const val TAG = "DebugSearchActivity"
        const val MPC_INTERNAL = "MPC_INTERNAL"
        const val MPC_EXTERNAL = "MPC_EXTERNAL"
        const val MPC_DECISION = "MPC_DECISION"
        const val MPC_OVERLAY_BIND = "MPC_OVERLAY_BIND"
    }
}
