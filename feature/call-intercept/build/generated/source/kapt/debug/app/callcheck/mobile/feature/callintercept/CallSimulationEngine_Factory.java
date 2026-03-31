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
public final class CallSimulationEngine_Factory implements Factory<CallSimulationEngine> {
  private final Provider<GlobalSearchProviderRegistry> registryProvider;

  public CallSimulationEngine_Factory(Provider<GlobalSearchProviderRegistry> registryProvider) {
    this.registryProvider = registryProvider;
  }

  @Override
  public CallSimulationEngine get() {
    return newInstance(registryProvider.get());
  }

  public static CallSimulationEngine_Factory create(
      Provider<GlobalSearchProviderRegistry> registryProvider) {
    return new CallSimulationEngine_Factory(registryProvider);
  }

  public static CallSimulationEngine newInstance(GlobalSearchProviderRegistry registry) {
    return new CallSimulationEngine(registry);
  }
}
