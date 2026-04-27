package app.myphonecheck.mobile.core.globalengine.simcontext

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * countryIso → ISO 4217 매핑 검증 (Architecture v2.0.0 §29 + 헌법 §8조).
 *
 * 글로벌 동작 보증: 카드사·국가 분기 코드 0.
 */
class CountryCurrencyMapperTest {

    @Test
    fun `KR → KRW`() {
        assertEquals("KRW", CountryCurrencyMapper.resolve("KR").currencyCode)
    }

    @Test
    fun `US → USD`() {
        assertEquals("USD", CountryCurrencyMapper.resolve("US").currencyCode)
    }

    @Test
    fun `JP → JPY`() {
        assertEquals("JPY", CountryCurrencyMapper.resolve("JP").currencyCode)
    }

    @Test
    fun `BH → BHD (3 decimals)`() {
        val currency = CountryCurrencyMapper.resolve("BH")
        assertEquals("BHD", currency.currencyCode)
        assertEquals(3, currency.defaultFractionDigits)
    }

    @Test
    fun `lowercase input → uppercase 처리`() {
        assertEquals("EUR", CountryCurrencyMapper.resolve("de").currencyCode)
    }

    @Test
    fun `blank input → fallback USD`() {
        assertEquals("USD", CountryCurrencyMapper.resolve("").currencyCode)
    }

    @Test
    fun `unknown country → fallback USD`() {
        assertEquals("USD", CountryCurrencyMapper.resolve("XX").currencyCode)
    }
}
