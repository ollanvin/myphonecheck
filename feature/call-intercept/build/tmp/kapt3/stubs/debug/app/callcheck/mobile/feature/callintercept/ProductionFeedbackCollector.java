package app.callcheck.mobile.feature.callintercept;

/**
 * 프로덕션 피드백 수집기.
 *
 * 빅테크 정석: Production Telemetry Feedback Loop
 * 출시 후 실사용 데이터를 수집하여 자동 보정에 활용.
 *
 * 수집 항목:
 *  1. 국가별 실패율 (검색 실패, 타임아웃, fallback)
 *  2. 검색 엔진별 응답 시간 + 실패율
 *  3. 2초 SLA 위반 건수
 *  4. 사용자 행동 (수신/거절/차단/무시)
 *  5. fallback 트리거 빈도
 *
 * 데이터 흐름:
 *  인터셉트 → 이벤트 기록 → 집계 → AutoPolicyAdjuster에 전달
 *
 * 100% 온디바이스. 서버 전송 없음.
 * SharedPreferences + 메모리 집계 기반.
 */
@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000j\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010%\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\n\b\u0007\u0018\u0000 +2\u00020\u0001:\u0007+,-./01B\u0007\b\u0007\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\u000f\u001a\u00020\u0010J\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00130\u0012J\u0006\u0010\u0014\u001a\u00020\u0015J\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00060\u0012J\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\t0\u0012J\u0010\u0010\u0018\u001a\u0004\u0018\u00010\u00062\u0006\u0010\u0019\u001a\u00020\u0005J\u0010\u0010\u001a\u001a\u0004\u0018\u00010\t2\u0006\u0010\u001b\u001a\u00020\bJ\u0016\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\f0\u00122\b\b\u0002\u0010\u001d\u001a\u00020\u001eJ\u000e\u0010\u001f\u001a\u00020\u00102\u0006\u0010 \u001a\u00020!J\u000e\u0010\"\u001a\u00020\u00102\u0006\u0010#\u001a\u00020\fJ\u001e\u0010$\u001a\u00020\u00102\u0006\u0010\u0019\u001a\u00020\u00052\u0006\u0010%\u001a\u00020&2\u0006\u0010\'\u001a\u00020(J\u000e\u0010)\u001a\u00020\u00102\u0006\u0010 \u001a\u00020!J\u0006\u0010*\u001a\u00020\u000eR\u001a\u0010\u0003\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00060\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\t0\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u00062"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/ProductionFeedbackCollector;", "", "()V", "countryStatsMap", "", "", "Lapp/callcheck/mobile/feature/callintercept/ProductionFeedbackCollector$CountryStats;", "engineStatsMap", "Lapp/callcheck/mobile/core/model/SearchEngine;", "Lapp/callcheck/mobile/feature/callintercept/ProductionFeedbackCollector$EngineStats;", "recentEvents", "Lkotlin/collections/ArrayDeque;", "Lapp/callcheck/mobile/feature/callintercept/ProductionFeedbackCollector$InterceptEvent;", "totalEventsCount", "", "clearAll", "", "detectProblematicCountries", "", "Lapp/callcheck/mobile/feature/callintercept/ProductionFeedbackCollector$ProblematicCountry;", "generateFeedbackSummary", "Lapp/callcheck/mobile/feature/callintercept/ProductionFeedbackCollector$FeedbackSummary;", "getAllCountryStats", "getAllEngineStats", "getCountryStats", "countryCode", "getEngineStats", "engine", "getRecentEvents", "limit", "", "persistStats", "context", "Landroid/content/Context;", "recordEvent", "event", "recordUserAction", "action", "Lapp/callcheck/mobile/feature/callintercept/ProductionFeedbackCollector$UserAction;", "agreedWithVerdict", "", "restoreStats", "totalEvents", "Companion", "CountryStats", "EngineStats", "FeedbackSummary", "InterceptEvent", "ProblematicCountry", "UserAction", "call-intercept_debug"})
public final class ProductionFeedbackCollector {
    
    /**
     * 국가별 통계 (메모리)
     */
    @org.jetbrains.annotations.NotNull()
    private final java.util.Map<java.lang.String, app.callcheck.mobile.feature.callintercept.ProductionFeedbackCollector.CountryStats> countryStatsMap = null;
    
    /**
     * 엔진별 통계 (메모리)
     */
    @org.jetbrains.annotations.NotNull()
    private final java.util.Map<app.callcheck.mobile.core.model.SearchEngine, app.callcheck.mobile.feature.callintercept.ProductionFeedbackCollector.EngineStats> engineStatsMap = null;
    
