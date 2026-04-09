package app.myphonecheck.mobile.feature.callintercept

import app.myphonecheck.mobile.core.model.SearchTier
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 글로벌 출시 대시보드.
 *
 * 자비스 기준:
 * "대표님이 봐야 할 것은 코드가 아니라:
 *  국가별 PASS 수, SLA 통과율, 검색 실패율, Tier별 상태, 위험 국가 목록.
 *  이걸 한 화면에서 보게 해야 합니다."
 *
 * 이 클래스는 모든 검증/모니터링 모듈의 결과를 수집하여
 * 대표님에게 보여줄 단일 대시보드 데이터를 생성.
 *
 * 100% 온디바이스. 서버 전송 없음.
 */
@Singleton
class GlobalLaunchDashboard @Inject constructor(
    private val registry: GlobalSearchProviderRegistry,
    private val complianceValidator: CountryComplianceValidator,
    private val launchLock: LaunchReadinessLock,
    private val circuitBreaker: OperationalCircuitBreaker,
    private val feedbackCollector: ProductionFeedbackCollector,
) {

    /**
     * 대시보드 전체 데이터.
     */
    data class DashboardData(
        /** 최종 출시 준비 상태 */
        val launchReady: Boolean,

        // ── 국가 현황 ──
        val totalRegisteredCountries: Int,
        val tierBreakdown: Map<String, Int>,
        val compliancePassCount: Int,
        val complianceFailCount: Int,

        // ── SLA 현황 ──
        val globalSlaDeadlineMs: Long,
        val slaComplianceRate: Float,

        // ── 검색 현황 ──
        val overallSearchFailureRate: Float,

        // ── 서킷 브레이커 현황 ──
        val circuitClosedCount: Int,
        val circuitOpenCount: Int,
        val circuitEmergencyCount: Int,
        val openCountries: List<String>,
        val emergencyCountries: List<String>,

        // ── 위험 국가 ──
        val problematicCountries: List<ProblematicCountryInfo>,

        // ── 잠금 상태 ──
        val hardRulesLocked: Boolean,
        val slaLocked: Boolean,
        val safeExpressionsLocked: Boolean,

        // ── 피드백 루프 ──
        val totalFeedbackEvents: Long,
        val feedbackCountryCount: Int,
    )

    data class ProblematicCountryInfo(
        val countryCode: String,
        val tier: String,
        val issues: List<String>,
    )

    /**
     * 대시보드 데이터 생성.
     * 모든 모듈에서 최신 데이터를 수집.
     */
    fun generateDashboard(): DashboardData {
        // 컴플라이언스
        val compliance = complianceValidator.validateAll()

        // 잠금 검증
        val locks = launchLock.verifyAllLocks()

        // 서킷 브레이커
        val circuitReport = circuitBreaker.getCircuitReport()

        // 피드백
        val feedbackSummary = feedbackCollector.generateFeedbackSummary()

        // 티어 분류
        val tierCounts = registry.tierCounts()
        val tierBreakdown = mutableMapOf<String, Int>()
        tierCounts.forEach { (tier, count) -> tierBreakdown[tier.name] = count }

        // 위험 국가 수집
        val problematic = mutableListOf<ProblematicCountryInfo>()

        // 컴플라이언스 FAIL 국가
        compliance.results.filter { !it.passed }.forEach { r ->
            problematic.add(
                ProblematicCountryInfo(
                    countryCode = r.countryCode,
                    tier = r.tier.name,
                    issues = r.failReasons,
                )
            )
        }

        // 서킷 OPEN/EMERGENCY 국가
        circuitReport.openCountries.forEach { cc ->
            val existing = problematic.find { it.countryCode == cc }
            if (existing != null) {
                val updated = existing.copy(issues = existing.issues + "서킷 OPEN (1순위 차단)")
                problematic.remove(existing)
                problematic.add(updated)
            } else {
                val config = registry.getConfig(cc)
                problematic.add(
                    ProblematicCountryInfo(cc, config.tier.name, listOf("서킷 OPEN (1순위 차단)"))
                )
            }
        }
        circuitReport.emergencyCountries.forEach { cc ->
            val existing = problematic.find { it.countryCode == cc }
            if (existing != null) {
                val updated = existing.copy(issues = existing.issues + "서킷 EMERGENCY (긴급 모드)")
                problematic.remove(existing)
                problematic.add(updated)
            } else {
                val config = registry.getConfig(cc)
                problematic.add(
                    ProblematicCountryInfo(cc, config.tier.name, listOf("서킷 EMERGENCY (긴급 모드)"))
                )
            }
        }

        // 피드백 문제 국가
        feedbackSummary.problematicCountries.forEach { pc ->
            val existing = problematic.find { it.countryCode == pc.countryCode }
            if (existing != null) {
                val updated = existing.copy(issues = existing.issues + pc.issues)
                problematic.remove(existing)
                problematic.add(updated)
            } else {
                val config = registry.getConfig(pc.countryCode)
                problematic.add(
                    ProblematicCountryInfo(pc.countryCode, config.tier.name, pc.issues)
                )
            }
        }

        // SLA compliance rate
        val totalCountries = registry.registeredCountryCount()
        val slaPassCount = compliance.results.count { it.slaCheck }
        val slaRate = if (totalCountries > 0) slaPassCount.toFloat() / totalCountries * 100f else 0f

        return DashboardData(
            launchReady = locks.allLocked && compliance.allPassed,
            totalRegisteredCountries = totalCountries,
            tierBreakdown = tierBreakdown,
            compliancePassCount = compliance.passCount,
            complianceFailCount = compliance.failCount,
            globalSlaDeadlineMs = LaunchReadinessLock.GLOBAL_HARD_DEADLINE_MS,
            slaComplianceRate = slaRate,
            overallSearchFailureRate = feedbackSummary.overallSearchFailureRate,
            circuitClosedCount = circuitReport.closedCount,
            circuitOpenCount = circuitReport.openCount,
            circuitEmergencyCount = circuitReport.emergencyCount,
            openCountries = circuitReport.openCountries,
            emergencyCountries = circuitReport.emergencyCountries,
            problematicCountries = problematic,
            hardRulesLocked = locks.hardRulesLocked,
            slaLocked = locks.slaLocked,
            safeExpressionsLocked = locks.safeExpressionsLocked,
            totalFeedbackEvents = feedbackSummary.totalEvents,
            feedbackCountryCount = feedbackSummary.countryCount,
        )
    }

    /**
     * 대시보드 포맷 출력.
     * 대표님 + 자비스님이 볼 최종 형태.
     */
    fun formatDashboard(data: DashboardData): String = buildString {
        appendLine("╔══════════════════════════════════════════════════════╗")
        appendLine("║     CALLCHECK — 글로벌 출시 대시보드                ║")
        appendLine("╚══════════════════════════════════════════════════════╝")
        appendLine()

        // ── 출시 준비 상태 ──
        val readyIcon = if (data.launchReady) "✅ LAUNCH READY" else "❌ NOT READY"
        appendLine("  출시 준비: $readyIcon")
        appendLine()

        // ── 국가 현황 ──
        appendLine("┌─── 국가 현황 ───────────────────────────────────────┐")
        appendLine("│ 총 등록:     ${data.totalRegisteredCountries}개국")
        data.tierBreakdown.entries.sortedBy { it.key }.forEach { (tier, count) ->
            appendLine("│ $tier: ${count}개국")
        }
        appendLine("│ 컴플라이언스: PASS ${data.compliancePassCount} / FAIL ${data.complianceFailCount}")
        appendLine("└─────────────────────────────────────────────────────┘")
        appendLine()

        // ── SLA 현황 ──
        appendLine("┌─── SLA 현황 ────────────────────────────────────────┐")
        appendLine("│ 글로벌 SLA:   ${data.globalSlaDeadlineMs}ms")
        appendLine("│ SLA 통과율:   ${String.format("%.1f", data.slaComplianceRate)}%")
        appendLine("│ 검색 실패율:  ${String.format("%.2f", data.overallSearchFailureRate * 100)}%")
        appendLine("└─────────────────────────────────────────────────────┘")
        appendLine()

        // ── 서킷 브레이커 ──
        appendLine("┌─── 서킷 브레이커 ──────────────────────────────────┐")
        appendLine("│ CLOSED (정상):  ${data.circuitClosedCount}")
        appendLine("│ OPEN (차단):    ${data.circuitOpenCount}")
        appendLine("│ EMERGENCY:      ${data.circuitEmergencyCount}")
        if (data.openCountries.isNotEmpty()) {
            appendLine("│ OPEN 국가:      ${data.openCountries.joinToString(", ")}")
        }
        if (data.emergencyCountries.isNotEmpty()) {
            appendLine("│ EMERGENCY 국가: ${data.emergencyCountries.joinToString(", ")}")
        }
        appendLine("└─────────────────────────────────────────────────────┘")
        appendLine()

        // ── 잠금 상태 ──
        appendLine("┌─── 잠금 상태 ──────────────────────────────────────┐")
        appendLine("│ 하드 룰:       ${if (data.hardRulesLocked) "🔒 LOCKED" else "🔓 UNLOCKED"}")
        appendLine("│ SLA 정책:      ${if (data.slaLocked) "🔒 LOCKED" else "🔓 UNLOCKED"}")
        appendLine("│ 안전 표현:     ${if (data.safeExpressionsLocked) "🔒 LOCKED" else "🔓 UNLOCKED"}")
        appendLine("└─────────────────────────────────────────────────────┘")
        appendLine()

        // ── 피드백 루프 ──
        appendLine("┌─── 피드백 루프 ─────────────────────────────────────┐")
        appendLine("│ 총 이벤트:     ${data.totalFeedbackEvents}")
        appendLine("│ 국가 수:       ${data.feedbackCountryCount}")
        appendLine("└─────────────────────────────────────────────────────┘")
        appendLine()

        // ── 위험 국가 ──
        if (data.problematicCountries.isNotEmpty()) {
            appendLine("┌─── 위험 국가 (${data.problematicCountries.size}개) ─────────────────────┐")
            data.problematicCountries
                .sortedByDescending { it.issues.size }
                .forEach { pc ->
                    appendLine("│ ⚠️ [${pc.countryCode}] ${pc.tier} — ${pc.issues.joinToString(", ")}")
                }
            appendLine("└─────────────────────────────────────────────────────┘")
        } else {
            appendLine("┌─── 위험 국가 ──────────────────────────────────────┐")
            appendLine("│ ✅ 위험 국가 없음")
            appendLine("└─────────────────────────────────────────────────────┘")
        }
    }
}
