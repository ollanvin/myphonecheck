package app.myphonecheck.mobile.data.search.repository

import app.myphonecheck.mobile.core.model.SearchEvidence

interface SearchEnrichmentRepository {
    suspend fun enrichWithSearch(
        normalizedNumber: String,
        countryCode: String?
    ): SearchEvidence
}
