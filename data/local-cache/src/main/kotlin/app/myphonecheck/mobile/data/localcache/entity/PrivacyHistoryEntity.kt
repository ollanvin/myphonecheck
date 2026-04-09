package app.myphonecheck.mobile.data.localcache.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * PrivacyCheck 카메라/마이크 접근 히스토리 엔티티.
 *
 * ┌──────────────────────────────────────────────────────────────┐
 * │ 핵심 설계 원칙:                                              │
 * │  • Android 12+ AppOpsManager 기반 센서 접근 기록               │
 * │  • 이상 탐지 결과 + 사용자 확인 상태 저장                      │
 * │  • 서버 전송 절대 없음 — 온디바이스 전용                      │
 * │  • 시각은 epoch millis — 디바이스 TimeZone 기준               │
 * └──────────────────────────────────────────────────────────────┘
 *
 * 인덱스:
 *  - app_package: 앱별 필터링
 *  - permission_type: 권한 유형별 필터링
 *  - used_at: 최신순 정렬
 *  - is_anomaly: 이상 탐지 빠른 조회
 */
@Entity(
    tableName = "privacy_history",
    indices = [
        Index(value = ["app_package"]),
        Index(value = ["permission_type"]),
        Index(value = ["used_at"]),
        Index(value = ["is_anomaly"]),
    ]
)
data class PrivacyHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** 앱 패키지명 */
    @ColumnInfo(name = "app_package")
    val appPackage: String,

    /** 앱 표시 이름 */
    @ColumnInfo(name = "app_label")
    val appLabel: String,

    /** 권한 유형: "CAMERA" / "MICROPHONE" */
    @ColumnInfo(name = "permission_type")
    val permissionType: String,

    /** 센서 사용 시각 (epoch millis — 디바이스 TimeZone 기준) */
    @ColumnInfo(name = "used_at")
    val usedAt: Long,

    /** 센서 사용 지속 시간 (초) */
    @ColumnInfo(name = "duration_sec")
    val durationSec: Long,

    /** 이상 탐지 여부 */
    @ColumnInfo(name = "is_anomaly")
    val isAnomaly: Boolean,

    /** 사용자 확인 상태: "CONFIRMED" / "DENIED" / "UNVERIFIED" */
    @ColumnInfo(name = "user_verified")
    val userVerified: String = "UNVERIFIED",
)
