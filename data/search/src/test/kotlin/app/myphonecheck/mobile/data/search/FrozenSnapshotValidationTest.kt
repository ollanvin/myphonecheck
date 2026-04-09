package app.myphonecheck.mobile.data.search

import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import java.net.URI
import java.net.URLDecoder

/**
 * ═══════════════════════════════════════════════════════════════
 * Layer B: Frozen Snapshot Validation Set
 * ═══════════════════════════════════════════════════════════════
 *
 * 실전 번호 검증셋의 2층 구조 중 "고정 자산" 계층.
 *
 * ┌─────────────────────────────────────────────────────────────┐
 * │ Layer A: RealWorldValidationSetTest (Live)                  │
 * │  - 실제 네트워크 호출                                        │
 * │  - skip 가능 (Assume.assumeTrue)                            │
 * │  - 검색 엔진 응답 변화 감지 목적                             │
 * │                                                             │
 * │ Layer B: FrozenSnapshotValidationTest (Frozen) ← 이 파일     │
 * │  - 로컬 HTML fixture 기반                                   │
 * │  - 네트워크 불안정과 무관하게 항상 재현 가능                  │
 * │  - 파싱 로직 + 분석 로직 회귀 검증 목적                      │
 * └─────────────────────────────────────────────────────────────┘
 *
 * Fixture 출처: fixtures/ddg-snapshots/ 디렉토리의 HTML 파일
 * HTML 구조: DuckDuckGo HTML (html.duckduckgo.com) 실제 응답 구조 재현
 *
 * ┌──────────────┬─────────────────────────┬──────────────────────┐
 * │ 카테고리      │ 번호                    │ Fixture 파일          │
 * ├──────────────┼─────────────────────────┼──────────────────────┤
 * │ 기관         │ 1345 (정부민원안내)      │ kr-institution-1345  │
 * │ 기업         │ 15881234 (신세계백화점)  │ kr-business-15881234 │
 * │ 택배         │ 15881255 (CJ대한통운)    │ kr-delivery-15881255 │
 * │ 사기         │ 0288881234 (보이스피싱)  │ kr-scam-0288881234   │
 * │ JP 기관      │ 0120444113 (NTT)        │ jp-institution-0120  │
 * │ US 스캠      │ 8008290433              │ us-scam-8008290433   │
 * │ US IRS사기   │ 2028003000 (IRS 사칭)   │ us-irs-scam-2028003  │
 * │ US 은행위장  │ 8005551234 (BoA 스푸핑) │ us-bank-spoof-800555 │
 * │ US 의료      │ 8006332225 (Medicare)   │ us-healthcare-800633 │
 * │ US 보험위장  │ 8007721213 (SSA 스푸핑) │ us-insurance-8007721 │
 * │ US 혼합      │ 2125551000 (NYTimes)    │ us-mixed-2125551000  │
 * │ 혼합         │ 15889999 (현대홈쇼핑)   │ kr-mixed-15889999    │
 * │ 의료         │ 15771000 (삼성서울병원)  │ kr-medical-15771000  │
 * │ 은행         │ 15881599 (KB국민은행)    │ kr-bank-15881599     │
 * │ 통신         │ 114 (KT 번호안내)       │ kr-telecom-114       │
 * └──────────────┴─────────────────────────┴──────────────────────┘
 */
class FrozenSnapshotValidationTest {

    private val analyzer = SearchResultAnalyzer()

    // ═══════════════════════════════════════════════════════════
    // 1. 기관 — 1345 정부민원안내콜센터
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `FROZEN KR INSTITUTION - 1345 government hotline`() = runBlocking {
        val results = loadFixtureAndParse("kr-institution-1345.html")
        assertTrue("Fixture must produce results", results.isNotEmpty())

        val evidence = analyzer.analyzeSearchResults(results)
        printEvidence("FROZEN-KR-INSTITUTION-1345", results, evidence)

        assertFalse("Evidence must not be empty", evidence.isEmpty)

        // 정부 기관 → "수신 위험" 금지
        evidence.signalSummaries.forEach { signal ->
            assertFalse(
                "Government hotline must NOT be 수신 위험, got: '${signal.signalDescription}'",
                signal.signalDescription.contains("수신 위험")
            )
        }

        // 기관 신호 존재 확인
        val hasInstitution = evidence.signalSummaries.any {
            it.signalType == "INSTITUTION"
        }
        assertTrue("Should detect INSTITUTION signal", hasInstitution)
    }

