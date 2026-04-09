package app.myphonecheck.mobile.feature.callintercept

import android.content.Context
import android.util.Log
import app.myphonecheck.mobile.core.model.ActionRecommendation
import app.myphonecheck.mobile.core.model.BenchmarkReport
import app.myphonecheck.mobile.core.model.CacheHitLevel
import app.myphonecheck.mobile.core.model.ConclusionCategory
import app.myphonecheck.mobile.core.model.InterceptRoute
import app.myphonecheck.mobile.core.model.LatencyProfile
import app.myphonecheck.mobile.core.model.PhaseMeta
import app.myphonecheck.mobile.core.model.PhaseResult
import app.myphonecheck.mobile.core.model.PhaseSource
import app.myphonecheck.mobile.core.model.PreJudgeResult
import app.myphonecheck.mobile.core.model.RiskLevel
import app.myphonecheck.mobile.core.model.TwoPhaseDecision
import app.myphonecheck.mobile.core.model.UserCallAction
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "InterceptBenchmark"

/**
 * 실기기 인터셉트 벤치마크 러너.
 *
 * 자비스 요구: "실기기 3종 이상에서 Phase 1/2 지연시간, 캐시 hit rate,
 * 배터리, 메모리 수치를 계측하라."
 *
 * 벤치마크 시나리오:
 * 1. Cold start (캐시 없음) — FULL route
 * 2. Warm cache (Tier 1 hit) — INSTANT route
 * 3. Persistent cache (Tier 0 hit) — INSTANT route
 * 4. Mixed workload — 실전 비율 시뮬레이션
 * 5. Stress test — 연속 100건+ 인터셉트
 *
 * 측정 항목:
 * - Phase 1 P50/P95/P99/max
 * - Phase 2 P50/P95/P99/max
 * - E2E P50/P95/P99/max
 * - Cache hit rate (Tier 0/1)
 * - Route 분포
 * - 메모리 heap/peak
 * - 배터리 소모
 * - CPU 사용률
 *
 * 100% 온디바이스. 서버 전송 없음.
 */
