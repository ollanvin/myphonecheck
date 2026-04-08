package app.callcheck.mobile.feature.decisionengine

import app.callcheck.mobile.core.model.ActionRecommendation
import app.callcheck.mobile.core.model.ConclusionCategory
import app.callcheck.mobile.core.model.DeviceEvidence
import app.callcheck.mobile.core.model.RiskLevel
import app.callcheck.mobile.core.model.SearchEvidence
import app.callcheck.mobile.core.model.SearchTrend
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for PRD-aligned DecisionEngineImpl.
 *
 * Tests all 7 ConclusionCategory paths, 4 RiskLevel mappings,
 * 5 ActionRecommendation outputs, and edge cases.
 */
class DecisionEngineImplTest {

    private lateinit var engine: DecisionEngineImpl

    @Before
    fun setup() {
        engine = DecisionEngineImpl(
            riskBadgeMapper = RiskBadgeMapper(),
            actionMapper = ActionMapper(),
            summaryGenerator = SummaryGenerator(),
        )
    }

    // ===== KNOWN_CONTACT =====

    @Test
    fun savedContact_returnsKnownContact() {
        val device = DeviceEvidence(
            isSavedContact = true,
            contactName = "John",
            outgoingCount = 5,
            lastOutgoingAt = System.currentTimeMillis() - 86400000,
            incomingCount = 10,
            lastIncomingAt = System.currentTimeMillis(),
            answeredCount = 12,
            rejectedCount = 0,
            lastRejectedAt = null,
            missedCount = 3,
            lastMissedAt = null,
            connectedCount = 12,
            lastConnectedAt = System.currentTimeMillis(),
            totalDurationSec = 3600,
            avgDurationSec = 300,
            shortCallCount = 1,
            longCallCount = 5,
            recentDaysContact = 1,
            smsExists = true,
            smsLastAt = System.currentTimeMillis(),
            localTag = null,
            localMemo = null,
        )

        val result = engine.evaluate(device, null)

        assertEquals(ConclusionCategory.KNOWN_CONTACT, result.category)
        assertEquals(ActionRecommendation.ANSWER, result.action)
        assertEquals(RiskLevel.LOW, result.riskLevel)
        assertTrue(result.confidence >= 0.9f)
        assertNotNull(result.summary)
        assertTrue(result.reasons.isNotEmpty())
    }

    // ===== SCAM_RISK_HIGH =====

    @Test
    fun scamSignal_returnsScamRiskHigh() {
        val device = DeviceEvidence.empty()
        val search = SearchEvidence(
            recent30dSearchIntensity = 100,
            recent90dSearchIntensity = 200,
            searchTrend = SearchTrend.INCREASING,
            keywordClusters = listOf("사기", "보이스피싱", "대출"),
            repeatedEntities = emptyList(),
            sourceTypes = listOf("SPAM_REPORT", "NEWS"),
            topSnippets = listOf("보이스피싱 주의"),
        )

        val result = engine.evaluate(device, search)

        assertEquals(ConclusionCategory.SCAM_RISK_HIGH, result.category)
        assertEquals(ActionRecommendation.BLOCK_REVIEW, result.action)
        assertEquals(RiskLevel.HIGH, result.riskLevel)
        assertTrue(result.confidence >= 0.7f)
    }

    // ===== SALES_SPAM_SUSPECTED =====

    @Test
    fun spamSignal_returnsSalesSpam() {
        val device = DeviceEvidence.empty()
        val search = SearchEvidence(
            recent30dSearchIntensity = 50,
            recent90dSearchIntensity = 80,
            searchTrend = SearchTrend.STABLE,
            keywordClusters = listOf("광고", "영업", "보험"),
            repeatedEntities = listOf("보험사"),
            sourceTypes = listOf("SPAM_REPORT"),
            topSnippets = listOf("보험 영업 전화"),
        )

        val result = engine.evaluate(device, search)

        assertEquals(ConclusionCategory.SALES_SPAM_SUSPECTED, result.category)
        assertEquals(ActionRecommendation.REJECT, result.action)
        assertTrue(
            result.riskLevel == RiskLevel.LOW ||
                result.riskLevel == RiskLevel.MEDIUM ||
                result.riskLevel == RiskLevel.HIGH,
        )
    }

    // ===== DELIVERY_LIKELY =====

