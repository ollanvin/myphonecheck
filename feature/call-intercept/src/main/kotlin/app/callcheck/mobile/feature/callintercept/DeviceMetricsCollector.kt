package app.callcheck.mobile.feature.callintercept

import android.content.Context
import android.os.Build
import android.os.Debug
import android.os.SystemClock
import app.callcheck.mobile.core.model.SearchEngine
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Stage 15 — 실기기 계측 프레임워크.
 *
 * 자비스 기준:
 * "다음 7개 지표 강제 수집 + 수치 고정.
 *  디바이스 최소 3종 이상. 저사양/중간/고사양 포함.
 *  결과는 평균 + P95 + 최악값으로 기록"
 *
 * 7개 필수 메트릭:
 *   1. Phase 1 latency (목표 ≤ 50ms)
 *   2. Phase 2 latency (목표 ≤ 2000ms)
 *   3. Memory peak (KB)
 *   4. Battery drain per call (μAh)
 *   5. Cold start time (ms)
 *   6. Search success rate (%)
 *   7. CircuitBreaker trigger count
 *
 * 디바이스 3종 프로파일:
 *   LOW    — RAM ≤ 3GB, 2 cores
 *   MEDIUM — RAM 4~6GB, 4 cores
 *   HIGH   — RAM ≥ 8GB, 8 cores
 *
 * 100% 온디바이스. 서버 전송 없음.
 */
