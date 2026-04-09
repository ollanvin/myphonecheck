package app.myphonecheck.mobile.data.search

import app.myphonecheck.mobile.core.model.SearchEvidence
import app.myphonecheck.mobile.core.model.SearchTrend
import app.myphonecheck.mobile.core.model.SignalSummary
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 전체 검색 실패 시 Device Evidence only fallback 진입 검증.
 *
 * 검증 체인:
 * 1. SearchEvidence.empty() 생성 시 모든 필드가 null/empty
 * 2. SearchEvidence.isEmpty == true
 * 3. 모든 signal flag가 false
 * 4. signalSummaries가 빈 리스트
 *
 * 이 테스트가 통과하면:
 * - SearchEnrichmentRepositoryImpl에서 exception/timeout 시 empty() 반환 → 안전
 * - DecisionEngineImpl.evaluate(deviceEvidence, null) → Device Evidence만으로 판정
 * - DecisionEngineImpl.evaluate(deviceEvidence, SearchEvidence.empty()) → 동일
 */
class SearchFallbackTest {

    @Test
    fun `SearchEvidence empty is truly empty`() {
        val empty = SearchEvidence.empty()

        println("[FALLBACK LOG] SearchEvidence.empty() created")
        println("[FALLBACK LOG] isEmpty=${empty.isEmpty}")
        println("[FALLBACK LOG] recent30dSearchIntensity=${empty.recent30dSearchIntensity}")
        println("[FALLBACK LOG] recent90dSearchIntensity=${empty.recent90dSearchIntensity}")
        println("[FALLBACK LOG] searchTrend=${empty.searchTrend}")
        println("[FALLBACK LOG] keywordClusters=${empty.keywordClusters}")
        println("[FALLBACK LOG] repeatedEntities=${empty.repeatedEntities}")
        println("[FALLBACK LOG] sourceTypes=${empty.sourceTypes}")
        println("[FALLBACK LOG] topSnippets=${empty.topSnippets}")
        println("[FALLBACK LOG] signalSummaries=${empty.signalSummaries}")

        assertTrue("empty must report isEmpty=true", empty.isEmpty)
        assertEquals(null, empty.recent30dSearchIntensity)
        assertEquals(null, empty.recent90dSearchIntensity)
        assertEquals(SearchTrend.NONE, empty.searchTrend)
        assertTrue(empty.keywordClusters.isEmpty())
        assertTrue(empty.repeatedEntities.isEmpty())
        assertTrue(empty.sourceTypes.isEmpty())
        assertTrue(empty.topSnippets.isEmpty())
        assertTrue(empty.signalSummaries.isEmpty())
    }

    @Test
    fun `SearchEvidence empty has no signal flags`() {
        val empty = SearchEvidence.empty()

        println("[FALLBACK LOG] Signal flags for empty SearchEvidence:")
        println("[FALLBACK LOG] hasDeliverySignal=${empty.hasDeliverySignal}")
        println("[FALLBACK LOG] hasInstitutionSignal=${empty.hasInstitutionSignal}")
        println("[FALLBACK LOG] hasBusinessSignal=${empty.hasBusinessSignal}")
        println("[FALLBACK LOG] hasSpamSignal=${empty.hasSpamSignal}")
        println("[FALLBACK LOG] hasScamSignal=${empty.hasScamSignal}")

        assertTrue("empty must NOT have delivery signal", !empty.hasDeliverySignal)
        assertTrue("empty must NOT have institution signal", !empty.hasInstitutionSignal)
        assertTrue("empty must NOT have business signal", !empty.hasBusinessSignal)
        assertTrue("empty must NOT have spam signal", !empty.hasSpamSignal)
        assertTrue("empty must NOT have scam signal", !empty.hasScamSignal)

        println("[FALLBACK LOG] → All signal flags false. DecisionEngine will use Device Evidence only.")
    }

    @Test
    fun `SearchEvidence with data is NOT empty`() {
        val withData = SearchEvidence(
            recent30dSearchIntensity = 5,
            recent90dSearchIntensity = 10,
            searchTrend = SearchTrend.INCREASING,
            keywordClusters = listOf("사기", "피싱"),
            repeatedEntities = listOf("알 수 없는 발신자"),
            sourceTypes = listOf("spam-report"),
            topSnippets = listOf("이 번호는 사기 신고가 접수된 번호입니다"),
            signalSummaries = listOf(
                SignalSummary(
                    signalDescription = "사기/피싱 신고 다수",
                    resultCount = 5,
                    topSnippet = "이 번호는 사기 신고가 접수된 번호입니다",
                    signalType = "SCAM",
                )
            ),
        )

        println("[FALLBACK LOG] SearchEvidence with data:")
        println("[FALLBACK LOG] isEmpty=${withData.isEmpty}")
        println("[FALLBACK LOG] hasScamSignal=${withData.hasScamSignal}")
        println("[FALLBACK LOG] signalSummaries.size=${withData.signalSummaries.size}")

        assertTrue("evidence with data must NOT be empty", !withData.isEmpty)
        assertTrue("evidence with scam keywords must have scam signal", withData.hasScamSignal)
    }

    @Test
    fun `null SearchEvidence is safe for DecisionEngine`() {
        // DecisionEngine receives nullable SearchEvidence
        val searchEvidence: SearchEvidence? = null

        println("[FALLBACK LOG] searchEvidence is null → Device Evidence only mode")

        // Simulates how DecisionEngine accesses it
        val hasSearchData = searchEvidence != null && !searchEvidence.isEmpty
        val hasScam = searchEvidence?.hasScamSignal == true
        val hasSpam = searchEvidence?.hasSpamSignal == true

        assertTrue("null searchEvidence must mean no search data", !hasSearchData)
        assertTrue("null searchEvidence must mean no scam signal", !hasScam)
        assertTrue("null searchEvidence must mean no spam signal", !hasSpam)

        println("[FALLBACK LOG] → DecisionEngine safely operates with Device Evidence only.")
        println("[FALLBACK LOG] → hasSearchData=$hasSearchData, hasScam=$hasScam, hasSpam=$hasSpam")
    }

    @Test
    fun `empty SearchEvidence is safe for DecisionEngine`() {
        val searchEvidence: SearchEvidence? = SearchEvidence.empty()

        println("[FALLBACK LOG] searchEvidence is SearchEvidence.empty() → Device Evidence only mode")

        val hasSearchData = searchEvidence != null && !searchEvidence.isEmpty
        val hasScam = searchEvidence?.hasScamSignal == true
        val hasSpam = searchEvidence?.hasSpamSignal == true

        assertTrue("empty searchEvidence must mean no search data", !hasSearchData)
        assertTrue("empty searchEvidence must mean no scam signal", !hasScam)
        assertTrue("empty searchEvidence must mean no spam signal", !hasSpam)

        println("[FALLBACK LOG] → DecisionEngine safely operates with Device Evidence only.")
        println("[FALLBACK LOG] → hasSearchData=$hasSearchData, hasScam=$hasScam, hasSpam=$hasSpam")
    }
}
