package app.myphonecheck.mobile.core.globalengine.decision

import app.myphonecheck.mobile.core.globalengine.search.SearchQuery
import app.myphonecheck.mobile.core.globalengine.search.SearchResult
import app.myphonecheck.mobile.core.globalengine.search.external.CustomTabExternalSearch
import app.myphonecheck.mobile.core.globalengine.search.external.ExternalSearchIntent
import app.myphonecheck.mobile.core.globalengine.search.internal.OnDeviceHistorySearch
import app.myphonecheck.mobile.core.globalengine.search.publicfeed.PublicFeedAggregator
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 검색 3대 축 통합 입력 aggregator (Architecture v2.0.0 §30 + 메모리 #5).
 *
 * Surface별 결정 입력을 코어가 통합 제공 — Surface는 본 결과만 소비.
 *
 * 헌법 정합:
 *  - §1 Out-Bound Zero: external은 인텐트만 빌드, public_feed는 옵트인 캐시.
 *  - §3 결정권 중앙집중 금지: aggregate는 후보 통합만, 결정은 사용자.
 *  - §8 SIM-Oriented: query.context.sim이 단일 진실원.
 */
@Singleton
class InputAggregator @Inject constructor(
    private val internal: OnDeviceHistorySearch,
    private val publicFeed: PublicFeedAggregator,
    private val external: CustomTabExternalSearch,
) {
    suspend fun aggregate(query: SearchQuery): AggregatedInput {
        val internalResult = internal.search(query)
        val publicResult = publicFeed.search(query)
        val externalIntent = external.buildIntent(query)
        return AggregatedInput(
            internal = internalResult,
            publicFeed = publicResult,
            externalSearchIntent = externalIntent,
        )
    }
}

data class AggregatedInput(
    val internal: SearchResult,
    val publicFeed: SearchResult,
    val externalSearchIntent: ExternalSearchIntent,
)
