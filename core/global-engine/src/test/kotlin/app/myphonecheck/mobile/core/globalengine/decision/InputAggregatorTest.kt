package app.myphonecheck.mobile.core.globalengine.decision

import app.myphonecheck.mobile.core.globalengine.search.MatchEntry
import app.myphonecheck.mobile.core.globalengine.search.QueryType
import app.myphonecheck.mobile.core.globalengine.search.SearchConfidence
import app.myphonecheck.mobile.core.globalengine.search.SearchContext
import app.myphonecheck.mobile.core.globalengine.search.SearchQuery
import app.myphonecheck.mobile.core.globalengine.search.SearchSource
import app.myphonecheck.mobile.core.globalengine.search.Severity
import app.myphonecheck.mobile.core.globalengine.search.internal.HistoryRepository
import app.myphonecheck.mobile.core.globalengine.search.internal.OnDeviceHistorySearch
import app.myphonecheck.mobile.core.globalengine.search.publicfeed.FeedOptInProvider
import app.myphonecheck.mobile.core.globalengine.search.publicfeed.FeedRegistry
import app.myphonecheck.mobile.core.globalengine.search.publicfeed.PublicFeedAggregator
import app.myphonecheck.mobile.core.globalengine.search.publicfeed.PublicFeedCache
import app.myphonecheck.mobile.core.globalengine.simcontext.SimContext
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
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

    private fun optInProvider(ids: Set<String>): FeedOptInProvider =
        object : FeedOptInProvider {
            override suspend fun optedInIds(): Set<String> = ids
        }

    @Test
    fun `aggregate returns 4-axis with externalAi and competitorReverse null`() = runTest {
        val internal = OnDeviceHistorySearch(FixedRepo(listOf(MatchEntry("k", "internal hit", null))))
        val cache = PublicFeedCache()
        cache.put("phishtank", "k", listOf(MatchEntry("k", "feed hit", Severity.HIGH)))
        val publicFeed = PublicFeedAggregator(
            cache,
            FeedRegistry(),
            optInProvider(setOf("phishtank")),
        )

        val agg = InputAggregator(internal, publicFeed).aggregate(query("k"))

        // 축 1 internalNkb
        assertNotNull(agg.internalNkb)
        assertEquals(SearchSource.INTERNAL, agg.internalNkb!!.source)
        assertEquals(1, agg.internalNkb!!.matches.size)
        assertEquals(SearchConfidence.HIGH, agg.internalNkb!!.confidence)

        // 축 2 publicAuthority
        assertNotNull(agg.publicAuthority)
        assertEquals(SearchSource.PUBLIC_FEED, agg.publicAuthority!!.source)
        assertEquals(1, agg.publicAuthority!!.matches.size)
        assertEquals(Severity.HIGH, agg.publicAuthority!!.matches[0].severity)

        // 축 3·4 — 헌법 §1 정합으로 비-call (null)
        assertNull(agg.externalAi)
        assertNull(agg.competitorReverse)

        // 메타
        assertEquals("k", agg.query.key)
        assertTrue(agg.timestamp > 0L)
    }

    @Test
    fun `aggregate without opted-in feeds yields empty public matches`() = runTest {
        val internal = OnDeviceHistorySearch(FixedRepo(emptyList()))
        val publicFeed = PublicFeedAggregator(
            PublicFeedCache(),
            FeedRegistry(),
            optInProvider(emptySet()),
        )

        val agg = InputAggregator(internal, publicFeed).aggregate(query("nothing"))

        assertTrue(agg.internalNkb!!.matches.isEmpty())
        assertTrue(agg.publicAuthority!!.matches.isEmpty())
        assertNull(agg.externalAi)
        assertNull(agg.competitorReverse)
    }

    @Test
    fun `aggregate axes 3 and 4 always null (헌법 1 사용자 직접 진입)`() = runTest {
        val internal = OnDeviceHistorySearch(FixedRepo(emptyList()))
        val publicFeed = PublicFeedAggregator(
            PublicFeedCache(),
            FeedRegistry(),
            optInProvider(emptySet()),
        )

        val agg = InputAggregator(internal, publicFeed).aggregate(query("hello world"))

        // 축 3, 4 = Custom Tab 사용자 직접 진입 외 비-call
        assertNull(agg.externalAi)
        assertNull(agg.competitorReverse)
    }

    @Test
    fun `appendUserTaggedExternalResult is callable interface stub`() = runTest {
        val internal = OnDeviceHistorySearch(FixedRepo(emptyList()))
        val publicFeed = PublicFeedAggregator(
            PublicFeedCache(),
            FeedRegistry(),
            optInProvider(emptySet()),
        )
        val aggregator = InputAggregator(internal, publicFeed)

        // 본 메서드는 stub (Stage 3-003에서 NKB DAO 통합). 호출 가능 검증만.
        aggregator.appendUserTaggedExternalResult(
            query = query("01012345678"),
            source = ExternalSource.EXTERNAL_AI,
            signal = 0.7f,
            snippet = "user tagged from AI search",
        )
        aggregator.appendUserTaggedExternalResult(
            query = query("01012345678"),
            source = ExternalSource.COMPETITOR_REVERSE_TRUECALLER,
            signal = 0.5f,
            snippet = "user tagged from Truecaller",
        )
    }
}
