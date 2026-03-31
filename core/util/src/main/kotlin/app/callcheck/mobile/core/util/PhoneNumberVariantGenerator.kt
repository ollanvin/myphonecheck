package app.callcheck.mobile.core.util

import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber

/**
 * 전화번호 검색 변환 생성기.
 *
 * 동일한 전화번호가 웹에서 다양한 포맷으로 존재합니다:
 * - E.164: +821012345678
 * - 국제 하이픈: +82-10-1234-5678
 * - 국제 공백: +82 10 1234 5678
 * - 국내 연속: 01012345678
 * - 국내 하이픈: 010-1234-5678
 * - 국내 공백: 010 1234 5678
 * - 국내 괄호: (02) 555-0199 (지역번호)
 * - 국가코드 +없음: 821012345678
 *
 * 이 클래스는 하나의 정규화된 번호로부터 웹 검색에 필요한
 * 모든 포맷 변환을 생성합니다.
 *
 * 설계 원칙:
 * - libphonenumber 정규 API만 사용 (하드코딩 금지)
 * - 국가별 포맷 규칙은 libphonenumber에 위임
 * - 최대 8개 변환 (검색 쿼리 길이 제한 준수)
 * - OR 쿼리 조합은 표준 검색엔진 구문 사용
 */
object PhoneNumberVariantGenerator {

    private val phoneNumberUtil = PhoneNumberUtil.getInstance()

    /**
     * 검색 쿼리용 최대 변환 수.
     *
     * Google 쿼리 길이 제한(~2048자)과 검색 품질 간 균형.
     * 8개 변환 × 평균 20자 = ~160자 + OR 연산자 ≈ 200자. 충분한 여유.
     */
    private const val MAX_VARIANTS = 8

    /**
     * 전화번호의 모든 검색 변환을 생성합니다.
     *
     * @param phoneNumber 정규화된 전화번호 (E.164 권장, 로컬 포맷도 가능)
     * @param countryCode ISO 3166-1 alpha-2 국가 코드 (예: "KR", "US")
     * @return 중복 제거된 포맷 변환 리스트 (최대 MAX_VARIANTS개)
     */
    fun generateVariants(phoneNumber: String, countryCode: String = "ZZ"): List<String> {
        val variants = linkedSetOf<String>()

        // 원본 번호 항상 포함
        val cleaned = phoneNumber.replace(Regex("[^\\d+]"), "")
        if (cleaned.isNotEmpty()) {
            variants.add(cleaned)
        }

        try {
            val parsed = phoneNumberUtil.parse(phoneNumber, countryCode)
            if (!phoneNumberUtil.isValidNumber(parsed)) {
                // 유효하지 않은 번호: 원본만 반환
                return variants.toList()
            }

            // === libphonenumber 정규 포맷 ===
            addLibPhoneNumberFormats(parsed, variants)

            // === 구분자 변환 (하이픈 → 공백, 하이픈 → 제거, 공백 → 제거) ===
            addSeparatorVariants(parsed, variants)

            // === 국가코드 + 없음 변환 ===
            addWithoutPlusVariants(parsed, variants)

            // === 국가별 특수 포맷 ===
            addCountrySpecificVariants(parsed, countryCode, variants)

        } catch (e: NumberParseException) {
            // 파싱 실패: 원본과 기본 변환만 반환
            addBasicVariants(phoneNumber, variants)
        } catch (e: Exception) {
            // 예상치 못한 에러: 원본만 반환
        }

        return variants.toList().take(MAX_VARIANTS)
    }

    /**
     * 변환 리스트를 검색엔진 OR 쿼리로 조합합니다.
     *
     * 출력 예시:
     * "01012345678" OR "010-1234-5678" OR "+82-10-1234-5678"
     *
     * 각 변환을 쌍따옴표로 감싸 정확 매치를 강제합니다.
     * OR 연산자는 Google, Bing, DuckDuckGo, Naver, Yahoo, Yandex에서
     * 표준으로 지원됩니다.
     *
     * @param variants generateVariants()의 결과
     * @return 검색엔진에 전달할 OR 쿼리 문자열
     */
    fun buildOrQuery(variants: List<String>): String {
        if (variants.isEmpty()) return ""
        if (variants.size == 1) return "\"${variants.first()}\""

        return variants.joinToString(" OR ") { "\"$it\"" }
    }

