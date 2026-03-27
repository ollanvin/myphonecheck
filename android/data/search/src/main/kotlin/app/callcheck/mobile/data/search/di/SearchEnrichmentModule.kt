package app.callcheck.mobile.data.search.di

import app.callcheck.mobile.data.search.SearchProviderRegistry
import app.callcheck.mobile.data.search.SearchResultAnalyzer
import app.callcheck.mobile.data.search.repository.SearchEnrichmentRepository
import app.callcheck.mobile.data.search.repository.SearchEnrichmentRepositoryImpl
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
