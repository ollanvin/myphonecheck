package app.callcheck.mobile.data.search

import app.callcheck.mobile.data.search.provider.BaiduScrapingSearchProvider
import app.callcheck.mobile.data.search.provider.DuckDuckGoScrapingSearchProvider
import app.callcheck.mobile.data.search.provider.GoogleScrapingSearchProvider
import app.callcheck.mobile.data.search.provider.NaverScrapingSearchProvider
import app.callcheck.mobile.data.search.provider.YahooJapanScrapingSearchProvider
import app.callcheck.mobile.data.search.provider.YandexScrapingSearchProvider
import okhttp3.OkHttpClient
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * CountrySearchRouter 국가별 라우팅 검증 테스트.
 *
 * 검증 항목:
 * 1. KR → Naver 우선
 * 2. JP → Yahoo Japan 우선
 * 3. CN → Baidu 우선
 * 4. RU → Yandex 우선
 * 5. CIS 9개국 → Yandex 우선
 * 6. UA → Google 우선, Yandex 보조
 * 7. 기본(US/DE/BR 등) → Google + DuckDuckGo
 * 8. null → US 기본값 적용
 * 9. 소문자 입력 → 대문자 변환 처리
 * 10. 모든 경로에 DuckDuckGo fallback 포함
 */
class CountrySearchRouterTest {

    private lateinit var router: CountrySearchRouter
    private lateinit var httpClient: OkHttpClient

    @Before
    fun setup() {
        httpClient = OkHttpClient.Builder().build()
        router = CountrySearchRouter(httpClient)
    }

    // ══════════════════════════════════════════════
    // 증거 3: CountrySearchRouter 국가별 provider 선택 로그
    // ══════════════════════════════════════════════

    @Test
    fun `KR - Naver first, Google second, DuckDuckGo fallback`() {
        val providers = router.getProvidersForCountry("KR")
        val names = providers.map { it.providerName }

        println("[ROUTING LOG] countryCode=KR → providers=$names")

        assertEquals(3, providers.size)
        assertTrue("KR 1st must be Naver", providers[0] is NaverScrapingSearchProvider)
        assertTrue("KR 2nd must be Google", providers[1] is GoogleScrapingSearchProvider)
        assertTrue("KR 3rd must be DuckDuckGo", providers[2] is DuckDuckGoScrapingSearchProvider)

        assertEquals("Naver", names[0])
        assertEquals("Google", names[1])
        assertEquals("DuckDuckGo", names[2])
    }

    @Test
    fun `JP - Yahoo Japan first, Google second, DuckDuckGo fallback`() {
        val providers = router.getProvidersForCountry("JP")
        val names = providers.map { it.providerName }

        println("[ROUTING LOG] countryCode=JP → providers=$names")

        assertEquals(3, providers.size)
        assertTrue("JP 1st must be YahooJapan", providers[0] is YahooJapanScrapingSearchProvider)
        assertTrue("JP 2nd must be Google", providers[1] is GoogleScrapingSearchProvider)
        assertTrue("JP 3rd must be DuckDuckGo", providers[2] is DuckDuckGoScrapingSearchProvider)

        assertEquals("YahooJapan", names[0])
        assertEquals("Google", names[1])
        assertEquals("DuckDuckGo", names[2])
    }

    @Test
    fun `CN - Baidu first, DuckDuckGo fallback, no Google`() {
        val providers = router.getProvidersForCountry("CN")
        val names = providers.map { it.providerName }

        println("[ROUTING LOG] countryCode=CN → providers=$names")

        assertEquals(2, providers.size)
        assertTrue("CN 1st must be Baidu", providers[0] is BaiduScrapingSearchProvider)
        assertTrue("CN 2nd must be DuckDuckGo", providers[1] is DuckDuckGoScrapingSearchProvider)

        // Google must NOT be in China routing (blocked by GFW)
        assertTrue("CN must NOT include Google", names.none { it == "Google" })
    }

    @Test
    fun `RU - Yandex first, Google second, DuckDuckGo fallback`() {
        val providers = router.getProvidersForCountry("RU")
        val names = providers.map { it.providerName }

        println("[ROUTING LOG] countryCode=RU → providers=$names")

        assertEquals(3, providers.size)
        assertTrue("RU 1st must be Yandex", providers[0] is YandexScrapingSearchProvider)
        assertTrue("RU 2nd must be Google", providers[1] is GoogleScrapingSearchProvider)
        assertTrue("RU 3rd must be DuckDuckGo", providers[2] is DuckDuckGoScrapingSearchProvider)
    }

