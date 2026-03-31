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
public final class GlobalLaunchDashboard_Factory implements Factory<GlobalLaunchDashboard> {
  private final Provider<GlobalSearchProviderRegistry> registryProvider;

  private final Provider<CountryComplianceValidator> complianceValidatorProvider;

  private final Provider<LaunchReadinessLock> launchLockProvider;

  private final Provider<OperationalCircuitBreaker> circuitBreakerProvider;

  private final Provider<ProductionFeedbackCollector> feedbackCollectorProvider;

  public GlobalLaunchDashboard_Factory(Provider<GlobalSearchProviderRegistry> registryProvider,
      Provider<CountryComplianceValidator> complianceValidatorProvider,
      Provider<LaunchReadinessLock> launchLockProvider,
      Provider<OperationalCircuitBreaker> circuitBreakerProvider,
      Provider<ProductionFeedbackCollector> feedbackCollectorProvider) {
    this.registryProvider = registryProvider;
    this.complianceValidatorProvider = complianceValidatorProvider;
    this.launchLockProvider = launchLockProvider;
    this.circuitBreakerProvider = circuitBreakerProvider;
    this.feedbackCollectorProvider = feedbackCollectorProvider;
  }

  @Override
  public GlobalLaunchDashboard get() {
    return newInstance(registryProvider.get(), complianceValidatorProvider.get(), launchLockProvider.get(), circuitBreakerProvider.get(), feedbackCollectorProvider.get());
  }

  public static GlobalLaunchDashboard_Factory create(
      Provider<GlobalSearchProviderRegistry> registryProvider,
      Provider<CountryComplianceValidator> complianceValidatorProvider,
      Provider<LaunchReadinessLock> launchLockProvider,
      Provider<OperationalCircuitBreaker> circuitBreakerProvider,
      Provider<ProductionFeedbackCollector> feedbackCollectorProvider) {
    return new GlobalLaunchDashboard_Factory(registryProvider, complianceValidatorProvider, launchLockProvider, circuitBreakerProvider, feedbackCollectorProvider);
  }

  public static GlobalLaunchDashboard newInstance(GlobalSearchProviderRegistry registry,
      CountryComplianceValidator complianceValidator, LaunchReadinessLock launchLock,
      OperationalCircuitBreaker circuitBreaker, ProductionFeedbackCollector feedbackCollector) {
    return new GlobalLaunchDashboard(registry, complianceValidator, launchLock, circuitBreaker, feedbackCollector);
  }
}
