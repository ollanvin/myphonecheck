package app.callcheck.mobile.feature.decisionengine;

/**
 * Generates human-readable summary and up to 3 supporting reasons.
 *
 * Summary: uses ConclusionCategory's built-in summaryKo/summaryEn.
 * Reasons: built from actual evidence fields — device history, search signals, recency.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u00002\u00020\u0001B\u0007\b\u0007\u00a2\u0006\u0002\u0010\u0002J(\u0010\u0003\u001a\u00020\u00042\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\b\u0010\b\u001a\u0004\u0018\u00010\t2\u0006\u0010\n\u001a\u00020\u0007H\u0002J(\u0010\u000b\u001a\u00020\u00042\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\b\u0010\b\u001a\u0004\u0018\u00010\t2\u0006\u0010\n\u001a\u00020\u0007H\u0002J(\u0010\f\u001a\u00020\u00042\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\b\u0010\r\u001a\u0004\u0018\u00010\u000e2\u0006\u0010\n\u001a\u00020\u0007H\u0002J2\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00070\u00102\u0006\u0010\u0011\u001a\u00020\u00122\b\u0010\u0013\u001a\u0004\u0018\u00010\t2\b\u0010\u0014\u001a\u0004\u0018\u00010\u000e2\b\b\u0002\u0010\u0015\u001a\u00020\u0007J\u0018\u0010\u0016\u001a\u00020\u00072\u0006\u0010\u0011\u001a\u00020\u00122\b\b\u0002\u0010\u0015\u001a\u00020\u0007\u00a8\u0006\u0017"}, d2 = {"Lapp/callcheck/mobile/feature/decisionengine/SummaryGenerator;", "", "()V", "addDeviceReason", "", "reasons", "", "", "device", "Lapp/callcheck/mobile/core/model/DeviceEvidence;", "lang", "addPatternReason", "addSearchReason", "search", "Lapp/callcheck/mobile/core/model/SearchEvidence;", "generateReasons", "", "category", "Lapp/callcheck/mobile/core/model/ConclusionCategory;", "deviceEvidence", "searchEvidence", "language", "generateSummary", "decision-engine_debug"})
public final class SummaryGenerator {
    
    @javax.inject.Inject()
    public SummaryGenerator() {
        super();
    }
    
    /**
     * One-line summary from the category's built-in display string.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String generateSummary(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.core.model.ConclusionCategory category, @org.jetbrains.annotations.NotNull()
    java.lang.String language) {
        return null;
    }
    
    /**
     * Up to 3 reasons explaining the decision, sourced from actual evidence.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> generateReasons(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.core.model.ConclusionCategory category, @org.jetbrains.annotations.Nullable()
    app.callcheck.mobile.core.model.DeviceEvidence deviceEvidence, @org.jetbrains.annotations.Nullable()
    app.callcheck.mobile.core.model.SearchEvidence searchEvidence, @org.jetbrains.annotations.NotNull()
    java.lang.String language) {
        return null;
    }
    
    private final void addDeviceReason(java.util.List<java.lang.String> reasons, app.callcheck.mobile.core.model.DeviceEvidence device, java.lang.String lang) {
    }
    
    private final void addSearchReason(java.util.List<java.lang.String> reasons, app.callcheck.mobile.core.model.SearchEvidence search, java.lang.String lang) {
    }
    
    private final void addPatternReason(java.util.List<java.lang.String> reasons, app.callcheck.mobile.core.model.DeviceEvidence device, java.lang.String lang) {
    }
}