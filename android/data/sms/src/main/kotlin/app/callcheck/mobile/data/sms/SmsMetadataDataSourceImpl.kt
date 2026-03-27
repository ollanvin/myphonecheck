package app.callcheck.mobile.data.sms

import android.content.ContentResolver
import android.content.Context
import android.provider.Telephony
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SmsMetadataDataSourceImpl(
    private val context: Context,
) : SmsMetadataDataSource {

    private val contentResolver: ContentResolver = context.contentResolver

    override suspend fun getSmsMetadata(normalizedNumber: String): SmsMetadata {
        return withContext(Dispatchers.IO) {
            try {
                querySmsMetadata(normalizedNumber)
            } catch (e: SecurityException) {
                SmsMetadata()
            } catch (e: Exception) {
                SmsMetadata()
            }
        }
    }

    private fun querySmsMetadata(normalizedNumber: String): SmsMetadata {
        var smsCount = 0
        var lastSmsTime: Long? = null
        var hasIncoming = false
        var hasOutgoing = false

        // Query inbox
        querySmsFromBox(normalizedNumber, SMS_TYPE_INBOX)?.let { (count, lastTime, hasInc, hasOut) ->
            smsCount += count
            if (lastTime != null && (lastSmsTime == null || lastTime > lastSmsTime)) {
                lastSmsTime = lastTime
            }
            hasIncoming = hasIncoming || hasInc
            hasOutgoing = hasOutgoing || hasOut
        }

        // Query sent
        querySmsFromBox(normalizedNumber, SMS_TYPE_SENT)?.let { (count, lastTime, hasInc, hasOut) ->
            smsCount += count
            if (lastTime != null && (lastSmsTime == null || lastTime > lastSmsTime)) {
                lastSmsTime = lastTime
            }
            hasIncoming = hasIncoming || hasInc
            hasOutgoing = hasOutgoing || hasOut
        }

        // Query drafts (only if they match our number)
        querySmsFromBox(normalizedNumber, SMS_TYPE_DRAFT)?.let { (count, lastTime, hasInc, hasOut) ->
            smsCount += count
            if (lastTime != null && (lastSmsTime == null || lastTime > lastSmsTime)) {
                lastSmsTime = lastTime
            }
            hasIncoming = hasIncoming || hasInc
            hasOutgoing = hasOutgoing || hasOut
        }

        return SmsMetadata(
            smsExists = smsCount > 0,
            smsCount = smsCount,
            smsLastAt = lastSmsTime,
            hasIncoming = hasIncoming,
            hasOutgoing = hasOutgoing,
        )
    }

    private data class SmsBoxResult(val count: Int, val lastTime: Long?, val hasInc: Boolean, val hasOut: Boolean)

    private fun querySmsFromBox(normalizedNumber: String, boxType: Int): SmsBoxResult? {
        return try {
            val contentUri = when (boxType) {
                SMS_TYPE_INBOX -> Telephony.Sms.Inbox.CONTENT_URI
                SMS_TYPE_SENT -> Telephony.Sms.Sent.CONTENT_URI
                SMS_TYPE_DRAFT -> Telephony.Sms.Draft.CONTENT_URI
                else -> return null
            }

            val cursor = contentResolver.query(
                contentUri,
                arrayOf(
                    Telephony.Sms._ID,
                    Telephony.Sms.ADDRESS,
                    Telephony.Sms.DATE,
                    Telephony.Sms.TYPE,
                ),
                null,
                null,
                "${Telephony.Sms.DATE} DESC",
            )

            var count = 0
            var lastTime: Long? = null
            var hasIncoming = false
            var hasOutgoing = false

            cursor?.use {
                val addressIdx = it.getColumnIndex(Telephony.Sms.ADDRESS)
                val dateIdx = it.getColumnIndex(Telephony.Sms.DATE)
                val typeIdx = it.getColumnIndex(Telephony.Sms.TYPE)

                while (it.moveToNext()) {
                    try {
                        val address = addressIdx.takeIf { idx -> idx >= 0 }?.let { idx -> it.getString(idx) } ?: continue
                        val date = dateIdx.takeIf { idx -> idx >= 0 }?.let { idx -> it.getLong(idx) } ?: 0
                        val type = typeIdx.takeIf { idx -> idx >= 0 }?.let { idx -> it.getInt(idx) } ?: 0

                        // Match phone numbers
                        if (matchPhoneNumbers(normalizedNumber, address)) {
                            count++
                            if (lastTime == null || date > lastTime) {
                                lastTime = date
                            }

                            // Track direction - but don't expose content
                            when (boxType) {
                                SMS_TYPE_INBOX -> hasIncoming = true
                                SMS_TYPE_SENT -> hasOutgoing = true
                                SMS_TYPE_DRAFT -> hasOutgoing = true
                            }
                        }
                    } catch (e: Exception) {
                        continue
                    }
                }
            }

            SmsBoxResult(count, lastTime, hasIncoming, hasOutgoing)
        } catch (e: Exception) {
            null
        }
    }

    private fun matchPhoneNumbers(normalizedNumber: String, storedNumber: String): Boolean {
        // Direct match
        if (normalizedNumber == storedNumber) {
            return true
        }

        // Extract digits for comparison
        val normalizedDigits = normalizedNumber.replace(Regex("[^\\d]"), "")
        val storedDigits = storedNumber.replace(Regex("[^\\d]"), "")

        // Match all digits
        if (normalizedDigits == storedDigits) {
            return true
        }

        // Match last 10 digits (for US/Canada)
        if (normalizedDigits.length >= 10 && storedDigits.length >= 10) {
            if (normalizedDigits.takeLast(10) == storedDigits.takeLast(10)) {
                return true
            }
        }

        return false
    }

    companion object {
        private const val SMS_TYPE_INBOX = 1
        private const val SMS_TYPE_SENT = 2
        private const val SMS_TYPE_DRAFT = 3
    }
}
