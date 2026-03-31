package app.callcheck.mobile.data.contacts.di;

import android.content.Context;
import app.callcheck.mobile.data.contacts.ContactsDataSource;
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
public final class ContactsModule_ProvideContactsDataSourceFactory implements Factory<ContactsDataSource> {
  private final Provider<Context> contextProvider;

  public ContactsModule_ProvideContactsDataSourceFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public ContactsDataSource get() {
    return provideContactsDataSource(contextProvider.get());
  }

  public static ContactsModule_ProvideContactsDataSourceFactory create(
      Provider<Context> contextProvider) {
    return new ContactsModule_ProvideContactsDataSourceFactory(contextProvider);
  }

  public static ContactsDataSource provideContactsDataSource(Context context) {
    return Preconditions.checkNotNullFromProvides(ContactsModule.INSTANCE.provideContactsDataSource(context));
  }
}
