package app.callcheck.mobile.data.sms.di

import android.content.Context
import app.callcheck.mobile.data.sms.SmsMetadataDataSource
import app.callcheck.mobile.data.sms.SmsMetadataDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SmsModule {

    @Provides
    @Singleton
    fun provideSmsMetadataDataSource(
        @ApplicationContext context: Context,
    ): SmsMetadataDataSource {
        return SmsMetadataDataSourceImpl(context)
    }
}
