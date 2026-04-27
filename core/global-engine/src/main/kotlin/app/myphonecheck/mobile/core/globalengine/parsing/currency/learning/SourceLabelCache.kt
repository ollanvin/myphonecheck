package app.myphonecheck.mobile.core.globalengine.parsing.currency.learning

import app.myphonecheck.mobile.data.localcache.dao.CardSourceLabelDao
import app.myphonecheck.mobile.data.localcache.entity.CardSourceLabelEntity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 발신자 라벨 캐시 (Architecture v2.0.0 §27-3-1 + §30 :core:global-engine).
 *
 * Stage 2-001 마이그레이션: :feature:card-check/learning/SourceLabelCache.kt → 본 위치 (기능 동일).
 *
 * 시드 0: 모든 항목은 사용자 확인 결과로만 추가.
 * 디바이스 로컬 (헌법 1조 Out-Bound Zero, 3조 결정권 중앙집중 금지).
 */
@Singleton
class SourceLabelCache @Inject constructor(
    private val dao: CardSourceLabelDao,
) {

    suspend fun find(sourceId: String): String? {
        return dao.find(sourceId)?.label
    }

    suspend fun upsert(sourceId: String, label: String) {
        dao.upsert(
            CardSourceLabelEntity(
                sourceId = sourceId,
                label = label,
                createdAt = System.currentTimeMillis(),
            ),
        )
    }

    suspend fun deleteById(sourceId: String) {
        dao.deleteById(sourceId)
    }

    suspend fun count(): Int = dao.count()
}
