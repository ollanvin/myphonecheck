package app.myphonecheck.mobile.core.globalengine.parsing.phone

import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 전화번호 유효성 + 분류 (Architecture v2.0.0 §30).
 *
 * Surface(CallCheck/MessageCheck/PushCheck)는 본 검증기를 통해 분기.
 */
@Singleton
class PhoneNumberValidator @Inject constructor() {

    private val util: PhoneNumberUtil = PhoneNumberUtil.getInstance()

    /** 유효 번호인지(국가 길이/prefix 규칙 포함). */
    fun isValid(rawNumber: String, defaultRegion: String): Boolean {
        return try {
            val parsed = util.parse(rawNumber, defaultRegion.ifEmpty { "ZZ" })
            util.isValidNumber(parsed)
        } catch (e: NumberParseException) {
            false
        }
    }

    /** Possible(예: 자릿수만) 검증 — 사용자 입력 즉시 피드백용. */
    fun isPossible(rawNumber: String, defaultRegion: String): Boolean {
        return try {
            val parsed = util.parse(rawNumber, defaultRegion.ifEmpty { "ZZ" })
            util.isPossibleNumber(parsed)
        } catch (e: NumberParseException) {
            false
        }
    }

    /** 모바일 / 고정 / 프리미엄 등 분류. */
    fun classify(rawNumber: String, defaultRegion: String): PhoneNumberUtil.PhoneNumberType {
        return try {
            val parsed = util.parse(rawNumber, defaultRegion.ifEmpty { "ZZ" })
            util.getNumberType(parsed)
        } catch (e: NumberParseException) {
            PhoneNumberUtil.PhoneNumberType.UNKNOWN
        }
    }
}
