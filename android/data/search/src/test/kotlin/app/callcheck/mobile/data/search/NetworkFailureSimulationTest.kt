package app.callcheck.mobile.data.search

import app.callcheck.mobile.core.model.SearchEvidence
import app.callcheck.mobile.core.model.SearchTrend
import org.junit.Assert.*
import org.junit.Test

/**
 * 네트워크 장애 시뮬레이션 검증.
 *
 * 190개국 실전 환경에서 발생 가능한 네트워크 장애 시나리오:
 *  - Google 차단 (중국, 러시아 일부)
 *  - DDG 접근 불가 (일부 ISP 차단)
 *  - 전체 검색 엔진 실패
 *  - 부분 결과만 반환
 *  - DNS 해석 실패
 *  - TLS 핸드셰이크 실패
 *
 * 검증 항목:
 *  1. SearchEvidence.empty()가 안전한 빈 객체인지
 *  2. 부분 결과로도 유효한 SearchEvidence 생성 가능한지
 *  3. isEmpty / signal flag 판정이 정확한지
 *  4. 절대 null 반환하지 않는지
 */
class NetworkFailureSimulationTest {

    // ═══════════════════════════════════════════════════════════
    // 1. 전체 검색 실패 → SearchEvidence.empty()
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `NETWORK-FAIL empty evidence has zero signal flags`() {
        val empty = SearchEvidence.empty()

        assertTrue("isEmpty must be true", empty.isEmpty)
        assertFalse("No delivery signal", empty.hasDeliverySignal)
        assertFalse("No institution signal", empty.hasInstitutionSignal)
        assertFalse("No business signal", empty.hasBusinessSignal)
        assertFalse("No spam signal", empty.hasSpamSignal)
        assertFalse("No scam signal", empty.hasScamSignal)
    }

    @Test
    fun `NETWORK-FAIL empty evidence has no keywords or entities`() {
        val empty = SearchEvidence.empty()

        assertTrue("keywordClusters empty", empty.keywordClusters.isEmpty())
        assertTrue("repeatedEntities empty", empty.repeatedEntities.isEmpty())
        assertTrue("sourceTypes empty", empty.sourceTypes.isEmpty())
        assertTrue("topSnippets empty", empty.topSnippets.isEmpty())
        assertTrue("signalSummaries empty", empty.signalSummaries.isEmpty())
    }

    @Test
    fun `NETWORK-FAIL empty evidence search intensity is null`() {
        val empty = SearchEvidence.empty()

        assertNull("30d intensity null", empty.recent30dSearchIntensity)
        assertNull("90d intensity null", empty.recent90dSearchIntensity)
        assertEquals(SearchTrend.NONE, empty.searchTrend)
    }

    // ═══════════════════════════════════════════════════════════
    // 2. 부분 검색 성공 (일부 Provider만 응답)
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `NETWORK-FAIL partial result — 1 provider only — still valid evidence`() {
        // Naver만 성공, Google+DDG 실패
        val partial = SearchEvidence(
            recent30dSearchIntensity = 3,
            recent90dSearchIntensity = 5,
            searchTrend = SearchTrend.LOW,
            keywordClusters = listOf("택배"),
            repeatedEntities = listOf("CJ대한통운"),
            sourceTypes = listOf("COMMUNITY"),
            topSnippets = listOf("택배 배송 조회"),
        )

        assertFalse("Partial result is NOT empty", partial.isEmpty)
        assertTrue("Has delivery signal", partial.hasDeliverySignal)
        assertFalse("No scam signal from delivery", partial.hasScamSignal)
    }

    @Test
    fun `NETWORK-FAIL partial result with only snippet — not empty`() {
        val minimal = SearchEvidence(
            recent30dSearchIntensity = 1,
            recent90dSearchIntensity = null,
            searchTrend = SearchTrend.NONE,
            keywordClusters = emptyList(),
            repeatedEntities = emptyList(),
            sourceTypes = emptyList(),
            topSnippets = listOf("Unknown number report"),
        )

        assertFalse("Has snippet → not empty", minimal.isEmpty)
    }

