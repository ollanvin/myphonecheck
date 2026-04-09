package app.myphonecheck.mobile.feature.deviceevidence

import app.myphonecheck.mobile.core.model.DeviceEvidence

interface DeviceEvidenceRepository {
    suspend fun gatherEvidence(normalizedNumber: String): DeviceEvidence
}
