package app.myphonecheck.mobile.core.globalengine.search

import app.myphonecheck.mobile.core.globalengine.search.publicfeed.PublicFeedAggregator
import app.myphonecheck.mobile.core.globalengine.search.publicfeed.PublicFeedCache
import app.myphonecheck.mobile.core.globalengine.search.publicfeed.PublicFeedSource
import app.myphonecheck.mobile.core.globalengine.simcontext.SimContext
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Currency
import java.util.TimeZone

class PublicFeedAggregatorTest {

    private fun query(key: String): SearchQuery {
        val sim = SimContext("", "", "KR", "", Currency.getInstance("KRW"), "KR", TimeZone.getTimeZone("UTC"))
        return SearchQuery(key, QueryType.PHONE_NUMBER, SearchContext(sim))
    }

    private class StubSource(
        override val id: String,
        private val optedIn: Boolean,
    ) : PublicFeedSource {
        override val name: String = "stub-$id"
        override suspend fun isOptedIn(): Boolean = optedIn
        override suspend fun lookup(query: SearchQuery): List<MatchEntry> = emptyList()
    }

    @Test
    fun `opted-in source contributes cached matches`() = runTest {
        val cache = PublicFeedCache()
        cache.put("kisa", "+821012345678", listOf(MatchEntry("+821012345678", "스미싱 신고", Severity.HIGH)))
        val agg = PublicFeedAggregator(cache, listOf(StubSource("kisa", true)))

        val result = agg.search(query("+821012345678"))
        assertEquals(SearchSource.PUBLIC_FEED, result.source)
        assertEquals(1, result.matches.size)
        assertEquals(Severity.HIGH, result.matches[0].severity)
        assertEquals(SearchConfidence.MEDIUM, result.confidence)
    }

    @Test
    fun `opted-out source skipped`() = runTest {
        val cache = PublicFeedCache()
        cache.put("kisa", "+821012345678", listOf(MatchEntry("+821012345678", "stale", Severity.LOW)))
        val agg = PublicFeedAggregator(cache, listOf(StubSource("kisa", false)))

        val result = agg.search(query("+821012345678"))
        assertTrue(result.matches.isEmpty())
    }

    @Test
    fun `multiple opted-in sources merged`() = runTest {
        val cache = PublicFeedCache()
        cache.put("kisa", "x", listOf(MatchEntry("x", "kisa hit", Severity.MEDIUM)))
        cache.put("abuse", "x", listOf(MatchEntry("x", "abuse hit", Severity.HIGH)))
        val agg = PublicFeedAggregator(
            cache,
            listOf(StubSource("kisa", true), StubSource("abuse", true)),
        )
        val result = agg.search(query("x"))
        assertEquals(2, result.matches.size)
    }

    @Test
    fun `no sources yields empty PUBLIC_FEED MEDIUM`() = runTest {
        val agg = PublicFeedAggregator(PublicFeedCache(), emptyList())
        val result = agg.search(query("anything"))
        assertEquals(SearchSource.PUBLIC_FEED, result.source)
        assertTrue(result.matches.isEmpty())
        assertEquals(SearchConfidence.MEDIUM, result.confidence)
    }
}
