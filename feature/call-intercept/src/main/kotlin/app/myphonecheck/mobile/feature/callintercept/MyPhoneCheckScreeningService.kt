package app.myphonecheck.mobile.feature.callintercept

import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import app.myphonecheck.mobile.core.model.IdentifierAnalysisInput
import app.myphonecheck.mobile.core.model.IdentifierChannel
import app.myphonecheck.mobile.core.util.PhoneNumberNormalizer
import app.myphonecheck.mobile.data.localcache.repository.NumberProfileRepository
import app.myphonecheck.mobile.data.localcache.repository.UserCallRecordRepository
import app.myphonecheck.mobile.feature.countryconfig.CountryConfigProvider
import app.myphonecheck.mobile.feature.countryconfig.SupportedLanguage
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.cancel

private const val TAG = "MyPhoneCheckScreening"
private const val MPC_SCREEN = "MPC_SCREEN"
private const val SCREENING_TIMEOUT_MS = 4500L

/**
 * 긴급번호 목록.
 * 이 번호들은 판정, 알림, 오버레이 모두 완전 스킵.
 * 사용자 안전 최우선 — 어떤 지연도 허용하지 않음.
 */
private val EMERGENCY_NUMBERS = setOf("911", "112", "119", "110", "999")

/**
 * UI 정책 상수.
 *
 * SKIP_UI_COMPLETELY: 판정 스킵 시 알림/오버레이도 완전 스킵
 * - 긴급번호: 판정 0ms, 알림 없음, 오버레이 없음
 * - Private/Blocked: 판정 0ms, 알림 없음, 오버레이 없음
 * - Null/Blank: 판정 0ms, 알림 없음, 오버레이 없음
 *
 * 이 정책은 MyPhoneCheckScreeningService의 early return 구조로 보장됨:
 * respondAllow() 후 즉시 return → assessThenAllow()(Notification 발행)에 도달하지 않음
 */
@Suppress("unused")
private const val SKIP_UI_COMPLETELY = true

/**
 * Android CallScreeningService implementation.
 *
 * v1.1 DEPRECATED: PhoneStateListener(ForcePhoneListener)로 전환 예정.
 * CallScreeningService는 기본 전화 앱 설정이 필요하여 UX 마찰이 큼.
 * 전환 완료 시 이 클래스 제거.
 *
 * 제품 철학: MyPhoneCheck는 행동 대행 앱이 아니라 행동 결정 보조 앱입니다.
 *
 * 동작 원칙:
 * - 모든 전화를 ALLOW (전화를 울리게 함)
 * - 엔진 판정 결과를 Notification으로 즉시 노출
 * - 최종 행동(수신/거절/차단)은 사용자가 직접 결정
 *
 * 국가 감지:
 * - onCreate 시 CountryConfigProvider.detectCountry()로 디바이스 국가 감지
 * - SIM → Network → Locale 순서 (CountryConfigProvider 내부 로직)
 * - 감지된 국가를 PhoneNumberNormalizer에 전달하여 로컬 번호 정규화
 * - 감지 실패 시 "ZZ" → 국제 포맷(+시작)만 파싱 가능 (안전 실패)
 *
 * 기술 제약:
 * - respondToCall() 호출 후 시스템이 즉시 onDestroy() 호출
 * - 따라서 판정을 먼저 완료한 뒤 respondToCall()과 Notification을 함께 발행
 * - coroutine 내에서 respondToCall()을 호출해야 판정 완료 후 응답 가능
 *
 * 예외:
 * - 긴급번호(911, 119 등): 판정 없이 즉시 ALLOW
 * - 시스템 에러/타임아웃: fail-safe ALLOW
 */
