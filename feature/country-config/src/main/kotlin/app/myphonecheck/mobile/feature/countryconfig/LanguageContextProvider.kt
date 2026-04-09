package app.myphonecheck.mobile.feature.countryconfig

/**
 * 언어 컨텍스트 제공자 — 기기 컨텍스트 동기화 기반.
 *
 * ┌──────────────────────────────────────────────────────────────┐
 * │ 언어 선택 우선순위                                            │
 * ├──────────────────────────────────────────────────────────────┤
 * │ Priority 1: App setting (수동 오버라이드, 딥 설정)            │
 * │ Priority 2: OS/App Locale (Configuration.getLocales())       │
 * │ Priority 3: Device Locale (Locale.getDefault())              │
 * │ Priority 4: EN fallback                                      │
 * └──────────────────────────────────────────────────────────────┘
 *
 * 이 인터페이스는 Android Context에 의존하지 않는다.
 * 구현체(LanguageContextProviderImpl)가 Android에서 locale을 읽어
 * 이 인터페이스로 추상화한다.
 */
interface LanguageContextProvider {

    /**
     * 현재 결정된 언어를 반환한다.
     * 우선순위 체인을 거쳐 최종 결정된 단일 언어.
     */
    fun resolveLanguage(): SupportedLanguage

    /**
     * 앱 설정에 수동 오버라이드 언어가 있는지 확인한다.
     * null이면 기기 자동 동기화 모드.
     */
    fun getAppSettingOverride(): SupportedLanguage?

    /**
     * 수동 오버라이드 언어를 설정한다.
     * null을 전달하면 기기 자동 동기화 모드로 복귀.
     *
     * @param language 설정할 언어. null이면 자동 모드.
     */
    fun setAppSettingOverride(language: SupportedLanguage?)
}
