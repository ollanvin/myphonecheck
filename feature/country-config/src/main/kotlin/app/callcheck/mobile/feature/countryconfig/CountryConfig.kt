package app.callcheck.mobile.feature.countryconfig

/**
 * Complete configuration for a specific country.
 *
 * Contains:
 * - Country/language metadata
 * - Phone number formatting rules
 * - Search provider priorities
 * - Keywords for call categorization
 * - Localized UI strings
 */
data class CountryConfig(
    val countryCode: String,
    val language: String,
    val phonePrefix: String,
    val searchProviderPriority: List<String>,
    val keywordDictionary: KeywordDictionary,
    val uiStrings: UiStrings,
)
