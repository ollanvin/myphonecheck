package app.callcheck.mobile.feature.callintercept

import app.callcheck.mobile.core.model.CountryInterceptPolicy
import app.callcheck.mobile.core.model.RiskPattern
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 국가별 인터셉트 정책 프로바이더.
 *
 * 190개국 정책을 인메모리 테이블로 관리.
 * 런타임에 deviceCountry로 lookup → O(1).
 *
 * 정책 데이터:
 * - 긴급번호: ITU-T 기반 + 국가별 추가
 * - 서비스번호: 국가별 관행 (KR 1588, JP 0120 등)
 * - 스팸 패턴: 각국 통신위원회 공표 기반
 * - VoIP 접두어: 국가별 VoIP 할당 대역
 *
 * 확장 전략:
 * - Phase 1: 주요 20개국 정밀 정책
 * - Phase 2: 나머지 170개국 기본 정책 (ITU 표준)
 * - Phase 3: 사용자 피드백 기반 정책 보강
 *
 * 100% 온디바이스. 서버 전송 없음.
 */
@Singleton
class CountryInterceptPolicyProvider @Inject constructor() {

    private val policyMap: Map<String, CountryInterceptPolicy> = buildPolicyMap()

    /** 국가 정책 조회. 미등록 국가는 DEFAULT_POLICY 반환. */
    fun getPolicy(countryCode: String): CountryInterceptPolicy {
        return policyMap[countryCode.uppercase()] ?: DEFAULT_POLICY
    }

    /** 번호가 긴급번호인지 확인 */
    fun isEmergencyNumber(number: String, countryCode: String): Boolean {
        val policy = getPolicy(countryCode)
        val clean = number.replace(Regex("[^\\d]"), "")
        return clean in policy.emergencyNumbers
    }

    /** 번호가 서비스 단축번호인지 확인 */
    fun isServiceNumber(number: String, countryCode: String): Boolean {
        val policy = getPolicy(countryCode)
        val clean = number.replace(Regex("[^\\d]"), "")
        return clean in policy.serviceShortNumbers ||
                policy.skipPatterns.any { it.matches(clean) }
    }

    /** 번호에 대한 추가 위험 가중치 (국가별 패턴) */
    fun getRiskBoost(normalizedNumber: String, countryCode: String): Float {
        val policy = getPolicy(countryCode)
        var boost = 0f

        // 스팸 접두어 매칭
        val numberWithoutPlus = normalizedNumber.removePrefix("+")
        for (prefix in policy.spamPrefixes) {
            if (numberWithoutPlus.startsWith(policy.dialCode + prefix) ||
                normalizedNumber.startsWith(prefix)) {
                boost += 0.10f
                break
            }
        }

        // VoIP 접두어 매칭
        for (prefix in policy.voipPrefixes) {
            if (numberWithoutPlus.startsWith(policy.dialCode + prefix) ||
                normalizedNumber.startsWith(prefix)) {
                boost += 0.05f
                break
            }
        }

        // 위험 패턴 매칭
        for (rp in policy.riskPatterns) {
            if (rp.pattern.containsMatchIn(normalizedNumber)) {
                boost += rp.riskBoost
            }
        }

        return boost
    }

    /** 현재 시간이 해당 국가의 스팸 피크 시간대인지 */
    fun isSpamPeakHour(currentHour: Int, countryCode: String): Boolean {
        val policy = getPolicy(countryCode)
        return policy.spamPeakHours?.contains(currentHour) == true
    }

    /** 해당 국가에서 국제전화가 일상적인지 */
    fun isInternationalCallCommon(countryCode: String): Boolean {
        return getPolicy(countryCode).internationalCallCommon
    }

    /** 위험 가중 국가인지 */
    fun isElevatedRiskCountry(countryCode: String): Boolean {
        return getPolicy(countryCode).elevatedRiskCountry
    }

