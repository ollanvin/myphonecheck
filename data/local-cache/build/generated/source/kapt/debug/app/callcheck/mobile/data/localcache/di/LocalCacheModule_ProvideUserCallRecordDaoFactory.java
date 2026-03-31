package app.callcheck.mobile.data.localcache.di;

import app.callcheck.mobile.data.localcache.dao.UserCallRecordDao;
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
public final class LocalCacheModule_ProvideUserCallRecordDaoFactory implements Factory<UserCallRecordDao> {
  private final Provider<CallCheckDatabase> databaseProvider;

  public LocalCacheModule_ProvideUserCallRecordDaoFactory(
      Provider<CallCheckDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public UserCallRecordDao get() {
    return provideUserCallRecordDao(databaseProvider.get());
  }

  public static LocalCacheModule_ProvideUserCallRecordDaoFactory create(
      Provider<CallCheckDatabase> databaseProvider) {
    return new LocalCacheModule_ProvideUserCallRecordDaoFactory(databaseProvider);
  }

  public static UserCallRecordDao provideUserCallRecordDao(CallCheckDatabase database) {
    return Preconditions.checkNotNullFromProvides(LocalCacheModule.INSTANCE.provideUserCallRecordDao(database));
  }
}
