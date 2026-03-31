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
public final class CountryCaseMatrix_Factory implements Factory<CountryCaseMatrix> {
  private final Provider<CountryInterceptPolicyProvider> policyProvider;

  private final Provider<InterceptPriorityRouter> routerProvider;

  public CountryCaseMatrix_Factory(Provider<CountryInterceptPolicyProvider> policyProvider,
      Provider<InterceptPriorityRouter> routerProvider) {
    this.policyProvider = policyProvider;
    this.routerProvider = routerProvider;
  }

  @Override
  public CountryCaseMatrix get() {
    return newInstance(policyProvider.get(), routerProvider.get());
  }

  public static CountryCaseMatrix_Factory create(
      Provider<CountryInterceptPolicyProvider> policyProvider,
      Provider<InterceptPriorityRouter> routerProvider) {
    return new CountryCaseMatrix_Factory(policyProvider, routerProvider);
  }

  public static CountryCaseMatrix newInstance(CountryInterceptPolicyProvider policyProvider,
      InterceptPriorityRouter router) {
    return new CountryCaseMatrix(policyProvider, router);
  }
}
