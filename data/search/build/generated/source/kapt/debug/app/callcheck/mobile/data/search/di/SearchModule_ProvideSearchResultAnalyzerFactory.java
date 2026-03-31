package app.callcheck.mobile.data.search.di;

import app.callcheck.mobile.data.search.SearchResultAnalyzer;
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
public final class SearchModule_ProvideSearchResultAnalyzerFactory implements Factory<SearchResultAnalyzer> {
  @Override
  public SearchResultAnalyzer get() {
    return provideSearchResultAnalyzer();
  }

  public static SearchModule_ProvideSearchResultAnalyzerFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static SearchResultAnalyzer provideSearchResultAnalyzer() {
    return Preconditions.checkNotNullFromProvides(SearchModule.INSTANCE.provideSearchResultAnalyzer());
  }

  private static final class InstanceHolder {
    private static final SearchModule_ProvideSearchResultAnalyzerFactory INSTANCE = new SearchModule_ProvideSearchResultAnalyzerFactory();
  }
}
