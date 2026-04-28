package app.myphonecheck.mobile.data.localcache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.myphonecheck.mobile.data.localcache.entity.FeedEntryEntity

@Dao
interface FeedEntryDao {

    @Query("SELECT * FROM feed_entry WHERE sourceId = :sourceId AND matchKey = :key")
    suspend fun lookup(sourceId: String, key: String): List<FeedEntryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<FeedEntryEntity>)

    @Query("DELETE FROM feed_entry WHERE sourceId = :sourceId")
    suspend fun deleteSource(sourceId: String)

    @Query("SELECT COUNT(*) FROM feed_entry WHERE sourceId = :sourceId")
    suspend fun countForSource(sourceId: String): Int

    @Query("SELECT COUNT(*) FROM feed_entry")
    suspend fun count(): Int

    @Query("DELETE FROM feed_entry")
    suspend fun clear()
}
