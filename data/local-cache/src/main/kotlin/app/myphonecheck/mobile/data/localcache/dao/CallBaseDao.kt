package app.myphonecheck.mobile.data.localcache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.myphonecheck.mobile.data.localcache.entity.CallBaseEntity

@Dao
interface CallBaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(entries: List<CallBaseEntity>)

    @Query("SELECT * FROM call_base_entry ORDER BY callCount DESC, lastCallMillis DESC")
    suspend fun getAll(): List<CallBaseEntity>

    @Query("SELECT COUNT(*) FROM call_base_entry")
    suspend fun count(): Int

    @Query("DELETE FROM call_base_entry")
    suspend fun clear()
}
