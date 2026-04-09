package app.myphonecheck.mobile.core.model

/**
 * 인터셉트 성능 계측 데이터.
 *
 * 자비스 요구: "좋아 보인다가 아니라 몇 ms냐로 가야 합니다."
 *
 * 측정 항목:
 * - Phase 1 표시까지 ms
 * - Phase 2 확정까지 ms
 * - 캐시 hit 비율 (Tier 0/1/2 각각)
 * - FULL 경로 진입 비율
 * - 배터리 소모량 (mAh 추정)
 * - 메모리 사용량 (MB)
 *
 * 100% 온디바이스. 서버 전송 없음.
 */
data class InterceptMetrics(
    /** 이번 인터셉트의 식별자 (UUID) */
    val interceptId: String,

    /** E.164 번호 (해싱 후 저장 권장) */
    val numberHash: String,

    /** 사용된 InterceptRoute */
    val route: InterceptRoute,

    /** Phase 1 완료까지 소요 시간 (ms) */
    val phase1LatencyMs: Long,

    /** Phase 2 완료까지 소요 시간 (ms). -1 = Phase 2 없음 */
    val phase2LatencyMs: Long = -1L,

    /** 캐시 히트 레벨: TIER_0, TIER_1, MISS */
    val cacheHitLevel: CacheHitLevel,

    /** Phase 소스 */
    val phaseSource: PhaseSource,

    /** 국가별 정책 risk boost 적용량 */
    val countryRiskBoost: Float = 0f,

    /** Phase 1 → Phase 2 불일치 발생 여부 */
    val phaseConflict: Boolean = false,

    /** Phase 1 → Phase 2 위험도 변화 (Phase2.risk - Phase1.risk) */
    val riskDelta: Float = 0f,

    /** 네트워크 상태 */
    val networkAvailable: Boolean = true,

    /** 파이프라인 시작 시각 (epoch ms) */
    val timestampMs: Long = System.currentTimeMillis(),
)

/**
 * 캐시 히트 레벨.
 */
enum class CacheHitLevel {
    /** Tier 0 PreJudge Room DB 히트 */
    TIER_0,
    /** Tier 1 인메모리 캐시 히트 */
    TIER_1,
    /** 캐시 미스 — 풀 파이프라인 진입 */
    MISS,
}

/**
 * 누적 성능 통계.
 *
 * 일정 기간의 인터셉트 성능을 집계한 결과.
 * Settings 화면 또는 디버그 패널에서 표시.
 */
data class InterceptPerformanceStats(
    /** 총 인터셉트 횟수 */
    val totalInterceptCount: Int = 0,

    /** Route별 분포 */
    val routeDistribution: Map<InterceptRoute, Int> = emptyMap(),

    /** 캐시 히트율 (0.0 ~ 1.0) */
    val cacheHitRate: Float = 0f,
    /** Tier 0 히트율 */
    val tier0HitRate: Float = 0f,
    /** Tier 1 히트율 */
    val tier1HitRate: Float = 0f,

    /** Phase 1 평균 지연시간 (ms) */
    val avgPhase1LatencyMs: Long = 0L,
    /** Phase 1 P95 지연시간 (ms) */
    val p95Phase1LatencyMs: Long = 0L,
    /** Phase 1 최대 지연시간 (ms) */
    val maxPhase1LatencyMs: Long = 0L,

    /** Phase 2 평균 지연시간 (ms) */
    val avgPhase2LatencyMs: Long = 0L,
    /** Phase 2 P95 지연시간 (ms) */
    val p95Phase2LatencyMs: Long = 0L,

    /** FULL 경로 진입 비율 */
    val fullRouteRate: Float = 0f,

    /** Phase 불일치 비율 */
    val phaseConflictRate: Float = 0f,

    /** 통계 기간 시작 (epoch ms) */
    val periodStartMs: Long = 0L,
    /** 통계 기간 끝 (epoch ms) */
    val periodEndMs: Long = 0L,
)
