package app.callcheck.mobile.feature.countryconfig

/**
 * CallCheck 프리미엄 가격 정책.
 *
 * ┌──────────────────────────────────────────────────────────────┐
 * │ 3-Tier 가격 구조 (월간 단일, USD 환산)                        │
 * ├──────────────────────────────────────────────────────────────┤
 * │ Tier 1: $9.9/월  — 고소득 선진국 (대표: 미국, 한국)           │
 * │ Tier 2: $6.9/월  — 중소득 국가                               │
 * │ Tier 3: $3.9/월  — 저소득 국가 (대표: 인도)                   │
 * │                                                              │
 * │ 연간 플랜 없음 — 월간 단일 구독만 제공                        │
 * │                                                              │
 * │ 무료 체험: 첫 구매 시 1개월 무료 (전 티어 동일)               │
 * │                                                              │
 * │ 해지 정책:                                                    │
 * │ - 리펀드 없음                                                 │
 * │ - 해지 시 남은 기간 끝까지 서비스 유지                        │
 * │ - 구독 취소 버튼 항상 노출                                    │
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
            playBasePlanMonthly = "callcheck-premium-monthly",
            playOfferIdTrial = "free-trial-1month",
        )
        val TIER_2 = PricingTier(
            tierId = 2,
            monthlyPriceUsd = "$6.9",
            freeTrialDays = 30,
            playBasePlanMonthly = "callcheck-premium-monthly",
            playOfferIdTrial = "free-trial-1month",
        )
        val TIER_3 = PricingTier(
            tierId = 3,
            monthlyPriceUsd = "$3.9",
            freeTrialDays = 30,
            playBasePlanMonthly = "callcheck-premium-monthly",
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

    // ══════════════════════════════════════
    // Tier 1: $9.99 — 고소득 선진국 (약 35개국)
    // ══════════════════════════════════════
    private val TIER_1_COUNTRIES = setOf(
        // North America
        "US", "CA",
        // Western Europe
        "GB", "DE", "FR", "IT", "ES", "NL", "BE", "AT", "CH", "IE",
        "LU", "FI", "DK", "SE", "NO", "IS",
        // Asia-Pacific (고소득)
        "JP", "KR", "AU", "NZ", "SG", "HK", "TW",
        // Middle East (고소득)
        "AE", "SA", "QA", "KW", "BH", "IL",
        // Other
        "PT", "CZ", "PL",
    )

    // ══════════════════════════════════════
    // Tier 2: $6.99 — 중소득 국가 (약 55개국)
    // ══════════════════════════════════════
    private val TIER_2_COUNTRIES = setOf(
        // Eastern Europe
        "RU", "UA", "RO", "HU", "SK", "BG", "HR", "SI", "RS", "BA",
        "ME", "MK", "AL", "GE", "AM", "AZ", "BY", "MD",
        // Latin America (중소득)
        "BR", "MX", "AR", "CL", "CO", "PE", "UY", "CR", "PA", "DO",
        "EC", "GT", "SV",
        // Asia (중소득)
        "CN", "MY", "TH", "ID", "PH", "VN",
        // Middle East / Africa (중소득)
        "TR", "EG", "MA", "TN", "ZA", "NG", "KE", "GH",
        // Central Asia
        "KZ", "UZ",
    )

    // ══════════════════════════════════════
    // Tier 3: $3.99 — 나머지 전체 (~100+ 국가)
    // 명시적 매핑 불필요: getTier() default = TIER_3
    // ══════════════════════════════════════
}

/**
 * 가격 관련 온보딩/결제 UI 문구 — 언어별.
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
) {
    fun formatFreeTrial(days: Int): String = freeTrialMessage.replace("{days}", days.toString())
    fun formatMonthlyPrice(price: String): String = monthlyPriceLabel.replace("{price}", price)

    companion object {
        fun forLanguage(language: SupportedLanguage): PricingUiMessages {
            return when (language) {
                SupportedLanguage.KO -> KO
                SupportedLanguage.JA -> JA
                SupportedLanguage.ZH -> ZH
                SupportedLanguage.RU -> RU
                SupportedLanguage.ES -> ES
                SupportedLanguage.AR -> AR
                else -> EN
            }
        }

        private val KO = PricingUiMessages(
            freeTrialMessage = "첫 {days}일 무료 체험",
            subscribeButton = "프리미엄 구독 시작",
            cancelSubscriptionButton = "구독 취소",
            cancellationNote = "언제든 Google Play에서 구독을 취소할 수 있습니다.",
            noRefundNotice = "구독 취소 시 환불은 제공되지 않으며, 남은 결제 기간까지 서비스가 유지됩니다.",
            monthlyPriceLabel = "월 {price}",
        )

        private val EN = PricingUiMessages(
            freeTrialMessage = "First {days} days free",
            subscribeButton = "Start Premium Subscription",
            cancelSubscriptionButton = "Cancel Subscription",
            cancellationNote = "You can cancel your subscription anytime on Google Play.",
            noRefundNotice = "No refunds on cancellation. Your service remains active until the end of the current billing period.",
            monthlyPriceLabel = "{price}/mo",
        )

        private val JA = PricingUiMessages(
            freeTrialMessage = "最初の{days}日間無料",
            subscribeButton = "プレミアム購読開始",
            cancelSubscriptionButton = "購読をキャンセル",
            cancellationNote = "いつでもGoogle Playでサブスクリプションをキャンセルできます。",
            noRefundNotice = "キャンセル時の返金はありません。現在の請求期間の終了までサービスは継続されます。",
            monthlyPriceLabel = "月額 {price}",
        )

        private val ZH = PricingUiMessages(
            freeTrialMessage = "前{days}天免费",
            subscribeButton = "开始高级订阅",
            cancelSubscriptionButton = "取消订阅",
            cancellationNote = "您可以随时在Google Play取消订阅。",
            noRefundNotice = "取消订阅不予退款。服务将持续到当前计费周期结束。",
            monthlyPriceLabel = "每月 {price}",
        )

        private val RU = PricingUiMessages(
            freeTrialMessage = "Первые {days} дней бесплатно",
            subscribeButton = "Начать премиум-подписку",
            cancelSubscriptionButton = "Отменить подписку",
            cancellationNote = "Вы можете отменить подписку в любое время через Google Play.",
            noRefundNotice = "Возврат средств при отмене не предусмотрен. Сервис будет доступен до конца текущего расчётного периода.",
            monthlyPriceLabel = "{price}/мес",
        )

        private val ES = PricingUiMessages(
            freeTrialMessage = "Primeros {days} días gratis",
            subscribeButton = "Iniciar suscripción premium",
            cancelSubscriptionButton = "Cancelar suscripción",
            cancellationNote = "Puedes cancelar tu suscripción en cualquier momento desde Google Play.",
            noRefundNotice = "No se realizan reembolsos al cancelar. El servicio se mantiene activo hasta el final del período de facturación actual.",
            monthlyPriceLabel = "{price}/mes",
        )

        private val AR = PricingUiMessages(
            freeTrialMessage = "أول {days} أيام مجاناً",
            subscribeButton = "بدء الاشتراك المميز",
            cancelSubscriptionButton = "إلغاء الاشتراك",
            cancellationNote = "يمكنك إلغاء اشتراكك في أي وقت من Google Play.",
            noRefundNotice = "لا يتم استرداد المبلغ عند الإلغاء. تستمر الخدمة حتى نهاية فترة الفوترة الحالية.",
            monthlyPriceLabel = "{price}/شهر",
        )
    }
}
