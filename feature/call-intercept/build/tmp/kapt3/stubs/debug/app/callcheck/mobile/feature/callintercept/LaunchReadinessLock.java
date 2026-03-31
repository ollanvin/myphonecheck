package app.callcheck.mobile.feature.callintercept;

/**
 * 출시 준비 최종 잠금 장치.
 *
 * 자비스 기준: "국가별 Tier, 검색엔진 우선순위, 하드 룰, SLA 기준을 최종 상수로 고정"
 *
 * 이 클래스의 모든 값은 상수(const/val)이며, 런타임에서 변경 불가.
 * AutoPolicyAdjuster의 자동 보정도 이 하드 룰을 침범 불가.
 *
 * 잠금 대상:
 *  1. 국가별 검색엔진 하드 룰 (절대 변경 금지)
 *  2. 글로벌 SLA 상수 (2초 절대 한계)
 *  3. 최소 안전 표현 (검색 결과 0건이어도 반드시 표시)
 *  4. 국가 Tier 분류 (Tier1/2/3 검증 기준)
 *  5. 출시 PASS/FAIL 판정 기준
 *
 * 100% 온디바이스. 서버 전송 없음.
 */
@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0007\u0018\u0000 \u00112\u00020\u0001:\u0003\u0011\u0012\u0013B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u000e\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\bJ\u000e\u0010\n\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\bJ\u000e\u0010\u000b\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\bJ\u000e\u0010\f\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\bJ\u0006\u0010\r\u001a\u00020\u000eJ\u0006\u0010\u000f\u001a\u00020\u0010R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0014"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/LaunchReadinessLock;", "", "registry", "Lapp/callcheck/mobile/feature/callintercept/GlobalSearchProviderRegistry;", "complianceValidator", "Lapp/callcheck/mobile/feature/callintercept/CountryComplianceValidator;", "(Lapp/callcheck/mobile/feature/callintercept/GlobalSearchProviderRegistry;Lapp/callcheck/mobile/feature/callintercept/CountryComplianceValidator;)V", "getLikelySafeLabel", "", "languageCode", "getMinimumSafeVerdict", "getScamLabel", "getSpamLabel", "isLaunchReady", "Lapp/callcheck/mobile/feature/callintercept/LaunchReadinessLock$LaunchReadiness;", "verifyAllLocks", "Lapp/callcheck/mobile/feature/callintercept/LaunchReadinessLock$LockVerification;", "Companion", "LaunchReadiness", "LockVerification", "call-intercept_debug"})
public final class LaunchReadinessLock {
    @org.jetbrains.annotations.NotNull()
    private final app.callcheck.mobile.feature.callintercept.GlobalSearchProviderRegistry registry = null;
    @org.jetbrains.annotations.NotNull()
    private final app.callcheck.mobile.feature.callintercept.CountryComplianceValidator complianceValidator = null;
    
    /**
     * 전 국가 공통 SLA 한계 (ms). 이 값은 절대 변경 금지.
     */
    public static final long GLOBAL_HARD_DEADLINE_MS = 2000L;
    
    /**
     * 번호 정규화 한계 (ms)
     */
    public static final long NORMALIZE_DEADLINE_MS = 50L;
    
    /**
     * 국가 라우팅 한계 (ms)
     */
    public static final long ROUTING_DEADLINE_MS = 150L;
    
    /**
     * 1순위 검색 기본 한계 (ms)
     */
    public static final long PRIMARY_SEARCH_DEADLINE_MS = 1200L;
    
    /**
     * 2순위 fallback 한계 (ms)
     */
    public static final long SECONDARY_FALLBACK_DEADLINE_MS = 1800L;
    
    /**
     * Early Display 시점 (ms) — 현재까지 결과 1차 표시
     */
    public static final long EARLY_DISPLAY_MS = 1500L;
    
    /**
     * 캐시 히트 목표 한계 (ms)
     */
    public static final long CACHE_HIT_DEADLINE_MS = 50L;
    
    /**
     * 국가별 1순위 검색엔진 강제 매핑. 이 국가들은 자동 보정 불가.
     */
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Map<java.lang.String, app.callcheck.mobile.core.model.SearchEngine> LOCKED_PRIMARY_ENGINES = null;
    
    /**
     * 국가별 글로벌 금지 엔진. 이 엔진은 해당 국가에서 어떤 순위로도 사용 불가.
     */
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Map<java.lang.String, java.util.Set<app.callcheck.mobile.core.model.SearchEngine>> GLOBAL_BANNED_ENGINES = null;
    
    /**
     * 자동 보정 금지 국가. 이 국가들의 엔진 순서는 수동으로만 변경 가능.
     */
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> AUTO_ADJUST_LOCKED_COUNTRIES = null;
    
