package app.myphonecheck.mobile.core.model

/**
 * 판단 결론 카테고리.
 *
 * 4엔진 통합 구조:
 * - CALL 엔진: KNOWN_CONTACT ~ INSUFFICIENT_EVIDENCE (기존)
 * - PUSH 엔진: PUSH_* 카테고리
 * - MESSAGE 엔진: MSG_* 카테고리
 * - PRIVACY 엔진: PRIV_* 카테고리
 *
 * 모든 카테고리는 동일한 Decision UI (Ring + 버튼)로 출력됩니다.
 */
enum class ConclusionCategory(
    val summaryEn: String,
    val summaryKo: String,
    val eventType: InterceptEventType = InterceptEventType.CALL,
) {
    // ═══════════════════════════════════════
    // MyPhoneCheck 엔진 (기존)
    // ═══════════════════════════════════════
    KNOWN_CONTACT(
        summaryEn = "Known contact",
        summaryKo = "저장된 연락처",
    ),
    BUSINESS_LIKELY(
        summaryEn = "Business contact likely",
        summaryKo = "거래처/업무 번호 가능성 높음",
    ),
    DELIVERY_LIKELY(
        summaryEn = "Delivery/courier likely",
        summaryKo = "택배/배송 가능성 높음",
    ),
    INSTITUTION_LIKELY(
        summaryEn = "Institution likely",
        summaryKo = "기관/병원 가능성 높음",
    ),
    SALES_SPAM_SUSPECTED(
        summaryEn = "Sales/advertising suspected",
        summaryKo = "광고/영업 의심",
    ),
    SCAM_RISK_HIGH(
        summaryEn = "Scam/phishing risk high",
        summaryKo = "스팸/사기 위험 높음",
    ),
    INSUFFICIENT_EVIDENCE(
        summaryEn = "Insufficient evidence",
        summaryKo = "판단 근거 부족",
    ),

    // ═══════════════════════════════════════
    // PushCheck 엔진
    // ═══════════════════════════════════════
    PUSH_CRITICAL(
        summaryEn = "Important notification",
        summaryKo = "핵심 알림",
        eventType = InterceptEventType.PUSH,
    ),
    PUSH_PROMOTION(
        summaryEn = "Promotional notification",
        summaryKo = "프로모션/광고 알림",
        eventType = InterceptEventType.PUSH,
    ),
    PUSH_NOISE(
        summaryEn = "Noise notification",
        summaryKo = "소음 알림 (반복/무의미)",
        eventType = InterceptEventType.PUSH,
    ),
    PUSH_NIGHT_DISTURB(
        summaryEn = "Night-time disturbance",
        summaryKo = "야간 방해 알림",
        eventType = InterceptEventType.PUSH,
    ),

    // ═══════════════════════════════════════
    // MessageCheck 엔진
    // ═══════════════════════════════════════
    MSG_SAFE(
        summaryEn = "Safe message",
        summaryKo = "안전한 메시지",
        eventType = InterceptEventType.MESSAGE,
    ),
    MSG_IMPERSONATION(
        summaryEn = "Impersonation suspected",
        summaryKo = "사칭 의심 메시지",
        eventType = InterceptEventType.MESSAGE,
    ),
    MSG_PHISHING_LINK(
        summaryEn = "Phishing link detected",
        summaryKo = "피싱 링크 감지",
        eventType = InterceptEventType.MESSAGE,
    ),
    MSG_FINANCIAL_SCAM(
        summaryEn = "Financial scam suspected",
        summaryKo = "금융 사기 의심",
        eventType = InterceptEventType.MESSAGE,
    ),
    MSG_UNKNOWN_SENDER(
        summaryEn = "Unknown sender",
        summaryKo = "알 수 없는 발신자",
        eventType = InterceptEventType.MESSAGE,
    ),

    // ═══════════════════════════════════════
    // PrivacyCheck 엔진
    // ═══════════════════════════════════════
    PRIV_NORMAL(
        summaryEn = "Normal sensor usage",
        summaryKo = "정상적인 센서 사용",
        eventType = InterceptEventType.PRIVACY,
    ),
    PRIV_FIRST_ACCESS(
        summaryEn = "First-time sensor access",
        summaryKo = "최초 센서 접근",
        eventType = InterceptEventType.PRIVACY,
    ),
    PRIV_BACKGROUND(
        summaryEn = "Background sensor access",
        summaryKo = "백그라운드 센서 접근",
        eventType = InterceptEventType.PRIVACY,
    ),
    PRIV_SUSPICIOUS(
        summaryEn = "Suspicious sensor activity",
        summaryKo = "의심스러운 센서 활동",
        eventType = InterceptEventType.PRIVACY,
    ),
}
