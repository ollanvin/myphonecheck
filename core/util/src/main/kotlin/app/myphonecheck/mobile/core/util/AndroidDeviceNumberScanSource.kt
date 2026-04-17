package app.myphonecheck.mobile.core.util

import android.Manifest
import android.content.Context
import android.provider.ContactsContract
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import app.myphonecheck.mobile.core.model.DeviceNumberScanSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AndroidDeviceNumberScanSource(
    private val context: Context,
) : DeviceNumberScanSource {

    /** v4.3: READ_CALL_LOG removed — always returns false */
    fun hasCallLogAccess(): Boolean = false

    fun hasContactsAccess(): Boolean = hasPermission(Manifest.permission.READ_CONTACTS)

    /** v4.3: READ_SMS removed — always returns false */
    fun hasSmsAccess(): Boolean = false

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
        return try {
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
            values
        } catch (e: Exception) {
            Log.w("MPC_SCAN", "queryStrings failed uri=$uri: ${e.message}")
            emptyList()
        }
    }
}
