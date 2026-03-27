package app.callcheck.mobile.feature.decisionui.firstrun

import app.callcheck.mobile.core.model.ActionRecommendation
import app.callcheck.mobile.core.model.ConclusionCategory
import app.callcheck.mobile.core.model.DecisionResult
import app.callcheck.mobile.core.model.RiskLevel
import app.callcheck.mobile.core.model.SearchEvidence
import app.callcheck.mobile.core.model.SearchTrend

/**
 * 첫 실행 Fake Call 시나리오.
 *
 * 목적: 사용자가 앱을 처음 열었을 때 "이 앱이 뭘 하는지"
 * 3초 안에 텍스트 없이 이해하게 만드는 것.
 *
 * 전략:
 * - 반드시 DANGER 시나리오 사용 (긴장 → 이해 → 기억)
 * - SAFE는 감정이 없으므 사용 금지
 *
 * 타이밍:
 * - 0ms: 번호 등장 + Ring LOADING 시작
 * - 1500ms: DANGER 결과 전환
 * - 결과 노출 후: 액션 버튼 페이드인
 */
object FakeCallScenario {

    /** 분석 대기 시간 (밀리초). Ring LOADING 상태 유지 시간. */
    const val LOADING_DURATION_MS = 1500L

    /** 액션 버튼 페이드인 지연 (밀리초). 결과 노출 후. */
    const val BUTTON_FADE_DELAY_MS = 600L

    /** 가짜 전화번호. 실제로 존재하지 않는 번호. */
    const val FAKE_PHONE_NUMBER = "02-555-0199"

    /**
     * DANGER 시나리오 결과.
     * 보이스피싱 의심 — 첫 경험에서 가장 강한 임팩트.
     */
    val dangerResult = DecisionResult(
        riskLevel = RiskLevel.HIGH,
        category = ConclusionCategory.SCAM_RISK_HIGH,
        action = ActionRecommendation.RISK_HIGH,
        confidence = 0.91f,
        summary = "보이스피싱 의심",
        reasons = listOf(
            "저장되지 않은 번호",
            "사기 신고 이력 다수",
            "최근 피해 급증 번호",
        ),
        deviceEvidence = null,
        searchEvidence = SearchEvidence(
            recent30dSearchIntensity = 240,
            recent90dSearchIntensity = 420,
            searchTrend = SearchTrend.INCREASING,
            keywordClusters = listOf("보이스피싱", "사기", "대출", "은행 사칭"),
            repeatedEntities = emptyList(),
            sourceTypes = listOf("SPAM_REPORT", "NEWS", "OFFICIAL"),
            topSnippets = listOf("금융감독원 사칭 보이스피싱 주의보"),
        ),
    )
}
