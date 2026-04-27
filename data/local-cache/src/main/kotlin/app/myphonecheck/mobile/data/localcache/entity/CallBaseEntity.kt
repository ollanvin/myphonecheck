package app.myphonecheck.mobile.data.localcache.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Initial Scan 통화 베이스 (Architecture v2.0.0 §28).
 *
 * 정규화된 발신자 (E.164) 기준 빈도/마지막 시각 캐시. 통화 본문·녹음 0 (헌법 §2 In-Bound Zero).
 */
@Entity(tableName = "call_base_entry")
data class CallBaseEntity(
    @PrimaryKey
    val e164: String,
    val regionCode: String,
    val callCount: Int,
    val lastCallMillis: Long,
    val numberType: String,
    val firstSeenMillis: Long,
)
