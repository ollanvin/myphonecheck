package app.callcheck.mobile.feature.callintercept;

/**
 * SLA 스트레스 테스터.
 *
 * 자비스 기준: "1000 calls / 국가, 2초 SLA 통과율 계산"
 *
 * 빅테크 정석:
 * - 국가당 티어별 콜 수 (Tier1: 1000, Tier2: 500, Tier3: 200)
 * - 2초 SLA 통과율 = (hardDeadline 이내 완료 콜) / (전체 콜) × 100
 * - P50/P95/P99 지연시간 산출
 * - fallback 발생율 추적
 * - FAIL 국가 자동 식별 + 원인 분류
 *
 * 100% 온디바이스. 서버 전송 없음.
 */
@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0007\u0018\u0000 \u00152\u00020\u0001:\u0004\u0015\u0016\u0017\u0018B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u001e\u0010\u0007\u001a\u00020\b2\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\b0\n2\u0006\u0010\u000b\u001a\u00020\fH\u0002J\u0016\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0010H\u0086@\u00a2\u0006\u0002\u0010\u0011J\u000e\u0010\u0012\u001a\u00020\u0013H\u0086@\u00a2\u0006\u0002\u0010\u0014R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0019"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/SlaStressTester;", "", "simulationEngine", "Lapp/callcheck/mobile/feature/callintercept/CallSimulationEngine;", "registry", "Lapp/callcheck/mobile/feature/callintercept/GlobalSearchProviderRegistry;", "(Lapp/callcheck/mobile/feature/callintercept/CallSimulationEngine;Lapp/callcheck/mobile/feature/callintercept/GlobalSearchProviderRegistry;)V", "percentile", "", "sorted", "", "p", "", "runCountryStressTest", "Lapp/callcheck/mobile/feature/callintercept/SlaStressTester$CountryStressResult;", "countryCode", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "runFullStressTest", "Lapp/callcheck/mobile/feature/callintercept/SlaStressTester$StressTestReport;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "Companion", "CountryStressResult", "FailureBreakdown", "StressTestReport", "call-intercept_debug"})
public final class SlaStressTester {
    @org.jetbrains.annotations.NotNull()
    private final app.callcheck.mobile.feature.callintercept.CallSimulationEngine simulationEngine = null;
    @org.jetbrains.annotations.NotNull()
    private final app.callcheck.mobile.feature.callintercept.GlobalSearchProviderRegistry registry = null;
    
    /**
     * SLA 통과율 기준: 95% 이상이면 PASS
     */
    public static final float PASS_THRESHOLD = 95.0F;
    @org.jetbrains.annotations.NotNull()
    public static final app.callcheck.mobile.feature.callintercept.SlaStressTester.Companion Companion = null;
    
    @javax.inject.Inject()
    public SlaStressTester(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.callintercept.CallSimulationEngine simulationEngine, @org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.callintercept.GlobalSearchProviderRegistry registry) {
        super();
    }
    