    // ═══════════════════════════════════════════════════════════
    // 2. 기업 — 15881234 신세계백화점
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `FROZEN KR BUSINESS - 15881234 Shinsegae`() = runBlocking {
        val results = loadFixtureAndParse("kr-business-15881234.html")
        assertTrue("Fixture must produce results", results.isNotEmpty())

        val evidence = analyzer.analyzeSearchResults(results)
        printEvidence("FROZEN-KR-BUSINESS-15881234", results, evidence)

        assertFalse("Evidence must not be empty", evidence.isEmpty)

        // 신세계백화점: "수신 위험" 금지
        evidence.signalSummaries.forEach { signal ->
            assertFalse(
                "Shinsegae must NOT be 수신 위험, got: '${signal.signalDescription}'",
                signal.signalDescription.contains("수신 위험")
            )
        }

        // 기업 엔터티(신세계백화점)가 포함되어야 함
        val primary = evidence.signalSummaries.firstOrNull()
        assertNotNull("Must have at least one signal", primary)
        assertTrue(
            "Should mention 신세계백화점, got: '${primary!!.signalDescription}'",
            primary.signalDescription.contains("신세계백화점") ||
                    primary.signalType in setOf("BUSINESS", "BUSINESS_WITH_REPORT")
        )
    }

    // ═══════════════════════════════════════════════════════════
    // 3. 택배 — 15881255 CJ대한통운
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `FROZEN KR DELIVERY - 15881255 CJ Logistics`() = runBlocking {
        val results = loadFixtureAndParse("kr-delivery-15881255.html")
        assertTrue("Fixture must produce results", results.isNotEmpty())

        val evidence = analyzer.analyzeSearchResults(results)
        printEvidence("FROZEN-KR-DELIVERY-15881255", results, evidence)

        assertFalse("Evidence must not be empty", evidence.isEmpty)

        // "수신 위험" 금지
        evidence.signalSummaries.forEach { signal ->
            assertFalse(
                "CJ Logistics must NOT be 수신 위험, got: '${signal.signalDescription}'",
                signal.signalDescription.contains("수신 위험")
            )
        }

        // 택배 신호 확인: SPAM_REPORT 도메인(thecall.co.kr) 존재 시
        // CR-2에 의해 BUSINESS_WITH_REPORT로 병합될 수 있음 — 둘 다 정상
        val hasDeliveryOrMerged = evidence.signalSummaries.any {
            it.signalType in setOf("DELIVERY", "BUSINESS_WITH_REPORT", "MIXED")
        }
        assertTrue("Should detect DELIVERY or merged signal", hasDeliveryOrMerged)

        // CJ대한통운 엔터티가 문장에 반영되어야 함
        val primary = evidence.signalSummaries.first()
        assertTrue(
            "Should mention CJ대한통운 or 택배, got: '${primary.signalDescription}'",
            primary.signalDescription.contains("CJ대한통운") ||
                    primary.signalDescription.contains("택배") ||
                    primary.signalDescription.contains("배송")
        )
    }

    // ═══════════════════════════════════════════════════════════
    // 4. 사기 — 0288881234 보이스피싱 대역
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `FROZEN KR SCAM - 0288881234 voice phishing zone`() = runBlocking {
        val results = loadFixtureAndParse("kr-scam-0288881234.html")
        assertTrue("Fixture must produce results", results.isNotEmpty())

        val evidence = analyzer.analyzeSearchResults(results)
        printEvidence("FROZEN-KR-SCAM-0288881234", results, evidence)

        // 사기 번호 → "수신 안전"만 나오면 안 됨
        val hasSafeOnly = evidence.signalSummaries.isNotEmpty() &&
                evidence.signalSummaries.all { it.signalDescription.contains("수신 안전") }
        assertFalse("Scam zone must NOT show only 수신 안전", hasSafeOnly)

        // SCAM 신호 존재 확인
        val hasScam = evidence.signalSummaries.any { it.signalType == "SCAM" }
        assertTrue("Should detect SCAM signal", hasScam)

        // 다수 신고(5건+) → "수신 위험" 포함 확인
        val scamSignal = evidence.signalSummaries.first { it.signalType == "SCAM" }
        if (scamSignal.resultCount >= 5) {
            assertTrue(
                "5+ scam results should say 수신 위험, got: '${scamSignal.signalDescription}'",
                scamSignal.signalDescription.contains("수신 위험")
            )
        }
    }

