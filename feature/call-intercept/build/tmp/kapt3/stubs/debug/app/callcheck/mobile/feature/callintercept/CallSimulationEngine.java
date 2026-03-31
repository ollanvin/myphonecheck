package app.callcheck.mobile.feature.callintercept;

/**
 * 가상 통신 환경 시뮬레이션 엔진.
 *
 * 빅테크 정석: Synthetic Simulation Engine
 * 실기기 없이 190개국 수신 시나리오를 재현.
 *
 * 시뮬레이션 축:
 *  1. 국가 (countryCode)
 *  2. 번호 유형 (SPAM, SCAM, DELIVERY, INSTITUTION, UNKNOWN, VOIP)
 *  3. 시간대 (LOCAL_BUSINESS, LOCAL_NIGHT, LOCAL_DAWN)
 *  4. 네트워크 상태 (GOOD, MODERATE, POOR, OFFLINE)
 *  5. 검색 응답 지연 (정상, 지연, 실패)
 *
 * 100% 온디바이스. 서버 전송 없음.
 */
@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000b\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\"\n\u0002\b\u0007\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000b\b\u0007\u0018\u00002\u00020\u0001:\n\'()*+,-./0B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0010\u0010\u000e\u001a\u00020\u00072\u0006\u0010\u000f\u001a\u00020\u0007H\u0002J\u0014\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\u0006\u0010\u000f\u001a\u00020\u0007J\u0014\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\u0006\u0010\u000f\u001a\u00020\u0007J\u000e\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u000f\u001a\u00020\u0007J\u0018\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u00172\u0006\u0010\u0019\u001a\u00020\u0017H\u0002J\u000e\u0010\u001a\u001a\u00020\u001bH\u0086@\u00a2\u0006\u0002\u0010\u001cJ\u0016\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020\u0012H\u0086@\u00a2\u0006\u0002\u0010 J\u0018\u0010!\u001a\u00020\"2\u0006\u0010#\u001a\u00020$2\u0006\u0010%\u001a\u00020&H\u0002R\u001a\u0010\u0005\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0017\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00070\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000b\u00a8\u00061"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/CallSimulationEngine;", "", "registry", "Lapp/callcheck/mobile/feature/callintercept/GlobalSearchProviderRegistry;", "(Lapp/callcheck/mobile/feature/callintercept/GlobalSearchProviderRegistry;)V", "countryDialCodes", "", "", "tier1Countries", "", "getTier1Countries", "()Ljava/util/Set;", "tier2Countries", "getTier2Countries", "generateSampleNumber", "countryCode", "generateStressTestCalls", "", "Lapp/callcheck/mobile/feature/callintercept/CallSimulationEngine$SimulatedCall;", "generateTestMatrix", "getValidationTier", "Lapp/callcheck/mobile/feature/callintercept/CallSimulationEngine$ValidationTier;", "randomLatency", "", "min", "max", "runFullSimulation", "Lapp/callcheck/mobile/feature/callintercept/CallSimulationEngine$FullSimulationReport;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "simulate", "Lapp/callcheck/mobile/feature/callintercept/CallSimulationEngine$SimulationResult;", "call", "(Lapp/callcheck/mobile/feature/callintercept/CallSimulationEngine$SimulatedCall;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "simulateClassification", "Lapp/callcheck/mobile/feature/callintercept/CallSimulationEngine$SimVerdict;", "callType", "Lapp/callcheck/mobile/feature/callintercept/CallSimulationEngine$CallType;", "config", "Lapp/callcheck/mobile/core/model/CountrySearchConfig;", "CallType", "CountrySimResult", "FullSimulationReport", "NetworkCondition", "SearchResponseScenario", "SimVerdict", "SimulatedCall", "SimulationResult", "TimeSlot", "ValidationTier", "call-intercept_debug"})
public final class CallSimulationEngine {
    @org.jetbrains.annotations.NotNull()
    private final app.callcheck.mobile.feature.callintercept.GlobalSearchProviderRegistry registry = null;
    
    /**
     * 검증 티어 1: 핵심 30개국 (실기기 필수)
     */
    @org.jetbrains.annotations.NotNull()
    private final java.util.Set<java.lang.String> tier1Countries = null;
    
