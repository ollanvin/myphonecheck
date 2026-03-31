package app.callcheck.mobile.feature.decisionengine;

/**
 * Maps normalized risk score (0.0–1.0) to RiskLevel enum.
 *
 * PRD risk levels: HIGH / MEDIUM / LOW / UNKNOWN
 * - HIGH: >= 0.6
 * - MEDIUM: >= 0.3
 * - LOW: >= 0.0 (with evidence)
 * - UNKNOWN: no evidence at all
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\u000b\n\u0000\u0018\u00002\u00020\u0001B\u0007\b\u0007\u00a2\u0006\u0002\u0010\u0002J\u0016\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b\u00a8\u0006\t"}, d2 = {"Lapp/callcheck/mobile/feature/decisionengine/RiskBadgeMapper;", "", "()V", "map", "Lapp/callcheck/mobile/core/model/RiskLevel;", "score", "", "hasEvidence", "", "decision-engine_debug"})
public final class RiskBadgeMapper {
    
    @javax.inject.Inject()
    public RiskBadgeMapper() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final app.callcheck.mobile.core.model.RiskLevel map(float score, boolean hasEvidence) {
        return null;
    }
}