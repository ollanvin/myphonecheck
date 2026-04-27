package app.myphonecheck.mobile.core.globalengine.search

import app.myphonecheck.mobile.core.globalengine.search.internal.HistoryRepository
import app.myphonecheck.mobile.core.globalengine.search.internal.OnDeviceHistorySearch
import app.myphonecheck.mobile.core.globalengine.simcontext.SimContext
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Currency
import java.util.TimeZone

class OnDeviceHistorySearchTest {

    private fun simContext() = SimContext(
        mcc = "", mnc = "", countryIso = "KR", operatorName = "",
        currency = Currency.getInstance("KRW"),
        phoneRegion = "KR", timezone = TimeZone.getTimeZone("Asia/Seoul"),
    )

    private fun query(key: String, type: QueryType = QueryType.PHONE_NUMBER) =
        SearchQuery(key, type, SearchContext(simContext()))

    @Test
    fun `internal search returns repository matches and HIGH confidence`() = runTest {
        val repo = object : HistoryRepository {
            override suspend fun findByKey(key: String, type: QueryType): List<MatchEntry> =
                listOf(MatchEntry(key, "spam reported by user", null))
        }
        val search = OnDeviceHistorySearch(repo)

        val result = search.search(query("+821012345678"))

        assertEquals(SearchSource.INTERNAL, result.source)
        assertEquals(1, result.matches.size)
        assertEquals("+821012345678", result.matches[0].sourceId)
        assertEquals(SearchConfidence.HIGH, result.confidence)
    }

    @Test
    fun `empty repository yields empty matches but still INTERNAL HIGH`() = runTest {
        val repo = object : HistoryRepository {
            override suspend fun findByKey(key: String, type: QueryType) = emptyList<MatchEntry>()
        }
        val result = OnDeviceHistorySearch(repo).search(query("unknown"))
        assertTrue(result.matches.isEmpty())
        assertEquals(SearchConfidence.HIGH, result.confidence)
        assertEquals(SearchSource.INTERNAL, result.source)
    }
}
