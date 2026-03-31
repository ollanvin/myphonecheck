package app.callcheck.mobile.feature.countryconfig;

import android.content.Context;
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
public final class LanguageContextProviderImpl_Factory implements Factory<LanguageContextProviderImpl> {
  private final Provider<Context> contextProvider;

  public LanguageContextProviderImpl_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public LanguageContextProviderImpl get() {
    return newInstance(contextProvider.get());
  }

  public static LanguageContextProviderImpl_Factory create(Provider<Context> contextProvider) {
    return new LanguageContextProviderImpl_Factory(contextProvider);
  }

  public static LanguageContextProviderImpl newInstance(Context context) {
    return new LanguageContextProviderImpl(context);
  }
}
