package app.callcheck.mobile.feature.callintercept;

/**
 * Production SearchEvidenceProvider that delegates to SearchEnrichmentRepository.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J \u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\b\u0010\t\u001a\u0004\u0018\u00010\bH\u0096@\u00a2\u0006\u0002\u0010\nR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000b"}, d2 = {"Lapp/callcheck/mobile/feature/callintercept/SearchEvidenceProviderImpl;", "Lapp/callcheck/mobile/feature/callintercept/SearchEvidenceProvider;", "searchEnrichmentRepository", "Lapp/callcheck/mobile/data/search/repository/SearchEnrichmentRepository;", "(Lapp/callcheck/mobile/data/search/repository/SearchEnrichmentRepository;)V", "gather", "Lapp/callcheck/mobile/core/model/SearchEvidence;", "normalizedNumber", "", "deviceCountryCode", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "call-intercept_debug"})
public final class SearchEvidenceProviderImpl implements app.callcheck.mobile.feature.callintercept.SearchEvidenceProvider {
    @org.jetbrains.annotations.NotNull()
    private final app.callcheck.mobile.data.search.repository.SearchEnrichmentRepository searchEnrichmentRepository = null;
    
    @javax.inject.Inject()
    public SearchEvidenceProviderImpl(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.data.search.repository.SearchEnrichmentRepository searchEnrichmentRepository) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object gather(@org.jetbrains.annotations.NotNull()
    java.lang.String normalizedNumber, @org.jetbrains.annotations.Nullable()
    java.lang.String deviceCountryCode, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super app.callcheck.mobile.core.model.SearchEvidence> $completion) {
        return null;
    }
}