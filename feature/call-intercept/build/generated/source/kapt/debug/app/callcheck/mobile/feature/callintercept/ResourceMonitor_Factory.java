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
public final class ResourceMonitor_Factory implements Factory<ResourceMonitor> {
  @Override
  public ResourceMonitor get() {
    return newInstance();
  }

  public static ResourceMonitor_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ResourceMonitor newInstance() {
    return new ResourceMonitor();
  }

  private static final class InstanceHolder {
    private static final ResourceMonitor_Factory INSTANCE = new ResourceMonitor_Factory();
  }
}
