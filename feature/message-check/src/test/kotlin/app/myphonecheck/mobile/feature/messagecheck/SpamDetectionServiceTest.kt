package app.myphonecheck.mobile.feature.messagecheck

import app.myphonecheck.mobile.core.globalengine.parsing.message.MessageCategory
import app.myphonecheck.mobile.core.globalengine.parsing.message.MessageClassifier
import app.myphonecheck.mobile.core.globalengine.parsing.message.SmsPatternExtractor
import app.myphonecheck.mobile.core.globalengine.simcontext.SimContext
import app.myphonecheck.mobile.core.globalengine.simcontext.SimContextProvider
import app.myphonecheck.mobile.feature.messagecheck.service.SpamDetectionService
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Currency
import java.util.TimeZone

class SpamDetectionServiceTest {

    private val extractor = SmsPatternExtractor()
    private val classifier = MessageClassifier()
    private val simProvider = mockk<SimContextProvider>()
    private val service = SpamDetectionService(extractor, classifier, simProvider)

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
    fun `Short URL message detected as SPAM_CANDIDATE`() {
        every { simProvider.resolve() } returns simFor("KR")
        val result = service.analyze("01012345678", "Click https://bit.ly/abc")
        assertEquals(MessageCategory.SPAM_CANDIDATE, result.category)
        assertTrue(service.isSpamCandidate("01012345678", "Click https://bit.ly/abc"))
    }

    @Test
    fun `Short sender with currency = PAYMENT_CANDIDATE`() {
        every { simProvider.resolve() } returns simFor("KR")
        val result = service.analyze("1588", "결제 ₩50000")
        assertEquals(MessageCategory.PAYMENT_CANDIDATE, result.category)
    }

    @Test
    fun `Plain text not spam`() {
        every { simProvider.resolve() } returns simFor("US")
        val result = service.analyze("FRIEND", "Hey are you free tonight")
        assertEquals(MessageCategory.NORMAL, result.category)
        assertFalse(service.isSpamCandidate("FRIEND", "Hey are you free tonight"))
    }
}
