package app.myphonecheck.mobile.feature.callintercept

import app.myphonecheck.mobile.core.model.SearchEngine
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 정책 자동 조정기.
 *
 * 빅테크 정석: Production Telemetry → Auto Policy Adjustment
 *
 * ProductionFeedbackCollector에서 수집된 데이터를 기반으로:
 *   1. 검색엔진 fallback 순서 자동 변경
 *   2. 키워드 가중치 자동 조정
 *   3. 위험 점수 자동 보정
 *   4. 타임아웃 정책 미세 조정
 *
 * 조정 원칙:
 * - 최소 샘플 수 확보 후 조정 (MIN_SAMPLES)
 * - 급격한 변경 방지 (최대 변경 폭 제한)
 * - 자비스 기준 위반 방지 (CN→Google 금지 등 하드 룰 보호)
 * - 모든 조정 이력 기록
 *
 * 100% 온디바이스. 서버 전송 없음.
 */
@Singleton
class AutoPolicyAdjuster @Inject constructor(
    private val feedbackCollector: ProductionFeedbackCollector,
    private val registry: GlobalSearchProviderRegistry,
) {

    // ══════════════════════════════════════
    // 조정 결과 모델
    // ══════════════════════════════════════

    /** 단일 조정 항목 */
    sealed class Adjustment {
        abstract val countryCode: String
        abstract val reason: String
        abstract val confidence: Float

        /** 검색엔진 순서 변경 권고 */
        data class EngineOrderChange(
            override val countryCode: String,
            val currentPrimary: SearchEngine,
            val suggestedPrimary: SearchEngine,
            val currentSecondary: SearchEngine,
            val suggestedSecondary: SearchEngine,
            override val reason: String,
            override val confidence: Float,
        ) : Adjustment()

        /** 키워드 가중치 조정 권고 */
        data class KeywordWeightChange(
            override val countryCode: String,
            val currentRiskWeight: Float,
            val suggestedRiskWeight: Float,
            val currentSafeWeight: Float,
            val suggestedSafeWeight: Float,
            override val reason: String,
            override val confidence: Float,
        ) : Adjustment()

        /** 타임아웃 조정 권고 */
        data class TimeoutChange(
            override val countryCode: String,
            val currentPrimaryTimeout: Long,
            val suggestedPrimaryTimeout: Long,
            override val reason: String,
            override val confidence: Float,
        ) : Adjustment()

        /** 위험 점수 보정 권고 */
        data class RiskScoreCalibration(
            override val countryCode: String,
            val currentBaseConfidence: Float,
            val suggestedBaseConfidence: Float,
            override val reason: String,
            override val confidence: Float,
        ) : Adjustment()
    }

    /** 조정 보고서 */
    data class AdjustmentReport(
        val adjustments: List<Adjustment>,
        val countriesAnalyzed: Int,
        val adjustmentsGenerated: Int,
    ) {
        fun toJarvisFormat(): String = buildString {
            appendLine("═══ Auto Policy Adjustment 보고 ═══")
            appendLine()
            appendLine("분석 국가: ${countriesAnalyzed}개국")
            appendLine("조정 권고: ${adjustmentsGenerated}건")
            appendLine()

            if (adjustments.isEmpty()) {
                appendLine("  ✅ 조정 필요 없음 — 전 국가 정상 범위")
                return@buildString
            }

            // 유형별 분류
            val engineChanges = adjustments.filterIsInstance<Adjustment.EngineOrderChange>()
            val weightChanges = adjustments.filterIsInstance<Adjustment.KeywordWeightChange>()
            val timeoutChanges = adjustments.filterIsInstance<Adjustment.TimeoutChange>()
            val riskChanges = adjustments.filterIsInstance<Adjustment.RiskScoreCalibration>()

            if (engineChanges.isNotEmpty()) {
                appendLine("── 엔진 순서 변경 (${engineChanges.size}건) ──")
                engineChanges.forEach { a ->
                    appendLine("  [${a.countryCode}] ${a.currentPrimary.displayName} → ${a.suggestedPrimary.displayName} | 신뢰도 ${String.format("%.0f", a.confidence * 100)}% | ${a.reason}")
                }
                appendLine()
            }

            if (weightChanges.isNotEmpty()) {
                appendLine("── 키워드 가중치 조정 (${weightChanges.size}건) ──")
                weightChanges.forEach { a ->
                    appendLine("  [${a.countryCode}] risk ${a.currentRiskWeight}→${a.suggestedRiskWeight} | 신뢰도 ${String.format("%.0f", a.confidence * 100)}% | ${a.reason}")
                }
                appendLine()
            }

            if (timeoutChanges.isNotEmpty()) {
                appendLine("── 타임아웃 조정 (${timeoutChanges.size}건) ──")
                timeoutChanges.forEach { a ->
                    appendLine("  [${a.countryCode}] ${a.currentPrimaryTimeout}ms → ${a.suggestedPrimaryTimeout}ms | ${a.reason}")
                }
                appendLine()
            }

            if (riskChanges.isNotEmpty()) {
                appendLine("── 위험 점수 보정 (${riskChanges.size}건) ──")
                riskChanges.forEach { a ->
                    appendLine("  [${a.countryCode}] base ${a.currentBaseConfidence} → ${a.suggestedBaseConfidence} | ${a.reason}")
                }
            }
        }
    }

    // ══════════════════════════════════════
    // 자동 분석 + 조정 생성
    // ══════════════════════════════════════

    /**
     * 전체 국가 분석 후 조정 권고 생성.
     *
     * @return AdjustmentReport — 적용 여부는 호출자가 결정
     */
    fun analyzeAndSuggest(): AdjustmentReport {
        val adjustments = mutableListOf<Adjustment>()
        var countriesAnalyzed = 0

        feedbackCollector.getAllCountryStats().forEach { stats ->
            if (stats.totalIntercepts < MIN_SAMPLES) return@forEach
            countriesAnalyzed++

            val config = registry.getConfig(stats.countryCode)

            // ── 1. 엔진 순서 조정 ──
            analyzeEngineOrder(stats, config)?.let { adjustments.add(it) }

            // ── 2. 키워드 가중치 조정 ──
            analyzeKeywordWeights(stats, config)?.let { adjustments.add(it) }

            // ── 3. 타임아웃 조정 ──
            analyzeTimeout(stats, config)?.let { adjustments.add(it) }

            // ── 4. 위험 점수 보정 ──
            analyzeRiskScore(stats, config)?.let { adjustments.add(it) }
        }

        return AdjustmentReport(
            adjustments = adjustments,
            countriesAnalyzed = countriesAnalyzed,
            adjustmentsGenerated = adjustments.size,
        )
    }

    // ══════════════════════════════════════
    // 개별 분석 로직
    // ══════════════════════════════════════

    /**
     * 엔진 순서 분석.
     * fallback률이 높으면 1순위/2순위 교체 권고.
     */
    private fun analyzeEngineOrder(
        stats: ProductionFeedbackCollector.CountryStats,
        config: app.myphonecheck.mobile.core.model.CountrySearchConfig,
    ): Adjustment.EngineOrderChange? {
        // fallback률 30% 이상 + 검색 실패율 10% 이상 → 엔진 교체 검토
        if (stats.fallbackRate < FALLBACK_RATE_TRIGGER) return null
        if (stats.searchFailureRate < SEARCH_FAILURE_TRIGGER) return null

        // 하드 룰 보호: 자비스 기준 강제 엔진 국가는 교체 금지
        if (config.countryCode in LOCKED_ENGINE_COUNTRIES) return null

        // 2순위를 1순위로 승격 제안
        return Adjustment.EngineOrderChange(
            countryCode = config.countryCode,
            currentPrimary = config.primaryEngine,
            suggestedPrimary = config.secondaryEngine,
            currentSecondary = config.secondaryEngine,
            suggestedSecondary = config.primaryEngine,
            reason = "1순위 fallback률 ${String.format("%.0f", stats.fallbackRate * 100)}%, 검색실패 ${String.format("%.0f", stats.searchFailureRate * 100)}%",
            confidence = calculateConfidence(stats.totalIntercepts, stats.fallbackRate),
        )
    }

    /**
     * 키워드 가중치 분석.
     * 사용자 불일치율이 높으면 가중치 조정 권고.
     */
    private fun analyzeKeywordWeights(
        stats: ProductionFeedbackCollector.CountryStats,
        config: app.myphonecheck.mobile.core.model.CountrySearchConfig,
    ): Adjustment.KeywordWeightChange? {
        if (stats.disagreementRate < DISAGREEMENT_TRIGGER) return null

        val currentRisk = config.parsingRules.riskKeywordWeight
        val currentSafe = config.parsingRules.safeKeywordWeight

        // 불일치율이 높으면: 사용자가 판정에 동의하지 않음
        // → risk 가중치를 약간 높이고 safe 가중치를 약간 낮춤
        val suggestedRisk = (currentRisk * (1f + WEIGHT_ADJUSTMENT_STEP)).coerceAtMost(MAX_KEYWORD_WEIGHT)
        val suggestedSafe = (currentSafe * (1f - WEIGHT_ADJUSTMENT_STEP)).coerceAtLeast(MIN_KEYWORD_WEIGHT)

        return Adjustment.KeywordWeightChange(
            countryCode = config.countryCode,
            currentRiskWeight = currentRisk,
            suggestedRiskWeight = suggestedRisk,
            currentSafeWeight = currentSafe,
            suggestedSafeWeight = suggestedSafe,
            reason = "사용자 불일치율 ${String.format("%.0f", stats.disagreementRate * 100)}%",
            confidence = calculateConfidence(stats.totalIntercepts, stats.disagreementRate),
        )
    }

    /**
     * 타임아웃 분석.
     * SLA 위반률이 높으면 1순위 타임아웃 단축 권고.
     */
    private fun analyzeTimeout(
        stats: ProductionFeedbackCollector.CountryStats,
        config: app.myphonecheck.mobile.core.model.CountrySearchConfig,
    ): Adjustment.TimeoutChange? {
        if (stats.slaViolationRate < SLA_VIOLATION_TRIGGER) return null

        val currentTimeout = config.timeoutPolicy.primaryTimeoutMs
        // SLA 위반이 잦으면 1순위 타임아웃을 단축하여 fallback 여유를 확보
        val suggestedTimeout = (currentTimeout * 0.8).toLong().coerceAtLeast(MIN_PRIMARY_TIMEOUT)

        if (suggestedTimeout >= currentTimeout) return null

        return Adjustment.TimeoutChange(
            countryCode = config.countryCode,
            currentPrimaryTimeout = currentTimeout,
            suggestedPrimaryTimeout = suggestedTimeout,
            reason = "SLA 위반율 ${String.format("%.0f", stats.slaViolationRate * 100)}% → 1순위 타임아웃 단축",
            confidence = calculateConfidence(stats.totalIntercepts, stats.slaViolationRate),
        )
    }

    /**
     * 위험 점수 보정.
     * 차단 비율이 비정상적이면 baseConfidence 조정.
     */
    private fun analyzeRiskScore(
        stats: ProductionFeedbackCollector.CountryStats,
        config: app.myphonecheck.mobile.core.model.CountrySearchConfig,
    ): Adjustment.RiskScoreCalibration? {
        val totalActions = stats.userAccepts + stats.userRejects + stats.userBlocks + stats.userIgnores
        if (totalActions < MIN_SAMPLES) return null

        val blockRate = stats.userBlocks.toFloat() / totalActions

        val currentBase = config.parsingRules.baseConfidenceWeight

        // 차단율이 매우 높은데 현재 신뢰도가 낮다면 → 상향
        if (blockRate > HIGH_BLOCK_RATE && currentBase < 1.0f) {
            return Adjustment.RiskScoreCalibration(
                countryCode = config.countryCode,
                currentBaseConfidence = currentBase,
                suggestedBaseConfidence = (currentBase + CONFIDENCE_ADJUSTMENT_STEP).coerceAtMost(1.5f),
                reason = "차단율 ${String.format("%.0f", blockRate * 100)}% — 위험도 감지 강화 필요",
                confidence = calculateConfidence(totalActions, blockRate),
            )
        }

        // 차단율이 매우 낮은데 현재 신뢰도가 높다면 → 하향
        if (blockRate < LOW_BLOCK_RATE && currentBase > 1.0f) {
            return Adjustment.RiskScoreCalibration(
                countryCode = config.countryCode,
                currentBaseConfidence = currentBase,
                suggestedBaseConfidence = (currentBase - CONFIDENCE_ADJUSTMENT_STEP).coerceAtLeast(0.5f),
                reason = "차단율 ${String.format("%.0f", blockRate * 100)}% — 위험도 과잉 감지 가능",
                confidence = calculateConfidence(totalActions, 1f - blockRate),
            )
        }

        return null
    }

    // ══════════════════════════════════════
    // 조정 적용
    // ══════════════════════════════════════

    /** 조정 이력 */
    private val adjustmentHistory = mutableListOf<AppliedAdjustment>()

    data class AppliedAdjustment(
        val timestampMs: Long,
        val adjustment: Adjustment,
        val applied: Boolean,
        val reason: String,
    )

    /**
     * 조정 적용 시뮬레이션.
     *
     * 실제 레지스트리 변경은 하지 않음 (불변 레지스트리 원칙).
     * 대신 오버라이드 맵을 반환하여 런타임에서 활용.
     */
    fun applyAdjustments(report: AdjustmentReport): List<AppliedAdjustment> {
        val applied = mutableListOf<AppliedAdjustment>()

        report.adjustments.forEach { adj ->
            // 신뢰도 임계값 미달 → 스킵
            if (adj.confidence < MIN_APPLY_CONFIDENCE) {
                applied.add(
                    AppliedAdjustment(
                        timestampMs = System.currentTimeMillis(),
                        adjustment = adj,
                        applied = false,
                        reason = "신뢰도 ${String.format("%.0f", adj.confidence * 100)}% < ${String.format("%.0f", MIN_APPLY_CONFIDENCE * 100)}% 미달",
                    )
                )
                return@forEach
            }

            // 하드 룰 위반 체크
            if (adj is Adjustment.EngineOrderChange && adj.countryCode in LOCKED_ENGINE_COUNTRIES) {
                applied.add(
                    AppliedAdjustment(
                        timestampMs = System.currentTimeMillis(),
                        adjustment = adj,
                        applied = false,
                        reason = "하드 룰 보호 — ${adj.countryCode} 엔진 변경 금지",
                    )
                )
                return@forEach
            }

            // 조정 적용 (이력 기록)
            applied.add(
                AppliedAdjustment(
                    timestampMs = System.currentTimeMillis(),
                    adjustment = adj,
                    applied = true,
                    reason = "자동 적용",
                )
            )
        }

        adjustmentHistory.addAll(applied)
        return applied
    }

    /** 조정 이력 조회 */
    fun getAdjustmentHistory(): List<AppliedAdjustment> = adjustmentHistory.toList()

    // ══════════════════════════════════════
    // Internal
    // ══════════════════════════════════════

    /** 신뢰도 계산: 샘플 수 + 문제 비율 기반 */
    private fun calculateConfidence(sampleCount: Int, problemRate: Float): Float {
        // 샘플이 많을수록, 문제가 뚜렷할수록 신뢰도 높음
        val sampleConfidence = (sampleCount.toFloat() / IDEAL_SAMPLE_SIZE).coerceAtMost(1f)
        val rateConfidence = (problemRate * 2f).coerceAtMost(1f)
        return (sampleConfidence * 0.6f + rateConfidence * 0.4f).coerceIn(0f, 1f)
    }

    companion object {
        /** 최소 샘플 수 */
        private const val MIN_SAMPLES = 50

        /** 이상적 샘플 수 (100% 신뢰도) */
        private const val IDEAL_SAMPLE_SIZE = 500f

        /** 최소 적용 신뢰도 */
        private const val MIN_APPLY_CONFIDENCE = 0.60f

        /** 트리거 임계값 */
        private const val FALLBACK_RATE_TRIGGER = 0.30f
        private const val SEARCH_FAILURE_TRIGGER = 0.10f
        private const val DISAGREEMENT_TRIGGER = 0.15f
        private const val SLA_VIOLATION_TRIGGER = 0.05f
        private const val HIGH_BLOCK_RATE = 0.40f
        private const val LOW_BLOCK_RATE = 0.05f

        /** 가중치 조정 스텝 */
        private const val WEIGHT_ADJUSTMENT_STEP = 0.10f
        private const val CONFIDENCE_ADJUSTMENT_STEP = 0.1f
        private const val MAX_KEYWORD_WEIGHT = 0.30f
        private const val MIN_KEYWORD_WEIGHT = 0.05f
        private const val MIN_PRIMARY_TIMEOUT = 500L

        /** 엔진 교체 금지 국가 (자비스 하드 룰) */
        private val LOCKED_ENGINE_COUNTRIES = setOf("KR", "CN", "JP", "RU", "CZ")
    }
}
