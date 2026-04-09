package app.myphonecheck.mobile.data.contacts.di

import android.content.Context
import app.myphonecheck.mobile.data.contacts.ContactsDataSource
import app.myphonecheck.mobile.data.contacts.ContactsDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ContactsModule {

    @Provides
    @Singleton
    fun provideContactsDataSource(
        @ApplicationContext context: Context,
    ): ContactsDataSource {
        return ContactsDataSourceImpl(context)
    }
}
