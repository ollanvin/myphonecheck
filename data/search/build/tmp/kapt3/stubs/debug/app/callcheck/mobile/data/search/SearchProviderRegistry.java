package app.callcheck.mobile.data.search;

/**
 * 온디바이스 분산 웹 스캔 레지스트리.
 *
 * CountrySearchRouter에서 국가별 최적 Provider 조합을 받아
 * 병렬로 실행한다.
 *
 * 타임아웃 계층:
 * - 개별 Provider: 1000ms (Provider 내부 자체 타임아웃)
 * - Registry 전체: 1200ms (개별보다 약간 여유)
 * - Enrichment Repository: 1500ms (최종 외곽)
 * - Decision Engine: <50ms (별도)
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u0000 \u00112\u00020\u0001:\u0001\u0011B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\b\u0010\b\u001a\u0004\u0018\u00010\u0007J&\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u00062\u0006\u0010\u000b\u001a\u00020\u00072\b\u0010\b\u001a\u0004\u0018\u00010\u0007H\u0086@\u00a2\u0006\u0002\u0010\fJ0\u0010\r\u001a\n\u0012\u0004\u0012\u00020\n\u0018\u00010\u00062\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u000b\u001a\u00020\u00072\b\u0010\b\u001a\u0004\u0018\u00010\u0007H\u0082@\u00a2\u0006\u0002\u0010\u0010R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0012"}, d2 = {"Lapp/callcheck/mobile/data/search/SearchProviderRegistry;", "", "router", "Lapp/callcheck/mobile/data/search/CountrySearchRouter;", "(Lapp/callcheck/mobile/data/search/CountrySearchRouter;)V", "getProviderNames", "", "", "countryCode", "searchAll", "Lapp/callcheck/mobile/data/search/RawSearchResult;", "phoneNumber", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "searchWithProvider", "provider", "Lapp/callcheck/mobile/data/search/SearchProvider;", "(Lapp/callcheck/mobile/data/search/SearchProvider;Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "Companion", "search_debug"})
public final class SearchProviderRegistry {
    @org.jetbrains.annotations.NotNull()
    private final app.callcheck.mobile.data.search.CountrySearchRouter router = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "SearchProviderRegistry";
    private static final long TOTAL_TIMEOUT_MS = 1200L;
    private static final long PER_PROVIDER_TIMEOUT_MS = 1000L;
    @org.jetbrains.annotations.NotNull()
    private static final app.callcheck.mobile.data.search.SearchProviderRegistry.Companion Companion = null;
    
    public SearchProviderRegistry(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.data.search.CountrySearchRouter router) {
        super();
    }
    
    /**
     * 국가 코드 기반으로 최적 Provider 조합을 선택하고 병렬 실행.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object searchAll(@org.jetbrains.annotations.NotNull()
    java.lang.String phoneNumber, @org.jetbrains.annotations.Nullable()
    java.lang.String countryCode, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<app.callcheck.mobile.data.search.RawSearchResult>> $completion) {
        return null;
    }
    
    private final java.lang.Object searchWithProvider(app.callcheck.mobile.data.search.SearchProvider provider, java.lang.String phoneNumber, java.lang.String countryCode, kotlin.coroutines.Continuation<? super java.util.List<app.callcheck.mobile.data.search.RawSearchResult>> $completion) {
        return null;
    }
    
    /**
     * 특정 국가에 대해 사용 가능한 Provider 이름 목록.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> getProviderNames(@org.jetbrains.annotations.Nullable()
    java.lang.String countryCode) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0082\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\b"}, d2 = {"Lapp/callcheck/mobile/data/search/SearchProviderRegistry$Companion;", "", "()V", "PER_PROVIDER_TIMEOUT_MS", "", "TAG", "", "TOTAL_TIMEOUT_MS", "search_debug"})
    static final class Companion {
        
        private Companion() {
            super();
        }
    }
}