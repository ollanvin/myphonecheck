package app.myphonecheck.mobile.feature.pushtrash.service

import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import app.myphonecheck.mobile.feature.pushtrash.repository.PushTrashRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PushTrashNotificationListener : NotificationListenerService() {

    @Inject
    lateinit var repository: PushTrashRepository

    private val job = SupervisorJob()
    private val scope = CoroutineScope(job + Dispatchers.Main.immediate)

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val pkg = sbn.packageName
        if (pkg == applicationContext.packageName) return

        val channelId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            sbn.notification.channelId?.takeIf { it.isNotBlank() }
        } else {
            null
        }

        scope.launch {
            repository.recordNotificationObserved(pkg, channelId, sbn.postTime)
            when (repository.decide(pkg, channelId)) {
                PushTrashRepository.Decision.Allow -> Unit
                PushTrashRepository.Decision.Block -> {
                    repository.recordTrashed(sbn)
                    cancelNotification(sbn.key)
                }
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        // Stage 1: intentionally ignored (see WO-STAGE1-001).
    }
}
