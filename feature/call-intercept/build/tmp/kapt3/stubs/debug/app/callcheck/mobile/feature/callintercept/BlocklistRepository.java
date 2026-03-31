package app.callcheck.mobile.feature.callintercept;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0003\bf\u0018\u00002\u00020\u0001J&\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00052\u0006\u0010\u0007\u001a\u00020\bH\u00a6@\u00a2\u0006\u0002\u0010\tJ\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00050\u000bH\u00a6@\u00a2\u0006\u0002\u0010\fJ\u0016\u0010\r\u001a\u00020\u000e2\u0006\u0010\u0004\u001a\u00020\u0005H\u00a6@\u00a2\u0006\u0002\u0010\u000fJ\u0016\u0010\u0010\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a6@\u00a2\u0006\u0002\u0010\u000f\u00a8\u0006\u0011"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/BlocklistRepository;", "", "addToBlocklist", "", "phoneNumber", "", "reason", "timestamp", "", "(Ljava/lang/String;Ljava/lang/String;JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getBlockedNumbers", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "isBlocked", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "removeFromBlocklist", "call-intercept_debug"})
public abstract interface BlocklistRepository {
    
    /**
     * Add a phone number to the local blocklist.
     *
     * @param phoneNumber The normalized phone number to block
     * @param reason The reason for blocking
     * @param timestamp When the block was created
     */
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object addToBlocklist(@org.jetbrains.annotations.NotNull()
    java.lang.String phoneNumber, @org.jetbrains.annotations.NotNull()
    java.lang.String reason, long timestamp, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * Check if a phone number is blocked.
     *
     * @param phoneNumber The normalized phone number to check
     * @return True if the number is on the blocklist
     */
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object isBlocked(@org.jetbrains.annotations.NotNull()
    java.lang.String phoneNumber, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion);
    
    /**
     * Remove a phone number from the blocklist.
     *
     * @param phoneNumber The normalized phone number to unblock
     */
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object removeFromBlocklist(@org.jetbrains.annotations.NotNull()
    java.lang.String phoneNumber, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * Get all blocked numbers.
     *
     * @return List of blocked phone numbers
     */
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getBlockedNumbers(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<java.lang.String>> $completion);
}