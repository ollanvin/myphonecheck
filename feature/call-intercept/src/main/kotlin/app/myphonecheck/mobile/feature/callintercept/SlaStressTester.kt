package app.myphonecheck.mobile.feature.callintercept

import app.myphonecheck.mobile.core.model.SearchTier
import javax.inject.Inject
import javax.inject.Singleton

/**
 * SLA 스트레스 테스터.
 *
 * 자비스 기준: "1000 calls / 국가, 2초 SLA 통과율 계산"
 *
 * 빅테크 정석:
 * - 국가당 티어별 콜 수 (Tier1: 1000, Tier2: 500, Tier3: 200)
 * - 2초 SLA 통과율 = (hardDeadline 이내 완료 콜) / (전체 콜) × 100
 * - P50/P95/P99 지연시간 산출
 * - fallback 발생율 추적
 * - FAIL 국가 자동 식별 + 원인 분류
 *
 * 100% 온디바이스. 서버 전송 없음.
 */
@Singleton
class SlaStressTester @Inject constructor(
    private val simulationEngine: CallSimulationEngine,
    private val registry: GlobalSearchProviderRegistry,
) {

    /**
     * 단일 국가 스트레스 테스트 결과.
     */
    data class CountryStressResult(
        val countryCode: String,
        val validationTier: CallSimulationEngine.ValidationTier,
        val searchTier: SearchTier,
        val totalCalls: Int,
        val slaPassCount: Int,
        val slaFailCount: Int,
        val slaPassRate: Float,
        val p50LatencyMs: Long,
        val p95LatencyMs: Long,
        val p99LatencyMs: Long,
        val maxLatencyMs: Long,
        val fallbackRate: Float,
        val correctClassificationRate: Float,
        val failureBreakdown: FailureBreakdown,
    ) {
        val passed: Boolean get() = slaPassRate >= PASS_THRESHOLD
    }

    /**
     * 실패 원인 분류.
     */
    data class FailureBreakdown(
        val slaViolations: Int,
        val misclassifications: Int,
        val insufficientResults: Int,
        val offlineFallbacks: Int,
    )

    /**
     * 전체 스트레스 테스트 보고서.
     */
    data class StressTestReport(
        val totalCountries: Int,
        val passCountries: Int,
        val failCountries: Int,
        val totalCalls: Int,
        val overallSlaPassRate: Float,
        val results: List<CountryStressResult>,
        val failedCountryDetails: List<CountryStressResult>,
    ) {
        val allPassed: Boolean get() = failCountries == 0

        fun toJarvisFormat(): String = buildString {
            appendLine("═══ SLA Stress Test 보고 ═══")
            appendLine()
            appendLine("총 국가: ${totalCountries}개국")
            appendLine("총 콜: ${totalCalls}")
            appendLine("PASS 국가: ${passCountries} | FAIL 국가: ${failCountries}")
            appendLine("전체 SLA 통과율: ${String.format("%.2f", overallSlaPassRate)}%")
            appendLine()

            if (failedCountryDetails.isNotEmpty()) {
                appendLine("── FAIL 국가 ──")
                failedCountryDetails.sortedBy { it.slaPassRate }.forEach { r ->
                    appendLine("  ❌ [${r.countryCode}] SLA ${String.format("%.1f", r.slaPassRate)}% | P95=${r.p95LatencyMs}ms | fallback=${String.format("%.1f", r.fallbackRate * 100)}%")
                    val fb = r.failureBreakdown
                    if (fb.slaViolations > 0) appendLine("     SLA 초과: ${fb.slaViolations}건")
                    if (fb.misclassifications > 0) appendLine("     오판: ${fb.misclassifications}건")
                }
                appendLine()
            }

            // 티어별 요약
            val byTier = results.groupBy { it.validationTier }
            appendLine("── 검증 티어별 ──")
            for (tier in CallSimulationEngine.ValidationTier.entries) {
                val tierResults = byTier[tier] ?: emptyList()
                val tierPass = tierResults.count { it.passed }
                val tierCalls = tierResults.sumOf { it.totalCalls }
                val tierAvgSla = if (tierResults.isNotEmpty()) tierResults.map { it.slaPassRate }.average() else 0.0
                appendLine("  ${tier.label}: ${tierResults.size}개국 | ${tierPass}/${tierResults.size} PASS | ${tierCalls}콜 | 평균 SLA ${String.format("%.1f", tierAvgSla)}%")
            }
            appendLine()

            // 상위/하위 국가
            appendLine("── P95 지연 Top 10 (최악) ──")
            results.sortedByDescending { it.p95LatencyMs }.take(10).forEach { r ->
                appendLine("  [${r.countryCode}] P95=${r.p95LatencyMs}ms | P99=${r.p99LatencyMs}ms | SLA ${String.format("%.1f", r.slaPassRate)}%")
            }
        }
    }

    /**
     * 전체 국가 스트레스 테스트 실행.
     */
    suspend fun runFullStressTest(): StressTestReport {
        val results = mutableListOf<CountryStressResult>()

        registry.allCountries().forEach { config ->
            val result = runCountryStressTest(config.countryCode)
            results.add(result)
        }

        val passCountries = results.count { it.passed }
        val failCountries = results.count { !it.passed }
        val totalCalls = results.sumOf { it.totalCalls }
        val totalSlaPass = results.sumOf { it.slaPassCount }
        val overallRate = if (totalCalls > 0) totalSlaPass.toFloat() / totalCalls * 100f else 0f

        return StressTestReport(
            totalCountries = results.size,
            passCountries = passCountries,
            failCountries = failCountries,
            totalCalls = totalCalls,
            overallSlaPassRate = overallRate,
            results = results,
            failedCountryDetails = results.filter { !it.passed },
        )
    }

    /**
     * 단일 국가 스트레스 테스트.
     */
    suspend fun runCountryStressTest(countryCode: String): CountryStressResult {
        val calls = simulationEngine.generateStressTestCalls(countryCode)
        val config = registry.getConfig(countryCode)
        val latencies = mutableListOf<Long>()
        var slaPass = 0
        var slaFail = 0
        var correct = 0
        var misclassified = 0
        var insufficient = 0
        var offlineFallback = 0
        var fallbacks = 0

        calls.forEach { call ->
            val result = simulationEngine.simulate(call)
            latencies.add(result.simulatedLatencyMs)

            if (result.slaPassed) slaPass++ else slaFail++
            if (result.fallbackTriggered) fallbacks++

            when (result.verdict) {
                CallSimulationEngine.SimVerdict.CORRECT -> correct++
                CallSimulationEngine.SimVerdict.MISCLASSIFIED -> misclassified++
                CallSimulationEngine.SimVerdict.INSUFFICIENT_BUT_DISPLAYED -> insufficient++
                CallSimulationEngine.SimVerdict.OFFLINE_FALLBACK -> offlineFallback++
                CallSimulationEngine.SimVerdict.SLA_VIOLATION -> { /* counted in slaFail */ }
            }
        }

        latencies.sort()
        val total = calls.size

        return CountryStressResult(
            countryCode = countryCode,
            validationTier = simulationEngine.getValidationTier(countryCode),
            searchTier = config.tier,
            totalCalls = total,
            slaPassCount = slaPass,
            slaFailCount = slaFail,
            slaPassRate = if (total > 0) slaPass.toFloat() / total * 100f else 0f,
            p50LatencyMs = percentile(latencies, 50),
            p95LatencyMs = percentile(latencies, 95),
            p99LatencyMs = percentile(latencies, 99),
            maxLatencyMs = latencies.lastOrNull() ?: 0L,
            fallbackRate = if (total > 0) fallbacks.toFloat() / total else 0f,
            correctClassificationRate = if (total > 0) correct.toFloat() / total * 100f else 0f,
            failureBreakdown = FailureBreakdown(
                slaViolations = slaFail,
                misclassifications = misclassified,
                insufficientResults = insufficient,
                offlineFallbacks = offlineFallback,
            ),
        )
    }

    // ── Internal ──

    private fun percentile(sorted: List<Long>, p: Int): Long {
        if (sorted.isEmpty()) return 0L
        val index = ((p / 100.0) * (sorted.size - 1)).toInt().coerceIn(0, sorted.size - 1)
        return sorted[index]
    }

    companion object {
        /** SLA 통과율 기준: 95% 이상이면 PASS */
        const val PASS_THRESHOLD = 95.0f
    }
}
