package app.myphonecheck.mobile.core.model

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
 */
data class DecisionResult(
    val riskLevel: RiskLevel,
    val category: ConclusionCategory,
    val action: ActionRecommendation,
    val confidence: Float,         // 0.0 ~ 1.0
    val summary: String,           // one-line human-readable conclusion
    val reasons: List<String>,     // max 3 supporting reasons
    val deviceEvidence: DeviceEvidence?,
    val searchEvidence: SearchEvidence?,
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
            deviceEvidence = null,
            searchEvidence = null,
        )
    }
}
