package app.myphonecheck.mobile.core.util

import app.myphonecheck.mobile.core.model.NumberSourceContext
import org.junit.Assert.*
import org.junit.Test

/**
 * 190개국 전화번호 처리 전수 검증.
 *
 * 검증 항목:
 *  1. raw → canonical (E.164 정규화)
 *  2. searchVariants 최소 2개 이상 생성
 *  3. rawNumber 원본 보존
 *  4. 파싱 실패 시 digits-only fallback
 *  5. 특수 번호 (짧은 번호, 콜센터 등) 처리
 *
 * 국가 샘플: 6대륙 주요 40개국 + 경계값 케이스
 */
class Global190CountryPhoneNumberTest {

    private val builder = PhoneNumberContextBuilder()

    // ═══════════════════════════════════════════════════════════
    // 대륙별 주요 국가 전화번호 검증
    // ═══════════════════════════════════════════════════════════

    /**
     * 아시아 11개국
     */
    @Test
    fun `GLOBAL-PHONE Asia KR mobile`() {
        val ctx = builder.build("010-1234-5678", "KR", NumberSourceContext.INCOMING_CALL)
        assertTrue("KR must parse", ctx.isParseable)
        assertEquals("+821012345678", ctx.deviceCanonicalNumber)
        assertTrue("rawNumber preserved", ctx.rawNumber == "010-1234-5678")
        assertTrue("variants >= 2", ctx.searchVariants.size >= 2)
        assertTrue("variants contain E.164", ctx.searchVariants.contains("+821012345678"))
    }

    @Test
    fun `GLOBAL-PHONE Asia JP landline`() {
        val ctx = builder.build("03-1234-5678", "JP", NumberSourceContext.INCOMING_CALL)
        assertTrue("JP must parse", ctx.isParseable)
        assertEquals("+81312345678", ctx.deviceCanonicalNumber)
        assertTrue("variants >= 2", ctx.searchVariants.size >= 2)
    }

    @Test
    fun `GLOBAL-PHONE Asia JP toll-free`() {
        val ctx = builder.build("0120-444-113", "JP", NumberSourceContext.INCOMING_CALL)
        assertTrue("JP toll-free must parse", ctx.isParseable)
        assertTrue("E.164 starts with +81", ctx.deviceCanonicalNumber.startsWith("+81"))
    }

    @Test
    fun `GLOBAL-PHONE Asia CN mobile`() {
        val ctx = builder.build("138-1234-5678", "CN", NumberSourceContext.INCOMING_CALL)
        assertTrue("CN must parse", ctx.isParseable)
        assertEquals("+8613812345678", ctx.deviceCanonicalNumber)
    }

    @Test
    fun `GLOBAL-PHONE Asia IN mobile`() {
        val ctx = builder.build("+91 98765 43210", "IN", NumberSourceContext.INCOMING_CALL)
        assertTrue("IN must parse", ctx.isParseable)
        assertEquals("+919876543210", ctx.deviceCanonicalNumber)
    }

    @Test
    fun `GLOBAL-PHONE Asia SG mobile`() {
        val ctx = builder.build("9123 4567", "SG", NumberSourceContext.INCOMING_CALL)
        assertTrue("SG must parse", ctx.isParseable)
        assertEquals("+6591234567", ctx.deviceCanonicalNumber)
    }

    @Test
    fun `GLOBAL-PHONE Asia TH mobile`() {
        val ctx = builder.build("081-234-5678", "TH", NumberSourceContext.INCOMING_CALL)
        assertTrue("TH must parse", ctx.isParseable)
        assertTrue("TH E.164 starts with +66", ctx.deviceCanonicalNumber.startsWith("+66"))
    }

    @Test
    fun `GLOBAL-PHONE Asia PH mobile`() {
        val ctx = builder.build("0917-123-4567", "PH", NumberSourceContext.INCOMING_CALL)
        assertTrue("PH must parse", ctx.isParseable)
        assertTrue("PH E.164 starts with +63", ctx.deviceCanonicalNumber.startsWith("+63"))
    }

