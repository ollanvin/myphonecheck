package app.callcheck.mobile.feature.callintercept;

/**
 * 글로벌 출시 대시보드.
 *
 * 자비스 기준:
 * "대표님이 봐야 할 것은 코드가 아니라:
 * 국가별 PASS 수, SLA 통과율, 검색 실패율, Tier별 상태, 위험 국가 목록.
 * 이걸 한 화면에서 보게 해야 합니다."
 *
 * 이 클래스는 모든 검증/모니터링 모듈의 결과를 수집하여
 * 대표님에게 보여줄 단일 대시보드 데이터를 생성.
 *
 * 100% 온디바이스. 서버 전송 없음.
 */
@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0007\u0018\u00002\u00020\u0001:\u0002\u0012\u0013B/\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u000b\u00a2\u0006\u0002\u0010\fJ\u000e\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0010J\u0006\u0010\u0011\u001a\u00020\u0010R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0014"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/GlobalLaunchDashboard;", "", "registry", "Lapp/callcheck/mobile/feature/callintercept/GlobalSearchProviderRegistry;", "complianceValidator", "Lapp/callcheck/mobile/feature/callintercept/CountryComplianceValidator;", "launchLock", "Lapp/callcheck/mobile/feature/callintercept/LaunchReadinessLock;", "circuitBreaker", "Lapp/callcheck/mobile/feature/callintercept/OperationalCircuitBreaker;", "feedbackCollector", "Lapp/callcheck/mobile/feature/callintercept/ProductionFeedbackCollector;", "(Lapp/callcheck/mobile/feature/callintercept/GlobalSearchProviderRegistry;Lapp/callcheck/mobile/feature/callintercept/CountryComplianceValidator;Lapp/callcheck/mobile/feature/callintercept/LaunchReadinessLock;Lapp/callcheck/mobile/feature/callintercept/OperationalCircuitBreaker;Lapp/callcheck/mobile/feature/callintercept/ProductionFeedbackCollector;)V", "formatDashboard", "", "data", "Lapp/callcheck/mobile/feature/callintercept/GlobalLaunchDashboard$DashboardData;", "generateDashboard", "DashboardData", "ProblematicCountryInfo", "call-intercept_debug"})
public final class GlobalLaunchDashboard {
    @org.jetbrains.annotations.NotNull()
    private final app.callcheck.mobile.feature.callintercept.GlobalSearchProviderRegistry registry = null;
    @org.jetbrains.annotations.NotNull()
    private final app.callcheck.mobile.feature.callintercept.CountryComplianceValidator complianceValidator = null;
    @org.jetbrains.annotations.NotNull()
    private final app.callcheck.mobile.feature.callintercept.LaunchReadinessLock launchLock = null;
    @org.jetbrains.annotations.NotNull()
    private final app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker circuitBreaker = null;
    @org.jetbrains.annotations.NotNull()
    private final app.callcheck.mobile.feature.callintercept.ProductionFeedbackCollector feedbackCollector = null;
    
    @javax.inject.Inject()
    public GlobalLaunchDashboard(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.callintercept.GlobalSearchProviderRegistry registry, @org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.callintercept.CountryComplianceValidator complianceValidator, @org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.callintercept.LaunchReadinessLock launchLock, @org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.callintercept.OperationalCircuitBreaker circuitBreaker, @org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.callintercept.ProductionFeedbackCollector feedbackCollector) {
        super();
    }
    
    /**
     * 대시보드 데이터 생성.
     * 모든 모듈에서 최신 데이터를 수집.
     */
    @org.jetbrains.annotations.NotNull()
    public final app.callcheck.mobile.feature.callintercept.GlobalLaunchDashboard.DashboardData generateDashboard() {
        return null;
    }
    
