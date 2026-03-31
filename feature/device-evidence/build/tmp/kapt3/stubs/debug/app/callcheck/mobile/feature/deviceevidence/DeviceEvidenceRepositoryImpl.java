package app.callcheck.mobile.feature.deviceevidence;

/**
 * Gathers device evidence from contacts, call log, and SMS metadata.
 *
 * CRITICAL: All granular call history fields from CallHistoryDetail
 * MUST be preserved in the output DeviceEvidence. No collapsing allowed.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\u0016\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0096@\u00a2\u0006\u0002\u0010\rJ9\u0010\u000e\u001a\u00020\n2\b\u0010\u000f\u001a\u0004\u0018\u00010\f2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00112\b\u0010\u0015\u001a\u0004\u0018\u00010\u0016H\u0002\u00a2\u0006\u0002\u0010\u0017R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0018"}, d2 = {"Lapp/callcheck/mobile/feature/deviceevidence/DeviceEvidenceRepositoryImpl;", "Lapp/callcheck/mobile/feature/deviceevidence/DeviceEvidenceRepository;", "contactsDataSource", "Lapp/callcheck/mobile/data/contacts/ContactsDataSource;", "callLogDataSource", "Lapp/callcheck/mobile/data/calllog/CallLogDataSource;", "smsMetadataDataSource", "Lapp/callcheck/mobile/data/sms/SmsMetadataDataSource;", "(Lapp/callcheck/mobile/data/contacts/ContactsDataSource;Lapp/callcheck/mobile/data/calllog/CallLogDataSource;Lapp/callcheck/mobile/data/sms/SmsMetadataDataSource;)V", "gatherEvidence", "Lapp/callcheck/mobile/core/model/DeviceEvidence;", "normalizedNumber", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "mapToDeviceEvidence", "contactName", "contactSaved", "", "callHistory", "Lapp/callcheck/mobile/data/calllog/CallHistoryDetail;", "smsExists", "smsLastAt", "", "(Ljava/lang/String;ZLapp/callcheck/mobile/data/calllog/CallHistoryDetail;ZLjava/lang/Long;)Lapp/callcheck/mobile/core/model/DeviceEvidence;", "device-evidence_debug"})
public final class DeviceEvidenceRepositoryImpl implements app.callcheck.mobile.feature.deviceevidence.DeviceEvidenceRepository {
    @org.jetbrains.annotations.NotNull()
    private final app.callcheck.mobile.data.contacts.ContactsDataSource contactsDataSource = null;
    @org.jetbrains.annotations.NotNull()
    private final app.callcheck.mobile.data.calllog.CallLogDataSource callLogDataSource = null;
    @org.jetbrains.annotations.NotNull()
    private final app.callcheck.mobile.data.sms.SmsMetadataDataSource smsMetadataDataSource = null;
    
    public DeviceEvidenceRepositoryImpl(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.data.contacts.ContactsDataSource contactsDataSource, @org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.data.calllog.CallLogDataSource callLogDataSource, @org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.data.sms.SmsMetadataDataSource smsMetadataDataSource) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object gatherEvidence(@org.jetbrains.annotations.NotNull()
    java.lang.String normalizedNumber, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super app.callcheck.mobile.core.model.DeviceEvidence> $completion) {
        return null;
    }
    
    /**
     * Direct mapping from data layer to core model.
     * Every field from CallHistoryDetail is preserved 1:1.
     */
    private final app.callcheck.mobile.core.model.DeviceEvidence mapToDeviceEvidence(java.lang.String contactName, boolean contactSaved, app.callcheck.mobile.data.calllog.CallHistoryDetail callHistory, boolean smsExists, java.lang.Long smsLastAt) {
        return null;
    }
}