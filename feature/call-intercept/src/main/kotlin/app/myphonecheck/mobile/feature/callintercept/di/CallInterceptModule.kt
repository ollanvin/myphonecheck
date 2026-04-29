package app.myphonecheck.mobile.feature.callintercept.di

import app.myphonecheck.mobile.feature.callintercept.CallInterceptRepository
import app.myphonecheck.mobile.feature.callintercept.CallInterceptRepositoryImpl
import app.myphonecheck.mobile.feature.callintercept.DeviceEvidenceProvider
import app.myphonecheck.mobile.feature.callintercept.DeviceEvidenceProviderImpl
import app.myphonecheck.mobile.feature.callintercept.SearchEvidenceProvider
import app.myphonecheck.mobile.feature.callintercept.SearchEvidenceProviderImpl
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
}
