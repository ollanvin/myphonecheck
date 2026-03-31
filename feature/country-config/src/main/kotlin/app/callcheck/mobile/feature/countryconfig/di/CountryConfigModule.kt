package app.callcheck.mobile.feature.countryconfig.di

import android.content.Context
import app.callcheck.mobile.feature.countryconfig.CountryConfigProvider
import app.callcheck.mobile.feature.countryconfig.CountryConfigProviderImpl
import app.callcheck.mobile.feature.countryconfig.LanguageContextProvider
import app.callcheck.mobile.feature.countryconfig.LanguageContextProviderImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CountryConfigModule {

    @Provides
    @Singleton
    fun provideCountryConfigProvider(): CountryConfigProvider {
        return CountryConfigProviderImpl()
    }

    @Provides
    @Singleton
    fun provideLanguageContextProvider(
        @ApplicationContext context: Context,
    ): LanguageContextProvider {
        return LanguageContextProviderImpl(context)
    }
}
