package app.callcheck.mobile.core.model

/**
 * Context for an incoming call that needs a decision.
 */
data class IncomingNumberContext(
    val rawNumber: String,
    val normalizedNumber: String,
    val countryCode: String?,
    val receivedAt: Long,
)
