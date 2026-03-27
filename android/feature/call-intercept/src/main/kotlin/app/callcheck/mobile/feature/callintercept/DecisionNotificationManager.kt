package app.callcheck.mobile.feature.callintercept

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import app.callcheck.mobile.core.model.DecisionResult
import app.callcheck.mobile.core.model.RiskLevel
import app.callcheck.mobile.feature.countryconfig.SignalSummaryLocalizer
import app.callcheck.mobile.feature.countryconfig.SupportedLanguage
import javax.inject.Inject

private const val TAG = "DecisionNotification"
private const val CHANNEL_ID = "callcheck_decisions"
private const val CHANNEL_NAME = "CallCheck Decisions"
private const val NOTIFICATION_ID_PREFIX = 1000
private const val ACTION_DETAIL = "action_detail"
private const val ACTION_REJECT = "action_reject"
private const val ACTION_BLOCK = "action_block"
private const val EXTRA_PHONE_NUMBER = "extra_phone_number"

class DecisionNotificationManager @Inject constructor() {

    fun showDecisionNotification(
        context: Context,
        result: DecisionResult,
        phoneNumber: String,
        language: SupportedLanguage = SupportedLanguage.EN,
        localizer: SignalSummaryLocalizer = SignalSummaryLocalizer(),
    ) {
        try {
            ensureChannel(context)

            val notificationId = generateId(phoneNumber)
            val notification = buildDecisionNotification(
                context, result, phoneNumber, notificationId, language, localizer
            )

            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.notify(notificationId, notification)
        } catch (e: Exception) {
            Log.e(TAG, "showDecisionNotification error", e)
        }
    }

    fun showTimeoutNotification(
        context: Context,
        phoneNumber: String,
        language: SupportedLanguage = SupportedLanguage.EN,
        localizer: SignalSummaryLocalizer = SignalSummaryLocalizer(),
    ) {
        try {
            ensureChannel(context)

            val notificationId = generateId(phoneNumber)
            val notification = buildTimeoutNotification(
                context, phoneNumber, notificationId, language
            )

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
        language: SupportedLanguage,
        localizer: SignalSummaryLocalizer,
    ): Notification {
        val uiText = NotificationUiText.forLanguage(language)

        // Title: 로컬라이즈된 카테고리
        val title = localizer.localizeCategory(result.category.name, language)

        // Subtitle: 로컬라이즈된 위험도
        val riskDisplay = when (language) {
            SupportedLanguage.KO -> result.riskLevel.displayNameKo
            else -> result.riskLevel.displayNameEn
        }

        // Build content with reasons
        val reasonsText = result.reasons.joinToString("\n")
        val bigText = buildString {
            append(result.summary)
            if (reasonsText.isNotEmpty()) {
                append("\n")
                append(reasonsText)
            }
            append("\n${uiText.confidence}: ${(result.confidence * 100).toInt()}%")
        }

        val detailPI = createActionPI(context, ACTION_DETAIL, phoneNumber, notificationId + 1)
        val rejectPI = createActionPI(context, ACTION_REJECT, phoneNumber, notificationId + 2)
        val blockPI = createActionPI(context, ACTION_BLOCK, phoneNumber, notificationId + 3)

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(riskDisplay)
            .setSubText(phoneNumber)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            .addAction(android.R.drawable.ic_menu_call, uiText.actionDetail, detailPI)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, uiText.actionReject, rejectPI)
            .addAction(android.R.drawable.ic_dialog_dialer, uiText.actionBlock, blockPI)
            .setColorized(true)
            .setColor(colorForRisk(result.riskLevel))
            .build()
    }

