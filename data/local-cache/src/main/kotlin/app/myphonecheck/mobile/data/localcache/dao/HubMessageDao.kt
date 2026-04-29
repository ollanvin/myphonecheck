package app.myphonecheck.mobile.data.localcache.dao

import androidx.room.Dao
import androidx.room.Query

/**
 * Hub message DAO minimal stub (Stage 3-007 빌드 그래프 정합 정정).
 * 실 hub message 영역은 별 시리즈 — 본 stub은 빌드 그래프 정합용.
 */
@Dao
interface HubMessageDao {
    @Query("SELECT COUNT(*) FROM hub_message")
    suspend fun count(): Int
}
