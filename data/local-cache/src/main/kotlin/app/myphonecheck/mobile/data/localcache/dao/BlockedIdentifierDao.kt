package app.myphonecheck.mobile.data.localcache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.myphonecheck.mobile.data.localcache.entity.BlockedIdentifierEntity

@Dao
interface BlockedIdentifierDao {

    @Query("SELECT EXISTS(SELECT 1 FROM blocked_identifier WHERE key = :key AND type = :type LIMIT 1)")
    suspend fun isBlocked(key: String, type: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: BlockedIdentifierEntity)

    @Query("DELETE FROM blocked_identifier WHERE key = :key AND type = :type")
    suspend fun delete(key: String, type: String)

    @Query("SELECT * FROM blocked_identifier ORDER BY addedAtMillis DESC")
    suspend fun listAll(): List<BlockedIdentifierEntity>

    @Query("SELECT COUNT(*) FROM blocked_identifier")
    suspend fun count(): Int

    @Query("DELETE FROM blocked_identifier")
    suspend fun clear()
}
