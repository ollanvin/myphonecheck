package app.callcheck.mobile.feature.callintercept;

/**
 * 2초 SLA 강제 타임아웃 엔포서.
 *
 * 자비스 기준:
 * "2초 안에 결과가 부족해도 '결과 없음'이 아니라
 * '현재까지 발견된 결과'를 먼저 보여주고,
 * 뒤에서 확정값으로 갱신해야 합니다."
 *
 * 타임라인:
 *  0ms      — 전화번호 정규화 시작
 *  ~50ms    — 정규화 완료, 국가 라우터 시작
 *  ~150ms   — 라우터 완료, 1순위 검색 시작
 *  ~1200ms  — 1순위 타임아웃, 결과 있으면 수집 / 없으면 2순위 시작
 *  ~1500ms  — earlyDisplay: 현재까지 결과 UI에 1차 표시
 *  ~1800ms  — 2순위 타임아웃, 3순위 디렉토리 검색 시작
 *  2000ms   — hardDeadline: 무조건 최종 결과 강제 표시
 *
 * 설계 원칙:
 * - 3순위 동시 검색, 각 엔진별 독립 타임아웃
 * - earlyDisplayMs 시점에 중간 결과 콜백
 * - hardDeadlineMs 시점에 확보된 결과로 강제 완료
 * - 모든 검색은 독립 코루틴, 하나가 실패해도 나머지 계속
 * - 100% 온디바이스. 서버 전송 없음.
 */
@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000r\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\t\b\u0007\u0018\u0000 &2\u00020\u0001:\b&\'()*+,-B\u0007\b\u0007\u00a2\u0006\u0002\u0010\u0002JJ\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b2\u0006\u0010\n\u001a\u00020\u000b2\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\r0\b2\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\r0\b2\u0006\u0010\u000f\u001a\u00020\u0010H\u0002JV\u0010\u0011\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00172\u0016\b\u0002\u0010\u0018\u001a\u0010\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u001a\u0018\u00010\u00192\u0016\b\u0002\u0010\u001b\u001a\u0010\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u001a\u0018\u00010\u0019H\u0086@\u00a2\u0006\u0002\u0010\u001cJ\u000e\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020 J\u0016\u0010!\u001a\u00020\"2\u0006\u0010#\u001a\u00020\u00042\u0006\u0010$\u001a\u00020%\u00a8\u0006."}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/SearchTimeoutEnforcer;", "", "()V", "buildSnapshot", "Lapp/callcheck/mobile/feature/callintercept/SearchTimeoutEnforcer$SearchSnapshot;", "phase", "Lapp/callcheck/mobile/feature/callintercept/SearchTimeoutEnforcer$SearchPhase;", "results", "", "Lapp/callcheck/mobile/feature/callintercept/SearchTimeoutEnforcer$SearchResult;", "startTime", "", "enginesUsed", "Lapp/callcheck/mobile/core/model/SearchEngine;", "enginesTimedOut", "isFinal", "", "executeWithSla", "phoneNumber", "", "config", "Lapp/callcheck/mobile/core/model/CountrySearchConfig;", "executor", "Lapp/callcheck/mobile/feature/callintercept/SearchTimeoutEnforcer$EngineExecutor;", "onEarlyDisplay", "Lkotlin/Function1;", "", "onHardDeadline", "(Ljava/lang/String;Lapp/callcheck/mobile/core/model/CountrySearchConfig;Lapp/callcheck/mobile/feature/callintercept/SearchTimeoutEnforcer$EngineExecutor;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "generateSlaComplianceReport", "Lapp/callcheck/mobile/feature/callintercept/SearchTimeoutEnforcer$SlaComplianceReport;", "registry", "Lapp/callcheck/mobile/feature/callintercept/GlobalSearchProviderRegistry;", "verifySla", "Lapp/callcheck/mobile/feature/callintercept/SearchTimeoutEnforcer$SlaVerification;", "snapshot", "policy", "Lapp/callcheck/mobile/core/model/TimeoutPolicy;", "Companion", "CountrySlaResult", "EngineExecutor", "SearchPhase", "SearchResult", "SearchSnapshot", "SlaComplianceReport", "SlaVerification", "call-intercept_debug"})
public final class SearchTimeoutEnforcer {
    
