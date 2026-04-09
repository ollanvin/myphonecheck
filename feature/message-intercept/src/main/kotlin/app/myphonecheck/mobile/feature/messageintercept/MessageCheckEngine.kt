package app.myphonecheck.mobile.feature.messageintercept

import app.myphonecheck.mobile.core.model.ActionRecommendation
import app.myphonecheck.mobile.core.model.ConclusionCategory
import app.myphonecheck.mobile.core.model.DecisionResult
import app.myphonecheck.mobile.core.model.MessageEvidence
import app.myphonecheck.mobile.core.model.RiskLevel

/**
 * MessageCheck 판단 엔진.
 *
 * SMS 수신 이벤트를 분석하여 DecisionResult를 생성합니다.
 *
 * 판단 기준:
 * 1. 발신자 — 저장된 연락처 여부
 * 2. 사칭 — 기관/은행/택배 사칭 키워드
 * 3. 피싱 — 의심 링크, 단축 URL, 개인정보 요청
 * 4. 금융 — 대출/투자/입금 유도
 * 5. 긴급성 — "즉시", "지금", "마감" 등 압박 표현
 *
 * 원칙:
 * - 메시지를 자동 삭제/차단하지 않음
 * - 사용자에게 위험 신호만 표시
 * - 최종 판단은 사용자
 */
object MessageCheckEngine {

    fun evaluate(evidence: MessageEvidence): DecisionResult {
        val scamScore = evidence.scamScore
        val category = determineCategory(evidence, scamScore)
        val riskLevel = determineRiskLevel(scamScore, category)
        val action = determineAction(riskLevel, category)
        val summary = buildSummary(evidence, category)
        val reasons = buildReasons(evidence)

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
        evidence: MessageEvidence,
        scamScore: Float,
    ): ConclusionCategory {
        return when {
            // 저장된 연락처 + 위험 신호 없음
            evidence.isLikelySafe ->
                ConclusionCategory.MSG_SAFE

            // 금융 사기 의심
            evidence.financialKeywordHits >= 2 && !evidence.isSavedContact ->
                ConclusionCategory.MSG_FINANCIAL_SCAM

            // 피싱 링크 감지
            evidence.hasShortenedUrl || (evidence.hasLinks && evidence.phishingKeywordHits >= 1) ->
                ConclusionCategory.MSG_PHISHING_LINK

            // 사칭 의심
            evidence.impersonationKeywordHits >= 1 && !evidence.isSavedContact ->
                ConclusionCategory.MSG_IMPERSONATION

            // 높은 사기 점수
            scamScore >= 0.5f ->
                ConclusionCategory.MSG_IMPERSONATION

            // 알 수 없는 발신자
            !evidence.isSavedContact ->
                ConclusionCategory.MSG_UNKNOWN_SENDER

            // 기본 안전
            else -> ConclusionCategory.MSG_SAFE
        }
    }

    private fun determineRiskLevel(
        scamScore: Float,
        category: ConclusionCategory,
    ): RiskLevel {
        return when (category) {
            ConclusionCategory.MSG_SAFE -> RiskLevel.LOW
            ConclusionCategory.MSG_FINANCIAL_SCAM -> RiskLevel.HIGH
            ConclusionCategory.MSG_PHISHING_LINK -> RiskLevel.HIGH
            ConclusionCategory.MSG_IMPERSONATION -> RiskLevel.MEDIUM
            ConclusionCategory.MSG_UNKNOWN_SENDER -> {
                if (scamScore >= 0.3f) RiskLevel.MEDIUM else RiskLevel.UNKNOWN
            }
            else -> RiskLevel.UNKNOWN
        }
    }

    private fun determineAction(
        riskLevel: RiskLevel,
        category: ConclusionCategory,
    ): ActionRecommendation {
        return when (category) {
            ConclusionCategory.MSG_SAFE -> ActionRecommendation.ANSWER
            ConclusionCategory.MSG_FINANCIAL_SCAM -> ActionRecommendation.BLOCK_REVIEW
            ConclusionCategory.MSG_PHISHING_LINK -> ActionRecommendation.REJECT
            ConclusionCategory.MSG_IMPERSONATION -> ActionRecommendation.ANSWER_WITH_CAUTION
            ConclusionCategory.MSG_UNKNOWN_SENDER -> ActionRecommendation.HOLD
            else -> ActionRecommendation.HOLD
        }
    }

    private fun buildSummary(
        evidence: MessageEvidence,
        category: ConclusionCategory,
    ): String {
        val senderLabel = if (evidence.isSavedContact) evidence.sender else "미저장 번호"
        return when (category) {
            ConclusionCategory.MSG_SAFE ->
                "$senderLabel — 안전한 메시지"
            ConclusionCategory.MSG_FINANCIAL_SCAM ->
                "$senderLabel — 금융 사기 의심 메시지"
            ConclusionCategory.MSG_PHISHING_LINK ->
                "$senderLabel — 의심 링크 포함 메시지"
            ConclusionCategory.MSG_IMPERSONATION ->
                "$senderLabel — 기관/서비스 사칭 의심"
            ConclusionCategory.MSG_UNKNOWN_SENDER ->
                "$senderLabel — 미확인 발신자"
            else ->
                "$senderLabel — 메시지 수신"
        }
    }

    private fun buildReasons(evidence: MessageEvidence): List<String> {
        val reasons = mutableListOf<String>()

        if (!evidence.isSavedContact) {
            reasons.add("저장되지 않은 발신자")
        }
        if (evidence.impersonationKeywordHits >= 1) {
            reasons.add("사칭 키워드 ${evidence.impersonationKeywordHits}건 감지")
        }
        if (evidence.phishingKeywordHits >= 1) {
            reasons.add("피싱 키워드 ${evidence.phishingKeywordHits}건 감지")
        }
        if (evidence.hasShortenedUrl) {
            reasons.add("단축 URL 포함 — 실제 주소 확인 불가")
        } else if (evidence.hasLinks) {
            reasons.add("링크 ${evidence.extractedUrls.size}개 포함")
        }
        if (evidence.financialKeywordHits >= 1) {
            reasons.add("금융 관련 키워드 ${evidence.financialKeywordHits}건 감지")
        }
        if (evidence.urgencyKeywordHits >= 1) {
            reasons.add("긴급성 유도 표현 감지")
        }

        return reasons.take(3)
    }

    private fun calculateConfidence(evidence: MessageEvidence): Float {
        var confidence = 0.3f
        if (evidence.body.length >= 20) confidence += 0.2f
        if (evidence.isSavedContact) confidence += 0.2f
        if (evidence.extractedUrls.isNotEmpty()) confidence += 0.1f
        if (evidence.impersonationKeywordHits + evidence.phishingKeywordHits >= 1) confidence += 0.2f
        return confidence.coerceIn(0f, 1f)
    }
}
