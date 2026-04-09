package app.myphonecheck.mobile.core.model

/**
 * 로컬 학습 신호.
 *
 * 사용자의 과거 행동(수신/거절/차단)을 기반으로 번호의 위험/안전 가중치를 제공.
 * 서버 전송 없음 — 100% 온디바이스 개인화.
 *
 * 설계 원칙:
 * - 사용자가 "차단" → 위험 가중 (+0.20)
 * - 사용자가 "거절 2회 이상" → 위험 가중 (+0.10)
 * - 사용자가 "수신" → 안전 가중 (-0.15)
 * - 사용자가 "수신 3회 이상" → 강한 안전 가중 (-0.25)
 * - 사용자 태그 "spam" → 위험 가중 (+0.20)
 * - 사용자 태그 "safe"/"personal" → 안전 가중 (-0.20)
 *
 * 이 가중치는 DecisionEngine의 riskScore에 가산/감산됩니다.
 */
data class LocalLearningSignal(
    /** 이 번호의 총 통화 횟수 */
    val callCount: Int = 0,
    /** 마지막 사용자 행동 */
    val lastAction: UserCallAction? = null,
    /** 사용자가 수신(answered)한 횟수 (callCount 중) */
    val answeredCount: Int = 0,
    /** 사용자가 거절(rejected)한 횟수 */
    val rejectedCount: Int = 0,
    /** 사용자가 차단(blocked)한 번호인지 */
    val isBlocked: Boolean = false,
    /** 사용자 태그 (safe, spam, business, personal, delivery, custom) */
    val userTag: String? = null,
) {
    /**
     * 로컬 학습 기반 위험 가중치.
     *
     * 양수 = 위험 가산, 음수 = 안전 가산.
     * DecisionEngine의 riskScore에 직접 가산.
     *
     * 범위: -0.25 ~ +0.30
     */
    fun riskAdjustment(): Float {
        var adj = 0f

        // 차단된 번호: 강한 위험 가중
        if (isBlocked) {
            adj += 0.20f
        }

        // 사용자 거절 반복: 위험 신호
        if (rejectedCount >= 2) {
            adj += 0.10f
        }

        // 사용자 수신 반복: 안전 신호
        when {
            answeredCount >= 3 -> adj -= 0.25f
            answeredCount >= 1 -> adj -= 0.15f
        }

        // 사용자 태그 기반
        when (userTag?.lowercase()) {
            "spam" -> adj += 0.20f
            "safe", "personal" -> adj -= 0.20f
            "business" -> adj -= 0.10f
            "delivery" -> {} // 중립
        }

        return adj.coerceIn(-0.25f, 0.30f)
    }

    companion object {
        /** 학습 데이터 없음 (첫 수신) */
        val EMPTY = LocalLearningSignal()
    }
}
