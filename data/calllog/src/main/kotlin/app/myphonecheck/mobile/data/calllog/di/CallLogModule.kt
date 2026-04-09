package app.myphonecheck.mobile.data.calllog.di

import android.content.Context
import app.myphonecheck.mobile.data.calllog.CallLogDataSource
import app.myphonecheck.mobile.data.calllog.CallLogDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CallLogModule {

    @Provides
    @Singleton
    fun provideCallLogDataSource(
        @ApplicationContext context: Context,
    ): CallLogDataSource {
        return CallLogDataSourceImpl(context)
    }
}
