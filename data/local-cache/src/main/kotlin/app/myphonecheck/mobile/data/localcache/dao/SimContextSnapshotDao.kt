package app.myphonecheck.mobile.data.localcache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.myphonecheck.mobile.data.localcache.entity.SimContextSnapshotEntity

@Dao
interface SimContextSnapshotDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(snapshot: SimContextSnapshotEntity)

    @Query("SELECT * FROM sim_context_snapshot WHERE id = :id LIMIT 1")
    suspend fun get(id: Long = SimContextSnapshotEntity.SINGLETON_ID): SimContextSnapshotEntity?

    @Query("DELETE FROM sim_context_snapshot")
    suspend fun clear()
}
