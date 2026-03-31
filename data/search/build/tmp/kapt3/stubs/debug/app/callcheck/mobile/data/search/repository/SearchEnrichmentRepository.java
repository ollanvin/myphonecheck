package app.callcheck.mobile.data.search.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\bf\u0018\u00002\u00020\u0001J \u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0005H\u00a6@\u00a2\u0006\u0002\u0010\u0007\u00a8\u0006\b"}, d2 = {"Lapp/callcheck/mobile/data/search/repository/SearchEnrichmentRepository;", "", "enrichWithSearch", "Lapp/callcheck/mobile/core/model/SearchEvidence;", "normalizedNumber", "", "countryCode", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "search_debug"})
public abstract interface SearchEnrichmentRepository {
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object enrichWithSearch(@org.jetbrains.annotations.NotNull()
    java.lang.String normalizedNumber, @org.jetbrains.annotations.Nullable()
    java.lang.String countryCode, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super app.callcheck.mobile.core.model.SearchEvidence> $completion);
}