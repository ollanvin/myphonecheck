package app.callcheck.mobile.data.localcache.dao;

/**
 * Tier 0 PreJudge 캐시 DAO.
 *
 * 핵심 연산:
 * - lookup: 번호 hash → 0ms 판단 반환 (SELECT by indexed column)
 * - upsert: 판단 결과 저장/갱신
 * - evict: LRU 기반 용량 관리 (최대 500건)
 *
 * 성능 목표:
 * - lookup: <1ms (인덱스 기반, WAL 모드)
 * - upsert: <5ms
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\bg\u0018\u00002\u00020\u0001J\u000e\u0010\u0002\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u0005\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010\bJ\u0016\u0010\t\u001a\u00020\u00032\u0006\u0010\n\u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010\fJ\u000e\u0010\r\u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010\u0004J \u0010\u000e\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\u000f\u001a\u00020\u0010H\u00a7@\u00a2\u0006\u0002\u0010\u0011J\u0018\u0010\u0012\u001a\u0004\u0018\u00010\u00132\u0006\u0010\u0006\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010\bJ(\u0010\u0014\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u0015\u001a\u00020\u00072\b\b\u0002\u0010\u000f\u001a\u00020\u0010H\u00a7@\u00a2\u0006\u0002\u0010\u0016J\u0016\u0010\u0017\u001a\u00020\u00102\u0006\u0010\u0018\u001a\u00020\u0013H\u00a7@\u00a2\u0006\u0002\u0010\u0019\u00a8\u0006\u001a"}, d2 = {"Lapp/callcheck/mobile/data/localcache/dao/PreJudgeCacheDao;", "", "deleteAll", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteByNumber", "canonicalNumber", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "evictOldest", "count", "", "(ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getCount", "incrementHit", "now", "", "(Ljava/lang/String;JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "lookup", "Lapp/callcheck/mobile/data/localcache/entity/PreJudgeCacheEntry;", "updateUserAction", "actionKey", "(Ljava/lang/String;Ljava/lang/String;JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "upsert", "entry", "(Lapp/callcheck/mobile/data/localcache/entity/PreJudgeCacheEntry;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "local-cache_debug"})
@androidx.room.Dao()
public abstract interface PreJudgeCacheDao {
    
    @androidx.room.Query(value = "SELECT * FROM pre_judge_cache WHERE canonical_number = :canonicalNumber LIMIT 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object lookup(@org.jetbrains.annotations.NotNull()
    java.lang.String canonicalNumber, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super app.callcheck.mobile.data.localcache.entity.PreJudgeCacheEntry> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object upsert(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.data.localcache.entity.PreJudgeCacheEntry entry, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Query(value = "SELECT COUNT(*) FROM pre_judge_cache")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getCount(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion);
    
    @androidx.room.Query(value = "\n        DELETE FROM pre_judge_cache WHERE id IN (\n            SELECT id FROM pre_judge_cache\n            ORDER BY hit_count ASC, last_judged_at ASC\n            LIMIT :count\n        )\n    ")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object evictOldest(int count, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM pre_judge_cache WHERE canonical_number = :canonicalNumber")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteByNumber(@org.jetbrains.annotations.NotNull()
    java.lang.String canonicalNumber, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM pre_judge_cache")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteAll(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "\n        UPDATE pre_judge_cache\n        SET hit_count = hit_count + 1,\n            last_judged_at = :now\n        WHERE canonical_number = :canonicalNumber\n    ")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object incrementHit(@org.jetbrains.annotations.NotNull()
    java.lang.String canonicalNumber, long now, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "\n        UPDATE pre_judge_cache\n        SET last_user_action = :actionKey,\n            last_judged_at = :now\n        WHERE canonical_number = :canonicalNumber\n    ")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateUserAction(@org.jetbrains.annotations.NotNull()
    java.lang.String canonicalNumber, @org.jetbrains.annotations.NotNull()
    java.lang.String actionKey, long now, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * Tier 0 PreJudge 캐시 DAO.
     *
     * 핵심 연산:
     * - lookup: 번호 hash → 0ms 판단 반환 (SELECT by indexed column)
     * - upsert: 판단 결과 저장/갱신
     * - evict: LRU 기반 용량 관리 (최대 500건)
     *
     * 성능 목표:
     * - lookup: <1ms (인덱스 기반, WAL 모드)
     * - upsert: <5ms
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 3, xi = 48)
    public static final class DefaultImpls {
    }
}