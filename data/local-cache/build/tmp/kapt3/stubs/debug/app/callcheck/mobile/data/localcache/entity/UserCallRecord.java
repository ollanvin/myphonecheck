package app.callcheck.mobile.data.localcache.entity;

/**
 * 사용자 통화 기록 엔티티.
 *
 * ┌──────────────────────────────────────────────────────────────┐
 * │ 핵심 설계 원칙:                                              │
 * │  • 키 = canonicalNumber (E.164 정규화)                       │
 * │  • AI 판단(RiskLevel)과 독립된 사용자 판단 축적               │
 * │  • TTL 캐시(SearchResult)와 완전 분리                        │
 * │  • 사용자 메모/태그만 영구 저장                               │
 * │  • 서버 전송 절대 없음 — 온디바이스 전용                      │
 * └──────────────────────────────────────────────────────────────┘
 *
 * 인덱스:
 * - canonical_number: 빠른 번호 조회
 * - tag: 태그별 필터링
 * - updated_at: 최신순 정렬
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\b\n\u0002\b\u001e\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0087\b\u0018\u00002\u00020\u0001By\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u0005\u0012\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u0005\u0012\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u0005\u0012\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u0005\u0012\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\u0005\u0012\b\b\u0002\u0010\f\u001a\u00020\r\u0012\b\b\u0002\u0010\u000e\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u000f\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0010J\t\u0010\u001f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010 \u001a\u00020\u0003H\u00c6\u0003J\t\u0010!\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\"\u001a\u00020\u0005H\u00c6\u0003J\t\u0010#\u001a\u00020\u0005H\u00c6\u0003J\u000b\u0010$\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\u000b\u0010%\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\u000b\u0010&\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\u000b\u0010\'\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\u000b\u0010(\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\t\u0010)\u001a\u00020\rH\u00c6\u0003J\u0081\u0001\u0010*\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\u00052\b\b\u0002\u0010\f\u001a\u00020\r2\b\b\u0002\u0010\u000e\u001a\u00020\u00032\b\b\u0002\u0010\u000f\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010+\u001a\u00020,2\b\u0010-\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010.\u001a\u00020\rH\u00d6\u0001J\t\u0010/\u001a\u00020\u0005H\u00d6\u0001R\u0018\u0010\u000b\u001a\u0004\u0018\u00010\u00058\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0018\u0010\n\u001a\u0004\u0018\u00010\u00058\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0012R\u0016\u0010\f\u001a\u00020\r8\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0016\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0012R\u0016\u0010\u000e\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0016\u0010\u0006\u001a\u00020\u00058\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0012R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0018R\u0018\u0010\t\u001a\u0004\u0018\u00010\u00058\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0012R\u0018\u0010\b\u001a\u0004\u0018\u00010\u00058\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u0012R\u0018\u0010\u0007\u001a\u0004\u0018\u00010\u00058\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u0012R\u0016\u0010\u000f\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u0018\u00a8\u00060"}, d2 = {"Lapp/callcheck/mobile/data/localcache/entity/UserCallRecord;", "", "id", "", "canonicalNumber", "", "displayNumber", "tag", "memo", "lastAction", "aiRiskLevel", "aiCategory", "callCount", "", "createdAt", "updatedAt", "(JLjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;IJJ)V", "getAiCategory", "()Ljava/lang/String;", "getAiRiskLevel", "getCallCount", "()I", "getCanonicalNumber", "getCreatedAt", "()J", "getDisplayNumber", "getId", "getLastAction", "getMemo", "getTag", "getUpdatedAt", "component1", "component10", "component11", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "", "other", "hashCode", "toString", "local-cache_debug"})
@androidx.room.Entity(tableName = "user_call_records", indices = {@androidx.room.Index(value = {"canonical_number"}, unique = true), @androidx.room.Index(value = {"tag"}), @androidx.room.Index(value = {"updated_at"})})
public final class UserCallRecord {
    @androidx.room.PrimaryKey(autoGenerate = true)
    private final long id = 0L;
    
    /**
     * E.164 정규화 번호 (예: +821012345678)
     */
    @androidx.room.ColumnInfo(name = "canonical_number")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String canonicalNumber = null;
    
    /**
     * 사용자가 보는 원본 번호 (예: 010-1234-5678)
     */
    @androidx.room.ColumnInfo(name = "display_number")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String displayNumber = null;
    
    /**
     * 사용자 태그 (safe, spam, business, personal, delivery, custom)
     */
    @androidx.room.ColumnInfo(name = "tag")
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String tag = null;
    
    /**
     * 사용자 메모 (자유 텍스트)
     */
    @androidx.room.ColumnInfo(name = "memo")
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String memo = null;
    
    /**
     * 마지막 행동 (answered, rejected, blocked, missed)
     */
    @androidx.room.ColumnInfo(name = "last_action")
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String lastAction = null;
    
    /**
     * AI가 마지막으로 판단한 리스크 레벨 (참조용)
     */
    @androidx.room.ColumnInfo(name = "ai_risk_level")
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String aiRiskLevel = null;
    
    /**
     * AI가 마지막으로 판단한 카테고리 (참조용)
     */
    @androidx.room.ColumnInfo(name = "ai_category")
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String aiCategory = null;
    
    /**
     * 이 번호의 총 통화 횟수
     */
    @androidx.room.ColumnInfo(name = "call_count")
    private final int callCount = 0;
    
    /**
     * 최초 기록 시각 (epoch millis)
     */
    @androidx.room.ColumnInfo(name = "created_at")
    private final long createdAt = 0L;
    
    /**
     * 최종 수정 시각 (epoch millis)
     */
    @androidx.room.ColumnInfo(name = "updated_at")
    private final long updatedAt = 0L;
    
    public UserCallRecord(long id, @org.jetbrains.annotations.NotNull()
    java.lang.String canonicalNumber, @org.jetbrains.annotations.NotNull()
    java.lang.String displayNumber, @org.jetbrains.annotations.Nullable()
    java.lang.String tag, @org.jetbrains.annotations.Nullable()
    java.lang.String memo, @org.jetbrains.annotations.Nullable()
    java.lang.String lastAction, @org.jetbrains.annotations.Nullable()
    java.lang.String aiRiskLevel, @org.jetbrains.annotations.Nullable()
    java.lang.String aiCategory, int callCount, long createdAt, long updatedAt) {
        super();
    }
    
    public final long getId() {
        return 0L;
    }
    
    /**
     * E.164 정규화 번호 (예: +821012345678)
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getCanonicalNumber() {
        return null;
    }
    
    /**
     * 사용자가 보는 원본 번호 (예: 010-1234-5678)
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getDisplayNumber() {
        return null;
    }
    
    /**
     * 사용자 태그 (safe, spam, business, personal, delivery, custom)
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getTag() {
        return null;
    }
    
    /**
     * 사용자 메모 (자유 텍스트)
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getMemo() {
        return null;
    }
    
    /**
     * 마지막 행동 (answered, rejected, blocked, missed)
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getLastAction() {
        return null;
    }
    
    /**
     * AI가 마지막으로 판단한 리스크 레벨 (참조용)
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getAiRiskLevel() {
        return null;
    }
    
    /**
     * AI가 마지막으로 판단한 카테고리 (참조용)
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getAiCategory() {
        return null;
    }
    
    /**
     * 이 번호의 총 통화 횟수
     */
    public final int getCallCount() {
        return 0;
    }
    
    /**
     * 최초 기록 시각 (epoch millis)
     */
    public final long getCreatedAt() {
        return 0L;
    }
    
    /**
     * 최종 수정 시각 (epoch millis)
     */
    public final long getUpdatedAt() {
        return 0L;
    }
    
    public final long component1() {
        return 0L;
    }
    
    public final long component10() {
        return 0L;
    }
    
    public final long component11() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component5() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component6() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component7() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component8() {
        return null;
    }
    
    public final int component9() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final app.callcheck.mobile.data.localcache.entity.UserCallRecord copy(long id, @org.jetbrains.annotations.NotNull()
    java.lang.String canonicalNumber, @org.jetbrains.annotations.NotNull()
    java.lang.String displayNumber, @org.jetbrains.annotations.Nullable()
    java.lang.String tag, @org.jetbrains.annotations.Nullable()
    java.lang.String memo, @org.jetbrains.annotations.Nullable()
    java.lang.String lastAction, @org.jetbrains.annotations.Nullable()
    java.lang.String aiRiskLevel, @org.jetbrains.annotations.Nullable()
    java.lang.String aiCategory, int callCount, long createdAt, long updatedAt) {
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