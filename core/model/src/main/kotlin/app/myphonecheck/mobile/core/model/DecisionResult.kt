package app.myphonecheck.mobile.core.model

import app.myphonecheck.core.common.risk.RiskTier

/**
 * Final decision output shown to the user.
 *
 * MUST contain:
 * - One risk badge
 * - One conclusion category
 * - One action recommendation
 * - One summary string (human-readable, one line)
 * - Max 3 supporting reasons
 * - Confidence score
 *
 * v2.5.0 신규: tier/score (§10-formula-2axis 가중치 합산 결과). default 값으로 호환 유지.
 */
data class DecisionResult(
    val riskLevel: RiskLevel,
    val category: ConclusionCategory,
    val action: ActionRecommendation,
    val confidence: Float,         // 0.0 ~ 1.0
    val summary: String,           // one-line human-readable conclusion
    val reasons: List<String>,     // max 3 supporting reasons
    val importanceLevel: ImportanceLevel = ImportanceLevel.UNKNOWN,
    val importanceReason: String = "",
    val deviceEvidence: DeviceEvidence?,
    val searchEvidence: SearchEvidence?,
    /** v2.5.0 §10-formula-2axis Tier (Stage 3-003-REV 신규, default Unknown). */
    val tier: RiskTier = RiskTier.Unknown,
    /** v2.5.0 §10-formula-2axis 가중치 합산 score [-1, 1] (Stage 3-003-REV 신규, default 0f). */
    val score: Float = 0f,
) {
    companion object {
        /** Safe fallback when everything fails */
        fun fallback() = DecisionResult(
            riskLevel = RiskLevel.UNKNOWN,
            category = ConclusionCategory.INSUFFICIENT_EVIDENCE,
            action = ActionRecommendation.HOLD,
            confidence = 0.0f,
            summary = "판단 근거 부족",
            reasons = listOf("검색 근거 부족"),
            importanceLevel = ImportanceLevel.UNKNOWN,
            importanceReason = "규칙 매칭 없음",
            deviceEvidence = null,
            searchEvidence = null,
        )
    }
}
