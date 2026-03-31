package app.callcheck.mobile.data.search.repository

import android.util.Log
import app.callcheck.mobile.core.model.SearchEvidence
import app.callcheck.mobile.data.search.SearchProviderRegistry
import app.callcheck.mobile.data.search.SearchResultAnalyzer
import kotlinx.coroutines.withTimeoutOrNull

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
                Log.d(TAG, "Starting enrichment: $normalizedNumber")
                val rawResults = providerRegistry.searchAll(normalizedNumber, countryCode)
                val evidence = analyzer.analyzeSearchResults(rawResults)

                Log.d(TAG, "Enrichment complete: clusters=${evidence.keywordClusters.size}, entities=${evidence.repeatedEntities.size}")
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