    /**
     * 대시보드 포맷 출력.
     * 대표님 + 자비스님이 볼 최종 형태.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String formatDashboard(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.callintercept.GlobalLaunchDashboard.DashboardData data) {
        return null;
    }
    
    /**
     * 대시보드 전체 데이터.
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0005\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b8\b\u0086\b\u0018\u00002\u00020\u0001B\u00bb\u0001\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0012\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00050\u0007\u0012\u0006\u0010\t\u001a\u00020\u0005\u0012\u0006\u0010\n\u001a\u00020\u0005\u0012\u0006\u0010\u000b\u001a\u00020\f\u0012\u0006\u0010\r\u001a\u00020\u000e\u0012\u0006\u0010\u000f\u001a\u00020\u000e\u0012\u0006\u0010\u0010\u001a\u00020\u0005\u0012\u0006\u0010\u0011\u001a\u00020\u0005\u0012\u0006\u0010\u0012\u001a\u00020\u0005\u0012\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\b0\u0014\u0012\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\b0\u0014\u0012\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00170\u0014\u0012\u0006\u0010\u0018\u001a\u00020\u0003\u0012\u0006\u0010\u0019\u001a\u00020\u0003\u0012\u0006\u0010\u001a\u001a\u00020\u0003\u0012\u0006\u0010\u001b\u001a\u00020\f\u0012\u0006\u0010\u001c\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u001dJ\t\u00107\u001a\u00020\u0003H\u00c6\u0003J\t\u00108\u001a\u00020\u0005H\u00c6\u0003J\t\u00109\u001a\u00020\u0005H\u00c6\u0003J\u000f\u0010:\u001a\b\u0012\u0004\u0012\u00020\b0\u0014H\u00c6\u0003J\u000f\u0010;\u001a\b\u0012\u0004\u0012\u00020\b0\u0014H\u00c6\u0003J\u000f\u0010<\u001a\b\u0012\u0004\u0012\u00020\u00170\u0014H\u00c6\u0003J\t\u0010=\u001a\u00020\u0003H\u00c6\u0003J\t\u0010>\u001a\u00020\u0003H\u00c6\u0003J\t\u0010?\u001a\u00020\u0003H\u00c6\u0003J\t\u0010@\u001a\u00020\fH\u00c6\u0003J\t\u0010A\u001a\u00020\u0005H\u00c6\u0003J\t\u0010B\u001a\u00020\u0005H\u00c6\u0003J\u0015\u0010C\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00050\u0007H\u00c6\u0003J\t\u0010D\u001a\u00020\u0005H\u00c6\u0003J\t\u0010E\u001a\u00020\u0005H\u00c6\u0003J\t\u0010F\u001a\u00020\fH\u00c6\u0003J\t\u0010G\u001a\u00020\u000eH\u00c6\u0003J\t\u0010H\u001a\u00020\u000eH\u00c6\u0003J\t\u0010I\u001a\u00020\u0005H\u00c6\u0003J\u00e5\u0001\u0010J\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\u0014\b\u0002\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00050\u00072\b\b\u0002\u0010\t\u001a\u00020\u00052\b\b\u0002\u0010\n\u001a\u00020\u00052\b\b\u0002\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\r\u001a\u00020\u000e2\b\b\u0002\u0010\u000f\u001a\u00020\u000e2\b\b\u0002\u0010\u0010\u001a\u00020\u00052\b\b\u0002\u0010\u0011\u001a\u00020\u00052\b\b\u0002\u0010\u0012\u001a\u00020\u00052\u000e\b\u0002\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\b0\u00142\u000e\b\u0002\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\b0\u00142\u000e\b\u0002\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00170\u00142\b\b\u0002\u0010\u0018\u001a\u00020\u00032\b\b\u0002\u0010\u0019\u001a\u00020\u00032\b\b\u0002\u0010\u001a\u001a\u00020\u00032\b\b\u0002\u0010\u001b\u001a\u00020\f2\b\b\u0002\u0010\u001c\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010K\u001a\u00020\u00032\b\u0010L\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010M\u001a\u00020\u0005H\u00d6\u0001J\t\u0010N\u001a\u00020\bH\u00d6\u0001R\u0011\u0010\u0010\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001fR\u0011\u0010\u0012\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010\u001fR\u0011\u0010\u0011\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\u001fR\u0011\u0010\n\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010\u001fR\u0011\u0010\t\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010\u001fR\u0017\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\b0\u0014\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010%R\u0011\u0010\u001c\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b&\u0010\u001fR\u0011\u0010\u000b\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\'\u0010(R\u0011\u0010\u0018\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b)\u0010*R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b+\u0010*R\u0017\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\b0\u0014\u00a2\u0006\b\n\u0000\u001a\u0004\b,\u0010%R\u0011\u0010\u000f\u001a\u00020\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b-\u0010.R\u0017\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00170\u0014\u00a2\u0006\b\n\u0000\u001a\u0004\b/\u0010%R\u0011\u0010\u001a\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b0\u0010*R\u0011\u0010\r\u001a\u00020\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b1\u0010.R\u0011\u0010\u0019\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b2\u0010*R\u001d\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00050\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b3\u00104R\u0011\u0010\u001b\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b5\u0010(R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b6\u0010\u001f\u00a8\u0006O"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/GlobalLaunchDashboard$DashboardData;", "", "launchReady", "", "totalRegisteredCountries", "", "tierBreakdown", "", "", "compliancePassCount", "complianceFailCount", "globalSlaDeadlineMs", "", "slaComplianceRate", "", "overallSearchFailureRate", "circuitClosedCount", "circuitOpenCount", "circuitEmergencyCount", "openCountries", "", "emergencyCountries", "problematicCountries", "Lapp/callcheck/mobile/feature/callintercept/GlobalLaunchDashboard$ProblematicCountryInfo;", "hardRulesLocked", "slaLocked", "safeExpressionsLocked", "totalFeedbackEvents", "feedbackCountryCount", "(ZILjava/util/Map;IIJFFIIILjava/util/List;Ljava/util/List;Ljava/util/List;ZZZJI)V", "getCircuitClosedCount", "()I", "getCircuitEmergencyCount", "getCircuitOpenCount", "getComplianceFailCount", "getCompliancePassCount", "getEmergencyCountries", "()Ljava/util/List;", "getFeedbackCountryCount", "getGlobalSlaDeadlineMs", "()J", "getHardRulesLocked", "()Z", "getLaunchReady", "getOpenCountries", "getOverallSearchFailureRate", "()F", "getProblematicCountries", "getSafeExpressionsLocked", "getSlaComplianceRate", "getSlaLocked", "getTierBreakdown", "()Ljava/util/Map;", "getTotalFeedbackEvents", "getTotalRegisteredCountries", "component1", "component10", "component11", "component12", "component13", "component14", "component15", "component16", "component17", "component18", "component19", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "other", "hashCode", "toString", "call-intercept_debug"})
    public static final class DashboardData {
        
        /**
         * 최종 출시 준비 상태
         */
        private final boolean launchReady = false;
        private final int totalRegisteredCountries = 0;
        @org.jetbrains.annotations.NotNull()
        private final java.util.Map<java.lang.String, java.lang.Integer> tierBreakdown = null;
        private final int compliancePassCount = 0;
        private final int complianceFailCount = 0;
        private final long globalSlaDeadlineMs = 0L;
        private final float slaComplianceRate = 0.0F;
        private final float overallSearchFailureRate = 0.0F;
        private final int circuitClosedCount = 0;
        private final int circuitOpenCount = 0;
        private final int circuitEmergencyCount = 0;
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<java.lang.String> openCountries = null;
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<java.lang.String> emergencyCountries = null;
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<app.callcheck.mobile.feature.callintercept.GlobalLaunchDashboard.ProblematicCountryInfo> problematicCountries = null;
        private final boolean hardRulesLocked = false;
        private final boolean slaLocked = false;
        private final boolean safeExpressionsLocked = false;
        private final long totalFeedbackEvents = 0L;
        private final int feedbackCountryCount = 0;
        