    /**
     * Baidu 전용 OR 쿼리 (파이프 구분자).
     *
     * Baidu는 OR 대신 | 구분자를 사용합니다.
     * 쌍따옴표 정확 매치는 동일하게 지원.
     */
    fun buildBaiduOrQuery(variants: List<String>): String {
        if (variants.isEmpty()) return ""
        if (variants.size == 1) return "\"${variants.first()}\""

        return variants.joinToString(" | ") { "\"$it\"" }
    }

    /**
     * 단일 번호를 OR 쿼리로 변환하는 편의 함수.
     *
     * generateVariants() + buildOrQuery()를 한 번에 수행.
     *
     * @param phoneNumber 정규화된 전화번호
     * @param countryCode ISO 3166-1 alpha-2 국가 코드
     * @return 검색엔진에 전달할 OR 쿼리 문자열
     */
    fun toSearchQuery(phoneNumber: String, countryCode: String = "ZZ"): String {
        val variants = generateVariants(phoneNumber, countryCode)
        return buildOrQuery(variants)
    }

    // ═══════════════════════════════════════════════
    // Internal: libphonenumber 정규 포맷
    // ═══════════════════════════════════════════════

    private fun addLibPhoneNumberFormats(
        parsed: Phonenumber.PhoneNumber,
        variants: LinkedHashSet<String>,
    ) {
        // E.164: +821012345678
        val e164 = phoneNumberUtil.format(parsed, PhoneNumberUtil.PhoneNumberFormat.E164)
        variants.add(e164)

        // National: 010-1234-5678 (국가별 표준 구분자 포함)
        val national = phoneNumberUtil.format(parsed, PhoneNumberUtil.PhoneNumberFormat.NATIONAL)
        variants.add(national)

        // International: +82 10-1234-5678 (국가별 표준)
        val international = phoneNumberUtil.format(
            parsed,
            PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL,
        )
        variants.add(international)
    }

    // ═══════════════════════════════════════════════
    // Internal: 구분자 변환
    // ═══════════════════════════════════════════════

    private fun addSeparatorVariants(
        parsed: Phonenumber.PhoneNumber,
        variants: LinkedHashSet<String>,
    ) {
        val national = phoneNumberUtil.format(parsed, PhoneNumberUtil.PhoneNumberFormat.NATIONAL)
        val international = phoneNumberUtil.format(
            parsed,
            PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL,
        )

        // National 숫자만: 01012345678
        val nationalDigitsOnly = national.replace(Regex("[^\\d]"), "")
        if (nationalDigitsOnly.isNotEmpty()) {
            variants.add(nationalDigitsOnly)
        }

        // National 공백 구분: 010 1234 5678
        val nationalSpaced = national.replace('-', ' ')
            .replace(Regex("[()]"), "")
            .replace(Regex("\\s+"), " ")
            .trim()
        if (nationalSpaced != national) {
            variants.add(nationalSpaced)
        }

        // International 하이픈 구분: +82-10-1234-5678
        val internationalHyphen = international.replace(' ', '-')
        if (internationalHyphen != international) {
            variants.add(internationalHyphen)
        }
    }

    // ═══════════════════════════════════════════════
    // Internal: + 접두사 제거 변환
    // ═══════════════════════════════════════════════

    private fun addWithoutPlusVariants(
        parsed: Phonenumber.PhoneNumber,
        variants: LinkedHashSet<String>,
    ) {
        val e164 = phoneNumberUtil.format(parsed, PhoneNumberUtil.PhoneNumberFormat.E164)

        // +없이 국가코드 포함: 821012345678
        if (e164.startsWith("+")) {
            variants.add(e164.removePrefix("+"))
        }
    }

    // ═══════════════════════════════════════════════
    // Internal: 국가별 특수 포맷
    // ═══════════════════════════════════════════════