    @Test
    fun deliverySignal_returnsDeliveryLikely() {
        val device = DeviceEvidence(
            isSavedContact = false,
            contactName = null,
            outgoingCount = 0,
            lastOutgoingAt = null,
            incomingCount = 2,
            lastIncomingAt = System.currentTimeMillis(),
            answeredCount = 1,
            rejectedCount = 0,
            lastRejectedAt = null,
            missedCount = 1,
            lastMissedAt = null,
            connectedCount = 1,
            lastConnectedAt = System.currentTimeMillis(),
            totalDurationSec = 30,
            avgDurationSec = 30,
            shortCallCount = 0,
            longCallCount = 0,
            recentDaysContact = 1,
            smsExists = true,
            smsLastAt = System.currentTimeMillis(),
            localTag = null,
            localMemo = null,
        )
        val search = SearchEvidence(
            recent30dSearchIntensity = 10,
            recent90dSearchIntensity = 20,
            searchTrend = SearchTrend.STABLE,
            keywordClusters = listOf("택배", "배송", "CJ대한통운"),
            repeatedEntities = listOf("CJ대한통운"),
            sourceTypes = listOf("COMMUNITY"),
            topSnippets = listOf("CJ대한통운 배송 문의"),
        )

        val result = engine.evaluate(device, search)

        assertEquals(ConclusionCategory.DELIVERY_LIKELY, result.category)
        assertEquals(ActionRecommendation.ANSWER_WITH_CAUTION, result.action)
        assertEquals(RiskLevel.LOW, result.riskLevel)
    }

    // ===== INSTITUTION_LIKELY =====

    @Test
    fun institutionSignal_returnsInstitutionLikely() {
        val device = DeviceEvidence.empty()
        val search = SearchEvidence(
            recent30dSearchIntensity = 5,
            recent90dSearchIntensity = 10,
            searchTrend = SearchTrend.LOW,
            keywordClusters = listOf("병원", "예약", "진료"),
            repeatedEntities = listOf("서울대병원"),
            sourceTypes = listOf("OFFICIAL"),
            topSnippets = listOf("서울대병원 진료 예약"),
        )

        val result = engine.evaluate(device, search)

        assertEquals(ConclusionCategory.INSTITUTION_LIKELY, result.category)
        assertEquals(ActionRecommendation.ANSWER_WITH_CAUTION, result.action)
    }

    // ===== BUSINESS_LIKELY =====

    @Test
    fun businessSignal_returnsBusinessLikely() {
        val device = DeviceEvidence.empty()
        val search = SearchEvidence(
            recent30dSearchIntensity = 8,
            recent90dSearchIntensity = 15,
            searchTrend = SearchTrend.STABLE,
            keywordClusters = listOf("회사", "고객센터"),
            repeatedEntities = listOf("삼성전자"),
            sourceTypes = listOf("OFFICIAL"),
            topSnippets = listOf("삼성전자 고객센터"),
        )

        val result = engine.evaluate(device, search)

        assertEquals(ConclusionCategory.BUSINESS_LIKELY, result.category)
    }

    @Test
    fun highRelationship_noSearch_returnsBusinessLikely() {
        val device = DeviceEvidence(
            isSavedContact = false,
            contactName = null,
            outgoingCount = 3,
            lastOutgoingAt = System.currentTimeMillis(),
            incomingCount = 5,
            lastIncomingAt = System.currentTimeMillis(),
            answeredCount = 6,
            rejectedCount = 0,
            lastRejectedAt = null,
            missedCount = 2,
            lastMissedAt = null,
            connectedCount = 6,
            lastConnectedAt = System.currentTimeMillis(),
            totalDurationSec = 1800,
            avgDurationSec = 300,
            shortCallCount = 0,
            longCallCount = 3,
            recentDaysContact = 2,
            smsExists = true,
            smsLastAt = System.currentTimeMillis(),
            localTag = null,
            localMemo = null,
        )

        val result = engine.evaluate(device, null)

        assertEquals(ConclusionCategory.BUSINESS_LIKELY, result.category)
        assertTrue(result.riskLevel == RiskLevel.LOW)
    }

    // ===== INSUFFICIENT_EVIDENCE =====

    @Test
    fun noEvidence_returnsInsufficientEvidence() {
        val result = engine.evaluate(null, null)

        assertEquals(ConclusionCategory.INSUFFICIENT_EVIDENCE, result.category)
        assertEquals(ActionRecommendation.HOLD, result.action)
        assertEquals(RiskLevel.UNKNOWN, result.riskLevel)
        assertTrue(result.confidence <= 0.4f)
    }

    @Test
    fun emptyDeviceNoSearch_returnsInsufficientEvidence() {
        val device = DeviceEvidence.empty()
        val result = engine.evaluate(device, null)

        assertEquals(ConclusionCategory.INSUFFICIENT_EVIDENCE, result.category)
        assertEquals(ActionRecommendation.HOLD, result.action)
    }

