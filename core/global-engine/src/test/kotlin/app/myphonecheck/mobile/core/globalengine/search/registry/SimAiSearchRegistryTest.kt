package app.myphonecheck.mobile.core.globalengine.search.registry

import app.myphonecheck.mobile.core.globalengine.search.external.ExternalMode
import app.myphonecheck.mobile.core.globalengine.simcontext.SimContext
import app.myphonecheck.mobile.core.globalengine.simcontext.SimContextProvider
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Currency
import java.util.TimeZone

class SimAiSearchRegistryTest {

    private fun simContext(countryIso: String) = SimContext(
        mcc = "", mnc = "", countryIso = countryIso, operatorName = "",
        currency = Currency.getInstance("USD"),
        phoneRegion = countryIso, timezone = TimeZone.getTimeZone("UTC"),
    )

    private fun registryFor(countryIso: String): SimAiSearchRegistry {
        val provider = mockk<SimContextProvider>()
        every { provider.resolve() } returns simContext(countryIso)
        return SimAiSearchRegistry(provider)
    }

    @Test
    fun `KR SIM returns 3 candidates including Naver AI`() {
        val candidates = registryFor("KR").getCandidates()
        assertEquals(3, candidates.size)
        assertTrue(candidates.contains(ExternalMode.NAVER_AI))
        assertTrue(candidates.contains(ExternalMode.GOOGLE_AI_MODE))
        assertTrue(candidates.contains(ExternalMode.BING_COPILOT))
    }

    @Test
    fun `JP SIM returns 3 candidates including Yahoo Japan AI`() {
        val candidates = registryFor("JP").getCandidates()
        assertEquals(3, candidates.size)
        assertTrue(candidates.contains(ExternalMode.YAHOO_JAPAN_AI))
    }

    @Test
    fun `CN SIM returns 2 candidates Baidu and Bing`() {
        val candidates = registryFor("CN").getCandidates()
        assertEquals(2, candidates.size)
        assertTrue(candidates.contains(ExternalMode.BAIDU_AI))
        assertTrue(candidates.contains(ExternalMode.BING_COPILOT))
    }

    @Test
    fun `Unknown SIM falls back to Global default 2 candidates`() {
        val candidates = registryFor("ZW").getCandidates()
        assertEquals(2, candidates.size)
        assertEquals(listOf(ExternalMode.GOOGLE_AI_MODE, ExternalMode.BING_COPILOT), candidates)
    }

    @Test
    fun `Lowercase SIM iso normalizes to uppercase`() {
        val candidates = registryFor("kr").getCandidates()
        assertEquals(3, candidates.size)
        assertTrue(candidates.contains(ExternalMode.NAVER_AI))
    }

    @Test
    fun `getCandidatesFor matches getCandidates with same country`() {
        val registry = registryFor("KR")
        assertEquals(registry.getCandidates(), registry.getCandidatesFor("KR"))
    }

    @Test
    fun `All SIM candidates have minimum 2 options (헌법 1 정합)`() {
        listOf("KR", "JP", "CN", "US", "GB", "ZW", "UNKNOWN").forEach { country ->
            val registry = registryFor(country)
            val candidates = registry.getCandidatesFor(country)
            assertTrue(candidates.size >= 2)
        }
    }
}
