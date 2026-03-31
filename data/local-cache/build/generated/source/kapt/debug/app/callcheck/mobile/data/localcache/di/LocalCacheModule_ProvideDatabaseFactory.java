package app.callcheck.mobile.data.localcache.di;

import android.content.Context;
import app.callcheck.mobile.data.localcache.db.CallCheckDatabase;
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
public final class LocalCacheModule_ProvideDatabaseFactory implements Factory<CallCheckDatabase> {
  private final Provider<Context> contextProvider;

  public LocalCacheModule_ProvideDatabaseFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public CallCheckDatabase get() {
    return provideDatabase(contextProvider.get());
  }

  public static LocalCacheModule_ProvideDatabaseFactory create(Provider<Context> contextProvider) {
    return new LocalCacheModule_ProvideDatabaseFactory(contextProvider);
  }

  public static CallCheckDatabase provideDatabase(Context context) {
    return Preconditions.checkNotNullFromProvides(LocalCacheModule.INSTANCE.provideDatabase(context));
  }
}
