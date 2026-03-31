package app.callcheck.mobile.data.search

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

/**
 * 온디바이스 분산 웹 스캔 레지스트리.
 *
 * CountrySearchRouter에서 국가별 최적 Provider 조합을 받아
 * 병렬로 실행한다.
 *
 * 타임아웃 계층:
 * - 개별 Provider: 1000ms (Provider 내부 자체 타임아웃)
 * - Registry 전체: 1200ms (개별보다 약간 여유)
 * - Enrichment Repository: 1500ms (최종 외곽)
 * - Decision Engine: <50ms (별도)
 */
class SearchProviderRegistry(
    private val router: CountrySearchRouter,
) {

    private companion object {
        private const val TAG = "SearchProviderRegistry"
        private const val TOTAL_TIMEOUT_MS = 1200L
        private const val PER_PROVIDER_TIMEOUT_MS = 1000L
    }

    /**
     * 국가 코드 기반으로 최적 Provider 조합을 선택하고 병렬 실행.
     */
    suspend fun searchAll(
        phoneNumber: String,
        countryCode: String?
    ): List<RawSearchResult> = withContext(Dispatchers.Default) {
        val providers = router.getProvidersForCountry(countryCode)

        if (providers.isEmpty()) {
            Log.w(TAG, "No providers available for country: $countryCode")
            return@withContext emptyList()
        }

        Log.d(TAG, "Country=$countryCode → Providers: ${providers.map { it.providerName }}")

        val results = mutableListOf<RawSearchResult>()

        try {
            withTimeoutOrNull(TOTAL_TIMEOUT_MS) {
                // Launch all providers in parallel
                val searchTasks = providers.map { provider ->
                    async {
                        searchWithProvider(provider, phoneNumber, countryCode)
                    }
                }

                // Wait for all to complete (or timeout)
                val allResults = try {
                    searchTasks.awaitAll()
                } catch (e: TimeoutCancellationException) {
                    Log.w(TAG, "Provider search timeout — collecting partial results")
                    searchTasks.mapNotNull {
                        try { it.getCompleted() } catch (_: Exception) { null }
                    }
                }

                // Merge and deduplicate results
                allResults
                    .filterNotNull()
                    .flatten()
                    .distinctBy { "${it.url}|${it.title}" }
                    .sortedByDescending { it.title.length }
                    .take(15)
                    .forEach { results.add(it) }
            } ?: run {
                Log.w(TAG, "Total search timeout after ${TOTAL_TIMEOUT_MS}ms")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error during parallel search", e)
        }

        Log.d(TAG, "Total results: ${results.size} for $phoneNumber ($countryCode)")
        results
    }

    private suspend fun searchWithProvider(
        provider: SearchProvider,
        phoneNumber: String,
        countryCode: String?
    ): List<RawSearchResult>? {
        return try {
            withTimeoutOrNull(PER_PROVIDER_TIMEOUT_MS) {
                val result = provider.search(phoneNumber, countryCode)
                if (result.success) {
                    Log.d(
                        TAG,
                        "${provider.providerName} returned ${result.results.size} results in ${result.responseTimeMs}ms"
                    )
                    result.results.map { it.copy(providerName = provider.providerName) }
                } else {
                    Log.w(TAG, "${provider.providerName} search failed: ${result.error}")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error searching with ${provider.providerName}", e)
            null
        }
    }

    /**
     * 특정 국가에 대해 사용 가능한 Provider 이름 목록.
     */
    fun getProviderNames(countryCode: String?): List<String> {
        return router.getProvidersForCountry(countryCode).map { it.providerName }
    }
}
