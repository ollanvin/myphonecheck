package app.myphonecheck.mobile.data.localcache.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "detail_tags",
    primaryKeys = ["normalized_number", "tag_name"],
    indices = [
        Index(value = ["normalized_number"]),
        Index(value = ["source"]),
    ],
)
data class DetailTagEntity(
    @ColumnInfo(name = "normalized_number")
    val normalizedNumber: String,
    @ColumnInfo(name = "tag_name")
    val tagName: String,
    @ColumnInfo(name = "source")
    val source: String = DetailTagSource.USER.storageKey,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),
)
