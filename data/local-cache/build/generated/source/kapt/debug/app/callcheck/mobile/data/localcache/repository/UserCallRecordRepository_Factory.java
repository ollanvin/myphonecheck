package app.callcheck.mobile.data.localcache.repository;

import app.callcheck.mobile.data.localcache.dao.UserCallRecordDao;
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
public final class UserCallRecordRepository_Factory implements Factory<UserCallRecordRepository> {
  private final Provider<UserCallRecordDao> daoProvider;

  public UserCallRecordRepository_Factory(Provider<UserCallRecordDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public UserCallRecordRepository get() {
    return newInstance(daoProvider.get());
  }

  public static UserCallRecordRepository_Factory create(Provider<UserCallRecordDao> daoProvider) {
    return new UserCallRecordRepository_Factory(daoProvider);
  }

  public static UserCallRecordRepository newInstance(UserCallRecordDao dao) {
    return new UserCallRecordRepository(dao);
  }
}
