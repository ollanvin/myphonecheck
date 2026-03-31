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
public final class LaunchSimulationRunner_Factory implements Factory<LaunchSimulationRunner> {
  private final Provider<GlobalSearchProviderRegistry> registryProvider;

  private final Provider<OperationalCircuitBreaker> circuitBreakerProvider;

  private final Provider<LaunchReadinessLock> launchLockProvider;

  private final Provider<GlobalLaunchDashboard> dashboardProvider;

  private final Provider<DeviceMetricsCollector> metricsCollectorProvider;

  private final Provider<CountryRealityTester> realityTesterProvider;

  private final Provider<CircuitBreakerRehearsalRunner> rehearsalRunnerProvider;

  public LaunchSimulationRunner_Factory(Provider<GlobalSearchProviderRegistry> registryProvider,
      Provider<OperationalCircuitBreaker> circuitBreakerProvider,
      Provider<LaunchReadinessLock> launchLockProvider,
      Provider<GlobalLaunchDashboard> dashboardProvider,
      Provider<DeviceMetricsCollector> metricsCollectorProvider,
      Provider<CountryRealityTester> realityTesterProvider,
      Provider<CircuitBreakerRehearsalRunner> rehearsalRunnerProvider) {
    this.registryProvider = registryProvider;
    this.circuitBreakerProvider = circuitBreakerProvider;
    this.launchLockProvider = launchLockProvider;
    this.dashboardProvider = dashboardProvider;
    this.metricsCollectorProvider = metricsCollectorProvider;
    this.realityTesterProvider = realityTesterProvider;
    this.rehearsalRunnerProvider = rehearsalRunnerProvider;
  }

  @Override
  public LaunchSimulationRunner get() {
    return newInstance(registryProvider.get(), circuitBreakerProvider.get(), launchLockProvider.get(), dashboardProvider.get(), metricsCollectorProvider.get(), realityTesterProvider.get(), rehearsalRunnerProvider.get());
  }

  public static LaunchSimulationRunner_Factory create(
      Provider<GlobalSearchProviderRegistry> registryProvider,
      Provider<OperationalCircuitBreaker> circuitBreakerProvider,
      Provider<LaunchReadinessLock> launchLockProvider,
      Provider<GlobalLaunchDashboard> dashboardProvider,
      Provider<DeviceMetricsCollector> metricsCollectorProvider,
      Provider<CountryRealityTester> realityTesterProvider,
      Provider<CircuitBreakerRehearsalRunner> rehearsalRunnerProvider) {
    return new LaunchSimulationRunner_Factory(registryProvider, circuitBreakerProvider, launchLockProvider, dashboardProvider, metricsCollectorProvider, realityTesterProvider, rehearsalRunnerProvider);
  }

  public static LaunchSimulationRunner newInstance(GlobalSearchProviderRegistry registry,
      OperationalCircuitBreaker circuitBreaker, LaunchReadinessLock launchLock,
      GlobalLaunchDashboard dashboard, DeviceMetricsCollector metricsCollector,
      CountryRealityTester realityTester, CircuitBreakerRehearsalRunner rehearsalRunner) {
    return new LaunchSimulationRunner(registry, circuitBreaker, launchLock, dashboard, metricsCollector, realityTester, rehearsalRunner);
  }
}