        public DashboardData(boolean launchReady, int totalRegisteredCountries, @org.jetbrains.annotations.NotNull()
        java.util.Map<java.lang.String, java.lang.Integer> tierBreakdown, int compliancePassCount, int complianceFailCount, long globalSlaDeadlineMs, float slaComplianceRate, float overallSearchFailureRate, int circuitClosedCount, int circuitOpenCount, int circuitEmergencyCount, @org.jetbrains.annotations.NotNull()
        java.util.List<java.lang.String> openCountries, @org.jetbrains.annotations.NotNull()
        java.util.List<java.lang.String> emergencyCountries, @org.jetbrains.annotations.NotNull()
        java.util.List<app.callcheck.mobile.feature.callintercept.GlobalLaunchDashboard.ProblematicCountryInfo> problematicCountries, boolean hardRulesLocked, boolean slaLocked, boolean safeExpressionsLocked, long totalFeedbackEvents, int feedbackCountryCount) {
            super();
        }
        
        /**
         * 최종 출시 준비 상태
         */
        public final boolean getLaunchReady() {
            return false;
        }
        
        public final int getTotalRegisteredCountries() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Map<java.lang.String, java.lang.Integer> getTierBreakdown() {
            return null;
        }
        
        public final int getCompliancePassCount() {
            return 0;
        }
        
