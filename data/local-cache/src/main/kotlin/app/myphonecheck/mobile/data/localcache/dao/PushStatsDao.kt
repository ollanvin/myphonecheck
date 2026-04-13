package app.myphonecheck.mobile.data.localcache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.myphonecheck.mobile.data.localcache.entity.PushStatsEntity
import kotlinx.coroutines.flow.Flow

/**
 * 푸시 알림 통계 DAO.
 *
 * 원자적 increment 쿼리로 동시성 안전하게 통계를 집계합니다.
 * PushInterceptService → PushStatsDao.incrementXxx() 호출 구조.
 *
 * 주간 집계는 dateKey BETWEEN 쿼리로 처리.
 * 모든 쿼리는 로컬 전용, 서버 동기화 없음.
 */
@Dao
interface PushStatsDao {

    // ── 원자적 Increment ──

    /**
     * 알림 카운트 원자적 증가.
     * row가 없으면 INSERT 후 증가해야 하므로 ensureRow() 선행 필요.
     */
    @Query("""
        UPDATE push_stats
        SET total_count = total_count + 1, updated_at = :now
        WHERE package_name = :packageName AND date_key = :dateKey
    """)
    suspend fun incrementTotal(packageName: String, dateKey: String, now: Long = System.currentTimeMillis())

    /** 야간 알림 카운트 원자적 증가 */
    @Query("""
        UPDATE push_stats
        SET night_count = night_count + 1, updated_at = :now
        WHERE package_name = :packageName AND date_key = :dateKey
    """)
    suspend fun incrementNight(packageName: String, dateKey: String, now: Long = System.currentTimeMillis())

    /** 프로모션 알림 카운트 원자적 증가 */
    @Query("""
        UPDATE push_stats
        SET promotion_count = promotion_count + 1, updated_at = :now
        WHERE package_name = :packageName AND date_key = :dateKey
    """)
    suspend fun incrementPromotion(packageName: String, dateKey: String, now: Long = System.currentTimeMillis())

    /** 링크 포함 알림 카운트 원자적 증가 */
    @Query("""
        UPDATE push_stats
        SET link_count = link_count + 1, updated_at = :now
        WHERE package_name = :packageName AND date_key = :dateKey
    """)
    suspend fun incrementLink(packageName: String, dateKey: String, now: Long = System.currentTimeMillis())

    /** HIGH 위험도 알림 카운트 원자적 증가 */
    @Query("""
        UPDATE push_stats
        SET high_risk_count = high_risk_count + 1, updated_at = :now
        WHERE package_name = :packageName AND date_key = :dateKey
    """)
    suspend fun incrementHighRisk(packageName: String, dateKey: String, now: Long = System.currentTimeMillis())

    // ── Row 보장 (INSERT OR IGNORE) ──

    /**
     * 해당 앱+날짜의 row가 없으면 생성.
     * IGNORE 전략으로 이미 존재하면 무시.
     * increment 전에 반드시 호출.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun ensureRow(entity: PushStatsEntity)

    // ── 조회 ──

    /** 특정 날짜의 앱별 통계 (총 알림 내림차순) */
    @Query("SELECT * FROM push_stats WHERE date_key = :dateKey ORDER BY total_count DESC")
    fun observeByDate(dateKey: String): Flow<List<PushStatsEntity>>

    /** 특정 앱의 날짜별 통계 (최신 날짜순) */
    @Query("SELECT * FROM push_stats WHERE package_name = :packageName ORDER BY date_key DESC")
    fun observeByPackage(packageName: String): Flow<List<PushStatsEntity>>

    /** 주간 집계: 특정 기간 내 앱별 총 알림 합계 (상위 N개) */
    @Query("""
        SELECT package_name, app_label, '' AS date_key,
               SUM(total_count) AS total_count,
               SUM(night_count) AS night_count,
               SUM(promotion_count) AS promotion_count,
               SUM(link_count) AS link_count,
               SUM(high_risk_count) AS high_risk_count,
               MAX(updated_at) AS updated_at
        FROM push_stats
        WHERE date_key BETWEEN :startDate AND :endDate
        GROUP BY package_name
        ORDER BY total_count DESC
        LIMIT :limit
    """)
    suspend fun getWeeklyAggregation(
        startDate: String,
        endDate: String,
        limit: Int = 20,
    ): List<PushStatsEntity>

    /** 전체 날짜의 총합 (리포트용) */
    @Query("""
        SELECT '' AS package_name, '' AS app_label, '' AS date_key,
               SUM(total_count) AS total_count,
               SUM(night_count) AS night_count,
               SUM(promotion_count) AS promotion_count,
               SUM(link_count) AS link_count,
               SUM(high_risk_count) AS high_risk_count,
               MAX(updated_at) AS updated_at
        FROM push_stats
        WHERE date_key BETWEEN :startDate AND :endDate
    """)
    suspend fun getTotalAggregation(startDate: String, endDate: String): PushStatsEntity?

    /** 오늘 통계 */
    @Query("SELECT * FROM push_stats WHERE date_key = :today ORDER BY total_count DESC")
    suspend fun getTodayStats(today: String): List<PushStatsEntity>

    /** 전체 레코드 수 */
    @Query("SELECT COUNT(*) FROM push_stats")
    suspend fun getCount(): Int

    // ── 삭제 ──

    /** 오래된 통계 정리 */
    @Query("DELETE FROM push_stats WHERE date_key < :cutoffDate")
    suspend fun deleteOlderThan(cutoffDate: String)

    /** 전체 삭제 */
    @Query("DELETE FROM push_stats")
    suspend fun deleteAll()
}
