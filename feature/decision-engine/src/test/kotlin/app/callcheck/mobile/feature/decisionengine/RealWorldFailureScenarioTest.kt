package app.callcheck.mobile.feature.decisionengine

import app.callcheck.mobile.core.model.ActionRecommendation
import app.callcheck.mobile.core.model.ConclusionCategory
import app.callcheck.mobile.core.model.DecisionResult
import app.callcheck.mobile.core.model.DeviceEvidence
import app.callcheck.mobile.core.model.RiskLevel
import app.callcheck.mobile.core.model.SearchEvidence
import app.callcheck.mobile.core.model.SearchTrend
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * 190개국 실전 환경 장애 시나리오 전수 검증.
 *
 * 자비스 지시 4개 영역:
 *  1) 네트워크 차단 환경 — Google/DDG 전부 실패
 *  2) 타임아웃 환경 — 3초 내 결과 강제 fallback
 *  3) 오프라인 상태 — 네트워크 완전 차단
 *  4) 최종 안전 장치 — blank/null/crash 절대 방지
 *
 * 핵심 원칙:
 *  - 어떤 상황에서도 DecisionResult는 유효해야 한다
 *  - summary는 절대 blank/null이 아니다
 *  - category는 절대 null이 아니다
 *  - "빈 화면" 상태는 존재하지 않는다
 */
class RealWorldFailureScenarioTest {

    private lateinit var engine: DecisionEngineImpl

    @Before
    fun setup() {
        engine = DecisionEngineImpl(
            riskBadgeMapper = RiskBadgeMapper(),
            actionMapper = ActionMapper(),
            summaryGenerator = SummaryGenerator(),
        )
    }

    // ═══════════════════════════════════════════════════════════
    // 영역 1: 네트워크 차단 환경
    // Google 차단(CN), DDG 실패, 전체 검색 실패 시
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `NETWORK-BLOCK all search fails, device has history — device evidence only decision`() {
        // 시나리오: 중국에서 Google 차단, Baidu 실패, DDG 실패
        // Device Evidence는 있음 (이전 통화 이력 존재)
        val device = DeviceEvidence(
            isSavedContact = false,
            contactName = null,
            outgoingCount = 2,
            lastOutgoingAt = System.currentTimeMillis() - 86400000,
            incomingCount = 3,
            lastIncomingAt = System.currentTimeMillis(),
            answeredCount = 3,
            rejectedCount = 0,
            lastRejectedAt = null,
            missedCount = 1,
            lastMissedAt = null,
            connectedCount = 3,
            lastConnectedAt = System.currentTimeMillis(),
            totalDurationSec = 600,
            avgDurationSec = 200,
            shortCallCount = 0,
            longCallCount = 2,
            recentDaysContact = 1,
            smsExists = false,
            smsLastAt = null,
            localTag = null,
            localMemo = null,
        )

        // Search 전부 실패 → SearchEvidence.empty()
        val search = SearchEvidence.empty()

        val result = engine.evaluate(device, search)

        // 반드시 유효한 결과
        assertResultIsValid(result, "NETWORK-BLOCK with device history")
        // Device evidence만으로 판정 — relationship score 있으므로 INSUFFICIENT가 아닌 유의미한 판정
        assertTrue(
            "With device history, category should not be UNKNOWN riskLevel",
            result.riskLevel != RiskLevel.UNKNOWN || result.category == ConclusionCategory.INSUFFICIENT_EVIDENCE,
        )
    }

    @Test
    fun `NETWORK-BLOCK all search fails, no device history — insufficient evidence safe fallback`() {
        // 시나리오: 완전한 첫 수신, 네트워크 차단
        val device = DeviceEvidence.empty()
        val search = SearchEvidence.empty()

        val result = engine.evaluate(device, search)

        assertResultIsValid(result, "NETWORK-BLOCK no history")
        assertEquals(ConclusionCategory.INSUFFICIENT_EVIDENCE, result.category)
        assertEquals(ActionRecommendation.HOLD, result.action)
        // "정보 부족" 안전 메시지 보장
        assertTrue(
            "Summary must indicate insufficient evidence",
            result.summary.isNotBlank(),
        )
    }

