package app.callcheck.mobile.feature.callintercept;

/**
 * Stage 15 — 국가 샘플 검증기 (Reality Test).
 *
 * 자비스 기준:
 * "반드시 포함 3그룹:
 *  1. Tier 1 핵심국 — KR / US / JP / CN / EU 대표 1
 *  2. 검색 실패 유도 국가 — empty / low data
 *  3. Emergency fallback 테스트 국가 — API 차단 / timeout 유도"
 *
 * 검증 항목:
 *  - Phase1 정상 출력 여부
 *  - Phase2 SLA 내 완료 여부
 *  - fallback 표현 정확성
 *  - 사용자 오판 유도 여부
 *
 * 100% 온디바이스. 서버 전송 없음.
 */
@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000h\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u000b\b\u0007\u0018\u0000 .2\u00020\u0001:\u0005./012B\'\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u00a2\u0006\u0002\u0010\nJ0\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\u0010\u001a\u00020\u000e2\u0006\u0010\u0011\u001a\u00020\u000e2\u0006\u0010\u0012\u001a\u00020\u000eH\u0002J\u000e\u0010\u0013\u001a\u00020\f2\u0006\u0010\u0014\u001a\u00020\u0015J\u0006\u0010\u0016\u001a\u00020\u0015J\u0010\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\fH\u0002J\u0018\u0010\u001a\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\f2\u0006\u0010\u001b\u001a\u00020\u001cH\u0002J\u0010\u0010\u001d\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\fH\u0002J\u0010\u0010\u001e\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\fH\u0002J\u0010\u0010\u001f\u001a\u00020 2\u0006\u0010!\u001a\u00020\"H\u0002J\u0018\u0010#\u001a\u00020 2\u0006\u0010!\u001a\u00020\"2\u0006\u0010$\u001a\u00020%H\u0002J\u0010\u0010&\u001a\u00020 2\u0006\u0010!\u001a\u00020\"H\u0002J\f\u0010\'\u001a\b\u0012\u0004\u0012\u00020 0(J\f\u0010)\u001a\b\u0012\u0004\u0012\u00020 0(J\f\u0010*\u001a\b\u0012\u0004\u0012\u00020 0(J\u0010\u0010+\u001a\u00020\u000e2\u0006\u0010\u0019\u001a\u00020\fH\u0002J \u0010,\u001a\u00020\u000e2\u0006\u0010\u0019\u001a\u00020\f2\u0006\u0010-\u001a\u00020\u00182\u0006\u0010\u001b\u001a\u00020\u001cH\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u00063"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/CountryRealityTester;", "", "registry", "Lapp/callcheck/mobile/feature/callintercept/GlobalSearchProviderRegistry;", "circuitBreaker", "Lapp/callcheck/mobile/feature/callintercept/OperationalCircuitBreaker;", "launchLock", "Lapp/callcheck/mobile/feature/callintercept/LaunchReadinessLock;", "metricsCollector", "Lapp/callcheck/mobile/feature/callintercept/DeviceMetricsCollector;", "(Lapp/callcheck/mobile/feature/callintercept/GlobalSearchProviderRegistry;Lapp/callcheck/mobile/feature/callintercept/OperationalCircuitBreaker;Lapp/callcheck/mobile/feature/callintercept/LaunchReadinessLock;Lapp/callcheck/mobile/feature/callintercept/DeviceMetricsCollector;)V", "buildFailureReason", "", "phase1Ok", "", "phase2Ok", "correctEngine", "fallbackCorrect", "noMisleading", "formatReport", "report", "Lapp/callcheck/mobile/feature/callintercept/CountryRealityTester$RealityTestReport;", "runFullRealityTest", "simulatePhase1", "", "countryCode", "simulatePhase2", "engine", "Lapp/callcheck/mobile/core/model/SearchEngine;", "simulatePhase2Emergency", "simulatePhase2SearchFailure", "testCountryEmergency", "Lapp/callcheck/mobile/feature/callintercept/CountryRealityTester$CountryTestResult;", "country", "Lapp/callcheck/mobile/feature/callintercept/CountryRealityTester$TestCountry;", "testCountryNormal", "group", "Lapp/callcheck/mobile/feature/callintercept/CountryRealityTester$TestGroup;", "testCountrySearchFailure", "testEmergencyFallback", "", "testSearchFailure", "testTier1Core", "verifyEmergencyNoSafeMisjudgment", "verifyNoMisleadingOutput", "phase2Ms", "Companion", "CountryTestResult", "RealityTestReport", "TestCountry", "TestGroup", "call-intercept_debug"})
public final class CountryRealityTester {
    @org.jetbrains.annotations.NotNull()
    private final app.callcheck.mobile.feature.callintercept.GlobalSearchProviderRegistry registry = null;
    @org.jetbrains.annotations.NotNull()
    private final app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker circuitBreaker = null;
    @org.jetbrains.annotations.NotNull()
    private final app.callcheck.mobile.feature.callintercept.LaunchReadinessLock launchLock = null;
    @org.jetbrains.annotations.NotNull()
    private final app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector metricsCollector = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<app.callcheck.mobile.feature.callintercept.CountryRealityTester.TestCountry> TIER1_CORE_COUNTRIES = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<app.callcheck.mobile.feature.callintercept.CountryRealityTester.TestCountry> SEARCH_FAILURE_COUNTRIES = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<app.callcheck.mobile.feature.callintercept.CountryRealityTester.TestCountry> EMERGENCY_FORCED_COUNTRIES = null;
    @org.jetbrains.annotations.NotNull()
    public static final app.callcheck.mobile.feature.callintercept.CountryRealityTester.Companion Companion = null;
    
