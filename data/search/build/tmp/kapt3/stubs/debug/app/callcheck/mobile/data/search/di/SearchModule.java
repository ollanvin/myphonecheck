package app.callcheck.mobile.data.search.di;

/**
 * 검색 모듈 DI 구성.
 *
 * 아키텍처 헌법:
 * - 모든 검색은 온디바이스 직접 HTTP 스크래핑으로만 수행
 * - 외부 API (Google CSE, Naver API 등) 절대 금지
 * - 서버/프록시/Lambda 중계 절대 금지
 * - 사용자 수 증가 = 비용 증가 구조 절대 금지
 *
 * 검색 엔진 6개 (모두 온디바이스 스크래핑):
 * - Google: 글로벌 190개국
 * - Naver: 한국
 * - Baidu: 중국
 * - Yahoo Japan: 일본
 * - Yandex: 러시아 + CIS 10개국
 * - DuckDuckGo HTML: 글로벌 fallback (SSR 100%)
 *
 * 국가별 라우팅: CountrySearchRouter가 국가 코드에 따라 최적 조합 결정.
 * 전체 검색 실패해도 Device Evidence만으로 판정 가능.
 */
@dagger.Module()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0007J\b\u0010\u0007\u001a\u00020\u0006H\u0007J\u0010\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u0004H\u0007J\b\u0010\u000b\u001a\u00020\fH\u0007\u00a8\u0006\r"}, d2 = {"Lapp/callcheck/mobile/data/search/di/SearchModule;", "", "()V", "provideCountrySearchRouter", "Lapp/callcheck/mobile/data/search/CountrySearchRouter;", "httpClient", "Lokhttp3/OkHttpClient;", "provideOkHttpClient", "provideSearchProviderRegistry", "Lapp/callcheck/mobile/data/search/SearchProviderRegistry;", "router", "provideSearchResultAnalyzer", "Lapp/callcheck/mobile/data/search/SearchResultAnalyzer;", "search_debug"})
@dagger.hilt.InstallIn(value = {dagger.hilt.components.SingletonComponent.class})
public final class SearchModule {
    @org.jetbrains.annotations.NotNull()
    public static final app.callcheck.mobile.data.search.di.SearchModule INSTANCE = null;
    
    private SearchModule() {
        super();
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final app.callcheck.mobile.data.search.SearchResultAnalyzer provideSearchResultAnalyzer() {
        return null;
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final okhttp3.OkHttpClient provideOkHttpClient() {
        return null;
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final app.callcheck.mobile.data.search.CountrySearchRouter provideCountrySearchRouter(@org.jetbrains.annotations.NotNull()
    okhttp3.OkHttpClient httpClient) {
        return null;
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final app.callcheck.mobile.data.search.SearchProviderRegistry provideSearchProviderRegistry(@org.jetbrains.annotations.NotNull()
    app.callcheck.mobile.data.search.CountrySearchRouter router) {
        return null;
    }
}