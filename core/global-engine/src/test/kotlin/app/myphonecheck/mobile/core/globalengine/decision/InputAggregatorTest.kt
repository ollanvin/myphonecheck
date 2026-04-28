package app.myphonecheck.mobile.core.globalengine.decision

import app.myphonecheck.mobile.core.globalengine.search.MatchEntry
import app.myphonecheck.mobile.core.globalengine.search.QueryType
import app.myphonecheck.mobile.core.globalengine.search.SearchConfidence
import app.myphonecheck.mobile.core.globalengine.search.SearchContext
import app.myphonecheck.mobile.core.globalengine.search.SearchQuery
import app.myphonecheck.mobile.core.globalengine.search.SearchSource
import app.myphonecheck.mobile.core.globalengine.search.Severity
import app.myphonecheck.mobile.core.globalengine.search.external.CustomTabExternalSearch
import app.myphonecheck.mobile.core.globalengine.search.internal.HistoryRepository
import app.myphonecheck.mobile.core.globalengine.search.internal.OnDeviceHistorySearch
import app.myphonecheck.mobile.core.globalengine.search.publicfeed.PublicFeedAggregator
import app.myphonecheck.mobile.core.globalengine.search.publicfeed.PublicFeedCache
import app.myphonecheck.mobile.core.globalengine.simcontext.SimContext
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Currency
import java.util.TimeZone

class InputAggregatorTest {

    private fun query(key: String): SearchQuery {
        val sim = SimContext("", "", "KR", "", Currency.getInstance("KRW"), "KR", TimeZone.getTimeZone("UTC"))
        return SearchQuery(key, QueryType.PHONE_NUMBER, SearchContext(sim))
    }

    private class FixedRepo(private val items: List<MatchEntry>) : HistoryRepository {
        override suspend fun findByKey(key: String, type: QueryType): List<MatchEntry> = items
    }

    private fun optInProvider(ids: Set<String>): app.myphonecheck.mobile.core.globalengine.search.publicfeed.FeedOptInProvider =
        object : app.myphonecheck.mobile.core.globalengine.search.publicfeed.FeedOptInProvider {
            override suspend fun optedInIds(): Set<String> = ids
        }

    @Test
    fun `aggregate returns all three axes`() = runTest {
        val internal = OnDeviceHistorySearch(FixedRepo(listOf(MatchEntry("k", "internal hit", null))))
        val cache = PublicFeedCache()
        // PR #29 FeedRegistry에 등록된 phishtank id 사용 (default registry).
        cache.put("phishtank", "k", listOf(MatchEntry("k", "feed hit", Severity.HIGH)))
        val publicFeed = PublicFeedAggregator(
            cache,
            app.myphonecheck.mobile.core.globalengine.search.publicfeed.FeedRegistry(),
            optInProvider(setOf("phishtank")),
        )
        val external = CustomTabExternalSearch()

        val agg = InputAggregator(internal, publicFeed, external).aggregate(query("k"))

        assertEquals(SearchSource.INTERNAL, agg.internal.source)
        assertEquals(1, agg.internal.matches.size)
        assertEquals(SearchConfidence.HIGH, agg.internal.confidence)

        assertEquals(SearchSource.PUBLIC_FEED, agg.publicFeed.source)
        assertEquals(1, agg.publicFeed.matches.size)
        assertEquals(Severity.HIGH, agg.publicFeed.matches[0].severity)

        assertTrue(agg.externalSearchIntent.url.startsWith("https://www.google.com/search?q="))
        assertTrue(agg.externalSearchIntent.url.endsWith("k"))
    }

    @Test
    fun `aggregate without opted-in feeds yields empty public matches`() = runTest {
        val internal = OnDeviceHistorySearch(FixedRepo(emptyList()))
        val publicFeed = PublicFeedAggregator(
            PublicFeedCache(),
            app.myphonecheck.mobile.core.globalengine.search.publicfeed.FeedRegistry(),
            optInProvider(emptySet()),
        )
        val external = CustomTabExternalSearch()

        val agg = InputAggregator(internal, publicFeed, external).aggregate(query("nothing"))

        assertTrue(agg.internal.matches.isEmpty())
        assertTrue(agg.publicFeed.matches.isEmpty())
        assertTrue(agg.externalSearchIntent.url.isNotEmpty())
    }

    @Test
    fun `external intent always built regardless of internal results`() = runTest {
        val internal = OnDeviceHistorySearch(FixedRepo(emptyList()))
        val publicFeed = PublicFeedAggregator(
            PublicFeedCache(),
            app.myphonecheck.mobile.core.globalengine.search.publicfeed.FeedRegistry(),
            optInProvider(emptySet()),
        )
        val external = CustomTabExternalSearch()

        val agg = InputAggregator(internal, publicFeed, external).aggregate(query("hello world"))

        assertTrue(agg.externalSearchIntent.url.contains("hello"))
    }
}
