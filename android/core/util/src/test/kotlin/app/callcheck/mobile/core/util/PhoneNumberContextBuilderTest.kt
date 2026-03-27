package app.callcheck.mobile.core.util

import app.callcheck.mobile.core.model.NumberSourceContext
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * PhoneNumberContextBuilder 단위 테스트.
 *
 * ┌──────────────────────────────────────────────────────────────┐
 * │ 검증 대상                                                     │
 * ├──────────────────────────────────────────────────────────────┤
 * │ 1. rawNumber 보존 원칙 (정규화로 덮어쓰지 않음)              │
 * │ 2. deviceCanonicalNumber 생성 (비교 전용)                    │
 * │ 3. searchVariants 다양성 (검색 정확도 보장)                  │
 * │ 4. 짧은 번호(114, 1345) 처리 — 파싱 실패해도 검색 가능     │
 * │ 5. 국가별 번호 형식 호환 (KR, US, JP, UK)                   │
 * │ 6. 빈 번호 / 경계값 처리                                     │
 * │ 7. sourceContext 전달 무결성                                  │
 * └──────────────────────────────────────────────────────────────┘
 */
class PhoneNumberContextBuilderTest {

    private lateinit var builder: PhoneNumberContextBuilder

    @Before
    fun setUp() {
        builder = PhoneNumberContextBuilder()
    }

    // ═══════════════════════════════════════════════════════════
    // 1. rawNumber 보존 원칙
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `rawNumber is preserved exactly as provided`() {
        val raw = "010-1234-5678"
        val ctx = builder.build(raw, "KR", NumberSourceContext.INCOMING_CALL)
        assertEquals(raw, ctx.rawNumber)
    }

    @Test
    fun `rawNumber with spaces and hyphens is preserved`() {
        val raw = "+82 10 1234 5678"
        val ctx = builder.build(raw, "KR", NumberSourceContext.CALL_LOG)
        assertEquals(raw, ctx.rawNumber)
    }

    @Test
    fun `rawNumber trimming removes only leading and trailing whitespace`() {
        val raw = "  010-1234-5678  "
        val ctx = builder.build(raw, "KR", NumberSourceContext.INCOMING_CALL)
        assertEquals("010-1234-5678", ctx.rawNumber)
    }

    // ═══════════════════════════════════════════════════════════
    // 2. deviceCanonicalNumber 생성
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `KR mobile number produces E164 canonical`() {
        val ctx = builder.build("010-1234-5678", "KR", NumberSourceContext.INCOMING_CALL)
        assertTrue(ctx.isParseable)
        assertEquals("+821012345678", ctx.deviceCanonicalNumber)
    }

    @Test
    fun `US number produces E164 canonical`() {
        val ctx = builder.build("(212) 555-1234", "US", NumberSourceContext.INCOMING_CALL)
        assertTrue(ctx.isParseable)
        assertEquals("+12125551234", ctx.deviceCanonicalNumber)
    }

    @Test
    fun `JP number produces E164 canonical`() {
        val ctx = builder.build("03-1234-5678", "JP", NumberSourceContext.INCOMING_CALL)
        assertTrue(ctx.isParseable)
        assertEquals("+81312345678", ctx.deviceCanonicalNumber)
    }

    @Test
    fun `UK number produces E164 canonical`() {
        val ctx = builder.build("020 7946 0958", "GB", NumberSourceContext.INCOMING_CALL)
        assertTrue(ctx.isParseable)
        assertEquals("+442079460958", ctx.deviceCanonicalNumber)
    }

    @Test
    fun `short number produces digits-only canonical`() {
        val ctx = builder.build("114", "KR", NumberSourceContext.INCOMING_CALL)
        assertFalse(ctx.isParseable)
        assertEquals("114", ctx.deviceCanonicalNumber)
    }

    @Test
    fun `4-digit service number produces digits-only canonical`() {
        val ctx = builder.build("1345", "KR", NumberSourceContext.INCOMING_CALL)
        assertFalse(ctx.isParseable)
        assertEquals("1345", ctx.deviceCanonicalNumber)
    }

    // ═══════════════════════════════════════════════════════════
    // 3. searchVariants 다양성
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `parseable number generates multiple search variants`() {
        val ctx = builder.build("010-1234-5678", "KR", NumberSourceContext.INCOMING_CALL)
        assertTrue(ctx.searchVariants.size >= 3)
        // raw 포함
        assertTrue(ctx.searchVariants.contains("010-1234-5678"))
        // digits only 포함
        assertTrue(ctx.searchVariants.contains("01012345678"))
        // E.164 포함
        assertTrue(ctx.searchVariants.contains("+821012345678"))
    }

    @Test
    fun `search variants are deduplicated`() {
        val ctx = builder.build("01012345678", "KR", NumberSourceContext.INCOMING_CALL)
        val uniqueCount = ctx.searchVariants.size
        val distinctCount = ctx.searchVariants.distinct().size
        assertEquals(uniqueCount, distinctCount)
    }

    @Test
    fun `US number search variants include national format`() {
        val ctx = builder.build("2125551234", "US", NumberSourceContext.INCOMING_CALL)
        assertTrue(ctx.isParseable)
        // national format은 (212) 555-1234 형식
        val hasNationalFormat = ctx.searchVariants.any { it.contains("212") && it.contains("555") }
        assertTrue("searchVariants should include national format", hasNationalFormat)
    }

