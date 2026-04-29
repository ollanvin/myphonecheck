package app.myphonecheck.mobile.feature.messagecheck.di

import app.myphonecheck.mobile.feature.messagecheck.data.MessageHubRepository
import app.myphonecheck.mobile.feature.messagecheck.data.MessageHubRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MessageCheckBindsModule {

    @Binds
    @Singleton
    abstract fun bindMessageHubRepository(impl: MessageHubRepositoryImpl): MessageHubRepository
}
