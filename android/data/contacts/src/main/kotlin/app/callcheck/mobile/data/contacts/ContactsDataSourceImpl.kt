package app.callcheck.mobile.data.contacts

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ContactsDataSourceImpl(
    private val context: Context,
) : ContactsDataSource {

    private val contentResolver: ContentResolver = context.contentResolver

    override suspend fun isContactSaved(normalizedNumber: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                queryContactName(normalizedNumber) != null
            } catch (e: Exception) {
                false
            }
        }
    }

    override suspend fun getContactName(normalizedNumber: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                queryContactName(normalizedNumber)
            } catch (e: Exception) {
                null
            }
        }
    }

    override suspend fun getContactInfo(normalizedNumber: String): ContactInfo? {
        return withContext(Dispatchers.IO) {
            try {
                queryContactInfo(normalizedNumber)
            } catch (e: Exception) {
                null
            }
        }
    }

    private fun queryContactName(normalizedNumber: String): String? {
        val numbersToTry = listOf(normalizedNumber) + generateNumberVariants(normalizedNumber)
        for (number in numbersToTry) {
            val cursor = queryPhoneLookup(number) ?: continue
            cursor.use { c ->
                if (c.moveToFirst()) {
                    val nameIdx = c.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)
                    if (nameIdx >= 0) {
                        val name = c.getString(nameIdx)
                        if (!name.isNullOrBlank()) return name
                    }
                }
            }
        }
        return null
    }

    private fun queryContactInfo(normalizedNumber: String): ContactInfo? {
        val numbersToTry = listOf(normalizedNumber) + generateNumberVariants(normalizedNumber)
        for (number in numbersToTry) {
            val cursor = queryPhoneLookup(number) ?: continue
            cursor.use { c ->
                if (c.moveToFirst()) {
                    val nameIdx = c.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)
                    val photoIdx = c.getColumnIndex(ContactsContract.PhoneLookup.PHOTO_URI)
                    val contactIdIdx = c.getColumnIndex(ContactsContract.PhoneLookup._ID)

                    val name = if (nameIdx >= 0) c.getString(nameIdx)?.takeIf { it.isNotBlank() } else null
                    val photoUri = if (photoIdx >= 0) c.getString(photoIdx)?.takeIf { it.isNotBlank() } else null
                    val contactId = if (contactIdIdx >= 0) c.getString(contactIdIdx) else null

                    return ContactInfo(
                        name = name,
                        phoneNumber = normalizedNumber,
                        photoUri = photoUri,
                        isFavorite = isFavoriteContact(contactId),
                    )
                }
            }
        }
        return null
    }

    private fun queryPhoneLookup(phoneNumber: String): Cursor? {
        return try {
            contentResolver.query(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI.buildUpon()
                    .appendPath(phoneNumber)
                    .build(),
                arrayOf(
                    ContactsContract.PhoneLookup.DISPLAY_NAME,
                    ContactsContract.PhoneLookup.PHOTO_URI,
                    ContactsContract.PhoneLookup._ID,
                ),
                null,
                null,
                null,
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun isFavoriteContact(contactId: String?): Boolean {
        if (contactId == null) return false
        return try {
            val cursor = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                arrayOf(ContactsContract.Contacts.STARRED),
                "${ContactsContract.Contacts._ID} = ?",
                arrayOf(contactId),
                null,
            )
            cursor?.use {
                if (it.moveToFirst()) {
                    val starredIdx = it.getColumnIndex(ContactsContract.Contacts.STARRED)
                    starredIdx >= 0 && it.getInt(starredIdx) == 1
                } else false
            } ?: false
        } catch (e: Exception) {
            false
        }
    }

    private fun generateNumberVariants(normalizedNumber: String): List<String> {
        val variants = mutableListOf<String>()
        if (normalizedNumber.startsWith("+")) {
            variants.add(normalizedNumber.substring(1))
        } else {
            variants.add("+$normalizedNumber")
        }
        if (normalizedNumber.length >= 10) {
            variants.add(normalizedNumber.takeLast(10))
        }
        val digitsOnly = normalizedNumber.replace(Regex("[^\\d+]"), "")
        if (digitsOnly != normalizedNumber) {
            variants.add(digitsOnly)
        }
        return variants.distinct()
    }
}
