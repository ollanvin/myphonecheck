package app.callcheck.mobile.core.model

/**
 * 사용자 정의 번호 태그.
 *
 * 사용자가 직접 번호에 부여하는 분류.
 * AI 판단(RiskLevel)과 독립적으로, 사용자 경험 기반 분류를 축적한다.
 *
 * 저장: Room DB (UserCallRecord.tag)
 * 키: canonicalNumber (E.164)
 */
enum class UserCallTag(val displayKey: String) {
    /** 사용자가 안전하다고 판단 */
    SAFE("safe"),
    /** 스팸/사기로 판단 */
    SPAM("spam"),
    /** 업무/거래처 */
    BUSINESS("business"),
    /** 개인 연락처 */
    PERSONAL("personal"),
    /** 배달/택배 */
    DELIVERY("delivery"),
    /** 사용자 커스텀 (메모로 구체화) */
    CUSTOM("custom"),
}
