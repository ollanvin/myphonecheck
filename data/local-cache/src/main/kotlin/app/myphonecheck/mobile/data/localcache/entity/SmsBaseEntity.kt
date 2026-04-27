package app.myphonecheck.mobile.data.localcache.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Initial Scan SMS 베이스 (Architecture v2.0.0 §28).
 *
 * 발신자별 빈도/우세 카테고리/short sender 여부 캐시. 본문 0 (헌법 §2 In-Bound Zero).
 *
 * dominantCategory 값: PAYMENT_CANDIDATE / SPAM_CANDIDATE / NOTIFICATION / NORMAL
 */
@Entity(tableName = "sms_base_entry")
data class SmsBaseEntity(
    @PrimaryKey
    val sender: String,
    val isShortSender: Boolean,
    val messageCount: Int,
    val lastSeenMillis: Long,
    val dominantCategory: String,
)
