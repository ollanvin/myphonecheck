package app.callcheck.mobile.feature.countryconfig

/**
 * CallCheck 지원 언어 목록.
 *
 * ┌──────────────────────────────────────────────────────────────┐
 * │ 언어 선택 원칙 (기기 컨텍스트 동기화)                         │
 * ├──────────────────────────────────────────────────────────────┤
 * │ 1. 앱 설정(수동 오버라이드)  → 최우선                        │
 * │ 2. OS/App Locale            → 자동 탐지                     │
 * │ 3. Device Locale             → 자동 탐지                     │
 * │ 4. EN fallback               → 최종 기본값                   │
 * ├──────────────────────────────────────────────────────────────┤
 * │ 기본 동작: UI 없음 (기기 자동 동기화)                         │
 * │ 예외: 딥 설정에서 수동 오버라이드 가능                         │
 * └──────────────────────────────────────────────────────────────┘
 *
 * ZH는 향후 ZH_HANS(간체)/ZH_HANT(번체) 분리 확장 여지를 남긴다.
 */
enum class SupportedLanguage(
    /** ISO 639-1 언어 코드 */
    val code: String,
    /** 원어 표기 (설정 UI용) */
    val nativeName: String,
    /** 영어 표기 (폴백/로그용) */
    val englishName: String,
) {
    KO("ko", "한국어", "Korean"),
    EN("en", "English", "English"),
    JA("ja", "日本語", "Japanese"),
    ZH("zh", "中文", "Chinese"),
    RU("ru", "Русский", "Russian"),
    ES("es", "Español", "Spanish"),
    AR("ar", "العربية", "Arabic"),
    ;

    companion object {

        /**
         * ISO 639-1 코드로 SupportedLanguage를 찾는다.
         * 지원하지 않는 코드면 null 반환.
         *
         * @param languageCode ISO 639-1 언어 코드 (대소문자 무관)
         */
        fun fromCode(languageCode: String): SupportedLanguage? {
            val lower = languageCode.lowercase()
            return entries.find { it.code == lower }
        }

        /**
         * ISO 639-1 코드로 SupportedLanguage를 찾되, 없으면 EN을 반환.
         */
        fun fromCodeOrDefault(languageCode: String): SupportedLanguage {
            return fromCode(languageCode) ?: EN
        }
    }
}
