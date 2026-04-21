package app.myphonecheck.mobile.data.localcache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.myphonecheck.mobile.data.localcache.entity.InitialScanMetaEntity
import kotlinx.coroutines.flow.Flow

/**
 * Initial Scan 메타데이터 DAO.
 *
 * 단일 레코드 (id=1) — REPLACE 전략으로 항상 최신값 유지.
 * 로컬 전용, 서버 동기화 없음.
 */
@Dao
interface InitialScanMetaDao {

    /** 메타데이터 Flow (UI 바인딩용 — Guard 실시간 반영) */
    @Query("SELECT * FROM initial_scan_meta WHERE id = 1 LIMIT 1")
    fun getMetaFlow(): Flow<InitialScanMetaEntity?>

    /** 메타데이터 1회 조회 */
    @Query("SELECT * FROM initial_scan_meta WHERE id = 1 LIMIT 1")
    suspend fun getMeta(): InitialScanMetaEntity?

    /** 메타데이터 저장/갱신 (id=1 고정 → REPLACE) */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: InitialScanMetaEntity)

    /** 메타데이터 삭제 (수동 재초기화 시) */
    @Query("DELETE FROM initial_scan_meta")
    suspend fun deleteAll()
}