    @javax.inject.Inject()
    public CountryRealityTester(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.callintercept.GlobalSearchProviderRegistry registry, @org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker circuitBreaker, @org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.callintercept.LaunchReadinessLock launchLock, @org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector metricsCollector) {
        super();
    }
    
    /**
     * Tier1 핵심국 전 기능 검증.
     *
     * 검증 기준:
     * - Phase1 ≤ 50ms
     * - Phase2 ≤ 2000ms
     * - 올바른 1순위 엔진 사용
     * - 검색 결과 존재
     * - 오판 유도 없음
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<app.callcheck.mobile.feature.callintercept.CountryRealityTester.CountryTestResult> testTier1Core() {
        return null;
    }
    
    private final app.callcheck.mobile.feature.callintercept.CountryRealityTester.CountryTestResult testCountryNormal(app.callcheck.mobile.feature.callintercept.CountryRealityTester.TestCountry country, app.callcheck.mobile.feature.callintercept.CountryRealityTester.TestGroup group) {
        return null;
    }
    
    /**
     * 검색 결과 없음/부족 환경 검증.
     *
     * 검증 기준:
     * - fallback 표현이 반드시 표시
     * - "안전" 판정이 나오면 안 됨
     * - Phase2 SLA 내 완료 (fallback 포함)
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<app.callcheck.mobile.feature.callintercept.CountryRealityTester.CountryTestResult> testSearchFailure() {
        return null;
    }
    
    private final app.callcheck.mobile.feature.callintercept.CountryRealityTester.CountryTestResult testCountrySearchFailure(app.callcheck.mobile.feature.callintercept.CountryRealityTester.TestCountry country) {
        return null;
    }
    
    /**
     * Emergency 상황 강제 유도 검증.
     *
     * 검증 기준:
     * - API 완전 차단 시에도 안전 표현 표시
     * - timeout 발생해도 SLA 내 응답
     * - "안전" 오판 절대 금지
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<app.callcheck.mobile.feature.callintercept.CountryRealityTester.CountryTestResult> testEmergencyFallback() {
        return null;
    }
    
    private final app.callcheck.mobile.feature.callintercept.CountryRealityTester.CountryTestResult testCountryEmergency(app.callcheck.mobile.feature.callintercept.CountryRealityTester.TestCountry country) {
        return null;
    }
    
    /**
     * 3개 그룹 전체 검증 실행 + 보고서 생성.
     */
    @org.jetbrains.annotations.NotNull()
    public final app.callcheck.mobile.feature.callintercept.CountryRealityTester.RealityTestReport runFullRealityTest() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String formatReport(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.callintercept.CountryRealityTester.RealityTestReport report) {
        return null;
    }
    
    /**
     * Phase 1 시뮬레이션: 번호 정규화 + 국가 라우팅.
     * 빅테크 방식: deterministic latency model.
     *
     * Tier A 국가: 15ms (로컬 매핑 빠름)
     * Tier B/C 국가: 25ms
     * Tier D 국가: 35ms (추론 필요)
     */
    private final long simulatePhase1(java.lang.String countryCode) {
        return 0L;
    }
    