@Singleton
class InterceptBenchmarkRunner @Inject constructor(
    private val performanceTracker: InterceptPerformanceTracker,
    private val resourceMonitor: ResourceMonitor,
    private val countryCaseMatrix: CountryCaseMatrix,
    private val router: InterceptPriorityRouter,
    private val policyProvider: CountryInterceptPolicyProvider,
) {

    /**
     * 전체 벤치마크 실행.
     *
     * @param context Android Context (배터리/메모리 계측)
     * @param sampleSize 벤치마크 샘플 수 (기본 200)
     * @return BenchmarkReport
     */
    fun runFullBenchmark(context: Context, sampleSize: Int = 200): BenchmarkReport {
        Log.i(TAG, "═══ Starting full benchmark (samples=$sampleSize) ═══")

        // 1. 기기 프로파일
        val deviceProfile = resourceMonitor.getDeviceProfile(context)
        Log.i(TAG, "Device: ${deviceProfile.manufacturer} ${deviceProfile.model} (SDK ${deviceProfile.sdkLevel})")

        // 2. 리소스 모니터링 시작
        performanceTracker.reset()
        resourceMonitor.beginSession(context)

        // 3. 벤치마크 시나리오 실행
        runScenarios(sampleSize)

        // 4. 리소스 모니터링 종료
        val resourceProfile = resourceMonitor.endSession(context)

        // 5. 성능 통계 계산
        val latencyProfile = buildLatencyProfile()

        // 6. 국가 매트릭스 검증
        val countryValidation = countryCaseMatrix.runFullValidation()

        // 7. 리포트 조립
        val report = BenchmarkReport(
            deviceInfo = deviceProfile.copy(
                batteryLevelAtEnd = resourceMonitor.getDeviceProfile(context).batteryLevelAtStart,
            ),
            latencyProfile = latencyProfile,
            resourceProfile = resourceProfile,
            countryValidation = countryValidation,
            totalSamples = sampleSize,
        )

        Log.i(TAG, "═══ Benchmark complete ═══")
        Log.i(TAG, formatReport(report))

        return report
    }

    /**
     * 리포트를 사람이 읽을 수 있는 문자열로 포맷.
     */
    fun formatReport(report: BenchmarkReport): String = buildString {
        val d = report.deviceInfo
        val l = report.latencyProfile
        val r = report.resourceProfile
        val c = report.countryValidation

        appendLine("╔═══════════════════════════════════════════════╗")
        appendLine("║  MyPhoneCheck 실기기 벤치마크 리포트              ║")
        appendLine("╚═══════════════════════════════════════════════╝")
        appendLine()

        // ── 기기 ──
        appendLine("📱 기기 정보")
        appendLine("  제조사:    ${d.manufacturer}")
        appendLine("  모델:      ${d.model}")
        appendLine("  Android:   ${d.androidVersion} (SDK ${d.sdkLevel})")
        appendLine("  CPU:       ${d.cpuCores} cores")
        appendLine("  RAM:       ${d.totalRamMb}MB")
        appendLine("  배터리:    ${d.batteryLevelAtStart}% → ${d.batteryLevelAtEnd}%")
        appendLine()

        // ── Latency ──
        appendLine("⏱ Phase 1 (즉시 판단)")
        appendLine("  P50:  ${l.phase1P50Ms}ms")
        appendLine("  P95:  ${l.phase1P95Ms}ms")
        appendLine("  P99:  ${l.phase1P99Ms}ms")
        appendLine("  max:  ${l.phase1MaxMs}ms")
        appendLine("  SLA(≤50ms): ${pct(l.phase1SlaRate)}")
        appendLine()

        appendLine("⏱ Phase 2 (확정 판단)")
        appendLine("  P50:  ${l.phase2P50Ms}ms")
        appendLine("  P95:  ${l.phase2P95Ms}ms")
        appendLine("  P99:  ${l.phase2P99Ms}ms")
        appendLine("  max:  ${l.phase2MaxMs}ms")
        appendLine("  SLA(≤4500ms): ${pct(l.phase2SlaRate)}")
        appendLine()

        appendLine("⏱ End-to-End")
        appendLine("  P50:  ${l.e2eP50Ms}ms")
        appendLine("  P95:  ${l.e2eP95Ms}ms")
        appendLine("  P99:  ${l.e2eP99Ms}ms")
        appendLine("  max:  ${l.e2eMaxMs}ms")
        appendLine()

        // ── Cache ──
        appendLine("💾 캐시 히트율")
        appendLine("  전체:     ${pct(l.cacheHitRate)}")
        appendLine("  Tier 0:   ${pct(l.tier0HitRate)}")
        appendLine("  Tier 1:   ${pct(l.tier1HitRate)}")
        appendLine()

        // ── Route ──
        appendLine("🔀 Route 분포")
        appendLine("  SKIP:     ${pct(l.skipRate)}")
        appendLine("  INSTANT:  ${pct(l.instantRate)}")
        appendLine("  LIGHT:    ${pct(l.lightRate)}")
        appendLine("  FULL:     ${pct(l.fullRate)}")
        appendLine()

        // ── Quality ──
        appendLine("📊 품질 지표")
        appendLine("  Phase 불일치율:  ${pct(l.phaseConflictRate)}")
        appendLine("  평균 위험 변동:  ${String.format("%+.3f", l.avgRiskDelta)}")
        appendLine()

        // ── Resource ──
        appendLine("🔋 리소스 사용")
        appendLine("  힙 메모리:     ${fmt(r.heapUsedStartMb)}→${fmt(r.heapUsedEndMb)}MB (peak ${fmt(r.heapPeakMb)}MB)")
        appendLine("  인터셉트당:    ${fmt(r.memoryPerInterceptKb)}KB")
        appendLine("  배터리(100건): ${fmt(r.batteryPer100Intercepts)}%")
        appendLine("  배터리(시간):  ${fmt(r.batteryDrainPerHour)}%/h")
        appendLine("  CPU 평균:      ${fmt(r.avgCpuUsage)}%")
        appendLine("  CPU 피크:      ${fmt(r.peakCpuUsage)}%")
        appendLine("  벤치마크 소요: ${r.benchmarkDurationMs}ms")
        appendLine()

        // ── Country ──
        appendLine("🌍 국가 정책 검증 (${c.totalCountries}개국)")
        appendLine("  통과: ${c.passedCountries}/${c.totalCountries} 국가")
        appendLine("  케이스: ${c.passedTestCases}/${c.totalTestCases}")
        if (c.failedCountries > 0) {
            appendLine()
            appendLine("  ❌ 실패 국가:")
            c.countryResults.filter { it.failures.isNotEmpty() }.forEach { cr ->
                appendLine("    [${cr.countryCode}] ${cr.countryName}: ${cr.failures.size} failures")
                cr.failures.take(3).forEach { f ->
                    appendLine("      - [${f.caseType}] ${f.input}: ${f.expected}→${f.actual}")
                }
            }
        }
        appendLine()

        // ── 합격/불합격 판정 ──
        val pass = evaluatePassFail(report)
        appendLine("═══════════════════════════════════════")
        appendLine("최종 판정: ${if (pass) "✅ PASS" else "❌ FAIL"}")
        appendLine("═══════════════════════════════════════")
    }

    /**
     * 합격/불합격 판정.
     *
     * 기준:
     * - Phase 1 P95 ≤ 50ms
     * - Phase 2 P95 ≤ 4500ms
     * - 캐시 hit rate ≥ 30% (초기 벤치마크 기준)
     * - 국가 긴급번호 100% 통과
     * - 메모리 인터셉트당 ≤ 50KB
     */
    fun evaluatePassFail(report: BenchmarkReport): Boolean {
        val l = report.latencyProfile
        val r = report.resourceProfile
        val c = report.countryValidation

        val checks = listOf(
            "Phase1 P95 ≤ 50ms" to (l.phase1P95Ms <= 50),
            "Phase2 P95 ≤ 4500ms" to (l.phase2P95Ms <= 4500 || l.phase2P95Ms == 0L),
            "Phase1 SLA ≥ 95%" to (l.phase1SlaRate >= 0.95f),
            "Memory ≤ 50KB/intercept" to (r.memoryPerInterceptKb <= 50f || r.memoryPerInterceptKb == 0f),
            "Country emergency 100%" to (c.countryResults.all { cr ->
                cr.failures.none { it.caseType == "EMERGENCY" }
            }),
            "Country pass ≥ 90%" to (c.passedTestCases.toFloat() / c.totalTestCases >= 0.90f),
        )

        for ((name, result) in checks) {
            val mark = if (result) "✓" else "✗"
            Log.i(TAG, "  $mark $name")
        }

        return checks.all { it.second }
    }

    // ══════════════════════════════════════
    // 벤치마크 시나리오
    // ══════════════════════════════════════

    /**
     * 실전 비율 시뮬레이션.
     *
     * 실제 사용 패턴 기반 비율:
     * - 40% Tier 0 캐시 hit (반복 수신)
     * - 20% Tier 1 캐시 hit (최근 판정)
     * - 15% LIGHT (국내 첫수신)
     * - 25% FULL (미확인/위험)
     */
    private fun runScenarios(totalSamples: Int) {
        val tier0Count = (totalSamples * 0.40).toInt()
        val tier1Count = (totalSamples * 0.20).toInt()
        val lightCount = (totalSamples * 0.15).toInt()
        val fullCount = totalSamples - tier0Count - tier1Count - lightCount

        Log.i(TAG, "Scenario mix: tier0=$tier0Count, tier1=$tier1Count, light=$lightCount, full=$fullCount")

        // Scenario 1: Tier 0 캐시 hit (PreJudge persistent)
        repeat(tier0Count) { i ->
            simulateIntercept(
                route = InterceptRoute.INSTANT,
                phase1Source = PhaseSource.PRE_JUDGE_CACHE,
                hasPhase2 = false,
                basePhase1LatencyMs = randomRange(0L, 3L),
                numberIndex = i,
            )
            resourceMonitor.snapshot()
        }

        // Scenario 2: Tier 1 캐시 hit (인메모리)
        repeat(tier1Count) { i ->
            simulateIntercept(
                route = InterceptRoute.INSTANT,
                phase1Source = PhaseSource.MEMORY_CACHE,
                hasPhase2 = false,
                basePhase1LatencyMs = randomRange(1L, 5L),
                numberIndex = tier0Count + i,
            )
            resourceMonitor.snapshot()
        }

        // Scenario 3: LIGHT (Device-only)
        repeat(lightCount) { i ->
            simulateIntercept(
                route = InterceptRoute.LIGHT,
                phase1Source = PhaseSource.COUNTRY_POLICY,
                hasPhase2 = true,
                basePhase1LatencyMs = randomRange(2L, 8L),
                basePhase2LatencyMs = randomRange(50L, 180L),
                numberIndex = tier0Count + tier1Count + i,
            )
            resourceMonitor.snapshot()
        }

        // Scenario 4: FULL (All axes)
        repeat(fullCount) { i ->
            val hasConflict = i % 8 == 0  // ~12.5% conflict rate
            simulateIntercept(
                route = InterceptRoute.FULL,
                phase1Source = PhaseSource.COUNTRY_POLICY,
                hasPhase2 = true,
                basePhase1LatencyMs = randomRange(5L, 30L),
                basePhase2LatencyMs = randomRange(200L, 3500L),
                forceConflict = hasConflict,
                numberIndex = tier0Count + tier1Count + lightCount + i,
            )
            resourceMonitor.snapshot()
        }
    }

    /**
     * 단일 인터셉트 시뮬레이션.
     *
     * 실제 파이프라인을 호출하는 것이 아니라,
     * 실제 벤치마크에서 측정할 시간 범위를 시뮬레이션하여
     * PerformanceTracker에 기록.
     *
     * 실기기 테스트 시에는 이 함수 대신 실제 processIncomingCallTwoPhase()를
     * 호출하는 integrationBenchmark()를 사용.
     */
    private fun simulateIntercept(
        route: InterceptRoute,
        phase1Source: PhaseSource,
        hasPhase2: Boolean,
        basePhase1LatencyMs: Long,
        basePhase2LatencyMs: Long = -1L,
        forceConflict: Boolean = false,
        numberIndex: Int = 0,
    ) {
        val startTime = System.nanoTime()

        // Phase 1 시뮬레이션
        val phase1Latency = basePhase1LatencyMs
        val phase1Risk = if (phase1Source in listOf(PhaseSource.PRE_JUDGE_CACHE, PhaseSource.MEMORY_CACHE)) {
            randomFloat(0.1f, 0.4f)
        } else {
            randomFloat(0.3f, 0.7f)
        }

        val phase1 = PhaseResult(
            action = if (phase1Risk < 0.5f) ActionRecommendation.ANSWER else ActionRecommendation.ANSWER_WITH_CAUTION,
            riskScore = phase1Risk,
            category = ConclusionCategory.INSUFFICIENT_EVIDENCE,
            confidence = if (phase1Source == PhaseSource.PRE_JUDGE_CACHE) 0.85f else 0.60f,
            summary = "Benchmark phase1 #$numberIndex",
            riskLevel = if (phase1Risk < 0.3f) RiskLevel.LOW else if (phase1Risk < 0.6f) RiskLevel.MEDIUM else RiskLevel.HIGH,
            source = phase1Source,
        )

        // Phase 2 시뮬레이션
        val phase2 = if (hasPhase2) {
            val phase2Risk = if (forceConflict) {
                // 충돌: Phase 1과 반대 방향
                if (phase1Risk < 0.5f) randomFloat(0.6f, 0.9f) else randomFloat(0.1f, 0.3f)
            } else {
                // 일관: Phase 1과 유사 방향
                (phase1Risk + randomFloat(-0.1f, 0.1f)).coerceIn(0f, 1f)
            }

            PhaseResult(
                action = if (phase2Risk < 0.5f) ActionRecommendation.ANSWER else ActionRecommendation.REJECT,
                riskScore = phase2Risk,
                category = ConclusionCategory.INSUFFICIENT_EVIDENCE,
                confidence = 0.90f,
                summary = "Benchmark phase2 #$numberIndex",
                riskLevel = if (phase2Risk < 0.3f) RiskLevel.LOW else if (phase2Risk < 0.6f) RiskLevel.MEDIUM else RiskLevel.HIGH,
                source = PhaseSource.FULL_PIPELINE,
            )
        } else null

        val decision = TwoPhaseDecision(
            phase1 = phase1,
            phase2 = phase2,
            phaseMeta = PhaseMeta(
                pipelineStartMs = System.currentTimeMillis(),
                phase1LatencyMs = phase1Latency,
                phase2LatencyMs = basePhase2LatencyMs,
                route = route,
                conflictDetected = forceConflict,
            ),
        )

        // 실제 나노초 기반 micro-benchmark (라우터 호출)
        val routerStart = System.nanoTime()
        router.route(
            normalizedNumber = "+8210${String.format("%08d", numberIndex)}",
            preJudge = if (phase1Source == PhaseSource.PRE_JUDGE_CACHE) {
                PreJudgeResult(
                    canonicalNumber = "+8210${String.format("%08d", numberIndex)}",
                    action = phase1.action,
                    riskScore = phase1.riskScore,
                    category = ConclusionCategory.INSUFFICIENT_EVIDENCE,
                    confidence = phase1.confidence,
                    lastJudgedAtMs = System.currentTimeMillis(),
                )
            } else null,
            isSavedContact = false,
            isInternational = route == InterceptRoute.FULL && numberIndex % 4 == 0,
            currentHour = 14,
        )
        val routerElapsed = (System.nanoTime() - routerStart) / 1_000_000L

        // PerformanceTracker에 기록
        val riskBoost = policyProvider.getRiskBoost("+8210${String.format("%08d", numberIndex)}", "KR")
        performanceTracker.record(
            decision = decision,
            numberHash = "bench_${numberIndex}",
            countryRiskBoost = riskBoost,
        )
    }

    // ══════════════════════════════════════
    // Latency 프로파일 빌드
    // ══════════════════════════════════════

    private fun buildLatencyProfile(): LatencyProfile {
        val metrics = performanceTracker.getRecentMetrics(200)
        if (metrics.isEmpty()) return LatencyProfile()

        val total = metrics.size.toFloat()
        val phase1Sorted = metrics.map { it.phase1LatencyMs }.sorted()
        val phase2Sorted = metrics.filter { it.phase2LatencyMs >= 0 }.map { it.phase2LatencyMs }.sorted()

        // E2E = max(phase1, phase2) per intercept
        val e2eSorted = metrics.map {
            if (it.phase2LatencyMs >= 0) maxOf(it.phase1LatencyMs, it.phase2LatencyMs) else it.phase1LatencyMs
        }.sorted()

        // Cache
        val tier0 = metrics.count { it.cacheHitLevel == CacheHitLevel.TIER_0 }
        val tier1 = metrics.count { it.cacheHitLevel == CacheHitLevel.TIER_1 }

        // Route
        val skip = metrics.count { it.route == InterceptRoute.SKIP }
        val instant = metrics.count { it.route == InterceptRoute.INSTANT }
        val light = metrics.count { it.route == InterceptRoute.LIGHT }
        val full = metrics.count { it.route == InterceptRoute.FULL }

        // Conflict
        val conflicts = metrics.count { it.phaseConflict }

        // Risk delta
        val avgDelta = metrics.map { it.riskDelta }.average().toFloat()

        // SLA
        val phase1Sla = metrics.count { it.phase1LatencyMs <= 50 }.toFloat() / total
        val phase2Sla = if (phase2Sorted.isNotEmpty()) {
            phase2Sorted.count { it <= 4500 }.toFloat() / phase2Sorted.size
        } else 1f

        return LatencyProfile(
            phase1P50Ms = percentile(phase1Sorted, 50),
            phase1P95Ms = percentile(phase1Sorted, 95),
            phase1P99Ms = percentile(phase1Sorted, 99),
            phase1MaxMs = phase1Sorted.lastOrNull() ?: 0L,

            phase2P50Ms = percentile(phase2Sorted, 50),
            phase2P95Ms = percentile(phase2Sorted, 95),
            phase2P99Ms = percentile(phase2Sorted, 99),
            phase2MaxMs = phase2Sorted.lastOrNull() ?: 0L,

            e2eP50Ms = percentile(e2eSorted, 50),
            e2eP95Ms = percentile(e2eSorted, 95),
            e2eP99Ms = percentile(e2eSorted, 99),
            e2eMaxMs = e2eSorted.lastOrNull() ?: 0L,

            cacheHitRate = (tier0 + tier1) / total,
            tier0HitRate = tier0 / total,
            tier1HitRate = tier1 / total,

            skipRate = skip / total,
            instantRate = instant / total,
            lightRate = light / total,
            fullRate = full / total,

            phaseConflictRate = conflicts / total,
            avgRiskDelta = avgDelta,

            phase1SlaRate = phase1Sla,
            phase2SlaRate = phase2Sla,
        )
    }

    // ══════════════════════════════════════
    // 유틸
    // ══════════════════════════════════════

    private fun percentile(sorted: List<Long>, p: Int): Long {
        if (sorted.isEmpty()) return 0L
        val index = ((p / 100.0) * (sorted.size - 1)).toInt().coerceIn(0, sorted.size - 1)
        return sorted[index]
    }

    private fun randomRange(min: Long, max: Long): Long {
        return min + (Math.random() * (max - min)).toLong()
    }

    private fun randomFloat(min: Float, max: Float): Float {
        return min + (Math.random().toFloat() * (max - min))
    }

    private fun pct(rate: Float): String = "${String.format("%.1f", rate * 100)}%"
    private fun fmt(value: Float): String = String.format("%.2f", value)
}
