package app.callcheck.mobile.feature.decisionengine

import app.callcheck.mobile.core.model.RiskLevel
import javax.inject.Inject

/**
 * Maps normalized risk score (0.0–1.0) to RiskLevel enum.
 *
 * PRD risk levels: HIGH / MEDIUM / LOW / UNKNOWN
 * - HIGH: >= 0.6
 * - MEDIUM: >= 0.3
 * - LOW: >= 0.0 (with evidence)
 * - UNKNOWN: no evidence at all
 */
class RiskBadgeMapper @Inject constructor() {

    fun map(score: Float, hasEvidence: Boolean): RiskLevel {
        if (!hasEvidence) return RiskLevel.UNKNOWN

        val clamped = score.coerceIn(0f, 1f)
        return when {
            clamped >= 0.6f -> RiskLevel.HIGH
            clamped >= 0.3f -> RiskLevel.MEDIUM
            else -> RiskLevel.LOW
        }
    }
}
