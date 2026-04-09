package app.myphonecheck.mobile.feature.callintercept

import android.content.Context
import android.content.SharedPreferences
import app.myphonecheck.mobile.core.model.SearchEngine
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 프로덕션 피드백 수집기.
 *
 * 빅테크 정석: Production Telemetry Feedback Loop
 * 출시 후 실사용 데이터를 수집하여 자동 보정에 활용.
 *
 * 수집 항목:
 *   1. 국가별 실패율 (검색 실패, 타임아웃, fallback)
 *   2. 검색 엔진별 응답 시간 + 실패율
 *   3. 2초 SLA 위반 건수
 *   4. 사용자 행동 (수신/거절/차단/무시)
 *   5. fallback 트리거 빈도
 *
 * 데이터 흐름:
 *   인터셉트 → 이벤트 기록 → 집계 → AutoPolicyAdjuster에 전달
 *
 * 100% 온디바이스. 서버 전송 없음.
 * SharedPreferences + 메모리 집계 기반.
 */
@Singleton
class ProductionFeedbackCollector @Inject constructor() {

    // ══════════════════════════════════════
    // 이벤트 모델
    // ══════════════════════════════════════

    /** 사용자 행동 */
    enum class UserAction(val label: String) {
        ACCEPT("수신"),
        REJECT("거절"),
        BLOCK("차단"),
        IGNORE("무시/부재중"),
    }

    /** 인터셉트 이벤트 */
    data class InterceptEvent(
        val timestampMs: Long,
        val countryCode: String,
        val phoneNumber: String,
        /** 사용된 1순위 검색 엔진 */
        val primaryEngine: SearchEngine,
        /** 실제 사용된 엔진 (fallback 포함) */
        val enginesUsed: List<SearchEngine>,
        /** 검색 소요 시간 (ms) */
        val searchLatencyMs: Long,
        /** 2초 SLA 통과 여부 */
        val slaPassed: Boolean,
        /** 검색 결과 수 */
        val resultCount: Int,
        /** fallback 발생 여부 */
        val fallbackTriggered: Boolean,
        /** 검색 실패 여부 (결과 0건) */
        val searchFailed: Boolean,
        /** 판정 결과 (SPAM, SAFE, UNKNOWN 등) */
        val verdict: String,
        /** 사용자 행동 (null = 아직 행동 안 함) */
        val userAction: UserAction? = null,
        /** 사용자가 판정에 동의했는지 (null = 미확인) */
        val userAgreedWithVerdict: Boolean? = null,
    )

    // ══════════════════════════════════════
    // 집계 데이터
    // ══════════════════════════════════════

    /** 국가별 집계 */
    data class CountryStats(
        val countryCode: String,
        val totalIntercepts: Int = 0,
        val searchFailures: Int = 0,
        val slaViolations: Int = 0,
        val fallbackCount: Int = 0,
        val totalLatencyMs: Long = 0,
        val userAccepts: Int = 0,
        val userRejects: Int = 0,
        val userBlocks: Int = 0,
        val userIgnores: Int = 0,
        val verdictDisagreements: Int = 0,
    ) {
        val searchFailureRate: Float
            get() = if (totalIntercepts > 0) searchFailures.toFloat() / totalIntercepts else 0f

        val slaViolationRate: Float
            get() = if (totalIntercepts > 0) slaViolations.toFloat() / totalIntercepts else 0f

        val fallbackRate: Float
            get() = if (totalIntercepts > 0) fallbackCount.toFloat() / totalIntercepts else 0f

        val avgLatencyMs: Long
            get() = if (totalIntercepts > 0) totalLatencyMs / totalIntercepts else 0L

        val disagreementRate: Float
            get() = if (totalIntercepts > 0) verdictDisagreements.toFloat() / totalIntercepts else 0f
    }

    /** 엔진별 집계 */
    data class EngineStats(
        val engine: SearchEngine,
        val totalRequests: Int = 0,
        val failures: Int = 0,
        val totalLatencyMs: Long = 0,
        val timeouts: Int = 0,
    ) {
        val failureRate: Float
            get() = if (totalRequests > 0) failures.toFloat() / totalRequests else 0f

        val avgLatencyMs: Long
            get() = if (totalRequests > 0) totalLatencyMs / totalRequests else 0L

        val timeoutRate: Float
            get() = if (totalRequests > 0) timeouts.toFloat() / totalRequests else 0f
    }

