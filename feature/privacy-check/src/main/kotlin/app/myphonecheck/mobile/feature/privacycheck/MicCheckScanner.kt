package app.myphonecheck.mobile.feature.privacycheck

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build
import android.util.Log
import app.myphonecheck.mobile.core.model.PrivacyEvidence

/**
 * MicCheck 스캐너 — 마이크 권한 앱 + 최근 사용 앱 수집.
 *
 * 데이터 소스:
 * - PackageManager → 마이크 권한 보유 앱
 * - AppOpsManager → 마이크 접근 허용 앱
 * - UsageStatsManager → 최근 사용 시간
 *
 * 온디바이스 전용, 네트워크 전송 없음.
 */
class MicCheckScanner(private val context: Context) {

    private val scannerEngine = PrivacyScannerEngine(context)

    /**
     * 마이크 센서 전체 스캔을 수행합니다.
     *
     * @return SensorCheckState (마이크)
     */
    fun scan(): SensorCheckState {
        Log.i(TAG, "SCAN_START sensor=MICROPHONE")
        val startMs = System.currentTimeMillis()

        return try {
            val allHolders = scannerEngine.scanPermissionHolders()
            val micHolders = allHolders.filter { it.hasMicrophone }

            val allowedApps = scannerEngine.queryAllowedApps(
                PrivacyEvidence.SensorType.MICROPHONE,
                micHolders,
            )

            val recentApps = queryRecentMicApps(micHolders)

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
            Log.i(TAG, "SCAN_COMPLETE sensor=MICROPHONE" +
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
            Log.e(TAG, "SCAN_FAIL sensor=MICROPHONE error=${e.message}", e)
            SensorCheckState(scanStatus = ScanStatus.SCANNED)
        }
    }

    private fun queryRecentMicApps(
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

                    val mode = appOpsManager.unsafeCheckOpNoThrow(
                        AppOpsManager.OPSTR_RECORD_AUDIO, uid, holder.packageName,
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
                    Log.w(TAG, "Error querying mic for ${holder.packageName}", e)
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
        const val TAG = "MPC_MIC_SCAN"
        const val RECENT_THRESHOLD_MS = 24 * 60 * 60 * 1000L
    }
}
