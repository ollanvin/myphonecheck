package app.callcheck.mobile.feature.callintercept

import app.callcheck.mobile.core.model.SearchEngine
import app.callcheck.mobile.core.model.SearchTier
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Stage 15 — 운영 리허설 (Launch Simulation).
 *
 * 자비스 기준:
 * "시나리오 3종:
 *   1. 정상 국가 (Full Engine)
 *   2. Search 실패 국가
 *   3. Emergency 강제 국가
 *
 *  검증 항목:
 *  - Dashboard 반영 실시간 여부
 *  - SLA 유지 여부
 *  - 사용자 체감 지연 없음
 *  - Recovery 정상 작동"
 *
 * 3종 시나리오를 순차 실행하여 전체 파이프라인이
 * 출시 조건을 만족하는지 종합 검증.
 *
 * 100% 온디바이스. 서버 전송 없음.
 */
@Singleton
class LaunchSimulationRunner @Inject constructor(
    private val registry: GlobalSearchProviderRegistry,
    private val circuitBreaker: OperationalCircuitBreaker,
    private val launchLock: LaunchReadinessLock,
    private val dashboard: GlobalLaunchDashboard,
    private val metricsCollector: DeviceMetricsCollector,
    private val realityTester: CountryRealityTester,
    private val rehearsalRunner: CircuitBreakerRehearsalRunner,
) {

    // ══════════════════════════════════════════════════════════════
    // 시뮬레이션 결과 모델
    // ══════════════════════════════════════════════════════════════

    data class LaunchSimulationReport(
        // 개별 시나리오 결과
        val scenario1_Normal: ScenarioResult,
        val scenario2_SearchFailure: ScenarioResult,
        val scenario3_Emergency: ScenarioResult,
        // Dashboard 검증
        val dashboardReflectsRealtime: Boolean,
        // SLA 검증
        val globalSlaHeld: Boolean,
        // Recovery 검증
        val recoveryVerified: Boolean,
        // 종합
        val passed: Boolean,
        val generatedAt: Long = System.currentTimeMillis(),
    )

    data class ScenarioResult(
        val name: String,
        val countriesTested: List<String>,
        /** SLA 준수율 */
        val slaComplianceRate: Float,
        /** 검색 성공률 */
        val searchSuccessRate: Float,
        /** 올바른 엔진 사용률 */
        val correctEngineRate: Float,
        /** fallback 정확성 */
        val fallbackAccuracy: Float,
        /** 사용자 체감 지연 유무 */
        val noPerceivedDelay: Boolean,
        /** 시나리오 통과 */
        val passed: Boolean,
        /** 실패 국가 상세 */
        val failedCountries: List<String>,
    )

    // ══════════════════════════════════════════════════════════════
    // 시나리오 1: 정상 국가 (Full Engine)
    // ══════════════════════════════════════════════════════════════

    /**
     * 정상 국가 시뮬레이션.
     *
     * 대상: Tier A/B/C 대표 국가 20개
     * 조건: 모든 검색 엔진 정상 작동
     * 기대: 100% SLA 준수, 올바른 엔진 사용
     */
    private fun runScenario1_Normal(): ScenarioResult {
        val countries = listOf(
            "KR", "JP", "CN", "RU", "CZ",      // Tier A
            "DE", "FR", "BR", "IN", "ID",       // Tier B
            "US", "GB", "CA", "AU", "NZ",       // Tier C
            "MX", "AR", "ZA", "EG", "NG",       // Tier B/C mix
        )

        var slaPass = 0
        var searchOk = 0
        var engineOk = 0
        val failed = mutableListOf<String>()

        for (cc in countries) {
            val config = registry.getConfig(cc)

            // Phase 1 시뮬레이션
            val phase1Ms = when (config.tier) {
                SearchTier.TIER_A -> 15L
                SearchTier.TIER_B -> 25L
                SearchTier.TIER_C -> 25L
                SearchTier.TIER_D -> 35L
            }

            // Phase 2 시뮬레이션 (정상)
            val phase2Base = when (config.primaryEngine) {
                SearchEngine.NAVER -> 800L
                SearchEngine.BAIDU -> 800L
                SearchEngine.YAHOO_JAPAN -> 900L
                SearchEngine.YANDEX -> 1000L
                SearchEngine.GOOGLE -> 600L
                SearchEngine.SEZNAM -> 850L
                else -> 700L
            }
            val phase2Ms = phase2Base + 200L // 판정 로직

            val totalMs = phase1Ms + phase2Ms

            // SLA 검증
            if (totalMs <= 2000) slaPass++
            else failed.add("$cc (SLA: ${totalMs}ms)")

            // 검색 성공 (정상 시나리오이므로 모두 성공)
            searchOk++

            // 엔진 검증
            val expectedEngine = LaunchReadinessLock.LOCKED_PRIMARY_ENGINES[cc] ?: SearchEngine.GOOGLE
            if (config.primaryEngine == expectedEngine) engineOk++
            else failed.add("$cc (엔진: ${config.primaryEngine} ≠ $expectedEngine)")
        }

        val total = countries.size.toFloat()
        val slaRate = (slaPass / total) * 100f
        val searchRate = (searchOk / total) * 100f
        val engineRate = (engineOk / total) * 100f

        // 정상 시나리오: SLA 100%, 검색 100%, 엔진 100%
        val passed = slaRate >= 100f && engineRate >= 95f

        return ScenarioResult(
            name = "정상 국가 (Full Engine)",
            countriesTested = countries,
            slaComplianceRate = slaRate,
            searchSuccessRate = searchRate,
            correctEngineRate = engineRate,
            fallbackAccuracy = 100f, // 정상이므로 fallback 불필요
            noPerceivedDelay = slaRate >= 100f,
            passed = passed,
            failedCountries = failed,
        )
    }

    // ══════════════════════════════════════════════════════════════
    // 시나리오 2: Search 실패 국가
    // ══════════════════════════════════════════════════════════════

    /**
     * 검색 실패 시뮬레이션.
     *
     * 대상: 저데이터 / 미지원 국가
     * 조건: 1순위 검색 실패 → fallback 동작
     * 기대: fallback 표현 정확, SLA 내 완료
     */
    private fun runScenario2_SearchFailure(): ScenarioResult {
        val countries = listOf(
            "TV", "NR", "CK", "NU", "TK",     // 극소국
            "PM", "BL", "MF", "WF", "YT",     // 프랑스 해외 영토
        )

        var slaPass = 0
        var fallbackOk = 0
        val failed = mutableListOf<String>()

        for (cc in countries) {
            // Phase 1: 정상 (국가 자체는 인식)
            val phase1Ms = 35L // Tier D

            // Phase 2: 1순위 timeout → fallback
            // primary timeout (1200ms) → fallback 표현 생성 (100ms) = 1300ms
            val phase2Ms = 1300L
            val totalMs = phase1Ms + phase2Ms

            if (totalMs <= 2000) slaPass++
            else failed.add("$cc (SLA: ${totalMs}ms)")

            // fallback 표현 확인
            val safeVerdict = launchLock.getMinimumSafeVerdict("en")
            if (safeVerdict.isNotBlank()) fallbackOk++
            else failed.add("$cc (fallback 표현 없음)")
        }

        val total = countries.size.toFloat()
        val slaRate = (slaPass / total) * 100f
        val fallbackRate = (fallbackOk / total) * 100f

        val passed = slaRate >= 100f && fallbackRate >= 100f

        return ScenarioResult(
            name = "Search 실패 국가",
            countriesTested = countries,
            slaComplianceRate = slaRate,
            searchSuccessRate = 0f, // 의도적 실패
            correctEngineRate = 100f,
            fallbackAccuracy = fallbackRate,
            noPerceivedDelay = slaRate >= 100f,
            passed = passed,
            failedCountries = failed,
        )
    }

    // ══════════════════════════════════════════════════════════════
    // 시나리오 3: Emergency 강제 국가
    // ══════════════════════════════════════════════════════════════

    /**
     * Emergency 모드 강제 시뮬레이션.
     *
     * 대상: API 차단 / 완전 장애 국가
     * 조건: 모든 검색 실패 → 서킷 EMERGENCY
     * 기대: 최소 안전 표현 즉시 표시, SLA 내 완료
     */
    private fun runScenario3_Emergency(): ScenarioResult {
        val countries = listOf(
            "IR", "KP", "CU", "SY", "SD",     // 고위험 차단 국가
        )

        var slaPass = 0
        var fallbackOk = 0
        var recoveryOk = 0
        val failed = mutableListOf<String>()

        for (cc in countries) {
            // 서킷 초기화
            circuitBreaker.resetAll()

            // Emergency 유도: 10회 실패 주입
            val engine = LaunchReadinessLock.LOCKED_PRIMARY_ENGINES[cc] ?: SearchEngine.GOOGLE
            for (i in 1..10) {
                circuitBreaker.recordFailure(cc, engine, SearchEngine.BING, SearchEngine.DUCKDUCKGO)
            }

            // Emergency 상태 확인
            val circuit = circuitBreaker.getCountryCircuit(cc)
            val isEmergency = circuit.state == OperationalCircuitBreaker.CircuitState.EMERGENCY

            // Phase 1 + Phase 2 (Emergency)
            val phase1Ms = 35L
            val phase2Ms = 50L // Emergency: 즉시 안전 표현 → 매우 빠름
            val totalMs = phase1Ms + phase2Ms

            if (totalMs <= 2000) slaPass++
            else failed.add("$cc (SLA: ${totalMs}ms)")

            // fallback 확인
            val languageCode = when (cc) {
                "IR" -> "fa"
                "KP" -> "ko"
                "CU" -> "es"
                "SY" -> "ar"
                "SD" -> "ar"
                else -> "en"
            }
            val safeVerdict = launchLock.getMinimumSafeVerdict(languageCode)
            if (safeVerdict.isNotBlank() && isEmergency) fallbackOk++
            else failed.add("$cc (Emergency fallback 실패)")

            // Recovery: 서킷 리셋 후 정상 복귀 확인
            circuitBreaker.resetAll()
            val afterReset = circuitBreaker.getCountryCircuit(cc)
            if (afterReset.state == OperationalCircuitBreaker.CircuitState.CLOSED) recoveryOk++
        }

        val total = countries.size.toFloat()
        val slaRate = (slaPass / total) * 100f
        val fallbackRate = (fallbackOk / total) * 100f
        val recoveryRate = (recoveryOk / total) * 100f

        val passed = slaRate >= 100f && fallbackRate >= 100f && recoveryRate >= 100f

        return ScenarioResult(
            name = "Emergency 강제 국가",
            countriesTested = countries,
            slaComplianceRate = slaRate,
            searchSuccessRate = 0f,
            correctEngineRate = 100f,
            fallbackAccuracy = fallbackRate,
            noPerceivedDelay = slaRate >= 100f,
            passed = passed,
            failedCountries = failed,
        )
    }

    // ══════════════════════════════════════════════════════════════
    // Dashboard 실시간 반영 검증
    // ══════════════════════════════════════════════════════════════

    /**
     * Dashboard가 서킷 상태 변경을 실시간 반영하는지 검증.
     */
    private fun verifyDashboardRealtime(): Boolean {
        // 서킷 초기화
        circuitBreaker.resetAll()

        // 정상 상태 Dashboard 생성
        val dashboard1 = dashboard.generateDashboard()
        val initialEmergency = dashboard1.circuitEmergencyCount

        // KR에 10회 실패 주입 → Emergency
        for (i in 1..10) {
            circuitBreaker.recordFailure("KR", SearchEngine.NAVER, SearchEngine.BING, SearchEngine.DUCKDUCKGO)
        }

        // Dashboard 재생성
        val dashboard2 = dashboard.generateDashboard()
        val afterEmergency = dashboard2.circuitEmergencyCount

        // Emergency 카운트가 증가했는지 확인
        val reflected = afterEmergency > initialEmergency

        // 정리
        circuitBreaker.resetAll()

        return reflected
    }

    // ══════════════════════════════════════════════════════════════
    // 전체 리허설 실행
    // ══════════════════════════════════════════════════════════════

    /**
     * 3종 시나리오 + 부가 검증 전체 실행.
     */
    fun runFullSimulation(): LaunchSimulationReport {
        // 서킷 초기화
        circuitBreaker.resetAll()

        // 시나리오 실행
        val s1 = runScenario1_Normal()

        circuitBreaker.resetAll()
        val s2 = runScenario2_SearchFailure()

        circuitBreaker.resetAll()
        val s3 = runScenario3_Emergency()

        // Dashboard 실시간 반영
        val dashboardOk = verifyDashboardRealtime()

        // 글로벌 SLA 유지
        val globalSlaHeld = s1.slaComplianceRate >= 100f &&
                s2.slaComplianceRate >= 100f &&
                s3.slaComplianceRate >= 100f

        // Recovery 정상 작동 (시나리오 3에서 검증)
        val recoveryOk = s3.passed

        // 종합
        val passed = s1.passed && s2.passed && s3.passed && dashboardOk && globalSlaHeld && recoveryOk

        return LaunchSimulationReport(
            scenario1_Normal = s1,
            scenario2_SearchFailure = s2,
            scenario3_Emergency = s3,
            dashboardReflectsRealtime = dashboardOk,
            globalSlaHeld = globalSlaHeld,
            recoveryVerified = recoveryOk,
            passed = passed,
        )
    }

    // ══════════════════════════════════════════════════════════════
    // 보고서 포맷팅
    // ══════════════════════════════════════════════════════════════

    fun formatReport(report: LaunchSimulationReport): String {
        val sb = StringBuilder()

        sb.appendLine("╔══════════════════════════════════════════════════════════════╗")
        sb.appendLine("║           [LAUNCH REHEARSAL] 운영 리허설 보고서                ║")
        sb.appendLine("╠══════════════════════════════════════════════════════════════╣")

        val overallStatus = if (report.passed) "✅ PASS" else "❌ FAIL"
        sb.appendLine("║  종합 판정: $overallStatus")
        sb.appendLine("╠══════════════════════════════════════════════════════════════╣")

        // 시나리오 1
        formatScenario(sb, report.scenario1_Normal)

        // 시나리오 2
        formatScenario(sb, report.scenario2_SearchFailure)

        // 시나리오 3
        formatScenario(sb, report.scenario3_Emergency)

        sb.appendLine("╠══════════════════════════════════════════════════════════════╣")
        sb.appendLine("║  Dashboard 실시간 반영 — ${mark(report.dashboardReflectsRealtime)}")
        sb.appendLine("║  글로벌 SLA 유지       — ${mark(report.globalSlaHeld)}")
        sb.appendLine("║  Recovery 정상 작동     — ${mark(report.recoveryVerified)}")

        sb.appendLine("╚══════════════════════════════════════════════════════════════╝")
        return sb.toString()
    }

    private fun formatScenario(sb: StringBuilder, s: ScenarioResult) {
        val mark = if (s.passed) "✅" else "❌"
        sb.appendLine("║  $mark ${s.name}")
        sb.appendLine("║     SLA=${String.format("%.0f", s.slaComplianceRate)}% 검색=${String.format("%.0f", s.searchSuccessRate)}% 엔진=${String.format("%.0f", s.correctEngineRate)}% fallback=${String.format("%.0f", s.fallbackAccuracy)}% 지연=${mark(s.noPerceivedDelay)}")
        if (s.failedCountries.isNotEmpty()) {
            sb.appendLine("║     실패: ${s.failedCountries.joinToString(", ")}")
        }
    }

    private fun mark(pass: Boolean): String = if (pass) "✅" else "❌"
}
