package app.myphonecheck.mobile.core.globalengine.search.external

import app.myphonecheck.mobile.core.globalengine.search.SearchQuery
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 외부 검색 = Custom Tab 사용자 trigger (Architecture v2.4.0 §30 + 메모리 #8).
 *
 * 헌법 §1 Out-Bound Zero 정합:
 *  - 본 클래스는 인텐트 URL만 빌드. 실제 요청 발신은 사용자가 UI 버튼 탭 후 Custom Tab.
 *  - 본 앱은 응답을 받지 않음 (헌법 §2 In-Bound Zero).
 *
 * v2.4.0 4축 매핑 (메모리 #8):
 *  - 축 3 1차: GOOGLE_AI_MODE (udm=50)
 *  - 축 3 fallback: BING_COPILOT (showconv=1) / NAVER_CUE (한국 SIM)
 *  - plain 변형: GOOGLE_PLAIN / BING_PLAIN / NAVER_PLAIN (AI 미작동 시)
 */
@Singleton
class CustomTabExternalSearch @Inject constructor() {

    /**
     * URL 빌더 (mode 기반).
     * Android Intent 생성은 UI 레이어 책임 (`Intent(ACTION_VIEW, Uri.parse(url))`).
     * core 모듈은 안드로이드 의존 0 — string URL만 노출 (헌법 §9-4 단일 코어 정합).
     */
    fun buildUrl(query: String, mode: ExternalMode = ExternalMode.GOOGLE_AI_MODE): String {
        val encoded = URLEncoder.encode(query, StandardCharsets.UTF_8.name())
        return when (mode) {
            ExternalMode.GOOGLE_AI_MODE -> "https://www.google.com/search?q=$encoded&udm=50"
            ExternalMode.BING_COPILOT   -> "https://www.bing.com/search?q=$encoded&showconv=1"
            ExternalMode.NAVER_CUE      -> "https://cue.search.naver.com/search?query=$encoded"
            ExternalMode.GOOGLE_PLAIN   -> "https://www.google.com/search?q=$encoded"
            ExternalMode.BING_PLAIN     -> "https://www.bing.com/search?q=$encoded"
            ExternalMode.NAVER_PLAIN    -> "https://search.naver.com/search.naver?query=$encoded"
        }
    }

    /**
     * 기존 호환: SearchQuery → ExternalSearchIntent.
     * v2.4.0에서 default = GOOGLE_AI_MODE (메모리 #8 1차).
     */
    fun buildIntent(query: SearchQuery, mode: ExternalMode = ExternalMode.GOOGLE_AI_MODE): ExternalSearchIntent =
        ExternalSearchIntent(buildUrl(query.key, mode))
}
