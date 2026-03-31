package app.callcheck.mobile.feature.countryconfig;

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
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\n\u0010\u0002\u001a\u0004\u0018\u00010\u0003H&J\b\u0010\u0004\u001a\u00020\u0003H&J\u0012\u0010\u0005\u001a\u00020\u00062\b\u0010\u0007\u001a\u0004\u0018\u00010\u0003H&\u00a8\u0006\b"}, d2 = {"Lapp/callcheck/mobile/feature/countryconfig/LanguageContextProvider;", "", "getAppSettingOverride", "Lapp/callcheck/mobile/feature/countryconfig/SupportedLanguage;", "resolveLanguage", "setAppSettingOverride", "", "language", "country-config_debug"})
public abstract interface LanguageContextProvider {
    
    /**
     * 현재 결정된 언어를 반환한다.
     * 우선순위 체인을 거쳐 최종 결정된 단일 언어.
     */
    @org.jetbrains.annotations.NotNull()
    public abstract app.callcheck.mobile.feature.countryconfig.SupportedLanguage resolveLanguage();
    
    /**
     * 앱 설정에 수동 오버라이드 언어가 있는지 확인한다.
     * null이면 기기 자동 동기화 모드.
     */
    @org.jetbrains.annotations.Nullable()
    public abstract app.callcheck.mobile.feature.countryconfig.SupportedLanguage getAppSettingOverride();
    
    /**
     * 수동 오버라이드 언어를 설정한다.
     * null을 전달하면 기기 자동 동기화 모드로 복귀.
     *
     * @param language 설정할 언어. null이면 자동 모드.
     */
    public abstract void setAppSettingOverride(@org.jetbrains.annotations.Nullable()
    app.callcheck.mobile.feature.countryconfig.SupportedLanguage language);
}