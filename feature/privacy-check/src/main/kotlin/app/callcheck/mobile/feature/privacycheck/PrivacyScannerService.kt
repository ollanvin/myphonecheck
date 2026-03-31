package app.callcheck.mobile.feature.privacycheck

import android.app.AppOpsManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import app.callcheck.mobile.core.model.PrivacyEvidence
import java.util.concurrent.ConcurrentHashMap

/**
 * PrivacyCheck 센서 스캐너 서비스.
 *
 * AppOpsManager를 사용하여 카메라/마이크 접근을 온디바이스로 감시합니다.
 *
 * ═══════════════════════════════════════════════
 * 동작 방식:
 * 1. 주기적으로 AppOpsManager에서 카메라/마이크 op 조회
 * 2. 앱별 접근 이력 기록 (메모리)
 * 3. 백그라운드 접근 판별 (포그라운드 Activity 유무)
 * 4. PrivacyCheckEngine.evaluate() 호출
 * 5. 결과를 로컬 저장소에 기록
 * ═══════════════════════════════════════════════
 *
 * API 요구사항:
 * - Android 10+ (API 29): AppOpsManager.getPackagesForOps()
 * - Android 12+ (API 31): 실시간 카메라/마이크 인디케이터 연동 가능
 *
 * 원칙:
 * - 앱 권한을 자동으로 해제/변경하지 않음
 * - 외부 네트워크로 접근 이력을 전송하지 않음
 * - 사용자에게 판단 보조 정보만 제공
 */
