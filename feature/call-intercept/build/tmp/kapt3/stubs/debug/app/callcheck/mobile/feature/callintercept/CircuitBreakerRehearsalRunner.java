package app.callcheck.mobile.feature.callintercept;

/**
 * Stage 15 — CircuitBreaker 리허설 러너.
 *
 * 자비스 기준:
 * "5회 실패 → OPEN 전환 확인
 * 10회 실패 → EMERGENCY 전환 확인
 * 5분 후 HALF-OPEN 복귀 확인
 *
 * 검증 포인트:
 * - 사용자 UX 깨짐 여부 없음
 * - 잘못된 '안전' 판단 출력 금지
 * - 상태 전이 로그 정상 기록"
 *
 * 강제 시나리오를 주입하여 서킷 브레이커의 상태 전이를
 * deterministic하게 검증.
 *
 * 100% 온디바이스. 서버 전송 없음.
 */
@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u000b\b\u0007\u0018\u0000 \u001f2\u00020\u0001:\u0004\u001f !\"B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J(\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\n2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\rH\u0002J\u000e\u0010\u000f\u001a\u00020\b2\u0006\u0010\u0010\u001a\u00020\u0011J\u0010\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\bH\u0002J\u0010\u0010\u0015\u001a\u00020\b2\u0006\u0010\u0014\u001a\u00020\bH\u0002J\u0006\u0010\u0016\u001a\u00020\u0011J\u0010\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0014\u001a\u00020\bH\u0002J\u0010\u0010\u0019\u001a\u00020\u00182\u0006\u0010\u0014\u001a\u00020\bH\u0002J\u0010\u0010\u001a\u001a\u00020\u00182\u0006\u0010\u0014\u001a\u00020\bH\u0002J\u0010\u0010\u001b\u001a\u00020\b2\u0006\u0010\u001c\u001a\u00020\rH\u0002J\b\u0010\u001d\u001a\u00020\rH\u0002J\b\u0010\u001e\u001a\u00020\rH\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006#"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/CircuitBreakerRehearsalRunner;", "", "circuitBreaker", "Lapp/callcheck/mobile/feature/callintercept/OperationalCircuitBreaker;", "launchLock", "Lapp/callcheck/mobile/feature/callintercept/LaunchReadinessLock;", "(Lapp/callcheck/mobile/feature/callintercept/OperationalCircuitBreaker;Lapp/callcheck/mobile/feature/callintercept/LaunchReadinessLock;)V", "buildFailureDetail", "", "actualState", "Lapp/callcheck/mobile/feature/callintercept/OperationalCircuitBreaker$CircuitState;", "expectedState", "uxSafe", "", "noMisjudgment", "formatReport", "report", "Lapp/callcheck/mobile/feature/callintercept/CircuitBreakerRehearsalRunner$RehearsalReport;", "getEngineForCountry", "Lapp/callcheck/mobile/core/model/SearchEngine;", "countryCode", "getLanguageForCountry", "runFullRehearsal", "runScenario_10Failures_ToEmergency", "Lapp/callcheck/mobile/feature/callintercept/CircuitBreakerRehearsalRunner$ScenarioResult;", "runScenario_5Failures_ToOpen", "runScenario_HalfOpen_Recovery", "statusMark", "pass", "verifyClosedRecoveryLogicExists", "verifyHalfOpenTransitionLogicExists", "Companion", "RehearsalReport", "ScenarioResult", "StateTransition", "call-intercept_debug"})
public final class CircuitBreakerRehearsalRunner {
    @org.jetbrains.annotations.NotNull()
    private final app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker circuitBreaker = null;
    @org.jetbrains.annotations.NotNull()
    private final app.callcheck.mobile.feature.callintercept.LaunchReadinessLock launchLock = null;
    
    /**
     * 리허설 대상 국가: 각 Tier에서 대표 1개씩
     */
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<java.lang.String> REHEARSAL_COUNTRIES = null;
    @org.jetbrains.annotations.NotNull()
    public static final app.callcheck.mobile.feature.callintercept.CircuitBreakerRehearsalRunner.Companion Companion = null;
    
