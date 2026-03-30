package app.callcheck.mobile.feature.decisionengine

import app.callcheck.mobile.core.model.BehaviorPatternSignal
import app.callcheck.mobile.core.model.DecisionResult
import app.callcheck.mobile.core.model.DeviceEvidence
import app.callcheck.mobile.core.model.LocalLearningSignal
import app.callcheck.mobile.core.model.RiskLevel
import app.callcheck.mobile.core.model.SearchEvidence

/**
 * Core decision engine interface.
 *
 * 4축 판단 입력:
 * 1. DeviceEvidence — 기기 내 통화 이력, 연락처 매칭
 * 2. SearchEvidence — 웹 검색 기반 평판
 * 3. LocalLearningSignal — 사용자 과거 행동 가중치
 * 4. BehaviorPatternSignal — 시간대/반복/VoIP 행동 패턴
 *
 * Contract:
 * - Must be synchronous (no I/O inside)
 * - Must complete in < 50ms
 * - Must always return a valid DecisionResult (never throw)
 *
 * 100% 온디바이스, 서버 전송 없음.
 */
interface DecisionEngine {
    fun evaluate(
        deviceEvidence: DeviceEvidence?,
        searchEvidence: SearchEvidence?,
        localLearning: LocalLearningSignal? = null,
        behaviorPattern: BehaviorPatternSignal? = null,
    ): DecisionResult

    /**
     * riskScore → RiskLevel 변환.
     * Tier 0 PreJudge 캐시 반환 시 사용.
     */
    fun riskLevelFromScore(score: Float): RiskLevel
}
