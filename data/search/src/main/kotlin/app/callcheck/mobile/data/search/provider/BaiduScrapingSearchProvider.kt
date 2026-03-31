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
 * Baidu 웹 검색 스크래핑 Provider.
 *
 * 중국 시장 전용. Baidu 검색 결과에서 전화번호 관련 정보를 온디바이스로 파싱한다.
 * API 키 불필요. 디바이스에서 직접 HTTP 요청.
 *
 * 구조 원칙:
 * - 서버 없음. 중앙 API 없음. 비용 없음.
 * - 앱이 직접 Baidu 검색 페이지를 가져와서 HTML 파싱.
 * - 사용자 수 증가와 비용 무관.
 */
class BaiduScrapingSearchProvider(
    private val httpClient: OkHttpClient,
    override val providerName: String = "Baidu",
) : SearchProvider {

    private companion object {
        private const val TAG = "BaiduScrapingProvider"
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
                    performBaiduSearch(phoneNumber)
                }
                val responseTime = System.currentTimeMillis() - startTime
                Log.d(TAG, "Baidu search: ${results.size} results in ${responseTime}ms")

                SearchProviderResult(
                    provider = providerName,
                    results = results,
                    responseTimeMs = responseTime,
                    success = true,
                )
            } catch (e: Exception) {
                Log.e(TAG, "Baidu search failed", e)
                SearchProviderResult(
                    provider = providerName,
                    results = emptyList(),
                    responseTimeMs = System.currentTimeMillis() - startTime,
                    success = false,
                    error = e.message,
                )
            }
        } ?: run {
            Log.w(TAG, "Baidu search timeout")
            SearchProviderResult(
                provider = providerName,
                results = emptyList(),
                responseTimeMs = System.currentTimeMillis() - startTime,
                success = false,
                error = "Timeout",
            )
        }
    }

    private fun performBaiduSearch(phoneNumber: String): List<RawSearchResult> {
        val query = URLEncoder.encode(phoneNumber, "UTF-8")
        val url = "https://www.baidu.com/s?wd=$query&rn=10"

        val request = Request.Builder()
            .url(url)
            .header("User-Agent", USER_AGENT)
            .header("Accept-Language", "zh-CN,zh;q=0.9")
            .header("Accept", "text/html,application/xhtml+xml")
            .get()
            .build()

        val response = httpClient.newCall(request).execute()

        if (!response.isSuccessful) {
            Log.w(TAG, "Baidu returned HTTP ${response.code}")
            return emptyList()
        }

        val html = response.body?.string() ?: return emptyList()
        return parseBaiduResults(html)
    }

    /**
     * Baidu 검색 결과 HTML 파싱.
     *
     * Baidu 모바일 검색 결과 구조:
     * - 결과 컨테이너: <div class="result c-container"> 또는 <div class="c-result">
     * - 제목: <h3 class="t"> 내부 <a> 태그
     * - 스니펫: <span class="content-right_..."> 또는 <div class="c-abstract">
     * - URL: data-log 속성 또는 href (Baidu redirect URL을 포함할 수 있음)
     *
     * Baidu는 서버사이드 렌더링을 유지하므로 OkHttp로 직접 파싱 가능.
     */
    private fun parseBaiduResults(html: String): List<RawSearchResult> {
        val results = mutableListOf<RawSearchResult>()

        Log.d(TAG, "HTML length: ${html.length}")

        // Pattern 1: <h3 class="t"> 제목 블록에서 링크 추출
        val titleBlockPattern = Regex(
            """<h3[^>]*class="[^"]*t[^"]*"[^>]*>\s*<a[^>]+href="([^"]+)"[^>]*>(.*?)</a>""",
            RegexOption.DOT_MATCHES_ALL
        )
        val titleMatches = titleBlockPattern.findAll(html).toList()
        Log.d(TAG, "Found ${titleMatches.size} title blocks")

        val seenUrls = mutableSetOf<String>()

        for (match in titleMatches) {
            if (results.size >= MAX_RESULTS) break

            val rawUrl = match.groupValues[1].replace("&amp;", "&")
            val rawTitle = cleanHtml(match.groupValues[2])

            if (rawTitle.length < 3) continue
            if (!seenUrls.add(rawUrl)) continue

            // Baidu redirect URL에서 실제 도메인 추정
            val domain = if (rawUrl.contains("baidu.com/link")) {
                extractDomainFromTitle(rawTitle)
            } else {
                extractDomain(rawUrl)
            }

            // 스니펫 추출: 제목 블록 이후 텍스트에서
            val matchEnd = match.range.last
            val contextEnd = minOf(html.length, matchEnd + 2000)
            val context = html.substring(matchEnd, contextEnd)
            val snippet = extractBaiduSnippet(context)

            results.add(
                RawSearchResult(
                    title = rawTitle.take(100),
                    snippet = snippet.take(200),
                    url = rawUrl,
                    domain = domain,
                    language = "zh",
                )
            )
        }

        // Pattern 2: Fallback - 일반 외부 링크
        if (results.isEmpty()) {
            Log.d(TAG, "No title blocks found, trying fallback pattern")
            val externalPattern = Regex(
                """href="(https?://(?!www\.baidu\.com|baidu\.com|baidustatic\.com|bdstatic\.com|bcebos\.com)[^"]{15,})"[^>]*>"""
            )
            val extMatches = externalPattern.findAll(html).toList()

            for (match in extMatches) {
                if (results.size >= MAX_RESULTS) break

                val url = match.groupValues[1].replace("&amp;", "&")
                if (!seenUrls.add(url)) continue

                val domain = extractDomain(url)
                if (domain == "unknown") continue

                val matchStart = match.range.first
                val contextEnd = minOf(html.length, matchStart + 1000)
                val context = html.substring(matchStart, contextEnd)
                val title = extractTextAfterTag(context)

                if (title.length >= 3) {
                    results.add(
                        RawSearchResult(
                            title = title.take(100),
                            snippet = "",
                            url = url,
                            domain = domain,
                            language = "zh",
                        )
                    )
                }
            }
        }

        Log.d(TAG, "Parsed ${results.size} results from Baidu HTML")
        return results
    }

    /**
     * Baidu 검색 결과에서 스니펫(요약) 텍스트 추출.
     * c-abstract, content-right 등의 클래스에서 텍스트를 가져온다.
     */
    private fun extractBaiduSnippet(context: String): String {
        // c-abstract 또는 content-right 클래스 내부 텍스트
        val abstractPattern = Regex("""class="[^"]*(?:c-abstract|content-right|c-span-last)[^"]*"[^>]*>(.*?)</""", RegexOption.DOT_MATCHES_ALL)
        val match = abstractPattern.find(context)
        if (match != null) {
            val text = cleanHtml(match.groupValues[1])
            if (text.length >= 10) return text
        }

        // Fallback: 첫 번째 의미있는 텍스트 블록
        val textBlocks = context.split(Regex("<[^>]+>"))
            .map { cleanHtml(it) }
            .filter { it.length in 10..300 }

        return textBlocks.firstOrNull() ?: ""
    }

    private fun extractTextAfterTag(context: String): String {
        val textBlocks = context.split(Regex("<[^>]+>"))
            .map { cleanHtml(it) }
            .filter { it.length in 3..200 && !it.startsWith("http") && !it.contains("{") }
        return textBlocks.firstOrNull() ?: ""
    }

    /**
     * Baidu redirect URL의 경우 title에서 도메인 힌트 추출.
     * "XXX - 百度百科" → "baike.baidu.com"
     * "114电话查询" → "114"
     */
    private fun extractDomainFromTitle(title: String): String {
        val dashParts = title.split(" - ", " – ", " — ")
        if (dashParts.size >= 2) {
            val source = dashParts.last().trim()
            // Known Chinese platforms
            return when {
                source.contains("百度") -> "baidu.com"
                source.contains("知乎") -> "zhihu.com"
                source.contains("360") -> "so.com"
                source.contains("搜狗") -> "sogou.com"
                source.contains("腾讯") -> "qq.com"
                source.contains("新浪") -> "sina.com.cn"
                source.contains("网易") -> "163.com"
                else -> source.lowercase()
            }
        }
        return "unknown"
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