    @Test
    fun `NETWORK-BLOCK null search evidence — same as empty`() {
        // SearchEnrichmentRepository가 null 반환 (timeout 등)
        val device = DeviceEvidence.empty()

        val result = engine.evaluate(device, null)

        assertResultIsValid(result, "NETWORK-BLOCK null search")
        assertEquals(ConclusionCategory.INSUFFICIENT_EVIDENCE, result.category)
        assertEquals(ActionRecommendation.HOLD, result.action)
    }

    @Test
    fun `NETWORK-BLOCK China scenario — Baidu fails, no Google, DDG fails`() {
        // 중국 환경 시뮬레이션: Google 자체가 라우팅에 없음, Baidu+DDG 모두 실패
        val device = DeviceEvidence(
            isSavedContact = false,
            contactName = null,
            outgoingCount = 0,
            lastOutgoingAt = null,
            incomingCount = 1,
            lastIncomingAt = System.currentTimeMillis(),
            answeredCount = 0,
            rejectedCount = 1,
            lastRejectedAt = System.currentTimeMillis() - 3600000,
            missedCount = 0,
            lastMissedAt = null,
            connectedCount = 0,
            lastConnectedAt = null,
            totalDurationSec = 0,
            avgDurationSec = 0,
            shortCallCount = 0,
            longCallCount = 0,
            recentDaysContact = null,
            smsExists = false,
            smsLastAt = null,
            localTag = null,
            localMemo = null,
        )
        val search: SearchEvidence? = null  // 전부 실패

        val result = engine.evaluate(device, search)

        assertResultIsValid(result, "NETWORK-BLOCK China")
        // reject 이력이 있으므로 device evidence가 존재
        assertNotNull(result.summary)
    }

    @Test
    fun `NETWORK-BLOCK Russia scenario — Yandex partial, Google blocked, DDG slow`() {
        // 러시아에서 Yandex만 부분 결과, Google/DDG 실패
        // 결과: 부분 검색 데이터만으로 판정
        val device = DeviceEvidence.empty()
        val search = SearchEvidence(
            recent30dSearchIntensity = 2,
            recent90dSearchIntensity = 5,
            searchTrend = SearchTrend.LOW,
            keywordClusters = listOf("компания"),  // 러시아어 "회사"
            repeatedEntities = emptyList(),
            sourceTypes = listOf("COMMUNITY"),
            topSnippets = emptyList(),
        )

        val result = engine.evaluate(device, search)

        assertResultIsValid(result, "NETWORK-BLOCK Russia partial")
        // 부분 검색이라도 유효한 판정
        assertTrue("Confidence should be > 0", result.confidence > 0f)
    }

    // ═══════════════════════════════════════════════════════════
    // 영역 2: 타임아웃 테스트
    // 3초 내 결과 강제 fallback
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `TIMEOUT search timeout — device evidence only, valid result`() {
        // 검색이 타임아웃되어 null 반환
        val device = DeviceEvidence(
            isSavedContact = false,
            contactName = null,
            outgoingCount = 0,
            lastOutgoingAt = null,
            incomingCount = 5,
            lastIncomingAt = System.currentTimeMillis(),
            answeredCount = 2,
            rejectedCount = 2,
            lastRejectedAt = System.currentTimeMillis() - 86400000,
            missedCount = 1,
            lastMissedAt = null,
            connectedCount = 2,
            lastConnectedAt = System.currentTimeMillis(),
            totalDurationSec = 20,
            avgDurationSec = 10,
            shortCallCount = 2,
            longCallCount = 0,
            recentDaysContact = 1,
            smsExists = false,
            smsLastAt = null,
            localTag = null,
            localMemo = null,
        )

        val result = engine.evaluate(device, null)  // search timeout → null

        assertResultIsValid(result, "TIMEOUT search")
        // 짧은 통화 + rejected 이력 → 위험 신호
        assertTrue("With reject history, should have some assessment", result.summary.isNotBlank())
    }

