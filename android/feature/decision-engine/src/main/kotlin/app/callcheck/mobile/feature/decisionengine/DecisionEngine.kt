package app.callcheck.mobile.feature.decisionengine

import app.callcheck.mobile.core.model.DecisionResult
import app.callcheck.mobile.core.model.DeviceEvidence
import app.callcheck.mobile.core.model.LocalLearningSignal
import app.callcheck.mobile.core.model.SearchEvidence

/**
 * Core decision engine interface.
 *
 * Accepts device evidence + search evidence + local learning signal,
 * returns a complete DecisionResult with risk badge, conclusion category,
 * action recommendation, summary, and reasons.
 *
 * Contract:
 * - Must be synchronous (no I/O inside)
 * - Must complete in < 50ms
 * - Must always return a valid DecisionResult (never throw)
 *
 * 로컬 학습:
 * - localLearning이 null이 아니면 사용자 과거 행동 기반 가중치 적용
 * - 차단 번호 → 위험 가산, 반복 수신 → 안전 가산
 * - 100% 온디바이스, 서버 전송 없음
 */
interface DecisionEngine {
    fun evaluate(
        deviceEvidence: DeviceEvidence?,
        searchEvidence: SearchEvidence?,
        localLearning: LocalLearningSignal? = null,
    ): DecisionResult
}
