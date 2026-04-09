package app.myphonecheck.mobile.core.model

/**
 * 행동 패턴 시그널 — 개인화 판단의 핵심 데이터.
 *
 * 번호 외 신호를 포함한 확장 판단 근거:
 * - 시간대 (새벽 전화 → 위험 가중)
 * - 반복 패턴 (같은 번호 연속 수신 → 위험 상승)
 * - VoIP 감지 (비 PSTN 경로)
 * - 사용자 습관 (평소 전화 받는 시간대)
 *
 * 100% 온디바이스. 서버 전송 없음.
 */
data class BehaviorPatternSignal(
    // ── 시간 패턴 ──
    /** 현재 수신 시각의 시간 (0~23) */
    val currentHour: Int,
    /** 이 번호의 최근 수신 시각 목록 (최대 10개, epoch ms) */
    val recentCallTimestamps: List<Long> = emptyList(),
    /** 사용자의 평소 활동 시간대 */
    val userActiveHourStart: Int = DEFAULT_ACTIVE_START,
    val userActiveHourEnd: Int = DEFAULT_ACTIVE_END,

    // ── 반복 패턴 ──
    /** 최근 1시간 내 이 번호의 수신 횟수 */
    val recentHourCallCount: Int = 0,
    /** 최근 24시간 내 이 번호의 수신 횟수 */
    val recent24hCallCount: Int = 0,

    // ── 통신 경로 ──
    /** VoIP 경로 감지 여부 */
    val isVoipCall: Boolean = false,
    /** 국제 전화 여부 */
    val isInternationalCall: Boolean = false,
    /** 로밍 상태 여부 */
    val isRoaming: Boolean = false,
) {
    /**
     * 행동 패턴 기반 위험 가중치.
     * 범위: -0.15 ~ +0.40
     */
    fun riskAdjustment(): Float {
        var adj = 0f

        // 새벽 (0~5시): 강한 위험 가중
        if (currentHour in 0..5) {
            adj += 0.15f
        } else if (!isWithinActiveHours()) {
            adj += 0.08f
        }

        // 1시간 내 3회 이상: 전화 폭탄 패턴
        if (recentHourCallCount >= 3) {
            adj += 0.15f
        } else if (recentHourCallCount >= 2) {
            adj += 0.08f
        }

        // 버스트 감지: 최근 2통이 5분 이내
        if (recentCallTimestamps.size >= 2) {
            val last = recentCallTimestamps.last()
            val secondLast = recentCallTimestamps[recentCallTimestamps.size - 2]
            if (last - secondLast < BURST_INTERVAL_MS) {
                adj += 0.10f
            }
        }

        if (isVoipCall) adj += 0.05f
        if (isInternationalCall) adj += 0.08f

        return adj.coerceIn(-0.15f, 0.40f)
    }

    private fun isWithinActiveHours(): Boolean {
        return if (userActiveHourStart <= userActiveHourEnd) {
            currentHour in userActiveHourStart..userActiveHourEnd
        } else {
            currentHour >= userActiveHourStart || currentHour <= userActiveHourEnd
        }
    }

    companion object {
        const val DEFAULT_ACTIVE_START = 8
        const val DEFAULT_ACTIVE_END = 22
        const val BURST_INTERVAL_MS = 5 * 60 * 1000L
    }
}