    /**
     * 전체 국가 스트레스 테스트 실행.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object runFullStressTest(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super app.callcheck.mobile.feature.callintercept.SlaStressTester.StressTestReport> $completion) {
        return null;
    }
    
    /**
     * 단일 국가 스트레스 테스트.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object runCountryStressTest(@org.jetbrains.annotations.NotNull()
    java.lang.String countryCode, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super app.callcheck.mobile.feature.callintercept.SlaStressTester.CountryStressResult> $completion) {
        return null;
    }
    
    private final long percentile(java.util.List<java.lang.Long> sorted, int p) {
        return 0L;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/SlaStressTester$Companion;", "", "()V", "PASS_THRESHOLD", "", "call-intercept_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
    
    /**
     * 단일 국가 스트레스 테스트 결과.
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\t\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0010\u000b\n\u0002\b\u001f\b\u0086\b\u0018\u00002\u00020\u0001Bu\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\t\u0012\u0006\u0010\u000b\u001a\u00020\t\u0012\u0006\u0010\f\u001a\u00020\r\u0012\u0006\u0010\u000e\u001a\u00020\u000f\u0012\u0006\u0010\u0010\u001a\u00020\u000f\u0012\u0006\u0010\u0011\u001a\u00020\u000f\u0012\u0006\u0010\u0012\u001a\u00020\u000f\u0012\u0006\u0010\u0013\u001a\u00020\r\u0012\u0006\u0010\u0014\u001a\u00020\r\u0012\u0006\u0010\u0015\u001a\u00020\u0016\u00a2\u0006\u0002\u0010\u0017J\t\u00101\u001a\u00020\u0003H\u00c6\u0003J\t\u00102\u001a\u00020\u000fH\u00c6\u0003J\t\u00103\u001a\u00020\u000fH\u00c6\u0003J\t\u00104\u001a\u00020\rH\u00c6\u0003J\t\u00105\u001a\u00020\rH\u00c6\u0003J\t\u00106\u001a\u00020\u0016H\u00c6\u0003J\t\u00107\u001a\u00020\u0005H\u00c6\u0003J\t\u00108\u001a\u00020\u0007H\u00c6\u0003J\t\u00109\u001a\u00020\tH\u00c6\u0003J\t\u0010:\u001a\u00020\tH\u00c6\u0003J\t\u0010;\u001a\u00020\tH\u00c6\u0003J\t\u0010<\u001a\u00020\rH\u00c6\u0003J\t\u0010=\u001a\u00020\u000fH\u00c6\u0003J\t\u0010>\u001a\u00020\u000fH\u00c6\u0003J\u0095\u0001\u0010?\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\t2\b\b\u0002\u0010\u000b\u001a\u00020\t2\b\b\u0002\u0010\f\u001a\u00020\r2\b\b\u0002\u0010\u000e\u001a\u00020\u000f2\b\b\u0002\u0010\u0010\u001a\u00020\u000f2\b\b\u0002\u0010\u0011\u001a\u00020\u000f2\b\b\u0002\u0010\u0012\u001a\u00020\u000f2\b\b\u0002\u0010\u0013\u001a\u00020\r2\b\b\u0002\u0010\u0014\u001a\u00020\r2\b\b\u0002\u0010\u0015\u001a\u00020\u0016H\u00c6\u0001J\u0013\u0010@\u001a\u00020%2\b\u0010A\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010B\u001a\u00020\tH\u00d6\u0001J\t\u0010C\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0014\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001bR\u0011\u0010\u0015\u001a\u00020\u0016\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001dR\u0011\u0010\u0013\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u0019R\u0011\u0010\u0012\u001a\u00020\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010 R\u0011\u0010\u000e\u001a\u00020\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010 R\u0011\u0010\u0010\u001a\u00020\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010 R\u0011\u0010\u0011\u001a\u00020\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010 R\u0011\u0010$\u001a\u00020%8F\u00a2\u0006\u0006\u001a\u0004\b&\u0010\'R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b(\u0010)R\u0011\u0010\u000b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b*\u0010+R\u0011\u0010\n\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b,\u0010+R\u0011\u0010\f\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b-\u0010\u0019R\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b.\u0010+R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b/\u00100\u00a8\u0006D"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/SlaStressTester$CountryStressResult;", "", "countryCode", "", "validationTier", "Lapp/callcheck/mobile/feature/callintercept/CallSimulationEngine$ValidationTier;", "searchTier", "Lapp/callcheck/mobile/core/model/SearchTier;", "totalCalls", "", "slaPassCount", "slaFailCount", "slaPassRate", "", "p50LatencyMs", "", "p95LatencyMs", "p99LatencyMs", "maxLatencyMs", "fallbackRate", "correctClassificationRate", "failureBreakdown", "Lapp/callcheck/mobile/feature/callintercept/SlaStressTester$FailureBreakdown;", "(Ljava/lang/String;Lapp/callcheck/mobile/feature/callintercept/CallSimulationEngine$ValidationTier;Lapp/callcheck/mobile/core/model/SearchTier;IIIFJJJJFFLapp/callcheck/mobile/feature/callintercept/SlaStressTester$FailureBreakdown;)V", "getCorrectClassificationRate", "()F", "getCountryCode", "()Ljava/lang/String;", "getFailureBreakdown", "()Lapp/callcheck/mobile/feature/callintercept/SlaStressTester$FailureBreakdown;", "getFallbackRate", "getMaxLatencyMs", "()J", "getP50LatencyMs", "getP95LatencyMs", "getP99LatencyMs", "passed", "", "getPassed", "()Z", "getSearchTier", "()Lapp/callcheck/mobile/core/model/SearchTier;", "getSlaFailCount", "()I", "getSlaPassCount", "getSlaPassRate", "getTotalCalls", "getValidationTier", "()Lapp/callcheck/mobile/feature/callintercept/CallSimulationEngine$ValidationTier;", "component1", "component10", "component11", "component12", "component13", "component14", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "other", "hashCode", "toString", "call-intercept_debug"})
    public static final class CountryStressResult {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String countryCode = null;
        @org.jetbrains.annotations.NotNull()
        private final app.callcheck.mobile.feature.callintercept.CallSimulationEngine.ValidationTier validationTier = null;
        @org.jetbrains.annotations.NotNull()
        private final app.callcheck.mobile.core.model.SearchTier searchTier = null;
        private final int totalCalls = 0;
        private final int slaPassCount = 0;
        private final int slaFailCount = 0;
        private final float slaPassRate = 0.0F;
        private final long p50LatencyMs = 0L;
        private final long p95LatencyMs = 0L;
        private final long p99LatencyMs = 0L;
        private final long maxLatencyMs = 0L;
        private final float fallbackRate = 0.0F;
        private final float correctClassificationRate = 0.0F;
        @org.jetbrains.annotations.NotNull()
        private final app.callcheck.mobile.feature.callintercept.SlaStressTester.FailureBreakdown failureBreakdown = null;
        
        public CountryStressResult(@org.jetbrains.annotations.NotNull()
        java.lang.String countryCode, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.CallSimulationEngine.ValidationTier validationTier, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.core.model.SearchTier searchTier, int totalCalls, int slaPassCount, int slaFailCount, float slaPassRate, long p50LatencyMs, long p95LatencyMs, long p99LatencyMs, long maxLatencyMs, float fallbackRate, float correctClassificationRate, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.SlaStressTester.FailureBreakdown failureBreakdown) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getCountryCode() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.CallSimulationEngine.ValidationTier getValidationTier() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.core.model.SearchTier getSearchTier() {
            return null;
        }
        
        public final int getTotalCalls() {
            return 0;
        }
        
        public final int getSlaPassCount() {
            return 0;
        }
        
        public final int getSlaFailCount() {
            return 0;
        }
        
        public final float getSlaPassRate() {
            return 0.0F;
        }
        
        public final long getP50LatencyMs() {
            return 0L;
        }
        
        public final long getP95LatencyMs() {
            return 0L;
        }
        
        public final long getP99LatencyMs() {
            return 0L;
        }
        
        public final long getMaxLatencyMs() {
            return 0L;
        }
        
        public final float getFallbackRate() {
            return 0.0F;
        }
        
        public final float getCorrectClassificationRate() {
            return 0.0F;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.SlaStressTester.FailureBreakdown getFailureBreakdown() {
            return null;
        }
        
        public final boolean getPassed() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component1() {
            return null;
        }
        
        public final long component10() {
            return 0L;
        }
        
        public final long component11() {
            return 0L;
        }
        
        public final float component12() {
            return 0.0F;
        }
        
        public final float component13() {
            return 0.0F;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.SlaStressTester.FailureBreakdown component14() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.CallSimulationEngine.ValidationTier component2() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.core.model.SearchTier component3() {
            return null;
        }
        
        public final int component4() {
            return 0;
        }
        
        public final int component5() {
            return 0;
        }
        
        public final int component6() {
            return 0;
        }
        
        public final float component7() {
            return 0.0F;
        }
        
        public final long component8() {
            return 0L;
        }
        
        public final long component9() {
            return 0L;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.SlaStressTester.CountryStressResult copy(@org.jetbrains.annotations.NotNull()
        java.lang.String countryCode, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.CallSimulationEngine.ValidationTier validationTier, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.core.model.SearchTier searchTier, int totalCalls, int slaPassCount, int slaFailCount, float slaPassRate, long p50LatencyMs, long p95LatencyMs, long p99LatencyMs, long maxLatencyMs, float fallbackRate, float correctClassificationRate, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.SlaStressTester.FailureBreakdown failureBreakdown) {
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
     * 실패 원인 분류.
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u000f\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0007J\t\u0010\r\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u000e\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u000f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0010\u001a\u00020\u0003H\u00c6\u0003J1\u0010\u0011\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0015\u001a\u00020\u0003H\u00d6\u0001J\t\u0010\u0016\u001a\u00020\u0017H\u00d6\u0001R\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\tR\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\tR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\t\u00a8\u0006\u0018"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/SlaStressTester$FailureBreakdown;", "", "slaViolations", "", "misclassifications", "insufficientResults", "offlineFallbacks", "(IIII)V", "getInsufficientResults", "()I", "getMisclassifications", "getOfflineFallbacks", "getSlaViolations", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "hashCode", "toString", "", "call-intercept_debug"})
    public static final class FailureBreakdown {
        private final int slaViolations = 0;
        private final int misclassifications = 0;
        private final int insufficientResults = 0;
        private final int offlineFallbacks = 0;
        
        public FailureBreakdown(int slaViolations, int misclassifications, int insufficientResults, int offlineFallbacks) {
            super();
        }
        
        public final int getSlaViolations() {
            return 0;
        }
        
        public final int getMisclassifications() {
            return 0;
        }
        
        public final int getInsufficientResults() {
            return 0;
        }
        
        public final int getOfflineFallbacks() {
            return 0;
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
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.SlaStressTester.FailureBreakdown copy(int slaViolations, int misclassifications, int insufficientResults, int offlineFallbacks) {
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
     * 전체 스트레스 테스트 보고서.
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0018\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001BI\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n\u0012\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000b0\n\u00a2\u0006\u0002\u0010\rJ\t\u0010\u001c\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001d\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001e\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010 \u001a\u00020\bH\u00c6\u0003J\u000f\u0010!\u001a\b\u0012\u0004\u0012\u00020\u000b0\nH\u00c6\u0003J\u000f\u0010\"\u001a\b\u0012\u0004\u0012\u00020\u000b0\nH\u00c6\u0003J[\u0010#\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\b2\u000e\b\u0002\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n2\u000e\b\u0002\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000b0\nH\u00c6\u0001J\u0013\u0010$\u001a\u00020\u000f2\b\u0010%\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010&\u001a\u00020\u0003H\u00d6\u0001J\u0006\u0010\'\u001a\u00020(J\t\u0010)\u001a\u00020(H\u00d6\u0001R\u0011\u0010\u000e\u001a\u00020\u000f8F\u00a2\u0006\u0006\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0017\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000b0\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0013R\u0017\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0015R\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0013R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0013\u00a8\u0006*"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/SlaStressTester$StressTestReport;", "", "totalCountries", "", "passCountries", "failCountries", "totalCalls", "overallSlaPassRate", "", "results", "", "Lapp/callcheck/mobile/feature/callintercept/SlaStressTester$CountryStressResult;", "failedCountryDetails", "(IIIIFLjava/util/List;Ljava/util/List;)V", "allPassed", "", "getAllPassed", "()Z", "getFailCountries", "()I", "getFailedCountryDetails", "()Ljava/util/List;", "getOverallSlaPassRate", "()F", "getPassCountries", "getResults", "getTotalCalls", "getTotalCountries", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "copy", "equals", "other", "hashCode", "toJarvisFormat", "", "toString", "call-intercept_debug"})
    public static final class StressTestReport {
        private final int totalCountries = 0;
        private final int passCountries = 0;
        private final int failCountries = 0;
        private final int totalCalls = 0;
        private final float overallSlaPassRate = 0.0F;
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<app.callcheck.mobile.feature.callintercept.SlaStressTester.CountryStressResult> results = null;
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<app.callcheck.mobile.feature.callintercept.SlaStressTester.CountryStressResult> failedCountryDetails = null;
        
        public StressTestReport(int totalCountries, int passCountries, int failCountries, int totalCalls, float overallSlaPassRate, @org.jetbrains.annotations.NotNull()
        java.util.List<app.callcheck.mobile.feature.callintercept.SlaStressTester.CountryStressResult> results, @org.jetbrains.annotations.NotNull()
        java.util.List<app.callcheck.mobile.feature.callintercept.SlaStressTester.CountryStressResult> failedCountryDetails) {
            super();
        }
        
        public final int getTotalCountries() {
            return 0;
        }
        
        public final int getPassCountries() {
            return 0;
        }
        
        public final int getFailCountries() {
            return 0;
        }
        
        public final int getTotalCalls() {
            return 0;
        }
        
        public final float getOverallSlaPassRate() {
            return 0.0F;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<app.callcheck.mobile.feature.callintercept.SlaStressTester.CountryStressResult> getResults() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<app.callcheck.mobile.feature.callintercept.SlaStressTester.CountryStressResult> getFailedCountryDetails() {
            return null;
        }
        
        public final boolean getAllPassed() {
            return false;
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
        
        public final float component5() {
            return 0.0F;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<app.callcheck.mobile.feature.callintercept.SlaStressTester.CountryStressResult> component6() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<app.callcheck.mobile.feature.callintercept.SlaStressTester.CountryStressResult> component7() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.SlaStressTester.StressTestReport copy(int totalCountries, int passCountries, int failCountries, int totalCalls, float overallSlaPassRate, @org.jetbrains.annotations.NotNull()
        java.util.List<app.callcheck.mobile.feature.callintercept.SlaStressTester.CountryStressResult> results, @org.jetbrains.annotations.NotNull()
        java.util.List<app.callcheck.mobile.feature.callintercept.SlaStressTester.CountryStressResult> failedCountryDetails) {
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