class PrivacyScannerService(
    private val context: Context,
) {
    private companion object {
        private const val TAG = "PrivacyScannerService"

        /** 자체 앱 무시 */
        private const val SELF_PACKAGE = "app.callcheck.mobile"

        /** 시스템 앱 무시 대상 */
        private val SYSTEM_IGNORE = setOf(
            "android",
            "com.android.systemui",
            "com.android.camera",
            "com.android.camera2",
            "com.google.android.GoogleCamera",
        )

        /** 알려진 정상 카메라/마이크 앱 (오탐 감소용) */
        private val KNOWN_SAFE_APPS = setOf(
            // 기본 카메라
            "com.samsung.android.app.camera",
            "com.huawei.camera",
            "com.google.android.GoogleCamera",
            // 화상회의
            "us.zoom.videomeetings",
            "com.google.android.apps.meetings",
            "com.microsoft.teams",
            // 메신저 (영상통화)
            "com.kakao.talk",
            "org.telegram.messenger",
            "com.whatsapp",
            "jp.naver.line.android",
        )
    }

    /**
     * 앱별 센서 접근 통계 (온디바이스 메모리).
     * key = "$packageName:$sensorType"
     */
    private val accessStats = ConcurrentHashMap<String, SensorAccessStats>()

    /**
     * 특정 앱의 센서 접근을 분석합니다.
     *
     * @param packageName 대상 앱 패키지명
     * @param sensorType 센서 타입 (CAMERA / MICROPHONE)
     * @param isBackground 백그라운드 접근 여부
     * @return PrivacyEvidence 기반 DecisionResult
     */
    fun analyzeAccess(
        packageName: String,
        sensorType: PrivacyEvidence.SensorType,
        isBackground: Boolean,
    ): PrivacyEvidence? {
        // 자체 앱 및 시스템 앱 무시
        if (packageName == SELF_PACKAGE) return null
        if (packageName in SYSTEM_IGNORE) return null

        return try {
            buildEvidence(packageName, sensorType, isBackground)
        } catch (e: Exception) {
            Log.e(TAG, "Error analyzing $packageName access", e)
            null
        }
    }

    /**
     * 설치된 앱 중 카메라/마이크 권한 보유 앱 목록을 반환합니다.
     *
     * 주기적 스캔 시 사용합니다.
     */
    fun scanPermissionHolders(): List<PermissionHolder> {
        val holders = mutableListOf<PermissionHolder>()
        val pm = context.packageManager

        val installedPackages = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.getInstalledPackages(
                PackageManager.PackageInfoFlags.of(PackageManager.GET_PERMISSIONS.toLong())
            )
        } else {
            @Suppress("DEPRECATION")
            pm.getInstalledPackages(PackageManager.GET_PERMISSIONS)
        }

        for (packageInfo in installedPackages) {
            val pkg = packageInfo.packageName ?: continue
            if (pkg == SELF_PACKAGE || pkg in SYSTEM_IGNORE) continue

            val permissions = packageInfo.requestedPermissions ?: continue
            val hasCamera = permissions.contains(android.Manifest.permission.CAMERA)
            val hasMicrophone = permissions.contains(android.Manifest.permission.RECORD_AUDIO)

            if (hasCamera || hasMicrophone) {
                val appLabel = try {
                    pm.getApplicationLabel(
                        pm.getApplicationInfo(pkg, 0)
                    ).toString()
                } catch (e: Exception) {
                    pkg.substringAfterLast(".")
                }

                val installTime = packageInfo.firstInstallTime
                val daysSinceInstall = ((System.currentTimeMillis() - installTime) /
                    (24 * 60 * 60 * 1000L)).toInt().coerceAtLeast(0)

                holders.add(
                    PermissionHolder(
                        packageName = pkg,
                        appLabel = appLabel,
                        hasCamera = hasCamera,
                        hasMicrophone = hasMicrophone,
                        daysSinceInstall = daysSinceInstall,
                        isKnownSafe = pkg in KNOWN_SAFE_APPS,
                    )
                )
            }
        }

        return holders
    }

    /**
     * 권한 보유 앱들의 센서 접근 허용 상태를 조회합니다.
     *
     * AppOpsManager.unsafeCheckOpNoThrow()를 사용하여
     * 각 앱의 센서 op 허용 여부를 확인합니다.
     * (getPackagesForOps는 시스템 API이므로 사용 불가)
     *
     * @param sensorType 조회할 센서 타입
     * @param holders scanPermissionHolders()로 얻은 권한 보유 앱 목록
     * @return 센서 접근이 허용된 앱 목록
     */
    fun queryAllowedApps(
        sensorType: PrivacyEvidence.SensorType,
        holders: List<PermissionHolder>,
    ): List<AllowedApp> {
        val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE)
            as? AppOpsManager ?: return emptyList()

        val opStr = when (sensorType) {
            PrivacyEvidence.SensorType.CAMERA -> AppOpsManager.OPSTR_CAMERA
            PrivacyEvidence.SensorType.MICROPHONE -> AppOpsManager.OPSTR_RECORD_AUDIO
        }

        val results = mutableListOf<AllowedApp>()

        for (holder in holders) {
            try {
                val uid = context.packageManager
                    .getApplicationInfo(holder.packageName, 0).uid

                val mode = appOpsManager.unsafeCheckOpNoThrow(
                    opStr, uid, holder.packageName
                )

                if (mode == AppOpsManager.MODE_ALLOWED) {
                    results.add(
                        AllowedApp(
                            packageName = holder.packageName,
                            appLabel = holder.appLabel,
                            sensorType = sensorType,
                            isKnownSafe = holder.isKnownSafe,
                            daysSinceInstall = holder.daysSinceInstall,
                        )
                    )
                }
            } catch (e: Exception) {
                Log.w(TAG, "Error checking op for ${holder.packageName}", e)
            }
        }

        return results
    }

    private fun buildEvidence(
        packageName: String,
        sensorType: PrivacyEvidence.SensorType,
        isBackground: Boolean,
    ): PrivacyEvidence {
        val statsKey = "$packageName:${sensorType.name}"
        val stats = accessStats.getOrPut(statsKey) { SensorAccessStats() }
        val now = System.currentTimeMillis()

        val isFirst = stats.totalCount == 0
        stats.recordAccess(now)

        val pm = context.packageManager
        val appLabel = try {
            pm.getApplicationLabel(
                pm.getApplicationInfo(packageName, 0)
            ).toString()
        } catch (e: Exception) {
            packageName.substringAfterLast(".")
        }

        val daysSinceInstall = try {
            val installTime = pm.getPackageInfo(packageName, 0).firstInstallTime
            ((now - installTime) / (24 * 60 * 60 * 1000L)).toInt().coerceAtLeast(0)
        } catch (e: Exception) {
            -1
        }

        return PrivacyEvidence(
            packageName = packageName,
            appLabel = appLabel,
            sensorType = sensorType,
            isFirstAccess = isFirst,
            isBackgroundAccess = isBackground,
            accessCountLast24h = stats.countLast24h(),
            accessCountLast7d = stats.countLast7d(),
            isCurrentlyActive = true,
            daysSinceInstall = daysSinceInstall,
            detectedAtMillis = now,
        )
    }

    /**
     * 센서 접근 통계 (온디바이스 메모리 전용).
     *
     * 최근 7일간의 접근 타임스탬프를 유지합니다.
     * 앱 재시작 시 초기화되며, 영구 저장은 Room DB로 이관 예정.
     */
    internal class SensorAccessStats {
        private val timestamps = mutableListOf<Long>()
        var totalCount: Int = 0
            private set

        @Synchronized
        fun recordAccess(timestampMillis: Long) {
            timestamps.add(timestampMillis)
            totalCount++
            // 7일 이상 지난 기록 제거
            val cutoff = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000L
            timestamps.removeAll { it < cutoff }
        }

        @Synchronized
        fun countLast24h(): Int {
            val cutoff = System.currentTimeMillis() - 24 * 60 * 60 * 1000L
            return timestamps.count { it >= cutoff }
        }

        @Synchronized
        fun countLast7d(): Int = timestamps.size
    }

    /**
     * 권한 보유 앱 정보.
     */
    data class PermissionHolder(
        val packageName: String,
        val appLabel: String,
        val hasCamera: Boolean,
        val hasMicrophone: Boolean,
        val daysSinceInstall: Int,
        val isKnownSafe: Boolean,
    )

    /**
     * 센서 접근이 허용된 앱 정보.
     */
    data class AllowedApp(
        val packageName: String,
        val appLabel: String,
        val sensorType: PrivacyEvidence.SensorType,
        val isKnownSafe: Boolean,
        val daysSinceInstall: Int,
    )
}
