package app.myphonecheck.mobile.core.security.tamper

/**
 * 탬퍼 탐지 결과를 다축 점수로 표현.
 *
 * 단일 boolean이 아닌 점수 기반으로 설계하여
 * 각 신호의 강도를 세밀하게 판단 가능.
 */
data class TamperSignal(
    val rootScore: Int,       // 0-100: 루팅 탐지 점수
    val hookScore: Int,       // 0-100: 후킹 프레임워크 탐지 점수
    val repackageScore: Int,  // 0-100: 리패키징/변조 탐지 점수
    val isEmulator: Boolean,  // 에뮬레이터 여부
) {
    /** 종합 위험 점수 (0-100) */
    val totalRiskScore: Int
        get() = (rootScore + hookScore + repackageScore) / 3

    /** 고위험 여부 — 프리미엄 기능 차단 기준 */
    val isHighRisk: Boolean
        get() = totalRiskScore > 60

    /** 중위험 여부 — 경고 표시 기준 */
    val isMediumRisk: Boolean
        get() = totalRiskScore in 30..60

    /** 빌링 차단 여부 — 결제 거부 기준 (후킹 + 고위험) */
    val shouldBlockBilling: Boolean
        get() = hookScore > 50 || isHighRisk

    companion object {
        /** 안전 상태 */
        val SAFE = TamperSignal(
            rootScore = 0,
            hookScore = 0,
            repackageScore = 0,
            isEmulator = false,
        )
    }
}
