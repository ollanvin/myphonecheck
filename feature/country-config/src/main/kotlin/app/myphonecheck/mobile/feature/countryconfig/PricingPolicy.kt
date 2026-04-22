package app.myphonecheck.mobile.feature.countryconfig

/**
 * MyPhoneCheck 프리미엄 가격 정책.
 *
 * ┌──────────────────────────────────────────────────────────────┐
 * │ v1.1 단일 가격 구조                                           │
 * ├──────────────────────────────────────────────────────────────┤
 * │ $1.99/월  — 전 세계 동일 가격                                 │
 * │                                                              │
 * │ 연간 플랜 없음 — 월간 단일 구독만 제공                        │
 * │                                                              │
 * │ 무료 체험: 첫 구매 시 1개월 무료                              │
 * │                                                              │
 * │ 해지 정책:                                                    │
 * │ - 리펀드 없음                                                 │
 * │ - 해지 시 남은 기간 끝까지 서비스 유지                        │
 * │ - 구독 취소 버튼 항상 노출                                    │
 * │                                                              │
 * │ Google Play Console:                                          │
 * │ - base-plan ID: monthly                                      │
 * │ - offer ID: free-trial-1month                                │
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
        /** v1.1: 단일 가격 $1.99/월 — 전 세계 동일 */
        val SINGLE = PricingTier(
            tierId = 1,
            monthlyPriceUsd = "$1.99",
            freeTrialDays = 30,
            playBasePlanMonthly = "myphonecheck-premium-monthly",
            playOfferIdTrial = "free-trial-1month",
        )

        // 하위호환: 기존 3-Tier 참조 → 전부 SINGLE로 매핑
        @Deprecated("v1.1: 단일 가격 정책으로 전환. SINGLE 사용")
        val TIER_1 = SINGLE
        @Deprecated("v1.1: 단일 가격 정책으로 전환. SINGLE 사용")
        val TIER_2 = SINGLE
        @Deprecated("v1.1: 단일 가격 정책으로 전환. SINGLE 사용")
        val TIER_3 = SINGLE
    }
}

/**
 * 국가별 Pricing Tier 매핑.
 *
 * v1.1: 단일 가격 $1.99/월 — 국가 무관.
 * 기존 3-Tier 분류 로직은 제거하고 단일 반환.
 */
object CountryPricingMapper {

    /**
     * 국가 코드 → PricingTier 반환.
     * v1.1: 전 세계 동일 $1.99/월.
     */
    fun getTier(countryCode: String?): PricingTier {
        return PricingTier.SINGLE
    }
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
            regionalPricingNote = "전 세계 동일 가격입니다.",
            trialCancelNote = "체험 기간 중 언제든 해지 가능. 해지해도 남은 기간까지 사용 가능.",
            valueProposition = "전화·문자·프라이버시, 3가지 위협을 하나의 앱이 판단합니다. 데이터는 기기 밖으로 나가지 않습니다.",
        )

        private val EN = PricingUiMessages(
            freeTrialMessage = "First {days} days free",
            subscribeButton = "Start Premium Subscription",
            cancelSubscriptionButton = "Cancel Subscription",
            cancellationNote = "You can cancel your subscription anytime on Google Play.",
            noRefundNotice = "No refunds on cancellation. Your service remains active until the end of the current billing period.",
            monthlyPriceLabel = "{price}/mo",
            regionalPricingNote = "Same price worldwide.",
            trialCancelNote = "Cancel anytime during your trial. Service continues until the end of the period.",
            valueProposition = "Calls, messages, and privacy — one app judges all three threats. Your data never leaves the device.",
        )

        private val JA = PricingUiMessages(
            freeTrialMessage = "最初の{days}日間無料",
            subscribeButton = "プレミアム購読開始",
            cancelSubscriptionButton = "購読をキャンセル",
            cancellationNote = "いつでもGoogle Playでサブスクリプションをキャンセルできます。",
            noRefundNotice = "キャンセル時の返金はありません。現在の請求期間の終了までサービスは継続されます。",
            monthlyPriceLabel = "月額 {price}",
            regionalPricingNote = "世界中同一価格です。",
            trialCancelNote = "体験期間中いつでも解約可能。解約後も期間終了まで利用できます。",
            valueProposition = "通話・メッセージ・プライバシー、3つの脅威を1つのアプリが判断。データは端末の外に出ません。",
        )

        private val ZH = PricingUiMessages(
            freeTrialMessage = "前{days}天免费",
            subscribeButton = "开始高级订阅",
            cancelSubscriptionButton = "取消订阅",
            cancellationNote = "您可以随时在Google Play取消订阅。",
            noRefundNotice = "取消订阅不予退款。服务将持续到当前计费周期结束。",
            monthlyPriceLabel = "每月 {price}",
            regionalPricingNote = "全球统一价格。",
            trialCancelNote = "试用期间随时可取消。取消后服务持续到期末。",
            valueProposition = "电话、短信、隐私——一个应用判断三种威胁。数据绝不离开设备。",
        )

        private val RU = PricingUiMessages(
            freeTrialMessage = "Первые {days} дней бесплатно",
            subscribeButton = "Начать премиум-подписку",
            cancelSubscriptionButton = "Отменить подписку",
            cancellationNote = "Вы можете отменить подписку в любое время через Google Play.",
            noRefundNotice = "Возврат средств при отмене не предусмотрен. Сервис будет доступен до конца текущего расчётного периода.",
            monthlyPriceLabel = "{price}/мес",
            regionalPricingNote = "Единая цена по всему миру.",
            trialCancelNote = "Отмена возможна в любой момент пробного периода. Сервис доступен до конца периода.",
            valueProposition = "Звонки, сообщения и конфиденциальность — одно приложение оценивает все 3 угрозы. Данные не покидают устройство.",
        )

        private val ES = PricingUiMessages(
            freeTrialMessage = "Primeros {days} días gratis",
            subscribeButton = "Iniciar suscripción premium",
            cancelSubscriptionButton = "Cancelar suscripción",
            cancellationNote = "Puedes cancelar tu suscripción en cualquier momento desde Google Play.",
            noRefundNotice = "No se realizan reembolsos al cancelar. El servicio se mantiene activo hasta el final del período de facturación actual.",
            monthlyPriceLabel = "{price}/mes",
            regionalPricingNote = "Mismo precio en todo el mundo.",
            trialCancelNote = "Cancela en cualquier momento durante la prueba. El servicio continúa hasta el final del período.",
            valueProposition = "Llamadas, mensajes y privacidad: una app evalúa las 3 amenazas. Tus datos nunca salen del dispositivo.",
        )

        private val AR = PricingUiMessages(
            freeTrialMessage = "أول {days} أيام مجاناً",
            subscribeButton = "بدء الاشتراك المميز",
            cancelSubscriptionButton = "إلغاء الاشتراك",
            cancellationNote = "يمكنك إلغاء اشتراكك في أي وقت من Google Play.",
            noRefundNotice = "لا يتم استرداد المبلغ عند الإلغاء. تستمر الخدمة حتى نهاية فترة الفوترة الحالية.",
            monthlyPriceLabel = "{price}/شهر",
            regionalPricingNote = "نفس السعر في جميع أنحاء العالم.",
            trialCancelNote = "يمكنك الإلغاء في أي وقت خلال الفترة التجريبية. تستمر الخدمة حتى نهاية الفترة.",
            valueProposition = "المكالمات والرسائل والخصوصية — تطبيق واحد يقيّم التهديدات الثلاثة. بياناتك لا تغادر الجهاز أبداً.",
        )
    }
}
