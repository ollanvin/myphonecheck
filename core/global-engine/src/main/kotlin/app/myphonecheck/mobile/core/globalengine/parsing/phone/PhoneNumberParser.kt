package app.myphonecheck.mobile.core.globalengine.parsing.phone

import app.myphonecheck.mobile.core.globalengine.simcontext.SimContext
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 글로벌 전화번호 파서 (Architecture v2.0.0 §30 :core:global-engine).
 *
 * libphonenumber 기반. SimContext.phoneRegion = 기본 region.
 * 입력 번호가 국제 양식(+) 이면 region 무시하고 E.164로 정규화.
 *
 * 헌법 정합:
 *  - 1조 Out-Bound Zero: libphonenumber 오프라인.
 *  - 8조 SIM-Oriented Single Core: SimContext.phoneRegion만 신뢰.
 */
@Singleton
class PhoneNumberParser @Inject constructor() {

    private val util: PhoneNumberUtil = PhoneNumberUtil.getInstance()

    fun parse(rawNumber: String, simContext: SimContext): PhoneNumberResult {
        val raw = rawNumber.trim()
        if (raw.isEmpty()) return PhoneNumberResult.invalid(rawNumber)

        return try {
            val defaultRegion = simContext.phoneRegion.ifEmpty { "ZZ" }
            val parsed = util.parse(raw, defaultRegion)
            val isValid = util.isValidNumber(parsed)
            val e164 = util.format(parsed, PhoneNumberUtil.PhoneNumberFormat.E164)
            val national = util.format(parsed, PhoneNumberUtil.PhoneNumberFormat.NATIONAL)
            val international = util.format(parsed, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)
            val region = util.getRegionCodeForNumber(parsed) ?: defaultRegion
            val type = util.getNumberType(parsed).name

            PhoneNumberResult(
                isValid = isValid,
                rawInput = rawNumber,
                e164 = e164,
                national = national,
                international = international,
                regionCode = region,
                numberType = type,
            )
        } catch (e: NumberParseException) {
            PhoneNumberResult.invalid(rawNumber)
        }
    }
}