    /**
     * 최근 이벤트 버퍼 (최대 1000건, 순환)
     */
    @org.jetbrains.annotations.NotNull()
    private final kotlin.collections.ArrayDeque<app.callcheck.mobile.feature.callintercept.ProductionFeedbackCollector.InterceptEvent> recentEvents = null;
    
    /**
     * 총 이벤트 수
     */
    private long totalEventsCount = 0L;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String PREFS_NAME = "callcheck_feedback";
    private static final int MAX_RECENT_EVENTS = 1000;
    
    /**
     * 문제 감지 최소 샘플 수
     */
    private static final int MIN_SAMPLES_FOR_DETECTION = 10;
    
    /**
     * 임계값
     */
    private static final float SLA_VIOLATION_THRESHOLD = 0.05F;
    private static final float SEARCH_FAILURE_THRESHOLD = 0.1F;
    private static final float FALLBACK_RATE_THRESHOLD = 0.3F;
    private static final float DISAGREEMENT_THRESHOLD = 0.15F;
    @org.jetbrains.annotations.NotNull()
    public static final app.callcheck.mobile.feature.callintercept.ProductionFeedbackCollector.Companion Companion = null;
    
    @javax.inject.Inject()
    public ProductionFeedbackCollector() {
        super();
    }
    
    /**
     * 인터셉트 이벤트 기록.
     * 매 전화 인터셉트 완료 후 호출.
     */
    @kotlin.jvm.Synchronized()
    public final synchronized void recordEvent(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.callintercept.ProductionFeedbackCollector.InterceptEvent event) {
    }
    
    /**
     * 사용자 행동 기록 (인터셉트 후 별도 시점에 호출).
     */
    @kotlin.jvm.Synchronized()
    public final synchronized void recordUserAction(@org.jetbrains.annotations.NotNull()
    java.lang.String countryCode, @org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.callintercept.ProductionFeedbackCollector.UserAction action, boolean agreedWithVerdict) {
    }
    
    /**
     * 전체 이벤트 수
     */
    public final long totalEvents() {
        return 0L;
    }
    
    /**
     * 국가별 통계 조회
     */
    @org.jetbrains.annotations.Nullable()
    public final app.callcheck.mobile.feature.callintercept.ProductionFeedbackCollector.CountryStats getCountryStats(@org.jetbrains.annotations.NotNull()
    java.lang.String countryCode) {
        return null;
    }
    
    /**
     * 전체 국가 통계
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<app.callcheck.mobile.feature.callintercept.ProductionFeedbackCollector.CountryStats> getAllCountryStats() {
        return null;
    }
    
    /**
     * 엔진별 통계 조회
     */
    @org.jetbrains.annotations.Nullable()
    public final app.callcheck.mobile.feature.callintercept.ProductionFeedbackCollector.EngineStats getEngineStats(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.core.model.SearchEngine engine) {
        return null;
    }
    
    /**
     * 전체 엔진 통계
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<app.callcheck.mobile.feature.callintercept.ProductionFeedbackCollector.EngineStats> getAllEngineStats() {
        return null;
    }
    
    /**
     * 최근 이벤트
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<app.callcheck.mobile.feature.callintercept.ProductionFeedbackCollector.InterceptEvent> getRecentEvents(int limit) {
        return null;
    }
    
    /**
     * 문제 국가 감지.
     * SLA 위반율, 검색 실패율, 사용자 불일치율 기준.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<app.callcheck.mobile.feature.callintercept.ProductionFeedbackCollector.ProblematicCountry> detectProblematicCountries() {
        return null;
    }
    
    /**
     * AutoPolicyAdjuster 입력용 피드백 요약 생성.
     */
    @org.jetbrains.annotations.NotNull()
    public final app.callcheck.mobile.feature.callintercept.ProductionFeedbackCollector.FeedbackSummary generateFeedbackSummary() {
        return null;
    }
    
