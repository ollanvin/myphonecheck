package app.callcheck.mobile.core.util

import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber

object PhoneNumberNormalizer {

    private val phoneNumberUtil = PhoneNumberUtil.getInstance()

    data class NormalizedNumber(
        val e164: String,
        val national: String,
        val international: String,
        val countryCode: String,
        val nationalNumber: String,
        val areaCode: String?,
        val isValid: Boolean,
    )

    fun normalize(phoneNumber: String, defaultCountry: String = "US"): NormalizedNumber? {
        return try {
            val parsed = phoneNumberUtil.parse(phoneNumber, defaultCountry)
            if (!phoneNumberUtil.isValidNumber(parsed)) {
                return null
            }
            normalizeInternal(parsed)
        } catch (e: NumberParseException) {
            // Try to parse without country code
            try {
                val cleanNumber = phoneNumber.replace(Regex("[^\\d+]"), "")
                val parsed = phoneNumberUtil.parseAndKeepRawInput(cleanNumber, defaultCountry)
                if (phoneNumberUtil.isValidNumber(parsed)) {
                    normalizeInternal(parsed)
                } else {
                    null
                }
            } catch (e2: Exception) {
                null
            }
        }
    }

    fun isValidPhoneNumber(phoneNumber: String, defaultCountry: String = "US"): Boolean {
        return try {
            val parsed = phoneNumberUtil.parse(phoneNumber, defaultCountry)
            phoneNumberUtil.isValidNumber(parsed)
        } catch (e: Exception) {
            false
        }
    }

    fun getCountryCodeForNumber(phoneNumber: String, defaultCountry: String = "US"): String? {
        return try {
            val parsed = phoneNumberUtil.parse(phoneNumber, defaultCountry)
            val countryCode = phoneNumberUtil.getRegionCodeForNumber(parsed)
            if (countryCode.isNullOrEmpty() || countryCode == "ZZ") {
                null
            } else {
                countryCode
            }
        } catch (e: Exception) {
            null
        }
    }

    fun formatE164(phoneNumber: String, defaultCountry: String = "US"): String? {
        return try {
            val parsed = phoneNumberUtil.parse(phoneNumber, defaultCountry)
            phoneNumberUtil.format(parsed, PhoneNumberUtil.PhoneNumberFormat.E164)
        } catch (e: Exception) {
            null
        }
    }

    fun formatNational(phoneNumber: String, defaultCountry: String = "US"): String? {
        return try {
            val parsed = phoneNumberUtil.parse(phoneNumber, defaultCountry)
            phoneNumberUtil.format(parsed, PhoneNumberUtil.PhoneNumberFormat.NATIONAL)
        } catch (e: Exception) {
            null
        }
    }

    fun formatInternational(phoneNumber: String, defaultCountry: String = "US"): String? {
        return try {
            val parsed = phoneNumberUtil.parse(phoneNumber, defaultCountry)
            phoneNumberUtil.format(parsed, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)
        } catch (e: Exception) {
            null
        }
    }

    fun extractAreaCode(phoneNumber: String, defaultCountry: String = "US"): String? {
        return try {
            val parsed = phoneNumberUtil.parse(phoneNumber, defaultCountry)
            val areaCodeLength = 3
            val nationalNumber = parsed.nationalNumber.toString()

            if (nationalNumber.length >= areaCodeLength) {
                nationalNumber.substring(0, areaCodeLength)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun normalizeInternal(parsedNumber: Phonenumber.PhoneNumber): NormalizedNumber {
        val countryCode = phoneNumberUtil.getRegionCodeForNumber(parsedNumber) ?: "UNKNOWN"
        val e164 = phoneNumberUtil.format(parsedNumber, PhoneNumberUtil.PhoneNumberFormat.E164)
        val national = phoneNumberUtil.format(parsedNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL)
        val international = phoneNumberUtil.format(parsedNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)
        val nationalNumber = parsedNumber.nationalNumber.toString()
        val areaCode = extractAreaCodeFromParsed(parsedNumber, countryCode)
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

    private fun extractAreaCodeFromParsed(
        parsedNumber: Phonenumber.PhoneNumber,
        countryCode: String
    ): String? {
        return try {
            val nationalNumber = parsedNumber.nationalNumber.toString()
            val areaCodeLength = 3

            if (nationalNumber.length >= areaCodeLength) {
                nationalNumber.substring(0, areaCodeLength)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
