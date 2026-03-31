package app.callcheck.mobile.feature.decisionengine

import app.callcheck.mobile.core.model.ActionRecommendation
import app.callcheck.mobile.core.model.BehaviorPatternSignal
import app.callcheck.mobile.core.model.ConclusionCategory
import app.callcheck.mobile.core.model.DecisionResult
import app.callcheck.mobile.core.model.DeviceEvidence
import app.callcheck.mobile.core.model.LocalLearningSignal
import app.callcheck.mobile.core.model.RiskLevel
import app.callcheck.mobile.core.model.SearchEvidence
import app.callcheck.mobile.core.model.SearchTrend
import javax.inject.Inject
import kotlin.math.min

/**
 * PRD-aligned decision engine.
 *
 * Scoring axes:
 * 1. Relationship score (0.0–1.0) — how trusted is this number based on device evidence?
 * 2. Risk score (0.0–1.0) — how dangerous based on search signals + device patterns?
 *
 * These two scores feed into:
 * - ConclusionCategory (7 PRD categories)
 * - RiskLevel (4 PRD levels)
 * - ActionRecommendation (5 PRD actions)
 * - confidence (0.0–1.0)
 * - summary (one-line, from ConclusionCategory)
 * - reasons (max 3, from actual evidence)
 */
class DecisionEngineImpl @Inject constructor(
    private val riskBadgeMapper: RiskBadgeMapper,
    private val actionMapper: ActionMapper,
    private val summaryGenerator: SummaryGenerator,
) : DecisionEngine {

    override fun evaluate(
        deviceEvidence: DeviceEvidence?,
        searchEvidence: SearchEvidence?,
        localLearning: LocalLearningSignal?,
        behaviorPattern: BehaviorPatternSignal?,
    ): DecisionResult {
        val hasAnyEvidence = (deviceEvidence != null && deviceEvidence.hasAnyHistory) ||
                (searchEvidence != null && !searchEvidence.isEmpty) ||
                (deviceEvidence?.isSavedContact == true) ||
                (localLearning != null && localLearning.callCount > 0)

        // Step 1: Relationship score from device evidence
        val relationshipScore = calculateRelationshipScore(deviceEvidence)

        // Step 2: Risk score from search + device patterns + local learning + behavior pattern
        val rawRiskScore = calculateRiskScore(deviceEvidence, searchEvidence)
        val localAdj = localLearning?.riskAdjustment() ?: 0f
        val behaviorAdj = behaviorPattern?.riskAdjustment() ?: 0f
        val riskScore = (rawRiskScore + localAdj + behaviorAdj).coerceIn(0f, 1f)

        // Step 3: Conclusion category
        val category = determineCategory(
            deviceEvidence = deviceEvidence,
            searchEvidence = searchEvidence,
            relationshipScore = relationshipScore,
            riskScore = riskScore,
        )

        // Step 4: Risk badge
        val riskLevel = riskBadgeMapper.map(riskScore, hasAnyEvidence)

        // Step 5: Action recommendation
        val action = actionMapper.map(category, riskLevel)

        // Step 6: Confidence
        val confidence = calculateConfidence(
            deviceEvidence = deviceEvidence,
            searchEvidence = searchEvidence,
            relationshipScore = relationshipScore,
            riskScore = riskScore,
            category = category,
        )

        // Step 7: Summary + reasons
        val summary = summaryGenerator.generateSummary(category)
        val reasons = summaryGenerator.generateReasons(category, deviceEvidence, searchEvidence)

        return DecisionResult(
            riskLevel = riskLevel,
            category = category,
            action = action,
            confidence = confidence,
            summary = summary,
            reasons = reasons,
            deviceEvidence = deviceEvidence,
            searchEvidence = searchEvidence,
        )
    }

    // ───────────────────────────────────────────────
    // Relationship Score
    // ───────────────────────────────────────────────

    /**
     * 0.0 = completely unknown, 1.0 = fully trusted.
     *
     * Factors (additive, clamped to 1.0):
     * - Saved contact → instant 1.0
     * - User-initiated outgoing calls → +0.30
     * - Connected calls ≥ 3 → +0.20
     * - Long calls (> 60s) exist → +0.15
     * - SMS exists → +0.10
     * - Recent contact ≤ 7 days → +0.10
     * - Recent contact ≤ 30 days → +0.05
     */
    private fun calculateRelationshipScore(device: DeviceEvidence?): Float {
        if (device == null) return 0f
        if (device.isSavedContact) return 1.0f

        var score = 0f

        // User initiated contact — strongest non-contact trust signal
        if (device.outgoingCount > 0) {
            score += 0.30f
        }

        // Multiple real conversations
        when {
            device.connectedCount >= 5 -> score += 0.25f
            device.connectedCount >= 3 -> score += 0.20f
            device.connectedCount >= 1 -> score += 0.10f
        }

        // Long meaningful calls
        if (device.longCallCount > 0) {
            score += 0.15f
        }

        // SMS interaction
        if (device.smsExists) {
            score += 0.10f
        }

        // Recency
        val days = device.recentDaysContact
        if (days != null) {
            when {
                days <= 7 -> score += 0.10f
                days <= 30 -> score += 0.05f
            }
        }

        return min(score, 1.0f)
    }

    // ───────────────────────────────────────────────
    // Risk Score
    // ───────────────────────────────────────────────

    /**
     * 0.0 = safe, 1.0 = max danger.
     *
     * 핵심 원칙: "Unknown ≠ Danger"
     * - 정보가 없다는 것은 위험하다는 뜻이 아님
     * - 위험 점수는 실제 위험 신호(search/device 패턴)에서만 발생
     * - 정보 부재는 confidence 저하로 반영 (리스크 가산 아님)
     *
     * Factors from search evidence:
     * - hasScamSignal → +0.40
     * - hasSpamSignal → +0.25
     * - searchTrend INCREASING → +0.10
     * - high 30d search intensity → +0.10
     *
     * Factors from device patterns:
     * - Short call pattern (many <10s, no >60s) → +0.10
     * - User rejected multiple times → +0.10
     *
     * 제거됨 (Unknown ≠ Danger 원칙):
     * - Zero history → +0.15 (제거)
     * - No device evidence → +0.15 (제거)
     * → 이 정보 부재는 hasAnyEvidence=false → UNKNOWN 분류로 처리
     */
    private fun calculateRiskScore(
        device: DeviceEvidence?,
        search: SearchEvidence?,
    ): Float {
        var score = 0f

        // --- Search-based signals (실제 위험 신호) ---
        if (search != null && !search.isEmpty) {
            if (search.hasScamSignal) score += 0.40f
            if (search.hasSpamSignal) score += 0.25f

            if (search.searchTrend == SearchTrend.INCREASING) {
                score += 0.10f
            }

            val intensity30d = search.recent30dSearchIntensity ?: 0
            if (intensity30d > 50) {
                score += 0.10f
            }
        }

        // --- Device-based signals (실제 행동 패턴) ---
        if (device != null) {
            // Short call pattern — robo-call indicator
            // 실제 통화 기록이 있고, 짧은 통화만 반복 → 위험 패턴
            if (device.shortCallCount >= 3 && device.longCallCount == 0 && device.connectedCount > 0) {
                score += 0.10f
            }

            // User actively rejected multiple times — they don't want this call
            // 사용자의 명시적 거부 의사 → 위험 신호
            if (device.rejectedCount >= 2) {
                score += 0.10f
            }
        }
        // device == null 또는 history 없음: 리스크 가산 없음 (Unknown ≠ Danger)

        // Saved contact dampens risk
        if (device?.isSavedContact == true) {
            score *= 0.1f
        }

        return min(score, 1.0f)
    }

    // ───────────────────────────────────────────────
    // Category Determination
    // ───────────────────────────────────────────────

    /**
     * Priority-ordered decision tree for PRD 7 categories.
     *
     * Priority order:
     * 1. KNOWN_CONTACT — saved in contacts (단, 강한 스캠 신호 시 SCAM_RISK_HIGH 우선)
     * 2. SCAM_RISK_HIGH — scam signal or very high risk
     * 3. SALES_SPAM_SUSPECTED — spam signal or medium-high risk
     * 4. DELIVERY_LIKELY — delivery keyword cluster
     * 5. INSTITUTION_LIKELY — institution keyword cluster
     * 6. BUSINESS_LIKELY — business keyword cluster or high relationship
     * 7. INSUFFICIENT_EVIDENCE — default
     *
     * 저장 연락처 과신 방지:
     * - 번호 스푸핑 시나리오 (연락처 이름 "엄마"지만 실제는 사기범)
     * - 강한 스캠 신호(hasScamSignal + riskScore ≥ 0.6) → SCAM_RISK_HIGH 유지
     * - 스팸 신호만 있으면 → KNOWN_CONTACT 유지 (risk 감쇄가 충분)
     */
    private fun determineCategory(
        deviceEvidence: DeviceEvidence?,
        searchEvidence: SearchEvidence?,
        relationshipScore: Float,
        riskScore: Float,
    ): ConclusionCategory {
        // 1. Saved contact — 단, 강한 스캠 신호가 있으면 SCAM이 우선
        if (deviceEvidence?.isSavedContact == true) {
            // 스푸핑 방어: 검색에서 강한 스캠 신호가 감지된 경우
            // risk 감쇄(×0.1) 이전의 원래 스캠 신호 존재 여부로 판단
            if (searchEvidence?.hasScamSignal == true) {
                return ConclusionCategory.SCAM_RISK_HIGH
            }
            return ConclusionCategory.KNOWN_CONTACT
        }

        // 2. Scam risk
        if (searchEvidence?.hasScamSignal == true || riskScore >= 0.6f) {
            return ConclusionCategory.SCAM_RISK_HIGH
        }

        // 3. Spam/sales
        if (searchEvidence?.hasSpamSignal == true || (riskScore >= 0.3f && riskScore < 0.6f)) {
            return ConclusionCategory.SALES_SPAM_SUSPECTED
        }

        // 4. Delivery signal
        if (searchEvidence?.hasDeliverySignal == true) {
            return ConclusionCategory.DELIVERY_LIKELY
        }

        // 5. Institution signal
        if (searchEvidence?.hasInstitutionSignal == true) {
            return ConclusionCategory.INSTITUTION_LIKELY
        }

        // 6. Business signal or strong relationship from device
        if (searchEvidence?.hasBusinessSignal == true) {
            return ConclusionCategory.BUSINESS_LIKELY
        }
        if (relationshipScore >= 0.5f) {
            return ConclusionCategory.BUSINESS_LIKELY
        }

        // 7. Default
        return ConclusionCategory.INSUFFICIENT_EVIDENCE
    }

    // ───────────────────────────────────────────────
    // Confidence
    // ───────────────────────────────────────────────

    /**
     * Confidence in the decision, 0.0–1.0.
     *
     * Base:
     * - Both evidence sources → 0.85
     * - Device only → 0.70
     * - Search only → 0.60
     * - Neither → 0.30
     *
     * Adjustments:
     * - KNOWN_CONTACT → floor 0.95
     * - SCAM_RISK_HIGH with strong signal → floor 0.80
     * - Conflicting signals (high relationship + high risk) → penalty
     */
    private fun calculateConfidence(
        deviceEvidence: DeviceEvidence?,
        searchEvidence: SearchEvidence?,
        relationshipScore: Float,
        riskScore: Float,
        category: ConclusionCategory,
    ): Float {
        val hasDevice = deviceEvidence != null &&
                (deviceEvidence.hasAnyHistory || deviceEvidence.isSavedContact)
        val hasSearch = searchEvidence != null && !searchEvidence.isEmpty

        var confidence = when {
            hasDevice && hasSearch -> 0.85f
            hasDevice -> 0.70f
            hasSearch -> 0.60f
            else -> 0.30f
        }

        // Conflicting signals reduce confidence
        if (relationshipScore > 0.5f && riskScore > 0.3f) {
            confidence -= 0.15f
        }

        // Category-specific floors
        when (category) {
            ConclusionCategory.KNOWN_CONTACT -> {
                confidence = maxOf(confidence, 0.95f)
            }
            ConclusionCategory.SCAM_RISK_HIGH -> {
                if (searchEvidence?.hasScamSignal == true) {
                    confidence = maxOf(confidence, 0.80f)
                }
            }
            ConclusionCategory.INSUFFICIENT_EVIDENCE -> {
                confidence = minOf(confidence, 0.40f)
            }
            else -> { /* use calculated value */ }
        }

        return confidence.coerceIn(0f, 1f)
    }

    // ───────────────────────────────────────────────
    // Risk Level from Score (Tier 0 PreJudge용)
    // ───────────────────────────────────────────────

    override fun riskLevelFromScore(score: Float): RiskLevel = when {
        score >= 0.6f -> RiskLevel.HIGH
        score >= 0.3f -> RiskLevel.MEDIUM
        score > 0f -> RiskLevel.LOW
        else -> RiskLevel.UNKNOWN
    }
}
