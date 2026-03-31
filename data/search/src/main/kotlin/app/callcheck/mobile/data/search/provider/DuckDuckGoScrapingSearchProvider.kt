package app.callcheck.mobile.data.search.provider

import android.util.Log
import app.callcheck.mobile.data.search.RawSearchResult
import app.callcheck.mobile.data.search.SearchProvider
import app.callcheck.mobile.data.search.SearchProviderResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder

/**
 * DuckDuckGo HTML 버전 온디바이스 스크래핑 Provider.
 *
 * 글로벌 fallback Provider. DuckDuckGo HTML 버전(html.duckduckgo.com)을
 * 온디바이스에서 파싱한다.
 *
 * html.duckduckgo.com은 순수 SSR(Server-Side Rendered) — JS 렌더링 불필요.
 * 가장 안정적인 스크래핑 대상이며, Google이 JS-only 페이지를 반환할 때
 * 핵심 보강 역할을 한다.
 *
 * API 키 불필요. 서버 불필요. 비용 불필요.
 * 디바이스에서 직접 HTTP 요청 → HTML 수신 → 텍스트 추출.
 *
 * 특징:
 * - 190+ 국가 모두 지원 (글로벌 coverage)
 * - countryCode 파라미터로 Accept-Language 동적 결정
 * - URL 리다이렉트: //duckduckgo.com/l/?uddg=ENCODED_URL
 *   → uddg 파라미터 추출 후 URL 디코딩으로 실제 목적지 URL 획득
 * - DuckDuckGo 내부 링크 필터링: duckduckgo.com, duck.com 제외
 *
 * 구조 원칙:
 * - DuckDuckGo API 사용 절대 금지
 * - 스크래핑 실패 시 → 파싱 방식 변경 또는 fallback
 * - 검색은 "디바이스가 직접 하는 행위"
 */
