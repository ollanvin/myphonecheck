package app.myphonecheck.mobile.data.localcache.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 공개 피드 캐시 엔트리 (Architecture v2.1.0 §30-4 Layer 3).
 *
 * sourceId × matchKey 복합 검색용. severity는 nullable (출처 따라 없음).
 *
 * 헌법 정합:
 *  - §1 Out-Bound Zero: 라이선스 정합 출처 다운로드 후 디바이스 캐싱.
 *  - §3 결정권 중앙집중 금지: 사용자 옵트인 후에만 채워짐.
 */
@Entity(
    tableName = "feed_entry",
    indices = [Index("sourceId"), Index("matchKey"), Index("downloadedAtMillis")],
)
data class FeedEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sourceId: String,
    val matchKey: String,
    val description: String,
    val severity: String?,
    val downloadedAtMillis: Long,
)
