package app.myphonecheck.mobile.core.globalengine.parsing.notification

import app.myphonecheck.mobile.core.globalengine.parsing.currency.CurrencyAmountParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NotificationFeatureExtractorTest {

    private val extractor = NotificationFeatureExtractor(CurrencyAmountParser())

    @Test
    fun `Currency in notification text detected`() {
        val f = extractor.extractFromText("Bank Alert", "Charged $42.50 at Coffee Shop")
        assertTrue(f.hasCurrencyPattern)
        assertEquals("Bank Alert", f.title)
    }

    @Test
    fun `KRW symbol in notification text detected`() {
        val f = extractor.extractFromText("결제 알림", "결제금액 ₩50,000")
        assertTrue(f.hasCurrencyPattern)
    }

    @Test
    fun `Plain text has no currency`() {
        val f = extractor.extractFromText("Hello", "Just saying hi")
        assertFalse(f.hasCurrencyPattern)
        assertEquals(14, f.bodyLength)
    }

    @Test
    fun `Empty text returns no currency and zero length`() {
        val f = extractor.extractFromText("", "")
        assertFalse(f.hasCurrencyPattern)
        assertEquals(0, f.bodyLength)
        assertEquals("", f.title)
    }
}
