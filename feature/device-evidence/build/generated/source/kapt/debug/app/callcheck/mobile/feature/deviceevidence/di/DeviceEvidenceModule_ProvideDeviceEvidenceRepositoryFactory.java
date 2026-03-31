package app.callcheck.mobile.feature.deviceevidence.di;

import app.callcheck.mobile.data.calllog.CallLogDataSource;
import app.callcheck.mobile.data.contacts.ContactsDataSource;
import app.callcheck.mobile.data.sms.SmsMetadataDataSource;
import app.callcheck.mobile.feature.deviceevidence.DeviceEvidenceRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DeviceEvidenceModule_ProvideDeviceEvidenceRepositoryFactory implements Factory<DeviceEvidenceRepository> {
  private final Provider<ContactsDataSource> contactsDataSourceProvider;

  private final Provider<CallLogDataSource> callLogDataSourceProvider;

  private final Provider<SmsMetadataDataSource> smsMetadataDataSourceProvider;

  public DeviceEvidenceModule_ProvideDeviceEvidenceRepositoryFactory(
      Provider<ContactsDataSource> contactsDataSourceProvider,
      Provider<CallLogDataSource> callLogDataSourceProvider,
      Provider<SmsMetadataDataSource> smsMetadataDataSourceProvider) {
    this.contactsDataSourceProvider = contactsDataSourceProvider;
    this.callLogDataSourceProvider = callLogDataSourceProvider;
    this.smsMetadataDataSourceProvider = smsMetadataDataSourceProvider;
  }

  @Override
  public DeviceEvidenceRepository get() {
    return provideDeviceEvidenceRepository(contactsDataSourceProvider.get(), callLogDataSourceProvider.get(), smsMetadataDataSourceProvider.get());
  }

  public static DeviceEvidenceModule_ProvideDeviceEvidenceRepositoryFactory create(
      Provider<ContactsDataSource> contactsDataSourceProvider,
      Provider<CallLogDataSource> callLogDataSourceProvider,
      Provider<SmsMetadataDataSource> smsMetadataDataSourceProvider) {
    return new DeviceEvidenceModule_ProvideDeviceEvidenceRepositoryFactory(contactsDataSourceProvider, callLogDataSourceProvider, smsMetadataDataSourceProvider);
  }

  public static DeviceEvidenceRepository provideDeviceEvidenceRepository(
      ContactsDataSource contactsDataSource, CallLogDataSource callLogDataSource,
      SmsMetadataDataSource smsMetadataDataSource) {
    return Preconditions.checkNotNullFromProvides(DeviceEvidenceModule.INSTANCE.provideDeviceEvidenceRepository(contactsDataSource, callLogDataSource, smsMetadataDataSource));
  }
}
