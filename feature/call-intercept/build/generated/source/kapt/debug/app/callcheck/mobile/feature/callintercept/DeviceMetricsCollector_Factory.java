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
public final class DeviceMetricsCollector_Factory implements Factory<DeviceMetricsCollector> {
  private final Provider<OperationalCircuitBreaker> circuitBreakerProvider;

  public DeviceMetricsCollector_Factory(
      Provider<OperationalCircuitBreaker> circuitBreakerProvider) {
    this.circuitBreakerProvider = circuitBreakerProvider;
  }

  @Override
  public DeviceMetricsCollector get() {
    return newInstance(circuitBreakerProvider.get());
  }

  public static DeviceMetricsCollector_Factory create(
      Provider<OperationalCircuitBreaker> circuitBreakerProvider) {
    return new DeviceMetricsCollector_Factory(circuitBreakerProvider);
  }

  public static DeviceMetricsCollector newInstance(OperationalCircuitBreaker circuitBreaker) {
    return new DeviceMetricsCollector(circuitBreaker);
  }
}
