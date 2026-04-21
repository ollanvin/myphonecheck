package app.myphonecheck.mobile.data.localcache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.myphonecheck.mobile.data.localcache.entity.SensorScanResultEntity
import kotlinx.coroutines.flow.Flow

/**
 * Initial Scan 결과 DAO.
 *
 * sensor_type UNIQUE 제약 → REPLACE 전략으로 항상 최신 1건 유지.
 * 로컬 전용, 서버 동기화 없음.
 */
@Dao
interface SensorScanResultDao {

    /** 특정 센서 스캔 결과 Flow (UI 바인딩용) */
    @Query("SELECT * FROM sensor_scan_result WHERE sensor_type = :sensorType LIMIT 1")
    fun getResultFlow(sensorType: String): Flow<SensorScanResultEntity?>

    /** 특정 센서 스캔 결과 (1회 조회) */
    @Query("SELECT * FROM sensor_scan_result WHERE sensor_type = :sensorType LIMIT 1")
    suspend fun getResult(sensorType: String): SensorScanResultEntity?

    /** 전체 스캔 결과 Flow */
    @Query("SELECT * FROM sensor_scan_result ORDER BY scanned_at DESC")
    fun getAllFlow(): Flow<List<SensorScanResultEntity>>

    /** 스캔 결과 저장 (REPLACE: sensor_type UNIQUE 기준) */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: SensorScanResultEntity)

    /** 전체 삭제 */
    @Query("DELETE FROM sensor_scan_result")
    suspend fun deleteAll()
}
