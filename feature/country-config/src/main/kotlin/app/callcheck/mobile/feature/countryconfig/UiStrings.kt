package app.callcheck.mobile.feature.countryconfig

/**
 * Localized UI strings for a specific country/language.
 *
 * Contains all user-facing strings for decision cards, settings, and other UI.
 */
data class UiStrings(
    // Risk level labels
    val riskLevelSafe: String,
    val riskLevelLow: String,
    val riskLevelMedium: String,
    val riskLevelHigh: String,
    val riskLevelCritical: String,

    // Call type labels
    val callTypeDelivery: String,
    val callTypeHospital: String,
    val callTypeInstitution: String,
    val callTypeBusiness: String,
    val callTypeFinanceSpam: String,
    val callTypeScam: String,
    val callTypeTelemarketing: String,
    val callTypeUnknown: String,

    // Action recommendations
    val actionAnswer: String,
    val actionReject: String,
    val actionBlock: String,

    // General UI
    val appName: String,
    val settings: String,
    val language: String,
    val country: String,
    val aboutUs: String,
    val privacyPolicy: String,
    val termsOfService: String,
    val contactUs: String,
    val version: String,
)
