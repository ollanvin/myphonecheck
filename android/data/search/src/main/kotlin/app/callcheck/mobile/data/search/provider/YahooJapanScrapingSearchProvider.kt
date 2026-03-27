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
 * Yahoo Japan 웹 검색 온디바이스 스크래핑 Provider.
 *
 * 日本 시장 전용. Yahoo Japan 검색 결과를 온디바이스로 파싱한다.
 * 전화번호 검색 시 스팸 신고, 업체 정보, 블로그 리뷰를 우선 노출한다.
 *
 * API 키 불필요. 서버 불필요. 비용 불필요.
 * 디바이스에서 직접 HTTP 요청 → HTML 수신 → 텍스트 추출.
 *
 * 구조 원칙:
 * - Yahoo Japan API 사용 절대 금지
 * - 스크래핑 실패 시 → Google/DuckDuckGo fallback
 * - 검색은 "디바이스가 직접 하는 행위"
 */
class YahooJapanScrapingSearchProvider(
    private val httpClient: OkHttpClient,
    override val providerName: String = "YahooJapan",
) : SearchProvider {

    private companion object {
        private const val TAG = "YahooJapanScrapingProvider"
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
                    performYahooJapanSearch(phoneNumber)
                }
                val responseTime = System.currentTimeMillis() - startTime
                Log.d(TAG, "Yahoo Japan search: ${results.size} results in ${responseTime}ms")

                SearchProviderResult(
                    provider = providerName,
                    results = results,
                    responseTimeMs = responseTime,
                    success = true,
                )
            } catch (e: Exception) {
                Log.e(TAG, "Yahoo Japan search failed", e)
                SearchProviderResult(
                    provider = providerName,
                    results = emptyList(),
                    responseTimeMs = System.currentTimeMillis() - startTime,
                    success = false,
                    error = e.message,
                )
            }
        } ?: run {
            Log.w(TAG, "Yahoo Japan search timeout")
            SearchProviderResult(
                provider = providerName,
                results = emptyList(),
                responseTimeMs = System.currentTimeMillis() - startTime,
                success = false,
                error = "Timeout",
            )
        }
    }

    private fun performYahooJapanSearch(phoneNumber: String): List<RawSearchResult> {
        val query = URLEncoder.encode(phoneNumber, "UTF-8")
        val url = "https://search.yahoo.co.jp/search?p=$query&ei=UTF-8"

        val request = Request.Builder()
            .url(url)
            .header("User-Agent", USER_AGENT)
            .header("Accept-Language", "ja,en;q=0.9")
            .get()
            .build()

        val response = httpClient.newCall(request).execute()

        if (!response.isSuccessful) {
            Log.w(TAG, "Yahoo Japan returned HTTP ${response.code}")
            return emptyList()
        }

        val html = response.body?.string() ?: return emptyList()
        return parseYahooJapanResults(html)
    }

    /**
     * Yahoo Japan 검색 결과 HTML 파싱.
     *
     * Yahoo Japan SSR(Server-Side Rendering) 구조:
     * - 결과 컨테이너: <div class="sw-Card"> 또는 <section class="Algo">
     * - 제목 링크: <a class="sw-Card__title"> 또는 <h3><a href="...">
     * - 스니펫: <div class="sw-Card__snippet"> 또는 <p class="sw-Card__description">
     * - 리다이렉트: r.search.yahoo.com 리다이렉트 URL에서 실제 URL 추출 필요
     *   패턴: RU=([^/]+)/ 형태로 base64 인코딩된 URL 추출
     */
    private fun parseYahooJapanResults(html: String): List<RawSearchResult> {
        val results = mutableListOf<RawSearchResult>()

        Log.d(TAG, "HTML length: ${html.length}")

        val seenUrls = mutableSetOf<String>()

        // Pattern 1: sw-Card 컨테이너 기반 파싱
        val cardPattern = Regex("""<div\s+class="sw-Card"[^>]*>""")
        val cardMatches = cardPattern.findAll(html).toList()
        Log.d(TAG, "Found ${cardMatches.size} sw-Card containers")

        for (match in cardMatches) {
            if (results.size >= MAX_RESULTS) break

            val matchStart = match.range.first
            val contextEnd = minOf(html.length, matchStart + 3000)
            val context = html.substring(matchStart, contextEnd)

            // Extract link
            val linkPattern = Regex("""<a\s+[^>]*href="([^"]+)"[^>]*class="sw-Card__title""")
            val linkMatch = linkPattern.find(context)
            if (linkMatch == null) continue

            var rawUrl = linkMatch.groupValues[1]
                .replace("&amp;", "&")

            // Extract actual URL from Yahoo redirect
            rawUrl = extractRealUrl(rawUrl)

            // Skip Yahoo internal domains
            if (rawUrl.contains("yahoo.co.jp") || rawUrl.contains("yahoo.com") ||
                rawUrl.contains("yimg.jp") || !rawUrl.startsWith("http")) {
                continue
            }

            if (!seenUrls.add(rawUrl)) continue

            val domain = extractDomain(rawUrl)

            // Extract title from sw-Card__title
            val titlePattern = Regex("""<a\s+[^>]*class="sw-Card__title"[^>]*>([^<]+)""")
            val titleMatch = titlePattern.find(context)
            val title = titleMatch?.groupValues?.get(1)?.let { cleanHtml(it) }
                ?: domain

            // Extract snippet from sw-Card__snippet
            val snippetPattern = Regex("""<div\s+class="sw-Card__snippet"[^>]*>([^<]+)""")
            val snippetMatch = snippetPattern.find(context)
            val snippet = snippetMatch?.groupValues?.get(1)?.let { cleanHtml(it) }
                ?: ""

            Log.d(TAG, "Result: url=$rawUrl, title=$title, snippet=${snippet.take(50)}")

            results.add(
                RawSearchResult(
                    title = title.take(100),
                    snippet = snippet.take(200),
                    url = rawUrl,
                    domain = domain,
                    language = "ja",
                )
            )
        }

        // Pattern 2: Algo 컨테이너 기반 파싱 (대체 패턴)
        if (results.isEmpty()) {
            Log.d(TAG, "No sw-Card results, trying Algo container pattern")

            val algoPattern = Regex("""<section\s+class="Algo"[^>]*>""")
            val algoMatches = algoPattern.findAll(html).toList()

            for (match in algoMatches) {
                if (results.size >= MAX_RESULTS) break

                val matchStart = match.range.first
                val contextEnd = minOf(html.length, matchStart + 2500)
                val context = html.substring(matchStart, contextEnd)

                // Extract link from h3 > a
                val h3LinkPattern = Regex("""<h3[^>]*>\s*<a\s+[^>]*href="([^"]+)"[^>]*>([^<]+)""")
                val h3LinkMatch = h3LinkPattern.find(context)
                if (h3LinkMatch == null) continue

                var rawUrl = h3LinkMatch.groupValues[1]
                    .replace("&amp;", "&")

                rawUrl = extractRealUrl(rawUrl)

                if (rawUrl.contains("yahoo.co.jp") || rawUrl.contains("yahoo.com") ||
                    rawUrl.contains("yimg.jp") || !rawUrl.startsWith("http")) {
                    continue
                }

                if (!seenUrls.add(rawUrl)) continue

                val domain = extractDomain(rawUrl)
                val title = cleanHtml(h3LinkMatch.groupValues[2])

                // Extract snippet from description or paragraph
                val descPattern = Regex("""<p\s+class="sw-Card__description"[^>]*>([^<]+)""")
                val descMatch = descPattern.find(context)
                val snippet = descMatch?.groupValues?.get(1)?.let { cleanHtml(it) }
                    ?: ""

                Log.d(TAG, "Result (Algo): url=$rawUrl, title=$title, snippet=${snippet.take(50)}")

                results.add(
                    RawSearchResult(
                        title = title.take(100),
                        snippet = snippet.take(200),
                        url = rawUrl,
                        domain = domain,
                        language = "ja",
                    )
                )
            }
        }

        // Pattern 3: Fallback - Generic external links
        if (results.isEmpty()) {
            Log.d(TAG, "No structured results, trying fallback link extraction")

            val genericLinkPattern = Regex(
                """<a[^>]+href="(https?://[^"]+)"[^>]*>"""
            )
            val genericMatches = genericLinkPattern.findAll(html).toList()

            for (match in genericMatches) {
                if (results.size >= MAX_RESULTS) break

                val rawUrl = match.groupValues[1]
                    .replace("&amp;", "&")

                val actualUrl = extractRealUrl(rawUrl)

                if (actualUrl.contains("yahoo.co.jp") || actualUrl.contains("yahoo.com") ||
                    actualUrl.contains("yimg.jp") || !actualUrl.startsWith("http")) {
                    continue
                }

                if (!seenUrls.add(actualUrl)) continue

                val domain = extractDomain(actualUrl)
                if (domain == "unknown") continue

                val matchStart = match.range.first
                val contextEnd = minOf(html.length, matchStart + 1500)
                val context = html.substring(matchStart, contextEnd)
                val title = extractTextBlock(context)

                if (title.length >= 5) {
                    results.add(
                        RawSearchResult(
                            title = title.take(100),
                            snippet = "".take(200),
                            url = actualUrl,
                            domain = domain,
                            language = "ja",
                        )
                    )
                }
            }
        }

        Log.d(TAG, "Parsed ${results.size} results from Yahoo Japan HTML")
        return results
    }

    /**
     * Yahoo リダイレクト URL から実際の URL を抽出する.
     *
     * Yahoo Japan は r.search.yahoo.com にリダイレクト URL を経由する.
     * RU=BASE64_ENCODED_URL/ のパターンから実際の URL をデコードする.
     */
    private fun extractRealUrl(url: String): String {
        // If URL doesn't contain r.search.yahoo.com redirect, return as-is
        if (!url.contains("r.search.yahoo.com")) {
            return url
        }

        // Extract RU parameter value
        val ruPattern = Regex("""RU=([^/]+)/""")
        val ruMatch = ruPattern.find(url)
        if (ruMatch != null) {
            val encodedUrl = ruMatch.groupValues[1]
            return try {
                // Yahoo uses URL-safe base64, try to decode
                val decoded = java.util.Base64.getUrlDecoder().decode(encodedUrl).toString(Charsets.UTF_8)
                if (decoded.startsWith("http")) {
                    decoded
                } else {
                    url
                }
            } catch (e: Exception) {
                Log.d(TAG, "Failed to decode RU parameter: $encodedUrl")
                url
            }
        }

        return url
    }

    private fun extractTextBlock(context: String): String {
        val textBlocks = context.split(Regex("<[^>]+>"))
            .map { cleanHtml(it.trim()) }
            .filter { it.length in 5..200 && !it.startsWith("http") && !it.contains("{") && !it.contains("function") }

        return textBlocks.firstOrNull() ?: ""
    }

    private fun cleanHtml(text: String): String {
        return text.replace(Regex("<[^>]+>"), "")
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
            .replace("&nbsp;", " ")
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
