package app.myphonecheck.mobile.data.search.repository

import android.util.Log
import app.myphonecheck.mobile.core.model.AdjacentNumberHint
import app.myphonecheck.mobile.core.model.SearchEvidence
import app.myphonecheck.mobile.core.util.AdjacentNumberMatcher
import app.myphonecheck.mobile.core.util.PhoneNumberVariantGenerator
import app.myphonecheck.mobile.data.search.SearchProviderRegistry
import app.myphonecheck.mobile.data.search.SearchResultAnalyzer
import kotlinx.coroutines.withTimeoutOrNull

/**
 * 검색 보강 레포지토리.
 *
 * 전화번호를 웹 검색으로 보강하여 SearchEvidence를 생성합니다.
 *
 * ═══════════════════════════════════════════════════
 * 2단계 검색 파이프라인
 * ═══════════════════════════════════════════════════
 *
 * [1차] 변환 검색 (Variant Search)
 * - PhoneNumberVariantGenerator로 동일 번호의 모든 포맷 변환 생성
 * - OR 쿼리로 결합하여 단일 검색으로 전체 커버
 * - 예: "+821012345678" OR "010-1234-5678" OR "01012345678"
 *
 * [2차] 인접 번호 검색 (Adjacent Number Search) — 1차 결과 0건 시만 발동
 * - AdjacentNumberMatcher로 끝자리 트렁크 쿼리 생성
 * - 같은 대역(연번)의 번호가 검색되면 참고 힌트로 제공
 * - 예: "021234567" → 0212345670~9 중 하나라도 검색되면 힌트 생성
 * - UI: "유사 번호에서 '서울시청' 관련 검색 결과 3건 확인"
 *
 * 타임아웃 설계:
 * - 1차 전용: 1500ms (기존과 동일)
 * - 1차 + 2차: 2000ms (2차는 잔여 시간 내에서만 실행)
 * - 전체 SLA 2000ms 준수
 */
