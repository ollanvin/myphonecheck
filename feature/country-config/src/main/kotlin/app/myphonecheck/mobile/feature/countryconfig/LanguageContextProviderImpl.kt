package app.myphonecheck.mobile.feature.countryconfig

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * LanguageContextProvider 구현체.
 *
 * 헌법 §9-1 정책: 앱 내부 SupportedLanguage 는 EN 단일.
 * 다국어 표시는 OS Locale + Android resource framework + ICU 가 처리한다.
 * App Setting 오버라이드 / 4단 fallback 영구 금지.
 *
 * resolveLanguage() 는 항상 SupportedLanguage.EN 반환.
 */
@Singleton
class LanguageContextProviderImpl @Inject constructor(
    @Suppress("UNUSED_PARAMETER")
    @ApplicationContext context: Context,
) : LanguageContextProvider {

    override fun resolveLanguage(): SupportedLanguage = SupportedLanguage.EN

    override fun getAppSettingOverride(): SupportedLanguage? = null

    override fun setAppSettingOverride(language: SupportedLanguage?) {
        // No-op. 영문 단일 정책 — 오버라이드 미지원.
    }
}
