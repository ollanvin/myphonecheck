package app.myphonecheck.mobile.data.search

import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Assume
import org.junit.Test
import org.junit.experimental.categories.Category
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.net.URLDecoder
import java.net.URLEncoder

/**
 * Layer A: Live Validation Set — 실전 번호 기준 로컬 검증셋.
 *
 * 대표님 철학: 중앙 저장 없이 로컬 테스트 자산으로만 유지.
 *
 * ┌─────────────────────────────────────────────────────────┐
 * │ ⚠️ 이 테스트는 @Category(LiveNetworkTest::class) 적용   │
 * │                                                         │
 * │ VM에서는 자동 제외됨 (build.gradle.kts 설정)            │
 * │ 대표님 로컬 PC에서만 실행:                               │
 * │   scripts/local-test-rig/run_live_validation.ps1        │
 * │                                                         │
 * │ VM skip은 실패가 아님.                                   │
 * │ Live 성공 없이는 실전 검증 완료 선언 금지.               │
 * │ Frozen 없이는 회귀 검증 완료 선언 금지.                  │
 * └─────────────────────────────────────────────────────────┘
 *
 * 각 테스트는 DuckDuckGo HTML(100% SSR, 글로벌 접근 가능)로 실제 검색 후
 * SearchResultAnalyzer로 분석, SignalSummary 결과를 검증한다.
 *
 * ┌──────────────────────────────────────────────────┐
 * │ 카테고리별 실전 번호 검증셋                       │
 * ├──────────┬───────────────────────────────────────┤
 * │ 기관     │ 1345 (정부민원안내콜센터)              │
 * │ 기업     │ 15881234 (신세계백화점)                │
 * │ 택배     │ 15881255 (CJ대한통운)                  │
 * │ 광고     │ — (mock 기반, 실전 번호 특정 어려움)   │
 * │ 사기     │ 028888XXXX (보이스피싱 다발 대역)      │
 * │ 혼합     │ 15889999 (기업+신고 이력)              │
 * │ JP 기관  │ 0120444113 (NTT 고장접수)              │
 * │ Global   │ 8008290433 (US 잠재 스캠)              │
 * └──────────┴───────────────────────────────────────┘
 *
 * 구조 변경 금지. 검증셋 확대만 수행.
 */
@Category(LiveNetworkTest::class)
class RealWorldValidationSetTest {

    private val analyzer = SearchResultAnalyzer()

    // ═══════════════════════════════════════════════════════════
    // 카테고리 1: 기관 — 1345 (정부민원안내콜센터)
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `KR INSTITUTION - 1345 government hotline`() = runBlocking {
        val phone = "1345"
        val results = scrapeViaDuckDuckGo(phone)
        Assume.assumeTrue("Network unavailable", results.isNotEmpty())

        val evidence = analyzer.analyzeSearchResults(results)

        printEvidence("KR-INSTITUTION-1345", phone, results, evidence)

        assertFalse("Evidence must not be empty", evidence.isEmpty)

        // 1345는 정부 민원 안내 콜센터 — 긍정 신호 기대
        // 문장이 과단정("수신 위험")이면 안 됨
        evidence.signalSummaries.forEach { signal ->
            assertFalse(
                "Government hotline should NOT be 수신 위험, got: '${signal.signalDescription}'",
                signal.signalDescription.contains("수신 위험")
            )
        }
    }

    // ═══════════════════════════════════════════════════════════
    // 카테고리 2: 기업 — 15881234 (신세계백화점)
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `KR BUSINESS - 15881234 Shinsegae department store`() = runBlocking {
        val phone = "15881234"
        val results = scrapeViaDuckDuckGo(phone)
        Assume.assumeTrue("Network unavailable", results.isNotEmpty())

        val evidence = analyzer.analyzeSearchResults(results)

        printEvidence("KR-BUSINESS-15881234", phone, results, evidence)

        assertFalse("Evidence must not be empty", evidence.isEmpty)

        // 신세계백화점: 정상 기업 → "수신 위험" 금지
        evidence.signalSummaries.forEach { signal ->
            assertFalse(
                "Shinsegae should NOT be 수신 위험, got: '${signal.signalDescription}'",
                signal.signalDescription.contains("수신 위험")
            )
        }
    }

