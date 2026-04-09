package app.myphonecheck.mobile.data.sms

interface SmsMetadataDataSource {
    suspend fun getSmsMetadata(normalizedNumber: String): SmsMetadata
}
