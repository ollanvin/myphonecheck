package app.myphonecheck.mobile.core.model

import androidx.annotation.StringRes

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
 *
 * Display copy는 [summaryResId] 한 경로로만 제공한다 (§9-1: EN default strings.xml, OS Locale 위임).
 */
enum class ConclusionCategory(
    @StringRes val summaryResId: Int,
    val eventType: InterceptEventType = InterceptEventType.CALL,
) {
    // ═══════════════════════════════════════
    // MyPhoneCheck 엔진 (기존)
    // ═══════════════════════════════════════
    KNOWN_CONTACT(R.string.cat_summary_known_contact),
    BUSINESS_LIKELY(R.string.cat_summary_business_likely),
    DELIVERY_LIKELY(R.string.cat_summary_delivery_likely),
    INSTITUTION_LIKELY(R.string.cat_summary_institution_likely),
    SALES_SPAM_SUSPECTED(R.string.cat_summary_sales_spam_suspected),
    SCAM_RISK_HIGH(R.string.cat_summary_scam_risk_high),
    INSUFFICIENT_EVIDENCE(R.string.cat_summary_insufficient_evidence),

    // ═══════════════════════════════════════
    // PushCheck 엔진
    // ═══════════════════════════════════════
    PUSH_CRITICAL(
        R.string.cat_summary_push_critical,
        eventType = InterceptEventType.PUSH,
    ),
    PUSH_PROMOTION(
        R.string.cat_summary_push_promotion,
        eventType = InterceptEventType.PUSH,
    ),
    PUSH_NOISE(
        R.string.cat_summary_push_noise,
        eventType = InterceptEventType.PUSH,
    ),
    PUSH_NIGHT_DISTURB(
        R.string.cat_summary_push_night_disturb,
        eventType = InterceptEventType.PUSH,
    ),

    // ═══════════════════════════════════════
    // MessageCheck 엔진
    // ═══════════════════════════════════════
    MSG_SAFE(
        R.string.cat_summary_msg_safe,
        eventType = InterceptEventType.MESSAGE,
    ),
    MSG_IMPERSONATION(
        R.string.cat_summary_msg_impersonation,
        eventType = InterceptEventType.MESSAGE,
    ),
    MSG_PHISHING_LINK(
        R.string.cat_summary_msg_phishing_link,
        eventType = InterceptEventType.MESSAGE,
    ),
    MSG_FINANCIAL_SCAM(
        R.string.cat_summary_msg_financial_scam,
        eventType = InterceptEventType.MESSAGE,
    ),
    MSG_UNKNOWN_SENDER(
        R.string.cat_summary_msg_unknown_sender,
        eventType = InterceptEventType.MESSAGE,
    ),

    // ═══════════════════════════════════════
    // PrivacyCheck 엔진
    // ═══════════════════════════════════════
    PRIV_NORMAL(
        R.string.cat_summary_priv_normal,
        eventType = InterceptEventType.PRIVACY,
    ),
    PRIV_FIRST_ACCESS(
        R.string.cat_summary_priv_first_access,
        eventType = InterceptEventType.PRIVACY,
    ),
    PRIV_BACKGROUND(
        R.string.cat_summary_priv_background,
        eventType = InterceptEventType.PRIVACY,
    ),
    PRIV_SUSPICIOUS(
        R.string.cat_summary_priv_suspicious,
        eventType = InterceptEventType.PRIVACY,
    ),
}
