package app.myphonecheck.mobile.data.localcache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.myphonecheck.mobile.data.localcache.entity.PrivacyHistoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * PrivacyCheck 히스토리 DAO.
 *
 * 모든 쿼리는 로컬 전용. 서버 동기화 없음.
 */
@Dao
interface PrivacyHistoryDao {

    // ── 조회 ──

    /** 전체 히스토리 (최신순) */
    @Query("SELECT * FROM privacy_history ORDER BY used_at DESC")
    fun getAllFlow(): Flow<List<PrivacyHistoryEntity>>

    /** 미확인 이상 탐지 항목 */
    @Query("SELECT * FROM privacy_history WHERE is_anomaly = 1 AND user_verified = 'UNVERIFIED' ORDER BY used_at DESC")
    fun getUnverifiedAnomaliesFlow(): Flow<List<PrivacyHistoryEntity>>

    /** 앱별 히스토리 */
    @Query("SELECT * FROM privacy_history WHERE app_package = :packageName ORDER BY used_at DESC")
    fun getByPackageFlow(packageName: String): Flow<List<PrivacyHistoryEntity>>

    /** 권한 유형별 히스토리 */
    @Query("SELECT * FROM privacy_history WHERE permission_type = :type ORDER BY used_at DESC")
    fun getByPermissionTypeFlow(type: String): Flow<List<PrivacyHistoryEntity>>

    /** 특정 앱+권한의 과거 사용 이력 존재 여부 */
    @Query("SELECT EXISTS(SELECT 1 FROM privacy_history WHERE app_package = :packageName AND permission_type = :type LIMIT 1)")
    suspend fun hasHistory(packageName: String, type: String): Boolean

    /** 최근 구간 내 동일 앱·권한 접근 횟수 (비정상 빈도 판단용) */
    @Query(
        "SELECT COUNT(*) FROM privacy_history WHERE app_package = :packageName AND permission_type = :type AND used_at >= :sinceMillis",
    )
    suspend fun countAccessSince(packageName: String, type: String, sinceMillis: Long): Int

    /** 전체 레코드 수 */
    @Query("SELECT COUNT(*) FROM privacy_history")
    suspend fun getCount(): Int

    /** 전체 기록 일괄 조회 (백업용) */
    @Query("SELECT * FROM privacy_history ORDER BY used_at DESC")
    suspend fun getAllOnce(): List<PrivacyHistoryEntity>

    // ── 삽입 ──

    /** 새 기록 삽입 */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: PrivacyHistoryEntity): Long

    /** 일괄 삽입 (백업 복원용) */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<PrivacyHistoryEntity>)

    // ── 업데이트 ──

    /** 사용자 확인 상태 업데이트 */
    @Query("UPDATE privacy_history SET user_verified = :verified WHERE id = :id")
    suspend fun updateVerified(id: Long, verified: String)

    // ── 삭제 ──

    /** 보존 기간 초과 기록 정리 */
    @Query("DELETE FROM privacy_history WHERE used_at < :cutoff")
    suspend fun deleteOlderThan(cutoff: Long)

    /** 전체 삭제 (사용자 명시적 요청 시에만) */
    @Query("DELETE FROM privacy_history")
    suspend fun deleteAll()
}
