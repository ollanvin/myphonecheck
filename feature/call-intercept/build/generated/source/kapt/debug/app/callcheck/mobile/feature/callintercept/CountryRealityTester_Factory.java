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
public final class CountryRealityTester_Factory implements Factory<CountryRealityTester> {
  private final Provider<GlobalSearchProviderRegistry> registryProvider;

  private final Provider<OperationalCircuitBreaker> circuitBreakerProvider;

  private final Provider<LaunchReadinessLock> launchLockProvider;

  private final Provider<DeviceMetricsCollector> metricsCollectorProvider;

  public CountryRealityTester_Factory(Provider<GlobalSearchProviderRegistry> registryProvider,
      Provider<OperationalCircuitBreaker> circuitBreakerProvider,
      Provider<LaunchReadinessLock> launchLockProvider,
      Provider<DeviceMetricsCollector> metricsCollectorProvider) {
    this.registryProvider = registryProvider;
    this.circuitBreakerProvider = circuitBreakerProvider;
    this.launchLockProvider = launchLockProvider;
    this.metricsCollectorProvider = metricsCollectorProvider;
  }

  @Override
  public CountryRealityTester get() {
    return newInstance(registryProvider.get(), circuitBreakerProvider.get(), launchLockProvider.get(), metricsCollectorProvider.get());
  }

  public static CountryRealityTester_Factory create(
      Provider<GlobalSearchProviderRegistry> registryProvider,
      Provider<OperationalCircuitBreaker> circuitBreakerProvider,
      Provider<LaunchReadinessLock> launchLockProvider,
      Provider<DeviceMetricsCollector> metricsCollectorProvider) {
    return new CountryRealityTester_Factory(registryProvider, circuitBreakerProvider, launchLockProvider, metricsCollectorProvider);
  }

  public static CountryRealityTester newInstance(GlobalSearchProviderRegistry registry,
      OperationalCircuitBreaker circuitBreaker, LaunchReadinessLock launchLock,
      DeviceMetricsCollector metricsCollector) {
    return new CountryRealityTester(registry, circuitBreaker, launchLock, metricsCollector);
  }
}
