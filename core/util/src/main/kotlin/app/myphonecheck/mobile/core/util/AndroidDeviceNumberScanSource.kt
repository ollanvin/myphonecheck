package app.myphonecheck.mobile.core.util

import android.Manifest
import android.content.Context
import android.provider.CallLog
import android.provider.ContactsContract
import android.provider.Telephony
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import app.myphonecheck.mobile.core.model.DeviceNumberScanSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AndroidDeviceNumberScanSource(
    private val context: Context,
) : DeviceNumberScanSource {

    fun hasCallLogAccess(): Boolean = hasPermission(Manifest.permission.READ_CALL_LOG)

    fun hasContactsAccess(): Boolean = hasPermission(Manifest.permission.READ_CONTACTS)

    fun hasSmsAccess(): Boolean = hasPermission(Manifest.permission.READ_SMS)

    override suspend fun recentCallHistoryNumbers(limit: Int): List<String> = withContext(Dispatchers.IO) {
        if (!hasCallLogAccess()) {
            return@withContext emptyList()
        }

        queryStrings(
            uri = CallLog.Calls.CONTENT_URI,
            projection = arrayOf(CallLog.Calls.NUMBER),
            columnName = CallLog.Calls.NUMBER,
            sortOrder = "${CallLog.Calls.DATE} DESC LIMIT $limit",
        )
    }

    override suspend fun recentSmsSenderNumbers(limit: Int): List<String> = withContext(Dispatchers.IO) {
        if (!hasSmsAccess()) {
            return@withContext emptyList()
        }

        queryStrings(
            uri = Telephony.Sms.CONTENT_URI,
            projection = arrayOf(Telephony.Sms.ADDRESS),
            columnName = Telephony.Sms.ADDRESS,
            sortOrder = "${Telephony.Sms.DATE} DESC LIMIT $limit",
        )
    }

    override suspend fun contactNumbers(limit: Int): List<String> = withContext(Dispatchers.IO) {
        if (!hasContactsAccess()) {
            return@withContext emptyList()
        }

        queryStrings(
            uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
            columnName = ContactsContract.CommonDataKinds.Phone.NUMBER,
            sortOrder = "${ContactsContract.CommonDataKinds.Phone.CONTACT_LAST_UPDATED_TIMESTAMP} DESC LIMIT $limit",
        )
    }

    private fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PermissionChecker.PERMISSION_GRANTED
    }

    private fun queryStrings(
        uri: android.net.Uri,
        projection: Array<String>,
        columnName: String,
        sortOrder: String,
    ): List<String> {
        val resolver = context.contentResolver
        val values = mutableListOf<String>()
        resolver.query(
            uri,
            projection,
            null,
            null,
            sortOrder,
        )?.use { cursor ->
            val columnIndex = cursor.getColumnIndex(columnName)
            if (columnIndex == -1) {
                return emptyList()
            }

            while (cursor.moveToNext()) {
                val value = cursor.getString(columnIndex)?.trim().orEmpty()
                if (value.isNotBlank()) {
                    values += value
                }
            }
        }
        return values
    }
}
