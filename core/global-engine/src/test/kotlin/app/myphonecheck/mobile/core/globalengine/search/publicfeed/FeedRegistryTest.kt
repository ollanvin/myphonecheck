package app.myphonecheck.mobile.core.globalengine.search.publicfeed

import app.myphonecheck.mobile.core.globalengine.simcontext.SimContext
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Currency
import java.util.TimeZone

class FeedRegistryTest {

    private val registry = FeedRegistry()

    private fun sim(country: String): SimContext = SimContext(
        mcc = "", mnc = "", countryIso = country, operatorName = "",
        currency = Currency.getInstance("USD"),
        phoneRegion = country, timezone = TimeZone.getTimeZone("UTC"),
    )

    @Test
    fun `all returns 5 default sources`() {
        // PR #29 등록: Abuse.ch, PhishTank, KISA, TheCall placeholder, KT placeholder
        assertEquals(5, registry.all().size)
    }

    @Test
    fun `recommendForSim KR returns global plus KR-only`() {
        val recommended = registry.recommendForSim(sim("KR"))
        // 글로벌 2개 + KR 3개 = 5
        assertEquals(5, recommended.size)
    }

    @Test
    fun `recommendForSim US returns only global`() {
        val recommended = registry.recommendForSim(sim("US"))
        assertEquals(2, recommended.size)
        assertTrue(recommended.all { it.countryScope is CountryScope.GLOBAL })
    }

    @Test
    fun `byType SecurityIntelligence returns 2`() {
        val list = registry.byType(FeedType.SecurityIntelligence)
        assertEquals(2, list.size)
    }

    @Test
    fun `byType GovernmentPublic returns 1`() {
        val list = registry.byType(FeedType.GovernmentPublic)
        assertEquals(1, list.size)
    }

    @Test
    fun `byType CompetitorApp returns 1 placeholder`() {
        val list = registry.byType(FeedType.CompetitorApp)
        assertEquals(1, list.size)
        assertTrue(registry.isPlaceholder(list[0]))
    }

    @Test
    fun `byType TelcoBlocklist returns 1 placeholder`() {
        val list = registry.byType(FeedType.TelcoBlocklist)
        assertEquals(1, list.size)
        assertTrue(registry.isPlaceholder(list[0]))
    }

    @Test
    fun `byId Abuse exists and is not placeholder`() {
        val src = registry.byId("abusech_urlhaus")
        assertNotNull(src)
        assertFalse(registry.isPlaceholder(src!!))
        assertEquals(FeedType.SecurityIntelligence, src.type)
    }

    @Test
    fun `byId unknown returns null`() {
        assertNull(registry.byId("nonexistent_id"))
    }

    @Test
    fun `placeholder detection by URL pattern`() {
        val ph = registry.byId("thecall_kr_placeholder")!!
        assertTrue(registry.isPlaceholder(ph))
        val real = registry.byId("phishtank")!!
        assertFalse(registry.isPlaceholder(real))
    }
}
