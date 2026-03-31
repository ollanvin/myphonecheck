package app.callcheck.mobile.core.util

import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil

/**
 * 인접 번호 매칭기 (Adjacent Number Matcher).
 *
 * ═══════════════════════════════════════════════════════════
 * 문제:
 * 수신된 번호 0212345678이 웹에 없다.
 * 하지만 0212345670 ~ 0212345679는 같은 기관/단체가 쓰는 연번(block).
 * 0212345670이 "서울시청"으로 검색되면, 0212345678도 서울시청일 가능성이 높다.
 *
 * 원리:
 * 통신사/기관은 전화번호를 연속 대역(consecutive block)으로 할당받는다.
 * 조직 규모에 따라 할당 대역이 다르다:
 *
 * - 끝 1자리 대역: 10번호 (같은 부서/직통)
 * - 끝 2자리 대역: 100번호 (같은 부서/층)
 * - 끝 3자리 대역: 1,000번호 (같은 건물/사업부)
 * - 끝 4자리 대역: 10,000번호 (국번 전유 — 삼성, 구글, 정부기관급)
 *
 * 삼성전자: 02-2255-0000 ~ 02-2255-9999 (국번 2255 전유)
 * 서울시청: 02-120-XXXX (대표번호 대역)
 * Google: +1-650-253-0000 ~ +1-650-253-9999
 *
 * 검색 전략:
 * 개별 번호를 하나씩 검색하지 않는다 (10개 → 10배 타임아웃).
 * 대신 "트렁크(trunk)"를 검색한다:
 * - 0212345678 → 트렁크 "021234567" (끝 1자리 제거)
 * - "021234567" 쌍따옴표 검색 → 0212345670~9 전부 매칭
 * - 단일 쿼리로 10개 인접 번호를 커버
 *
 * 좁은 대역부터 넓은 대역으로 순차 확장:
 * 10번호 → 100번호 → 1,000번호 → 10,000번호
 * 첫 번째 히트에서 중단 → 최소 쿼리로 최대 커버
 * ═══════════════════════════════════════════════════════════
 */
object AdjacentNumberMatcher {

    private val phoneNumberUtil = PhoneNumberUtil.getInstance()

    /**
     * 트렁크 검색 쿼리 결과.
     *
     * @param trunkVariants 트렁크의 포맷 변환 리스트 (OR 쿼리 구성용)
     * @param rangeDescription 커버 범위 설명 ("끝 1자리 대역 (10번호)")
     * @param truncatedDigits 제거된 자릿수 (1, 2)
     */
    data class TrunkQuery(
        val trunkVariants: List<String>,
        val rangeDescription: String,
        val truncatedDigits: Int,
    ) {
        /** 표준 OR 쿼리 */
        fun toOrQuery(): String = trunkVariants.joinToString(" OR ") { "\"$it\"" }
    }

