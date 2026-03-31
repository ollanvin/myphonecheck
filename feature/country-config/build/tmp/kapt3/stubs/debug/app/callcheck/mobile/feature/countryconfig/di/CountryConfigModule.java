package app.callcheck.mobile.feature.countryconfig.di;

@dagger.Module()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H\u0007J\u0012\u0010\u0005\u001a\u00020\u00062\b\b\u0001\u0010\u0007\u001a\u00020\bH\u0007\u00a8\u0006\t"}, d2 = {"Lapp/callcheck/mobile/feature/countryconfig/di/CountryConfigModule;", "", "()V", "provideCountryConfigProvider", "Lapp/callcheck/mobile/feature/countryconfig/CountryConfigProvider;", "provideLanguageContextProvider", "Lapp/callcheck/mobile/feature/countryconfig/LanguageContextProvider;", "context", "Landroid/content/Context;", "country-config_debug"})
@dagger.hilt.InstallIn(value = {dagger.hilt.components.SingletonComponent.class})
public final class CountryConfigModule {
    @org.jetbrains.annotations.NotNull()
    public static final app.callcheck.mobile.feature.countryconfig.di.CountryConfigModule INSTANCE = null;
    
    private CountryConfigModule() {
        super();
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final app.callcheck.mobile.feature.countryconfig.CountryConfigProvider provideCountryConfigProvider() {
        return null;
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final app.callcheck.mobile.feature.countryconfig.LanguageContextProvider provideLanguageContextProvider(@dagger.hilt.android.qualifiers.ApplicationContext()
    @org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
}