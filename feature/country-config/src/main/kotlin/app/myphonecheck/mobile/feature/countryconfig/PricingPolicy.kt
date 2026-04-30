package app.myphonecheck.mobile.feature.countryconfig

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

/**
 * MyPhoneCheck 프리미엄 가격 정책.
 *
 * ┌──────────────────────────────────────────────────────────────┐
 * │ 3-Tier 가격 구조 (월간 단일, USD 환산)                        │
 * ├──────────────────────────────────────────────────────────────┤
 * │ Tier 1: $9.9/월  — 고소득 선진국 (대표: 미국, 한국)           │
 * │ Tier 2: $6.9/월  — 중소득 국가                               │
 * │ Tier 3: $3.9/월  — 저소득 국가 (대표: 인도)                   │
 * │                                                              │
 * │ 연간 플랜 없음 — 월간 단일 구독만 제공                        │
 * │ 무료 체험: 첫 30일 무료 (전 티어 동일)                        │
 * │                                                              │
 * │ Google Play Console:                                          │
 * │ - base-plan ID: monthly                                      │
 * │ - offer ID: free-trial-1month                                │
 * │ - 국가별 가격은 Play Console에서 "국가별 가격 설정" 사용      │
 * └──────────────────────────────────────────────────────────────┘
 */
data class PricingTier(
    val tierId: Int,
    val monthlyPriceUsd: String,
    val freeTrialDays: Int,
    val playBasePlanMonthly: String,
    val playOfferIdTrial: String,
) {
    companion object {
        val TIER_1 = PricingTier(
            tierId = 1,
            monthlyPriceUsd = "$9.9",
            freeTrialDays = 30,
            playBasePlanMonthly = "myphonecheck-premium-monthly",
            playOfferIdTrial = "free-trial-1month",
        )
        val TIER_2 = PricingTier(
            tierId = 2,
            monthlyPriceUsd = "$6.9",
            freeTrialDays = 30,
            playBasePlanMonthly = "myphonecheck-premium-monthly",
            playOfferIdTrial = "free-trial-1month",
        )
        val TIER_3 = PricingTier(
            tierId = 3,
            monthlyPriceUsd = "$3.9",
            freeTrialDays = 30,
            playBasePlanMonthly = "myphonecheck-premium-monthly",
            playOfferIdTrial = "free-trial-1month",
        )
    }
}

/**
 * 국가별 Pricing Tier 매핑.
 *
 * 분류 기준: World Bank income group + 통신 시장 성숙도.
 * Google Play Console 국가 코드(ISO 3166-1 alpha-2) 기준.
 */
object CountryPricingMapper {

    /**
     * 국가 코드 → PricingTier 반환.
     * 미매핑 국가는 Tier 3 (가장 저렴) 적용.
     */
    fun getTier(countryCode: String?): PricingTier {
        if (countryCode == null) return PricingTier.TIER_1  // 탐지 실패 시 최고가 (보수적)
        return when (countryCode.uppercase()) {
            in TIER_1_COUNTRIES -> PricingTier.TIER_1
            in TIER_2_COUNTRIES -> PricingTier.TIER_2
            else -> PricingTier.TIER_3
        }
    }

    private val TIER_1_COUNTRIES = setOf(
        "US", "CA",
        "GB", "DE", "FR", "IT", "ES", "NL", "BE", "AT", "CH", "IE",
        "LU", "FI", "DK", "SE", "NO", "IS",
        "JP", "KR", "AU", "NZ", "SG", "HK", "TW",
        "AE", "SA", "QA", "KW", "BH", "IL",
        "PT", "CZ", "PL",
    )

    private val TIER_2_COUNTRIES = setOf(
        "RU", "UA", "RO", "HU", "SK", "BG", "HR", "SI", "RS", "BA",
        "ME", "MK", "AL", "GE", "AM", "AZ", "BY", "MD",
        "BR", "MX", "AR", "CL", "CO", "PE", "UY", "CR", "PA", "DO",
        "EC", "GT", "SV",
        "CN", "MY", "TH", "ID", "PH", "VN",
        "TR", "EG", "MA", "TN", "ZA", "NG", "KE", "GH",
        "KZ", "UZ",
    )
    // Tier 3: 명시 매핑 불필요 — getTier() default 분기.
}

/**
 * 가격 관련 온보딩/결제 UI 문구 — 영문 단일 (헌법 §9-1).
 *
 * 통화 표시는 ICU `NumberFormat` 으로 OS Locale 기반 표기.
 * 다국어 분기 / 다국어 하드코딩 영구 금지.
 */
data class PricingUiMessages(
    /** 무료 체험 안내 문구 (일수는 {days} 플레이스홀더) */
    val freeTrialMessage: String,
    /** 구독 시작 버튼 텍스트 */
    val subscribeButton: String,
    /** 구독 취소 버튼 텍스트 */
    val cancelSubscriptionButton: String,
    /** 취소 안내 (Google Play 경유) */
    val cancellationNote: String,
    /** 리펀드 불가 + 잔여 기간 서비스 유지 안내 */
    val noRefundNotice: String,
    /** 월간 가격 표시 ({price} 플레이스홀더) */
    val monthlyPriceLabel: String,
    /** 지역별 가격 차이 설명 — 사용자가 "왜 이 가격?" 의문 해소 */
    val regionalPricingNote: String,
    /** 무료 체험 중 해지 가능 안내 — 경계심 해소 */
    val trialCancelNote: String,
    /** 핵심 가치 제안 — 왜 결제해야 하는지 (불안 제거 결제) */
    val valueProposition: String,
) {
    fun formatFreeTrial(days: Int): String = freeTrialMessage.replace("{days}", days.toString())
    fun formatMonthlyPrice(price: String): String = monthlyPriceLabel.replace("{price}", price)

    companion object {
        /**
         * 영문 단일 문구 반환. SupportedLanguage 입력은 무시하고 EN 동일 결과.
         * 호출자 시그니처 보존을 위해 파라미터는 유지한다.
         */
        @Suppress("UNUSED_PARAMETER")
        fun forLanguage(language: SupportedLanguage): PricingUiMessages = EN

        /**
         * ICU `NumberFormat` 기반 통화 표시.
         * OS Locale 자동 추종 — 호출자가 Locale 분기 영구 금지.
         *
         * @param amountUsd USD 금액 (Tier 1 = 9.9, Tier 2 = 6.9, Tier 3 = 3.9)
         * @param currencyCode ISO 4217 통화 코드. 기본값 USD.
         */
        fun formatCurrency(amountUsd: Double, currencyCode: String = "USD"): String {
            val format = NumberFormat.getCurrencyInstance(Locale.getDefault()).apply {
                currency = Currency.getInstance(currencyCode)
            }
            return format.format(amountUsd)
        }

        private val EN = PricingUiMessages(
            freeTrialMessage = "First {days} days free",
            subscribeButton = "Start Premium Subscription",
            cancelSubscriptionButton = "Cancel Subscription",
            cancellationNote = "You can cancel your subscription anytime on Google Play.",
            noRefundNotice = "No refunds on cancellation. Your service remains active until the end of the current billing period.",
            monthlyPriceLabel = "{price}/mo",
            regionalPricingNote = "All plans include the same features. Pricing varies by region.",
            trialCancelNote = "Cancel anytime during your trial. Service continues until the end of the period.",
            valueProposition = "Calls, notifications, messages, and privacy — one app judges all four threats. Your data never leaves the device.",
        )
    }
}
