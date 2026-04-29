package app.myphonecheck.mobile.core.globalengine.search

import app.myphonecheck.mobile.core.globalengine.search.external.CustomTabExternalSearch
import app.myphonecheck.mobile.core.globalengine.search.external.ExternalMode
import app.myphonecheck.mobile.core.globalengine.simcontext.SimContext
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Currency
import java.util.TimeZone

class CustomTabExternalSearchTest {

    private val external = CustomTabExternalSearch()

    private fun query(key: String, type: QueryType = QueryType.PHONE_NUMBER): SearchQuery {
        val sim = SimContext("", "", "KR", "", Currency.getInstance("KRW"), "KR", TimeZone.getTimeZone("UTC"))
        return SearchQuery(key, type, SearchContext(sim))
    }

    // ── ExternalMode URL 빌더 (v2.4.0 신규) ──────────────────────────

    @Test
    fun `GOOGLE_AI_MODE produces udm 50 URL`() {
        val url = external.buildUrl("01012345678", ExternalMode.GOOGLE_AI_MODE)
        assertEquals("https://www.google.com/search?q=01012345678&udm=50", url)
    }

    @Test
    fun `BING_COPILOT produces showconv 1 URL`() {
        val url = external.buildUrl("01012345678", ExternalMode.BING_COPILOT)
        assertEquals("https://www.bing.com/search?q=01012345678&showconv=1", url)
    }

    @Test
    fun `NAVER_CUE produces cue subdomain URL`() {
        val url = external.buildUrl("01012345678", ExternalMode.NAVER_CUE)
        assertTrue(url.startsWith("https://cue.search.naver.com"))
        assertTrue(url.contains("query=01012345678"))
    }

    @Test
    fun `GOOGLE_PLAIN omits udm parameter`() {
        val url = external.buildUrl("01012345678", ExternalMode.GOOGLE_PLAIN)
        assertEquals("https://www.google.com/search?q=01012345678", url)
        assertFalse(url.contains("udm"))
    }

    @Test
    fun `BING_PLAIN omits showconv parameter`() {
        val url = external.buildUrl("01012345678", ExternalMode.BING_PLAIN)
        assertEquals("https://www.bing.com/search?q=01012345678", url)
        assertFalse(url.contains("showconv"))
    }

    @Test
    fun `NAVER_PLAIN uses default search subdomain`() {
        val url = external.buildUrl("01012345678", ExternalMode.NAVER_PLAIN)
        assertTrue(url.startsWith("https://search.naver.com/search.naver"))
    }

    @Test
    fun `query with special chars properly URL-encoded`() {
        val url = external.buildUrl("+82 10-1234 5678", ExternalMode.GOOGLE_AI_MODE)
        assertTrue(url.contains("%2B82") || url.contains("%2B"))
        assertFalse(url.contains(" "))
    }

    @Test
    fun `default mode is GOOGLE_AI_MODE`() {
        val url = external.buildUrl("01012345678")
        assertEquals("https://www.google.com/search?q=01012345678&udm=50", url)
    }

    // ── 기존 buildIntent(SearchQuery) 호환 (default = GOOGLE_AI_MODE) ──

    @Test
    fun `buildIntent default mode produces udm 50 URL`() {
        val intent = external.buildIntent(query("+821012345678"))
        assertEquals("https://www.google.com/search?q=%2B821012345678&udm=50", intent.url)
    }

    @Test
    fun `buildIntent key with spaces encoded as plus`() {
        val intent = external.buildIntent(query("Bank Alert"))
        assertTrue(intent.url.endsWith("Bank+Alert&udm=50"))
    }

    @Test
    fun `buildIntent query type does not change URL structure`() {
        val phone = external.buildIntent(query("abc", QueryType.PHONE_NUMBER)).url
        val pkg = external.buildIntent(query("abc", QueryType.APP_PACKAGE)).url
        assertEquals(phone, pkg)
    }

    @Test
    fun `buildIntent URL prefix uses Google search base`() {
        val intent = external.buildIntent(query("x"))
        assertTrue(intent.url.startsWith("https://www.google.com/search?q="))
    }

    @Test
    fun `buildIntent with explicit BING_COPILOT mode`() {
        val intent = external.buildIntent(query("xyz"), ExternalMode.BING_COPILOT)
        assertTrue(intent.url.startsWith("https://www.bing.com/search?q="))
        assertTrue(intent.url.contains("showconv=1"))
    }
}
