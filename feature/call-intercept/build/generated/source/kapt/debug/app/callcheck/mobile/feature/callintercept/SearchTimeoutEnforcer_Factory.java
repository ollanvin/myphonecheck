package app.callcheck.mobile.feature.callintercept;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class SearchTimeoutEnforcer_Factory implements Factory<SearchTimeoutEnforcer> {
  @Override
  public SearchTimeoutEnforcer get() {
    return newInstance();
  }

  public static SearchTimeoutEnforcer_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static SearchTimeoutEnforcer newInstance() {
    return new SearchTimeoutEnforcer();
  }

  private static final class InstanceHolder {
    private static final SearchTimeoutEnforcer_Factory INSTANCE = new SearchTimeoutEnforcer_Factory();
  }
}
