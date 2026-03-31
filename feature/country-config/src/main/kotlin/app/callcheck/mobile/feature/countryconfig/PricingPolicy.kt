package app.callcheck.mobile.feature.countryconfig

/**
 * CallCheck 프리미엄 가격 정책.
 *
 * ┌──────────────────────────────────────────────────────────────┐
 * │ 3-Tier 가격 구조 (월간 기준, USD 환산)                        │
 * ├──────────────────────────────────────────────────────────────┤
 * │ Tier 1: $9.99/월  — 고소득 선진국                            │
 * │ Tier 2: $6.99/월  — 중소득 국가                              │
 * │ Tier 3: $3.99/월  — 저소득 국가                              │
 * │                                                              │
 * │ 연간 구독: 월간 × 10 (2개월 무료)                            │
 * │ Tier 1: $99.99/년, Tier 2: $69.99/년, Tier 3: $39.99/년    │
 * │                                                              │
 * │ 무료 체험:                                                    │
 * │ Tier 1: 7일, Tier 2: 5일, Tier 3: 3일                      │
 * │                                                              │
 * │ Google Play Console:                                          │
 * │ - base-plan ID: monthly / yearly                             │
 * │ - offer ID: free-trial-t1 / free-trial-t2 / free-trial-t3  │
 * │ - 국가별 가격은 Play Console에서 "국가별 가격 설정" 사용      │
 * └──────────────────────────────────────────────────────────────┘
 */
data class PricingTier(
    val tierId: Int,
    val monthlyPriceUsd: String,
    val yearlyPriceUsd: String,
    val freeTrialDays: Int,
    val playBasePlanMonthly: String,
    val playBasePlanYearly: String,
    val playOfferIdTrial: String,
) {
    companion object {
        val TIER_1 = PricingTier(
            tierId = 1,
            monthlyPriceUsd = "$9.99",
            yearlyPriceUsd = "$99.99",
            freeTrialDays = 7,
            playBasePlanMonthly = "callcheck-premium-monthly",
            playBasePlanYearly = "callcheck-premium-yearly",
            playOfferIdTrial = "free-trial-t1",
        )
        val TIER_2 = PricingTier(
            tierId = 2,
            monthlyPriceUsd = "$6.99",
            yearlyPriceUsd = "$69.99",
            freeTrialDays = 5,
            playBasePlanMonthly = "callcheck-premium-monthly",
            playBasePlanYearly = "callcheck-premium-yearly",
            playOfferIdTrial = "free-trial-t2",
        )
        val TIER_3 = PricingTier(
            tierId = 3,
            monthlyPriceUsd = "$3.99",
            yearlyPriceUsd = "$39.99",
            freeTrialDays = 3,
            playBasePlanMonthly = "callcheck-premium-monthly",
            playBasePlanYearly = "callcheck-premium-yearly",
            playOfferIdTrial = "free-trial-t3",
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
    /** 연간 절약 안내 */
    val yearlySavingsMessage: String,
    /** 취소 안내 */
    val cancellationNote: String,
) {
    fun formatFreeTrial(days: Int): String = freeTrialMessage.replace("{days}", days.toString())

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
            freeTrialMessage = "{days}일 무료 체험 시작",
            subscribeButton = "프리미엄 구독 시작",
            yearlySavingsMessage = "연간 구독으로 2개월 무료 혜택",
            cancellationNote = "언제든 Google Play에서 구독을 취소할 수 있습니다.",
        )

        private val EN = PricingUiMessages(
            freeTrialMessage = "Start {days}-day free trial",
            subscribeButton = "Start Premium Subscription",
            yearlySavingsMessage = "Save 2 months with yearly subscription",
            cancellationNote = "You can cancel your subscription anytime on Google Play.",
        )

        private val JA = PricingUiMessages(
            freeTrialMessage = "{days}日間無料トライアル開始",
            subscribeButton = "プレミアム購読開始",
            yearlySavingsMessage = "年間プランで2ヶ月分お得",
            cancellationNote = "いつでもGoogle Playでサブスクリプションをキャンセルできます。",
        )

        private val ZH = PricingUiMessages(
            freeTrialMessage = "开始{days}天免费试用",
            subscribeButton = "开始高级订阅",
            yearlySavingsMessage = "年度订阅可节省2个月费用",
            cancellationNote = "您可以随时在Google Play取消订阅。",
        )

        private val RU = PricingUiMessages(
            freeTrialMessage = "Начать бесплатный пробный период на {days} дней",
            subscribeButton = "Начать премиум-подписку",
            yearlySavingsMessage = "Экономьте 2 месяца с годовой подпиской",
            cancellationNote = "Вы можете отменить подписку в любое время через Google Play.",
        )

        private val ES = PricingUiMessages(
            freeTrialMessage = "Comenzar prueba gratuita de {days} días",
            subscribeButton = "Iniciar suscripción premium",
            yearlySavingsMessage = "Ahorra 2 meses con la suscripción anual",
            cancellationNote = "Puedes cancelar tu suscripción en cualquier momento desde Google Play.",
        )

        private val AR = PricingUiMessages(
            freeTrialMessage = "بدء تجربة مجانية لمدة {days} أيام",
            subscribeButton = "بدء الاشتراك المميز",
            yearlySavingsMessage = "وفر شهرين مع الاشتراك السنوي",
            cancellationNote = "يمكنك إلغاء اشتراكك في أي وقت من Google Play.",
        )
    }
}
