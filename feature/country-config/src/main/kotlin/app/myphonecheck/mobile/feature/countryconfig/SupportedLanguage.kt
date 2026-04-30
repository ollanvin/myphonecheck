package app.myphonecheck.mobile.feature.countryconfig

/**
 * MyPhoneCheck 지원 언어.
 *
 * 헌법 §9-1 빅테크 정공법: 앱 코드/리소스는 영문 단일.
 * 다국어 표시는 OS Locale + ICU + Locale fallback 만 사용한다.
 * values-{locale} 수동 추가 / 다국어 하드코딩 분기 영구 금지.
 */
enum class SupportedLanguage(
    /** ISO 639-1 언어 코드 */
    val code: String,
    /** 원어 표기 (설정 UI용) */
    val nativeName: String,
    /** 영어 표기 (폴백/로그용) */
    val englishName: String,
) {
    EN("en", "English", "English"),
    ;

    companion object {

        /**
         * ISO 639-1 코드 매칭. 영문(`en`)만 매칭한다.
         * 다른 모든 코드는 null 반환 (헌법 §9-1).
         *
         * @param languageCode ISO 639-1 언어 코드 (대소문자 무관)
         */
        fun fromCode(languageCode: String): SupportedLanguage? {
            val lower = languageCode.lowercase()
            return entries.find { it.code == lower }
        }

        /**
         * 모든 코드는 EN 으로 폴백한다 (영문 단일 정책).
         */
        fun fromCodeOrDefault(languageCode: String): SupportedLanguage {
            return fromCode(languageCode) ?: EN
        }
    }
}
