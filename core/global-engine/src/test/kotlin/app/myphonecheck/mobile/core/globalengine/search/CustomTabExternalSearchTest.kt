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
    private val sim = SimContext("", "", "KR", "", Currency.getInstance("KRW"), "KR", TimeZone.getTimeZone("UTC"))

    private fun phoneInput(value: String): SearchInput.PhoneNumber =
        SearchInput.PhoneNumber(value, sim)

    // ── string query 호환 (Stage 3-001 시그니처) ──────────────────

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
    fun `NAVER_AI produces ai 1 query parameter`() {
        val url = external.buildUrl("01012345678", ExternalMode.NAVER_AI)
        assertTrue(url.startsWith("https://m.search.naver.com"))
        assertTrue(url.contains("ai=1"))
    }

    @Test
    fun `YAHOO_JAPAN_AI produces yahoo co jp URL`() {
        val url = external.buildUrl("01012345678", ExternalMode.YAHOO_JAPAN_AI)
        assertTrue(url.startsWith("https://search.yahoo.co.jp"))
    }

    @Test
    fun `BAIDU_AI produces baidu URL`() {
        val url = external.buildUrl("01012345678", ExternalMode.BAIDU_AI)
        assertTrue(url.startsWith("https://www.baidu.com"))
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
        assertFalse(url.contains("showconv"))
    }

    @Test
    fun `NAVER_PLAIN omits ai parameter`() {
        val url = external.buildUrl("01012345678", ExternalMode.NAVER_PLAIN)
        assertFalse(url.contains("ai="))
    }

    @Test
    fun `default mode is GOOGLE_AI_MODE`() {
        val url = external.buildUrl("01012345678")
        assertEquals("https://www.google.com/search?q=01012345678&udm=50", url)
    }

    @Test
    fun `query with special chars properly URL-encoded`() {
        val url = external.buildUrl("+82 10-1234 5678", ExternalMode.GOOGLE_AI_MODE)
        assertTrue(url.contains("%2B"))
        assertFalse(url.contains(" "))
    }

    // ── SearchInput 시그니처 (v2.5.0 신규) ──────────────────

    @Test
    fun `buildUrl with PhoneNumber input + GOOGLE_AI_MODE`() {
        val url = external.buildUrl(phoneInput("01012345678"))
        assertEquals("https://www.google.com/search?q=01012345678&udm=50", url)
    }

    @Test
    fun `buildUrl with Url input encodes URL value`() {
        val input = SearchInput.Url("https://phishing.example.com", "MESSAGE")
        val url = external.buildUrl(input, ExternalMode.GOOGLE_AI_MODE)
        assertTrue(url.contains("phishing.example.com"))
    }

    @Test
    fun `buildUrl with AppPackage input includes security CVE keywords`() {
        val input = SearchInput.AppPackage("com.example.suspicious")
        val url = external.buildUrl(input, ExternalMode.GOOGLE_AI_MODE)
        // toAiSearchQuery returns "com.example.suspicious security CVE" → URL-encoded
        assertTrue(url.contains("CVE"))
        assertTrue(url.contains("com.example.suspicious"))
    }

    @Test
    fun `buildUrl with MessageBody prefers extracted URL`() {
        val input = SearchInput.MessageBody(
            text = "click here",
            extractedUrls = listOf("https://phishing.example.com"),
            extractedNumbers = emptyList(),
        )
        val url = external.buildUrl(input, ExternalMode.GOOGLE_AI_MODE)
        assertTrue(url.contains("phishing.example.com"))
    }

    @Test
    fun `buildIntent with PhoneNumber input produces URL`() {
        val intent = external.buildIntent(phoneInput("01012345678"))
        assertEquals("https://www.google.com/search?q=01012345678&udm=50", intent.url)
    }

    @Test
    fun `buildIntent with explicit BING_COPILOT mode`() {
        val intent = external.buildIntent(phoneInput("01012345678"), ExternalMode.BING_COPILOT)
        assertTrue(intent.url.startsWith("https://www.bing.com/search?q="))
        assertTrue(intent.url.contains("showconv=1"))
    }
}
