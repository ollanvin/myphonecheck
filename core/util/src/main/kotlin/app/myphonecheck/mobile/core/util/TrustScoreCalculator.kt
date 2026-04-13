package app.myphonecheck.mobile.core.util

import app.myphonecheck.mobile.core.model.DecisionResult
import app.myphonecheck.mobile.core.model.RiskLevel
import app.myphonecheck.mobile.core.model.SearchEvidence

/**
 * 0~100 단일 신뢰도 점수 계산기.
 *
 * ┌───────────┬────────────┬──────────────────────┐
 * │ 점수 범위  │ 등급       │ 표시                  │
 * ├───────────┼────────────┼──────────────────────┤
 * │ 0 ~ 30    │ 위험 🔴    │ "위험"                │
 * │ 31 ~ 60   │ 주의 🟠    │ "주의"                │
 * │ 61 ~ 100  │ 낮음 🟢    │ "위험 신호 적음"       │
 * └───────────┴────────────┴──────────────────────┘
 *
 * 핵심 원칙:
 * - '안전' 확정 표현 절대 금지 → "위험 신호 적음"만 허용
 * - 검색 결과 없음 = 안전 처리 금지 → 미확인/주의 상태 처리
 * - 사용자 학습(차단 이력) → 가중치 반영
 * - 신뢰도(confidence)가 낮을수록 중앙(50)으로 수렴
 */
object TrustScoreCalculator {

    /**
     * DecisionResult에서 0~100 신뢰도 점수를 계산한다.
     *
     * @param result 판정 결과
     * @param userBlockCount 사용자가 이 번호를 차단한 누적 횟수 (학습 반영)
     * @return 0~100 정수 (0=가장 위험, 100=위험 신호 가장 적음)
     */
    fun calculate(
        result: DecisionResult,
        userBlockCount: Int = 0,
    ): Int {
        // 1. RiskLevel → 기본 점수 대역
        val baseScore = when (result.riskLevel) {
            RiskLevel.HIGH -> 15      // 0~30 대역 중심
            RiskLevel.MEDIUM -> 45    // 31~60 대역 중심
            RiskLevel.LOW -> 75       // 61~100 대역 중심
            RiskLevel.UNKNOWN -> 50   // 미확인 → 주의 대역 중심
        }

        // 2. confidence로 대역 내 미세 조정
        // confidence 높으면 → 해당 대역 극단으로 이동
        // confidence 낮으면 → 50(중앙)으로 수렴
        val confidenceModifier = calculateConfidenceModifier(result)

        // 3. 검색 증거 기반 조정
        val searchModifier = calculateSearchModifier(result.searchEvidence)

        // 4. 사용자 학습 가중치
        val learningModifier = calculateLearningModifier(userBlockCount)

        // 5. 최종 계산
        val rawScore = baseScore + confidenceModifier + searchModifier + learningModifier
        return rawScore.coerceIn(0, 100)
    }

    /**
     * 점수에서 등급 문자열을 반환한다.
     * '안전' 표현은 절대 사용하지 않는다.
     */
    fun gradeLabel(score: Int): String = when {
        score <= 30 -> "위험"
        score <= 60 -> "주의"
        else -> "위험 신호 적음"
    }

    /**
     * 점수에서 등급 색상 (ARGB)을 반환한다.
     */
    fun gradeColor(score: Int): Int = when {
        score <= 30 -> 0xFFC62828.toInt()   // 빨강
        score <= 60 -> 0xFFE65100.toInt()   // 주황
        else -> 0xFF2E7D32.toInt()          // 초록
    }

    /**
     * 검색 결과 없음 여부를 판단한다.
     * 결과 없음 = 안전 처리 금지 → '미확인' 상태
     */
    fun isUnverified(result: DecisionResult): Boolean {
        val se = result.searchEvidence ?: return true
        return se.isEmpty
    }

    // ═══════════════════════════════════════════════
    // Internal: 세부 조정자
    // ═══════════════════════════════════════════════

    private fun calculateConfidenceModifier(result: DecisionResult): Int {
        val conf = result.confidence
        // confidence 범위: 0.0 ~ 1.0
        // 높은 confidence → 해당 방향으로 더 극단적
        val direction = when (result.riskLevel) {
            RiskLevel.HIGH -> -1     // 위험할수록 낮은 점수
            RiskLevel.MEDIUM -> 0    // 중간은 방향 없음
            RiskLevel.LOW -> 1       // 안전할수록 높은 점수
            RiskLevel.UNKNOWN -> 0
        }
        // 최대 ±12점 조정
        return (direction * conf * 12).toInt()
    }

    private fun calculateSearchModifier(searchEvidence: SearchEvidence?): Int {
        if (searchEvidence == null || searchEvidence.isEmpty) {
            // 검색 결과 없음 → 안전 처리 금지 → -5 (주의 쪽으로)
            return -5
        }

        var modifier = 0

        // 시그널 기반 조정
        for (signal in searchEvidence.signalSummaries) {
            when (signal.signalType) {
                "SCAM" -> modifier -= when {
                    signal.resultCount >= 5 -> 15
                    signal.resultCount >= 2 -> 10
                    else -> 5
                }
                "SPAM" -> modifier -= when {
                    signal.resultCount >= 3 -> 8
                    else -> 4
                }
                "SPAM_REPORT" -> modifier -= when {
                    signal.resultCount >= 3 -> 6
                    else -> 3
                }
                "INSTITUTION" -> modifier += 5
                "BUSINESS" -> modifier += 4
                "DELIVERY" -> modifier += 3
            }
        }

        return modifier.coerceIn(-20, 10)
    }

    private fun calculateLearningModifier(userBlockCount: Int): Int {
        // 사용자가 이전에 차단한 횟수 → 가중치
        return when {
            userBlockCount >= 3 -> -10   // 반복 차단 = 강한 부정 신호
            userBlockCount >= 1 -> -5    // 차단 이력 있음
            else -> 0
        }
    }
}
