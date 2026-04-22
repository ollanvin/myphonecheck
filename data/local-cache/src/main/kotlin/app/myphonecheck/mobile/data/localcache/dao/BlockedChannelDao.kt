package app.myphonecheck.mobile.data.localcache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.myphonecheck.mobile.data.localcache.entity.BlockedChannelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockedChannelDao {

    @Query(
        "SELECT EXISTS(SELECT 1 FROM blocked_channels WHERE packageName = :packageName AND channelId = :channelId)",
    )
    suspend fun isBlocked(packageName: String, channelId: String): Boolean

    @Query("SELECT * FROM blocked_channels WHERE packageName = :packageName")
    fun observeForPackage(packageName: String): Flow<List<BlockedChannelEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: BlockedChannelEntity)

    @Query("DELETE FROM blocked_channels WHERE packageName = :packageName AND channelId = :channelId")
    suspend fun delete(packageName: String, channelId: String)

    @Query("DELETE FROM blocked_channels WHERE packageName = :packageName")
    suspend fun deleteAllForPackage(packageName: String)

    @Query("SELECT COUNT(*) FROM blocked_channels")
    suspend fun countAll(): Int
}