    /**
     * 검증 티어 2: 중요 60개국
     */
    @org.jetbrains.annotations.NotNull()
    private final java.util.Set<java.lang.String> tier2Countries = null;
    
    /**
     * 국가별 국제전화 코드 (주요국)
     */
    @org.jetbrains.annotations.NotNull()
    private final java.util.Map<java.lang.String, java.lang.String> countryDialCodes = null;
    
    @javax.inject.Inject()
    public CallSimulationEngine(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.callintercept.GlobalSearchProviderRegistry registry) {
        super();
    }
    
    /**
     * 검증 티어 1: 핵심 30개국 (실기기 필수)
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.Set<java.lang.String> getTier1Countries() {
        return null;
    }
    
    /**
     * 검증 티어 2: 중요 60개국
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.Set<java.lang.String> getTier2Countries() {
        return null;
    }
    
    /**
     * 나머지는 자동으로 Tier 3
     */
    @org.jetbrains.annotations.NotNull()
    public final app.callcheck.mobile.feature.callintercept.CallSimulationEngine.ValidationTier getValidationTier(@org.jetbrains.annotations.NotNull()
    java.lang.String countryCode) {
        return null;
    }
    
    /**
     * 단일 시뮬레이션 전화 실행.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object simulate(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.callintercept.CallSimulationEngine.SimulatedCall call, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super app.callcheck.mobile.feature.callintercept.CallSimulationEngine.SimulationResult> $completion) {
        return null;
    }
    
    /**
     * 국가별 전체 시뮬레이션 세트 생성.
     * 자비스 기준: 번호유형 6개 × 시간대 3개 × 네트워크 4개 × 검색시나리오 5개 = 360 조합
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<app.callcheck.mobile.feature.callintercept.CallSimulationEngine.SimulatedCall> generateTestMatrix(@org.jetbrains.annotations.NotNull()
    java.lang.String countryCode) {
        return null;
    }
    
    /**
     * 국가별 SLA 스트레스 테스트용 콜 생성.
     * 티어별 콜 수에 맞춰 랜덤 조합 생성.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<app.callcheck.mobile.feature.callintercept.CallSimulationEngine.SimulatedCall> generateStressTestCalls(@org.jetbrains.annotations.NotNull()
    java.lang.String countryCode) {
        return null;
    }
    
    /**
     * 전체 190개국 시뮬레이션 보고서 생성 (티어별 요약).
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object runFullSimulation(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super app.callcheck.mobile.feature.callintercept.CallSimulationEngine.FullSimulationReport> $completion) {
        return null;
    }
    
    /**
     * 국가별 샘플 전화번호 생성
     */
    private final java.lang.String generateSampleNumber(java.lang.String countryCode) {
        return null;
    }
    
    /**
     * 분류 시뮬레이션: 검색 결과 기반으로 정확 판정 확률
     */
    private final app.callcheck.mobile.feature.callintercept.CallSimulationEngine.SimVerdict simulateClassification(app.callcheck.mobile.feature.callintercept.CallSimulationEngine.CallType callType, app.callcheck.mobile.core.model.CountrySearchConfig config) {
        return null;
    }
    
    /**
     * 결정론적 지연 시간 생성 (시드 기반, 재현 가능)
     */
    private final long randomLatency(long min, long max) {
        return 0L;
    }
    
    /**
     * 시뮬레이션 전화 유형
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\u000e\n\u0002\b\n\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u000f\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\nj\u0002\b\u000bj\u0002\b\f\u00a8\u0006\r"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/CallSimulationEngine$CallType;", "", "label", "", "(Ljava/lang/String;ILjava/lang/String;)V", "getLabel", "()Ljava/lang/String;", "SPAM", "SCAM", "DELIVERY", "INSTITUTION", "UNKNOWN", "VOIP", "call-intercept_debug"})
    public static enum CallType {
        /*public static final*/ SPAM /* = new SPAM(null) */,
        /*public static final*/ SCAM /* = new SCAM(null) */,
        /*public static final*/ DELIVERY /* = new DELIVERY(null) */,
        /*public static final*/ INSTITUTION /* = new INSTITUTION(null) */,
        /*public static final*/ UNKNOWN /* = new UNKNOWN(null) */,
        /*public static final*/ VOIP /* = new VOIP(null) */;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String label = null;
        
