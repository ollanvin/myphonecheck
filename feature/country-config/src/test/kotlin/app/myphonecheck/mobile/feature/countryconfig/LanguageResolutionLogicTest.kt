package app.myphonecheck.mobile.feature.countryconfig

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * 언어 선택 우선순위 로직 테스트.
 *
 * ┌──────────────────────────────────────────────────────────────┐
 * │ 우선순위 체인                                                 │
 * ├──────────────────────────────────────────────────────────────┤
 * │ Priority 1: App setting (수동 오버라이드)                     │
 * │ Priority 2: OS/App Locale                                    │
 * │ Priority 3: Device Locale                                    │
 * │ Priority 4: EN fallback                                      │
 * └──────────────────────────────────────────────────────────────┘
 *
 * Android Context 없이 순수 JVM에서 우선순위 로직을 검증한다.
 * resolveLanguage 함수를 시뮬레이션하여 우선순위 체인을 테스트.
 */
class LanguageResolutionLogicTest {

    /**
     * 우선순위 로직 시뮬레이션.
     * LanguageContextProviderImpl.resolveLanguage()와 동일한 로직.
     */
    private fun resolveLanguage(
        appSettingOverride: SupportedLanguage?,
        osLocaleLanguageCode: String?,
        deviceLocaleLanguageCode: String?,
    ): SupportedLanguage {
        // Priority 1
        appSettingOverride?.let { return it }

        // Priority 2
        osLocaleLanguageCode?.let {
            SupportedLanguage.fromCode(it)?.let { lang -> return lang }
        }

        // Priority 3
        deviceLocaleLanguageCode?.let {
            SupportedLanguage.fromCode(it)?.let { lang -> return lang }
        }

        // Priority 4
        return SupportedLanguage.EN
    }

    // ═══════════════════════════════════════════════════════════
    // Priority 1: App setting override 최우선
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `app setting override takes highest priority`() {
        val result = resolveLanguage(
            appSettingOverride = SupportedLanguage.JA,
            osLocaleLanguageCode = "ko",
            deviceLocaleLanguageCode = "en",
        )
        assertEquals(SupportedLanguage.JA, result)
    }

    @Test
    fun `app setting override ignores OS and device locale`() {
        val result = resolveLanguage(
            appSettingOverride = SupportedLanguage.AR,
            osLocaleLanguageCode = "ko",
            deviceLocaleLanguageCode = "ko",
        )
        assertEquals(SupportedLanguage.AR, result)
    }

    // ═══════════════════════════════════════════════════════════
    // Priority 2: OS/App Locale
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `OS locale used when no app override`() {
        val result = resolveLanguage(
            appSettingOverride = null,
            osLocaleLanguageCode = "ko",
            deviceLocaleLanguageCode = "en",
        )
        assertEquals(SupportedLanguage.KO, result)
    }

    @Test
    fun `OS locale with unsupported language falls through to device locale`() {
        val result = resolveLanguage(
            appSettingOverride = null,
            osLocaleLanguageCode = "fr",
            deviceLocaleLanguageCode = "ja",
        )
        assertEquals(SupportedLanguage.JA, result)
    }

    // ═══════════════════════════════════════════════════════════
    // Priority 3: Device Locale
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `device locale used when no app override and OS locale unsupported`() {
        val result = resolveLanguage(
            appSettingOverride = null,
            osLocaleLanguageCode = "de",
            deviceLocaleLanguageCode = "zh",
        )
        assertEquals(SupportedLanguage.ZH, result)
    }

    @Test
    fun `device locale used when OS locale is null`() {
        val result = resolveLanguage(
            appSettingOverride = null,
            osLocaleLanguageCode = null,
            deviceLocaleLanguageCode = "ru",
        )
        assertEquals(SupportedLanguage.RU, result)
    }

    // ═══════════════════════════════════════════════════════════
    // Priority 4: EN fallback
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `EN fallback when all sources null`() {
        val result = resolveLanguage(
            appSettingOverride = null,
            osLocaleLanguageCode = null,
            deviceLocaleLanguageCode = null,
        )
        assertEquals(SupportedLanguage.EN, result)
    }

    @Test
    fun `EN fallback when all locales unsupported`() {
        val result = resolveLanguage(
            appSettingOverride = null,
            osLocaleLanguageCode = "fr",
            deviceLocaleLanguageCode = "de",
        )
        assertEquals(SupportedLanguage.EN, result)
    }

    @Test
    fun `EN fallback when OS locale unsupported and device locale null`() {
        val result = resolveLanguage(
            appSettingOverride = null,
            osLocaleLanguageCode = "pt",
            deviceLocaleLanguageCode = null,
        )
        assertEquals(SupportedLanguage.EN, result)
    }

    // ═══════════════════════════════════════════════════════════
    // 전체 7개 언어 검색 가능 확인
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `all 7 supported languages resolvable from OS locale`() {
        for (lang in SupportedLanguage.entries) {
            val result = resolveLanguage(
                appSettingOverride = null,
                osLocaleLanguageCode = lang.code,
                deviceLocaleLanguageCode = null,
            )
            assertEquals(
                "Language ${lang.code} should resolve to ${lang.name}",
                lang, result
            )
        }
    }
}
