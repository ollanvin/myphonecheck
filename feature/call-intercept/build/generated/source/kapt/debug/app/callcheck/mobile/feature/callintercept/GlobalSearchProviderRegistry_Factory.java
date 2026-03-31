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
public final class GlobalSearchProviderRegistry_Factory implements Factory<GlobalSearchProviderRegistry> {
  @Override
  public GlobalSearchProviderRegistry get() {
    return newInstance();
  }

  public static GlobalSearchProviderRegistry_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static GlobalSearchProviderRegistry newInstance() {
    return new GlobalSearchProviderRegistry();
  }

  private static final class InstanceHolder {
    private static final GlobalSearchProviderRegistry_Factory INSTANCE = new GlobalSearchProviderRegistry_Factory();
  }
}
