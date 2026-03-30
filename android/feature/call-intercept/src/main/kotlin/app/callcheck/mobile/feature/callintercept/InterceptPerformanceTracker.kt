package app.callcheck.mobile.feature.callintercept

import android.util.Log
import app.callcheck.mobile.core.model.CacheHitLevel
import app.callcheck.mobile.core.model.InterceptMetrics
import app.callcheck.mobile.core.model.InterceptPerformanceStats
import app.callcheck.mobile.core.model.InterceptRoute
import app.callcheck.mobile.core.model.PhaseSource
import app.callcheck.mobile.core.model.TwoPhaseDecision
import java.util.UUID
import java.util.concurrent.ConcurrentLinkedDeque
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "InterceptPerformance"
private const val MAX_METRICS_BUFFER = 200

/**
 * 인터셉트 성능 계측 엔진.
 *
 * 자비스 요구: "이제 '좋아 보인다'가 아니라 '몇 ms냐'로 가야 합니다."
 *
 * 측정 항목 (전수 기록):
 * - Phase 1 표시까지 ms
 * - Phase 2 확정까지 ms
 * - 캐시 hit 비율 (Tier 0/1/Miss)
 * - Route 분포 (SKIP/INSTANT/LIGHT/FULL)
 * - Phase 불일치 비율
 * - 국가별 risk boost 적용량
 *
 * 계측 데이터 생명주기:
 * - 인메모리 링 버퍼 (최근 200건)
 * - 앱 재시작 시 초기화 (영속 불필요 — 통계만 의미)
 * - 누적 통계는 실시간 계산
 *
 * 성능: < 0.1ms per record (ConcurrentLinkedDeque, 잠금 없음)
 * 100% 온디바이스. 서버 전송 없음.
 */
@Singleton
class InterceptPerformanceTracker @Inject constructor() {

    /** 최근 인터셉트 계측 데이터 (링 버퍼, 최대 200건) */
    private val metricsBuffer = ConcurrentLinkedDeque<InterceptMetrics>()

    /** 누적 카운터 (앱 생명주기 동안) */
    @Volatile private var totalCount = 0
    @Volatile private var tier0HitCount = 0
    @Volatile private var tier1HitCount = 0
    @Volatile private var missCount = 0
    @Volatile private var skipCount = 0
    @Volatile private var instantCount = 0
    @Volatile private var lightCount = 0
    @Volatile private var fullCount = 0
    @Volatile private var conflictCount = 0

    /**
     * 인터셉트 완료 후 계측 데이터 기록.
     *
     * CallInterceptRepositoryImpl에서 TwoPhaseDecision 반환 직후 호출.
     */
    fun record(
        decision: TwoPhaseDecision,
        numberHash: String,
        countryRiskBoost: Float = 0f,
        networkAvailable: Boolean = true,
    ) {
        val meta = decision.phaseMeta
        val route = meta.route ?: InterceptRoute.FULL

        val cacheHitLevel = when (decision.phase1.source) {
            PhaseSource.PRE_JUDGE_CACHE -> CacheHitLevel.TIER_0
            PhaseSource.MEMORY_CACHE -> CacheHitLevel.TIER_1
            else -> CacheHitLevel.MISS
        }

        val p2 = decision.phase2
        val riskDelta = if (p2 != null) {
            p2.riskScore - decision.phase1.riskScore
        } else 0f

        val metrics = InterceptMetrics(
            interceptId = UUID.randomUUID().toString().take(8),
            numberHash = numberHash,
            route = route,
            phase1LatencyMs = meta.phase1LatencyMs,
            phase2LatencyMs = meta.phase2LatencyMs,
            cacheHitLevel = cacheHitLevel,
            phaseSource = decision.phase1.source,
            countryRiskBoost = countryRiskBoost,
            phaseConflict = decision.hasPhaseConflict(),
            riskDelta = riskDelta,
            networkAvailable = networkAvailable,
        )

        // 링 버퍼에 추가
        metricsBuffer.addLast(metrics)
        while (metricsBuffer.size > MAX_METRICS_BUFFER) {
            metricsBuffer.pollFirst()
        }

        // 누적 카운터 업데이트
        totalCount++
        when (cacheHitLevel) {
            CacheHitLevel.TIER_0 -> tier0HitCount++
            CacheHitLevel.TIER_1 -> tier1HitCount++
            CacheHitLevel.MISS -> missCount++
        }
        when (route) {
            InterceptRoute.SKIP -> skipCount++
            InterceptRoute.INSTANT -> instantCount++
            InterceptRoute.LIGHT -> lightCount++
            InterceptRoute.FULL -> fullCount++
        }
        if (decision.hasPhaseConflict()) conflictCount++

        // 실시간 로그
        Log.i(TAG, buildString {
            append("[${metrics.interceptId}] ")
            append("route=${route.name} ")
            append("cache=${cacheHitLevel.name} ")
            append("p1=${meta.phase1LatencyMs}ms ")
            if (meta.phase2LatencyMs >= 0) append("p2=${meta.phase2LatencyMs}ms ")
            if (decision.hasPhaseConflict()) append("CONFLICT ")
            if (countryRiskBoost > 0) append("countryBoost=+${String.format("%.2f", countryRiskBoost)} ")
            append("net=${if (networkAvailable) "ON" else "OFF"}")
        })
    }

