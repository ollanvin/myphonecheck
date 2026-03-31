package app.callcheck.mobile.core.model

/**
 * 실기기 벤치마크 리포트.
 *
 * 자비스 요구: "감각이 아니라 수치로 제품을 다뤄야 합니다."
 *
 * 수집 항목:
 * - Phase 1/2 latency (P50, P95, P99, max)
 * - Cache hit rate (Tier 0 / Tier 1 / Miss)
 * - Route 분포
 * - 배터리 소모 (mAh / 100 intercepts)
 * - 메모리 peak (RSS, heap)
 * - 국가 정책 검증 매트릭스 통과율
 *
 * 100% 온디바이스. 서버 전송 없음.
 */
data class BenchmarkReport(
    /** 기기 정보 */
    val deviceInfo: DeviceProfile,

    /** 성능 수치 */
    val latencyProfile: LatencyProfile,

    /** 리소스 수치 */
    val resourceProfile: ResourceProfile,

    /** 국가 검증 결과 */
    val countryValidation: CountryValidationSummary,

    /** 리포트 생성 시각 (epoch ms) */
    val generatedAtMs: Long = System.currentTimeMillis(),

    /** 총 벤치마크 건수 */
    val totalSamples: Int = 0,
)

/**
 * 기기 프로파일.
 * 성능 수치 비교의 기준점.
 */
data class DeviceProfile(
    /** 기기 제조사 */
    val manufacturer: String,
    /** 기기 모델 */
    val model: String,
    /** Android 버전 */
    val androidVersion: String,
    /** SDK 레벨 */
    val sdkLevel: Int,
    /** CPU 코어 수 */
    val cpuCores: Int,
    /** 총 RAM (MB) */
    val totalRamMb: Long,
    /** 배터리 용량 (mAh, 0 if unknown) */
    val batteryCapacityMah: Int = 0,
    /** 벤치마크 시작 배터리 레벨 (0~100) */
    val batteryLevelAtStart: Int = 0,
    /** 벤치마크 종료 배터리 레벨 */
    val batteryLevelAtEnd: Int = 0,
)

/**
 * 지연시간 프로파일.
 *
 * 백분위수(percentile) 기반:
 * P50 = 절반은 이보다 빠름
 * P95 = 95%가 이보다 빠름 (SLA 기준)
 * P99 = 99%가 이보다 빠름 (tail latency)
 * max = 최악 케이스
 */
data class LatencyProfile(
    // ── Phase 1 ──
    val phase1P50Ms: Long = 0L,
    val phase1P95Ms: Long = 0L,
    val phase1P99Ms: Long = 0L,
    val phase1MaxMs: Long = 0L,

    // ── Phase 2 ──
    val phase2P50Ms: Long = 0L,
    val phase2P95Ms: Long = 0L,
    val phase2P99Ms: Long = 0L,
    val phase2MaxMs: Long = 0L,

    // ── End-to-End (Phase 1 또는 Phase 2까지 전체) ──
    val e2eP50Ms: Long = 0L,
    val e2eP95Ms: Long = 0L,
    val e2eP99Ms: Long = 0L,
    val e2eMaxMs: Long = 0L,

    // ── Cache ──
    val cacheHitRate: Float = 0f,
    val tier0HitRate: Float = 0f,
    val tier1HitRate: Float = 0f,

    // ── Route 분포 ──
    val skipRate: Float = 0f,
    val instantRate: Float = 0f,
    val lightRate: Float = 0f,
    val fullRate: Float = 0f,

    // ── 품질 ──
    val phaseConflictRate: Float = 0f,
    val avgRiskDelta: Float = 0f,

    /** Phase 1 SLA 충족율 (50ms 이내 비율) */
    val phase1SlaRate: Float = 0f,
    /** Phase 2 SLA 충족율 (4500ms 이내 비율) */
    val phase2SlaRate: Float = 0f,
)

/**
 * 리소스 사용 프로파일.
 */
data class ResourceProfile(
    // ── 메모리 ──
    /** 벤치마크 시작 시 heap 사용량 (MB) */
    val heapUsedStartMb: Float = 0f,
    /** 벤치마크 종료 시 heap 사용량 (MB) */
    val heapUsedEndMb: Float = 0f,
    /** 벤치마크 중 heap peak (MB) */
    val heapPeakMb: Float = 0f,
    /** 인터셉트 1건당 추가 메모리 (KB) */
    val memoryPerInterceptKb: Float = 0f,

    // ── 배터리 ──
    /** 벤치마크 소요 시간 (ms) */
    val benchmarkDurationMs: Long = 0L,
    /** 추정 배터리 소모 (mAh per 100 intercepts) */
    val batteryPer100Intercepts: Float = 0f,
    /** 시간당 추정 배터리 소모 (%) */
    val batteryDrainPerHour: Float = 0f,

    // ── CPU ──
    /** 벤치마크 중 평균 CPU 점유율 (%, 0~100) */
    val avgCpuUsage: Float = 0f,
    /** 벤치마크 중 peak CPU 점유율 */
    val peakCpuUsage: Float = 0f,
)

/**
 * 30개국 검증 결과 요약.
 */
data class CountryValidationSummary(
    /** 검증 대상 국가 수 */
    val totalCountries: Int = 0,
    /** 통과 국가 수 */
    val passedCountries: Int = 0,
    /** 실패 국가 수 */
    val failedCountries: Int = 0,
    /** 국가별 상세 결과 */
    val countryResults: List<CountryTestResult> = emptyList(),
    /** 전체 테스트 케이스 수 */
    val totalTestCases: Int = 0,
    /** 통과한 테스트 케이스 수 */
    val passedTestCases: Int = 0,
)

/**
 * 개별 국가 테스트 결과.
 */
data class CountryTestResult(
    val countryCode: String,
    val countryName: String,
    /** 이 국가의 테스트 케이스 수 */
    val totalCases: Int,
    /** 통과한 케이스 수 */
    val passedCases: Int,
    /** 실패 상세 */
    val failures: List<CaseFailure> = emptyList(),
)

/**
 * 개별 케이스 실패 상세.
 */
data class CaseFailure(
    val caseType: String,
    val input: String,
    val expected: String,
    val actual: String,
    val reason: String,
)
