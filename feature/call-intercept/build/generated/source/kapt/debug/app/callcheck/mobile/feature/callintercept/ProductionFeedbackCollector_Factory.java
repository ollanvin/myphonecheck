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
public final class ProductionFeedbackCollector_Factory implements Factory<ProductionFeedbackCollector> {
  @Override
  public ProductionFeedbackCollector get() {
    return newInstance();
  }

  public static ProductionFeedbackCollector_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ProductionFeedbackCollector newInstance() {
    return new ProductionFeedbackCollector();
  }

  private static final class InstanceHolder {
    private static final ProductionFeedbackCollector_Factory INSTANCE = new ProductionFeedbackCollector_Factory();
  }
}
