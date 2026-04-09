package app.myphonecheck.mobile.feature.countryconfig

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * LanguageContextProvider 구현체 — Android 기기 컨텍스트 기반.
 *
 * ┌──────────────────────────────────────────────────────────────┐
 * │ 구현 전략                                                     │
 * ├──────────────────────────────────────────────────────────────┤
 * │ 1. appSettingOverride: SharedPreferences에 저장된 수동 설정  │
 * │ 2. osAppLocale: Configuration.getLocales()의 첫 번째 locale  │
 * │ 3. deviceLocale: Locale.getDefault()                        │
 * │ 4. EN fallback: 위 모두 매칭 실패 시                         │
 * ├──────────────────────────────────────────────────────────────┤
 * │ 기본 동작: 언어 선택 UI 없음 (기기 자동 동기화)               │
 * │ 딥 설정에서만 수동 오버라이드 가능                             │
 * └──────────────────────────────────────────────────────────────┘
 */
@Singleton
class LanguageContextProviderImpl @Inject constructor(
    private val context: Context,
) : LanguageContextProvider {

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    override fun resolveLanguage(): SupportedLanguage {
        // Priority 1: App setting override
        getAppSettingOverride()?.let { return it }

        // Priority 2: OS/App Locale
        resolveFromOsAppLocale()?.let { return it }

        // Priority 3: Device Locale
        resolveFromDeviceLocale()?.let { return it }

        // Priority 4: EN fallback
        return SupportedLanguage.EN
    }

    override fun getAppSettingOverride(): SupportedLanguage? {
        val code = prefs.getString(KEY_LANGUAGE_OVERRIDE, null) ?: return null
        return SupportedLanguage.fromCode(code)
    }

    override fun setAppSettingOverride(language: SupportedLanguage?) {
        prefs.edit().apply {
            if (language != null) {
                putString(KEY_LANGUAGE_OVERRIDE, language.code)
            } else {
                remove(KEY_LANGUAGE_OVERRIDE)
            }
            apply()
        }
    }

    // ═══════════════════════════════════════════════════════════
    // Internal: Locale 탐지
    // ═══════════════════════════════════════════════════════════

    /**
     * OS/App Locale에서 지원 언어를 찾는다.
     * Android 7.0+ 에서는 LocaleList의 첫 번째 locale을 사용한다.
     */
    private fun resolveFromOsAppLocale(): SupportedLanguage? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val localeList = context.resources.configuration.locales
            if (localeList.size() > 0) {
                matchLocale(localeList.get(0))
            } else {
                null
            }
        } else {
            @Suppress("DEPRECATION")
            val locale = context.resources.configuration.locale
            matchLocale(locale)
        }
    }

    /**
     * Device Locale (Locale.getDefault())에서 지원 언어를 찾는다.
     */
    private fun resolveFromDeviceLocale(): SupportedLanguage? {
        return matchLocale(Locale.getDefault())
    }

    /**
     * Locale → SupportedLanguage 매칭.
     * ISO 639-1 language code 기준.
     */
    private fun matchLocale(locale: Locale): SupportedLanguage? {
        return SupportedLanguage.fromCode(locale.language)
    }

    companion object {
        private const val PREFS_NAME = "myphonecheck_language_prefs"
        private const val KEY_LANGUAGE_OVERRIDE = "language_override"
    }
}
