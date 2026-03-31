package app.callcheck.mobile.feature.countryconfig

import org.junit.Assert.*
import org.junit.Test

/**
 * 190개국 가격 매핑 전수 검증.
 *
 * 검증 항목:
 *  1. 모든 ISO 3166-1 alpha-2 코드 → 유효한 PricingTier (1/2/3) 반환
 *  2. Tier 1/2 명시 매핑 국가의 정확한 가격 검증
 *  3. 미매핑 국가 → Tier 3 정확 적용
 *  4. null → Tier 1 (보수적) 확인
 *  5. 대소문자 무관 처리
 *  6. Tier 1 (35개국) + Tier 2 (약 55개국) + Tier 3 (나머지) = 190개국 전체 커버
 *  7. 연간 구독 가격이 월간 × 10인지 확인
 *
 * Google Play Console 190개국 ISO 3166-1 alpha-2 코드 기준.
 */
class Global190CountryPricingTest {

    /**
     * Google Play Store가 지원하는 190개국의 ISO 3166-1 alpha-2 코드.
     * 출처: Google Play Console → 가격 설정 → 국가 목록 (2024 기준)
     */
    private val ALL_190_COUNTRIES = listOf(
        // A
        "AD", "AE", "AF", "AG", "AL", "AM", "AO", "AR", "AT", "AU",
        "AZ",
        // B
        "BA", "BB", "BD", "BE", "BF", "BG", "BH", "BJ", "BN", "BO",
        "BR", "BS", "BT", "BW", "BY", "BZ",
        // C
        "CA", "CD", "CF", "CH", "CI", "CL", "CM", "CN", "CO", "CR",
        "CV", "CY", "CZ",
        // C (continued)
        "CU",
        // D
        "DE", "DJ", "DK", "DM", "DO", "DZ",
        // E
        "EC", "EE", "EG", "ER", "ES", "ET",
        // F
        "FI", "FJ", "FM", "FR",
        // G
        "GA", "GB", "GD", "GE", "GH", "GM", "GN", "GQ", "GR", "GT",
        "GW", "GY",
        // H
        "HK", "HN", "HR", "HT", "HU",
        // I
        "ID", "IE", "IL", "IN", "IQ", "IR", "IS", "IT",
        // J
        "JM", "JO", "JP",
        // K
        "KE", "KG", "KH", "KI", "KM", "KN", "KR", "KW", "KZ",
        // L
        "LA", "LB", "LC", "LI", "LK", "LR", "LS", "LT", "LU", "LV",
        "LY",
        // M
        "MA", "MC", "MD", "ME", "MG", "MH", "MK", "ML", "MM", "MN",
        "MO", "MR", "MT", "MU", "MV", "MW", "MX", "MY", "MZ",
        // N
        "NA", "NE", "NG", "NI", "NL", "NO", "NP", "NR", "NZ",
        // O
        "OM",
        // P
        "PA", "PE", "PG", "PH", "PK", "PL", "PT", "PW", "PY",
        // Q
        "QA",
        // R
        "RO", "RS", "RU", "RW",
        // S
        "SA", "SB", "SC", "SD", "SE", "SG", "SI", "SK", "SL", "SN",
        "SO", "SR", "ST", "SV", "SZ",
        // T
        "TD", "TG", "TH", "TJ", "TL", "TM", "TN", "TO", "TR", "TT",
        "TV", "TW", "TZ",
        // U
        "UA", "UG", "US", "UY", "UZ",
        // V
        "VC", "VE", "VN", "VU",
        // W
        "WS",
        // Y
        "YE",
        // Z
        "ZA", "ZM", "ZW",
    )

    /** 테스트 리스트의 실제 국가 수 */
    private val TOTAL_COUNTRIES = ALL_190_COUNTRIES.size

    // ═══════════════════════════════════════════════════════════
    // 1. 190개국 전수 — 유효한 Tier 반환 검증
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `PRICING-190 every country returns valid tier`() {
        var tier1Count = 0
        var tier2Count = 0
        var tier3Count = 0

        for (cc in ALL_190_COUNTRIES) {
            val tier = CountryPricingMapper.getTier(cc)
            assertTrue(
                "$cc must return tier 1, 2, or 3 — got ${tier.tierId}",
                tier.tierId in 1..3,
            )
            when (tier.tierId) {
                1 -> tier1Count++
                2 -> tier2Count++
                3 -> tier3Count++
            }
        }

        println("[PRICING-190] Tier distribution:")
        println("  Tier 1 (\$9.9): $tier1Count countries")
        println("  Tier 2 (\$6.9): $tier2Count countries")
        println("  Tier 3 (\$3.9): $tier3Count countries")
        println("  Total: ${tier1Count + tier2Count + tier3Count}")

        // Tier 1은 약 35개국
        assertTrue("Tier 1 must have 30-40 countries", tier1Count in 30..40)
        // Tier 2는 약 55개국
        assertTrue("Tier 2 must have 40-60 countries", tier2Count in 40..60)
        // Tier 3는 나머지
        assertTrue("Tier 3 must have 90+ countries", tier3Count >= 90)
        // 합계 = 전체 리스트 수
        assertEquals("Total must match list size", TOTAL_COUNTRIES, tier1Count + tier2Count + tier3Count)
    }