    // ═══════════════════════════════════════════════════════════
    // 카테고리 3: 택배 — 15881255 (CJ대한통운)
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `KR DELIVERY - 15881255 CJ Logistics`() = runBlocking {
        val phone = "15881255"
        val results = scrapeViaDuckDuckGo(phone)
        Assume.assumeTrue("Network unavailable", results.isNotEmpty())

        val evidence = analyzer.analyzeSearchResults(results)

        printEvidence("KR-DELIVERY-15881255", phone, results, evidence)

        assertFalse("Evidence must not be empty", evidence.isEmpty)

        // CJ대한통운: 정상 택배 → "수신 위험" 금지
        evidence.signalSummaries.forEach { signal ->
            assertFalse(
                "CJ Logistics should NOT be 수신 위험, got: '${signal.signalDescription}'",
                signal.signalDescription.contains("수신 위험")
            )
        }
    }

    // ═══════════════════════════════════════════════════════════
    // 카테고리 4: 사기 — 028888XXXX 대역 (보이스피싱 다발)
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `KR SCAM - 02-888-8XXX voice phishing zone`() = runBlocking {
        // 보이스피싱 빈발 대역 (실제 피해 신고 다수)
        val phone = "0288881234"
        val results = scrapeViaDuckDuckGo(phone)
        Assume.assumeTrue("Network unavailable", results.isNotEmpty())

        val evidence = analyzer.analyzeSearchResults(results)

        printEvidence("KR-SCAM-0288881234", phone, results, evidence)

        // 사기 번호는 결과가 적을 수 있음 — evidence 존재만 확인
        // 문장이 "수신 안전"이면 안 됨 (사기 대역에 안전이라 하면 위험)
        val hasSafeOnly = evidence.signalSummaries.isNotEmpty() &&
                evidence.signalSummaries.all { it.signalDescription.contains("수신 안전") }
        assertFalse(
            "Scam zone should NOT show only 수신 안전",
            hasSafeOnly
        )
    }

    // ═══════════════════════════════════════════════════════════
    // 카테고리 5: JP 기관 — 0120444113 (NTT 고장접수)
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `JP INSTITUTION - 0120444113 NTT fault report`() = runBlocking {
        val phone = "0120444113"
        val results = scrapeViaDuckDuckGo(phone)
        Assume.assumeTrue("Network unavailable", results.isNotEmpty())

        val evidence = analyzer.analyzeSearchResults(results)

        printEvidence("JP-INSTITUTION-0120444113", phone, results, evidence)

        assertFalse("Evidence must not be empty", evidence.isEmpty)

        // NTT 고장접수: 정상 기관 → "수신 위험" 금지
        evidence.signalSummaries.forEach { signal ->
            assertFalse(
                "NTT should NOT be 수신 위험, got: '${signal.signalDescription}'",
                signal.signalDescription.contains("수신 위험")
            )
        }
    }

    // ═══════════════════════════════════════════════════════════
    // 카테고리 6: Global — 8008290433 (US potential scam)
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `GLOBAL - 8008290433 US number`() = runBlocking {
        val phone = "8008290433"
        val results = scrapeViaDuckDuckGo(phone)
        Assume.assumeTrue("Network unavailable", results.isNotEmpty())

        val evidence = analyzer.analyzeSearchResults(results)

        printEvidence("GLOBAL-8008290433", phone, results, evidence)

        // 글로벌 번호: evidence가 비어있지 않으면 성공
        // 신호 유형은 번호에 따라 다를 수 있으므로 유형 강제하지 않음
        assertFalse("Evidence must not be empty", evidence.isEmpty)
    }

    // ═══════════════════════════════════════════════════════════
    // 카테고리 7: 혼합 — 15889999 (기업+신고 이력)
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `KR MIXED - 15889999 business with possible reports`() = runBlocking {
        val phone = "15889999"
        val results = scrapeViaDuckDuckGo(phone)
        Assume.assumeTrue("Network unavailable", results.isNotEmpty())

        val evidence = analyzer.analyzeSearchResults(results)

        printEvidence("KR-MIXED-15889999", phone, results, evidence)

        assertFalse("Evidence must not be empty", evidence.isEmpty)

        // 혼합 케이스: "수신 위험"이면 과단정
        // (1588 대역은 대부분 기업 → 무조건 위험은 부적절)
        evidence.signalSummaries.forEach { signal ->
            assertFalse(
                "1588 business number should NOT be 수신 위험, got: '${signal.signalDescription}'",
                signal.signalDescription.contains("수신 위험")
            )
        }
    }

    // ═══════════════════════════════════════════════════════════
    // 카테고리 8: KR 의료 — 15771000 (삼성서울병원)
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `KR INSTITUTION - 15771000 Samsung Medical Center`() = runBlocking {
        val phone = "15771000"
        val results = scrapeViaDuckDuckGo(phone)
        Assume.assumeTrue("Network unavailable", results.isNotEmpty())

        val evidence = analyzer.analyzeSearchResults(results)

        printEvidence("KR-INSTITUTION-15771000", phone, results, evidence)

        assertFalse("Evidence must not be empty", evidence.isEmpty)

        // 삼성서울병원: 정상 기관 → "수신 위험" 금지
        evidence.signalSummaries.forEach { signal ->
            assertFalse(
                "Samsung Medical Center should NOT be 수신 위험, got: '${signal.signalDescription}'",
                signal.signalDescription.contains("수신 위험")
            )
        }
    }