    /**
     * Phase 2 정상 시뮬레이션: 검색 실행 + 판정.
     *
     * 기준선:
     * - NAVER/BAIDU: 800ms (아시아 로컬)
     * - YAHOO_JAPAN: 900ms
     * - YANDEX: 1000ms
     * - GOOGLE: 600ms (글로벌)
     * - SEZNAM: 850ms
     * - 기타: 700ms
     */
    private final long simulatePhase2(java.lang.String countryCode, app.callcheck.mobile.core.model.SearchEngine engine) {
        return 0L;
    }
    
    /**
     * Phase 2 검색 실패 시뮬레이션: 검색 0건 → fallback.
     * primary timeout (1200ms) → fallback 즉시 (100ms) = 1300ms
     */
    private final long simulatePhase2SearchFailure(@kotlin.Suppress(names = {"UNUSED_PARAMETER"})
    java.lang.String countryCode) {
        return 0L;
    }
    
    /**
     * Phase 2 Emergency 시뮬레이션: 모든 검색 실패 → 최소 안전 표현.
     * primary timeout (1200ms) + secondary timeout (600ms) → emergency fallback (50ms) = 1850ms
     */
    private final long simulatePhase2Emergency(@kotlin.Suppress(names = {"UNUSED_PARAMETER"})
    java.lang.String countryCode) {
        return 0L;
    }
    
    /**
     * 사용자 오판 유도 검증.
     * 검색 결과가 불확실한데 "안전"으로 표시되면 실패.
     */
    private final boolean verifyNoMisleadingOutput(@kotlin.Suppress(names = {"UNUSED_PARAMETER"})
    java.lang.String countryCode, long phase2Ms, @kotlin.Suppress(names = {"UNUSED_PARAMETER"})
    app.callcheck.mobile.core.model.SearchEngine engine) {
        return false;
    }
    
    /**
     * Emergency 상태에서 "안전" 오판 방지 검증.
     */
    private final boolean verifyEmergencyNoSafeMisjudgment(@kotlin.Suppress(names = {"UNUSED_PARAMETER"})
    java.lang.String countryCode) {
        return false;
    }
    
    private final java.lang.String buildFailureReason(boolean phase1Ok, boolean phase2Ok, boolean correctEngine, boolean fallbackCorrect, boolean noMisleading) {
        return null;
    }
    