    // ═══════════════════════════════════════════════════════════
    // 3. 중국 환경 — Google 없음, Baidu만
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `NETWORK-FAIL China — Baidu success, no Google — valid evidence`() {
        // SearchResultAnalyzer는 중국어 원시 결과를 분석 후 정규화된 키워드를 생성
        // keywordClusters에는 SCAM_KEYWORDS에 매칭되는 정규화 키워드가 들어감
        val baiduOnly = SearchEvidence(
            recent30dSearchIntensity = 15,
            recent90dSearchIntensity = 30,
            searchTrend = SearchTrend.STABLE,
            keywordClusters = listOf("scam", "fraud"),  // Analyzer가 변환한 정규화 키워드
            repeatedEntities = emptyList(),
            sourceTypes = listOf("SPAM_REPORT"),
            topSnippets = listOf("这个号码是骗子"),
        )

        assertFalse("Baidu result not empty", baiduOnly.isEmpty)
        assertTrue("Has scam signal from analyzed keywords", baiduOnly.hasScamSignal)
    }

    @Test
    fun `NETWORK-FAIL China — Baidu also fails — safe empty`() {
        val empty = SearchEvidence.empty()
        assertTrue("Must be empty", empty.isEmpty)
        // DecisionEngine은 이 상태를 안전하게 처리
    }

    // ═══════════════════════════════════════════════════════════
    // 4. 러시아 환경 — Yandex 부분 성공
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `NETWORK-FAIL Russia — Yandex partial, Google blocked — still usable`() {
        // Yandex 결과를 SearchResultAnalyzer가 분석 후 정규화 키워드 생성
        val yandexPartial = SearchEvidence(
            recent30dSearchIntensity = 8,
            recent90dSearchIntensity = 20,
            searchTrend = SearchTrend.INCREASING,
            keywordClusters = listOf("사기", "경고"),  // Analyzer가 변환한 정규화 키워드
            repeatedEntities = emptyList(),
            sourceTypes = listOf("SPAM_REPORT"),
            topSnippets = emptyList(),
        )

        assertFalse("Yandex partial not empty", yandexPartial.isEmpty)
        assertTrue("Has scam signal from analyzed keywords", yandexPartial.hasScamSignal)
    }

    // ═══════════════════════════════════════════════════════════
    // 5. 아프리카 저속 네트워크 — DDG만 성공
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `NETWORK-FAIL Africa slow network — DDG only — minimal evidence`() {
        val ddgOnly = SearchEvidence(
            recent30dSearchIntensity = 1,
            recent90dSearchIntensity = 2,
            searchTrend = SearchTrend.NONE,
            keywordClusters = emptyList(),
            repeatedEntities = emptyList(),
            sourceTypes = listOf("COMMUNITY"),
            topSnippets = listOf("This number has been reported"),
        )

        assertFalse("DDG result not empty", ddgOnly.isEmpty)
    }

    // ═══════════════════════════════════════════════════════════
    // 6. 절대 null 아닌 empty 보장
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `NETWORK-FAIL SearchEvidence empty is a real object not null`() {
        val empty: SearchEvidence? = SearchEvidence.empty()

        assertNotNull("empty() must not return null", empty)
        assertTrue("Must be empty", empty!!.isEmpty)

        // null-safe access chain 검증
        val hasScam = empty.hasScamSignal
        val hasSpam = empty.hasSpamSignal
        assertFalse(hasScam)
        assertFalse(hasSpam)
    }

    @Test
    fun `NETWORK-FAIL multiple empty calls return same structure`() {
        val empty1 = SearchEvidence.empty()
        val empty2 = SearchEvidence.empty()

        // 구조적 동등성
        assertEquals(empty1.isEmpty, empty2.isEmpty)
        assertEquals(empty1.hasScamSignal, empty2.hasScamSignal)
        assertEquals(empty1.searchTrend, empty2.searchTrend)
    }
}
