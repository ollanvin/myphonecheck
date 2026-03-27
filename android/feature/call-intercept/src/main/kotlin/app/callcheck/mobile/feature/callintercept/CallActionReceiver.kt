package app.callcheck.mobile.feature.callintercept

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "CallActionReceiver"
private const val EXTRA_PHONE_NUMBER = "extra_phone_number"
private const val ACTION_DETAIL = "action_detail"
private const val ACTION_REJECT = "action_reject"
private const val ACTION_BLOCK = "action_block"

@AndroidEntryPoint
class CallActionReceiver : BroadcastReceiver() {

    @Inject
    lateinit var decisionNotificationManager: DecisionNotificationManager

    @Inject
    lateinit var blocklistRepository: BlocklistRepository

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

            Log.d(TAG, "Received action: ${intent.action} for $phoneNumber")

            when (intent.action) {
                ACTION_DETAIL -> handleDetailAction(context, phoneNumber)
                ACTION_REJECT -> handleRejectAction(context, phoneNumber)
                ACTION_BLOCK -> handleBlockAction(context, phoneNumber)
                else -> Log.w(TAG, "Unknown action: ${intent.action}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling broadcast", e)
        }
    }

    private fun handleDetailAction(context: Context, phoneNumber: String) {
        try {
            Log.i(TAG, "User chose DETAIL for $phoneNumber")

            // Log the user action
            logUserAction(phoneNumber, "DETAIL")

            // Dismiss notification
            decisionNotificationManager.dismissNotification(context, phoneNumber)

            // Future: Open decision detail screen with full evidence
        } catch (e: Exception) {
            Log.e(TAG, "Error handling detail action", e)
        }
    }

    private fun handleRejectAction(context: Context, phoneNumber: String) {
        try {
            Log.d(TAG, "User chose to REJECT call from $phoneNumber")

            // Log the user override
            logUserAction(phoneNumber, "REJECT")

            // Dismiss notification
            decisionNotificationManager.dismissNotification(context, phoneNumber)

            // Future: Could update user preferences
        } catch (e: Exception) {
            Log.e(TAG, "Error handling reject action", e)
        }
    }

    private fun handleBlockAction(context: Context, phoneNumber: String) {
        try {
            Log.d(TAG, "User chose to BLOCK call from $phoneNumber")

            // Log the user override
            logUserAction(phoneNumber, "BLOCK")

            // Add to blocklist
            addToBlocklist(phoneNumber)

            // Dismiss notification
            decisionNotificationManager.dismissNotification(context, phoneNumber)

            // Show confirmation
            showBlockConfirmation(context, phoneNumber)
        } catch (e: Exception) {
            Log.e(TAG, "Error handling block action", e)
        }
    }

    private fun addToBlocklist(phoneNumber: String) {
        receiverScope.launch {
            try {
                blocklistRepository.addToBlocklist(
                    phoneNumber = phoneNumber,
                    reason = "User blocked from notification",
                    timestamp = System.currentTimeMillis()
                )
                Log.d(TAG, "Successfully added $phoneNumber to blocklist")
            } catch (e: Exception) {
                Log.e(TAG, "Error adding to blocklist", e)
            }
        }
    }

    private fun logUserAction(phoneNumber: String, action: String) {
        receiverScope.launch {
            try {
                // Log user override decision for analytics
                Log.d(TAG, "Logged user action: $action for $phoneNumber")
                // Future: Send to analytics service
            } catch (e: Exception) {
                Log.e(TAG, "Error logging user action", e)
            }
        }
    }

    private fun showBlockConfirmation(context: Context, phoneNumber: String) {
        try {
            // In a real app, you could show a toast or other confirmation
            Log.d(TAG, "Block confirmation: $phoneNumber is now blocked")
        } catch (e: Exception) {
            Log.e(TAG, "Error showing block confirmation", e)
        }
    }
}
