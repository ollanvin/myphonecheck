package app.myphonecheck.mobile.feature.callintercept

import app.myphonecheck.mobile.core.model.DeviceEvidence
import app.myphonecheck.mobile.feature.deviceevidence.DeviceEvidenceRepository
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
