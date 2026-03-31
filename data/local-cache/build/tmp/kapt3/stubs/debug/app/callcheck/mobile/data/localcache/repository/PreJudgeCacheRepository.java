package app.callcheck.mobile.data.localcache.repository;

/**
 * Tier 0 PreJudge 캐시 Repository.
 *
 * Entity ↔ Model 변환 + LRU eviction 비즈니스 로직.
 */
@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0086@\u00a2\u0006\u0002\u0010\tJ\u000e\u0010\n\u001a\u00020\u0006H\u0086@\u00a2\u0006\u0002\u0010\u000bJ\u0018\u0010\f\u001a\u0004\u0018\u00010\r2\u0006\u0010\u0007\u001a\u00020\bH\u0086@\u00a2\u0006\u0002\u0010\tJ\u001e\u0010\u000e\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\u000f\u001a\u00020\u0010H\u0086@\u00a2\u0006\u0002\u0010\u0011J\u001e\u0010\u0012\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\u0013\u001a\u00020\u0014H\u0086@\u00a2\u0006\u0002\u0010\u0015R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0016"}, d2 = {"Lapp/callcheck/mobile/data/localcache/repository/PreJudgeCacheRepository;", "", "dao", "Lapp/callcheck/mobile/data/localcache/dao/PreJudgeCacheDao;", "(Lapp/callcheck/mobile/data/localcache/dao/PreJudgeCacheDao;)V", "delete", "", "canonicalNumber", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteAll", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "lookup", "Lapp/callcheck/mobile/core/model/PreJudgeResult;", "store", "result", "Lapp/callcheck/mobile/core/model/DecisionResult;", "(Ljava/lang/String;Lapp/callcheck/mobile/core/model/DecisionResult;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateUserAction", "action", "Lapp/callcheck/mobile/core/model/UserCallAction;", "(Ljava/lang/String;Lapp/callcheck/mobile/core/model/UserCallAction;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "local-cache_debug"})
public final class PreJudgeCacheRepository {
    @org.jetbrains.annotations.NotNull()
    private final app.callcheck.mobile.data.localcache.dao.PreJudgeCacheDao dao = null;
    
    @javax.inject.Inject()
    public PreJudgeCacheRepository(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.data.localcache.dao.PreJudgeCacheDao dao) {
        super();
    }
    
    /**
     * Tier 0 핵심: 번호 hash lookup → PreJudgeResult.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object lookup(@org.jetbrains.annotations.NotNull()
    java.lang.String canonicalNumber, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super app.callcheck.mobile.core.model.PreJudgeResult> $completion) {
        return null;
    }
    
    /**
     * 판단 결과를 PreJudge 캐시에 영속 저장.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object store(@org.jetbrains.annotations.NotNull()
    java.lang.String canonicalNumber, @org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.core.model.DecisionResult result, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object updateUserAction(@org.jetbrains.annotations.NotNull()
    java.lang.String canonicalNumber, @org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.core.model.UserCallAction action, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object delete(@org.jetbrains.annotations.NotNull()
    java.lang.String canonicalNumber, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteAll(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
}