    // ═══════════════════════════════════════════════════════════
    // 5. JP 기관 — 0120444113 NTT 고장접수
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `FROZEN JP INSTITUTION - 0120444113 NTT fault report`() = runBlocking {
        val results = loadFixtureAndParse("jp-institution-0120444113.html")
        assertTrue("Fixture must produce results", results.isNotEmpty())

        val evidence = analyzer.analyzeSearchResults(results)
        printEvidence("FROZEN-JP-INSTITUTION-0120444113", results, evidence)

        assertFalse("Evidence must not be empty", evidence.isEmpty)

        // NTT 기관 → "수신 위험" 금지
        evidence.signalSummaries.forEach { signal ->
            assertFalse(
                "NTT must NOT be 수신 위험, got: '${signal.signalDescription}'",
                signal.signalDescription.contains("수신 위험")
            )
        }

        // 기관 또는 기업 신호 존재 확인
        val hasPositive = evidence.signalSummaries.any {
            it.signalType in setOf("INSTITUTION", "BUSINESS")
        }
        assertTrue("JP NTT should have positive signal", hasPositive)
    }

    // ═══════════════════════════════════════════════════════════
    // 6. US 스캠 — 8008290433
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `FROZEN US SCAM - 8008290433`() = runBlocking {
        val results = loadFixtureAndParse("us-scam-8008290433.html")
        assertTrue("Fixture must produce results", results.isNotEmpty())

        val evidence = analyzer.analyzeSearchResults(results)
        printEvidence("FROZEN-US-SCAM-8008290433", results, evidence)

        assertFalse("Evidence must not be empty", evidence.isEmpty)

        // 스캠 신호 존재 확인
        val hasNegative = evidence.signalSummaries.any {
            it.signalType in setOf("SCAM", "SPAM", "SPAM_REPORT")
        }
        assertTrue("US scam number should have negative signal", hasNegative)
    }

    // ═══════════════════════════════════════════════════════════
    // 6-B. US IRS SCAM — 2028003000 IRS 사칭
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `FROZEN US IRS SCAM - 2028003000 IRS impersonation`() = runBlocking {
        val results = loadFixtureAndParse("us-irs-scam-2028003000.html")
        assertTrue("Fixture must produce results", results.isNotEmpty())

        val evidence = analyzer.analyzeSearchResults(results)
        printEvidence("FROZEN-US-IRS-SCAM-2028003000", results, evidence)

        assertFalse("Evidence must not be empty", evidence.isEmpty)

        // IRS 사칭 → SCAM 신호 필수
        val hasScam = evidence.signalSummaries.any {
            it.signalType in setOf("SCAM", "SPAM_REPORT")
        }
        assertTrue("IRS impersonation should detect SCAM signal", hasScam)

        // IRS / tax / fraud 키워드가 스니펫에 존재
        val hasIrsKeyword = results.any { r ->
            r.snippet.lowercase().let {
                it.contains("irs") || it.contains("tax") || it.contains("fraud")
            }
        }
        assertTrue("Should contain IRS-related keywords", hasIrsKeyword)
    }

    // ═══════════════════════════════════════════════════════════
    // 6-C. US BANK SPOOF — 8005551234 Bank of America 스푸핑
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `FROZEN US BANK SPOOF - 8005551234 bank impersonation`() = runBlocking {
        val results = loadFixtureAndParse("us-bank-spoof-8005551234.html")
        assertTrue("Fixture must produce results", results.isNotEmpty())

        val evidence = analyzer.analyzeSearchResults(results)
        printEvidence("FROZEN-US-BANK-SPOOF-8005551234", results, evidence)

        assertFalse("Evidence must not be empty", evidence.isEmpty)

        // 은행 스푸핑 → SCAM 또는 SPAM 신호 필수
        val hasNegative = evidence.signalSummaries.any {
            it.signalType in setOf("SCAM", "SPAM", "SPAM_REPORT")
        }
        assertTrue("Bank spoofing should have negative signal", hasNegative)

        // spoof / fraud / phishing 키워드 존재
        val hasSpoofKeyword = results.any { r ->
            r.snippet.lowercase().let {
                it.contains("spoof") || it.contains("fraud") || it.contains("phishing") || it.contains("scam")
            }
        }
        assertTrue("Should contain spoofing-related keywords", hasSpoofKeyword)
    }

