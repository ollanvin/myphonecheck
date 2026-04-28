package app.myphonecheck.mobile.core.globalengine.search.publicfeed

/**
 * 피드 출처의 적용 범위 (Architecture v2.1.0 §30-3-A-3).
 *
 * SIM countryIso 기반 자동 추천 시 GLOBAL > COUNTRY > REGION 매칭.
 */
sealed class CountryScope {
    object GLOBAL : CountryScope()
    data class COUNTRY(val iso: String) : CountryScope()
    data class REGION(val isoList: List<String>) : CountryScope()

    fun matches(simIso: String): Boolean = when (this) {
        is GLOBAL -> true
        is COUNTRY -> iso.equals(simIso, ignoreCase = true)
        is REGION -> isoList.any { it.equals(simIso, ignoreCase = true) }
    }
}
