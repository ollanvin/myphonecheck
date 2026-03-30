package app.callcheck.mobile.data.localcache.repository

import app.callcheck.mobile.core.model.ActionRecommendation
import app.callcheck.mobile.core.model.ConclusionCategory
import app.callcheck.mobile.core.model.DecisionResult
import app.callcheck.mobile.core.model.PreJudgeResult
import app.callcheck.mobile.core.model.UserCallAction
import app.callcheck.mobile.data.localcache.dao.PreJudgeCacheDao
import app.callcheck.mobile.data.localcache.entity.PreJudgeCacheEntry
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Tier 0 PreJudge 캐시 Repository.
 *
 * Entity ↔ Model 변환 + LRU eviction 비즈니스 로직.
 */
@Singleton
class PreJudgeCacheRepository @Inject constructor(
    private val dao: PreJudgeCacheDao,
) {
    /**
     * Tier 0 핵심: 번호 hash lookup → PreJudgeResult.
     */
    suspend fun lookup(canonicalNumber: String): PreJudgeResult? {
        val entry = dao.lookup(canonicalNumber) ?: return null

        val action = try {
            ActionRecommendation.valueOf(entry.action)
        } catch (_: Exception) {
            return null
        }

        val category = try {
            ConclusionCategory.valueOf(entry.category)
        } catch (_: Exception) {
            ConclusionCategory.INSUFFICIENT_EVIDENCE
        }

        val lastUserAction = entry.lastUserAction?.let { key ->
            try {
                UserCallAction.entries.firstOrNull { it.displayKey == key }
            } catch (_: Exception) {
                null
            }
        }

        return PreJudgeResult(
            canonicalNumber = entry.canonicalNumber,
            action = action,
            riskScore = entry.riskScore,
            category = category,
            confidence = entry.confidence,
            summary = entry.summary,
            hitCount = entry.hitCount,
            lastJudgedAtMs = entry.lastJudgedAt,
            lastUserAction = lastUserAction,
        )
    }

    /**
     * 판단 결과를 PreJudge 캐시에 영속 저장.
     */
    suspend fun store(canonicalNumber: String, result: DecisionResult) {
        val count = dao.getCount()
        if (count >= PreJudgeResult.MAX_ENTRIES) {
            dao.evictOldest(count - PreJudgeResult.MAX_ENTRIES + 10)
        }

        val existing = dao.lookup(canonicalNumber)
        val now = System.currentTimeMillis()

        val entry = PreJudgeCacheEntry(
            id = existing?.id ?: 0,
            canonicalNumber = canonicalNumber,
            action = result.action.name,
            riskScore = result.riskLevel.ordinal / 3f,
            category = result.category.name,
            confidence = result.confidence,
            summary = result.summary,
            hitCount = (existing?.hitCount ?: 0) + 1,
            lastUserAction = existing?.lastUserAction,
            lastJudgedAt = now,
            createdAt = existing?.createdAt ?: now,
        )

        dao.upsert(entry)
    }

    suspend fun updateUserAction(canonicalNumber: String, action: UserCallAction) {
        dao.updateUserAction(canonicalNumber, action.displayKey)
    }

    suspend fun delete(canonicalNumber: String) {
        dao.deleteByNumber(canonicalNumber)
    }

    suspend fun deleteAll() {
        dao.deleteAll()
    }
}
