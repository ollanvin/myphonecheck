package app.myphonecheck.mobile.data.search

import app.myphonecheck.mobile.data.search.provider.BaiduScrapingSearchProvider
import app.myphonecheck.mobile.data.search.provider.DuckDuckGoScrapingSearchProvider
import app.myphonecheck.mobile.data.search.provider.GoogleScrapingSearchProvider
import app.myphonecheck.mobile.data.search.provider.NaverScrapingSearchProvider
import app.myphonecheck.mobile.data.search.provider.YahooJapanScrapingSearchProvider
import app.myphonecheck.mobile.data.search.provider.YandexScrapingSearchProvider
import okhttp3.OkHttpClient

/**
 * 190개국 검색 엔진 우선순위 라우팅.
 *
 * 국가 코드(ISO 3166-1 alpha-2)를 기반으로
 * 해당 국가에 최적화된 검색 Provider 조합을 반환한다.
 *
 * 구조 원칙:
 * - 모든 Provider는 온디바이스 직접 HTTP 스크래핑만 사용
 * - API 절대 금지. 서버 절대 금지. 비용 0.
 * - 국가별 로컬 강자를 우선 배치, Google/DuckDuckGo를 보조/fallback으로 사용
 * - 최대 3개 Provider를 병렬 실행 (응답 속도 보장)
 *
 * 라우팅 전략:
 * - Tier 1 (로컬 강자 존재): 로컬 엔진 우선 + Google/DuckDuckGo 보조
 *   → KR, CN, JP, RU+CIS
 * - Tier 2 (Google 독점): Google 우선 + DuckDuckGo fallback
 *   → 나머지 186개국
 *
 * DuckDuckGo HTML(html.duckduckgo.com)은 100% SSR이므로
 * Google이 JS-only 페이지를 반환할 때 핵심 보강 역할.
 */
class CountrySearchRouter(
    private val httpClient: OkHttpClient,
) {

    /**
     * 국가 코드에 따른 검색 Provider 리스트 반환.
     *
     * 반환 순서 = 우선순위. 병렬 실행되지만 결과 정렬 시 앞쪽이 우선.
     * 최대 3개 Provider 반환.
     */
    fun getProvidersForCountry(countryCode: String?): List<SearchProvider> {
        val code = countryCode?.uppercase() ?: "US"

        return when (code) {
            // ══════════════════════════════════════════════
            // Tier 1: 로컬 검색 엔진이 강한 국가
            // ══════════════════════════════════════════════

            // 한국: Naver가 전화번호 검색에 최적화
            "KR" -> listOf(
                NaverScrapingSearchProvider(httpClient),
                GoogleScrapingSearchProvider(httpClient),
                DuckDuckGoScrapingSearchProvider(httpClient),
            )

            // 중국: Google 차단. Baidu 독점.
            "CN" -> listOf(
                BaiduScrapingSearchProvider(httpClient),
                DuckDuckGoScrapingSearchProvider(httpClient),
            )

            // 일본: Yahoo Japan이 로컬 전화번호 검색에 강함
            "JP" -> listOf(
                YahooJapanScrapingSearchProvider(httpClient),
                GoogleScrapingSearchProvider(httpClient),
                DuckDuckGoScrapingSearchProvider(httpClient),
            )

            // 러시아: Yandex가 Google보다 점유율 높음
            "RU" -> listOf(
                YandexScrapingSearchProvider(httpClient),
                GoogleScrapingSearchProvider(httpClient),
                DuckDuckGoScrapingSearchProvider(httpClient),
            )

            // CIS 국가: Yandex 영향권
            // 벨라루스, 카자흐스탄, 우즈베키스탄, 키르기스스탄,
            // 타지키스탄, 아르메니아, 아제르바이잔, 조지아, 몰도바
            "BY", "KZ", "UZ", "KG", "TJ", "AM", "AZ", "GE", "MD" -> listOf(
                YandexScrapingSearchProvider(httpClient),
                GoogleScrapingSearchProvider(httpClient),
                DuckDuckGoScrapingSearchProvider(httpClient),
            )

            // 우크라이나: Google 우선이지만 Yandex도 유효
            "UA" -> listOf(
                GoogleScrapingSearchProvider(httpClient),
                YandexScrapingSearchProvider(httpClient),
                DuckDuckGoScrapingSearchProvider(httpClient),
            )

            // ══════════════════════════════════════════════
            // Tier 2: Google 독점 국가 (186개국)
            // Google + DuckDuckGo HTML fallback
            // ══════════════════════════════════════════════
            else -> listOf(
                GoogleScrapingSearchProvider(httpClient),
                DuckDuckGoScrapingSearchProvider(httpClient),
            )
        }
    }

    companion object {
        /**
         * 지원 현황.
         *
         * 검색 엔진 6개:
         * - Google: 글로벌 (190개국)
         * - Naver: 한국
         * - Baidu: 중국
         * - Yahoo Japan: 일본
         * - Yandex: 러시아 + CIS 10개국
         * - DuckDuckGo HTML: 글로벌 fallback (190개국, SSR 100%)
         *
         * 국가별 라우팅:
         * - KR → Naver + Google + DuckDuckGo
         * - CN → Baidu + DuckDuckGo
         * - JP → Yahoo Japan + Google + DuckDuckGo
         * - RU + CIS 9개국 → Yandex + Google + DuckDuckGo
         * - UA → Google + Yandex + DuckDuckGo
         * - 나머지 176개국 → Google + DuckDuckGo
         *
         * 전체 실패 시 → Device Evidence만으로 판정 (정상 동작)
         */
        val SUPPORTED_LOCAL_ENGINES = mapOf(
            "KR" to "Naver",
            "CN" to "Baidu",
            "JP" to "YahooJapan",
            "RU" to "Yandex",
            "BY" to "Yandex",
            "KZ" to "Yandex",
            "UZ" to "Yandex",
            "KG" to "Yandex",
            "TJ" to "Yandex",
            "AM" to "Yandex",
            "AZ" to "Yandex",
            "GE" to "Yandex",
            "MD" to "Yandex",
            "UA" to "Yandex",
        )
    }
}
