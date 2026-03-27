package app.callcheck.mobile.feature.decisionui.preview

import app.callcheck.mobile.core.model.ActionRecommendation
import app.callcheck.mobile.core.model.ConclusionCategory
import app.callcheck.mobile.core.model.DecisionResult
import app.callcheck.mobile.core.model.DeviceEvidence
import app.callcheck.mobile.core.model.RiskLevel
import app.callcheck.mobile.core.model.SearchEvidence
import app.callcheck.mobile.core.model.SearchTrend

/**
 * Preview data for Composable @Preview functions.
 *
 * ActionRecommendation: SAFE_LIKELY / CAUTION / RISK_HIGH / UNKNOWN (판단 정보형)
 */
object PreviewData {

    // ====== SCENARIO 1: Known Contact (GREEN - Low) ======
    val knownDevice = DeviceEvidence(
        isSavedContact = true,
        contactName = "CJ 대한통운",
        outgoingCount = 3,
        lastOutgoingAt = 1710110400000L,
        incomingCount = 12,
        lastIncomingAt = 1710110400000L,
        answeredCount = 10,
        rejectedCount = 0,
        lastRejectedAt = null,
        missedCount = 2,
        lastMissedAt = 1709500800000L,
        connectedCount = 10,
        lastConnectedAt = 1710110400000L,
        totalDurationSec = 600,
        avgDurationSec = 60,
        shortCallCount = 2,
        longCallCount = 3,
        recentDaysContact = 1,
        smsExists = true,
        smsLastAt = 1710110400000L,
        localTag = null,
        localMemo = null,
    )

    val knownResult = DecisionResult(
        riskLevel = RiskLevel.LOW,
        category = ConclusionCategory.KNOWN_CONTACT,
        action = ActionRecommendation.SAFE_LIKELY,
        confidence = 0.95f,
        summary = "저장된 연락처",
        reasons = listOf("저장된 연락처: CJ 대한통운", "발신 3회, 수신 12회, 평균통화 60초", "최근 1일 내 통화 이력"),
        deviceEvidence = knownDevice,
        searchEvidence = null,
    )

    // ====== SCENARIO 2: Delivery Likely (YELLOW - Low) ======
    val deliveryDevice = DeviceEvidence(
        isSavedContact = false,
        contactName = null,
        outgoingCount = 0,
        lastOutgoingAt = null,
        incomingCount = 2,
        lastIncomingAt = 1709932800000L,
        answeredCount = 1,
        rejectedCount = 0,
        lastRejectedAt = null,
        missedCount = 1,
        lastMissedAt = 1709500800000L,
        connectedCount = 1,
        lastConnectedAt = 1709932800000L,
        totalDurationSec = 45,
        avgDurationSec = 45,
        shortCallCount = 0,
        longCallCount = 0,
        recentDaysContact = 5,
        smsExists = true,
        smsLastAt = 1709932800000L,
        localTag = null,
        localMemo = null,
    )

    val deliverySearch = SearchEvidence(
        recent30dSearchIntensity = 15,
        recent90dSearchIntensity = 30,
        searchTrend = SearchTrend.STABLE,
        keywordClusters = listOf("택배", "배송", "로젠택배"),
        repeatedEntities = listOf("로젠택배"),
        sourceTypes = listOf("COMMUNITY", "BLOG"),
        topSnippets = listOf("로젠택배 배송 문의 전화번호"),
    )

    val deliveryResult = DecisionResult(
        riskLevel = RiskLevel.LOW,
        category = ConclusionCategory.DELIVERY_LIKELY,
        action = ActionRecommendation.CAUTION,
        confidence = 0.82f,
        summary = "택배/배송 가능성 높음",
        reasons = listOf("수신 2회, 평균통화 45초", "검색 결과: 택배/배송 관련 키워드 발견", "최근 5일 내 통화 이력"),
        deviceEvidence = deliveryDevice,
        searchEvidence = deliverySearch,
    )

