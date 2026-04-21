package app.myphonecheck.mobile.feature.privacycheck

/**
 * Camera/Mic 스캔 결과 상태 모델.
 *
 * UI 상태 규칙:
 * - NOT_SCANNED: 스캔 전 (카드에 "미스캔" 표시)
 * - SCANNING: 스캔 중 (카드에 "스캔중" 표시)
 * - SCANNED: 스캔 완료 (실데이터 표시)
 * - STALE: 이전 저장값 로드됨 — 유효하지만 갱신 권장
 * - FAILED: 스캔 실패 — 재시도 필요
 */
data class SensorCheckState(
    /** 스캔 상태 */
    val scanStatus: ScanStatus = ScanStatus.NOT_SCANNED,

    /** 해당 센서 권한을 보유한 앱 목록 */
    val grantedApps: List<SensorAppInfo> = emptyList(),

    /** 최근 해당 센서를 사용한 앱 목록 */
    val recentApps: List<SensorAppInfo> = emptyList(),

    /** 마지막 사용 시각 (epoch ms, null = 기록 없음) */
    val lastUsedAt: Long? = null,
) {
    /** 권한 앱 수 */
    val grantedAppCount: Int get() = grantedApps.size

    /** 최근 사용 앱 수 */
    val recentAppCount: Int get() = recentApps.size

    /** 최근 사용 여부 */
    val hasRecentUsage: Boolean get() = recentApps.isNotEmpty()

    /** 데이터가 유효한 상태인지 (SCANNED 또는 STALE) */
    val hasValidData: Boolean get() = scanStatus == ScanStatus.SCANNED || scanStatus == ScanStatus.STALE

    /** 상태 요약 레벨 */
    val statusLevel: StatusLevel get() = when {
        !hasValidData -> StatusLevel.UNKNOWN
        recentApps.isEmpty() -> StatusLevel.NORMAL
        recentApps.any { !it.isKnownSafe } -> StatusLevel.CAUTION
        else -> StatusLevel.NORMAL
    }
}

/** 스캔 상태 */
enum class ScanStatus {
    /** 스캔 전 — baseline 없음 */
    NOT_SCANNED,
    /** 스캔 진행 중 */
    SCANNING,
    /** 스캔 완료 — 실데이터 유효 */
    SCANNED,
    /** 이전 저장값 로드됨 — 유효하지만 최신이 아닐 수 있음 */
    STALE,
    /** 스캔 실패 — 재시도 필요 */
    FAILED,
}

/** 상태 요약 레벨 */
enum class StatusLevel {
    NORMAL,
    CAUTION,
    UNKNOWN,
}

/** 센서 접근 앱 정보 */
data class SensorAppInfo(
    val packageName: String,
    val appLabel: String,
    val isKnownSafe: Boolean,
    val daysSinceInstall: Int,
    /** 마지막 사용 시각 (epoch ms, null = 권한만 보유) */
    val lastUsedAt: Long? = null,
)
