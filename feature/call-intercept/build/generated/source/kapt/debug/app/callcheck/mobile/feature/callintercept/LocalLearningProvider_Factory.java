package app.callcheck.mobile.feature.callintercept;

import app.callcheck.mobile.data.localcache.repository.UserCallRecordRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class LocalLearningProvider_Factory implements Factory<LocalLearningProvider> {
  private final Provider<UserCallRecordRepository> userCallRecordRepositoryProvider;

  public LocalLearningProvider_Factory(
      Provider<UserCallRecordRepository> userCallRecordRepositoryProvider) {
    this.userCallRecordRepositoryProvider = userCallRecordRepositoryProvider;
  }

  @Override
  public LocalLearningProvider get() {
    return newInstance(userCallRecordRepositoryProvider.get());
  }

  public static LocalLearningProvider_Factory create(
      Provider<UserCallRecordRepository> userCallRecordRepositoryProvider) {
    return new LocalLearningProvider_Factory(userCallRecordRepositoryProvider);
  }

  public static LocalLearningProvider newInstance(
      UserCallRecordRepository userCallRecordRepository) {
    return new LocalLearningProvider(userCallRecordRepository);
  }
}
