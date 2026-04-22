package app.myphonecheck.mobile.data.localcache.entity

import androidx.room.Entity

@Entity(
    tableName = "blocked_channels",
    primaryKeys = ["packageName", "channelId"],
)
data class BlockedChannelEntity(
    val packageName: String,
    val channelId: String,
    val blockedAt: Long,
)
