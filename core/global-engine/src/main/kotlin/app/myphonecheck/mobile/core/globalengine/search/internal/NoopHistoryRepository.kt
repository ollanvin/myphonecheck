package app.myphonecheck.mobile.core.globalengine.search.internal

import app.myphonecheck.mobile.core.globalengine.search.MatchEntry
import app.myphonecheck.mobile.core.globalengine.search.QueryType
import javax.inject.Inject
import javax.inject.Singleton

/**
 * NoOp 기본 구현 (Architecture v2.0.0 §30 Stage 2-005).
 *
 * 후속 PR에서 :data:local-cache의 실 구현으로 교체.
 * 본 PR은 인터페이스 + InputAggregator 명문화에 한정 — 실제 이력 검색은 Stage 후속.
 */
@Singleton
class NoopHistoryRepository @Inject constructor() : HistoryRepository {
    override suspend fun findByKey(key: String, type: QueryType): List<MatchEntry> = emptyList()
}
