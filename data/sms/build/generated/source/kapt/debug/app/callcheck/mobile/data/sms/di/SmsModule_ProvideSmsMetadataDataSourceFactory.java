package app.callcheck.mobile.data.sms.di;

import android.content.Context;
import app.callcheck.mobile.data.sms.SmsMetadataDataSource;
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
public final class SmsModule_ProvideSmsMetadataDataSourceFactory implements Factory<SmsMetadataDataSource> {
  private final Provider<Context> contextProvider;

  public SmsModule_ProvideSmsMetadataDataSourceFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public SmsMetadataDataSource get() {
    return provideSmsMetadataDataSource(contextProvider.get());
  }

  public static SmsModule_ProvideSmsMetadataDataSourceFactory create(
      Provider<Context> contextProvider) {
    return new SmsModule_ProvideSmsMetadataDataSourceFactory(contextProvider);
  }

  public static SmsMetadataDataSource provideSmsMetadataDataSource(Context context) {
    return Preconditions.checkNotNullFromProvides(SmsModule.INSTANCE.provideSmsMetadataDataSource(context));
  }
}