    /**
     * 글로벌 SLA 한계 (ms)
     */
    public static final long GLOBAL_SLA_MS = 2000L;
    @org.jetbrains.annotations.NotNull()
    public static final app.callcheck.mobile.feature.callintercept.SearchTimeoutEnforcer.Companion Companion = null;
    
    @javax.inject.Inject()
    public SearchTimeoutEnforcer() {
        super();
    }
    
    /**
     * 2초 SLA 강제 검색 실행.
     *
     * @param phoneNumber E.164 정규화된 전화번호
     * @param config 국가별 검색 설정
     * @param executor 검색 엔진 실행기
     * @param onEarlyDisplay 중간 결과 콜백 (earlyDisplayMs 시점)
     * @param onHardDeadline 최종 강제 결과 콜백 (hardDeadlineMs 시점)
     * @return 최종 SearchSnapshot
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object executeWithSla(@org.jetbrains.annotations.NotNull()
    java.lang.String phoneNumber, @org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.core.model.CountrySearchConfig config, @org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.callintercept.SearchTimeoutEnforcer.EngineExecutor executor, @org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function1<? super app.callcheck.mobile.feature.callintercept.SearchTimeoutEnforcer.SearchSnapshot, kotlin.Unit> onEarlyDisplay, @org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function1<? super app.callcheck.mobile.feature.callintercept.SearchTimeoutEnforcer.SearchSnapshot, kotlin.Unit> onHardDeadline, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super app.callcheck.mobile.feature.callintercept.SearchTimeoutEnforcer.SearchSnapshot> $completion) {
        return null;
    }
    
    /**
     * SLA 통과 여부 검증.
     *
     * @param snapshot 검색 결과 스냅샷
     * @param policy 타임아웃 정책
     * @return true = 2초 SLA 통과
     */
    @org.jetbrains.annotations.NotNull()
    public final app.callcheck.mobile.feature.callintercept.SearchTimeoutEnforcer.SlaVerification verifySla(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.callintercept.SearchTimeoutEnforcer.SearchSnapshot snapshot, @org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.core.model.TimeoutPolicy policy) {
        return null;
    }
    
    /**
     * 전체 190개국 SLA 시뮬레이션 보고.
     *
     * 실제 네트워크 없이 타임아웃 정책만으로 통과 여부 판정.
     * 모든 국가의 hardDeadlineMs ≤ 2000ms 이면 PASS.
     */
    @org.jetbrains.annotations.NotNull()
    public final app.callcheck.mobile.feature.callintercept.SearchTimeoutEnforcer.SlaComplianceReport generateSlaComplianceReport(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.callintercept.GlobalSearchProviderRegistry registry) {
        return null;
    }
    
