package app.callcheck.mobile.feature.callintercept;

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
public final class InterceptBenchmarkRunner_Factory implements Factory<InterceptBenchmarkRunner> {
  private final Provider<InterceptPerformanceTracker> performanceTrackerProvider;

  private final Provider<ResourceMonitor> resourceMonitorProvider;

  private final Provider<CountryCaseMatrix> countryCaseMatrixProvider;

  private final Provider<InterceptPriorityRouter> routerProvider;

  private final Provider<CountryInterceptPolicyProvider> policyProvider;

  public InterceptBenchmarkRunner_Factory(
      Provider<InterceptPerformanceTracker> performanceTrackerProvider,
      Provider<ResourceMonitor> resourceMonitorProvider,
      Provider<CountryCaseMatrix> countryCaseMatrixProvider,
      Provider<InterceptPriorityRouter> routerProvider,
      Provider<CountryInterceptPolicyProvider> policyProvider) {
    this.performanceTrackerProvider = performanceTrackerProvider;
    this.resourceMonitorProvider = resourceMonitorProvider;
    this.countryCaseMatrixProvider = countryCaseMatrixProvider;
    this.routerProvider = routerProvider;
    this.policyProvider = policyProvider;
  }

  @Override
  public InterceptBenchmarkRunner get() {
    return newInstance(performanceTrackerProvider.get(), resourceMonitorProvider.get(), countryCaseMatrixProvider.get(), routerProvider.get(), policyProvider.get());
  }

  public static InterceptBenchmarkRunner_Factory create(
      Provider<InterceptPerformanceTracker> performanceTrackerProvider,
      Provider<ResourceMonitor> resourceMonitorProvider,
      Provider<CountryCaseMatrix> countryCaseMatrixProvider,
      Provider<InterceptPriorityRouter> routerProvider,
      Provider<CountryInterceptPolicyProvider> policyProvider) {
    return new InterceptBenchmarkRunner_Factory(performanceTrackerProvider, resourceMonitorProvider, countryCaseMatrixProvider, routerProvider, policyProvider);
  }

  public static InterceptBenchmarkRunner newInstance(InterceptPerformanceTracker performanceTracker,
      ResourceMonitor resourceMonitor, CountryCaseMatrix countryCaseMatrix,
      InterceptPriorityRouter router, CountryInterceptPolicyProvider policyProvider) {
    return new InterceptBenchmarkRunner(performanceTracker, resourceMonitor, countryCaseMatrix, router, policyProvider);
  }
}