        public final int getComplianceFailCount() {
            return 0;
        }
        
        public final long getGlobalSlaDeadlineMs() {
            return 0L;
        }
        
        public final float getSlaComplianceRate() {
            return 0.0F;
        }
        
        public final float getOverallSearchFailureRate() {
            return 0.0F;
        }
        
        public final int getCircuitClosedCount() {
            return 0;
        }
        
        public final int getCircuitOpenCount() {
            return 0;
        }
        
        public final int getCircuitEmergencyCount() {
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
        public final java.util.List<app.callcheck.mobile.feature.callintercept.GlobalLaunchDashboard.ProblematicCountryInfo> getProblematicCountries() {
            return null;
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
        
        public final long getTotalFeedbackEvents() {
            return 0L;
        }
        
        public final int getFeedbackCountryCount() {
            return 0;
        }
        
        public final boolean component1() {
            return false;
        }
        
        public final int component10() {
            return 0;
        }
        
        public final int component11() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<java.lang.String> component12() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<java.lang.String> component13() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<app.callcheck.mobile.feature.callintercept.GlobalLaunchDashboard.ProblematicCountryInfo> component14() {
            return null;
        }
        
        public final boolean component15() {
            return false;
        }
        
        public final boolean component16() {
            return false;
        }
        
        public final boolean component17() {
            return false;
        }
        
        public final long component18() {
            return 0L;
        }
        
        public final int component19() {
            return 0;
        }
        
        public final int component2() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.Map<java.lang.String, java.lang.Integer> component3() {
            return null;
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
        
        public final float component7() {
            return 0.0F;
        }
        
        public final float component8() {
            return 0.0F;
        }
        
        public final int component9() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.GlobalLaunchDashboard.DashboardData copy(boolean launchReady, int totalRegisteredCountries, @org.jetbrains.annotations.NotNull()
        java.util.Map<java.lang.String, java.lang.Integer> tierBreakdown, int compliancePassCount, int complianceFailCount, long globalSlaDeadlineMs, float slaComplianceRate, float overallSearchFailureRate, int circuitClosedCount, int circuitOpenCount, int circuitEmergencyCount, @org.jetbrains.annotations.NotNull()
        java.util.List<java.lang.String> openCountries, @org.jetbrains.annotations.NotNull()
        java.util.List<java.lang.String> emergencyCountries, @org.jetbrains.annotations.NotNull()
        java.util.List<app.callcheck.mobile.feature.callintercept.GlobalLaunchDashboard.ProblematicCountryInfo> problematicCountries, boolean hardRulesLocked, boolean slaLocked, boolean safeExpressionsLocked, long totalFeedbackEvents, int feedbackCountryCount) {
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u000b\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B#\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00030\u0006\u00a2\u0006\u0002\u0010\u0007J\t\u0010\r\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u000e\u001a\u00020\u0003H\u00c6\u0003J\u000f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00030\u0006H\u00c6\u0003J-\u0010\u0010\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00030\u0006H\u00c6\u0001J\u0013\u0010\u0011\u001a\u00020\u00122\b\u0010\u0013\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0014\u001a\u00020\u0015H\u00d6\u0001J\t\u0010\u0016\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0017\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00030\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\t\u00a8\u0006\u0017"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/GlobalLaunchDashboard$ProblematicCountryInfo;", "", "countryCode", "", "tier", "issues", "", "(Ljava/lang/String;Ljava/lang/String;Ljava/util/List;)V", "getCountryCode", "()Ljava/lang/String;", "getIssues", "()Ljava/util/List;", "getTier", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "", "toString", "call-intercept_debug"})
    public static final class ProblematicCountryInfo {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String countryCode = null;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String tier = null;
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<java.lang.String> issues = null;
        
        public ProblematicCountryInfo(@org.jetbrains.annotations.NotNull()
        java.lang.String countryCode, @org.jetbrains.annotations.NotNull()
        java.lang.String tier, @org.jetbrains.annotations.NotNull()
        java.util.List<java.lang.String> issues) {
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
        public final java.util.List<java.lang.String> getIssues() {
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
        public final java.util.List<java.lang.String> component3() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.GlobalLaunchDashboard.ProblematicCountryInfo copy(@org.jetbrains.annotations.NotNull()
        java.lang.String countryCode, @org.jetbrains.annotations.NotNull()
        java.lang.String tier, @org.jetbrains.annotations.NotNull()
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