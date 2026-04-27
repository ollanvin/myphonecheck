package app.myphonecheck.mobile.core.globalengine.parsing.phone

/**
 * 전화번호 파싱 결과 (Architecture v2.0.0 §30 + 헌법 §8조 SIM-Oriented Single Core).
 *
 * libphonenumber 정규화 결과를 단일 도메인 모델로 노출.
 * Surface는 본 결과만 소비 — 자체 정규화·검증 금지.
 */
data class PhoneNumberResult(
    val isValid: Boolean,
    val rawInput: String,
    val e164: String,
    val national: String,
    val international: String,
    val regionCode: String,
    val numberType: String,
) {
    companion object {
        fun invalid(raw: String): PhoneNumberResult = PhoneNumberResult(
            isValid = false,
            rawInput = raw,
            e164 = raw,
            national = raw,
            international = raw,
            regionCode = "",
            numberType = "UNKNOWN",
        )
    }
}
