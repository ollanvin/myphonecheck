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
public final class CallerIdOverlayManager_Factory implements Factory<CallerIdOverlayManager> {
  @Override
  public CallerIdOverlayManager get() {
    return newInstance();
  }

  public static CallerIdOverlayManager_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static CallerIdOverlayManager newInstance() {
    return new CallerIdOverlayManager();
  }

  private static final class InstanceHolder {
    private static final CallerIdOverlayManager_Factory INSTANCE = new CallerIdOverlayManager_Factory();
  }
}