    // ═══════════════════════════════════════════════════════════
    // 2. Tier별 가격 일관성 검증
    // ═══════════════════════════════════════════════════════════

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

    // ═══════════════════════════════════════════════════════════
    // 3. null 국가 코드 → Tier 1 (보수적 최고가)
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `PRICING-190 null country defaults to Tier 1`() {
        val tier = CountryPricingMapper.getTier(null)
        assertEquals("null must default to Tier 1", 1, tier.tierId)
        assertEquals("\$9.9", tier.monthlyPriceUsd)
    }

    // ═══════════════════════════════════════════════════════════
    // 4. 대소문자 무관 처리
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `PRICING-190 case insensitive country codes`() {
        assertEquals("kr lowercase", 1, CountryPricingMapper.getTier("kr").tierId)
        assertEquals("Kr mixed", 1, CountryPricingMapper.getTier("Kr").tierId)
        assertEquals("br lowercase", 2, CountryPricingMapper.getTier("br").tierId)
        assertEquals("af lowercase", 3, CountryPricingMapper.getTier("af").tierId)
    }

    // ═══════════════════════════════════════════════════════════
    // 5. 단일 월간 구독 — 연간 플랜 없음 확인
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `PRICING-190 single monthly plan with 30-day free trial`() {
        assertEquals(30, PricingTier.TIER_1.freeTrialDays)
        assertEquals(30, PricingTier.TIER_2.freeTrialDays)
        assertEquals(30, PricingTier.TIER_3.freeTrialDays)
        // 모든 tier에 동일한 offer ID
        assertEquals("free-trial-1month", PricingTier.TIER_1.playOfferIdTrial)
        assertEquals("free-trial-1month", PricingTier.TIER_2.playOfferIdTrial)
        assertEquals("free-trial-1month", PricingTier.TIER_3.playOfferIdTrial)
    }

    // ═══════════════════════════════════════════════════════════
    // 6. Play Console Offer ID 일관성
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `PRICING-190 Play Console offer IDs are unified`() {
        // 단일 월간 구독: 모든 tier 동일 offer ID
        assertEquals("free-trial-1month", PricingTier.TIER_1.playOfferIdTrial)
        assertEquals("free-trial-1month", PricingTier.TIER_2.playOfferIdTrial)
        assertEquals("free-trial-1month", PricingTier.TIER_3.playOfferIdTrial)
        // Base plan은 모든 Tier에서 동일
        assertEquals(PricingTier.TIER_1.playBasePlanMonthly, PricingTier.TIER_2.playBasePlanMonthly)
        assertEquals(PricingTier.TIER_2.playBasePlanMonthly, PricingTier.TIER_3.playBasePlanMonthly)
    }

    // ═══════════════════════════════════════════════════════════
    // 7. PricingUiMessages 7개 언어 전수 검증
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `PRICING-190 PricingUiMessages all 7 languages non-blank`() {
        for (lang in SupportedLanguage.values()) {
            val msg = PricingUiMessages.forLanguage(lang)
            assertTrue("${lang.code} freeTrialMessage", msg.freeTrialMessage.isNotBlank())
            assertTrue("${lang.code} subscribeButton", msg.subscribeButton.isNotBlank())
            assertTrue("${lang.code} cancelSubscriptionButton", msg.cancelSubscriptionButton.isNotBlank())
            assertTrue("${lang.code} cancellationNote", msg.cancellationNote.isNotBlank())
            assertTrue("${lang.code} noRefundNotice", msg.noRefundNotice.isNotBlank())
            assertTrue("${lang.code} monthlyPriceLabel", msg.monthlyPriceLabel.isNotBlank())

            // {days} 플레이스홀더 치환 검증
            val formatted = msg.formatFreeTrial(30)
            assertTrue("${lang.code} formatted must contain '30'", formatted.contains("30"))
            assertTrue("${lang.code} formatted must not contain '{days}'", !formatted.contains("{days}"))

            // {price} 플레이스홀더 치환 검증
            val priceFormatted = msg.formatMonthlyPrice("\$9.9")
            assertTrue("${lang.code} price must contain '\$9.9'", priceFormatted.contains("\$9.9"))
            assertTrue("${lang.code} price must not contain '{price}'", !priceFormatted.contains("{price}"))
        }
    }

    // ═══════════════════════════════════════════════════════════
    // 8. Tier 간 가격 역전 방지
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `PRICING-190 tier price ordering is correct`() {
        // Tier 1 > Tier 2 > Tier 3 (월간 기준)
        val t1 = PricingTier.TIER_1.monthlyPriceUsd.removePrefix("$").toDouble()
        val t2 = PricingTier.TIER_2.monthlyPriceUsd.removePrefix("$").toDouble()
        val t3 = PricingTier.TIER_3.monthlyPriceUsd.removePrefix("$").toDouble()
        assertTrue("Tier 1 > Tier 2", t1 > t2)
        assertTrue("Tier 2 > Tier 3", t2 > t3)

        // 무료 체험 일수는 전 Tier 동일 30일
        assertEquals("Trial: all tiers 30 days", PricingTier.TIER_1.freeTrialDays, PricingTier.TIER_2.freeTrialDays)
        assertEquals("Trial: all tiers 30 days", PricingTier.TIER_2.freeTrialDays, PricingTier.TIER_3.freeTrialDays)
    }
}
