package app.callcheck.mobile.core.util

import app.callcheck.mobile.core.model.NumberSourceContext
import app.callcheck.mobile.core.model.PhoneNumberContext
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil

/**
 * 전화번호 문맥 빌더 — 번호 "표준화"가 아니라 번호 "문맥 구성".
 *
 * ┌──────────────────────────────────────────────────────────────┐
 * │ 기기 컨텍스트 동기화 원칙                                     │
 * ├──────────────────────────────────────────────────────────────┤
 * │ 1. rawNumber는 기기 원본 그대로 보존한다                     │
 * │ 2. 정규화 결과(deviceCanonicalNumber)는 비교 전용이다        │
 * │ 3. 검색에는 searchVariants 전체를 사용한다                   │
 * │ 4. 국가 코드는 기기(SIM/Network/Locale)에서 자동 탐지한다   │
 * │ 5. 앱이 자체 규칙으로 번호 형식을 정의하지 않는다            │
 * └──────────────────────────────────────────────────────────────┘
 *
 * 사용법:
 * ```kotlin
 * val builder = PhoneNumberContextBuilder()
 * val context = builder.build(
 *     rawNumber = "010-1234-5678",
 *     deviceCountryCode = "KR",
 *     sourceContext = NumberSourceContext.INCOMING_CALL,
 * )
 * // context.rawNumber          → "010-1234-5678"  (원본 보존)
 * // context.deviceCanonicalNumber → "+821012345678" (비교 전용)
 * // context.searchVariants     → ["010-1234-5678", "01012345678", "010-1234-5678", "+821012345678"]
 * ```
 */
class PhoneNumberContextBuilder {

    private val phoneUtil: PhoneNumberUtil = PhoneNumberUtil.getInstance()

    /**
     * rawNumber와 기기 컨텍스트로부터 [PhoneNumberContext]를 구성한다.
     *
     * @param rawNumber 기기가 제공한 원본 번호. 그대로 보존된다.
     * @param deviceCountryCode 기기에서 탐지된 ISO 3166-1 alpha-2 국가 코드.
     *                          null이면 국가 추정 없이 처리한다.
     * @param sourceContext 번호 유입 경로.
     * @return 번호 문맥. rawNumber가 빈 문자열이어도 반환한다 (빈 문맥).
     */
    fun build(
        rawNumber: String,
        deviceCountryCode: String?,
        sourceContext: NumberSourceContext,
    ): PhoneNumberContext {
        val trimmedRaw = rawNumber.trim()

        if (trimmedRaw.isEmpty()) {
            return PhoneNumberContext(
                rawNumber = trimmedRaw,
                deviceCanonicalNumber = "",
                searchVariants = emptyList(),
                deviceCountryCode = deviceCountryCode,
                sourceContext = sourceContext,
                isParseable = false,
            )
        }

        // libphonenumber 파싱 시도
        val parseResult = tryParse(trimmedRaw, deviceCountryCode)

        val deviceCanonicalNumber: String
        val searchVariants: MutableList<String>
        val isParseable: Boolean

        if (parseResult != null) {
            // ── 파싱 성공: E.164 기반 canonical + 다양한 variants ──
            isParseable = true
            deviceCanonicalNumber = phoneUtil.format(
                parseResult, PhoneNumberUtil.PhoneNumberFormat.E164
            )

            searchVariants = buildParseableVariants(
                trimmedRaw, parseResult, deviceCountryCode
            )
        } else {
            // ── 파싱 실패: 짧은 번호(114, 1345) 또는 비표준 형식 ──
            isParseable = false
            deviceCanonicalNumber = extractDigitsOnly(trimmedRaw)

            searchVariants = buildUnparseableVariants(trimmedRaw)
        }

        return PhoneNumberContext(
            rawNumber = trimmedRaw,
            deviceCanonicalNumber = deviceCanonicalNumber,
            searchVariants = searchVariants.distinct(),
            deviceCountryCode = deviceCountryCode,
            sourceContext = sourceContext,
            isParseable = isParseable,
        )
    }

