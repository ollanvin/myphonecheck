package app.callcheck.mobile.feature.callintercept

import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import app.callcheck.mobile.core.model.DecisionResult
import app.callcheck.mobile.core.model.NumberSourceContext
import app.callcheck.mobile.core.model.PhoneNumberContext
import app.callcheck.mobile.core.util.PhoneNumberContextBuilder
import app.callcheck.mobile.data.contacts.ContactsDataSource
import app.callcheck.mobile.feature.countryconfig.CountryConfigProvider
import app.callcheck.mobile.feature.countryconfig.LanguageContextProvider
import app.callcheck.mobile.feature.countryconfig.SignalSummaryLocalizer
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.TimeoutCancellationException

private const val TAG = "CallCheckScreening"
private const val SCREENING_TIMEOUT_MS = 4500L
private val EMERGENCY_NUMBERS = setOf("911", "112", "119", "110", "999")

/**
 * Android CallScreeningService implementation.
 *
 * 핵심 원칙:
 * 1. 저장된 연락처 → 즉시 패스 (판정/알림/오버레이 일절 없음)
 * 2. 미저장 번호만 → 판정 → 오버레이(ringing 중 즉시 인지) + Notification(fallback)
 * 3. v1.0: 자동 차단/거절 없음. 항상 ALLOW. 사용자가 결정.
 *
 * 글로벌 대응 (기기 컨텍스트 동기화):
 * - PhoneNumberContextBuilder로 번호 문맥 구성 (raw 보존, canonical/variants 생성)
 * - CountryConfigProvider로 기기 국가 탐지
 * - LanguageContextProvider로 기기 언어 결정
 * - SignalSummaryLocalizer로 사용자 대면 텍스트 로컬라이즈
 *
 * Uses @EntryPoint pattern — Telecom framework instantiates this service.
 */
