package app.myphonecheck.mobile.feature.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetailsResponseListener
import com.android.billingclient.api.PurchasesResponseListener
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * Manages Google Play Billing for MyPhoneCheck.
 *
 * Handles:
 * - BillingClient connection and lifecycle
 * - Querying subscription status
 * - Launching purchase flow
 * - Acknowledging purchases
 * - Restoring purchases
 * - Emitting subscription state via StateFlow
 */
class BillingManager(
    context: Context,
) {
    private val applicationContext = context.applicationContext
    private val billingScope = CoroutineScope(Dispatchers.Default)

    private val _subscriptionState = MutableStateFlow<SubscriptionState>(
        SubscriptionState.Loading
    )
    val subscriptionState: StateFlow<SubscriptionState> = _subscriptionState.asStateFlow()

    private var billingClient: BillingClient? = null
    private var isConnected = false

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            billingScope.launch {
                for (purchase in purchases) {
                    acknowledgePurchase(purchase)
                }
                querySubscriptionStatus()
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            // User cancelled - do nothing, state remains unchanged
        } else {
            _subscriptionState.value = SubscriptionState.Error(
                "결제 오류: ${billingResult.debugMessage}"
            )
        }
    }

    private val billingClientStateListener = object : BillingClientStateListener {
        override fun onBillingServiceDisconnected() {
            isConnected = false
        }

        override fun onBillingSetupFinished(billingResult: BillingResult) {
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                isConnected = true
                billingScope.launch {
                    querySubscriptionStatus()
                }
            } else {
                _subscriptionState.value = SubscriptionState.Error(
                    "청구 서비스 연결 실패: ${billingResult.debugMessage}"
                )
            }
        }
    }

    /**
     * Initialize the BillingManager and connect to Google Play Billing.
     */
    fun initialize() {
        if (billingClient == null) {
            billingClient = BillingClient.newBuilder(applicationContext)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build()
        }

        billingClient?.let { client ->
            if (!isConnected) {
                client.startConnection(billingClientStateListener)
            }
        }
    }

    /**
     * Disconnect from billing service.
     */
    fun disconnect() {
        billingClient?.endConnection()
        billingClient = null
        isConnected = false
    }

    /**
     * Query current subscription status.
     *
     * Updates the _subscriptionState based on purchase history.
     */
    suspend fun querySubscriptionStatus() {
        if (!isConnected || billingClient == null) {
            _subscriptionState.value = SubscriptionState.Error(
                "청구 서비스에 연결되지 않았습니다"
            )
            return
        }

        try {
            val client = billingClient ?: return
            val purchasesResult = suspendCancellableCoroutine { continuation ->
                val listener = PurchasesResponseListener { billingResult, purchasesList ->
                    continuation.resume(Pair(billingResult, purchasesList))
                }
                client.queryPurchasesAsync(
                    QueryPurchasesParams.newBuilder()
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build(),
                    listener
                )
            }

            if (purchasesResult.first.responseCode != BillingClient.BillingResponseCode.OK) {
                _subscriptionState.value = SubscriptionState.Error(
                    "구독 상태 조회 실패: ${purchasesResult.first.debugMessage}"
                )
                return
            }

            val purchases = purchasesResult.second
            val subscriptionPurchase = purchases.firstOrNull { purchase ->
                purchase.products.contains(SUBSCRIPTION_PRODUCT_ID)
            }

            if (subscriptionPurchase == null) {
                _subscriptionState.value = SubscriptionState.NotPurchased
                return
            }

            // Check if purchase is acknowledged
            if (!subscriptionPurchase.isAcknowledged) {
                acknowledgePurchase(subscriptionPurchase)
            }

            // Determine subscription state based on purchase state
            when (subscriptionPurchase.purchaseState) {
                com.android.billingclient.api.Purchase.PurchaseState.PURCHASED -> {
                    // Check if subscription is still valid (not cancelled)
                    if (subscriptionPurchase.isAutoRenewing) {
                        _subscriptionState.value = SubscriptionState.Active
                    } else {
                        // Subscription was cancelled but may still be valid until expiration
                        _subscriptionState.value = SubscriptionState.Expired
                    }
                }
                com.android.billingclient.api.Purchase.PurchaseState.PENDING -> {
                    _subscriptionState.value = SubscriptionState.Loading
                }
                else -> {
                    _subscriptionState.value = SubscriptionState.Expired
                }
            }
        } catch (e: Exception) {
            _subscriptionState.value = SubscriptionState.Error(
                "구독 상태 조회 중 오류: ${e.message}"
            )
        }
    }

    /**
     * Launch the purchase flow for the MyPhoneCheck monthly subscription.
     *
     * @param activity The activity to launch the purchase flow from
     */
    suspend fun launchPurchaseFlow(activity: Activity) {
        if (!isConnected || billingClient == null) {
            _subscriptionState.value = SubscriptionState.Error(
                "청구 서비스에 연결되지 않았습니다"
            )
            return
        }

        try {
            val client = billingClient ?: return
            _subscriptionState.value = SubscriptionState.Loading

            // Query product details
            val productDetailsResult = suspendCancellableCoroutine { continuation ->
                val listener = ProductDetailsResponseListener { billingResult, productDetailsList ->
                    continuation.resume(Pair(billingResult, productDetailsList))
                }
                client.queryProductDetailsAsync(
                    QueryProductDetailsParams.newBuilder()
                        .setProductList(listOf(
                            QueryProductDetailsParams.Product.newBuilder()
                                .setProductId(SUBSCRIPTION_PRODUCT_ID)
                                .setProductType(BillingClient.ProductType.SUBS)
                                .build()
                        )
                        )
                        .build(),
                    listener
                )
            }

            if (productDetailsResult.first.responseCode != BillingClient.BillingResponseCode.OK) {
                _subscriptionState.value = SubscriptionState.Error(
                    "상품 정보 조회 실패: ${productDetailsResult.first.debugMessage}"
                )
                return
            }

            val productDetails = productDetailsResult.second.firstOrNull()
            if (productDetails == null) {
                _subscriptionState.value = SubscriptionState.Error(
                    "구독 상품을 찾을 수 없습니다"
                )
                return
            }

            // Get the subscription offer (first available)
            val subscriptionOfferDetail = productDetails.subscriptionOfferDetails?.firstOrNull()
            if (subscriptionOfferDetail == null) {
                _subscriptionState.value = SubscriptionState.Error(
                    "구독 요금 옵션을 찾을 수 없습니다"
                )
                return
            }

            // Launch purchase flow
            val billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(
                    listOf(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                            .setProductDetails(productDetails)
                            .setOfferToken(subscriptionOfferDetail.offerToken)
                            .build()
                    )
                )
                .build()

            val billingResult = client.launchBillingFlow(activity, billingFlowParams)
            if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                _subscriptionState.value = SubscriptionState.Error(
                    "결제 흐름 시작 실패: ${billingResult.debugMessage}"
                )
            }
        } catch (e: Exception) {
            _subscriptionState.value = SubscriptionState.Error(
                "결제 흐름 시작 중 오류: ${e.message}"
            )
        }
    }

    /**
     * Restore purchases (for users who already have active subscriptions).
     */
    suspend fun restorePurchases() {
        querySubscriptionStatus()
    }

    /**
     * Acknowledge a purchase (required for subscription management).
     */
    private suspend fun acknowledgePurchase(purchase: com.android.billingclient.api.Purchase) {
        if (purchase.isAcknowledged) {
            return
        }

        val client = billingClient ?: return

        try {
            suspendCancellableCoroutine { continuation ->
                client.acknowledgePurchase(
                    com.android.billingclient.api.AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()
                ) { billingResult ->
                    continuation.resume(Unit)
                }
            }
        } catch (e: Exception) {
            // Log error but don't fail - purchase is still valid
        }
    }

    companion object {
        private const val SUBSCRIPTION_PRODUCT_ID = "myphonecheck_monthly"
    }
}
