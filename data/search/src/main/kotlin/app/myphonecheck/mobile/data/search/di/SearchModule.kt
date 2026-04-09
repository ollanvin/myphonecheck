package app.myphonecheck.mobile.data.search.di

import app.myphonecheck.mobile.data.search.CountrySearchRouter
import app.myphonecheck.mobile.data.search.SearchProviderRegistry
import app.myphonecheck.mobile.data.search.SearchResultAnalyzer
import app.myphonecheck.mobile.data.search.provider.createOkHttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

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
@Module
@InstallIn(SingletonComponent::class)
object SearchModule {

    @Provides
    @Singleton
    fun provideSearchResultAnalyzer(): SearchResultAnalyzer {
        return SearchResultAnalyzer()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): okhttp3.OkHttpClient {
        return createOkHttpClient(timeoutSeconds = 2)
    }

    @Provides
    @Singleton
    fun provideCountrySearchRouter(
        httpClient: okhttp3.OkHttpClient
    ): CountrySearchRouter {
        return CountrySearchRouter(httpClient)
    }

    @Provides
    @Singleton
    fun provideSearchProviderRegistry(
        router: CountrySearchRouter
    ): SearchProviderRegistry {
        return SearchProviderRegistry(router)
    }
}
