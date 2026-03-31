package app.callcheck.mobile.feature.callintercept;

/**
 * Stage 15 — 운영 리허설 (Launch Simulation).
 *
 * 자비스 기준:
 * "시나리오 3종:
 *  1. 정상 국가 (Full Engine)
 *  2. Search 실패 국가
 *  3. Emergency 강제 국가
 *
 * 검증 항목:
 * - Dashboard 반영 실시간 여부
 * - SLA 유지 여부
 * - 사용자 체감 지연 없음
 * - Recovery 정상 작동"
 *
 * 3종 시나리오를 순차 실행하여 전체 파이프라인이
 * 출시 조건을 만족하는지 종합 검증.
 *
 * 100% 온디바이스. 서버 전송 없음.
 */
@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000b\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\b\b\u0007\u0018\u00002\u00020\u0001:\u0002$%B?\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u000b\u0012\u0006\u0010\f\u001a\u00020\r\u0012\u0006\u0010\u000e\u001a\u00020\u000f\u00a2\u0006\u0002\u0010\u0010J\u000e\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0014J\u001c\u0010\u0015\u001a\u00020\u00162\n\u0010\u0017\u001a\u00060\u0018j\u0002`\u00192\u0006\u0010\u001a\u001a\u00020\u001bH\u0002J\u0010\u0010\u001c\u001a\u00020\u00122\u0006\u0010\u001d\u001a\u00020\u001eH\u0002J\u0006\u0010\u001f\u001a\u00020\u0014J\b\u0010 \u001a\u00020\u001bH\u0002J\b\u0010!\u001a\u00020\u001bH\u0002J\b\u0010\"\u001a\u00020\u001bH\u0002J\b\u0010#\u001a\u00020\u001eH\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006&"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/LaunchSimulationRunner;", "", "registry", "Lapp/callcheck/mobile/feature/callintercept/GlobalSearchProviderRegistry;", "circuitBreaker", "Lapp/callcheck/mobile/feature/callintercept/OperationalCircuitBreaker;", "launchLock", "Lapp/callcheck/mobile/feature/callintercept/LaunchReadinessLock;", "dashboard", "Lapp/callcheck/mobile/feature/callintercept/GlobalLaunchDashboard;", "metricsCollector", "Lapp/callcheck/mobile/feature/callintercept/DeviceMetricsCollector;", "realityTester", "Lapp/callcheck/mobile/feature/callintercept/CountryRealityTester;", "rehearsalRunner", "Lapp/callcheck/mobile/feature/callintercept/CircuitBreakerRehearsalRunner;", "(Lapp/callcheck/mobile/feature/callintercept/GlobalSearchProviderRegistry;Lapp/callcheck/mobile/feature/callintercept/OperationalCircuitBreaker;Lapp/callcheck/mobile/feature/callintercept/LaunchReadinessLock;Lapp/callcheck/mobile/feature/callintercept/GlobalLaunchDashboard;Lapp/callcheck/mobile/feature/callintercept/DeviceMetricsCollector;Lapp/callcheck/mobile/feature/callintercept/CountryRealityTester;Lapp/callcheck/mobile/feature/callintercept/CircuitBreakerRehearsalRunner;)V", "formatReport", "", "report", "Lapp/callcheck/mobile/feature/callintercept/LaunchSimulationRunner$LaunchSimulationReport;", "formatScenario", "", "sb", "Ljava/lang/StringBuilder;", "Lkotlin/text/StringBuilder;", "s", "Lapp/callcheck/mobile/feature/callintercept/LaunchSimulationRunner$ScenarioResult;", "mark", "pass", "", "runFullSimulation", "runScenario1_Normal", "runScenario2_SearchFailure", "runScenario3_Emergency", "verifyDashboardRealtime", "LaunchSimulationReport", "ScenarioResult", "call-intercept_debug"})
public final class LaunchSimulationRunner {
    @org.jetbrains.annotations.NotNull()
    private final app.callcheck.mobile.feature.callintercept.GlobalSearchProviderRegistry registry = null;
    @org.jetbrains.annotations.NotNull()
    private final app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker circuitBreaker = null;
    @org.jetbrains.annotations.NotNull()
    private final app.callcheck.mobile.feature.callintercept.LaunchReadinessLock launchLock = null;
    @org.jetbrains.annotations.NotNull()
    private final app.callcheck.mobile.feature.callintercept.GlobalLaunchDashboard dashboard = null;
    @org.jetbrains.annotations.NotNull()
    private final app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector metricsCollector = null;
    @org.jetbrains.annotations.NotNull()
    private final app.callcheck.mobile.feature.callintercept.CountryRealityTester realityTester = null;
    @org.jetbrains.annotations.NotNull()
    private final app.callcheck.mobile.feature.callintercept.CircuitBreakerRehearsalRunner rehearsalRunner = null;
    
    @javax.inject.Inject()
    public LaunchSimulationRunner(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.callintercept.GlobalSearchProviderRegistry registry, @org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker circuitBreaker, @org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.callintercept.LaunchReadinessLock launchLock, @org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.callintercept.GlobalLaunchDashboard dashboard, @org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector metricsCollector, @org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.callintercept.CountryRealityTester realityTester, @org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.callintercept.CircuitBreakerRehearsalRunner rehearsalRunner) {
        super();
    }
    
    /**
     * 정상 국가 시뮬레이션.
     *
     * 대상: Tier A/B/C 대표 국가 20개
     * 조건: 모든 검색 엔진 정상 작동
     * 기대: 100% SLA 준수, 올바른 엔진 사용
     */
    private final app.callcheck.mobile.feature.callintercept.LaunchSimulationRunner.ScenarioResult runScenario1_Normal() {
        return null;
    }
    
    /**
     * 검색 실패 시뮬레이션.
     *
     * 대상: 저데이터 / 미지원 국가
     * 조건: 1순위 검색 실패 → fallback 동작
     * 기대: fallback 표현 정확, SLA 내 완료
     */
    private final app.callcheck.mobile.feature.callintercept.LaunchSimulationRunner.ScenarioResult runScenario2_SearchFailure() {
        return null;
    }
    
    /**
     * Emergency 모드 강제 시뮬레이션.
     *
     * 대상: API 차단 / 완전 장애 국가
     * 조건: 모든 검색 실패 → 서킷 EMERGENCY
     * 기대: 최소 안전 표현 즉시 표시, SLA 내 완료
     */
    private final app.callcheck.mobile.feature.callintercept.LaunchSimulationRunner.ScenarioResult runScenario3_Emergency() {
        return null;
    }
    
    /**
     * Dashboard가 서킷 상태 변경을 실시간 반영하는지 검증.
     */
    private final boolean verifyDashboardRealtime() {
        return false;
    }
    
    /**
     * 3종 시나리오 + 부가 검증 전체 실행.
     */
    @org.jetbrains.annotations.NotNull()
    public final app.callcheck.mobile.feature.callintercept.LaunchSimulationRunner.LaunchSimulationReport runFullSimulation() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String formatReport(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.callintercept.LaunchSimulationRunner.LaunchSimulationReport report) {
        return null;
    }
    
    private final void formatScenario(java.lang.StringBuilder sb, app.callcheck.mobile.feature.callintercept.LaunchSimulationRunner.ScenarioResult s) {
    }
    
    private final java.lang.String mark(boolean pass) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\t\n\u0002\b\u0018\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001BG\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0007\u0012\u0006\u0010\t\u001a\u00020\u0007\u0012\u0006\u0010\n\u001a\u00020\u0007\u0012\b\b\u0002\u0010\u000b\u001a\u00020\f\u00a2\u0006\u0002\u0010\rJ\t\u0010\u0019\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001c\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u001d\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u001e\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u001f\u001a\u00020\u0007H\u00c6\u0003J\t\u0010 \u001a\u00020\fH\u00c6\u0003JY\u0010!\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00072\b\b\u0002\u0010\t\u001a\u00020\u00072\b\b\u0002\u0010\n\u001a\u00020\u00072\b\b\u0002\u0010\u000b\u001a\u00020\fH\u00c6\u0001J\u0013\u0010\"\u001a\u00020\u00072\b\u0010#\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010$\u001a\u00020%H\u00d6\u0001J\t\u0010&\u001a\u00020\'H\u00d6\u0001R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u000b\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\b\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u000fR\u0011\u0010\n\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u000fR\u0011\u0010\t\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u000fR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0016R\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0016\u00a8\u0006("}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/LaunchSimulationRunner$LaunchSimulationReport;", "", "scenario1_Normal", "Lapp/callcheck/mobile/feature/callintercept/LaunchSimulationRunner$ScenarioResult;", "scenario2_SearchFailure", "scenario3_Emergency", "dashboardReflectsRealtime", "", "globalSlaHeld", "recoveryVerified", "passed", "generatedAt", "", "(Lapp/callcheck/mobile/feature/callintercept/LaunchSimulationRunner$ScenarioResult;Lapp/callcheck/mobile/feature/callintercept/LaunchSimulationRunner$ScenarioResult;Lapp/callcheck/mobile/feature/callintercept/LaunchSimulationRunner$ScenarioResult;ZZZZJ)V", "getDashboardReflectsRealtime", "()Z", "getGeneratedAt", "()J", "getGlobalSlaHeld", "getPassed", "getRecoveryVerified", "getScenario1_Normal", "()Lapp/callcheck/mobile/feature/callintercept/LaunchSimulationRunner$ScenarioResult;", "getScenario2_SearchFailure", "getScenario3_Emergency", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "copy", "equals", "other", "hashCode", "", "toString", "", "call-intercept_debug"})
    public static final class LaunchSimulationReport {
        @org.jetbrains.annotations.NotNull()
        private final app.callcheck.mobile.feature.callintercept.LaunchSimulationRunner.ScenarioResult scenario1_Normal = null;
        @org.jetbrains.annotations.NotNull()
        private final app.callcheck.mobile.feature.callintercept.LaunchSimulationRunner.ScenarioResult scenario2_SearchFailure = null;
        @org.jetbrains.annotations.NotNull()
        private final app.callcheck.mobile.feature.callintercept.LaunchSimulationRunner.ScenarioResult scenario3_Emergency = null;
        private final boolean dashboardReflectsRealtime = false;
        private final boolean globalSlaHeld = false;
        private final boolean recoveryVerified = false;
        private final boolean passed = false;
        private final long generatedAt = 0L;
        
        public LaunchSimulationReport(@org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.LaunchSimulationRunner.ScenarioResult scenario1_Normal, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.LaunchSimulationRunner.ScenarioResult scenario2_SearchFailure, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.LaunchSimulationRunner.ScenarioResult scenario3_Emergency, boolean dashboardReflectsRealtime, boolean globalSlaHeld, boolean recoveryVerified, boolean passed, long generatedAt) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.LaunchSimulationRunner.ScenarioResult getScenario1_Normal() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.LaunchSimulationRunner.ScenarioResult getScenario2_SearchFailure() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.LaunchSimulationRunner.ScenarioResult getScenario3_Emergency() {
            return null;
        }
        
        public final boolean getDashboardReflectsRealtime() {
            return false;
        }
        
        public final boolean getGlobalSlaHeld() {
            return false;
        }
        
        public final boolean getRecoveryVerified() {
            return false;
        }
        
        public final boolean getPassed() {
            return false;
        }
        
        public final long getGeneratedAt() {
            return 0L;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.LaunchSimulationRunner.ScenarioResult component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.LaunchSimulationRunner.ScenarioResult component2() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.LaunchSimulationRunner.ScenarioResult component3() {
            return null;
        }
        
        public final boolean component4() {
            return false;
        }
        
        public final boolean component5() {
            return false;
        }
        
        public final boolean component6() {
            return false;
        }
        
        public final boolean component7() {
            return false;
        }
        
        public final long component8() {
            return 0L;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.LaunchSimulationRunner.LaunchSimulationReport copy(@org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.LaunchSimulationRunner.ScenarioResult scenario1_Normal, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.LaunchSimulationRunner.ScenarioResult scenario2_SearchFailure, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.LaunchSimulationRunner.ScenarioResult scenario3_Emergency, boolean dashboardReflectsRealtime, boolean globalSlaHeld, boolean recoveryVerified, boolean passed, long generatedAt) {
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u001d\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001BY\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0007\u0012\u0006\u0010\t\u001a\u00020\u0007\u0012\u0006\u0010\n\u001a\u00020\u0007\u0012\u0006\u0010\u000b\u001a\u00020\f\u0012\u0006\u0010\r\u001a\u00020\f\u0012\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00030\u0005\u00a2\u0006\u0002\u0010\u000fJ\t\u0010\u001d\u001a\u00020\u0003H\u00c6\u0003J\u000f\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00030\u0005H\u00c6\u0003J\t\u0010\u001f\u001a\u00020\u0007H\u00c6\u0003J\t\u0010 \u001a\u00020\u0007H\u00c6\u0003J\t\u0010!\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\"\u001a\u00020\u0007H\u00c6\u0003J\t\u0010#\u001a\u00020\fH\u00c6\u0003J\t\u0010$\u001a\u00020\fH\u00c6\u0003J\u000f\u0010%\u001a\b\u0012\u0004\u0012\u00020\u00030\u0005H\u00c6\u0003Jo\u0010&\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00072\b\b\u0002\u0010\t\u001a\u00020\u00072\b\b\u0002\u0010\n\u001a\u00020\u00072\b\b\u0002\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\r\u001a\u00020\f2\u000e\b\u0002\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00030\u0005H\u00c6\u0001J\u0013\u0010\'\u001a\u00020\f2\b\u0010(\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010)\u001a\u00020*H\u00d6\u0001J\t\u0010+\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\t\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0017\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0017\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00030\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0013R\u0011\u0010\n\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0011R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0011\u0010\u000b\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019R\u0011\u0010\r\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0019R\u0011\u0010\b\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0011R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u0011\u00a8\u0006,"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/LaunchSimulationRunner$ScenarioResult;", "", "name", "", "countriesTested", "", "slaComplianceRate", "", "searchSuccessRate", "correctEngineRate", "fallbackAccuracy", "noPerceivedDelay", "", "passed", "failedCountries", "(Ljava/lang/String;Ljava/util/List;FFFFZZLjava/util/List;)V", "getCorrectEngineRate", "()F", "getCountriesTested", "()Ljava/util/List;", "getFailedCountries", "getFallbackAccuracy", "getName", "()Ljava/lang/String;", "getNoPerceivedDelay", "()Z", "getPassed", "getSearchSuccessRate", "getSlaComplianceRate", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "other", "hashCode", "", "toString", "call-intercept_debug"})
    public static final class ScenarioResult {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String name = null;
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<java.lang.String> countriesTested = null;
        
        /**
         * SLA 준수율
         */
        private final float slaComplianceRate = 0.0F;
        
        /**
         * 검색 성공률
         */
        private final float searchSuccessRate = 0.0F;
        
        /**
         * 올바른 엔진 사용률
         */
        private final float correctEngineRate = 0.0F;
        
        /**
         * fallback 정확성
         */
        private final float fallbackAccuracy = 0.0F;
        
        /**
         * 사용자 체감 지연 유무
         */
        private final boolean noPerceivedDelay = false;
        
        /**
         * 시나리오 통과
         */
        private final boolean passed = false;
        
        /**
         * 실패 국가 상세
         */
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<java.lang.String> failedCountries = null;
        
        public ScenarioResult(@org.jetbrains.annotations.NotNull()
        java.lang.String name, @org.jetbrains.annotations.NotNull()
        java.util.List<java.lang.String> countriesTested, float slaComplianceRate, float searchSuccessRate, float correctEngineRate, float fallbackAccuracy, boolean noPerceivedDelay, boolean passed, @org.jetbrains.annotations.NotNull()
        java.util.List<java.lang.String> failedCountries) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getName() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<java.lang.String> getCountriesTested() {
            return null;
        }
        
        /**
         * SLA 준수율
         */
        public final float getSlaComplianceRate() {
            return 0.0F;
        }
        
        /**
         * 검색 성공률
         */
        public final float getSearchSuccessRate() {
            return 0.0F;
        }
        
        /**
         * 올바른 엔진 사용률
         */
        public final float getCorrectEngineRate() {
            return 0.0F;
        }
        
        /**
         * fallback 정확성
         */
        public final float getFallbackAccuracy() {
            return 0.0F;
        }
        
        /**
         * 사용자 체감 지연 유무
         */
        public final boolean getNoPerceivedDelay() {
            return false;
        }
        
        /**
         * 시나리오 통과
         */
        public final boolean getPassed() {
            return false;
        }
        
        /**
         * 실패 국가 상세
         */
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<java.lang.String> getFailedCountries() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<java.lang.String> component2() {
            return null;
        }
        
        public final float component3() {
            return 0.0F;
        }
        
        public final float component4() {
            return 0.0F;
        }
        
        public final float component5() {
            return 0.0F;
        }
        
        public final float component6() {
            return 0.0F;
        }
        
        public final boolean component7() {
            return false;
        }
        
        public final boolean component8() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<java.lang.String> component9() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.LaunchSimulationRunner.ScenarioResult copy(@org.jetbrains.annotations.NotNull()
        java.lang.String name, @org.jetbrains.annotations.NotNull()
        java.util.List<java.lang.String> countriesTested, float slaComplianceRate, float searchSuccessRate, float correctEngineRate, float fallbackAccuracy, boolean noPerceivedDelay, boolean passed, @org.jetbrains.annotations.NotNull()
        java.util.List<java.lang.String> failedCountries) {
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