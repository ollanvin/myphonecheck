package app.callcheck.mobile.feature.countryconfig

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * ═══════════════════════════════════════════════════════════════
 * 런타임 연결 증거 — feature:country-config 모듈
 * ═══════════════════════════════════════════════════════════════
 *
 * 자비스 검증 요구사항 중 feature:country-config 모듈에서 검증 가능한 항목.
 *
 * ┌──────────────────────────────────────────────────────────────┐
 * │ 증거 2: Localizer 런타임 적용 (다국어 출력)                   │
 * │ 증거 5: Privacy Trust UX 삽입 (7언어 × 3화면)                │
 * │ 증거 6: Pricing Policy 실전 자산 (3-tier + Play Console표)   │
 * └──────────────────────────────────────────────────────────────┘
 *
 * 증거 1,3,4,7은 data:search 모듈 테스트에 위치.
 */
class RuntimeConnectionEvidenceCountryConfigTest {

    private lateinit var localizer: SignalSummaryLocalizer

    @Before
    fun setup() {
        localizer = SignalSummaryLocalizer()
    }

    // ═══════════════════════════════════════════════════════════
    // 증거 2: Localizer 런타임 적용 — 같은 시그널 3개 언어 출력
    // ═══════════════════════════════════════════════════════════
    //
    // 코드 경로:
    // CallCheckScreeningService.assessThenAllow() (line 203):
    //   val currentLanguage = languageContextProvider.resolveLanguage()
    // → callerIdOverlayManager.showOverlay(..., language=currentLanguage, localizer=signalSummaryLocalizer) (line 205-211)
    // → buildOverlayView() → localizer.localizeCategory(result.category.name, language) (line 195)
    // → uiText.oneWordVerdict(result.riskLevel) — 1초 인지 한 단어 (line 181)

    @Test
    fun `EVIDENCE-2 Localizer produces different output per language for same signal`() {
        println("\n═══ 증거 2: Localizer 런타임 적용 ═══")

        val intensityKey = SignalSummaryLocalizer.KEY_DANGER
        val categoryKey = "SCAM_RISK_HIGH"

        val languages = listOf(SupportedLanguage.EN, SupportedLanguage.KO, SupportedLanguage.JA)
        for (lang in languages) {
            val intensity = localizer.localizeIntensity(intensityKey, lang)
            val category = localizer.localizeCategory(categoryKey, lang)
            val combined = localizer.localize(intensityKey, categoryKey, lang)

            println("[${lang.code}] intensity='$intensity', category='$category'")
            println("[${lang.code}] combined='$combined'")
        }

        // EN과 KO의 출력이 달라야 함
        val enResult = localizer.localize(intensityKey, categoryKey, SupportedLanguage.EN)
        val koResult = localizer.localize(intensityKey, categoryKey, SupportedLanguage.KO)
        val jaResult = localizer.localize(intensityKey, categoryKey, SupportedLanguage.JA)

        assertNotEquals("EN and KO must produce different output", enResult, koResult)
        assertNotEquals("EN and JA must produce different output", enResult, jaResult)
        assertNotEquals("KO and JA must produce different output", koResult, jaResult)
    }

    @Test
    fun `EVIDENCE-2 Entity substitution works across languages`() {
        val entityName = "Samsung Hospital"
        val languages = listOf(SupportedLanguage.EN, SupportedLanguage.KO, SupportedLanguage.JA)

        println("\n[Entity substitution test]")
        for (lang in languages) {
            val result = localizer.localizeCategory("INSTITUTION_LIKELY", lang, entityName)
            println("[${lang.code}] INSTITUTION with entity='$entityName' → '$result'")
            assertTrue("Must contain entity name", result.contains(entityName))
        }
    }

    // ═══════════════════════════════════════════════════════════
    // 증거 5: Privacy Trust UX 실제 삽입
    // ═══════════════════════════════════════════════════════════
    //
    // PrivacyTrustMessages.kt — 7개 언어 × 3개 화면 메시지 존재 증명

    @Test
    fun `EVIDENCE-5 PrivacyTrustMessages has all 3 screens in all 7 languages`() {
        println("\n═══ 증거 5: Privacy Trust UX ═══")

        val languages = SupportedLanguage.values()
        assertEquals("Must support 7 languages", 7, languages.size)

        for (lang in languages) {
            val msg = PrivacyTrustMessages.forLanguage(lang)

            // 온보딩
            assertTrue("${lang.code} onboardingTagline must not be blank", msg.onboardingTagline.isNotBlank())
            assertTrue("${lang.code} onboardingPrivacyCore must not be blank", msg.onboardingPrivacyCore.isNotBlank())
            assertTrue("${lang.code} onboardingPrivacyDetail must not be blank", msg.onboardingPrivacyDetail.isNotBlank())
            assertTrue("${lang.code} onboardingNoServerPledge must not be blank", msg.onboardingNoServerPledge.isNotBlank())

            // 설정
            assertTrue("${lang.code} settingsPrivacyTitle must not be blank", msg.settingsPrivacyTitle.isNotBlank())
            assertTrue("${lang.code} settingsPrivacyDescription must not be blank", msg.settingsPrivacyDescription.isNotBlank())
            assertTrue("${lang.code} settingsDataHandling must not be blank", msg.settingsDataHandling.isNotBlank())

            // 결제 직전
            assertTrue("${lang.code} purchasePrivacyGuarantee must not be blank", msg.purchasePrivacyGuarantee.isNotBlank())
            assertTrue("${lang.code} purchaseValueProposition must not be blank", msg.purchaseValueProposition.isNotBlank())

            println("[${lang.code}] onboarding='${msg.onboardingTagline}'")
            println("[${lang.code}] privacy='${msg.onboardingPrivacyCore}'")
            println("[${lang.code}] settings='${msg.settingsPrivacyTitle}'")
            println("[${lang.code}] purchase='${msg.purchaseValueProposition}'")
        }

        // EN 핵심 메시지 검증 — 신뢰 확정형 헤드라인
        val en = PrivacyTrustMessages.forLanguage(SupportedLanguage.EN)
        assertTrue("EN must mention 'never leaves'", en.onboardingPrivacyCore.contains("never leaves"))
        assertTrue("EN must mention 'never sends'", en.onboardingPrivacyDetail.contains("never sends"))

        // KO 핵심 메시지 검증 — 신뢰 확정형 헤드라인
        val ko = PrivacyTrustMessages.forLanguage(SupportedLanguage.KO)
        assertTrue("KO must mention '벗어나지 않습니다'", ko.onboardingPrivacyCore.contains("벗어나지 않습니다"))
        assertTrue("KO must mention '전송하지 않습니다'", ko.onboardingPrivacyDetail.contains("전송하지 않습니다"))

        println("\n  별도 재검증 필요: 온보딩/설정/결제 화면에 실제 삽입된 상태 에뮬레이터 캡처")
    }

