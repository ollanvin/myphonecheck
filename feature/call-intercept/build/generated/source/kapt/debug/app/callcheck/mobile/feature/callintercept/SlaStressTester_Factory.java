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
public final class SlaStressTester_Factory implements Factory<SlaStressTester> {
  private final Provider<CallSimulationEngine> simulationEngineProvider;

  private final Provider<GlobalSearchProviderRegistry> registryProvider;

  public SlaStressTester_Factory(Provider<CallSimulationEngine> simulationEngineProvider,
      Provider<GlobalSearchProviderRegistry> registryProvider) {
    this.simulationEngineProvider = simulationEngineProvider;
    this.registryProvider = registryProvider;
  }

  @Override
  public SlaStressTester get() {
    return newInstance(simulationEngineProvider.get(), registryProvider.get());
  }

  public static SlaStressTester_Factory create(
      Provider<CallSimulationEngine> simulationEngineProvider,
      Provider<GlobalSearchProviderRegistry> registryProvider) {
    return new SlaStressTester_Factory(simulationEngineProvider, registryProvider);
  }

  public static SlaStressTester newInstance(CallSimulationEngine simulationEngine,
      GlobalSearchProviderRegistry registry) {
    return new SlaStressTester(simulationEngine, registry);
  }
}
