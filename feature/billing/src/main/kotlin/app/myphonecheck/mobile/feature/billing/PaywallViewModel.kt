package app.myphonecheck.mobile.feature.billing

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Subscription / Paywall screen.
 *
 * 책임:
 *  - 구독 상태 관찰 (BillingManager)
 *  - 가치 앵커 카드 상태 로드 (Repository)
 *  - 체험 잔여 일수(D-day) 계산 (computeTrialDday)
 *  - 구매 흐름 / 복원 트리거
 */
@HiltViewModel
class PaywallViewModel @Inject constructor(
    private val billingManager: BillingManager,
    private val valueAnchorRepository: SubscriptionValueAnchorRepository,
) : ViewModel() {

    val subscriptionState: StateFlow<SubscriptionState> = billingManager.subscriptionState

    /** 체험 잔여 일수. BillingManager.activePurchaseTimeMillis와 동기화. */
    val trialCountdown: StateFlow<TrialCountdown> = billingManager.activePurchaseTimeMillis
        .combine(billingManager.subscriptionState) { purchaseTime, state ->
            if (state is SubscriptionState.Active) {
                computeTrialDday(purchaseTimeMillis = purchaseTime)
            } else {
                TrialCountdown.NotApplicable
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = TrialCountdown.NotApplicable,
        )

    private val _valueAnchor = MutableStateFlow<SubscriptionValueAnchorState?>(null)
    val valueAnchor: StateFlow<SubscriptionValueAnchorState?> = _valueAnchor.asStateFlow()

    init {
        billingManager.initialize()
        loadValueAnchor()
    }

    /**
     * 가치 앵커 카드 상태를 로드한다.
     * 모든 창에서 0건이면 null로 설정 (UI에서 카드 숨김).
     */
    fun loadValueAnchor() {
        viewModelScope.launch {
            _valueAnchor.value = valueAnchorRepository.load()
        }
    }

    /**
     * Trigger the purchase flow for the MyPhoneCheck monthly subscription.
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