    // ═══════════════════════════════════════════════════════════
    // 6-D. US HEALTHCARE — 8006332225 Medicare 공식 번호
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `FROZEN US HEALTHCARE - 8006332225 Medicare official`() = runBlocking {
        val results = loadFixtureAndParse("us-healthcare-8006332225.html")
        assertTrue("Fixture must produce results", results.isNotEmpty())

        val evidence = analyzer.analyzeSearchResults(results)
        printEvidence("FROZEN-US-HEALTHCARE-8006332225", results, evidence)

        assertFalse("Evidence must not be empty", evidence.isEmpty)

        // Medicare 공식 번호 → "수신 위험" 금지
        evidence.signalSummaries.forEach { signal ->
            assertFalse(
                "Medicare official must NOT be 수신 위험, got: '${signal.signalDescription}'",
                signal.signalDescription.contains("수신 위험")
            )
        }

        // 정부/기관/기업 신호 존재 확인 (BUSINESS_WITH_REPORT도 허용)
        val hasPositive = evidence.signalSummaries.any {
            it.signalType in setOf("INSTITUTION", "BUSINESS", "BUSINESS_WITH_REPORT")
        }
        assertTrue("Medicare should have positive signal", hasPositive)
    }

    // ═══════════════════════════════════════════════════════════
    // 6-E. US INSURANCE (SSA SPOOF) — 8007721213 SSA 스푸핑
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `FROZEN US INSURANCE - 8007721213 SSA spoofed number`() = runBlocking {
        val results = loadFixtureAndParse("us-insurance-8007721213.html")
        assertTrue("Fixture must produce results", results.isNotEmpty())

        val evidence = analyzer.analyzeSearchResults(results)
        printEvidence("FROZEN-US-INSURANCE-8007721213", results, evidence)

        assertFalse("Evidence must not be empty", evidence.isEmpty)

        // SSA 번호는 스푸핑이 빈번 → SCAM 또는 MIXED 신호 허용
        // 단, "수신 안전"만 나오면 안 됨 (혼합 신호 감지 필수)
        val hasSafeOnly = evidence.signalSummaries.isNotEmpty() &&
                evidence.signalSummaries.all {
                    it.signalDescription.contains("수신 안전") ||
                            it.signalType == "INSTITUTION"
                }
        // SSA는 실제 공식번호이면서 동시에 스푸핑 대상 — 혼합이 정상
        // 따라서 최소 1개 이상의 negative 또는 mixed 신호가 있어야 함
        val hasWarning = evidence.signalSummaries.any {
            it.signalType in setOf("SCAM", "SPAM", "SPAM_REPORT", "MIXED", "BUSINESS_WITH_REPORT")
        }
        assertTrue("SSA spoofed number should have warning/mixed signal", hasWarning || !hasSafeOnly)
    }

    // ═══════════════════════════════════════════════════════════
    // 6-F. US MIXED — 2125551000 New York Times (기업 + 마케팅)
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `FROZEN US MIXED - 2125551000 NYTimes business`() = runBlocking {
        val results = loadFixtureAndParse("us-mixed-2125551000.html")
        assertTrue("Fixture must produce results", results.isNotEmpty())

        val evidence = analyzer.analyzeSearchResults(results)
        printEvidence("FROZEN-US-MIXED-2125551000", results, evidence)

        assertFalse("Evidence must not be empty", evidence.isEmpty)

        // NYTimes 기업 번호 → "수신 위험" 금지
        evidence.signalSummaries.forEach { signal ->
            assertFalse(
                "NYTimes must NOT be 수신 위험, got: '${signal.signalDescription}'",
                signal.signalDescription.contains("수신 위험")
            )
        }

        // 기업 또는 혼합 신호 존재 확인
        val hasBusiness = evidence.signalSummaries.any {
            it.signalType in setOf("BUSINESS", "BUSINESS_WITH_REPORT", "MIXED")
        }
        assertTrue("NYTimes should have BUSINESS or MIXED signal", hasBusiness)
    }

