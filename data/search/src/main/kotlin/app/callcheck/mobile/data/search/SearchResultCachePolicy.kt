package app.callcheck.mobile.data.search

/**
 * 검색 결과 TTL 캐시 정책.
 *
 * ┌──────────────────────────────────────────────────────────────┐
 * │ 절대 원칙: 검색 결과는 영구 저장하지 않는다.                  │
 * ├──────────────────────────────────────────────────────────────┤
 * │                                                              │
 * │ 【영구 저장 허용 (사용자 명시 행위만)】                       │
 * │ • 사용자 메모 (UserMemo)                                     │
 * │ • 사용자 차단 목록 (BlockList)                               │
 * │ • 사용자 화이트리스트 (TrustList)                            │
 * │                                                              │
 * │ 【TTL 캐시만 허용 (자동 만료 삭제)】                         │
 * │ • 검색 결과 원문 (RawSearchResult)                          │
 * │ • 분석 결과 (SearchEvidence)                                │
 * │ • 판정 결과 (DecisionResult)                                │
 * │ • SignalSummary                                              │
 * │                                                              │
 * │ 【TTL 기간】                                                  │
 * │ • SEARCH_RESULT_TTL  = 24시간  (같은 번호 재수신 시 재활용)  │
 * │ • DECISION_CACHE_TTL = 1시간   (같은 번호 연속 수신 시)      │
 * │ • SIGNAL_SUMMARY_TTL = 12시간  (분석 요약 중간 캐시)         │
 * │                                                              │
 * │ 【만료 메커니즘】                                             │
 * │ • LRU + TTL 복합 (시간 만료 우선, 용량 초과 시 LRU)         │
 * │ • 앱 시작 시 만료 항목 일괄 정리 (cold sweep)               │
 * │ • 메모리 캐시: 앱 종료 시 자동 소멸                          │
 * │ • 디스크 캐시: TTL 만료 즉시 삭제 (lazy + cold sweep)       │
 * │                                                              │
 * │ 【캐시 용량 제한】                                            │
 * │ • 메모리: 최대 50 항목 (번호 기준)                           │
 * │ • 디스크: 최대 200 항목 (번호 기준)                          │
 * │ • 디스크 총 용량: 최대 10MB                                   │
 * │                                                              │
 * │ 【사용자에게 보이는 데이터 흐름】                              │
 * │ 1. 전화 수신 → 검색 실행 → 판정 표시                        │
 * │ 2. 판정 결과는 TTL 동안 캐시                                 │
 * │ 3. TTL 만료 후 같은 번호 재수신 → 재검색                    │
 * │ 4. 사용자가 메모/차단/신뢰 설정 → 영구 저장                 │
 * │                                                              │
 * │ 【프라이버시 근거】                                           │
 * │ • 검색 결과 영구 보관 = 사용자 통화 패턴 프로파일링 위험    │
 * │ • TTL만 허용하면 기기 분실/도난 시에도 과거 데이터 노출 없음 │
 * │ • 사용자 명시 행위(메모/차단)만 영구 = 사용자 주권 보장      │
 * └──────────────────────────────────────────────────────────────┘
 */
object SearchResultCachePolicy {

    // ══════════════════════════════════════
    // TTL 상수 (밀리초)
    // ══════════════════════════════════════

    /** 검색 결과 원문 캐시 TTL: 24시간 */
    const val SEARCH_RESULT_TTL_MS: Long = 24 * 60 * 60 * 1000L  // 86,400,000ms

    /** 판정 결과 캐시 TTL: 1시간 */
    const val DECISION_CACHE_TTL_MS: Long = 1 * 60 * 60 * 1000L  // 3,600,000ms

    /** SignalSummary 캐시 TTL: 12시간 */
    const val SIGNAL_SUMMARY_TTL_MS: Long = 12 * 60 * 60 * 1000L  // 43,200,000ms

    // ══════════════════════════════════════
    // 용량 제한
    // ══════════════════════════════════════

    /** 메모리 캐시 최대 항목 수 (번호 기준) */
    const val MEMORY_CACHE_MAX_ENTRIES: Int = 50

    /** 디스크 캐시 최대 항목 수 (번호 기준) */
    const val DISK_CACHE_MAX_ENTRIES: Int = 200

    /** 디스크 캐시 최대 총 용량 (바이트) */
    const val DISK_CACHE_MAX_SIZE_BYTES: Long = 10 * 1024 * 1024L  // 10MB

    // ══════════════════════════════════════
    // 만료 검사
    // ══════════════════════════════════════

    /**
     * 주어진 타임스탬프가 TTL 내인지 확인.
     * @param cachedAtMs 캐시 저장 시각 (epoch millis)
     * @param ttlMs TTL 기간 (밀리초)
     * @param nowMs 현재 시각 (epoch millis). 테스트에서 주입 가능.
     * @return true이면 유효, false이면 만료
     */
    fun isValid(cachedAtMs: Long, ttlMs: Long, nowMs: Long = System.currentTimeMillis()): Boolean {
        return (nowMs - cachedAtMs) < ttlMs
    }

    /**
     * 검색 결과 캐시 유효성 확인.
     */
    fun isSearchResultValid(cachedAtMs: Long, nowMs: Long = System.currentTimeMillis()): Boolean {
        return isValid(cachedAtMs, SEARCH_RESULT_TTL_MS, nowMs)
    }

    /**
     * 판정 결과 캐시 유효성 확인.
     */
    fun isDecisionValid(cachedAtMs: Long, nowMs: Long = System.currentTimeMillis()): Boolean {
        return isValid(cachedAtMs, DECISION_CACHE_TTL_MS, nowMs)
    }

    /**
     * SignalSummary 캐시 유효성 확인.
     */
    fun isSignalSummaryValid(cachedAtMs: Long, nowMs: Long = System.currentTimeMillis()): Boolean {
        return isValid(cachedAtMs, SIGNAL_SUMMARY_TTL_MS, nowMs)
    }
}

/**
 * TTL 캐시 엔트리 래퍼.
 * 모든 캐시 가능한 데이터를 이 래퍼로 감싸서 TTL 관리.
 */
data class CachedEntry<T>(
    val data: T,
    val cachedAtMs: Long = System.currentTimeMillis(),
    val phoneNumber: String,
) {
    /**
     * 이 엔트리가 주어진 TTL 내에서 유효한지 확인.
     */
    fun isValid(ttlMs: Long, nowMs: Long = System.currentTimeMillis()): Boolean {
        return SearchResultCachePolicy.isValid(cachedAtMs, ttlMs, nowMs)
    }
}
