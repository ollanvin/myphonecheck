package app.myphonecheck.mobile.core.model

/**
 * 프라이버시(센서 접근) 분석 증거.
 *
 * 카메라/마이크 권한을 보유한 앱의 상태를 정규화한 모델.
 *
 * 판단 기준:
 * - 권한 보유: 카메라/마이크 권한이 있는 앱인지
 * - 최초 사용: 이전에 사용한 적 없는 앱인지
 * - 백그라운드 접근: 포그라운드 없이 센서에 접근하는지
 * - 빈도: 최근 사용 빈도가 비정상적인지
 */
data class PrivacyEvidence(
    /** 앱 패키지명 */
    val packageName: String,

    /** 앱 표시 이름 */
    val appLabel: String,

    /** 감지된 센서 타입 */
    val sensorType: SensorType,

    /** 최초 접근 여부 (이 앱이 이 센서를 처음 사용) */
    val isFirstAccess: Boolean,

    /** 백그라운드 접근 여부 */
    val isBackgroundAccess: Boolean,

    /** 최근 24시간 접근 횟수 */
    val accessCountLast24h: Int,

    /** 최근 7일 접근 횟수 */
    val accessCountLast7d: Int,

    /** 현재 접근 중 여부 */
    val isCurrentlyActive: Boolean,

    /** 앱 설치 후 경과일 */
    val daysSinceInstall: Int,

    /** 감지 시각 (epoch millis) */
    val detectedAtMillis: Long,
) {
    /** 센서 타입 */
    enum class SensorType(
        val displayNameEn: String,
        val displayNameKo: String,
    ) {
        CAMERA("Camera", "카메라"),
        MICROPHONE("Microphone", "마이크"),
    }

    companion object {
        fun empty(packageName: String, sensorType: SensorType) = PrivacyEvidence(
            packageName = packageName,
            appLabel = "",
            sensorType = sensorType,
            isFirstAccess = false,
            isBackgroundAccess = false,
            accessCountLast24h = 0,
            accessCountLast7d = 0,
            isCurrentlyActive = false,
            daysSinceInstall = 0,
            detectedAtMillis = System.currentTimeMillis(),
        )
    }

    /** 의심 점수 (0.0~1.0) */
    val suspicionScore: Float
        get() {
            var score = 0f
            // 최초 접근
            if (isFirstAccess) score += 0.25f
            // 백그라운드 접근 (가장 의심스러움)
            if (isBackgroundAccess) score += 0.35f
            // 비정상 빈도 (24시간에 10회 이상)
            if (accessCountLast24h >= 10) score += 0.2f
            // 설치 직후 접근 (1일 이내)
            if (daysSinceInstall <= 1 && isFirstAccess) score += 0.15f
            return score.coerceIn(0f, 1f)
        }

    /** 정상 사용 가능성 */
    val isLikelyNormal: Boolean
        get() = !isFirstAccess && !isBackgroundAccess && accessCountLast24h < 5
}
