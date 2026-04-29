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
    fun `all returns 6 sources after Stage 3-000 CompetitorApp removal`() {
        // Stage 3-000 (헌법 §1 v2.4.0 정합):
        // - Active 4: Abuse.ch, PhishTank, KISA Phishing URL, KISA Phishing URL Recent
        // - Hidden 2: kisa_smishing_kr, kt_blocklist_kr
        // - 폐기: thecall_kr, whowho_kr, moaff_kr, whoscall_global (CompetitorApp)
        assertEquals(6, registry.all().size)
    }

    @Test
    fun `recommendForSim KR returns global plus KR-only`() {
        val recommended = registry.recommendForSim(sim("KR"))
        // GLOBAL 2 (abusech, phishtank) + KR 4 (kisa × 3 + telco 1) = 6
        assertEquals(6, recommended.size)
    }

    @Test
    fun `recommendForSim US returns only global sources`() {
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
    fun `byType GovernmentPublic returns 3 (2 active KISA + 1 hidden smishing)`() {
        val list = registry.byType(FeedType.GovernmentPublic)
        assertEquals(3, list.size)
        // 2 active + 1 hidden
        assertEquals(2, list.count { it.requiresUserOptIn })
        assertEquals(1, list.count { !it.requiresUserOptIn })
    }

    @Test
    fun `byType TelcoBlocklist returns 1 hidden placeholder`() {
        val list = registry.byType(FeedType.TelcoBlocklist)
        assertEquals(1, list.size)
        assertFalse(list[0].requiresUserOptIn)
        assertTrue(registry.isPlaceholder(list[0]))
    }

    @Test
    fun `byId Abuse exists and is not placeholder`() {
        val src = registry.byId("abusech_urlhaus")
        assertNotNull(src)
        assertFalse(registry.isPlaceholder(src!!))
        assertEquals(FeedType.SecurityIntelligence, src.type)
        assertTrue(src.requiresUserOptIn)
    }

    @Test
    fun `byId unknown returns null`() {
        assertNull(registry.byId("nonexistent_id"))
    }

    @Test
    fun `placeholder detection - blank URL is placeholder`() {
        val telcoPlaceholder = registry.byId("kt_blocklist_kr")!!
        assertTrue(registry.isPlaceholder(telcoPlaceholder))
        val real = registry.byId("phishtank")!!
        assertFalse(registry.isPlaceholder(real))
    }

    // Phase 2-C 신규 테스트 (WO-V210-FEEDS-ACTIVATE STEP 5)

    @Test
    fun `KISA phishing URL active sources have real data dot go dot kr URL`() {
        val ds1 = registry.byId("kisa_phishing_url_kr")
        assertNotNull("kisa_phishing_url_kr should exist", ds1)
        assertEquals(
            "https://www.data.go.kr/data/15109780/fileData.do",
            ds1!!.downloadUrl,
        )
        assertTrue("KISA phishing URL must be opt-in active", ds1.requiresUserOptIn)
        assertFalse("KISA active source must not be placeholder", registry.isPlaceholder(ds1))

        val ds2 = registry.byId("kisa_phishing_url_recent_kr")
        assertNotNull(ds2)
        assertEquals(
            "https://www.data.go.kr/data/15143094/fileData.do",
            ds2!!.downloadUrl,
        )
        assertTrue(ds2.requiresUserOptIn)
        assertFalse(registry.isPlaceholder(ds2))
    }

    @Test
    fun `Competitor sources fully removed in Stage 3-000`() {
        // 폐기 완료 (헌법 §1 v2.4.0 정합): thecall_kr, whowho_kr, moaff_kr, whoscall_global
        listOf("thecall_kr", "whowho_kr", "moaff_kr", "whoscall_global").forEach { id ->
            assertNull("CompetitorApp id $id must be removed", registry.byId(id))
        }
    }

    @Test
    fun `recommendForSim KR returns active KR sources`() {
        val recommended = registry.recommendForSim(sim("KR"))
        val active = recommended.filter { it.requiresUserOptIn }
        // Active = GLOBAL 2 (abusech, phishtank) + KR 2 (KISA Phishing URL × 2) = 4
        assertEquals(4, active.size)
        assertTrue(active.any { it.id == "kisa_phishing_url_kr" })
        assertTrue(active.any { it.id == "kisa_phishing_url_recent_kr" })
        assertTrue(active.any { it.id == "abusech_urlhaus" })
        assertTrue(active.any { it.id == "phishtank" })
        // Hidden Telco / kisa_smishing 미포함
        assertTrue(active.none { it.type == FeedType.TelcoBlocklist })
        assertTrue(active.none { it.id == "kisa_smishing_kr" })
    }
}
