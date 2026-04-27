package app.myphonecheck.mobile.core.globalengine.search.internal

import app.myphonecheck.mobile.core.globalengine.search.SearchConfidence
import app.myphonecheck.mobile.core.globalengine.search.SearchQuery
import app.myphonecheck.mobile.core.globalengine.search.SearchResult
import app.myphonecheck.mobile.core.globalengine.search.SearchSource
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 내부 검색 — 온디바이스 이력만 조회 (Architecture v2.0.0 §30, 헌법 §1 Out-Bound Zero).
 *
 * 결과는 사용자 본인 이력이므로 confidence = HIGH.
 */
@Singleton
class OnDeviceHistorySearch @Inject constructor(
    private val historyRepository: HistoryRepository,
) {
    suspend fun search(query: SearchQuery): SearchResult {
        val matches = historyRepository.findByKey(query.key, query.type)
        return SearchResult(
            source = SearchSource.INTERNAL,
            matches = matches,
            confidence = SearchConfidence.HIGH,
        )
    }
}
