package app.myphonecheck.mobile.core.globalengine.decision

import app.myphonecheck.mobile.core.globalengine.search.SearchQuery
import app.myphonecheck.mobile.core.globalengine.search.SearchResult
import app.myphonecheck.mobile.core.globalengine.search.internal.OnDeviceHistorySearch
import app.myphonecheck.mobile.core.globalengine.search.publicfeed.PublicFeedAggregator
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 검색 4대 축 통합 입력 aggregator (Architecture v2.4.0 §30 + 메모리 #8).
 *
 * 4축 매핑:
 *  - 축 1 internalNkb: 온디바이스 NKB (사용자 본인 이력)
 *  - 축 2 publicAuthority: 공공 공신력 (KISA 외) — 사용자 옵트인 다운로드
 *  - 축 3 externalAi: 외부 AI 검색 — Custom Tab 사용자 직접 진입 외 비-call (null)
 *  - 축 4 competitorReverse: 경쟁사 Reverse Lookup — Custom Tab 사용자 직접 진입 외 비-call (null)
 *
 * 헌법 정합:
 *  - §1 Out-Bound Zero: 축 3·4는 사용자 직접 진입만, 우리 송신 0.
 *  - §3 결정권 중앙집중 금지: aggregate는 후보 통합만, 결정은 사용자.
 *  - §8 SIM-Oriented: query.context.sim이 단일 진실원.
 *
 * 사용자 직접 검색 후 태그 추가 시점에는 [appendUserTaggedExternalResult]를 통해
 * NKB로 영속화. 다음 동일 query 시 internalNkb 축으로 흡수.
 */
@Singleton
class InputAggregator @Inject constructor(
    private val internal: OnDeviceHistorySearch,
    private val publicFeed: PublicFeedAggregator,
) {
    suspend fun aggregate(query: SearchQuery): AggregatedInput {
        return AggregatedInput(
            internalNkb = internal.search(query),
            publicAuthority = publicFeed.search(query),
            externalAi = null,
            competitorReverse = null,
            query = query,
            timestamp = System.currentTimeMillis(),
        )
    }

    /**
     * 사용자 직접 검색 후 태그 추가 시점에 호출 → NKB DAO 영속화.
     * 본 메서드는 인터페이스 정의 단계. 실제 NKB 영속화는 후속 Stage 3-003 의 NKB DAO 통합에서 구현.
     */
    @Suppress("UNUSED_PARAMETER")
    suspend fun appendUserTaggedExternalResult(
        query: SearchQuery,
        source: ExternalSource,
        signal: Float,
        snippet: String,
    ) {
        // TODO(Stage 3-003): NKB DAO 통합 후 실제 영속화. 현재는 인터페이스 stub.
    }
}

data class AggregatedInput(
    val internalNkb: SearchResult?,
    val publicAuthority: SearchResult?,
    val externalAi: SearchResult?,
    val competitorReverse: SearchResult?,
    val query: SearchQuery,
    val timestamp: Long,
)

enum class ExternalSource {
    EXTERNAL_AI,
    COMPETITOR_REVERSE_TRUECALLER,
    COMPETITOR_REVERSE_WHOSCALL,
    COMPETITOR_REVERSE_HIYA,
}
