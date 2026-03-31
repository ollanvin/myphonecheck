package app.callcheck.mobile.feature.deviceevidence

import app.callcheck.mobile.core.model.DeviceEvidence

interface DeviceEvidenceRepository {
    suspend fun gatherEvidence(normalizedNumber: String): DeviceEvidence
}
