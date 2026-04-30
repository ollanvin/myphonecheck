package app.myphonecheck.mobile.feature.pushtrash.service

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import app.myphonecheck.mobile.core.globalengine.decision.ActionType
import app.myphonecheck.mobile.core.globalengine.decision.RealTimeActionEngine
import app.myphonecheck.mobile.core.globalengine.parsing.notification.NotificationSourceParser
import app.myphonecheck.mobile.feature.messageintercept.router.IngestRouter
import app.myphonecheck.mobile.feature.pushtrash.repository.PushTrashRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * PushCheck NotificationListener — Stage 1-001(휴지통) + v2.1.0 §31 Real-time Action 통합.
 *
 * 새 로직:
 *  1. Real-time BLOCK (Layer 2 BlockList 매칭) → 즉시 cancel + 휴지통 저장.
 *  2. Real-time SILENT (Layer 2 SUSPICIOUS Tag) → 휴지통 저장만, OS 알림 그대로 (priority 조정 한계로 cancel 안 함).
 *  3. PASS → 기존 휴지통 차단 규칙 (PushTrashRepository.decide) 유지.
 *
 * 회귀 0: 기존 차단 규칙 동작 그대로. Real-time BLOCK은 차단 규칙보다 먼저 cancel하므로 결과 동일하거나 더 빠름.
 */
@AndroidEntryPoint
class PushTrashNotificationListener : NotificationListenerService() {

    @Inject
    lateinit var repository: PushTrashRepository

    @Inject
    lateinit var sourceParser: NotificationSourceParser

    @Inject
    lateinit var actionEngine: RealTimeActionEngine

    @Inject
    lateinit var ingestRouter: IngestRouter

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

            // WO-INGEST-WIRING-001 — MessageHub + CardTransaction 양쪽 인입 와이어링.
            // 기존 RealTimeActionEngine 분기 / decide 분기 / cancelNotification 동작 100% 보존.
            val extras = sbn.notification?.extras
            val title = extras?.getCharSequence(Notification.EXTRA_TITLE)?.toString()
            val text = extras?.getCharSequence(Notification.EXTRA_TEXT)?.toString()
            val senderLabel = resolveAppLabel(source.packageName)
            ingestRouter.routeNotification(
                packageName = source.packageName,
                senderLabel = senderLabel,
                title = title,
                body = text,
                receivedAt = source.postTime,
            )

            // v2.1.0 §31 Real-time Action 우선 적용.
            val realtime = actionEngine.decideForNotification(source.packageName)
            if (realtime.action == ActionType.BLOCK) {
                repository.recordTrashed(sbn)
                cancelNotification(sbn.key)
                return@launch
            }

            // 기존 차단 규칙 (Stage 1-001) 적용.
            when (repository.decide(source.packageName, channelId)) {
                PushTrashRepository.Decision.Allow -> Unit
                PushTrashRepository.Decision.Block -> {
                    repository.recordTrashed(sbn)
                    cancelNotification(sbn.key)
                }
            }
        }
    }

    private fun resolveAppLabel(packageName: String): String? {
        return runCatching {
            val pm = packageManager
            pm.getApplicationLabel(pm.getApplicationInfo(packageName, 0)).toString()
        }.getOrNull()
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        // Stage 1: intentionally ignored (see WO-STAGE1-001).
    }
}
