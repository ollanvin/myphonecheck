package app.callcheck.mobile.feature.decisionengine

import app.callcheck.mobile.core.model.DecisionResult
import app.callcheck.mobile.core.model.DeviceEvidence
import app.callcheck.mobile.core.model.SearchEvidence

/**
 * Core decision engine interface.
 *
 * Accepts device evidence + search evidence, returns a complete DecisionResult
 * with risk badge, conclusion category, action recommendation, summary, and reasons.
 *
 * Contract:
 * - Must be synchronous (no I/O inside)
 * - Must complete in < 50ms
 * - Must always return a valid DecisionResult (never throw)
 */
interface DecisionEngine {
    fun evaluate(
        deviceEvidence: DeviceEvidence?,
        searchEvidence: SearchEvidence?,
    ): DecisionResult
}