    /**
     * 국가별 특수 포맷 변환.
     *
     * 각 국가의 일반적인 비표준 표기법을 생성합니다.
     * libphonenumber가 생성하지 않는 포맷만 추가합니다.
     *
     * - US: (212) 555-1234 (괄호 지역번호)
     * - KR: 없음 (libphonenumber National 포맷이 이미 커버)
     * - JP: 없음 (libphonenumber National 포맷이 이미 커버)
     * - CN: 없음 (libphonenumber National 포맷이 이미 커버)
     */
    private fun addCountrySpecificVariants(
        parsed: Phonenumber.PhoneNumber,
        countryCode: String,
        variants: LinkedHashSet<String>,
    ) {
        val region = phoneNumberUtil.getRegionCodeForNumber(parsed) ?: countryCode

        when (region.uppercase()) {
            // NANPA 국가 (US, CA 등): (지역번호) 뒷번호 포맷
            "US", "CA", "PR", "VI", "GU", "AS", "MP" -> {
                addNanpaVariants(parsed, variants)
            }
            // UK: 0-prefix + 공백 포맷
            "GB" -> {
                addUkVariants(parsed, variants)
            }
        }
    }

    /**
     * NANPA 괄호 지역번호 포맷.
     *
     * 예: +12125551234 → (212) 555-1234
     * libphonenumber의 National 포맷이 이미 이 형태를 생성할 수 있지만,
     * 일부 지역에서 (212)555-1234 (공백 없음)도 사용됩니다.
     */
    private fun addNanpaVariants(
        parsed: Phonenumber.PhoneNumber,
        variants: LinkedHashSet<String>,
    ) {
        val areaCodeLength = phoneNumberUtil.getLengthOfGeographicalAreaCode(parsed)
        if (areaCodeLength <= 0) return

        val nationalNumber = parsed.nationalNumber.toString()
        if (nationalNumber.length < areaCodeLength + 1) return

        val areaCode = nationalNumber.substring(0, areaCodeLength)
        val subscriberNumber = nationalNumber.substring(areaCodeLength)

        // (212) 555-1234 — 7자리 가입자번호의 경우 3-4 분할
        if (subscriberNumber.length == 7) {
            val prefix = subscriberNumber.substring(0, 3)
            val line = subscriberNumber.substring(3)
            val parenFormat = "($areaCode) $prefix-$line"
            variants.add(parenFormat)
        }
    }

    /**
     * UK 특수 포맷.
     *
     * UK 번호는 0-prefix가 국내에서 일반적.
     * libphonenumber National은 이미 0-prefix를 포함하지만,
     * 점(.) 구분자 포맷도 웹에 존재합니다.
     */
    private fun addUkVariants(
        parsed: Phonenumber.PhoneNumber,
        variants: LinkedHashSet<String>,
    ) {
        val national = phoneNumberUtil.format(parsed, PhoneNumberUtil.PhoneNumberFormat.NATIONAL)
        // 점 구분자: 020.7946.0958
        val dottedFormat = national.replace(Regex("[\\s-]"), ".")
        if (dottedFormat != national) {
            variants.add(dottedFormat)
        }
    }

    // ═══════════════════════════════════════════════
    // Internal: 파싱 실패 시 기본 변환
    // ═══════════════════════════════════════════════

    /**
     * libphonenumber 파싱 실패 시 기본 변환.
     *
     * 숫자만 추출하여 최소한의 변환을 생성합니다.
     * +가 있으면 국제 번호로 간주하고 +없는 변환도 추가.
     */
    private fun addBasicVariants(phoneNumber: String, variants: LinkedHashSet<String>) {
        val digitsOnly = phoneNumber.replace(Regex("[^\\d]"), "")
        if (digitsOnly.isNotEmpty()) {
            variants.add(digitsOnly)
        }

        // 원본에 구분자가 있으면 구분자 없는 버전 추가
        val cleaned = phoneNumber.replace(Regex("[^\\d+]"), "")
        if (cleaned.isNotEmpty() && cleaned != phoneNumber) {
            variants.add(cleaned)
        }

        // + prefix 제거 버전
        if (phoneNumber.startsWith("+")) {
            variants.add(phoneNumber.removePrefix("+").replace(Regex("[^\\d]"), ""))
        }
    }
}
