package app.myphonecheck.mobile.core.globalengine.decision

import app.myphonecheck.mobile.core.globalengine.search.SearchInput
import app.myphonecheck.mobile.core.globalengine.search.SearchResult
import app.myphonecheck.mobile.core.globalengine.search.internal.OnDeviceHistorySearch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 검색 2축 통합 입력 aggregator (Architecture v2.5.0 §30 + 헌법 §1 2축 매핑).
 *
 * 2축 매핑:
 *  - 축 1 internalNkb: 온디바이스 NKB (사용자 본인 이력) — W=0.40
 *  - 축 2 externalAi: 외부 AI 검색 — Custom Tab 사용자 직접 진입 외 비-call (null) — W=0.60
 *
 * 헌법 정합:
 *  - §1 Out-Bound Zero: 축 2는 사용자 직접 진입만, 우리 송신 0.
 *  - §3 결정권 중앙집중 금지: aggregate는 후보 통합만, 결정은 사용자.
 *  - §7 One Engine, N Inputs: SearchInput sealed class 단일 인터페이스.
 *
 * v2.4.0 → v2.5.0 단순화 근거: AI 검색 모드 인프라가 (구) 공공 + 경쟁사 reverse 자체 통합
 * → 4축 분리 가중치 합산보다 정확. SimAiSearchRegistry가 SIM 기준 후보군 자동 추출.
 *
 * 사용자 직접 검색 후 태그 추가 시점에는 [appendUserTaggedExternalResult]를 통해
 * NKB로 영속화. 다음 동일 query 시 internalNkb 축으로 흡수.
 */
@Singleton
class InputAggregator @Inject constructor(
    private val onDeviceHistory: OnDeviceHistorySearch,
) {
    suspend fun aggregate(input: SearchInput): AggregatedInput {
        return AggregatedInput(
            internalNkb = onDeviceHistory.search(input),
            externalAi = null,
            input = input,
            timestamp = System.currentTimeMillis(),
        )
    }

    /**
     * 사용자 직접 검색 후 태그 추가 시점에 호출 → NKB DAO 영속화.
     * provider = "Google AI Mode" / "Naver AI" 등 (SimAiSearchRegistry 정의).
     * 본 메서드는 인터페이스 정의 단계. 실제 NKB 영속화는 후속 Stage 3-003-REV 의 NKB DAO 통합에서 구현.
     */
    @Suppress("UNUSED_PARAMETER")
    suspend fun appendUserTaggedExternalResult(
        input: SearchInput,
        provider: String,
        signal: Float,
        snippet: String,
    ) {
        // TODO(Stage 3-003-REV): NKB DAO 통합 후 실제 영속화. 현재는 인터페이스 stub.
    }
}

data class AggregatedInput(
    val internalNkb: SearchResult?,
    val externalAi: SearchResult?,
    val input: SearchInput,
    val timestamp: Long,
)
