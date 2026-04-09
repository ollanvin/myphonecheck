package app.myphonecheck.mobile.feature.decisionengine.integration

import app.myphonecheck.mobile.core.model.ActionRecommendation
import app.myphonecheck.mobile.core.model.DecisionResult
import app.myphonecheck.mobile.core.model.DeviceEvidence
import app.myphonecheck.mobile.core.model.SearchEvidence
import app.myphonecheck.mobile.feature.decisionengine.DecisionEngine
import javax.inject.Inject

/**
 * Example integration showing how to use DecisionEngine in feature components.
 */
class DecisionEngineIntegrationExample @Inject constructor(
    private val decisionEngine: DecisionEngine,
) {

    /**
     * Full evaluation with both evidence sources.
     */
    fun evaluateIncomingCall(
        deviceEvidence: DeviceEvidence?,
        searchEvidence: SearchEvidence?,
    ): DecisionResult {
        return decisionEngine.evaluate(
            deviceEvidence = deviceEvidence,
            searchEvidence = searchEvidence,
        )
    }

    /**
     * Quick evaluation with device evidence only (fast path, ~0.8s).
     * Search evidence not yet available.
     */
    fun quickEvaluateDeviceOnly(
        deviceEvidence: DeviceEvidence,
    ): DecisionResult {
        return decisionEngine.evaluate(
            deviceEvidence = deviceEvidence,
            searchEvidence = null,
        )
    }

    /**
     * Re-evaluate with search results after initial device-only assessment.
     */
    fun updateWithSearchResults(
        deviceEvidence: DeviceEvidence,
        searchEvidence: SearchEvidence,
    ): DecisionResult {
        return decisionEngine.evaluate(
            deviceEvidence = deviceEvidence,
            searchEvidence = searchEvidence,
        )
    }

    /**
     * Determine call handling strategy from decision result.
     */
    fun getCallHandlingStrategy(result: DecisionResult): CallHandlingStrategy {
        return when (result.action) {
            ActionRecommendation.ANSWER -> CallHandlingStrategy.ALLOW_WITH_NOTIFICATION
            ActionRecommendation.ANSWER_WITH_CAUTION -> CallHandlingStrategy.ALLOW_WITH_NOTIFICATION
            ActionRecommendation.REJECT -> CallHandlingStrategy.SILENT_REJECT
            ActionRecommendation.BLOCK_REVIEW -> CallHandlingStrategy.BLOCK_AND_LOG
            ActionRecommendation.HOLD -> CallHandlingStrategy.NORMAL_HANDLING
        }
    }
}

/**
 * Call handling strategies that map to Android CallScreeningService responses.
 */
enum class CallHandlingStrategy {
    ALLOW_WITH_NOTIFICATION,
    SILENT_REJECT,
    BLOCK_AND_LOG,
    NORMAL_HANDLING,
}
