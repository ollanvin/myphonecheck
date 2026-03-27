package app.callcheck.mobile.core.util

import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber

/**
 * 전화번호 정규화 유틸리티.
 *
 * 설계 원칙:
 * - 모든 public 함수의 defaultCountry는 호출자가 반드시 전달해야 함
 * - 디폴트 "ZZ" (unknown region) → libphonenumber가 국제 포맷(+로 시작)만 파싱 가능
 * - 디바이스 국가 없이 호출하면 로컬 번호 파싱 불가 → null 반환 (안전 실패)
 * - 하드코딩된 area code 길이 금지 → libphonenumber getLengthOfGeographicalAreaCode() 사용
 *
 * 호출자 책임:
 * - CallCheckScreeningService → CountryConfigProvider.detectCountry() 결과 전달
 * - UI 레이어 → 동일하게 디바이스 국가 전달
 */
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

    /**
     * 전화번호를 정규화합니다.
     *
     * @param phoneNumber 원시 전화번호
     * @param defaultCountry ISO 3166-1 alpha-2 국가 코드 (예: "KR", "US", "JP").
     *        "ZZ"가 기본값 — 국제 포맷(+로 시작)이 아니면 파싱 실패 → null 반환.
     *        호출자는 반드시 디바이스 국가를 전달해야 합니다.
     */
    fun normalize(phoneNumber: String, defaultCountry: String = "ZZ"): NormalizedNumber? {
        return try {
            val parsed = phoneNumberUtil.parse(phoneNumber, defaultCountry)
            if (!phoneNumberUtil.isValidNumber(parsed)) {
                return null
            }
            normalizeInternal(parsed)
        } catch (e: NumberParseException) {
            // + 접두사가 있으면 국제 포맷으로 재시도
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

    /**
     * 전화번호 유효성 검증.
     *
     * @param defaultCountry 디바이스 국가. "ZZ" 기본값 → 국제 포맷만 검증 가능.
     */
    fun isValidPhoneNumber(phoneNumber: String, defaultCountry: String = "ZZ"): Boolean {
        return try {
            val parsed = phoneNumberUtil.parse(phoneNumber, defaultCountry)
            phoneNumberUtil.isValidNumber(parsed)
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 전화번호에서 국가 코드 추출.
     *
     * @param defaultCountry 디바이스 국가. "ZZ" 기본값.
     */
    fun getCountryCodeForNumber(phoneNumber: String, defaultCountry: String = "ZZ"): String? {
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

    /**
     * E.164 포맷으로 변환.
     *
     * @param defaultCountry 디바이스 국가. "ZZ" 기본값 → 로컬 번호 변환 불가.
     */
    fun formatE164(phoneNumber: String, defaultCountry: String = "ZZ"): String? {
        return try {
            val parsed = phoneNumberUtil.parse(phoneNumber, defaultCountry)
            phoneNumberUtil.format(parsed, PhoneNumberUtil.PhoneNumberFormat.E164)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 국내 포맷으로 변환.
     *
     * @param defaultCountry 디바이스 국가. "ZZ" 기본값.
     */
    fun formatNational(phoneNumber: String, defaultCountry: String = "ZZ"): String? {
        return try {
            val parsed = phoneNumberUtil.parse(phoneNumber, defaultCountry)
            phoneNumberUtil.format(parsed, PhoneNumberUtil.PhoneNumberFormat.NATIONAL)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 국제 포맷으로 변환.
     *
     * @param defaultCountry 디바이스 국가. "ZZ" 기본값.
     */
    fun formatInternational(phoneNumber: String, defaultCountry: String = "ZZ"): String? {
        return try {
            val parsed = phoneNumberUtil.parse(phoneNumber, defaultCountry)
            phoneNumberUtil.format(parsed, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 지역 번호(area code) 추출.
     *
     * libphonenumber의 getLengthOfGeographicalAreaCode()를 사용하여
     * 국가별 area code 길이를 동적으로 결정합니다.
     * 하드코딩된 길이(3)를 사용하지 않습니다.
     *
     * 예시:
     * - US: 212-555-1234 → area code "212" (길이 3)
     * - KR: 02-555-0199 → area code "2" (길이 1~2)
     * - JP: 03-1234-5678 → area code "3" (길이 1~4)
     *
     * @param defaultCountry 디바이스 국가. "ZZ" 기본값.
     */
    fun extractAreaCode(phoneNumber: String, defaultCountry: String = "ZZ"): String? {
        return try {
            val parsed = phoneNumberUtil.parse(phoneNumber, defaultCountry)
            extractAreaCodeFromParsed(parsed)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 전화번호의 + prefix에서 국가를 추정합니다.
     *
     * deviceCountry가 "ZZ"(unknown)일 때, 번호가 +로 시작하면
     * libphonenumber를 통해 국가를 추정할 수 있습니다.
     *
     * 예시:
     * - "+82-2-555-0199" → "KR"
     * - "+1-212-555-0199" → "US"
     * - "02-555-0199" → null (+ 없으면 추정 불가)
     *
     * @param phoneNumber 원시 전화번호
     * @return ISO 3166-1 alpha-2 국가 코드, 또는 null
     */
    fun inferCountryFromNumber(phoneNumber: String): String? {
        val cleaned = phoneNumber.replace(Regex("[^\\d+]"), "")
        if (!cleaned.startsWith("+")) return null

        return try {
            // "ZZ"로 파싱해도 +로 시작하면 국제 포맷으로 인식
            val parsed = phoneNumberUtil.parse(cleaned, "ZZ")
            val region = phoneNumberUtil.getRegionCodeForNumber(parsed)
            if (region.isNullOrEmpty() || region == "ZZ" || region == "001") {
                null
            } else {
                region
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * ISO 3166-1 alpha-2 국가 코드 → 국제 전화 접두사 반환.
     *
     * 예시:
     * - "KR" → "+82"
     * - "US" → "+1"
     * - "JP" → "+81"
     * - "ZZ" → null
     *
     * @param countryCode ISO 3166-1 alpha-2 국가 코드
     * @return "+국가코드" 형태, 또는 null (알 수 없는 국가)
     */
    fun getPhonePrefix(countryCode: String): String? {
        return try {
            val callingCode = phoneNumberUtil.getCountryCodeForRegion(countryCode.uppercase())
            if (callingCode > 0) "+$callingCode" else null
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

    /**
     * libphonenumber 정규 API로 area code 추출.
     *
     * getLengthOfGeographicalAreaCode()가 0을 반환하면
     * 해당 번호에 지역 코드가 없는 것 (모바일, 톨프리 등).
     */
    private fun extractAreaCodeFromParsed(
        parsedNumber: Phonenumber.PhoneNumber,
    ): String? {
        return try {
            val areaCodeLength = phoneNumberUtil.getLengthOfGeographicalAreaCode(parsedNumber)
            if (areaCodeLength <= 0) return null

            val nationalNumber = parsedNumber.nationalNumber.toString()
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