    /**
     * 인접 번호 트렁크 쿼리를 생성합니다.
     *
     * 4단계 대역 확장 (좁은 → 넓은 순서):
     * 1. 끝 1자리 대역 (10번호) — 같은 부서/직통 번호
     * 2. 끝 2자리 대역 (100번호) — 같은 부서/층
     * 3. 끝 3자리 대역 (1,000번호) — 같은 건물/사업부
     * 4. 끝 4자리 대역 (10,000번호) — 국번 전유 (삼성/구글/정부기관급)
     *
     * 검색 엔진에 보내는 쿼리 수가 아닌 트렁크 길이만 달라지므로
     * 각 단계는 프로바이더당 1개 쿼리. 타임아웃 부담 최소화.
     *
     * SearchEnrichmentRepositoryImpl에서 좁은 대역부터 시도하고,
     * 첫 번째 히트에서 중단합니다.
     *
     * @param phoneNumber 정규화된 전화번호 (E.164 권장)
     * @param countryCode ISO 3166-1 alpha-2 국가 코드
     * @return 트렁크 쿼리 리스트 (좁은 대역 → 넓은 대역 순서)
     */
    fun generateTrunkQueries(
        phoneNumber: String,
        countryCode: String = "ZZ",
    ): List<TrunkQuery> {
        val queries = mutableListOf<TrunkQuery>()

        try {
            val parsed = phoneNumberUtil.parse(phoneNumber, countryCode)
            if (!phoneNumberUtil.isValidNumber(parsed)) {
                return generateBasicTrunkQueries(phoneNumber)
            }

            val nationalNumber = parsed.nationalNumber.toString()
            val regionCode = phoneNumberUtil.getRegionCodeForNumber(parsed) ?: countryCode
            val countryCodeNum = parsed.countryCode

            // 최소 길이 검증: 국내번호 4자리 이상이어야 트렁크 의미가 있음
            if (nationalNumber.length < 4) return emptyList()

            // 대역 정의: (제거할 자릿수, 최소 필요 국내번호 길이, 설명)
            val trunkLevels = listOf(
                Triple(1, 4, "끝 1자리 대역 (10번호)"),
                Triple(2, 6, "끝 2자리 대역 (100번호)"),
                Triple(3, 7, "끝 3자리 대역 (1,000번호)"),
                Triple(4, 8, "끝 4자리 대역 (10,000번호 — 국번 전유)"),
            )

            for ((dropDigits, minLength, description) in trunkLevels) {
                if (nationalNumber.length < minLength) continue

                val trunk = nationalNumber.dropLast(dropDigits)
                // 트렁크가 너무 짧으면 오탐 위험 → 최소 3자리 이상
                if (trunk.length < 3) continue

                val trunkVariants = buildTrunkVariants(trunk, countryCodeNum, regionCode)
                queries.add(
                    TrunkQuery(
                        trunkVariants = trunkVariants,
                        rangeDescription = description,
                        truncatedDigits = dropDigits,
                    ),
                )
            }

        } catch (e: NumberParseException) {
            return generateBasicTrunkQueries(phoneNumber)
        } catch (e: Exception) {
            // 예상치 못한 에러: 빈 리스트 반환
        }

        return queries
    }

    // ═══════════════════════════════════════════════
    // Internal: 트렁크 포맷 변환 생성
    // ═══════════════════════════════════════════════

    /**
     * 트렁크 숫자열의 포맷 변환을 생성합니다.
     *
     * 트렁크는 "불완전한 번호"이므로 libphonenumber로 포맷할 수 없습니다.
     * 대신 국가별 일반적인 구분자 패턴을 적용합니다.
     *
     * 예: 한국 트렁크 "021234567" (끝 1자리 제거)
     * → "021234567", "02-1234-567", "02 1234 567", "+8221234567", "8221234567"
     *
     * 핵심: 쌍따옴표 검색이므로 트렁크가 웹페이지 텍스트의
     * 부분 문자열로 매칭됩니다.
     * "021234567" → "0212345670", "0212345671" ... 전부 매칭.
     */
    private fun buildTrunkVariants(
        nationalTrunk: String,
        countryCode: Int,
        regionCode: String,
    ): List<String> {
        val variants = linkedSetOf<String>()

        // 1. 국내 숫자만 (구분자 없음): "021234567"
        val withNationalPrefix = addNationalPrefix(nationalTrunk, regionCode)
        variants.add(withNationalPrefix)

        // 2. 국내 숫자만 (prefix 없이): "21234567" (일부 국가)
        if (withNationalPrefix != nationalTrunk) {
            variants.add(nationalTrunk)
        }

        // 3. E.164 prefix 포함 (+ 없이): "821234567" or "8221234567"
        val withCountryCode = "$countryCode$nationalTrunk"
        variants.add(withCountryCode)

        // 4. E.164 prefix 포함 (+ 포함): "+821234567" or "+8221234567"
        variants.add("+$withCountryCode")

        // 5. 국가별 구분자 패턴 적용
        val separatorVariants = applySeparatorPatterns(withNationalPrefix, regionCode)
        variants.addAll(separatorVariants)

        return variants.toList().take(6) // 쿼리 길이 제한
    }