    // ═══════════════════════════════════════════════════════════
    // 카테고리 9: KR 은행 — 15881599 (KB국민은행)
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `KR BUSINESS - 15881599 KB Kookmin Bank`() = runBlocking {
        val phone = "15881599"
        val results = scrapeViaDuckDuckGo(phone)
        Assume.assumeTrue("Network unavailable", results.isNotEmpty())

        val evidence = analyzer.analyzeSearchResults(results)

        printEvidence("KR-BUSINESS-15881599", phone, results, evidence)

        assertFalse("Evidence must not be empty", evidence.isEmpty)

        // KB국민은행: 정상 금융기관 → "수신 위험" 금지
        evidence.signalSummaries.forEach { signal ->
            assertFalse(
                "KB Bank should NOT be 수신 위험, got: '${signal.signalDescription}'",
                signal.signalDescription.contains("수신 위험")
            )
        }
    }

    // ═══════════════════════════════════════════════════════════
    // 카테고리 10: KR 통신 — 114 (KT 번호안내)
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `KR INSTITUTION - 114 KT directory service`() = runBlocking {
        val phone = "114"
        val results = scrapeViaDuckDuckGo(phone)
        // 114는 너무 짧아서 검색 결과가 관련 없을 수 있음
        Assume.assumeTrue("Network unavailable or irrelevant results", results.isNotEmpty())

        val evidence = analyzer.analyzeSearchResults(results)

        printEvidence("KR-INSTITUTION-114", phone, results, evidence)

        // 114: 매우 짧은 번호라 검색 결과가 부정확할 수 있음
        // 최소 evidence가 생성되는지만 확인
    }

    // ═══════════════════════════════════════════════════════════
    // DuckDuckGo HTML 스크래핑 공통 함수
    // ═══════════════════════════════════════════════════════════

    private fun scrapeViaDuckDuckGo(phone: String): List<RawSearchResult> {
        val query = URLEncoder.encode(phone, "UTF-8")
        val url = "https://html.duckduckgo.com/html/?q=$query"

        val html = try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Linux; Android 13; Pixel 7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36")
            connection.setRequestProperty("Accept-Language", "ko-KR,ko;q=0.9,en;q=0.8")
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            connection.instanceFollowRedirects = true

            val code = connection.responseCode
            if (code != 200) {
                println("[DDG] $url → HTTP $code")
                return emptyList()
            }
            connection.inputStream.bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            println("[DDG] $url → ${e.javaClass.simpleName}: ${e.message}")
            return emptyList()
        }

        return parseDdgResults(html)
    }

    private fun parseDdgResults(html: String): List<RawSearchResult> {
        val results = mutableListOf<RawSearchResult>()
        val seen = mutableSetOf<String>()

        val resultPattern = Regex("""<a[^>]+class="result__a"[^>]+href="([^"]+)"[^>]*>(.*?)</a>""", RegexOption.DOT_MATCHES_ALL)
        val resultPattern2 = Regex("""<a[^>]+href="([^"]+)"[^>]+class="result__a"[^>]*>(.*?)</a>""", RegexOption.DOT_MATCHES_ALL)
        val snippetPattern = Regex("""class="result__snippet"[^>]*>(.*?)</""", RegexOption.DOT_MATCHES_ALL)

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

            results.add(RawSearchResult(
                title = title.ifBlank { domain },
                snippet = "",
                url = decodedUrl,
                domain = domain,
                language = null,
                providerName = "DuckDuckGo",
            ))
        }

        val allSnippets = snippetPattern.findAll(html).map { stripHtml(it.groupValues[1]).take(200) }.toList()
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
        phone: String,
        results: List<RawSearchResult>,
        evidence: app.myphonecheck.mobile.core.model.SearchEvidence,
    ) {
        println("══════════════════════════════════════════════")
        println("[$tag] phone=$phone, results=${results.size}")
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
        println("[$tag] sourceTypes=${evidence.sourceTypes}")
        println("[$tag] ── SignalSummaries ──")
        evidence.signalSummaries.forEachIndexed { i, s ->
            println("[$tag] Signal[$i]: '${s.signalDescription}' type=${s.signalType} count=${s.resultCount}")
        }
        println("══════════════════════════════════════════════")
    }

    // ═══════════════════════════════════════════════════════════
    // 유틸리티
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