    // ══════════════════════════════════════
    // 메모리 저장소
    // ══════════════════════════════════════

    /** 국가별 통계 (메모리) */
    private val countryStatsMap = mutableMapOf<String, CountryStats>()

    /** 엔진별 통계 (메모리) */
    private val engineStatsMap = mutableMapOf<SearchEngine, EngineStats>()

    /** 최근 이벤트 버퍼 (최대 1000건, 순환) */
    private val recentEvents = ArrayDeque<InterceptEvent>(MAX_RECENT_EVENTS)

    /** 총 이벤트 수 */
    private var totalEventsCount = 0L

    // ══════════════════════════════════════
    // 이벤트 기록
    // ══════════════════════════════════════

    /**
     * 인터셉트 이벤트 기록.
     * 매 전화 인터셉트 완료 후 호출.
     */
    @Synchronized
    fun recordEvent(event: InterceptEvent) {
        totalEventsCount++

        // 최근 이벤트 버퍼
        if (recentEvents.size >= MAX_RECENT_EVENTS) {
            recentEvents.removeFirst()
        }
        recentEvents.addLast(event)

        // 국가별 집계
        val cs = countryStatsMap.getOrPut(event.countryCode) {
            CountryStats(countryCode = event.countryCode)
        }
        countryStatsMap[event.countryCode] = cs.copy(
            totalIntercepts = cs.totalIntercepts + 1,
            searchFailures = cs.searchFailures + if (event.searchFailed) 1 else 0,
            slaViolations = cs.slaViolations + if (!event.slaPassed) 1 else 0,
            fallbackCount = cs.fallbackCount + if (event.fallbackTriggered) 1 else 0,
            totalLatencyMs = cs.totalLatencyMs + event.searchLatencyMs,
            userAccepts = cs.userAccepts + if (event.userAction == UserAction.ACCEPT) 1 else 0,
            userRejects = cs.userRejects + if (event.userAction == UserAction.REJECT) 1 else 0,
            userBlocks = cs.userBlocks + if (event.userAction == UserAction.BLOCK) 1 else 0,
            userIgnores = cs.userIgnores + if (event.userAction == UserAction.IGNORE) 1 else 0,
            verdictDisagreements = cs.verdictDisagreements + if (event.userAgreedWithVerdict == false) 1 else 0,
        )

        // 엔진별 집계
        event.enginesUsed.forEach { engine ->
            val es = engineStatsMap.getOrPut(engine) { EngineStats(engine = engine) }
            engineStatsMap[engine] = es.copy(
                totalRequests = es.totalRequests + 1,
                failures = es.failures + if (event.searchFailed) 1 else 0,
                totalLatencyMs = es.totalLatencyMs + event.searchLatencyMs,
                timeouts = es.timeouts + if (!event.slaPassed) 1 else 0,
            )
        }
    }

    /**
     * 사용자 행동 기록 (인터셉트 후 별도 시점에 호출).
     */
    @Synchronized
    fun recordUserAction(
        countryCode: String,
        action: UserAction,
        agreedWithVerdict: Boolean,
    ) {
        val cs = countryStatsMap[countryCode] ?: return
        countryStatsMap[countryCode] = cs.copy(
            userAccepts = cs.userAccepts + if (action == UserAction.ACCEPT) 1 else 0,
            userRejects = cs.userRejects + if (action == UserAction.REJECT) 1 else 0,
            userBlocks = cs.userBlocks + if (action == UserAction.BLOCK) 1 else 0,
            userIgnores = cs.userIgnores + if (action == UserAction.IGNORE) 1 else 0,
            verdictDisagreements = cs.verdictDisagreements + if (!agreedWithVerdict) 1 else 0,
        )
    }

    // ══════════════════════════════════════
    // 조회
    // ══════════════════════════════════════

    /** 전체 이벤트 수 */
    fun totalEvents(): Long = totalEventsCount

    /** 국가별 통계 조회 */
    fun getCountryStats(countryCode: String): CountryStats? = countryStatsMap[countryCode]

