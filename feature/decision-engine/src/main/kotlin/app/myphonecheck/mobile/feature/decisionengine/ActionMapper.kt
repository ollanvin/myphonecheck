package app.myphonecheck.mobile.feature.decisionengine

import app.myphonecheck.mobile.core.model.ActionRecommendation
import app.myphonecheck.mobile.core.model.ConclusionCategory
import app.myphonecheck.mobile.core.model.RiskLevel
import javax.inject.Inject

/**
 * Maps (ConclusionCategory, RiskLevel) → ActionRecommendation enum.
 *
 * PRD actions: ANSWER / ANSWER_WITH_CAUTION / REJECT / BLOCK_REVIEW / HOLD
 */
class ActionMapper @Inject constructor() {

    fun map(category: ConclusionCategory, riskLevel: RiskLevel): ActionRecommendation {
        return when (category) {
            ConclusionCategory.KNOWN_CONTACT -> ActionRecommendation.ANSWER

            ConclusionCategory.BUSINESS_LIKELY -> when (riskLevel) {
                RiskLevel.HIGH -> ActionRecommendation.ANSWER_WITH_CAUTION
                else -> ActionRecommendation.ANSWER
            }

            ConclusionCategory.DELIVERY_LIKELY -> ActionRecommendation.ANSWER_WITH_CAUTION

            ConclusionCategory.INSTITUTION_LIKELY -> ActionRecommendation.ANSWER_WITH_CAUTION

            ConclusionCategory.SALES_SPAM_SUSPECTED -> when (riskLevel) {
                RiskLevel.HIGH -> ActionRecommendation.BLOCK_REVIEW
                else -> ActionRecommendation.REJECT
            }

            ConclusionCategory.SCAM_RISK_HIGH -> ActionRecommendation.BLOCK_REVIEW

            ConclusionCategory.INSUFFICIENT_EVIDENCE -> ActionRecommendation.HOLD

            // ═══════════════════════════════════════
            // PushCheck 엔진
            // ═══════════════════════════════════════
            ConclusionCategory.PUSH_CRITICAL -> ActionRecommendation.ANSWER
            ConclusionCategory.PUSH_PROMOTION -> ActionRecommendation.ANSWER_WITH_CAUTION
            ConclusionCategory.PUSH_NOISE -> ActionRecommendation.REJECT
            ConclusionCategory.PUSH_NIGHT_DISTURB -> ActionRecommendation.REJECT

            // ═══════════════════════════════════════
            // MessageCheck 엔진
            // ═══════════════════════════════════════
            ConclusionCategory.MSG_SAFE -> ActionRecommendation.ANSWER
            ConclusionCategory.MSG_IMPERSONATION -> ActionRecommendation.BLOCK_REVIEW
            ConclusionCategory.MSG_PHISHING_LINK -> ActionRecommendation.BLOCK_REVIEW
            ConclusionCategory.MSG_FINANCIAL_SCAM -> ActionRecommendation.BLOCK_REVIEW
            ConclusionCategory.MSG_UNKNOWN_SENDER -> ActionRecommendation.ANSWER_WITH_CAUTION

            // ═══════════════════════════════════════
            // PrivacyCheck 엔진
            // ═══════════════════════════════════════
            ConclusionCategory.PRIV_NORMAL -> ActionRecommendation.ANSWER
            ConclusionCategory.PRIV_FIRST_ACCESS -> ActionRecommendation.ANSWER_WITH_CAUTION
            ConclusionCategory.PRIV_BACKGROUND -> ActionRecommendation.BLOCK_REVIEW
            ConclusionCategory.PRIV_SUSPICIOUS -> ActionRecommendation.REJECT
        }
    }
}
