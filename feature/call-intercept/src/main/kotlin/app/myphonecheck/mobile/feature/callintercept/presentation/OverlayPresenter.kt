package app.myphonecheck.mobile.feature.callintercept.presentation

import android.content.Context
import android.util.Log
import app.myphonecheck.mobile.core.model.DecisionResult
import app.myphonecheck.mobile.core.model.TwoPhaseDecision
import app.myphonecheck.mobile.data.localcache.repository.NumberProfileSnapshot
import app.myphonecheck.mobile.feature.callintercept.CallerIdOverlayManager
import app.myphonecheck.mobile.feature.callintercept.DecisionNotificationManager
import app.myphonecheck.mobile.feature.countryconfig.SupportedLanguage
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "OverlayPresenter"

/**
 * v4.3 Presentation Layer — single entry point for overlay + notification.
 *
 * 규칙:
 * - showOverlay() 호출은 이 클래스 내부에서만 발생
 * - ScreeningService는 이 클래스만 호출
 * - CallerIdOverlayManager에 직접 접근 금지
 *
 * rg "showOverlay(" → 이 파일 1곳만 허용
 */
@Singleton
class OverlayPresenter @Inject constructor(
    private val callerIdOverlayManager: CallerIdOverlayManager,
    private val decisionNotificationManager: DecisionNotificationManager,
) {

    /**
     * 판정 결과를 사용자에게 표시한다.
     * Overlay + Notification을 모두 이 메서드에서 처리.
     *
     * @param context Android Context
     * @param result 최종 판정 결과
     * @param phoneNumber 정규화된 번호
     * @param twoPhaseDecision 2-Phase 메타
     * @param language 기기 언어
     * @param userBlockCount 사용자 차단 이력 횟수
     * @param numberProfileSnapshot 번호 프로필 스냅샷
     */
    fun present(
        context: Context,
        result: DecisionResult,
        phoneNumber: String,
        twoPhaseDecision: TwoPhaseDecision? = null,
        language: SupportedLanguage = SupportedLanguage.EN,
        userBlockCount: Int = 0,
        numberProfileSnapshot: NumberProfileSnapshot? = null,
        phaseUpgraded: Boolean = false,
    ) {
        // 1. Notification
        try {
            if (phaseUpgraded) {
                decisionNotificationManager.showDecisionNotification(
                    context = context,
                    result = result,
                    phoneNumber = phoneNumber,
                    phaseUpgraded = true,
                )
            } else {
                decisionNotificationManager.showDecisionNotification(
                    context = context,
                    result = result,
                    phoneNumber = phoneNumber,
                )
            }
            Log.i(TAG, "Notification shown for: $phoneNumber")
        } catch (e: Exception) {
            Log.w(TAG, "Notification failed (non-fatal): ${e.message}")
        }

        // 2. Overlay — SINGLE showOverlay() call point (v4.3 PHASE 5)
        try {
            callerIdOverlayManager.showOverlay(
                context = context,
                result = result,
                phoneNumber = phoneNumber,
                language = language,
                twoPhaseDecision = twoPhaseDecision,
                userBlockCount = userBlockCount,
                numberProfileSnapshot = numberProfileSnapshot,
            )
            Log.i(TAG, "Overlay shown for: $phoneNumber")
        } catch (e: Exception) {
            Log.w(TAG, "Overlay failed (non-fatal): ${e.message}")
        }
    }

    /**
     * 타임아웃 시 알림만 표시 (오버레이 없음).
     */
    fun presentTimeout(
        context: Context,
        phoneNumber: String,
    ) {
        try {
            decisionNotificationManager.showTimeoutNotification(
                context = context,
                phoneNumber = phoneNumber,
            )
            Log.i(TAG, "Timeout notification shown for: $phoneNumber")
        } catch (e: Exception) {
            Log.w(TAG, "Timeout notification failed (non-fatal): ${e.message}")
        }
    }
}
