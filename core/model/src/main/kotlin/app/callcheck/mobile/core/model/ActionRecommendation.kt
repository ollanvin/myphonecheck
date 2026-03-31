package app.callcheck.mobile.core.model

/**
 * 판단 상태 (Judgment State).
 *
 * 시스템이 통화에 대한 평가 상태를 서술합니다.
 * 사용자에게 명령하지 않고, 상황을 설명합니다.
 * 사용자가 최종 행동을 결정합니다.
 *
 * NOTE: displayName은 UI에서 판단 상태 표시용.
 * Notification 액션 버튼(거절/차단/자세히)은 별도 하드코딩.
 */
enum class ActionRecommendation(
    val displayNameEn: String,
    val displayNameKo: String,
) {
    ANSWER(
        displayNameEn = "Likely Safe",
        displayNameKo = "안전 추정",
    ),
    ANSWER_WITH_CAUTION(
        displayNameEn = "Caution Advised",
        displayNameKo = "주의 권고",
    ),
    REJECT(
        displayNameEn = "Risk Suspected",
        displayNameKo = "위험 의심",
    ),
    BLOCK_REVIEW(
        displayNameEn = "High Risk Detected",
        displayNameKo = "고위험 감지",
    ),
    HOLD(
        displayNameEn = "Pending Review",
        displayNameKo = "판단 보류",
    ),
}
