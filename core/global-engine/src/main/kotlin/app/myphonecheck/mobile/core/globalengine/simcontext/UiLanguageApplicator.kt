package app.myphonecheck.mobile.core.globalengine.simcontext

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * UI 언어 Locale 적용기 (Architecture v2.1.0 §29 + 헌법 §8-2).
 *
 * AppCompatDelegate.setApplicationLocales (API 33+ native + AppCompat 1.6.0+ 백포트) 활용.
 *
 * 사용자 선택 → preference 저장(:feature:settings DataStore) → 본 클래스가 시스템에 적용.
 * 적용 시 Activity recreate가 자동 트리거됨 (AppCompat 메커니즘).
 *
 * 헌법 §8-2 비적용 영역 (UI 언어 사용자 선택). 통화·전화번호 등 다른 영역은 SIM 그대로.
 */
@Singleton
class UiLanguageApplicator @Inject constructor() {

    /**
     * 사용자 선호 + SIM 컨텍스트를 기반으로 Locale 결정 + 시스템에 적용.
     */
    fun apply(preference: UiLanguagePreference, simContext: SimContext) {
        val locale = resolveLocale(preference, simContext)
        val list = if (locale == null) {
            // DEVICE_SYSTEM 명시 시 emptyLocaleList = 시스템 default 따름.
            LocaleListCompat.getEmptyLocaleList()
        } else {
            LocaleListCompat.create(locale)
        }
        AppCompatDelegate.setApplicationLocales(list)
    }

    /**
     * 테스트 가능한 순수 Locale 결정 로직.
     *
     * @return [Locale] 또는 [DEVICE_SYSTEM] 의도 표현으로 `null` (호출 측이 emptyLocaleList 적용).
     */
    fun resolveLocale(preference: UiLanguagePreference, simContext: SimContext): Locale? = when (preference) {
        UiLanguagePreference.SIM_BASED -> simBasedLocale(simContext)
        UiLanguagePreference.DEVICE_SYSTEM -> null
        UiLanguagePreference.ENGLISH -> Locale.ENGLISH
    }

    /**
     * SIM countryIso → 언어 태그 매핑 + Locale 변환. 매핑 없으면 영문 fallback.
     */
    private fun simBasedLocale(simContext: SimContext): Locale {
        val tag = CountryToLanguageMap.resolve(simContext.countryIso) ?: "en"
        return Locale.forLanguageTag(tag)
    }
}
