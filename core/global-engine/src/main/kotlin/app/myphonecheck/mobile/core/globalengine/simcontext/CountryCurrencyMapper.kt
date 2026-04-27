package app.myphonecheck.mobile.core.globalengine.simcontext

import java.util.Currency
import java.util.Locale

/**
 * countryIso (ISO 3166-1 alpha-2) → Currency (ISO 4217).
 *
 * 헌법 §8조 SIM-Oriented Single Core 정합 (Architecture v2.0.0 §29).
 * java.util.Currency.getInstance(Locale) 활용 — JVM 표준 매핑.
 */
object CountryCurrencyMapper {

    /**
     * @param countryIso ISO 3166-1 alpha-2 (예: "KR", "US", "JP", "BH")
     * @return Currency (예: KRW, USD, JPY, BHD). 알 수 없는 country는 USD fallback.
     */
    fun resolve(countryIso: String): Currency {
        if (countryIso.isBlank()) return fallback()
        return try {
            Currency.getInstance(Locale("", countryIso.uppercase()))
        } catch (e: Exception) {
            fallback()
        }
    }

    private fun fallback(): Currency = Currency.getInstance("USD")
}
