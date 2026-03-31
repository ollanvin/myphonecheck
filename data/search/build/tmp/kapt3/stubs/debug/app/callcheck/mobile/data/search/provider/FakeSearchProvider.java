package app.callcheck.mobile.data.search.provider;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\u000f\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bH\u0002J\u000e\u0010\n\u001a\b\u0012\u0004\u0012\u00020\t0\bH\u0002J\u000e\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\t0\bH\u0002J\u000e\u0010\f\u001a\b\u0012\u0004\u0012\u00020\t0\bH\u0002J\u000e\u0010\r\u001a\b\u0012\u0004\u0012\u00020\t0\bH\u0002J \u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00032\b\u0010\u0011\u001a\u0004\u0018\u00010\u0003H\u0096@\u00a2\u0006\u0002\u0010\u0012R\u0014\u0010\u0002\u001a\u00020\u0003X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0013"}, d2 = {"Lapp/callcheck/mobile/data/search/provider/FakeSearchProvider;", "Lapp/callcheck/mobile/data/search/SearchProvider;", "providerName", "", "(Ljava/lang/String;)V", "getProviderName", "()Ljava/lang/String;", "generateBusinessResults", "", "Lapp/callcheck/mobile/data/search/RawSearchResult;", "generateDeliveryResults", "generateMixedResults", "generateSpamResults", "generateUnknownResults", "search", "Lapp/callcheck/mobile/data/search/SearchProviderResult;", "phoneNumber", "countryCode", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "search_debug"})
public final class FakeSearchProvider implements app.callcheck.mobile.data.search.SearchProvider {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String providerName = null;
    
    public FakeSearchProvider(@org.jetbrains.annotations.NotNull()
    java.lang.String providerName) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String getProviderName() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object search(@org.jetbrains.annotations.NotNull()
    java.lang.String phoneNumber, @org.jetbrains.annotations.Nullable()
    java.lang.String countryCode, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super app.callcheck.mobile.data.search.SearchProviderResult> $completion) {
        return null;
    }
    
    private final java.util.List<app.callcheck.mobile.data.search.RawSearchResult> generateDeliveryResults() {
        return null;
    }
    
    private final java.util.List<app.callcheck.mobile.data.search.RawSearchResult> generateSpamResults() {
        return null;
    }
    
    private final java.util.List<app.callcheck.mobile.data.search.RawSearchResult> generateBusinessResults() {
        return null;
    }
    
    private final java.util.List<app.callcheck.mobile.data.search.RawSearchResult> generateMixedResults() {
        return null;
    }
    
    private final java.util.List<app.callcheck.mobile.data.search.RawSearchResult> generateUnknownResults() {
        return null;
    }
    
    public FakeSearchProvider() {
        super();
    }
}