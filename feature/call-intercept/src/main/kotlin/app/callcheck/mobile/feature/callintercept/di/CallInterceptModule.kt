package app.callcheck.mobile.feature.callintercept.di

import app.callcheck.mobile.feature.callintercept.BlocklistRepository
import app.callcheck.mobile.feature.callintercept.BlocklistRepositoryImpl
import app.callcheck.mobile.feature.callintercept.CallInterceptRepository
import app.callcheck.mobile.feature.callintercept.CallInterceptRepositoryImpl
import app.callcheck.mobile.feature.callintercept.DeviceEvidenceProvider
import app.callcheck.mobile.feature.callintercept.DeviceEvidenceProviderImpl
import app.callcheck.mobile.feature.callintercept.SearchEvidenceProvider
import app.callcheck.mobile.feature.callintercept.SearchEvidenceProviderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CallInterceptModule {

    @Binds
    @Singleton
    abstract fun bindCallInterceptRepository(
        impl: CallInterceptRepositoryImpl
    ): CallInterceptRepository

    @Binds
    @Singleton
    abstract fun bindDeviceEvidenceProvider(
        impl: DeviceEvidenceProviderImpl
    ): DeviceEvidenceProvider

    @Binds
    @Singleton
    abstract fun bindSearchEvidenceProvider(
        impl: SearchEvidenceProviderImpl
    ): SearchEvidenceProvider

    @Binds
    @Singleton
    abstract fun bindBlocklistRepository(
        impl: BlocklistRepositoryImpl
    ): BlocklistRepository
}
