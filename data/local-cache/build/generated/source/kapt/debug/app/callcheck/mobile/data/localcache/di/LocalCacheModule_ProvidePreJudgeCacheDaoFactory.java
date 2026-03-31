package app.callcheck.mobile.data.localcache.di;

import app.callcheck.mobile.data.localcache.dao.PreJudgeCacheDao;
import app.callcheck.mobile.data.localcache.db.CallCheckDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class LocalCacheModule_ProvidePreJudgeCacheDaoFactory implements Factory<PreJudgeCacheDao> {
  private final Provider<CallCheckDatabase> databaseProvider;

  public LocalCacheModule_ProvidePreJudgeCacheDaoFactory(
      Provider<CallCheckDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public PreJudgeCacheDao get() {
    return providePreJudgeCacheDao(databaseProvider.get());
  }

  public static LocalCacheModule_ProvidePreJudgeCacheDaoFactory create(
      Provider<CallCheckDatabase> databaseProvider) {
    return new LocalCacheModule_ProvidePreJudgeCacheDaoFactory(databaseProvider);
  }

  public static PreJudgeCacheDao providePreJudgeCacheDao(CallCheckDatabase database) {
    return Preconditions.checkNotNullFromProvides(LocalCacheModule.INSTANCE.providePreJudgeCacheDao(database));
  }
}
