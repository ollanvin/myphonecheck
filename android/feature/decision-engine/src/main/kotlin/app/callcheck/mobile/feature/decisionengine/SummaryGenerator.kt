package app.callcheck.mobile.feature.decisionengine

import app.callcheck.mobile.core.model.ConclusionCategory
import app.callcheck.mobile.core.model.DeviceEvidence
import app.callcheck.mobile.core.model.SearchEvidence
import javax.inject.Inject

/**
 * Generates human-readable summary and up to 3 supporting reasons.
 *
 * Summary: uses ConclusionCategory's built-in summaryKo/summaryEn.
 * Reasons: built from actual evidence fields — device history, search signals, recency.
 */
class SummaryGenerator @Inject constructor() {

    /**
     * One-line summary from the category's built-in display string.
     */
    fun generateSummary(
        category: ConclusionCategory,
        language: String = "ko",
    ): String {
        return if (language == "ko") category.summaryKo else category.summaryEn
    }

    /**
     * Up to 3 reasons explaining the decision, sourced from actual evidence.
     */
    fun generateReasons(
        category: ConclusionCategory,
        deviceEvidence: DeviceEvidence?,
        searchEvidence: SearchEvidence?,
        language: String = "ko",
    ): List<String> {
        val reasons = mutableListOf<String>()

        // === Reason 1: Device history ===
        addDeviceReason(reasons, deviceEvidence, language)

        // === Reason 2: Search evidence ===
        addSearchReason(reasons, searchEvidence, language)

        // === Reason 3: Recency / pattern ===
        addPatternReason(reasons, deviceEvidence, language)

        return reasons.take(3)
    }

    private fun addDeviceReason(
        reasons: MutableList<String>,
        device: DeviceEvidence?,
        lang: String,
    ) {
        if (device == null) {
            reasons.add(if (lang == "ko") "기기 기록 없음" else "No device history")
            return
        }

        if (device.isSavedContact) {
            val name = device.contactName
            if (name != null) {
                reasons.add(
                    if (lang == "ko") "저장된 연락처: $name"
                    else "Saved contact: $name"
                )
            } else {
                reasons.add(
                    if (lang == "ko") "저장된 연락처"
                    else "Saved contact"
                )
            }
            return
        }

        if (!device.hasAnyHistory) {
            reasons.add(
                if (lang == "ko") "저장되지 않은 번호, 기기 기록 없음"
                else "Unsaved number, no device history"
            )
            return
        }

        // Build granular call summary
        val parts = mutableListOf<String>()
        if (device.outgoingCount > 0) {
            parts.add(if (lang == "ko") "발신 ${device.outgoingCount}회" else "outgoing ${device.outgoingCount}")
        }
        if (device.incomingCount > 0) {
            parts.add(if (lang == "ko") "수신 ${device.incomingCount}회" else "incoming ${device.incomingCount}")
        }
        if (device.missedCount > 0) {
            parts.add(if (lang == "ko") "부재중 ${device.missedCount}회" else "missed ${device.missedCount}")
        }
        if (device.connectedCount > 0 && device.avgDurationSec > 0) {
            parts.add(
                if (lang == "ko") "평균통화 ${device.avgDurationSec}초"
                else "avg ${device.avgDurationSec}s"
            )
        }

        if (parts.isNotEmpty()) {
            reasons.add(parts.joinToString(", "))
        }
    }

    private fun addSearchReason(
        reasons: MutableList<String>,
        search: SearchEvidence?,
        lang: String,
    ) {
        if (search == null || search.isEmpty) return

        // Prioritize the strongest signal
        when {
            search.hasScamSignal -> reasons.add(
                if (lang == "ko") "검색 결과: 사기/피싱 관련 키워드 다수"
                else "Search: scam/phishing keywords found"
            )
            search.hasSpamSignal -> reasons.add(
                if (lang == "ko") "검색 결과: 광고/영업 관련 키워드 다수"
                else "Search: spam/telemarketing keywords found"
            )
            search.hasDeliverySignal -> reasons.add(
                if (lang == "ko") "검색 결과: 택배/배송 관련 키워드 발견"
                else "Search: delivery/courier keywords found"
            )
            search.hasInstitutionSignal -> reasons.add(
                if (lang == "ko") "검색 결과: 기관/병원 관련 키워드 발견"
                else "Search: institution keywords found"
            )
            search.hasBusinessSignal -> reasons.add(
                if (lang == "ko") "검색 결과: 기업/업무 관련 키워드 발견"
                else "Search: business keywords found"
            )
            search.repeatedEntities.isNotEmpty() -> {
                val entity = search.repeatedEntities.first()
                reasons.add(
                    if (lang == "ko") "검색 결과: \"$entity\" 반복 노출"
                    else "Search: \"$entity\" repeatedly mentioned"
                )
            }
        }
    }

    private fun addPatternReason(
        reasons: MutableList<String>,
        device: DeviceEvidence?,
        lang: String,
    ) {
        if (device == null) return

        // Recency
        val days = device.recentDaysContact
        if (days != null && days <= 7) {
            reasons.add(
                if (lang == "ko") "최근 ${days}일 내 통화 이력"
                else "Contact within last $days days"
            )
            return
        }

        // Short call pattern (possible robo-call indicator)
        if (device.shortCallCount >= 3 && device.longCallCount == 0) {
            reasons.add(
                if (lang == "ko") "짧은 통화(10초 미만)만 ${device.shortCallCount}회"
                else "${device.shortCallCount} short calls (<10s), no long calls"
            )
            return
        }

        // User-initiated contact (strong trust signal)
        if (device.userInitiated && device.connectedCount > 0) {
            reasons.add(
                if (lang == "ko") "사용자가 먼저 발신한 이력 있음"
                else "User initiated previous calls"
            )
            return
        }

        // SMS exists
        if (device.smsExists) {
            reasons.add(
                if (lang == "ko") "문자 이력 있음"
                else "SMS history exists"
            )
        }
    }
}
