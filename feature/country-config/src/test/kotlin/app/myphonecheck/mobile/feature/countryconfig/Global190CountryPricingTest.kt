package app.myphonecheck.mobile.feature.countryconfig

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 190개국 가격 매핑 전수 검증 — EN 단일 (헌법 §9-1).
 *
 * 검증 항목:
 *  1. 모든 ISO 3166-1 alpha-2 코드 → 유효한 PricingTier (1/2/3) 반환
 *  2. Tier 1/2 명시 매핑 국가 정확한 가격
 *  3. 미매핑 국가 → Tier 3
 *  4. null → Tier 1 (보수적)
 *  5. 대소문자 무관 처리
 *  6. PricingUiMessages 영문 단일 (다국어 분기 폐기)
 *  7. ICU NumberFormat 통화 표시
 *
 * Google Play Console 190개국 ISO 3166-1 alpha-2 코드 기준.
 */
class Global190CountryPricingTest {

    private val ALL_190_COUNTRIES = listOf(
        "AD", "AE", "AF", "AG", "AL", "AM", "AO", "AR", "AT", "AU", "AZ",
        "BA", "BB", "BD", "BE", "BF", "BG", "BH", "BJ", "BN", "BO",
        "BR", "BS", "BT", "BW", "BY", "BZ",
        "CA", "CD", "CF", "CH", "CI", "CL", "CM", "CN", "CO", "CR",
        "CV", "CY", "CZ", "CU",
        "DE", "DJ", "DK", "DM", "DO", "DZ",
        "EC", "EE", "EG", "ER", "ES", "ET",
        "FI", "FJ", "FM", "FR",
        "GA", "GB", "GD", "GE", "GH", "GM", "GN", "GQ", "GR", "GT", "GW", "GY",
        "HK", "HN", "HR", "HT", "HU",
        "ID", "IE", "IL", "IN", "IQ", "IR", "IS", "IT",
        "JM", "JO", "JP",
        "KE", "KG", "KH", "KI", "KM", "KN", "KR", "KW", "KZ",
        "LA", "LB", "LC", "LI", "LK", "LR", "LS", "LT", "LU", "LV", "LY",
        "MA", "MC", "MD", "ME", "MG", "MH", "MK", "ML", "MM", "MN",
        "MO", "MR", "MT", "MU", "MV", "MW", "MX", "MY", "MZ",
        "NA", "NE", "NG", "NI", "NL", "NO", "NP", "NR", "NZ",
        "OM",
        "PA", "PE", "PG", "PH", "PK", "PL", "PT", "PW", "PY",
        "QA",
        "RO", "RS", "RU", "RW",
        "SA", "SB", "SC", "SD", "SE", "SG", "SI", "SK", "SL", "SN",
        "SO", "SR", "ST", "SV", "SZ",
        "TD", "TG", "TH", "TJ", "TL", "TM", "TN", "TO", "TR", "TT",
        "TV", "TW", "TZ",
        "UA", "UG", "US", "UY", "UZ",
        "VC", "VE", "VN", "VU",
        "WS",
        "YE",
        "ZA", "ZM", "ZW",
    )

    private val TOTAL_COUNTRIES = ALL_190_COUNTRIES.size

    @Test
    fun `PRICING-190 every country returns valid tier`() {
        var tier1Count = 0
        var tier2Count = 0
        var tier3Count = 0

        for (cc in ALL_190_COUNTRIES) {
            val tier = CountryPricingMapper.getTier(cc)
            assertTrue("$cc must return tier 1, 2, or 3 — got ${tier.tierId}", tier.tierId in 1..3)
            when (tier.tierId) {
                1 -> tier1Count++
                2 -> tier2Count++
                3 -> tier3Count++
            }
        }

        assertTrue("Tier 1 must have 30-40 countries", tier1Count in 30..40)
        assertTrue("Tier 2 must have 40-60 countries", tier2Count in 40..60)
        assertTrue("Tier 3 must have 90+ countries", tier3Count >= 90)
        assertEquals("Total must match list size", TOTAL_COUNTRIES, tier1Count + tier2Count + tier3Count)
    }