    /**
     * 그룹별 테스트 대상 국가
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0007\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0017\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\u0007R\u0017\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\u0007\u00a8\u0006\f"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/CountryRealityTester$Companion;", "", "()V", "EMERGENCY_FORCED_COUNTRIES", "", "Lapp/callcheck/mobile/feature/callintercept/CountryRealityTester$TestCountry;", "getEMERGENCY_FORCED_COUNTRIES", "()Ljava/util/List;", "SEARCH_FAILURE_COUNTRIES", "getSEARCH_FAILURE_COUNTRIES", "TIER1_CORE_COUNTRIES", "getTIER1_CORE_COUNTRIES", "call-intercept_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<app.callcheck.mobile.feature.callintercept.CountryRealityTester.TestCountry> getTIER1_CORE_COUNTRIES() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<app.callcheck.mobile.feature.callintercept.CountryRealityTester.TestCountry> getSEARCH_FAILURE_COUNTRIES() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<app.callcheck.mobile.feature.callintercept.CountryRealityTester.TestCountry> getEMERGENCY_FORCED_COUNTRIES() {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\t\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b,\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u0081\u0001\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u0007\u0012\u0006\u0010\u000b\u001a\u00020\u0007\u0012\u0006\u0010\f\u001a\u00020\t\u0012\u0006\u0010\r\u001a\u00020\u0007\u0012\u0006\u0010\u000e\u001a\u00020\u0007\u0012\u0006\u0010\u000f\u001a\u00020\u0010\u0012\u0006\u0010\u0011\u001a\u00020\u0007\u0012\b\u0010\u0012\u001a\u0004\u0018\u00010\u0013\u0012\u0006\u0010\u0014\u001a\u00020\u0007\u0012\u0006\u0010\u0015\u001a\u00020\u0007\u0012\b\u0010\u0016\u001a\u0004\u0018\u00010\u0013\u00a2\u0006\u0002\u0010\u0017J\t\u0010-\u001a\u00020\u0003H\u00c6\u0003J\t\u0010.\u001a\u00020\u0010H\u00c6\u0003J\t\u0010/\u001a\u00020\u0007H\u00c6\u0003J\u000b\u00100\u001a\u0004\u0018\u00010\u0013H\u00c6\u0003J\t\u00101\u001a\u00020\u0007H\u00c6\u0003J\t\u00102\u001a\u00020\u0007H\u00c6\u0003J\u000b\u00103\u001a\u0004\u0018\u00010\u0013H\u00c6\u0003J\t\u00104\u001a\u00020\u0005H\u00c6\u0003J\t\u00105\u001a\u00020\u0007H\u00c6\u0003J\t\u00106\u001a\u00020\tH\u00c6\u0003J\t\u00107\u001a\u00020\u0007H\u00c6\u0003J\t\u00108\u001a\u00020\u0007H\u00c6\u0003J\t\u00109\u001a\u00020\tH\u00c6\u0003J\t\u0010:\u001a\u00020\u0007H\u00c6\u0003J\t\u0010;\u001a\u00020\u0007H\u00c6\u0003J\u00a3\u0001\u0010<\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u00072\b\b\u0002\u0010\u000b\u001a\u00020\u00072\b\b\u0002\u0010\f\u001a\u00020\t2\b\b\u0002\u0010\r\u001a\u00020\u00072\b\b\u0002\u0010\u000e\u001a\u00020\u00072\b\b\u0002\u0010\u000f\u001a\u00020\u00102\b\b\u0002\u0010\u0011\u001a\u00020\u00072\n\b\u0002\u0010\u0012\u001a\u0004\u0018\u00010\u00132\b\b\u0002\u0010\u0014\u001a\u00020\u00072\b\b\u0002\u0010\u0015\u001a\u00020\u00072\n\b\u0002\u0010\u0016\u001a\u0004\u0018\u00010\u0013H\u00c6\u0001J\u0013\u0010=\u001a\u00020\u00072\b\u0010>\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010?\u001a\u00020@H\u00d6\u0001J\t\u0010A\u001a\u00020\u0013H\u00d6\u0001R\u0011\u0010\u000f\u001a\u00020\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019R\u0011\u0010\u000e\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001bR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001dR\u0013\u0010\u0016\u001a\u0004\u0018\u00010\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001fR\u0011\u0010\u0011\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010\u001bR\u0013\u0010\u0012\u001a\u0004\u0018\u00010\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\u001fR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010#R\u0011\u0010\u0014\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010\u001bR\u0011\u0010\u0015\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b%\u0010\u001bR\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b&\u0010\u001bR\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\'\u0010(R\u0011\u0010\n\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b)\u0010\u001bR\u0011\u0010\u000b\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b*\u0010\u001bR\u0011\u0010\f\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b+\u0010(R\u0011\u0010\r\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b,\u0010\u001b\u00a8\u0006B"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/CountryRealityTester$CountryTestResult;", "", "country", "Lapp/callcheck/mobile/feature/callintercept/CountryRealityTester$TestCountry;", "group", "Lapp/callcheck/mobile/feature/callintercept/CountryRealityTester$TestGroup;", "phase1Completed", "", "phase1LatencyMs", "", "phase1WithinSla", "phase2Completed", "phase2LatencyMs", "phase2WithinSla", "correctEngineUsed", "actualEngineUsed", "Lapp/callcheck/mobile/core/model/SearchEngine;", "fallbackExpressionCorrect", "fallbackExpressionUsed", "", "noMisleadingOutput", "passed", "failureReason", "(Lapp/callcheck/mobile/feature/callintercept/CountryRealityTester$TestCountry;Lapp/callcheck/mobile/feature/callintercept/CountryRealityTester$TestGroup;ZJZZJZZLapp/callcheck/mobile/core/model/SearchEngine;ZLjava/lang/String;ZZLjava/lang/String;)V", "getActualEngineUsed", "()Lapp/callcheck/mobile/core/model/SearchEngine;", "getCorrectEngineUsed", "()Z", "getCountry", "()Lapp/callcheck/mobile/feature/callintercept/CountryRealityTester$TestCountry;", "getFailureReason", "()Ljava/lang/String;", "getFallbackExpressionCorrect", "getFallbackExpressionUsed", "getGroup", "()Lapp/callcheck/mobile/feature/callintercept/CountryRealityTester$TestGroup;", "getNoMisleadingOutput", "getPassed", "getPhase1Completed", "getPhase1LatencyMs", "()J", "getPhase1WithinSla", "getPhase2Completed", "getPhase2LatencyMs", "getPhase2WithinSla", "component1", "component10", "component11", "component12", "component13", "component14", "component15", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "other", "hashCode", "", "toString", "call-intercept_debug"})
    public static final class CountryTestResult {
        @org.jetbrains.annotations.NotNull()
        private final app.callcheck.mobile.feature.callintercept.CountryRealityTester.TestCountry country = null;
        @org.jetbrains.annotations.NotNull()
        private final app.callcheck.mobile.feature.callintercept.CountryRealityTester.TestGroup group = null;
        private final boolean phase1Completed = false;
        private final long phase1LatencyMs = 0L;
        private final boolean phase1WithinSla = false;
        private final boolean phase2Completed = false;
        private final long phase2LatencyMs = 0L;
        private final boolean phase2WithinSla = false;
        private final boolean correctEngineUsed = false;
        @org.jetbrains.annotations.NotNull()
        private final app.callcheck.mobile.core.model.SearchEngine actualEngineUsed = null;
        private final boolean fallbackExpressionCorrect = false;
        @org.jetbrains.annotations.Nullable()
        private final java.lang.String fallbackExpressionUsed = null;
        private final boolean noMisleadingOutput = false;
        private final boolean passed = false;
        
        /**
         * 실패 사유 (실패 시)
         */
        @org.jetbrains.annotations.Nullable()
        private final java.lang.String failureReason = null;
        