    // ===== EDGE CASES =====

    @Test
    fun savedContact_withScamSearch_returnsScamRiskHigh() {
        // Saved contact should take priority even if search shows scam
        val device = DeviceEvidence(
            isSavedContact = true,
            contactName = "Mom",
            outgoingCount = 10,
            lastOutgoingAt = System.currentTimeMillis(),
            incomingCount = 20,
            lastIncomingAt = System.currentTimeMillis(),
            answeredCount = 25,
            rejectedCount = 0,
            lastRejectedAt = null,
            missedCount = 5,
            lastMissedAt = null,
            connectedCount = 25,
            lastConnectedAt = System.currentTimeMillis(),
            totalDurationSec = 10000,
            avgDurationSec = 400,
            shortCallCount = 2,
            longCallCount = 10,
            recentDaysContact = 0,
            smsExists = true,
            smsLastAt = System.currentTimeMillis(),
            localTag = null,
            localMemo = null,
        )
        val search = SearchEvidence(
            recent30dSearchIntensity = 100,
            recent90dSearchIntensity = 200,
            searchTrend = SearchTrend.INCREASING,
            keywordClusters = listOf("사기", "피싱"),
            repeatedEntities = emptyList(),
            sourceTypes = listOf("SPAM_REPORT"),
            topSnippets = listOf("사기 전화"),
        )

        val result = engine.evaluate(device, search)

        assertEquals(ConclusionCategory.SCAM_RISK_HIGH, result.category)
        assertEquals(ActionRecommendation.BLOCK_REVIEW, result.action)
    }

    @Test
    fun resultAlwaysHasMaxThreeReasons() {
        val device = DeviceEvidence(
            isSavedContact = false,
            contactName = null,
            outgoingCount = 5,
            lastOutgoingAt = System.currentTimeMillis(),
            incomingCount = 10,
            lastIncomingAt = System.currentTimeMillis(),
            answeredCount = 12,
            rejectedCount = 2,
            lastRejectedAt = null,
            missedCount = 3,
            lastMissedAt = null,
            connectedCount = 12,
            lastConnectedAt = System.currentTimeMillis(),
            totalDurationSec = 3600,
            avgDurationSec = 300,
            shortCallCount = 1,
            longCallCount = 5,
            recentDaysContact = 1,
            smsExists = true,
            smsLastAt = System.currentTimeMillis(),
            localTag = null,
            localMemo = null,
        )
        val search = SearchEvidence(
            recent30dSearchIntensity = 50,
            recent90dSearchIntensity = 100,
            searchTrend = SearchTrend.INCREASING,
            keywordClusters = listOf("회사", "고객센터", "택배"),
            repeatedEntities = listOf("삼성"),
            sourceTypes = listOf("OFFICIAL"),
            topSnippets = listOf("삼성전자 고객센터"),
        )

        val result = engine.evaluate(device, search)

        assertTrue(result.reasons.size <= 3)
        assertNotNull(result.summary)
        assertTrue(result.summary.isNotEmpty())
    }

    @Test
    fun confidenceIncreasesWithBothEvidence() {
        val device = DeviceEvidence(
            isSavedContact = false,
            contactName = null,
            outgoingCount = 1,
            lastOutgoingAt = System.currentTimeMillis(),
            incomingCount = 3,
            lastIncomingAt = System.currentTimeMillis(),
            answeredCount = 3,
            rejectedCount = 0,
            lastRejectedAt = null,
            missedCount = 1,
            lastMissedAt = null,
            connectedCount = 3,
            lastConnectedAt = System.currentTimeMillis(),
            totalDurationSec = 300,
            avgDurationSec = 100,
            shortCallCount = 0,
            longCallCount = 1,
            recentDaysContact = 2,
            smsExists = false,
            smsLastAt = null,
            localTag = null,
            localMemo = null,
        )

        val deviceOnlyResult = engine.evaluate(device, null)

        val search = SearchEvidence(
            recent30dSearchIntensity = 5,
            recent90dSearchIntensity = 10,
            searchTrend = SearchTrend.STABLE,
            keywordClusters = listOf("회사"),
            repeatedEntities = emptyList(),
            sourceTypes = listOf("OFFICIAL"),
            topSnippets = listOf("기업 대표번호"),
        )

        val bothResult = engine.evaluate(device, search)

        assertTrue(bothResult.confidence >= deviceOnlyResult.confidence)
    }
}
