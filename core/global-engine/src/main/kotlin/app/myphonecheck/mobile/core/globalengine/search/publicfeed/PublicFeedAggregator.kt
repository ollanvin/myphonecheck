package app.myphonecheck.mobile.core.globalengine.search.publicfeed

import app.myphonecheck.mobile.core.globalengine.search.MatchEntry
import app.myphonecheck.mobile.core.globalengine.search.SearchConfidence
import app.myphonecheck.mobile.core.globalengine.search.SearchQuery
import app.myphonecheck.mobile.core.globalengine.search.SearchResult
import app.myphonecheck.mobile.core.globalengine.search.SearchSource
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 공개 피드 통합 검색 (Architecture v2.0.0 §30).
 *
 * 헌법 §1 Out-Bound Zero 정합:
 *  - 옵트인된 출처만 lookup.
 *  - lookup은 디바이스 캐시 대상 — 외부 통신 0.
 *
 * 본 PR 범위: 인터페이스 + 캐시 lookup. 실제 다운로드 워커는 후속 PR.
 */
@Singleton
class PublicFeedAggregator @Inject constructor(
    private val cache: PublicFeedCache,
    private val sources: List<@JvmSuppressWildcards PublicFeedSource>,
) {

    suspend fun search(query: SearchQuery): SearchResult {
        val matches = mutableListOf<MatchEntry>()
        for (source in sources) {
            if (source.isOptedIn()) {
                matches += cache.lookup(source.id, query)
            }
        }
        return SearchResult(
            source = SearchSource.PUBLIC_FEED,
            matches = matches,
            confidence = SearchConfidence.MEDIUM,
        )
    }
}
