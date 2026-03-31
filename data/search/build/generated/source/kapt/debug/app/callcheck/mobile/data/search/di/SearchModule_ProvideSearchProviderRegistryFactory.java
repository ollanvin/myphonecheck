package app.callcheck.mobile.data.search.di;

import app.callcheck.mobile.data.search.CountrySearchRouter;
import app.callcheck.mobile.data.search.SearchProviderRegistry;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class SearchModule_ProvideSearchProviderRegistryFactory implements Factory<SearchProviderRegistry> {
  private final Provider<CountrySearchRouter> routerProvider;

  public SearchModule_ProvideSearchProviderRegistryFactory(
      Provider<CountrySearchRouter> routerProvider) {
    this.routerProvider = routerProvider;
  }

  @Override
  public SearchProviderRegistry get() {
    return provideSearchProviderRegistry(routerProvider.get());
  }

  public static SearchModule_ProvideSearchProviderRegistryFactory create(
      Provider<CountrySearchRouter> routerProvider) {
    return new SearchModule_ProvideSearchProviderRegistryFactory(routerProvider);
  }

  public static SearchProviderRegistry provideSearchProviderRegistry(CountrySearchRouter router) {
    return Preconditions.checkNotNullFromProvides(SearchModule.INSTANCE.provideSearchProviderRegistry(router));
  }
}
