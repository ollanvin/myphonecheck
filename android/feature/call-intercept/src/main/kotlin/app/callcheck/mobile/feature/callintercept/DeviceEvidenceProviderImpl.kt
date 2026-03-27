package app.callcheck.mobile.feature.callintercept

import app.callcheck.mobile.core.model.DeviceEvidence
import app.callcheck.mobile.feature.deviceevidence.DeviceEvidenceRepository
import javax.inject.Inject

/**
 * Production DeviceEvidenceProvider that delegates to DeviceEvidenceRepository.
 */
class DeviceEvidenceProviderImpl @Inject constructor(
    private val deviceEvidenceRepository: DeviceEvidenceRepository,
) : DeviceEvidenceProvider {

    override suspend fun gather(normalizedNumber: String): DeviceEvidence {
        return deviceEvidenceRepository.gatherEvidence(normalizedNumber)
    }
}
