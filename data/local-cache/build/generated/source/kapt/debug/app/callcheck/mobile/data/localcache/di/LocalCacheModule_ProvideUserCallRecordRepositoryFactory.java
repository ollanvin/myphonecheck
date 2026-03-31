package app.callcheck.mobile.data.localcache.di;

import app.callcheck.mobile.data.localcache.dao.UserCallRecordDao;
import app.callcheck.mobile.data.localcache.repository.UserCallRecordRepository;
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
public final class LocalCacheModule_ProvideUserCallRecordRepositoryFactory implements Factory<UserCallRecordRepository> {
  private final Provider<UserCallRecordDao> daoProvider;

  public LocalCacheModule_ProvideUserCallRecordRepositoryFactory(
      Provider<UserCallRecordDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public UserCallRecordRepository get() {
    return provideUserCallRecordRepository(daoProvider.get());
  }

  public static LocalCacheModule_ProvideUserCallRecordRepositoryFactory create(
      Provider<UserCallRecordDao> daoProvider) {
    return new LocalCacheModule_ProvideUserCallRecordRepositoryFactory(daoProvider);
  }

  public static UserCallRecordRepository provideUserCallRecordRepository(UserCallRecordDao dao) {
    return Preconditions.checkNotNullFromProvides(LocalCacheModule.INSTANCE.provideUserCallRecordRepository(dao));
  }
}
