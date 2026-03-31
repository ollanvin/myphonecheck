package app.callcheck.mobile.feature.decisionengine

import app.callcheck.mobile.core.model.ActionRecommendation
import app.callcheck.mobile.core.model.ConclusionCategory
import app.callcheck.mobile.core.model.RiskLevel
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
        }
    }
}
