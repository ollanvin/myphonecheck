package app.callcheck.mobile.feature.callintercept;

/**
 * 정책 자동 조정기.
 *
 * 빅테크 정석: Production Telemetry → Auto Policy Adjustment
 *
 * ProductionFeedbackCollector에서 수집된 데이터를 기반으로:
 *  1. 검색엔진 fallback 순서 자동 변경
 *  2. 키워드 가중치 자동 조정
 *  3. 위험 점수 자동 보정
 *  4. 타임아웃 정책 미세 조정
 *
 * 조정 원칙:
 * - 최소 샘플 수 확보 후 조정 (MIN_SAMPLES)
 * - 급격한 변경 방지 (최대 변경 폭 제한)
 * - 자비스 기준 위반 방지 (CN→Google 금지 등 하드 룰 보호)
 * - 모든 조정 이력 기록
 *
 * 100% 온디바이스. 서버 전송 없음.
 */
@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000b\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\b\n\u0002\b\u0007\b\u0007\u0018\u0000 $2\u00020\u0001:\u0004!\"#$B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0006\u0010\n\u001a\u00020\u000bJ\u001a\u0010\f\u001a\u0004\u0018\u00010\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011H\u0002J\u001a\u0010\u0012\u001a\u0004\u0018\u00010\u00132\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011H\u0002J\u001a\u0010\u0014\u001a\u0004\u0018\u00010\u00152\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011H\u0002J\u001a\u0010\u0016\u001a\u0004\u0018\u00010\u00172\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011H\u0002J\u0014\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\t0\u00192\u0006\u0010\u001a\u001a\u00020\u000bJ\u0018\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020\u001cH\u0002J\f\u0010 \u001a\b\u0012\u0004\u0012\u00020\t0\u0019R\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006%"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/AutoPolicyAdjuster;", "", "feedbackCollector", "Lapp/callcheck/mobile/feature/callintercept/ProductionFeedbackCollector;", "registry", "Lapp/callcheck/mobile/feature/callintercept/GlobalSearchProviderRegistry;", "(Lapp/callcheck/mobile/feature/callintercept/ProductionFeedbackCollector;Lapp/callcheck/mobile/feature/callintercept/GlobalSearchProviderRegistry;)V", "adjustmentHistory", "", "Lapp/callcheck/mobile/feature/callintercept/AutoPolicyAdjuster$AppliedAdjustment;", "analyzeAndSuggest", "Lapp/callcheck/mobile/feature/callintercept/AutoPolicyAdjuster$AdjustmentReport;", "analyzeEngineOrder", "Lapp/callcheck/mobile/feature/callintercept/AutoPolicyAdjuster$Adjustment$EngineOrderChange;", "stats", "Lapp/callcheck/mobile/feature/callintercept/ProductionFeedbackCollector$CountryStats;", "config", "Lapp/callcheck/mobile/core/model/CountrySearchConfig;", "analyzeKeywordWeights", "Lapp/callcheck/mobile/feature/callintercept/AutoPolicyAdjuster$Adjustment$KeywordWeightChange;", "analyzeRiskScore", "Lapp/callcheck/mobile/feature/callintercept/AutoPolicyAdjuster$Adjustment$RiskScoreCalibration;", "analyzeTimeout", "Lapp/callcheck/mobile/feature/callintercept/AutoPolicyAdjuster$Adjustment$TimeoutChange;", "applyAdjustments", "", "report", "calculateConfidence", "", "sampleCount", "", "problemRate", "getAdjustmentHistory", "Adjustment", "AdjustmentReport", "AppliedAdjustment", "Companion", "call-intercept_debug"})
public final class AutoPolicyAdjuster {
    @org.jetbrains.annotations.NotNull()
    private final app.callcheck.mobile.feature.callintercept.ProductionFeedbackCollector feedbackCollector = null;
    @org.jetbrains.annotations.NotNull()
    private final app.callcheck.mobile.feature.callintercept.GlobalSearchProviderRegistry registry = null;
    
    /**
     * 조정 이력
     */
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<app.callcheck.mobile.feature.callintercept.AutoPolicyAdjuster.AppliedAdjustment> adjustmentHistory = null;
    
    /**
     * 최소 샘플 수
     */
    private static final int MIN_SAMPLES = 50;
    
