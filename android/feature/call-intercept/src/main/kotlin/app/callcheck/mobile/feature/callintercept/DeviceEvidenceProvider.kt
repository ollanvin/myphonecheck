package app.callcheck.mobile.feature.callintercept

import app.callcheck.mobile.core.model.DeviceEvidence

/**
 * Gathers device-local evidence for a phone number.
 *
 * Implementation delegates to ContactsDataSource, CallLogDataSource,
 * and SmsMetadataDataSource under the hood.
 */
interface DeviceEvidenceProvider {
    suspend fun gather(normalizedNumber: String): DeviceEvidence
}