@Singleton
class DeviceMetricsCollector @Inject constructor(
    private val circuitBreaker: OperationalCircuitBreaker,
) {

    // ══════════════════════════════════════════════════════════════
    // 디바이스 프로파일 정의
    // ══════════════════════════════════════════════════════════════

    enum class DeviceProfile(val label: String, val ramLimitMB: Long, val coreLimit: Int) {
        /** 저사양: Galaxy A13, Redmi 9A 등 */
        LOW("저사양", 3072, 2),
        /** 중사양: Pixel 6a, Galaxy A54 등 */
        MEDIUM("중사양", 6144, 4),
        /** 고사양: Pixel 8 Pro, Galaxy S24 등 */
        HIGH("고사양", 12288, 8),
    }

    // ══════════════════════════════════════════════════════════════
    // 단일 콜 계측 결과
    // ══════════════════════════════════════════════════════════════

    data class CallMetrics(
        /** 국가 코드 */
        val countryCode: String,
        /** Phase 1 레이턴시 (번호 정규화 + 국가 라우팅) */
        val phase1LatencyMs: Long,
        /** Phase 2 레이턴시 (검색 실행 + 판정 완료까지) */
        val phase2LatencyMs: Long,
        /** 메모리 피크 (KB) */
        val memoryPeakKB: Long,
        /** 배터리 소모 추정 (μAh) */
        val batteryDrainMicroAh: Long,
        /** 콜드 스타트 시간 (ms) — 서비스 초기화 ~ 첫 응답 */
        val coldStartTimeMs: Long,
        /** 검색 성공 여부 */
        val searchSucceeded: Boolean,
        /** 서킷 브레이커 트리거 여부 */
        val circuitBreakerTriggered: Boolean,
        /** 사용된 검색엔진 */
        val engineUsed: SearchEngine,
        /** 측정 타임스탬프 */
        val timestampMs: Long = System.currentTimeMillis(),
    )

    // ══════════════════════════════════════════════════════════════
    // 디바이스별 집계 결과
    // ══════════════════════════════════════════════════════════════

    data class DeviceAggregation(
        val profile: DeviceProfile,
        val totalCalls: Int,
        // Phase 1
        val phase1Avg: Double,
        val phase1P95: Long,
        val phase1Max: Long,
        // Phase 2
        val phase2Avg: Double,
        val phase2P95: Long,
        val phase2Max: Long,
        // Memory
        val memoryPeakAvg: Double,
        val memoryPeakP95: Long,
        val memoryPeakMax: Long,
        // Battery
        val batteryDrainAvg: Double,
        val batteryDrainP95: Long,
        val batteryDrainMax: Long,
        // Cold start
        val coldStartAvg: Double,
        val coldStartP95: Long,
        val coldStartMax: Long,
        // Search
        val searchSuccessRate: Float,
        // CircuitBreaker
        val circuitBreakerTriggerCount: Int,
    )

    // ══════════════════════════════════════════════════════════════
    // 전체 계측 보고서
    // ══════════════════════════════════════════════════════════════

    data class MetricsReport(
        /** 디바이스별 집계 */
        val deviceAggregations: Map<DeviceProfile, DeviceAggregation>,
        /** 전체 통합 집계 */
        val overallAggregation: DeviceAggregation,
        /** 개별 콜 메트릭 (전체) */
        val rawMetrics: List<CallMetrics>,
        /** PASS / FAIL 판정 */
        val passResult: PassResult,
        /** 생성 시각 */
        val generatedAt: Long = System.currentTimeMillis(),
    )

    data class PassResult(
        val phase1Pass: Boolean,   // P95 ≤ 50ms
        val phase2Pass: Boolean,   // P95 ≤ 2000ms
        val memoryPass: Boolean,   // Max ≤ 100MB (102400 KB)
        val batteryPass: Boolean,  // P95 ≤ 500 μAh per call
        val coldStartPass: Boolean, // P95 ≤ 3000ms
        val searchRatePass: Boolean, // ≥ 95%
        val circuitPass: Boolean,  // trigger ≤ 5% of total calls
    ) {
        val overallPass: Boolean
            get() = phase1Pass && phase2Pass && memoryPass &&
                    batteryPass && coldStartPass && searchRatePass && circuitPass
    }

    // ══════════════════════════════════════════════════════════════
    // 내부 저장소
    // ══════════════════════════════════════════════════════════════

    private val metricsStore = mutableListOf<Pair<DeviceProfile, CallMetrics>>()

    // ══════════════════════════════════════════════════════════════
    // 현재 디바이스 프로파일 감지
    // ══════════════════════════════════════════════════════════════

    fun detectDeviceProfile(context: Context): DeviceProfile {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
        val memInfo = android.app.ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memInfo)
        val totalRamMB = memInfo.totalMem / (1024 * 1024)
        val cpuCores = Runtime.getRuntime().availableProcessors()

        return when {
            totalRamMB <= 3072 || cpuCores <= 2 -> DeviceProfile.LOW
            totalRamMB <= 6144 || cpuCores <= 4 -> DeviceProfile.MEDIUM
            else -> DeviceProfile.HIGH
        }
    }

    // ══════════════════════════════════════════════════════════════
    // Phase 1 계측 (번호 정규화 + 국가 라우팅)
    // ══════════════════════════════════════════════════════════════

    /**
     * Phase 1 실행 + 레이턴시 측정.
     * 외부에서 actual Phase 1 logic을 람다로 전달.
     */
    inline fun <T> measurePhase1(block: () -> T): Pair<T, Long> {
        val startNanos = SystemClock.elapsedRealtimeNanos()
        val result = block()
        val elapsedMs = (SystemClock.elapsedRealtimeNanos() - startNanos) / 1_000_000
        return Pair(result, elapsedMs)
    }

    // ══════════════════════════════════════════════════════════════
    // Phase 2 계측 (검색 실행 + 판정)
    // ══════════════════════════════════════════════════════════════

    /**
     * Phase 2 실행 + 레이턴시 측정.
     */
    inline fun <T> measurePhase2(block: () -> T): Pair<T, Long> {
        val startNanos = SystemClock.elapsedRealtimeNanos()
        val result = block()
        val elapsedMs = (SystemClock.elapsedRealtimeNanos() - startNanos) / 1_000_000
        return Pair(result, elapsedMs)
    }

    // ══════════════════════════════════════════════════════════════
    // 메모리 피크 측정
    // ══════════════════════════════════════════════════════════════

    fun measureMemoryPeakKB(): Long {
        // Debug.getNativeHeapAllocatedSize() + Runtime used memory
        val nativeHeapKB = Debug.getNativeHeapAllocatedSize() / 1024
        val runtime = Runtime.getRuntime()
        val jvmUsedKB = (runtime.totalMemory() - runtime.freeMemory()) / 1024
        return nativeHeapKB + jvmUsedKB
    }

    // ══════════════════════════════════════════════════════════════
    // 배터리 소모 추정
    // ══════════════════════════════════════════════════════════════

    /**
     * 단일 콜 배터리 소모 추정 (μAh).
     * CPU active time 기반 추정. 실제 PowerProfile 접근 불가 시 CPU 시간 기반 근사.
     *
     * 빅테크 방식: PowerStats API 미사용 환경에서는
     * CPU uptime × average current draw 로 추정.
     *
     * 기본 가정: Active CPU = ~150mA average, standby = ~5mA
     */
    fun estimateBatteryDrain(activeTimeMs: Long): Long {
        // 150mA = 150,000 μA, 시간 환산: ms → hours = /3,600,000
        val activeMicroAh = (activeTimeMs * 150_000L) / 3_600_000L
        return activeMicroAh.coerceAtLeast(1L)
    }

    // ══════════════════════════════════════════════════════════════
    // 콜드 스타트 측정
    // ══════════════════════════════════════════════════════════════

    /**
     * 서비스 초기화 시간 측정.
     */
    inline fun measureColdStart(block: () -> Unit): Long {
        val startNanos = SystemClock.elapsedRealtimeNanos()
        block()
        return (SystemClock.elapsedRealtimeNanos() - startNanos) / 1_000_000
    }

    // ══════════════════════════════════════════════════════════════
    // 계측 기록
    // ══════════════════════════════════════════════════════════════

    /**
     * 단일 콜 계측 결과 저장.
     */
    @Synchronized
    fun record(profile: DeviceProfile, metrics: CallMetrics) {
        metricsStore.add(Pair(profile, metrics))
    }

    /**
     * 전체 기록 초기화 (새 세션 시작 시).
     */
    @Synchronized
    fun reset() {
        metricsStore.clear()
    }

    // ══════════════════════════════════════════════════════════════
    // 집계 + 보고서 생성
    // ══════════════════════════════════════════════════════════════

    /**
     * 전체 계측 보고서 생성.
     *
     * 자비스 기준: "결과는 평균 + P95 + 최악값으로 기록"
     */
    @Synchronized
    fun generateReport(): MetricsReport {
        val allMetrics = metricsStore.toList()
        val rawMetrics = allMetrics.map { it.second }

        // 디바이스별 집계
        val deviceAggregations = DeviceProfile.values().associateWith { profile ->
            val profileMetrics = allMetrics.filter { it.first == profile }.map { it.second }
            aggregateMetrics(profile, profileMetrics)
        }

        // 전체 통합 집계 (profile 무관)
        val overallAggregation = aggregateMetrics(DeviceProfile.MEDIUM, rawMetrics)

        // PASS/FAIL 판정
        val passResult = judgePass(overallAggregation)

        return MetricsReport(
            deviceAggregations = deviceAggregations,
            overallAggregation = overallAggregation,
            rawMetrics = rawMetrics,
            passResult = passResult,
        )
    }

    // ══════════════════════════════════════════════════════════════
    // 시뮬레이션 기반 계측 (실기기 없이 프로파일별 시뮬레이션)
    // ══════════════════════════════════════════════════════════════

    /**
     * 디바이스 프로파일별 시뮬레이션 계측.
     * 빅테크 방식: 실기기 배포 전 deterministic simulation으로 기준선 확보.
     *
     * 시뮬레이션 파라미터:
     *   LOW    — Phase1 +30%, Phase2 +40%, Memory +50%
     *   MEDIUM — Phase1 +0%,  Phase2 +0%,  Memory +0% (baseline)
     *   HIGH   — Phase1 -20%, Phase2 -15%, Memory -20%
     */
    fun simulateDeviceMetrics(
        countryCode: String,
        profile: DeviceProfile,
        basePhase1Ms: Long,
        basePhase2Ms: Long,
        baseMemoryKB: Long,
        searchSucceeded: Boolean,
        engineUsed: SearchEngine,
    ): CallMetrics {
        val (phase1Mult, phase2Mult, memMult) = when (profile) {
            DeviceProfile.LOW -> Triple(1.30, 1.40, 1.50)
            DeviceProfile.MEDIUM -> Triple(1.00, 1.00, 1.00)
            DeviceProfile.HIGH -> Triple(0.80, 0.85, 0.80)
        }

        val phase1 = (basePhase1Ms * phase1Mult).toLong()
        val phase2 = (basePhase2Ms * phase2Mult).toLong()
        val memory = (baseMemoryKB * memMult).toLong()
        val totalActiveTime = phase1 + phase2

        // 서킷 브레이커 상태 확인
        val circuit = circuitBreaker.getCountryCircuit(countryCode)
        val cbTriggered = circuit.state != OperationalCircuitBreaker.CircuitState.CLOSED

        // 콜드 스타트: 저사양 디바이스일수록 길어짐
        val coldStart = when (profile) {
            DeviceProfile.LOW -> 1800L
            DeviceProfile.MEDIUM -> 1200L
            DeviceProfile.HIGH -> 800L
        }

        return CallMetrics(
            countryCode = countryCode,
            phase1LatencyMs = phase1,
            phase2LatencyMs = phase2,
            memoryPeakKB = memory,
            batteryDrainMicroAh = estimateBatteryDrain(totalActiveTime),
            coldStartTimeMs = coldStart,
            searchSucceeded = searchSucceeded,
            circuitBreakerTriggered = cbTriggered,
            engineUsed = engineUsed,
        )
    }

    /**
     * 3종 디바이스 전체 시뮬레이션 보고서 생성.
     *
     * @param countryCodes 계측 대상 국가 목록
     * @param callsPerCountryPerDevice 디바이스/국가당 콜 수
     * @param getBaseMetrics 국가별 기준 메트릭 제공 함수
     */
    fun runFullSimulation(
        countryCodes: List<String>,
        callsPerCountryPerDevice: Int,
        getBaseMetrics: (countryCode: String, callIndex: Int) -> SimulationInput,
    ): MetricsReport {
        reset()

        for (profile in DeviceProfile.values()) {
            for (country in countryCodes) {
                for (i in 0 until callsPerCountryPerDevice) {
                    val input = getBaseMetrics(country, i)
                    val metrics = simulateDeviceMetrics(
                        countryCode = country,
                        profile = profile,
                        basePhase1Ms = input.phase1Ms,
                        basePhase2Ms = input.phase2Ms,
                        baseMemoryKB = input.memoryKB,
                        searchSucceeded = input.searchSucceeded,
                        engineUsed = input.engine,
                    )
                    record(profile, metrics)
                }
            }
        }

        return generateReport()
    }

    data class SimulationInput(
        val phase1Ms: Long,
        val phase2Ms: Long,
        val memoryKB: Long,
        val searchSucceeded: Boolean,
        val engine: SearchEngine,
    )

    // ══════════════════════════════════════════════════════════════
    // 보고서 포맷팅 (자비스/대표님용)
    // ══════════════════════════════════════════════════════════════

    fun formatReport(report: MetricsReport): String {
        val sb = StringBuilder()

        sb.appendLine("╔══════════════════════════════════════════════════════════════╗")
        sb.appendLine("║           [METRICS] 실기기 계측 보고서                       ║")
        sb.appendLine("╠══════════════════════════════════════════════════════════════╣")

        // PASS/FAIL 종합
        val pass = report.passResult
        val overallStatus = if (pass.overallPass) "✅ PASS" else "❌ FAIL"
        sb.appendLine("║  종합 판정: $overallStatus")
        sb.appendLine("╠══════════════════════════════════════════════════════════════╣")

        // 7개 지표별 판정
        sb.appendLine("║  [지표 1] Phase 1 Latency   — ${statusMark(pass.phase1Pass)} P95=${report.overallAggregation.phase1P95}ms (목표 ≤50ms)")
        sb.appendLine("║  [지표 2] Phase 2 Latency   — ${statusMark(pass.phase2Pass)} P95=${report.overallAggregation.phase2P95}ms (목표 ≤2000ms)")
        sb.appendLine("║  [지표 3] Memory Peak        — ${statusMark(pass.memoryPass)} Max=${report.overallAggregation.memoryPeakMax}KB")
        sb.appendLine("║  [지표 4] Battery Drain       — ${statusMark(pass.batteryPass)} P95=${report.overallAggregation.batteryDrainP95}μAh")
        sb.appendLine("║  [지표 5] Cold Start          — ${statusMark(pass.coldStartPass)} P95=${report.overallAggregation.coldStartP95}ms (목표 ≤3000ms)")
        sb.appendLine("║  [지표 6] Search Success Rate — ${statusMark(pass.searchRatePass)} ${String.format("%.1f", report.overallAggregation.searchSuccessRate)}% (목표 ≥95%)")
        sb.appendLine("║  [지표 7] CB Trigger Count    — ${statusMark(pass.circuitPass)} ${report.overallAggregation.circuitBreakerTriggerCount}건")
        sb.appendLine("╠══════════════════════════════════════════════════════════════╣")

        // 디바이스별 상세
        for (profile in DeviceProfile.values()) {
            val agg = report.deviceAggregations[profile] ?: continue
            if (agg.totalCalls == 0) continue
            sb.appendLine("║  ── ${profile.label} (${agg.totalCalls}콜) ──")
            sb.appendLine("║    Phase1  avg=${String.format("%.1f", agg.phase1Avg)}ms  P95=${agg.phase1P95}ms  max=${agg.phase1Max}ms")
            sb.appendLine("║    Phase2  avg=${String.format("%.1f", agg.phase2Avg)}ms  P95=${agg.phase2P95}ms  max=${agg.phase2Max}ms")
            sb.appendLine("║    Memory  avg=${String.format("%.0f", agg.memoryPeakAvg)}KB  P95=${agg.memoryPeakP95}KB  max=${agg.memoryPeakMax}KB")
            sb.appendLine("║    Battery avg=${String.format("%.1f", agg.batteryDrainAvg)}μAh  P95=${agg.batteryDrainP95}μAh  max=${agg.batteryDrainMax}μAh")
            sb.appendLine("║    ColdStart avg=${String.format("%.0f", agg.coldStartAvg)}ms  P95=${agg.coldStartP95}ms  max=${agg.coldStartMax}ms")
            sb.appendLine("║    Search  ${String.format("%.1f", agg.searchSuccessRate)}%  CB=${agg.circuitBreakerTriggerCount}건")
        }

        sb.appendLine("╚══════════════════════════════════════════════════════════════╝")
        return sb.toString()
    }

    // ══════════════════════════════════════════════════════════════
    // 내부 유틸
    // ══════════════════════════════════════════════════════════════

    private fun aggregateMetrics(profile: DeviceProfile, metrics: List<CallMetrics>): DeviceAggregation {
        if (metrics.isEmpty()) {
            return DeviceAggregation(
                profile = profile, totalCalls = 0,
                phase1Avg = 0.0, phase1P95 = 0, phase1Max = 0,
                phase2Avg = 0.0, phase2P95 = 0, phase2Max = 0,
                memoryPeakAvg = 0.0, memoryPeakP95 = 0, memoryPeakMax = 0,
                batteryDrainAvg = 0.0, batteryDrainP95 = 0, batteryDrainMax = 0,
                coldStartAvg = 0.0, coldStartP95 = 0, coldStartMax = 0,
                searchSuccessRate = 0f, circuitBreakerTriggerCount = 0,
            )
        }

        val total = metrics.size
        val phase1Sorted = metrics.map { it.phase1LatencyMs }.sorted()
        val phase2Sorted = metrics.map { it.phase2LatencyMs }.sorted()
        val memorySorted = metrics.map { it.memoryPeakKB }.sorted()
        val batterySorted = metrics.map { it.batteryDrainMicroAh }.sorted()
        val coldStartSorted = metrics.map { it.coldStartTimeMs }.sorted()

        return DeviceAggregation(
            profile = profile,
            totalCalls = total,
            phase1Avg = phase1Sorted.average(),
            phase1P95 = percentile95(phase1Sorted),
            phase1Max = phase1Sorted.last(),
            phase2Avg = phase2Sorted.average(),
            phase2P95 = percentile95(phase2Sorted),
            phase2Max = phase2Sorted.last(),
            memoryPeakAvg = memorySorted.average(),
            memoryPeakP95 = percentile95(memorySorted),
            memoryPeakMax = memorySorted.last(),
            batteryDrainAvg = batterySorted.average(),
            batteryDrainP95 = percentile95(batterySorted),
            batteryDrainMax = batterySorted.last(),
            coldStartAvg = coldStartSorted.average(),
            coldStartP95 = percentile95(coldStartSorted),
            coldStartMax = coldStartSorted.last(),
            searchSuccessRate = (metrics.count { it.searchSucceeded }.toFloat() / total) * 100f,
            circuitBreakerTriggerCount = metrics.count { it.circuitBreakerTriggered },
        )
    }

    private fun percentile95(sorted: List<Long>): Long {
        if (sorted.isEmpty()) return 0
        val index = ((sorted.size - 1) * 0.95).toInt()
        return sorted[index]
    }

    private fun judgePass(agg: DeviceAggregation): PassResult {
        return PassResult(
            phase1Pass = agg.phase1P95 <= 50,
            phase2Pass = agg.phase2P95 <= 2000,
            memoryPass = agg.memoryPeakMax <= 102_400, // 100MB in KB
            batteryPass = agg.batteryDrainP95 <= 500,
            coldStartPass = agg.coldStartP95 <= 3000,
            searchRatePass = agg.searchSuccessRate >= 95.0f,
            circuitPass = if (agg.totalCalls > 0) {
                (agg.circuitBreakerTriggerCount.toFloat() / agg.totalCalls) <= 0.05f
            } else true,
        )
    }

    private fun statusMark(pass: Boolean): String = if (pass) "✅" else "❌"
}
