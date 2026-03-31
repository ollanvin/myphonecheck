package app.callcheck.mobile.feature.decisionengine;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class RiskBadgeMapper_Factory implements Factory<RiskBadgeMapper> {
  @Override
  public RiskBadgeMapper get() {
    return newInstance();
  }

  public static RiskBadgeMapper_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static RiskBadgeMapper newInstance() {
    return new RiskBadgeMapper();
  }

  private static final class InstanceHolder {
    private static final RiskBadgeMapper_Factory INSTANCE = new RiskBadgeMapper_Factory();
  }
}
