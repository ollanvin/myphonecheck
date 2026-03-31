package app.callcheck.mobile.feature.callintercept;

import app.callcheck.mobile.data.search.repository.SearchEnrichmentRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class SearchEvidenceProviderImpl_Factory implements Factory<SearchEvidenceProviderImpl> {
  private final Provider<SearchEnrichmentRepository> searchEnrichmentRepositoryProvider;

  public SearchEvidenceProviderImpl_Factory(
      Provider<SearchEnrichmentRepository> searchEnrichmentRepositoryProvider) {
    this.searchEnrichmentRepositoryProvider = searchEnrichmentRepositoryProvider;
  }

  @Override
  public SearchEvidenceProviderImpl get() {
    return newInstance(searchEnrichmentRepositoryProvider.get());
  }

  public static SearchEvidenceProviderImpl_Factory create(
      Provider<SearchEnrichmentRepository> searchEnrichmentRepositoryProvider) {
    return new SearchEvidenceProviderImpl_Factory(searchEnrichmentRepositoryProvider);
  }

  public static SearchEvidenceProviderImpl newInstance(
      SearchEnrichmentRepository searchEnrichmentRepository) {
    return new SearchEvidenceProviderImpl(searchEnrichmentRepository);
  }
}
