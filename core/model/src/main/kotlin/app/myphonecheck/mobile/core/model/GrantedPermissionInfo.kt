package app.myphonecheck.mobile.core.model

/**
 * "허용된 앱" 탭에 표시할 앱별 권한 현황.
 *
 * 하나의 앱이 보유한 위험 권한 목록을 집계한 결과.
 * UI에서 앱 이름, 아이콘, 허용된 권한 칩, 설치일수, 위험도를 한눈에 표시.
 *
 * 온디바이스 전용 — PackageManager + AppOpsManager에서 수집.
 */
data class GrantedPermissionInfo(
    /** 앱 패키지명 */
    val packageName: String,
    /** 사용자 표시 앱 이름 */
    val appLabel: String,
    /** 이 앱에 허용된 위험 권한 목록 */
    val grantedPermissions: List<DangerousPermission>,
    /** 설치 후 경과 일수 */
    val daysSinceInstall: Int,
    /** 알려진 안전 앱 여부 */
    val isKnownSafe: Boolean,
    /** 시스템 앱 여부 */
    val isSystemApp: Boolean,
    /** 위험도 점수 (0~100, 높을수록 주의) — 권한 개수/조합 기반 */
    val riskScore: Int,
)

/**
 * Android 위험 권한 분류.
 *
 * 사용자가 이해할 수 있도록 카테고리별로 그룹화.
 * displayKey: Android Manifest 권한 문자열
 * groupKey: 권한 그룹 (UI 그룹화용)
 */
enum class DangerousPermission(
    val displayKey: String,
    val groupKey: String,
    val labelEn: String,
    val labelKo: String,
) {
    CAMERA(
        "android.permission.CAMERA",
        "camera", "Camera", "카메라",
    ),
    RECORD_AUDIO(
        "android.permission.RECORD_AUDIO",
        "microphone", "Microphone", "마이크",
    ),
    ACCESS_FINE_LOCATION(
        "android.permission.ACCESS_FINE_LOCATION",
        "location", "Precise Location", "정밀 위치",
    ),
    ACCESS_COARSE_LOCATION(
        "android.permission.ACCESS_COARSE_LOCATION",
        "location", "Approximate Location", "대략적 위치",
    ),
    READ_CONTACTS(
        "android.permission.READ_CONTACTS",
        "contacts", "Read Contacts", "연락처 읽기",
    ),
    READ_CALL_LOG(
        "android.permission.READ_CALL_LOG",
        "call_log", "Read Call Log", "통화 기록 읽기",
    ),
    READ_SMS(
        "android.permission.READ_SMS",
        "sms", "Read SMS", "문자 읽기",
    ),
    READ_PHONE_STATE(
        "android.permission.READ_PHONE_STATE",
        "phone", "Phone State", "전화 상태",
    ),
    READ_CALENDAR(
        "android.permission.READ_CALENDAR",
        "calendar", "Read Calendar", "캘린더 읽기",
    ),
    BODY_SENSORS(
        "android.permission.BODY_SENSORS",
        "sensors", "Body Sensors", "신체 센서",
    ),
    ;

    companion object {
        private val byDisplayKey = entries.associateBy { it.displayKey }

        /** Android Manifest 권한 문자열로 enum 조회. null이면 추적 대상이 아닌 권한. */
        fun fromManifestPermission(permission: String): DangerousPermission? {
            return byDisplayKey[permission]
        }
    }
}
