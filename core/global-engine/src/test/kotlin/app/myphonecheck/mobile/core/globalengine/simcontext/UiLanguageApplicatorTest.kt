package app.myphonecheck.mobile.core.globalengine.simcontext

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Currency
import java.util.Locale
import java.util.TimeZone

class UiLanguageApplicatorTest {

    private val applicator = UiLanguageApplicator()

    private fun simFor(country: String): SimContext = SimContext(
        mcc = "", mnc = "", countryIso = country, operatorName = "",
        currency = Currency.getInstance("USD"),
        phoneRegion = country,
        timezone = TimeZone.getTimeZone("UTC"),
    )

    @Test
    fun `SIM_BASED with KR returns ko Locale`() {
        val locale = applicator.resolveLocale(UiLanguagePreference.SIM_BASED, simFor("KR"))!!
        assertEquals("ko", locale.language)
    }

    @Test
    fun `SIM_BASED with JP returns ja Locale`() {
        val locale = applicator.resolveLocale(UiLanguagePreference.SIM_BASED, simFor("JP"))!!
        assertEquals("ja", locale.language)
    }

    @Test
    fun `SIM_BASED with US returns en-US`() {
        val locale = applicator.resolveLocale(UiLanguagePreference.SIM_BASED, simFor("US"))!!
        assertEquals("en", locale.language)
        assertEquals("US", locale.country)
    }

    @Test
    fun `SIM_BASED with TW preserves Traditional Chinese`() {
        val locale = applicator.resolveLocale(UiLanguagePreference.SIM_BASED, simFor("TW"))!!
        assertEquals("zh", locale.language)
        assertEquals("TW", locale.country)
    }

    @Test
    fun `SIM_BASED with unknown country falls back to English`() {
        val locale = applicator.resolveLocale(UiLanguagePreference.SIM_BASED, simFor("ZZ"))!!
        assertEquals("en", locale.language)
    }

    @Test
    fun `ENGLISH preference always returns Locale ENGLISH`() {
        val l1 = applicator.resolveLocale(UiLanguagePreference.ENGLISH, simFor("KR"))
        val l2 = applicator.resolveLocale(UiLanguagePreference.ENGLISH, simFor("JP"))
        assertNotNull(l1)
        assertEquals("en", l1!!.language)
        assertEquals(Locale.ENGLISH, l1)
        assertEquals(Locale.ENGLISH, l2)
    }

    @Test
    fun `DEVICE_SYSTEM returns null (signal to use empty locale list)`() {
        val locale = applicator.resolveLocale(UiLanguagePreference.DEVICE_SYSTEM, simFor("KR"))
        assertNull(locale)
    }

    @Test
    fun `SIM_BASED is case-insensitive on countryIso`() {
        val upper = applicator.resolveLocale(UiLanguagePreference.SIM_BASED, simFor("KR"))!!
        val lower = applicator.resolveLocale(UiLanguagePreference.SIM_BASED, simFor("kr"))!!
        assertEquals(upper.language, lower.language)
    }
}
