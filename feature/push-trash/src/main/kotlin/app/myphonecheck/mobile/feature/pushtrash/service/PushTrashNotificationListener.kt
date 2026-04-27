package app.myphonecheck.mobile.feature.pushtrash.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import app.myphonecheck.mobile.core.globalengine.parsing.notification.NotificationSourceParser
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

    @Inject
    lateinit var sourceParser: NotificationSourceParser

    private val job = SupervisorJob()
    private val scope = CoroutineScope(job + Dispatchers.Main.immediate)

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val source = sourceParser.parseSource(sbn)
        if (source.packageName == applicationContext.packageName) return

        val channelId = source.channelId.takeIf { it.isNotBlank() }

        scope.launch {
            repository.recordNotificationObserved(source.packageName, channelId, source.postTime)
            when (repository.decide(source.packageName, channelId)) {
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
