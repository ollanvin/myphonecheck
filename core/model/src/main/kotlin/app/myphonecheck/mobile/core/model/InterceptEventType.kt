package app.myphonecheck.mobile.core.model

/**
 * 디바이스 인터셉트 이벤트 타입.
 *
 * MyPhoneCheck 4엔진 통합 구조의 핵심 구분자.
 * 모든 이벤트는 동일한 Decision 파이프라인을 통과하되,
 * 이벤트 타입에 따라 Evidence 수집 방식과 판단 가중치가 달라진다.
 *
 * ═══════════════════════════════════════════════
 * CALL     → 전화 수신 → MyPhoneCheck 엔진
 * MESSAGE  → 문자 수신 → MessageCheck 엔진
 * PRIVACY  → 센서 접근 → PrivacyCheck 엔진
 *
 * v1.1: PUSH 엔진 제거됨
 * ═══════════════════════════════════════════════
 */
enum class InterceptEventType(
    val displayNameEn: String,
    val displayNameKo: String,
    val iconLabel: String,
) {
    CALL(
        displayNameEn = "Incoming Call",
        displayNameKo = "전화 수신",
        iconLabel = "CALL",
    ),
    // PUSH removed per v1.1 Architecture
    MESSAGE(
        displayNameEn = "Message Received",
        displayNameKo = "메시지 수신",
        iconLabel = "MSG",
    ),
    PRIVACY(
        displayNameEn = "Sensor Access",
        displayNameKo = "센서 접근",
        iconLabel = "PRIV",
    ),
}
