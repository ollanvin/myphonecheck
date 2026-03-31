package app.callcheck.mobile.feature.callintercept

import app.callcheck.mobile.core.model.SearchEvidence

/**
 * Gathers search platform evidence for a phone number.
 *
 * Implementation delegates to search provider registry
 * and search result analyzer.
 */
interface SearchEvidenceProvider {
    /**
     * @param normalizedNumber  비교/검색용 canonical 번호
     * @param deviceCountryCode 기기 탐지 국가 코드 (ISO 3166-1 alpha-2).
     *                          SearchRouter 국가별 최적 Provider 선택에 사용.
     */
    suspend fun gather(
        normalizedNumber: String,
        deviceCountryCode: String?,
    ): SearchEvidence
}
