package app.callcheck.mobile.feature.callintercept;

/**
 * 190개국 검색 프로바이더 전수 레지스트리.
 *
 * 자비스 기준:
 * - 190개국 동시 출시
 * - 국가별 1순위/2순위/3순위 검색엔진
 * - 현지어 쿼리 템플릿
 * - 위험/안전/기관 키워드 사전
 * - 2초 SLA 강제
 * - 금지 엔진 (CN→Google, KR→Google 1순위 금지 등)
 *
 * 4-Tier 체계:
 * - Tier A (8국): 현지 검색엔진 강국 — KR, CN, JP, RU, CZ, TW, VN, TH
 * - Tier B (22국): Google + 현지 디렉토리 병행
 * - Tier C (40국): Google 중심 + 지역 디렉토리
 * - Tier D (120국): Google fallback + Truecaller/Whoscall
 *
 * 구현률 관리:
 * - Registry 완료: 190/190
 * - 현지어 쿼리: Tier A 전수 + Tier B 전수 + Tier C 주요 + Tier D 영어 기본
 * - 파서: 전 국가 공통 ParsingRules + Tier A/B 커스텀 가중치
 * - 2초 SLA: 전 국가 hardDeadline=2000ms 적용
 *
 * 100% 온디바이스. 서버 전송 없음.
 */
@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\"\n\u0002\b\u0005\b\u0007\u0018\u0000 \"2\u00020\u0001:\u0001\"B\u0007\b\u0007\u00a2\u0006\u0002\u0010\u0002J\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00060\bJ\u0014\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00060\u0004H\u0002J\u000e\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00060\bH\u0002J\u000e\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00060\bH\u0002J\u000e\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00060\bH\u0002J\u000e\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00060\bH\u0002J\u0014\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00060\b2\u0006\u0010\u000f\u001a\u00020\u0010J\u0006\u0010\u0011\u001a\u00020\u0005J\u000e\u0010\u0012\u001a\u00020\u00062\u0006\u0010\u0013\u001a\u00020\u0005J\u0010\u0010\u0014\u001a\u00020\u00052\u0006\u0010\u0015\u001a\u00020\u0005H\u0002J\u0006\u0010\u0016\u001a\u00020\u0017JX\u0010\u0018\u001a\u00020\u00062\u0006\u0010\u0015\u001a\u00020\u00052\u0006\u0010\u0019\u001a\u00020\u00052\u0006\u0010\u001a\u001a\u00020\u001b2\f\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u00050\b2\f\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u00050\u001e2\f\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u00050\u001e2\f\u0010 \u001a\b\u0012\u0004\u0012\u00020\u00050\u001eH\u0002J\u0012\u0010!\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00170\u0004R\u001a\u0010\u0003\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00060\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006#"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/GlobalSearchProviderRegistry;", "", "()V", "registry", "", "", "Lapp/callcheck/mobile/core/model/CountrySearchConfig;", "allCountries", "", "buildFullRegistry", "buildTierA", "buildTierB", "buildTierC", "buildTierD", "countriesByTier", "tier", "Lapp/callcheck/mobile/core/model/SearchTier;", "getComplianceReport", "getConfig", "countryCode", "inferLanguage", "cc", "registeredCountryCount", "", "tierB", "lang", "tertiary", "Lapp/callcheck/mobile/core/model/SearchEngine;", "templates", "risk", "", "safe", "institution", "tierCounts", "Companion", "call-intercept_debug"})
public final class GlobalSearchProviderRegistry {
    @org.jetbrains.annotations.NotNull()
    private final java.util.Map<java.lang.String, app.callcheck.mobile.core.model.CountrySearchConfig> registry = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> EN_RISK_KEYWORDS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> EN_SAFE_KEYWORDS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> EN_INSTITUTION_KEYWORDS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> KO_RISK_KEYWORDS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> KO_SAFE_KEYWORDS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> KO_INSTITUTION_KEYWORDS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> JA_RISK_KEYWORDS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> JA_SAFE_KEYWORDS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> JA_INSTITUTION_KEYWORDS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> ZH_RISK_KEYWORDS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> ZH_SAFE_KEYWORDS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> ZH_INSTITUTION_KEYWORDS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> RU_RISK_KEYWORDS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> RU_SAFE_KEYWORDS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> RU_INSTITUTION_KEYWORDS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> ES_RISK_KEYWORDS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> ES_SAFE_KEYWORDS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> ES_INSTITUTION_KEYWORDS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> FR_RISK_KEYWORDS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> FR_SAFE_KEYWORDS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> FR_INSTITUTION_KEYWORDS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> DE_RISK_KEYWORDS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> DE_SAFE_KEYWORDS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> DE_INSTITUTION_KEYWORDS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> PT_RISK_KEYWORDS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> PT_SAFE_KEYWORDS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> PT_INSTITUTION_KEYWORDS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> AR_RISK_KEYWORDS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> AR_SAFE_KEYWORDS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> AR_INSTITUTION_KEYWORDS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> HI_RISK_KEYWORDS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> HI_SAFE_KEYWORDS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> HI_INSTITUTION_KEYWORDS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> TH_RISK_KEYWORDS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> TH_SAFE_KEYWORDS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> VI_RISK_KEYWORDS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> VI_SAFE_KEYWORDS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> TR_RISK_KEYWORDS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> TR_SAFE_KEYWORDS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> ID_RISK_KEYWORDS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> ID_SAFE_KEYWORDS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> MS_RISK_KEYWORDS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> PL_RISK_KEYWORDS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> NL_RISK_KEYWORDS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> IT_RISK_KEYWORDS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> SV_RISK_KEYWORDS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> CZ_RISK_KEYWORDS = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> ZH_TW_RISK_KEYWORDS = null;
    
