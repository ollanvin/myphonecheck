package app.myphonecheck.mobile.core.globalengine.decision

/**
 * Real-time Action 결정 (Architecture v2.1.0 §31).
 *
 * Surface(call-screening / sms-block / push-trash)는 본 결정만 소비.
 * 결정 산출은 50ms 이내 (CallScreeningService 5s 제한 대비 여유).
 */
data class ActionDecision(
    val action: ActionType,
    val matchedSource: MatchedSource,
    val tag: String?,
    val confidence: ActionConfidence,
)

enum class ActionType {
    BLOCK,          // 즉시 차단
    SILENT,         // 무음 처리
    TAG_DISPLAY,    // 태그 라벨 표시
    LABEL_DISPLAY,  // 사용자 라벨 표시
    PASS,           // OS 기본
}

enum class MatchedSource {
    LAYER_2_BLOCKLIST,
    LAYER_2_TAG,
    LAYER_2_LABEL,
    LAYER_3_COMPETITOR,
    LAYER_3_GOVERNMENT,
    LAYER_3_TELCO,
    LAYER_3_SECURITY,
    NONE,
}

enum class ActionConfidence { HIGH, MEDIUM, LOW }

enum class IdentifierType { PHONE_E164, SMS_SENDER, NOTIFICATION_PACKAGE }
