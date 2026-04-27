package app.myphonecheck.mobile.feature.callcheck.repository

/**
 * 통화 이력 항목 (Architecture v2.0.0 §21 CallCheck).
 *
 * Android CallLog 한 행을 코어 PhoneNumberParser로 정규화한 결과 + OS 메타.
 * 영구 저장 안 함 (헌법 §2 In-Bound Zero) — UI 화면 라이프사이클 동안만 보유.
 */
data class CallEntry(
    val rawNumber: String,
    val displayNumber: String,
    val e164: String,
    val isValid: Boolean,
    val regionCode: String,
    val numberType: String,
    val timestampMillis: Long,
    val durationSeconds: Long,
    val direction: CallDirection,
)

enum class CallDirection {
    INCOMING,
    OUTGOING,
    MISSED,
    REJECTED,
    BLOCKED,
    VOICEMAIL,
    UNKNOWN,
}
