package app.myphonecheck.mobile.core.model

/**
 * 인터셉트 우선순위 라우팅 결과.
 *
 * 전화 수신 순간, 풀 파이프라인을 돌리기 전에
 * "이 번호를 어느 깊이로 분석할 것인가"를 결정하는 분기 테이블.
 *
 * 설계 철학:
 * - 같은 전화라도 전부 같은 깊이로 분석하면 안 됨
 * - 캐시 hit + 반복 안전 → 즉시 SAFE 계열 (배터리 0)
 * - 첫 수신 + 심야 + 국제 → 즉시 고위험 경로 (풀 파이프라인)
 * - 긴급/비공개 → 완전 무개입 (latency 0ms)
 *
 * 결과: latency + 배터리를 동시에 최적화
 */
enum class InterceptRoute(
    val pipelineDepth: PipelineDepth,
    val maxLatencyMs: Long,
) {
    /**
     * 완전 스킵. 판정/알림/오버레이 없음.
     * - 긴급번호 (911, 112, 119 등)
     * - 비공개/차단 번호
     * - null/blank 번호
     */
    SKIP(PipelineDepth.NONE, 0L),

    /**
     * Tier 0 즉시 반환. 엔진 실행 없음.
     * - PreJudge 캐시 hit + usable confidence
     * - 저장된 연락처 (isSavedContact)
     * - 반복 안전 패턴 (수신 3회+ answered)
     */
    INSTANT(PipelineDepth.CACHE_ONLY, 5L),

    /**
     * 경량 판단. Device evidence만 수집.
     * - Tier 0 miss + 국내 번호 + 주간 + 비반복
     * - "평범한" 첫 수신 전화
     */
    LIGHT(PipelineDepth.DEVICE_ONLY, 200L),

    /**
     * 풀 파이프라인. Device + Search + LocalLearning + BehaviorPattern 병렬.
     * - 첫 수신 + 위험 신호 (심야/국제/VoIP/반복 미수신)
     * - Tier 0 hit이지만 confidence가 낮은 경우
     * - 위험 가중 국가별 정책
     */
    FULL(PipelineDepth.ALL_AXES, 4500L),
}

/**
 * 파이프라인 깊이.
 * InterceptRoute와 1:1 대응.
 */
enum class PipelineDepth {
    /** 아무것도 안 함 */
    NONE,
    /** Tier 0/1 캐시만 조회 */
    CACHE_ONLY,
    /** Device evidence만 수집 (네트워크 0) */
    DEVICE_ONLY,
    /** Device + Search + LocalLearning + BehaviorPattern 전체 */
    ALL_AXES,
}
