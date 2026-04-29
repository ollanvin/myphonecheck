package app.myphonecheck.mobile.feature.callintercept

/**
 * 인메모리 결정 캐시 정책 (call-intercept 한정).
 *
 * data/search 모듈 폐기 (Stage 3-000, 헌법 §1 v2.4.0 정합)에 따라
 * 기존 data.search.SearchResultCachePolicy / CachedEntry 의 필요한 상수·래퍼를
 * 본 모듈 자체 정의로 대체. 검색 결과 영구 저장 0 + TTL만 허용 원칙 보존.
 */
internal object CallInterceptCachePolicy {
    /** 판정 결과 캐시 TTL: 1시간 */
    const val DECISION_CACHE_TTL_MS: Long = 1 * 60 * 60 * 1000L

    /** 메모리 캐시 최대 항목 수 */
    const val MEMORY_CACHE_MAX_ENTRIES: Int = 50

    fun isValid(cachedAtMs: Long, ttlMs: Long, nowMs: Long = System.currentTimeMillis()): Boolean {
        return (nowMs - cachedAtMs) < ttlMs
    }
}

/** TTL 캐시 엔트리 래퍼 (call-intercept 자체 정의). */
internal data class CachedEntry<T>(
    val data: T,
    val cachedAtMs: Long = System.currentTimeMillis(),
    val phoneNumber: String,
) {
    fun isValid(ttlMs: Long, nowMs: Long = System.currentTimeMillis()): Boolean {
        return CallInterceptCachePolicy.isValid(cachedAtMs, ttlMs, nowMs)
    }
}
