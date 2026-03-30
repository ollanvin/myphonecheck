package app.callcheck.mobile.core.model

/**
 * Tier 0 — 0ms 사전 판단 결과.
 *
 * 전화가 울리기 전, 번호 hash lookup만으로 즉시 반환하는 판단.
 * Room DB에 영속 저장 (앱 재시작 후에도 유효).
 *
 * 설계 철학:
 * - 기존 Decision 캐시 (인메모리, TTL 1h) 와 별개
 * - PreJudge는 과거 모든 판단의 "축적된 기억"
 * - 동일 번호 재수신 시 엔진 실행 없이 0ms 응답
 *
 * 갱신 정책:
 * - 새 판단이 나올 때마다 덮어쓰기
 * - 사용자 행동(수신/거절/차단) 시 confidence 가중치 갱신
 * - 7일 미사용 시 soft expire (판단 유지, 신뢰도 하락)
 *
 * 프라이버시:
 * - 100% 온디바이스, 서버 전송 없음
 * - 앱 삭제 시 자동 소멸
 */
data class PreJudgeResult(
    /** E.164 정규화된 번호 */
    val canonicalNumber: String,

    /** 축적된 최종 행동 권장 */
    val action: ActionRecommendation,

    /** 축적된 위험 점수 (0.0 ~ 1.0) */
    val riskScore: Float,

    /** 마지막 판단의 카테고리 */
    val category: ConclusionCategory,

    /** 판단 신뢰도 (0.0 ~ 1.0). 오래될수록 감쇠. */
    val confidence: Float,

    /** 판단 요약 1줄 */
    val summary: String = "",

    /** 이 번호의 총 수신 횟수 */
    val hitCount: Int = 1,

    /** 마지막 판단 시각 (epoch ms) */
    val lastJudgedAtMs: Long = System.currentTimeMillis(),

    /** 마지막 사용자 행동 */
    val lastUserAction: UserCallAction? = null,
) {
    /**
     * Soft expire 확인.
     *
     * 7일 경과 시 신뢰도를 점진 감쇠시키되 판단 자체는 유지.
     * 이유: 완전 삭제하면 같은 스팸에게 또 엔진을 돌려야 함.
     */
    fun effectiveConfidence(): Float {
        val ageMs = System.currentTimeMillis() - lastJudgedAtMs
        val ageDays = ageMs / (24 * 60 * 60 * 1000f)
        return when {
            ageDays <= FRESH_DAYS -> confidence
            ageDays <= DECAY_MAX_DAYS -> {
                val decayProgress = (ageDays - FRESH_DAYS) / (DECAY_MAX_DAYS - FRESH_DAYS)
                confidence * (1f - 0.5f * decayProgress)
            }
            else -> confidence * 0.3f
        }
    }

    /**
     * 이 판단이 즉시 사용 가능한지 확인.
     * effectiveConfidence >= 0.4 이면 Tier 0 즉시 반환 가능.
     */
    fun isUsable(): Boolean = effectiveConfidence() >= USABLE_THRESHOLD

    companion object {
        const val FRESH_DAYS = 7f
        const val DECAY_MAX_DAYS = 30f
        const val USABLE_THRESHOLD = 0.4f
        const val MAX_ENTRIES = 500
    }
}
