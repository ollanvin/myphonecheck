package app.myphonecheck.mobile.core.globalengine.search.external

/**
 * 외부 AI/일반 검색 모드 (Architecture v2.4.0 §1 + 메모리 #8).
 *
 * 헌법 §1 정합: Custom Tab 사용자 직접 진입만 사용. 우리 송신 0.
 * AI 검색 모드 1차 → 일반 검색 fallback.
 */
enum class ExternalMode {
    GOOGLE_AI_MODE,    // udm=50, 1차 default
    BING_COPILOT,      // showconv=1, fallback
    NAVER_CUE,         // 한국 SIM, 별건 검증 후 활성
    GOOGLE_PLAIN,      // udm 없음, fallback
    BING_PLAIN,        // showconv 없음
    NAVER_PLAIN,       // cue 미적용
}
