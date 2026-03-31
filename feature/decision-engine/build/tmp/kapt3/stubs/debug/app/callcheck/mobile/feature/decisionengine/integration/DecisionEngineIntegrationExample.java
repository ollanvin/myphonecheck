package app.callcheck.mobile.feature.decisionengine.integration;

/**
 * Example integration showing how to use DecisionEngine in feature components.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u001a\u0010\u0005\u001a\u00020\u00062\b\u0010\u0007\u001a\u0004\u0018\u00010\b2\b\u0010\t\u001a\u0004\u0018\u00010\nJ\u000e\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u0006J\u000e\u0010\u000e\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bJ\u0016\u0010\u000f\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0010"}, d2 = {"Lapp/callcheck/mobile/feature/decisionengine/integration/DecisionEngineIntegrationExample;", "", "decisionEngine", "Lapp/callcheck/mobile/feature/decisionengine/DecisionEngine;", "(Lapp/callcheck/mobile/feature/decisionengine/DecisionEngine;)V", "evaluateIncomingCall", "Lapp/callcheck/mobile/core/model/DecisionResult;", "deviceEvidence", "Lapp/callcheck/mobile/core/model/DeviceEvidence;", "searchEvidence", "Lapp/callcheck/mobile/core/model/SearchEvidence;", "getCallHandlingStrategy", "Lapp/callcheck/mobile/feature/decisionengine/integration/CallHandlingStrategy;", "result", "quickEvaluateDeviceOnly", "updateWithSearchResults", "decision-engine_debug"})
public final class DecisionEngineIntegrationExample {
    @org.jetbrains.annotations.NotNull()
    private final app.callcheck.mobile.feature.decisionengine.DecisionEngine decisionEngine = null;
    
    @javax.inject.Inject()
    public DecisionEngineIntegrationExample(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.decisionengine.DecisionEngine decisionEngine) {
        super();
    }
    
    /**
     * Full evaluation with both evidence sources.
     */
    @org.jetbrains.annotations.NotNull()
    public final app.callcheck.mobile.core.model.DecisionResult evaluateIncomingCall(@org.jetbrains.annotations.Nullable()
    app.callcheck.mobile.core.model.DeviceEvidence deviceEvidence, @org.jetbrains.annotations.Nullable()
    app.callcheck.mobile.core.model.SearchEvidence searchEvidence) {
        return null;
    }
    
    /**
     * Quick evaluation with device evidence only (fast path, ~0.8s).
     * Search evidence not yet available.
     */
    @org.jetbrains.annotations.NotNull()
    public final app.callcheck.mobile.core.model.DecisionResult quickEvaluateDeviceOnly(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.core.model.DeviceEvidence deviceEvidence) {
        return null;
    }
    
    /**
     * Re-evaluate with search results after initial device-only assessment.
     */
    @org.jetbrains.annotations.NotNull()
    public final app.callcheck.mobile.core.model.DecisionResult updateWithSearchResults(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.core.model.DeviceEvidence deviceEvidence, @org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.core.model.SearchEvidence searchEvidence) {
        return null;
    }
    
    /**
     * Determine call handling strategy from decision result.
     */
    @org.jetbrains.annotations.NotNull()
    public final app.callcheck.mobile.feature.decisionengine.integration.CallHandlingStrategy getCallHandlingStrategy(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.core.model.DecisionResult result) {
        return null;
    }
}