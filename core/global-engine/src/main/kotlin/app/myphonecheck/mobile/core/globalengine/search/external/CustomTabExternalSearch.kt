package app.myphonecheck.mobile.core.globalengine.search.external

import app.myphonecheck.mobile.core.globalengine.search.SearchInput
import app.myphonecheck.mobile.core.globalengine.search.toAiSearchQuery
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 외부 검색 = Custom Tab 사용자 trigger (Architecture v2.5.0 §30 + 헌법 §1).
 *
 * 헌법 §1 Out-Bound Zero 정합:
 *  - 본 클래스는 인텐트 URL만 빌드. 실제 요청 발신은 사용자가 UI 버튼 탭 후 Custom Tab.
 *  - 본 앱은 응답을 받지 않음 (헌법 §2 In-Bound Zero).
 *
 * v2.5.0 2축 매핑 (4축 → 2축 단순화):
 *  - 축 1: 내부 NKB (W=0.40)
 *  - 축 2: 외부 AI 검색 (W=0.60) — SIM 기준 SimAiSearchRegistry 후보군
 *
 * One Engine, N Inputs (헌법 §7 정합) — SearchInput sealed class 단일 인터페이스.
 */
@Singleton
class CustomTabExternalSearch @Inject constructor() {

    /**
     * URL 빌더 (input 타입 + mode 기반).
     * Android Intent 생성은 UI 레이어 책임 (`Intent(ACTION_VIEW, Uri.parse(url))`).
     */
    fun buildUrl(input: SearchInput, mode: ExternalMode = ExternalMode.GOOGLE_AI_MODE): String {
        val query = input.toAiSearchQuery()
        return buildUrlForQuery(query, mode)
    }

    /**
     * 호환 보존: query string 직접 입력 (테스트 / 단순 케이스).
     */
    fun buildUrl(query: String, mode: ExternalMode = ExternalMode.GOOGLE_AI_MODE): String =
        buildUrlForQuery(query, mode)

    private fun buildUrlForQuery(query: String, mode: ExternalMode): String {
        val encoded = URLEncoder.encode(query, StandardCharsets.UTF_8.name())
        return when (mode) {
            ExternalMode.GOOGLE_AI_MODE -> "https://www.google.com/search?q=$encoded&udm=50"
            ExternalMode.BING_COPILOT   -> "https://www.bing.com/search?q=$encoded&showconv=1"
            ExternalMode.NAVER_AI       -> "https://m.search.naver.com/search.naver?where=nexearch&query=$encoded&ai=1"
            ExternalMode.YAHOO_JAPAN_AI -> "https://search.yahoo.co.jp/search?p=$encoded&ai=1"
            ExternalMode.BAIDU_AI       -> "https://www.baidu.com/s?wd=$encoded&ie=UTF-8"
            ExternalMode.GOOGLE_PLAIN   -> "https://www.google.com/search?q=$encoded"
            ExternalMode.BING_PLAIN     -> "https://www.bing.com/search?q=$encoded"
            ExternalMode.NAVER_PLAIN    -> "https://m.search.naver.com/search.naver?query=$encoded"
        }
    }

    /**
     * 사용자 trigger용 외부 검색 인텐트 빌드 (UI 레이어가 Custom Tab으로 띄움).
     * v2.5.0 SearchInput 정합.
     */
    fun buildIntent(input: SearchInput, mode: ExternalMode = ExternalMode.GOOGLE_AI_MODE): ExternalSearchIntent =
        ExternalSearchIntent(buildUrl(input, mode))
}