    // ═══════════════════════════════════════════════════════════
    // Internal: 파싱 시도
    // ═══════════════════════════════════════════════════════════

    private fun tryParse(
        number: String,
        defaultCountry: String?,
    ): com.google.i18n.phonenumbers.Phonenumber.PhoneNumber? {
        val region = defaultCountry?.uppercase() ?: "US"
        return try {
            val parsed = phoneUtil.parse(number, region)
            if (phoneUtil.isValidNumber(parsed)) parsed else null
        } catch (e: NumberParseException) {
            // 재시도: 숫자+기호만 추출 후 파싱
            try {
                val cleaned = number.replace(Regex("[^\\d+]"), "")
                val parsed = phoneUtil.parse(cleaned, region)
                if (phoneUtil.isValidNumber(parsed)) parsed else null
            } catch (e2: Exception) {
                null
            }
        }
    }

    // ═══════════════════════════════════════════════════════════
    // Internal: 파싱 성공 시 searchVariants 생성
    // ═══════════════════════════════════════════════════════════

    /**
     * 파싱 성공한 번호의 검색 변형을 생성한다.
     *
     * 생성 전략:
     * 1. rawNumber 그대로 (사용자 입력 원본)
     * 2. digits only (하이픈/공백/괄호 제거)
     * 3. national format (libphonenumber 기준)
     * 4. E.164 format
     * 5. nationalNumber만 (국가코드 제외 숫자)
     */
    private fun buildParseableVariants(
        rawNumber: String,
        parsed: com.google.i18n.phonenumbers.Phonenumber.PhoneNumber,
        deviceCountryCode: String?,
    ): MutableList<String> {
        val variants = mutableListOf<String>()

        // 1. rawNumber 그대로
        variants.add(rawNumber)

        // 2. digits only
        val digitsOnly = extractDigitsOnly(rawNumber)
        variants.add(digitsOnly)

        // 3. national format
        try {
            val national = phoneUtil.format(
                parsed, PhoneNumberUtil.PhoneNumberFormat.NATIONAL
            )
            variants.add(national)
        } catch (_: Exception) {
            // format 실패 시 무시
        }

        // 4. E.164 format
        try {
            val e164 = phoneUtil.format(
                parsed, PhoneNumberUtil.PhoneNumberFormat.E164
            )
            variants.add(e164)
        } catch (_: Exception) {
            // format 실패 시 무시
        }

        // 5. nationalNumber만 (국가코드 제외 순수 번호)
        val nationalNumber = parsed.nationalNumber.toString()
        variants.add(nationalNumber)

        return variants
    }

    // ═══════════════════════════════════════════════════════════
    // Internal: 파싱 실패 시 searchVariants 생성
    // ═══════════════════════════════════════════════════════════

    /**
     * 파싱 실패한 번호(짧은 번호, 비표준 형식)의 검색 변형을 생성한다.
     *
     * 생성 전략:
     * 1. rawNumber 그대로
     * 2. digits only (숫자만)
     */
    private fun buildUnparseableVariants(rawNumber: String): MutableList<String> {
        val variants = mutableListOf<String>()

        variants.add(rawNumber)

        val digitsOnly = extractDigitsOnly(rawNumber)
        if (digitsOnly != rawNumber && digitsOnly.isNotEmpty()) {
            variants.add(digitsOnly)
        }

        return variants
    }

    // ═══════════════════════════════════════════════════════════
    // Internal: 유틸리티
    // ═══════════════════════════════════════════════════════════

    /**
     * 숫자와 선행 '+' 기호만 추출한다.
     * 예: "010-1234-5678" → "01012345678"
     * 예: "+82-10-1234-5678" → "+821012345678"
     */
    private fun extractDigitsOnly(number: String): String {
        val hasPlus = number.startsWith("+")
        val digits = number.replace(Regex("[^\\d]"), "")
        return if (hasPlus) "+$digits" else digits
    }
}