    /**
     * 현재까지의 누적 성능 통계 계산.
     */
    fun getStats(): InterceptPerformanceStats {
        val snapshot = metricsBuffer.toList()
        if (snapshot.isEmpty()) return InterceptPerformanceStats()

        val phase1Latencies = snapshot.map { it.phase1LatencyMs }.sorted()
        val phase2Latencies = snapshot.filter { it.phase2LatencyMs >= 0 }.map { it.phase2LatencyMs }.sorted()

        return InterceptPerformanceStats(
            totalInterceptCount = totalCount,
            routeDistribution = mapOf(
                InterceptRoute.SKIP to skipCount,
                InterceptRoute.INSTANT to instantCount,
                InterceptRoute.LIGHT to lightCount,
                InterceptRoute.FULL to fullCount,
            ),
            cacheHitRate = if (totalCount > 0) (tier0HitCount + tier1HitCount).toFloat() / totalCount else 0f,
            tier0HitRate = if (totalCount > 0) tier0HitCount.toFloat() / totalCount else 0f,
            tier1HitRate = if (totalCount > 0) tier1HitCount.toFloat() / totalCount else 0f,
            avgPhase1LatencyMs = if (phase1Latencies.isNotEmpty()) phase1Latencies.average().toLong() else 0L,
            p95Phase1LatencyMs = percentile(phase1Latencies, 95),
            maxPhase1LatencyMs = phase1Latencies.maxOrNull() ?: 0L,
            avgPhase2LatencyMs = if (phase2Latencies.isNotEmpty()) phase2Latencies.average().toLong() else 0L,
            p95Phase2LatencyMs = percentile(phase2Latencies, 95),
            fullRouteRate = if (totalCount > 0) fullCount.toFloat() / totalCount else 0f,
            phaseConflictRate = if (totalCount > 0) conflictCount.toFloat() / totalCount else 0f,
            periodStartMs = snapshot.firstOrNull()?.timestampMs ?: 0L,
            periodEndMs = snapshot.lastOrNull()?.timestampMs ?: 0L,
        )
    }

    /**
     * 성능 통계를 사람이 읽을 수 있는 문자열로 포맷.
     * Settings 화면 또는 디버그 패널용.
     */
    fun getStatsReport(): String {
        val s = getStats()
        if (s.totalInterceptCount == 0) return "계측 데이터 없음"

        return buildString {
            appendLine("═══ CallCheck 인터셉트 성능 ═══")
            appendLine()
            appendLine("총 인터셉트: ${s.totalInterceptCount}건")
            appendLine()
            appendLine("── Phase 1 (즉시 판단) ──")
            appendLine("  평균: ${s.avgPhase1LatencyMs}ms")
            appendLine("  P95:  ${s.p95Phase1LatencyMs}ms")
            appendLine("  최대: ${s.maxPhase1LatencyMs}ms")
            appendLine()
            appendLine("── Phase 2 (확정 판단) ──")
            appendLine("  평균: ${s.avgPhase2LatencyMs}ms")
            appendLine("  P95:  ${s.p95Phase2LatencyMs}ms")
            appendLine()
            appendLine("── 캐시 히트율 ──")
            appendLine("  전체: ${pct(s.cacheHitRate)}")
            appendLine("  Tier 0 (Room): ${pct(s.tier0HitRate)}")
            appendLine("  Tier 1 (메모리): ${pct(s.tier1HitRate)}")
            appendLine()
            appendLine("── Route 분포 ──")
            s.routeDistribution.forEach { (route, count) ->
                appendLine("  ${route.name}: ${count}건 (${pct(count.toFloat() / s.totalInterceptCount)})")
            }
            appendLine()
            appendLine("── 품질 지표 ──")
            appendLine("  FULL 진입율: ${pct(s.fullRouteRate)}")
            appendLine("  Phase 불일치율: ${pct(s.phaseConflictRate)}")
        }
    }

    /** 최근 N건의 원시 메트릭 반환 (디버그용) */
    fun getRecentMetrics(count: Int = 20): List<InterceptMetrics> {
        return metricsBuffer.toList().takeLast(count)
    }

    /** 계측 데이터 초기화 */
    fun reset() {
        metricsBuffer.clear()
        totalCount = 0; tier0HitCount = 0; tier1HitCount = 0; missCount = 0
        skipCount = 0; instantCount = 0; lightCount = 0; fullCount = 0
        conflictCount = 0
        Log.i(TAG, "Metrics reset")
    }

    // ── 유틸 ──

    private fun percentile(sorted: List<Long>, p: Int): Long {
        if (sorted.isEmpty()) return 0L
        val index = ((p / 100.0) * (sorted.size - 1)).toInt().coerceIn(0, sorted.size - 1)
        return sorted[index]
    }

    private fun pct(rate: Float): String = "${String.format("%.1f", rate * 100)}%"
}