    @Test
    fun `GLOBAL-PHONE Asia VN mobile`() {
        val ctx = builder.build("091-234-56-78", "VN", NumberSourceContext.INCOMING_CALL)
        assertTrue("VN must parse", ctx.isParseable)
        assertTrue("VN E.164 starts with +84", ctx.deviceCanonicalNumber.startsWith("+84"))
    }

    @Test
    fun `GLOBAL-PHONE Asia MY mobile`() {
        val ctx = builder.build("012-345 6789", "MY", NumberSourceContext.INCOMING_CALL)
        assertTrue("MY must parse", ctx.isParseable)
        assertTrue("MY E.164 starts with +60", ctx.deviceCanonicalNumber.startsWith("+60"))
    }

    @Test
    fun `GLOBAL-PHONE Asia ID mobile`() {
        val ctx = builder.build("0812-3456-7890", "ID", NumberSourceContext.INCOMING_CALL)
        assertTrue("ID must parse", ctx.isParseable)
        assertTrue("ID E.164 starts with +62", ctx.deviceCanonicalNumber.startsWith("+62"))
    }

    /**
     * 북미 2개국
     */
    @Test
    fun `GLOBAL-PHONE NorthAmerica US`() {
        val ctx = builder.build("(212) 555-1234", "US", NumberSourceContext.INCOMING_CALL)
        assertTrue("US must parse", ctx.isParseable)
        assertEquals("+12125551234", ctx.deviceCanonicalNumber)
        assertTrue("variants >= 2", ctx.searchVariants.size >= 2)
    }

    @Test
    fun `GLOBAL-PHONE NorthAmerica CA`() {
        val ctx = builder.build("(416) 555-0123", "CA", NumberSourceContext.INCOMING_CALL)
        assertTrue("CA must parse", ctx.isParseable)
        assertTrue("CA E.164 starts with +1", ctx.deviceCanonicalNumber.startsWith("+1"))
    }

    /**
     * 유럽 10개국
     */
    @Test
    fun `GLOBAL-PHONE Europe GB`() {
        val ctx = builder.build("020 7946 0958", "GB", NumberSourceContext.INCOMING_CALL)
        assertTrue("GB must parse", ctx.isParseable)
        assertTrue("GB E.164 starts with +44", ctx.deviceCanonicalNumber.startsWith("+44"))
    }

    @Test
    fun `GLOBAL-PHONE Europe DE`() {
        val ctx = builder.build("030 12345678", "DE", NumberSourceContext.INCOMING_CALL)
        assertTrue("DE must parse", ctx.isParseable)
        assertTrue("DE E.164 starts with +49", ctx.deviceCanonicalNumber.startsWith("+49"))
    }

    @Test
    fun `GLOBAL-PHONE Europe FR`() {
        val ctx = builder.build("01 23 45 67 89", "FR", NumberSourceContext.INCOMING_CALL)
        assertTrue("FR must parse", ctx.isParseable)
        assertTrue("FR E.164 starts with +33", ctx.deviceCanonicalNumber.startsWith("+33"))
    }

    @Test
    fun `GLOBAL-PHONE Europe IT`() {
        val ctx = builder.build("02 1234 5678", "IT", NumberSourceContext.INCOMING_CALL)
        assertTrue("IT must parse", ctx.isParseable)
        assertTrue("IT E.164 starts with +39", ctx.deviceCanonicalNumber.startsWith("+39"))
    }

    @Test
    fun `GLOBAL-PHONE Europe ES`() {
        val ctx = builder.build("912 34 56 78", "ES", NumberSourceContext.INCOMING_CALL)
        assertTrue("ES must parse", ctx.isParseable)
        assertTrue("ES E.164 starts with +34", ctx.deviceCanonicalNumber.startsWith("+34"))
    }

    @Test
    fun `GLOBAL-PHONE Europe RU`() {
        val ctx = builder.build("+7 495 123-45-67", "RU", NumberSourceContext.INCOMING_CALL)
        assertTrue("RU must parse", ctx.isParseable)
        assertEquals("+74951234567", ctx.deviceCanonicalNumber)
    }

