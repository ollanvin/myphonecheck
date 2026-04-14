package app.myphonecheck.mobile.feature.billing

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import app.myphonecheck.mobile.core.security.tamper.TamperChecker
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
 * Security Hardened:
 * - Entitlement cache stored in EncryptedSharedPreferences
 * - Purchase token deduplication (anti-replay)
 * - TamperChecker integration (blocks billing on compromised devices)
 * - Graceful degradation with cached state + expiry
 */
class BillingManager(
    context: Context,
    private val tamperChecker: TamperChecker? = null,
) {
    private val applicationContext = context.applicationContext
    private val billingScope = CoroutineScope(Dispatchers.Default)

    /** 암호화된 entitlement 캐시 */
    private val securePrefs: SharedPreferences by lazy {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        EncryptedSharedPreferences.create(
            "ollanvin_billing_secure",
            masterKeyAlias,
            applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    companion object {
        private const val SUBSCRIPTION_PRODUCT_ID = "myphonecheck_monthly"
        private const val PREF_LAST_KNOWN_STATE = "last_known_subscription_state"
        private const val PREF_STATE_TIMESTAMP = "subscription_state_timestamp"
        private const val PREF_ACKNOWLEDGED_TOKENS = "acknowledged_purchase_tokens"
        /** 캐시 만료 시간: 24시간 */
        private const val CACHE_EXPIRY_MS = 24 * 60 * 60 * 1000L
    }

    private val _subscriptionState = MutableStateFlow<SubscriptionState>(
        SubscriptionState.Loading
    )
    val subscriptionState: StateFlow<SubscriptionState> = _subscriptionState.asStateFlow()

    /**
     * 현재 활성 구독의 Play 결제 승인 시각 (epoch millis). 0이면 미구매 또는 미확정.
     * 체험 기간 D-day 계산의 기준점으로 사용.
     */
    private val _activePurchaseTimeMillis = MutableStateFlow(0L)
    val activePurchaseTimeMillis: StateFlow<Long> = _activePurchaseTimeMillis.asStateFlow()

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
                _activePurchaseTimeMillis.value = 0L
                cacheSubscriptionState(false)
                return
            }

            // Check if purchase is acknowledged
            if (!subscriptionPurchase.isAcknowledged) {
                acknowledgePurchase(subscriptionPurchase)
            }

            // Determine subscription state based on purchase state
            when (subscriptionPurchase.purchaseState) {
                com.android.billingclient.api.Purchase.PurchaseState.PURCHASED -> {
                    _activePurchaseTimeMillis.value = subscriptionPurchase.purchaseTime
                    // Check if subscription is still valid (not cancelled)
                    if (subscriptionPurchase.isAutoRenewing) {
                        _subscriptionState.value = SubscriptionState.Active
                        cacheSubscriptionState(true)
                    } else {
                        // Subscription was cancelled but may still be valid until expiration
                        _subscriptionState.value = SubscriptionState.Expired
                        cacheSubscriptionState(false)
                    }
                }
                com.android.billingclient.api.Purchase.PurchaseState.PENDING -> {
                    _subscriptionState.value = SubscriptionState.Loading
                }
                else -> {
                    _subscriptionState.value = SubscriptionState.Expired
                    _activePurchaseTimeMillis.value = 0L
                    cacheSubscriptionState(false)
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
     * Security: TamperChecker 검증 후 결제 흐름 시작.
     * 고위험 기기에서는 결제 차단.
     *
     * @param activity The activity to launch the purchase flow from
     */
    suspend fun launchPurchaseFlow(activity: Activity) {
        // 탬퍼 체크 — 후킹/루팅 기기에서 결제 차단
        tamperChecker?.let { checker ->
            val signal = checker.check()
            if (signal.shouldBlockBilling) {
                _subscriptionState.value = SubscriptionState.Error(
                    "This device does not meet security requirements for purchases."
                )
                return
            }
        }

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
     *
     * Security: 토큰 중복 차단 — 이미 처리된 토큰은 재처리 안 함.
     */
    private suspend fun acknowledgePurchase(purchase: com.android.billingclient.api.Purchase) {
        if (purchase.isAcknowledged) {
            return
        }

        // 토큰 중복 차단 (anti-replay)
        val token = purchase.purchaseToken
        val acknowledgedTokens = securePrefs.getStringSet(PREF_ACKNOWLEDGED_TOKENS, emptySet())
            ?: emptySet()
        if (acknowledgedTokens.contains(token)) {
            return // 이미 처리된 토큰
        }

        val client = billingClient ?: return

        try {
            suspendCancellableCoroutine { continuation ->
                client.acknowledgePurchase(
                    com.android.billingclient.api.AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(token)
                        .build()
                ) { billingResult ->
                    continuation.resume(Unit)
                }
            }

            // 토큰 기록 (최대 50개 유지)
            val updatedTokens = acknowledgedTokens.toMutableSet()
            updatedTokens.add(token)
            // LRU: 50개 초과 시 오래된 것 제거
            while (updatedTokens.size > 50) {
                updatedTokens.remove(updatedTokens.first())
            }
            securePrefs.edit().putStringSet(PREF_ACKNOWLEDGED_TOKENS, updatedTokens).apply()
        } catch (e: Exception) {
            // Log error but don't fail - purchase is still valid
        }
    }

    /**
     * 마지막 알려진 구독 상태를 암호화 캐시에 저장.
     */
    private fun cacheSubscriptionState(isActive: Boolean) {
        securePrefs.edit()
            .putBoolean(PREF_LAST_KNOWN_STATE, isActive)
            .putLong(PREF_STATE_TIMESTAMP, System.currentTimeMillis())
            .apply()
    }

    /**
     * BillingClient 사용 불가 시 캐시된 구독 상태 반환.
     * 만료 시간 초과 시 NotPurchased로 fallback.
     */
    fun getCachedSubscriptionState(): SubscriptionState {
        val timestamp = securePrefs.getLong(PREF_STATE_TIMESTAMP, 0L)
        if (System.currentTimeMillis() - timestamp > CACHE_EXPIRY_MS) {
            return SubscriptionState.NotPurchased
        }
        val isActive = securePrefs.getBoolean(PREF_LAST_KNOWN_STATE, false)
        return if (isActive) SubscriptionState.Active else SubscriptionState.NotPurchased
    }
}
