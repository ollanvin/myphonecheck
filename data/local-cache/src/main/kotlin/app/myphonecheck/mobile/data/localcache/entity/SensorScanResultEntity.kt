package app.myphonecheck.mobile.data.localcache.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Initial Scan 결과 영속 저장 엔티티.
 *
 * 센서별(CAMERA/MICROPHONE) 스캔 결과를 저장합니다.
 * - 앱 설치 직후 → Initial Scan → 결과 저장
 * - 앱 재실행 시 → Room에서 즉시 복원 → 백그라운드 갱신
 *
 * sensor_type은 UNIQUE — 센서당 최신 스캔 결과 1건만 유지.
 * 온디바이스 전용, 서버 전송 없음.
 */
@Entity(
    tableName = "sensor_scan_result",
    indices = [
        Index(value = ["sensor_type"], unique = true),
    ],
)
data class SensorScanResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** 센서 유형: "CAMERA" / "MICROPHONE" */
    @ColumnInfo(name = "sensor_type")
    val sensorType: String,

    /** 권한 보유 앱 수 */
    @ColumnInfo(name = "granted_app_count")
    val grantedAppCount: Int,

    /** 최근 사용 앱 수 */
    @ColumnInfo(name = "recent_app_count")
    val recentAppCount: Int,

    /** 마지막 센서 사용 시각 (epoch ms, null = 기록 없음) */
    @ColumnInfo(name = "last_used_at")
    val lastUsedAt: Long? = null,

    /** 권한 보유 앱 목록 (JSON: [{"pkg":"...","label":"...","safe":true,"days":10,"lastUsed":null}]) */
    @ColumnInfo(name = "granted_apps_json")
    val grantedAppsJson: String,

    /** 최근 사용 앱 목록 (JSON: 동일 포맷) */
    @ColumnInfo(name = "recent_apps_json")
    val recentAppsJson: String,

    /** 스캔 완료 시각 (epoch ms) */
    @ColumnInfo(name = "scanned_at")
    val scannedAt: Long,

    /** 상태 레벨: "NORMAL" / "CAUTION" / "UNKNOWN" */
    @ColumnInfo(name = "status_level")
    val statusLevel: String,
)
