package app.myphonecheck.mobile.feature.callintercept

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import app.myphonecheck.mobile.data.contacts.ContactsDataSource
import app.myphonecheck.mobile.data.localcache.repository.NumberProfileRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

private const val TAG = "OverlayDismiss"

/**
 * 전화 상태 변경 시 오버레이를 자동 제거한다.
 *
 * - IDLE (전화 종료) → 오버레이 dismiss
 * - OFFHOOK (통화 중 = 사용자가 받음) → 오버레이 dismiss
 *
 * AndroidManifest에 등록:
 * <receiver android:name=".feature.callintercept.OverlayDismissReceiver"
 *           android:exported="false">
 *     <intent-filter>
 *         <action android:name="android.intent.action.PHONE_STATE" />
 *     </intent-filter>
 * </receiver>
 */
class OverlayDismissReceiver : BroadcastReceiver() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface DismissReceiverEntryPoint {
        fun callerIdOverlayManager(): CallerIdOverlayManager
        fun contactsDataSource(): ContactsDataSource
        fun numberProfileRepository(): NumberProfileRepository
    }

    private val receiverScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != TelephonyManager.ACTION_PHONE_STATE_CHANGED) return

        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE) ?: return

        when (state) {
            TelephonyManager.EXTRA_STATE_IDLE,
            TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                val pendingResult = goAsync()
                receiverScope.launch {
                    try {
                        val entryPoint = EntryPointAccessors.fromApplication(
                            context.applicationContext,
                            DismissReceiverEntryPoint::class.java,
                        )
                        val overlayManager = entryPoint.callerIdOverlayManager()

                        if (overlayManager.isOverlayShowing()) {
                            overlayManager.dismissOverlay(context.applicationContext)
                            Log.i(TAG, "Overlay dismissed on phone state: $state")
                        }

                        if (state == TelephonyManager.EXTRA_STATE_IDLE) {
                            val promptContext = overlayManager.consumePendingPromptContext()
                            val number = promptContext?.phoneNumber
                            if (!number.isNullOrBlank()) {
                                val isSavedContact = entryPoint.contactsDataSource().isContactSaved(number)
                                val snapshot = entryPoint.numberProfileRepository().getSnapshot(number)
                                val shouldPrompt = !isSavedContact && (snapshot == null || !snapshot.hasUserSignals)
                                if (shouldPrompt) {
                                    val launchIntent = Intent(
                                        context.applicationContext,
                                        Class.forName("app.myphonecheck.mobile.NonContactQuickLabelActivity"),
                                    ).apply {
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        putExtra("extra_normalized_number", number)
                                        putExtra("extra_summary", promptContext?.summary)
                                        putExtra("extra_search_status", promptContext?.searchStatus)
                                    }
                                    context.applicationContext.startActivity(launchIntent)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Overlay dismiss error", e)
                    } finally {
                        pendingResult.finish()
                    }
                }
            }
        }
    }
}
