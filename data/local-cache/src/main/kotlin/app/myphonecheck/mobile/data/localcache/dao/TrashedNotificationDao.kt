package app.myphonecheck.mobile.data.localcache.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import app.myphonecheck.mobile.data.localcache.entity.TrashedNotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrashedNotificationDao {

    @Insert
    suspend fun insert(entity: TrashedNotificationEntity): Long

    @Query("SELECT * FROM trashed_notifications ORDER BY capturedAt DESC")
    fun observeAll(): Flow<List<TrashedNotificationEntity>>

    @Query(
        "SELECT COUNT(*) FROM trashed_notifications WHERE capturedAt >= :sinceMillis",
    )
    fun observeCountSince(sinceMillis: Long): Flow<Int>

    @Delete
    suspend fun delete(entity: TrashedNotificationEntity)

    @Query("DELETE FROM trashed_notifications WHERE id = :id")
    suspend fun deleteById(id: Long)
}
