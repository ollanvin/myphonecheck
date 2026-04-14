package app.myphonecheck.mobile.data.localcache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.myphonecheck.mobile.data.localcache.entity.MessageHubEntity
import kotlinx.coroutines.flow.Flow

/**
 * MessageCheck Hub DAO.
 *
 * 모든 쿼리는 로컬 전용. 서버 동기화 없음.
 */
@Dao
interface MessageHubDao {

    // ── 조회 ──

    /** 전체 메시지 (최신순) */
    @Query("SELECT * FROM message_hub ORDER BY received_at DESC")
    fun observeAll(): Flow<List<MessageHubEntity>>

    /** 전체 메시지 Flow (UI 허브용 별칭) */
    @Query("SELECT * FROM message_hub ORDER BY received_at DESC")
    fun getAllFlow(): Flow<List<MessageHubEntity>>

    /** 앱별 메시지 (최신순) */
    @Query("SELECT * FROM message_hub WHERE package_name = :packageName ORDER BY received_at DESC")
    fun observeByPackage(packageName: String): Flow<List<MessageHubEntity>>

    /** 위험도별 메시지 (최신순) */
    @Query("SELECT * FROM message_hub WHERE risk_level = :riskLevel ORDER BY received_at DESC")
    fun observeByRiskLevel(riskLevel: String): Flow<List<MessageHubEntity>>

    /** 차단된 발신자의 메시지 */
    @Query("SELECT * FROM message_hub WHERE is_blocked = 1 ORDER BY received_at DESC")
    fun observeBlocked(): Flow<List<MessageHubEntity>>

    /** 링크가 포함된 메시지 */
    @Query("SELECT * FROM message_hub WHERE link_count > 0 ORDER BY received_at DESC")
    fun observeWithLinks(): Flow<List<MessageHubEntity>>

    /** 단건 조회 */
    @Query("SELECT * FROM message_hub WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): MessageHubEntity?

    /** 전체 레코드 수 */
    @Query("SELECT COUNT(*) FROM message_hub")
    suspend fun getCount(): Int

    /** 전체 기록 일괄 조회 (백업용) */
    @Query("SELECT * FROM message_hub ORDER BY received_at DESC")
    suspend fun getAllOnce(): List<MessageHubEntity>

    /** 특정 앱의 차단 여부 확인 */
    @Query("SELECT EXISTS(SELECT 1 FROM message_hub WHERE package_name = :packageName AND is_blocked = 1 LIMIT 1)")
    suspend fun isBlockedSender(packageName: String): Boolean

    // ── 구독 가치 앵커용 집계 쿼리 (since 기반) ──

    /**
     * 지정 시각 이후 차단된 발신자(package) 메시지 건수.
     * 현재 is_blocked=1로 전환된 발신자의 해당 기간 메시지 수.
     */
    @Query("SELECT COUNT(*) FROM message_hub WHERE is_blocked = 1 AND received_at >= :sinceMillis")
    suspend fun countBlockedSince(sinceMillis: Long): Int

    /**
     * 지정 시각 이후 위험 링크 포함 메시지 수.
     * 정의: link_count > 0 AND risk_level IN ('HIGH','MEDIUM').
     */
    @Query(
        "SELECT COUNT(*) FROM message_hub " +
            "WHERE link_count > 0 " +
            "AND risk_level IN ('HIGH','MEDIUM') " +
            "AND received_at >= :sinceMillis"
    )
    suspend fun countRiskyLinkMessagesSince(sinceMillis: Long): Int

    /**
     * 지정 시각 이후 반복 메시지 건수.
     * 정의: 같은 package_name에서 3건 이상 수신된 그룹의 총 메시지 수 합.
     * 동일 앱/발신자가 집요하게 반복 송신하는 패턴 탐지.
     */
    @Query(
        "SELECT COALESCE(SUM(cnt), 0) FROM (" +
            "SELECT COUNT(*) AS cnt FROM message_hub " +
            "WHERE received_at >= :sinceMillis " +
            "GROUP BY package_name " +
            "HAVING cnt >= 3" +
            ")"
    )
    suspend fun countRepeatMessagesSince(sinceMillis: Long): Int

    /**
     * 지정 시각 이후 고유 알림 송신자(앱) 수.
     * 알림 송신자 통계 지표로 사용.
     */
    @Query("SELECT COUNT(DISTINCT package_name) FROM message_hub WHERE received_at >= :sinceMillis")
    suspend fun countDistinctSendersSince(sinceMillis: Long): Int

    // ── 삽입 ──

    /** 새 메시지 삽입 */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: MessageHubEntity): Long

    /** 일괄 삽입 (백업 복원용) */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<MessageHubEntity>)

    // ── 업데이트 ──

    /** 차단 상태 변경 (특정 메시지) */
    @Query("UPDATE message_hub SET is_blocked = :blocked WHERE id = :id")
    suspend fun updateBlocked(id: Long, blocked: Boolean)

    /** 특정 앱 전체 차단/해제 */
    @Query("UPDATE message_hub SET is_blocked = :blocked WHERE package_name = :packageName")
    suspend fun updateBlockedByPackage(packageName: String, blocked: Boolean)

    /** 발신 앱 패키지 기준 차단 (허브 UI) */
    @Query("UPDATE message_hub SET is_blocked = 1 WHERE package_name = :senderIdentifier")
    suspend fun blockSender(senderIdentifier: String)

    /** 사용자 메모 업데이트 */
    @Query("UPDATE message_hub SET user_memo = :memo WHERE id = :id")
    suspend fun updateMemo(id: Long, memo: String?)

    // ── 삭제 ──

    /** 단건 삭제 */
    @Query("DELETE FROM message_hub WHERE id = :id")
    suspend fun deleteById(id: Long)

    /** 앱별 전체 삭제 */
    @Query("DELETE FROM message_hub WHERE package_name = :packageName")
    suspend fun deleteByPackage(packageName: String)

    /** 전체 삭제 (사용자 명시적 요청 시에만) */
    @Query("DELETE FROM message_hub")
    suspend fun deleteAll()

    /** 오래된 기록 정리 (retention 기간 초과) */
    @Query("DELETE FROM message_hub WHERE received_at < :cutoffMillis")
    suspend fun deleteOlderThan(cutoffMillis: Long)
}
