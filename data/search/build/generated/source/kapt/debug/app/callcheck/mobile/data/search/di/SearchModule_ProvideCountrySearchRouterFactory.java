package app.callcheck.mobile.data.search.di;

import app.callcheck.mobile.data.search.CountrySearchRouter;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import okhttp3.OkHttpClient;

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
public final class SearchModule_ProvideCountrySearchRouterFactory implements Factory<CountrySearchRouter> {
  private final Provider<OkHttpClient> httpClientProvider;

  public SearchModule_ProvideCountrySearchRouterFactory(Provider<OkHttpClient> httpClientProvider) {
    this.httpClientProvider = httpClientProvider;
  }

  @Override
  public CountrySearchRouter get() {
    return provideCountrySearchRouter(httpClientProvider.get());
  }

  public static SearchModule_ProvideCountrySearchRouterFactory create(
      Provider<OkHttpClient> httpClientProvider) {
    return new SearchModule_ProvideCountrySearchRouterFactory(httpClientProvider);
  }

  public static CountrySearchRouter provideCountrySearchRouter(OkHttpClient httpClient) {
    return Preconditions.checkNotNullFromProvides(SearchModule.INSTANCE.provideCountrySearchRouter(httpClient));
  }
}
