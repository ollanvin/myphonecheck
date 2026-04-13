package app.myphonecheck.mobile.core.util

import app.myphonecheck.mobile.core.model.ActionRecommendation
import app.myphonecheck.mobile.core.model.ConclusionCategory
import app.myphonecheck.mobile.core.model.DecisionResult
import app.myphonecheck.mobile.core.model.RiskLevel

/**
 * UI-only helpers: map [DecisionResult] + evidence into structured reasoning lines.
 * Does not alter scoring or engine output — presentation for overlays and cards.
 */
object DecisionReasoningFormatter {

    enum class Lang { KO, EN }

    fun riskTriLabel(level: RiskLevel, lang: Lang): String = when (lang) {
        Lang.KO -> when (level) {
            RiskLevel.LOW -> "SAFE"
            RiskLevel.MEDIUM -> "WARNING"
            RiskLevel.HIGH -> "DANGER"
            RiskLevel.UNKNOWN -> "불명"
        }
        Lang.EN -> when (level) {
            RiskLevel.LOW -> "SAFE"
            RiskLevel.MEDIUM -> "WARNING"
            RiskLevel.HIGH -> "DANGER"
            RiskLevel.UNKNOWN -> "UNKNOWN"
        }
    }

    fun confidencePercent(confidence: Float): Int =
        (confidence * 100f).toInt().coerceIn(0, 100)

    /**
     * 허브(DB)에 저장된 엔진 스냅샷을 [DecisionResult]로 복원.
     * UI는 이 결과만 넘겨 [reportHistoryLine] 등 단일 경로로 렌더링한다.
     */
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

    /** 신고·패턴·행동·검색 네 줄 — 오버레이/카드/허브가 동일 순서로 사용 */
    fun sectionBodiesInOrder(result: DecisionResult, lang: Lang): List<String> = listOf(
        reportHistoryLine(result, lang),
        patternAnalysisLine(result, lang),
        userBehaviorLine(result, lang),
        searchSummaryLine(result, lang),
    )

    /** PrivacyCollector가 저장한 줄바꿈 구분 이유 → 표시용 줄 목록 */
    fun privacyStoredReasonLines(storedMultiline: String?): List<String> =
        storedMultiline
            ?.split("\n")
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            .orEmpty()

    /**
     * Privacy 이상 사유 표시: 줄이 있으면 bullet 결합, 없으면 [emptyFallback] 그대로.
     * 이유 **문구 생성**은 Collector 책임 — 여기서는 포맷만.
     */
    fun privacyAnomalyDisplayText(storedMultiline: String?, emptyFallback: String): String {
        val lines = privacyStoredReasonLines(storedMultiline)
        if (lines.isEmpty()) return emptyFallback
        return lines.joinToString("\n") { "· $it" }
    }

    /** 신고 이력 — 검색·커뮤니티에서 드러난 신고/사기 신호 요약 */
    fun reportHistoryLine(result: DecisionResult, lang: Lang): String {
        val search = result.searchEvidence
        if (search == null || search.isEmpty) {
            return when (lang) {
                Lang.KO -> "신고 이력: 웹·커뮤니티 신호 없음 (온디바이스·기록만 반영)"
                Lang.EN -> "Reports: no web/community signals (on-device only)"
            }
        }
        when {
            search.hasScamSignal -> return when (lang) {
                Lang.KO -> "신고 이력: 사기·피싱·스팸 신고 관련 키워드 다수"
                Lang.EN -> "Reports: many scam/phishing/spam-related mentions"
            }
            search.hasSpamSignal -> return when (lang) {
                Lang.KO -> "신고 이력: 광고·영업·스팸 관련 언급"
                Lang.EN -> "Reports: telemarketing/spam-related mentions"
            }
        }
        val spamSignals = search.signalSummaries.filter {
            (it.signalType ?: "").contains("SPAM", ignoreCase = true) ||
                (it.signalType ?: "").contains("REPORT", ignoreCase = true)
        }
        if (spamSignals.isNotEmpty()) {
            val t = spamSignals.first().signalDescription
            return when (lang) {
                Lang.KO -> "신고 이력: $t"
                Lang.EN -> "Reports: $t"
            }
        }
        val first = search.signalSummaries.firstOrNull()?.signalDescription
        return if (first != null) {
            when (lang) {
                Lang.KO -> "신고 이력: $first"
                Lang.EN -> "Reports: $first"
            }
        } else {
            when (lang) {
                Lang.KO -> "신고 이력: 검색 스니펫·키워드에서 위험 신호 확인"
                Lang.EN -> "Reports: risk cues from search snippets/keywords"
            }
        }
    }

