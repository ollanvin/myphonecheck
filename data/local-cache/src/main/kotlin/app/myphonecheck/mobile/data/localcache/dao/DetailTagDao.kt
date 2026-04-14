package app.myphonecheck.mobile.data.localcache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.myphonecheck.mobile.data.localcache.entity.DetailTagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DetailTagDao {
    @Query("SELECT * FROM detail_tags WHERE normalized_number = :normalizedNumber ORDER BY updated_at DESC, tag_name ASC")
    fun observeByNumber(normalizedNumber: String): Flow<List<DetailTagEntity>>

    @Query("SELECT * FROM detail_tags ORDER BY updated_at DESC, tag_name ASC")
    fun observeAll(): Flow<List<DetailTagEntity>>

    @Query("SELECT * FROM detail_tags WHERE normalized_number = :normalizedNumber ORDER BY updated_at DESC, tag_name ASC")
    suspend fun getByNumber(normalizedNumber: String): List<DetailTagEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: DetailTagEntity)

    @Query("DELETE FROM detail_tags WHERE normalized_number = :normalizedNumber AND tag_name = :tagName")
    suspend fun deleteTag(normalizedNumber: String, tagName: String)
}
