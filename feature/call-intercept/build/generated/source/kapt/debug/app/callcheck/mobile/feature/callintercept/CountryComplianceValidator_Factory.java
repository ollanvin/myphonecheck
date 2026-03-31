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
public final class CountryComplianceValidator_Factory implements Factory<CountryComplianceValidator> {
  private final Provider<GlobalSearchProviderRegistry> registryProvider;

  public CountryComplianceValidator_Factory(
      Provider<GlobalSearchProviderRegistry> registryProvider) {
    this.registryProvider = registryProvider;
  }

  @Override
  public CountryComplianceValidator get() {
    return newInstance(registryProvider.get());
  }

  public static CountryComplianceValidator_Factory create(
      Provider<GlobalSearchProviderRegistry> registryProvider) {
    return new CountryComplianceValidator_Factory(registryProvider);
  }

  public static CountryComplianceValidator newInstance(GlobalSearchProviderRegistry registry) {
    return new CountryComplianceValidator(registry);
  }
}
