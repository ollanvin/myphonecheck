package app.callcheck.mobile.data.calllog;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0010\t\n\u0002\b3\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u00b1\u0001\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0003\u0012\b\b\u0002\u0010\b\u001a\u00020\u0003\u0012\b\b\u0002\u0010\t\u001a\u00020\n\u0012\b\b\u0002\u0010\u000b\u001a\u00020\n\u0012\b\b\u0002\u0010\f\u001a\u00020\u0003\u0012\b\b\u0002\u0010\r\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\n\u0012\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\n\u0012\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\n\u0012\n\b\u0002\u0010\u0011\u001a\u0004\u0018\u00010\n\u0012\n\b\u0002\u0010\u0012\u001a\u0004\u0018\u00010\n\u0012\n\b\u0002\u0010\u0013\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\u0002\u0010\u0014J\t\u0010+\u001a\u00020\u0003H\u00c6\u0003J\t\u0010,\u001a\u00020\u0003H\u00c6\u0003J\u0010\u0010-\u001a\u0004\u0018\u00010\nH\u00c6\u0003\u00a2\u0006\u0002\u0010\u001cJ\u0010\u0010.\u001a\u0004\u0018\u00010\nH\u00c6\u0003\u00a2\u0006\u0002\u0010\u001cJ\u0010\u0010/\u001a\u0004\u0018\u00010\nH\u00c6\u0003\u00a2\u0006\u0002\u0010\u001cJ\u0010\u00100\u001a\u0004\u0018\u00010\nH\u00c6\u0003\u00a2\u0006\u0002\u0010\u001cJ\u0010\u00101\u001a\u0004\u0018\u00010\nH\u00c6\u0003\u00a2\u0006\u0002\u0010\u001cJ\u0010\u00102\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003\u00a2\u0006\u0002\u0010&J\t\u00103\u001a\u00020\u0003H\u00c6\u0003J\t\u00104\u001a\u00020\u0003H\u00c6\u0003J\t\u00105\u001a\u00020\u0003H\u00c6\u0003J\t\u00106\u001a\u00020\u0003H\u00c6\u0003J\t\u00107\u001a\u00020\u0003H\u00c6\u0003J\t\u00108\u001a\u00020\nH\u00c6\u0003J\t\u00109\u001a\u00020\nH\u00c6\u0003J\t\u0010:\u001a\u00020\u0003H\u00c6\u0003J\u00ba\u0001\u0010;\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\u00032\b\b\u0002\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\n2\b\b\u0002\u0010\f\u001a\u00020\u00032\b\b\u0002\u0010\r\u001a\u00020\u00032\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\n2\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\n2\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\n2\n\b\u0002\u0010\u0011\u001a\u0004\u0018\u00010\n2\n\b\u0002\u0010\u0012\u001a\u0004\u0018\u00010\n2\n\b\u0002\u0010\u0013\u001a\u0004\u0018\u00010\u0003H\u00c6\u0001\u00a2\u0006\u0002\u0010<J\u0013\u0010=\u001a\u00020>2\b\u0010?\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010@\u001a\u00020\u0003H\u00d6\u0001J\t\u0010A\u001a\u00020BH\u00d6\u0001R\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\u000b\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0011\u0010\b\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0016R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0016R\u0015\u0010\u0010\u001a\u0004\u0018\u00010\n\u00a2\u0006\n\n\u0002\u0010\u001d\u001a\u0004\b\u001b\u0010\u001cR\u0015\u0010\u000f\u001a\u0004\u0018\u00010\n\u00a2\u0006\n\n\u0002\u0010\u001d\u001a\u0004\b\u001e\u0010\u001cR\u0015\u0010\u0012\u001a\u0004\u0018\u00010\n\u00a2\u0006\n\n\u0002\u0010\u001d\u001a\u0004\b\u001f\u0010\u001cR\u0015\u0010\u000e\u001a\u0004\u0018\u00010\n\u00a2\u0006\n\n\u0002\u0010\u001d\u001a\u0004\b \u0010\u001cR\u0015\u0010\u0011\u001a\u0004\u0018\u00010\n\u00a2\u0006\n\n\u0002\u0010\u001d\u001a\u0004\b!\u0010\u001cR\u0011\u0010\r\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010\u0016R\u0011\u0010\u0007\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010\u0016R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010\u0016R\u0015\u0010\u0013\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\n\n\u0002\u0010\'\u001a\u0004\b%\u0010&R\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b(\u0010\u0016R\u0011\u0010\f\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b)\u0010\u0016R\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b*\u0010\u0018\u00a8\u0006C"}, d2 = {"Lapp/callcheck/mobile/data/calllog/CallHistoryDetail;", "", "outgoingCount", "", "incomingCount", "answeredCount", "rejectedCount", "missedCount", "connectedCount", "totalDurationSec", "", "avgDurationSec", "shortCallCount", "longCallCount", "lastOutgoingAt", "lastIncomingAt", "lastConnectedAt", "lastRejectedAt", "lastMissedAt", "recentDaysContact", "(IIIIIIJJIILjava/lang/Long;Ljava/lang/Long;Ljava/lang/Long;Ljava/lang/Long;Ljava/lang/Long;Ljava/lang/Integer;)V", "getAnsweredCount", "()I", "getAvgDurationSec", "()J", "getConnectedCount", "getIncomingCount", "getLastConnectedAt", "()Ljava/lang/Long;", "Ljava/lang/Long;", "getLastIncomingAt", "getLastMissedAt", "getLastOutgoingAt", "getLastRejectedAt", "getLongCallCount", "getMissedCount", "getOutgoingCount", "getRecentDaysContact", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "getRejectedCount", "getShortCallCount", "getTotalDurationSec", "component1", "component10", "component11", "component12", "component13", "component14", "component15", "component16", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "(IIIIIIJJIILjava/lang/Long;Ljava/lang/Long;Ljava/lang/Long;Ljava/lang/Long;Ljava/lang/Long;Ljava/lang/Integer;)Lapp/callcheck/mobile/data/calllog/CallHistoryDetail;", "equals", "", "other", "hashCode", "toString", "", "calllog_debug"})
public final class CallHistoryDetail {
    private final int outgoingCount = 0;
    private final int incomingCount = 0;
    private final int answeredCount = 0;
    private final int rejectedCount = 0;
    private final int missedCount = 0;
    private final int connectedCount = 0;
    private final long totalDurationSec = 0L;
    private final long avgDurationSec = 0L;
    private final int shortCallCount = 0;
    private final int longCallCount = 0;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Long lastOutgoingAt = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Long lastIncomingAt = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Long lastConnectedAt = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Long lastRejectedAt = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Long lastMissedAt = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Integer recentDaysContact = null;
    