    companion object {
        /** 미등록 국가 기본 정책 */
        val DEFAULT_POLICY = CountryInterceptPolicy(
            countryCode = "ZZ",
            dialCode = "",
            emergencyNumbers = setOf("911", "112", "999", "000"),
        )

        private fun buildPolicyMap(): Map<String, CountryInterceptPolicy> = mapOf(
            // ════════════════════════════════════════
            // 한국 (KR)
            // ════════════════════════════════════════
            "KR" to CountryInterceptPolicy(
                countryCode = "KR",
                dialCode = "82",
                emergencyNumbers = setOf("112", "119", "110", "111", "113", "122", "125", "128", "129", "131", "132"),
                serviceShortNumbers = setOf("114", "100", "106", "108", "109", "116"),
                skipPatterns = listOf(
                    Regex("^1588\\d{4}$"),  // 대표번호
                    Regex("^1577\\d{4}$"),  // 대표번호
                    Regex("^1544\\d{4}$"),  // 대표번호
                    Regex("^1566\\d{4}$"),  // 대표번호
                    Regex("^1599\\d{4}$"),  // 대표번호
                    Regex("^1600\\d{4}$"),  // 대표번호
                    Regex("^1644\\d{4}$"),  // 대표번호
                    Regex("^1688\\d{4}$"),  // 대표번호
                    Regex("^1899\\d{4}$"),  // 대표번호
                ),
                spamPrefixes = setOf("070"),  // VoIP = 스팸 다발
                voipPrefixes = setOf("070", "050"),
                spamPeakHours = 11..17,  // 한국 스팸 피크: 오전 11시~오후 5시
                riskPatterns = listOf(
                    RiskPattern(
                        pattern = Regex("^\\+82(70|50)"),
                        riskBoost = 0.08f,
                        description = "KR VoIP prefix (070/050)"
                    ),
                ),
            ),

            // ════════════════════════════════════════
            // 미국/캐나다 (US/CA) — NANP
            // ════════════════════════════════════════
            "US" to CountryInterceptPolicy(
                countryCode = "US",
                dialCode = "1",
                emergencyNumbers = setOf("911", "988"),  // 988 = Suicide Prevention
                serviceShortNumbers = setOf("211", "311", "411", "511", "611", "711", "811"),
                skipPatterns = listOf(
                    Regex("^1?800\\d{7}$"),  // toll-free
                    Regex("^1?888\\d{7}$"),
                    Regex("^1?877\\d{7}$"),
                    Regex("^1?866\\d{7}$"),
                    Regex("^1?855\\d{7}$"),
                    Regex("^1?844\\d{7}$"),
                    Regex("^1?833\\d{7}$"),
                ),
                spamPeakHours = 10..18,
                riskPatterns = listOf(
                    RiskPattern(
                        pattern = Regex("^\\+1(900)"),
                        riskBoost = 0.15f,
                        description = "US premium rate (900)"
                    ),
                ),
            ),
            "CA" to CountryInterceptPolicy(
                countryCode = "CA",
                dialCode = "1",
                emergencyNumbers = setOf("911"),
                serviceShortNumbers = setOf("211", "311", "411", "511", "611", "711", "811"),
                skipPatterns = listOf(
                    Regex("^1?800\\d{7}$"),
                    Regex("^1?888\\d{7}$"),
                    Regex("^1?877\\d{7}$"),
                ),
                spamPeakHours = 10..18,
            ),

            // ════════════════════════════════════════
            // 일본 (JP)
            // ════════════════════════════════════════
            "JP" to CountryInterceptPolicy(
                countryCode = "JP",
                dialCode = "81",
                emergencyNumbers = setOf("110", "119", "118"),
                serviceShortNumbers = setOf("104", "115", "117", "171", "177"),
                skipPatterns = listOf(
                    Regex("^0120\\d{6}$"),  // 수신자 부담 (フリーダイヤル)
                    Regex("^0800\\d{7}$"),
                ),
                voipPrefixes = setOf("050"),
                spamPeakHours = 11..17,
                riskPatterns = listOf(
                    RiskPattern(
                        pattern = Regex("^\\+81(50)"),
                        riskBoost = 0.05f,
                        description = "JP VoIP prefix (050)"
                    ),
                ),
            ),

            // ════════════════════════════════════════
            // 중국 (CN)
            // ════════════════════════════════════════
            "CN" to CountryInterceptPolicy(
                countryCode = "CN",
                dialCode = "86",
                emergencyNumbers = setOf("110", "119", "120", "122", "12110"),
                serviceShortNumbers = setOf("114", "10000", "10010", "10086"),
                spamPeakHours = 10..18,
                riskPatterns = listOf(
                    RiskPattern(
                        pattern = Regex("^\\+86(400|800)"),
                        riskBoost = 0.05f,
                        description = "CN toll-free/business (400/800)"
                    ),
                ),
            ),

            // ════════════════════════════════════════
            // 영국 (GB)
            // ════════════════════════════════════════
            "GB" to CountryInterceptPolicy(
                countryCode = "GB",
                dialCode = "44",
                emergencyNumbers = setOf("999", "112"),
                serviceShortNumbers = setOf("100", "105", "111", "116", "118", "155"),
                skipPatterns = listOf(
                    Regex("^0800\\d{7}$"),
                    Regex("^0808\\d{7}$"),
                ),
                spamPeakHours = 10..17,
                riskPatterns = listOf(
                    RiskPattern(
                        pattern = Regex("^\\+44(90[0-9])"),
                        riskBoost = 0.15f,
                        description = "GB premium rate (09xx)"
                    ),
                    RiskPattern(
                        pattern = Regex("^\\+44(70)"),
                        riskBoost = 0.10f,
                        description = "GB personal numbering (070)"
                    ),
                ),
            ),

            // ════════════════════════════════════════
            // 독일 (DE)
            // ════════════════════════════════════════
            "DE" to CountryInterceptPolicy(
                countryCode = "DE",
                dialCode = "49",
                emergencyNumbers = setOf("110", "112"),
                serviceShortNumbers = setOf("115", "116"),
                skipPatterns = listOf(
                    Regex("^0800\\d{7}$"),
                ),
                spamPeakHours = 10..17,
                riskPatterns = listOf(
                    RiskPattern(
                        pattern = Regex("^\\+49(900)"),
                        riskBoost = 0.15f,
                        description = "DE premium rate (0900)"
                    ),
                ),
            ),

            // ════════════════════════════════════════
            // 프랑스 (FR)
            // ════════════════════════════════════════
            "FR" to CountryInterceptPolicy(
                countryCode = "FR",
                dialCode = "33",
                emergencyNumbers = setOf("15", "17", "18", "112", "114", "115", "119", "191", "196"),
                skipPatterns = listOf(
                    Regex("^0800\\d{6}$"),
                ),
                spamPeakHours = 10..17,
                riskPatterns = listOf(
                    RiskPattern(
                        pattern = Regex("^\\+33(89)"),
                        riskBoost = 0.15f,
                        description = "FR premium rate (089x)"
                    ),
                ),
            ),

            // ════════════════════════════════════════
            // 호주 (AU)
            // ════════════════════════════════════════
            "AU" to CountryInterceptPolicy(
                countryCode = "AU",
                dialCode = "61",
                emergencyNumbers = setOf("000", "112", "106"),
                serviceShortNumbers = setOf("1223", "1234"),
                skipPatterns = listOf(
                    Regex("^1800\\d{6}$"),
                    Regex("^1300\\d{6}$"),
                    Regex("^13\\d{4}$"),
                ),
                spamPeakHours = 10..17,
            ),

            // ════════════════════════════════════════
            // 인도 (IN)
            // ════════════════════════════════════════
            "IN" to CountryInterceptPolicy(
                countryCode = "IN",
                dialCode = "91",
                emergencyNumbers = setOf("100", "101", "102", "108", "112"),
                serviceShortNumbers = setOf("1098", "1091", "181"),
                spamPeakHours = 10..18,
                elevatedRiskCountry = true,  // 높은 스팸 비율
                riskPatterns = listOf(
                    RiskPattern(
                        pattern = Regex("^\\+91(140)"),
                        riskBoost = 0.08f,
                        description = "IN telemarketing prefix (140)"
                    ),
                ),
            ),

            // ════════════════════════════════════════
            // 브라질 (BR)
            // ════════════════════════════════════════
            "BR" to CountryInterceptPolicy(
                countryCode = "BR",
                dialCode = "55",
                emergencyNumbers = setOf("190", "192", "193", "191", "194", "197", "198", "199"),
                skipPatterns = listOf(
                    Regex("^0800\\d{7}$"),
                ),
                spamPeakHours = 10..18,
                elevatedRiskCountry = true,
            ),

            // ════════════════════════════════════════
            // 대만 (TW)
            // ════════════════════════════════════════
            "TW" to CountryInterceptPolicy(
                countryCode = "TW",
                dialCode = "886",
                emergencyNumbers = setOf("110", "119", "112"),
                serviceShortNumbers = setOf("104", "105", "117"),
                spamPeakHours = 10..17,
            ),

            // ════════════════════════════════════════
            // 홍콩 (HK)
            // ════════════════════════════════════════
            "HK" to CountryInterceptPolicy(
                countryCode = "HK",
                dialCode = "852",
                emergencyNumbers = setOf("999", "112"),
                internationalCallCommon = true,  // 국제전화 빈도 높음
                spamPeakHours = 10..17,
            ),

            // ════════════════════════════════════════
            // 싱가포르 (SG)
            // ════════════════════════════════════════
            "SG" to CountryInterceptPolicy(
                countryCode = "SG",
                dialCode = "65",
                emergencyNumbers = setOf("995", "999", "112"),
                internationalCallCommon = true,
                spamPeakHours = 10..17,
            ),

            // ════════════════════════════════════════
            // 태국 (TH)
            // ════════════════════════════════════════
            "TH" to CountryInterceptPolicy(
                countryCode = "TH",
                dialCode = "66",
                emergencyNumbers = setOf("191", "199", "1669"),
                spamPeakHours = 10..17,
            ),

            // ════════════════════════════════════════
            // 베트남 (VN)
            // ════════════════════════════════════════
            "VN" to CountryInterceptPolicy(
                countryCode = "VN",
                dialCode = "84",
                emergencyNumbers = setOf("113", "114", "115"),
                spamPeakHours = 9..17,
            ),

            // ════════════════════════════════════════
            // 필리핀 (PH)
            // ════════════════════════════════════════
            "PH" to CountryInterceptPolicy(
                countryCode = "PH",
                dialCode = "63",
                emergencyNumbers = setOf("911", "117"),
                spamPeakHours = 9..17,
            ),

            // ════════════════════════════════════════
            // 인도네시아 (ID)
            // ════════════════════════════════════════
            "ID" to CountryInterceptPolicy(
                countryCode = "ID",
                dialCode = "62",
                emergencyNumbers = setOf("110", "112", "113", "118", "119"),
                spamPeakHours = 9..17,
            ),

            // ════════════════════════════════════════
            // 말레이시아 (MY)
            // ════════════════════════════════════════
            "MY" to CountryInterceptPolicy(
                countryCode = "MY",
                dialCode = "60",
                emergencyNumbers = setOf("999", "112"),
                spamPeakHours = 10..17,
            ),

            // ════════════════════════════════════════
            // 멕시코 (MX)
            // ════════════════════════════════════════
            "MX" to CountryInterceptPolicy(
                countryCode = "MX",
                dialCode = "52",
                emergencyNumbers = setOf("911", "066", "060"),
                spamPeakHours = 10..18,
            ),

            // ════════════════════════════════════════
            // 러시아 (RU)
            // ════════════════════════════════════════
            "RU" to CountryInterceptPolicy(
                countryCode = "RU",
                dialCode = "7",
                emergencyNumbers = setOf("112", "101", "102", "103", "104"),
                spamPeakHours = 10..18,
            ),
        )
    }
}
