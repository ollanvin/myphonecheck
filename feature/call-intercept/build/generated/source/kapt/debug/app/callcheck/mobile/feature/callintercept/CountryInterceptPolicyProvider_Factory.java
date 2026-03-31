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
public final class CountryInterceptPolicyProvider_Factory implements Factory<CountryInterceptPolicyProvider> {
  @Override
  public CountryInterceptPolicyProvider get() {
    return newInstance();
  }

  public static CountryInterceptPolicyProvider_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static CountryInterceptPolicyProvider newInstance() {
    return new CountryInterceptPolicyProvider();
  }

  private static final class InstanceHolder {
    private static final CountryInterceptPolicyProvider_Factory INSTANCE = new CountryInterceptPolicyProvider_Factory();
  }
}
