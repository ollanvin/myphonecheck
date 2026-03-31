package app.callcheck.mobile.feature.countryconfig.di;

import android.content.Context;
import app.callcheck.mobile.feature.countryconfig.LanguageContextProvider;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class CountryConfigModule_ProvideLanguageContextProviderFactory implements Factory<LanguageContextProvider> {
  private final Provider<Context> contextProvider;

  public CountryConfigModule_ProvideLanguageContextProviderFactory(
      Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public LanguageContextProvider get() {
    return provideLanguageContextProvider(contextProvider.get());
  }

  public static CountryConfigModule_ProvideLanguageContextProviderFactory create(
      Provider<Context> contextProvider) {
    return new CountryConfigModule_ProvideLanguageContextProviderFactory(contextProvider);
  }

  public static LanguageContextProvider provideLanguageContextProvider(Context context) {
    return Preconditions.checkNotNullFromProvides(CountryConfigModule.INSTANCE.provideLanguageContextProvider(context));
  }
}
