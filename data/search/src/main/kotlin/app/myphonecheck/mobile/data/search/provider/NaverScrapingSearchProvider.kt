package app.myphonecheck.mobile.data.search.provider

import android.util.Log
import app.myphonecheck.mobile.data.search.RawSearchResult
import app.myphonecheck.mobile.data.search.SearchProvider
import app.myphonecheck.mobile.data.search.SearchProviderResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder

/**
 * Naver 웹 검색 온디바이스 스크래핑 Provider.
 *
 * 한국 시장 전용. Naver 검색 결과에서 전화번호 관련 정보를 직접 파싱한다.
 * Naver는 전화번호 검색 시 스팸 신고, 업체 정보, 커뮤니티 후기를
 * 우선 노출하므로 한국 전화번호 판정에 매우 유용하다.
 *
 * API 키 불필요. 서버 불필요. 비용 불필요.
 * 디바이스에서 직접 HTTP 요청 → HTML 수신 → 텍스트 추출.
 *
 * 구조 원칙:
 * - Naver Search API 사용 절대 금지
 * - 스크래핑 실패 시 → 파싱 방식 변경 또는 Google/Baidu fallback
 * - 검색은 "디바이스가 직접 하는 행위"
 */
class NaverScrapingSearchProvider(
    private val httpClient: OkHttpClient,
    override val providerName: String = "Naver",
) : SearchProvider {

    private companion object {
        private const val TAG = "NaverScrapingProvider"
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
                    performNaverSearch(phoneNumber)
                }
                val responseTime = System.currentTimeMillis() - startTime
                Log.d(TAG, "Naver search: ${results.size} results in ${responseTime}ms")

                SearchProviderResult(
                    provider = providerName,
                    results = results,
                    responseTimeMs = responseTime,
                    success = true,
                )
            } catch (e: Exception) {
                Log.e(TAG, "Naver search failed", e)
                SearchProviderResult(
                    provider = providerName,
                    results = emptyList(),
                    responseTimeMs = System.currentTimeMillis() - startTime,
                    success = false,
                    error = e.message,
                )
            }
        } ?: run {
            Log.w(TAG, "Naver search timeout")
            SearchProviderResult(
                provider = providerName,
                results = emptyList(),
                responseTimeMs = System.currentTimeMillis() - startTime,
                success = false,
                error = "Timeout",
            )
        }
    }

    private fun performNaverSearch(phoneNumber: String): List<RawSearchResult> {
        val query = URLEncoder.encode(phoneNumber, "UTF-8")
        val url = "https://search.naver.com/search.naver?query=$query&where=web"

        val request = Request.Builder()
            .url(url)
            .header("User-Agent", USER_AGENT)
            .header("Accept-Language", "ko-KR,ko;q=0.9")
            .get()
            .build()

        val response = httpClient.newCall(request).execute()

        if (!response.isSuccessful) {
            Log.w(TAG, "Naver returned HTTP ${response.code}")
            return emptyList()
        }

        val html = response.body?.string() ?: return emptyList()
        return parseNaverResults(html)
    }

    /**
     * Naver 검색 결과 HTML 파싱.
     *
     * Naver 2024+ SDS(Search Design System) 구조:
     * - 결과 링크: <a nocr="1" href="URL" class="fender-ui_...">
     * - 제목/스니펫: <span class="sds-comps-text">TEXT</span>
     * - 기존 class 패턴(link_tit 등)은 더 이상 사용되지 않음
     */
    private fun parseNaverResults(html: String): List<RawSearchResult> {
        val results = mutableListOf<RawSearchResult>()

        Log.d(TAG, "HTML length: ${html.length}")

        // Step 1: nocr="1" 링크에서 외부 URL 수집 (중복 제거)
        val nocrPattern = Regex("""<a\s+nocr="1"\s+href="([^"]+)"[^>]*>""")
        val nocrMatches = nocrPattern.findAll(html).toList()
        Log.d(TAG, "Found ${nocrMatches.size} nocr links")

        val seenUrls = mutableSetOf<String>()

        for (match in nocrMatches) {
            if (results.size >= MAX_RESULTS) break

            val rawUrl = match.groupValues[1]
                .replace("&amp;", "&")

            // Skip Naver internal links and duplicates
            if (rawUrl.contains("naver.com") || rawUrl.contains("naver.net")) continue
            if (!seenUrls.add(rawUrl)) continue

            val domain = extractDomain(rawUrl)

            // Extract title & snippet from the text AFTER this link
            val matchStart = match.range.first
            val contextEnd = minOf(html.length, matchStart + 2000)
            val context = html.substring(matchStart, contextEnd)

            val titleAndSnippet = extractSdsTextBlocks(context)
            val title = titleAndSnippet.first.ifEmpty { domain }
            val snippet = titleAndSnippet.second

            Log.d(TAG, "Result: url=$rawUrl, title=$title, snippet=${snippet.take(50)}")

            results.add(
                RawSearchResult(
                    title = title.take(100),
                    snippet = snippet.take(200),
                    url = rawUrl,
                    domain = domain,
                    language = "ko",
                )
            )
        }

        // Fallback: 일반 외부 링크 (nocr 없는 경우)
        if (results.isEmpty()) {
            Log.d(TAG, "No nocr results, trying fallback pattern")
            // 모든 naver 서브도메인 차단: *.naver.com, *.naver.net, *.pstatic.net
            val genericPattern = Regex(
                """<a[^>]+href="(https?://[^"]+)"[^>]*>"""
            )
            val genericMatches = genericPattern.findAll(html).toList()

            for (match in genericMatches) {
                if (results.size >= MAX_RESULTS) break

                val rawUrl = match.groupValues[1].replace("&amp;", "&")
                if (!seenUrls.add(rawUrl)) continue

                val domain = extractDomain(rawUrl)
                if (domain == "unknown") continue

                // 모든 naver/내부 도메인 차단
                if (domain.contains("naver.") || domain.contains("pstatic.net") ||
                    domain.contains("navercorp.") || domain.contains("nid.") ||
                    domain.endsWith(".naver.com") || domain.endsWith(".naver.net")) continue

                val matchStart = match.range.first
                val contextEnd = minOf(html.length, matchStart + 1500)
                val context = html.substring(matchStart, contextEnd)
                val titleAndSnippet = extractSdsTextBlocks(context)

                results.add(
                    RawSearchResult(
                        title = titleAndSnippet.first.ifEmpty { domain }.take(100),
                        snippet = titleAndSnippet.second.take(200),
                        url = rawUrl,
                        domain = domain,
                        language = "ko",
                    )
                )
            }
        }

        Log.d(TAG, "Parsed ${results.size} results from Naver HTML")
        return results
    }

    /**
     * SDS 컴포넌트에서 텍스트 블록 추출.
     * <span class="sds-comps-text...">TEXT</span> 패턴에서
     * 첫 번째 의미있는 텍스트 = 제목, 이후 긴 텍스트 = 스니펫
     */
    private fun extractSdsTextBlocks(context: String): Pair<String, String> {
        val textPattern = Regex("""sds-comps-text[^>]*>([^<]{3,})""")
        val textMatches = textPattern.findAll(context).toList()

        var title = ""
        var snippet = ""

        for (m in textMatches) {
            val text = cleanHtml(m.groupValues[1])
            // Skip domain-like texts and very short texts
            if (text.length < 3) continue
            if (text.contains("www.") || text.contains(".co.kr") || text.contains(".com")) continue
            if (text == "›" || text == "Keep에 저장" || text == "Keep에 바로가기") continue

            if (title.isEmpty()) {
                title = text
            } else if (snippet.isEmpty() && text.length >= 10) {
                snippet = text
                break
            }
        }

        return Pair(title, snippet)
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
