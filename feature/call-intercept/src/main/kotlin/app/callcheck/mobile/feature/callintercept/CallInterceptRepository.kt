package app.callcheck.mobile.feature.callintercept

import app.callcheck.mobile.core.model.DecisionResult
import app.callcheck.mobile.core.model.TwoPhaseDecision

/**
 * Orchestrates the full call intercept decision pipeline.
 *
 * Stage 9 아키텍처:
 * 1. InterceptPriorityRouter — 분석 깊이 결정 (SKIP/INSTANT/LIGHT/FULL)
 * 2. CountryInterceptPolicy — 국가별 정책 적용
 * 3. 2-Phase Scoring — Phase 1 즉시 판단 + Phase 2 확정 판단
 * 4. InterceptOutcomeLearner — 사용자 행동 기반 학습 루프
 *
 * Total target: 3 seconds optimal, 4.5-second hard limit.
 * Returns partial results if search enrichment times out.
 */
interface CallInterceptRepository {
    /**
     * 수신 전화 처리 (기존 호환).
     * TwoPhaseDecision의 finalResult()를 반환.
     *
     * @param normalizedNumber  비교/검색용 canonical 번호
     * @param deviceCountryCode 기기 탐지 국가 코드 (ISO 3166-1 alpha-2).
     */
    suspend fun processIncomingCall(
        normalizedNumber: String,
        deviceCountryCode: String?,
    ): DecisionResult

    /**
     * 수신 전화 처리 (2-Phase).
     * Phase 1 즉시 판단 + Phase 2 확정 판단을 모두 포함.
     *
     * @param normalizedNumber  비교/검색용 canonical 번호
     * @param deviceCountryCode 기기 탐지 국가 코드 (ISO 3166-1 alpha-2).
     */
    suspend fun processIncomingCallTwoPhase(
        normalizedNumber: String,
        deviceCountryCode: String?,
    ): TwoPhaseDecision
}
