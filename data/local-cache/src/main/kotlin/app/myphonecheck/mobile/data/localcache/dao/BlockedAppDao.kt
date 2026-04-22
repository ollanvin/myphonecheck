package app.myphonecheck.mobile.data.localcache.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import app.myphonecheck.mobile.data.localcache.entity.BlockedAppEntity

@Dao
interface BlockedAppDao {

    @Query("SELECT * FROM blocked_apps WHERE packageName = :packageName LIMIT 1")
    suspend fun find(packageName: String): BlockedAppEntity?

    @Upsert
    suspend fun upsert(entity: BlockedAppEntity)

    @Query("DELETE FROM blocked_apps WHERE packageName = :packageName")
    suspend fun delete(packageName: String)

    @Query("SELECT COUNT(*) FROM blocked_apps WHERE mode = 'all_blocked'")
    suspend fun countAllBlocked(): Int
}
