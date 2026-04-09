package app.myphonecheck.mobile.feature.countryconfig

/**
 * Keyword dictionary for categorizing incoming calls.
 *
 * Contains sets of keywords that indicate different call types.
 */
data class KeywordDictionary(
    val delivery: Set<String>,
    val hospital: Set<String>,
    val institution: Set<String>,
    val business: Set<String>,
    val financeSpam: Set<String>,
    val scam: Set<String>,
    val telemarketing: Set<String>,
)