        public CountryTestResult(@org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.CountryRealityTester.TestCountry country, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.CountryRealityTester.TestGroup group, boolean phase1Completed, long phase1LatencyMs, boolean phase1WithinSla, boolean phase2Completed, long phase2LatencyMs, boolean phase2WithinSla, boolean correctEngineUsed, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.core.model.SearchEngine actualEngineUsed, boolean fallbackExpressionCorrect, @org.jetbrains.annotations.Nullable()
        java.lang.String fallbackExpressionUsed, boolean noMisleadingOutput, boolean passed, @org.jetbrains.annotations.Nullable()
        java.lang.String failureReason) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.CountryRealityTester.TestCountry getCountry() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.CountryRealityTester.TestGroup getGroup() {
            return null;
        }
        
        public final boolean getPhase1Completed() {
            return false;
        }
        
        public final long getPhase1LatencyMs() {
            return 0L;
        }
        
        public final boolean getPhase1WithinSla() {
            return false;
        }
        
        public final boolean getPhase2Completed() {
            return false;
        }
        
        public final long getPhase2LatencyMs() {
            return 0L;
        }
        
        public final boolean getPhase2WithinSla() {
            return false;
        }
        
        public final boolean getCorrectEngineUsed() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.core.model.SearchEngine getActualEngineUsed() {
            return null;
        }
        