    @Test
    fun `GLOBAL-PHONE Europe PL`() {
        val ctx = builder.build("22 123 45 67", "PL", NumberSourceContext.INCOMING_CALL)
        assertTrue("PL must parse", ctx.isParseable)
        assertTrue("PL E.164 starts with +48", ctx.deviceCanonicalNumber.startsWith("+48"))
    }

    @Test
    fun `GLOBAL-PHONE Europe NL`() {
        val ctx = builder.build("020 123 4567", "NL", NumberSourceContext.INCOMING_CALL)
        assertTrue("NL must parse", ctx.isParseable)
        assertTrue("NL E.164 starts with +31", ctx.deviceCanonicalNumber.startsWith("+31"))
    }

    @Test
    fun `GLOBAL-PHONE Europe SE`() {
        val ctx = builder.build("08-123 456 78", "SE", NumberSourceContext.INCOMING_CALL)
        assertTrue("SE must parse", ctx.isParseable)
        assertTrue("SE E.164 starts with +46", ctx.deviceCanonicalNumber.startsWith("+46"))
    }

    @Test
    fun `GLOBAL-PHONE Europe UA`() {
        val ctx = builder.build("+380 44 123 4567", "UA", NumberSourceContext.INCOMING_CALL)
        assertTrue("UA must parse", ctx.isParseable)
        assertEquals("+380441234567", ctx.deviceCanonicalNumber)
    }

    /**
     * 남미 5개국
     */
    @Test
    fun `GLOBAL-PHONE SouthAmerica BR`() {
        val ctx = builder.build("(11) 91234-5678", "BR", NumberSourceContext.INCOMING_CALL)
        assertTrue("BR must parse", ctx.isParseable)
        assertTrue("BR E.164 starts with +55", ctx.deviceCanonicalNumber.startsWith("+55"))
    }

    @Test
    fun `GLOBAL-PHONE SouthAmerica MX`() {
        val ctx = builder.build("55 1234 5678", "MX", NumberSourceContext.INCOMING_CALL)
        assertTrue("MX must parse", ctx.isParseable)
        assertTrue("MX E.164 starts with +52", ctx.deviceCanonicalNumber.startsWith("+52"))
    }

    @Test
    fun `GLOBAL-PHONE SouthAmerica AR`() {
        val ctx = builder.build("011 1234-5678", "AR", NumberSourceContext.INCOMING_CALL)
        assertTrue("AR must parse", ctx.isParseable)
        assertTrue("AR E.164 starts with +54", ctx.deviceCanonicalNumber.startsWith("+54"))
    }

    @Test
    fun `GLOBAL-PHONE SouthAmerica CL`() {
        val ctx = builder.build("2 2123 4567", "CL", NumberSourceContext.INCOMING_CALL)
        assertTrue("CL must parse", ctx.isParseable)
        assertTrue("CL E.164 starts with +56", ctx.deviceCanonicalNumber.startsWith("+56"))
    }

    @Test
    fun `GLOBAL-PHONE SouthAmerica CO`() {
        val ctx = builder.build("311 1234567", "CO", NumberSourceContext.INCOMING_CALL)
        assertTrue("CO must parse", ctx.isParseable)
        assertTrue("CO E.164 starts with +57", ctx.deviceCanonicalNumber.startsWith("+57"))
    }

    /**
     * 아프리카 5개국
     */
    @Test
    fun `GLOBAL-PHONE Africa ZA`() {
        val ctx = builder.build("021 123 4567", "ZA", NumberSourceContext.INCOMING_CALL)
        assertTrue("ZA must parse", ctx.isParseable)
        assertTrue("ZA E.164 starts with +27", ctx.deviceCanonicalNumber.startsWith("+27"))
    }

