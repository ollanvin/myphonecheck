package app.myphonecheck.mobile.data.localcache.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * WO-DATA-MSG-001 Message Hub row (parallel to legacy [MessageHubEntity] / `message_hub`).
 *
 * Stores normalized SMS / notification lines for MessageCheck aggregation & classification.
 */
@Entity(
    tableName = "hub_message",
    indices = [
        Index(value = ["received_at"]),
        Index(value = ["sender_identifier", "received_at"]),
    ],
)
data class HubMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "sender_identifier") val senderIdentifier: String,
    @ColumnInfo(name = "sender_label") val senderLabel: String?,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "has_link") val hasLink: Boolean,
    @ColumnInfo(name = "extracted_urls") val extractedUrls: String,
    @ColumnInfo(name = "extracted_numbers") val extractedNumbers: String,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "received_at") val receivedAt: Long,
    @ColumnInfo(name = "source") val source: String,
)
