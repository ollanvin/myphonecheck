package app.myphonecheck.mobile.data.contacts

interface ContactsDataSource {
    suspend fun isContactSaved(normalizedNumber: String): Boolean
    suspend fun getContactName(normalizedNumber: String): String?
    suspend fun getContactInfo(normalizedNumber: String): ContactInfo?
}
