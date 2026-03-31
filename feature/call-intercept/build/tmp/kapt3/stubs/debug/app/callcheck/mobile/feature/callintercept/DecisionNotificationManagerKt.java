package app.callcheck.mobile.feature.callintercept;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000\u0012\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0010\b\n\u0002\b\u0002\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0002\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0003\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0004\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0005\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0006\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0007\u001a\u00020\bX\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\t\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\n"}, d2 = {"ACTION_BLOCK", "", "ACTION_DETAIL", "ACTION_REJECT", "CHANNEL_ID", "CHANNEL_NAME", "EXTRA_PHONE_NUMBER", "NOTIFICATION_ID_PREFIX", "", "TAG", "call-intercept_debug"})
public final class DecisionNotificationManagerKt {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "DecisionNotification";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String CHANNEL_ID = "callcheck_decisions";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String CHANNEL_NAME = "CallCheck \ud310\uc815";
    private static final int NOTIFICATION_ID_PREFIX = 1000;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String ACTION_REJECT = "action_reject";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String ACTION_BLOCK = "action_block";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String ACTION_DETAIL = "action_detail";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String EXTRA_PHONE_NUMBER = "extra_phone_number";
}