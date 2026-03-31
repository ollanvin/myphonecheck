package app.callcheck.mobile.core.model

/**
 * 사용자의 통화 행동 기록.
 *
 * 각 통화에 대해 사용자가 취한 최종 행동.
 * 시간순 축적 → 번호별 행동 패턴 분석 가능.
 */
enum class UserCallAction(val displayKey: String) {
    /** 전화를 받음 */
    ANSWERED("answered"),
    /** 전화를 거절함 */
    REJECTED("rejected"),
    /** 번호를 차단함 */
    BLOCKED("blocked"),
    /** 행동 미기록 (부재중 등) */
    MISSED("missed"),
}