    @Test
    fun `GLOBAL-PHONE Africa NG`() {
        val ctx = builder.build("0801 234 5678", "NG", NumberSourceContext.INCOMING_CALL)
        assertTrue("NG must parse", ctx.isParseable)
        assertTrue("NG E.164 starts with +234", ctx.deviceCanonicalNumber.startsWith("+234"))
    }

    @Test
    fun `GLOBAL-PHONE Africa KE`() {
        val ctx = builder.build("0712 345678", "KE", NumberSourceContext.INCOMING_CALL)
        assertTrue("KE must parse", ctx.isParseable)
        assertTrue("KE E.164 starts with +254", ctx.deviceCanonicalNumber.startsWith("+254"))
    }

    @Test
    fun `GLOBAL-PHONE Africa EG`() {
        val ctx = builder.build("010 1234 5678", "EG", NumberSourceContext.INCOMING_CALL)
        assertTrue("EG must parse", ctx.isParseable)
        assertTrue("EG E.164 starts with +20", ctx.deviceCanonicalNumber.startsWith("+20"))
    }

    @Test
    fun `GLOBAL-PHONE Africa GH`() {
        val ctx = builder.build("024 123 4567", "GH", NumberSourceContext.INCOMING_CALL)
        assertTrue("GH must parse", ctx.isParseable)
        assertTrue("GH E.164 starts with +233", ctx.deviceCanonicalNumber.startsWith("+233"))
    }

    /**
     * 중동 5개국
     */
    @Test
    fun `GLOBAL-PHONE MiddleEast AE`() {
        val ctx = builder.build("050 123 4567", "AE", NumberSourceContext.INCOMING_CALL)
        assertTrue("AE must parse", ctx.isParseable)
        assertTrue("AE E.164 starts with +971", ctx.deviceCanonicalNumber.startsWith("+971"))
    }

    @Test
    fun `GLOBAL-PHONE MiddleEast SA`() {
        val ctx = builder.build("050 123 4567", "SA", NumberSourceContext.INCOMING_CALL)
        assertTrue("SA must parse", ctx.isParseable)
        assertTrue("SA E.164 starts with +966", ctx.deviceCanonicalNumber.startsWith("+966"))
    }

    @Test
    fun `GLOBAL-PHONE MiddleEast TR`() {
        val ctx = builder.build("0532 123 45 67", "TR", NumberSourceContext.INCOMING_CALL)
        assertTrue("TR must parse", ctx.isParseable)
        assertTrue("TR E.164 starts with +90", ctx.deviceCanonicalNumber.startsWith("+90"))
    }

    @Test
    fun `GLOBAL-PHONE MiddleEast IL`() {
        val ctx = builder.build("052-2345678", "IL", NumberSourceContext.INCOMING_CALL)
        assertTrue("IL must parse", ctx.isParseable)
        assertTrue("IL E.164 starts with +972", ctx.deviceCanonicalNumber.startsWith("+972"))
    }

    @Test
    fun `GLOBAL-PHONE MiddleEast QA`() {
        val ctx = builder.build("3312 3456", "QA", NumberSourceContext.INCOMING_CALL)
        assertTrue("QA must parse", ctx.isParseable)
        assertTrue("QA E.164 starts with +974", ctx.deviceCanonicalNumber.startsWith("+974"))
    }

    /**
     * 오세아니아 2개국
     */
    @Test
    fun `GLOBAL-PHONE Oceania AU`() {
        val ctx = builder.build("0412 345 678", "AU", NumberSourceContext.INCOMING_CALL)
        assertTrue("AU must parse", ctx.isParseable)
        assertTrue("AU E.164 starts with +61", ctx.deviceCanonicalNumber.startsWith("+61"))
    }

    @Test
    fun `GLOBAL-PHONE Oceania NZ`() {
        val ctx = builder.build("021 123 4567", "NZ", NumberSourceContext.INCOMING_CALL)
        assertTrue("NZ must parse", ctx.isParseable)
        assertTrue("NZ E.164 starts with +64", ctx.deviceCanonicalNumber.startsWith("+64"))
    }

