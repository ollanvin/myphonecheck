package app.myphonecheck.mobile.data.search

import app.myphonecheck.mobile.data.search.provider.DuckDuckGoScrapingSearchProvider
import okhttp3.OkHttpClient
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * 190개국 검색 라우팅 전수 검증.
 *
 * 검증 항목:
 *  1. 모든 190개국에 대해 Provider 리스트가 비어있지 않음
 *  2. 모든 190개국에서 DuckDuckGo가 마지막 fallback
 *  3. Provider 수가 2~3개 범위
 *  4. 로컬 엔진 국가 (KR, CN, JP, RU, CIS, UA) 확인
 *  5. 나머지 176개국은 Google + DuckDuckGo
 *  6. 모든 Provider가 null이 아님
 */
class Global190CountryRoutingTest {

    private lateinit var router: CountrySearchRouter
    private lateinit var httpClient: OkHttpClient

    private val ALL_190_COUNTRIES = listOf(
        "AD", "AE", "AF", "AG", "AL", "AM", "AO", "AR", "AT", "AU",
        "AZ", "BA", "BB", "BD", "BE", "BF", "BG", "BH", "BJ", "BN",
        "BO", "BR", "BS", "BT", "BW", "BY", "BZ", "CA", "CD", "CF",
        "CH", "CI", "CL", "CM", "CN", "CO", "CR", "CU", "CV", "CY",
        "CZ", "DE", "DJ", "DK", "DM", "DO", "DZ", "EC", "EE", "EG",
        "ER", "ES", "ET", "FI", "FJ", "FM", "FR", "GA", "GB", "GD",
        "GE", "GH", "GM", "GN", "GQ", "GR", "GT", "GW", "GY", "HK",
        "HN", "HR", "HT", "HU", "ID", "IE", "IL", "IN", "IQ", "IR",
        "IS", "IT", "JM", "JO", "JP", "KE", "KG", "KH", "KI", "KM",
        "KN", "KR", "KW", "KZ", "LA", "LB", "LC", "LI", "LK", "LR",
        "LS", "LT", "LU", "LV", "LY", "MA", "MC", "MD", "ME", "MG",
        "MH", "MK", "ML", "MM", "MN", "MO", "MR", "MT", "MU", "MV",
        "MW", "MX", "MY", "MZ", "NA", "NE", "NG", "NI", "NL", "NO",
        "NP", "NR", "NZ", "OM", "PA", "PE", "PG", "PH", "PK", "PL",
        "PT", "PW", "PY", "QA", "RO", "RS", "RU", "RW", "SA", "SB",
        "SC", "SD", "SE", "SG", "SI", "SK", "SL", "SN", "SO", "SR",
        "ST", "SV", "SZ", "TD", "TG", "TH", "TJ", "TL", "TM", "TN",
        "TO", "TR", "TT", "TV", "TW", "TZ", "UA", "UG", "US", "UY",
        "UZ", "VC", "VE", "VN", "VU", "WS", "YE", "ZA", "ZM", "ZW",
    )

    private val LOCAL_ENGINE_COUNTRIES = setOf(
        "KR", "CN", "JP", "RU",
        "BY", "KZ", "UZ", "KG", "TJ", "AM", "AZ", "GE", "MD",
        "UA",
    )

    @Before
    fun setup() {
        httpClient = OkHttpClient.Builder().build()
        router = CountrySearchRouter(httpClient)
    }

    // ═══════════════════════════════════════════════════════════
    // 1. 190개국 전수 — Provider 리스트 비어있지 않음
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `ROUTING-190 every country returns non-empty provider list`() {
        for (cc in ALL_190_COUNTRIES) {
            val providers = router.getProvidersForCountry(cc)
            assertTrue(
                "$cc must return at least 1 provider",
                providers.isNotEmpty(),
            )
        }
    }

    // ═══════════════════════════════════════════════════════════
    // 2. 190개국 전수 — DuckDuckGo 마지막 fallback 보장
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `ROUTING-190 DuckDuckGo is always last fallback for all 190 countries`() {
        var failedCountries = mutableListOf<String>()

        for (cc in ALL_190_COUNTRIES) {
            val providers = router.getProvidersForCountry(cc)
            val last = providers.last()
            if (last !is DuckDuckGoScrapingSearchProvider) {
                failedCountries.add("$cc (last=${last.providerName})")
            }
        }

        assertTrue(
            "DuckDuckGo must be last for all 190 countries. Failed: $failedCountries",
            failedCountries.isEmpty(),
        )
    }

    // ═══════════════════════════════════════════════════════════
    // 3. Provider 수 범위 (2~3)
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `ROUTING-190 provider count is 2 or 3 for all countries`() {
        for (cc in ALL_190_COUNTRIES) {
            val count = router.getProvidersForCountry(cc).size
            assertTrue(
                "$cc must have 2 or 3 providers, got $count",
                count in 2..3,
            )
        }
    }

    // ═══════════════════════════════════════════════════════════
    // 4. 로컬 엔진 국가는 3개 Provider (CN 제외 — 2개)
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `ROUTING-190 local engine countries have correct provider count`() {
        for (cc in LOCAL_ENGINE_COUNTRIES) {
            val providers = router.getProvidersForCountry(cc)
            val expectedCount = if (cc == "CN") 2 else 3

            assertEquals(
                "$cc must have $expectedCount providers",
                expectedCount,
                providers.size,
            )
        }
    }

    // ═══════════════════════════════════════════════════════════
    // 5. 비로컬 국가는 정확히 Google + DuckDuckGo (2개)
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `ROUTING-190 non-local countries have exactly Google and DuckDuckGo`() {
        val globalCountries = ALL_190_COUNTRIES.filter { it !in LOCAL_ENGINE_COUNTRIES }

        var tier2Count = 0
        for (cc in globalCountries) {
            val providers = router.getProvidersForCountry(cc)
            val names = providers.map { it.providerName }

            assertEquals("$cc must have 2 providers", 2, providers.size)
            assertEquals("$cc 1st must be Google", "Google", names[0])
            assertEquals("$cc 2nd must be DuckDuckGo", "DuckDuckGo", names[1])
            tier2Count++
        }

        println("[ROUTING-190] Global (Google+DDG) countries: $tier2Count")
        println("[ROUTING-190] Local engine countries: ${LOCAL_ENGINE_COUNTRIES.size}")
        println("[ROUTING-190] Total: ${tier2Count + LOCAL_ENGINE_COUNTRIES.size}")
        assertEquals("Must cover all 190", 190, tier2Count + LOCAL_ENGINE_COUNTRIES.size)
    }

    // ═══════════════════════════════════════════════════════════
    // 6. 모든 Provider가 null이 아님
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `ROUTING-190 no null providers in any country`() {
        for (cc in ALL_190_COUNTRIES) {
            val providers = router.getProvidersForCountry(cc)
            for ((idx, provider) in providers.withIndex()) {
                assertNotNull("$cc provider[$idx] must not be null", provider)
                assertTrue(
                    "$cc provider[$idx] must have non-blank name",
                    provider.providerName.isNotBlank(),
                )
            }
        }
    }
}
