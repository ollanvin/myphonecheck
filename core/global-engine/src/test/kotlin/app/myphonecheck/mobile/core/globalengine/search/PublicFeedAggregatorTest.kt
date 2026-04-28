package app.myphonecheck.mobile.core.globalengine.search

import app.myphonecheck.mobile.core.globalengine.search.publicfeed.FeedOptInProvider
import app.myphonecheck.mobile.core.globalengine.search.publicfeed.FeedRegistry
import app.myphonecheck.mobile.core.globalengine.search.publicfeed.PublicFeedAggregator
import app.myphonecheck.mobile.core.globalengine.search.publicfeed.PublicFeedCache
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

    private fun optInProvider(ids: Set<String>): FeedOptInProvider = object : FeedOptInProvider {
        override suspend fun optedInIds(): Set<String> = ids
    }

    @Test
    fun `empty optedIn returns empty matches and PUBLIC_FEED MEDIUM`() = runTest {
        val agg = PublicFeedAggregator(
            cache = PublicFeedCache(),
            registry = FeedRegistry(),
            optInProvider = optInProvider(emptySet()),
        )
        val result = agg.search(query("anything"))
        assertEquals(SearchSource.PUBLIC_FEED, result.source)
        assertTrue(result.matches.isEmpty())
        assertEquals(SearchConfidence.MEDIUM, result.confidence)
    }

    @Test
    fun `opted-in source contributes cached matches when registered`() = runTest {
        val cache = PublicFeedCache()
        cache.put("phishtank", "+821012345678", listOf(MatchEntry("+821012345678", "phish", Severity.HIGH)))
        val agg = PublicFeedAggregator(
            cache = cache,
            registry = FeedRegistry(),  // FeedRegistry 기본 등록 출처에 phishtank 포함
            optInProvider = optInProvider(setOf("phishtank")),
        )
        val result = agg.search(query("+821012345678"))
        assertEquals(SearchSource.PUBLIC_FEED, result.source)
        assertEquals(1, result.matches.size)
        assertEquals(Severity.HIGH, result.matches[0].severity)
    }

    @Test
    fun `opted-in source not in registry returns empty (defensive)`() = runTest {
        val cache = PublicFeedCache()
        cache.put("unknown_id", "x", listOf(MatchEntry("x", "ghost", Severity.LOW)))
        val agg = PublicFeedAggregator(
            cache = cache,
            registry = FeedRegistry(),
            optInProvider = optInProvider(setOf("unknown_id")),
        )
        val result = agg.search(query("x"))
        // unknown_id는 registry.all()에 없으므로 매칭 안 됨.
        assertTrue(result.matches.isEmpty())
    }

    @Test
    fun `multiple opted-in sources from registry merged`() = runTest {
        val cache = PublicFeedCache()
        cache.put("abusech_urlhaus", "x", listOf(MatchEntry("x", "abuse", Severity.MEDIUM)))
        cache.put("phishtank", "x", listOf(MatchEntry("x", "phish", Severity.HIGH)))
        val agg = PublicFeedAggregator(
            cache = cache,
            registry = FeedRegistry(),
            optInProvider = optInProvider(setOf("abusech_urlhaus", "phishtank")),
        )
        val result = agg.search(query("x"))
        assertEquals(2, result.matches.size)
    }
}
