package app.callcheck.mobile.feature.messageintercept

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log

/**
 * MessageCheck SMS 수신 리시버.
 *
 * SMS 수신 시 메시지를 분석하여 판단 결과를 생성합니다.
 * 자동 차단하지 않으며, 사용자에게 판단 보조 정보만 제공합니다.
 *
 * 동작 방식:
 * 1. SMS_RECEIVED 브로드캐스트 수신
 * 2. 발신자/본문 추출
 * 3. MessageTextAnalyzer로 분석
 * 4. MessageCheckEngine으로 판단
 * 5. 로컬 저장소에 결과 기록
 *
 * 원칙:
 * - SMS를 차단하거나 가로채지 않음 (abortBroadcast 호출 금지)
 * - 외부 네트워크 전송 금지
 * - 온디바이스 분석만 수행
 */
class SmsInterceptReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "SmsInterceptReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        if (messages.isNullOrEmpty()) return

        for (smsMessage in messages) {
            val sender = smsMessage.displayOriginatingAddress ?: continue
            val body = smsMessage.displayMessageBody ?: continue

            try {
                // 연락처 저장 여부 확인은 ContentResolver 필요 — 여기서는 false로 기본값
                val evidence = MessageTextAnalyzer.analyze(
                    sender = sender,
                    body = body,
                    isSavedContact = false,
                )

                val result = MessageCheckEngine.evaluate(evidence)

                Log.d(TAG, "SMS analyzed: sender=$sender, " +
                    "category=${result.category}, risk=${result.riskLevel}")

                // TODO: 로컬 저장소(Room DB)에 결과 기록
                // TODO: 위험도 높을 시 알림 표시 (POST_NOTIFICATIONS 권한 필요)

            } catch (e: Exception) {
                Log.e(TAG, "Error analyzing SMS from $sender", e)
            }
        }

        // 절대 abortBroadcast() 호출 금지 — SMS 전달을 차단하지 않음
    }
}
