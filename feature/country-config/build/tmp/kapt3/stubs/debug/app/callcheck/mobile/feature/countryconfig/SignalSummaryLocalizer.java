package app.callcheck.mobile.feature.countryconfig;

/**
 * SignalSummary 로컬라이저 — 번역기가 아니라 언어별 템플릿 선택기.
 *
 * ┌──────────────────────────────────────────────────────────────┐
 * │ 핵심 원칙                                                     │
 * ├──────────────────────────────────────────────────────────────┤
 * │ 1. SearchResultAnalyzer는 언어 중립을 유지한다               │
 * │    → intensity 상수 + category enum을 반환                    │
 * │ 2. SignalSummaryLocalizer는 그 결과를 받아                    │
 * │    → 현재 언어에 맞는 템플릿을 선택한다                       │
 * │ 3. "번역"이 아니라 "선택" — 각 언어는 자체 표현 체계를 갖는다│
 * │ 4. 새 언어 추가 = 새 템플릿 맵 추가 (기존 코드 수정 없음)   │
 * └──────────────────────────────────────────────────────────────┘
 *
 * 데이터 흐름:
 * ```
 * SearchResultAnalyzer
 *  → intensity: "SAFE" | "REFERENCE" | "CAUTION_LIGHT" | ...
 *  → category: ConclusionCategory enum
 *       │
 *       ▼
 * SignalSummaryLocalizer.localize(intensity, category, language)
 *  → 사용자 대면 로컬라이즈 텍스트
 * ```
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u0000 \f2\u00020\u0001:\u0001\fB\u0005\u00a2\u0006\u0002\u0010\u0002J*\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0006\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\b2\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u0004J\"\u0010\n\u001a\u00020\u00042\u0006\u0010\u0006\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\b2\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u0004J\u0016\u0010\u000b\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\b\u00a8\u0006\r"}, d2 = {"Lapp/callcheck/mobile/feature/countryconfig/SignalSummaryLocalizer;", "", "()V", "localize", "", "intensityKey", "categoryKey", "language", "Lapp/callcheck/mobile/feature/countryconfig/SupportedLanguage;", "entityName", "localizeCategory", "localizeIntensity", "Companion", "country-config_debug"})
public final class SignalSummaryLocalizer {
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String KEY_SAFE = "SAFE";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String KEY_REFERENCE = "REFERENCE";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String KEY_CAUTION_LIGHT = "CAUTION_LIGHT";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String KEY_CAUTION = "CAUTION";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String KEY_DANGER = "DANGER";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String KEY_REJECT = "REJECT";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String KEY_VERIFY = "VERIFY";
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Map<app.callcheck.mobile.feature.countryconfig.SupportedLanguage, java.util.Map<java.lang.String, java.lang.String>> intensityTemplates = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Map<app.callcheck.mobile.feature.countryconfig.SupportedLanguage, java.util.Map<java.lang.String, java.lang.String>> categoryTemplates = null;
    @org.jetbrains.annotations.NotNull()
    public static final app.callcheck.mobile.feature.countryconfig.SignalSummaryLocalizer.Companion Companion = null;
    
    public SignalSummaryLocalizer() {
        super();
    }
    
    /**
     * 위험도 수준(intensity)을 현재 언어로 로컬라이즈한다.
     *
     * @param intensityKey INTENSITY_* 상수의 키 (예: "SAFE", "DANGER")
     * @param language 대상 언어
     * @return 로컬라이즈된 위험도 텍스트. 매칭 실패 시 EN 폴백.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String localizeIntensity(@org.jetbrains.annotations.NotNull()
    java.lang.String intensityKey, @org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.countryconfig.SupportedLanguage language) {
        return null;
    }
    
    /**
     * 카테고리별 문장 템플릿을 현재 언어로 로컬라이즈한다.
     *
     * @param categoryKey ConclusionCategory enum의 name (예: "SCAM_RISK_HIGH")
     * @param language 대상 언어
     * @param entityName 엔티티명 (검색에서 발견된 업체/기관명). null이면 일반형 사용.
     * @return 로컬라이즈된 문장. 매칭 실패 시 EN 폴백.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String localizeCategory(@org.jetbrains.annotations.NotNull()
    java.lang.String categoryKey, @org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.countryconfig.SupportedLanguage language, @org.jetbrains.annotations.Nullable()
    java.lang.String entityName) {
        return null;
    }
    
    /**
     * 전체 SignalSummary 텍스트를 로컬라이즈한다.
     * intensity + category를 조합하여 최종 사용자 대면 텍스트를 생성.
     *
     * @param intensityKey INTENSITY_* 상수 키
     * @param categoryKey ConclusionCategory enum name
     * @param language 대상 언어
     * @param entityName 엔티티명 (선택)
     * @return "위험도 — 카테고리 설명" 형태의 로컬라이즈된 문장
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String localize(@org.jetbrains.annotations.NotNull()
    java.lang.String intensityKey, @org.jetbrains.annotations.NotNull()
    java.lang.String categoryKey, @org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.countryconfig.SupportedLanguage language, @org.jetbrains.annotations.Nullable()
    java.lang.String entityName) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010$\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R&\u0010\u000b\u001a\u001a\u0012\u0004\u0012\u00020\r\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00040\f0\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R&\u0010\u000e\u001a\u001a\u0012\u0004\u0012\u00020\r\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00040\f0\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000f"}, d2 = {"Lapp/callcheck/mobile/feature/countryconfig/SignalSummaryLocalizer$Companion;", "", "()V", "KEY_CAUTION", "", "KEY_CAUTION_LIGHT", "KEY_DANGER", "KEY_REFERENCE", "KEY_REJECT", "KEY_SAFE", "KEY_VERIFY", "categoryTemplates", "", "Lapp/callcheck/mobile/feature/countryconfig/SupportedLanguage;", "intensityTemplates", "country-config_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}