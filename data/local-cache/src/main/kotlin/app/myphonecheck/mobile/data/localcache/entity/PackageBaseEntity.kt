package app.myphonecheck.mobile.data.localcache.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Initial Scan 패키지 인벤토리 (Architecture v2.0.0 §28).
 *
 * 민감 권한(RECORD_AUDIO, CAMERA, READ_SMS, READ_CALL_LOG, ...)을 보유한 앱만 기록.
 * MicCheck/CameraCheck/CardCheck/PushCheck 베이스로 사용.
 */
@Entity(tableName = "package_base_entry")
data class PackageBaseEntity(
    @PrimaryKey
    val packageName: String,
    val appLabel: String,
    val sensitivePermissionsCsv: String,
    val installedAtMillis: Long,
    val firstScannedMillis: Long,
)
