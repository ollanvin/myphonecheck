package app.callcheck.mobile.feature.callintercept;

@dagger.hilt.android.AndroidEntryPoint()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0014H\u0002J\u0018\u0010\u0015\u001a\u00020\u00122\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0013\u001a\u00020\u0014H\u0002J\u0018\u0010\u0018\u001a\u00020\u00122\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0013\u001a\u00020\u0014H\u0002J\u0018\u0010\u0019\u001a\u00020\u00122\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0013\u001a\u00020\u0014H\u0002J\u0018\u0010\u001a\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u001b\u001a\u00020\u0014H\u0002J\u001a\u0010\u001c\u001a\u00020\u00122\u0006\u0010\u0016\u001a\u00020\u00172\b\u0010\u001d\u001a\u0004\u0018\u00010\u001eH\u0016J\u0018\u0010\u001f\u001a\u00020\u00122\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0013\u001a\u00020\u0014H\u0002R\u001e\u0010\u0003\u001a\u00020\u00048\u0006@\u0006X\u0087.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\bR\u001e\u0010\t\u001a\u00020\n8\u0006@\u0006X\u0087.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000eR\u000e\u0010\u000f\u001a\u00020\u0010X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006 "}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/CallActionReceiver;", "Landroid/content/BroadcastReceiver;", "()V", "blocklistRepository", "Lapp/callcheck/mobile/feature/callintercept/BlocklistRepository;", "getBlocklistRepository", "()Lapp/callcheck/mobile/feature/callintercept/BlocklistRepository;", "setBlocklistRepository", "(Lapp/callcheck/mobile/feature/callintercept/BlocklistRepository;)V", "decisionNotificationManager", "Lapp/callcheck/mobile/feature/callintercept/DecisionNotificationManager;", "getDecisionNotificationManager", "()Lapp/callcheck/mobile/feature/callintercept/DecisionNotificationManager;", "setDecisionNotificationManager", "(Lapp/callcheck/mobile/feature/callintercept/DecisionNotificationManager;)V", "receiverScope", "Lkotlinx/coroutines/CoroutineScope;", "addToBlocklist", "", "phoneNumber", "", "handleBlockAction", "context", "Landroid/content/Context;", "handleDetailAction", "handleRejectAction", "logUserAction", "action", "onReceive", "intent", "Landroid/content/Intent;", "showBlockConfirmation", "call-intercept_debug"})
public final class CallActionReceiver extends android.content.BroadcastReceiver {
    @javax.inject.Inject()
    public app.callcheck.mobile.feature.callintercept.DecisionNotificationManager decisionNotificationManager;
    @javax.inject.Inject()
    public app.callcheck.mobile.feature.callintercept.BlocklistRepository blocklistRepository;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.CoroutineScope receiverScope = null;
    
    public CallActionReceiver() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final app.callcheck.mobile.feature.callintercept.DecisionNotificationManager getDecisionNotificationManager() {
        return null;
    }
    
    public final void setDecisionNotificationManager(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.callintercept.DecisionNotificationManager p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final app.callcheck.mobile.feature.callintercept.BlocklistRepository getBlocklistRepository() {
        return null;
    }
    
    public final void setBlocklistRepository(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.callintercept.BlocklistRepository p0) {
    }
    
    @java.lang.Override()
    public void onReceive(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.Nullable()
    android.content.Intent intent) {
    }
    
    private final void handleDetailAction(android.content.Context context, java.lang.String phoneNumber) {
    }
    
    private final void handleRejectAction(android.content.Context context, java.lang.String phoneNumber) {
    }
    
    private final void handleBlockAction(android.content.Context context, java.lang.String phoneNumber) {
    }
    
    private final void addToBlocklist(java.lang.String phoneNumber) {
    }
    
    private final void logUserAction(java.lang.String phoneNumber, java.lang.String action) {
    }
    
    private final void showBlockConfirmation(android.content.Context context, java.lang.String phoneNumber) {
    }
}