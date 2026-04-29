package app.myphonecheck.mobile.feature.decisionengine

import app.myphonecheck.core.common.risk.RiskTier
import app.myphonecheck.mobile.core.globalengine.decision.AggregatedInput
import app.myphonecheck.mobile.core.globalengine.search.MatchEntry
import app.myphonecheck.mobile.core.globalengine.search.SearchConfidence
import app.myphonecheck.mobile.core.globalengine.search.SearchInput
import app.myphonecheck.mobile.core.globalengine.search.SearchResult
import app.myphonecheck.mobile.core.globalengine.search.SearchSource
import app.myphonecheck.mobile.core.globalengine.search.Severity
import app.myphonecheck.mobile.core.globalengine.simcontext.SimContext
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Currency
import java.util.TimeZone

class DecisionEngine2AxisTest {

    private val sim = SimContext("", "", "KR", "", Currency.getInstance("KRW"), "KR", TimeZone.getTimeZone("UTC"))

    private val engine = DecisionEngineImpl(
        riskBadgeMapper = RiskBadgeMapper(),
        actionMapper = ActionMapper(),
        summaryGenerator = SummaryGenerator(),
    )

    private fun phoneInput(): SearchInput.PhoneNumber = SearchInput.PhoneNumber("01012345678", sim)

    private fun aggregatedDanger(): AggregatedInput = AggregatedInput(
        internalNkb = SearchResult(
            source = SearchSource.INTERNAL,
            matches = listOf(MatchEntry("k", "user blocked", Severity.CRITICAL)),
            confidence = SearchConfidence.HIGH,
        ),
        externalAi = SearchResult(
            source = SearchSource.EXTERNAL,
            matches = listOf(MatchEntry("k", "AI scam report", Severity.HIGH)),
            confidence = SearchConfidence.HIGH,
        ),
        input = phoneInput(),
        timestamp = 0L,
    )

    private fun aggregatedEmpty(): AggregatedInput = AggregatedInput(
        internalNkb = null,
        externalAi = null,
        input = phoneInput(),
        timestamp = 0L,
    )

    @Test
    fun `aggregatedInput null falls back to legacy decision (regression)`() {
        val input = DecisionInput()  // 모두 null
        val result = engine.evaluate(input)
        // legacy path: 모든 evidence null → INSUFFICIENT_EVIDENCE / UNKNOWN → Tier.Unknown
        assertEquals(RiskTier.Unknown, result.tier)
        assertEquals(0f, result.score, 0.001f)
    }

    @Test
    fun `Both axes empty produces Unknown tier`() {
        val input = DecisionInput(aggregatedInput = aggregatedEmpty())
        val result = engine.evaluate(input)
        assertEquals(RiskTier.Unknown, result.tier)
        assertEquals(0f, result.score, 0.001f)
    }

    @Test
    fun `Both axes with strong danger signals produce Danger tier`() {
        val input = DecisionInput(aggregatedInput = aggregatedDanger())
        val result = engine.evaluate(input)
        // NKB: 0.40 * HIGH(1.0) * CRITICAL(1.0) = 0.40
        // AI:  0.60 * HIGH(1.0) * HIGH(0.7)    = 0.42
        // total score = 0.82 → Danger
        assertEquals(RiskTier.Danger, result.tier)
        assertEquals(0.82f, result.score, 0.01f)
    }

    @Test
    fun `AI hit only with HIGH severity elevates to Caution`() {
        val aggregated = AggregatedInput(
            internalNkb = null,
            externalAi = SearchResult(
                source = SearchSource.EXTERNAL,
                matches = listOf(MatchEntry("k", "AI hit", Severity.HIGH)),
                confidence = SearchConfidence.HIGH,
            ),
            input = phoneInput(),
            timestamp = 0L,
        )
        val input = DecisionInput(aggregatedInput = aggregated)
        val result = engine.evaluate(input)
        // 0.60 * 1.0 * 0.7 = 0.42 → Caution (>= 0.2 < 0.5)
        assertEquals(RiskTier.Caution, result.tier)
    }

    @Test
    fun `legacy path maps RiskLevel UNKNOWN to RiskTier Unknown`() {
        // aggregatedInput=null, all evidence null → riskLevel=UNKNOWN → tier=Unknown
        val input = DecisionInput()
        val result = engine.evaluate(input)
        assertEquals(RiskTier.Unknown, result.tier)
    }
}
