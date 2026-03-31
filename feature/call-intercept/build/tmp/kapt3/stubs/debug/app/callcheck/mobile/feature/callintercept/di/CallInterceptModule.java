package app.callcheck.mobile.feature.callintercept.di;

@dagger.Module()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b\'\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\'J\u0010\u0010\u0007\u001a\u00020\b2\u0006\u0010\u0005\u001a\u00020\tH\'J\u0010\u0010\n\u001a\u00020\u000b2\u0006\u0010\u0005\u001a\u00020\fH\'J\u0010\u0010\r\u001a\u00020\u000e2\u0006\u0010\u0005\u001a\u00020\u000fH\'\u00a8\u0006\u0010"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/di/CallInterceptModule;", "", "()V", "bindBlocklistRepository", "Lapp/callcheck/mobile/feature/callintercept/BlocklistRepository;", "impl", "Lapp/callcheck/mobile/feature/callintercept/BlocklistRepositoryImpl;", "bindCallInterceptRepository", "Lapp/callcheck/mobile/feature/callintercept/CallInterceptRepository;", "Lapp/callcheck/mobile/feature/callintercept/CallInterceptRepositoryImpl;", "bindDeviceEvidenceProvider", "Lapp/callcheck/mobile/feature/callintercept/DeviceEvidenceProvider;", "Lapp/callcheck/mobile/feature/callintercept/DeviceEvidenceProviderImpl;", "bindSearchEvidenceProvider", "Lapp/callcheck/mobile/feature/callintercept/SearchEvidenceProvider;", "Lapp/callcheck/mobile/feature/callintercept/SearchEvidenceProviderImpl;", "call-intercept_debug"})
@dagger.hilt.InstallIn(value = {dagger.hilt.components.SingletonComponent.class})
public abstract class CallInterceptModule {
    
    public CallInterceptModule() {
        super();
    }
    
    @dagger.Binds()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public abstract app.callcheck.mobile.feature.callintercept.CallInterceptRepository bindCallInterceptRepository(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.callintercept.CallInterceptRepositoryImpl impl);
    
    @dagger.Binds()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public abstract app.callcheck.mobile.feature.callintercept.DeviceEvidenceProvider bindDeviceEvidenceProvider(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.callintercept.DeviceEvidenceProviderImpl impl);
    
    @dagger.Binds()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public abstract app.callcheck.mobile.feature.callintercept.SearchEvidenceProvider bindSearchEvidenceProvider(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.callintercept.SearchEvidenceProviderImpl impl);
    
    @dagger.Binds()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public abstract app.callcheck.mobile.feature.callintercept.BlocklistRepository bindBlocklistRepository(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.callintercept.BlocklistRepositoryImpl impl);
}