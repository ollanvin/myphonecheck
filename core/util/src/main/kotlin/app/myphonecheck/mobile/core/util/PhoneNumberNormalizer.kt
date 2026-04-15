package app.myphonecheck.mobile.core.util

import app.myphonecheck.mobile.core.model.NumberParseInput
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber

object PhoneNumberNormalizer {

    private val phoneNumberUtil = PhoneNumberUtil.getInstance()
    private val globalNumberEngine = DefaultGlobalNumberEngine(phoneNumberUtil)

    data class NormalizedNumber(
        val e164: String,
        val national: String,
        val international: String,
        val countryCode: String,
        val nationalNumber: String,
        val areaCode: String?,
        val isValid: Boolean,
    )

    fun normalize(phoneNumber: String, defaultCountry: String = "ZZ"): NormalizedNumber? {
        val result = globalNumberEngine.resolve(
            NumberParseInput(
                rawNumber = phoneNumber,
                defaultCountryCode = defaultCountry,
                devicePatternProfile = GlobalNumberEngineProfileStore.current(),
            ),
        )
        if (!result.isValid || !result.canonicalNumber.startsWith("+")) {
            return null
        }

        return try {
            val parsed = phoneNumberUtil.parse(result.canonicalNumber, "ZZ")
            normalizeInternal(parsed)
        } catch (_: Exception) {
            null
        }
    }

    fun isValidPhoneNumber(phoneNumber: String, defaultCountry: String = "ZZ"): Boolean {
        return globalNumberEngine.resolve(
            NumberParseInput(
                rawNumber = phoneNumber,
                defaultCountryCode = defaultCountry,
                devicePatternProfile = GlobalNumberEngineProfileStore.current(),
            ),
        ).isValid
    }

    fun getCountryCodeForNumber(phoneNumber: String, defaultCountry: String = "ZZ"): String? {
        return globalNumberEngine.resolve(
            NumberParseInput(
                rawNumber = phoneNumber,
                defaultCountryCode = defaultCountry,
                devicePatternProfile = GlobalNumberEngineProfileStore.current(),
            ),
        ).resolvedCountryCode
    }

    fun formatE164(phoneNumber: String, defaultCountry: String = "ZZ"): String? {
        return globalNumberEngine.resolve(
            NumberParseInput(
                rawNumber = phoneNumber,
                defaultCountryCode = defaultCountry,
                devicePatternProfile = GlobalNumberEngineProfileStore.current(),
            ),
        ).takeIf { it.isValid && it.canonicalNumber.startsWith("+") }?.canonicalNumber
    }

    fun formatNational(phoneNumber: String, defaultCountry: String = "ZZ"): String? {
        return globalNumberEngine.resolve(
            NumberParseInput(
                rawNumber = phoneNumber,
                defaultCountryCode = defaultCountry,
                devicePatternProfile = GlobalNumberEngineProfileStore.current(),
            ),
        ).nationalFormat
    }

    fun formatInternational(phoneNumber: String, defaultCountry: String = "ZZ"): String? {
        return globalNumberEngine.resolve(
            NumberParseInput(
                rawNumber = phoneNumber,
                defaultCountryCode = defaultCountry,
                devicePatternProfile = GlobalNumberEngineProfileStore.current(),
            ),
        ).internationalFormat
    }

    fun extractAreaCode(phoneNumber: String, defaultCountry: String = "ZZ"): String? {
        return try {
            val parsed = phoneNumberUtil.parse(phoneNumber, defaultCountry)
            extractAreaCodeFromParsed(parsed)
        } catch (_: Exception) {
            null
        }
    }

    fun inferCountryFromNumber(phoneNumber: String): String? {
        return globalNumberEngine.resolve(
            NumberParseInput(
                rawNumber = phoneNumber,
                defaultCountryCode = "ZZ",
                devicePatternProfile = GlobalNumberEngineProfileStore.current(),
            ),
        ).resolvedCountryCode
    }

    fun getPhonePrefix(countryCode: String): String? {
        return try {
            val callingCode = phoneNumberUtil.getCountryCodeForRegion(countryCode.uppercase())
            if (callingCode > 0) "+$callingCode" else null
        } catch (_: Exception) {
            null
        }
    }

    fun canonicalKey(
        phoneNumber: String,
        defaultCountry: String = "ZZ",
    ): String {
        return globalNumberEngine.canonicalKey(
            rawNumber = phoneNumber,
            defaultCountryCode = defaultCountry,
            devicePatternProfile = GlobalNumberEngineProfileStore.current(),
        )
    }

    private fun normalizeInternal(parsedNumber: Phonenumber.PhoneNumber): NormalizedNumber {
        val countryCode = phoneNumberUtil.getRegionCodeForNumber(parsedNumber) ?: "UNKNOWN"
        val e164 = phoneNumberUtil.format(parsedNumber, PhoneNumberUtil.PhoneNumberFormat.E164)
        val national = phoneNumberUtil.format(parsedNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL)
        val international = phoneNumberUtil.format(parsedNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)
        val nationalNumber = parsedNumber.nationalNumber.toString()
        val areaCode = extractAreaCodeFromParsed(parsedNumber)
        val isValid = phoneNumberUtil.isValidNumber(parsedNumber)

        return NormalizedNumber(
            e164 = e164,
            national = national,
            international = international,
            countryCode = countryCode,
            nationalNumber = nationalNumber,
            areaCode = areaCode,
            isValid = isValid,
        )
    }

    private fun extractAreaCodeFromParsed(parsedNumber: Phonenumber.PhoneNumber): String? {
        return try {
            val areaCodeLength = phoneNumberUtil.getLengthOfGeographicalAreaCode(parsedNumber)
            if (areaCodeLength <= 0) {
                return null
            }

            val nationalNumber = parsedNumber.nationalNumber.toString()
            if (nationalNumber.length >= areaCodeLength) {
                nationalNumber.substring(0, areaCodeLength)
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }
}
