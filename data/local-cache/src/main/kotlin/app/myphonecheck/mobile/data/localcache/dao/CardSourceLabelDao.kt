package app.myphonecheck.mobile.data.localcache.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.myphonecheck.mobile.data.localcache.entity.CardSourceLabelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CardSourceLabelDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: CardSourceLabelEntity)

    @Query("SELECT * FROM card_source_label WHERE sourceId = :sourceId LIMIT 1")
    suspend fun find(sourceId: String): CardSourceLabelEntity?

    @Query("SELECT * FROM card_source_label ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<CardSourceLabelEntity>>

    @Query("SELECT COUNT(*) FROM card_source_label")
    suspend fun count(): Int

    @Delete
    suspend fun delete(entity: CardSourceLabelEntity)

    @Query("DELETE FROM card_source_label WHERE sourceId = :sourceId")
    suspend fun deleteById(sourceId: String)
}
