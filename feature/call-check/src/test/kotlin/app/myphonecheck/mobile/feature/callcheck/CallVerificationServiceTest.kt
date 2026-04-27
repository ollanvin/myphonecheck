package app.myphonecheck.mobile.feature.callcheck

import app.myphonecheck.mobile.core.globalengine.parsing.phone.PhoneNumberParser
import app.myphonecheck.mobile.core.globalengine.simcontext.SimContext
import app.myphonecheck.mobile.core.globalengine.simcontext.SimContextProvider
import app.myphonecheck.mobile.feature.callcheck.service.CallClassification
import app.myphonecheck.mobile.feature.callcheck.service.CallVerificationService
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Currency
import java.util.TimeZone

/**
 * CallVerificationService 통합 테스트.
 *
 * 코어 PhoneNumberParser/Validator (실제 인스턴스) + SimContextProvider (mock).
 */
class CallVerificationServiceTest {

    private val phoneParser = PhoneNumberParser()
    private val simProvider = mockk<SimContextProvider>()

    private val service = CallVerificationService(phoneParser, simProvider)

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
    fun `KR mobile under KR SIM classified as MOBILE`() {
        every { simProvider.resolve() } returns simFor("KR")
        val v = service.verify("010-1234-5678")
        assertTrue(v.parsed.isValid)
        assertEquals(CallClassification.MOBILE, v.classification)
    }

    @Test
    fun `KR mobile under US SIM classified as INTERNATIONAL`() {
        val v = service.verify("+82-10-1234-5678", simFor("US"))
        assertTrue(v.parsed.isValid)
        assertEquals(CallClassification.INTERNATIONAL, v.classification)
    }

    @Test
    fun `garbage input classified INVALID`() {
        every { simProvider.resolve() } returns simFor("KR")
        val v = service.verify("xxx")
        assertFalse(v.parsed.isValid)
        assertEquals(CallClassification.INVALID, v.classification)
    }

    @Test
    fun `KR landline classified as FIXED_LINE`() {
        every { simProvider.resolve() } returns simFor("KR")
        val v = service.verify("02-123-4567")
        assertTrue(v.parsed.isValid)
        // KR 02-123-4567 may be FIXED_LINE
        assertTrue(
            v.classification == CallClassification.FIXED_LINE ||
                v.classification == CallClassification.FIXED_OR_MOBILE
        )
    }
}
