package app.callcheck.mobile.feature.callintercept

import app.callcheck.mobile.core.model.SearchEvidence
import app.callcheck.mobile.data.search.repository.SearchEnrichmentRepository
import javax.inject.Inject

/**
 * Production SearchEvidenceProvider that delegates to SearchEnrichmentRepository.
 */
class SearchEvidenceProviderImpl @Inject constructor(
    private val searchEnrichmentRepository: SearchEnrichmentRepository,
) : SearchEvidenceProvider {

    override suspend fun gather(
        normalizedNumber: String,
        deviceCountryCode: String?,
    ): SearchEvidence {
        return searchEnrichmentRepository.enrichWithSearch(
            normalizedNumber = normalizedNumber,
            countryCode = deviceCountryCode,
        )
    }
}