    // ====== SCENARIO 3: Spam Suspected (ORANGE - Medium) ======
    val spamDevice = DeviceEvidence(
        isSavedContact = false,
        contactName = null,
        outgoingCount = 0,
        lastOutgoingAt = null,
        incomingCount = 5,
        lastIncomingAt = 1710110400000L,
        answeredCount = 1,
        rejectedCount = 2,
        lastRejectedAt = 1710024000000L,
        missedCount = 2,
        lastMissedAt = 1709937600000L,
        connectedCount = 1,
        lastConnectedAt = 1709500800000L,
        totalDurationSec = 8,
        avgDurationSec = 8,
        shortCallCount = 1,
        longCallCount = 0,
        recentDaysContact = 1,
        smsExists = false,
        smsLastAt = null,
        localTag = null,
        localMemo = null,
    )

    val spamSearch = SearchEvidence(
        recent30dSearchIntensity = 80,
        recent90dSearchIntensity = 120,
        searchTrend = SearchTrend.INCREASING,
        keywordClusters = listOf("광고", "영업", "보험"),
        repeatedEntities = listOf("보험사"),
        sourceTypes = listOf("SPAM_REPORT", "COMMUNITY"),
        topSnippets = listOf("보험사 스팸 전화로 신고됨"),
    )

    val spamResult = DecisionResult(
        riskLevel = RiskLevel.MEDIUM,
        category = ConclusionCategory.SALES_SPAM_SUSPECTED,
        action = ActionRecommendation.CAUTION,
        confidence = 0.78f,
        summary = "광고/영업 의심",
        reasons = listOf("수신 5회, 평균통화 8초", "검색 결과: 광고/영업 관련 키워드 다수", "짧은 통화(10초 미만)만 1회"),
        deviceEvidence = spamDevice,
        searchEvidence = spamSearch,
    )

    // ====== SCENARIO 4: Scam Risk High (RED - High) ======
    val scamDevice = DeviceEvidence(
        isSavedContact = false,
        contactName = null,
        outgoingCount = 0,
        lastOutgoingAt = null,
        incomingCount = 1,
        lastIncomingAt = null,
        answeredCount = 0,
        rejectedCount = 0,
        lastRejectedAt = null,
        missedCount = 1,
        lastMissedAt = 1710110400000L,
        connectedCount = 0,
        lastConnectedAt = null,
        totalDurationSec = 0,
        avgDurationSec = 0,
        shortCallCount = 0,
        longCallCount = 0,
        recentDaysContact = null,
        smsExists = false,
        smsLastAt = null,
        localTag = null,
        localMemo = null,
    )

    val scamSearch = SearchEvidence(
        recent30dSearchIntensity = 200,
        recent90dSearchIntensity = 350,
        searchTrend = SearchTrend.INCREASING,
        keywordClusters = listOf("사기", "보이스피싱", "대출"),
        repeatedEntities = emptyList(),
        sourceTypes = listOf("SPAM_REPORT", "NEWS", "OFFICIAL"),
        topSnippets = listOf("보이스피싱 조심 - 은행 사칭 사기 전화"),
    )

    val scamResult = DecisionResult(
        riskLevel = RiskLevel.HIGH,
        category = ConclusionCategory.SCAM_RISK_HIGH,
        action = ActionRecommendation.RISK_HIGH,
        confidence = 0.89f,
        summary = "스팸/사기 위험 높음",
        reasons = listOf("저장되지 않은 번호, 기기 기록 없음", "검색 결과: 사기/피싱 관련 키워드 다수"),
        deviceEvidence = scamDevice,
        searchEvidence = scamSearch,
    )

    // ====== SCENARIO 5: Insufficient Evidence (GRAY - Unknown) ======
    val unknownResult = DecisionResult(
        riskLevel = RiskLevel.UNKNOWN,
        category = ConclusionCategory.INSUFFICIENT_EVIDENCE,
        action = ActionRecommendation.UNKNOWN,
        confidence = 0.30f,
        summary = "판단 근거 부족",
        reasons = listOf("기기 기록 없음"),
        deviceEvidence = null,
        searchEvidence = null,
    )
}