    @javax.inject.Inject()
    public CircuitBreakerRehearsalRunner(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker circuitBreaker, @org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.callintercept.LaunchReadinessLock launchLock) {
        super();
    }
    
    /**
     * 5회 연속 실패 → CLOSED→OPEN 전이 검증.
     *
     * 검증:
     * - 4회까지는 CLOSED 유지
     * - 5회째 OPEN 전환
     * - OPEN 상태에서 1순위 엔진 차단 확인
     */
    private final app.callcheck.mobile.feature.callintercept.CircuitBreakerRehearsalRunner.ScenarioResult runScenario_5Failures_ToOpen(java.lang.String countryCode) {
        return null;
    }
    
    /**
     * 10회 연속 실패 → OPEN→EMERGENCY 전이 검증.
     *
     * 검증:
     * - OPEN 상태에서 추가 실패 시 EMERGENCY
     * - EMERGENCY에서 최소 안전 표현만 출력
     * - 검색 엔진 사용 완전 중단
     */
    private final app.callcheck.mobile.feature.callintercept.CircuitBreakerRehearsalRunner.ScenarioResult runScenario_10Failures_ToEmergency(java.lang.String countryCode) {
        return null;
    }
    
    /**
     * OPEN → 쿨다운 → HALF_OPEN → 3회 연속 성공 → CLOSED 복귀.
     *
     * 검증:
     * - 5분 쿨다운 후 HALF_OPEN 전이
     * - 3회 연속 성공으로 CLOSED 복귀
     * - 복귀 후 1순위 엔진 정상 사용
     */
    private final app.callcheck.mobile.feature.callintercept.CircuitBreakerRehearsalRunner.ScenarioResult runScenario_HalfOpen_Recovery(java.lang.String countryCode) {
        return null;
    }
    
    /**
     * 모든 국가 × 3개 시나리오 전체 리허설.
     */
    @org.jetbrains.annotations.NotNull()
    public final app.callcheck.mobile.feature.callintercept.CircuitBreakerRehearsalRunner.RehearsalReport runFullRehearsal() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String formatReport(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.callintercept.CircuitBreakerRehearsalRunner.RehearsalReport report) {
        return null;
    }
    
    private final app.callcheck.mobile.core.model.SearchEngine getEngineForCountry(java.lang.String countryCode) {
        return null;
    }
    
    private final java.lang.String getLanguageForCountry(java.lang.String countryCode) {
        return null;
    }
    
    /**
     * OPEN → HALF_OPEN 전이 로직이 OperationalCircuitBreaker에 구현되어 있는지 확인.
     * 빅테크 방식: 코드 구조 기반 정적 검증.
     *
     * OperationalCircuitBreaker.recordSuccess()에서:
     *  CircuitState.OPEN → elapsed >= OPEN_TO_HALF_OPEN_COOLDOWN_MS → HALF_OPEN
     */
    private final boolean verifyHalfOpenTransitionLogicExists() {
        return false;
    }
    
    /**
     * HALF_OPEN → 3회 성공 → CLOSED 복귀 로직 존재 확인.
     *
     * OperationalCircuitBreaker.recordSuccess()에서:
     *  CircuitState.HALF_OPEN → consecutiveSuccesses >= HALF_OPEN_SUCCESS_THRESHOLD → CLOSED
     */
    private final boolean verifyClosedRecoveryLogicExists() {
        return false;
    }
    
    private final java.lang.String buildFailureDetail(app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.CircuitState actualState, app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.CircuitState expectedState, boolean uxSafe, boolean noMisjudgment) {
        return null;
    }
    