    public CallHistoryDetail(int outgoingCount, int incomingCount, int answeredCount, int rejectedCount, int missedCount, int connectedCount, long totalDurationSec, long avgDurationSec, int shortCallCount, int longCallCount, @org.jetbrains.annotations.Nullable()
    java.lang.Long lastOutgoingAt, @org.jetbrains.annotations.Nullable()
    java.lang.Long lastIncomingAt, @org.jetbrains.annotations.Nullable()
    java.lang.Long lastConnectedAt, @org.jetbrains.annotations.Nullable()
    java.lang.Long lastRejectedAt, @org.jetbrains.annotations.Nullable()
    java.lang.Long lastMissedAt, @org.jetbrains.annotations.Nullable()
    java.lang.Integer recentDaysContact) {
        super();
    }
    
    public final int getOutgoingCount() {
        return 0;
    }
    
    public final int getIncomingCount() {
        return 0;
    }
    
    public final int getAnsweredCount() {
        return 0;
    }
    
    public final int getRejectedCount() {
        return 0;
    }
    
    public final int getMissedCount() {
        return 0;
    }
    
    public final int getConnectedCount() {
        return 0;
    }
    
    public final long getTotalDurationSec() {
        return 0L;
    }
    
    public final long getAvgDurationSec() {
        return 0L;
    }
    
    public final int getShortCallCount() {
        return 0;
    }
    
    public final int getLongCallCount() {
        return 0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long getLastOutgoingAt() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long getLastIncomingAt() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long getLastConnectedAt() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long getLastRejectedAt() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long getLastMissedAt() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getRecentDaysContact() {
        return null;
    }
    
    public CallHistoryDetail() {
        super();
    }
    
    public final int component1() {
        return 0;
    }
    
    public final int component10() {
        return 0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long component11() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long component12() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long component13() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long component14() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long component15() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer component16() {
        return null;
    }
    
    public final int component2() {
        return 0;
    }
    
    public final int component3() {
        return 0;
    }
    
    public final int component4() {
        return 0;
    }
    
    public final int component5() {
        return 0;
    }
    
    public final int component6() {
        return 0;
    }
    
    public final long component7() {
        return 0L;
    }
    
    public final long component8() {
        return 0L;
    }
    
    public final int component9() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final app.callcheck.mobile.data.calllog.CallHistoryDetail copy(int outgoingCount, int incomingCount, int answeredCount, int rejectedCount, int missedCount, int connectedCount, long totalDurationSec, long avgDurationSec, int shortCallCount, int longCallCount, @org.jetbrains.annotations.Nullable()
    java.lang.Long lastOutgoingAt, @org.jetbrains.annotations.Nullable()
    java.lang.Long lastIncomingAt, @org.jetbrains.annotations.Nullable()
    java.lang.Long lastConnectedAt, @org.jetbrains.annotations.Nullable()
    java.lang.Long lastRejectedAt, @org.jetbrains.annotations.Nullable()
    java.lang.Long lastMissedAt, @org.jetbrains.annotations.Nullable()
    java.lang.Integer recentDaysContact) {
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