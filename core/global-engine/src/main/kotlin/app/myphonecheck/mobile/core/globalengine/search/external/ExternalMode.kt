package app.myphonecheck.mobile.core.globalengine.search.external

/**
 * AI 검색 모드 (Architecture v2.5.0 헌법 §1 정합).
 *
 * SIM 기준 SimAiSearchRegistry가 후보군 자동 추출.
 * 최소 2개 보장 + 사용자 자율 결정 + 마지막 선택 기억.
 *
 * AI Mode 우선, *_PLAIN은 fallback (AI 미작동 시).
 * (구) v2.4.0 4축 모델은 v2.5.0에서 2축으로 단순화 (NKB 0.40 + AI 0.60).
 */
enum class ExternalMode {
    GOOGLE_AI_MODE,    // udm=50
    BING_COPILOT,      // showconv=1
    NAVER_AI,          // ai=1 (정정: NAVER_CUE → NAVER_AI 정본 명칭)
    YAHOO_JAPAN_AI,    // 신규 (JP SIM 후보)
    BAIDU_AI,          // 신규 (CN SIM 후보)
    GOOGLE_PLAIN,      // fallback
    BING_PLAIN,        // fallback
    NAVER_PLAIN,       // fallback
}
