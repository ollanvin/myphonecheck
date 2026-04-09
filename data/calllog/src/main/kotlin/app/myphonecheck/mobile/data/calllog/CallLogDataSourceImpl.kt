package app.myphonecheck.mobile.data.calllog

import android.content.ContentResolver
import android.content.Context
import android.provider.CallLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar

class CallLogDataSourceImpl(
    private val context: Context,
) : CallLogDataSource {

    private val contentResolver: ContentResolver = context.contentResolver

    override suspend fun getCallHistory(normalizedNumber: String): CallHistoryDetail {
        return withContext(Dispatchers.IO) {
            try {
                queryCallHistory(normalizedNumber)
            } catch (e: SecurityException) {
                CallHistoryDetail()
            } catch (e: Exception) {
                CallHistoryDetail()
            }
        }
    }

    private fun queryCallHistory(normalizedNumber: String): CallHistoryDetail {
        val callRecords = mutableListOf<CallRecord>()

        val cursor = contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            arrayOf(
                CallLog.Calls._ID,
                CallLog.Calls.NUMBER,
                CallLog.Calls.TYPE,
                CallLog.Calls.DATE,
                CallLog.Calls.DURATION,
            ),
            null,
            null,
            "${CallLog.Calls.DATE} DESC",
        )

        cursor?.use {
            val numberIdx = it.getColumnIndex(CallLog.Calls.NUMBER)
            val typeIdx = it.getColumnIndex(CallLog.Calls.TYPE)
            val dateIdx = it.getColumnIndex(CallLog.Calls.DATE)
            val durationIdx = it.getColumnIndex(CallLog.Calls.DURATION)

            while (it.moveToNext()) {
                try {
                    val number = numberIdx.takeIf { idx -> idx >= 0 }?.let { idx -> it.getString(idx) } ?: continue
                    val type = typeIdx.takeIf { idx -> idx >= 0 }?.let { idx -> it.getInt(idx) } ?: continue
                    val date = dateIdx.takeIf { idx -> idx >= 0 }?.let { idx -> it.getLong(idx) } ?: 0
                    val duration = durationIdx.takeIf { idx -> idx >= 0 }?.let { idx -> it.getLong(idx) } ?: 0

                    // Match phone numbers
                    if (matchPhoneNumbers(normalizedNumber, number)) {
                        callRecords.add(
                            CallRecord(
                                type = type,
                                date = date,
                                duration = duration,
                            )
                        )
                    }
                } catch (e: Exception) {
                    continue
                }
            }
        }

        return buildCallHistoryDetail(callRecords)
    }

    private fun buildCallHistoryDetail(records: List<CallRecord>): CallHistoryDetail {
        if (records.isEmpty()) {
            return CallHistoryDetail()
        }

        var outgoingCount = 0
        var incomingCount = 0
        var answeredCount = 0
        var rejectedCount = 0
        var missedCount = 0
        var totalDurationSec = 0L
        var shortCallCount = 0
        var longCallCount = 0
        var lastOutgoingAt: Long? = null
        var lastIncomingAt: Long? = null
        var lastConnectedAt: Long? = null
        var lastRejectedAt: Long? = null
        var lastMissedAt: Long? = null

        for (record in records) {
            when (record.type) {
                CallLog.Calls.OUTGOING_TYPE -> {
                    outgoingCount++
                    if (lastOutgoingAt == null) lastOutgoingAt = record.date
                    if (record.duration > 0) {
                        answeredCount++
                        if (lastConnectedAt == null) lastConnectedAt = record.date
                    }
                }

                CallLog.Calls.INCOMING_TYPE -> {
                    incomingCount++
                    if (lastIncomingAt == null) lastIncomingAt = record.date
                    if (record.duration > 0) {
                        answeredCount++
                        if (lastConnectedAt == null) lastConnectedAt = record.date
                    }
                }

                CallLog.Calls.MISSED_TYPE -> {
                    missedCount++
                    if (lastMissedAt == null) lastMissedAt = record.date
                }

                CallLog.Calls.REJECTED_TYPE -> {
                    rejectedCount++
                    if (lastRejectedAt == null) lastRejectedAt = record.date
                }

                // Treat other types (blocked, voicemail) as part of incoming
                else -> {
                    incomingCount++
                    if (lastIncomingAt == null) lastIncomingAt = record.date
                }
            }

            // Track duration metrics
            totalDurationSec += record.duration
            if (record.duration in 1..<10) {
                shortCallCount++
            } else if (record.duration > 60) {
                longCallCount++
            }
        }

        // connectedCount = calls where duration > 0 (actual conversation happened)
        val connectedCount = answeredCount
        val avgDurationSec = if (connectedCount > 0) totalDurationSec / connectedCount else 0

        // Calculate how many days ago was the last contact
        val lastContact = listOfNotNull(lastOutgoingAt, lastIncomingAt).maxOrNull()
        val recentDaysContact = lastContact?.let {
            val now = System.currentTimeMillis()
            val diffMs = now - it
            val diffDays = (diffMs / (1000 * 60 * 60 * 24)).toInt()
            diffDays
        }

        return CallHistoryDetail(
            outgoingCount = outgoingCount,
            incomingCount = incomingCount,
            answeredCount = answeredCount,
            rejectedCount = rejectedCount,
            missedCount = missedCount,
            connectedCount = connectedCount,
            totalDurationSec = totalDurationSec,
            avgDurationSec = avgDurationSec,
            shortCallCount = shortCallCount,
            longCallCount = longCallCount,
            lastOutgoingAt = lastOutgoingAt,
            lastIncomingAt = lastIncomingAt,
            lastConnectedAt = lastConnectedAt,
            lastRejectedAt = lastRejectedAt,
            lastMissedAt = lastMissedAt,
            recentDaysContact = recentDaysContact,
        )
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

    private data class CallRecord(
        val type: Int,
        val date: Long,
        val duration: Long,
    )
}
