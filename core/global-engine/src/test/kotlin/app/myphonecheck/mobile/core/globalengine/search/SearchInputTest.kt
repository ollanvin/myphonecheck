package app.myphonecheck.mobile.core.globalengine.search

import app.myphonecheck.mobile.core.globalengine.simcontext.SimContext
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Currency
import java.util.TimeZone

class SearchInputTest {

    private val sim = SimContext("", "", "KR", "", Currency.getInstance("KRW"), "KR", TimeZone.getTimeZone("UTC"))

    @Test
    fun `PhoneNumber toAiSearchQuery returns value`() {
        val input = SearchInput.PhoneNumber("01012345678", sim)
        assertEquals("01012345678", input.toAiSearchQuery())
    }

    @Test
    fun `Url toAiSearchQuery returns value`() {
        val input = SearchInput.Url("https://phishing.example.com", "MESSAGE")
        assertEquals("https://phishing.example.com", input.toAiSearchQuery())
    }

    @Test
    fun `MessageBody toAiSearchQuery prefers extracted URL`() {
        val input = SearchInput.MessageBody(
            text = "click here",
            extractedUrls = listOf("https://phishing.example.com"),
            extractedNumbers = emptyList(),
        )
        assertEquals("https://phishing.example.com", input.toAiSearchQuery())
    }

    @Test
    fun `MessageBody toAiSearchQuery falls back to extractedNumber`() {
        val input = SearchInput.MessageBody(
            text = "call me",
            extractedUrls = emptyList(),
            extractedNumbers = listOf("01098765432"),
        )
        assertEquals("01098765432", input.toAiSearchQuery())
    }

    @Test
    fun `MessageBody toAiSearchQuery falls back to text head when no extracts`() {
        val input = SearchInput.MessageBody(
            text = "hello world".repeat(20),
            extractedUrls = emptyList(),
            extractedNumbers = emptyList(),
        )
        assertEquals(100, input.toAiSearchQuery().length)
    }

    @Test
    fun `AppPackage toAiSearchQuery includes security CVE keyword`() {
        val input = SearchInput.AppPackage("com.example.suspicious")
        assertEquals("com.example.suspicious security CVE", input.toAiSearchQuery())
    }

    @Test
    fun `all SearchInput types have timestamp`() {
        val phone = SearchInput.PhoneNumber("x", sim)
        val url = SearchInput.Url("x", "CALL")
        val msg = SearchInput.MessageBody("x", emptyList(), emptyList())
        val pkg = SearchInput.AppPackage("x")
        assertTrue(phone.timestamp > 0L)
        assertTrue(url.timestamp > 0L)
        assertTrue(msg.timestamp > 0L)
        assertTrue(pkg.timestamp > 0L)
    }
}
