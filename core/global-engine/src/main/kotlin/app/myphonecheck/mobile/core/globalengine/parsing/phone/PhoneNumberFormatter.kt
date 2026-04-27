package app.myphonecheck.mobile.core.globalengine.parsing.phone

import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import javax.inject.Inject
import javax.inject.Singleton

/**
 * E.164 → UI 표시용 양식 변환 (Architecture v2.0.0 §30).
 *
 * 디바이스 SIM region 기준으로 동일 국가는 NATIONAL, 다른 국가는 INTERNATIONAL.
 */
@Singleton
class PhoneNumberFormatter @Inject constructor() {

    private val util: PhoneNumberUtil = PhoneNumberUtil.getInstance()

    fun formatForDisplay(e164: String, simRegion: String): String {
        return try {
            val parsed = util.parse(e164, simRegion.ifEmpty { "ZZ" })
            val numberRegion = util.getRegionCodeForNumber(parsed)
            val format = if (numberRegion == simRegion) {
                PhoneNumberUtil.PhoneNumberFormat.NATIONAL
            } else {
                PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL
            }
            util.format(parsed, format)
        } catch (e: NumberParseException) {
            e164
        }
    }
}
