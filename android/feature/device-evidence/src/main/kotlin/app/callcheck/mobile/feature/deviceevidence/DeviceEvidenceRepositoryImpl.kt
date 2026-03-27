package app.callcheck.mobile.feature.deviceevidence

import app.callcheck.mobile.core.model.DeviceEvidence
import app.callcheck.mobile.data.calllog.CallHistoryDetail
import app.callcheck.mobile.data.calllog.CallLogDataSource
import app.callcheck.mobile.data.contacts.ContactsDataSource
import app.callcheck.mobile.data.sms.SmsMetadataDataSource
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

/**
 * Gathers device evidence from contacts, call log, and SMS metadata.
 *
 * CRITICAL: All granular call history fields from CallHistoryDetail
 * MUST be preserved in the output DeviceEvidence. No collapsing allowed.
 */
class DeviceEvidenceRepositoryImpl(
    private val contactsDataSource: ContactsDataSource,
    private val callLogDataSource: CallLogDataSource,
    private val smsMetadataDataSource: SmsMetadataDataSource,
) : DeviceEvidenceRepository {

    override suspend fun gatherEvidence(normalizedNumber: String): DeviceEvidence {
        return coroutineScope {
            // Run all queries in parallel for speed
            val contactNameDeferred = async { contactsDataSource.getContactName(normalizedNumber) }
            val contactSavedDeferred = async { contactsDataSource.isContactSaved(normalizedNumber) }
            val callHistoryDeferred = async { callLogDataSource.getCallHistory(normalizedNumber) }
            val smsMetadataDeferred = async { smsMetadataDataSource.getSmsMetadata(normalizedNumber) }

            val contactName = contactNameDeferred.await()
            val contactSaved = contactSavedDeferred.await()
            val callHistory = callHistoryDeferred.await()
            val smsMetadata = smsMetadataDeferred.await()

            // Map directly — NO collapsing, NO lossy conversion
            mapToDeviceEvidence(
                contactName = contactName,
                contactSaved = contactSaved,
                callHistory = callHistory,
                smsExists = smsMetadata.smsExists,
                smsLastAt = smsMetadata.smsLastAt,
            )
        }
    }

    /**
     * Direct mapping from data layer to core model.
     * Every field from CallHistoryDetail is preserved 1:1.
     */
    private fun mapToDeviceEvidence(
        contactName: String?,
        contactSaved: Boolean,
        callHistory: CallHistoryDetail,
        smsExists: Boolean,
        smsLastAt: Long?,
    ): DeviceEvidence {
        return DeviceEvidence(
            // Contact status
            isSavedContact = contactSaved,
            contactName = contactName,

            // Outgoing — user initiated
            outgoingCount = callHistory.outgoingCount,
            lastOutgoingAt = callHistory.lastOutgoingAt,

            // Incoming — other party initiated
            incomingCount = callHistory.incomingCount,
            lastIncomingAt = callHistory.lastIncomingAt,

            // Answered — user picked up
            answeredCount = callHistory.answeredCount,

            // Rejected — user explicitly declined
            rejectedCount = callHistory.rejectedCount,
            lastRejectedAt = callHistory.lastRejectedAt,

            // Missed — rang but not answered
            missedCount = callHistory.missedCount,
            lastMissedAt = callHistory.lastMissedAt,

            // Connected — duration > 0
            connectedCount = callHistory.connectedCount,
            lastConnectedAt = callHistory.lastConnectedAt,

            // Duration metrics
            totalDurationSec = callHistory.totalDurationSec,
            avgDurationSec = callHistory.avgDurationSec,
            shortCallCount = callHistory.shortCallCount,
            longCallCount = callHistory.longCallCount,

            // Recent activity
            recentDaysContact = callHistory.recentDaysContact,

            // SMS
            smsExists = smsExists,
            smsLastAt = smsLastAt,

            // Tags (not yet implemented in v1.0, reserved)
            localTag = null,
            localMemo = null,
        )
    }
}
