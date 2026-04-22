package app.myphonecheck.core.common.risk

/**
 * Discrete risk level aligned with v1.6.1 decision contract.
 * UI maps badges from this; user makes final judgment (constitution art.5).
 * FREEZE: five levels until MAJOR.
 */
enum class RiskLevel(val score: Float) {
    SAFE(0.0f),
    SAFE_UNKNOWN(0.2f),
    UNKNOWN(0.4f),
    CAUTION(0.7f),
    DANGER(0.95f),
    ;

    companion object {
        fun fromScore(score: Float): RiskLevel = when {
            score < 0.1f -> SAFE
            score < 0.3f -> SAFE_UNKNOWN
            score < 0.5f -> UNKNOWN
            score < 0.85f -> CAUTION
            else -> DANGER
        }
    }
}