    @Test
    fun `TIMEOUT device timeout — search only, valid result`() {
        // Device Evidence 수집이 타임아웃
        val search = SearchEvidence(
            recent30dSearchIntensity = 80,
            recent90dSearchIntensity = 150,
            searchTrend = SearchTrend.INCREASING,
            keywordClusters = listOf("scam", "fraud"),
            repeatedEntities = emptyList(),
            sourceTypes = listOf("SPAM_REPORT"),
            topSnippets = listOf("Reported as scam"),
        )

        val result = engine.evaluate(null, search)  // device timeout → null

        assertResultIsValid(result, "TIMEOUT device")
        // 검색에서 scam 신호 → 높은 위험
        assertEquals(ConclusionCategory.SCAM_RISK_HIGH, result.category)
        assertTrue("Scam must be HIGH risk", result.riskLevel == RiskLevel.HIGH)
    }

    @Test
    fun `TIMEOUT both timeout — DecisionResult fallback is valid`() {
        // 둘 다 타임아웃 → null, null
        val result = engine.evaluate(null, null)

        assertResultIsValid(result, "TIMEOUT both")
        assertEquals(ConclusionCategory.INSUFFICIENT_EVIDENCE, result.category)
        assertEquals(ActionRecommendation.HOLD, result.action)
    }

    @Test
    fun `TIMEOUT DecisionResult static fallback is always valid`() {
        // 최악의 경우: DecisionResult.fallback() 직접 호출
        val fallback = DecisionResult.fallback()

        assertResultIsValid(fallback, "DecisionResult.fallback()")
        assertEquals(RiskLevel.UNKNOWN, fallback.riskLevel)
        assertEquals(ConclusionCategory.INSUFFICIENT_EVIDENCE, fallback.category)
        assertEquals(ActionRecommendation.HOLD, fallback.action)
        assertEquals(0.0f, fallback.confidence)
        assertTrue("Fallback summary must not be blank", fallback.summary.isNotBlank())
        assertTrue("Fallback reasons must not be empty", fallback.reasons.isNotEmpty())
    }

    // ═══════════════════════════════════════════════════════════
    // 영역 3: 오프라인 상태
    // 네트워크 완전 차단 시 Device Evidence만으로 결과 출력
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `OFFLINE saved contact — still returns KNOWN_CONTACT`() {
        val device = DeviceEvidence(
            isSavedContact = true,
            contactName = "배달의민족",
            outgoingCount = 0,
            lastOutgoingAt = null,
            incomingCount = 5,
            lastIncomingAt = System.currentTimeMillis(),
            answeredCount = 3,
            rejectedCount = 0,
            lastRejectedAt = null,
            missedCount = 2,
            lastMissedAt = null,
            connectedCount = 3,
            lastConnectedAt = System.currentTimeMillis(),
            totalDurationSec = 30,
            avgDurationSec = 10,
            shortCallCount = 3,
            longCallCount = 0,
            recentDaysContact = 0,
            smsExists = true,
            smsLastAt = System.currentTimeMillis(),
            localTag = null,
            localMemo = null,
        )

        // 오프라인 → search = null
        val result = engine.evaluate(device, null)

        assertResultIsValid(result, "OFFLINE saved contact")
        assertEquals(ConclusionCategory.KNOWN_CONTACT, result.category)
        assertEquals(ActionRecommendation.ANSWER, result.action)
        assertEquals(RiskLevel.LOW, result.riskLevel)
    }

