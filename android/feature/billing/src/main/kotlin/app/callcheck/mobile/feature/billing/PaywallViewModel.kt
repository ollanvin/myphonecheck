package app.callcheck.mobile.feature.billing

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Paywall screen.
 *
 * Manages:
 * - Observing subscription state
 * - Triggering purchase flow
 * - Triggering restore purchases
 */
@HiltViewModel
class PaywallViewModel @Inject constructor(
    private val billingManager: BillingManager,
) : ViewModel() {

    val subscriptionState: StateFlow<SubscriptionState> = billingManager.subscriptionState

    init {
        billingManager.initialize()
    }

    /**
     * Trigger the purchase flow for the CallCheck monthly subscription.
     *
     * @param activity The activity to launch the purchase flow from
     */
    fun purchaseSubscription(activity: Activity) {
        viewModelScope.launch {
            billingManager.launchPurchaseFlow(activity)
        }
    }

    /**
     * Restore previously purchased subscriptions.
     */
    fun restorePurchases() {
        viewModelScope.launch {
            billingManager.restorePurchases()
        }
    }

    override fun onCleared() {
        super.onCleared()
        billingManager.disconnect()
    }
}