    // ═══════════════════════════════════════════════════════════
    // 4. 짧은 번호 / 비표준 형식
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `short number 114 is not parseable but has search variants`() {
        val ctx = builder.build("114", "KR", NumberSourceContext.INCOMING_CALL)
        assertFalse(ctx.isParseable)
        assertTrue(ctx.searchVariants.isNotEmpty())
        assertTrue(ctx.searchVariants.contains("114"))
    }

    @Test
    fun `short number 1345 has at least raw as variant`() {
        val ctx = builder.build("1345", "KR", NumberSourceContext.INCOMING_CALL)
        assertFalse(ctx.isParseable)
        assertTrue(ctx.searchVariants.contains("1345"))
    }

    @Test
    fun `8-digit service number 15881234 handling`() {
        val ctx = builder.build("15881234", "KR", NumberSourceContext.INCOMING_CALL)
        // 1588 번호는 KR 특수번호, libphonenumber이 처리할 수도 있고 못 할 수도 있음
        // 중요한 것은: searchVariants에 원본이 반드시 포함되어야 함
        assertTrue(ctx.searchVariants.contains("15881234"))
    }

    // ═══════════════════════════════════════════════════════════
    // 5. 국가별 번호 형식 호환
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `international format input with KR country`() {
        val ctx = builder.build("+82-10-1234-5678", "KR", NumberSourceContext.INCOMING_CALL)
        assertTrue(ctx.isParseable)
        assertEquals("+821012345678", ctx.deviceCanonicalNumber)
        assertEquals("+82-10-1234-5678", ctx.rawNumber)
    }

    @Test
    fun `number with parentheses US format`() {
        val ctx = builder.build("(800) 829-0433", "US", NumberSourceContext.INCOMING_CALL)
        assertTrue(ctx.isParseable)
        assertEquals("+18008290433", ctx.deviceCanonicalNumber)
    }

    @Test
    fun `JP toll-free number`() {
        val ctx = builder.build("0120-444-113", "JP", NumberSourceContext.INCOMING_CALL)
        assertTrue(ctx.isParseable)
        assertEquals("+81120444113", ctx.deviceCanonicalNumber)
    }

    // ═══════════════════════════════════════════════════════════
    // 6. 빈 번호 / 경계값
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `empty string produces empty context`() {
        val ctx = builder.build("", "KR", NumberSourceContext.INCOMING_CALL)
        assertEquals("", ctx.rawNumber)
        assertEquals("", ctx.deviceCanonicalNumber)
        assertTrue(ctx.searchVariants.isEmpty())
        assertFalse(ctx.isParseable)
    }

    @Test
    fun `whitespace only produces empty context`() {
        val ctx = builder.build("   ", "KR", NumberSourceContext.INCOMING_CALL)
        assertEquals("", ctx.rawNumber)
        assertEquals("", ctx.deviceCanonicalNumber)
        assertTrue(ctx.searchVariants.isEmpty())
        assertFalse(ctx.isParseable)
    }

    @Test
    fun `null country code uses fallback parsing`() {
        val ctx = builder.build("+821012345678", null, NumberSourceContext.INCOMING_CALL)
        assertTrue(ctx.isParseable)
        assertEquals("+821012345678", ctx.deviceCanonicalNumber)
        assertNull(ctx.deviceCountryCode)
    }

    // ═══════════════════════════════════════════════════════════
    // 7. sourceContext 전달 무결성
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `sourceContext INCOMING_CALL is preserved`() {
        val ctx = builder.build("01012345678", "KR", NumberSourceContext.INCOMING_CALL)
        assertEquals(NumberSourceContext.INCOMING_CALL, ctx.sourceContext)
    }

    @Test
    fun `sourceContext CALL_LOG is preserved`() {
        val ctx = builder.build("01012345678", "KR", NumberSourceContext.CALL_LOG)
        assertEquals(NumberSourceContext.CALL_LOG, ctx.sourceContext)
    }

    @Test
    fun `sourceContext SMS is preserved`() {
        val ctx = builder.build("01012345678", "KR", NumberSourceContext.SMS)
        assertEquals(NumberSourceContext.SMS, ctx.sourceContext)
    }

    @Test
    fun `sourceContext CONTACT is preserved`() {
        val ctx = builder.build("01012345678", "KR", NumberSourceContext.CONTACT)
        assertEquals(NumberSourceContext.CONTACT, ctx.sourceContext)
    }

    // ═══════════════════════════════════════════════════════════
    // 8. deviceCountryCode 전달
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `deviceCountryCode is passed through`() {
        val ctx = builder.build("01012345678", "KR", NumberSourceContext.INCOMING_CALL)
        assertEquals("KR", ctx.deviceCountryCode)
    }

    @Test
    fun `deviceCountryCode US is passed through`() {
        val ctx = builder.build("2125551234", "US", NumberSourceContext.INCOMING_CALL)
        assertEquals("US", ctx.deviceCountryCode)
    }

    @Test
    fun `deviceCountryCode null is passed through`() {
        val ctx = builder.build("+821012345678", null, NumberSourceContext.INCOMING_CALL)
        assertNull(ctx.deviceCountryCode)
    }
}
