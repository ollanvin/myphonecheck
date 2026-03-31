package app.callcheck.mobile.feature.countryconfig;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\bf\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&J\u0010\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u0003H&J\b\u0010\t\u001a\u00020\u0007H&\u00a8\u0006\n"}, d2 = {"Lapp/callcheck/mobile/feature/countryconfig/CountryConfigProvider;", "", "detectCountry", "", "context", "Landroid/content/Context;", "getConfig", "Lapp/callcheck/mobile/feature/countryconfig/CountryConfig;", "countryCode", "getDefaultConfig", "country-config_debug"})
public abstract interface CountryConfigProvider {
    
    @org.jetbrains.annotations.NotNull()
    public abstract app.callcheck.mobile.feature.countryconfig.CountryConfig getConfig(@org.jetbrains.annotations.NotNull()
    java.lang.String countryCode);
    
    @org.jetbrains.annotations.NotNull()
    public abstract app.callcheck.mobile.feature.countryconfig.CountryConfig getDefaultConfig();
    
    @org.jetbrains.annotations.NotNull()
    public abstract java.lang.String detectCountry(@org.jetbrains.annotations.NotNull()
    android.content.Context context);
}