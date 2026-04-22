package app.myphonecheck.core.common.risk

import app.myphonecheck.core.common.identifier.IdentifierType

/**
 * Common output contract for four surfaces (golden-egg four attributes + identifier).
 * FREEZE: required properties until MAJOR; extra fields only on concrete types.
 */
interface RiskKnowledge {

    /** Source identifier for this knowledge row. */
    val identifier: IdentifierType

    /** Normalized risk score in [0, 1]. */
    val riskScore: Float

    /** Derived level from [riskScore]. */
    val riskLevel: RiskLevel
        get() = RiskLevel.fromScore(riskScore)

    val expectedDamage: DamageEstimate

    val damageType: DamageType

    /** Reason string or strings.xml key for i18n. */
    val reasoning: String

    val evidence: List<SearchEvidence>
        get() = emptyList()

    val analyzedAt: Long
}
