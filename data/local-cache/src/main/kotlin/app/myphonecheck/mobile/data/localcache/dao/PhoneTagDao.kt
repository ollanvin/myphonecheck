package app.myphonecheck.mobile.data.localcache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.myphonecheck.mobile.data.localcache.entity.PhoneTagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PhoneTagDao {

    @Query("SELECT * FROM phone_tag WHERE identifierKey = :key AND identifierType = :type LIMIT 1")
    suspend fun findByKey(key: String, type: String): PhoneTagEntity?

    @Query("SELECT * FROM phone_tag ORDER BY priority ASC, lastSeenAtMillis DESC")
    fun observeAll(): Flow<List<PhoneTagEntity>>

    @Query("SELECT * FROM phone_tag WHERE priority = :priority ORDER BY lastSeenAtMillis DESC")
    fun observeByPriority(priority: String): Flow<List<PhoneTagEntity>>

    @Query(
        "UPDATE phone_tag SET lastSeenAtMillis = :time, seenCount = seenCount + 1 " +
            "WHERE identifierKey = :key AND identifierType = :type",
    )
    suspend fun recordSeen(key: String, type: String, time: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(tag: PhoneTagEntity)

    @Query("DELETE FROM phone_tag WHERE identifierKey = :key AND identifierType = :type")
    suspend fun delete(key: String, type: String)

    @Query(
        "SELECT * FROM phone_tag WHERE priority = 'REMIND_ME' " +
            "AND (lastSeenAtMillis IS NULL OR lastSeenAtMillis < :threshold) ORDER BY createdAtMillis ASC",
    )
    suspend fun pendingReminders(threshold: Long): List<PhoneTagEntity>

    @Query("SELECT COUNT(*) FROM phone_tag")
    suspend fun count(): Int

    @Query("DELETE FROM phone_tag")
    suspend fun clear()
}