@Deprecated("v1.1: PhoneStateListener(ForcePhoneListener)로 전환 예정")
class MyPhoneCheckScreeningService : CallScreeningService() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface ScreeningServiceEntryPoint {
        fun callInterceptRepository(): CallInterceptRepository
        fun callerIdOverlayManager(): CallerIdOverlayManager
        fun countryConfigProvider(): CountryConfigProvider
        fun numberProfileRepository(): NumberProfileRepository
        fun userCallRecordRepository(): UserCallRecordRepository
        fun overlayPresenter(): app.myphonecheck.mobile.feature.callintercept.presentation.OverlayPresenter
    }

    private lateinit var callInterceptRepository: CallInterceptRepository
    private lateinit var callerIdOverlayManager: CallerIdOverlayManager
    private lateinit var countryConfigProvider: CountryConfigProvider
    private lateinit var numberProfileRepository: NumberProfileRepository
    private lateinit var userCallRecordRepository: UserCallRecordRepository
    private lateinit var overlayPresenter: app.myphonecheck.mobile.feature.callintercept.presentation.OverlayPresenter
    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    /**
     * 디바이스 국가 코드.
     * onCreate 시점에 SIM → Network → Locale 순으로 감지.
     * 감지 실패 시 "ZZ" (unknown) → E.164 국제 포맷만 파싱 가능.
     */
    private var deviceCountry: String = "ZZ"

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate: initializing dependencies via EntryPoint")
        try {
            val entryPoint = EntryPointAccessors.fromApplication(
                applicationContext,
                ScreeningServiceEntryPoint::class.java,
            )
            callInterceptRepository = entryPoint.callInterceptRepository()
            callerIdOverlayManager = entryPoint.callerIdOverlayManager()
            countryConfigProvider = entryPoint.countryConfigProvider()
            numberProfileRepository = entryPoint.numberProfileRepository()
            userCallRecordRepository = entryPoint.userCallRecordRepository()
            overlayPresenter = entryPoint.overlayPresenter()

            // 디바이스 국가 감지 (SIM → Network → Locale)
            deviceCountry = countryConfigProvider.detectCountry(applicationContext)
            Log.i(TAG, "onCreate: dependencies injected, deviceCountry=$deviceCountry")
        } catch (e: Exception) {
            Log.e(TAG, "onCreate: dependency injection failed", e)
        }
    }

    override fun onScreenCall(callDetails: Call.Details) {
        Log.i(TAG, "onScreenCall invoked")
        Log.i(MPC_SCREEN, "ENTER onScreenCall")
        try {
            val rawNumber = extractPhoneNumber(callDetails)

            // null/blank → ALLOW + 판정/알림/오버레이 완전 스킵
            if (rawNumber.isNullOrBlank()) {
                Log.w(TAG, "Null/blank phone number — ALLOW, no notification, no overlay")
                respondAllow(callDetails)
                return
            }

            val cleanNumber = rawNumber.replace(Regex("[^\\d+]"), "")

            // 긴급번호 → 즉시 ALLOW + 판정/알림/오버레이 완전 스킵
            // 사용자 안전 최우선: 0ms 지연, 어떤 UI도 표시하지 않음
            if (cleanNumber in EMERGENCY_NUMBERS) {
                Log.d(TAG, "Emergency number: $cleanNumber — immediate ALLOW, no notification, no overlay")
                respondAllow(callDetails)
                return
            }

            // Private/blocked → ALLOW + 판정/알림/오버레이 완전 스킵
            // 비공개 번호는 정규화 불가 → 판정 무의미 → 완전 무개입
            if (isPrivateNumber(rawNumber)) {
                Log.d(TAG, "Private caller — ALLOW, no notification, no overlay")
                respondAllow(callDetails)
                return
            }

            // DI 실패 시 → fail-safe ALLOW
            if (!::callInterceptRepository.isInitialized) {
                Log.e(TAG, "Repository not initialized — fail-safe ALLOW")
                respondAllow(callDetails)
                return
            }

            // 일반 번호: 판정 수행 → ALLOW + Notification
            assessThenAllow(callDetails, rawNumber)

        } catch (e: Exception) {
            Log.e(TAG, "onScreenCall error — fail-safe ALLOW", e)
            respondAllow(callDetails)
        }
    }

    /**
     * 판정 완료 → ALLOW 응답 → Notification 노출.
     *
     * 순서가 중요합니다:
     * 1. 엔진 판정 수행 (비동기, 최대 4.5초)
     * 2. respondAllow() (시스템에 ALLOW 응답)
     * 3. Notification 발행 (판단 재료 노출)
     *
     * 번호 정규화 시 deviceCountry를 전달하여 로컬 번호도 올바르게 E.164 변환.
     *
     * ZZ fallback 보완:
     * - deviceCountry가 "ZZ"(SIM/Network/Locale 모두 실패)일 때
     * - 번호가 +로 시작하면 libphonenumber로 국가 추정
     * - 추정 성공 시 해당 국가로 정규화
     * - 추정 실패 시 rawNumber 그대로 사용 (UNKNOWN 판정)
     */
    private fun assessThenAllow(callDetails: Call.Details, rawNumber: String) {
        serviceScope.launch {
            // ZZ fallback: deviceCountry 감지 실패 시 번호 prefix에서 국가 추정
            // try 블록 밖에 선언하여 catch(timeout)에서도 접근 가능
            val effectiveCountry = if (deviceCountry == "ZZ") {
                val inferred = PhoneNumberNormalizer.inferCountryFromNumber(rawNumber)
                if (inferred != null) {
                    Log.i(TAG, "ZZ fallback: inferred country=$inferred from number prefix")
                }
                inferred ?: "ZZ"
            } else {
                deviceCountry
            }

            try {
                // effectiveCountry를 전달하여 로컬 번호 정규화
                val normalizedNumber = PhoneNumberNormalizer.formatE164(rawNumber, effectiveCountry)
                    ?: rawNumber
                callerIdOverlayManager.rememberPostCallNumber(normalizedNumber)
                if (::numberProfileRepository.isInitialized) {
                    numberProfileRepository.touchCallInteraction(normalizedNumber)
                }
                val profileActionState = if (::numberProfileRepository.isInitialized) {
                    try {
                        numberProfileRepository.getSnapshot(normalizedNumber)?.actionState
                    } catch (_: Exception) {
                        null
                    }
                } else {
                    null
                }

                // ═══════════════════════════════════════════════
                // 2-Phase UX: Phase 1 즉시 표시 → Phase 2 확정 업데이트
                // ═══════════════════════════════════════════════
                val twoPhase = withTimeout(SCREENING_TIMEOUT_MS) {
                    callInterceptRepository.analyzeIdentifierTwoPhase(
                        IdentifierAnalysisInput(
                            normalizedNumber = normalizedNumber,
                            deviceCountryCode = effectiveCountry,
                            channel = IdentifierChannel.CALL,
                            actionState = profileActionState,
                        ),
                    )
                }

                val finalResult = twoPhase.finalResult()
                val hasPhase2 = twoPhase.phase2 != null
                val meta = twoPhase.phaseMeta

                Log.i(TAG, "2-Phase: p1=${twoPhase.phase1.action}(${meta.phase1LatencyMs}ms) " +
                    "p2=${twoPhase.phase2?.action ?: "N/A"}(${meta.phase2LatencyMs}ms) " +
                    "conflict=${twoPhase.hasPhaseConflict()}")

                // v4.3: Presentation via OverlayPresenter (single entry point)
                if (::overlayPresenter.isInitialized) {
                    val overlayLang = when (
                        applicationContext.resources.configuration.locales[0]?.language
                    ) {
                        "ko" -> SupportedLanguage.KO
                        else -> SupportedLanguage.EN
                    }
                    val existingRecord = try {
                        userCallRecordRepository.findByNumber(normalizedNumber)
                    } catch (e: Exception) {
                        Log.w(TAG, "UserCallRecord lookup failed (non-fatal): ${e.message}")
                        null
                    }
                    val numberProfileSnapshot = try {
                        numberProfileRepository.getSnapshot(normalizedNumber)
                    } catch (e: Exception) {
                        Log.w(TAG, "NumberProfile lookup failed (non-fatal): ${e.message}")
                        null
                    }
                    val blockCount = if (existingRecord?.lastAction == "blocked") {
                        existingRecord.callCount
                    } else {
                        0
                    }

                    overlayPresenter.present(
                        context = applicationContext,
                        result = finalResult,
                        phoneNumber = normalizedNumber,
                        twoPhaseDecision = twoPhase,
                        language = overlayLang,
                        userBlockCount = blockCount,
                        numberProfileSnapshot = numberProfileSnapshot,
                        phaseUpgraded = hasPhase2 && twoPhase.hasPhaseConflict() && twoPhase.riskEscalated(),
                    )
                    Log.i(MPC_SCREEN, "OVERLAY_SHOWN number=$normalizedNumber risk=${finalResult.riskLevel}")
                }

                // ALLOW 응답 (전화 울림)
                respondAllow(callDetails)
                Log.i(TAG, "Call ALLOWED after 2-Phase assessment for: $normalizedNumber")

            } catch (e: TimeoutCancellationException) {
                Log.w(TAG, "Assessment timeout (${SCREENING_TIMEOUT_MS}ms) — ALLOW")
                if (::overlayPresenter.isInitialized) {
                    val normalizedNumber = PhoneNumberNormalizer.formatE164(rawNumber, effectiveCountry)
                        ?: rawNumber
                    overlayPresenter.presentTimeout(
                        context = applicationContext,
                        phoneNumber = normalizedNumber,
                    )
                }
                respondAllow(callDetails)
            } catch (e: Exception) {
                Log.e(TAG, "Assessment error — fail-safe ALLOW", e)
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
        serviceScope.cancel()
        Log.i(TAG, "onDestroy")
    }
}
