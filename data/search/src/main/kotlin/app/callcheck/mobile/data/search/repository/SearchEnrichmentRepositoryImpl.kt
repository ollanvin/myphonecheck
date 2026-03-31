package app.callcheck.mobile.data.search.repository

import android.util.Log
import app.callcheck.mobile.core.model.SearchEvidence
import app.callcheck.mobile.core.util.PhoneNumberVariantGenerator
import app.callcheck.mobile.data.search.SearchProviderRegistry
import app.callcheck.mobile.data.search.SearchResultAnalyzer
import kotlinx.coroutines.withTimeoutOrNull

/**
 * 검색 보강 레포지토리.
 *
 * 전화번호를 웹 검색으로 보강하여 SearchEvidence를 생성합니다.
 *
 * 번호 변환 검색 (Variant Search):
 * 웹에서 동일한 전화번호가 다양한 포맷으로 존재합니다.
 * PhoneNumberVariantGenerator로 가능한 모든 포맷을 생성한 뒤,
 * OR 쿼리로 결합하여 단일 검색으로 전체 커버합니다.
 *
 * 예시 (한국 번호):
 * 입력: +821012345678
 * 검색 쿼리: "+821012345678" OR "010-1234-5678" OR "01012345678" OR "+82 10-1234-5678"
 *
 * 타임아웃 영향: ZERO
 * - 프로바이더당 쿼리 수 변화 없음 (1개 → 1개)
 * - OR 연산은 검색엔진 내부에서 처리
 * - 기존 1500ms 외곽 타임아웃 유지
 */
class SearchEnrichmentRepositoryImpl(
    private val providerRegistry: SearchProviderRegistry,
    private val analyzer: SearchResultAnalyzer,
) : SearchEnrichmentRepository {

    private companion object {
        private const val TAG = "SearchEnrichmentRepo"
        private const val ENRICHMENT_TIMEOUT_MS = 1500L
    }

    override suspend fun enrichWithSearch(
        normalizedNumber: String,
        countryCode: String?,
    ): SearchEvidence {
        return withTimeoutOrNull(ENRICHMENT_TIMEOUT_MS) {
            try {
                // 번호 변환 생성 → OR 쿼리 조합
                val effectiveCountry = countryCode ?: "ZZ"
                val variants = PhoneNumberVariantGenerator.generateVariants(
                    normalizedNumber,
                    effectiveCountry,
                )
                val searchQuery = PhoneNumberVariantGenerator.buildOrQuery(variants)

                Log.d(TAG, "Variant search: $normalizedNumber → ${variants.size} variants")
                Log.d(TAG, "Search query: $searchQuery")

                val rawResults = providerRegistry.searchAll(searchQuery, countryCode)
                val evidence = analyzer.analyzeSearchResults(rawResults)

                Log.d(TAG, "Enrichment complete: clusters=${evidence.keywordClusters.size}, " +
                    "entities=${evidence.repeatedEntities.size}, variants=${variants.size}")
                evidence
            } catch (e: Exception) {
                Log.e(TAG, "Enrichment error", e)
                SearchEvidence.empty()
            }
        } ?: run {
            Log.w(TAG, "Enrichment timeout (${ENRICHMENT_TIMEOUT_MS}ms)")
            SearchEvidence.empty()
        }
    }
}
