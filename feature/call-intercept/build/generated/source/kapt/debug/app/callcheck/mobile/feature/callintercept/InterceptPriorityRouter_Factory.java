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
public final class InterceptPriorityRouter_Factory implements Factory<InterceptPriorityRouter> {
  @Override
  public InterceptPriorityRouter get() {
    return newInstance();
  }

  public static InterceptPriorityRouter_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static InterceptPriorityRouter newInstance() {
    return new InterceptPriorityRouter();
  }

  private static final class InstanceHolder {
    private static final InterceptPriorityRouter_Factory INSTANCE = new InterceptPriorityRouter_Factory();
  }
}
