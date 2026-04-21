package app.myphonecheck.mobile.feature.privacycheck

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build
import android.util.Log
import app.myphonecheck.mobile.core.model.PrivacyEvidence

/**
 * CameraCheck 스캐너 — 카메라 권한 앱 + 최근 사용 앱 수집.
 *
 * 데이터 소스:
 * - PackageManager → 카메라 권한 보유 앱
 * - AppOpsManager → 카메라 접근 허용 앱
 * - UsageStatsManager → 최근 사용 시간
 *
 * 온디바이스 전용, 네트워크 전송 없음.
 */
class CameraCheckScanner(private val context: Context) {

    private val scannerEngine = PrivacyScannerEngine(context)

    /**
     * 카메라 센서 전체 스캔을 수행합니다.
     *
     * @return SensorCheckState (카메라)
     */
    fun scan(): SensorCheckState {
        Log.i(TAG, "SCAN_START sensor=CAMERA")
        val startMs = System.currentTimeMillis()

        return try {
            // 1. 권한 보유 앱 목록
            val allHolders = scannerEngine.scanPermissionHolders()
            val cameraHolders = allHolders.filter { it.hasCamera }

            // 2. 카메라 접근 허용된 앱
            val allowedApps = scannerEngine.queryAllowedApps(
                PrivacyEvidence.SensorType.CAMERA,
                cameraHolders,
            )

            // 3. 최근 사용 앱 (AppOpsManager 기반)
            val recentApps = queryRecentCameraApps(cameraHolders)

            // 4. 마지막 사용 시각
            val lastUsedAt = recentApps.maxOfOrNull { it.lastUsedAt ?: 0L }
                ?.takeIf { it > 0L }

            val grantedApps = allowedApps.map { app ->
                SensorAppInfo(
                    packageName = app.packageName,
                    appLabel = app.appLabel,
                    isKnownSafe = app.isKnownSafe,
                    daysSinceInstall = app.daysSinceInstall,
                    lastUsedAt = recentApps.find { it.packageName == app.packageName }?.lastUsedAt,
                )
            }

            val elapsedMs = System.currentTimeMillis() - startMs
            Log.i(TAG, "SCAN_COMPLETE sensor=CAMERA" +
                " granted=${grantedApps.size}" +
                " recent=${recentApps.size}" +
                " lastUsed=$lastUsedAt" +
                " elapsed=${elapsedMs}ms")

            SensorCheckState(
                scanStatus = ScanStatus.SCANNED,
                grantedApps = grantedApps,
                recentApps = recentApps,
                lastUsedAt = lastUsedAt,
            )
        } catch (e: Exception) {
            Log.e(TAG, "SCAN_FAIL sensor=CAMERA error=${e.message}", e)
            SensorCheckState(scanStatus = ScanStatus.SCANNED)
        }
    }

    private fun queryRecentCameraApps(
        holders: List<PrivacyScannerEngine.PermissionHolder>,
    ): List<SensorAppInfo> {
        val results = mutableListOf<SensorAppInfo>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE)
                as? AppOpsManager ?: return emptyList()

            for (holder in holders) {
                try {
                    val uid = context.packageManager
                        .getApplicationInfo(holder.packageName, 0).uid

                    // noteOpNoThrow 대신 접근 허용 상태 + UsageStats로 최근 사용 판별
                    val mode = appOpsManager.unsafeCheckOpNoThrow(
                        AppOpsManager.OPSTR_CAMERA, uid, holder.packageName,
                    )
                    if (mode == AppOpsManager.MODE_ALLOWED) {
                        val lastUsed = getLastUsageTime(holder.packageName)
                        if (lastUsed != null && lastUsed > System.currentTimeMillis() - RECENT_THRESHOLD_MS) {
                            results.add(
                                SensorAppInfo(
                                    packageName = holder.packageName,
                                    appLabel = holder.appLabel,
                                    isKnownSafe = holder.isKnownSafe,
                                    daysSinceInstall = holder.daysSinceInstall,
                                    lastUsedAt = lastUsed,
                                )
                            )
                        }
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Error querying camera for ${holder.packageName}", e)
                }
            }
        }

        return results
    }

    private fun getLastUsageTime(packageName: String): Long? {
        return try {
            val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE)
                as? UsageStatsManager ?: return null

            val endTime = System.currentTimeMillis()
            val startTime = endTime - RECENT_THRESHOLD_MS

            val stats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY, startTime, endTime,
            )

            stats?.find { it.packageName == packageName }
                ?.lastTimeUsed
                ?.takeIf { it > 0 }
        } catch (e: Exception) {
            null
        }
    }

    private companion object {
        const val TAG = "MPC_CAMERA_SCAN"
        /** 최근 사용 기준: 24시간 */
        const val RECENT_THRESHOLD_MS = 24 * 60 * 60 * 1000L
    }
}