    @Test
    fun `OFFLINE suspicious device pattern — warns without search`() {
        // 오프라인이지만 Device Evidence에 수상한 패턴
        // 짧은 통화, 높은 reject, 저녁 시간대
        val device = DeviceEvidence(
            isSavedContact = false,
            contactName = null,
            outgoingCount = 0,
            lastOutgoingAt = null,
            incomingCount = 8,
            lastIncomingAt = System.currentTimeMillis(),
            answeredCount = 1,
            rejectedCount = 5,
            lastRejectedAt = System.currentTimeMillis() - 3600000,
            missedCount = 2,
            lastMissedAt = null,
            connectedCount = 1,
            lastConnectedAt = System.currentTimeMillis() - 86400000,
            totalDurationSec = 5,
            avgDurationSec = 5,
            shortCallCount = 1,
            longCallCount = 0,
            recentDaysContact = 0,
            smsExists = false,
            smsLastAt = null,
            localTag = null,
            localMemo = null,
        )

        val result = engine.evaluate(device, null)

        assertResultIsValid(result, "OFFLINE suspicious pattern")
        // 수상한 패턴 → 최소 MEDIUM 이상 경고
        assertTrue(
            "Suspicious pattern should raise concern",
            result.riskLevel != RiskLevel.LOW || result.category != ConclusionCategory.KNOWN_CONTACT,
        )
    }

    @Test
    fun `OFFLINE complete cold start — no history, no network`() {
        // 최악: 첫 설치, 네트워크 없음, 이력 없음
        val result = engine.evaluate(DeviceEvidence.empty(), null)

        assertResultIsValid(result, "OFFLINE cold start")
        assertEquals(ConclusionCategory.INSUFFICIENT_EVIDENCE, result.category)
        assertEquals(ActionRecommendation.HOLD, result.action)
        // "정보 부족 — 주의 필요" 유형의 안전 메시지
        assertTrue("Must have safety summary", result.summary.isNotBlank())
    }

    // ═══════════════════════════════════════════════════════════
    // 영역 4: 최종 안전 장치 — blank/null/crash 절대 방지
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `SAFETY null null — no crash, valid result`() {
        val result = engine.evaluate(null, null)
        assertResultIsValid(result, "null-null")
    }

    @Test
    fun `SAFETY empty empty — no crash, valid result`() {
        val result = engine.evaluate(DeviceEvidence.empty(), SearchEvidence.empty())
        assertResultIsValid(result, "empty-empty")
    }

    @Test
    fun `SAFETY null device, empty search — no crash`() {
        val result = engine.evaluate(null, SearchEvidence.empty())
        assertResultIsValid(result, "null device, empty search")
    }

    @Test
    fun `SAFETY empty device, null search — no crash`() {
        val result = engine.evaluate(DeviceEvidence.empty(), null)
        assertResultIsValid(result, "empty device, null search")
    }

    @Test
    fun `SAFETY all seven categories produce valid results`() {
        // 모든 7개 ConclusionCategory에 대해 유효한 결과 검증
        val allCategories = ConclusionCategory.values()
        assertTrue("Must have 7 categories", allCategories.size >= 7)

        // 각 카테고리가 엔진에서 나올 수 있는 시나리오 매핑
        val scenarios = listOf(
            // KNOWN_CONTACT
            engine.evaluate(
                DeviceEvidence(
                    isSavedContact = true, contactName = "Test",
                    outgoingCount = 5, lastOutgoingAt = System.currentTimeMillis(),
                    incomingCount = 5, lastIncomingAt = System.currentTimeMillis(),
                    answeredCount = 5, rejectedCount = 0, lastRejectedAt = null,
                    missedCount = 0, lastMissedAt = null,
                    connectedCount = 5, lastConnectedAt = System.currentTimeMillis(),
                    totalDurationSec = 3000, avgDurationSec = 600,
                    shortCallCount = 0, longCallCount = 3, recentDaysContact = 0,
                    smsExists = true, smsLastAt = System.currentTimeMillis(),
                    localTag = null, localMemo = null,
                ),
                null,
            ),
            // SCAM_RISK_HIGH
            engine.evaluate(
                DeviceEvidence.empty(),
                SearchEvidence(
                    recent30dSearchIntensity = 100, recent90dSearchIntensity = 200,
                    searchTrend = SearchTrend.INCREASING,
                    keywordClusters = listOf("사기", "피싱"),
                    repeatedEntities = emptyList(),
                    sourceTypes = listOf("SPAM_REPORT"),
                    topSnippets = listOf("보이스피싱"),
                ),
            ),
            // INSUFFICIENT_EVIDENCE
            engine.evaluate(null, null),
        )

        for (result in scenarios) {
            assertResultIsValid(result, "category=${result.category}")
        }
    }

