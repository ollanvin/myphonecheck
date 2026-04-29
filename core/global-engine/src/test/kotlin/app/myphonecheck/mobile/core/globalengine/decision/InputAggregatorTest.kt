package app.myphonecheck.mobile.core.globalengine.decision

import app.myphonecheck.mobile.core.globalengine.search.MatchEntry
import app.myphonecheck.mobile.core.globalengine.search.QueryType
import app.myphonecheck.mobile.core.globalengine.search.SearchConfidence
import app.myphonecheck.mobile.core.globalengine.search.SearchInput
import app.myphonecheck.mobile.core.globalengine.search.SearchSource
import app.myphonecheck.mobile.core.globalengine.search.internal.HistoryRepository
import app.myphonecheck.mobile.core.globalengine.search.internal.OnDeviceHistorySearch
import app.myphonecheck.mobile.core.globalengine.simcontext.SimContext
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Currency
import java.util.TimeZone

class InputAggregatorTest {

    private val sim = SimContext("", "", "KR", "", Currency.getInstance("KRW"), "KR", TimeZone.getTimeZone("UTC"))

    private fun phoneInput(value: String): SearchInput.PhoneNumber =
        SearchInput.PhoneNumber(value, sim)

    private class FixedRepo(private val items: List<MatchEntry>) : HistoryRepository {
        override suspend fun findByKey(key: String, type: QueryType): List<MatchEntry> = items
    }

    @Test
    fun `aggregate returns 2-axis with externalAi null (헌법 1 정합)`() = runTest {
        val onDevice = OnDeviceHistorySearch(FixedRepo(listOf(MatchEntry("k", "internal hit", null))))
        val agg = InputAggregator(onDevice).aggregate(phoneInput("k"))

        // 축 1 internalNkb
        assertNotNull(agg.internalNkb)
        assertEquals(SearchSource.INTERNAL, agg.internalNkb!!.source)
        assertEquals(1, agg.internalNkb!!.matches.size)
        assertEquals(SearchConfidence.HIGH, agg.internalNkb!!.confidence)

        // 축 2 externalAi — Custom Tab 사용자 직접 진입 외 비-call (null)
        assertNull(agg.externalAi)

        // input + 메타
        assertEquals("k", (agg.input as SearchInput.PhoneNumber).value)
        assert(agg.timestamp > 0L)
    }

    @Test
    fun `aggregate without history yields empty internal matches`() = runTest {
        val onDevice = OnDeviceHistorySearch(FixedRepo(emptyList()))
        val agg = InputAggregator(onDevice).aggregate(phoneInput("nothing"))

        assert(agg.internalNkb!!.matches.isEmpty())
        assertNull(agg.externalAi)
    }

    @Test
    fun `aggregate accepts all SearchInput types`() = runTest {
        val onDevice = OnDeviceHistorySearch(FixedRepo(emptyList()))
        val aggregator = InputAggregator(onDevice)

        // 4 input types 모두 호출 가능
        val phone = aggregator.aggregate(SearchInput.PhoneNumber("01012345678", sim))
        val url = aggregator.aggregate(SearchInput.Url("https://example.com", "CALL"))
        val msg = aggregator.aggregate(SearchInput.MessageBody("text", emptyList(), emptyList()))
        val pkg = aggregator.aggregate(SearchInput.AppPackage("com.example.app"))

        // 모두 externalAi=null
        assertNull(phone.externalAi)
        assertNull(url.externalAi)
        assertNull(msg.externalAi)
        assertNull(pkg.externalAi)
    }

    @Test
    fun `appendUserTaggedExternalResult callable for all input types`() = runTest {
        val onDevice = OnDeviceHistorySearch(FixedRepo(emptyList()))
        val aggregator = InputAggregator(onDevice)

        // stub — Stage 3-003-REV에서 NKB DAO 통합. 현재는 호출 가능 검증만.
        aggregator.appendUserTaggedExternalResult(
            input = phoneInput("01012345678"),
            provider = "Google AI Mode",
            signal = 0.7f,
            snippet = "user tagged from AI search",
        )
        aggregator.appendUserTaggedExternalResult(
            input = SearchInput.AppPackage("com.example.app"),
            provider = "Bing Copilot",
            signal = 0.5f,
            snippet = "user tagged from Bing",
        )
    }
}
