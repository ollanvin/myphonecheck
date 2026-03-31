package app.callcheck.mobile.feature.callintercept;

/**
 * Stage 15 — 실기기 계측 프레임워크.
 *
 * 자비스 기준:
 * "다음 7개 지표 강제 수집 + 수치 고정.
 * 디바이스 최소 3종 이상. 저사양/중간/고사양 포함.
 * 결과는 평균 + P95 + 최악값으로 기록"
 *
 * 7개 필수 메트릭:
 *  1. Phase 1 latency (목표 ≤ 50ms)
 *  2. Phase 2 latency (목표 ≤ 2000ms)
 *  3. Memory peak (KB)
 *  4. Battery drain per call (μAh)
 *  5. Cold start time (ms)
 *  6. Search success rate (%)
 *  7. CircuitBreaker trigger count
 *
 * 디바이스 3종 프로파일:
 *  LOW    — RAM ≤ 3GB, 2 cores
 *  MEDIUM — RAM 4~6GB, 4 cores
 *  HIGH   — RAM ≥ 8GB, 8 cores
 *
 * 100% 온디바이스. 서버 전송 없음.
 */
@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u008c\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u000b\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\b\u0007\u0018\u00002\u00020\u0001:\u0006>?@ABCB\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u001e\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\b2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\t0\u000eH\u0002J\u000e\u0010\u000f\u001a\u00020\b2\u0006\u0010\u0010\u001a\u00020\u0011J\u000e\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0013J\u000e\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0018J\u0006\u0010\u0019\u001a\u00020\u0018J\u0010\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u000bH\u0002J\u001a\u0010\u001d\u001a\u00020\u00132\f\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020 0\u001fH\u0086\b\u00f8\u0001\u0000J\u0006\u0010!\u001a\u00020\u0013J,\u0010\"\u001a\u000e\u0012\u0004\u0012\u0002H#\u0012\u0004\u0012\u00020\u00130\u0007\"\u0004\b\u0000\u0010#2\f\u0010\u001e\u001a\b\u0012\u0004\u0012\u0002H#0\u001fH\u0086\b\u00f8\u0001\u0000J,\u0010$\u001a\u000e\u0012\u0004\u0012\u0002H#\u0012\u0004\u0012\u00020\u00130\u0007\"\u0004\b\u0000\u0010#2\f\u0010\u001e\u001a\b\u0012\u0004\u0012\u0002H#0\u001fH\u0086\b\u00f8\u0001\u0000J\u0016\u0010%\u001a\u00020\u00132\f\u0010&\u001a\b\u0012\u0004\u0012\u00020\u00130\u000eH\u0002J\u0016\u0010\'\u001a\u00020 2\u0006\u0010\f\u001a\u00020\b2\u0006\u0010\r\u001a\u00020\tJ\u0006\u0010(\u001a\u00020 JT\u0010)\u001a\u00020\u00182\f\u0010*\u001a\b\u0012\u0004\u0012\u00020\u00160\u000e2\u0006\u0010+\u001a\u00020,26\u0010-\u001a2\u0012\u0013\u0012\u00110\u0016\u00a2\u0006\f\b/\u0012\b\b0\u0012\u0004\b\b(1\u0012\u0013\u0012\u00110,\u00a2\u0006\f\b/\u0012\b\b0\u0012\u0004\b\b(2\u0012\u0004\u0012\u0002030.J>\u00104\u001a\u00020\t2\u0006\u00101\u001a\u00020\u00162\u0006\u0010\f\u001a\u00020\b2\u0006\u00105\u001a\u00020\u00132\u0006\u00106\u001a\u00020\u00132\u0006\u00107\u001a\u00020\u00132\u0006\u00108\u001a\u0002092\u0006\u0010:\u001a\u00020;J\u0010\u0010<\u001a\u00020\u00162\u0006\u0010=\u001a\u000209H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R \u0010\u0005\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\t0\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u0007\n\u0005\b\u009920\u0001\u00a8\u0006D"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/DeviceMetricsCollector;", "", "circuitBreaker", "Lapp/callcheck/mobile/feature/callintercept/OperationalCircuitBreaker;", "(Lapp/callcheck/mobile/feature/callintercept/OperationalCircuitBreaker;)V", "metricsStore", "", "Lkotlin/Pair;", "Lapp/callcheck/mobile/feature/callintercept/DeviceMetricsCollector$DeviceProfile;", "Lapp/callcheck/mobile/feature/callintercept/DeviceMetricsCollector$CallMetrics;", "aggregateMetrics", "Lapp/callcheck/mobile/feature/callintercept/DeviceMetricsCollector$DeviceAggregation;", "profile", "metrics", "", "detectDeviceProfile", "context", "Landroid/content/Context;", "estimateBatteryDrain", "", "activeTimeMs", "formatReport", "", "report", "Lapp/callcheck/mobile/feature/callintercept/DeviceMetricsCollector$MetricsReport;", "generateReport", "judgePass", "Lapp/callcheck/mobile/feature/callintercept/DeviceMetricsCollector$PassResult;", "agg", "measureColdStart", "block", "Lkotlin/Function0;", "", "measureMemoryPeakKB", "measurePhase1", "T", "measurePhase2", "percentile95", "sorted", "record", "reset", "runFullSimulation", "countryCodes", "callsPerCountryPerDevice", "", "getBaseMetrics", "Lkotlin/Function2;", "Lkotlin/ParameterName;", "name", "countryCode", "callIndex", "Lapp/callcheck/mobile/feature/callintercept/DeviceMetricsCollector$SimulationInput;", "simulateDeviceMetrics", "basePhase1Ms", "basePhase2Ms", "baseMemoryKB", "searchSucceeded", "", "engineUsed", "Lapp/callcheck/mobile/core/model/SearchEngine;", "statusMark", "pass", "CallMetrics", "DeviceAggregation", "DeviceProfile", "MetricsReport", "PassResult", "SimulationInput", "call-intercept_debug"})
public final class DeviceMetricsCollector {
    @org.jetbrains.annotations.NotNull()
    private final app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker circuitBreaker = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<kotlin.Pair<app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector.DeviceProfile, app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector.CallMetrics>> metricsStore = null;
    