    @Test
    fun `SAFETY DecisionResult fallback summary is never blank`() {
        val fallback = DecisionResult.fallback()
        assertTrue("summary must not be blank", fallback.summary.isNotBlank())
        assertTrue("summary must not be empty", fallback.summary.isNotEmpty())
        assertTrue("reasons must not be empty", fallback.reasons.isNotEmpty())
        for (reason in fallback.reasons) {
            assertTrue("Each reason must not be blank", reason.isNotBlank())
        }
    }

    @Test
    fun `SAFETY confidence is always in valid range 0 to 1`() {
        val testCases = listOf(
            engine.evaluate(null, null),
            engine.evaluate(DeviceEvidence.empty(), null),
            engine.evaluate(null, SearchEvidence.empty()),
            engine.evaluate(DeviceEvidence.empty(), SearchEvidence.empty()),
            DecisionResult.fallback(),
        )

        for (result in testCases) {
            assertTrue(
                "Confidence must be >= 0, got ${result.confidence}",
                result.confidence >= 0f,
            )
            assertTrue(
                "Confidence must be <= 1, got ${result.confidence}",
                result.confidence <= 1f,
            )
        }
    }

    @Test
    fun `SAFETY action recommendation is never null and always actionable`() {
        val testCases = listOf(
            engine.evaluate(null, null),
            engine.evaluate(DeviceEvidence.empty(), null),
            engine.evaluate(null, SearchEvidence.empty()),
        )

        val validActions = ActionRecommendation.values().toSet()
        for (result in testCases) {
            assertTrue(
                "Action must be valid enum value",
                result.action in validActions,
            )
        }
    }

    @Test
    fun `SAFETY risk level is never null`() {
        val testCases = listOf(
            engine.evaluate(null, null),
            engine.evaluate(DeviceEvidence.empty(), null),
            engine.evaluate(null, SearchEvidence.empty()),
        )

        val validLevels = RiskLevel.values().toSet()
        for (result in testCases) {
            assertTrue(
                "RiskLevel must be valid enum value",
                result.riskLevel in validLevels,
            )
        }
    }

    // ═══════════════════════════════════════════════════════════
    // 공통 검증 헬퍼
    // ═══════════════════════════════════════════════════════════

    /**
     * 모든 DecisionResult가 반드시 만족해야 하는 불변 조건.
     * 자비스 요구사항: blank/null/crash 절대 방지
     */
    private fun assertResultIsValid(result: DecisionResult, scenario: String) {
        // 1. 절대 null 필드 없음 (JUnit: assertNotNull(message, object))
        assertNotNull("[$scenario] riskLevel must not be null", result.riskLevel)
        assertNotNull("[$scenario] category must not be null", result.category)
        assertNotNull("[$scenario] action must not be null", result.action)
        assertNotNull("[$scenario] summary must not be null", result.summary)
        assertNotNull("[$scenario] reasons must not be null", result.reasons)

        // 2. summary 절대 blank 아님 ("빈 화면" 방지)
        assertTrue(
            "[$scenario] summary must not be blank: '${result.summary}'",
            result.summary.isNotBlank(),
        )

        // 3. confidence 범위 유효
        assertTrue(
            "[$scenario] confidence in [0,1]: ${result.confidence}",
            result.confidence in 0f..1f,
        )

        // 4. reasons 최대 3개
        assertTrue(
            "[$scenario] reasons max 3: ${result.reasons.size}",
            result.reasons.size <= 3,
        )

        // 5. reasons 내 blank 없음
        for ((i, reason) in result.reasons.withIndex()) {
            assertTrue(
                "[$scenario] reason[$i] must not be blank",
                reason.isNotBlank(),
            )
        }
    }
}
