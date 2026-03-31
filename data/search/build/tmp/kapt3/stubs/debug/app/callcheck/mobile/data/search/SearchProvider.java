package app.callcheck.mobile.data.search;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\bf\u0018\u00002\u00020\u0001J \u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u00032\b\u0010\t\u001a\u0004\u0018\u00010\u0003H\u00a6@\u00a2\u0006\u0002\u0010\nR\u0012\u0010\u0002\u001a\u00020\u0003X\u00a6\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0004\u0010\u0005\u00a8\u0006\u000b"}, d2 = {"Lapp/callcheck/mobile/data/search/SearchProvider;", "", "providerName", "", "getProviderName", "()Ljava/lang/String;", "search", "Lapp/callcheck/mobile/data/search/SearchProviderResult;", "phoneNumber", "countryCode", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "search_debug"})
public abstract interface SearchProvider {
    
    @org.jetbrains.annotations.NotNull()
    public abstract java.lang.String getProviderName();
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object search(@org.jetbrains.annotations.NotNull()
    java.lang.String phoneNumber, @org.jetbrains.annotations.Nullable()
    java.lang.String countryCode, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super app.callcheck.mobile.data.search.SearchProviderResult> $completion);
}