    /** 전체 국가 통계 */
    fun getAllCountryStats(): List<CountryStats> = countryStatsMap.values.toList()

    /** 엔진별 통계 조회 */
    fun getEngineStats(engine: SearchEngine): EngineStats? = engineStatsMap[engine]

    /** 전체 엔진 통계 */
    fun getAllEngineStats(): List<EngineStats> = engineStatsMap.values.toList()

    /** 최근 이벤트 */
    fun getRecentEvents(limit: Int = 100): List<InterceptEvent> {
        return recentEvents.takeLast(limit)
    }

    // ══════════════════════════════════════
    // 문제 국가 감지
    // ══════════════════════════════════════

    /**
     * 문제 국가 감지.
     * SLA 위반율, 검색 실패율, 사용자 불일치율 기준.
     */
    fun detectProblematicCountries(): List<ProblematicCountry> {
        return countryStatsMap.values
            .filter { it.totalIntercepts >= MIN_SAMPLES_FOR_DETECTION }
            .mapNotNull { cs ->
                val issues = mutableListOf<String>()

                if (cs.slaViolationRate > SLA_VIOLATION_THRESHOLD) {
                    issues.add("SLA 위반율 ${String.format("%.1f", cs.slaViolationRate * 100)}%")
                }
                if (cs.searchFailureRate > SEARCH_FAILURE_THRESHOLD) {
                    issues.add("검색 실패율 ${String.format("%.1f", cs.searchFailureRate * 100)}%")
                }
                if (cs.fallbackRate > FALLBACK_RATE_THRESHOLD) {
                    issues.add("fallback율 ${String.format("%.1f", cs.fallbackRate * 100)}%")
                }
                if (cs.disagreementRate > DISAGREEMENT_THRESHOLD) {
                    issues.add("사용자 불일치율 ${String.format("%.1f", cs.disagreementRate * 100)}%")
                }

                if (issues.isNotEmpty()) {
                    ProblematicCountry(
                        countryCode = cs.countryCode,
                        stats = cs,
                        issues = issues,
                    )
                } else null
            }
            .sortedByDescending { it.issues.size }
    }

    data class ProblematicCountry(
        val countryCode: String,
        val stats: CountryStats,
        val issues: List<String>,
    )

    // ══════════════════════════════════════
    // 보고서
    // ══════════════════════════════════════

    /**
     * AutoPolicyAdjuster 입력용 피드백 요약 생성.
     */
    fun generateFeedbackSummary(): FeedbackSummary {
        val problematic = detectProblematicCountries()
        val topFailEngines = engineStatsMap.values
            .filter { it.totalRequests >= MIN_SAMPLES_FOR_DETECTION }
            .sortedByDescending { it.failureRate }
            .take(5)

        return FeedbackSummary(
            totalEvents = totalEventsCount,
            countryCount = countryStatsMap.size,
            problematicCountries = problematic,
            topFailingEngines = topFailEngines,
            overallSlaViolationRate = run {
                val totalIntercepts = countryStatsMap.values.sumOf { it.totalIntercepts }
                val totalViolations = countryStatsMap.values.sumOf { it.slaViolations }
                if (totalIntercepts > 0) totalViolations.toFloat() / totalIntercepts else 0f
            },
            overallSearchFailureRate = run {
                val totalIntercepts = countryStatsMap.values.sumOf { it.totalIntercepts }
                val totalFailures = countryStatsMap.values.sumOf { it.searchFailures }
                if (totalIntercepts > 0) totalFailures.toFloat() / totalIntercepts else 0f
            },
        )
    }