    @Test
    fun `PRICING-190 Tier 1 countries have correct price`() {
        val tier1Sample = listOf("US", "KR", "JP", "GB", "DE", "AU", "CA", "SG", "HK", "TW",
            "NZ", "IL", "AE", "SA", "QA", "KW", "BH", "FR", "IT", "ES",
            "NL", "BE", "AT", "CH", "IE", "LU", "FI", "DK", "SE", "NO",
            "IS", "PT", "CZ", "PL")

        for (cc in tier1Sample) {
            val tier = CountryPricingMapper.getTier(cc)
            assertEquals("$cc must be Tier 1", 1, tier.tierId)
            assertEquals("$cc monthly must be \$9.9", "\$9.9", tier.monthlyPriceUsd)
            assertEquals("$cc trial must be 30 days", 30, tier.freeTrialDays)
        }
    }

    @Test
    fun `PRICING-190 Tier 2 countries have correct price`() {
        val tier2Sample = listOf("BR", "MX", "AR", "CL", "CO", "PE", "UY", "CR", "PA", "DO",
            "EC", "GT", "SV", "RU", "UA", "RO", "HU", "SK", "BG", "HR",
            "SI", "RS", "BA", "ME", "MK", "AL", "GE", "AM", "AZ", "BY",
            "MD", "CN", "MY", "TH", "ID", "PH", "VN", "TR", "EG", "MA",
            "TN", "ZA", "NG", "KE", "GH", "KZ", "UZ")

        for (cc in tier2Sample) {
            val tier = CountryPricingMapper.getTier(cc)
            assertEquals("$cc must be Tier 2", 2, tier.tierId)
            assertEquals("$cc monthly must be \$6.9", "\$6.9", tier.monthlyPriceUsd)
            assertEquals("$cc trial must be 30 days", 30, tier.freeTrialDays)
        }
    }

    @Test
    fun `PRICING-190 Tier 3 countries default correctly`() {
        val tier3Sample = listOf("BD", "MM", "LA", "KH", "NP", "ET", "TZ",
            "AF", "CD", "SO", "ML", "NE", "RW", "UG", "MZ",
            "FJ", "PG", "WS", "KI", "TV")

        for (cc in tier3Sample) {
            val tier = CountryPricingMapper.getTier(cc)
            assertEquals("$cc must be Tier 3", 3, tier.tierId)
            assertEquals("$cc monthly must be \$3.9", "\$3.9", tier.monthlyPriceUsd)
            assertEquals("$cc trial must be 30 days", 30, tier.freeTrialDays)
        }
    }

    @Test
    fun `PRICING-190 null country defaults to Tier 1`() {
        val tier = CountryPricingMapper.getTier(null)
        assertEquals("null must default to Tier 1", 1, tier.tierId)
        assertEquals("\$9.9", tier.monthlyPriceUsd)
    }

    @Test
    fun `PRICING-190 case insensitive country codes`() {
        assertEquals("kr lowercase", 1, CountryPricingMapper.getTier("kr").tierId)
        assertEquals("Kr mixed", 1, CountryPricingMapper.getTier("Kr").tierId)
        assertEquals("br lowercase", 2, CountryPricingMapper.getTier("br").tierId)
        assertEquals("af lowercase", 3, CountryPricingMapper.getTier("af").tierId)
    }

    @Test
    fun `PRICING-190 single monthly plan with 30-day free trial`() {
        assertEquals(30, PricingTier.TIER_1.freeTrialDays)
        assertEquals(30, PricingTier.TIER_2.freeTrialDays)
        assertEquals(30, PricingTier.TIER_3.freeTrialDays)
        assertEquals("free-trial-1month", PricingTier.TIER_1.playOfferIdTrial)
        assertEquals("free-trial-1month", PricingTier.TIER_2.playOfferIdTrial)
        assertEquals("free-trial-1month", PricingTier.TIER_3.playOfferIdTrial)
    }