    /**
     * 이상적 샘플 수 (100% 신뢰도)
     */
    private static final float IDEAL_SAMPLE_SIZE = 500.0F;
    
    /**
     * 최소 적용 신뢰도
     */
    private static final float MIN_APPLY_CONFIDENCE = 0.6F;
    
    /**
     * 트리거 임계값
     */
    private static final float FALLBACK_RATE_TRIGGER = 0.3F;
    private static final float SEARCH_FAILURE_TRIGGER = 0.1F;
    private static final float DISAGREEMENT_TRIGGER = 0.15F;
    private static final float SLA_VIOLATION_TRIGGER = 0.05F;
    private static final float HIGH_BLOCK_RATE = 0.4F;
    private static final float LOW_BLOCK_RATE = 0.05F;
    
    /**
     * 가중치 조정 스텝
     */
    private static final float WEIGHT_ADJUSTMENT_STEP = 0.1F;
    private static final float CONFIDENCE_ADJUSTMENT_STEP = 0.1F;
    private static final float MAX_KEYWORD_WEIGHT = 0.3F;
    private static final float MIN_KEYWORD_WEIGHT = 0.05F;
    private static final long MIN_PRIMARY_TIMEOUT = 500L;
    
    /**
     * 엔진 교체 금지 국가 (자비스 하드 룰)
     */
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> LOCKED_ENGINE_COUNTRIES = null;
    @org.jetbrains.annotations.NotNull()
    public static final app.callcheck.mobile.feature.callintercept.AutoPolicyAdjuster.Companion Companion = null;
    
    @javax.inject.Inject()
    public AutoPolicyAdjuster(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.callintercept.ProductionFeedbackCollector feedbackCollector, @org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.callintercept.GlobalSearchProviderRegistry registry) {
        super();
    }
    
    /**
     * 전체 국가 분석 후 조정 권고 생성.
     *
     * @return AdjustmentReport — 적용 여부는 호출자가 결정
     */
    @org.jetbrains.annotations.NotNull()
    public final app.callcheck.mobile.feature.callintercept.AutoPolicyAdjuster.AdjustmentReport analyzeAndSuggest() {
        return null;
    }
    
    /**
     * 엔진 순서 분석.
     * fallback률이 높으면 1순위/2순위 교체 권고.
     */
    private final app.callcheck.mobile.feature.callintercept.AutoPolicyAdjuster.Adjustment.EngineOrderChange analyzeEngineOrder(app.callcheck.mobile.feature.callintercept.ProductionFeedbackCollector.CountryStats stats, app.callcheck.mobile.core.model.CountrySearchConfig config) {
        return null;
    }
    
    /**
     * 키워드 가중치 분석.
     * 사용자 불일치율이 높으면 가중치 조정 권고.
     */
    private final app.callcheck.mobile.feature.callintercept.AutoPolicyAdjuster.Adjustment.KeywordWeightChange analyzeKeywordWeights(app.callcheck.mobile.feature.callintercept.ProductionFeedbackCollector.CountryStats stats, app.callcheck.mobile.core.model.CountrySearchConfig config) {
        return null;
    }
    
    /**
     * 타임아웃 분석.
     * SLA 위반률이 높으면 1순위 타임아웃 단축 권고.
     */
    private final app.callcheck.mobile.feature.callintercept.AutoPolicyAdjuster.Adjustment.TimeoutChange analyzeTimeout(app.callcheck.mobile.feature.callintercept.ProductionFeedbackCollector.CountryStats stats, app.callcheck.mobile.core.model.CountrySearchConfig config) {
        return null;
    }
    
    /**
     * 위험 점수 보정.
     * 차단 비율이 비정상적이면 baseConfidence 조정.
     */
    private final app.callcheck.mobile.feature.callintercept.AutoPolicyAdjuster.Adjustment.RiskScoreCalibration analyzeRiskScore(app.callcheck.mobile.feature.callintercept.ProductionFeedbackCollector.CountryStats stats, app.callcheck.mobile.core.model.CountrySearchConfig config) {
        return null;
    }
    
