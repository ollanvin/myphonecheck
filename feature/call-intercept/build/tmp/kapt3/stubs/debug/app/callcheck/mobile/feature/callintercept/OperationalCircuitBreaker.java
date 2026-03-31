package app.callcheck.mobile.feature.callintercept;

/**
 * 운영 서킷 브레이커.
 *
 * 자비스 기준:
 * "특정 국가에서 검색 실패율이 급증하면 자동으로 2순위/3순위로 넘기고,
 * 그래도 실패하면 최소 안전 표현으로 떨어지는 운영 모드가 필요"
 *
 * 서킷 브레이커 3-State 패턴 (빅테크 정석):
 *  CLOSED  → 정상 운영 (1순위 사용)
 *  HALF_OPEN → 1순위 부분 시도 (성공하면 CLOSED 복귀)
 *  OPEN    → 1순위 차단, 2순위/3순위만 사용
 *
 * 추가 Emergency Mode:
 *  2순위/3순위도 실패 → 최소 안전 표현 강제 표시
 *
 * 100% 온디바이스. 서버 전송 없음.
 */
@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010%\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\f\b\u0007\u0018\u0000  2\u00020\u0001:\u0007\u001e\u001f !\"#$B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010\f\u001a\u00020\rJ\u000e\u0010\u000e\u001a\u00020\b2\u0006\u0010\u000f\u001a\u00020\u0007J\u000e\u0010\u0010\u001a\u00020\u000b2\u0006\u0010\u0011\u001a\u00020\nJ.\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u000f\u001a\u00020\u00072\u0006\u0010\u0014\u001a\u00020\n2\u0006\u0010\u0015\u001a\u00020\n2\u0006\u0010\u0016\u001a\u00020\n2\u0006\u0010\u0017\u001a\u00020\u0007J&\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u000f\u001a\u00020\u00072\u0006\u0010\u0011\u001a\u00020\n2\u0006\u0010\u001a\u001a\u00020\n2\u0006\u0010\u001b\u001a\u00020\nJ\u0016\u0010\u001c\u001a\u00020\u00192\u0006\u0010\u000f\u001a\u00020\u00072\u0006\u0010\u0011\u001a\u00020\nJ\u0006\u0010\u001d\u001a\u00020\u0019R\u001a\u0010\u0005\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\b0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u000b0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006%"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/OperationalCircuitBreaker;", "", "launchLock", "Lapp/callcheck/mobile/feature/callintercept/LaunchReadinessLock;", "(Lapp/callcheck/mobile/feature/callintercept/LaunchReadinessLock;)V", "countryCircuits", "", "", "Lapp/callcheck/mobile/feature/callintercept/OperationalCircuitBreaker$CountryCircuit;", "engineCircuits", "Lapp/callcheck/mobile/core/model/SearchEngine;", "Lapp/callcheck/mobile/feature/callintercept/OperationalCircuitBreaker$EngineCircuit;", "getCircuitReport", "Lapp/callcheck/mobile/feature/callintercept/OperationalCircuitBreaker$CircuitReport;", "getCountryCircuit", "countryCode", "getEngineCircuit", "engine", "getRoutingDecision", "Lapp/callcheck/mobile/feature/callintercept/OperationalCircuitBreaker$RoutingDecision;", "originalPrimary", "secondary", "tertiary", "languageCode", "recordFailure", "", "secondaryEngine", "tertiaryEngine", "recordSuccess", "resetAll", "CircuitReport", "CircuitState", "Companion", "CountryCircuit", "EngineCircuit", "OperationMode", "RoutingDecision", "call-intercept_debug"})
public final class OperationalCircuitBreaker {
    @org.jetbrains.annotations.NotNull()
    private final app.callcheck.mobile.feature.callintercept.LaunchReadinessLock launchLock = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Map<java.lang.String, app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.CountryCircuit> countryCircuits = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Map<app.callcheck.mobile.core.model.SearchEngine, app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.EngineCircuit> engineCircuits = null;
    
