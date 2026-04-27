package app.myphonecheck.mobile.core.globalengine.parsing.notification

import android.os.Build
import android.service.notification.StatusBarNotification
import javax.inject.Inject
import javax.inject.Singleton

/**
 * NotificationListenerService 알림 → NotificationSource 정규화 (v2.0.0 §30).
 *
 * Surface(:feature:push-trash 등)는 본 파서를 통해서만 source 정보 획득 — 자체 추출 금지.
 *
 * JVM 단위 테스트 호환을 위해 raw 입력 overload 제공:
 *  - parseSource(StatusBarNotification): Android 인스트루먼트 환경.
 *  - parseRaw(packageName, channelId, postTime, id, tag): JVM 가능.
 */
@Singleton
class NotificationSourceParser @Inject constructor() {

    fun parseSource(sbn: StatusBarNotification): NotificationSource {
        val channelId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            sbn.notification?.channelId.orEmpty()
        } else {
            ""
        }
        return parseRaw(
            packageName = sbn.packageName.orEmpty(),
            channelId = channelId,
            postTime = sbn.postTime,
            id = sbn.id,
            tag = sbn.tag.orEmpty(),
        )
    }

    fun parseRaw(
        packageName: String,
        channelId: String,
        postTime: Long,
        id: Int,
        tag: String,
    ): NotificationSource = NotificationSource(
        packageName = packageName,
        channelId = channelId,
        postTime = postTime,
        id = id,
        tag = tag,
    )
}
