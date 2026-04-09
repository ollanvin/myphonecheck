package app.myphonecheck.mobile.feature.pushintercept

import app.myphonecheck.mobile.core.model.ActionRecommendation
import app.myphonecheck.mobile.core.model.ConclusionCategory
import app.myphonecheck.mobile.core.model.DecisionResult
import app.myphonecheck.mobile.core.model.PushEvidence
import app.myphonecheck.mobile.core.model.RiskLevel

/**
 * PushCheck 판단 엔진.
 *
 * 알림 수신 이벤트를 분석하여 DecisionResult를 생성합니다.
 * MyPhoneCheck Decision Engine과 동일한 출력 포맷을 사용합니다.
 *
 * 판단 기준:
 * 1. 빈도 — 같은 앱이 짧은 시간 내 과도한 알림을 보내는가
 * 2. 내용 — 프로모션/광고성 키워드가 포함되어 있는가
 * 3. 시간대 — 야간 시간대(22:00~07:00) 알림인가
 * 4. 상호작용 — 사용자가 이 앱의 알림을 실제로 탭하는 비율
 *
 * 판단 원칙 (MyPhoneCheck과 동일):
 * - 자동 차단 금지
 * - 사용자에게 판단 보조 정보만 제공
 * - 행동 결정은 사용자
 */
object PushCheckEngine {

    /**
     * 푸시 알림을 분석하여 판단 결과를 생성합니다.
     *
     * @param evidence NotificationListenerService에서 수집한 알림 증거
     * @return MyPhoneCheck과 동일한 DecisionResult 포맷
     */
    fun evaluate(evidence: PushEvidence): DecisionResult {
        val spamScore = evidence.spamScore
        val category = determineCategory(evidence, spamScore)
        val riskLevel = determineRiskLevel(spamScore, category)
        val action = determineAction(riskLevel, category)
        val summary = buildSummary(evidence, category)
        val reasons = buildReasons(evidence, spamScore)

        return DecisionResult(
            riskLevel = riskLevel,
            category = category,
            action = action,
            confidence = calculateConfidence(evidence),
            summary = summary,
            reasons = reasons,
            deviceEvidence = null,
            searchEvidence = null,
        )
    }

    private fun determineCategory(
        evidence: PushEvidence,
        spamScore: Float,
    ): ConclusionCategory {
        return when {
            // 야간 방해
            evidence.isNightTime && evidence.countLast24h >= 3 ->
                ConclusionCategory.PUSH_NIGHT_DISTURB

            // 명확한 프로모션
            evidence.promotionKeywordHits >= 2 || spamScore >= 0.6f ->
                ConclusionCategory.PUSH_PROMOTION

            // 소음 (빈도만 높고 내용 무의미)
            evidence.countLast24h >= 10 && evidence.interactionRate < 0.1f ->
                ConclusionCategory.PUSH_NOISE

            // 핵심 알림
            evidence.isLikelyCritical ->
                ConclusionCategory.PUSH_CRITICAL

            // 프로모션 의심
            spamScore >= 0.3f ->
                ConclusionCategory.PUSH_PROMOTION

            // 기본
            else -> ConclusionCategory.PUSH_CRITICAL
        }
    }

    private fun determineRiskLevel(
        spamScore: Float,
        category: ConclusionCategory,
    ): RiskLevel {
        return when {
            category == ConclusionCategory.PUSH_CRITICAL -> RiskLevel.LOW
            spamScore >= 0.6f -> RiskLevel.HIGH
            spamScore >= 0.3f -> RiskLevel.MEDIUM
            else -> RiskLevel.LOW
        }
    }

    private fun determineAction(
        riskLevel: RiskLevel,
        category: ConclusionCategory,
    ): ActionRecommendation {
        return when (category) {
            ConclusionCategory.PUSH_CRITICAL -> ActionRecommendation.ANSWER
            ConclusionCategory.PUSH_PROMOTION -> ActionRecommendation.ANSWER_WITH_CAUTION
            ConclusionCategory.PUSH_NOISE -> ActionRecommendation.REJECT
            ConclusionCategory.PUSH_NIGHT_DISTURB -> ActionRecommendation.REJECT
            else -> ActionRecommendation.HOLD
        }
    }

    private fun buildSummary(
        evidence: PushEvidence,
        category: ConclusionCategory,
    ): String {
        val appName = evidence.appLabel.ifEmpty { evidence.packageName.substringAfterLast(".") }
        return when (category) {
            ConclusionCategory.PUSH_CRITICAL ->
                "$appName — 핵심 알림"
            ConclusionCategory.PUSH_PROMOTION ->
                "$appName — 프로모션/광고 알림"
            ConclusionCategory.PUSH_NOISE ->
                "$appName — 24시간 ${evidence.countLast24h}건, 소음 알림"
            ConclusionCategory.PUSH_NIGHT_DISTURB ->
                "$appName — 야간 방해 알림"
            else ->
                "$appName — 알림 수신"
        }
    }

    private fun buildReasons(
        evidence: PushEvidence,
        spamScore: Float,
    ): List<String> {
        val reasons = mutableListOf<String>()

        if (evidence.countLast24h >= 5) {
            reasons.add("최근 24시간 알림 ${evidence.countLast24h}건")
        }
        if (evidence.countLast7d >= 20) {
            reasons.add("최근 7일 알림 ${evidence.countLast7d}건")
        }
        if (evidence.promotionKeywordHits >= 1) {
            reasons.add("프로모션 키워드 ${evidence.promotionKeywordHits}건 감지")
        }
        if (evidence.isNightTime) {
            reasons.add("야간 시간대 수신 (22:00~07:00)")
        }
        if (evidence.interactionRate < 0.1f && evidence.countLast7d >= 10) {
            reasons.add("알림 확인율 ${(evidence.interactionRate * 100).toInt()}% (무시 비율 높음)")
        }

        return reasons.take(3)
    }

    private fun calculateConfidence(evidence: PushEvidence): Float {
        // 데이터가 많을수록 높은 신뢰도
        var confidence = 0.3f
        if (evidence.countLast7d >= 5) confidence += 0.2f
        if (evidence.countLast24h >= 1) confidence += 0.1f
        if (evidence.channelId != null) confidence += 0.1f
        if (evidence.interactionRate > 0f) confidence += 0.2f
        return confidence.coerceIn(0f, 1f)
    }
}
