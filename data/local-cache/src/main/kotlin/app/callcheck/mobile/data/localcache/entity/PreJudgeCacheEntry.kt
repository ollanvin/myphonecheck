package app.callcheck.mobile.data.localcache.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Tier 0 — PreJudge 영속 캐시 엔티티.
 *
 * 전화가 울리기 전 0ms 판단을 위한 Room 테이블.
 * 모든 과거 판단 결과를 축적하여, 동일 번호 재수신 시
 * 엔진 실행 없이 즉시 Ring 상태를 반환.
 *
 * 설계:
 * - Key: canonical_number (E.164)
 * - 최대 500건 (LRU eviction)
 * - 7일 미사용 시 confidence 감쇠 (soft expire)
 * - 앱 삭제 시 자동 소멸
 * - 서버 전송 없음, 온디바이스 전용
 */
@Entity(
    tableName = "pre_judge_cache",
    indices = [
        Index(value = ["canonical_number"], unique = true),
        Index(value = ["last_judged_at"]),
        Index(value = ["hit_count"]),
    ]
)
data class PreJudgeCacheEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "canonical_number")
    val canonicalNumber: String,

    @ColumnInfo(name = "action")
    val action: String,

    @ColumnInfo(name = "risk_score")
    val riskScore: Float,

    @ColumnInfo(name = "category")
    val category: String,

    @ColumnInfo(name = "confidence")
    val confidence: Float,

    @ColumnInfo(name = "summary")
    val summary: String,

    @ColumnInfo(name = "hit_count")
    val hitCount: Int = 1,

    @ColumnInfo(name = "last_user_action")
    val lastUserAction: String? = null,

    @ColumnInfo(name = "last_judged_at")
    val lastJudgedAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
)
