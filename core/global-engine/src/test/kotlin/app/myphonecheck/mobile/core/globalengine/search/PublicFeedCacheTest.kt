package app.myphonecheck.mobile.core.globalengine.search

import app.myphonecheck.mobile.core.globalengine.search.publicfeed.PublicFeedCache
import app.myphonecheck.mobile.core.globalengine.simcontext.SimContext
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Currency
import java.util.TimeZone

class PublicFeedCacheTest {

    private fun query(key: String): SearchQuery {
        val sim = SimContext("", "", "KR", "", Currency.getInstance("KRW"), "KR", TimeZone.getTimeZone("UTC"))
        return SearchQuery(key, QueryType.PHONE_NUMBER, SearchContext(sim))
    }

    @Test
    fun `lookup returns put entries`() {
        val cache = PublicFeedCache()
        val entry = MatchEntry("+82100000", "spam", Severity.MEDIUM)
        cache.put("kisa", "+82100000", listOf(entry))
        assertEquals(listOf(entry), cache.lookup("kisa", query("+82100000")))
    }

    @Test
    fun `different source id isolates entries`() {
        val cache = PublicFeedCache()
        cache.put("kisa", "x", listOf(MatchEntry("x", "kisa", null)))
        assertTrue(cache.lookup("abuse", query("x")).isEmpty())
    }

    @Test
    fun `clear empties cache`() {
        val cache = PublicFeedCache()
        cache.put("kisa", "x", listOf(MatchEntry("x", "k", null)))
        cache.clear()
        assertTrue(cache.lookup("kisa", query("x")).isEmpty())
    }
}
