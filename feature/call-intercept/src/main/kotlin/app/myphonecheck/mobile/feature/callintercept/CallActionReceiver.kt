package app.myphonecheck.mobile.feature.callintercept

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import app.myphonecheck.mobile.core.model.UserCallAction
import app.myphonecheck.mobile.data.localcache.entity.NumberProfileBlockState
import app.myphonecheck.mobile.data.localcache.repository.NumberProfileRepository
import app.myphonecheck.mobile.data.localcache.repository.UserCallRecordRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "CallActionReceiver"
private const val EXTRA_PHONE_NUMBER = "extra_phone_number"
private const val EXTRA_ACTION_TYPE = "action_type"

/**
 * 사용자 행동 브로드캐스트 수신자.
 *
 * 오버레이/Notification에서 사용자가 선택한 행동(수신/거절/차단)을
 * UserCallRecordRepository + BlocklistRepository에 기록한다.
 *
 * 인텐트 포맷:
 * - action: "app.myphonecheck.mobile.ACTION_CALL"
 * - extra "extra_phone_number": E.164 번호
 * - extra "action_type": "action_accept" | "action_reject" | "action_block" | "action_detail"
 */
@AndroidEntryPoint
class CallActionReceiver : BroadcastReceiver() {

    @Inject
    lateinit var decisionNotificationManager: DecisionNotificationManager

    @Inject
    lateinit var blocklistRepository: BlocklistRepository

    @Inject
    lateinit var userCallRecordRepository: UserCallRecordRepository

    @Inject
    lateinit var numberProfileRepository: NumberProfileRepository

    private val receiverScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent == null) {
            Log.w(TAG, "Received null intent")
            return
        }

        try {
            val phoneNumber = intent.getStringExtra(EXTRA_PHONE_NUMBER)
            if (phoneNumber.isNullOrBlank()) {
                Log.w(TAG, "Phone number not found in intent extras")
                return
            }

            val actionType = intent.getStringExtra(EXTRA_ACTION_TYPE)
                ?: intent.action
                ?: ""

            Log.d(TAG, "Received action: $actionType for $phoneNumber")

            when (actionType) {
                "action_accept" -> handleAcceptAction(context, phoneNumber)
                "action_detail" -> handleDetailAction(context, phoneNumber)
                "action_reject" -> handleRejectAction(context, phoneNumber)
                "action_block" -> handleBlockAction(context, phoneNumber)
                else -> Log.w(TAG, "Unknown action type: $actionType")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling broadcast", e)
        }
    }

    private fun handleAcceptAction(context: Context, phoneNumber: String) {
        try {
            Log.i(TAG, "User chose ACCEPT for $phoneNumber")

            // UserCallRecord에 수신 행동 기록
            recordUserAction(phoneNumber, UserCallAction.ANSWERED)
            updateBlockState(phoneNumber, NumberProfileBlockState.NONE)

            // Notification 해제
            decisionNotificationManager.dismissNotification(context, phoneNumber)
        } catch (e: Exception) {
            Log.e(TAG, "Error handling accept action", e)
        }
    }

    private fun handleDetailAction(context: Context, phoneNumber: String) {
        try {
            Log.i(TAG, "User chose DETAIL for $phoneNumber")

            // Notification 해제
            decisionNotificationManager.dismissNotification(context, phoneNumber)

            // TODO: 상세 화면 열기
        } catch (e: Exception) {
            Log.e(TAG, "Error handling detail action", e)
        }
    }

    private fun handleRejectAction(context: Context, phoneNumber: String) {
        try {
            Log.d(TAG, "User chose to REJECT call from $phoneNumber")

            // UserCallRecord에 거절 행동 기록
            recordUserAction(phoneNumber, UserCallAction.REJECTED)
            updateBlockState(phoneNumber, NumberProfileBlockState.NONE)

            // Notification 해제
            decisionNotificationManager.dismissNotification(context, phoneNumber)
        } catch (e: Exception) {
            Log.e(TAG, "Error handling reject action", e)
        }
    }

    private fun handleBlockAction(context: Context, phoneNumber: String) {
        try {
            Log.d(TAG, "User chose to BLOCK call from $phoneNumber")

            // UserCallRecord에 차단 행동 기록
            recordUserAction(phoneNumber, UserCallAction.BLOCKED)
            updateBlockState(phoneNumber, NumberProfileBlockState.BLOCKED)

            // BlocklistRepository에도 추가
            addToBlocklist(phoneNumber)

            // Notification 해제
            decisionNotificationManager.dismissNotification(context, phoneNumber)
        } catch (e: Exception) {
            Log.e(TAG, "Error handling block action", e)
        }
    }

    /**
     * UserCallRecordRepository에 사용자 행동 기록.
     * 기존 레코드가 있으면 callCount 증가 + lastAction 업데이트.
     * 없으면 새 레코드 생성.
     */
    private fun recordUserAction(phoneNumber: String, action: UserCallAction) {
        receiverScope.launch {
            try {
                userCallRecordRepository.recordCall(
                    canonicalNumber = phoneNumber,
                    displayNumber = phoneNumber,
                    action = action,
                )
                Log.d(TAG, "UserCallRecord saved: ${action.displayKey} for $phoneNumber")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to record user action to UserCallRecord", e)
            }
        }
    }

    private fun addToBlocklist(phoneNumber: String) {
        receiverScope.launch {
            try {
                blocklistRepository.addToBlocklist(
                    phoneNumber = phoneNumber,
                    reason = "User blocked from overlay/notification",
                    timestamp = System.currentTimeMillis(),
                )
                Log.d(TAG, "Successfully added $phoneNumber to blocklist")
            } catch (e: Exception) {
                Log.e(TAG, "Error adding to blocklist", e)
            }
        }
    }

    private fun updateBlockState(phoneNumber: String, state: NumberProfileBlockState) {
        receiverScope.launch {
            runCatching {
                numberProfileRepository.setBlockState(phoneNumber, state)
            }.onFailure {
                Log.w(TAG, "Failed to update NumberProfile block state for $phoneNumber", it)
            }
        }
    }
}
