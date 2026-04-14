package app.myphonecheck.mobile.data.localcache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.myphonecheck.mobile.data.localcache.entity.MessageHubEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageHubDao {

    @Query("SELECT * FROM message_hub ORDER BY received_at DESC")
    fun observeAll(): Flow<List<MessageHubEntity>>

    @Query("SELECT * FROM message_hub ORDER BY received_at DESC")
    fun getAllFlow(): Flow<List<MessageHubEntity>>

    @Query("SELECT * FROM message_hub WHERE package_name = :packageName ORDER BY received_at DESC")
    fun observeByPackage(packageName: String): Flow<List<MessageHubEntity>>

    @Query("SELECT * FROM message_hub WHERE risk_level = :riskLevel ORDER BY received_at DESC")
    fun observeByRiskLevel(riskLevel: String): Flow<List<MessageHubEntity>>

    @Query("SELECT * FROM message_hub WHERE is_blocked = 1 ORDER BY received_at DESC")
    fun observeBlocked(): Flow<List<MessageHubEntity>>

    @Query("SELECT * FROM message_hub WHERE link_count > 0 ORDER BY received_at DESC")
    fun observeWithLinks(): Flow<List<MessageHubEntity>>

    @Query("SELECT * FROM message_hub WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): MessageHubEntity?

    @Query("SELECT COUNT(*) FROM message_hub")
    suspend fun getCount(): Int

    @Query("SELECT * FROM message_hub ORDER BY received_at DESC")
    suspend fun getAllOnce(): List<MessageHubEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM message_hub WHERE package_name = :packageName AND is_blocked = 1 LIMIT 1)")
    suspend fun isBlockedSender(packageName: String): Boolean

    @Query("SELECT COUNT(*) FROM message_hub WHERE is_blocked = 1 AND received_at >= :sinceMillis")
    suspend fun countBlockedSince(sinceMillis: Long): Int

    @Query(
        "SELECT COUNT(*) FROM message_hub " +
            "WHERE link_count > 0 " +
            "AND risk_level IN ('HIGH','MEDIUM') " +
            "AND received_at >= :sinceMillis"
    )
    suspend fun countRiskyLinkMessagesSince(sinceMillis: Long): Int

    @Query(
        "SELECT COUNT(*) FROM message_hub " +
            "WHERE link_count > 0 " +
            "AND risk_level IN ('HIGH','MEDIUM')"
    )
    suspend fun countRiskyLinkMessagesCumulative(): Int

    @Query(
        "SELECT COALESCE(SUM(cnt), 0) FROM (" +
            "SELECT COUNT(*) AS cnt FROM message_hub " +
            "WHERE received_at >= :sinceMillis " +
            "GROUP BY package_name " +
            "HAVING cnt >= 3" +
            ")"
    )
    suspend fun countRepeatMessagesSince(sinceMillis: Long): Int

    @Query("SELECT COUNT(DISTINCT package_name) FROM message_hub WHERE received_at >= :sinceMillis")
    suspend fun countDistinctSendersSince(sinceMillis: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: MessageHubEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<MessageHubEntity>)

    @Query("UPDATE message_hub SET is_blocked = :blocked WHERE id = :id")
    suspend fun updateBlocked(id: Long, blocked: Boolean)

    @Query("UPDATE message_hub SET is_blocked = :blocked WHERE package_name = :packageName")
    suspend fun updateBlockedByPackage(packageName: String, blocked: Boolean)

    @Query("UPDATE message_hub SET is_blocked = 1 WHERE package_name = :senderIdentifier")
    suspend fun blockSender(senderIdentifier: String)

    @Query("UPDATE message_hub SET user_memo = :memo WHERE id = :id")
    suspend fun updateMemo(id: Long, memo: String?)

    @Query("DELETE FROM message_hub WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM message_hub WHERE package_name = :packageName")
    suspend fun deleteByPackage(packageName: String)

    @Query("DELETE FROM message_hub")
    suspend fun deleteAll()

    @Query("DELETE FROM message_hub WHERE received_at < :cutoffMillis")
    suspend fun deleteOlderThan(cutoffMillis: Long)
}
