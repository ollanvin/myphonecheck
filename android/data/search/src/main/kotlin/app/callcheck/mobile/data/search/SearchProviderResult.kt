package app.callcheck.mobile.data.search

data class SearchProviderResult(
    val provider: String,
    val results: List<RawSearchResult>,
    val responseTimeMs: Long,
    val success: Boolean,
    val error: String? = null
)

data class RawSearchResult(
    val title: String,
    val snippet: String,
    val url: String,
    val domain: String,
    val language: String?,
    val providerName: String = "Unknown",  // 어느 검색 엔진에서 온 결과인지
)
