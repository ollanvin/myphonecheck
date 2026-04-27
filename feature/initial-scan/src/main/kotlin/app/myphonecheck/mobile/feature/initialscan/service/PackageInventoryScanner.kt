package app.myphonecheck.mobile.feature.initialscan.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import app.myphonecheck.mobile.data.localcache.entity.PackageBaseEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 권한 보유 앱 인벤토리 스캐너 (Architecture v2.0.0 §28).
 *
 * 민감 권한(RECORD_AUDIO, CAMERA, READ_SMS, READ_CALL_LOG, READ_PHONE_STATE,
 * RECEIVE_SMS, WRITE_SMS) 보유 앱만 인벤토리에 기록 → MicCheck/CameraCheck/CardCheck/PushCheck 베이스.
 */
@Singleton
class PackageInventoryScanner @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val pm: PackageManager get() = context.packageManager

    suspend fun scan(): List<PackageBaseEntity> = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val packages = try {
            pm.getInstalledPackages(PackageManager.GET_PERMISSIONS)
        } catch (e: Exception) {
            emptyList<PackageInfo>()
        }
        packages.mapNotNull { pkg ->
            val requested = pkg.requestedPermissions ?: return@mapNotNull null
            val sensitive = requested.filter { it in SENSITIVE_PERMISSIONS }
            if (sensitive.isEmpty()) return@mapNotNull null
            PackageBaseEntity(
                packageName = pkg.packageName.orEmpty(),
                appLabel = runCatching { pm.getApplicationLabel(pkg.applicationInfo!!).toString() }
                    .getOrDefault(pkg.packageName.orEmpty()),
                sensitivePermissionsCsv = sensitive.joinToString(","),
                installedAtMillis = pkg.firstInstallTime,
                firstScannedMillis = now,
            )
        }
    }

    companion object {
        val SENSITIVE_PERMISSIONS: Set<String> = setOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CONTACTS,
        )
    }
}
