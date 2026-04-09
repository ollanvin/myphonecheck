package app.myphonecheck.mobile.feature.billing

sealed interface SubscriptionState {
    object Loading : SubscriptionState
    object Active : SubscriptionState
    object Expired : SubscriptionState
    object NotPurchased : SubscriptionState
    data class Error(val message: String) : SubscriptionState
}
