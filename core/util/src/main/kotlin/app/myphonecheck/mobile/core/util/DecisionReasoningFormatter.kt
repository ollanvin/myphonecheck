package app.myphonecheck.mobile.core.util

import android.content.res.Resources
import app.myphonecheck.mobile.core.model.ActionRecommendation
import app.myphonecheck.mobile.core.model.ConclusionCategory
import app.myphonecheck.mobile.core.model.DecisionResult
import app.myphonecheck.mobile.core.model.RiskLevel

/**
 * UI-only helpers: map [DecisionResult] + evidence into structured reasoning lines.
 * Does not alter scoring or engine output — presentation for overlays and cards.
 *
 * Copy resolves via [Resources.getString] only (§9-1 / §9-4).
 */
object DecisionReasoningFormatter {

    fun riskTriLabel(resources: Resources, level: RiskLevel): String {
        val id = when (level) {
            RiskLevel.LOW -> R.string.reason_risk_tri_safe
            RiskLevel.MEDIUM -> R.string.reason_risk_tri_warning
            RiskLevel.HIGH -> R.string.reason_risk_tri_danger
            RiskLevel.UNKNOWN -> R.string.reason_risk_tri_unknown
        }
        return resources.getString(id)
    }

    fun confidencePercent(confidence: Float): Int =
        (confidence * 100f).toInt().coerceIn(0, 100)

    fun parseDecisionResultFromHubSnapshot(
        riskLevelName: String,
        categoryName: String,
        actionName: String,
        confidence: Float,
        summary: String,
        reasonsPipeSeparated: String?,
    ): DecisionResult? = try {
        val reasonList = reasonsPipeSeparated
            ?.split('|')
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            .orEmpty()
        DecisionResult(
            riskLevel = RiskLevel.valueOf(riskLevelName),
            category = ConclusionCategory.valueOf(categoryName),
            action = ActionRecommendation.valueOf(actionName),
            confidence = confidence,
            summary = summary,
            reasons = reasonList,
            deviceEvidence = null,
            searchEvidence = null,
        )
    } catch (_: IllegalArgumentException) {
        null
    }

    fun sectionBodiesInOrder(resources: Resources, result: DecisionResult): List<String> = listOf(
        reportHistoryLine(resources, result),
        patternAnalysisLine(resources, result),
        userBehaviorLine(resources, result),
        searchSummaryLine(resources, result),
    )

    fun privacyStoredReasonLines(storedMultiline: String?): List<String> =
        storedMultiline
            ?.split("\n")
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            .orEmpty()

    fun privacyAnomalyDisplayText(storedMultiline: String?, emptyFallback: String): String {
        val lines = privacyStoredReasonLines(storedMultiline)
        if (lines.isEmpty()) return emptyFallback
        return lines.joinToString("\n") { "· $it" }
    }

    fun reportHistoryLine(resources: Resources, result: DecisionResult): String {
        val search = result.searchEvidence
        if (search == null || search.isEmpty) {
            return resources.getString(R.string.reason_report_none_ondevice)
        }
        when {
            search.hasScamSignal ->
                return resources.getString(R.string.reason_report_scam)
            search.hasSpamSignal ->
                return resources.getString(R.string.reason_report_spam_mentions)
        }
        val spamSignals = search.signalSummaries.filter {
            (it.signalType ?: "").contains("SPAM", ignoreCase = true) ||
                (it.signalType ?: "").contains("REPORT", ignoreCase = true)
        }
        if (spamSignals.isNotEmpty()) {
            val t = spamSignals.first().signalDescription
            return resources.getString(R.string.reason_report_detail, t)
        }
        val first = search.signalSummaries.firstOrNull()?.signalDescription
        return if (first != null) {
            resources.getString(R.string.reason_report_detail, first)
        } else {
            resources.getString(R.string.reason_report_snippets_risk)
        }
    }

    fun patternAnalysisLine(resources: Resources, result: DecisionResult): String {
        val dev = result.deviceEvidence
        val search = result.searchEvidence
        val parts = mutableListOf<String>()

        if (dev != null) {
            if (dev.shortCallCount > 0) {
                parts += resources.getString(R.string.reason_pattern_short_calls, dev.shortCallCount)
            }
            if (dev.longCallCount > 0) {
                parts += resources.getString(R.string.reason_pattern_long_calls, dev.longCallCount)
            }
            if (dev.rejectedCount >= 2) {
                parts += resources.getString(R.string.reason_pattern_rejected, dev.rejectedCount)
            }
        }
        if (search != null && !search.isEmpty) {
            if (search.keywordClusters.isNotEmpty()) {
                val c = search.keywordClusters.take(4).joinToString(", ")
                parts += resources.getString(R.string.reason_pattern_clusters, c)
            }
            parts += resources.getString(R.string.reason_pattern_trend, search.searchTrend.name)
        }
        if (parts.isEmpty()) {
            return resources.getString(R.string.reason_pattern_none)
        }
        return resources.getString(R.string.reason_pattern_line, parts.joinToString(" · "))
    }

    fun userBehaviorLine(resources: Resources, result: DecisionResult): String {
        val dev = result.deviceEvidence
        if (dev == null || !dev.hasAnyHistory) {
            return resources.getString(R.string.reason_behavior_none)
        }
        val parts = mutableListOf<String>()
        if (dev.outgoingCount > 0) parts += resources.getString(R.string.reason_behavior_outgoing, dev.outgoingCount)
        if (dev.incomingCount > 0) parts += resources.getString(R.string.reason_behavior_incoming, dev.incomingCount)
        if (dev.answeredCount > 0) parts += resources.getString(R.string.reason_behavior_answered, dev.answeredCount)
        if (dev.rejectedCount > 0) parts += resources.getString(R.string.reason_behavior_rejected, dev.rejectedCount)
        if (dev.missedCount > 0) parts += resources.getString(R.string.reason_behavior_missed, dev.missedCount)
        if (dev.smsExists) parts += resources.getString(R.string.reason_behavior_sms)
        dev.recentDaysContact?.let { d ->
            parts += resources.getString(R.string.reason_behavior_last_contact, d)
        }
        return resources.getString(R.string.reason_behavior_line, parts.joinToString(", "))
    }

    fun searchSummaryLine(resources: Resources, result: DecisionResult): String {
        val search = result.searchEvidence
        if (search == null || search.isEmpty) {
            return resources.getString(R.string.reason_search_none)
        }
        val sig = search.signalSummaries.map { it.signalDescription }.filter { it.isNotBlank() }
        if (sig.isNotEmpty()) {
            val joined = sig.take(4).joinToString(" / ")
            return resources.getString(R.string.reason_search_signals_line, joined)
        }
        val snip = search.topSnippets.firstOrNull()?.trim()
        if (snip != null) {
            val short = if (snip.length > 120) snip.take(120) + "\u2026" else snip
            return resources.getString(R.string.reason_search_snippet_line, short)
        }
        val clusters = search.keywordClusters.take(3).joinToString(", ")
        return resources.getString(R.string.reason_search_keywords_line, clusters)
    }

    fun judgmentBasisMultiline(resources: Resources, result: DecisionResult): String {
        val lines = mutableListOf<String>()
        lines += sectionBodiesInOrder(resources, result)
        if (result.reasons.isNotEmpty()) {
            lines += ""
            lines += resources.getString(R.string.reason_engine_reasons_title)
            result.reasons.forEach { lines += "· $it" }
        }
        return lines.joinToString("\n")
    }

    fun useGlobalDataBanner(result: DecisionResult): Boolean {
        val s = result.searchEvidence
        return s != null && !s.isEmpty
    }
}
