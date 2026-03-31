package app.callcheck.mobile.feature.callintercept;

/**
 * Production DeviceEvidenceProvider that delegates to DeviceEvidenceRepository.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0096@\u00a2\u0006\u0002\u0010\tR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\n"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/DeviceEvidenceProviderImpl;", "Lapp/callcheck/mobile/feature/callintercept/DeviceEvidenceProvider;", "deviceEvidenceRepository", "Lapp/callcheck/mobile/feature/deviceevidence/DeviceEvidenceRepository;", "(Lapp/callcheck/mobile/feature/deviceevidence/DeviceEvidenceRepository;)V", "gather", "Lapp/callcheck/mobile/core/model/DeviceEvidence;", "normalizedNumber", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "call-intercept_debug"})
public final class DeviceEvidenceProviderImpl implements app.callcheck.mobile.feature.callintercept.DeviceEvidenceProvider {
    @org.jetbrains.annotations.NotNull()
    private final app.callcheck.mobile.feature.deviceevidence.DeviceEvidenceRepository deviceEvidenceRepository = null;
    
    @javax.inject.Inject()
    public DeviceEvidenceProviderImpl(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.deviceevidence.DeviceEvidenceRepository deviceEvidenceRepository) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object gather(@org.jetbrains.annotations.NotNull()
    java.lang.String normalizedNumber, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super app.callcheck.mobile.core.model.DeviceEvidence> $completion) {
        return null;
    }
}