package app.callcheck.mobile.core.model

data class CountryConfig(
    val countryCode: String,
    val countryName: String,
    val phoneNumberFormat: String,
    val phoneNumberLength: IntRange,
    val areaCodeLength: Int,
    val currencyCode: String,
    val billingAvailable: Boolean,
    val searchProvidersAvailable: List<String>,
    val isEnabled: Boolean = true,
    val lastUpdated: Long,
)

data class CountryConfigRequest(
    val countryCode: String,
)

data class CountryConfigResponse(
    val config: CountryConfig,
    val timestamp: Long,
)
