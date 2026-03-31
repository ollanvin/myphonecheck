package app.callcheck.mobile.feature.callintercept;

import app.callcheck.mobile.data.localcache.repository.PreJudgeCacheRepository;
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
public final class InterceptOutcomeLearner_Factory implements Factory<InterceptOutcomeLearner> {
  private final Provider<PreJudgeCacheRepository> preJudgeCacheRepositoryProvider;

  public InterceptOutcomeLearner_Factory(
      Provider<PreJudgeCacheRepository> preJudgeCacheRepositoryProvider) {
    this.preJudgeCacheRepositoryProvider = preJudgeCacheRepositoryProvider;
  }

  @Override
  public InterceptOutcomeLearner get() {
    return newInstance(preJudgeCacheRepositoryProvider.get());
  }

  public static InterceptOutcomeLearner_Factory create(
      Provider<PreJudgeCacheRepository> preJudgeCacheRepositoryProvider) {
    return new InterceptOutcomeLearner_Factory(preJudgeCacheRepositoryProvider);
  }

  public static InterceptOutcomeLearner newInstance(
      PreJudgeCacheRepository preJudgeCacheRepository) {
    return new InterceptOutcomeLearner(preJudgeCacheRepository);
  }
}
