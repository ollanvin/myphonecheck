package app.myphonecheck.mobile.core.globalengine.simcontext

import java.util.Locale

/**
 * UI 언어 3단 fallback (Architecture v2.0.0 §29-3 + 헌법 §8-2).
 *
 * 1순위 (default): SIM 기반 언어 추론 (예: SIM countryIso="KR" → ko)
 * 2순위: 디바이스 시스템 언어 (Locale.getDefault())
 * 3순위: English (만국 공통)
 *
 * UI 언어만 사용자 선택 가능. 통화·전화번호 양식·파싱 엔진 등 다른 영역은 SIM 그대로.
 *
 * 시나리오:
 *  - 한국인 한국 SIM → 1순위 default → 한국어
 *  - 한국인 해외 SIM → 2순위 사용자 선택 → 한국어 (시스템 그대로)
 *  - 다국적 사용자 → 3순위 사용자 선택 → English
 */
class UiLanguageResolver(
    private val simContext: SimContext,
    private val userPreference: UiLanguagePreference,
) {

    fun resolveLocale(): Locale = when (userPreference) {
        UiLanguagePreference.SIM_BASED -> simBasedLocale()
        UiLanguagePreference.DEVICE_SYSTEM -> Locale.getDefault()
        UiLanguagePreference.ENGLISH -> Locale.ENGLISH
    }

    /**
     * SIM countryIso 기반 언어 추론.
     *
     * 보편 매핑:
     *  - KR → ko, JP → ja, CN → zh, TW → zh-TW, HK → zh-HK
     *  - DE/AT/CH 일부 → de, FR/BE 일부 → fr, ES → es, IT → it
     *  - RU → ru, AR → ar (아랍 국가 다수 매핑은 별도)
     *  - 매핑 안 된 country는 country code를 region으로 한 Locale 반환 (시스템에 의해 처리)
     */
    private fun simBasedLocale(): Locale {
        val country = simContext.countryIso.uppercase()
        val language = COUNTRY_TO_LANGUAGE[country]
        return if (language != null) {
            Locale(language, country)
        } else {
            // 매핑 안 된 country는 country code 그대로 (시스템 fallback)
            Locale("", country)
        }
    }

    companion object {

        /**
         * countryIso → 주 언어 매핑 (보편적, 카드사·국가 분기 코드 0 원칙 정합).
         *
         * 시드 매핑이지만 헌법 §8-2 비적용 영역 (UI 언어). 사용자가 3단 fallback 선택 가능.
         */
        private val COUNTRY_TO_LANGUAGE = mapOf(
            "KR" to "ko",
            "JP" to "ja",
            "CN" to "zh",
            "TW" to "zh",
            "HK" to "zh",
            "DE" to "de",
            "AT" to "de",
            "CH" to "de",
            "FR" to "fr",
            "BE" to "fr",
            "ES" to "es",
            "MX" to "es",
            "AR" to "es",
            "IT" to "it",
            "RU" to "ru",
            "PT" to "pt",
            "BR" to "pt",
            "TR" to "tr",
            "SA" to "ar",
            "AE" to "ar",
            "EG" to "ar",
            "TH" to "th",
            "VN" to "vi",
            "ID" to "id",
            "IN" to "hi",
            "US" to "en",
            "GB" to "en",
            "AU" to "en",
            "CA" to "en",
            "NZ" to "en",
        )
    }
}

/**
 * UI 언어 사용자 선택 옵션 (헌법 §8-2 비적용 영역).
 */
enum class UiLanguagePreference {
    SIM_BASED,      // 1순위 (default)
    DEVICE_SYSTEM,  // 2순위 (사용자 선택)
    ENGLISH,        // 3순위 (만국 공통)
}
