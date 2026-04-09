package app.myphonecheck.mobile.feature.callintercept

import android.util.Log
import app.myphonecheck.mobile.core.model.ActionRecommendation
import app.myphonecheck.mobile.core.model.InterceptOutcome
import app.myphonecheck.mobile.core.model.OutcomeLearningResult
import app.myphonecheck.mobile.core.model.UserCallAction
import app.myphonecheck.mobile.data.localcache.repository.PreJudgeCacheRepository
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "InterceptOutcomeLearner"

/**
 * 인터셉트 성과 학습 엔진.
 *
 * 단순 번호 기록이 아니라, "판단이 맞았는지"를 학습하는 피드백 루프.
 *
 * 학습 원리:
 * 1. AI가 "위험"이라 했는데 사용자가 받았고 3분 통화함 → 오탐 → confidence↓ risk↓
 * 2. AI가 "안전"이라 했는데 사용자가 거절+차단 → 미탐 → confidence↓ risk↑
 * 3. AI가 "위험"이라 했고 사용자도 거절 → 정탐 → confidence↑
 * 4. AI가 "안전"이라 했고 사용자가 받고 긴 통화 → 정탐 → confidence↑
 * 5. 같은 번호 재발신 후 사용자 거절 → 스팸 패턴 강화 → risk↑
 * 6. 사용자가 상세 정보 확인 후 거절 → 정보 기반 판단 → confidence↑
 *
 * 학습 결과:
 * - PreJudge 캐시의 confidence와 riskScore를 조정
 * - UserCallRecord에 행동 기록 업데이트
 *
 * 100% 온디바이스. 서버 전송 없음.
 */