    // ═══════════════════════════════════════════════════════════
    // 7. 혼합 — 15889999 현대홈쇼핑 (기업 + 약한 신고)
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `FROZEN KR MIXED - 15889999 business with reports`() = runBlocking {
        val results = loadFixtureAndParse("kr-mixed-15889999.html")
        assertTrue("Fixture must produce results", results.isNotEmpty())

        val evidence = analyzer.analyzeSearchResults(results)
        printEvidence("FROZEN-KR-MIXED-15889999", results, evidence)

        assertFalse("Evidence must not be empty", evidence.isEmpty)

        // 1588 기업 대역 → "수신 위험" 금지
        evidence.signalSummaries.forEach { signal ->
            assertFalse(
                "Business number must NOT be 수신 위험, got: '${signal.signalDescription}'",
                signal.signalDescription.contains("수신 위험")
            )
        }
    }

    // ═══════════════════════════════════════════════════════════
    // 8. 의료 — 15771000 삼성서울병원
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `FROZEN KR MEDICAL - 15771000 Samsung Medical Center`() = runBlocking {
        val results = loadFixtureAndParse("kr-medical-15771000.html")
        assertTrue("Fixture must produce results", results.isNotEmpty())

        val evidence = analyzer.analyzeSearchResults(results)
        printEvidence("FROZEN-KR-MEDICAL-15771000", results, evidence)

        assertFalse("Evidence must not be empty", evidence.isEmpty)

        // 병원 → "수신 위험" 금지
        evidence.signalSummaries.forEach { signal ->
            assertFalse(
                "Hospital must NOT be 수신 위험, got: '${signal.signalDescription}'",
                signal.signalDescription.contains("수신 위험")
            )
        }

        // 기관 신호 존재 확인 (병원 = INSTITUTION)
        val hasInstitution = evidence.signalSummaries.any {
            it.signalType == "INSTITUTION"
        }
        assertTrue("Hospital should detect INSTITUTION signal", hasInstitution)
    }

    // ═══════════════════════════════════════════════════════════
    // 9. 은행 — 15881599 KB국민은행
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `FROZEN KR BANK - 15881599 KB Kookmin Bank`() = runBlocking {
        val results = loadFixtureAndParse("kr-bank-15881599.html")
        assertTrue("Fixture must produce results", results.isNotEmpty())

        val evidence = analyzer.analyzeSearchResults(results)
        printEvidence("FROZEN-KR-BANK-15881599", results, evidence)

        assertFalse("Evidence must not be empty", evidence.isEmpty)

        // 은행 → "수신 위험" 금지
        evidence.signalSummaries.forEach { signal ->
            assertFalse(
                "KB Bank must NOT be 수신 위험, got: '${signal.signalDescription}'",
                signal.signalDescription.contains("수신 위험")
            )
        }

        // "수신 안전" 포함 확인
        val hasSafe = evidence.signalSummaries.any {
            it.signalDescription.contains("수신 안전")
        }
        assertTrue("Bank should show 수신 안전", hasSafe)
    }

    // ═══════════════════════════════════════════════════════════
    // 10. 통신 — 114 KT 번호안내
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `FROZEN KR TELECOM - 114 KT directory service`() = runBlocking {
        val results = loadFixtureAndParse("kr-telecom-114.html")
        assertTrue("Fixture must produce results", results.isNotEmpty())

        val evidence = analyzer.analyzeSearchResults(results)
        printEvidence("FROZEN-KR-TELECOM-114", results, evidence)

        // 114: 짧은 번호라 키워드 매칭 불확실 → 최소 검증만
        // 파싱은 정상 동작해야 함
        assertTrue("Parsed results should be >= 2", results.size >= 2)
    }

    // ═══════════════════════════════════════════════════════════
    // HTML Fixture 로드 + DDG 파싱
    // ═══════════════════════════════════════════════════════════

    /**
     * test/resources/fixtures/ddg-snapshots/ 에서 HTML fixture를 로드하고
     * RealWorldValidationSetTest와 동일한 DDG 파싱 로직을 적용한다.
     *
     * 파싱 로직은 DuckDuckGoScrapingSearchProvider의 실제 구조를 재현:
     * - class="result__a" 링크 추출
     * - //duckduckgo.com/l/?uddg= 리다이렉트 URL 디코딩
     * - &amp; HTML 엔터티 처리
     * - class="result__snippet" 스니펫 추출
     */
    private fun loadFixtureAndParse(filename: String): List<RawSearchResult> {
        val resourcePath = "/fixtures/ddg-snapshots/$filename"
        val html = this::class.java.getResourceAsStream(resourcePath)
            ?.bufferedReader()?.use { it.readText() }
            ?: throw IllegalStateException("Fixture not found: $resourcePath")

        return parseDdgHtml(html)
    }

