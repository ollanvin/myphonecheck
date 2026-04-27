package app.myphonecheck.mobile.core.globalengine.simcontext

import java.util.Currency
import java.util.TimeZone

/**
 * SIM-Oriented Single Core (Architecture v2.0.0 §29 + 헌법 §8조).
 *
 * 모든 국가·통화·전화번호 양식의 단일 진실원.
 * Surface별 자체 매핑 코드 금지 — 본 컨텍스트만 사용.
 *
 * 적용 영역:
 *  - 국가 결정: countryIso (ISO 3166-1)
 *  - 통신사 결정: mcc + mnc
 *  - 통화: currency (ISO 4217)
 *  - 전화번호 양식: phoneRegion (libphonenumber Region)
 *  - timezone
 *
 * 비적용 영역 (사용자 선택):
 *  - UI 언어 — UiLanguageResolver 3단 fallback (SIM/시스템/English) 참조
 */
data class SimContext(
    val mcc: String,                    // Mobile Country Code (예: "450")
    val mnc: String,                    // Mobile Network Code (예: "08")
    val countryIso: String,             // ISO 3166-1 alpha-2 (예: "KR")
    val operatorName: String,           // 통신사 이름 (예: "SK Telecom")
    val currency: Currency,             // ISO 4217 (countryIso → 매핑)
    val phoneRegion: String,            // libphonenumber Region (countryIso 그대로)
    val timezone: TimeZone,             // SIM 또는 디바이스 timezone
) {
    companion object {

        /**
         * SIM 부재 시 fallback (WiFi-only 태블릿, SIM 미장착).
         *
         * countryIso는 디바이스 시스템 Locale country (헌법 §8-5 정합).
         * 사용자에게 명시 (Settings 배너): "SIM 미감지. 디바이스 설정 사용."
         */
        fun fallback(systemCountryIso: String): SimContext {
            val country = systemCountryIso.uppercase().ifEmpty { "US" }
            val currency = CountryCurrencyMapper.resolve(country)
            return SimContext(
                mcc = "",
                mnc = "",
                countryIso = country,
                operatorName = "",
                currency = currency,
                phoneRegion = country,
                timezone = TimeZone.getDefault(),
            )
        }
    }
}
