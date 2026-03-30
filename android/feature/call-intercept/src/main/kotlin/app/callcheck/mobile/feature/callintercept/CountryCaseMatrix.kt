package app.callcheck.mobile.feature.callintercept

import android.util.Log
import app.callcheck.mobile.core.model.CaseFailure
import app.callcheck.mobile.core.model.CountryTestResult
import app.callcheck.mobile.core.model.CountryValidationSummary
import app.callcheck.mobile.core.model.InterceptRoute
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "CountryCaseMatrix"

/**
 * 30개국 실전화 케이스 매트릭스 전수 검증 엔진.
 *
 * 자비스 요구: "190개국 동시 출시를 하더라도,
 * 실제 경쟁력은 상위 핵심국가를 얼마나 깊게 잠갔느냐에서 갈립니다."
 *
 * 검증 매트릭스 (국가당 6개 축):
 * 1. 긴급번호 → SKIP (절대 인터셉트 금지)
 * 2. 서비스번호 → SKIP 또는 INSTANT (무개입 ~ 최소 개입)
 * 3. 저장번호 → INSTANT (P1 캐시 즉시)
 * 4. 미저장 국내번호 → LIGHT 또는 FULL (경량~풀 분석)
 * 5. 국제번호 → FULL (반드시 풀 파이프라인)
 * 6. VoIP/위험패턴 → FULL (반드시 풀 파이프라인)
 *
 * 합격 기준:
 * - 긴급번호 100% SKIP (0 실패 허용)
 * - 서비스번호 100% SKIP/INSTANT
 * - 저장번호 100% INSTANT
 * - 나머지 경로 적절성 검증
 *
 * 100% 온디바이스. 서버 전송 없음.
 */