    private final app.callcheck.mobile.feature.callintercept.SearchTimeoutEnforcer.SearchSnapshot buildSnapshot(app.callcheck.mobile.feature.callintercept.SearchTimeoutEnforcer.SearchPhase phase, java.util.List<app.callcheck.mobile.feature.callintercept.SearchTimeoutEnforcer.SearchResult> results, long startTime, java.util.List<? extends app.callcheck.mobile.core.model.SearchEngine> enginesUsed, java.util.List<? extends app.callcheck.mobile.core.model.SearchEngine> enginesTimedOut, boolean isFinal) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/SearchTimeoutEnforcer$Companion;", "", "()V", "GLOBAL_SLA_MS", "", "call-intercept_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0015\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B9\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\u0002\u0010\u000bJ\t\u0010\u0015\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0016\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0017\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u0019\u001a\u00020\tH\u00c6\u0003J\u000b\u0010\u001a\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003JG\u0010\u001b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u0003H\u00c6\u0001J\u0013\u0010\u001c\u001a\u00020\t2\b\u0010\u001d\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001e\u001a\u00020\u001fH\u00d6\u0001J\t\u0010 \u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0013\u0010\n\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\rR\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\rR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\r\u00a8\u0006!"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/SearchTimeoutEnforcer$CountrySlaResult;", "", "countryCode", "", "tier", "primaryEngine", "hardDeadlineMs", "", "passed", "", "failureReason", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;JZLjava/lang/String;)V", "getCountryCode", "()Ljava/lang/String;", "getFailureReason", "getHardDeadlineMs", "()J", "getPassed", "()Z", "getPrimaryEngine", "getTier", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "equals", "other", "hashCode", "", "toString", "call-intercept_debug"})
    public static final class CountrySlaResult {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String countryCode = null;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String tier = null;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String primaryEngine = null;
        private final long hardDeadlineMs = 0L;
        private final boolean passed = false;
        @org.jetbrains.annotations.Nullable()
        private final java.lang.String failureReason = null;
        
        public CountrySlaResult(@org.jetbrains.annotations.NotNull()
        java.lang.String countryCode, @org.jetbrains.annotations.NotNull()
        java.lang.String tier, @org.jetbrains.annotations.NotNull()
        java.lang.String primaryEngine, long hardDeadlineMs, boolean passed, @org.jetbrains.annotations.Nullable()
        java.lang.String failureReason) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getCountryCode() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getTier() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getPrimaryEngine() {
            return null;
        }
        
        public final long getHardDeadlineMs() {
            return 0L;
        }
        
        public final boolean getPassed() {
            return false;
        }
        
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
        public final java.lang.String component3() {
            return null;
        }
        
        public final long component4() {
            return 0L;
        }
        
        public final boolean component5() {
            return false;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.String component6() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.SearchTimeoutEnforcer.CountrySlaResult copy(@org.jetbrains.annotations.NotNull()
        java.lang.String countryCode, @org.jetbrains.annotations.NotNull()
        java.lang.String tier, @org.jetbrains.annotations.NotNull()
        java.lang.String primaryEngine, long hardDeadlineMs, boolean passed, @org.jetbrains.annotations.Nullable()
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
    
    /**
     * 검색 엔진 실행기 인터페이스.
     *
     * 실제 검색 로직은 이 인터페이스를 구현하여 주입.
     * SearchEvidenceProvider가 이를 구현.
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00e6\u0080\u0001\u0018\u00002\u00020\u0001J,\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u00a6@\u00a2\u0006\u0002\u0010\u000b\u00a8\u0006\f"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/SearchTimeoutEnforcer$EngineExecutor;", "", "execute", "", "Lapp/callcheck/mobile/feature/callintercept/SearchTimeoutEnforcer$SearchResult;", "engine", "Lapp/callcheck/mobile/core/model/SearchEngine;", "phoneNumber", "", "config", "Lapp/callcheck/mobile/core/model/CountrySearchConfig;", "(Lapp/callcheck/mobile/core/model/SearchEngine;Ljava/lang/String;Lapp/callcheck/mobile/core/model/CountrySearchConfig;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "call-intercept_debug"})
    public static abstract interface EngineExecutor {
        
        /**
         * 지정 엔진으로 전화번호 검색을 수행.
         *
         * @param engine 사용할 검색 엔진
         * @param phoneNumber E.164 정규화된 전화번호
         * @param config 국가별 검색 설정
         * @return 검색 결과 목록 (빈 목록 = 결과 없음)
         */
        @org.jetbrains.annotations.Nullable()
        public abstract java.lang.Object execute(@org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.core.model.SearchEngine engine, @org.jetbrains.annotations.NotNull()
        java.lang.String phoneNumber, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.core.model.CountrySearchConfig config, @org.jetbrains.annotations.NotNull()
        kotlin.coroutines.Continuation<? super java.util.List<app.callcheck.mobile.feature.callintercept.SearchTimeoutEnforcer.SearchResult>> $completion);
    }
    
    /**
     * 검색 실행 상태.
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\n\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\n\u00a8\u0006\u000b"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/SearchTimeoutEnforcer$SearchPhase;", "", "(Ljava/lang/String;I)V", "NORMALIZING", "ROUTING", "PRIMARY_SEARCH", "SECONDARY_SEARCH", "TERTIARY_SEARCH", "EARLY_DISPLAY", "HARD_DEADLINE_REACHED", "COMPLETED", "call-intercept_debug"})
    public static enum SearchPhase {
        /*public static final*/ NORMALIZING /* = new NORMALIZING() */,
        /*public static final*/ ROUTING /* = new ROUTING() */,
        /*public static final*/ PRIMARY_SEARCH /* = new PRIMARY_SEARCH() */,
        /*public static final*/ SECONDARY_SEARCH /* = new SECONDARY_SEARCH() */,
        /*public static final*/ TERTIARY_SEARCH /* = new TERTIARY_SEARCH() */,
        /*public static final*/ EARLY_DISPLAY /* = new EARLY_DISPLAY() */,
        /*public static final*/ HARD_DEADLINE_REACHED /* = new HARD_DEADLINE_REACHED() */,
        /*public static final*/ COMPLETED /* = new COMPLETED() */;
        
        SearchPhase() {
        }
        
        @org.jetbrains.annotations.NotNull()
        public static kotlin.enums.EnumEntries<app.callcheck.mobile.feature.callintercept.SearchTimeoutEnforcer.SearchPhase> getEntries() {
            return null;
        }
    }
    
    /**
     * 검색 결과 단일 항목.
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0013\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B=\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0007\u001a\u00020\b\u0012\b\b\u0002\u0010\t\u001a\u00020\b\u0012\b\b\u0002\u0010\n\u001a\u00020\u000b\u00a2\u0006\u0002\u0010\fJ\t\u0010\u0017\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0019\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\bH\u00c6\u0003J\t\u0010\u001b\u001a\u00020\bH\u00c6\u0003J\t\u0010\u001c\u001a\u00020\u000bH\u00c6\u0003JE\u0010\u001d\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\b2\b\b\u0002\u0010\n\u001a\u00020\u000bH\u00c6\u0001J\u0013\u0010\u001e\u001a\u00020\u001f2\b\u0010 \u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010!\u001a\u00020\"H\u00d6\u0001J\t\u0010#\u001a\u00020\u0005H\u00d6\u0001R\u0011\u0010\n\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\t\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0012R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0015\u00a8\u0006$"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/SearchTimeoutEnforcer$SearchResult;", "", "engine", "Lapp/callcheck/mobile/core/model/SearchEngine;", "snippet", "", "url", "riskScore", "", "safeScore", "collectedAtMs", "", "(Lapp/callcheck/mobile/core/model/SearchEngine;Ljava/lang/String;Ljava/lang/String;FFJ)V", "getCollectedAtMs", "()J", "getEngine", "()Lapp/callcheck/mobile/core/model/SearchEngine;", "getRiskScore", "()F", "getSafeScore", "getSnippet", "()Ljava/lang/String;", "getUrl", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "equals", "", "other", "hashCode", "", "toString", "call-intercept_debug"})
    public static final class SearchResult {
        
        /**
         * 결과를 제공한 검색 엔진
         */
        @org.jetbrains.annotations.NotNull()
        private final app.callcheck.mobile.core.model.SearchEngine engine = null;
        
        /**
         * 검색 결과 snippet
         */
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String snippet = null;
        
        /**
         * 결과 URL
         */
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String url = null;
        
        /**
         * 이 결과의 위험 점수 (0.0 ~ 1.0)
         */
        private final float riskScore = 0.0F;
        
        /**
         * 이 결과의 안전 점수 (0.0 ~ 1.0)
         */
        private final float safeScore = 0.0F;
        
        /**
         * 수집 시각 (시작으로부터의 경과 ms)
         */
        private final long collectedAtMs = 0L;
        
        public SearchResult(@org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.core.model.SearchEngine engine, @org.jetbrains.annotations.NotNull()
        java.lang.String snippet, @org.jetbrains.annotations.NotNull()
        java.lang.String url, float riskScore, float safeScore, long collectedAtMs) {
            super();
        }
        
        /**
         * 결과를 제공한 검색 엔진
         */
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.core.model.SearchEngine getEngine() {
            return null;
        }
        
        /**
         * 검색 결과 snippet
         */
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getSnippet() {
            return null;
        }
        
        /**
         * 결과 URL
         */
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getUrl() {
            return null;
        }
        
        /**
         * 이 결과의 위험 점수 (0.0 ~ 1.0)
         */
        public final float getRiskScore() {
            return 0.0F;
        }
        
        /**
         * 이 결과의 안전 점수 (0.0 ~ 1.0)
         */
        public final float getSafeScore() {
            return 0.0F;
        }
        
        /**
         * 수집 시각 (시작으로부터의 경과 ms)
         */
        public final long getCollectedAtMs() {
            return 0L;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.core.model.SearchEngine component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component2() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component3() {
            return null;
        }
        
        public final float component4() {
            return 0.0F;
        }
        
        public final float component5() {
            return 0.0F;
        }
        
        public final long component6() {
            return 0L;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.SearchTimeoutEnforcer.SearchResult copy(@org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.core.model.SearchEngine engine, @org.jetbrains.annotations.NotNull()
        java.lang.String snippet, @org.jetbrains.annotations.NotNull()
        java.lang.String url, float riskScore, float safeScore, long collectedAtMs) {
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
     * 검색 진행 상태 스냅샷.
     *
     * earlyDisplay 및 hardDeadline 시점에 콜백으로 전달.
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0014\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001BG\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0005\u0012\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\n0\u0005\u0012\u0006\u0010\f\u001a\u00020\r\u00a2\u0006\u0002\u0010\u000eJ\t\u0010\u0018\u001a\u00020\u0003H\u00c6\u0003J\u000f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\bH\u00c6\u0003J\u000f\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\n0\u0005H\u00c6\u0003J\u000f\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\n0\u0005H\u00c6\u0003J\t\u0010\u001d\u001a\u00020\rH\u00c6\u0003JW\u0010\u001e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\b\b\u0002\u0010\u0007\u001a\u00020\b2\u000e\b\u0002\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u00052\u000e\b\u0002\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\n0\u00052\b\b\u0002\u0010\f\u001a\u00020\rH\u00c6\u0001J\u0013\u0010\u001f\u001a\u00020\r2\b\u0010 \u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010!\u001a\u00020\"H\u00d6\u0001J\t\u0010#\u001a\u00020$H\u00d6\u0001R\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0017\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\n0\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0017\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0012R\u0011\u0010\f\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u0014R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0017\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0012\u00a8\u0006%"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/SearchTimeoutEnforcer$SearchSnapshot;", "", "phase", "Lapp/callcheck/mobile/feature/callintercept/SearchTimeoutEnforcer$SearchPhase;", "results", "", "Lapp/callcheck/mobile/feature/callintercept/SearchTimeoutEnforcer$SearchResult;", "elapsedMs", "", "enginesUsed", "Lapp/callcheck/mobile/core/model/SearchEngine;", "enginesTimedOut", "isFinal", "", "(Lapp/callcheck/mobile/feature/callintercept/SearchTimeoutEnforcer$SearchPhase;Ljava/util/List;JLjava/util/List;Ljava/util/List;Z)V", "getElapsedMs", "()J", "getEnginesTimedOut", "()Ljava/util/List;", "getEnginesUsed", "()Z", "getPhase", "()Lapp/callcheck/mobile/feature/callintercept/SearchTimeoutEnforcer$SearchPhase;", "getResults", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "equals", "other", "hashCode", "", "toString", "", "call-intercept_debug"})
    public static final class SearchSnapshot {
        
        /**
         * 현재 검색 단계
         */
        @org.jetbrains.annotations.NotNull()
        private final app.callcheck.mobile.feature.callintercept.SearchTimeoutEnforcer.SearchPhase phase = null;
        
        /**
         * 현재까지 수집된 결과 목록
         */
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<app.callcheck.mobile.feature.callintercept.SearchTimeoutEnforcer.SearchResult> results = null;
        
        /**
         * 검색 시작 후 경과 시간 (ms)
         */
        private final long elapsedMs = 0L;
        
        /**
         * 사용된 검색 엔진 목록
         */
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<app.callcheck.mobile.core.model.SearchEngine> enginesUsed = null;
        
        /**
         * 타임아웃으로 실패한 엔진 목록
         */
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<app.callcheck.mobile.core.model.SearchEngine> enginesTimedOut = null;
        
        /**
         * 최종 완료 여부
         */
        private final boolean isFinal = false;
        
        public SearchSnapshot(@org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.SearchTimeoutEnforcer.SearchPhase phase, @org.jetbrains.annotations.NotNull()
        java.util.List<app.callcheck.mobile.feature.callintercept.SearchTimeoutEnforcer.SearchResult> results, long elapsedMs, @org.jetbrains.annotations.NotNull()
        java.util.List<? extends app.callcheck.mobile.core.model.SearchEngine> enginesUsed, @org.jetbrains.annotations.NotNull()
        java.util.List<? extends app.callcheck.mobile.core.model.SearchEngine> enginesTimedOut, boolean isFinal) {
            super();
        }
        
        /**
         * 현재 검색 단계
         */
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.SearchTimeoutEnforcer.SearchPhase getPhase() {
            return null;
        }
        
        /**
         * 현재까지 수집된 결과 목록
         */
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<app.callcheck.mobile.feature.callintercept.SearchTimeoutEnforcer.SearchResult> getResults() {
            return null;
        }
        
        /**
         * 검색 시작 후 경과 시간 (ms)
         */
        public final long getElapsedMs() {
            return 0L;
        }
        
        /**
         * 사용된 검색 엔진 목록
         */
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<app.callcheck.mobile.core.model.SearchEngine> getEnginesUsed() {
            return null;
        }
        
        /**
         * 타임아웃으로 실패한 엔진 목록
         */
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<app.callcheck.mobile.core.model.SearchEngine> getEnginesTimedOut() {
            return null;
        }
        
        /**
         * 최종 완료 여부
         */
        public final boolean isFinal() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.SearchTimeoutEnforcer.SearchPhase component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<app.callcheck.mobile.feature.callintercept.SearchTimeoutEnforcer.SearchResult> component2() {
            return null;
        }
        
        public final long component3() {
            return 0L;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<app.callcheck.mobile.core.model.SearchEngine> component4() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<app.callcheck.mobile.core.model.SearchEngine> component5() {
            return null;
        }
        
        public final boolean component6() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.SearchTimeoutEnforcer.SearchSnapshot copy(@org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.SearchTimeoutEnforcer.SearchPhase phase, @org.jetbrains.annotations.NotNull()
        java.util.List<app.callcheck.mobile.feature.callintercept.SearchTimeoutEnforcer.SearchResult> results, long elapsedMs, @org.jetbrains.annotations.NotNull()
        java.util.List<? extends app.callcheck.mobile.core.model.SearchEngine> enginesUsed, @org.jetbrains.annotations.NotNull()
        java.util.List<? extends app.callcheck.mobile.core.model.SearchEngine> enginesTimedOut, boolean isFinal) {
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u000f\n\u0002\u0010\u000b\n\u0002\b\u0005\b\u0086\b\u0018\u00002\u00020\u0001B9\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007\u0012\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0007\u00a2\u0006\u0002\u0010\u000bJ\t\u0010\u0013\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0014\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0015\u001a\u00020\u0003H\u00c6\u0003J\u000f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\b0\u0007H\u00c6\u0003J\u000f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\n0\u0007H\u00c6\u0003JG\u0010\u0018\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\u000e\b\u0002\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0007H\u00c6\u0001J\u0013\u0010\u0019\u001a\u00020\u001a2\b\u0010\u001b\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001c\u001a\u00020\u0003H\u00d6\u0001J\u0006\u0010\u001d\u001a\u00020\nJ\t\u0010\u001e\u001a\u00020\nH\u00d6\u0001R\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0017\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\rR\u0017\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u000fR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\r\u00a8\u0006\u001f"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/SearchTimeoutEnforcer$SlaComplianceReport;", "", "totalCountries", "", "passCount", "failCount", "results", "", "Lapp/callcheck/mobile/feature/callintercept/SearchTimeoutEnforcer$CountrySlaResult;", "failures", "", "(IIILjava/util/List;Ljava/util/List;)V", "getFailCount", "()I", "getFailures", "()Ljava/util/List;", "getPassCount", "getResults", "getTotalCountries", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "", "other", "hashCode", "toFormattedReport", "toString", "call-intercept_debug"})
    public static final class SlaComplianceReport {
        private final int totalCountries = 0;
        private final int passCount = 0;
        private final int failCount = 0;
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<app.callcheck.mobile.feature.callintercept.SearchTimeoutEnforcer.CountrySlaResult> results = null;
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<java.lang.String> failures = null;
        
        public SlaComplianceReport(int totalCountries, int passCount, int failCount, @org.jetbrains.annotations.NotNull()
        java.util.List<app.callcheck.mobile.feature.callintercept.SearchTimeoutEnforcer.CountrySlaResult> results, @org.jetbrains.annotations.NotNull()
        java.util.List<java.lang.String> failures) {
            super();
        }
        
        public final int getTotalCountries() {
            return 0;
        }
        
        public final int getPassCount() {
            return 0;
        }
        
        public final int getFailCount() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<app.callcheck.mobile.feature.callintercept.SearchTimeoutEnforcer.CountrySlaResult> getResults() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<java.lang.String> getFailures() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String toFormattedReport() {
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
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<app.callcheck.mobile.feature.callintercept.SearchTimeoutEnforcer.CountrySlaResult> component4() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<java.lang.String> component5() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.SearchTimeoutEnforcer.SlaComplianceReport copy(int totalCountries, int passCount, int failCount, @org.jetbrains.annotations.NotNull()
        java.util.List<app.callcheck.mobile.feature.callintercept.SearchTimeoutEnforcer.CountrySlaResult> results, @org.jetbrains.annotations.NotNull()
        java.util.List<java.lang.String> failures) {
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
     * SLA 검증 결과.
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u001d\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001BE\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\b\u0012\u0006\u0010\n\u001a\u00020\b\u0012\u0006\u0010\u000b\u001a\u00020\u0003\u0012\u0006\u0010\f\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\rJ\t\u0010\u0019\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u001b\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u001c\u001a\u00020\bH\u00c6\u0003J\t\u0010\u001d\u001a\u00020\bH\u00c6\u0003J\t\u0010\u001e\u001a\u00020\bH\u00c6\u0003J\t\u0010\u001f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010 \u001a\u00020\u0003H\u00c6\u0003JY\u0010!\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\b2\b\b\u0002\u0010\n\u001a\u00020\b2\b\b\u0002\u0010\u000b\u001a\u00020\u00032\b\b\u0002\u0010\f\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\"\u001a\u00020\u00032\b\u0010#\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010$\u001a\u00020\bH\u00d6\u0001J\u0006\u0010%\u001a\u00020&J\t\u0010\'\u001a\u00020&H\u00d6\u0001R\u0011\u0010\f\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\n\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\t\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0013R\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0011R\u0011\u0010\u000b\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u000fR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u000fR\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0013\u00a8\u0006("}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/SearchTimeoutEnforcer$SlaVerification;", "", "passed", "", "elapsedMs", "", "hardDeadlineMs", "resultCount", "", "enginesUsed", "enginesTimedOut", "hasResults", "allEnginesResponded", "(ZJJIIIZZ)V", "getAllEnginesResponded", "()Z", "getElapsedMs", "()J", "getEnginesTimedOut", "()I", "getEnginesUsed", "getHardDeadlineMs", "getHasResults", "getPassed", "getResultCount", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "copy", "equals", "other", "hashCode", "toReportLine", "", "toString", "call-intercept_debug"})
    public static final class SlaVerification {
        
        /**
         * SLA 통과 여부 (2초 이내 UI 표시)
         */
        private final boolean passed = false;
        
        /**
         * 실제 소요 시간 (ms)
         */
        private final long elapsedMs = 0L;
        
        /**
         * SLA 한계 시간 (ms)
         */
        private final long hardDeadlineMs = 0L;
        
        /**
         * 수집된 결과 수
         */
        private final int resultCount = 0;
        
        /**
         * 사용된 엔진 수
         */
        private final int enginesUsed = 0;
        
        /**
         * 타임아웃된 엔진 수
         */
        private final int enginesTimedOut = 0;
        
        /**
         * 결과 존재 여부
         */
        private final boolean hasResults = false;
        
        /**
         * 모든 엔진 응답 여부
         */
        private final boolean allEnginesResponded = false;
        
        public SlaVerification(boolean passed, long elapsedMs, long hardDeadlineMs, int resultCount, int enginesUsed, int enginesTimedOut, boolean hasResults, boolean allEnginesResponded) {
            super();
        }
        
        /**
         * SLA 통과 여부 (2초 이내 UI 표시)
         */
        public final boolean getPassed() {
            return false;
        }
        
        /**
         * 실제 소요 시간 (ms)
         */
        public final long getElapsedMs() {
            return 0L;
        }
        
        /**
         * SLA 한계 시간 (ms)
         */
        public final long getHardDeadlineMs() {
            return 0L;
        }
        
        /**
         * 수집된 결과 수
         */
        public final int getResultCount() {
            return 0;
        }
        
        /**
         * 사용된 엔진 수
         */
        public final int getEnginesUsed() {
            return 0;
        }
        
        /**
         * 타임아웃된 엔진 수
         */
        public final int getEnginesTimedOut() {
            return 0;
        }
        
        /**
         * 결과 존재 여부
         */
        public final boolean getHasResults() {
            return false;
        }
        
        /**
         * 모든 엔진 응답 여부
         */
        public final boolean getAllEnginesResponded() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String toReportLine() {
            return null;
        }
        
        public final boolean component1() {
            return false;
        }
        
        public final long component2() {
            return 0L;
        }
        
        public final long component3() {
            return 0L;
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
        
        public final boolean component7() {
            return false;
        }
        
        public final boolean component8() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.SearchTimeoutEnforcer.SlaVerification copy(boolean passed, long elapsedMs, long hardDeadlineMs, int resultCount, int enginesUsed, int enginesTimedOut, boolean hasResults, boolean allEnginesResponded) {
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