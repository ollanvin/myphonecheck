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
public final class LaunchReadinessLock_Factory implements Factory<LaunchReadinessLock> {
  private final Provider<GlobalSearchProviderRegistry> registryProvider;

  private final Provider<CountryComplianceValidator> complianceValidatorProvider;

  public LaunchReadinessLock_Factory(Provider<GlobalSearchProviderRegistry> registryProvider,
      Provider<CountryComplianceValidator> complianceValidatorProvider) {
    this.registryProvider = registryProvider;
    this.complianceValidatorProvider = complianceValidatorProvider;
  }

  @Override
  public LaunchReadinessLock get() {
    return newInstance(registryProvider.get(), complianceValidatorProvider.get());
  }

  public static LaunchReadinessLock_Factory create(
      Provider<GlobalSearchProviderRegistry> registryProvider,
      Provider<CountryComplianceValidator> complianceValidatorProvider) {
    return new LaunchReadinessLock_Factory(registryProvider, complianceValidatorProvider);
  }

  public static LaunchReadinessLock newInstance(GlobalSearchProviderRegistry registry,
      CountryComplianceValidator complianceValidator) {
    return new LaunchReadinessLock(registry, complianceValidator);
  }
}
