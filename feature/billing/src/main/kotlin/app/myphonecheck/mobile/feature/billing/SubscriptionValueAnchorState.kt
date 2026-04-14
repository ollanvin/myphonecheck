package app.myphonecheck.mobile.feature.billing

data class SubscriptionValueAnchorState(
    val selectedPeriodLabel: String,
    val suspiciousCallsCount: Int?,
    val riskyLinkMessagesCount: Int?,
    val visible: Boolean,
)
