package app.myphonecheck.mobile.feature.callintercept

import app.myphonecheck.mobile.core.model.SearchEvidence
import javax.inject.Inject

/**
 * data/search 모듈 폐기 (Stage 3-000, 헌법 §1 v2.4.0 정합) 후 stub 구현.
 *
 * v2.4.0 §1 명문 금지: 경쟁사 비공식 API/scraper 통합. data/search/provider/ 6
 * scraping provider + SearchEnrichmentRepository 일괄 폐기. 후속 Stage 3-001~003 에서
 * 4축 검색 (Custom Tab 사용자 직접 진입) 정공법으로 전면 교체 예정.
 */
class SearchEvidenceProviderImpl @Inject constructor() : SearchEvidenceProvider {

    override suspend fun gather(
        normalizedNumber: String,
        deviceCountryCode: String?,
    ): SearchEvidence {
        return SearchEvidence.empty()
    }
}
