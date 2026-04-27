package app.myphonecheck.mobile.data.localcache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.myphonecheck.mobile.data.localcache.entity.SmsBaseEntity

@Dao
interface SmsBaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(entries: List<SmsBaseEntity>)

    @Query("SELECT * FROM sms_base_entry ORDER BY messageCount DESC, lastSeenMillis DESC")
    suspend fun getAll(): List<SmsBaseEntity>

    @Query("SELECT COUNT(*) FROM sms_base_entry")
    suspend fun count(): Int

    @Query("DELETE FROM sms_base_entry")
    suspend fun clear()
}
