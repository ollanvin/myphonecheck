package app.myphonecheck.mobile.feature.callintercept

import app.myphonecheck.mobile.core.model.SearchEngine
import app.myphonecheck.mobile.core.model.SearchTier
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 223개국 레지스트리 컴플라이언스 검증기.
 *
 * 자비스 기준 5개 축:
 *   1. 1순위/2순위 검색엔진 정합성 (국가별 강제 규칙 포함)
 *   2. 현지어 쿼리 존재 (빈 템플릿 0건)
 *   3. ParsingRules 적용 (null 또는 누락 0건)
 *   4. 2초 SLA (hardDeadline ≤ 2000ms)
 *   5. 결과 미표시 방지 구조 (SearchTimeoutEnforcer 연동)
 *
 * 축 5는 SearchTimeoutEnforcer 자체가 보장.
 * 이 검증기는 축 1~4를 레지스트리 데이터 기준으로 전수 검증.
 *
 * 100% 온디바이스. 서버 전송 없음.
 */
@Singleton
class CountryComplianceValidator @Inject constructor(
    private val registry: GlobalSearchProviderRegistry,
) {

    /**
     * 단일 국가 검증 결과.
     */
    data class CountryResult(
        val countryCode: String,
        val tier: SearchTier,
        val engineCheck: Boolean,
        val queryCheck: Boolean,
        val parserCheck: Boolean,
        val slaCheck: Boolean,
        val failReasons: List<String>,
    ) {
        val passed: Boolean get() = engineCheck && queryCheck && parserCheck && slaCheck
    }

    /**
     * 전체 검증 보고서.
     */
    data class ComplianceReport(
        val totalCountries: Int,
        val passCount: Int,
        val failCount: Int,
        val results: List<CountryResult>,
    ) {
        val allPassed: Boolean get() = failCount == 0

        fun toJarvisFormat(): String = buildString {
            appendLine("═══ 국가별 컴플라이언스 PASS 표 ═══")
            appendLine()
            appendLine("총 등록: ${totalCountries}개국")
            appendLine("PASS: ${passCount}개국")
            appendLine("FAIL: ${failCount}개국")
            appendLine()

            if (failCount > 0) {
                appendLine("── FAIL 국가 ──")
                results.filter { !it.passed }.forEach { r ->
                    appendLine("  ❌ [${r.countryCode}] ${r.failReasons.joinToString(", ")}")
                }
                appendLine()
            }

            // Tier summary
            val byTier = results.groupBy { it.tier }
            appendLine("── 티어별 ──")
            for (tier in SearchTier.entries) {
                val tierResults = byTier[tier] ?: emptyList()
                val tierPass = tierResults.count { it.passed }
                appendLine("  ${tier.name}: ${tierPass}/${tierResults.size}")
            }
            appendLine()

            // Per-country table
            appendLine("── 국가별 상세 ──")
            appendLine("  CC  | Tier | Engine | Query | Parser | SLA  | 판정")
            appendLine("  ----|------|--------|-------|--------|------|------")
            results.sortedWith(compareBy({ it.tier }, { it.countryCode })).forEach { r ->
                val status = if (r.passed) "✅" else "❌"
                appendLine("  ${r.countryCode}  | ${r.tier.name.takeLast(1)}    | ${check(r.engineCheck)}     | ${check(r.queryCheck)}    | ${check(r.parserCheck)}     | ${check(r.slaCheck)}   | $status")
            }
        }

        private fun check(ok: Boolean): String = if (ok) "✅" else "❌"
    }

    /** 국가별 검색엔진 강제 규칙 */
    private val engineRules: Map<String, EngineRule> = mapOf(
        "CN" to EngineRule(
            requiredPrimary = null,
            bannedAsPrimary = setOf(SearchEngine.GOOGLE),
            bannedAsSecondary = setOf(SearchEngine.GOOGLE),
            mustBanGlobal = setOf(SearchEngine.GOOGLE, SearchEngine.BING, SearchEngine.DUCKDUCKGO),
        ),
        "KR" to EngineRule(requiredPrimary = SearchEngine.NAVER),
        "JP" to EngineRule(requiredPrimary = SearchEngine.YAHOO_JAPAN),
        "RU" to EngineRule(requiredPrimary = SearchEngine.YANDEX),
        "CZ" to EngineRule(requiredPrimary = SearchEngine.SEZNAM),
    )

    private data class EngineRule(
        val requiredPrimary: SearchEngine? = null,
        val bannedAsPrimary: Set<SearchEngine> = emptySet(),
        val bannedAsSecondary: Set<SearchEngine> = emptySet(),
        val mustBanGlobal: Set<SearchEngine> = emptySet(),
    )

    /**
     * 223개국 전수 검증 실행.
     *
     * @return ComplianceReport — FAIL 0이면 완성 판정
     */
    fun validateAll(): ComplianceReport {
        val results = mutableListOf<CountryResult>()

        registry.allCountries().forEach { config ->
            val fails = mutableListOf<String>()

            // ── 축 1: 엔진 정합성 ──
            var engineOk = true

            // 기본: primary/secondary가 NONE이 아닌지
            if (config.primaryEngine == SearchEngine.NONE) {
                engineOk = false
                fails.add("1순위 엔진 없음")
            }

            // 국가별 강제 규칙
            val rule = engineRules[config.countryCode]
            if (rule != null) {
                if (rule.requiredPrimary != null && config.primaryEngine != rule.requiredPrimary) {
                    engineOk = false
                    fails.add("1순위 ${config.primaryEngine.displayName} — ${rule.requiredPrimary.displayName} 필수")
                }
                if (config.primaryEngine in rule.bannedAsPrimary) {
                    engineOk = false
                    fails.add("1순위 ${config.primaryEngine.displayName} 금지")
                }
                if (config.secondaryEngine in rule.bannedAsSecondary) {
                    engineOk = false
                    fails.add("2순위 ${config.secondaryEngine.displayName} 금지")
                }
                for (banned in rule.mustBanGlobal) {
                    if (banned !in config.bannedEngines) {
                        engineOk = false
                        fails.add("${banned.displayName} 글로벌 금지 미설정")
                    }
                }
            }

            // ── 축 2: 현지어 쿼리 ──
            val queryOk = config.queryLocalization.queryTemplates.isNotEmpty()
            if (!queryOk) {
                fails.add("쿼리 템플릿 0건")
            }

            // ── 축 3: 파서 ──
            val parserOk = true  // ParsingRules는 data class, 기본값 항상 존재
            // 추가 검증: baseConfidenceWeight > 0
            if (config.parsingRules.baseConfidenceWeight <= 0f) {
                fails.add("baseConfidenceWeight ≤ 0")
            }

            // ── 축 4: 2초 SLA ──
            val slaOk = config.timeoutPolicy.hardDeadlineMs <= SearchTimeoutEnforcer.GLOBAL_SLA_MS
            if (!slaOk) {
                fails.add("hardDeadline ${config.timeoutPolicy.hardDeadlineMs}ms > 2000ms")
            }

            results.add(
                CountryResult(
                    countryCode = config.countryCode,
                    tier = config.tier,
                    engineCheck = engineOk,
                    queryCheck = queryOk,
                    parserCheck = parserOk && config.parsingRules.baseConfidenceWeight > 0f,
                    slaCheck = slaOk,
                    failReasons = fails,
                )
            )
        }

        val passCount = results.count { it.passed }
        return ComplianceReport(
            totalCountries = results.size,
            passCount = passCount,
            failCount = results.size - passCount,
            results = results,
        )
    }
}
