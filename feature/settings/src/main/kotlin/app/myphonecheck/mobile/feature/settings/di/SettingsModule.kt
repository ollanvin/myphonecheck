package app.myphonecheck.mobile.feature.settings.di

import android.content.Context
import app.myphonecheck.mobile.feature.settings.SettingsRepository
import app.myphonecheck.mobile.feature.settings.SettingsRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SettingsModule {

    @Provides
    @Singleton
    fun provideSettingsRepository(
        @ApplicationContext context: Context,
    ): SettingsRepository {
        return SettingsRepositoryImpl(context)
    }
}
