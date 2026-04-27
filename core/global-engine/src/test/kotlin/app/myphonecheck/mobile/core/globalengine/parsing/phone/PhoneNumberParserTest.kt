package app.myphonecheck.mobile.core.globalengine.parsing.phone

import app.myphonecheck.mobile.core.globalengine.simcontext.SimContext
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Currency
import java.util.TimeZone

/**
 * PhoneNumberParser 다양성 테스트 (Architecture v2.0.0 §30).
 *
 * 글로벌 동작 검증 — 헌법 §8조 SIM-Oriented Single Core.
 * SimContext.phoneRegion 별로 동일 입력이 다르게 정규화되는지 확인.
 */
class PhoneNumberParserTest {

    private val parser = PhoneNumberParser()

    private fun simFor(country: String): SimContext = SimContext(
        mcc = "",
        mnc = "",
        countryIso = country,
        operatorName = "",
        currency = Currency.getInstance("USD"),
        phoneRegion = country,
        timezone = TimeZone.getTimeZone("UTC"),
    )

    @Test
    fun `KR mobile national input parsed to E164`() {
        val r = parser.parse("010-1234-5678", simFor("KR"))
        assertTrue(r.isValid)
        assertEquals("+821012345678", r.e164)
        assertEquals("KR", r.regionCode)
    }

    @Test
    fun `US mobile with parens and dashes parsed`() {
        val r = parser.parse("(202) 555-0181", simFor("US"))
        assertTrue(r.isValid)
        assertEquals("+12025550181", r.e164)
        assertEquals("US", r.regionCode)
    }

    @Test
    fun `JP mobile parsed and formatted`() {
        val r = parser.parse("090-1234-5678", simFor("JP"))
        assertTrue(r.isValid)
        assertEquals("+819012345678", r.e164)
        assertEquals("JP", r.regionCode)
    }

    @Test
    fun `GB mobile parsed`() {
        val r = parser.parse("07400 123456", simFor("GB"))
        assertTrue(r.isValid)
        assertEquals("+447400123456", r.e164)
        assertEquals("GB", r.regionCode)
    }

    @Test
    fun `International plus format ignores default region`() {
        val r = parser.parse("+33 1 42 68 53 00", simFor("US"))
        assertTrue(r.isValid)
        assertEquals("+33142685300", r.e164)
        assertEquals("FR", r.regionCode)
    }

    @Test
    fun `Empty input returns invalid`() {
        val r = parser.parse("   ", simFor("KR"))
        assertFalse(r.isValid)
        assertEquals("UNKNOWN", r.numberType)
    }

    @Test
    fun `Garbage input returns invalid result`() {
        val r = parser.parse("not-a-phone-number", simFor("KR"))
        assertFalse(r.isValid)
    }

    @Test
    fun `Too short for region returns invalid`() {
        // 자릿수 부족 — libphonenumber parse는 성공해도 isValidNumber=false
        val r = parser.parse("123", simFor("KR"))
        assertFalse(r.isValid)
    }

    @Test
    fun `KR landline Seoul prefix parsed`() {
        val r = parser.parse("02-123-4567", simFor("KR"))
        assertTrue(r.isValid)
        assertEquals("+8221234567", r.e164)
    }

    @Test
    fun `Same number different region produces different E164`() {
        // 동일 raw가 region에 따라 다른 국가 코드 부여
        val rUs = parser.parse("212-555-0188", simFor("US"))
        val rGb = parser.parse("212-555-0188", simFor("GB"))
        assertEquals("+12125550188", rUs.e164)
        // GB 기본 region이면 US 형식으로 파싱 안 됨 — invalid 또는 GB 해석
        // 핵심: 동일 raw가 SimContext에 따라 결과가 갈라짐
        assertTrue(rUs.e164 != rGb.e164 || rUs.isValid != rGb.isValid || rUs.regionCode != rGb.regionCode)
    }
}
