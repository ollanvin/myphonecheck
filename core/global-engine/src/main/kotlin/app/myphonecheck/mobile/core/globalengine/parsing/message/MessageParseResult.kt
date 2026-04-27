package app.myphonecheck.mobile.core.globalengine.parsing.message

/**
 * 메시지 파싱 결과 (Architecture v2.0.0 §22 + §30).
 *
 * 발신자 + 본문 → 패턴 추출 + 분류 결과.
 * Surface는 본 결과만 소비 — 자체 정규식·분류 금지 (헌법 §8조).
 */
data class MessageFeatures(
    val sender: String,
    val isShortSender: Boolean,
    val hasUrl: Boolean,
    val hasCurrencyPattern: Boolean,
    val bodyLength: Int,
    val urlCount: Int,
    val countryHint: String,
)

enum class MessageCategory {
    /** 결제 후보 — CardCheck로 라우팅 가능 */
    PAYMENT_CANDIDATE,

    /** 스팸 후보 — MessageCheck 차단 prompt */
    SPAM_CANDIDATE,

    /** 알림 (정부·통신사 short sender, URL 없음) */
    NOTIFICATION,

    /** 일반 — 통상 메시지 */
    NORMAL,
}

data class MessageParseResult(
    val features: MessageFeatures,
    val category: MessageCategory,
)