    /**
     * 조정 적용 시뮬레이션.
     *
     * 실제 레지스트리 변경은 하지 않음 (불변 레지스트리 원칙).
     * 대신 오버라이드 맵을 반환하여 런타임에서 활용.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<app.callcheck.mobile.feature.callintercept.AutoPolicyAdjuster.AppliedAdjustment> applyAdjustments(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.callintercept.AutoPolicyAdjuster.AdjustmentReport report) {
        return null;
    }
    
    /**
     * 조정 이력 조회
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<app.callcheck.mobile.feature.callintercept.AutoPolicyAdjuster.AppliedAdjustment> getAdjustmentHistory() {
        return null;
    }
    
    /**
     * 신뢰도 계산: 샘플 수 + 문제 비율 기반
     */
    private final float calculateConfidence(int sampleCount, float problemRate) {
        return 0.0F;
    }
    
    /**
     * 단일 조정 항목
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b6\u0018\u00002\u00020\u0001:\u0004\r\u000e\u000f\u0010B\u0007\b\u0004\u00a2\u0006\u0002\u0010\u0002R\u0012\u0010\u0003\u001a\u00020\u0004X\u00a6\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0005\u0010\u0006R\u0012\u0010\u0007\u001a\u00020\bX\u00a6\u0004\u00a2\u0006\u0006\u001a\u0004\b\t\u0010\nR\u0012\u0010\u000b\u001a\u00020\bX\u00a6\u0004\u00a2\u0006\u0006\u001a\u0004\b\f\u0010\n\u0082\u0001\u0004\u0011\u0012\u0013\u0014\u00a8\u0006\u0015"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/AutoPolicyAdjuster$Adjustment;", "", "()V", "confidence", "", "getConfidence", "()F", "countryCode", "", "getCountryCode", "()Ljava/lang/String;", "reason", "getReason", "EngineOrderChange", "KeywordWeightChange", "RiskScoreCalibration", "TimeoutChange", "Lapp/callcheck/mobile/feature/callintercept/AutoPolicyAdjuster$Adjustment$EngineOrderChange;", "Lapp/callcheck/mobile/feature/callintercept/AutoPolicyAdjuster$Adjustment$KeywordWeightChange;", "Lapp/callcheck/mobile/feature/callintercept/AutoPolicyAdjuster$Adjustment$RiskScoreCalibration;", "Lapp/callcheck/mobile/feature/callintercept/AutoPolicyAdjuster$Adjustment$TimeoutChange;", "call-intercept_debug"})
    public static abstract class Adjustment {
        
        private Adjustment() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public abstract java.lang.String getCountryCode();
        
        @org.jetbrains.annotations.NotNull()
        public abstract java.lang.String getReason();
        
        public abstract float getConfidence();
        
        /**
         * 검색엔진 순서 변경 권고
         */
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0007\n\u0002\b\u0014\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B=\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\u0005\u0012\u0006\u0010\b\u001a\u00020\u0005\u0012\u0006\u0010\t\u001a\u00020\u0003\u0012\u0006\u0010\n\u001a\u00020\u000b\u00a2\u0006\u0002\u0010\fJ\t\u0010\u0017\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0019\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u001b\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u001c\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001d\u001a\u00020\u000bH\u00c6\u0003JO\u0010\u001e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\u00052\b\b\u0002\u0010\b\u001a\u00020\u00052\b\b\u0002\u0010\t\u001a\u00020\u00032\b\b\u0002\u0010\n\u001a\u00020\u000bH\u00c6\u0001J\u0013\u0010\u001f\u001a\u00020 2\b\u0010!\u001a\u0004\u0018\u00010\"H\u00d6\u0003J\t\u0010#\u001a\u00020$H\u00d6\u0001J\t\u0010%\u001a\u00020\u0003H\u00d6\u0001R\u0014\u0010\n\u001a\u00020\u000bX\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0014\u0010\u0002\u001a\u00020\u0003X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u0007\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0012R\u0014\u0010\t\u001a\u00020\u0003X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0010R\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0012R\u0011\u0010\b\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0012\u00a8\u0006&"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/AutoPolicyAdjuster$Adjustment$EngineOrderChange;", "Lapp/callcheck/mobile/feature/callintercept/AutoPolicyAdjuster$Adjustment;", "countryCode", "", "currentPrimary", "Lapp/callcheck/mobile/core/model/SearchEngine;", "suggestedPrimary", "currentSecondary", "suggestedSecondary", "reason", "confidence", "", "(Ljava/lang/String;Lapp/callcheck/mobile/core/model/SearchEngine;Lapp/callcheck/mobile/core/model/SearchEngine;Lapp/callcheck/mobile/core/model/SearchEngine;Lapp/callcheck/mobile/core/model/SearchEngine;Ljava/lang/String;F)V", "getConfidence", "()F", "getCountryCode", "()Ljava/lang/String;", "getCurrentPrimary", "()Lapp/callcheck/mobile/core/model/SearchEngine;", "getCurrentSecondary", "getReason", "getSuggestedPrimary", "getSuggestedSecondary", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "copy", "equals", "", "other", "", "hashCode", "", "toString", "call-intercept_debug"})
        public static final class EngineOrderChange extends app.callcheck.mobile.feature.callintercept.AutoPolicyAdjuster.Adjustment {
            @org.jetbrains.annotations.NotNull()
            private final java.lang.String countryCode = null;
            @org.jetbrains.annotations.NotNull()
            private final app.callcheck.mobile.core.model.SearchEngine currentPrimary = null;
            @org.jetbrains.annotations.NotNull()
            private final app.callcheck.mobile.core.model.SearchEngine suggestedPrimary = null;
            @org.jetbrains.annotations.NotNull()
            private final app.callcheck.mobile.core.model.SearchEngine currentSecondary = null;
            @org.jetbrains.annotations.NotNull()
            private final app.callcheck.mobile.core.model.SearchEngine suggestedSecondary = null;
            @org.jetbrains.annotations.NotNull()
            private final java.lang.String reason = null;
            private final float confidence = 0.0F;
            
