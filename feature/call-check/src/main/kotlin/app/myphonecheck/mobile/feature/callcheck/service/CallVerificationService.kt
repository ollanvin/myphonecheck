package app.myphonecheck.mobile.feature.callcheck.service

import app.myphonecheck.mobile.core.globalengine.parsing.phone.PhoneNumberParser
import app.myphonecheck.mobile.core.globalengine.parsing.phone.PhoneNumberResult
import app.myphonecheck.mobile.core.globalengine.simcontext.SimContext
import app.myphonecheck.mobile.core.globalengine.simcontext.SimContextProvider
import javax.inject.Inject
import javax.inject.Singleton

/**
 * CallCheck 발신자 검증 (Architecture v2.0.0 §21).
 *
 * 코어 PhoneNumberParser만 사용 — libphonenumber 직접 의존 0.
 * 분류는 PhoneNumberResult.numberType (String) 기반으로 분기 → 코어 의존 외부 표면 제거.
 *
 * 헌법 정합:
 *  - 3조 결정권 중앙집중 금지: 검증 결과만 노출, 차단 결정은 사용자.
 *  - 8조 SIM-Oriented Single Core: SimContextProvider만 신뢰.
 */
@Singleton
class CallVerificationService @Inject constructor(
    private val phoneParser: PhoneNumberParser,
    private val simContextProvider: SimContextProvider,
) {

    fun verify(rawNumber: String, simContext: SimContext = simContextProvider.resolve()): CallVerification {
        val parsed = phoneParser.parse(rawNumber, simContext)
        val classification = when {
            !parsed.isValid -> CallClassification.INVALID
            parsed.regionCode != simContext.phoneRegion -> CallClassification.INTERNATIONAL
            parsed.numberType == "PREMIUM_RATE" -> CallClassification.PREMIUM_RATE
            parsed.numberType == "TOLL_FREE" -> CallClassification.TOLL_FREE
            parsed.numberType == "MOBILE" -> CallClassification.MOBILE
            parsed.numberType == "FIXED_LINE" -> CallClassification.FIXED_LINE
            parsed.numberType == "FIXED_LINE_OR_MOBILE" -> CallClassification.FIXED_OR_MOBILE
            else -> CallClassification.OTHER
        }
        return CallVerification(parsed = parsed, classification = classification)
    }
}

data class CallVerification(
    val parsed: PhoneNumberResult,
    val classification: CallClassification,
)

enum class CallClassification {
    INVALID,
    MOBILE,
    FIXED_LINE,
    FIXED_OR_MOBILE,
    INTERNATIONAL,
    PREMIUM_RATE,
    TOLL_FREE,
    OTHER,
}
