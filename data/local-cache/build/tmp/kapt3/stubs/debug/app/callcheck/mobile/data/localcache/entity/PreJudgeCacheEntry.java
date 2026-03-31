package app.callcheck.mobile.data.localcache.entity;

/**
 * Tier 0 — PreJudge 영속 캐시 엔티티.
 *
 * 전화가 울리기 전 0ms 판단을 위한 Room 테이블.
 * 모든 과거 판단 결과를 축적하여, 동일 번호 재수신 시
 * 엔진 실행 없이 즉시 Ring 상태를 반환.
 *
 * 설계:
 * - Key: canonical_number (E.164)
 * - 최대 500건 (LRU eviction)
 * - 7일 미사용 시 confidence 감쇠 (soft expire)
 * - 앱 삭제 시 자동 소멸
 * - 서버 전송 없음, 온디바이스 전용
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b \n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0087\b\u0018\u00002\u00020\u0001Bi\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\u0005\u0012\u0006\u0010\n\u001a\u00020\b\u0012\u0006\u0010\u000b\u001a\u00020\u0005\u0012\b\b\u0002\u0010\f\u001a\u00020\r\u0012\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\u0005\u0012\b\b\u0002\u0010\u000f\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0010\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0011J\t\u0010!\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\"\u001a\u00020\u0003H\u00c6\u0003J\t\u0010#\u001a\u00020\u0003H\u00c6\u0003J\t\u0010$\u001a\u00020\u0005H\u00c6\u0003J\t\u0010%\u001a\u00020\u0005H\u00c6\u0003J\t\u0010&\u001a\u00020\bH\u00c6\u0003J\t\u0010\'\u001a\u00020\u0005H\u00c6\u0003J\t\u0010(\u001a\u00020\bH\u00c6\u0003J\t\u0010)\u001a\u00020\u0005H\u00c6\u0003J\t\u0010*\u001a\u00020\rH\u00c6\u0003J\u000b\u0010+\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003Jy\u0010,\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\u00052\b\b\u0002\u0010\n\u001a\u00020\b2\b\b\u0002\u0010\u000b\u001a\u00020\u00052\b\b\u0002\u0010\f\u001a\u00020\r2\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\u00052\b\b\u0002\u0010\u000f\u001a\u00020\u00032\b\b\u0002\u0010\u0010\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010-\u001a\u00020.2\b\u0010/\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u00100\u001a\u00020\rH\u00d6\u0001J\t\u00101\u001a\u00020\u0005H\u00d6\u0001R\u0016\u0010\u0006\u001a\u00020\u00058\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0016\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0013R\u0016\u0010\t\u001a\u00020\u00058\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0013R\u0016\u0010\n\u001a\u00020\b8\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0016\u0010\u0010\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019R\u0016\u0010\f\u001a\u00020\r8\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001bR\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u0019R\u0016\u0010\u000f\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u0019R\u0018\u0010\u000e\u001a\u0004\u0018\u00010\u00058\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u0013R\u0016\u0010\u0007\u001a\u00020\b8\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u0017R\u0016\u0010\u000b\u001a\u00020\u00058\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010\u0013\u00a8\u00062"}, d2 = {"Lapp/callcheck/mobile/data/localcache/entity/PreJudgeCacheEntry;", "", "id", "", "canonicalNumber", "", "action", "riskScore", "", "category", "confidence", "summary", "hitCount", "", "lastUserAction", "lastJudgedAt", "createdAt", "(JLjava/lang/String;Ljava/lang/String;FLjava/lang/String;FLjava/lang/String;ILjava/lang/String;JJ)V", "getAction", "()Ljava/lang/String;", "getCanonicalNumber", "getCategory", "getConfidence", "()F", "getCreatedAt", "()J", "getHitCount", "()I", "getId", "getLastJudgedAt", "getLastUserAction", "getRiskScore", "getSummary", "component1", "component10", "component11", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "", "other", "hashCode", "toString", "local-cache_debug"})
@androidx.room.Entity(tableName = "pre_judge_cache", indices = {@androidx.room.Index(value = {"canonical_number"}, unique = true), @androidx.room.Index(value = {"last_judged_at"}), @androidx.room.Index(value = {"hit_count"})})
public final class PreJudgeCacheEntry {
    @androidx.room.PrimaryKey(autoGenerate = true)
    private final long id = 0L;
    @androidx.room.ColumnInfo(name = "canonical_number")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String canonicalNumber = null;
    @androidx.room.ColumnInfo(name = "action")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String action = null;
    @androidx.room.ColumnInfo(name = "risk_score")
    private final float riskScore = 0.0F;
    @androidx.room.ColumnInfo(name = "category")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String category = null;
    @androidx.room.ColumnInfo(name = "confidence")
    private final float confidence = 0.0F;
    @androidx.room.ColumnInfo(name = "summary")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String summary = null;
    @androidx.room.ColumnInfo(name = "hit_count")
    private final int hitCount = 0;
    @androidx.room.ColumnInfo(name = "last_user_action")
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String lastUserAction = null;
    @androidx.room.ColumnInfo(name = "last_judged_at")
    private final long lastJudgedAt = 0L;
    @androidx.room.ColumnInfo(name = "created_at")
    private final long createdAt = 0L;
    
    public PreJudgeCacheEntry(long id, @org.jetbrains.annotations.NotNull()
    java.lang.String canonicalNumber, @org.jetbrains.annotations.NotNull()
    java.lang.String action, float riskScore, @org.jetbrains.annotations.NotNull()
    java.lang.String category, float confidence, @org.jetbrains.annotations.NotNull()
    java.lang.String summary, int hitCount, @org.jetbrains.annotations.Nullable()
    java.lang.String lastUserAction, long lastJudgedAt, long createdAt) {
        super();
    }
    
    public final long getId() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getCanonicalNumber() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getAction() {
        return null;
    }
    
    public final float getRiskScore() {
        return 0.0F;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getCategory() {
        return null;
    }
    
    public final float getConfidence() {
        return 0.0F;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getSummary() {
        return null;
    }
    
    public final int getHitCount() {
        return 0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getLastUserAction() {
        return null;
    }
    
    public final long getLastJudgedAt() {
        return 0L;
    }
    
    public final long getCreatedAt() {
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
    
    public final float component4() {
        return 0.0F;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component5() {
        return null;
    }
    
    public final float component6() {
        return 0.0F;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component7() {
        return null;
    }
    
    public final int component8() {
        return 0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final app.callcheck.mobile.data.localcache.entity.PreJudgeCacheEntry copy(long id, @org.jetbrains.annotations.NotNull()
    java.lang.String canonicalNumber, @org.jetbrains.annotations.NotNull()
    java.lang.String action, float riskScore, @org.jetbrains.annotations.NotNull()
    java.lang.String category, float confidence, @org.jetbrains.annotations.NotNull()
    java.lang.String summary, int hitCount, @org.jetbrains.annotations.Nullable()
    java.lang.String lastUserAction, long lastJudgedAt, long createdAt) {
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