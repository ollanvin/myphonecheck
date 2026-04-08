package app.callcheck.mobile.data.localcache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import app.callcheck.mobile.data.localcache.entity.UserCallRecord
import kotlinx.coroutines.flow.Flow

/**
 * 사용자 통화 기록 DAO.
 *
 * 모든 쿼리는 로컬 전용. 서버 동기화 없음.
 */
@Dao
interface UserCallRecordDao {

    // ── 조회 ──

    /** 정규화 번호로 단건 조회 */
    @Query("SELECT * FROM user_call_records WHERE canonical_number = :canonicalNumber LIMIT 1")
    suspend fun findByNumber(canonicalNumber: String): UserCallRecord?

    /** 정규화 번호로 실시간 관찰 */
    @Query("SELECT * FROM user_call_records WHERE canonical_number = :canonicalNumber LIMIT 1")
    fun observeByNumber(canonicalNumber: String): Flow<UserCallRecord?>

    /** 전체 기록 (최신순) */
    @Query("SELECT * FROM user_call_records ORDER BY updated_at DESC")
    fun observeAll(): Flow<List<UserCallRecord>>

    /** 태그별 필터 */
    @Query("SELECT * FROM user_call_records WHERE tag = :tag ORDER BY updated_at DESC")
    fun observeByTag(tag: String): Flow<List<UserCallRecord>>

    /** 차단 목록 */
    @Query("SELECT * FROM user_call_records WHERE last_action = 'blocked' ORDER BY updated_at DESC")
    fun observeBlockedNumbers(): Flow<List<UserCallRecord>>

    /** 전체 기록 수 */
    @Query("SELECT COUNT(*) FROM user_call_records")
    suspend fun getRecordCount(): Int

    /** 전체 기록 일괄 조회 (백업용) */
    @Query("SELECT * FROM user_call_records ORDER BY updated_at DESC")
    suspend fun getAllOnce(): List<UserCallRecord>

    /** 메모가 있는 기록만 */
    @Query("SELECT * FROM user_call_records WHERE memo IS NOT NULL AND memo != '' ORDER BY updated_at DESC")
    fun observeWithMemos(): Flow<List<UserCallRecord>>

    // ── 삽입/업데이트 ──

    /** 새 기록 삽입 (충돌 시 교체) */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(record: UserCallRecord): Long

    /** 일괄 삽입 (백업 복원용) */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(records: List<UserCallRecord>)

    /** 기존 기록 업데이트 */
    @Update
    suspend fun update(record: UserCallRecord)

    /** 메모만 업데이트 */
    @Query("UPDATE user_call_records SET memo = :memo, updated_at = :updatedAt WHERE canonical_number = :canonicalNumber")
    suspend fun updateMemo(canonicalNumber: String, memo: String, updatedAt: Long = System.currentTimeMillis())

    /** 태그만 업데이트 */
    @Query("UPDATE user_call_records SET tag = :tag, updated_at = :updatedAt WHERE canonical_number = :canonicalNumber")
    suspend fun updateTag(canonicalNumber: String, tag: String, updatedAt: Long = System.currentTimeMillis())

    /** 행동만 업데이트 */
    @Query("UPDATE user_call_records SET last_action = :action, updated_at = :updatedAt WHERE canonical_number = :canonicalNumber")
    suspend fun updateAction(canonicalNumber: String, action: String, updatedAt: Long = System.currentTimeMillis())

    /** 통화 횟수 증가 */
    @Query("UPDATE user_call_records SET call_count = call_count + 1, updated_at = :updatedAt WHERE canonical_number = :canonicalNumber")
    suspend fun incrementCallCount(canonicalNumber: String, updatedAt: Long = System.currentTimeMillis())

    // ── 삭제 ──

    /** 번호별 삭제 */
    @Query("DELETE FROM user_call_records WHERE canonical_number = :canonicalNumber")
    suspend fun deleteByNumber(canonicalNumber: String)

    /** 전체 삭제 (사용자 명시적 요청 시에만) */
    @Query("DELETE FROM user_call_records")
    suspend fun deleteAll()
}
