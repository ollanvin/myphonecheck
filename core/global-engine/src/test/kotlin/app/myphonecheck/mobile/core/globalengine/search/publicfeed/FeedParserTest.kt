package app.myphonecheck.mobile.core.globalengine.search.publicfeed

import app.myphonecheck.mobile.core.globalengine.search.publicfeed.download.FeedParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FeedParserTest {

    private val parser = FeedParser()

    @Test
    fun `CSV produces entries with first column as key`() {
        val raw = """
            +821012345678,KISA reported
            +12025550181,US phishing
            # comment ignored
        """.trimIndent()
        val entries = parser.parse(raw, FeedFormat.CSV, FeedDataType.PHONE_NUMBER)
        assertEquals(2, entries.size)
        assertEquals("+821012345678", entries[0].sourceId)
        assertEquals("KISA reported", entries[0].description)
        assertEquals("+12025550181", entries[1].sourceId)
    }

    @Test
    fun `CSV strips quotes and ignores empty lines`() {
        val raw = "\"010-1234\",\"with quotes\"\n\n\"abc\""
        val entries = parser.parse(raw, FeedFormat.CSV, FeedDataType.PHONE_NUMBER)
        assertEquals(2, entries.size)
        assertEquals("010-1234", entries[0].sourceId)
        assertEquals("with quotes", entries[0].description)
    }

    @Test
    fun `JSON_ARRAY with key field produces entries`() {
        val raw = """[{"key":"k1","description":"d1"},{"key":"k2","description":"d2"}]"""
        val entries = parser.parse(raw, FeedFormat.JSON_ARRAY, FeedDataType.PHONE_NUMBER)
        assertEquals(2, entries.size)
        assertEquals("k1", entries[0].sourceId)
        assertEquals("d2", entries[1].description)
    }

    @Test
    fun `JSON_ARRAY with url field falls back`() {
        val raw = """[{"url":"https://phish.example/abc","target":"bank"}]"""
        val entries = parser.parse(raw, FeedFormat.JSON_ARRAY, FeedDataType.PHISHING_URL)
        assertEquals(1, entries.size)
        assertEquals("https://phish.example/abc", entries[0].sourceId)
        assertEquals("bank", entries[0].description)
    }

    @Test
    fun `Malformed JSON returns empty`() {
        val raw = "not-a-json"
        val entries = parser.parse(raw, FeedFormat.JSON_ARRAY, FeedDataType.PHONE_NUMBER)
        assertTrue(entries.isEmpty())
    }

    @Test
    fun `RSS and XML formats return empty (post-PR)`() {
        assertTrue(parser.parse("<rss/>", FeedFormat.RSS, FeedDataType.THREAT_DESCRIPTION).isEmpty())
        assertTrue(parser.parse("<x/>", FeedFormat.XML, FeedDataType.THREAT_DESCRIPTION).isEmpty())
    }

    @Test
    fun `PLAIN_TEXT returns one entry per non-comment line`() {
        val raw = "+821011112222\n# comment\n+12025550100\n\n"
        val entries = parser.parse(raw, FeedFormat.PLAIN_TEXT, FeedDataType.PHONE_NUMBER)
        assertEquals(2, entries.size)
        assertEquals("+821011112222", entries[0].sourceId)
        assertEquals("+12025550100", entries[1].sourceId)
    }

    @Test
    fun `JSON_LINES parses each line independently`() {
        val raw = "{\"key\":\"a\"}\n{\"key\":\"b\",\"description\":\"hit\"}"
        val entries = parser.parse(raw, FeedFormat.JSON_LINES, FeedDataType.PHISHING_URL)
        assertEquals(2, entries.size)
        assertEquals("a", entries[0].sourceId)
        assertEquals("b", entries[1].sourceId)
        assertEquals("hit", entries[1].description)
    }
}
