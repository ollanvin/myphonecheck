package app.callcheck.mobile.feature.decisionengine.integration;

/**
 * Call handling strategies that map to Android CallScreeningService responses.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0006\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006\u00a8\u0006\u0007"}, d2 = {"Lapp/callcheck/mobile/feature/decisionengine/integration/CallHandlingStrategy;", "", "(Ljava/lang/String;I)V", "ALLOW_WITH_NOTIFICATION", "SILENT_REJECT", "BLOCK_AND_LOG", "NORMAL_HANDLING", "decision-engine_debug"})
public enum CallHandlingStrategy {
    /*public static final*/ ALLOW_WITH_NOTIFICATION /* = new ALLOW_WITH_NOTIFICATION() */,
    /*public static final*/ SILENT_REJECT /* = new SILENT_REJECT() */,
    /*public static final*/ BLOCK_AND_LOG /* = new BLOCK_AND_LOG() */,
    /*public static final*/ NORMAL_HANDLING /* = new NORMAL_HANDLING() */;
    
    CallHandlingStrategy() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public static kotlin.enums.EnumEntries<app.callcheck.mobile.feature.decisionengine.integration.CallHandlingStrategy> getEntries() {
        return null;
    }
}