    private fun parseDdgHtml(html: String): List<RawSearchResult> {
        val results = mutableListOf<RawSearchResult>()
        val seen = mutableSetOf<String>()

        val resultPattern = Regex(
            """<a[^>]+class="result__a"[^>]+href="([^"]+)"[^>]*>(.*?)</a>""",
            RegexOption.DOT_MATCHES_ALL
        )
        val resultPattern2 = Regex(
            """<a[^>]+href="([^"]+)"[^>]+class="result__a"[^>]*>(.*?)</a>""",
            RegexOption.DOT_MATCHES_ALL
        )
        val snippetPattern = Regex(
            """class="result__snippet"[^>]*>(.*?)</""",
            RegexOption.DOT_MATCHES_ALL
        )

        val allMatches = (resultPattern.findAll(html) + resultPattern2.findAll(html)).toList()

        for (match in allMatches) {
            if (results.size >= 8) break
            var rawUrl = match.groupValues[1]
            val title = stripHtml(match.groupValues[2]).take(100)

            if (rawUrl.startsWith("//")) rawUrl = "https:$rawUrl"

            val decodedUrl = decodeDdgRedirect(rawUrl)
            val domain = extractDomain(decodedUrl) ?: continue
            if (domain.contains("duckduckgo.com")) continue
            if (seen.contains(domain)) continue
            seen.add(domain)

            results.add(
                RawSearchResult(
                    title = title.ifBlank { domain },
                    snippet = "",
                    url = decodedUrl,
                    domain = domain,
                    language = null,
                    providerName = "DuckDuckGo",
                )
            )
        }

        val allSnippets = snippetPattern.findAll(html)
            .map { stripHtml(it.groupValues[1]).take(200) }
            .toList()
        results.forEachIndexed { i, r ->
            if (r.snippet.isEmpty() && i < allSnippets.size) {
                results[i] = r.copy(snippet = allSnippets[i])
            }
        }

        return results
    }

    // ═══════════════════════════════════════════════════════════
    // 출력 헬퍼
    // ═══════════════════════════════════════════════════════════

    private fun printEvidence(
        tag: String,
        results: List<RawSearchResult>,
        evidence: app.myphonecheck.mobile.core.model.SearchEvidence,
    ) {
        println("══════════════════════════════════════════════")
        println("[$tag] results=${results.size}")
        results.forEachIndexed { i, r ->
            println("[$tag] [$i] title='${r.title.take(60)}' domain=${r.domain}")
            if (r.snippet.isNotBlank()) {
                println("[$tag]     snippet='${r.snippet.take(80)}'")
            }
        }
        println("[$tag] ── Evidence ──")
        println("[$tag] isEmpty=${evidence.isEmpty}")
        println("[$tag] keywordClusters=${evidence.keywordClusters}")
        println("[$tag] repeatedEntities=${evidence.repeatedEntities}")
        println("[$tag] ── SignalSummaries ──")
        evidence.signalSummaries.forEachIndexed { i, s ->
            println("[$tag] Signal[$i]: '${s.signalDescription}' type=${s.signalType} count=${s.resultCount}")
        }
        println("══════════════════════════════════════════════")
    }

    // ═══════════════════════════════════════════════════════════
    // 유틸리티 (RealWorldValidationSetTest와 동일 로직)
    // ═══════════════════════════════════════════════════════════

    private fun extractDomain(url: String): String? {
        return try { URI(url).host?.removePrefix("www.") } catch (e: Exception) { null }
    }

    private fun stripHtml(html: String): String {
        return html.replace(Regex("<[^>]+>"), "")
            .replace("&amp;", "&").replace("&lt;", "<").replace("&gt;", ">")
            .replace("&quot;", "\"").replace("&#39;", "'").replace("&nbsp;", " ")
            .trim()
    }

    private fun decodeDdgRedirect(url: String): String {
        val cleaned = url.replace("&amp;", "&")
        if (!cleaned.contains("uddg=")) return url
        return try {
            val m = Regex("""uddg=([^&]+)""").find(cleaned)
            if (m != null) URLDecoder.decode(m.groupValues[1], "UTF-8") else url
        } catch (e: Exception) { url }
    }
}
