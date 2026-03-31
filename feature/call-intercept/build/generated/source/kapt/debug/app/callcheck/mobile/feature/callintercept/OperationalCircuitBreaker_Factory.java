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
public final class OperationalCircuitBreaker_Factory implements Factory<OperationalCircuitBreaker> {
  private final Provider<LaunchReadinessLock> launchLockProvider;

  public OperationalCircuitBreaker_Factory(Provider<LaunchReadinessLock> launchLockProvider) {
    this.launchLockProvider = launchLockProvider;
  }

  @Override
  public OperationalCircuitBreaker get() {
    return newInstance(launchLockProvider.get());
  }

  public static OperationalCircuitBreaker_Factory create(
      Provider<LaunchReadinessLock> launchLockProvider) {
    return new OperationalCircuitBreaker_Factory(launchLockProvider);
  }

  public static OperationalCircuitBreaker newInstance(LaunchReadinessLock launchLock) {
    return new OperationalCircuitBreaker(launchLock);
  }
}
