package app.myphonecheck.mobile.feature.countryconfig

import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * 런타임 연결 증거 — feature:country-config 모듈 (영문 단일 EN, 헌법 §9-1).
 *
 * 다국어 검증 / Locale switching / R.string lookup 영구 폐기.
 *
 * ┌──────────────────────────────────────────────────────────────┐
 * │ 증거 2: Localizer 런타임 적용 (영문 단일 출력)                │
 * │ 증거 5: Privacy Trust UX 삽입 (영문 3 화면)                  │
 * │ 증거 6: Pricing Policy 실전 자산 (3-tier + Play Console표)   │
 * └──────────────────────────────────────────────────────────────┘
 *
 * Robolectric 은 PrivacyTrustMessages 가 Context 를 받기 때문에 유지한다
 * (실제 Locale switching 은 수행하지 않음).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class RuntimeConnectionEvidenceCountryConfigTest {

    private lateinit var localizer: SignalSummaryLocalizer

    @Before
    fun setup() {
        localizer = SignalSummaryLocalizer()
    }

    // ═══════════════════════════════════════════════════════════
    // 증거 2: Localizer 런타임 적용 — 영문 단일 출력
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `EVIDENCE-2 Localizer produces English output for SCAM signal`() {
        val intensityKey = SignalSummaryLocalizer.KEY_DANGER
        val categoryKey = "SCAM_RISK_HIGH"

        val intensity = localizer.localizeIntensity(intensityKey)
        val category = localizer.localizeCategory(categoryKey)
        val combined = localizer.localize(intensityKey, categoryKey)

        assertEquals("High Risk", intensity)
        assertEquals("Scam/Phishing Risk", category)
        assertEquals("Scam/Phishing Risk — High Risk", combined)
    }

    @Test
    fun `EVIDENCE-2 Entity substitution preserves entity name`() {
        val entityName = "Samsung Hospital"
        val result = localizer.localizeCategory("INSTITUTION_LIKELY", entityName)
        assertTrue("Must contain entity name", result.contains(entityName))
        assertEquals("Samsung Hospital Institution Call", result)
    }

    @Test
    fun `EVIDENCE-2 All 7 intensity keys produce English output`() {
        val keys = listOf("SAFE", "REFERENCE", "CAUTION_LIGHT", "CAUTION", "DANGER", "REJECT", "VERIFY")
        for (key in keys) {
            val result = localizer.localizeIntensity(key)
            assertTrue("$key result must be non-blank", result.isNotBlank())
            assertTrue("$key must produce localized output", result != key)
        }
    }

    // ═══════════════════════════════════════════════════════════
    // 증거 5: Privacy Trust UX — 영문 단일 3 화면
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `EVIDENCE-5 PrivacyTrustMessages all 3 screens have English content`() {
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        val msg = PrivacyTrustMessages(ctx)

        // 온보딩
        assertTrue("onboardingTagline must not be blank", msg.onboardingTagline.isNotBlank())
        assertTrue("onboardingPrivacyCore must not be blank", msg.onboardingPrivacyCore.isNotBlank())
        assertTrue("onboardingPrivacyDetail must not be blank", msg.onboardingPrivacyDetail.isNotBlank())
        assertTrue("onboardingNoServerPledge must not be blank", msg.onboardingNoServerPledge.isNotBlank())

        // 설정
        assertTrue("settingsPrivacyTitle must not be blank", msg.settingsPrivacyTitle.isNotBlank())
        assertTrue("settingsPrivacyDescription must not be blank", msg.settingsPrivacyDescription.isNotBlank())
        assertTrue("settingsDataHandling must not be blank", msg.settingsDataHandling.isNotBlank())

        // 결제 직전
        assertTrue("purchasePrivacyGuarantee must not be blank", msg.purchasePrivacyGuarantee.isNotBlank())
        assertTrue("purchaseValueProposition must not be blank", msg.purchaseValueProposition.isNotBlank())
    }

    @Test
    fun `EVIDENCE-5 PrivacyTrustMessages core privacy promises are present`() {
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        val msg = PrivacyTrustMessages(ctx)
        assertTrue("must mention 'never leaves'", msg.onboardingPrivacyCore.contains("never leaves"))
        assertTrue("must mention 'never sends'", msg.onboardingPrivacyDetail.contains("never sends"))
    }

    // ═══════════════════════════════════════════════════════════
    // 증거 6: Pricing Policy 실전 자산
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `EVIDENCE-6 Pricing 3-tier country mapping complete`() {
        val tier1Countries = listOf("US", "KR", "JP", "GB", "DE", "AU", "CA", "FR", "SG")
        for (cc in tier1Countries) {
            val tier = CountryPricingMapper.getTier(cc)
            assertEquals("$cc must be Tier 1", 1, tier.tierId)
            assertEquals("$cc must be \$9.9", "\$9.9", tier.monthlyPriceUsd)
            assertEquals("$cc must have 30-day trial", 30, tier.freeTrialDays)
        }

        val tier2Countries = listOf("BR", "MX", "RU", "CN", "TR", "TH", "PH")
        for (cc in tier2Countries) {
            val tier = CountryPricingMapper.getTier(cc)
            assertEquals("$cc must be Tier 2", 2, tier.tierId)
            assertEquals("$cc must be \$6.9", "\$6.9", tier.monthlyPriceUsd)
            assertEquals("$cc must have 30-day trial", 30, tier.freeTrialDays)
        }

        val tier3Countries = listOf("BD", "MM", "LA", "KH", "NP", "ET", "TZ")
        for (cc in tier3Countries) {
            val tier = CountryPricingMapper.getTier(cc)
            assertEquals("$cc must be Tier 3", 3, tier.tierId)
            assertEquals("$cc must be \$3.9", "\$3.9", tier.monthlyPriceUsd)
            assertEquals("$cc must have 30-day trial", 30, tier.freeTrialDays)
        }

        val nullTier = CountryPricingMapper.getTier(null)
        assertEquals("null must default to Tier 1", 1, nullTier.tierId)

        // 영문 단일 PricingUiMessages
        val msg = PricingUiMessages.forLanguage(SupportedLanguage.EN)
        val trialMsg = msg.formatFreeTrial(30)
        assertTrue("trial message must contain 30", trialMsg.contains("30"))
        assertTrue("subscribeButton must be non-blank", msg.subscribeButton.isNotBlank())

        assertEquals("Tier 1 monthly", "\$9.9", PricingTier.TIER_1.monthlyPriceUsd)
        assertEquals("Tier 2 monthly", "\$6.9", PricingTier.TIER_2.monthlyPriceUsd)
        assertEquals("Tier 3 monthly", "\$3.9", PricingTier.TIER_3.monthlyPriceUsd)
    }
}
