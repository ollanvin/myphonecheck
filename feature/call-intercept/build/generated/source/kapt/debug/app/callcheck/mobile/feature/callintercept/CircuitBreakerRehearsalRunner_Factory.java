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
public final class CircuitBreakerRehearsalRunner_Factory implements Factory<CircuitBreakerRehearsalRunner> {
  private final Provider<OperationalCircuitBreaker> circuitBreakerProvider;

  private final Provider<LaunchReadinessLock> launchLockProvider;

  public CircuitBreakerRehearsalRunner_Factory(
      Provider<OperationalCircuitBreaker> circuitBreakerProvider,
      Provider<LaunchReadinessLock> launchLockProvider) {
    this.circuitBreakerProvider = circuitBreakerProvider;
    this.launchLockProvider = launchLockProvider;
  }

  @Override
  public CircuitBreakerRehearsalRunner get() {
    return newInstance(circuitBreakerProvider.get(), launchLockProvider.get());
  }

  public static CircuitBreakerRehearsalRunner_Factory create(
      Provider<OperationalCircuitBreaker> circuitBreakerProvider,
      Provider<LaunchReadinessLock> launchLockProvider) {
    return new CircuitBreakerRehearsalRunner_Factory(circuitBreakerProvider, launchLockProvider);
  }

  public static CircuitBreakerRehearsalRunner newInstance(OperationalCircuitBreaker circuitBreaker,
      LaunchReadinessLock launchLock) {
    return new CircuitBreakerRehearsalRunner(circuitBreaker, launchLock);
  }
}