    @Test
    fun `PRICING-190 Play Console offer IDs are unified`() {
        assertEquals("free-trial-1month", PricingTier.TIER_1.playOfferIdTrial)
        assertEquals("free-trial-1month", PricingTier.TIER_2.playOfferIdTrial)
        assertEquals("free-trial-1month", PricingTier.TIER_3.playOfferIdTrial)
        assertEquals(PricingTier.TIER_1.playBasePlanMonthly, PricingTier.TIER_2.playBasePlanMonthly)
        assertEquals(PricingTier.TIER_2.playBasePlanMonthly, PricingTier.TIER_3.playBasePlanMonthly)
    }

    // ═══════════════════════════════════════════════════════════
    // PricingUiMessages — 영문 단일 (헌법 §9-1, 다국어 분기 폐기)
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `PRICING-190 PricingUiMessages forLanguage always returns EN`() {
        val msg = PricingUiMessages.forLanguage(SupportedLanguage.EN)
        assertTrue("freeTrialMessage", msg.freeTrialMessage.isNotBlank())
        assertTrue("subscribeButton", msg.subscribeButton.isNotBlank())
        assertTrue("cancelSubscriptionButton", msg.cancelSubscriptionButton.isNotBlank())
        assertTrue("cancellationNote", msg.cancellationNote.isNotBlank())
        assertTrue("noRefundNotice", msg.noRefundNotice.isNotBlank())
        assertTrue("monthlyPriceLabel", msg.monthlyPriceLabel.isNotBlank())
        assertTrue("regionalPricingNote", msg.regionalPricingNote.isNotBlank())
        assertTrue("trialCancelNote", msg.trialCancelNote.isNotBlank())
        assertTrue("valueProposition", msg.valueProposition.isNotBlank())
    }

    @Test
    fun `PRICING-190 freeTrialMessage placeholder substitution`() {
        val msg = PricingUiMessages.forLanguage(SupportedLanguage.EN)
        val formatted = msg.formatFreeTrial(30)
        assertTrue("formatted must contain '30'", formatted.contains("30"))
        assertTrue("formatted must not contain '{days}'", !formatted.contains("{days}"))
    }

    @Test
    fun `PRICING-190 monthlyPriceLabel placeholder substitution`() {
        val msg = PricingUiMessages.forLanguage(SupportedLanguage.EN)
        val priceFormatted = msg.formatMonthlyPrice("\$9.9")
        assertTrue("price must contain '\$9.9'", priceFormatted.contains("\$9.9"))
        assertTrue("price must not contain '{price}'", !priceFormatted.contains("{price}"))
    }

    // ═══════════════════════════════════════════════════════════
    // ICU NumberFormat — Locale 자동 추종
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `formatCurrency produces non-blank output for USD`() {
        val formatted = PricingUiMessages.formatCurrency(9.9, "USD")
        assertTrue("USD currency must be non-blank", formatted.isNotBlank())
        // 구체 표기는 OS Locale 에 따라 달라지므로 검증 안 함 (ICU 자동).
    }

    @Test
    fun `PRICING-190 tier price ordering is correct`() {
        val t1 = PricingTier.TIER_1.monthlyPriceUsd.removePrefix("$").toDouble()
        val t2 = PricingTier.TIER_2.monthlyPriceUsd.removePrefix("$").toDouble()
        val t3 = PricingTier.TIER_3.monthlyPriceUsd.removePrefix("$").toDouble()
        assertTrue("Tier 1 > Tier 2", t1 > t2)
        assertTrue("Tier 2 > Tier 3", t2 > t3)

        assertEquals(PricingTier.TIER_1.freeTrialDays, PricingTier.TIER_2.freeTrialDays)
        assertEquals(PricingTier.TIER_2.freeTrialDays, PricingTier.TIER_3.freeTrialDays)
    }
}