            public EngineOrderChange(@org.jetbrains.annotations.NotNull()
            java.lang.String countryCode, @org.jetbrains.annotations.NotNull()
            app.callcheck.mobile.core.model.SearchEngine currentPrimary, @org.jetbrains.annotations.NotNull()
            app.callcheck.mobile.core.model.SearchEngine suggestedPrimary, @org.jetbrains.annotations.NotNull()
            app.callcheck.mobile.core.model.SearchEngine currentSecondary, @org.jetbrains.annotations.NotNull()
            app.callcheck.mobile.core.model.SearchEngine suggestedSecondary, @org.jetbrains.annotations.NotNull()
            java.lang.String reason, float confidence) {
            }
            
            @java.lang.Override()
            @org.jetbrains.annotations.NotNull()
            public java.lang.String getCountryCode() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final app.callcheck.mobile.core.model.SearchEngine getCurrentPrimary() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final app.callcheck.mobile.core.model.SearchEngine getSuggestedPrimary() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final app.callcheck.mobile.core.model.SearchEngine getCurrentSecondary() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final app.callcheck.mobile.core.model.SearchEngine getSuggestedSecondary() {
                return null;
            }
            
            @java.lang.Override()
            @org.jetbrains.annotations.NotNull()
            public java.lang.String getReason() {
                return null;
            }
            