class CallCheckScreeningService : CallScreeningService() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface ScreeningServiceEntryPoint {
        fun callInterceptRepository(): CallInterceptRepository
        fun decisionNotificationManager(): DecisionNotificationManager
        fun callerIdOverlayManager(): CallerIdOverlayManager
        fun contactsDataSource(): ContactsDataSource
        fun countryConfigProvider(): CountryConfigProvider
        fun languageContextProvider(): LanguageContextProvider
    }

    private lateinit var callInterceptRepository: CallInterceptRepository
    private lateinit var decisionNotificationManager: DecisionNotificationManager
    private lateinit var callerIdOverlayManager: CallerIdOverlayManager
    private lateinit var contactsDataSource: ContactsDataSource
    private lateinit var countryConfigProvider: CountryConfigProvider
    private lateinit var languageContextProvider: LanguageContextProvider

    private val phoneNumberContextBuilder = PhoneNumberContextBuilder()
    private val signalSummaryLocalizer = SignalSummaryLocalizer()
    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        try {
            val entryPoint = EntryPointAccessors.fromApplication(
                applicationContext, ScreeningServiceEntryPoint::class.java
            )
            callInterceptRepository = entryPoint.callInterceptRepository()
            decisionNotificationManager = entryPoint.decisionNotificationManager()
            callerIdOverlayManager = entryPoint.callerIdOverlayManager()
            contactsDataSource = entryPoint.contactsDataSource()
            countryConfigProvider = entryPoint.countryConfigProvider()
            languageContextProvider = entryPoint.languageContextProvider()
            Log.i(TAG, "onCreate: dependencies injected via EntryPoint")
        } catch (e: Exception) {
            Log.e(TAG, "onCreate: DI failed", e)
        }
    }

    override fun onScreenCall(callDetails: Call.Details) {
        Log.i(TAG, "onScreenCall invoked")
        try {
            // DI failure guard
            if (!::callInterceptRepository.isInitialized ||
                !::decisionNotificationManager.isInitialized ||
                !::callerIdOverlayManager.isInitialized ||
                !::contactsDataSource.isInitialized ||
                !::countryConfigProvider.isInitialized ||
                !::languageContextProvider.isInitialized
            ) {
                Log.e(TAG, "Dependencies not initialized, allowing call")
                respondAllow(callDetails)
                return
            }

            val rawNumber = extractPhoneNumber(callDetails)

            if (rawNumber.isNullOrBlank()) {
                Log.w(TAG, "Null/blank phone number, allowing")
                respondAllow(callDetails)
                return
            }

            val cleanNumber = rawNumber.replace(Regex("[^\\d+]"), "")

            // Emergency numbers: always allow, no processing
            if (cleanNumber in EMERGENCY_NUMBERS) {
                Log.i(TAG, "Emergency number: $cleanNumber")
                respondAllow(callDetails)
                return
            }

            // Private/blocked caller
            if (isPrivateNumber(rawNumber)) {
                Log.i(TAG, "Private caller detected")
                respondAllow(callDetails)
                return
            }

            assessThenAllow(callDetails, rawNumber)
        } catch (e: Exception) {
            Log.e(TAG, "onScreenCall error", e)
            respondAllow(callDetails)
        }
    }

    /**
     * Core flow (글로벌 대응):
     *   1. PhoneNumberContextBuilder로 번호 문맥 구성 (raw 보존)
     *   2. 연락처 확인 → 저장된 번호면 즉시 패스
     *   3. 미저장 번호 → 판정 파이프라인 실행
     *   4. 오버레이 표시 (ringing 중 즉시 인지, 기기 언어 기반 로컬라이즈)
     *   5. Notification 표시 (오버레이 실패 시 fallback)
     *   6. respondAllow() LAST
     */
    private fun assessThenAllow(callDetails: Call.Details, rawNumber: String) {
        serviceScope.launch {
            try {
                // ── Step 1: 번호 문맥 구성 ──
                // rawNumber는 기기 원본 그대로 보존.
                // deviceCountryCode는 기기(SIM/Network/Locale)에서 자동 탐지.
                val deviceCountryCode = countryConfigProvider.detectCountry(applicationContext)
                val phoneContext = phoneNumberContextBuilder.build(
                    rawNumber = rawNumber,
                    deviceCountryCode = deviceCountryCode,
                    sourceContext = NumberSourceContext.INCOMING_CALL,
                )

                Log.i(
                    TAG,
                    "PhoneContext: raw=${phoneContext.rawNumber}, " +
                            "canonical=${phoneContext.deviceCanonicalNumber}, " +
                            "country=$deviceCountryCode, " +
                            "parseable=${phoneContext.isParseable}, " +
                            "variants=${phoneContext.searchVariants.size}"
                )

                // 비교/연락처 확인에는 canonicalNumber 사용
                val canonicalNumber = phoneContext.deviceCanonicalNumber

                // ── Step 2: 저장된 연락처 확인 ──
                // 저장된 번호는 CallCheck가 관여하지 않음.
                // 판정/알림/오버레이 일절 없이 즉시 통과.
                val isSaved = try {
                    contactsDataSource.isContactSaved(canonicalNumber)
                } catch (e: Exception) {
                    Log.w(TAG, "Contact check failed, proceeding with assessment", e)
                    false
                }

                if (isSaved) {
                    Log.i(TAG, "SAVED_CONTACT: $canonicalNumber → 즉시 패스 (판정 없음)")
                    respondAllow(callDetails)
                    return@launch
                }

                // ── Step 3: 미저장 번호 → 판정 파이프라인 ──
                Log.i(TAG, "UNSAVED_NUMBER: $canonicalNumber → 판정 시작")

                val result = withTimeout(SCREENING_TIMEOUT_MS) {
                    callInterceptRepository.processIncomingCall(
                        normalizedNumber = canonicalNumber,
                        deviceCountryCode = deviceCountryCode,
                    )
                }

                Log.i(
                    TAG,
                    "Assessment: ${result.category} / ${result.action} / " +
                            "risk=${result.riskLevel} / confidence=${(result.confidence * 100).toInt()}%"
                )

                // ── Step 4: 언어 결정 + 오버레이 표시 ──
                // 번호는 raw 형식 유지, 의미 문구만 locale에 맞게 변환
                val currentLanguage = languageContextProvider.resolveLanguage()

                val overlayShown = callerIdOverlayManager.showOverlay(
                    context = applicationContext,
                    result = result,
                    phoneNumber = phoneContext.rawNumber,
                    language = currentLanguage,
                    localizer = signalSummaryLocalizer,
                )

                // ── Step 5: Notification 표시 ──
                // 오버레이가 성공하면 Notification은 불필요 (중복 표시 방지).
                // 오버레이 실패 시에만 Notification을 fallback으로 표시.
                if (!overlayShown) {
                    decisionNotificationManager.showDecisionNotification(
                        context = applicationContext,
                        result = result,
                        phoneNumber = phoneContext.rawNumber,
                        language = currentLanguage,
                        localizer = signalSummaryLocalizer,
                    )
                    Log.i(TAG, "Overlay failed → Notification fallback shown")
                } else {
                    Log.i(TAG, "Overlay shown → Notification skipped (중복 방지)")
                }

                // ── Step 6: ALLOW (v1.0: 자동 차단 없음) ──
                respondAllow(callDetails)
                Log.i(TAG, "Call ALLOWED for: ${phoneContext.rawNumber}")

            } catch (e: TimeoutCancellationException) {
                Log.w(TAG, "Pipeline timeout (${SCREENING_TIMEOUT_MS}ms)")
                decisionNotificationManager.showTimeoutNotification(
                    context = applicationContext,
                    phoneNumber = rawNumber,
                    language = languageContextProvider.resolveLanguage(),
                    localizer = signalSummaryLocalizer,
                )
                respondAllow(callDetails)
            } catch (e: Exception) {
                Log.e(TAG, "assessThenAllow error", e)
                respondAllow(callDetails)
            }
        }
    }

    private fun respondAllow(callDetails: Call.Details) {
        try {
            val response = CallResponse.Builder()
                .setDisallowCall(false)
                .setSkipCallLog(false)
                .setSkipNotification(false)
                .build()
            respondToCall(callDetails, response)
        } catch (e: Exception) {
            Log.e(TAG, "respondAllow error", e)
        }
    }

    private fun extractPhoneNumber(callDetails: Call.Details): String? {
        return try {
            callDetails.handle?.schemeSpecificPart?.takeIf { it.isNotEmpty() }
        } catch (e: Exception) {
            Log.w(TAG, "Extract phone number error", e)
            null
        }
    }

    private fun isPrivateNumber(phoneNumber: String): Boolean {
        val lower = phoneNumber.lowercase()
        return lower.contains("private") ||
                lower.contains("blocked") ||
                lower.contains("unknown") ||
                phoneNumber.trim() == "*67"
    }

    override fun onDestroy() {
        super.onDestroy()
        // Overlay는 전화 종료 시까지 유지해야 하므로 여기서 dismiss 하지 않음.
        // PhoneStateListener 또는 별도 타이밍으로 dismiss 처리 필요.
        serviceScope.cancel()
    }
}
