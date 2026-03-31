package app.callcheck.mobile.feature.countryconfig.di;

import app.callcheck.mobile.feature.countryconfig.CountryConfigProvider;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class CountryConfigModule_ProvideCountryConfigProviderFactory implements Factory<CountryConfigProvider> {
  @Override
  public CountryConfigProvider get() {
    return provideCountryConfigProvider();
  }

  public static CountryConfigModule_ProvideCountryConfigProviderFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static CountryConfigProvider provideCountryConfigProvider() {
    return Preconditions.checkNotNullFromProvides(CountryConfigModule.INSTANCE.provideCountryConfigProvider());
  }

  private static final class InstanceHolder {
    private static final CountryConfigModule_ProvideCountryConfigProviderFactory INSTANCE = new CountryConfigModule_ProvideCountryConfigProviderFactory();
  }
}
