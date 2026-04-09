package app.myphonecheck.mobile.core.model

/**
 * 인터셉트 결과 (사용자 행동 + 후속 신호).
 *
 * 단순 번호 기록이 아니라, 인터셉트 "성과"를 측정하는 데이터.
 * 다음 인터셉트에 반영되어 판단 정확도를 점진적으로 개선.
 *
 * 수집 시점:
 * - 사용자가 전화를 받았을 때 (ANSWERED)
 * - 사용자가 거절했을 때 (REJECTED)
 * - 사용자가 차단했을 때 (BLOCKED)
 * - 전화가 끝났을 때 (통화 시간 측정)
 * - 같은 번호가 재발신했을 때 (CALLBACK)
 * - 사용자가 상세 정보를 확인했을 때 (DETAIL_VIEWED)
 *
 * 100% 온디바이스. 서버 전송 없음.
 */
data class InterceptOutcome(
    /** E.164 정규화 번호 */
    val canonicalNumber: String,

    /** 사용자의 최종 행동 */
    val userAction: UserCallAction,

    /** AI가 판단한 action (판단 시점) */
    val aiAction: ActionRecommendation,

    /** AI가 판단한 riskScore (판단 시점) */
    val aiRiskScore: Float,

    /** 통화 시간 (초). 0이면 받지 않음 / 즉시 끊음 */
    val callDurationSeconds: Int = 0,

    /** 사용자가 상세 정보를 확인했는지 */
    val detailViewed: Boolean = false,

    /** 같은 번호의 재발신 횟수 (이번 인터셉트 이후) */
    val callbackCount: Int = 0,

    /** 수신 시각 (epoch ms) */
    val interceptedAtMs: Long = System.currentTimeMillis(),
)

/**
 * 인터셉트 성과 학습 결과.
 *
 * InterceptOutcome을 분석하여 다음 인터셉트에 반영할 가중치.
 * PreJudge 캐시의 confidence와 riskScore 조정에 사용.
 */
data class OutcomeLearningResult(
    /** confidence 조정 (-0.30 ~ +0.30) */
    val confidenceAdjustment: Float,

    /** riskScore 조정 (-0.20 ~ +0.20) */
    val riskAdjustment: Float,

    /** 학습 근거 (디버그용) */
    val reason: String,
) {
    companion object {
        val NEUTRAL = OutcomeLearningResult(0f, 0f, "no adjustment")
    }
}
