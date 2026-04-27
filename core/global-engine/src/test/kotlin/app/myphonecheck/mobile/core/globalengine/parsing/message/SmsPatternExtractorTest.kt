package app.myphonecheck.mobile.core.globalengine.parsing.message

import app.myphonecheck.mobile.core.globalengine.simcontext.SimContext
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Currency
import java.util.TimeZone

class SmsPatternExtractorTest {

    private val extractor = SmsPatternExtractor()

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
    fun `Short numeric sender detected`() {
        val f = extractor.extract("1588", "Hello", simFor("KR"))
        assertTrue(f.isShortSender)
    }

    @Test
    fun `Long numeric sender not classified as short`() {
        val f = extractor.extract("01012345678", "Hello", simFor("KR"))
        assertFalse(f.isShortSender)
    }

    @Test
    fun `Alphanumeric sender not short sender`() {
        val f = extractor.extract("BANK", "Hello", simFor("US"))
        assertFalse(f.isShortSender)
    }

    @Test
    fun `URL pattern detected`() {
        val f = extractor.extract("123", "Visit https://example.com now", simFor("KR"))
        assertTrue(f.hasUrl)
        assertEquals(1, f.urlCount)
    }

    @Test
    fun `Multiple URLs counted`() {
        val f = extractor.extract(
            "123",
            "https://a.com and http://b.org plus https://c.io",
            simFor("KR"),
        )
        assertTrue(f.hasUrl)
        assertEquals(3, f.urlCount)
    }

    @Test
    fun `KRW symbol currency pattern detected`() {
        val f = extractor.extract("123", "결제금액 ₩50000", simFor("KR"))
        assertTrue(f.hasCurrencyPattern)
    }

    @Test
    fun `USD symbol currency pattern detected`() {
        val f = extractor.extract("123", "Charged $42.50 today", simFor("US"))
        assertTrue(f.hasCurrencyPattern)
    }

    @Test
    fun `EUR ISO code currency pattern detected`() {
        val f = extractor.extract("123", "Amount EUR 100", simFor("DE"))
        assertTrue(f.hasCurrencyPattern)
    }

    @Test
    fun `Plain text no special pattern`() {
        val f = extractor.extract("01012345678", "Hello, how are you", simFor("KR"))
        assertFalse(f.hasUrl)
        assertFalse(f.hasCurrencyPattern)
        assertFalse(f.isShortSender)
    }

    @Test
    fun `Country hint propagated from SimContext`() {
        val f = extractor.extract("123", "Hi", simFor("JP"))
        assertEquals("JP", f.countryHint)
    }
}
