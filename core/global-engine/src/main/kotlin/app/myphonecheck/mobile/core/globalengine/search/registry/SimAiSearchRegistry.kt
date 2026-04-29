package app.myphonecheck.mobile.core.globalengine.search.registry

import app.myphonecheck.mobile.core.globalengine.search.external.ExternalMode
import app.myphonecheck.mobile.core.globalengine.simcontext.SimContextProvider
import javax.inject.Inject
import javax.inject.Singleton

/**
 * SIM 기준 AI 검색 후보군 자동 추출 (Architecture v2.5.0 헌법 §1 정합).
 *
 * 원칙:
 *  - 최소 2개 옵션 보장 (init 검증)
 *  - 고정 우선순위 없음 (사용자 자율 결정 + 마지막 선택 기억)
 *  - 후보군은 헌법 v2.5.0 §1 본문 표로 정의 (변경 시 헌법 패치 의무)
 *
 * v2.5.0 §1 표:
 *  KR              → Naver AI / Google AI Mode / Bing Copilot
 *  JP              → Yahoo Japan AI / Google AI Mode / Bing Copilot
 *  CN              → Baidu AI / Bing Copilot
 *  Global default  → Google AI Mode / Bing Copilot
 */
@Singleton
class SimAiSearchRegistry @Inject constructor(
    private val simContextProvider: SimContextProvider,
) {

    /**
     * 현재 SIM 기준 AI 검색 후보군 반환. 최소 2개 보장.
     */
    fun getCandidates(): List<ExternalMode> {
        val countryIso = simContextProvider.resolve().countryIso.uppercase()
        return CANDIDATES_BY_COUNTRY[countryIso] ?: GLOBAL_DEFAULT
    }

    /**
     * 지정 SIM 국가 기준 후보군 (테스트 / SIM 부재 fallback 용).
     */
    fun getCandidatesFor(countryIso: String): List<ExternalMode> {
        return CANDIDATES_BY_COUNTRY[countryIso.uppercase()] ?: GLOBAL_DEFAULT
    }

    companion object {
        // v2.5.0 §1 본문 표 정합. 변경 시 헌법 패치 의무.
        // 최소 2개 보장 — 모든 후보 리스트 size >= 2.
        private val CANDIDATES_BY_COUNTRY: Map<String, List<ExternalMode>> = mapOf(
            "KR" to listOf(ExternalMode.NAVER_AI, ExternalMode.GOOGLE_AI_MODE, ExternalMode.BING_COPILOT),
            "JP" to listOf(ExternalMode.YAHOO_JAPAN_AI, ExternalMode.GOOGLE_AI_MODE, ExternalMode.BING_COPILOT),
            "CN" to listOf(ExternalMode.BAIDU_AI, ExternalMode.BING_COPILOT),
        )

        // SIM 미식별 / 미정의 국가 — 글로벌 default (최소 2개)
        private val GLOBAL_DEFAULT: List<ExternalMode> = listOf(
            ExternalMode.GOOGLE_AI_MODE,
            ExternalMode.BING_COPILOT,
        )

        init {
            // 헌법 §1 정합 검증: 최소 2개 보장
            CANDIDATES_BY_COUNTRY.forEach { (country, candidates) ->
                require(candidates.size >= 2) {
                    "SIM[$country] candidates must be >= 2 (헌법 §1 v2.5.0 정합)"
                }
            }
            require(GLOBAL_DEFAULT.size >= 2) {
                "GLOBAL_DEFAULT candidates must be >= 2 (헌법 §1 v2.5.0 정합)"
            }
        }
    }
}
