package app.callcheck.mobile.feature.callintercept;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class InterceptPerformanceTracker_Factory implements Factory<InterceptPerformanceTracker> {
  @Override
  public InterceptPerformanceTracker get() {
    return newInstance();
  }

  public static InterceptPerformanceTracker_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static InterceptPerformanceTracker newInstance() {
    return new InterceptPerformanceTracker();
  }

  private static final class InstanceHolder {
    private static final InterceptPerformanceTracker_Factory INSTANCE = new InterceptPerformanceTracker_Factory();
  }
}
