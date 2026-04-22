package app.myphonecheck.mobile.data.localcache.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Observed notifications (allowed or blocked) for 7-day app/channel statistics in settings UI.
 * [channelId] empty string when the OS did not provide a channel id.
 */
@Entity(tableName = "push_notification_observations")
data class PushNotificationObservationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val packageName: String,
    val channelId: String,
    val postedAt: Long,
)
