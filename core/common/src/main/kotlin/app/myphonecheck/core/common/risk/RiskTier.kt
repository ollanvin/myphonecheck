package app.myphonecheck.core.common.risk

/**
 * v2.5.0 §10-formula-2axis Tier 매핑 (Architecture v2.5.0 헌법 §1).
 * 2축 가중치 합산 결과를 사용자 의사결정 보조 Tier로 변환.
 *
 * Stage 0 FREEZE 4 계약 (Checker / DecisionEngineContract / IdentifierType / RiskKnowledge)와 무관 —
 * 본 sealed class는 신규 도메인 (FreezeMarkerTest 22 시그니처 영향 0).
 */
sealed class RiskTier(
    val code: String,
    val color: String,
    val recommendedAction: String,
) {
    object Safe : RiskTier("SAFE", "green", "정상 수신")
    object Caution : RiskTier("CAUTION", "orange", "거절 권장 + 직접 검색")
    object Unknown : RiskTier("UNKNOWN", "gray", "직접 검색 강력 권장")
    object Danger : RiskTier("DANGER", "red", "거절 + 차단 + 신고")
}

/**
 * v2.5.0 §10-formula-2axis Tier 매핑 표:
 *
 * | RiskScore 범위                                            | Tier    |
 * |-----------------------------------------------------------|---------|
 * | score >= +0.5                                             | Danger  |
 * | +0.2 <= score < +0.5                                      | Caution |
 * | -0.2 < score < +0.2 AND totalConfidence < 0.4             | Unknown |
 * | -0.2 < score < +0.2 AND totalConfidence >= 0.4            | Caution |
 * | score <= -0.2                                             | Safe    |
 */
object TierMapping {
    fun from(score: Float, totalConfidence: Float): RiskTier = when {
        score >= 0.5f -> RiskTier.Danger
        score >= 0.2f -> RiskTier.Caution
        score <= -0.2f -> RiskTier.Safe
        totalConfidence < 0.4f -> RiskTier.Unknown
        else -> RiskTier.Caution
    }
}
