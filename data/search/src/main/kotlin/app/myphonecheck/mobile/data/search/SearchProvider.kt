package app.myphonecheck.mobile.data.search

interface SearchProvider {
    val providerName: String
    suspend fun search(phoneNumber: String, countryCode: String?): SearchProviderResult
}
