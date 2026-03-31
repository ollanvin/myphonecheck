package app.callcheck.mobile.feature.decisionengine;

/**
 * Core decision engine interface.
 *
 * 4축 판단 입력:
 * 1. DeviceEvidence — 기기 내 통화 이력, 연락처 매칭
 * 2. SearchEvidence — 웹 검색 기반 평판
 * 3. LocalLearningSignal — 사용자 과거 행동 가중치
 * 4. BehaviorPatternSignal — 시간대/반복/VoIP 행동 패턴
 *
 * Contract:
 * - Must be synchronous (no I/O inside)
 * - Must complete in < 50ms
 * - Must always return a valid DecisionResult (never throw)
 *
 * 100% 온디바이스, 서버 전송 없음.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0000\bf\u0018\u00002\u00020\u0001J4\u0010\u0002\u001a\u00020\u00032\b\u0010\u0004\u001a\u0004\u0018\u00010\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\t2\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u000bH&J\u0010\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fH&\u00a8\u0006\u0010"}, d2 = {"Lapp/callcheck/mobile/feature/decisionengine/DecisionEngine;", "", "evaluate", "Lapp/callcheck/mobile/core/model/DecisionResult;", "deviceEvidence", "Lapp/callcheck/mobile/core/model/DeviceEvidence;", "searchEvidence", "Lapp/callcheck/mobile/core/model/SearchEvidence;", "localLearning", "Lapp/callcheck/mobile/core/model/LocalLearningSignal;", "behaviorPattern", "Lapp/callcheck/mobile/core/model/BehaviorPatternSignal;", "riskLevelFromScore", "Lapp/callcheck/mobile/core/model/RiskLevel;", "score", "", "decision-engine_debug"})
public abstract interface DecisionEngine {
    
    @org.jetbrains.annotations.NotNull()
    public abstract app.callcheck.mobile.core.model.DecisionResult evaluate(@org.jetbrains.annotations.Nullable()
    app.callcheck.mobile.core.model.DeviceEvidence deviceEvidence, @org.jetbrains.annotations.Nullable()
    app.callcheck.mobile.core.model.SearchEvidence searchEvidence, @org.jetbrains.annotations.Nullable()
    app.callcheck.mobile.core.model.LocalLearningSignal localLearning, @org.jetbrains.annotations.Nullable()
    app.callcheck.mobile.core.model.BehaviorPatternSignal behaviorPattern);
    
    /**
     * riskScore → RiskLevel 변환.
     * Tier 0 PreJudge 캐시 반환 시 사용.
     */
    @org.jetbrains.annotations.NotNull()
    public abstract app.callcheck.mobile.core.model.RiskLevel riskLevelFromScore(float score);
    
    /**
     * Core decision engine interface.
     *
     * 4축 판단 입력:
     * 1. DeviceEvidence — 기기 내 통화 이력, 연락처 매칭
     * 2. SearchEvidence — 웹 검색 기반 평판
     * 3. LocalLearningSignal — 사용자 과거 행동 가중치
     * 4. BehaviorPatternSignal — 시간대/반복/VoIP 행동 패턴
     *
     * Contract:
     * - Must be synchronous (no I/O inside)
     * - Must complete in < 50ms
     * - Must always return a valid DecisionResult (never throw)
     *
     * 100% 온디바이스, 서버 전송 없음.
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 3, xi = 48)
    public static final class DefaultImpls {
    }
}