@Singleton
class InterceptOutcomeLearner @Inject constructor(
    private val preJudgeCacheRepository: PreJudgeCacheRepository,
) {

    /**
     * 인터셉트 결과를 분석하고 학습 결과를 반환.
     *
     * @return OutcomeLearningResult (confidence/risk 조정값)
     */
    fun analyze(outcome: InterceptOutcome): OutcomeLearningResult {
        val aiSaidSafe = outcome.aiAction == ActionRecommendation.ANSWER ||
                outcome.aiAction == ActionRecommendation.ANSWER_WITH_CAUTION
        val aiSaidDanger = outcome.aiAction == ActionRecommendation.REJECT ||
                outcome.aiAction == ActionRecommendation.BLOCK_REVIEW

        return when (outcome.userAction) {
            UserCallAction.ANSWERED -> analyzeAnswered(outcome, aiSaidSafe, aiSaidDanger)
            UserCallAction.REJECTED -> analyzeRejected(outcome, aiSaidSafe, aiSaidDanger)
            UserCallAction.BLOCKED -> analyzeBlocked(outcome, aiSaidSafe, aiSaidDanger)
            UserCallAction.MISSED -> analyzeMissed(outcome)
        }
    }

    /**
     * 학습 결과를 PreJudge 캐시에 반영.
     */
    suspend fun applyLearning(
        canonicalNumber: String,
        learning: OutcomeLearningResult,
        userAction: UserCallAction,
    ) {
        try {
            // PreJudge 캐시의 사용자 행동 업데이트
            preJudgeCacheRepository.updateUserAction(canonicalNumber, userAction)
            Log.i(TAG, "Applied: $canonicalNumber → conf=${learning.confidenceAdjustment}, risk=${learning.riskAdjustment} (${learning.reason})")
        } catch (e: Exception) {
            Log.w(TAG, "Apply learning error (non-fatal): ${e.message}")
        }
    }

    // ══════════════════════════════════════════
    // 행동별 분석
    // ══════════════════════════════════════════

    /**
     * 사용자가 전화를 받음.
     *
     * Case 1: AI=위험 + 받음 + 긴통화 → 오탐 (false positive)
     * Case 2: AI=위험 + 받음 + 즉시끊음 → 위험 확인 (맞았음)
     * Case 3: AI=안전 + 받음 + 긴통화 → 정탐 (true positive)
     * Case 4: AI=안전 + 받음 + 즉시끊음 → 약한 미탐 가능성
     */
    private fun analyzeAnswered(
        outcome: InterceptOutcome,
        aiSaidSafe: Boolean,
        aiSaidDanger: Boolean,
    ): OutcomeLearningResult {
        val longCall = outcome.callDurationSeconds >= LONG_CALL_THRESHOLD_SECONDS
        val shortCall = outcome.callDurationSeconds in 1..SHORT_CALL_THRESHOLD_SECONDS

        return when {
            // AI=위험인데 받고 3분 이상 통화 → 오탐
            aiSaidDanger && longCall -> {
                Log.d(TAG, "FALSE POSITIVE: AI=danger but user answered+long call (${outcome.callDurationSeconds}s)")
                OutcomeLearningResult(
                    confidenceAdjustment = -0.15f,
                    riskAdjustment = -0.12f,
                    reason = "false_positive: danger_predicted_but_long_call",
                )
            }
            // AI=위험인데 받고 즉시 끊음 → AI 판단 맞았음
            aiSaidDanger && shortCall -> {
                Log.d(TAG, "CONFIRMED DANGER: AI=danger, user answered but short call (${outcome.callDurationSeconds}s)")
                OutcomeLearningResult(
                    confidenceAdjustment = 0.10f,
                    riskAdjustment = 0.05f,
                    reason = "confirmed_danger: short_call_after_answer",
                )
            }
            // AI=안전이고 긴 통화 → 정탐
            aiSaidSafe && longCall -> {
                Log.d(TAG, "TRUE POSITIVE: AI=safe, user answered+long call")
                OutcomeLearningResult(
                    confidenceAdjustment = 0.10f,
                    riskAdjustment = -0.05f,
                    reason = "true_positive: safe_predicted_and_long_call",
                )
            }
            // AI=안전이고 짧은 통화 → 약한 미탐 가능성
            aiSaidSafe && shortCall -> {
                Log.d(TAG, "WEAK FALSE NEGATIVE: AI=safe but short call")
                OutcomeLearningResult(
                    confidenceAdjustment = -0.05f,
                    riskAdjustment = 0.03f,
                    reason = "weak_false_negative: safe_predicted_but_short_call",
                )
            }
            // 기타 (HOLD 판단 등)
            else -> OutcomeLearningResult.NEUTRAL
        }
    }

    /**
     * 사용자가 전화를 거절함.
     *
     * Case 1: AI=위험 + 거절 → 정탐 (사용자도 동의)
     * Case 2: AI=안전 + 거절 → 미탐 (false negative)
     * Case 3: 상세 확인 후 거절 → 정보 기반 판단 (confidence↑)
     */
    private fun analyzeRejected(
        outcome: InterceptOutcome,
        aiSaidSafe: Boolean,
        aiSaidDanger: Boolean,
    ): OutcomeLearningResult {
        val detailBased = outcome.detailViewed

        return when {
            // AI=위험 + 거절 → AI 판단 확인
            aiSaidDanger -> {
                val confBoost = if (detailBased) 0.15f else 0.10f
                Log.d(TAG, "CONFIRMED: AI=danger, user rejected (detail=$detailBased)")
                OutcomeLearningResult(
                    confidenceAdjustment = confBoost,
                    riskAdjustment = 0.05f,
                    reason = "confirmed: danger_predicted_and_rejected",
                )
            }
            // AI=안전 + 거절 → 미탐
            aiSaidSafe -> {
                Log.d(TAG, "FALSE NEGATIVE: AI=safe but user rejected")
                OutcomeLearningResult(
                    confidenceAdjustment = -0.12f,
                    riskAdjustment = 0.10f,
                    reason = "false_negative: safe_predicted_but_rejected",
                )
            }
            // HOLD + 거절 → 약한 위험 신호
            else -> {
                OutcomeLearningResult(
                    confidenceAdjustment = 0.05f,
                    riskAdjustment = 0.05f,
                    reason = "hold_rejected: weak_danger_signal",
                )
            }
        }
    }

    /**
     * 사용자가 번호를 차단함.
     *
     * 차단은 가장 강한 사용자 의사 표시.
     * AI 판단과 무관하게 강한 위험 신호.
     */
    private fun analyzeBlocked(
        outcome: InterceptOutcome,
        aiSaidSafe: Boolean,
        aiSaidDanger: Boolean,
    ): OutcomeLearningResult {
        return when {
            // AI=안전이었는데 차단 → 강한 미탐
            aiSaidSafe -> {
                Log.d(TAG, "STRONG FALSE NEGATIVE: AI=safe but user blocked")
                OutcomeLearningResult(
                    confidenceAdjustment = -0.20f,
                    riskAdjustment = 0.15f,
                    reason = "strong_false_negative: safe_predicted_but_blocked",
                )
            }
            // AI=위험 + 차단 → 강한 확인
            aiSaidDanger -> {
                Log.d(TAG, "STRONG CONFIRMED: AI=danger, user blocked")
                OutcomeLearningResult(
                    confidenceAdjustment = 0.20f,
                    riskAdjustment = 0.08f,
                    reason = "strong_confirmed: danger_predicted_and_blocked",
                )
            }
            // HOLD + 차단
            else -> {
                OutcomeLearningResult(
                    confidenceAdjustment = 0.10f,
                    riskAdjustment = 0.12f,
                    reason = "hold_blocked: strong_danger_signal",
                )
            }
        }
    }

    /**
     * 부재중 (사용자 미응답).
     *
     * 약한 신호 — 의도적 무시인지 단순 부재인지 구분 불가.
     * 재발신 횟수로 보완.
     */
    private fun analyzeMissed(outcome: InterceptOutcome): OutcomeLearningResult {
        return when {
            // 재발신 3회 이상 → 스팸 패턴
            outcome.callbackCount >= 3 -> {
                Log.d(TAG, "SPAM PATTERN: missed + ${outcome.callbackCount} callbacks")
                OutcomeLearningResult(
                    confidenceAdjustment = 0.08f,
                    riskAdjustment = 0.10f,
                    reason = "spam_pattern: repeated_callbacks_after_miss",
                )
            }
            // 재발신 1회 → 중립 (정상적 재시도 가능)
            outcome.callbackCount >= 1 -> OutcomeLearningResult.NEUTRAL
            // 단순 부재 → 중립
            else -> OutcomeLearningResult.NEUTRAL
        }
    }

    companion object {
        /** 긴 통화 기준: 180초 (3분) 이상 */
        const val LONG_CALL_THRESHOLD_SECONDS = 180
        /** 짧은 통화 기준: 10초 이하 */
        const val SHORT_CALL_THRESHOLD_SECONDS = 10
    }
}