    @javax.inject.Inject()
    public DeviceMetricsCollector(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker circuitBreaker) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector.DeviceProfile detectDeviceProfile(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
    
    /**
     * Phase 1 실행 + 레이턴시 측정.
     * 외부에서 actual Phase 1 logic을 람다로 전달.
     */
    @org.jetbrains.annotations.NotNull()
    public final <T extends java.lang.Object>kotlin.Pair<T, java.lang.Long> measurePhase1(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<? extends T> block) {
        return null;
    }
    
    /**
     * Phase 2 실행 + 레이턴시 측정.
     */
    @org.jetbrains.annotations.NotNull()
    public final <T extends java.lang.Object>kotlin.Pair<T, java.lang.Long> measurePhase2(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<? extends T> block) {
        return null;
    }
    
    public final long measureMemoryPeakKB() {
        return 0L;
    }
    
    /**
     * 단일 콜 배터리 소모 추정 (μAh).
     * CPU active time 기반 추정. 실제 PowerProfile 접근 불가 시 CPU 시간 기반 근사.
     *
     * 빅테크 방식: PowerStats API 미사용 환경에서는
     * CPU uptime × average current draw 로 추정.
     *
     * 기본 가정: Active CPU = ~150mA average, standby = ~5mA
     */
    public final long estimateBatteryDrain(long activeTimeMs) {
        return 0L;
    }
    
    /**
     * 서비스 초기화 시간 측정.
     */
    public final long measureColdStart(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> block) {
        return 0L;
    }
    
    /**
     * 단일 콜 계측 결과 저장.
     */
    @kotlin.jvm.Synchronized()
    public final synchronized void record(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector.DeviceProfile profile, @org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector.CallMetrics metrics) {
    }
    
    /**
     * 전체 기록 초기화 (새 세션 시작 시).
     */
    @kotlin.jvm.Synchronized()
    public final synchronized void reset() {
    }
    
    /**
     * 전체 계측 보고서 생성.
     *
     * 자비스 기준: "결과는 평균 + P95 + 최악값으로 기록"
     */
    @kotlin.jvm.Synchronized()
    @org.jetbrains.annotations.NotNull()
    public final synchronized app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector.MetricsReport generateReport() {
        return null;
    }
    
    /**
     * 디바이스 프로파일별 시뮬레이션 계측.
     * 빅테크 방식: 실기기 배포 전 deterministic simulation으로 기준선 확보.
     *
     * 시뮬레이션 파라미터:
     *  LOW    — Phase1 +30%, Phase2 +40%, Memory +50%
     *  MEDIUM — Phase1 +0%,  Phase2 +0%,  Memory +0% (baseline)
     *  HIGH   — Phase1 -20%, Phase2 -15%, Memory -20%
     */
    @org.jetbrains.annotations.NotNull()
    public final app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector.CallMetrics simulateDeviceMetrics(@org.jetbrains.annotations.NotNull()
    java.lang.String countryCode, @org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector.DeviceProfile profile, long basePhase1Ms, long basePhase2Ms, long baseMemoryKB, boolean searchSucceeded, @org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.core.model.SearchEngine engineUsed) {
        return null;
    }
    
    /**
     * 3종 디바이스 전체 시뮬레이션 보고서 생성.
     *
     * @param countryCodes 계측 대상 국가 목록
     * @param callsPerCountryPerDevice 디바이스/국가당 콜 수
     * @param getBaseMetrics 국가별 기준 메트릭 제공 함수
     */
    @org.jetbrains.annotations.NotNull()
    public final app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector.MetricsReport runFullSimulation(@org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> countryCodes, int callsPerCountryPerDevice, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function2<? super java.lang.String, ? super java.lang.Integer, app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector.SimulationInput> getBaseMetrics) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String formatReport(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector.MetricsReport report) {
        return null;
    }
    
    private final app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector.DeviceAggregation aggregateMetrics(app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector.DeviceProfile profile, java.util.List<app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector.CallMetrics> metrics) {
        return null;
    }
    
    private final long percentile95(java.util.List<java.lang.Long> sorted) {
        return 0L;
    }
    
    private final app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector.PassResult judgePass(app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector.DeviceAggregation agg) {
        return null;
    }
    
    private final java.lang.String statusMark(boolean pass) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u001e\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001BW\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\u0005\u0012\u0006\u0010\b\u001a\u00020\u0005\u0012\u0006\u0010\t\u001a\u00020\u0005\u0012\u0006\u0010\n\u001a\u00020\u000b\u0012\u0006\u0010\f\u001a\u00020\u000b\u0012\u0006\u0010\r\u001a\u00020\u000e\u0012\b\b\u0002\u0010\u000f\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0010J\t\u0010\u001f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010 \u001a\u00020\u0005H\u00c6\u0003J\t\u0010!\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\"\u001a\u00020\u0005H\u00c6\u0003J\t\u0010#\u001a\u00020\u0005H\u00c6\u0003J\t\u0010$\u001a\u00020\u0005H\u00c6\u0003J\t\u0010%\u001a\u00020\u0005H\u00c6\u0003J\t\u0010&\u001a\u00020\u000bH\u00c6\u0003J\t\u0010\'\u001a\u00020\u000bH\u00c6\u0003J\t\u0010(\u001a\u00020\u000eH\u00c6\u0003Jm\u0010)\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\u00052\b\b\u0002\u0010\b\u001a\u00020\u00052\b\b\u0002\u0010\t\u001a\u00020\u00052\b\b\u0002\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\u000b2\b\b\u0002\u0010\r\u001a\u00020\u000e2\b\b\u0002\u0010\u000f\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010*\u001a\u00020\u000b2\b\u0010+\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010,\u001a\u00020-H\u00d6\u0001J\t\u0010.\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\b\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\f\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\t\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0012R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0011\u0010\r\u001a\u00020\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019R\u0011\u0010\u0007\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0012R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0012R\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u0012R\u0011\u0010\n\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u0014R\u0011\u0010\u000f\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u0012\u00a8\u0006/"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/DeviceMetricsCollector$CallMetrics;", "", "countryCode", "", "phase1LatencyMs", "", "phase2LatencyMs", "memoryPeakKB", "batteryDrainMicroAh", "coldStartTimeMs", "searchSucceeded", "", "circuitBreakerTriggered", "engineUsed", "Lapp/callcheck/mobile/core/model/SearchEngine;", "timestampMs", "(Ljava/lang/String;JJJJJZZLapp/callcheck/mobile/core/model/SearchEngine;J)V", "getBatteryDrainMicroAh", "()J", "getCircuitBreakerTriggered", "()Z", "getColdStartTimeMs", "getCountryCode", "()Ljava/lang/String;", "getEngineUsed", "()Lapp/callcheck/mobile/core/model/SearchEngine;", "getMemoryPeakKB", "getPhase1LatencyMs", "getPhase2LatencyMs", "getSearchSucceeded", "getTimestampMs", "component1", "component10", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "other", "hashCode", "", "toString", "call-intercept_debug"})
    public static final class CallMetrics {
        
        /**
         * 국가 코드
         */
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String countryCode = null;
        
        /**
         * Phase 1 레이턴시 (번호 정규화 + 국가 라우팅)
         */
        private final long phase1LatencyMs = 0L;
        
        /**
         * Phase 2 레이턴시 (검색 실행 + 판정 완료까지)
         */
        private final long phase2LatencyMs = 0L;
        
        /**
         * 메모리 피크 (KB)
         */
        private final long memoryPeakKB = 0L;
        
        /**
         * 배터리 소모 추정 (μAh)
         */
        private final long batteryDrainMicroAh = 0L;
        
        /**
         * 콜드 스타트 시간 (ms) — 서비스 초기화 ~ 첫 응답
         */
        private final long coldStartTimeMs = 0L;
        
        /**
         * 검색 성공 여부
         */
        private final boolean searchSucceeded = false;
        
        /**
         * 서킷 브레이커 트리거 여부
         */
        private final boolean circuitBreakerTriggered = false;
        
        /**
         * 사용된 검색엔진
         */
        @org.jetbrains.annotations.NotNull()
        private final app.callcheck.mobile.core.model.SearchEngine engineUsed = null;
        
        /**
         * 측정 타임스탬프
         */
        private final long timestampMs = 0L;
        
        public CallMetrics(@org.jetbrains.annotations.NotNull()
        java.lang.String countryCode, long phase1LatencyMs, long phase2LatencyMs, long memoryPeakKB, long batteryDrainMicroAh, long coldStartTimeMs, boolean searchSucceeded, boolean circuitBreakerTriggered, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.core.model.SearchEngine engineUsed, long timestampMs) {
            super();
        }
        
        /**
         * 국가 코드
         */
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getCountryCode() {
            return null;
        }
        
        /**
         * Phase 1 레이턴시 (번호 정규화 + 국가 라우팅)
         */
        public final long getPhase1LatencyMs() {
            return 0L;
        }
        
        /**
         * Phase 2 레이턴시 (검색 실행 + 판정 완료까지)
         */
        public final long getPhase2LatencyMs() {
            return 0L;
        }
        
        /**
         * 메모리 피크 (KB)
         */
        public final long getMemoryPeakKB() {
            return 0L;
        }
        
        /**
         * 배터리 소모 추정 (μAh)
         */
        public final long getBatteryDrainMicroAh() {
            return 0L;
        }
        
        /**
         * 콜드 스타트 시간 (ms) — 서비스 초기화 ~ 첫 응답
         */
        public final long getColdStartTimeMs() {
            return 0L;
        }
        
        /**
         * 검색 성공 여부
         */
        public final boolean getSearchSucceeded() {
            return false;
        }
        
        /**
         * 서킷 브레이커 트리거 여부
         */
        public final boolean getCircuitBreakerTriggered() {
            return false;
        }
        
        /**
         * 사용된 검색엔진
         */
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.core.model.SearchEngine getEngineUsed() {
            return null;
        }
        
        /**
         * 측정 타임스탬프
         */
        public final long getTimestampMs() {
            return 0L;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component1() {
            return null;
        }
        
        public final long component10() {
            return 0L;
        }
        
        public final long component2() {
            return 0L;
        }
        
        public final long component3() {
            return 0L;
        }
        
        public final long component4() {
            return 0L;
        }
        
        public final long component5() {
            return 0L;
        }
        
        public final long component6() {
            return 0L;
        }
        
        public final boolean component7() {
            return false;
        }
        
        public final boolean component8() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.core.model.SearchEngine component9() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector.CallMetrics copy(@org.jetbrains.annotations.NotNull()
        java.lang.String countryCode, long phase1LatencyMs, long phase2LatencyMs, long memoryPeakKB, long batteryDrainMicroAh, long coldStartTimeMs, boolean searchSucceeded, boolean circuitBreakerTriggered, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.core.model.SearchEngine engineUsed, long timestampMs) {
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\t\n\u0002\b\u000e\n\u0002\u0010\u0007\n\u0002\b/\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u009d\u0001\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\t\u0012\u0006\u0010\u000b\u001a\u00020\u0007\u0012\u0006\u0010\f\u001a\u00020\t\u0012\u0006\u0010\r\u001a\u00020\t\u0012\u0006\u0010\u000e\u001a\u00020\u0007\u0012\u0006\u0010\u000f\u001a\u00020\t\u0012\u0006\u0010\u0010\u001a\u00020\t\u0012\u0006\u0010\u0011\u001a\u00020\u0007\u0012\u0006\u0010\u0012\u001a\u00020\t\u0012\u0006\u0010\u0013\u001a\u00020\t\u0012\u0006\u0010\u0014\u001a\u00020\u0007\u0012\u0006\u0010\u0015\u001a\u00020\t\u0012\u0006\u0010\u0016\u001a\u00020\t\u0012\u0006\u0010\u0017\u001a\u00020\u0018\u0012\u0006\u0010\u0019\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u001aJ\t\u00103\u001a\u00020\u0003H\u00c6\u0003J\t\u00104\u001a\u00020\tH\u00c6\u0003J\t\u00105\u001a\u00020\tH\u00c6\u0003J\t\u00106\u001a\u00020\u0007H\u00c6\u0003J\t\u00107\u001a\u00020\tH\u00c6\u0003J\t\u00108\u001a\u00020\tH\u00c6\u0003J\t\u00109\u001a\u00020\u0007H\u00c6\u0003J\t\u0010:\u001a\u00020\tH\u00c6\u0003J\t\u0010;\u001a\u00020\tH\u00c6\u0003J\t\u0010<\u001a\u00020\u0018H\u00c6\u0003J\t\u0010=\u001a\u00020\u0005H\u00c6\u0003J\t\u0010>\u001a\u00020\u0005H\u00c6\u0003J\t\u0010?\u001a\u00020\u0007H\u00c6\u0003J\t\u0010@\u001a\u00020\tH\u00c6\u0003J\t\u0010A\u001a\u00020\tH\u00c6\u0003J\t\u0010B\u001a\u00020\u0007H\u00c6\u0003J\t\u0010C\u001a\u00020\tH\u00c6\u0003J\t\u0010D\u001a\u00020\tH\u00c6\u0003J\t\u0010E\u001a\u00020\u0007H\u00c6\u0003J\u00c7\u0001\u0010F\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\t2\b\b\u0002\u0010\u000b\u001a\u00020\u00072\b\b\u0002\u0010\f\u001a\u00020\t2\b\b\u0002\u0010\r\u001a\u00020\t2\b\b\u0002\u0010\u000e\u001a\u00020\u00072\b\b\u0002\u0010\u000f\u001a\u00020\t2\b\b\u0002\u0010\u0010\u001a\u00020\t2\b\b\u0002\u0010\u0011\u001a\u00020\u00072\b\b\u0002\u0010\u0012\u001a\u00020\t2\b\b\u0002\u0010\u0013\u001a\u00020\t2\b\b\u0002\u0010\u0014\u001a\u00020\u00072\b\b\u0002\u0010\u0015\u001a\u00020\t2\b\b\u0002\u0010\u0016\u001a\u00020\t2\b\b\u0002\u0010\u0017\u001a\u00020\u00182\b\b\u0002\u0010\u0019\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010G\u001a\u00020H2\b\u0010I\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010J\u001a\u00020\u0005H\u00d6\u0001J\t\u0010K\u001a\u00020LH\u00d6\u0001R\u0011\u0010\u0011\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001cR\u0011\u0010\u0013\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001eR\u0011\u0010\u0012\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u001eR\u0011\u0010\u0019\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010!R\u0011\u0010\u0014\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010\u001cR\u0011\u0010\u0016\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010\u001eR\u0011\u0010\u0015\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010\u001eR\u0011\u0010\u000e\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b%\u0010\u001cR\u0011\u0010\u0010\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b&\u0010\u001eR\u0011\u0010\u000f\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\'\u0010\u001eR\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b(\u0010\u001cR\u0011\u0010\n\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b)\u0010\u001eR\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b*\u0010\u001eR\u0011\u0010\u000b\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b+\u0010\u001cR\u0011\u0010\r\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b,\u0010\u001eR\u0011\u0010\f\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b-\u0010\u001eR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b.\u0010/R\u0011\u0010\u0017\u001a\u00020\u0018\u00a2\u0006\b\n\u0000\u001a\u0004\b0\u00101R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b2\u0010!\u00a8\u0006M"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/DeviceMetricsCollector$DeviceAggregation;", "", "profile", "Lapp/callcheck/mobile/feature/callintercept/DeviceMetricsCollector$DeviceProfile;", "totalCalls", "", "phase1Avg", "", "phase1P95", "", "phase1Max", "phase2Avg", "phase2P95", "phase2Max", "memoryPeakAvg", "memoryPeakP95", "memoryPeakMax", "batteryDrainAvg", "batteryDrainP95", "batteryDrainMax", "coldStartAvg", "coldStartP95", "coldStartMax", "searchSuccessRate", "", "circuitBreakerTriggerCount", "(Lapp/callcheck/mobile/feature/callintercept/DeviceMetricsCollector$DeviceProfile;IDJJDJJDJJDJJDJJFI)V", "getBatteryDrainAvg", "()D", "getBatteryDrainMax", "()J", "getBatteryDrainP95", "getCircuitBreakerTriggerCount", "()I", "getColdStartAvg", "getColdStartMax", "getColdStartP95", "getMemoryPeakAvg", "getMemoryPeakMax", "getMemoryPeakP95", "getPhase1Avg", "getPhase1Max", "getPhase1P95", "getPhase2Avg", "getPhase2Max", "getPhase2P95", "getProfile", "()Lapp/callcheck/mobile/feature/callintercept/DeviceMetricsCollector$DeviceProfile;", "getSearchSuccessRate", "()F", "getTotalCalls", "component1", "component10", "component11", "component12", "component13", "component14", "component15", "component16", "component17", "component18", "component19", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "", "other", "hashCode", "toString", "", "call-intercept_debug"})
    public static final class DeviceAggregation {
        @org.jetbrains.annotations.NotNull()
        private final app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector.DeviceProfile profile = null;
        private final int totalCalls = 0;
        private final double phase1Avg = 0.0;
        private final long phase1P95 = 0L;
        private final long phase1Max = 0L;
        private final double phase2Avg = 0.0;
        private final long phase2P95 = 0L;
        private final long phase2Max = 0L;
        private final double memoryPeakAvg = 0.0;
        private final long memoryPeakP95 = 0L;
        private final long memoryPeakMax = 0L;
        private final double batteryDrainAvg = 0.0;
        private final long batteryDrainP95 = 0L;
        private final long batteryDrainMax = 0L;
        private final double coldStartAvg = 0.0;
        private final long coldStartP95 = 0L;
        private final long coldStartMax = 0L;
        private final float searchSuccessRate = 0.0F;
        private final int circuitBreakerTriggerCount = 0;
        
        public DeviceAggregation(@org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector.DeviceProfile profile, int totalCalls, double phase1Avg, long phase1P95, long phase1Max, double phase2Avg, long phase2P95, long phase2Max, double memoryPeakAvg, long memoryPeakP95, long memoryPeakMax, double batteryDrainAvg, long batteryDrainP95, long batteryDrainMax, double coldStartAvg, long coldStartP95, long coldStartMax, float searchSuccessRate, int circuitBreakerTriggerCount) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector.DeviceProfile getProfile() {
            return null;
        }
        
        public final int getTotalCalls() {
            return 0;
        }
        
        public final double getPhase1Avg() {
            return 0.0;
        }
        
        public final long getPhase1P95() {
            return 0L;
        }
        
        public final long getPhase1Max() {
            return 0L;
        }
        
        public final double getPhase2Avg() {
            return 0.0;
        }
        
        public final long getPhase2P95() {
            return 0L;
        }
        
        public final long getPhase2Max() {
            return 0L;
        }
        
        public final double getMemoryPeakAvg() {
            return 0.0;
        }
        
        public final long getMemoryPeakP95() {
            return 0L;
        }
        
        public final long getMemoryPeakMax() {
            return 0L;
        }
        
        public final double getBatteryDrainAvg() {
            return 0.0;
        }
        
        public final long getBatteryDrainP95() {
            return 0L;
        }
        
        public final long getBatteryDrainMax() {
            return 0L;
        }
        
        public final double getColdStartAvg() {
            return 0.0;
        }
        
        public final long getColdStartP95() {
            return 0L;
        }
        
        public final long getColdStartMax() {
            return 0L;
        }
        
        public final float getSearchSuccessRate() {
            return 0.0F;
        }
        
        public final int getCircuitBreakerTriggerCount() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector.DeviceProfile component1() {
            return null;
        }
        
        public final long component10() {
            return 0L;
        }
        
        public final long component11() {
            return 0L;
        }
        
        public final double component12() {
            return 0.0;
        }
        
        public final long component13() {
            return 0L;
        }
        
        public final long component14() {
            return 0L;
        }
        
        public final double component15() {
            return 0.0;
        }
        
        public final long component16() {
            return 0L;
        }
        
        public final long component17() {
            return 0L;
        }
        
        public final float component18() {
            return 0.0F;
        }
        
        public final int component19() {
            return 0;
        }
        
        public final int component2() {
            return 0;
        }
        
        public final double component3() {
            return 0.0;
        }
        
        public final long component4() {
            return 0L;
        }
        
        public final long component5() {
            return 0L;
        }
        
        public final double component6() {
            return 0.0;
        }
        
        public final long component7() {
            return 0L;
        }
        
        public final long component8() {
            return 0L;
        }
        
        public final double component9() {
            return 0.0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector.DeviceAggregation copy(@org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector.DeviceProfile profile, int totalCalls, double phase1Avg, long phase1P95, long phase1Max, double phase2Avg, long phase2P95, long phase2Max, double memoryPeakAvg, long memoryPeakP95, long memoryPeakMax, double batteryDrainAvg, long batteryDrainP95, long batteryDrainMax, double coldStartAvg, long coldStartP95, long coldStartMax, float searchSuccessRate, int circuitBreakerTriggerCount) {
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\b\n\u0002\b\u000b\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u001f\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bR\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000ej\u0002\b\u000fj\u0002\b\u0010j\u0002\b\u0011\u00a8\u0006\u0012"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/DeviceMetricsCollector$DeviceProfile;", "", "label", "", "ramLimitMB", "", "coreLimit", "", "(Ljava/lang/String;ILjava/lang/String;JI)V", "getCoreLimit", "()I", "getLabel", "()Ljava/lang/String;", "getRamLimitMB", "()J", "LOW", "MEDIUM", "HIGH", "call-intercept_debug"})
    public static enum DeviceProfile {
        /*public static final*/ LOW /* = new LOW(null, 0L, 0) */,
        /*public static final*/ MEDIUM /* = new MEDIUM(null, 0L, 0) */,
        /*public static final*/ HIGH /* = new HIGH(null, 0L, 0) */;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String label = null;
        private final long ramLimitMB = 0L;
        private final int coreLimit = 0;
        
        DeviceProfile(java.lang.String label, long ramLimitMB, int coreLimit) {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getLabel() {
            return null;
        }
        
        public final long getRamLimitMB() {
            return 0L;
        }
        
        public final int getCoreLimit() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public static kotlin.enums.EnumEntries<app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector.DeviceProfile> getEntries() {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0012\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001BA\u0012\u0012\u0010\u0002\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00050\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b\u0012\u0006\u0010\n\u001a\u00020\u000b\u0012\b\b\u0002\u0010\f\u001a\u00020\r\u00a2\u0006\u0002\u0010\u000eJ\u0015\u0010\u0019\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00050\u0003H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\u0005H\u00c6\u0003J\u000f\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\t0\bH\u00c6\u0003J\t\u0010\u001c\u001a\u00020\u000bH\u00c6\u0003J\t\u0010\u001d\u001a\u00020\rH\u00c6\u0003JM\u0010\u001e\u001a\u00020\u00002\u0014\b\u0002\u0010\u0002\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00050\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00052\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b2\b\b\u0002\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\rH\u00c6\u0001J\u0013\u0010\u001f\u001a\u00020 2\b\u0010!\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\"\u001a\u00020#H\u00d6\u0001J\t\u0010$\u001a\u00020%H\u00d6\u0001R\u001d\u0010\u0002\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00050\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\f\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\n\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0017\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018\u00a8\u0006&"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/DeviceMetricsCollector$MetricsReport;", "", "deviceAggregations", "", "Lapp/callcheck/mobile/feature/callintercept/DeviceMetricsCollector$DeviceProfile;", "Lapp/callcheck/mobile/feature/callintercept/DeviceMetricsCollector$DeviceAggregation;", "overallAggregation", "rawMetrics", "", "Lapp/callcheck/mobile/feature/callintercept/DeviceMetricsCollector$CallMetrics;", "passResult", "Lapp/callcheck/mobile/feature/callintercept/DeviceMetricsCollector$PassResult;", "generatedAt", "", "(Ljava/util/Map;Lapp/callcheck/mobile/feature/callintercept/DeviceMetricsCollector$DeviceAggregation;Ljava/util/List;Lapp/callcheck/mobile/feature/callintercept/DeviceMetricsCollector$PassResult;J)V", "getDeviceAggregations", "()Ljava/util/Map;", "getGeneratedAt", "()J", "getOverallAggregation", "()Lapp/callcheck/mobile/feature/callintercept/DeviceMetricsCollector$DeviceAggregation;", "getPassResult", "()Lapp/callcheck/mobile/feature/callintercept/DeviceMetricsCollector$PassResult;", "getRawMetrics", "()Ljava/util/List;", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "", "other", "hashCode", "", "toString", "", "call-intercept_debug"})
    public static final class MetricsReport {
        
        /**
         * 디바이스별 집계
         */
        @org.jetbrains.annotations.NotNull()
        private final java.util.Map<app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector.DeviceProfile, app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector.DeviceAggregation> deviceAggregations = null;
        
        /**
         * 전체 통합 집계
         */
        @org.jetbrains.annotations.NotNull()
        private final app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector.DeviceAggregation overallAggregation = null;
        
        /**
         * 개별 콜 메트릭 (전체)
         */
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector.CallMetrics> rawMetrics = null;
        
        /**
         * PASS / FAIL 판정
         */
        @org.jetbrains.annotations.NotNull()
        private final app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector.PassResult passResult = null;
        
        /**
         * 생성 시각
         */
        private final long generatedAt = 0L;
        
        public MetricsReport(@org.jetbrains.annotations.NotNull()
        java.util.Map<app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector.DeviceProfile, app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector.DeviceAggregation> deviceAggregations, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector.DeviceAggregation overallAggregation, @org.jetbrains.annotations.NotNull()
        java.util.List<app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector.CallMetrics> rawMetrics, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector.PassResult passResult, long generatedAt) {
            super();
        }
        
        /**
         * 디바이스별 집계
         */
        @org.jetbrains.annotations.NotNull()
        public final java.util.Map<app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector.DeviceProfile, app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector.DeviceAggregation> getDeviceAggregations() {
            return null;
        }
        
        /**
         * 전체 통합 집계
         */
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector.DeviceAggregation getOverallAggregation() {
            return null;
        }
        
        /**
         * 개별 콜 메트릭 (전체)
         */
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector.CallMetrics> getRawMetrics() {
            return null;
        }
        
        /**
         * PASS / FAIL 판정
         */
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector.PassResult getPassResult() {
            return null;
        }
        
        /**
         * 생성 시각
         */
        public final long getGeneratedAt() {
            return 0L;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Map<app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector.DeviceProfile, app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector.DeviceAggregation> component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector.DeviceAggregation component2() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector.CallMetrics> component3() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector.PassResult component4() {
            return null;
        }
        
        public final long component5() {
            return 0L;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector.MetricsReport copy(@org.jetbrains.annotations.NotNull()
        java.util.Map<app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector.DeviceProfile, app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector.DeviceAggregation> deviceAggregations, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector.DeviceAggregation overallAggregation, @org.jetbrains.annotations.NotNull()
        java.util.List<app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector.CallMetrics> rawMetrics, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector.PassResult passResult, long generatedAt) {
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u001c\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B=\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\u0003\u0012\u0006\u0010\b\u001a\u00020\u0003\u0012\u0006\u0010\t\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\nJ\t\u0010\u0015\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0016\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0017\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0019\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001b\u001a\u00020\u0003H\u00c6\u0003JO\u0010\u001c\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\u00032\b\b\u0002\u0010\t\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\u001d\u001a\u00020\u00032\b\u0010\u001e\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001f\u001a\u00020 H\u00d6\u0001J\t\u0010!\u001a\u00020\"H\u00d6\u0001R\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\t\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\fR\u0011\u0010\u0007\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\fR\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\fR\u0011\u0010\u0010\u001a\u00020\u00038F\u00a2\u0006\u0006\u001a\u0004\b\u0011\u0010\fR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\fR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\fR\u0011\u0010\b\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\f\u00a8\u0006#"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/DeviceMetricsCollector$PassResult;", "", "phase1Pass", "", "phase2Pass", "memoryPass", "batteryPass", "coldStartPass", "searchRatePass", "circuitPass", "(ZZZZZZZ)V", "getBatteryPass", "()Z", "getCircuitPass", "getColdStartPass", "getMemoryPass", "overallPass", "getOverallPass", "getPhase1Pass", "getPhase2Pass", "getSearchRatePass", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "copy", "equals", "other", "hashCode", "", "toString", "", "call-intercept_debug"})
    public static final class PassResult {
        private final boolean phase1Pass = false;
        private final boolean phase2Pass = false;
        private final boolean memoryPass = false;
        private final boolean batteryPass = false;
        private final boolean coldStartPass = false;
        private final boolean searchRatePass = false;
        private final boolean circuitPass = false;
        
        public PassResult(boolean phase1Pass, boolean phase2Pass, boolean memoryPass, boolean batteryPass, boolean coldStartPass, boolean searchRatePass, boolean circuitPass) {
            super();
        }
        
        public final boolean getPhase1Pass() {
            return false;
        }
        
        public final boolean getPhase2Pass() {
            return false;
        }
        
        public final boolean getMemoryPass() {
            return false;
        }
        
        public final boolean getBatteryPass() {
            return false;
        }
        
        public final boolean getColdStartPass() {
            return false;
        }
        
        public final boolean getSearchRatePass() {
            return false;
        }
        
        public final boolean getCircuitPass() {
            return false;
        }
        
        public final boolean getOverallPass() {
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
        
        public final boolean component5() {
            return false;
        }
        
        public final boolean component6() {
            return false;
        }
        
        public final boolean component7() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector.PassResult copy(boolean phase1Pass, boolean phase2Pass, boolean memoryPass, boolean batteryPass, boolean coldStartPass, boolean searchRatePass, boolean circuitPass) {
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0012\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B-\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u00a2\u0006\u0002\u0010\nJ\t\u0010\u0013\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0014\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0015\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0016\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u0017\u001a\u00020\tH\u00c6\u0003J;\u0010\u0018\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\tH\u00c6\u0001J\u0013\u0010\u0019\u001a\u00020\u00072\b\u0010\u001a\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001b\u001a\u00020\u001cH\u00d6\u0001J\t\u0010\u001d\u001a\u00020\u001eH\u00d6\u0001R\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u000eR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000eR\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012\u00a8\u0006\u001f"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/DeviceMetricsCollector$SimulationInput;", "", "phase1Ms", "", "phase2Ms", "memoryKB", "searchSucceeded", "", "engine", "Lapp/callcheck/mobile/core/model/SearchEngine;", "(JJJZLapp/callcheck/mobile/core/model/SearchEngine;)V", "getEngine", "()Lapp/callcheck/mobile/core/model/SearchEngine;", "getMemoryKB", "()J", "getPhase1Ms", "getPhase2Ms", "getSearchSucceeded", "()Z", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "other", "hashCode", "", "toString", "", "call-intercept_debug"})
    public static final class SimulationInput {
        private final long phase1Ms = 0L;
        private final long phase2Ms = 0L;
        private final long memoryKB = 0L;
        private final boolean searchSucceeded = false;
        @org.jetbrains.annotations.NotNull()
        private final app.callcheck.mobile.core.model.SearchEngine engine = null;
        
        public SimulationInput(long phase1Ms, long phase2Ms, long memoryKB, boolean searchSucceeded, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.core.model.SearchEngine engine) {
            super();
        }
        
        public final long getPhase1Ms() {
            return 0L;
        }
        
        public final long getPhase2Ms() {
            return 0L;
        }
        
        public final long getMemoryKB() {
            return 0L;
        }
        
        public final boolean getSearchSucceeded() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.core.model.SearchEngine getEngine() {
            return null;
        }
        
        public final long component1() {
            return 0L;
        }
        
        public final long component2() {
            return 0L;
        }
        
        public final long component3() {
            return 0L;
        }
        
        public final boolean component4() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.core.model.SearchEngine component5() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.DeviceMetricsCollector.SimulationInput copy(long phase1Ms, long phase2Ms, long memoryKB, boolean searchSucceeded, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.core.model.SearchEngine engine) {
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