    /**
     * 국가별 National prefix (trunk prefix) 추가.
     *
     * libphonenumber의 public API인 getCountryCodeForRegion()과
     * getNddPrefixForRegion()을 사용합니다.
     *
     * - 한국: "0" prefix (02, 010, 031 등)
     * - 일본: "0" prefix (03, 06, 090 등)
     * - 영국: "0" prefix (020, 07 등)
     * - 미국: "1" (NANPA long distance prefix, 일반적으로 생략)
     * - 중국: "0" (장거리 prefix)
     */
    private fun addNationalPrefix(nationalTrunk: String, regionCode: String): String {
        val nationalPrefix = getNationalPrefix(regionCode) ?: return nationalTrunk

        // 이미 national prefix로 시작하면 그대로 반환
        if (nationalTrunk.startsWith(nationalPrefix)) return nationalTrunk

        return "$nationalPrefix$nationalTrunk"
    }

    /**
     * 국가별 national dialing prefix를 반환합니다.
     *
     * libphonenumber의 getNddPrefixForRegion() public API 사용.
     * NDD = National Direct Dialing prefix (국내 직접 다이얼 접두사).
     */
    private fun getNationalPrefix(regionCode: String): String? {
        return try {
            val ndd = phoneNumberUtil.getNddPrefixForRegion(regionCode, false)
            if (ndd.isNullOrEmpty()) null else ndd
        } catch (e: Exception) {
            // 알 수 없는 지역코드
            null
        }
    }

    /**
     * 구분자 패턴 적용.
     *
     * 트렁크는 불완전한 번호이므로 libphonenumber 포맷을 사용할 수 없습니다.
     * 대신 국가별 일반적인 구분자 위치를 경험적으로 적용합니다.
     *
     * 핵심: 쌍따옴표 부분 매칭이므로, 구분자 위치가 정확하지 않아도
     * 검색엔진이 매칭합니다.
     * "02-1234-567" → "02-1234-5670" 포함하는 페이지 매칭.
     */
    private fun applySeparatorPatterns(
        prefixedTrunk: String,
        regionCode: String,
    ): List<String> {
        val variants = mutableListOf<String>()

        when (regionCode.uppercase()) {
            // 한국: 02-XXXX-XXXX, 010-XXXX-XXXX, 031-XXX-XXXX
            "KR" -> {
                val hyphenated = applyKoreanSeparator(prefixedTrunk)
                if (hyphenated != null) variants.add(hyphenated)
            }
            // 일본: 03-XXXX-XXXX, 090-XXXX-XXXX
            "JP" -> {
                val hyphenated = applyJapaneseSeparator(prefixedTrunk)
                if (hyphenated != null) variants.add(hyphenated)
            }
            // NANPA (US/CA): (XXX) XXX-XXXX or XXX-XXX-XXXX
            "US", "CA" -> {
                val formatted = applyNanpaSeparator(prefixedTrunk)
                if (formatted != null) variants.add(formatted)
            }
            // 기타: 하이픈/공백 일반 패턴
            else -> {
                // 공백 구분 (3-4-3 패턴)
                val spaced = prefixedTrunk.replace(Regex("(\\d{3})(\\d{3,4})(\\d+)"), "$1 $2 $3")
                if (spaced != prefixedTrunk) variants.add(spaced)
            }
        }

        return variants
    }

