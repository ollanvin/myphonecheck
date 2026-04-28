package app.myphonecheck.mobile.core.globalengine.search.publicfeed

import app.myphonecheck.mobile.core.globalengine.search.MatchEntry
import app.myphonecheck.mobile.core.globalengine.search.SearchConfidence
import app.myphonecheck.mobile.core.globalengine.search.SearchQuery
import app.myphonecheck.mobile.core.globalengine.search.SearchResult
import app.myphonecheck.mobile.core.globalengine.search.SearchSource
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 공개 피드 통합 검색 (Architecture v2.1.0 §30-4).
 *
 * v2.0.0 PR #22 시점 의존(`List<PublicFeedSource>` + source.isOptedIn()) →
 * v2.1.0 PR #29: FeedRegistry(메타) + FeedOptInProvider(런타임 옵트인).
 *
 * 헌법 §1 Out-Bound Zero 정합:
 *  - FeedOptInProvider.optedInIds()에 포함된 출처만 lookup.
 *  - lookup은 디바이스 PublicFeedCache 대상 — 외부 통신 0.
 *  - 실제 다운로드는 FeedDownloadWorker가 별도 수행.
 */
@Singleton
class PublicFeedAggregator @Inject constructor(
    private val cache: PublicFeedCache,
    private val registry: FeedRegistry,
    private val optInProvider: FeedOptInProvider,
) {

    suspend fun search(query: SearchQuery): SearchResult {
        val optedIn = optInProvider.optedInIds()
        if (optedIn.isEmpty()) {
            return SearchResult(SearchSource.PUBLIC_FEED, emptyList(), SearchConfidence.MEDIUM)
        }

        val matches = mutableListOf<MatchEntry>()
        for (source in registry.all()) {
            if (source.id in optedIn) {
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