            @java.lang.Override()
            public float getConfidence() {
                return 0.0F;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final java.lang.String component1() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final app.callcheck.mobile.core.model.SearchEngine component2() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final app.callcheck.mobile.core.model.SearchEngine component3() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final app.callcheck.mobile.core.model.SearchEngine component4() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final app.callcheck.mobile.core.model.SearchEngine component5() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final java.lang.String component6() {
                return null;
            }
            
            public final float component7() {
                return 0.0F;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final app.callcheck.mobile.feature.callintercept.AutoPolicyAdjuster.Adjustment.EngineOrderChange copy(@org.jetbrains.annotations.NotNull()
            java.lang.String countryCode, @org.jetbrains.annotations.NotNull()
            app.callcheck.mobile.core.model.SearchEngine currentPrimary, @org.jetbrains.annotations.NotNull()
            app.callcheck.mobile.core.model.SearchEngine suggestedPrimary, @org.jetbrains.annotations.NotNull()
            app.callcheck.mobile.core.model.SearchEngine currentSecondary, @org.jetbrains.annotations.NotNull()
            app.callcheck.mobile.core.model.SearchEngine suggestedSecondary, @org.jetbrains.annotations.NotNull()
            java.lang.String reason, float confidence) {
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
         * 키워드 가중치 조정 권고
         */
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0018\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B=\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\u0005\u0012\u0006\u0010\b\u001a\u00020\u0005\u0012\u0006\u0010\t\u001a\u00020\u0003\u0012\u0006\u0010\n\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u000bJ\t\u0010\u0015\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0016\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0017\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0019\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001b\u001a\u00020\u0005H\u00c6\u0003JO\u0010\u001c\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\u00052\b\b\u0002\u0010\b\u001a\u00020\u00052\b\b\u0002\u0010\t\u001a\u00020\u00032\b\b\u0002\u0010\n\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010\u001d\u001a\u00020\u001e2\b\u0010\u001f\u001a\u0004\u0018\u00010 H\u00d6\u0003J\t\u0010!\u001a\u00020\"H\u00d6\u0001J\t\u0010#\u001a\u00020\u0003H\u00d6\u0001R\u0014\u0010\n\u001a\u00020\u0005X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0014\u0010\u0002\u001a\u00020\u0003X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\rR\u0011\u0010\u0007\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\rR\u0014\u0010\t\u001a\u00020\u0003X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u000fR\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\rR\u0011\u0010\b\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\r\u00a8\u0006$"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/AutoPolicyAdjuster$Adjustment$KeywordWeightChange;", "Lapp/callcheck/mobile/feature/callintercept/AutoPolicyAdjuster$Adjustment;", "countryCode", "", "currentRiskWeight", "", "suggestedRiskWeight", "currentSafeWeight", "suggestedSafeWeight", "reason", "confidence", "(Ljava/lang/String;FFFFLjava/lang/String;F)V", "getConfidence", "()F", "getCountryCode", "()Ljava/lang/String;", "getCurrentRiskWeight", "getCurrentSafeWeight", "getReason", "getSuggestedRiskWeight", "getSuggestedSafeWeight", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "copy", "equals", "", "other", "", "hashCode", "", "toString", "call-intercept_debug"})
        public static final class KeywordWeightChange extends app.callcheck.mobile.feature.callintercept.AutoPolicyAdjuster.Adjustment {
            @org.jetbrains.annotations.NotNull()
            private final java.lang.String countryCode = null;
            private final float currentRiskWeight = 0.0F;
            private final float suggestedRiskWeight = 0.0F;
            private final float currentSafeWeight = 0.0F;
            private final float suggestedSafeWeight = 0.0F;
            @org.jetbrains.annotations.NotNull()
            private final java.lang.String reason = null;
            private final float confidence = 0.0F;
            
            public KeywordWeightChange(@org.jetbrains.annotations.NotNull()
            java.lang.String countryCode, float currentRiskWeight, float suggestedRiskWeight, float currentSafeWeight, float suggestedSafeWeight, @org.jetbrains.annotations.NotNull()
            java.lang.String reason, float confidence) {
            }
            
            @java.lang.Override()
            @org.jetbrains.annotations.NotNull()
            public java.lang.String getCountryCode() {
                return null;
            }
            
            public final float getCurrentRiskWeight() {
                return 0.0F;
            }
            
            public final float getSuggestedRiskWeight() {
                return 0.0F;
            }
            
            public final float getCurrentSafeWeight() {
                return 0.0F;
            }
            
            public final float getSuggestedSafeWeight() {
                return 0.0F;
            }
            
            @java.lang.Override()
            @org.jetbrains.annotations.NotNull()
            public java.lang.String getReason() {
                return null;
            }
            
            @java.lang.Override()
            public float getConfidence() {
                return 0.0F;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final java.lang.String component1() {
                return null;
            }
            
            public final float component2() {
                return 0.0F;
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
            
            @org.jetbrains.annotations.NotNull()
            public final java.lang.String component6() {
                return null;
            }
            
            public final float component7() {
                return 0.0F;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final app.callcheck.mobile.feature.callintercept.AutoPolicyAdjuster.Adjustment.KeywordWeightChange copy(@org.jetbrains.annotations.NotNull()
            java.lang.String countryCode, float currentRiskWeight, float suggestedRiskWeight, float currentSafeWeight, float suggestedSafeWeight, @org.jetbrains.annotations.NotNull()
            java.lang.String reason, float confidence) {
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
         * 위험 점수 보정 권고
         */
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0012\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B-\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\u0003\u0012\u0006\u0010\b\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\tJ\t\u0010\u0011\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0012\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0013\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0014\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0015\u001a\u00020\u0005H\u00c6\u0003J;\u0010\u0016\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010\u0017\u001a\u00020\u00182\b\u0010\u0019\u001a\u0004\u0018\u00010\u001aH\u00d6\u0003J\t\u0010\u001b\u001a\u00020\u001cH\u00d6\u0001J\t\u0010\u001d\u001a\u00020\u0003H\u00d6\u0001R\u0014\u0010\b\u001a\u00020\u0005X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0014\u0010\u0002\u001a\u00020\u0003X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000bR\u0014\u0010\u0007\u001a\u00020\u0003X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\rR\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000b\u00a8\u0006\u001e"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/AutoPolicyAdjuster$Adjustment$RiskScoreCalibration;", "Lapp/callcheck/mobile/feature/callintercept/AutoPolicyAdjuster$Adjustment;", "countryCode", "", "currentBaseConfidence", "", "suggestedBaseConfidence", "reason", "confidence", "(Ljava/lang/String;FFLjava/lang/String;F)V", "getConfidence", "()F", "getCountryCode", "()Ljava/lang/String;", "getCurrentBaseConfidence", "getReason", "getSuggestedBaseConfidence", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "", "other", "", "hashCode", "", "toString", "call-intercept_debug"})
        public static final class RiskScoreCalibration extends app.callcheck.mobile.feature.callintercept.AutoPolicyAdjuster.Adjustment {
            @org.jetbrains.annotations.NotNull()
            private final java.lang.String countryCode = null;
            private final float currentBaseConfidence = 0.0F;
            private final float suggestedBaseConfidence = 0.0F;
            @org.jetbrains.annotations.NotNull()
            private final java.lang.String reason = null;
            private final float confidence = 0.0F;
            
            public RiskScoreCalibration(@org.jetbrains.annotations.NotNull()
            java.lang.String countryCode, float currentBaseConfidence, float suggestedBaseConfidence, @org.jetbrains.annotations.NotNull()
            java.lang.String reason, float confidence) {
            }
            
            @java.lang.Override()
            @org.jetbrains.annotations.NotNull()
            public java.lang.String getCountryCode() {
                return null;
            }
            
            public final float getCurrentBaseConfidence() {
                return 0.0F;
            }
            
            public final float getSuggestedBaseConfidence() {
                return 0.0F;
            }
            
            @java.lang.Override()
            @org.jetbrains.annotations.NotNull()
            public java.lang.String getReason() {
                return null;
            }
            
            @java.lang.Override()
            public float getConfidence() {
                return 0.0F;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final java.lang.String component1() {
                return null;
            }
            
            public final float component2() {
                return 0.0F;
            }
            
            public final float component3() {
                return 0.0F;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final java.lang.String component4() {
                return null;
            }
            
            public final float component5() {
                return 0.0F;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final app.callcheck.mobile.feature.callintercept.AutoPolicyAdjuster.Adjustment.RiskScoreCalibration copy(@org.jetbrains.annotations.NotNull()
            java.lang.String countryCode, float currentBaseConfidence, float suggestedBaseConfidence, @org.jetbrains.annotations.NotNull()
            java.lang.String reason, float confidence) {
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
         * 타임아웃 조정 권고
         */
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0002\b\u0010\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B-\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\u0003\u0012\u0006\u0010\b\u001a\u00020\t\u00a2\u0006\u0002\u0010\nJ\t\u0010\u0013\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0014\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0015\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0016\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0017\u001a\u00020\tH\u00c6\u0003J;\u0010\u0018\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\tH\u00c6\u0001J\u0013\u0010\u0019\u001a\u00020\u001a2\b\u0010\u001b\u001a\u0004\u0018\u00010\u001cH\u00d6\u0003J\t\u0010\u001d\u001a\u00020\u001eH\u00d6\u0001J\t\u0010\u001f\u001a\u00020\u0003H\u00d6\u0001R\u0014\u0010\b\u001a\u00020\tX\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0014\u0010\u0002\u001a\u00020\u0003X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0014\u0010\u0007\u001a\u00020\u0003X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u000eR\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0010\u00a8\u0006 "}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/AutoPolicyAdjuster$Adjustment$TimeoutChange;", "Lapp/callcheck/mobile/feature/callintercept/AutoPolicyAdjuster$Adjustment;", "countryCode", "", "currentPrimaryTimeout", "", "suggestedPrimaryTimeout", "reason", "confidence", "", "(Ljava/lang/String;JJLjava/lang/String;F)V", "getConfidence", "()F", "getCountryCode", "()Ljava/lang/String;", "getCurrentPrimaryTimeout", "()J", "getReason", "getSuggestedPrimaryTimeout", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "", "other", "", "hashCode", "", "toString", "call-intercept_debug"})
        public static final class TimeoutChange extends app.callcheck.mobile.feature.callintercept.AutoPolicyAdjuster.Adjustment {
            @org.jetbrains.annotations.NotNull()
            private final java.lang.String countryCode = null;
            private final long currentPrimaryTimeout = 0L;
            private final long suggestedPrimaryTimeout = 0L;
            @org.jetbrains.annotations.NotNull()
            private final java.lang.String reason = null;
            private final float confidence = 0.0F;
            
            public TimeoutChange(@org.jetbrains.annotations.NotNull()
            java.lang.String countryCode, long currentPrimaryTimeout, long suggestedPrimaryTimeout, @org.jetbrains.annotations.NotNull()
            java.lang.String reason, float confidence) {
            }
            
            @java.lang.Override()
            @org.jetbrains.annotations.NotNull()
            public java.lang.String getCountryCode() {
                return null;
            }
            
            public final long getCurrentPrimaryTimeout() {
                return 0L;
            }
            
            public final long getSuggestedPrimaryTimeout() {
                return 0L;
            }
            
            @java.lang.Override()
            @org.jetbrains.annotations.NotNull()
            public java.lang.String getReason() {
                return null;
            }
            
            @java.lang.Override()
            public float getConfidence() {
                return 0.0F;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final java.lang.String component1() {
                return null;
            }
            
            public final long component2() {
                return 0L;
            }
            
            public final long component3() {
                return 0L;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final java.lang.String component4() {
                return null;
            }
            
            public final float component5() {
                return 0.0F;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final app.callcheck.mobile.feature.callintercept.AutoPolicyAdjuster.Adjustment.TimeoutChange copy(@org.jetbrains.annotations.NotNull()
            java.lang.String countryCode, long currentPrimaryTimeout, long suggestedPrimaryTimeout, @org.jetbrains.annotations.NotNull()
            java.lang.String reason, float confidence) {
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
    
    /**
     * 조정 보고서
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B#\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\u0002\u0010\bJ\u000f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J\t\u0010\u000f\u001a\u00020\u0006H\u00c6\u0003J\t\u0010\u0010\u001a\u00020\u0006H\u00c6\u0003J-\u0010\u0011\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\u0006H\u00c6\u0001J\u0013\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0015\u001a\u00020\u0006H\u00d6\u0001J\u0006\u0010\u0016\u001a\u00020\u0017J\t\u0010\u0018\u001a\u00020\u0017H\u00d6\u0001R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\f\u00a8\u0006\u0019"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/AutoPolicyAdjuster$AdjustmentReport;", "", "adjustments", "", "Lapp/callcheck/mobile/feature/callintercept/AutoPolicyAdjuster$Adjustment;", "countriesAnalyzed", "", "adjustmentsGenerated", "(Ljava/util/List;II)V", "getAdjustments", "()Ljava/util/List;", "getAdjustmentsGenerated", "()I", "getCountriesAnalyzed", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "toJarvisFormat", "", "toString", "call-intercept_debug"})
    public static final class AdjustmentReport {
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<app.callcheck.mobile.feature.callintercept.AutoPolicyAdjuster.Adjustment> adjustments = null;
        private final int countriesAnalyzed = 0;
        private final int adjustmentsGenerated = 0;
        
        public AdjustmentReport(@org.jetbrains.annotations.NotNull()
        java.util.List<? extends app.callcheck.mobile.feature.callintercept.AutoPolicyAdjuster.Adjustment> adjustments, int countriesAnalyzed, int adjustmentsGenerated) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<app.callcheck.mobile.feature.callintercept.AutoPolicyAdjuster.Adjustment> getAdjustments() {
            return null;
        }
        
        public final int getCountriesAnalyzed() {
            return 0;
        }
        
        public final int getAdjustmentsGenerated() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String toJarvisFormat() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<app.callcheck.mobile.feature.callintercept.AutoPolicyAdjuster.Adjustment> component1() {
            return null;
        }
        
        public final int component2() {
            return 0;
        }
        
        public final int component3() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.AutoPolicyAdjuster.AdjustmentReport copy(@org.jetbrains.annotations.NotNull()
        java.util.List<? extends app.callcheck.mobile.feature.callintercept.AutoPolicyAdjuster.Adjustment> adjustments, int countriesAnalyzed, int adjustmentsGenerated) {
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0011\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u00a2\u0006\u0002\u0010\nJ\t\u0010\u0013\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0014\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0015\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u0016\u001a\u00020\tH\u00c6\u0003J1\u0010\u0017\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\tH\u00c6\u0001J\u0013\u0010\u0018\u001a\u00020\u00072\b\u0010\u0019\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001a\u001a\u00020\u001bH\u00d6\u0001J\t\u0010\u001c\u001a\u00020\tH\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012\u00a8\u0006\u001d"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/AutoPolicyAdjuster$AppliedAdjustment;", "", "timestampMs", "", "adjustment", "Lapp/callcheck/mobile/feature/callintercept/AutoPolicyAdjuster$Adjustment;", "applied", "", "reason", "", "(JLapp/callcheck/mobile/feature/callintercept/AutoPolicyAdjuster$Adjustment;ZLjava/lang/String;)V", "getAdjustment", "()Lapp/callcheck/mobile/feature/callintercept/AutoPolicyAdjuster$Adjustment;", "getApplied", "()Z", "getReason", "()Ljava/lang/String;", "getTimestampMs", "()J", "component1", "component2", "component3", "component4", "copy", "equals", "other", "hashCode", "", "toString", "call-intercept_debug"})
    public static final class AppliedAdjustment {
        private final long timestampMs = 0L;
        @org.jetbrains.annotations.NotNull()
        private final app.callcheck.mobile.feature.callintercept.AutoPolicyAdjuster.Adjustment adjustment = null;
        private final boolean applied = false;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String reason = null;
        
        public AppliedAdjustment(long timestampMs, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.AutoPolicyAdjuster.Adjustment adjustment, boolean applied, @org.jetbrains.annotations.NotNull()
        java.lang.String reason) {
            super();
        }
        
        public final long getTimestampMs() {
            return 0L;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.AutoPolicyAdjuster.Adjustment getAdjustment() {
            return null;
        }
        
        public final boolean getApplied() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getReason() {
            return null;
        }
        
        public final long component1() {
            return 0L;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.AutoPolicyAdjuster.Adjustment component2() {
            return null;
        }
        
        public final boolean component3() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component4() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.AutoPolicyAdjuster.AppliedAdjustment copy(long timestampMs, @org.jetbrains.annotations.NotNull()
        app.callcheck.mobile.feature.callintercept.AutoPolicyAdjuster.Adjustment adjustment, boolean applied, @org.jetbrains.annotations.NotNull()
        java.lang.String reason) {
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0005\n\u0002\u0010\"\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\t\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0017"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/AutoPolicyAdjuster$Companion;", "", "()V", "CONFIDENCE_ADJUSTMENT_STEP", "", "DISAGREEMENT_TRIGGER", "FALLBACK_RATE_TRIGGER", "HIGH_BLOCK_RATE", "IDEAL_SAMPLE_SIZE", "LOCKED_ENGINE_COUNTRIES", "", "", "LOW_BLOCK_RATE", "MAX_KEYWORD_WEIGHT", "MIN_APPLY_CONFIDENCE", "MIN_KEYWORD_WEIGHT", "MIN_PRIMARY_TIMEOUT", "", "MIN_SAMPLES", "", "SEARCH_FAILURE_TRIGGER", "SLA_VIOLATION_TRIGGER", "WEIGHT_ADJUSTMENT_STEP", "call-intercept_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}