    /**
     * 한국 전화번호 구분자 적용.
     *
     * 지역번호 체계:
     * - 02: 서울 (2자리)
     * - 031~064: 기타 지역 (3자리)
     * - 010~019: 이동통신 (3자리)
     * - 070: 인터넷전화 (3자리)
     * - 1588, 1544 등: 대표번호 (4자리)
     */
    private fun applyKoreanSeparator(trunk: String): String? {
        if (!trunk.startsWith("0")) return null

        return when {
            // 서울 02
            trunk.startsWith("02") -> {
                val rest = trunk.substring(2)
                when {
                    rest.length >= 7 -> "02-${rest.substring(0, 4)}-${rest.substring(4)}"
                    rest.length >= 4 -> "02-${rest.substring(0, 3)}-${rest.substring(3)}"
                    else -> null
                }
            }
            // 이동통신/지역번호 (010, 031 등)
            trunk.length >= 3 && trunk.substring(0, 3).matches(Regex("0[1-9]\\d")) -> {
                val prefix = trunk.substring(0, 3)
                val rest = trunk.substring(3)
                when {
                    rest.length >= 7 -> "$prefix-${rest.substring(0, 4)}-${rest.substring(4)}"
                    rest.length >= 4 -> "$prefix-${rest.substring(0, 3)}-${rest.substring(3)}"
                    rest.length >= 3 -> "$prefix-${rest}"
                    else -> null
                }
            }
            else -> null
        }
    }

    /**
     * 일본 전화번호 구분자 적용.
     *
     * 지역번호 체계:
     * - 03: 도쿄 (2자리)
     * - 06: 오사카 (2자리)
     * - 090, 080, 070: 이동통신 (3자리)
     * - 0120: 무료전화 (4자리)
     */
    private fun applyJapaneseSeparator(trunk: String): String? {
        if (!trunk.startsWith("0")) return null

        return when {
            // 도쿄/오사카 (03, 06)
            trunk.startsWith("03") || trunk.startsWith("06") -> {
                val rest = trunk.substring(2)
                if (rest.length >= 7) "0${trunk[1]}-${rest.substring(0, 4)}-${rest.substring(4)}"
                else null
            }
            // 이동통신 (090, 080, 070)
            trunk.matches(Regex("0[7-9]0.*")) -> {
                val rest = trunk.substring(3)
                if (rest.length >= 7) "${trunk.substring(0, 3)}-${rest.substring(0, 4)}-${rest.substring(4)}"
                else null
            }
            else -> null
        }
    }

    /**
     * NANPA (미국/캐나다) 구분자 적용.
     * NPA-NXX-XXXX (3-3-4)
     */
    private fun applyNanpaSeparator(trunk: String): String? {
        if (trunk.length < 9) return null

        val npa = trunk.substring(0, 3)
        val nxx = trunk.substring(3, 6)
        val rest = trunk.substring(6)

        return "$npa-$nxx-$rest"
    }

    // ═══════════════════════════════════════════════
    // Internal: 기본 트렁크 (libphonenumber 파싱 실패 시)
    // ═══════════════════════════════════════════════

    private fun generateBasicTrunkQueries(phoneNumber: String): List<TrunkQuery> {
        val digits = phoneNumber.replace(Regex("[^\\d]"), "")
        if (digits.length < 4) return emptyList()

        val queries = mutableListOf<TrunkQuery>()

        val trunkLevels = listOf(
            Triple(1, 4, "끝 1자리 대역 (10번호)"),
            Triple(2, 6, "끝 2자리 대역 (100번호)"),
            Triple(3, 7, "끝 3자리 대역 (1,000번호)"),
            Triple(4, 8, "끝 4자리 대역 (10,000번호 — 국번 전유)"),
        )

        for ((dropDigits, minLength, description) in trunkLevels) {
            if (digits.length < minLength) continue

            val trunk = digits.dropLast(dropDigits)
            if (trunk.length < 3) continue

            queries.add(
                TrunkQuery(
                    trunkVariants = listOf(trunk),
                    rangeDescription = description,
                    truncatedDigits = dropDigits,
                ),
            )
        }

        return queries
    }
}
