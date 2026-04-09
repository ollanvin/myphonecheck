package app.myphonecheck.mobile.core.model

/**
 * 푸시 알림 분석 증거.
 *
 * NotificationListenerService에서 수집한 알림 데이터를
 * 판단 엔진이 소비할 수 있는 형태로 정규화한 모델.
 *
 * 판단 기준:
 * - 반복성: 같은 앱/채널에서 얼마나 자주 오는가
 * - 프로모션 비율: 프로모션 vs 핵심 알림 비율
 * - 야간 방해: 22:00~07:00 사이 알림 여부
 * - 내용 패턴: 광고성 키워드/패턴 탐지
 */
data class PushEvidence(
    /** 발신 앱 패키지명 */
    val packageName: String,

    /** 앱 표시 이름 */
    val appLabel: String,

    /** 알림 채널 ID */
    val channelId: String?,

    /** 알림 채널 이름 */
    val channelName: String?,

    /** 알림 제목 */
    val title: String?,

    /** 알림 본문 텍스트 */
    val text: String?,

    /** 최근 24시간 이 앱의 알림 수 */
    val countLast24h: Int,

    /** 최근 7일 이 앱의 알림 수 */
    val countLast7d: Int,

    /** 프로모션 키워드 매칭 수 */
    val promotionKeywordHits: Int,

    /** 야간 시간대 알림 여부 (22:00~07:00) */
    val isNightTime: Boolean,

    /** 사용자가 이 앱의 알림을 탭한 비율 (0.0~1.0) */
    val interactionRate: Float,

    /** 수신 시각 (epoch millis) */
    val receivedAtMillis: Long,
) {
    companion object {
        fun empty(packageName: String) = PushEvidence(
            packageName = packageName,
            appLabel = "",
            channelId = null,
            channelName = null,
            title = null,
            text = null,
            countLast24h = 0,
            countLast7d = 0,
            promotionKeywordHits = 0,
            isNightTime = false,
            interactionRate = 0f,
            receivedAtMillis = System.currentTimeMillis(),
        )
    }

    /** 프로모션/스팸 의심 점수 (0.0~1.0) */
    val spamScore: Float
        get() {
            var score = 0f
            // 빈도 기반
            if (countLast24h >= 10) score += 0.3f
            else if (countLast24h >= 5) score += 0.15f
            // 프로모션 키워드
            if (promotionKeywordHits >= 3) score += 0.3f
            else if (promotionKeywordHits >= 1) score += 0.15f
            // 야간 방해
            if (isNightTime) score += 0.1f
            // 낮은 상호작용율
            if (interactionRate < 0.1f && countLast7d >= 10) score += 0.2f
            return score.coerceIn(0f, 1f)
        }

    /** 핵심 알림 가능성 */
    val isLikelyCritical: Boolean
        get() = promotionKeywordHits == 0 && interactionRate > 0.5f
}
