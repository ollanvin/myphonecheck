package app.callcheck.mobile.feature.deviceevidence.di;

@dagger.Module()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J \u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0007\u00a8\u0006\u000b"}, d2 = {"Lapp/callcheck/mobile/feature/deviceevidence/di/DeviceEvidenceModule;", "", "()V", "provideDeviceEvidenceRepository", "Lapp/callcheck/mobile/feature/deviceevidence/DeviceEvidenceRepository;", "contactsDataSource", "Lapp/callcheck/mobile/data/contacts/ContactsDataSource;", "callLogDataSource", "Lapp/callcheck/mobile/data/calllog/CallLogDataSource;", "smsMetadataDataSource", "Lapp/callcheck/mobile/data/sms/SmsMetadataDataSource;", "device-evidence_debug"})
@dagger.hilt.InstallIn(value = {dagger.hilt.components.SingletonComponent.class})
public final class DeviceEvidenceModule {
    @org.jetbrains.annotations.NotNull()
    public static final app.callcheck.mobile.feature.deviceevidence.di.DeviceEvidenceModule INSTANCE = null;
    
    private DeviceEvidenceModule() {
        super();
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final app.callcheck.mobile.feature.deviceevidence.DeviceEvidenceRepository provideDeviceEvidenceRepository(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.data.contacts.ContactsDataSource contactsDataSource, @org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.data.calllog.CallLogDataSource callLogDataSource, @org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.data.sms.SmsMetadataDataSource smsMetadataDataSource) {
        return null;
    }
}