    // ══════════════════════════════════════════════
    // CIS 9개국 Yandex 라우팅 검증
    // ══════════════════════════════════════════════

    @Test
    fun `CIS countries - all route to Yandex first`() {
        val cisCountries = listOf("BY", "KZ", "UZ", "KG", "TJ", "AM", "AZ", "GE", "MD")

        cisCountries.forEach { code ->
            val providers = router.getProvidersForCountry(code)
            val names = providers.map { it.providerName }

            println("[ROUTING LOG] countryCode=$code → providers=$names")

            assertTrue(
                "CIS country $code 1st must be Yandex, got ${names[0]}",
                providers[0] is YandexScrapingSearchProvider
            )
            assertEquals("CIS $code must have 3 providers", 3, providers.size)
        }
    }

    @Test
    fun `UA - Google first, Yandex second (not Yandex-first like RU)`() {
        val providers = router.getProvidersForCountry("UA")
        val names = providers.map { it.providerName }

        println("[ROUTING LOG] countryCode=UA → providers=$names")

        assertEquals(3, providers.size)
        assertTrue("UA 1st must be Google", providers[0] is GoogleScrapingSearchProvider)
        assertTrue("UA 2nd must be Yandex", providers[1] is YandexScrapingSearchProvider)
        assertTrue("UA 3rd must be DuckDuckGo", providers[2] is DuckDuckGoScrapingSearchProvider)
    }

    // ══════════════════════════════════════════════
    // Tier 2: Google 독점 국가 (176개국)
    // ══════════════════════════════════════════════

    @Test
    fun `Global default - Google first, DuckDuckGo fallback`() {
        val globalCountries = listOf("US", "DE", "BR", "IN", "AU", "FR", "GB", "MX", "NG", "ZA")

        globalCountries.forEach { code ->
            val providers = router.getProvidersForCountry(code)
            val names = providers.map { it.providerName }

            println("[ROUTING LOG] countryCode=$code → providers=$names")

            assertEquals("$code must have 2 providers", 2, providers.size)
            assertTrue("$code 1st must be Google", providers[0] is GoogleScrapingSearchProvider)
            assertTrue("$code 2nd must be DuckDuckGo", providers[1] is DuckDuckGoScrapingSearchProvider)
        }
    }

    // ══════════════════════════════════════════════
    // Edge cases
    // ══════════════════════════════════════════════

    @Test
    fun `null countryCode defaults to US routing`() {
        val providers = router.getProvidersForCountry(null)
        val names = providers.map { it.providerName }

        println("[ROUTING LOG] countryCode=null → providers=$names (defaults to US)")

        assertEquals(2, providers.size)
        assertEquals("Google", names[0])
        assertEquals("DuckDuckGo", names[1])
    }

    @Test
    fun `lowercase countryCode is normalized to uppercase`() {
        val providersLower = router.getProvidersForCountry("kr")
        val providersUpper = router.getProvidersForCountry("KR")

        val namesLower = providersLower.map { it.providerName }
        val namesUpper = providersUpper.map { it.providerName }

        println("[ROUTING LOG] countryCode=kr → providers=$namesLower")
        println("[ROUTING LOG] countryCode=KR → providers=$namesUpper")

        assertEquals("lowercase kr must equal uppercase KR", namesUpper, namesLower)
    }

    // ══════════════════════════════════════════════
    // 구조 보장: 모든 경로에 DuckDuckGo fallback 존재
    // ══════════════════════════════════════════════

    @Test
    fun `every route includes DuckDuckGo as last fallback`() {
        val allCodes = listOf(
            "KR", "CN", "JP", "RU",
            "BY", "KZ", "UZ", "KG", "TJ", "AM", "AZ", "GE", "MD",
            "UA",
            "US", "DE", "BR", "IN", "AU", "FR", "GB", null
        )

        allCodes.forEach { code ->
            val providers = router.getProvidersForCountry(code)
            val lastProvider = providers.last()

            assertTrue(
                "DuckDuckGo must be last fallback for $code, got ${lastProvider.providerName}",
                lastProvider is DuckDuckGoScrapingSearchProvider
            )
        }

        println("[ROUTING LOG] DuckDuckGo fallback verified for all ${allCodes.size} test cases")
    }
}
