package app.myphonecheck.mobile.data.localcache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import app.myphonecheck.mobile.data.localcache.entity.BackupMetadataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BackupMetadataDao {

    @Insert
    suspend fun insert(metadata: BackupMetadataEntity): Long

    @Query("SELECT * FROM backup_metadata ORDER BY created_at DESC LIMIT 1")
    suspend fun getLatest(): BackupMetadataEntity?

    @Query("SELECT * FROM backup_metadata ORDER BY created_at DESC")
    fun getAllFlow(): Flow<List<BackupMetadataEntity>>

    @Query("SELECT * FROM backup_metadata ORDER BY created_at DESC")
    suspend fun getAll(): List<BackupMetadataEntity>

    @Query("DELETE FROM backup_metadata WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM backup_metadata")
    suspend fun deleteAll()
}
