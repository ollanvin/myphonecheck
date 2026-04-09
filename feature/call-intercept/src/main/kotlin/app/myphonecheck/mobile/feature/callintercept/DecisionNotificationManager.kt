package app.myphonecheck.mobile.feature.callintercept

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import app.myphonecheck.mobile.core.model.DecisionResult
import app.myphonecheck.mobile.core.model.RingSystem
import javax.inject.Inject

private const val TAG = "DecisionNotification"
private const val CHANNEL_ID = "myphonecheck_decisions"
private const val CHANNEL_NAME = "MyPhoneCheck 판정"
private const val NOTIFICATION_ID_PREFIX = 1000
private const val ACTION_REJECT = "action_reject"
private const val ACTION_BLOCK = "action_block"
private const val ACTION_DETAIL = "action_detail"
private const val EXTRA_PHONE_NUMBER = "extra_phone_number"

/**
 * 판단 재료 Notification 매니저.
 *
 * 제품 철학: 행동 대행이 아니라 판단 재료 노출.
 *
 * 색상 체계: [RingSystem] 단일 소스 참조.
 * - SAFE_LIKELY → 초록 (#4CAF50)
 * - CAUTION    → 노랑 (#FFC107)
 * - RISK_HIGH  → 빨강 (#F44336)
 * - UNKNOWN    → 회색 (#808080)
 *
 * Notification 구성:
 * - 제목: 이모지 + 판단 상태 + 전화번호
 * - 내용: 카테고리 요약 (한 줄 결론)
 * - 확장: 근거 최대 3개
 * - 액션: 거절 / 차단 / 자세히 보기
 * - 면책: RingSystem.DISCLAIMER_KO
 *
 * "수신(Answer)" 버튼은 의도적으로 제외.
 * 수신 행동은 시스템 콜 UI가 담당합니다.
 */
class DecisionNotificationManager @Inject constructor() {

    /**
     * 판단 결과 Notification 표시.
     *
     * @param phaseUpgraded Phase 2에서 위험도가 상승한 경우 true.
     *        true이면 "추가 확인됨" 강화 문구를 Notification에 추가.
     */
    fun showDecisionNotification(
        context: Context,
        result: DecisionResult,
        phoneNumber: String,
        phaseUpgraded: Boolean = false,
    ) {
        try {
            ensureChannel(context)

            val notificationId = generateId(phoneNumber)
            val notification = buildDecisionNotification(context, result, phoneNumber, notificationId, phaseUpgraded)

            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.notify(notificationId, notification)
        } catch (e: Exception) {
            Log.e(TAG, "showDecisionNotification error", e)
        }
    }

    fun showTimeoutNotification(context: Context, phoneNumber: String) {
        try {
            ensureChannel(context)

            val notificationId = generateId(phoneNumber)
            val notification = buildTimeoutNotification(context, phoneNumber, notificationId)

            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.notify(notificationId, notification)
        } catch (e: Exception) {
            Log.e(TAG, "showTimeoutNotification error", e)
        }
    }

    fun dismissNotification(context: Context, phoneNumber: String) {
        try {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.cancel(generateId(phoneNumber))
        } catch (e: Exception) {
            Log.e(TAG, "dismissNotification error", e)
        }
    }

    private fun buildDecisionNotification(
        context: Context,
        result: DecisionResult,
        phoneNumber: String,
        notificationId: Int,
        phaseUpgraded: Boolean = false,
    ): Notification {
        // RingSystem 단일 소스에서 색상·라벨·이모지 조회 (action 기반 통일)
        val stateColor = RingSystem.color(result.action)
        val stateEmoji = RingSystem.emoji(result.action)
        val stateLabel = RingSystem.labelKo(result.action)

        // 2-Phase UX: 위험 상승 시 강화 문구
        val phaseTag = if (phaseUpgraded) " [추가 확인됨]" else ""

        // 제목: 이모지 + 상태 + Phase 태그 + 번호
        val title = "$stateEmoji $stateLabel$phaseTag — $phoneNumber"

        // 내용: 한 줄 요약
        val contentText = if (phaseUpgraded) {
            "${result.summary} (심층 분석 완료)"
        } else {
            result.summary
        }

        // 확장 텍스트: 요약 + Phase 정보 + 근거 + 면책
        val bigText = buildString {
            append(result.summary)
            if (phaseUpgraded) {
                append("\n※ 추가 분석에서 위험도가 상승했습니다")
            }
            if (result.reasons.isNotEmpty()) {
                result.reasons.forEachIndexed { index, reason ->
                    append("\n${index + 1}. $reason")
                }
            }
            append("\n\n")
            append(RingSystem.DISCLAIMER_KO)
        }

        // 액션: 거절 / 차단 / 자세히
        val rejectPI = createActionPI(context, ACTION_REJECT, phoneNumber, notificationId + 1)
        val blockPI = createActionPI(context, ACTION_BLOCK, phoneNumber, notificationId + 2)
        val detailPI = createActionPI(context, ACTION_DETAIL, phoneNumber, notificationId + 3)

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(contentText)
            .setSubText("MyPhoneCheck")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setAutoCancel(false)
            .setOngoing(true)
            .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "거절", rejectPI)
            .addAction(android.R.drawable.ic_delete, "차단", blockPI)
            .addAction(android.R.drawable.ic_menu_info_details, "자세히", detailPI)
            .setColorized(true)
            .setColor(stateColor)
            .build()
    }

    private fun buildTimeoutNotification(
        context: Context,
        phoneNumber: String,
        notificationId: Int,
    ): Notification {
        val rejectPI = createActionPI(context, ACTION_REJECT, phoneNumber, notificationId + 1)
        val blockPI = createActionPI(context, ACTION_BLOCK, phoneNumber, notificationId + 2)

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("${RingSystem.emoji(app.myphonecheck.mobile.core.model.RiskLevel.UNKNOWN)} 판단 불가 — $phoneNumber")
            .setContentText("판정 시간 초과 — 직접 확인하세요")
            .setSubText("MyPhoneCheck")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setAutoCancel(false)
            .setOngoing(true)
            .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "거절", rejectPI)
            .addAction(android.R.drawable.ic_delete, "차단", blockPI)
            .setColorized(true)
            .setColor(RingSystem.COLOR_UNKNOWN)
            .build()
    }

    private fun createActionPI(
        context: Context,
        action: String,
        phoneNumber: String,
        requestCode: Int,
    ): PendingIntent {
        val intent = Intent(context, CallActionReceiver::class.java).apply {
            this.action = action
            putExtra(EXTRA_PHONE_NUMBER, phoneNumber)
        }
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (nm.getNotificationChannel(CHANNEL_ID) != null) return

            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = "수신 전화 판정 결과"
                enableVibration(true)
                enableLights(true)
                setShowBadge(true)
            }
            nm.createNotificationChannel(channel)
        }
    }

    private fun generateId(phoneNumber: String): Int {
        return (NOTIFICATION_ID_PREFIX + phoneNumber.hashCode()).coerceIn(
            NOTIFICATION_ID_PREFIX,
            NOTIFICATION_ID_PREFIX + 10000,
        )
    }
}
