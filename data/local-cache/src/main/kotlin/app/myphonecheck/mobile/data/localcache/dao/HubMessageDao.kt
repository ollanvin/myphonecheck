package app.myphonecheck.mobile.data.localcache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import app.myphonecheck.mobile.data.localcache.entity.HubMessageEntity
import kotlinx.coroutines.flow.Flow

/** Aggregation row for rolling weekly sender stats (WO-DATA-MSG-001). */
data class SenderProfileRow(
    val sender_identifier: String,
    val sender_label: String?,
    val count: Int,
    val last_at: Long,
    val link_count: Int,
)

@Dao
interface HubMessageDao {

    @Insert
    suspend fun insert(entity: HubMessageEntity): Long

    @Query(
        "SELECT * FROM hub_message ORDER BY received_at DESC LIMIT :limit",
    )
    fun observeRecent(limit: Int): Flow<List<HubMessageEntity>>

    @Query(
        "SELECT * FROM hub_message WHERE sender_identifier = :sender ORDER BY received_at DESC",
    )
    fun observeBySender(sender: String): Flow<List<HubMessageEntity>>

    @Query(
        """
        SELECT sender_identifier, sender_label, COUNT(*) AS count,
               MAX(received_at) AS last_at,
               SUM(CASE WHEN has_link THEN 1 ELSE 0 END) AS link_count
        FROM hub_message
        WHERE received_at >= :sinceMs
        GROUP BY sender_identifier, sender_label
        ORDER BY count DESC
        """,
    )
    fun observeSenderProfiles(sinceMs: Long): Flow<List<SenderProfileRow>>

    @Query("DELETE FROM hub_message WHERE received_at < :beforeMs")
    suspend fun deleteOlderThan(beforeMs: Long): Int

    @Query("SELECT COUNT(*) FROM hub_message")
    suspend fun count(): Int
}
