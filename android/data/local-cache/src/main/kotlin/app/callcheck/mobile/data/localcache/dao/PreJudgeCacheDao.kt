package app.callcheck.mobile.data.localcache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.callcheck.mobile.data.localcache.entity.PreJudgeCacheEntry

/**
 * Tier 0 PreJudge 캐시 DAO.
 *
 * 핵심 연산:
 * - lookup: 번호 hash → 0ms 판단 반환 (SELECT by indexed column)
 * - upsert: 판단 결과 저장/갱신
 * - evict: LRU 기반 용량 관리 (최대 500건)
 *
 * 성능 목표:
 * - lookup: <1ms (인덱스 기반, WAL 모드)
 * - upsert: <5ms
 */
@Dao
interface PreJudgeCacheDao {

    @Query("SELECT * FROM pre_judge_cache WHERE canonical_number = :canonicalNumber LIMIT 1")
    suspend fun lookup(canonicalNumber: String): PreJudgeCacheEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: PreJudgeCacheEntry): Long

    @Query("SELECT COUNT(*) FROM pre_judge_cache")
    suspend fun getCount(): Int

    @Query("""
        DELETE FROM pre_judge_cache WHERE id IN (
            SELECT id FROM pre_judge_cache
            ORDER BY hit_count ASC, last_judged_at ASC
            LIMIT :count
        )
    """)
    suspend fun evictOldest(count: Int)

    @Query("DELETE FROM pre_judge_cache WHERE canonical_number = :canonicalNumber")
    suspend fun deleteByNumber(canonicalNumber: String)

    @Query("DELETE FROM pre_judge_cache")
    suspend fun deleteAll()

    @Query("""
        UPDATE pre_judge_cache
        SET hit_count = hit_count + 1,
            last_judged_at = :now
        WHERE canonical_number = :canonicalNumber
    """)
    suspend fun incrementHit(canonicalNumber: String, now: Long = System.currentTimeMillis())

    @Query("""
        UPDATE pre_judge_cache
        SET last_user_action = :actionKey,
            last_judged_at = :now
        WHERE canonical_number = :canonicalNumber
    """)
    suspend fun updateUserAction(
        canonicalNumber: String,
        actionKey: String,
        now: Long = System.currentTimeMillis(),
    )
}
