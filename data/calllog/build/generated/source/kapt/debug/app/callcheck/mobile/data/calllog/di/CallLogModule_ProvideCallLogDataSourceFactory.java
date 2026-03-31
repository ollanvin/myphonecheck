package app.callcheck.mobile.data.calllog.di;

import android.content.Context;
import app.callcheck.mobile.data.calllog.CallLogDataSource;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class CallLogModule_ProvideCallLogDataSourceFactory implements Factory<CallLogDataSource> {
  private final Provider<Context> contextProvider;

  public CallLogModule_ProvideCallLogDataSourceFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public CallLogDataSource get() {
    return provideCallLogDataSource(contextProvider.get());
  }

  public static CallLogModule_ProvideCallLogDataSourceFactory create(
      Provider<Context> contextProvider) {
    return new CallLogModule_ProvideCallLogDataSourceFactory(contextProvider);
  }

  public static CallLogDataSource provideCallLogDataSource(Context context) {
    return Preconditions.checkNotNullFromProvides(CallLogModule.INSTANCE.provideCallLogDataSource(context));
  }
}
