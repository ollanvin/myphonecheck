package app.callcheck.mobile.feature.callintercept;

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
public final class DecisionNotificationManager_Factory implements Factory<DecisionNotificationManager> {
  @Override
  public DecisionNotificationManager get() {
    return newInstance();
  }

  public static DecisionNotificationManager_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static DecisionNotificationManager newInstance() {
    return new DecisionNotificationManager();
  }

  private static final class InstanceHolder {
    private static final DecisionNotificationManager_Factory INSTANCE = new DecisionNotificationManager_Factory();
  }
}
