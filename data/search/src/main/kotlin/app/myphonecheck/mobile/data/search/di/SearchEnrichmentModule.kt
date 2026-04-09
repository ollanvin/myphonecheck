package app.myphonecheck.mobile.data.search.di

import app.myphonecheck.mobile.data.search.SearchProviderRegistry
import app.myphonecheck.mobile.data.search.SearchResultAnalyzer
import app.myphonecheck.mobile.data.search.repository.SearchEnrichmentRepository
import app.myphonecheck.mobile.data.search.repository.SearchEnrichmentRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SearchEnrichmentModule {

    @Provides
    @Singleton
    fun provideSearchEnrichmentRepository(
        providerRegistry: SearchProviderRegistry,
        analyzer: SearchResultAnalyzer
    ): SearchEnrichmentRepository {
        return SearchEnrichmentRepositoryImpl(
            providerRegistry = providerRegistry,
            analyzer = analyzer
        )
    }
}
