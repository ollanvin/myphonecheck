package app.callcheck.mobile.data.search.di;

import app.callcheck.mobile.data.search.SearchProviderRegistry;
import app.callcheck.mobile.data.search.SearchResultAnalyzer;
import app.callcheck.mobile.data.search.repository.SearchEnrichmentRepository;
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
public final class SearchEnrichmentModule_ProvideSearchEnrichmentRepositoryFactory implements Factory<SearchEnrichmentRepository> {
  private final Provider<SearchProviderRegistry> providerRegistryProvider;

  private final Provider<SearchResultAnalyzer> analyzerProvider;

  public SearchEnrichmentModule_ProvideSearchEnrichmentRepositoryFactory(
      Provider<SearchProviderRegistry> providerRegistryProvider,
      Provider<SearchResultAnalyzer> analyzerProvider) {
    this.providerRegistryProvider = providerRegistryProvider;
    this.analyzerProvider = analyzerProvider;
  }

  @Override
  public SearchEnrichmentRepository get() {
    return provideSearchEnrichmentRepository(providerRegistryProvider.get(), analyzerProvider.get());
  }

  public static SearchEnrichmentModule_ProvideSearchEnrichmentRepositoryFactory create(
      Provider<SearchProviderRegistry> providerRegistryProvider,
      Provider<SearchResultAnalyzer> analyzerProvider) {
    return new SearchEnrichmentModule_ProvideSearchEnrichmentRepositoryFactory(providerRegistryProvider, analyzerProvider);
  }

  public static SearchEnrichmentRepository provideSearchEnrichmentRepository(
      SearchProviderRegistry providerRegistry, SearchResultAnalyzer analyzer) {
    return Preconditions.checkNotNullFromProvides(SearchEnrichmentModule.INSTANCE.provideSearchEnrichmentRepository(providerRegistry, analyzer));
  }
}
