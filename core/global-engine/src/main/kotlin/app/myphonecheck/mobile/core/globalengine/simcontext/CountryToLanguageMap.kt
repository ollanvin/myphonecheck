package app.myphonecheck.mobile.core.globalengine.simcontext

/**
 * ISO 3166-1 → BCP-47 언어 태그 매핑 (Architecture v2.1.0 §29 + 헌법 §8-2).
 *
 * Stage 2-009 신설 (PR #30) — 기존 UiLanguageResolver의 private companion COUNTRY_TO_LANGUAGE를
 * 별도 object로 분리·확장 (단순 lookup table, 코드 분기 0).
 *
 * 본 매핑은 SIM_BASED preference의 default. 사용자가 DEVICE_SYSTEM 또는 ENGLISH로 override 가능.
 *
 * 매핑 안 된 국가는 null 반환 → 호출 측이 fallback 처리 (UiLanguageApplicator에서 영문).
 *
 * 헌법 §8-2 비적용 영역 (UI 언어): 사용자 3단 fallback 선택 가능.
 */
object CountryToLanguageMap {

    private val map: Map<String, String> = mapOf(
        // 동아시아
        "KR" to "ko",
        "JP" to "ja",
        "CN" to "zh-CN",
        "TW" to "zh-TW",
        "HK" to "zh-HK",
        // 영어권
        "US" to "en-US",
        "GB" to "en-GB",
        "AU" to "en-AU",
        "CA" to "en-CA",
        "NZ" to "en-NZ",
        "IE" to "en-IE",
        "SG" to "en-SG",
        // 독일어권
        "DE" to "de",
        "AT" to "de-AT",
        "CH" to "de-CH",
        // 프랑스어권
        "FR" to "fr",
        "BE" to "fr-BE",
        "LU" to "fr-LU",
        // 스페인어권
        "ES" to "es",
        "MX" to "es-MX",
        "AR" to "es-AR",
        "CL" to "es-CL",
        "CO" to "es-CO",
        // 이탈리아어
        "IT" to "it",
        // 포르투갈어
        "PT" to "pt-PT",
        "BR" to "pt-BR",
        // 슬라브·러시아
        "RU" to "ru",
        "PL" to "pl",
        "UA" to "uk",
        "CZ" to "cs",
        // 북유럽
        "SE" to "sv",
        "NO" to "nb",
        "DK" to "da",
        "FI" to "fi",
        // 베네룩스
        "NL" to "nl",
        // 동남아·남아시아
        "TH" to "th",
        "VN" to "vi",
        "ID" to "id",
        "MY" to "ms",
        "PH" to "fil",
        "IN" to "hi",
        // 중동·터키
        "TR" to "tr",
        "SA" to "ar",
        "AE" to "ar",
        "EG" to "ar",
        "IL" to "he",
    )

    /**
     * countryIso (대소문자 무관) → BCP-47 언어 태그. 매핑 안 된 경우 null.
     */
    fun resolve(countryIso: String): String? = map[countryIso.uppercase()]

    /** 매핑된 국가 수 (테스트·헌법 정합성 검증용). */
    fun size(): Int = map.size
}
