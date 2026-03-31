package app.callcheck.mobile.feature.callintercept;

import android.content.Context;
import app.callcheck.mobile.data.localcache.repository.PreJudgeCacheRepository;
import app.callcheck.mobile.feature.decisionengine.DecisionEngine;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class CallInterceptRepositoryImpl_Factory implements Factory<CallInterceptRepositoryImpl> {
  private final Provider<Context> contextProvider;

  private final Provider<DeviceEvidenceProvider> deviceEvidenceProvider;

  private final Provider<SearchEvidenceProvider> searchEvidenceProvider;

  private final Provider<LocalLearningProvider> localLearningProvider;

  private final Provider<PreJudgeCacheRepository> preJudgeCacheRepositoryProvider;

  private final Provider<DecisionEngine> decisionEngineProvider;

  private final Provider<InterceptPriorityRouter> priorityRouterProvider;

  private final Provider<CountryInterceptPolicyProvider> countryPolicyProvider;

  private final Provider<InterceptPerformanceTracker> performanceTrackerProvider;

  public CallInterceptRepositoryImpl_Factory(Provider<Context> contextProvider,
      Provider<DeviceEvidenceProvider> deviceEvidenceProvider,
      Provider<SearchEvidenceProvider> searchEvidenceProvider,
      Provider<LocalLearningProvider> localLearningProvider,
      Provider<PreJudgeCacheRepository> preJudgeCacheRepositoryProvider,
      Provider<DecisionEngine> decisionEngineProvider,
      Provider<InterceptPriorityRouter> priorityRouterProvider,
      Provider<CountryInterceptPolicyProvider> countryPolicyProvider,
      Provider<InterceptPerformanceTracker> performanceTrackerProvider) {
    this.contextProvider = contextProvider;
    this.deviceEvidenceProvider = deviceEvidenceProvider;
    this.searchEvidenceProvider = searchEvidenceProvider;
    this.localLearningProvider = localLearningProvider;
    this.preJudgeCacheRepositoryProvider = preJudgeCacheRepositoryProvider;
    this.decisionEngineProvider = decisionEngineProvider;
    this.priorityRouterProvider = priorityRouterProvider;
    this.countryPolicyProvider = countryPolicyProvider;
    this.performanceTrackerProvider = performanceTrackerProvider;
  }

  @Override
  public CallInterceptRepositoryImpl get() {
    return newInstance(contextProvider.get(), deviceEvidenceProvider.get(), searchEvidenceProvider.get(), localLearningProvider.get(), preJudgeCacheRepositoryProvider.get(), decisionEngineProvider.get(), priorityRouterProvider.get(), countryPolicyProvider.get(), performanceTrackerProvider.get());
  }

  public static CallInterceptRepositoryImpl_Factory create(Provider<Context> contextProvider,
      Provider<DeviceEvidenceProvider> deviceEvidenceProvider,
      Provider<SearchEvidenceProvider> searchEvidenceProvider,
      Provider<LocalLearningProvider> localLearningProvider,
      Provider<PreJudgeCacheRepository> preJudgeCacheRepositoryProvider,
      Provider<DecisionEngine> decisionEngineProvider,
      Provider<InterceptPriorityRouter> priorityRouterProvider,
      Provider<CountryInterceptPolicyProvider> countryPolicyProvider,
      Provider<InterceptPerformanceTracker> performanceTrackerProvider) {
    return new CallInterceptRepositoryImpl_Factory(contextProvider, deviceEvidenceProvider, searchEvidenceProvider, localLearningProvider, preJudgeCacheRepositoryProvider, decisionEngineProvider, priorityRouterProvider, countryPolicyProvider, performanceTrackerProvider);
  }

  public static CallInterceptRepositoryImpl newInstance(Context context,
      DeviceEvidenceProvider deviceEvidenceProvider, SearchEvidenceProvider searchEvidenceProvider,
      LocalLearningProvider localLearningProvider, PreJudgeCacheRepository preJudgeCacheRepository,
      DecisionEngine decisionEngine, InterceptPriorityRouter priorityRouter,
      CountryInterceptPolicyProvider countryPolicyProvider,
      InterceptPerformanceTracker performanceTracker) {
    return new CallInterceptRepositoryImpl(context, deviceEvidenceProvider, searchEvidenceProvider, localLearningProvider, preJudgeCacheRepository, decisionEngine, priorityRouter, countryPolicyProvider, performanceTracker);
  }
}
