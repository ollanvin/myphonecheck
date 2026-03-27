package app.callcheck.mobile.data.search.repository

import app.callcheck.mobile.core.model.SearchEvidence

interface SearchEnrichmentRepository {
    suspend fun enrichWithSearch(
        normalizedNumber: String,
        countryCode: String?
    ): SearchEvidence
}
