package app.callcheck.mobile.data.search.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\u0018\u0000 \r2\u00020\u0001:\u0001\rB\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J \u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\nH\u0096@\u00a2\u0006\u0002\u0010\fR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000e"}, d2 = {"Lapp/callcheck/mobile/data/search/repository/SearchEnrichmentRepositoryImpl;", "Lapp/callcheck/mobile/data/search/repository/SearchEnrichmentRepository;", "providerRegistry", "Lapp/callcheck/mobile/data/search/SearchProviderRegistry;", "analyzer", "Lapp/callcheck/mobile/data/search/SearchResultAnalyzer;", "(Lapp/callcheck/mobile/data/search/SearchProviderRegistry;Lapp/callcheck/mobile/data/search/SearchResultAnalyzer;)V", "enrichWithSearch", "Lapp/callcheck/mobile/core/model/SearchEvidence;", "normalizedNumber", "", "countryCode", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "Companion", "search_debug"})
public final class SearchEnrichmentRepositoryImpl implements app.callcheck.mobile.data.search.repository.SearchEnrichmentRepository {
    @org.jetbrains.annotations.NotNull()
    private final app.callcheck.mobile.data.search.SearchProviderRegistry providerRegistry = null;
    @org.jetbrains.annotations.NotNull()
    private final app.callcheck.mobile.data.search.SearchResultAnalyzer analyzer = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "SearchEnrichmentRepo";
    private static final long ENRICHMENT_TIMEOUT_MS = 1500L;
    @org.jetbrains.annotations.NotNull()
    private static final app.callcheck.mobile.data.search.repository.SearchEnrichmentRepositoryImpl.Companion Companion = null;
    
    public SearchEnrichmentRepositoryImpl(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.data.search.SearchProviderRegistry providerRegistry, @org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.data.search.SearchResultAnalyzer analyzer) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object enrichWithSearch(@org.jetbrains.annotations.NotNull()
    java.lang.String normalizedNumber, @org.jetbrains.annotations.Nullable()
    java.lang.String countryCode, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super app.callcheck.mobile.core.model.SearchEvidence> $completion) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0082\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0007"}, d2 = {"Lapp/callcheck/mobile/data/search/repository/SearchEnrichmentRepositoryImpl$Companion;", "", "()V", "ENRICHMENT_TIMEOUT_MS", "", "TAG", "", "search_debug"})
    static final class Companion {
        
        private Companion() {
            super();
        }
    }
}