    data class FeedbackSummary(
        val totalEvents: Long,
        val countryCount: Int,
        val problematicCountries: List<ProblematicCountry>,
        val topFailingEngines: List<EngineStats>,
        val overallSlaViolationRate: Float,
        val overallSearchFailureRate: Float,
    ) {
        fun toJarvisFormat(): String = buildString {
            appendLine("═══ Production Feedback 요약 ═══")
            appendLine()
            appendLine("총 이벤트: $totalEvents")
            appendLine("국가 수: $countryCount")
            appendLine("전체 SLA 위반율: ${String.format("%.2f", overallSlaViolationRate * 100)}%")
            appendLine("전체 검색 실패율: ${String.format("%.2f", overallSearchFailureRate * 100)}%")
            appendLine()

            if (problematicCountries.isNotEmpty()) {
                appendLine("── 문제 국가 (${problematicCountries.size}개) ──")
                problematicCountries.forEach { pc ->
                    appendLine("  ⚠️ [${pc.countryCode}] ${pc.issues.joinToString(", ")}")
                }
                appendLine()
            }

            if (topFailingEngines.isNotEmpty()) {
                appendLine("── 실패 상위 엔진 ──")
                topFailingEngines.forEach { es ->
                    appendLine("  ${es.engine.displayName}: 실패 ${String.format("%.1f", es.failureRate * 100)}% | 평균 ${es.avgLatencyMs}ms | 타임아웃 ${String.format("%.1f", es.timeoutRate * 100)}%")
                }
            }
        }
    }

    // ══════════════════════════════════════
    // 영속화 (SharedPreferences 기반)
    // ══════════════════════════════════════

    /**
     * 집계 데이터를 SharedPreferences에 저장.
     * 앱 재시작 시에도 통계 유지.
     */
    fun persistStats(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()

        editor.putLong("total_events", totalEventsCount)

        countryStatsMap.forEach { (cc, stats) ->
            editor.putInt("cs_${cc}_total", stats.totalIntercepts)
            editor.putInt("cs_${cc}_fail", stats.searchFailures)
            editor.putInt("cs_${cc}_sla", stats.slaViolations)
            editor.putInt("cs_${cc}_fb", stats.fallbackCount)
            editor.putLong("cs_${cc}_lat", stats.totalLatencyMs)
            editor.putInt("cs_${cc}_accept", stats.userAccepts)
            editor.putInt("cs_${cc}_reject", stats.userRejects)
            editor.putInt("cs_${cc}_block", stats.userBlocks)
            editor.putInt("cs_${cc}_ignore", stats.userIgnores)
            editor.putInt("cs_${cc}_disagree", stats.verdictDisagreements)
        }

        editor.putStringSet("country_codes", countryStatsMap.keys)
        editor.apply()
    }

    /**
     * 집계 데이터를 SharedPreferences에서 복원.
     */
    fun restoreStats(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        totalEventsCount = prefs.getLong("total_events", 0L)

        val codes = prefs.getStringSet("country_codes", emptySet()) ?: emptySet()
        codes.forEach { cc ->
            countryStatsMap[cc] = CountryStats(
                countryCode = cc,
                totalIntercepts = prefs.getInt("cs_${cc}_total", 0),
                searchFailures = prefs.getInt("cs_${cc}_fail", 0),
                slaViolations = prefs.getInt("cs_${cc}_sla", 0),
                fallbackCount = prefs.getInt("cs_${cc}_fb", 0),
                totalLatencyMs = prefs.getLong("cs_${cc}_lat", 0L),
                userAccepts = prefs.getInt("cs_${cc}_accept", 0),
                userRejects = prefs.getInt("cs_${cc}_reject", 0),
                userBlocks = prefs.getInt("cs_${cc}_block", 0),
                userIgnores = prefs.getInt("cs_${cc}_ignore", 0),
                verdictDisagreements = prefs.getInt("cs_${cc}_disagree", 0),
            )
        }
    }

    /**
     * 통계 초기화 (테스트용).
     */
    @Synchronized
    fun clearAll() {
        countryStatsMap.clear()
        engineStatsMap.clear()
        recentEvents.clear()
        totalEventsCount = 0L
    }

    companion object {
        private const val PREFS_NAME = "myphonecheck_feedback"
        private const val MAX_RECENT_EVENTS = 1000

        /** 문제 감지 최소 샘플 수 */
        private const val MIN_SAMPLES_FOR_DETECTION = 10

        /** 임계값 */
        private const val SLA_VIOLATION_THRESHOLD = 0.05f    // 5% 이상 → 문제
        private const val SEARCH_FAILURE_THRESHOLD = 0.10f   // 10% 이상 → 문제
        private const val FALLBACK_RATE_THRESHOLD = 0.30f    // 30% 이상 → 문제
        private const val DISAGREEMENT_THRESHOLD = 0.15f     // 15% 이상 → 문제
    }
}
