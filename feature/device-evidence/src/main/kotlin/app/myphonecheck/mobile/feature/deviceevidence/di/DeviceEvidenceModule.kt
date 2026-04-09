package app.myphonecheck.mobile.feature.deviceevidence.di

import app.myphonecheck.mobile.data.contacts.ContactsDataSource
import app.myphonecheck.mobile.data.calllog.CallLogDataSource
import app.myphonecheck.mobile.data.sms.SmsMetadataDataSource
import app.myphonecheck.mobile.feature.deviceevidence.DeviceEvidenceRepository
import app.myphonecheck.mobile.feature.deviceevidence.DeviceEvidenceRepositoryImpl
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
