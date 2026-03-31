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
 * Yandex 웹 검색 스크래핑 Provider.
 *
 * 러시아 및 CIS 시장 전용. Yandex 검색 결과를 온디바이스로 파싱한다.
 * API 키 불필요. 서버 불필요. 비용 불필요.
 *
 * 구조 원칙:
 * - 서버 없음. Yandex API 사용 절대 금지.
 * - 앱이 직접 Yandex 검색 페이지를 가져와서 HTML 파싱.
 * - 사용자 수 증가와 비용 무관.
 * - 러시아, 벨라루스, 카자흐스탄, 우즈베키스탄 등 CIS 전화번호 대응.
 * - 스크래핑 실패 시 → Google/DuckDuckGo fallback
 */
class YandexScrapingSearchProvider(
    private val httpClient: OkHttpClient,
    override val providerName: String = "Yandex",
) : SearchProvider {

    private companion object {
        private const val TAG = "YandexScrapingProvider"
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
                    performYandexSearch(phoneNumber)
                }
                val responseTime = System.currentTimeMillis() - startTime
                Log.d(TAG, "Yandex search: ${results.size} results in ${responseTime}ms")

                SearchProviderResult(
                    provider = providerName,
                    results = results,
                    responseTimeMs = responseTime,
                    success = true,
                )
            } catch (e: Exception) {
                Log.e(TAG, "Yandex search failed", e)
                SearchProviderResult(
                    provider = providerName,
                    results = emptyList(),
                    responseTimeMs = System.currentTimeMillis() - startTime,
                    success = false,
                    error = e.message,
                )
            }
        } ?: run {
            Log.w(TAG, "Yandex search timeout")
            SearchProviderResult(
                provider = providerName,
                results = emptyList(),
                responseTimeMs = System.currentTimeMillis() - startTime,
                success = false,
                error = "Timeout",
            )
        }
    }

    private fun performYandexSearch(phoneNumber: String): List<RawSearchResult> {
        val query = URLEncoder.encode(phoneNumber, "UTF-8")
        val url = "https://yandex.ru/search/?text=$query&lr=213"

        val request = Request.Builder()
            .url(url)
            .header("User-Agent", USER_AGENT)
            .header("Accept-Language", "ru-RU,ru;q=0.9,en;q=0.8")
            .header("Accept", "text/html,application/xhtml+xml")
            .get()
            .build()

        val response = httpClient.newCall(request).execute()

        if (!response.isSuccessful) {
            Log.w(TAG, "Yandex returned HTTP ${response.code}")
            return emptyList()
        }

        val html = response.body?.string() ?: return emptyList()
        return parseYandexResults(html)
    }

    /**
     * Yandex 검색 결과 HTML 파싱.
     *
     * Yandex 검색 결과 구조:
     * - 결과 컨테이너: <li class="serp-item"> 또는 <div class="organic">
     * - 제목: <a class="OrganicTitle-Link" href="..."> 또는 <h2 class="OrganicTitle"><a href="...">
     * - 스니펫: <span class="OrganicTextContentSpan"> 또는 <div class="text-container">
     * - URL: href에서 직접 추출 (Yandex redirect 미사용)
     *
     * Yandex는 서버사이드 렌더링을 유지하므로 OkHttp로 직접 파싱 가능.
     * 주의: yandex.ru, yandex.com, yandex.net, ya.ru 내부 도메인 제외.
     */
    private fun parseYandexResults(html: String): List<RawSearchResult> {
        val results = mutableListOf<RawSearchResult>()

        Log.d(TAG, "HTML length: ${html.length}")

        // Pattern 1: <a class="OrganicTitle-Link"> 제목 링크에서 추출
        val organicLinkPattern = Regex(
            """<a[^>]*class="[^"]*OrganicTitle-Link[^"]*"[^>]+href="([^"]+)"[^>]*>(.*?)</a>""",
            RegexOption.DOT_MATCHES_ALL
        )
        val organicMatches = organicLinkPattern.findAll(html).toList()
        Log.d(TAG, "Found ${organicMatches.size} OrganicTitle-Link matches")

        val seenUrls = mutableSetOf<String>()

        for (match in organicMatches) {
            if (results.size >= MAX_RESULTS) break

            val rawUrl = match.groupValues[1].replace("&amp;", "&")
            val rawTitle = cleanHtml(match.groupValues[2])

            if (rawTitle.length < 3) continue
            if (isYandexInternalDomain(rawUrl)) continue
            if (!seenUrls.add(rawUrl)) continue

            val domain = extractDomain(rawUrl)
            if (domain == "unknown") continue

            // 스니펫 추출: 링크 이후 텍스트에서
            val matchEnd = match.range.last
            val contextEnd = minOf(html.length, matchEnd + 1500)
            val context = html.substring(matchEnd, contextEnd)
            val snippet = extractYandexSnippet(context)

            results.add(
                RawSearchResult(
                    title = rawTitle.take(100),
                    snippet = snippet.take(200),
                    url = rawUrl,
                    domain = domain,
                    language = "ru",
                )
            )
        }

        // Pattern 2: Fallback - <h2 class="OrganicTitle"> 컨테이너
        if (results.isEmpty()) {
            Log.d(TAG, "No OrganicTitle-Link found, trying h2 fallback")
            val h2TitlePattern = Regex(
                """<h2[^>]*class="[^"]*OrganicTitle[^"]*"[^>]*>(.*?)<a[^>]+href="([^"]+)"[^>]*>(.*?)</a>""",
                RegexOption.DOT_MATCHES_ALL
            )
            val h2Matches = h2TitlePattern.findAll(html).toList()

            for (match in h2Matches) {
                if (results.size >= MAX_RESULTS) break

                val rawUrl = match.groupValues[2].replace("&amp;", "&")
                val rawTitle = cleanHtml(match.groupValues[3])

                if (rawTitle.length < 3) continue
                if (isYandexInternalDomain(rawUrl)) continue
                if (!seenUrls.add(rawUrl)) continue

                val domain = extractDomain(rawUrl)
                if (domain == "unknown") continue

                val matchEnd = match.range.last
                val contextEnd = minOf(html.length, matchEnd + 1500)
                val context = html.substring(matchEnd, contextEnd)
                val snippet = extractYandexSnippet(context)

                results.add(
                    RawSearchResult(
                        title = rawTitle.take(100),
                        snippet = snippet.take(200),
                        url = rawUrl,
                        domain = domain,
                        language = "ru",
                    )
                )
            }
        }

        // Pattern 3: Generic external link fallback
        if (results.isEmpty()) {
            Log.d(TAG, "No title patterns found, trying generic external links")
            val externalPattern = Regex(
                """href="(https?://(?!yandex\.(?:ru|com|net)|ya\.ru)[^"]{15,})"[^>]*>"""
            )
            val extMatches = externalPattern.findAll(html).toList()

            for (match in extMatches) {
                if (results.size >= MAX_RESULTS) break

                val url = match.groupValues[1].replace("&amp;", "&")
                if (!seenUrls.add(url)) continue

                val domain = extractDomain(url)
                if (domain == "unknown" || isYandexInternalDomain(url)) continue

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
                            language = "ru",
                        )
                    )
                }
            }
        }

        Log.d(TAG, "Parsed ${results.size} results from Yandex HTML")
        return results
    }

    /**
     * Yandex 검색 결과에서 스니펫(요약) 텍스트 추출.
     * OrganicTextContentSpan 또는 text-container 클래스에서 텍스트를 가져온다.
     */
    private fun extractYandexSnippet(context: String): String {
        // OrganicTextContentSpan 또는 text-container 클래스 내부 텍스트
        val snippetPattern = Regex(
            """class="[^"]*(?:OrganicTextContentSpan|text-container)[^"]*"[^>]*>(.*?)</""",
            RegexOption.DOT_MATCHES_ALL
        )
        val match = snippetPattern.find(context)
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
     * Yandex 내부 도메인 여부 확인.
     * 제외: yandex.ru, yandex.com, yandex.net, ya.ru
     */
    private fun isYandexInternalDomain(url: String): Boolean {
        return url.contains("yandex.ru", ignoreCase = true) ||
                url.contains("yandex.com", ignoreCase = true) ||
                url.contains("yandex.net", ignoreCase = true) ||
                url.contains("ya.ru", ignoreCase = true)
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