        CallType(java.lang.String label) {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getLabel() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public static kotlin.enums.EnumEntries<app.callcheck.mobile.feature.callintercept.CallSimulationEngine.CallType> getEntries() {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\t\n\u0002\b\u001e\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\b\u0018\u00002\u00020\u0001BU\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\t\u0012\u0006\u0010\u000b\u001a\u00020\t\u0012\u0006\u0010\f\u001a\u00020\t\u0012\u0006\u0010\r\u001a\u00020\u000e\u0012\u0006\u0010\u000f\u001a\u00020\u0010\u0012\u0006\u0010\u0011\u001a\u00020\u000e\u00a2\u0006\u0002\u0010\u0012J\t\u0010#\u001a\u00020\u0003H\u00c6\u0003J\t\u0010$\u001a\u00020\u000eH\u00c6\u0003J\t\u0010%\u001a\u00020\u0005H\u00c6\u0003J\t\u0010&\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\'\u001a\u00020\tH\u00c6\u0003J\t\u0010(\u001a\u00020\tH\u00c6\u0003J\t\u0010)\u001a\u00020\tH\u00c6\u0003J\t\u0010*\u001a\u00020\tH\u00c6\u0003J\t\u0010+\u001a\u00020\u000eH\u00c6\u0003J\t\u0010,\u001a\u00020\u0010H\u00c6\u0003Jm\u0010-\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\t2\b\b\u0002\u0010\u000b\u001a\u00020\t2\b\b\u0002\u0010\f\u001a\u00020\t2\b\b\u0002\u0010\r\u001a\u00020\u000e2\b\b\u0002\u0010\u000f\u001a\u00020\u00102\b\b\u0002\u0010\u0011\u001a\u00020\u000eH\u00c6\u0001J\u0013\u0010.\u001a\u00020/2\b\u00100\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u00101\u001a\u00020\tH\u00d6\u0001J\t\u00102\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u000f\u001a\u00020\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\u000b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0011\u0010\r\u001a\u00020\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u0011\u0010\n\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0018R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001dR\u0011\u0010\u0011\u001a\u00020\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001aR\u0011\u0010\f\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u0018R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010!R\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010\u0018\u00a8\u00063"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/CallSimulationEngine$CountrySimResult;", "", "countryCode", "", "tier", "Lapp/callcheck/mobile/feature/callintercept/CallSimulationEngine$ValidationTier;", "searchTier", "Lapp/callcheck/mobile/core/model/SearchTier;", "totalCalls", "", "passCount", "failCount", "slaViolations", "fallbackRate", "", "avgLatencyMs", "", "slaPassRate", "(Ljava/lang/String;Lapp/callcheck/mobile/feature/callintercept/CallSimulationEngine$ValidationTier;Lapp/callcheck/mobile/core/model/SearchTier;IIIIFJF)V", "getAvgLatencyMs", "()J", "getCountryCode", "()Ljava/lang/String;", "getFailCount", "()I", "getFallbackRate", "()F", "getPassCount", "getSearchTier", "()Lapp/callcheck/mobile/core/model/SearchTier;", "getSlaPassRate", "getSlaViolations", "getTier", "()Lapp/callcheck/mobile/feature/callintercept/CallSimulationEngine$ValidationTier;", "getTotalCalls", "component1", "component10", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "", "other", "hashCode", "toString", "call-intercept_debug"})
    public static final class CountrySimResult {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String countryCode = null;
        @org.jetbrains.annotations.NotNull()
        private final app.callcheck.mobile.feature.callintercept.CallSimulationEngine.ValidationTier tier = null;
        @org.jetbrains.annotations.NotNull()
        private final app.callcheck.mobile.core.model.SearchTier searchTier = null;
        private final int totalCalls = 0;
        private final int passCount = 0;
        private final int failCount = 0;
        private final int slaViolations = 0;
        private final float fallbackRate = 0.0F;
        private final long avgLatencyMs = 0L;
        private final float slaPassRate = 0.0F;
        
        public CountrySimResult(@org.jetbrains.annotations.NotNull()
        java.lang.String countryCode, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.CallSimulationEngine.ValidationTier tier, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.core.model.SearchTier searchTier, int totalCalls, int passCount, int failCount, int slaViolations, float fallbackRate, long avgLatencyMs, float slaPassRate) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getCountryCode() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.CallSimulationEngine.ValidationTier getTier() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.core.model.SearchTier getSearchTier() {
            return null;
        }
        
        public final int getTotalCalls() {
            return 0;
        }
        
        public final int getPassCount() {
            return 0;
        }
        
        public final int getFailCount() {
            return 0;
        }
        
        public final int getSlaViolations() {
            return 0;
        }
        
        public final float getFallbackRate() {
            return 0.0F;
        }
        
        public final long getAvgLatencyMs() {
            return 0L;
        }
        
        public final float getSlaPassRate() {
            return 0.0F;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component1() {
            return null;
        }
        
        public final float component10() {
            return 0.0F;
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
        
        public final int component7() {
            return 0;
        }
        
        public final float component8() {
            return 0.0F;
        }
        
        public final long component9() {
            return 0L;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.CallSimulationEngine.CountrySimResult copy(@org.jetbrains.annotations.NotNull()
        java.lang.String countryCode, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.CallSimulationEngine.ValidationTier tier, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.core.model.SearchTier searchTier, int totalCalls, int passCount, int failCount, int slaViolations, float fallbackRate, long avgLatencyMs, float slaPassRate) {
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0011\n\u0002\u0010\u000b\n\u0002\b\u0005\b\u0086\b\u0018\u00002\u00020\u0001BA\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b\u0012\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\b\u00a2\u0006\u0002\u0010\fJ\t\u0010\u0015\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0016\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0017\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\u0003H\u00c6\u0003J\u000f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\t0\bH\u00c6\u0003J\u000f\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u000b0\bH\u00c6\u0003JQ\u0010\u001b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b2\u000e\b\u0002\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\bH\u00c6\u0001J\u0013\u0010\u001c\u001a\u00020\u001d2\b\u0010\u001e\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001f\u001a\u00020\u0003H\u00d6\u0001J\u0006\u0010 \u001a\u00020\u000bJ\t\u0010!\u001a\u00020\u000bH\u00d6\u0001R\u0017\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0017\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u000eR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0011R\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0011R\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0011\u00a8\u0006\""}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/CallSimulationEngine$FullSimulationReport;", "", "totalCountries", "", "totalCalls", "totalPass", "totalFail", "countryResults", "", "Lapp/callcheck/mobile/feature/callintercept/CallSimulationEngine$CountrySimResult;", "failedCountries", "", "(IIIILjava/util/List;Ljava/util/List;)V", "getCountryResults", "()Ljava/util/List;", "getFailedCountries", "getTotalCalls", "()I", "getTotalCountries", "getTotalFail", "getTotalPass", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "equals", "", "other", "hashCode", "toJarvisFormat", "toString", "call-intercept_debug"})
    public static final class FullSimulationReport {
        private final int totalCountries = 0;
        private final int totalCalls = 0;
        private final int totalPass = 0;
        private final int totalFail = 0;
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<app.callcheck.mobile.feature.callintercept.CallSimulationEngine.CountrySimResult> countryResults = null;
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<java.lang.String> failedCountries = null;
        
        public FullSimulationReport(int totalCountries, int totalCalls, int totalPass, int totalFail, @org.jetbrains.annotations.NotNull()
        java.util.List<app.callcheck.mobile.feature.callintercept.CallSimulationEngine.CountrySimResult> countryResults, @org.jetbrains.annotations.NotNull()
        java.util.List<java.lang.String> failedCountries) {
            super();
        }
        
        public final int getTotalCountries() {
            return 0;
        }
        
        public final int getTotalCalls() {
            return 0;
        }
        
        public final int getTotalPass() {
            return 0;
        }
        
        public final int getTotalFail() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<app.callcheck.mobile.feature.callintercept.CallSimulationEngine.CountrySimResult> getCountryResults() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<java.lang.String> getFailedCountries() {
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
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<app.callcheck.mobile.feature.callintercept.CallSimulationEngine.CountrySimResult> component5() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<java.lang.String> component6() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.CallSimulationEngine.FullSimulationReport copy(int totalCountries, int totalCalls, int totalPass, int totalFail, @org.jetbrains.annotations.NotNull()
        java.util.List<app.callcheck.mobile.feature.callintercept.CallSimulationEngine.CountrySimResult> countryResults, @org.jetbrains.annotations.NotNull()
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
    
    /**
     * 네트워크 상태
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0007\n\u0002\b\n\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0017\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nj\u0002\b\u000bj\u0002\b\fj\u0002\b\rj\u0002\b\u000e\u00a8\u0006\u000f"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/CallSimulationEngine$NetworkCondition;", "", "label", "", "latencyMultiplier", "", "(Ljava/lang/String;ILjava/lang/String;F)V", "getLabel", "()Ljava/lang/String;", "getLatencyMultiplier", "()F", "GOOD", "MODERATE", "POOR", "OFFLINE", "call-intercept_debug"})
    public static enum NetworkCondition {
        /*public static final*/ GOOD /* = new GOOD(null, 0.0F) */,
        /*public static final*/ MODERATE /* = new MODERATE(null, 0.0F) */,
        /*public static final*/ POOR /* = new POOR(null, 0.0F) */,
        /*public static final*/ OFFLINE /* = new OFFLINE(null, 0.0F) */;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String label = null;
        private final float latencyMultiplier = 0.0F;
        
        NetworkCondition(java.lang.String label, float latencyMultiplier) {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getLabel() {
            return null;
        }
        
        public final float getLatencyMultiplier() {
            return 0.0F;
        }
        
        @org.jetbrains.annotations.NotNull()
        public static kotlin.enums.EnumEntries<app.callcheck.mobile.feature.callintercept.CallSimulationEngine.NetworkCondition> getEntries() {
            return null;
        }
    }
    
    /**
     * 검색 엔진 응답 시나리오
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\u000e\n\u0002\b\t\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u000f\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\nj\u0002\b\u000b\u00a8\u0006\f"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/CallSimulationEngine$SearchResponseScenario;", "", "label", "", "(Ljava/lang/String;ILjava/lang/String;)V", "getLabel", "()Ljava/lang/String;", "NORMAL", "DELAYED", "PRIMARY_FAIL", "ALL_FAIL", "CACHED", "call-intercept_debug"})
    public static enum SearchResponseScenario {
        /*public static final*/ NORMAL /* = new NORMAL(null) */,
        /*public static final*/ DELAYED /* = new DELAYED(null) */,
        /*public static final*/ PRIMARY_FAIL /* = new PRIMARY_FAIL(null) */,
        /*public static final*/ ALL_FAIL /* = new ALL_FAIL(null) */,
        /*public static final*/ CACHED /* = new CACHED(null) */;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String label = null;
        
        SearchResponseScenario(java.lang.String label) {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getLabel() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public static kotlin.enums.EnumEntries<app.callcheck.mobile.feature.callintercept.CallSimulationEngine.SearchResponseScenario> getEntries() {
            return null;
        }
    }
    
    /**
     * 시뮬레이션 결과 판정
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0007\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007\u00a8\u0006\b"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/CallSimulationEngine$SimVerdict;", "", "(Ljava/lang/String;I)V", "CORRECT", "INSUFFICIENT_BUT_DISPLAYED", "MISCLASSIFIED", "SLA_VIOLATION", "OFFLINE_FALLBACK", "call-intercept_debug"})
    public static enum SimVerdict {
        /*public static final*/ CORRECT /* = new CORRECT() */,
        /*public static final*/ INSUFFICIENT_BUT_DISPLAYED /* = new INSUFFICIENT_BUT_DISPLAYED() */,
        /*public static final*/ MISCLASSIFIED /* = new MISCLASSIFIED() */,
        /*public static final*/ SLA_VIOLATION /* = new SLA_VIOLATION() */,
        /*public static final*/ OFFLINE_FALLBACK /* = new OFFLINE_FALLBACK() */;
        
        SimVerdict() {
        }
        
        @org.jetbrains.annotations.NotNull()
        public static kotlin.enums.EnumEntries<app.callcheck.mobile.feature.callintercept.CallSimulationEngine.SimVerdict> getEntries() {
            return null;
        }
    }
    
    /**
     * 시뮬레이션 대상 전화 설정.
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0014\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B7\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\n\u0012\b\b\u0002\u0010\u000b\u001a\u00020\f\u00a2\u0006\u0002\u0010\rJ\t\u0010\u0019\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001b\u001a\u00020\u0006H\u00c6\u0003J\t\u0010\u001c\u001a\u00020\bH\u00c6\u0003J\t\u0010\u001d\u001a\u00020\nH\u00c6\u0003J\t\u0010\u001e\u001a\u00020\fH\u00c6\u0003JE\u0010\u001f\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\fH\u00c6\u0001J\u0013\u0010 \u001a\u00020!2\b\u0010\"\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010#\u001a\u00020$H\u00d6\u0001J\t\u0010%\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0011R\u0011\u0010\u000b\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018\u00a8\u0006&"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/CallSimulationEngine$SimulatedCall;", "", "country", "", "phoneNumber", "callType", "Lapp/callcheck/mobile/feature/callintercept/CallSimulationEngine$CallType;", "timeSlot", "Lapp/callcheck/mobile/feature/callintercept/CallSimulationEngine$TimeSlot;", "network", "Lapp/callcheck/mobile/feature/callintercept/CallSimulationEngine$NetworkCondition;", "searchScenario", "Lapp/callcheck/mobile/feature/callintercept/CallSimulationEngine$SearchResponseScenario;", "(Ljava/lang/String;Ljava/lang/String;Lapp/callcheck/mobile/feature/callintercept/CallSimulationEngine$CallType;Lapp/callcheck/mobile/feature/callintercept/CallSimulationEngine$TimeSlot;Lapp/callcheck/mobile/feature/callintercept/CallSimulationEngine$NetworkCondition;Lapp/callcheck/mobile/feature/callintercept/CallSimulationEngine$SearchResponseScenario;)V", "getCallType", "()Lapp/callcheck/mobile/feature/callintercept/CallSimulationEngine$CallType;", "getCountry", "()Ljava/lang/String;", "getNetwork", "()Lapp/callcheck/mobile/feature/callintercept/CallSimulationEngine$NetworkCondition;", "getPhoneNumber", "getSearchScenario", "()Lapp/callcheck/mobile/feature/callintercept/CallSimulationEngine$SearchResponseScenario;", "getTimeSlot", "()Lapp/callcheck/mobile/feature/callintercept/CallSimulationEngine$TimeSlot;", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "equals", "", "other", "hashCode", "", "toString", "call-intercept_debug"})
    public static final class SimulatedCall {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String country = null;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String phoneNumber = null;
        @org.jetbrains.annotations.NotNull()
        private final app.callcheck.mobile.feature.callintercept.CallSimulationEngine.CallType callType = null;
        @org.jetbrains.annotations.NotNull()
        private final app.callcheck.mobile.feature.callintercept.CallSimulationEngine.TimeSlot timeSlot = null;
        @org.jetbrains.annotations.NotNull()
        private final app.callcheck.mobile.feature.callintercept.CallSimulationEngine.NetworkCondition network = null;
        @org.jetbrains.annotations.NotNull()
        private final app.callcheck.mobile.feature.callintercept.CallSimulationEngine.SearchResponseScenario searchScenario = null;
        
        public SimulatedCall(@org.jetbrains.annotations.NotNull()
        java.lang.String country, @org.jetbrains.annotations.NotNull()
        java.lang.String phoneNumber, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.CallSimulationEngine.CallType callType, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.CallSimulationEngine.TimeSlot timeSlot, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.CallSimulationEngine.NetworkCondition network, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.CallSimulationEngine.SearchResponseScenario searchScenario) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getCountry() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getPhoneNumber() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.CallSimulationEngine.CallType getCallType() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.CallSimulationEngine.TimeSlot getTimeSlot() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.CallSimulationEngine.NetworkCondition getNetwork() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.CallSimulationEngine.SearchResponseScenario getSearchScenario() {
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
        public final app.callcheck.mobile.feature.callintercept.CallSimulationEngine.CallType component3() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.CallSimulationEngine.TimeSlot component4() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.CallSimulationEngine.NetworkCondition component5() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.CallSimulationEngine.SearchResponseScenario component6() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.CallSimulationEngine.SimulatedCall copy(@org.jetbrains.annotations.NotNull()
        java.lang.String country, @org.jetbrains.annotations.NotNull()
        java.lang.String phoneNumber, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.CallSimulationEngine.CallType callType, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.CallSimulationEngine.TimeSlot timeSlot, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.CallSimulationEngine.NetworkCondition network, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.CallSimulationEngine.SearchResponseScenario searchScenario) {
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u001c\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001BK\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000b\u0012\u0006\u0010\r\u001a\u00020\u000e\u0012\u0006\u0010\u000f\u001a\u00020\u000e\u0012\u0006\u0010\u0010\u001a\u00020\u0011\u00a2\u0006\u0002\u0010\u0012J\t\u0010\"\u001a\u00020\u0003H\u00c6\u0003J\t\u0010#\u001a\u00020\u0005H\u00c6\u0003J\t\u0010$\u001a\u00020\u0007H\u00c6\u0003J\t\u0010%\u001a\u00020\tH\u00c6\u0003J\u000f\u0010&\u001a\b\u0012\u0004\u0012\u00020\f0\u000bH\u00c6\u0003J\t\u0010\'\u001a\u00020\u000eH\u00c6\u0003J\t\u0010(\u001a\u00020\u000eH\u00c6\u0003J\t\u0010)\u001a\u00020\u0011H\u00c6\u0003J_\u0010*\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\u000e\b\u0002\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000b2\b\b\u0002\u0010\r\u001a\u00020\u000e2\b\b\u0002\u0010\u000f\u001a\u00020\u000e2\b\b\u0002\u0010\u0010\u001a\u00020\u0011H\u00c6\u0001J\u0013\u0010+\u001a\u00020\u000e2\b\u0010,\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010-\u001a\u00020.H\u00d6\u0001J\t\u0010/\u001a\u00020\u0011H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0017\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0011\u0010\r\u001a\u00020\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u0011\u0010\u0010\u001a\u00020\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001cR\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001eR\u0011\u0010\u000f\u001a\u00020\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u001aR\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010!\u00a8\u00060"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/CallSimulationEngine$SimulationResult;", "", "call", "Lapp/callcheck/mobile/feature/callintercept/CallSimulationEngine$SimulatedCall;", "config", "Lapp/callcheck/mobile/core/model/CountrySearchConfig;", "verdict", "Lapp/callcheck/mobile/feature/callintercept/CallSimulationEngine$SimVerdict;", "simulatedLatencyMs", "", "enginesUsed", "", "Lapp/callcheck/mobile/core/model/SearchEngine;", "fallbackTriggered", "", "slaPassed", "message", "", "(Lapp/callcheck/mobile/feature/callintercept/CallSimulationEngine$SimulatedCall;Lapp/callcheck/mobile/core/model/CountrySearchConfig;Lapp/callcheck/mobile/feature/callintercept/CallSimulationEngine$SimVerdict;JLjava/util/List;ZZLjava/lang/String;)V", "getCall", "()Lapp/callcheck/mobile/feature/callintercept/CallSimulationEngine$SimulatedCall;", "getConfig", "()Lapp/callcheck/mobile/core/model/CountrySearchConfig;", "getEnginesUsed", "()Ljava/util/List;", "getFallbackTriggered", "()Z", "getMessage", "()Ljava/lang/String;", "getSimulatedLatencyMs", "()J", "getSlaPassed", "getVerdict", "()Lapp/callcheck/mobile/feature/callintercept/CallSimulationEngine$SimVerdict;", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "copy", "equals", "other", "hashCode", "", "toString", "call-intercept_debug"})
    public static final class SimulationResult {
        @org.jetbrains.annotations.NotNull()
        private final app.callcheck.mobile.feature.callintercept.CallSimulationEngine.SimulatedCall call = null;
        @org.jetbrains.annotations.NotNull()
        private final app.callcheck.mobile.core.model.CountrySearchConfig config = null;
        @org.jetbrains.annotations.NotNull()
        private final app.callcheck.mobile.feature.callintercept.CallSimulationEngine.SimVerdict verdict = null;
        
        /**
         * 시뮬레이션된 총 소요 시간 (ms)
         */
        private final long simulatedLatencyMs = 0L;
        
        /**
         * 사용된 검색 엔진
         */
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<app.callcheck.mobile.core.model.SearchEngine> enginesUsed = null;
        
        /**
         * fallback 발생 여부
         */
        private final boolean fallbackTriggered = false;
        
        /**
         * 2초 SLA 통과 여부
         */
        private final boolean slaPassed = false;
        
        /**
         * 시뮬레이션 결과 메시지
         */
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String message = null;
        
        public SimulationResult(@org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.CallSimulationEngine.SimulatedCall call, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.core.model.CountrySearchConfig config, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.CallSimulationEngine.SimVerdict verdict, long simulatedLatencyMs, @org.jetbrains.annotations.NotNull()
        java.util.List<? extends app.callcheck.mobile.core.model.SearchEngine> enginesUsed, boolean fallbackTriggered, boolean slaPassed, @org.jetbrains.annotations.NotNull()
        java.lang.String message) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.CallSimulationEngine.SimulatedCall getCall() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.core.model.CountrySearchConfig getConfig() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.CallSimulationEngine.SimVerdict getVerdict() {
            return null;
        }
        
        /**
         * 시뮬레이션된 총 소요 시간 (ms)
         */
        public final long getSimulatedLatencyMs() {
            return 0L;
        }
        
        /**
         * 사용된 검색 엔진
         */
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<app.callcheck.mobile.core.model.SearchEngine> getEnginesUsed() {
            return null;
        }
        
        /**
         * fallback 발생 여부
         */
        public final boolean getFallbackTriggered() {
            return false;
        }
        
        /**
         * 2초 SLA 통과 여부
         */
        public final boolean getSlaPassed() {
            return false;
        }
        
        /**
         * 시뮬레이션 결과 메시지
         */
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getMessage() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.CallSimulationEngine.SimulatedCall component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.core.model.CountrySearchConfig component2() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.CallSimulationEngine.SimVerdict component3() {
            return null;
        }
        
        public final long component4() {
            return 0L;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<app.callcheck.mobile.core.model.SearchEngine> component5() {
            return null;
        }
        
        public final boolean component6() {
            return false;
        }
        
        public final boolean component7() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component8() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.CallSimulationEngine.SimulationResult copy(@org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.CallSimulationEngine.SimulatedCall call, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.core.model.CountrySearchConfig config, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.CallSimulationEngine.SimVerdict verdict, long simulatedLatencyMs, @org.jetbrains.annotations.NotNull()
        java.util.List<? extends app.callcheck.mobile.core.model.SearchEngine> enginesUsed, boolean fallbackTriggered, boolean slaPassed, @org.jetbrains.annotations.NotNull()
        java.lang.String message) {
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
     * 수신 시간대
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0007\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u000f\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\t\u00a8\u0006\n"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/CallSimulationEngine$TimeSlot;", "", "label", "", "(Ljava/lang/String;ILjava/lang/String;)V", "getLabel", "()Ljava/lang/String;", "LOCAL_BUSINESS", "LOCAL_NIGHT", "LOCAL_DAWN", "call-intercept_debug"})
    public static enum TimeSlot {
        /*public static final*/ LOCAL_BUSINESS /* = new LOCAL_BUSINESS(null) */,
        /*public static final*/ LOCAL_NIGHT /* = new LOCAL_NIGHT(null) */,
        /*public static final*/ LOCAL_DAWN /* = new LOCAL_DAWN(null) */;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String label = null;
        
        TimeSlot(java.lang.String label) {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getLabel() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public static kotlin.enums.EnumEntries<app.callcheck.mobile.feature.callintercept.CallSimulationEngine.TimeSlot> getEntries() {
            return null;
        }
    }
    
    /**
     * 검증 티어 (자비스 기준)
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\t\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0017\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nj\u0002\b\u000bj\u0002\b\fj\u0002\b\r\u00a8\u0006\u000e"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/CallSimulationEngine$ValidationTier;", "", "label", "", "callsPerCountry", "", "(Ljava/lang/String;ILjava/lang/String;I)V", "getCallsPerCountry", "()I", "getLabel", "()Ljava/lang/String;", "TIER_1", "TIER_2", "TIER_3", "call-intercept_debug"})
    public static enum ValidationTier {
        /*public static final*/ TIER_1 /* = new TIER_1(null, 0) */,
        /*public static final*/ TIER_2 /* = new TIER_2(null, 0) */,
        /*public static final*/ TIER_3 /* = new TIER_3(null, 0) */;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String label = null;
        private final int callsPerCountry = 0;
        
        ValidationTier(java.lang.String label, int callsPerCountry) {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getLabel() {
            return null;
        }
        
        public final int getCallsPerCountry() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public static kotlin.enums.EnumEntries<app.callcheck.mobile.feature.callintercept.CallSimulationEngine.ValidationTier> getEntries() {
            return null;
        }
    }
}