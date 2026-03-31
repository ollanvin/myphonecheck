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
public final class AutoPolicyAdjuster_Factory implements Factory<AutoPolicyAdjuster> {
  private final Provider<ProductionFeedbackCollector> feedbackCollectorProvider;

  private final Provider<GlobalSearchProviderRegistry> registryProvider;

  public AutoPolicyAdjuster_Factory(Provider<ProductionFeedbackCollector> feedbackCollectorProvider,
      Provider<GlobalSearchProviderRegistry> registryProvider) {
    this.feedbackCollectorProvider = feedbackCollectorProvider;
    this.registryProvider = registryProvider;
  }

  @Override
  public AutoPolicyAdjuster get() {
    return newInstance(feedbackCollectorProvider.get(), registryProvider.get());
  }

  public static AutoPolicyAdjuster_Factory create(
      Provider<ProductionFeedbackCollector> feedbackCollectorProvider,
      Provider<GlobalSearchProviderRegistry> registryProvider) {
    return new AutoPolicyAdjuster_Factory(feedbackCollectorProvider, registryProvider);
  }

  public static AutoPolicyAdjuster newInstance(ProductionFeedbackCollector feedbackCollector,
      GlobalSearchProviderRegistry registry) {
    return new AutoPolicyAdjuster(feedbackCollector, registry);
  }
}