@Singleton
class CountryCaseMatrix @Inject constructor(
    private val policyProvider: CountryInterceptPolicyProvider,
    private val router: InterceptPriorityRouter,
) {

    /**
     * 30개국 전수 검증 실행.
     *
     * @return CountryValidationSummary
     */
    fun runFullValidation(): CountryValidationSummary {
        val allCases = buildAllCountryCases()
        val results = mutableListOf<CountryTestResult>()
        var totalCases = 0
        var totalPassed = 0

        for ((countryCode, countryName, cases) in allCases) {
            val failures = mutableListOf<CaseFailure>()
            var passed = 0

            for (case in cases) {
                val result = executeCase(case, countryCode)
                totalCases++

                if (result.success) {
                    passed++
                    totalPassed++
                } else {
                    failures.add(CaseFailure(
                        caseType = case.type.name,
                        input = case.number,
                        expected = case.expectedRoute.name,
                        actual = result.actualRoute.name,
                        reason = result.reason,
                    ))
                }
            }

            val countryResult = CountryTestResult(
                countryCode = countryCode,
                countryName = countryName,
                totalCases = cases.size,
                passedCases = passed,
                failures = failures,
            )
            results.add(countryResult)

            val status = if (failures.isEmpty()) "PASS" else "FAIL(${failures.size})"
            Log.i(TAG, "[$countryCode] $countryName: $passed/${cases.size} $status")
        }

        val passedCountries = results.count { it.failures.isEmpty() }
        val summary = CountryValidationSummary(
            totalCountries = results.size,
            passedCountries = passedCountries,
            failedCountries = results.size - passedCountries,
            countryResults = results,
            totalTestCases = totalCases,
            passedTestCases = totalPassed,
        )

        Log.i(TAG, buildString {
            appendLine("══════ Country Matrix Summary ══════")
            appendLine("Countries: $passedCountries/${results.size} passed")
            appendLine("Cases: $totalPassed/$totalCases passed")
            if (summary.failedCountries > 0) {
                appendLine("Failed countries:")
                results.filter { it.failures.isNotEmpty() }.forEach { r ->
                    appendLine("  ${r.countryCode}: ${r.failures.size} failures")
                    r.failures.forEach { f ->
                        appendLine("    - [${f.caseType}] ${f.input}: expected=${f.expected} actual=${f.actual}")
                    }
                }
            }
        })

        return summary
    }

    /**
     * 개별 테스트 케이스 검증 리포트 (문자열).
     */
    fun getValidationReport(): String {
        val summary = runFullValidation()
        return buildString {
            appendLine("═══ CallCheck 국가 정책 검증 리포트 ═══")
            appendLine()
            appendLine("검증 국가: ${summary.totalCountries}개국")
            appendLine("통과: ${summary.passedCountries}개국")
            appendLine("실패: ${summary.failedCountries}개국")
            appendLine("테스트 케이스: ${summary.passedTestCases}/${summary.totalTestCases}")
            appendLine("통과율: ${String.format("%.1f", summary.passedTestCases.toFloat() / summary.totalTestCases * 100)}%")
            appendLine()

            for (result in summary.countryResults) {
                val status = if (result.failures.isEmpty()) "✓" else "✗"
                appendLine("$status [${result.countryCode}] ${result.countryName}: ${result.passedCases}/${result.totalCases}")

                for (failure in result.failures) {
                    appendLine("    ✗ [${failure.caseType}] ${failure.input}")
                    appendLine("      expected: ${failure.expected}, actual: ${failure.actual}")
                    appendLine("      reason: ${failure.reason}")
                }
            }
        }
    }

    // ══════════════════════════════════════
    // 내부: 케이스 실행
    // ══════════════════════════════════════

    private data class CaseResult(
        val success: Boolean,
        val actualRoute: InterceptRoute,
        val reason: String,
    )

    private fun executeCase(case: TestCase, countryCode: String): CaseResult {
        val policy = policyProvider.getPolicy(countryCode)

        // 긴급번호 → isEmergencyNumber로 검증
        if (case.type == CaseType.EMERGENCY) {
            val isEmergency = policyProvider.isEmergencyNumber(case.number, countryCode)
            return if (isEmergency) {
                CaseResult(true, InterceptRoute.SKIP, "Emergency recognized")
            } else {
                CaseResult(false, InterceptRoute.FULL, "Emergency NOT recognized by policy")
            }
        }

        // 서비스번호 → isServiceNumber로 검증
        if (case.type == CaseType.SERVICE) {
            val isService = policyProvider.isServiceNumber(case.number, countryCode)
            return if (isService) {
                CaseResult(true, InterceptRoute.SKIP, "Service number recognized")
            } else {
                CaseResult(false, InterceptRoute.FULL, "Service number NOT recognized by policy")
            }
        }

        // Router 기반 검증
        val route = router.route(
            normalizedNumber = case.number,
            preJudge = null,
            isSavedContact = case.isSavedContact,
            isInternational = case.isInternational,
            isVoip = case.isVoip,
            currentHour = case.hour,
            recentCallCount = case.recentCallCount,
            lastUserAction = null,
            totalAnsweredCount = case.totalAnsweredCount,
            countryRiskElevated = policyProvider.isElevatedRiskCountry(countryCode),
        )

        val success = isRouteAcceptable(case.expectedRoute, route, case.type)
        return CaseResult(
            success = success,
            actualRoute = route,
            reason = if (success) "Route matched" else "Route mismatch: expected ${case.expectedRoute}, got $route",
        )
    }

    /**
     * 경로 허용 범위 검증.
     *
     * 엄격한 매칭이 아니라 "최소 보장" 검증:
     * - SKIP 기대 → SKIP만 허용
     * - INSTANT 기대 → INSTANT만 허용
     * - LIGHT 기대 → LIGHT 또는 FULL 허용 (더 깊은 분석은 허용)
     * - FULL 기대 → FULL만 허용 (위험 경로는 반드시 풀 분석)
     */
    private fun isRouteAcceptable(expected: InterceptRoute, actual: InterceptRoute, type: CaseType): Boolean {
        return when (expected) {
            InterceptRoute.SKIP -> actual == InterceptRoute.SKIP
            InterceptRoute.INSTANT -> actual == InterceptRoute.INSTANT
            InterceptRoute.LIGHT -> actual == InterceptRoute.LIGHT || actual == InterceptRoute.FULL
            InterceptRoute.FULL -> actual == InterceptRoute.FULL
        }
    }

    // ══════════════════════════════════════
    // 테스트 케이스 정의
    // ══════════════════════════════════════

    private data class TestCase(
        val number: String,
        val type: CaseType,
        val expectedRoute: InterceptRoute,
        val isSavedContact: Boolean = false,
        val isInternational: Boolean = false,
        val isVoip: Boolean = false,
        val hour: Int = 14,
        val recentCallCount: Int = 0,
        val totalAnsweredCount: Int = 0,
    )

    private enum class CaseType {
        EMERGENCY,
        SERVICE,
        SAVED_CONTACT,
        UNSAVED_DOMESTIC,
        INTERNATIONAL,
        VOIP_RISK,
    }

    private data class CountryCaseSet(
        val countryCode: String,
        val countryName: String,
        val cases: List<TestCase>,
    )

    /**
     * 30개국 전수 테스트 케이스 구성.
     *
     * 각 국가마다 6개 축:
     * 1. Emergency → SKIP
     * 2. Service → SKIP
     * 3. Saved → INSTANT
     * 4. Unsaved domestic → LIGHT (주간, 첫수신)
     * 5. International → FULL
     * 6. VoIP/Risk → FULL
     */
    private fun buildAllCountryCases(): List<CountryCaseSet> = listOf(
        // ── KR ──
        CountryCaseSet("KR", "한국", listOf(
            TestCase("112", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("119", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("110", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("15881234", CaseType.SERVICE, InterceptRoute.SKIP),
            TestCase("15771234", CaseType.SERVICE, InterceptRoute.SKIP),
            TestCase("+821012345678", CaseType.SAVED_CONTACT, InterceptRoute.INSTANT, isSavedContact = true),
            TestCase("+821098765432", CaseType.UNSAVED_DOMESTIC, InterceptRoute.LIGHT, hour = 14),
            TestCase("+14155551234", CaseType.INTERNATIONAL, InterceptRoute.FULL, isInternational = true),
            TestCase("+827012345678", CaseType.VOIP_RISK, InterceptRoute.FULL, isVoip = true),
        )),

        // ── US ──
        CountryCaseSet("US", "미국", listOf(
            TestCase("911", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("988", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("211", CaseType.SERVICE, InterceptRoute.SKIP),
            TestCase("311", CaseType.SERVICE, InterceptRoute.SKIP),
            TestCase("18001234567", CaseType.SERVICE, InterceptRoute.SKIP),
            TestCase("+14155551234", CaseType.SAVED_CONTACT, InterceptRoute.INSTANT, isSavedContact = true),
            TestCase("+12125551234", CaseType.UNSAVED_DOMESTIC, InterceptRoute.LIGHT, hour = 14),
            TestCase("+442071234567", CaseType.INTERNATIONAL, InterceptRoute.FULL, isInternational = true),
        )),

        // ── CA ──
        CountryCaseSet("CA", "캐나다", listOf(
            TestCase("911", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("211", CaseType.SERVICE, InterceptRoute.SKIP),
            TestCase("18001234567", CaseType.SERVICE, InterceptRoute.SKIP),
            TestCase("+16135551234", CaseType.SAVED_CONTACT, InterceptRoute.INSTANT, isSavedContact = true),
            TestCase("+14165551234", CaseType.UNSAVED_DOMESTIC, InterceptRoute.LIGHT, hour = 14),
            TestCase("+819012345678", CaseType.INTERNATIONAL, InterceptRoute.FULL, isInternational = true),
        )),

        // ── JP ──
        CountryCaseSet("JP", "일본", listOf(
            TestCase("110", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("119", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("118", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("0120123456", CaseType.SERVICE, InterceptRoute.SKIP),
            TestCase("+819012345678", CaseType.SAVED_CONTACT, InterceptRoute.INSTANT, isSavedContact = true),
            TestCase("+813012345678", CaseType.UNSAVED_DOMESTIC, InterceptRoute.LIGHT, hour = 14),
            TestCase("+821012345678", CaseType.INTERNATIONAL, InterceptRoute.FULL, isInternational = true),
            TestCase("+815012345678", CaseType.VOIP_RISK, InterceptRoute.FULL, isVoip = true),
        )),

        // ── CN ──
        CountryCaseSet("CN", "중국", listOf(
            TestCase("110", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("119", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("120", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("114", CaseType.SERVICE, InterceptRoute.SKIP),
            TestCase("10086", CaseType.SERVICE, InterceptRoute.SKIP),
            TestCase("+8613912345678", CaseType.SAVED_CONTACT, InterceptRoute.INSTANT, isSavedContact = true),
            TestCase("+8613812345678", CaseType.UNSAVED_DOMESTIC, InterceptRoute.LIGHT, hour = 14),
            TestCase("+14155551234", CaseType.INTERNATIONAL, InterceptRoute.FULL, isInternational = true),
        )),

        // ── GB ──
        CountryCaseSet("GB", "영국", listOf(
            TestCase("999", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("112", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("111", CaseType.SERVICE, InterceptRoute.SKIP),
            TestCase("08001234567", CaseType.SERVICE, InterceptRoute.SKIP),
            TestCase("+447911123456", CaseType.SAVED_CONTACT, InterceptRoute.INSTANT, isSavedContact = true),
            TestCase("+442071234567", CaseType.UNSAVED_DOMESTIC, InterceptRoute.LIGHT, hour = 14),
            TestCase("+14155551234", CaseType.INTERNATIONAL, InterceptRoute.FULL, isInternational = true),
            TestCase("+449001234567", CaseType.VOIP_RISK, InterceptRoute.FULL, isVoip = true),
        )),

        // ── DE ──
        CountryCaseSet("DE", "독일", listOf(
            TestCase("110", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("112", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("115", CaseType.SERVICE, InterceptRoute.SKIP),
            TestCase("08001234567", CaseType.SERVICE, InterceptRoute.SKIP),
            TestCase("+4917612345678", CaseType.SAVED_CONTACT, InterceptRoute.INSTANT, isSavedContact = true),
            TestCase("+493012345678", CaseType.UNSAVED_DOMESTIC, InterceptRoute.LIGHT, hour = 14),
            TestCase("+14155551234", CaseType.INTERNATIONAL, InterceptRoute.FULL, isInternational = true),
        )),

        // ── FR ──
        CountryCaseSet("FR", "프랑스", listOf(
            TestCase("15", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("17", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("18", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("112", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("0800123456", CaseType.SERVICE, InterceptRoute.SKIP),
            TestCase("+33612345678", CaseType.SAVED_CONTACT, InterceptRoute.INSTANT, isSavedContact = true),
            TestCase("+33112345678", CaseType.UNSAVED_DOMESTIC, InterceptRoute.LIGHT, hour = 14),
            TestCase("+14155551234", CaseType.INTERNATIONAL, InterceptRoute.FULL, isInternational = true),
        )),

        // ── AU ──
        CountryCaseSet("AU", "호주", listOf(
            TestCase("000", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("112", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("1800123456", CaseType.SERVICE, InterceptRoute.SKIP),
            TestCase("1300123456", CaseType.SERVICE, InterceptRoute.SKIP),
            TestCase("131234", CaseType.SERVICE, InterceptRoute.SKIP),
            TestCase("+61412345678", CaseType.SAVED_CONTACT, InterceptRoute.INSTANT, isSavedContact = true),
            TestCase("+61212345678", CaseType.UNSAVED_DOMESTIC, InterceptRoute.LIGHT, hour = 14),
            TestCase("+14155551234", CaseType.INTERNATIONAL, InterceptRoute.FULL, isInternational = true),
        )),

        // ── IN ──
        CountryCaseSet("IN", "인도", listOf(
            TestCase("100", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("112", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("108", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("1098", CaseType.SERVICE, InterceptRoute.SKIP),
            TestCase("+919812345678", CaseType.SAVED_CONTACT, InterceptRoute.INSTANT, isSavedContact = true),
            // IN = elevatedRiskCountry → FULL expected for unsaved
            TestCase("+919812345679", CaseType.UNSAVED_DOMESTIC, InterceptRoute.FULL, hour = 14),
            TestCase("+14155551234", CaseType.INTERNATIONAL, InterceptRoute.FULL, isInternational = true),
        )),

        // ── BR ──
        CountryCaseSet("BR", "브라질", listOf(
            TestCase("190", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("192", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("0800123456789", CaseType.SERVICE, InterceptRoute.SKIP),
            TestCase("+5511912345678", CaseType.SAVED_CONTACT, InterceptRoute.INSTANT, isSavedContact = true),
            // BR = elevatedRiskCountry → FULL
            TestCase("+5511912345679", CaseType.UNSAVED_DOMESTIC, InterceptRoute.FULL, hour = 14),
            TestCase("+14155551234", CaseType.INTERNATIONAL, InterceptRoute.FULL, isInternational = true),
        )),

        // ── TW ──
        CountryCaseSet("TW", "대만", listOf(
            TestCase("110", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("119", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("104", CaseType.SERVICE, InterceptRoute.SKIP),
            TestCase("+886912345678", CaseType.SAVED_CONTACT, InterceptRoute.INSTANT, isSavedContact = true),
            TestCase("+886212345678", CaseType.UNSAVED_DOMESTIC, InterceptRoute.LIGHT, hour = 14),
            TestCase("+14155551234", CaseType.INTERNATIONAL, InterceptRoute.FULL, isInternational = true),
        )),

        // ── HK ──
        CountryCaseSet("HK", "홍콩", listOf(
            TestCase("999", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("112", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("+85291234567", CaseType.SAVED_CONTACT, InterceptRoute.INSTANT, isSavedContact = true),
            TestCase("+85221234567", CaseType.UNSAVED_DOMESTIC, InterceptRoute.LIGHT, hour = 14),
            TestCase("+14155551234", CaseType.INTERNATIONAL, InterceptRoute.FULL, isInternational = true),
        )),

        // ── SG ──
        CountryCaseSet("SG", "싱가포르", listOf(
            TestCase("995", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("999", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("+6591234567", CaseType.SAVED_CONTACT, InterceptRoute.INSTANT, isSavedContact = true),
            TestCase("+6561234567", CaseType.UNSAVED_DOMESTIC, InterceptRoute.LIGHT, hour = 14),
            TestCase("+14155551234", CaseType.INTERNATIONAL, InterceptRoute.FULL, isInternational = true),
        )),

        // ── TH ──
        CountryCaseSet("TH", "태국", listOf(
            TestCase("191", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("199", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("1669", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("+66812345678", CaseType.SAVED_CONTACT, InterceptRoute.INSTANT, isSavedContact = true),
            TestCase("+66212345678", CaseType.UNSAVED_DOMESTIC, InterceptRoute.LIGHT, hour = 14),
            TestCase("+14155551234", CaseType.INTERNATIONAL, InterceptRoute.FULL, isInternational = true),
        )),

        // ── VN ──
        CountryCaseSet("VN", "베트남", listOf(
            TestCase("113", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("114", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("115", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("+84912345678", CaseType.SAVED_CONTACT, InterceptRoute.INSTANT, isSavedContact = true),
            TestCase("+84212345678", CaseType.UNSAVED_DOMESTIC, InterceptRoute.LIGHT, hour = 14),
            TestCase("+14155551234", CaseType.INTERNATIONAL, InterceptRoute.FULL, isInternational = true),
        )),

        // ── PH ──
        CountryCaseSet("PH", "필리핀", listOf(
            TestCase("911", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("117", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("+63912345678", CaseType.SAVED_CONTACT, InterceptRoute.INSTANT, isSavedContact = true),
            TestCase("+63212345678", CaseType.UNSAVED_DOMESTIC, InterceptRoute.LIGHT, hour = 14),
            TestCase("+14155551234", CaseType.INTERNATIONAL, InterceptRoute.FULL, isInternational = true),
        )),

        // ── ID ──
        CountryCaseSet("ID", "인도네시아", listOf(
            TestCase("110", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("112", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("119", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("+62812345678", CaseType.SAVED_CONTACT, InterceptRoute.INSTANT, isSavedContact = true),
            TestCase("+62212345678", CaseType.UNSAVED_DOMESTIC, InterceptRoute.LIGHT, hour = 14),
            TestCase("+14155551234", CaseType.INTERNATIONAL, InterceptRoute.FULL, isInternational = true),
        )),

        // ── MY ──
        CountryCaseSet("MY", "말레이시아", listOf(
            TestCase("999", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("112", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("+60122345678", CaseType.SAVED_CONTACT, InterceptRoute.INSTANT, isSavedContact = true),
            TestCase("+60312345678", CaseType.UNSAVED_DOMESTIC, InterceptRoute.LIGHT, hour = 14),
            TestCase("+14155551234", CaseType.INTERNATIONAL, InterceptRoute.FULL, isInternational = true),
        )),

        // ── MX ──
        CountryCaseSet("MX", "멕시코", listOf(
            TestCase("911", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("066", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("+525512345678", CaseType.SAVED_CONTACT, InterceptRoute.INSTANT, isSavedContact = true),
            TestCase("+525512345679", CaseType.UNSAVED_DOMESTIC, InterceptRoute.LIGHT, hour = 14),
            TestCase("+14155551234", CaseType.INTERNATIONAL, InterceptRoute.FULL, isInternational = true),
        )),

        // ── RU ──
        CountryCaseSet("RU", "러시아", listOf(
            TestCase("112", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("101", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("102", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("+79161234567", CaseType.SAVED_CONTACT, InterceptRoute.INSTANT, isSavedContact = true),
            TestCase("+74951234567", CaseType.UNSAVED_DOMESTIC, InterceptRoute.LIGHT, hour = 14),
            TestCase("+14155551234", CaseType.INTERNATIONAL, InterceptRoute.FULL, isInternational = true),
        )),

        // ── IT ──
        CountryCaseSet("IT", "이탈리아", listOf(
            TestCase("112", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("113", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("118", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("1500", CaseType.SERVICE, InterceptRoute.SKIP),
            TestCase("800123456", CaseType.SERVICE, InterceptRoute.SKIP),
            TestCase("+393212345678", CaseType.SAVED_CONTACT, InterceptRoute.INSTANT, isSavedContact = true),
            TestCase("+390212345678", CaseType.UNSAVED_DOMESTIC, InterceptRoute.LIGHT, hour = 14),
            TestCase("+14155551234", CaseType.INTERNATIONAL, InterceptRoute.FULL, isInternational = true),
        )),

        // ── ES ──
        CountryCaseSet("ES", "스페인", listOf(
            TestCase("112", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("091", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("010", CaseType.SERVICE, InterceptRoute.SKIP),
            TestCase("900123456", CaseType.SERVICE, InterceptRoute.SKIP),
            TestCase("+34612345678", CaseType.SAVED_CONTACT, InterceptRoute.INSTANT, isSavedContact = true),
            TestCase("+34912345678", CaseType.UNSAVED_DOMESTIC, InterceptRoute.LIGHT, hour = 14),
            TestCase("+14155551234", CaseType.INTERNATIONAL, InterceptRoute.FULL, isInternational = true),
        )),

        // ── NL ──
        CountryCaseSet("NL", "네덜란드", listOf(
            TestCase("112", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("1400", CaseType.SERVICE, InterceptRoute.SKIP),
            TestCase("08001234567", CaseType.SERVICE, InterceptRoute.SKIP),
            TestCase("+31612345678", CaseType.SAVED_CONTACT, InterceptRoute.INSTANT, isSavedContact = true),
            TestCase("+31201234567", CaseType.UNSAVED_DOMESTIC, InterceptRoute.LIGHT, hour = 14),
            TestCase("+14155551234", CaseType.INTERNATIONAL, InterceptRoute.FULL, isInternational = true),
        )),

        // ── SE ──
        CountryCaseSet("SE", "스웨덴", listOf(
            TestCase("112", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("113", CaseType.SERVICE, InterceptRoute.SKIP),
            TestCase("1177", CaseType.SERVICE, InterceptRoute.SKIP),
            TestCase("020123456", CaseType.SERVICE, InterceptRoute.SKIP),
            TestCase("+46701234567", CaseType.SAVED_CONTACT, InterceptRoute.INSTANT, isSavedContact = true),
            TestCase("+468123456", CaseType.UNSAVED_DOMESTIC, InterceptRoute.LIGHT, hour = 14),
            TestCase("+14155551234", CaseType.INTERNATIONAL, InterceptRoute.FULL, isInternational = true),
        )),

        // ── PL ──
        CountryCaseSet("PL", "폴란드", listOf(
            TestCase("112", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("997", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("998", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("999", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("800123456", CaseType.SERVICE, InterceptRoute.SKIP),
            TestCase("+48501234567", CaseType.SAVED_CONTACT, InterceptRoute.INSTANT, isSavedContact = true),
            TestCase("+48221234567", CaseType.UNSAVED_DOMESTIC, InterceptRoute.LIGHT, hour = 14),
            TestCase("+14155551234", CaseType.INTERNATIONAL, InterceptRoute.FULL, isInternational = true),
        )),

        // ── CH ──
        CountryCaseSet("CH", "스위스", listOf(
            TestCase("112", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("117", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("118", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("143", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("144", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("0800123456", CaseType.SERVICE, InterceptRoute.SKIP),
            TestCase("+41791234567", CaseType.SAVED_CONTACT, InterceptRoute.INSTANT, isSavedContact = true),
            TestCase("+41441234567", CaseType.UNSAVED_DOMESTIC, InterceptRoute.LIGHT, hour = 14),
            TestCase("+14155551234", CaseType.INTERNATIONAL, InterceptRoute.FULL, isInternational = true),
        )),

        // ── NZ ──
        CountryCaseSet("NZ", "뉴질랜드", listOf(
            TestCase("111", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("0800123456", CaseType.SERVICE, InterceptRoute.SKIP),
            TestCase("0508123456", CaseType.SERVICE, InterceptRoute.SKIP),
            TestCase("+64211234567", CaseType.SAVED_CONTACT, InterceptRoute.INSTANT, isSavedContact = true),
            TestCase("+6491234567", CaseType.UNSAVED_DOMESTIC, InterceptRoute.LIGHT, hour = 14),
            TestCase("+14155551234", CaseType.INTERNATIONAL, InterceptRoute.FULL, isInternational = true),
        )),

        // ── SA ──
        CountryCaseSet("SA", "사우디아라비아", listOf(
            TestCase("911", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("997", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("998", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("999", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("900", CaseType.SERVICE, InterceptRoute.SKIP),
            TestCase("+966512345678", CaseType.SAVED_CONTACT, InterceptRoute.INSTANT, isSavedContact = true),
            TestCase("+966112345678", CaseType.UNSAVED_DOMESTIC, InterceptRoute.LIGHT, hour = 14),
            TestCase("+14155551234", CaseType.INTERNATIONAL, InterceptRoute.FULL, isInternational = true),
        )),

        // ── AE ──
        CountryCaseSet("AE", "UAE", listOf(
            TestCase("999", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("998", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("112", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("600", CaseType.SERVICE, InterceptRoute.SKIP),
            TestCase("800", CaseType.SERVICE, InterceptRoute.SKIP),
            TestCase("+971501234567", CaseType.SAVED_CONTACT, InterceptRoute.INSTANT, isSavedContact = true),
            TestCase("+97141234567", CaseType.UNSAVED_DOMESTIC, InterceptRoute.LIGHT, hour = 14),
            TestCase("+14155551234", CaseType.INTERNATIONAL, InterceptRoute.FULL, isInternational = true),
        )),

        // ── IL ──
        CountryCaseSet("IL", "이스라엘", listOf(
            TestCase("100", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("101", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("102", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("112", CaseType.EMERGENCY, InterceptRoute.SKIP),
            TestCase("144", CaseType.SERVICE, InterceptRoute.SKIP),
            TestCase("199", CaseType.SERVICE, InterceptRoute.SKIP),
            TestCase("+972501234567", CaseType.SAVED_CONTACT, InterceptRoute.INSTANT, isSavedContact = true),
            TestCase("+97221234567", CaseType.UNSAVED_DOMESTIC, InterceptRoute.LIGHT, hour = 14),
            TestCase("+14155551234", CaseType.INTERNATIONAL, InterceptRoute.FULL, isInternational = true),
        )),
    )
}