    /**
     * 결과 부족 시 표현 — "아무것도 안 보여줌" 금지
     */
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Map<java.lang.String, java.lang.String> MINIMUM_SAFE_VERDICTS = null;
    
    /**
     * 스팸 의심 표현
     */
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Map<java.lang.String, java.lang.String> SPAM_SUSPECTED_LABELS = null;
    
    /**
     * 사기 의심 표현
     */
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Map<java.lang.String, java.lang.String> SCAM_RISK_LABELS = null;
    
    /**
     * 기관/배송/서비스 추정 표현
     */
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Map<java.lang.String, java.lang.String> LIKELY_SAFE_LABELS = null;
    
    /**
     * SLA 통과율 하한 (이하면 해당 국가 FAIL)
     */
    public static final float SLA_PASS_RATE_THRESHOLD = 95.0F;
    
    /**
     * 검색 실패율 상한 (이상이면 해당 국가 FAIL)
     */
    public static final float SEARCH_FAILURE_RATE_THRESHOLD = 0.1F;
    
    /**
     * 총 등록 국가 최소 기준
     */
    public static final int MINIMUM_REGISTERED_COUNTRIES = 190;
    
    /**
     * Tier1 국가 최소 기준
     */
    public static final int MINIMUM_TIER1_COUNTRIES = 25;
    @org.jetbrains.annotations.NotNull()
    public static final app.callcheck.mobile.feature.callintercept.LaunchReadinessLock.Companion Companion = null;
    
    @javax.inject.Inject()
    public LaunchReadinessLock(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.callintercept.GlobalSearchProviderRegistry registry, @org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.callintercept.CountryComplianceValidator complianceValidator) {
        super();
    }
    
    /**
     * 전체 잠금 상태 검증.
     * 출시 전 이 검증이 통과해야만 배포 가능.
     */
    @org.jetbrains.annotations.NotNull()
    public final app.callcheck.mobile.feature.callintercept.LaunchReadinessLock.LockVerification verifyAllLocks() {
        return null;
    }
    
    /**
     * 최종 출시 준비 판정.
     */
    @org.jetbrains.annotations.NotNull()
    public final app.callcheck.mobile.feature.callintercept.LaunchReadinessLock.LaunchReadiness isLaunchReady() {
        return null;
    }
    
    /**
     * 결과 부족 시 표현 (언어코드 기반, fallback: 영어)
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getMinimumSafeVerdict(@org.jetbrains.annotations.NotNull()
    java.lang.String languageCode) {
        return null;
    }
    
    /**
     * 스팸 의심 표현
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getSpamLabel(@org.jetbrains.annotations.NotNull()
    java.lang.String languageCode) {
        return null;
    }
    
    /**
     * 사기 위험 표현
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getScamLabel(@org.jetbrains.annotations.NotNull()
    java.lang.String languageCode) {
        return null;
    }
    
    /**
     * 기관/서비스 추정 표현
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getLikelySafeLabel(@org.jetbrains.annotations.NotNull()
    java.lang.String languageCode) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\"\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\b\n\u0002\b\t\n\u0002\u0010\u0007\n\u0002\b\u0005\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0017\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u000e\u0010\b\u001a\u00020\tX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\tX\u0086T\u00a2\u0006\u0002\n\u0000R#\u0010\u000b\u001a\u0014\u0012\u0004\u0012\u00020\u0005\u0012\n\u0012\b\u0012\u0004\u0012\u00020\r0\u00040\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u000e\u0010\u0010\u001a\u00020\tX\u0086T\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u0011\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00050\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u000fR\u001d\u0010\u0013\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\r0\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u000fR\u000e\u0010\u0015\u001a\u00020\u0016X\u0086T\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u0017\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00050\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u000fR\u000e\u0010\u0019\u001a\u00020\u0016X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001a\u001a\u00020\tX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001b\u001a\u00020\tX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001c\u001a\u00020\tX\u0086T\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u001d\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00050\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u000fR\u000e\u0010\u001f\u001a\u00020 X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010!\u001a\u00020\tX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\"\u001a\u00020 X\u0086T\u00a2\u0006\u0002\n\u0000R\u001d\u0010#\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00050\f\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010\u000f\u00a8\u0006%"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/LaunchReadinessLock$Companion;", "", "()V", "AUTO_ADJUST_LOCKED_COUNTRIES", "", "", "getAUTO_ADJUST_LOCKED_COUNTRIES", "()Ljava/util/Set;", "CACHE_HIT_DEADLINE_MS", "", "EARLY_DISPLAY_MS", "GLOBAL_BANNED_ENGINES", "", "Lapp/callcheck/mobile/core/model/SearchEngine;", "getGLOBAL_BANNED_ENGINES", "()Ljava/util/Map;", "GLOBAL_HARD_DEADLINE_MS", "LIKELY_SAFE_LABELS", "getLIKELY_SAFE_LABELS", "LOCKED_PRIMARY_ENGINES", "getLOCKED_PRIMARY_ENGINES", "MINIMUM_REGISTERED_COUNTRIES", "", "MINIMUM_SAFE_VERDICTS", "getMINIMUM_SAFE_VERDICTS", "MINIMUM_TIER1_COUNTRIES", "NORMALIZE_DEADLINE_MS", "PRIMARY_SEARCH_DEADLINE_MS", "ROUTING_DEADLINE_MS", "SCAM_RISK_LABELS", "getSCAM_RISK_LABELS", "SEARCH_FAILURE_RATE_THRESHOLD", "", "SECONDARY_FALLBACK_DEADLINE_MS", "SLA_PASS_RATE_THRESHOLD", "SPAM_SUSPECTED_LABELS", "getSPAM_SUSPECTED_LABELS", "call-intercept_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        /**
         * 국가별 1순위 검색엔진 강제 매핑. 이 국가들은 자동 보정 불가.
         */
        @org.jetbrains.annotations.NotNull()
        public final java.util.Map<java.lang.String, app.callcheck.mobile.core.model.SearchEngine> getLOCKED_PRIMARY_ENGINES() {
            return null;
        }
        