    private final java.lang.String statusMark(boolean pass) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0017\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\b"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/CircuitBreakerRehearsalRunner$Companion;", "", "()V", "REHEARSAL_COUNTRIES", "", "", "getREHEARSAL_COUNTRIES", "()Ljava/util/List;", "call-intercept_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        /**
         * 리허설 대상 국가: 각 Tier에서 대표 1개씩
         */
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<java.lang.String> getREHEARSAL_COUNTRIES() {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0010\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B3\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\b\b\u0002\u0010\b\u001a\u00020\t\u00a2\u0006\u0002\u0010\nJ\u000f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J\t\u0010\u0013\u001a\u00020\u0006H\u00c6\u0003J\u000f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J\t\u0010\u0015\u001a\u00020\tH\u00c6\u0003J=\u0010\u0016\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0002\u0010\b\u001a\u00020\tH\u00c6\u0001J\u0013\u0010\u0017\u001a\u00020\u00062\b\u0010\u0018\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0019\u001a\u00020\u001aH\u00d6\u0001J\t\u0010\u001b\u001a\u00020\u001cH\u00d6\u0001R\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0017\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u000e\u00a8\u0006\u001d"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/CircuitBreakerRehearsalRunner$RehearsalReport;", "", "scenarioResults", "", "Lapp/callcheck/mobile/feature/callintercept/CircuitBreakerRehearsalRunner$ScenarioResult;", "allPassed", "", "failures", "generatedAt", "", "(Ljava/util/List;ZLjava/util/List;J)V", "getAllPassed", "()Z", "getFailures", "()Ljava/util/List;", "getGeneratedAt", "()J", "getScenarioResults", "component1", "component2", "component3", "component4", "copy", "equals", "other", "hashCode", "", "toString", "", "call-intercept_debug"})
    public static final class RehearsalReport {
        
        /**
         * 시나리오별 결과
         */
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<app.callcheck.mobile.feature.callintercept.CircuitBreakerRehearsalRunner.ScenarioResult> scenarioResults = null;
        
        /**
         * 전체 PASS 여부
         */
        private final boolean allPassed = false;
        
        /**
         * 실패한 시나리오 목록
         */
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<app.callcheck.mobile.feature.callintercept.CircuitBreakerRehearsalRunner.ScenarioResult> failures = null;
        private final long generatedAt = 0L;
        
        public RehearsalReport(@org.jetbrains.annotations.NotNull()
        java.util.List<app.callcheck.mobile.feature.callintercept.CircuitBreakerRehearsalRunner.ScenarioResult> scenarioResults, boolean allPassed, @org.jetbrains.annotations.NotNull()
        java.util.List<app.callcheck.mobile.feature.callintercept.CircuitBreakerRehearsalRunner.ScenarioResult> failures, long generatedAt) {
            super();
        }
        
        /**
         * 시나리오별 결과
         */
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<app.callcheck.mobile.feature.callintercept.CircuitBreakerRehearsalRunner.ScenarioResult> getScenarioResults() {
            return null;
        }
        
        /**
         * 전체 PASS 여부
         */
        public final boolean getAllPassed() {
            return false;
        }
        
        /**
         * 실패한 시나리오 목록
         */
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<app.callcheck.mobile.feature.callintercept.CircuitBreakerRehearsalRunner.ScenarioResult> getFailures() {
            return null;
        }
        
        public final long getGeneratedAt() {
            return 0L;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<app.callcheck.mobile.feature.callintercept.CircuitBreakerRehearsalRunner.ScenarioResult> component1() {
            return null;
        }
        
        public final boolean component2() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<app.callcheck.mobile.feature.callintercept.CircuitBreakerRehearsalRunner.ScenarioResult> component3() {
            return null;
        }
        
        public final long component4() {
            return 0L;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.CircuitBreakerRehearsalRunner.RehearsalReport copy(@org.jetbrains.annotations.NotNull()
        java.util.List<app.callcheck.mobile.feature.callintercept.CircuitBreakerRehearsalRunner.ScenarioResult> scenarioResults, boolean allPassed, @org.jetbrains.annotations.NotNull()
        java.util.List<app.callcheck.mobile.feature.callintercept.CircuitBreakerRehearsalRunner.ScenarioResult> failures, long generatedAt) {
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u001e\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001BU\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\t\u0012\u0006\u0010\u000b\u001a\u00020\f\u0012\u0006\u0010\r\u001a\u00020\f\u0012\u0006\u0010\u000e\u001a\u00020\f\u0012\b\u0010\u000f\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\u0002\u0010\u0010J\t\u0010\u001e\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001f\u001a\u00020\u0003H\u00c6\u0003J\u000f\u0010 \u001a\b\u0012\u0004\u0012\u00020\u00070\u0006H\u00c6\u0003J\t\u0010!\u001a\u00020\tH\u00c6\u0003J\t\u0010\"\u001a\u00020\tH\u00c6\u0003J\t\u0010#\u001a\u00020\fH\u00c6\u0003J\t\u0010$\u001a\u00020\fH\u00c6\u0003J\t\u0010%\u001a\u00020\fH\u00c6\u0003J\u000b\u0010&\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003Jk\u0010\'\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\t2\b\b\u0002\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\r\u001a\u00020\f2\b\b\u0002\u0010\u000e\u001a\u00020\f2\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u0003H\u00c6\u0001J\u0013\u0010(\u001a\u00020\f2\b\u0010)\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010*\u001a\u00020+H\u00d6\u0001J\t\u0010,\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\n\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0012R\u0013\u0010\u000f\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0014R\u0011\u0010\r\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0011\u0010\u000e\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0018R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0014R\u0017\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001cR\u0011\u0010\u000b\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u0018\u00a8\u0006-"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/CircuitBreakerRehearsalRunner$ScenarioResult;", "", "scenarioName", "", "countryCode", "stateTransitions", "", "Lapp/callcheck/mobile/feature/callintercept/CircuitBreakerRehearsalRunner$StateTransition;", "expectedFinalState", "Lapp/callcheck/mobile/feature/callintercept/OperationalCircuitBreaker$CircuitState;", "actualFinalState", "uxSafetyPassed", "", "noMisjudgmentPassed", "passed", "failureReason", "(Ljava/lang/String;Ljava/lang/String;Ljava/util/List;Lapp/callcheck/mobile/feature/callintercept/OperationalCircuitBreaker$CircuitState;Lapp/callcheck/mobile/feature/callintercept/OperationalCircuitBreaker$CircuitState;ZZZLjava/lang/String;)V", "getActualFinalState", "()Lapp/callcheck/mobile/feature/callintercept/OperationalCircuitBreaker$CircuitState;", "getCountryCode", "()Ljava/lang/String;", "getExpectedFinalState", "getFailureReason", "getNoMisjudgmentPassed", "()Z", "getPassed", "getScenarioName", "getStateTransitions", "()Ljava/util/List;", "getUxSafetyPassed", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "other", "hashCode", "", "toString", "call-intercept_debug"})
    public static final class ScenarioResult {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String scenarioName = null;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String countryCode = null;
        
        /**
         * 각 단계의 상태 전이 로그
         */
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<app.callcheck.mobile.feature.callintercept.CircuitBreakerRehearsalRunner.StateTransition> stateTransitions = null;
        
        /**
         * 최종 기대 상태
         */
        @org.jetbrains.annotations.NotNull()
        private final app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.CircuitState expectedFinalState = null;
        
        /**
         * 실제 최종 상태
         */
        @org.jetbrains.annotations.NotNull()
        private final app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.CircuitState actualFinalState = null;
        
        /**
         * UX 안전 검증 통과
         */
        private final boolean uxSafetyPassed = false;
        
        /**
         * 오판 방지 검증 통과
         */
        private final boolean noMisjudgmentPassed = false;
        
        /**
         * 시나리오 전체 통과
         */
        private final boolean passed = false;
        
        /**
         * 실패 사유
         */
        @org.jetbrains.annotations.Nullable()
        private final java.lang.String failureReason = null;
        
        public ScenarioResult(@org.jetbrains.annotations.NotNull()
        java.lang.String scenarioName, @org.jetbrains.annotations.NotNull()
        java.lang.String countryCode, @org.jetbrains.annotations.NotNull()
        java.util.List<app.callcheck.mobile.feature.callintercept.CircuitBreakerRehearsalRunner.StateTransition> stateTransitions, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.CircuitState expectedFinalState, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.CircuitState actualFinalState, boolean uxSafetyPassed, boolean noMisjudgmentPassed, boolean passed, @org.jetbrains.annotations.Nullable()
        java.lang.String failureReason) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getScenarioName() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getCountryCode() {
            return null;
        }
        
        /**
         * 각 단계의 상태 전이 로그
         */
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<app.callcheck.mobile.feature.callintercept.CircuitBreakerRehearsalRunner.StateTransition> getStateTransitions() {
            return null;
        }
        
        /**
         * 최종 기대 상태
         */
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.CircuitState getExpectedFinalState() {
            return null;
        }
        
        /**
         * 실제 최종 상태
         */
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.CircuitState getActualFinalState() {
            return null;
        }
        
        /**
         * UX 안전 검증 통과
         */
        public final boolean getUxSafetyPassed() {
            return false;
        }
        
        /**
         * 오판 방지 검증 통과
         */
        public final boolean getNoMisjudgmentPassed() {
            return false;
        }
        
        /**
         * 시나리오 전체 통과
         */
        public final boolean getPassed() {
            return false;
        }
        
        /**
         * 실패 사유
         */
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String getFailureReason() {
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
        public final java.util.List<app.callcheck.mobile.feature.callintercept.CircuitBreakerRehearsalRunner.StateTransition> component3() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.CircuitState component4() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.CircuitState component5() {
            return null;
        }
        
        public final boolean component6() {
            return false;
        }
        
        public final boolean component7() {
            return false;
        }
        
        public final boolean component8() {
            return false;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String component9() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.CircuitBreakerRehearsalRunner.ScenarioResult copy(@org.jetbrains.annotations.NotNull()
        java.lang.String scenarioName, @org.jetbrains.annotations.NotNull()
        java.lang.String countryCode, @org.jetbrains.annotations.NotNull()
        java.util.List<app.callcheck.mobile.feature.callintercept.CircuitBreakerRehearsalRunner.StateTransition> stateTransitions, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.CircuitState expectedFinalState, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.CircuitState actualFinalState, boolean uxSafetyPassed, boolean noMisjudgmentPassed, boolean passed, @org.jetbrains.annotations.Nullable()
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0012\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\b\u0018\u00002\u00020\u0001B-\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0007\u0012\u0006\u0010\t\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\nJ\t\u0010\u0013\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0014\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0015\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u0016\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u0017\u001a\u00020\u0005H\u00c6\u0003J;\u0010\u0018\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00072\b\b\u0002\u0010\t\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010\u0019\u001a\u00020\u001a2\b\u0010\u001b\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001c\u001a\u00020\u0003H\u00d6\u0001J\t\u0010\u001d\u001a\u00020\u0005H\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\b\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u000eR\u0011\u0010\t\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\fR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012\u00a8\u0006\u001e"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/CircuitBreakerRehearsalRunner$StateTransition;", "", "step", "", "action", "", "beforeState", "Lapp/callcheck/mobile/feature/callintercept/OperationalCircuitBreaker$CircuitState;", "afterState", "detail", "(ILjava/lang/String;Lapp/callcheck/mobile/feature/callintercept/OperationalCircuitBreaker$CircuitState;Lapp/callcheck/mobile/feature/callintercept/OperationalCircuitBreaker$CircuitState;Ljava/lang/String;)V", "getAction", "()Ljava/lang/String;", "getAfterState", "()Lapp/callcheck/mobile/feature/callintercept/OperationalCircuitBreaker$CircuitState;", "getBeforeState", "getDetail", "getStep", "()I", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "", "other", "hashCode", "toString", "call-intercept_debug"})
    public static final class StateTransition {
        private final int step = 0;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String action = null;
        @org.jetbrains.annotations.NotNull()
        private final app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.CircuitState beforeState = null;
        @org.jetbrains.annotations.NotNull()
        private final app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.CircuitState afterState = null;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String detail = null;
        
        public StateTransition(int step, @org.jetbrains.annotations.NotNull()
        java.lang.String action, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.CircuitState beforeState, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.CircuitState afterState, @org.jetbrains.annotations.NotNull()
        java.lang.String detail) {
            super();
        }
        
        public final int getStep() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getAction() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.CircuitState getBeforeState() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.CircuitState getAfterState() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getDetail() {
            return null;
        }
        
        public final int component1() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component2() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.CircuitState component3() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.CircuitState component4() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component5() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.CircuitBreakerRehearsalRunner.StateTransition copy(int step, @org.jetbrains.annotations.NotNull()
        java.lang.String action, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.CircuitState beforeState, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.CircuitState afterState, @org.jetbrains.annotations.NotNull()
        java.lang.String detail) {
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