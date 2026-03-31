package app.callcheck.mobile.feature.countryconfig;

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
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0010\b\u0086\u0081\u0002\u0018\u0000 \u00122\b\u0012\u0004\u0012\u00020\u00000\u0001:\u0001\u0012B\u001f\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0006R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\bR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\bj\u0002\b\u000bj\u0002\b\fj\u0002\b\rj\u0002\b\u000ej\u0002\b\u000fj\u0002\b\u0010j\u0002\b\u0011\u00a8\u0006\u0013"}, d2 = {"Lapp/callcheck/mobile/feature/countryconfig/SupportedLanguage;", "", "code", "", "nativeName", "englishName", "(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getCode", "()Ljava/lang/String;", "getEnglishName", "getNativeName", "KO", "EN", "JA", "ZH", "RU", "ES", "AR", "Companion", "country-config_debug"})
public enum SupportedLanguage {
    /*public static final*/ KO /* = new KO(null, null, null) */,
    /*public static final*/ EN /* = new EN(null, null, null) */,
    /*public static final*/ JA /* = new JA(null, null, null) */,
    /*public static final*/ ZH /* = new ZH(null, null, null) */,
    /*public static final*/ RU /* = new RU(null, null, null) */,
    /*public static final*/ ES /* = new ES(null, null, null) */,
    /*public static final*/ AR /* = new AR(null, null, null) */;
    
    /**
     * ISO 639-1 언어 코드
     */
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String code = null;
    
    /**
     * 원어 표기 (설정 UI용)
     */
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String nativeName = null;
    
    /**
     * 영어 표기 (폴백/로그용)
     */
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String englishName = null;
    @org.jetbrains.annotations.NotNull()
    public static final app.callcheck.mobile.feature.countryconfig.SupportedLanguage.Companion Companion = null;
    
    SupportedLanguage(java.lang.String code, java.lang.String nativeName, java.lang.String englishName) {
    }
    
    /**
     * ISO 639-1 언어 코드
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getCode() {
        return null;
    }
    
    /**
     * 원어 표기 (설정 UI용)
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getNativeName() {
        return null;
    }
    
    /**
     * 영어 표기 (폴백/로그용)
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getEnglishName() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public static kotlin.enums.EnumEntries<app.callcheck.mobile.feature.countryconfig.SupportedLanguage> getEntries() {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u0005\u001a\u00020\u0006J\u000e\u0010\u0007\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006\u00a8\u0006\b"}, d2 = {"Lapp/callcheck/mobile/feature/countryconfig/SupportedLanguage$Companion;", "", "()V", "fromCode", "Lapp/callcheck/mobile/feature/countryconfig/SupportedLanguage;", "languageCode", "", "fromCodeOrDefault", "country-config_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        /**
         * ISO 639-1 코드로 SupportedLanguage를 찾는다.
         * 지원하지 않는 코드면 null 반환.
         *
         * @param languageCode ISO 639-1 언어 코드 (대소문자 무관)
         */
        @org.jetbrains.annotations.Nullable()
        public final app.callcheck.mobile.feature.countryconfig.SupportedLanguage fromCode(@org.jetbrains.annotations.NotNull()
        java.lang.String languageCode) {
            return null;
        }
        
        /**
         * ISO 639-1 코드로 SupportedLanguage를 찾되, 없으면 EN을 반환.
         */
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.countryconfig.SupportedLanguage fromCodeOrDefault(@org.jetbrains.annotations.NotNull()
        java.lang.String languageCode) {
            return null;
        }
    }
}