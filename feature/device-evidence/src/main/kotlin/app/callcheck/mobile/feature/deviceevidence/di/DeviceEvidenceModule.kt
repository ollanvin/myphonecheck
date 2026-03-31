package app.callcheck.mobile.feature.deviceevidence.di

import app.callcheck.mobile.data.contacts.ContactsDataSource
import app.callcheck.mobile.data.calllog.CallLogDataSource
import app.callcheck.mobile.data.sms.SmsMetadataDataSource
import app.callcheck.mobile.feature.deviceevidence.DeviceEvidenceRepository
import app.callcheck.mobile.feature.deviceevidence.DeviceEvidenceRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DeviceEvidenceModule {

    @Provides
    @Singleton
    fun provideDeviceEvidenceRepository(
        contactsDataSource: ContactsDataSource,
        callLogDataSource: CallLogDataSource,
        smsMetadataDataSource: SmsMetadataDataSource,
    ): DeviceEvidenceRepository {
        return DeviceEvidenceRepositoryImpl(
            contactsDataSource = contactsDataSource,
            callLogDataSource = callLogDataSource,
            smsMetadataDataSource = smsMetadataDataSource,
        )
    }
}
