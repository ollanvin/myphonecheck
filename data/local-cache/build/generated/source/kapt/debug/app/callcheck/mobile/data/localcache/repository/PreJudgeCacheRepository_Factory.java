package app.callcheck.mobile.data.localcache.repository;

import app.callcheck.mobile.data.localcache.dao.PreJudgeCacheDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class PreJudgeCacheRepository_Factory implements Factory<PreJudgeCacheRepository> {
  private final Provider<PreJudgeCacheDao> daoProvider;

  public PreJudgeCacheRepository_Factory(Provider<PreJudgeCacheDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public PreJudgeCacheRepository get() {
    return newInstance(daoProvider.get());
  }

  public static PreJudgeCacheRepository_Factory create(Provider<PreJudgeCacheDao> daoProvider) {
    return new PreJudgeCacheRepository_Factory(daoProvider);
  }

  public static PreJudgeCacheRepository newInstance(PreJudgeCacheDao dao) {
    return new PreJudgeCacheRepository(dao);
  }
}
