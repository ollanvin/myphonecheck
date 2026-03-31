package app.callcheck.mobile.feature.countryconfig;

/**
 * Complete configuration for a specific country.
 *
 * Contains:
 * - Country/language metadata
 * - Phone number formatting rules
 * - Search provider priorities
 * - Keywords for call categorization
 * - Localized UI strings
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0013\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B;\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00030\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u000b\u00a2\u0006\u0002\u0010\fJ\t\u0010\u0017\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0019\u001a\u00020\u0003H\u00c6\u0003J\u000f\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00030\u0007H\u00c6\u0003J\t\u0010\u001b\u001a\u00020\tH\u00c6\u0003J\t\u0010\u001c\u001a\u00020\u000bH\u00c6\u0003JK\u0010\u001d\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00030\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u000bH\u00c6\u0001J\u0013\u0010\u001e\u001a\u00020\u001f2\b\u0010 \u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010!\u001a\u00020\"H\u00d6\u0001J\t\u0010#\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u000eR\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u000eR\u0017\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00030\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\n\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016\u00a8\u0006$"}, d2 = {"Lapp/callcheck/mobile/feature/countryconfig/CountryConfig;", "", "countryCode", "", "language", "phonePrefix", "searchProviderPriority", "", "keywordDictionary", "Lapp/callcheck/mobile/feature/countryconfig/KeywordDictionary;", "uiStrings", "Lapp/callcheck/mobile/feature/countryconfig/UiStrings;", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;Lapp/callcheck/mobile/feature/countryconfig/KeywordDictionary;Lapp/callcheck/mobile/feature/countryconfig/UiStrings;)V", "getCountryCode", "()Ljava/lang/String;", "getKeywordDictionary", "()Lapp/callcheck/mobile/feature/countryconfig/KeywordDictionary;", "getLanguage", "getPhonePrefix", "getSearchProviderPriority", "()Ljava/util/List;", "getUiStrings", "()Lapp/callcheck/mobile/feature/countryconfig/UiStrings;", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "equals", "", "other", "hashCode", "", "toString", "country-config_debug"})
public final class CountryConfig {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String countryCode = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String language = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String phonePrefix = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<java.lang.String> searchProviderPriority = null;
    @org.jetbrains.annotations.NotNull()
    private final app.callcheck.mobile.feature.countryconfig.KeywordDictionary keywordDictionary = null;
    @org.jetbrains.annotations.NotNull()
    private final app.callcheck.mobile.feature.countryconfig.UiStrings uiStrings = null;
    
    public CountryConfig(@org.jetbrains.annotations.NotNull()
    java.lang.String countryCode, @org.jetbrains.annotations.NotNull()
    java.lang.String language, @org.jetbrains.annotations.NotNull()
    java.lang.String phonePrefix, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> searchProviderPriority, @org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.countryconfig.KeywordDictionary keywordDictionary, @org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.countryconfig.UiStrings uiStrings) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getCountryCode() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getLanguage() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getPhonePrefix() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> getSearchProviderPriority() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final app.callcheck.mobile.feature.countryconfig.KeywordDictionary getKeywordDictionary() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final app.callcheck.mobile.feature.countryconfig.UiStrings getUiStrings() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final app.callcheck.mobile.feature.countryconfig.KeywordDictionary component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final app.callcheck.mobile.feature.countryconfig.UiStrings component6() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final app.callcheck.mobile.feature.countryconfig.CountryConfig copy(@org.jetbrains.annotations.NotNull()
    java.lang.String countryCode, @org.jetbrains.annotations.NotNull()
    java.lang.String language, @org.jetbrains.annotations.NotNull()
    java.lang.String phonePrefix, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> searchProviderPriority, @org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.countryconfig.KeywordDictionary keywordDictionary, @org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.feature.countryconfig.UiStrings uiStrings) {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
}