package app.myphonecheck.mobile.data.localcache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.myphonecheck.mobile.data.localcache.entity.NumberProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NumberProfileDao {
    @Query("SELECT * FROM number_profiles WHERE normalized_number = :normalizedNumber LIMIT 1")
    suspend fun findByNumber(normalizedNumber: String): NumberProfileEntity?

    @Query("SELECT * FROM number_profiles WHERE normalized_number = :normalizedNumber LIMIT 1")
    fun observeByNumber(normalizedNumber: String): Flow<NumberProfileEntity?>

    @Query("SELECT * FROM number_profiles ORDER BY updated_at DESC")
    fun observeAll(): Flow<List<NumberProfileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: NumberProfileEntity)
}
