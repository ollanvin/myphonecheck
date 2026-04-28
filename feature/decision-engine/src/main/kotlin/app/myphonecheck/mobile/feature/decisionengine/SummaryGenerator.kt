package app.myphonecheck.mobile.feature.decisionengine

import android.content.Context
import app.myphonecheck.mobile.core.model.ConclusionCategory
import app.myphonecheck.mobile.core.model.DeviceEvidence
import app.myphonecheck.mobile.core.model.SearchEvidence
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Generates human-readable summary and up to 3 supporting reasons.
 *
 * Summary: [ConclusionCategory.summaryResId] via resources (§9-1).
 * Reasons: built from actual evidence fields — device history, search signals, recency.
 */
class SummaryGenerator @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val res get() = context.resources

    /**
     * One-line summary from the category string resource.
     */
    fun generateSummary(category: ConclusionCategory): String =
        res.getString(category.summaryResId)

    /**
     * Up to 3 reasons explaining the decision, sourced from actual evidence.
     */
    fun generateReasons(
        category: ConclusionCategory,
        deviceEvidence: DeviceEvidence?,
        searchEvidence: SearchEvidence?,
    ): List<String> {
        val reasons = mutableListOf<String>()

        addDeviceReason(reasons, deviceEvidence)
        addSearchReason(reasons, searchEvidence)
        addPatternReason(reasons, deviceEvidence)

        return reasons.take(3)
    }

    private fun addDeviceReason(
        reasons: MutableList<String>,
        device: DeviceEvidence?,
    ) {
        if (device == null) {
            reasons.add(res.getString(R.string.engine_reason_no_device_history))
            return
        }

        if (device.isSavedContact) {
            val name = device.contactName
            if (name != null) {
                reasons.add(res.getString(R.string.engine_reason_saved_contact_named, name))
            } else {
                reasons.add(res.getString(R.string.engine_reason_saved_contact))
            }
            return
        }

        if (!device.hasAnyHistory) {
            reasons.add(res.getString(R.string.engine_reason_unsaved_no_history))
            return
        }

        val parts = mutableListOf<String>()
        if (device.outgoingCount > 0) {
            parts.add(res.getString(R.string.engine_reason_outgoing_fmt, device.outgoingCount))
        }
        if (device.incomingCount > 0) {
            parts.add(res.getString(R.string.engine_reason_incoming_fmt, device.incomingCount))
        }
        if (device.missedCount > 0) {
            parts.add(res.getString(R.string.engine_reason_missed_fmt, device.missedCount))
        }
        if (device.connectedCount > 0 && device.avgDurationSec > 0) {
            parts.add(res.getString(R.string.engine_reason_avg_duration_fmt, device.avgDurationSec))
        }

        if (parts.isNotEmpty()) {
            reasons.add(parts.joinToString(", "))
        }
    }

    private fun addSearchReason(
        reasons: MutableList<String>,
        search: SearchEvidence?,
    ) {
        if (search == null || search.isEmpty) return

        when {
            search.hasScamSignal -> reasons.add(res.getString(R.string.engine_reason_search_scam))
            search.hasSpamSignal -> reasons.add(res.getString(R.string.engine_reason_search_spam))
            search.hasDeliverySignal -> reasons.add(res.getString(R.string.engine_reason_search_delivery))
            search.hasInstitutionSignal -> reasons.add(res.getString(R.string.engine_reason_search_institution))
            search.hasBusinessSignal -> reasons.add(res.getString(R.string.engine_reason_search_business))
            search.repeatedEntities.isNotEmpty() -> {
                val entity = search.repeatedEntities.first()
                reasons.add(res.getString(R.string.engine_reason_search_entity_repeat, entity))
            }
        }
    }

    private fun addPatternReason(
        reasons: MutableList<String>,
        device: DeviceEvidence?,
    ) {
        if (device == null) return

        val days = device.recentDaysContact
        if (days != null && days <= 7) {
            reasons.add(res.getString(R.string.engine_reason_recent_contact_days, days))
            return
        }

        if (device.shortCallCount >= 3 && device.longCallCount == 0) {
            reasons.add(res.getString(R.string.engine_reason_short_calls_only, device.shortCallCount))
            return
        }

        if (device.userInitiated && device.connectedCount > 0) {
            reasons.add(res.getString(R.string.engine_reason_user_initiated))
            return
        }

        if (device.smsExists) {
            reasons.add(res.getString(R.string.engine_reason_sms_history))
        }
    }
}
