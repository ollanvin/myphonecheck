package app.myphonecheck.mobile.core.globalengine.search.external

import app.myphonecheck.mobile.core.globalengine.search.SearchQuery
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 외부 검색 = Custom Tab 사용자 trigger (Architecture v2.0.0 §30).
 *
 * 헌법 §1 Out-Bound Zero 정합:
 *  - 본 클래스는 인텐트 URL만 빌드. 실제 요청 발신은 사용자가 UI 버튼 탭 후 Custom Tab.
 *  - 본 앱은 응답을 받지 않음 (헌법 §2 In-Bound Zero).
 */
@Singleton
class CustomTabExternalSearch @Inject constructor() {

    fun buildIntent(query: SearchQuery): ExternalSearchIntent {
        val encoded = URLEncoder.encode(query.key, StandardCharsets.UTF_8.name())
        return ExternalSearchIntent("$BASE_URL$encoded")
    }

    companion object {
        const val BASE_URL = "https://www.google.com/search?q="
    }
}