    // ═══════════════════════════════════════════════════════════
    // 증거 6: Pricing Policy 실전 자산
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `EVIDENCE-6 Pricing 3-tier country mapping complete`() {
        println("\n═══ 증거 6: Pricing Policy ═══")

        // Tier 1 ($9.99) 주요 국가
        val tier1Countries = listOf("US", "KR", "JP", "GB", "DE", "AU", "CA", "FR", "SG")
        for (cc in tier1Countries) {
            val tier = CountryPricingMapper.getTier(cc)
            println("[Tier1] $cc → ${tier.monthlyPriceUsd}/月 (trial=${tier.freeTrialDays}d)")
            assertEquals("$cc must be Tier 1", 1, tier.tierId)
            assertEquals("$cc must be \$9.99", "\$9.99", tier.monthlyPriceUsd)
            assertEquals("$cc must have 7-day trial", 7, tier.freeTrialDays)
        }

        // Tier 2 ($6.99) 주요 국가
        val tier2Countries = listOf("BR", "MX", "RU", "CN", "TR", "TH", "PH")
        for (cc in tier2Countries) {
            val tier = CountryPricingMapper.getTier(cc)
            println("[Tier2] $cc → ${tier.monthlyPriceUsd}/月 (trial=${tier.freeTrialDays}d)")
            assertEquals("$cc must be Tier 2", 2, tier.tierId)
            assertEquals("$cc must be \$6.99", "\$6.99", tier.monthlyPriceUsd)
            assertEquals("$cc must have 5-day trial", 5, tier.freeTrialDays)
        }

        // Tier 3 ($3.99) — 미매핑 국가
        val tier3Countries = listOf("BD", "MM", "LA", "KH", "NP", "ET", "TZ")
        for (cc in tier3Countries) {
            val tier = CountryPricingMapper.getTier(cc)
            println("[Tier3] $cc → ${tier.monthlyPriceUsd}/月 (trial=${tier.freeTrialDays}d)")
            assertEquals("$cc must be Tier 3", 3, tier.tierId)
            assertEquals("$cc must be \$3.99", "\$3.99", tier.monthlyPriceUsd)
            assertEquals("$cc must have 3-day trial", 3, tier.freeTrialDays)
        }

        // null → Tier 1 (보수적)
        val nullTier = CountryPricingMapper.getTier(null)
        println("[null] → ${nullTier.monthlyPriceUsd}/月 (탐지 실패 시 최고가)")
        assertEquals("null must default to Tier 1", 1, nullTier.tierId)

        // Play Console 입력용 표
        println("\n[Play Console 입력용 표]")
        println("┌──────────┬─────────┬──────────┬──────────┬──────────────────────────────┐")
        println("│ Tier     │ Monthly │ Yearly   │ Trial    │ Play Offer ID                │")
        println("├──────────┼─────────┼──────────┼──────────┼──────────────────────────────┤")
        println("│ Tier 1   │ \$9.9    │ 30 days  │ free-trial-1month            │")
        println("│ Tier 2   │ \$6.9    │ 30 days  │ free-trial-1month            │")
        println("│ Tier 3   │ \$3.9    │ 30 days  │ free-trial-1month            │")
        println("└──────────┴─────────┴──────────┴──────────────────────────────┘")

        // 앱 내 가격 설명 문구 (7개 언어)
        println("\n[앱 내 가격 설명 문구]")
        for (lang in SupportedLanguage.values()) {
            val msg = PricingUiMessages.forLanguage(lang)
            val trialMsg = msg.formatFreeTrial(30)
            println("[${lang.code}] trial='$trialMsg', subscribe='${msg.subscribeButton}'")
            println("[${lang.code}] cancel='${msg.cancelSubscriptionButton}', refund='${msg.noRefundNotice}'")
            assertTrue("${lang.code} trial message must contain 30", trialMsg.contains("30"))
        }

        // 월간 단일 구독 가격 확인
        assertEquals("Tier 1 monthly", "\$9.9", PricingTier.TIER_1.monthlyPriceUsd)
        assertEquals("Tier 2 monthly", "\$6.9", PricingTier.TIER_2.monthlyPriceUsd)
        assertEquals("Tier 3 monthly", "\$3.9", PricingTier.TIER_3.monthlyPriceUsd)
    }
}