    private fun buildTimeoutNotification(
        context: Context,
        phoneNumber: String,
        notificationId: Int,
        language: SupportedLanguage,
    ): Notification {
        val uiText = NotificationUiText.forLanguage(language)

        val detailPI = createActionPI(context, ACTION_DETAIL, phoneNumber, notificationId + 1)
        val rejectPI = createActionPI(context, ACTION_REJECT, phoneNumber, notificationId + 2)

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(uiText.timeoutTitle)
            .setContentText(uiText.timeoutContent)
            .setSubText(phoneNumber)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .addAction(android.R.drawable.ic_menu_call, uiText.actionDetail, detailPI)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, uiText.actionReject, rejectPI)
            .setColorized(true)
            .setColor(android.graphics.Color.YELLOW)
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
                description = "Incoming call screening decisions"
                enableVibration(true)
                enableLights(true)
                setShowBadge(true)
            }
            nm.createNotificationChannel(channel)
        }
    }

    private fun colorForRisk(riskLevel: RiskLevel): Int {
        return when (riskLevel) {
            RiskLevel.LOW -> android.graphics.Color.GREEN
            RiskLevel.MEDIUM -> android.graphics.Color.YELLOW
            RiskLevel.HIGH -> android.graphics.Color.RED
            RiskLevel.UNKNOWN -> android.graphics.Color.GRAY
        }
    }

    private fun generateId(phoneNumber: String): Int {
        return (NOTIFICATION_ID_PREFIX + phoneNumber.hashCode()).coerceIn(
            NOTIFICATION_ID_PREFIX,
            NOTIFICATION_ID_PREFIX + 10000,
        )
    }
}

/**
 * Notification UI 텍스트 — 언어별 템플릿.
 */
internal data class NotificationUiText(
    val actionDetail: String,
    val actionReject: String,
    val actionBlock: String,
    val timeoutTitle: String,
    val timeoutContent: String,
    val confidence: String,
) {
    companion object {
        fun forLanguage(language: SupportedLanguage): NotificationUiText {
            return when (language) {
                SupportedLanguage.KO -> KO
                SupportedLanguage.JA -> JA
                SupportedLanguage.ZH -> ZH
                SupportedLanguage.RU -> RU
                SupportedLanguage.ES -> ES
                SupportedLanguage.AR -> AR
                else -> EN
            }
        }

        private val KO = NotificationUiText(
            actionDetail = "자세히",
            actionReject = "거절",
            actionBlock = "차단",
            timeoutTitle = "판단 보류",
            timeoutContent = "검증 시간 초과 — 통화 허용됨",
            confidence = "신뢰도",
        )

        private val EN = NotificationUiText(
            actionDetail = "Details",
            actionReject = "Reject",
            actionBlock = "Block",
            timeoutTitle = "Assessment Pending",
            timeoutContent = "Verification timeout — call allowed",
            confidence = "Confidence",
        )

        private val JA = NotificationUiText(
            actionDetail = "詳細",
            actionReject = "拒否",
            actionBlock = "ブロック",
            timeoutTitle = "判定保留",
            timeoutContent = "検証タイムアウト — 通話許可",
            confidence = "信頼度",
        )

        private val ZH = NotificationUiText(
            actionDetail = "详情",
            actionReject = "拒接",
            actionBlock = "拉黑",
            timeoutTitle = "判断待定",
            timeoutContent = "验证超时 — 已允许通话",
            confidence = "置信度",
        )

        private val RU = NotificationUiText(
            actionDetail = "Детали",
            actionReject = "Откл.",
            actionBlock = "Блок",
            timeoutTitle = "Оценка ожидает",
            timeoutContent = "Тайм-аут проверки — звонок разрешён",
            confidence = "Достоверность",
        )

        private val ES = NotificationUiText(
            actionDetail = "Detalles",
            actionReject = "Rechazar",
            actionBlock = "Bloquear",
            timeoutTitle = "Evaluación pendiente",
            timeoutContent = "Tiempo de verificación agotado — llamada permitida",
            confidence = "Confianza",
        )

        private val AR = NotificationUiText(
            actionDetail = "تفاصيل",
            actionReject = "رفض",
            actionBlock = "حظر",
            timeoutTitle = "التقييم معلق",
            timeoutContent = "انتهت مهلة التحقق — المكالمة مسموح بها",
            confidence = "الثقة",
        )
    }
}