        /**
         * 국가별 글로벌 금지 엔진. 이 엔진은 해당 국가에서 어떤 순위로도 사용 불가.
         */
        @org.jetbrains.annotations.NotNull()
        public final java.util.Map<java.lang.String, java.util.Set<app.callcheck.mobile.core.model.SearchEngine>> getGLOBAL_BANNED_ENGINES() {
            return null;
        }
        
        /**
         * 자동 보정 금지 국가. 이 국가들의 엔진 순서는 수동으로만 변경 가능.
         */
        @org.jetbrains.annotations.NotNull()
        public final java.util.Set<java.lang.String> getAUTO_ADJUST_LOCKED_COUNTRIES() {
            return null;
        }
        
        /**
         * 결과 부족 시 표현 — "아무것도 안 보여줌" 금지
         */
        @org.jetbrains.annotations.NotNull()
        public final java.util.Map<java.lang.String, java.lang.String> getMINIMUM_SAFE_VERDICTS() {
            return null;
        }
        
        /**
         * 스팸 의심 표현
         */
        @org.jetbrains.annotations.NotNull()
        public final java.util.Map<java.lang.String, java.lang.String> getSPAM_SUSPECTED_LABELS() {
            return null;
        }
        
        /**
         * 사기 의심 표현
         */
        @org.jetbrains.annotations.NotNull()
        public final java.util.Map<java.lang.String, java.lang.String> getSCAM_RISK_LABELS() {
            return null;
        }
        