    /**
     * 연속 실패 임계값 → OPEN 전이
     */
    private static final int FAILURE_THRESHOLD = 5;
    
    /**
     * 연속 실패 임계값 → EMERGENCY 전이
     */
    private static final int EMERGENCY_THRESHOLD = 10;
    
    /**
     * HALF_OPEN에서 CLOSED 복귀 필요 연속 성공 수
     */
    private static final int HALF_OPEN_SUCCESS_THRESHOLD = 3;
    
    /**
     * OPEN → HALF_OPEN 전이 쿨다운 (ms) — 5분
     */
    private static final long OPEN_TO_HALF_OPEN_COOLDOWN_MS = 300000L;
    @org.jetbrains.annotations.NotNull()
    public static final app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.Companion Companion = null;
    
    @javax.inject.Inject()
    public OperationalCircuitBreaker(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.callintercept.LaunchReadinessLock launchLock) {
        super();
    }
    
    /**
     * 국가별 서킷 상태 조회
     */
    @org.jetbrains.annotations.NotNull()
    public final app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.CountryCircuit getCountryCircuit(@org.jetbrains.annotations.NotNull()
    java.lang.String countryCode) {
        return null;
    }
    
    /**
     * 엔진별 서킷 상태 조회
     */
    @org.jetbrains.annotations.NotNull()
    public final app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.EngineCircuit getEngineCircuit(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.core.model.SearchEngine engine) {
        return null;
    }
    
    /**
     * 검색 성공 기록.
     */
    @kotlin.jvm.Synchronized()
    public final synchronized void recordSuccess(@org.jetbrains.annotations.NotNull()
    java.lang.String countryCode, @org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.core.model.SearchEngine engine) {
    }
    
    /**
     * 검색 실패 기록.
     */
    @kotlin.jvm.Synchronized()
    public final synchronized void recordFailure(@org.jetbrains.annotations.NotNull()
    java.lang.String countryCode, @org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.core.model.SearchEngine engine, @org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.core.model.SearchEngine secondaryEngine, @org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.core.model.SearchEngine tertiaryEngine) {
    }
    
    /**
     * 현재 서킷 상태 기반 라우팅 결정.
     *
     * @return RoutingDecision — 어떤 엔진을 사용할지, 안전 표현을 바로 표시할지
     */
    @org.jetbrains.annotations.NotNull()
    public final app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.RoutingDecision getRoutingDecision(@org.jetbrains.annotations.NotNull()
    java.lang.String countryCode, @org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.core.model.SearchEngine originalPrimary, @org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.core.model.SearchEngine secondary, @org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.core.model.SearchEngine tertiary, @org.jetbrains.annotations.NotNull()
    java.lang.String languageCode) {
        return null;
    }
    
    /**
     * 전체 서킷 상태 보고
     */
    @org.jetbrains.annotations.NotNull()
    public final app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.CircuitReport getCircuitReport() {
        return null;
    }
    
