package app.myphonecheck.mobile.feature.privacycheck

import app.myphonecheck.mobile.core.model.ActionRecommendation
import app.myphonecheck.mobile.core.model.ConclusionCategory
import app.myphonecheck.mobile.core.model.DecisionResult
import app.myphonecheck.mobile.core.model.PrivacyEvidence
import app.myphonecheck.mobile.core.model.RiskLevel

/**
 * PrivacyCheck 판단 엔진.
 *
 * 카메라/마이크 센서 접근 이벤트를 분석하여 DecisionResult를 생성합니다.
 * MyPhoneCheck Decision Engine과 동일한 출력 포맷을 사용합니다.
 *
 * 판단 기준:
 * 1. 최초 접근 — 이 앱이 이 센서를 처음 사용하는가
 * 2. 백그라운드 — 포그라운드 Activity 없이 센서에 접근하는가
 * 3. 빈도 — 비정상적으로 자주 접근하는가
 * 4. 설치 경과 — 설치 직후 즉시 센서에 접근하는가
 *
 * 판단 원칙 (MyPhoneCheck과 동일):
 * - 자동 차단/권한 해제 금지
 * - 사용자에게 판단 보조 정보만 제공
 * - 행동 결정은 사용자
 */
object PrivacyCheckEngine {

    /**
     * 센서 접근 이벤트를 분석하여 판단 결과를 생성합니다.
     *
     * @param evidence AppOpsManager / PackageManager에서 수집한 센서 접근 증거
     * @return MyPhoneCheck과 동일한 DecisionResult 포맷
     */
    fun evaluate(evidence: PrivacyEvidence): DecisionResult {
        val suspicionScore = evidence.suspicionScore
        val category = determineCategory(evidence, suspicionScore)
        val riskLevel = determineRiskLevel(suspicionScore, category)
        val action = determineAction(riskLevel, category)
        val summary = buildSummary(evidence, category)
        val reasons = buildReasons(evidence)

        return DecisionResult(
            riskLevel = riskLevel,
            category = category,
            action = action,
            confidence = calculateConfidence(evidence),
            summary = summary,
            reasons = reasons,
            deviceEvidence = null,
            searchEvidence = null,
        )
    }

    private fun determineCategory(
        evidence: PrivacyEvidence,
        suspicionScore: Float,
    ): ConclusionCategory {
        return when {
            // 백그라운드 센서 접근 (가장 의심스러운 행위)
            evidence.isBackgroundAccess ->
                ConclusionCategory.PRIV_BACKGROUND

            // 높은 의심 점수 (복합 지표)
            suspicionScore >= 0.5f ->
                ConclusionCategory.PRIV_SUSPICIOUS

            // 최초 접근
            evidence.isFirstAccess ->
                ConclusionCategory.PRIV_FIRST_ACCESS

            // 비정상 빈도 (24시간 내 과다 접근)
            evidence.accessCountLast24h >= 10 ->
                ConclusionCategory.PRIV_SUSPICIOUS

            // 정상 사용
            evidence.isLikelyNormal ->
                ConclusionCategory.PRIV_NORMAL

            // 기본: 정상
            else -> ConclusionCategory.PRIV_NORMAL
        }
    }

    private fun determineRiskLevel(
        suspicionScore: Float,
        category: ConclusionCategory,
    ): RiskLevel {
        return when (category) {
            ConclusionCategory.PRIV_NORMAL -> RiskLevel.LOW
            ConclusionCategory.PRIV_FIRST_ACCESS -> RiskLevel.MEDIUM
            ConclusionCategory.PRIV_BACKGROUND -> RiskLevel.HIGH
            ConclusionCategory.PRIV_SUSPICIOUS -> {
                if (suspicionScore >= 0.6f) RiskLevel.HIGH else RiskLevel.MEDIUM
            }
            else -> RiskLevel.UNKNOWN
        }
    }

    private fun determineAction(
        riskLevel: RiskLevel,
        category: ConclusionCategory,
    ): ActionRecommendation {
        return when (category) {
            ConclusionCategory.PRIV_NORMAL -> ActionRecommendation.ANSWER
            ConclusionCategory.PRIV_FIRST_ACCESS -> ActionRecommendation.ANSWER_WITH_CAUTION
            ConclusionCategory.PRIV_BACKGROUND -> ActionRecommendation.BLOCK_REVIEW
            ConclusionCategory.PRIV_SUSPICIOUS -> ActionRecommendation.REJECT
            else -> ActionRecommendation.HOLD
        }
    }

    private fun buildSummary(
        evidence: PrivacyEvidence,
        category: ConclusionCategory,
    ): String {
        val appName = evidence.appLabel.ifEmpty {
            evidence.packageName.substringAfterLast(".")
        }
        val sensorLabel = evidence.sensorType.displayNameKo

        return when (category) {
            ConclusionCategory.PRIV_NORMAL ->
                "$appName — $sensorLabel 정상 사용"

            ConclusionCategory.PRIV_FIRST_ACCESS ->
                "$appName — $sensorLabel 최초 접근"

            ConclusionCategory.PRIV_BACKGROUND ->
                "$appName — $sensorLabel 백그라운드 접근 감지"

            ConclusionCategory.PRIV_SUSPICIOUS ->
                "$appName — $sensorLabel 의심스러운 접근 패턴"

            else ->
                "$appName — $sensorLabel 접근 감지"
        }
    }

    private fun buildReasons(evidence: PrivacyEvidence): List<String> {
        val reasons = mutableListOf<String>()

        if (evidence.isBackgroundAccess) {
            reasons.add("앱이 화면에 없는 상태에서 ${evidence.sensorType.displayNameKo} 접근")
        }
        if (evidence.isFirstAccess) {
            reasons.add("이 앱이 ${evidence.sensorType.displayNameKo}에 처음 접근")
        }
        if (evidence.daysSinceInstall <= 1) {
            reasons.add("설치 후 ${evidence.daysSinceInstall}일 — 신규 앱")
        }
        if (evidence.accessCountLast24h >= 10) {
            reasons.add("최근 24시간 ${evidence.accessCountLast24h}회 접근 (과다)")
        } else if (evidence.accessCountLast24h >= 5) {
            reasons.add("최근 24시간 ${evidence.accessCountLast24h}회 접근")
        }
        if (evidence.isCurrentlyActive) {
            reasons.add("현재 ${evidence.sensorType.displayNameKo} 사용 중")
        }

        return reasons.take(3)
    }

    private fun calculateConfidence(evidence: PrivacyEvidence): Float {
        var confidence = 0.3f

        // 백그라운드 접근은 명확한 신호
        if (evidence.isBackgroundAccess) confidence += 0.3f

        // 이력이 많을수록 판단 신뢰도 향상
        if (evidence.accessCountLast7d >= 5) confidence += 0.15f
        if (evidence.accessCountLast24h >= 1) confidence += 0.1f

        // 앱 정보가 충분할수록 신뢰도 향상
        if (evidence.appLabel.isNotEmpty()) confidence += 0.05f
        if (evidence.daysSinceInstall > 0) confidence += 0.1f

        return confidence.coerceIn(0f, 1f)
    }
}