    /**
     * 집계 데이터를 SharedPreferences에 저장.
     * 앱 재시작 시에도 통계 유지.
     */
    public final void persistStats(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
    
    /**
     * 집계 데이터를 SharedPreferences에서 복원.
     */
    public final void restoreStats(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
    
    /**
     * 통계 초기화 (테스트용).
     */
    @kotlin.jvm.Synchronized()
    public final synchronized void clearAll() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0007X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\r"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/ProductionFeedbackCollector$Companion;", "", "()V", "DISAGREEMENT_THRESHOLD", "", "FALLBACK_RATE_THRESHOLD", "MAX_RECENT_EVENTS", "", "MIN_SAMPLES_FOR_DETECTION", "PREFS_NAME", "", "SEARCH_FAILURE_THRESHOLD", "SLA_VIOLATION_THRESHOLD", "call-intercept_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
    
    /**
     * 국가별 집계
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\t\n\u0002\b\f\n\u0002\u0010\u0007\n\u0002\b \n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\b\u0018\u00002\u00020\u0001Bq\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0005\u0012\b\b\u0002\u0010\b\u001a\u00020\u0005\u0012\b\b\u0002\u0010\t\u001a\u00020\n\u0012\b\b\u0002\u0010\u000b\u001a\u00020\u0005\u0012\b\b\u0002\u0010\f\u001a\u00020\u0005\u0012\b\b\u0002\u0010\r\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u000e\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u000f\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0010J\t\u0010+\u001a\u00020\u0003H\u00c6\u0003J\t\u0010,\u001a\u00020\u0005H\u00c6\u0003J\t\u0010-\u001a\u00020\u0005H\u00c6\u0003J\t\u0010.\u001a\u00020\u0005H\u00c6\u0003J\t\u0010/\u001a\u00020\u0005H\u00c6\u0003J\t\u00100\u001a\u00020\u0005H\u00c6\u0003J\t\u00101\u001a\u00020\u0005H\u00c6\u0003J\t\u00102\u001a\u00020\nH\u00c6\u0003J\t\u00103\u001a\u00020\u0005H\u00c6\u0003J\t\u00104\u001a\u00020\u0005H\u00c6\u0003J\t\u00105\u001a\u00020\u0005H\u00c6\u0003Jw\u00106\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\u00052\b\b\u0002\u0010\b\u001a\u00020\u00052\b\b\u0002\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\u00052\b\b\u0002\u0010\f\u001a\u00020\u00052\b\b\u0002\u0010\r\u001a\u00020\u00052\b\b\u0002\u0010\u000e\u001a\u00020\u00052\b\b\u0002\u0010\u000f\u001a\u00020\u0005H\u00c6\u0001J\u0013\u00107\u001a\u0002082\b\u00109\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010:\u001a\u00020\u0005H\u00d6\u0001J\t\u0010;\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0011\u001a\u00020\n8F\u00a2\u0006\u0006\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0011\u0010\u0016\u001a\u00020\u00178F\u00a2\u0006\u0006\u001a\u0004\b\u0018\u0010\u0019R\u0011\u0010\b\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001bR\u0011\u0010\u001c\u001a\u00020\u00178F\u00a2\u0006\u0006\u001a\u0004\b\u001d\u0010\u0019R\u0011\u0010\u001e\u001a\u00020\u00178F\u00a2\u0006\u0006\u001a\u0004\b\u001f\u0010\u0019R\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010\u001bR\u0011\u0010!\u001a\u00020\u00178F\u00a2\u0006\u0006\u001a\u0004\b\"\u0010\u0019R\u0011\u0010\u0007\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010\u001bR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010\u001bR\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b%\u0010\u0013R\u0011\u0010\u000b\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b&\u0010\u001bR\u0011\u0010\r\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\'\u0010\u001bR\u0011\u0010\u000e\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b(\u0010\u001bR\u0011\u0010\f\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b)\u0010\u001bR\u0011\u0010\u000f\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b*\u0010\u001b\u00a8\u0006<"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/ProductionFeedbackCollector$CountryStats;", "", "countryCode", "", "totalIntercepts", "", "searchFailures", "slaViolations", "fallbackCount", "totalLatencyMs", "", "userAccepts", "userRejects", "userBlocks", "userIgnores", "verdictDisagreements", "(Ljava/lang/String;IIIIJIIIII)V", "avgLatencyMs", "getAvgLatencyMs", "()J", "getCountryCode", "()Ljava/lang/String;", "disagreementRate", "", "getDisagreementRate", "()F", "getFallbackCount", "()I", "fallbackRate", "getFallbackRate", "searchFailureRate", "getSearchFailureRate", "getSearchFailures", "slaViolationRate", "getSlaViolationRate", "getSlaViolations", "getTotalIntercepts", "getTotalLatencyMs", "getUserAccepts", "getUserBlocks", "getUserIgnores", "getUserRejects", "getVerdictDisagreements", "component1", "component10", "component11", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "", "other", "hashCode", "toString", "call-intercept_debug"})
    public static final class CountryStats {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String countryCode = null;
        private final int totalIntercepts = 0;
        private final int searchFailures = 0;
        private final int slaViolations = 0;
        private final int fallbackCount = 0;
        private final long totalLatencyMs = 0L;
        private final int userAccepts = 0;
        private final int userRejects = 0;
        private final int userBlocks = 0;
        private final int userIgnores = 0;
        private final int verdictDisagreements = 0;
        
        public CountryStats(@org.jetbrains.annotations.NotNull()
        java.lang.String countryCode, int totalIntercepts, int searchFailures, int slaViolations, int fallbackCount, long totalLatencyMs, int userAccepts, int userRejects, int userBlocks, int userIgnores, int verdictDisagreements) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getCountryCode() {
            return null;
        }
        
        public final int getTotalIntercepts() {
            return 0;
        }
        
        public final int getSearchFailures() {
            return 0;
        }
        
        public final int getSlaViolations() {
            return 0;
        }
        
        public final int getFallbackCount() {
            return 0;
        }
        
        public final long getTotalLatencyMs() {
            return 0L;
        }
        
        public final int getUserAccepts() {
            return 0;
        }
        
        public final int getUserRejects() {
            return 0;
        }
        
        public final int getUserBlocks() {
            return 0;
        }
        
        public final int getUserIgnores() {
            return 0;
        }
        
        public final int getVerdictDisagreements() {
            return 0;
        }
        
        public final float getSearchFailureRate() {
            return 0.0F;
        }
        
        public final float getSlaViolationRate() {
            return 0.0F;
        }
        
        public final float getFallbackRate() {
            return 0.0F;
        }
        
        public final long getAvgLatencyMs() {
            return 0L;
        }
        
        public final float getDisagreementRate() {
            return 0.0F;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component1() {
            return null;
        }
        
        public final int component10() {
            return 0;
        }
        
        public final int component11() {
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
        
        public final long component6() {
            return 0L;
        }
        
        public final int component7() {
            return 0;
        }
        
        public final int component8() {
            return 0;
        }
        
        public final int component9() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.ProductionFeedbackCollector.CountryStats copy(@org.jetbrains.annotations.NotNull()
        java.lang.String countryCode, int totalIntercepts, int searchFailures, int slaViolations, int fallbackCount, long totalLatencyMs, int userAccepts, int userRejects, int userBlocks, int userIgnores, int verdictDisagreements) {
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
     * 엔진별 집계
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\b\n\u0002\u0010\u0007\n\u0002\b\u0010\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B5\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0007\u001a\u00020\b\u0012\b\b\u0002\u0010\t\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\nJ\t\u0010\u001b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001c\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u001d\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u001e\u001a\u00020\bH\u00c6\u0003J\t\u0010\u001f\u001a\u00020\u0005H\u00c6\u0003J;\u0010 \u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010!\u001a\u00020\"2\b\u0010#\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010$\u001a\u00020\u0005H\u00d6\u0001J\t\u0010%\u001a\u00020&H\u00d6\u0001R\u0011\u0010\u000b\u001a\u00020\b8F\u00a2\u0006\u0006\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0010\u001a\u00020\u00118F\u00a2\u0006\u0006\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0011\u0010\u0016\u001a\u00020\u00118F\u00a2\u0006\u0006\u001a\u0004\b\u0017\u0010\u0013R\u0011\u0010\t\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0015R\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\rR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0015\u00a8\u0006\'"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/ProductionFeedbackCollector$EngineStats;", "", "engine", "Lapp/callcheck/mobile/core/model/SearchEngine;", "totalRequests", "", "failures", "totalLatencyMs", "", "timeouts", "(Lapp/callcheck/mobile/core/model/SearchEngine;IIJI)V", "avgLatencyMs", "getAvgLatencyMs", "()J", "getEngine", "()Lapp/callcheck/mobile/core/model/SearchEngine;", "failureRate", "", "getFailureRate", "()F", "getFailures", "()I", "timeoutRate", "getTimeoutRate", "getTimeouts", "getTotalLatencyMs", "getTotalRequests", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "", "other", "hashCode", "toString", "", "call-intercept_debug"})
    public static final class EngineStats {
        @org.jetbrains.annotations.NotNull()
        private final app.callcheck.mobile.core.model.SearchEngine engine = null;
        private final int totalRequests = 0;
        private final int failures = 0;
        private final long totalLatencyMs = 0L;
        private final int timeouts = 0;
        
        public EngineStats(@org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.core.model.SearchEngine engine, int totalRequests, int failures, long totalLatencyMs, int timeouts) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.core.model.SearchEngine getEngine() {
            return null;
        }
        
        public final int getTotalRequests() {
            return 0;
        }
        
        public final int getFailures() {
            return 0;
        }
        
        public final long getTotalLatencyMs() {
            return 0L;
        }
        
        public final int getTimeouts() {
            return 0;
        }
        
        public final float getFailureRate() {
            return 0.0F;
        }
        
        public final long getAvgLatencyMs() {
            return 0L;
        }
        
        public final float getTimeoutRate() {
            return 0.0F;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.core.model.SearchEngine component1() {
            return null;
        }
        
        public final int component2() {
            return 0;
        }
        
        public final int component3() {
            return 0;
        }
        
        public final long component4() {
            return 0L;
        }
        
        public final int component5() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.ProductionFeedbackCollector.EngineStats copy(@org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.core.model.SearchEngine engine, int totalRequests, int failures, long totalLatencyMs, int timeouts) {
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0014\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001BA\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007\u0012\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0007\u0012\u0006\u0010\u000b\u001a\u00020\f\u0012\u0006\u0010\r\u001a\u00020\f\u00a2\u0006\u0002\u0010\u000eJ\t\u0010\u0019\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\u0005H\u00c6\u0003J\u000f\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\b0\u0007H\u00c6\u0003J\u000f\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\n0\u0007H\u00c6\u0003J\t\u0010\u001d\u001a\u00020\fH\u00c6\u0003J\t\u0010\u001e\u001a\u00020\fH\u00c6\u0003JQ\u0010\u001f\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\u000e\b\u0002\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u00072\b\b\u0002\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\r\u001a\u00020\fH\u00c6\u0001J\u0013\u0010 \u001a\u00020!2\b\u0010\"\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010#\u001a\u00020\u0005H\u00d6\u0001J\u0006\u0010$\u001a\u00020%J\t\u0010&\u001a\u00020%H\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\r\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u000b\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0012R\u0017\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0017\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0015R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018\u00a8\u0006\'"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/ProductionFeedbackCollector$FeedbackSummary;", "", "totalEvents", "", "countryCount", "", "problematicCountries", "", "Lapp/callcheck/mobile/feature/callintercept/ProductionFeedbackCollector$ProblematicCountry;", "topFailingEngines", "Lapp/callcheck/mobile/feature/callintercept/ProductionFeedbackCollector$EngineStats;", "overallSlaViolationRate", "", "overallSearchFailureRate", "(JILjava/util/List;Ljava/util/List;FF)V", "getCountryCount", "()I", "getOverallSearchFailureRate", "()F", "getOverallSlaViolationRate", "getProblematicCountries", "()Ljava/util/List;", "getTopFailingEngines", "getTotalEvents", "()J", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "equals", "", "other", "hashCode", "toJarvisFormat", "", "toString", "call-intercept_debug"})
    public static final class FeedbackSummary {
        private final long totalEvents = 0L;
        private final int countryCount = 0;
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<app.callcheck.mobile.feature.callintercept.ProductionFeedbackCollector.ProblematicCountry> problematicCountries = null;
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<app.callcheck.mobile.feature.callintercept.ProductionFeedbackCollector.EngineStats> topFailingEngines = null;
        private final float overallSlaViolationRate = 0.0F;
        private final float overallSearchFailureRate = 0.0F;
        
        public FeedbackSummary(long totalEvents, int countryCount, @org.jetbrains.annotations.NotNull()
        java.util.List<app.callcheck.mobile.feature.callintercept.ProductionFeedbackCollector.ProblematicCountry> problematicCountries, @org.jetbrains.annotations.NotNull()
        java.util.List<app.callcheck.mobile.feature.callintercept.ProductionFeedbackCollector.EngineStats> topFailingEngines, float overallSlaViolationRate, float overallSearchFailureRate) {
            super();
        }
        
        public final long getTotalEvents() {
            return 0L;
        }
        
        public final int getCountryCount() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<app.callcheck.mobile.feature.callintercept.ProductionFeedbackCollector.ProblematicCountry> getProblematicCountries() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<app.callcheck.mobile.feature.callintercept.ProductionFeedbackCollector.EngineStats> getTopFailingEngines() {
            return null;
        }
        
        public final float getOverallSlaViolationRate() {
            return 0.0F;
        }
        
        public final float getOverallSearchFailureRate() {
            return 0.0F;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String toJarvisFormat() {
            return null;
        }
        
        public final long component1() {
            return 0L;
        }
        
        public final int component2() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<app.callcheck.mobile.feature.callintercept.ProductionFeedbackCollector.ProblematicCountry> component3() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<app.callcheck.mobile.feature.callintercept.ProductionFeedbackCollector.EngineStats> component4() {
            return null;
        }
        
        public final float component5() {
            return 0.0F;
        }
        
        public final float component6() {
            return 0.0F;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.ProductionFeedbackCollector.FeedbackSummary copy(long totalEvents, int countryCount, @org.jetbrains.annotations.NotNull()
        java.util.List<app.callcheck.mobile.feature.callintercept.ProductionFeedbackCollector.ProblematicCountry> problematicCountries, @org.jetbrains.annotations.NotNull()
        java.util.List<app.callcheck.mobile.feature.callintercept.ProductionFeedbackCollector.EngineStats> topFailingEngines, float overallSlaViolationRate, float overallSearchFailureRate) {
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
     * 인터셉트 이벤트
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b,\b\u0086\b\u0018\u00002\u00020\u0001B{\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\b0\n\u0012\u0006\u0010\u000b\u001a\u00020\u0003\u0012\u0006\u0010\f\u001a\u00020\r\u0012\u0006\u0010\u000e\u001a\u00020\u000f\u0012\u0006\u0010\u0010\u001a\u00020\r\u0012\u0006\u0010\u0011\u001a\u00020\r\u0012\u0006\u0010\u0012\u001a\u00020\u0005\u0012\n\b\u0002\u0010\u0013\u001a\u0004\u0018\u00010\u0014\u0012\n\b\u0002\u0010\u0015\u001a\u0004\u0018\u00010\r\u00a2\u0006\u0002\u0010\u0016J\t\u0010-\u001a\u00020\u0003H\u00c6\u0003J\t\u0010.\u001a\u00020\rH\u00c6\u0003J\t\u0010/\u001a\u00020\u0005H\u00c6\u0003J\u000b\u00100\u001a\u0004\u0018\u00010\u0014H\u00c6\u0003J\u0010\u00101\u001a\u0004\u0018\u00010\rH\u00c6\u0003\u00a2\u0006\u0002\u0010*J\t\u00102\u001a\u00020\u0005H\u00c6\u0003J\t\u00103\u001a\u00020\u0005H\u00c6\u0003J\t\u00104\u001a\u00020\bH\u00c6\u0003J\u000f\u00105\u001a\b\u0012\u0004\u0012\u00020\b0\nH\u00c6\u0003J\t\u00106\u001a\u00020\u0003H\u00c6\u0003J\t\u00107\u001a\u00020\rH\u00c6\u0003J\t\u00108\u001a\u00020\u000fH\u00c6\u0003J\t\u00109\u001a\u00020\rH\u00c6\u0003J\u009a\u0001\u0010:\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\b2\u000e\b\u0002\u0010\t\u001a\b\u0012\u0004\u0012\u00020\b0\n2\b\b\u0002\u0010\u000b\u001a\u00020\u00032\b\b\u0002\u0010\f\u001a\u00020\r2\b\b\u0002\u0010\u000e\u001a\u00020\u000f2\b\b\u0002\u0010\u0010\u001a\u00020\r2\b\b\u0002\u0010\u0011\u001a\u00020\r2\b\b\u0002\u0010\u0012\u001a\u00020\u00052\n\b\u0002\u0010\u0013\u001a\u0004\u0018\u00010\u00142\n\b\u0002\u0010\u0015\u001a\u0004\u0018\u00010\rH\u00c6\u0001\u00a2\u0006\u0002\u0010;J\u0013\u0010<\u001a\u00020\r2\b\u0010=\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010>\u001a\u00020\u000fH\u00d6\u0001J\t\u0010?\u001a\u00020\u0005H\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0017\u0010\t\u001a\b\u0012\u0004\u0012\u00020\b0\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u0011\u0010\u0010\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001cR\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u0018R\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001fR\u0011\u0010\u000e\u001a\u00020\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010!R\u0011\u0010\u0011\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010\u001cR\u0011\u0010\u000b\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010$R\u0011\u0010\f\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b%\u0010\u001cR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b&\u0010$R\u0013\u0010\u0013\u001a\u0004\u0018\u00010\u0014\u00a2\u0006\b\n\u0000\u001a\u0004\b\'\u0010(R\u0015\u0010\u0015\u001a\u0004\u0018\u00010\r\u00a2\u0006\n\n\u0002\u0010+\u001a\u0004\b)\u0010*R\u0011\u0010\u0012\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b,\u0010\u0018\u00a8\u0006@"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/ProductionFeedbackCollector$InterceptEvent;", "", "timestampMs", "", "countryCode", "", "phoneNumber", "primaryEngine", "Lapp/callcheck/mobile/core/model/SearchEngine;", "enginesUsed", "", "searchLatencyMs", "slaPassed", "", "resultCount", "", "fallbackTriggered", "searchFailed", "verdict", "userAction", "Lapp/callcheck/mobile/feature/callintercept/ProductionFeedbackCollector$UserAction;", "userAgreedWithVerdict", "(JLjava/lang/String;Ljava/lang/String;Lapp/callcheck/mobile/core/model/SearchEngine;Ljava/util/List;JZIZZLjava/lang/String;Lapp/callcheck/mobile/feature/callintercept/ProductionFeedbackCollector$UserAction;Ljava/lang/Boolean;)V", "getCountryCode", "()Ljava/lang/String;", "getEnginesUsed", "()Ljava/util/List;", "getFallbackTriggered", "()Z", "getPhoneNumber", "getPrimaryEngine", "()Lapp/callcheck/mobile/core/model/SearchEngine;", "getResultCount", "()I", "getSearchFailed", "getSearchLatencyMs", "()J", "getSlaPassed", "getTimestampMs", "getUserAction", "()Lapp/callcheck/mobile/feature/callintercept/ProductionFeedbackCollector$UserAction;", "getUserAgreedWithVerdict", "()Ljava/lang/Boolean;", "Ljava/lang/Boolean;", "getVerdict", "component1", "component10", "component11", "component12", "component13", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "(JLjava/lang/String;Ljava/lang/String;Lapp/callcheck/mobile/core/model/SearchEngine;Ljava/util/List;JZIZZLjava/lang/String;Lapp/callcheck/mobile/feature/callintercept/ProductionFeedbackCollector$UserAction;Ljava/lang/Boolean;)Lapp/callcheck/mobile/feature/callintercept/ProductionFeedbackCollector$InterceptEvent;", "equals", "other", "hashCode", "toString", "call-intercept_debug"})
    public static final class InterceptEvent {
        private final long timestampMs = 0L;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String countryCode = null;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String phoneNumber = null;
        
        /**
         * 사용된 1순위 검색 엔진
         */
        @org.jetbrains.annotations.NotNull()
        private final app.callcheck.mobile.core.model.SearchEngine primaryEngine = null;
        
        /**
         * 실제 사용된 엔진 (fallback 포함)
         */
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<app.callcheck.mobile.core.model.SearchEngine> enginesUsed = null;
        
        /**
         * 검색 소요 시간 (ms)
         */
        private final long searchLatencyMs = 0L;
        
        /**
         * 2초 SLA 통과 여부
         */
        private final boolean slaPassed = false;
        
        /**
         * 검색 결과 수
         */
        private final int resultCount = 0;
        
        /**
         * fallback 발생 여부
         */
        private final boolean fallbackTriggered = false;
        
        /**
         * 검색 실패 여부 (결과 0건)
         */
        private final boolean searchFailed = false;
        
        /**
         * 판정 결과 (SPAM, SAFE, UNKNOWN 등)
         */
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String verdict = null;
        
        /**
         * 사용자 행동 (null = 아직 행동 안 함)
         */
        @org.jetbrains.annotations.Nullable()
        private final app.callcheck.mobile.feature.callintercept.ProductionFeedbackCollector.UserAction userAction = null;
        
        /**
         * 사용자가 판정에 동의했는지 (null = 미확인)
         */
        @org.jetbrains.annotations.Nullable()
        private final java.lang.Boolean userAgreedWithVerdict = null;
        
        public InterceptEvent(long timestampMs, @org.jetbrains.annotations.NotNull()
        java.lang.String countryCode, @org.jetbrains.annotations.NotNull()
        java.lang.String phoneNumber, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.core.model.SearchEngine primaryEngine, @org.jetbrains.annotations.NotNull()
        java.util.List<? extends app.callcheck.mobile.core.model.SearchEngine> enginesUsed, long searchLatencyMs, boolean slaPassed, int resultCount, boolean fallbackTriggered, boolean searchFailed, @org.jetbrains.annotations.NotNull()
        java.lang.String verdict, @org.jetbrains.annotations.Nullable()
        app.callcheck.mobile.feature.callintercept.ProductionFeedbackCollector.UserAction userAction, @org.jetbrains.annotations.Nullable()
        java.lang.Boolean userAgreedWithVerdict) {
            super();
        }
        
        public final long getTimestampMs() {
            return 0L;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getCountryCode() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getPhoneNumber() {
            return null;
        }
        
        /**
         * 사용된 1순위 검색 엔진
         */
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.core.model.SearchEngine getPrimaryEngine() {
            return null;
        }
        
        /**
         * 실제 사용된 엔진 (fallback 포함)
         */
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<app.callcheck.mobile.core.model.SearchEngine> getEnginesUsed() {
            return null;
        }
        
        /**
         * 검색 소요 시간 (ms)
         */
        public final long getSearchLatencyMs() {
            return 0L;
        }
        
        /**
         * 2초 SLA 통과 여부
         */
        public final boolean getSlaPassed() {
            return false;
        }
        
        /**
         * 검색 결과 수
         */
        public final int getResultCount() {
            return 0;
        }
        
        /**
         * fallback 발생 여부
         */
        public final boolean getFallbackTriggered() {
            return false;
        }
        
        /**
         * 검색 실패 여부 (결과 0건)
         */
        public final boolean getSearchFailed() {
            return false;
        }
        
        /**
         * 판정 결과 (SPAM, SAFE, UNKNOWN 등)
         */
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getVerdict() {
            return null;
        }
        
        /**
         * 사용자 행동 (null = 아직 행동 안 함)
         */
        @org.jetbrains.annotations.Nullable()
        public final app.callcheck.mobile.feature.callintercept.ProductionFeedbackCollector.UserAction getUserAction() {
            return null;
        }
        
        /**
         * 사용자가 판정에 동의했는지 (null = 미확인)
         */
        @org.jetbrains.annotations.Nullable()
        public final java.lang.Boolean getUserAgreedWithVerdict() {
            return null;
        }
        
        public final long component1() {
            return 0L;
        }
        
        public final boolean component10() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component11() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final app.callcheck.mobile.feature.callintercept.ProductionFeedbackCollector.UserAction component12() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.Boolean component13() {
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
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.core.model.SearchEngine component4() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<app.callcheck.mobile.core.model.SearchEngine> component5() {
            return null;
        }
        
        public final long component6() {
            return 0L;
        }
        
        public final boolean component7() {
            return false;
        }
        
        public final int component8() {
            return 0;
        }
        
        public final boolean component9() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.ProductionFeedbackCollector.InterceptEvent copy(long timestampMs, @org.jetbrains.annotations.NotNull()
        java.lang.String countryCode, @org.jetbrains.annotations.NotNull()
        java.lang.String phoneNumber, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.core.model.SearchEngine primaryEngine, @org.jetbrains.annotations.NotNull()
        java.util.List<? extends app.callcheck.mobile.core.model.SearchEngine> enginesUsed, long searchLatencyMs, boolean slaPassed, int resultCount, boolean fallbackTriggered, boolean searchFailed, @org.jetbrains.annotations.NotNull()
        java.lang.String verdict, @org.jetbrains.annotations.Nullable()
        app.callcheck.mobile.feature.callintercept.ProductionFeedbackCollector.UserAction userAction, @org.jetbrains.annotations.Nullable()
        java.lang.Boolean userAgreedWithVerdict) {
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B#\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00030\u0007\u00a2\u0006\u0002\u0010\bJ\t\u0010\u000f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0010\u001a\u00020\u0005H\u00c6\u0003J\u000f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00030\u0007H\u00c6\u0003J-\u0010\u0012\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00030\u0007H\u00c6\u0001J\u0013\u0010\u0013\u001a\u00020\u00142\b\u0010\u0015\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0016\u001a\u00020\u0017H\u00d6\u0001J\t\u0010\u0018\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0017\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00030\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000e\u00a8\u0006\u0019"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/ProductionFeedbackCollector$ProblematicCountry;", "", "countryCode", "", "stats", "Lapp/callcheck/mobile/feature/callintercept/ProductionFeedbackCollector$CountryStats;", "issues", "", "(Ljava/lang/String;Lapp/callcheck/mobile/feature/callintercept/ProductionFeedbackCollector$CountryStats;Ljava/util/List;)V", "getCountryCode", "()Ljava/lang/String;", "getIssues", "()Ljava/util/List;", "getStats", "()Lapp/callcheck/mobile/feature/callintercept/ProductionFeedbackCollector$CountryStats;", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "", "toString", "call-intercept_debug"})
    public static final class ProblematicCountry {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String countryCode = null;
        @org.jetbrains.annotations.NotNull()
        private final app.callcheck.mobile.feature.callintercept.ProductionFeedbackCollector.CountryStats stats = null;
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<java.lang.String> issues = null;
        
        public ProblematicCountry(@org.jetbrains.annotations.NotNull()
        java.lang.String countryCode, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.ProductionFeedbackCollector.CountryStats stats, @org.jetbrains.annotations.NotNull()
        java.util.List<java.lang.String> issues) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getCountryCode() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.ProductionFeedbackCollector.CountryStats getStats() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<java.lang.String> getIssues() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.ProductionFeedbackCollector.CountryStats component2() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<java.lang.String> component3() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.ProductionFeedbackCollector.ProblematicCountry copy(@org.jetbrains.annotations.NotNull()
        java.lang.String countryCode, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.ProductionFeedbackCollector.CountryStats stats, @org.jetbrains.annotations.NotNull()
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
    
    /**
     * 사용자 행동
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\u000e\n\u0002\b\b\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u000f\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\n\u00a8\u0006\u000b"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/ProductionFeedbackCollector$UserAction;", "", "label", "", "(Ljava/lang/String;ILjava/lang/String;)V", "getLabel", "()Ljava/lang/String;", "ACCEPT", "REJECT", "BLOCK", "IGNORE", "call-intercept_debug"})
    public static enum UserAction {
        /*public static final*/ ACCEPT /* = new ACCEPT(null) */,
        /*public static final*/ REJECT /* = new REJECT(null) */,
        /*public static final*/ BLOCK /* = new BLOCK(null) */,
        /*public static final*/ IGNORE /* = new IGNORE(null) */;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String label = null;
        
        UserAction(java.lang.String label) {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getLabel() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public static kotlin.enums.EnumEntries<app.callcheck.mobile.feature.callintercept.ProductionFeedbackCollector.UserAction> getEntries() {
            return null;
        }
    }
}