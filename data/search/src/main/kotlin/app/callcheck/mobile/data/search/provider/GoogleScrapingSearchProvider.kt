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
 * Google 웹 검색 온디바이스 스크래핑 Provider.
 *
 * Google 모바일 검색 페이지에서 전화번호 검색 결과를 직접 파싱한다.
 * API 키 불필요. 서버 불필요. 비용 불필요.
 * 디바이스에서 직접 HTTP 요청 → HTML 수신 → 텍스트 추출.
 *
 * 구조 원칙:
 * - 외부 API 사용 절대 금지 (Google Custom Search API 포함)
 * - 스크래핑 실패 시 → 파싱 방식 변경 또는 다른 엔진으로 fallback
 * - 절대 "API로 도망" 금지
 */
class GoogleScrapingSearchProvider(
    private val httpClient: OkHttpClient,
    override val providerName: String = "Google",
) : SearchProvider {

    private companion object {
        private const val TAG = "GoogleScrapingProvider"
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
                    performGoogleSearch(phoneNumber)
                }
                val responseTime = System.currentTimeMillis() - startTime
                Log.d(TAG, "Google search: ${results.size} results in ${responseTime}ms")

                SearchProviderResult(
                    provider = providerName,
                    results = results,
                    responseTimeMs = responseTime,
                    success = true,
                )
            } catch (e: Exception) {
                Log.e(TAG, "Google search failed", e)
                SearchProviderResult(
                    provider = providerName,
                    results = emptyList(),
                    responseTimeMs = System.currentTimeMillis() - startTime,
                    success = false,
                    error = e.message,
                )
            }
        } ?: run {
            Log.w(TAG, "Google search timeout")
            SearchProviderResult(
                provider = providerName,
                results = emptyList(),
                responseTimeMs = System.currentTimeMillis() - startTime,
                success = false,
                error = "Timeout",
            )
        }
    }

    private fun performGoogleSearch(phoneNumber: String): List<RawSearchResult> {
        val query = URLEncoder.encode(phoneNumber, "UTF-8")
        val url = "https://www.google.com/search?q=$query&hl=ko&num=10"

        val request = Request.Builder()
            .url(url)
            .header("User-Agent", USER_AGENT)
            .header("Accept-Language", "ko-KR,ko;q=0.9,en;q=0.8")
            .get()
            .build()

        val response = httpClient.newCall(request).execute()

        if (!response.isSuccessful) {
            Log.w(TAG, "Google returned HTTP ${response.code}")
            return emptyList()
        }

        val html = response.body?.string() ?: return emptyList()
        return parseGoogleResults(html)
    }

    /**
     * Google 검색 결과 HTML 파싱.
     *
     * [주의] Google은 JS 렌더링 비중이 높아 OkHttp로 파싱 가능한
     * 결과가 제한적일 수 있다. 이는 정상 동작이며,
     * Google 파싱 실패 시 Naver/Baidu fallback으로 보강한다.
     *
     * 해결 방향 (API 아님):
     * - 파싱 패턴 다변화 (data-href, AMP, 캐시 등)
     * - 브라우저 유사 요청 헤더 최적화
     * - 엔진별 요청 포맷 적응
     * - Naver/Baidu/DuckDuckGo fallback 의존
     */
    private fun parseGoogleResults(html: String): List<RawSearchResult> {
        val results = mutableListOf<RawSearchResult>()

        Log.d(TAG, "HTML length: ${html.length}")

        // Pattern 1: /url?q= (classic Google, 거의 반환되지 않음)
        val urlPattern = Regex("""href="/url\?q=([^&"]+)&""")
        val matches = urlPattern.findAll(html).toList()

        for (match in matches) {
            if (results.size >= MAX_RESULTS) break

            val rawUrl = try {
                java.net.URLDecoder.decode(match.groupValues[1], "UTF-8")
            } catch (e: Exception) {
                match.groupValues[1]
            }

            if (rawUrl.contains("google.com") || rawUrl.contains("accounts.google")) continue

            val domain = extractDomain(rawUrl)
            val matchStart = match.range.first
            val contextEnd = minOf(html.length, matchStart + 1000)
            val context = html.substring(matchStart, contextEnd)
            val title = extractTextBlock(context)

            if (title.isNotEmpty()) {
                results.add(
                    RawSearchResult(
                        title = title.take(80),
                        snippet = "",
                        url = rawUrl,
                        domain = domain,
                        language = "ko",
                    )
                )
            }
        }

        // Pattern 2: data-href or direct external links
        if (results.isEmpty()) {
            val externalUrlPattern = Regex("""href="(https?://(?!www\.google|accounts\.google|support\.google|schema\.org|play\.google|maps\.google|gstatic|fonts\.google)[^"]{15,})"[^>]*>""")
            val extMatches = externalUrlPattern.findAll(html).toList()

            val seenDomains = mutableSetOf<String>()
            for (match in extMatches) {
                if (results.size >= MAX_RESULTS) break

                val url = match.groupValues[1]
                val domain = extractDomain(url)
                if (domain == "unknown" || !seenDomains.add(domain)) continue

                val matchStart = match.range.first
                val contextEnd = minOf(html.length, matchStart + 800)
                val context = html.substring(matchStart, contextEnd)
                val title = extractTextBlock(context)

                if (title.length >= 5) {
                    results.add(
                        RawSearchResult(
                            title = title.take(80),
                            snippet = "",
                            url = url,
                            domain = domain,
                            language = "ko",
                        )
                    )
                }
            }
        }

        if (results.isEmpty()) {
            Log.w(TAG, "Google returned JS-only page — no results parseable. " +
                "Fallback to Naver/Baidu for search enrichment.")
        }

        Log.d(TAG, "Parsed ${results.size} results from Google HTML")
        return results
    }

    private fun extractTextBlock(context: String): String {
        // Extract meaningful text from HTML context
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