    /** 패턴 분석 결과 — 통화 길이·검색 키워드·추세 */
    fun patternAnalysisLine(result: DecisionResult, lang: Lang): String {
        val dev = result.deviceEvidence
        val search = result.searchEvidence
        val parts = mutableListOf<String>()

        if (dev != null) {
            if (dev.shortCallCount > 0) {
                parts += if (lang == Lang.KO) "짧은 통화(<10초) ${dev.shortCallCount}회" else "${dev.shortCallCount} short calls (<10s)"
            }
            if (dev.longCallCount > 0) {
                parts += if (lang == Lang.KO) "긴 통화(>60초) ${dev.longCallCount}회" else "${dev.longCallCount} long calls (>60s)"
            }
            if (dev.rejectedCount >= 2) {
                parts += if (lang == Lang.KO) "거절 ${dev.rejectedCount}회" else "${dev.rejectedCount}× rejected"
            }
        }
        if (search != null && !search.isEmpty) {
            if (search.keywordClusters.isNotEmpty()) {
                val c = search.keywordClusters.take(4).joinToString(", ")
                parts += if (lang == Lang.KO) "검색 키워드 클러스터: $c" else "Search clusters: $c"
            }
            parts += if (lang == Lang.KO) "검색 추세: ${search.searchTrend.name}" else "Search trend: ${search.searchTrend.name}"
        }
        if (parts.isEmpty()) {
            return when (lang) {
                Lang.KO -> "패턴 분석: 특이 통화·검색 패턴 없음"
                Lang.EN -> "Patterns: no notable call/search pattern"
            }
        }
        return when (lang) {
            Lang.KO -> "패턴 분석 결과: ${parts.joinToString(" · ")}"
            Lang.EN -> "Pattern analysis: ${parts.joinToString(" · ")}"
        }
    }

    /** 사용자 행동 이력 — 기기에 남은 수신·발신·거절·부재중 */
    fun userBehaviorLine(result: DecisionResult, lang: Lang): String {
        val dev = result.deviceEvidence
        if (dev == null || !dev.hasAnyHistory) {
            return when (lang) {
                Lang.KO -> "사용자 행동 이력: 이 번호에 대한 통화·문자 기록 없음"
                Lang.EN -> "User behavior: no call/SMS history for this number"
            }
        }
        val parts = mutableListOf<String>()
        if (dev.outgoingCount > 0) parts += if (lang == Lang.KO) "발신 ${dev.outgoingCount}회" else "outgoing ${dev.outgoingCount}"
        if (dev.incomingCount > 0) parts += if (lang == Lang.KO) "수신 ${dev.incomingCount}회" else "incoming ${dev.incomingCount}"
        if (dev.answeredCount > 0) parts += if (lang == Lang.KO) "수신(응답) ${dev.answeredCount}회" else "answered ${dev.answeredCount}"
        if (dev.rejectedCount > 0) parts += if (lang == Lang.KO) "거절 ${dev.rejectedCount}회" else "rejected ${dev.rejectedCount}"
        if (dev.missedCount > 0) parts += if (lang == Lang.KO) "부재중 ${dev.missedCount}회" else "missed ${dev.missedCount}"
        if (dev.smsExists) parts += if (lang == Lang.KO) "문자 이력 있음" else "SMS present"
        dev.recentDaysContact?.let { d ->
            parts += if (lang == Lang.KO) "마지막 접촉 약 ${d}일 전" else "last contact ~${d}d ago"
        }
        return when (lang) {
            Lang.KO -> "사용자 행동 이력: ${parts.joinToString(", ")}"
            Lang.EN -> "User behavior: ${parts.joinToString(", ")}"
        }
    }

    /** 검색 결과 요약 — 신호·스니펫 */
    fun searchSummaryLine(result: DecisionResult, lang: Lang): String {
        val search = result.searchEvidence
        if (search == null || search.isEmpty) {
            return when (lang) {
                Lang.KO -> "검색 결과 요약: 없음 (검색·보강 미실시 또는 결과 없음)"
                Lang.EN -> "Search summary: none (skipped or empty)"
            }
        }
        val sig = search.signalSummaries.map { it.signalDescription }.filter { it.isNotBlank() }
        if (sig.isNotEmpty()) {
            return when (lang) {
                Lang.KO -> "검색 결과 요약: ${sig.take(4).joinToString(" / ")}"
                Lang.EN -> "Search summary: ${sig.take(4).joinToString(" / ")}"
            }
        }
        val snip = search.topSnippets.firstOrNull()?.trim()
        if (snip != null) {
            val short = if (snip.length > 120) snip.take(120) + "…" else snip
            return when (lang) {
                Lang.KO -> "검색 결과 요약: $short"
                Lang.EN -> "Search summary: $short"
            }
        }
        val clusters = search.keywordClusters.take(3).joinToString(", ")
        return when (lang) {
            Lang.KO -> "검색 결과 요약: 키워드 $clusters"
            Lang.EN -> "Search summary: keywords $clusters"
        }
    }

    fun judgmentBasisMultiline(result: DecisionResult, lang: Lang): String {
        val lines = mutableListOf<String>()
        lines += sectionBodiesInOrder(result, lang)
        if (result.reasons.isNotEmpty()) {
            lines += ""
            lines += when (lang) {
                Lang.KO -> "엔진 요약 근거:"
                Lang.EN -> "Engine reasons:"
            }
            result.reasons.forEach { lines += "· $it" }
        }
        return lines.joinToString("\n")
    }

    fun useGlobalDataBanner(result: DecisionResult): Boolean {
        val s = result.searchEvidence
        return s != null && !s.isEmpty
    }
}
