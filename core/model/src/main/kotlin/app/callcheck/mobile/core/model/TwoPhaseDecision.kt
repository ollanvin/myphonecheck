package app.callcheck.mobile.core.model

/**
 * 2-Phase 판단 결과.
 *
 * Phase 1 (PreJudge Score): 전화가 울리는 순간 0~50ms 안에 보여주는 즉시 판단
 * Phase 2 (Final Decision Score): 병렬 엔진 완료 후 150~400ms에 확정하는 최종 판단
 *
 * UX 시나리오:
 * - 0~50ms: Phase 1 표시 → "주의 필요" 임시 링
 * - 150~400ms: Phase 2 확정 → "보이스피싱 위험 높음" 확정 링
 *
 * 이 구조로 "가로채는 느낌"이 훨씬 강해짐:
 * - 사용자가 전화를 받으려는 순간 이미 판단이 표시됨
 * - 풀 파이프라인 결과가 오면 업데이트됨
 *
 * Phase 전환 규칙:
 * - Phase 1만 있으면: Phase 1을 최종으로 사용 (Tier 0 캐시만 hit)
 * - Phase 2가 오면: Phase 2가 최종 (Phase 1은 히스토리용)
 * - Phase 1과 Phase 2가 불일치: Phase 2 우선, 불일치 기록
 *
 * 100% 온디바이스. 서버 전송 없음.
 */
data class TwoPhaseDecision(
    /** Phase 1: 즉시 판단 (0~50ms). PreJudge 캐시 또는 경량 분석 결과. */
    val phase1: PhaseResult,

    /** Phase 2: 확정 판단 (150~4500ms). 풀 파이프라인 결과. null이면 Phase 1만 완료. */
    val phase2: PhaseResult? = null,

    /** Phase 전환 메타데이터 */
    val phaseMeta: PhaseMeta = PhaseMeta(),
) {
    /** 최종 DecisionResult — Phase 2가 있으면 Phase 2, 없으면 Phase 1 */
    fun finalResult(): DecisionResult = phase2?.toDecisionResult() ?: phase1.toDecisionResult()

    /** Phase 1과 Phase 2의 판단이 불일치하는지 */
    fun hasPhaseConflict(): Boolean {
        if (phase2 == null) return false
        return phase1.action != phase2.action
    }

    /** Phase 1 → Phase 2로 위험도가 상승했는지 */
    fun riskEscalated(): Boolean {
        if (phase2 == null) return false
        return phase2.riskScore > phase1.riskScore + 0.15f
    }

    /** Phase 1 → Phase 2로 위험도가 하락했는지 */
    fun riskDeescalated(): Boolean {
        if (phase2 == null) return false
        return phase1.riskScore > phase2.riskScore + 0.15f
    }
}

/**
 * 개별 Phase 판단 결과.
 */
data class PhaseResult(
    val action: ActionRecommendation,
    val riskScore: Float,
    val category: ConclusionCategory,
    val confidence: Float,
    val summary: String,
    val riskLevel: RiskLevel,
    /** 이 Phase가 어디서 왔는지 */
    val source: PhaseSource,
    /** 판단 완료 시각 (epoch ms) */
    val completedAtMs: Long = System.currentTimeMillis(),
) {
    fun toDecisionResult(): DecisionResult = DecisionResult(
        riskLevel = riskLevel,
        category = category,
        action = action,
        confidence = confidence,
        summary = summary,
        reasons = emptyList(),
        deviceEvidence = null,
        searchEvidence = null,
    )
}

/**
 * Phase 판단의 출처.
 */
enum class PhaseSource {
    /** Tier 0 PreJudge 영속 캐시 */
    PRE_JUDGE_CACHE,
    /** Tier 1 인메모리 캐시 */
    MEMORY_CACHE,
    /** Device evidence만으로 경량 판단 */
    DEVICE_ONLY,
    /** 풀 파이프라인 (Device + Search + LocalLearning + BehaviorPattern) */
    FULL_PIPELINE,
    /** 국가 정책 기반 기본 판단 */
    COUNTRY_POLICY,
    /** Fallback (에러/타임아웃) */
    FALLBACK,
}

/**
 * Phase 전환 메타데이터.
 */
data class PhaseMeta(
    /** 파이프라인 시작 시각 */
    val pipelineStartMs: Long = System.currentTimeMillis(),
    /** Phase 1 완료까지 소요 시간 (ms) */
    val phase1LatencyMs: Long = 0L,
    /** Phase 2 완료까지 소요 시간 (ms). -1이면 미완료 */
    val phase2LatencyMs: Long = -1L,
    /** 사용된 InterceptRoute */
    val route: InterceptRoute? = null,
    /** Phase 불일치 발생 여부 */
    val conflictDetected: Boolean = false,
)