    /**
     * 미등록 국가 기본 설정
     */
    @org.jetbrains.annotations.NotNull()
    private static final app.callcheck.mobile.core.model.CountrySearchConfig GLOBAL_FALLBACK = null;
    @org.jetbrains.annotations.NotNull()
    public static final app.callcheck.mobile.feature.callintercept.GlobalSearchProviderRegistry.Companion Companion = null;
    
    @javax.inject.Inject()
    public GlobalSearchProviderRegistry() {
        super();
    }
    
    /**
     * 국가 검색 설정 조회. 미등록 → GLOBAL_FALLBACK
     */
    @org.jetbrains.annotations.NotNull()
    public final app.callcheck.mobile.core.model.CountrySearchConfig getConfig(@org.jetbrains.annotations.NotNull()
    java.lang.String countryCode) {
        return null;
    }
    
    /**
     * 등록된 국가 수
     */
    public final int registeredCountryCount() {
        return 0;
    }
    
    /**
     * 티어별 국가 수
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.Map<app.callcheck.mobile.core.model.SearchTier, java.lang.Integer> tierCounts() {
        return null;
    }
    
    /**
     * 전체 국가 목록 (티어별 정렬)
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<app.callcheck.mobile.core.model.CountrySearchConfig> allCountries() {
        return null;
    }
    
    /**
     * 특정 티어의 국가 목록
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<app.callcheck.mobile.core.model.CountrySearchConfig> countriesByTier(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.core.model.SearchTier tier) {
        return null;
    }
    
    /**
     * 국가별 SLA 통과 여부 보고용
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getComplianceReport() {
        return null;
    }
    
    private final java.util.Map<java.lang.String, app.callcheck.mobile.core.model.CountrySearchConfig> buildFullRegistry() {
        return null;
    }
    
    private final java.util.List<app.callcheck.mobile.core.model.CountrySearchConfig> buildTierA() {
        return null;
    }
    
    private final java.util.List<app.callcheck.mobile.core.model.CountrySearchConfig> buildTierB() {
        return null;
    }
    
    private final app.callcheck.mobile.core.model.CountrySearchConfig tierB(java.lang.String cc, java.lang.String lang, app.callcheck.mobile.core.model.SearchEngine tertiary, java.util.List<java.lang.String> templates, java.util.Set<java.lang.String> risk, java.util.Set<java.lang.String> safe, java.util.Set<java.lang.String> institution) {
        return null;
    }
    
    private final java.util.List<app.callcheck.mobile.core.model.CountrySearchConfig> buildTierC() {
        return null;
    }
    
    private final java.util.List<app.callcheck.mobile.core.model.CountrySearchConfig> buildTierD() {
        return null;
    }
    
    /**
     * 국가코드 → 주 언어 추론
     */
    private final java.lang.String inferLanguage(java.lang.String cc) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\"\n\u0002\u0010\u000e\n\u0002\b!\n\u0002\u0018\u0002\n\u0002\bC\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0017\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\u0007R\u0017\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\u0007R\u0017\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u0007R\u0017\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0007R\u0017\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0007R\u0017\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0007R\u0017\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0007R\u0017\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0007R\u0017\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0007R\u0017\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0007R\u0017\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u0007R\u0017\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u0007R\u0017\u0010 \u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\u0007R\u0017\u0010\"\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010\u0007R\u0017\u0010$\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b%\u0010\u0007R\u0011\u0010&\u001a\u00020\'\u00a2\u0006\b\n\u0000\u001a\u0004\b(\u0010)R\u0017\u0010*\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b+\u0010\u0007R\u0017\u0010,\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b-\u0010\u0007R\u0017\u0010.\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b/\u0010\u0007R\u0017\u00100\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b1\u0010\u0007R\u0017\u00102\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b3\u0010\u0007R\u0017\u00104\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b5\u0010\u0007R\u0017\u00106\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b7\u0010\u0007R\u0017\u00108\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b9\u0010\u0007R\u0017\u0010:\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b;\u0010\u0007R\u0017\u0010<\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b=\u0010\u0007R\u0017\u0010>\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b?\u0010\u0007R\u0017\u0010@\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\bA\u0010\u0007R\u0017\u0010B\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\bC\u0010\u0007R\u0017\u0010D\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\bE\u0010\u0007R\u0017\u0010F\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\bG\u0010\u0007R\u0017\u0010H\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\bI\u0010\u0007R\u0017\u0010J\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\bK\u0010\u0007R\u0017\u0010L\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\bM\u0010\u0007R\u0017\u0010N\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\bO\u0010\u0007R\u0017\u0010P\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\bQ\u0010\u0007R\u0017\u0010R\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\bS\u0010\u0007R\u0017\u0010T\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\bU\u0010\u0007R\u0017\u0010V\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\bW\u0010\u0007R\u0017\u0010X\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\bY\u0010\u0007R\u0017\u0010Z\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b[\u0010\u0007R\u0017\u0010\\\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b]\u0010\u0007R\u0017\u0010^\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b_\u0010\u0007R\u0017\u0010`\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\ba\u0010\u0007R\u0017\u0010b\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\bc\u0010\u0007R\u0017\u0010d\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\be\u0010\u0007R\u0017\u0010f\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\bg\u0010\u0007R\u0017\u0010h\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\bi\u0010\u0007\u00a8\u0006j"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/GlobalSearchProviderRegistry$Companion;", "", "()V", "AR_INSTITUTION_KEYWORDS", "", "", "getAR_INSTITUTION_KEYWORDS", "()Ljava/util/Set;", "AR_RISK_KEYWORDS", "getAR_RISK_KEYWORDS", "AR_SAFE_KEYWORDS", "getAR_SAFE_KEYWORDS", "CZ_RISK_KEYWORDS", "getCZ_RISK_KEYWORDS", "DE_INSTITUTION_KEYWORDS", "getDE_INSTITUTION_KEYWORDS", "DE_RISK_KEYWORDS", "getDE_RISK_KEYWORDS", "DE_SAFE_KEYWORDS", "getDE_SAFE_KEYWORDS", "EN_INSTITUTION_KEYWORDS", "getEN_INSTITUTION_KEYWORDS", "EN_RISK_KEYWORDS", "getEN_RISK_KEYWORDS", "EN_SAFE_KEYWORDS", "getEN_SAFE_KEYWORDS", "ES_INSTITUTION_KEYWORDS", "getES_INSTITUTION_KEYWORDS", "ES_RISK_KEYWORDS", "getES_RISK_KEYWORDS", "ES_SAFE_KEYWORDS", "getES_SAFE_KEYWORDS", "FR_INSTITUTION_KEYWORDS", "getFR_INSTITUTION_KEYWORDS", "FR_RISK_KEYWORDS", "getFR_RISK_KEYWORDS", "FR_SAFE_KEYWORDS", "getFR_SAFE_KEYWORDS", "GLOBAL_FALLBACK", "Lapp/callcheck/mobile/core/model/CountrySearchConfig;", "getGLOBAL_FALLBACK", "()Lapp/callcheck/mobile/core/model/CountrySearchConfig;", "HI_INSTITUTION_KEYWORDS", "getHI_INSTITUTION_KEYWORDS", "HI_RISK_KEYWORDS", "getHI_RISK_KEYWORDS", "HI_SAFE_KEYWORDS", "getHI_SAFE_KEYWORDS", "ID_RISK_KEYWORDS", "getID_RISK_KEYWORDS", "ID_SAFE_KEYWORDS", "getID_SAFE_KEYWORDS", "IT_RISK_KEYWORDS", "getIT_RISK_KEYWORDS", "JA_INSTITUTION_KEYWORDS", "getJA_INSTITUTION_KEYWORDS", "JA_RISK_KEYWORDS", "getJA_RISK_KEYWORDS", "JA_SAFE_KEYWORDS", "getJA_SAFE_KEYWORDS", "KO_INSTITUTION_KEYWORDS", "getKO_INSTITUTION_KEYWORDS", "KO_RISK_KEYWORDS", "getKO_RISK_KEYWORDS", "KO_SAFE_KEYWORDS", "getKO_SAFE_KEYWORDS", "MS_RISK_KEYWORDS", "getMS_RISK_KEYWORDS", "NL_RISK_KEYWORDS", "getNL_RISK_KEYWORDS", "PL_RISK_KEYWORDS", "getPL_RISK_KEYWORDS", "PT_INSTITUTION_KEYWORDS", "getPT_INSTITUTION_KEYWORDS", "PT_RISK_KEYWORDS", "getPT_RISK_KEYWORDS", "PT_SAFE_KEYWORDS", "getPT_SAFE_KEYWORDS", "RU_INSTITUTION_KEYWORDS", "getRU_INSTITUTION_KEYWORDS", "RU_RISK_KEYWORDS", "getRU_RISK_KEYWORDS", "RU_SAFE_KEYWORDS", "getRU_SAFE_KEYWORDS", "SV_RISK_KEYWORDS", "getSV_RISK_KEYWORDS", "TH_RISK_KEYWORDS", "getTH_RISK_KEYWORDS", "TH_SAFE_KEYWORDS", "getTH_SAFE_KEYWORDS", "TR_RISK_KEYWORDS", "getTR_RISK_KEYWORDS", "TR_SAFE_KEYWORDS", "getTR_SAFE_KEYWORDS", "VI_RISK_KEYWORDS", "getVI_RISK_KEYWORDS", "VI_SAFE_KEYWORDS", "getVI_SAFE_KEYWORDS", "ZH_INSTITUTION_KEYWORDS", "getZH_INSTITUTION_KEYWORDS", "ZH_RISK_KEYWORDS", "getZH_RISK_KEYWORDS", "ZH_SAFE_KEYWORDS", "getZH_SAFE_KEYWORDS", "ZH_TW_RISK_KEYWORDS", "getZH_TW_RISK_KEYWORDS", "call-intercept_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Set<java.lang.String> getEN_RISK_KEYWORDS() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Set<java.lang.String> getEN_SAFE_KEYWORDS() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Set<java.lang.String> getEN_INSTITUTION_KEYWORDS() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Set<java.lang.String> getKO_RISK_KEYWORDS() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Set<java.lang.String> getKO_SAFE_KEYWORDS() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Set<java.lang.String> getKO_INSTITUTION_KEYWORDS() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Set<java.lang.String> getJA_RISK_KEYWORDS() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Set<java.lang.String> getJA_SAFE_KEYWORDS() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Set<java.lang.String> getJA_INSTITUTION_KEYWORDS() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Set<java.lang.String> getZH_RISK_KEYWORDS() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Set<java.lang.String> getZH_SAFE_KEYWORDS() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Set<java.lang.String> getZH_INSTITUTION_KEYWORDS() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Set<java.lang.String> getRU_RISK_KEYWORDS() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Set<java.lang.String> getRU_SAFE_KEYWORDS() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Set<java.lang.String> getRU_INSTITUTION_KEYWORDS() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Set<java.lang.String> getES_RISK_KEYWORDS() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Set<java.lang.String> getES_SAFE_KEYWORDS() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Set<java.lang.String> getES_INSTITUTION_KEYWORDS() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Set<java.lang.String> getFR_RISK_KEYWORDS() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Set<java.lang.String> getFR_SAFE_KEYWORDS() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Set<java.lang.String> getFR_INSTITUTION_KEYWORDS() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Set<java.lang.String> getDE_RISK_KEYWORDS() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Set<java.lang.String> getDE_SAFE_KEYWORDS() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Set<java.lang.String> getDE_INSTITUTION_KEYWORDS() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Set<java.lang.String> getPT_RISK_KEYWORDS() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Set<java.lang.String> getPT_SAFE_KEYWORDS() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Set<java.lang.String> getPT_INSTITUTION_KEYWORDS() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Set<java.lang.String> getAR_RISK_KEYWORDS() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Set<java.lang.String> getAR_SAFE_KEYWORDS() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Set<java.lang.String> getAR_INSTITUTION_KEYWORDS() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Set<java.lang.String> getHI_RISK_KEYWORDS() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Set<java.lang.String> getHI_SAFE_KEYWORDS() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Set<java.lang.String> getHI_INSTITUTION_KEYWORDS() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Set<java.lang.String> getTH_RISK_KEYWORDS() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Set<java.lang.String> getTH_SAFE_KEYWORDS() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Set<java.lang.String> getVI_RISK_KEYWORDS() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Set<java.lang.String> getVI_SAFE_KEYWORDS() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Set<java.lang.String> getTR_RISK_KEYWORDS() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Set<java.lang.String> getTR_SAFE_KEYWORDS() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Set<java.lang.String> getID_RISK_KEYWORDS() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Set<java.lang.String> getID_SAFE_KEYWORDS() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Set<java.lang.String> getMS_RISK_KEYWORDS() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Set<java.lang.String> getPL_RISK_KEYWORDS() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Set<java.lang.String> getNL_RISK_KEYWORDS() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Set<java.lang.String> getIT_RISK_KEYWORDS() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Set<java.lang.String> getSV_RISK_KEYWORDS() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Set<java.lang.String> getCZ_RISK_KEYWORDS() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Set<java.lang.String> getZH_TW_RISK_KEYWORDS() {
            return null;
        }
        
        /**
         * 미등록 국가 기본 설정
         */
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.core.model.CountrySearchConfig getGLOBAL_FALLBACK() {
            return null;
        }
    }
}