        /**
         * 기관/배송/서비스 추정 표현
         */
        @org.jetbrains.annotations.NotNull()
        public final java.util.Map<java.lang.String, java.lang.String> getLIKELY_SAFE_LABELS() {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u001b\b\u0086\b\u0018\u00002\u00020\u0001BC\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\u0006\u0012\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t\u0012\u0006\u0010\u000b\u001a\u00020\u0006\u0012\u0006\u0010\f\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\rJ\t\u0010\u0018\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0019\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\u0006H\u00c6\u0003J\t\u0010\u001b\u001a\u00020\u0006H\u00c6\u0003J\u000f\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\n0\tH\u00c6\u0003J\t\u0010\u001d\u001a\u00020\u0006H\u00c6\u0003J\t\u0010\u001e\u001a\u00020\u0003H\u00c6\u0003JU\u0010\u001f\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\u00062\u000e\b\u0002\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t2\b\b\u0002\u0010\u000b\u001a\u00020\u00062\b\b\u0002\u0010\f\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010 \u001a\u00020\u00032\b\u0010!\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\"\u001a\u00020\u0006H\u00d6\u0001J\u0006\u0010#\u001a\u00020\nJ\t\u0010$\u001a\u00020\nH\u00d6\u0001R\u0011\u0010\u000b\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0011R\u0011\u0010\f\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0011R\u0011\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u000fR\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u000f\u00a8\u0006%"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/LaunchReadinessLock$LaunchReadiness;", "", "locksPassed", "", "compliancePassed", "totalCountries", "", "tier1Countries", "lockIssues", "", "", "complianceFailCount", "ready", "(ZZIILjava/util/List;IZ)V", "getComplianceFailCount", "()I", "getCompliancePassed", "()Z", "getLockIssues", "()Ljava/util/List;", "getLocksPassed", "getReady", "getTier1Countries", "getTotalCountries", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "copy", "equals", "other", "hashCode", "toJarvisFormat", "toString", "call-intercept_debug"})
    public static final class LaunchReadiness {
        private final boolean locksPassed = false;
        private final boolean compliancePassed = false;
        private final int totalCountries = 0;
        private final int tier1Countries = 0;
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<java.lang.String> lockIssues = null;
        private final int complianceFailCount = 0;
        private final boolean ready = false;
        
        public LaunchReadiness(boolean locksPassed, boolean compliancePassed, int totalCountries, int tier1Countries, @org.jetbrains.annotations.NotNull()
        java.util.List<java.lang.String> lockIssues, int complianceFailCount, boolean ready) {
            super();
        }
        
        public final boolean getLocksPassed() {
            return false;
        }
        
        public final boolean getCompliancePassed() {
            return false;
        }
        
        public final int getTotalCountries() {
            return 0;
        }
        
        public final int getTier1Countries() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<java.lang.String> getLockIssues() {
            return null;
        }
        
        public final int getComplianceFailCount() {
            return 0;
        }
        
        public final boolean getReady() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String toJarvisFormat() {
            return null;
        }
        
        public final boolean component1() {
            return false;
        }
        
        public final boolean component2() {
            return false;
        }
        
        public final int component3() {
            return 0;
        }
        
        public final int component4() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<java.lang.String> component5() {
            return null;
        }
        
        public final int component6() {
            return 0;
        }
        
        public final boolean component7() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.LaunchReadinessLock.LaunchReadiness copy(boolean locksPassed, boolean compliancePassed, int totalCountries, int tier1Countries, @org.jetbrains.annotations.NotNull()
        java.util.List<java.lang.String> lockIssues, int complianceFailCount, boolean ready) {
            return null;
        }
        
        @java.lang.Override()
        public boolean equals(@org.jetbrains.annotations.Nullable()
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override()
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public java.lang.String toString() {
            return null;
        }
    }
    
    /**
     * 잠금 상태 검증 결과.
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u0013\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B3\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b\u00a2\u0006\u0002\u0010\nJ\t\u0010\u0014\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0015\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0016\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0017\u001a\u00020\u0003H\u00c6\u0003J\u000f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\t0\bH\u00c6\u0003JA\u0010\u0019\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bH\u00c6\u0001J\u0013\u0010\u001a\u001a\u00020\u00032\b\u0010\u001b\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001c\u001a\u00020\u001dH\u00d6\u0001J\t\u0010\u001e\u001a\u00020\tH\u00d6\u0001R\u0011\u0010\u000b\u001a\u00020\u00038F\u00a2\u0006\u0006\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\rR\u0017\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\rR\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\rR\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\r\u00a8\u0006\u001f"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/LaunchReadinessLock$LockVerification;", "", "registryLocked", "", "hardRulesLocked", "slaLocked", "safeExpressionsLocked", "issues", "", "", "(ZZZZLjava/util/List;)V", "allLocked", "getAllLocked", "()Z", "getHardRulesLocked", "getIssues", "()Ljava/util/List;", "getRegistryLocked", "getSafeExpressionsLocked", "getSlaLocked", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "other", "hashCode", "", "toString", "call-intercept_debug"})
    public static final class LockVerification {
        private final boolean registryLocked = false;
        private final boolean hardRulesLocked = false;
        private final boolean slaLocked = false;
        private final boolean safeExpressionsLocked = false;
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<java.lang.String> issues = null;
        
        public LockVerification(boolean registryLocked, boolean hardRulesLocked, boolean slaLocked, boolean safeExpressionsLocked, @org.jetbrains.annotations.NotNull()
        java.util.List<java.lang.String> issues) {
            super();
        }
        
        public final boolean getRegistryLocked() {
            return false;
        }
        
        public final boolean getHardRulesLocked() {
            return false;
        }
        
        public final boolean getSlaLocked() {
            return false;
        }
        
        public final boolean getSafeExpressionsLocked() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<java.lang.String> getIssues() {
            return null;
        }
        
        public final boolean getAllLocked() {
            return false;
        }
        
        public final boolean component1() {
            return false;
        }
        
        public final boolean component2() {
            return false;
        }
        
        public final boolean component3() {
            return false;
        }
        
        public final boolean component4() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<java.lang.String> component5() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.LaunchReadinessLock.LockVerification copy(boolean registryLocked, boolean hardRulesLocked, boolean slaLocked, boolean safeExpressionsLocked, @org.jetbrains.annotations.NotNull()
        java.util.List<java.lang.String> issues) {
            return null;
        }
        
        @java.lang.Override()
        public boolean equals(@org.jetbrains.annotations.Nullable()
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override()
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public java.lang.String toString() {
            return null;
        }
    }
}