    // ═══════════════════════════════════════════════════════════
    // 경계값 케이스
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `GLOBAL-PHONE Edge short number KR 114`() {
        val ctx = builder.build("114", "KR", NumberSourceContext.INCOMING_CALL)
        assertFalse("Short number must NOT parse", ctx.isParseable)
        assertEquals("114", ctx.deviceCanonicalNumber)
        assertTrue("Short number must have variants", ctx.searchVariants.isNotEmpty())
    }

    @Test
    fun `GLOBAL-PHONE Edge short number KR 1588`() {
        val ctx = builder.build("15881234", "KR", NumberSourceContext.INCOMING_CALL)
        // 1588 콜센터 번호 — 파싱 성공/실패 상관없이 searchVariants에 포함
        assertTrue("1588 must have variants", ctx.searchVariants.isNotEmpty())
        assertTrue("rawNumber preserved", ctx.rawNumber == "15881234")
    }

    @Test
    fun `GLOBAL-PHONE Edge international prefix already included`() {
        val ctx = builder.build("+821012345678", "US", NumberSourceContext.INCOMING_CALL)
        assertTrue("International must parse", ctx.isParseable)
        assertEquals("+821012345678", ctx.deviceCanonicalNumber)
    }

    @Test
    fun `GLOBAL-PHONE Edge wrong country but international prefix`() {
        // 일본 번호지만 기기 국가가 KR — 국제 prefix로 정확히 파싱
        val ctx = builder.build("+81312345678", "KR", NumberSourceContext.INCOMING_CALL)
        assertTrue("International prefix must parse regardless of device country", ctx.isParseable)
        assertEquals("+81312345678", ctx.deviceCanonicalNumber)
    }

    @Test
    fun `GLOBAL-PHONE Edge empty string`() {
        val ctx = builder.build("", "US", NumberSourceContext.INCOMING_CALL)
        assertFalse("Empty must NOT parse", ctx.isParseable)
        assertTrue("rawNumber preserved", ctx.rawNumber == "")
    }

    @Test
    fun `GLOBAL-PHONE Edge null country fallback`() {
        val ctx = builder.build("+821012345678", null, NumberSourceContext.INCOMING_CALL)
        assertTrue("Must parse with null country (international prefix)", ctx.isParseable)
        assertEquals("+821012345678", ctx.deviceCanonicalNumber)
    }

    @Test
    fun `GLOBAL-PHONE Edge special characters`() {
        val ctx = builder.build("(010) 1234 - 5678", "KR", NumberSourceContext.INCOMING_CALL)
        assertTrue("Must parse with special chars", ctx.isParseable)
        assertEquals("+821012345678", ctx.deviceCanonicalNumber)
    }

    @Test
    fun `GLOBAL-PHONE Edge US toll-free 800`() {
        val ctx = builder.build("1-800-555-0199", "US", NumberSourceContext.INCOMING_CALL)
        assertTrue("US toll-free must parse", ctx.isParseable)
        assertTrue("US toll-free E.164 starts with +1800", ctx.deviceCanonicalNumber.startsWith("+1800"))
    }

    // ═══════════════════════════════════════════════════════════
    // searchVariants 품질 검증
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `GLOBAL-PHONE Variants KR must contain raw, digits, national, E164`() {
        val ctx = builder.build("010-1234-5678", "KR", NumberSourceContext.INCOMING_CALL)
        val variants = ctx.searchVariants
        assertTrue("must contain raw", variants.contains("010-1234-5678"))
        assertTrue("must contain digits-only", variants.any { it == "01012345678" || it == "+821012345678" })
        assertTrue("must contain E.164", variants.contains("+821012345678"))
        assertTrue("variants >= 3", variants.size >= 3)
    }

    @Test
    fun `GLOBAL-PHONE Variants US must contain E164 and national`() {
        val ctx = builder.build("(212) 555-1234", "US", NumberSourceContext.INCOMING_CALL)
        val variants = ctx.searchVariants
        assertTrue("must contain E.164", variants.contains("+12125551234"))
        assertTrue("variants >= 3", variants.size >= 3)
    }
}
