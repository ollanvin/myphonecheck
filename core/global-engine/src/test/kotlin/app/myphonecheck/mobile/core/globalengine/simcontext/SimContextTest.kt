package app.myphonecheck.mobile.core.globalengine.simcontext

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * SimContext fallback 동작 검증 (Architecture v2.0.0 §29-5 + 헌법 §8-5).
 *
 * SIM 부재 시 디바이스 시스템 Locale country fallback (사용자 명시 필수).
 */
class SimContextTest {

    @Test
    fun `fallback KR → countryIso KR + currency KRW`() {
        val ctx = SimContext.fallback("KR")
        assertEquals("KR", ctx.countryIso)
        assertEquals("KRW", ctx.currency.currencyCode)
        assertEquals("KR", ctx.phoneRegion)
        assertEquals("", ctx.mcc)
        assertEquals("", ctx.mnc)
        assertEquals("", ctx.operatorName)
    }

    @Test
    fun `fallback lowercase → uppercase`() {
        val ctx = SimContext.fallback("us")
        assertEquals("US", ctx.countryIso)
        assertEquals("USD", ctx.currency.currencyCode)
    }

    @Test
    fun `fallback empty → US 기본값`() {
        val ctx = SimContext.fallback("")
        assertEquals("US", ctx.countryIso)
        assertEquals("USD", ctx.currency.currencyCode)
    }

    @Test
    fun `fallback BH → 3 decimals 통화`() {
        val ctx = SimContext.fallback("BH")
        assertEquals("BHD", ctx.currency.currencyCode)
        assertEquals(3, ctx.currency.defaultFractionDigits)
    }

    @Test
    fun `fallback timezone 항상 디바이스 default`() {
        val ctx = SimContext.fallback("JP")
        assertTrue(ctx.timezone.id.isNotEmpty())
    }
}