    /**
     * 서킷 초기화 (테스트용)
     */
    @kotlin.jvm.Synchronized()
    public final synchronized void resetAll() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0015\n\u0002\u0010\u000b\n\u0002\b\u0005\b\u0086\b\u0018\u00002\u00020\u0001BW\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\u0003\u0012\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t\u0012\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\n0\t\u0012\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\r0\t\u00a2\u0006\u0002\u0010\u000eJ\t\u0010\u0019\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001c\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001d\u001a\u00020\u0003H\u00c6\u0003J\u000f\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\n0\tH\u00c6\u0003J\u000f\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\n0\tH\u00c6\u0003J\u000f\u0010 \u001a\b\u0012\u0004\u0012\u00020\r0\tH\u00c6\u0003Jk\u0010!\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u00032\u000e\b\u0002\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t2\u000e\b\u0002\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\n0\t2\u000e\b\u0002\u0010\f\u001a\b\u0012\u0004\u0012\u00020\r0\tH\u00c6\u0001J\u0013\u0010\"\u001a\u00020#2\b\u0010$\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010%\u001a\u00020\u0003H\u00d6\u0001J\u0006\u0010&\u001a\u00020\nJ\t\u0010\'\u001a\u00020\nH\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0007\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0010R\u0017\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\n0\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0017\u0010\f\u001a\b\u0012\u0004\u0012\u00020\r0\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0013R\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0010R\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0010R\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0013R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0010\u00a8\u0006("}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/OperationalCircuitBreaker$CircuitReport;", "", "totalCountriesMonitored", "", "closedCount", "halfOpenCount", "openCount", "emergencyCount", "openCountries", "", "", "emergencyCountries", "engineStates", "Lapp/callcheck/mobile/feature/callintercept/OperationalCircuitBreaker$EngineCircuit;", "(IIIIILjava/util/List;Ljava/util/List;Ljava/util/List;)V", "getClosedCount", "()I", "getEmergencyCount", "getEmergencyCountries", "()Ljava/util/List;", "getEngineStates", "getHalfOpenCount", "getOpenCount", "getOpenCountries", "getTotalCountriesMonitored", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "copy", "equals", "", "other", "hashCode", "toJarvisFormat", "toString", "call-intercept_debug"})
    public static final class CircuitReport {
        private final int totalCountriesMonitored = 0;
        private final int closedCount = 0;
        private final int halfOpenCount = 0;
        private final int openCount = 0;
        private final int emergencyCount = 0;
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<java.lang.String> openCountries = null;
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<java.lang.String> emergencyCountries = null;
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.EngineCircuit> engineStates = null;
        
        public CircuitReport(int totalCountriesMonitored, int closedCount, int halfOpenCount, int openCount, int emergencyCount, @org.jetbrains.annotations.NotNull()
        java.util.List<java.lang.String> openCountries, @org.jetbrains.annotations.NotNull()
        java.util.List<java.lang.String> emergencyCountries, @org.jetbrains.annotations.NotNull()
        java.util.List<app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.EngineCircuit> engineStates) {
            super();
        }
        
        public final int getTotalCountriesMonitored() {
            return 0;
        }
        
        public final int getClosedCount() {
            return 0;
        }
        
        public final int getHalfOpenCount() {
            return 0;
        }
        
        public final int getOpenCount() {
            return 0;
        }
        
        public final int getEmergencyCount() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<java.lang.String> getOpenCountries() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<java.lang.String> getEmergencyCountries() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.EngineCircuit> getEngineStates() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String toJarvisFormat() {
            return null;
        }
        
        public final int component1() {
            return 0;
        }
        
        public final int component2() {
            return 0;
        }
        
        public final int component3() {
            return 0;
        }
        
        public final int component4() {
            return 0;
        }
        
        public final int component5() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<java.lang.String> component6() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<java.lang.String> component7() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.EngineCircuit> component8() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.CircuitReport copy(int totalCountriesMonitored, int closedCount, int halfOpenCount, int openCount, int emergencyCount, @org.jetbrains.annotations.NotNull()
        java.util.List<java.lang.String> openCountries, @org.jetbrains.annotations.NotNull()
        java.util.List<java.lang.String> emergencyCountries, @org.jetbrains.annotations.NotNull()
        java.util.List<app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.EngineCircuit> engineStates) {
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\u000e\n\u0002\b\b\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u000f\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\n\u00a8\u0006\u000b"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/OperationalCircuitBreaker$CircuitState;", "", "label", "", "(Ljava/lang/String;ILjava/lang/String;)V", "getLabel", "()Ljava/lang/String;", "CLOSED", "HALF_OPEN", "OPEN", "EMERGENCY", "call-intercept_debug"})
    public static enum CircuitState {
        /*public static final*/ CLOSED /* = new CLOSED(null) */,
        /*public static final*/ HALF_OPEN /* = new HALF_OPEN(null) */,
        /*public static final*/ OPEN /* = new OPEN(null) */,
        /*public static final*/ EMERGENCY /* = new EMERGENCY(null) */;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String label = null;
        
        CircuitState(java.lang.String label) {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getLabel() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public static kotlin.enums.EnumEntries<app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.CircuitState> getEntries() {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\t\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\t"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/OperationalCircuitBreaker$Companion;", "", "()V", "EMERGENCY_THRESHOLD", "", "FAILURE_THRESHOLD", "HALF_OPEN_SUCCESS_THRESHOLD", "OPEN_TO_HALF_OPEN_COOLDOWN_MS", "", "call-intercept_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
    
    /**
     * 국가별 서킷 상태
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0017\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\b\u0018\u00002\u00020\u0001B=\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0007\u0012\u0006\u0010\t\u001a\u00020\n\u0012\u0006\u0010\u000b\u001a\u00020\f\u0012\u0006\u0010\r\u001a\u00020\f\u00a2\u0006\u0002\u0010\u000eJ\t\u0010\u001b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001c\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u001d\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u001e\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u001f\u001a\u00020\nH\u00c6\u0003J\t\u0010 \u001a\u00020\fH\u00c6\u0003J\t\u0010!\u001a\u00020\fH\u00c6\u0003JO\u0010\"\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00072\b\b\u0002\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\r\u001a\u00020\fH\u00c6\u0001J\u0013\u0010#\u001a\u00020$2\b\u0010%\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010&\u001a\u00020\u0007H\u00d6\u0001J\t\u0010\'\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\b\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0010R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\u000b\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0011\u0010\r\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0015R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001a\u00a8\u0006("}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/OperationalCircuitBreaker$CountryCircuit;", "", "countryCode", "", "state", "Lapp/callcheck/mobile/feature/callintercept/OperationalCircuitBreaker$CircuitState;", "consecutiveFailures", "", "consecutiveSuccesses", "lastStateChangeMs", "", "effectivePrimaryEngine", "Lapp/callcheck/mobile/core/model/SearchEngine;", "originalPrimaryEngine", "(Ljava/lang/String;Lapp/callcheck/mobile/feature/callintercept/OperationalCircuitBreaker$CircuitState;IIJLapp/callcheck/mobile/core/model/SearchEngine;Lapp/callcheck/mobile/core/model/SearchEngine;)V", "getConsecutiveFailures", "()I", "getConsecutiveSuccesses", "getCountryCode", "()Ljava/lang/String;", "getEffectivePrimaryEngine", "()Lapp/callcheck/mobile/core/model/SearchEngine;", "getLastStateChangeMs", "()J", "getOriginalPrimaryEngine", "getState", "()Lapp/callcheck/mobile/feature/callintercept/OperationalCircuitBreaker$CircuitState;", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "copy", "equals", "", "other", "hashCode", "toString", "call-intercept_debug"})
    public static final class CountryCircuit {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String countryCode = null;
        @org.jetbrains.annotations.NotNull()
        private final app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.CircuitState state = null;
        
        /**
         * 연속 실패 횟수
         */
        private final int consecutiveFailures = 0;
        
        /**
         * 연속 성공 횟수 (HALF_OPEN에서 사용)
         */
        private final int consecutiveSuccesses = 0;
        
        /**
         * 마지막 상태 변경 시각
         */
        private final long lastStateChangeMs = 0L;
        
        /**
         * 현재 사용 중인 검색 엔진 (서킷에 의해 변경될 수 있음)
         */
        @org.jetbrains.annotations.NotNull()
        private final app.callcheck.mobile.core.model.SearchEngine effectivePrimaryEngine = null;
        
        /**
         * 원래 1순위 엔진
         */
        @org.jetbrains.annotations.NotNull()
        private final app.callcheck.mobile.core.model.SearchEngine originalPrimaryEngine = null;
        
        public CountryCircuit(@org.jetbrains.annotations.NotNull()
        java.lang.String countryCode, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.CircuitState state, int consecutiveFailures, int consecutiveSuccesses, long lastStateChangeMs, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.core.model.SearchEngine effectivePrimaryEngine, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.core.model.SearchEngine originalPrimaryEngine) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getCountryCode() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.CircuitState getState() {
            return null;
        }
        
        /**
         * 연속 실패 횟수
         */
        public final int getConsecutiveFailures() {
            return 0;
        }
        
        /**
         * 연속 성공 횟수 (HALF_OPEN에서 사용)
         */
        public final int getConsecutiveSuccesses() {
            return 0;
        }
        
        /**
         * 마지막 상태 변경 시각
         */
        public final long getLastStateChangeMs() {
            return 0L;
        }
        
        /**
         * 현재 사용 중인 검색 엔진 (서킷에 의해 변경될 수 있음)
         */
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.core.model.SearchEngine getEffectivePrimaryEngine() {
            return null;
        }
        
        /**
         * 원래 1순위 엔진
         */
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.core.model.SearchEngine getOriginalPrimaryEngine() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.CircuitState component2() {
            return null;
        }
        
        public final int component3() {
            return 0;
        }
        
        public final int component4() {
            return 0;
        }
        
        public final long component5() {
            return 0L;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.core.model.SearchEngine component6() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.core.model.SearchEngine component7() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.CountryCircuit copy(@org.jetbrains.annotations.NotNull()
        java.lang.String countryCode, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.CircuitState state, int consecutiveFailures, int consecutiveSuccesses, long lastStateChangeMs, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.core.model.SearchEngine effectivePrimaryEngine, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.core.model.SearchEngine originalPrimaryEngine) {
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
     * 엔진별 서킷 상태
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0011\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B-\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0007\u0012\u0006\u0010\t\u001a\u00020\n\u00a2\u0006\u0002\u0010\u000bJ\t\u0010\u0015\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0016\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0017\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u0019\u001a\u00020\nH\u00c6\u0003J;\u0010\u001a\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00072\b\b\u0002\u0010\t\u001a\u00020\nH\u00c6\u0001J\u0013\u0010\u001b\u001a\u00020\u001c2\b\u0010\u001d\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001e\u001a\u00020\u0007H\u00d6\u0001J\t\u0010\u001f\u001a\u00020 H\u00d6\u0001R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\b\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\rR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014\u00a8\u0006!"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/OperationalCircuitBreaker$EngineCircuit;", "", "engine", "Lapp/callcheck/mobile/core/model/SearchEngine;", "state", "Lapp/callcheck/mobile/feature/callintercept/OperationalCircuitBreaker$CircuitState;", "consecutiveFailures", "", "consecutiveSuccesses", "lastStateChangeMs", "", "(Lapp/callcheck/mobile/core/model/SearchEngine;Lapp/callcheck/mobile/feature/callintercept/OperationalCircuitBreaker$CircuitState;IIJ)V", "getConsecutiveFailures", "()I", "getConsecutiveSuccesses", "getEngine", "()Lapp/callcheck/mobile/core/model/SearchEngine;", "getLastStateChangeMs", "()J", "getState", "()Lapp/callcheck/mobile/feature/callintercept/OperationalCircuitBreaker$CircuitState;", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "", "other", "hashCode", "toString", "", "call-intercept_debug"})
    public static final class EngineCircuit {
        @org.jetbrains.annotations.NotNull()
        private final app.callcheck.mobile.core.model.SearchEngine engine = null;
        @org.jetbrains.annotations.NotNull()
        private final app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.CircuitState state = null;
        private final int consecutiveFailures = 0;
        private final int consecutiveSuccesses = 0;
        private final long lastStateChangeMs = 0L;
        
        public EngineCircuit(@org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.core.model.SearchEngine engine, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.CircuitState state, int consecutiveFailures, int consecutiveSuccesses, long lastStateChangeMs) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.core.model.SearchEngine getEngine() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.CircuitState getState() {
            return null;
        }
        
        public final int getConsecutiveFailures() {
            return 0;
        }
        
        public final int getConsecutiveSuccesses() {
            return 0;
        }
        
        public final long getLastStateChangeMs() {
            return 0L;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.core.model.SearchEngine component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.CircuitState component2() {
            return null;
        }
        
        public final int component3() {
            return 0;
        }
        
        public final int component4() {
            return 0;
        }
        
        public final long component5() {
            return 0L;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.EngineCircuit copy(@org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.core.model.SearchEngine engine, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.CircuitState state, int consecutiveFailures, int consecutiveSuccesses, long lastStateChangeMs) {
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
     * 운영 모드
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\u000e\n\u0002\b\b\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u000f\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\n\u00a8\u0006\u000b"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/OperationalCircuitBreaker$OperationMode;", "", "label", "", "(Ljava/lang/String;ILjava/lang/String;)V", "getLabel", "()Ljava/lang/String;", "NORMAL", "CAUTIOUS", "FALLBACK", "EMERGENCY", "call-intercept_debug"})
    public static enum OperationMode {
        /*public static final*/ NORMAL /* = new NORMAL(null) */,
        /*public static final*/ CAUTIOUS /* = new CAUTIOUS(null) */,
        /*public static final*/ FALLBACK /* = new FALLBACK(null) */,
        /*public static final*/ EMERGENCY /* = new EMERGENCY(null) */;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String label = null;
        
        OperationMode(java.lang.String label) {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getLabel() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public static kotlin.enums.EnumEntries<app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.OperationMode> getEntries() {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B%\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\b\u0010\u0007\u001a\u0004\u0018\u00010\b\u00a2\u0006\u0002\u0010\tJ\u000f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J\t\u0010\u0011\u001a\u00020\u0006H\u00c6\u0003J\u000b\u0010\u0012\u001a\u0004\u0018\u00010\bH\u00c6\u0003J/\u0010\u0013\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\bH\u00c6\u0001J\u0013\u0010\u0014\u001a\u00020\u00152\b\u0010\u0016\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0017\u001a\u00020\u0018H\u00d6\u0001J\t\u0010\u0019\u001a\u00020\bH\u00d6\u0001R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0013\u0010\u0007\u001a\u0004\u0018\u00010\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f\u00a8\u0006\u001a"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/OperationalCircuitBreaker$RoutingDecision;", "", "effectiveEngines", "", "Lapp/callcheck/mobile/core/model/SearchEngine;", "mode", "Lapp/callcheck/mobile/feature/callintercept/OperationalCircuitBreaker$OperationMode;", "safeExpression", "", "(Ljava/util/List;Lapp/callcheck/mobile/feature/callintercept/OperationalCircuitBreaker$OperationMode;Ljava/lang/String;)V", "getEffectiveEngines", "()Ljava/util/List;", "getMode", "()Lapp/callcheck/mobile/feature/callintercept/OperationalCircuitBreaker$OperationMode;", "getSafeExpression", "()Ljava/lang/String;", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "", "toString", "call-intercept_debug"})
    public static final class RoutingDecision {
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<app.callcheck.mobile.core.model.SearchEngine> effectiveEngines = null;
        @org.jetbrains.annotations.NotNull()
        private final app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.OperationMode mode = null;
        
        /**
         * EMERGENCY 모드에서 즉시 표시할 안전 표현
         */
        @org.jetbrains.annotations.Nullable()
        private final java.lang.String safeExpression = null;
        
        public RoutingDecision(@org.jetbrains.annotations.NotNull()
        java.util.List<? extends app.callcheck.mobile.core.model.SearchEngine> effectiveEngines, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.OperationMode mode, @org.jetbrains.annotations.Nullable()
        java.lang.String safeExpression) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<app.callcheck.mobile.core.model.SearchEngine> getEffectiveEngines() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.OperationMode getMode() {
            return null;
        }
        
        /**
         * EMERGENCY 모드에서 즉시 표시할 안전 표현
         */
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String getSafeExpression() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<app.callcheck.mobile.core.model.SearchEngine> component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.OperationMode component2() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String component3() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.RoutingDecision copy(@org.jetbrains.annotations.NotNull()
        java.util.List<? extends app.callcheck.mobile.core.model.SearchEngine> effectiveEngines, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker.OperationMode mode, @org.jetbrains.annotations.Nullable()
        java.lang.String safeExpression) {
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