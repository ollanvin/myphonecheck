package app.callcheck.mobile.feature.callintercept

import app.callcheck.mobile.core.model.DecisionResult

/**
 * Orchestrates the full call intercept decision pipeline.
 *
 * Pipeline:
 * 1. Device evidence gathering (target: < 1s)
 * 2. Search enrichment (target: < 3s, parallel with step 1)
 * 3. Decision engine evaluation (target: < 50ms)
 *
 * Total target: 3 seconds optimal, 5-second hard limit.
 * Returns partial results if search enrichment times out.
 */
interface CallInterceptRepository {
    /**
     * @param normalizedNumber  비교/검색용 canonical 번호
     * @param deviceCountryCode 기기 탐지 국가 코드 (ISO 3166-1 alpha-2).
     *                          CountryConfigProvider.detectCountry() 결과.
     *                          SearchRouter 국가별 최적 Provider 선택에 사용.
     *                          null이면 SearchRouter 기본값(US) 적용.
     */
    suspend fun processIncomingCall(
        normalizedNumber: String,
        deviceCountryCode: String?,
    ): DecisionResult
}
