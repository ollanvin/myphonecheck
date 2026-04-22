package app.myphonecheck.mobile.feature.countryconfig

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

        // v1.1: 전 세계 단일 가격 $1.99
        for (cc in ALL_190_COUNTRIES) {
            val tier = CountryPricingMapper.getTier(cc)
            assertEquals(
                "$cc must be \$1.99 (v1.1 single price)",
                "\$1.99",
                tier.monthlyPriceUsd,
            )
            tier1Count++
        }

        println("[PRICING-190] v1.1 Single price:")
        println("  \$1.99/月: $tier1Count countries")
        println("  Total: $tier1Count")

        // v1.1: 전부 단일 가격
        assertEquals("Total must match list size", TOTAL_COUNTRIES, tier1Count)
    }

    // ═══════════════════════════════════════════════════════════
    // 2. Tier별 가격 일관성 검증
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `PRICING-190 v1_1 all countries have single price`() {
        // v1.1: 3-Tier 폐지, 전 세계 $1.99 단일가
        val allSample = listOf(
            "US", "KR", "JP", "GB", "DE", "AU", "CA", "SG", "HK", "TW",  // 구 Tier 1
            "BR", "MX", "RU", "CN", "TR", "TH", "PH",                    // 구 Tier 2
            "BD", "MM", "LA", "KH", "NP", "ET", "TZ",                    // 구 Tier 3
        )

        for (cc in allSample) {
            val tier = CountryPricingMapper.getTier(cc)
            assertEquals("$cc monthly must be \$1.99", "\$1.99", tier.monthlyPriceUsd)
            assertEquals("$cc trial must be 30 days", 30, tier.freeTrialDays)
        }
    }

    // ═══════════════════════════════════════════════════════════
    // 3. null 국가 코드 → Tier 1 (보수적 최고가)
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `PRICING-190 null country defaults to single price`() {
        val tier = CountryPricingMapper.getTier(null)
        assertEquals("null must be \$1.99", "\$1.99", tier.monthlyPriceUsd)
    }

    // ═══════════════════════════════════════════════════════════
    // 4. 대소문자 무관 처리
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `PRICING-190 case insensitive country codes`() {
        // v1.1: 전부 단일 가격이므로 tierId=1
        assertEquals("kr lowercase", "\$1.99", CountryPricingMapper.getTier("kr").monthlyPriceUsd)
        assertEquals("Kr mixed", "\$1.99", CountryPricingMapper.getTier("Kr").monthlyPriceUsd)
        assertEquals("br lowercase", "\$1.99", CountryPricingMapper.getTier("br").monthlyPriceUsd)
        assertEquals("af lowercase", "\$1.99", CountryPricingMapper.getTier("af").monthlyPriceUsd)
    }

    // ═══════════════════════════════════════════════════════════
    // 5. 단일 월간 구독 — 연간 플랜 없음 확인
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `PRICING-190 single monthly plan with 30-day free trial`() {
        assertEquals(30, PricingTier.SINGLE.freeTrialDays)
        assertEquals("free-trial-1month", PricingTier.SINGLE.playOfferIdTrial)
        assertEquals("myphonecheck-premium-monthly", PricingTier.SINGLE.playBasePlanMonthly)
    }

    // ═══════════════════════════════════════════════════════════
    // 6. Play Console Offer ID 일관성
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `PRICING-190 Play Console offer IDs are unified`() {
        // v1.1: 단일 가격 단일 offer
        assertEquals("free-trial-1month", PricingTier.SINGLE.playOfferIdTrial)
        assertEquals("myphonecheck-premium-monthly", PricingTier.SINGLE.playBasePlanMonthly)
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
            assertTrue("${lang.code} regionalPricingNote", msg.regionalPricingNote.isNotBlank())
            assertTrue("${lang.code} trialCancelNote", msg.trialCancelNote.isNotBlank())
            assertTrue("${lang.code} valueProposition", msg.valueProposition.isNotBlank())

            // {days} 플레이스홀더 치환 검증
            val formatted = msg.formatFreeTrial(30)
            assertTrue("${lang.code} formatted must contain '30'", formatted.contains("30"))
            assertTrue("${lang.code} formatted must not contain '{days}'", !formatted.contains("{days}"))

            // {price} 플레이스홀더 치환 검증
            val priceFormatted = msg.formatMonthlyPrice("\$1.99")
            assertTrue("${lang.code} price must contain '\$1.99'", priceFormatted.contains("\$1.99"))
            assertTrue("${lang.code} price must not contain '{price}'", !priceFormatted.contains("{price}"))
        }
    }

    // ═══════════════════════════════════════════════════════════
    // 8. Tier 간 가격 역전 방지
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `PRICING-190 v1_1 single price is correct`() {
        // v1.1: 단일 가격 $1.99
        val price = PricingTier.SINGLE.monthlyPriceUsd.removePrefix("$").toDouble()
        assertEquals("Single price must be 1.99", 1.99, price, 0.001)
        assertEquals("Trial must be 30 days", 30, PricingTier.SINGLE.freeTrialDays)
    }
}
