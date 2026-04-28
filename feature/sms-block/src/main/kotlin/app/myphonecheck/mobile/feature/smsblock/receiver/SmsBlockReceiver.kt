package app.myphonecheck.mobile.feature.smsblock.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import app.myphonecheck.mobile.core.globalengine.decision.ActionType
import app.myphonecheck.mobile.core.globalengine.decision.RealTimeActionEngine
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

/**
 * SMS 차단 BroadcastReceiver (Architecture v2.1.0 §31-4).
 *
 * intent-filter priority 999 + BLOCK 결정 시 abortBroadcast →
 * Default SMS App 모드(Mode A)에서 OS Inbox 도달 전 완전 차단.
 *
 * Observer 모드(Mode B)에서도 ordered broadcast 인 경우 abort 시도.
 *
 * 50ms timeout — RealTimeActionEngine 자체 timeout 적용.
 */
@AndroidEntryPoint
class SmsBlockReceiver : BroadcastReceiver() {

    @Inject lateinit var actionEngine: RealTimeActionEngine

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action != Telephony.Sms.Intents.SMS_DELIVER_ACTION &&
            action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION
        ) return

        val messages = runCatching {
            Telephony.Sms.Intents.getMessagesFromIntent(intent)
        }.getOrNull() ?: return

        for (msg in messages) {
            val sender = msg.originatingAddress ?: continue
            val decision = runBlocking { actionEngine.decideForSms(sender) }
            if (decision.action == ActionType.BLOCK) {
                if (isOrderedBroadcast) abortBroadcast()
                return
            }
        }
    }
}
