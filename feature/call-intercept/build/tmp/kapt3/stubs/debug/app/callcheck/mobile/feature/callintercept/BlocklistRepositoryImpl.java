package app.callcheck.mobile.feature.callintercept;

/**
 * In-memory implementation of BlocklistRepository.
 *
 * TODO: Replace with Room-backed implementation for persistence across app restarts.
 */
@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0007\u0018\u00002\u00020\u0001:\u0001\u0015B\u0007\b\u0007\u00a2\u0006\u0002\u0010\u0002J&\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\u00052\u0006\u0010\n\u001a\u00020\u00052\u0006\u0010\u000b\u001a\u00020\fH\u0096@\u00a2\u0006\u0002\u0010\rJ\u0014\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00050\u000fH\u0096@\u00a2\u0006\u0002\u0010\u0010J\u0016\u0010\u0011\u001a\u00020\u00122\u0006\u0010\t\u001a\u00020\u0005H\u0096@\u00a2\u0006\u0002\u0010\u0013J\u0016\u0010\u0014\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\u0005H\u0096@\u00a2\u0006\u0002\u0010\u0013R\u001a\u0010\u0003\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00060\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0016"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/BlocklistRepositoryImpl;", "Lapp/callcheck/mobile/feature/callintercept/BlocklistRepository;", "()V", "blocklist", "Ljava/util/concurrent/ConcurrentHashMap;", "", "Lapp/callcheck/mobile/feature/callintercept/BlocklistRepositoryImpl$BlockEntry;", "addToBlocklist", "", "phoneNumber", "reason", "timestamp", "", "(Ljava/lang/String;Ljava/lang/String;JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getBlockedNumbers", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "isBlocked", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "removeFromBlocklist", "BlockEntry", "call-intercept_debug"})
public final class BlocklistRepositoryImpl implements app.callcheck.mobile.feature.callintercept.BlocklistRepository {
    @org.jetbrains.annotations.NotNull()
    private final java.util.concurrent.ConcurrentHashMap<java.lang.String, app.callcheck.mobile.feature.callintercept.BlocklistRepositoryImpl.BlockEntry> blocklist = null;
    
    @javax.inject.Inject()
    public BlocklistRepositoryImpl() {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object addToBlocklist(@org.jetbrains.annotations.NotNull()
    java.lang.String phoneNumber, @org.jetbrains.annotations.NotNull()
    java.lang.String reason, long timestamp, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object isBlocked(@org.jetbrains.annotations.NotNull()
    java.lang.String phoneNumber, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object removeFromBlocklist(@org.jetbrains.annotations.NotNull()
    java.lang.String phoneNumber, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object getBlockedNumbers(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<java.lang.String>> $completion) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u000b\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0082\b\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\u0002\u0010\u0007J\t\u0010\r\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u000e\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u000f\u001a\u00020\u0006H\u00c6\u0003J\'\u0010\u0010\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0006H\u00c6\u0001J\u0013\u0010\u0011\u001a\u00020\u00122\b\u0010\u0013\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0014\u001a\u00020\u0015H\u00d6\u0001J\t\u0010\u0016\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\tR\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\f\u00a8\u0006\u0017"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/BlocklistRepositoryImpl$BlockEntry;", "", "phoneNumber", "", "reason", "timestamp", "", "(Ljava/lang/String;Ljava/lang/String;J)V", "getPhoneNumber", "()Ljava/lang/String;", "getReason", "getTimestamp", "()J", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "", "toString", "call-intercept_debug"})
    static final class BlockEntry {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String phoneNumber = null;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String reason = null;
        private final long timestamp = 0L;
        
        public BlockEntry(@org.jetbrains.annotations.NotNull()
        java.lang.String phoneNumber, @org.jetbrains.annotations.NotNull()
        java.lang.String reason, long timestamp) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getPhoneNumber() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getReason() {
            return null;
        }
        
        public final long getTimestamp() {
            return 0L;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component2() {
            return null;
        }
        
        public final long component3() {
            return 0L;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final app.callcheck.mobile.feature.callintercept.BlocklistRepositoryImpl.BlockEntry copy(@org.jetbrains.annotations.NotNull()
        java.lang.String phoneNumber, @org.jetbrains.annotations.NotNull()
        java.lang.String reason, long timestamp) {
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