class SearchEnrichmentRepositoryImpl(
    private val providerRegistry: SearchProviderRegistry,
    private val analyzer: SearchResultAnalyzer,
) : SearchEnrichmentRepository {

    private companion object {
        private const val TAG = "SearchEnrichmentRepo"

        /** 1차 변환 검색 타임아웃 */
        private const val PRIMARY_TIMEOUT_MS = 1500L

        /** 전체 파이프라인 타임아웃 (1차 + 2차 합산) */
        private const val TOTAL_TIMEOUT_MS = 2000L

        /** 1차 결과가 "빈약"한 기준 (이 이하면 2차 발동) */
        private const val SPARSE_RESULT_THRESHOLD = 0
    }

    override suspend fun enrichWithSearch(
        normalizedNumber: String,
        countryCode: String?,
    ): SearchEvidence {
        val pipelineStart = System.currentTimeMillis()

        return withTimeoutOrNull(TOTAL_TIMEOUT_MS) {
            try {
                val effectiveCountry = countryCode ?: "ZZ"

                // ═══════════════════════════════════════
                // [1차] 변환 검색 (Variant Search)
                // ═══════════════════════════════════════
                val primaryEvidence = executePrimarySearch(
                    normalizedNumber,
                    effectiveCountry,
                    countryCode,
                )

                // 1차 결과가 충분하면 즉시 반환
                if (!isPrimaryResultSparse(primaryEvidence)) {
                    Log.d(TAG, "Primary search sufficient — skipping adjacent search")
                    return@withTimeoutOrNull primaryEvidence
                }

                // ═══════════════════════════════════════
                // [2차] 인접 번호 검색 (Adjacent Number Search)
                // ═══════════════════════════════════════
                val elapsed = System.currentTimeMillis() - pipelineStart
                val remainingMs = TOTAL_TIMEOUT_MS - elapsed

                if (remainingMs < 300) {
                    Log.w(TAG, "Insufficient time for adjacent search: ${remainingMs}ms remaining")
                    return@withTimeoutOrNull primaryEvidence
                }

                Log.d(TAG, "Primary result sparse — initiating adjacent number search " +
                    "(${remainingMs}ms remaining)")

                val adjacentHint = executeAdjacentSearch(
                    normalizedNumber,
                    effectiveCountry,
                    countryCode,
                    remainingMs,
                )

                // 인접 힌트를 기존 evidence에 병합
                if (adjacentHint != null) {
                    primaryEvidence.copy(adjacentNumberHint = adjacentHint)
                } else {
                    primaryEvidence
                }

            } catch (e: Exception) {
                Log.e(TAG, "Enrichment pipeline error", e)
                SearchEvidence.empty()
            }
        } ?: run {
            Log.w(TAG, "Enrichment pipeline timeout (${TOTAL_TIMEOUT_MS}ms)")
            SearchEvidence.empty()
        }
    }

    // ═══════════════════════════════════════════════
    // [1차] 변환 검색
    // ═══════════════════════════════════════════════

    private suspend fun executePrimarySearch(
        normalizedNumber: String,
        effectiveCountry: String,
        countryCode: String?,
    ): SearchEvidence {
        return withTimeoutOrNull(PRIMARY_TIMEOUT_MS) {
            val variants = PhoneNumberVariantGenerator.generateVariants(
                normalizedNumber,
                effectiveCountry,
            )
            val searchQuery = PhoneNumberVariantGenerator.buildOrQuery(variants)

            Log.d(TAG, "[1차] Variant search: $normalizedNumber → ${variants.size} variants")
            Log.d(TAG, "[1차] Search query: $searchQuery")

            val rawResults = providerRegistry.searchAll(searchQuery, countryCode)
            val evidence = analyzer.analyzeSearchResults(rawResults)

            Log.d(TAG, "[1차] Result: clusters=${evidence.keywordClusters.size}, " +
                "entities=${evidence.repeatedEntities.size}")
            evidence
        } ?: run {
            Log.w(TAG, "[1차] Primary search timeout")
            SearchEvidence.empty()
        }
    }

    // ═══════════════════════════════════════════════
    // [2차] 인접 번호 검색
    // ═══════════════════════════════════════════════

    /**
     * 인접 번호 트렁크 검색.
     *
     * 끝 1자리 트렁크로 먼저 검색하고, 결과가 있으면 힌트 생성.
     * 끝 1자리 결과가 없으면 끝 2자리 트렁크로 확장.
     *
     * 트렁크 쿼리 예시 (한국 번호 0212345678):
     * 1차 트렁크: "021234567" OR "02-1234-567" OR "+8221234567"
     *   → 0212345670~9 중 하나라도 웹에 있으면 매칭
     * 2차 트렁크: "02123456" OR "02-1234-56" OR "+822123456"
     *   → 0212345600~99 중 하나라도 웹에 있으면 매칭
     */
    private suspend fun executeAdjacentSearch(
        normalizedNumber: String,
        effectiveCountry: String,
        countryCode: String?,
        remainingMs: Long,
    ): AdjacentNumberHint? {
        return withTimeoutOrNull(remainingMs) {
            val trunkQueries = AdjacentNumberMatcher.generateTrunkQueries(
                normalizedNumber,
                effectiveCountry,
            )

            if (trunkQueries.isEmpty()) {
                Log.d(TAG, "[2차] No trunk queries generated")
                return@withTimeoutOrNull null
            }

            // 좁은 범위(끝 1자리)부터 시도
            for (trunkQuery in trunkQueries) {
                val orQuery = trunkQuery.toOrQuery()
                Log.d(TAG, "[2차] Trunk search (${trunkQuery.rangeDescription}): $orQuery")

                val rawResults = providerRegistry.searchAll(orQuery, countryCode)

                if (rawResults.isNotEmpty()) {
                    val adjacentEvidence = analyzer.analyzeSearchResults(rawResults)

                    Log.d(TAG, "[2차] Adjacent hit! ${rawResults.size} results, " +
                        "entities=${adjacentEvidence.repeatedEntities}, " +
                        "range=${trunkQuery.rangeDescription}")

                    return@withTimeoutOrNull AdjacentNumberHint(
                        matchedEntity = adjacentEvidence.repeatedEntities.firstOrNull(),
                        resultCount = rawResults.size,
                        rangeDescription = trunkQuery.rangeDescription,
                        keywordClusters = adjacentEvidence.keywordClusters,
                        topSnippets = adjacentEvidence.topSnippets.take(3),
                        signalSummaries = adjacentEvidence.signalSummaries,
                    )
                }

                Log.d(TAG, "[2차] No results for ${trunkQuery.rangeDescription}")
            }

            Log.d(TAG, "[2차] No adjacent number matches found")
            null
        }
    }

    // ═══════════════════════════════════════════════
    // Internal: 결과 판단
    // ═══════════════════════════════════════════════

    /**
     * 1차 검색 결과가 "빈약"한지 판단.
     *
     * 기준: 검색 결과 건수가 SPARSE_RESULT_THRESHOLD 이하
     * AND 유의미한 신호(키워드/엔티티)가 없음.
     */
    private fun isPrimaryResultSparse(evidence: SearchEvidence): Boolean {
        // 유의미한 신호가 하나라도 있으면 충분
        if (evidence.keywordClusters.isNotEmpty()) return false
        if (evidence.repeatedEntities.isNotEmpty()) return false
        if (evidence.signalSummaries.isNotEmpty()) return false
        if (evidence.topSnippets.size > SPARSE_RESULT_THRESHOLD) return false

        return true
    }
}
