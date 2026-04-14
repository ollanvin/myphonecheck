package app.myphonecheck.mobile.core.model

/**
 * Country differences must stay inside the policy layer, not the global core pipeline.
 */
interface CountryPolicyLayer {
    val countryCode: String

    fun normalizeIdentifier(rawIdentifier: String): String?

    fun isEmergencyOrSpecialNumber(normalizedIdentifier: String): Boolean

    fun riskBoost(normalizedIdentifier: String): Float

    fun searchSourceAvailable(): Boolean
}

/**
 * Presentation-only policy for wording and formatting.
 */
interface PresentationLayerPolicy {
    fun searchStatusLabel(status: SearchStatus): String

    fun formatDateTime(epochMillis: Long): String
}
