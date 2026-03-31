package app.callcheck.mobile.data.localcache.di;

import app.callcheck.mobile.data.localcache.dao.PreJudgeCacheDao;
import app.callcheck.mobile.data.localcache.repository.PreJudgeCacheRepository;
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
public final class LocalCacheModule_ProvidePreJudgeCacheRepositoryFactory implements Factory<PreJudgeCacheRepository> {
  private final Provider<PreJudgeCacheDao> daoProvider;

  public LocalCacheModule_ProvidePreJudgeCacheRepositoryFactory(
      Provider<PreJudgeCacheDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public PreJudgeCacheRepository get() {
    return providePreJudgeCacheRepository(daoProvider.get());
  }

  public static LocalCacheModule_ProvidePreJudgeCacheRepositoryFactory create(
      Provider<PreJudgeCacheDao> daoProvider) {
    return new LocalCacheModule_ProvidePreJudgeCacheRepositoryFactory(daoProvider);
  }

  public static PreJudgeCacheRepository providePreJudgeCacheRepository(PreJudgeCacheDao dao) {
    return Preconditions.checkNotNullFromProvides(LocalCacheModule.INSTANCE.providePreJudgeCacheRepository(dao));
  }
}
