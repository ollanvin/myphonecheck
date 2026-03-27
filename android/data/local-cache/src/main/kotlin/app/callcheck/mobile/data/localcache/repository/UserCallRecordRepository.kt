package app.callcheck.mobile.data.localcache.repository

import app.callcheck.mobile.core.model.UserCallAction
import app.callcheck.mobile.core.model.UserCallTag
import app.callcheck.mobile.data.localcache.dao.UserCallRecordDao
import app.callcheck.mobile.data.localcache.entity.UserCallRecord
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 사용자 통화 기록 Repository.
 *
 * 비즈니스 로직 계층:
 *  - DAO 직접 호출 방지
 *  - upsert 로직 (존재하면 업데이트, 없으면 신규 생성)
 *  - 타입 안전 (UserCallTag, UserCallAction enum 사용)
 *
 * 서버 동기화: 없음 (온디바이스 전용)
 */
@Singleton
class UserCallRecordRepository @Inject constructor(
    private val dao: UserCallRecordDao,
) {
    // ── 조회 ──

    /** 번호로 기록 조회 */
    suspend fun findByNumber(canonicalNumber: String): UserCallRecord? {
        return dao.findByNumber(canonicalNumber)
    }

    /** 번호 실시간 관찰 */
    fun observeByNumber(canonicalNumber: String): Flow<UserCallRecord?> {
        return dao.observeByNumber(canonicalNumber)
    }

    /** 전체 기록 관찰 (최신순) */
    fun observeAll(): Flow<List<UserCallRecord>> {
        return dao.observeAll()
    }

    /** 태그별 필터 */
    fun observeByTag(tag: UserCallTag): Flow<List<UserCallRecord>> {
        return dao.observeByTag(tag.displayKey)
    }

    /** 차단 목록 */
    fun observeBlockedNumbers(): Flow<List<UserCallRecord>> {
        return dao.observeBlockedNumbers()
    }

    /** 메모가 있는 기록만 */
    fun observeWithMemos(): Flow<List<UserCallRecord>> {
        return dao.observeWithMemos()
    }

    /** 전체 기록 수 */
    suspend fun getRecordCount(): Int {
        return dao.getRecordCount()
    }

    // ── 기록 ──

    /**
     * 통화 기록 저장 (upsert).
     *
     * - 이미 존재하면: callCount 증가 + lastAction/aiRiskLevel 업데이트
     * - 신규면: 새 레코드 생성
     */
    suspend fun recordCall(
        canonicalNumber: String,
        displayNumber: String,
        action: UserCallAction,
        aiRiskLevel: String? = null,
        aiCategory: String? = null,
    ): UserCallRecord {
        val existing = dao.findByNumber(canonicalNumber)
        val now = System.currentTimeMillis()

        return if (existing != null) {
            val updated = existing.copy(
                displayNumber = displayNumber,
                lastAction = action.displayKey,
                aiRiskLevel = aiRiskLevel ?: existing.aiRiskLevel,
                aiCategory = aiCategory ?: existing.aiCategory,
                callCount = existing.callCount + 1,
                updatedAt = now,
            )
            dao.upsert(updated)
            updated
        } else {
            val newRecord = UserCallRecord(
                canonicalNumber = canonicalNumber,
                displayNumber = displayNumber,
                lastAction = action.displayKey,
                aiRiskLevel = aiRiskLevel,
                aiCategory = aiCategory,
                callCount = 1,
                createdAt = now,
                updatedAt = now,
            )
            val id = dao.upsert(newRecord)
            newRecord.copy(id = id)
        }
    }

    /** 메모 저장 */
    suspend fun saveMemo(canonicalNumber: String, memo: String) {
        val existing = dao.findByNumber(canonicalNumber)
        if (existing != null) {
            dao.updateMemo(canonicalNumber, memo)
        }
    }

    /** 태그 저장 */
    suspend fun saveTag(canonicalNumber: String, tag: UserCallTag) {
        val existing = dao.findByNumber(canonicalNumber)
        if (existing != null) {
            dao.updateTag(canonicalNumber, tag.displayKey)
        }
    }

    /** 번호 차단 */
    suspend fun blockNumber(canonicalNumber: String) {
        val existing = dao.findByNumber(canonicalNumber)
        if (existing != null) {
            dao.updateAction(canonicalNumber, UserCallAction.BLOCKED.displayKey)
        }
    }

    /** 기록 삭제 */
    suspend fun deleteRecord(canonicalNumber: String) {
        dao.deleteByNumber(canonicalNumber)
    }

    /** 전체 삭제 (사용자 명시적 요청) */
    suspend fun deleteAllRecords() {
        dao.deleteAll()
    }
}
