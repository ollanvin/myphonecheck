package app.myphonecheck.mobile.data.localcache.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trashed_notifications")
data class TrashedNotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val packageName: String,
    val channelId: String?,
    val title: String?,
    val text: String?,
    val iconResId: Int?,
    val postedAt: Long,
    val capturedAt: Long,
)
