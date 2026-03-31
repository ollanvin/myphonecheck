package app.callcheck.mobile.data.localcache.di;

/**
 * 로컬 캐시 Hilt 모듈.
 *
 * Room Database + DAO + Repository 제공.
 * 앱 전체 수명 동안 단일 인스턴스 보장 (Singleton).
 */
@dagger.Module()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0012\u0010\u0003\u001a\u00020\u00042\b\b\u0001\u0010\u0005\u001a\u00020\u0006H\u0007J\u0010\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\u0004H\u0007J\u0010\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\bH\u0007J\u0010\u0010\r\u001a\u00020\u000e2\u0006\u0010\t\u001a\u00020\u0004H\u0007J\u0010\u0010\u000f\u001a\u00020\u00102\u0006\u0010\f\u001a\u00020\u000eH\u0007\u00a8\u0006\u0011"}, d2 = {"Lapp/callcheck/mobile/data/localcache/di/LocalCacheModule;", "", "()V", "provideDatabase", "Lapp/callcheck/mobile/data/localcache/db/CallCheckDatabase;", "context", "Landroid/content/Context;", "providePreJudgeCacheDao", "Lapp/callcheck/mobile/data/localcache/dao/PreJudgeCacheDao;", "database", "providePreJudgeCacheRepository", "Lapp/callcheck/mobile/data/localcache/repository/PreJudgeCacheRepository;", "dao", "provideUserCallRecordDao", "Lapp/callcheck/mobile/data/localcache/dao/UserCallRecordDao;", "provideUserCallRecordRepository", "Lapp/callcheck/mobile/data/localcache/repository/UserCallRecordRepository;", "local-cache_debug"})
@dagger.hilt.InstallIn(value = {dagger.hilt.components.SingletonComponent.class})
public final class LocalCacheModule {
    @org.jetbrains.annotations.NotNull()
    public static final app.callcheck.mobile.data.localcache.di.LocalCacheModule INSTANCE = null;
    
    private LocalCacheModule() {
        super();
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final app.callcheck.mobile.data.localcache.db.CallCheckDatabase provideDatabase(@dagger.hilt.android.qualifiers.ApplicationContext()
    @org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final app.callcheck.mobile.data.localcache.dao.PreJudgeCacheDao providePreJudgeCacheDao(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.data.localcache.db.CallCheckDatabase database) {
        return null;
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final app.callcheck.mobile.data.localcache.repository.PreJudgeCacheRepository providePreJudgeCacheRepository(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.data.localcache.dao.PreJudgeCacheDao dao) {
        return null;
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final app.callcheck.mobile.data.localcache.dao.UserCallRecordDao provideUserCallRecordDao(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.data.localcache.db.CallCheckDatabase database) {
        return null;
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final app.callcheck.mobile.data.localcache.repository.UserCallRecordRepository provideUserCallRecordRepository(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.data.localcache.dao.UserCallRecordDao dao) {
        return null;
    }
}