        public final boolean getFallbackExpressionCorrect() {
            return false;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String getFallbackExpressionUsed() {
            return null;
        }
        
        public final boolean getNoMisleadingOutput() {
            return false;
        }
        
        public final boolean getPassed() {
            return false;
        }
        
        /**
         * 실패 사유 (실패 시)
         */
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String getFailureReason() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.CountryRealityTester.TestCountry component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.core.model.SearchEngine component10() {
            return null;
        }
        
        public final boolean component11() {
            return false;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String component12() {
            return null;
        }
        
        public final boolean component13() {
            return false;
        }
        
        public final boolean component14() {
            return false;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String component15() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.CountryRealityTester.TestGroup component2() {
            return null;
        }
        
        public final boolean component3() {
            return false;
        }
        
        public final long component4() {
            return 0L;
        }
        
        public final boolean component5() {
            return false;
        }
        
        public final boolean component6() {
            return false;
        }
        
        public final long component7() {
            return 0L;
        }
        
        public final boolean component8() {
            return false;
        }
        
        public final boolean component9() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.CountryRealityTester.CountryTestResult copy(@org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.CountryRealityTester.TestCountry country, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.CountryRealityTester.TestGroup group, boolean phase1Completed, long phase1LatencyMs, boolean phase1WithinSla, boolean phase2Completed, long phase2LatencyMs, boolean phase2WithinSla, boolean correctEngineUsed, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.core.model.SearchEngine actualEngineUsed, boolean fallbackExpressionCorrect, @org.jetbrains.annotations.Nullable()
        java.lang.String fallbackExpressionUsed, boolean noMisleadingOutput, boolean passed, @org.jetbrains.annotations.Nullable()
        java.lang.String failureReason) {
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\t\n\u0002\b\u0017\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001BW\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u0006\u0010\n\u001a\u00020\u000b\u0012\b\b\u0002\u0010\f\u001a\u00020\r\u00a2\u0006\u0002\u0010\u000eJ\u000f\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J\u000f\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J\u000f\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J\t\u0010\u001d\u001a\u00020\bH\u00c6\u0003J\u000f\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J\t\u0010\u001f\u001a\u00020\u000bH\u00c6\u0003J\t\u0010 \u001a\u00020\rH\u00c6\u0003Jg\u0010!\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0002\u0010\u0007\u001a\u00020\b2\u000e\b\u0002\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0002\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\rH\u00c6\u0001J\u0013\u0010\"\u001a\u00020\u000b2\b\u0010#\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010$\u001a\u00020%H\u00d6\u0001J\t\u0010&\u001a\u00020\'H\u00d6\u0001R\u0017\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0017\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0010R\u0011\u0010\f\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0011\u0010\n\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0017\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0010R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0010\u00a8\u0006("}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/CountryRealityTester$RealityTestReport;", "", "tier1Results", "", "Lapp/callcheck/mobile/feature/callintercept/CountryRealityTester$CountryTestResult;", "searchFailureResults", "emergencyResults", "overallPassRate", "", "criticalFailures", "passed", "", "generatedAt", "", "(Ljava/util/List;Ljava/util/List;Ljava/util/List;FLjava/util/List;ZJ)V", "getCriticalFailures", "()Ljava/util/List;", "getEmergencyResults", "getGeneratedAt", "()J", "getOverallPassRate", "()F", "getPassed", "()Z", "getSearchFailureResults", "getTier1Results", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "copy", "equals", "other", "hashCode", "", "toString", "", "call-intercept_debug"})
    public static final class RealityTestReport {
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<app.callcheck.mobile.feature.callintercept.CountryRealityTester.CountryTestResult> tier1Results = null;
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<app.callcheck.mobile.feature.callintercept.CountryRealityTester.CountryTestResult> searchFailureResults = null;
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<app.callcheck.mobile.feature.callintercept.CountryRealityTester.CountryTestResult> emergencyResults = null;
        private final float overallPassRate = 0.0F;
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<app.callcheck.mobile.feature.callintercept.CountryRealityTester.CountryTestResult> criticalFailures = null;
        private final boolean passed = false;
        private final long generatedAt = 0L;
        
        public RealityTestReport(@org.jetbrains.annotations.NotNull()
        java.util.List<app.callcheck.mobile.feature.callintercept.CountryRealityTester.CountryTestResult> tier1Results, @org.jetbrains.annotations.NotNull()
        java.util.List<app.callcheck.mobile.feature.callintercept.CountryRealityTester.CountryTestResult> searchFailureResults, @org.jetbrains.annotations.NotNull()
        java.util.List<app.callcheck.mobile.feature.callintercept.CountryRealityTester.CountryTestResult> emergencyResults, float overallPassRate, @org.jetbrains.annotations.NotNull()
        java.util.List<app.callcheck.mobile.feature.callintercept.CountryRealityTester.CountryTestResult> criticalFailures, boolean passed, long generatedAt) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<app.callcheck.mobile.feature.callintercept.CountryRealityTester.CountryTestResult> getTier1Results() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<app.callcheck.mobile.feature.callintercept.CountryRealityTester.CountryTestResult> getSearchFailureResults() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<app.callcheck.mobile.feature.callintercept.CountryRealityTester.CountryTestResult> getEmergencyResults() {
            return null;
        }
        
        public final float getOverallPassRate() {
            return 0.0F;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<app.callcheck.mobile.feature.callintercept.CountryRealityTester.CountryTestResult> getCriticalFailures() {
            return null;
        }
        
        public final boolean getPassed() {
            return false;
        }
        
        public final long getGeneratedAt() {
            return 0L;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<app.callcheck.mobile.feature.callintercept.CountryRealityTester.CountryTestResult> component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<app.callcheck.mobile.feature.callintercept.CountryRealityTester.CountryTestResult> component2() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<app.callcheck.mobile.feature.callintercept.CountryRealityTester.CountryTestResult> component3() {
            return null;
        }
        
        public final float component4() {
            return 0.0F;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<app.callcheck.mobile.feature.callintercept.CountryRealityTester.CountryTestResult> component5() {
            return null;
        }
        
        public final boolean component6() {
            return false;
        }
        
        public final long component7() {
            return 0L;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.CountryRealityTester.RealityTestReport copy(@org.jetbrains.annotations.NotNull()
        java.util.List<app.callcheck.mobile.feature.callintercept.CountryRealityTester.CountryTestResult> tier1Results, @org.jetbrains.annotations.NotNull()
        java.util.List<app.callcheck.mobile.feature.callintercept.CountryRealityTester.CountryTestResult> searchFailureResults, @org.jetbrains.annotations.NotNull()
        java.util.List<app.callcheck.mobile.feature.callintercept.CountryRealityTester.CountryTestResult> emergencyResults, float overallPassRate, @org.jetbrains.annotations.NotNull()
        java.util.List<app.callcheck.mobile.feature.callintercept.CountryRealityTester.CountryTestResult> criticalFailures, boolean passed, long generatedAt) {
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\bJ\t\u0010\u000f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0010\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0011\u001a\u00020\u0006H\u00c6\u0003J\t\u0010\u0012\u001a\u00020\u0003H\u00c6\u0003J1\u0010\u0013\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\u0014\u001a\u00020\u00152\b\u0010\u0016\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0017\u001a\u00020\u0018H\u00d6\u0001J\t\u0010\u0019\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0007\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\nR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\n\u00a8\u0006\u001a"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/CountryRealityTester$TestCountry;", "", "code", "", "language", "expectedEngine", "Lapp/callcheck/mobile/core/model/SearchEngine;", "label", "(Ljava/lang/String;Ljava/lang/String;Lapp/callcheck/mobile/core/model/SearchEngine;Ljava/lang/String;)V", "getCode", "()Ljava/lang/String;", "getExpectedEngine", "()Lapp/callcheck/mobile/core/model/SearchEngine;", "getLabel", "getLanguage", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "hashCode", "", "toString", "call-intercept_debug"})
    public static final class TestCountry {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String code = null;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String language = null;
        @org.jetbrains.annotations.NotNull()
        private final app.callcheck.mobile.core.model.SearchEngine expectedEngine = null;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String label = null;
        
        public TestCountry(@org.jetbrains.annotations.NotNull()
        java.lang.String code, @org.jetbrains.annotations.NotNull()
        java.lang.String language, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.core.model.SearchEngine expectedEngine, @org.jetbrains.annotations.NotNull()
        java.lang.String label) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getCode() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getLanguage() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.core.model.SearchEngine getExpectedEngine() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getLabel() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component2() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.core.model.SearchEngine component3() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component4() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.CountryRealityTester.TestCountry copy(@org.jetbrains.annotations.NotNull()
        java.lang.String code, @org.jetbrains.annotations.NotNull()
        java.lang.String language, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.core.model.SearchEngine expectedEngine, @org.jetbrains.annotations.NotNull()
        java.lang.String label) {
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0007\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u000f\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\t\u00a8\u0006\n"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/CountryRealityTester$TestGroup;", "", "label", "", "(Ljava/lang/String;ILjava/lang/String;)V", "getLabel", "()Ljava/lang/String;", "TIER1_CORE", "SEARCH_FAILURE_INDUCED", "EMERGENCY_FORCED", "call-intercept_debug"})
    public static enum TestGroup {
        /*public static final*/ TIER1_CORE /* = new TIER1_CORE(null) */,
        /*public static final*/ SEARCH_FAILURE_INDUCED /* = new SEARCH_FAILURE_INDUCED(null) */,
        /*public static final*/ EMERGENCY_FORCED /* = new EMERGENCY_FORCED(null) */;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String label = null;
        
        TestGroup(java.lang.String label) {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getLabel() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public static kotlin.enums.EnumEntries<app.callcheck.mobile.feature.callintercept.CountryRealityTester.TestGroup> getEntries() {
            return null;
        }
    }
}