class DuckDuckGoScrapingSearchProvider(
    private val httpClient: OkHttpClient,
    override val providerName: String = "DuckDuckGo",
) : SearchProvider {

    private companion object {
        private const val TAG = "DuckDuckGoScrapingProvider"
        private const val TIMEOUT_MS = 1000L
        private const val MAX_RESULTS = 8
        private const val USER_AGENT =
            "Mozilla/5.0 (Linux; Android 13; Pixel 7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
    }

    override suspend fun search(
        phoneNumber: String,
        countryCode: String?,
    ): SearchProviderResult {
        val startTime = System.currentTimeMillis()

        return withTimeoutOrNull(TIMEOUT_MS) {
            try {
                val results = withContext(Dispatchers.IO) {
                    performDuckDuckGoSearch(phoneNumber, countryCode)
                }
                val responseTime = System.currentTimeMillis() - startTime
                Log.d(TAG, "DuckDuckGo search: ${results.size} results in ${responseTime}ms")

                SearchProviderResult(
                    provider = providerName,
                    results = results,
                    responseTimeMs = responseTime,
                    success = true,
                )
            } catch (e: Exception) {
                Log.e(TAG, "DuckDuckGo search failed", e)
                SearchProviderResult(
                    provider = providerName,
                    results = emptyList(),
                    responseTimeMs = System.currentTimeMillis() - startTime,
                    success = false,
                    error = e.message,
                )
            }
        } ?: run {
            Log.w(TAG, "DuckDuckGo search timeout")
            SearchProviderResult(
                provider = providerName,
                results = emptyList(),
                responseTimeMs = System.currentTimeMillis() - startTime,
                success = false,
                error = "Timeout",
            )
        }
    }

    private fun performDuckDuckGoSearch(
        phoneNumber: String,
        countryCode: String?,
    ): List<RawSearchResult> {
        val query = URLEncoder.encode(phoneNumber, "UTF-8")
        val url = "https://html.duckduckgo.com/html/?q=$query"

        val acceptLanguage = getAcceptLanguageForCountry(countryCode)

        val request = Request.Builder()
            .url(url)
            .header("User-Agent", USER_AGENT)
            .header("Accept-Language", acceptLanguage)
            .get()
            .build()

        val response = httpClient.newCall(request).execute()

        if (!response.isSuccessful) {
            Log.w(TAG, "DuckDuckGo returned HTTP ${response.code}")
            return emptyList()
        }

        val html = response.body?.string() ?: return emptyList()
        return parseDuckDuckGoResults(html)
    }

    /**
     * DuckDuckGo 검색 결과 HTML 파싱.
     *
     * html.duckduckgo.com은 100% SSR — JS 렌더링 없이 순수 HTML로 반환됨.
     * 다음 패턴으로 결과를 추출:
     *
     * 1. 결과 컨테이너: <div class="result results_links results_links_deep web-result">
     * 2. 제목 링크: <a class="result__a" href="...">
     * 3. 스니펫: <a class="result__snippet">
     * 4. URL 리다이렉트: //duckduckgo.com/l/?uddg=ENCODED_URL
     *    → uddg 파라미터에서 URL 디코딩으로 실제 URL 추출
     */
    private fun parseDuckDuckGoResults(html: String): List<RawSearchResult> {
        val results = mutableListOf<RawSearchResult>()

        Log.d(TAG, "HTML length: ${html.length}")

        // Pattern 1: result__a 클래스 링크에서 uddg URL 추출 (우선순위: 높음)
        val resultAPattern = Regex(
            """<a\s+class="result__a"[^>]*href="([^"]*\?uddg=([^"&]+))[^"]*"[^>]*>"""
        )
        val resultAMatches = resultAPattern.findAll(html).toList()
        Log.d(TAG, "Found ${resultAMatches.size} result__a links")

        val seenUrls = mutableSetOf<String>()

        for (match in resultAMatches) {
            if (results.size >= MAX_RESULTS) break

            try {
                val uddgParam = match.groupValues[2]
                val decodedUrl = java.net.URLDecoder.decode(uddgParam, "UTF-8")

                // DuckDuckGo 내부 링크 제외
                if (decodedUrl.contains("duckduckgo.com") || decodedUrl.contains("duck.com")) {
                    Log.d(TAG, "Skipping DDG internal link: $decodedUrl")
                    continue
                }

                if (!seenUrls.add(decodedUrl)) continue

                val domain = extractDomain(decodedUrl)

                // 제목과 스니펫 추출
                val matchStart = match.range.first
                val contextEnd = minOf(html.length, matchStart + 1500)
                val context = html.substring(matchStart, contextEnd)

                val titleAndSnippet = extractDuckDuckGoTextBlocks(context)
                val title = titleAndSnippet.first.ifEmpty { domain }
                val snippet = titleAndSnippet.second

                Log.d(TAG, "Result: url=$decodedUrl, title=$title, snippet=${snippet.take(50)}")

                results.add(
                    RawSearchResult(
                        title = title.take(100),
                        snippet = snippet.take(200),
                        url = decodedUrl,
                        domain = domain,
                        language = getLanguageForCountry(extractCountryFromDomain(domain)),
                    )
                )
            } catch (e: Exception) {
                Log.d(TAG, "Failed to parse uddg URL: ${e.message}")
            }
        }

        // Fallback Pattern 2: 일반 result__ 클래스 텍스트 추출
        if (results.isEmpty()) {
            Log.d(TAG, "No result__a links found, trying generic external links")
            val externalLinkPattern = Regex(
                """<a[^>]+href="(https?://(?!(?:www\.)?duckduckgo\.com|duck\.com)[^"]{15,})"[^>]*>"""
            )
            val externalMatches = externalLinkPattern.findAll(html).toList()

            for (match in externalMatches) {
                if (results.size >= MAX_RESULTS) break

                val rawUrl = match.groupValues[1].replace("&amp;", "&")
                if (!seenUrls.add(rawUrl)) continue

                val domain = extractDomain(rawUrl)
                if (domain == "unknown") continue

                val matchStart = match.range.first
                val contextEnd = minOf(html.length, matchStart + 1000)
                val context = html.substring(matchStart, contextEnd)

                val titleAndSnippet = extractDuckDuckGoTextBlocks(context)

                results.add(
                    RawSearchResult(
                        title = titleAndSnippet.first.ifEmpty { domain }.take(100),
                        snippet = titleAndSnippet.second.take(200),
                        url = rawUrl,
                        domain = domain,
                        language = getLanguageForCountry(extractCountryFromDomain(domain)),
                    )
                )
            }
        }

        if (results.isEmpty()) {
            Log.w(TAG, "DuckDuckGo returned no parseable results. " +
                "This may indicate rate limiting or structural changes.")
        }

        Log.d(TAG, "Parsed ${results.size} results from DuckDuckGo HTML")
        return results
    }

    /**
     * DuckDuckGo 텍스트 블록 추출.
     * 제목과 스니펫을 result__snippet 클래스 패턴에서 추출.
     */
    private fun extractDuckDuckGoTextBlocks(context: String): Pair<String, String> {
        var title = ""
        var snippet = ""

        // 직접 텍스트 노드 추출: > 다음에 오는 텍스트
        val textNodes = context.split(Regex("<[^>]+>"))
            .map { cleanHtml(it.trim()) }
            .filter { it.isNotEmpty() }

        for (node in textNodes) {
            if (node.length < 3) continue
            if (node.contains("{") || node.contains("function") || node.startsWith("http")) continue

            if (title.isEmpty() && node.length >= 5) {
                title = node
            } else if (snippet.isEmpty() && title.isNotEmpty() && node.length >= 10) {
                snippet = node
                break
            }
        }

        // snippet 추출 실패 시 result__snippet 클래스에서 추출 시도
        if (snippet.isEmpty()) {
            val snippetPattern = Regex("""result__snippet">([^<]{5,})""")
            val snippetMatch = snippetPattern.find(context)
            if (snippetMatch != null) {
                snippet = cleanHtml(snippetMatch.groupValues[1])
            }
        }

        return Pair(title, snippet)
    }

    /**
     * countryCode를 기반으로 Accept-Language 헤더 생성.
     *
     * 글로벌 대응: KR → ko-KR, US → en-US, JP → ja-JP, BR → pt-BR, etc.
     * 기본값: en-US (fallback)
     */
    private fun getAcceptLanguageForCountry(countryCode: String?): String {
        return when (countryCode?.uppercase()) {
            "KR" -> "ko-KR,ko;q=0.9,en;q=0.8"
            "US", "GB", "AU", "CA" -> "en-US,en;q=0.9"
            "JP" -> "ja-JP,ja;q=0.9,en;q=0.8"
            "CN" -> "zh-CN,zh;q=0.9,en;q=0.8"
            "BR" -> "pt-BR,pt;q=0.9,en;q=0.8"
            "ES" -> "es-ES,es;q=0.9,en;q=0.8"
            "FR" -> "fr-FR,fr;q=0.9,en;q=0.8"
            "DE" -> "de-DE,de;q=0.9,en;q=0.8"
            "IT" -> "it-IT,it;q=0.9,en;q=0.8"
            "RU" -> "ru-RU,ru;q=0.9,en;q=0.8"
            "IN" -> "hi-IN,en-IN,en;q=0.9"
            "MX" -> "es-MX,es;q=0.9,en;q=0.8"
            "TH" -> "th-TH,th;q=0.9,en;q=0.8"
            "VI" -> "vi-VN,vi;q=0.9,en;q=0.8"
            "ID" -> "id-ID,id;q=0.9,en;q=0.8"
            "TR" -> "tr-TR,tr;q=0.9,en;q=0.8"
            else -> "en-US,en;q=0.9"
        }
    }

    /**
     * countryCode를 기반으로 언어 코드 결정.
     */
    private fun getLanguageForCountry(countryCode: String?): String {
        return when (countryCode?.uppercase()) {
            "KR" -> "ko"
            "US", "GB", "AU", "CA" -> "en"
            "JP" -> "ja"
            "CN" -> "zh"
            "BR", "PT" -> "pt"
            "ES", "MX", "AR" -> "es"
            "FR" -> "fr"
            "DE" -> "de"
            "IT" -> "it"
            "RU" -> "ru"
            "IN" -> "hi"
            "TH" -> "th"
            "VI" -> "vi"
            "ID" -> "id"
            "TR" -> "tr"
            else -> "en"
        }
    }

    /**
     * URL의 도메인에서 국가 코드 추출 시도.
     * 예: naver.com → KR, google.co.jp → JP
     */
    private fun extractCountryFromDomain(domain: String): String? {
        return when {
            domain.contains("naver.") || domain.contains(".kr") -> "KR"
            domain.contains(".jp") -> "JP"
            domain.contains(".cn") -> "CN"
            domain.contains(".br") -> "BR"
            domain.contains(".es") -> "ES"
            domain.contains(".fr") -> "FR"
            domain.contains(".de") -> "DE"
            domain.contains(".it") -> "IT"
            domain.contains(".ru") -> "RU"
            domain.contains(".th") -> "TH"
            domain.contains(".id") -> "ID"
            domain.contains(".tr") -> "TR"
            else -> null
        }
    }

    private fun cleanHtml(text: String): String {
        return text.replace(Regex("<[^>]+>"), "")
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
            .replace("&nbsp;", " ")
            .replace("&#x27;", "'")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    private fun extractDomain(url: String): String {
        return try {
            java.net.URI(url).host ?: "unknown"
        } catch (e: Exception) {
            "unknown"
        }
    }
}
