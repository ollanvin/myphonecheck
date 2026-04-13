package app.myphonecheck.mobile.feature.privacycheck

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import app.myphonecheck.mobile.core.model.DangerousPermission
import app.myphonecheck.mobile.core.model.GrantedPermissionInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "PermissionGrantedCollector"

/**
 * 전체 위험 권한 보유 앱을 수집하는 콜렉터.
 *
 * 기존 PrivacyScannerEngine.scanPermissionHolders()가 카메라/마이크만 다루는 것을
 * 확장하여, DangerousPermission enum에 정의된 전체 위험 권한을 커버합니다.
 *
 * 수집 방식:
 * 1. PackageManager.getInstalledPackages(GET_PERMISSIONS)로 전체 앱 스캔
 * 2. 각 앱의 requestedPermissions에서 DangerousPermission 매칭
 * 3. PackageManager.checkPermission()으로 실제 granted 여부 확인
 * 4. 앱별 GrantedPermissionInfo 생성 (위험도 점수 포함)
 *
 * 위험도 점수 계산:
 * - 기본: 허용된 위험 권한 수 × 10
 * - 가중: 카메라+마이크 동시 보유 +15, 위치+연락처 동시 +10
 * - 감경: 알려진 안전 앱 -20, 설치 365일+ -5
 * - 범위: 0~100
 *
 * 온디바이스 전용 — 네트워크 전송 없음.
 */
@Singleton
class PermissionGrantedCollector @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    companion object {
        /** 시스템 앱 무시 대상 (PrivacyScannerEngine과 동일) */
        private val SYSTEM_IGNORE = setOf(
            "android",
            "com.android.systemui",
            "com.android.camera",
            "com.android.camera2",
            "com.google.android.GoogleCamera",
        )

        /** 알려진 정상 앱 (오탐 감소) */
        private val KNOWN_SAFE_APPS = setOf(
            "com.samsung.android.app.camera",
            "com.huawei.camera",
            "com.google.android.GoogleCamera",
            "us.zoom.videomeetings",
            "com.google.android.apps.meetings",
            "com.microsoft.teams",
            "com.kakao.talk",
            "org.telegram.messenger",
            "com.whatsapp",
            "jp.naver.line.android",
            "com.google.android.apps.maps",
            "com.google.android.gm",
        )

        private const val SELF_PACKAGE = "app.myphonecheck.mobile"
    }

    /**
     * 전체 설치 앱에서 위험 권한이 허용된 앱 목록을 수집합니다.
     *
     * @param includeSystemApps 시스템 앱 포함 여부 (기본 false)
     * @return 위험 권한 보유 앱 목록 (riskScore 내림차순 정렬)
     */
    fun collectAll(includeSystemApps: Boolean = false): List<GrantedPermissionInfo> {
        val pm = context.packageManager
        val results = mutableListOf<GrantedPermissionInfo>()

        val installedPackages = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.getInstalledPackages(
                PackageManager.PackageInfoFlags.of(PackageManager.GET_PERMISSIONS.toLong()),
            )
        } else {
            @Suppress("DEPRECATION")
            pm.getInstalledPackages(PackageManager.GET_PERMISSIONS)
        }

        for (packageInfo in installedPackages) {
            val pkg = packageInfo.packageName ?: continue
            if (pkg == SELF_PACKAGE || pkg in SYSTEM_IGNORE) continue

            val isSystem = (packageInfo.applicationInfo?.flags
                ?: 0) and ApplicationInfo.FLAG_SYSTEM != 0
            if (!includeSystemApps && isSystem) continue

            val requestedPermissions = packageInfo.requestedPermissions ?: continue
            val requestedFlags = packageInfo.requestedPermissionsFlags

            // 실제 granted된 위험 권한만 수집
            val grantedDangerous = mutableListOf<DangerousPermission>()
            for (i in requestedPermissions.indices) {
                val perm = requestedPermissions[i]
                val dangerous = DangerousPermission.fromManifestPermission(perm) ?: continue

                // requestedPermissionsFlags로 granted 여부 확인
                val isGranted = if (requestedFlags != null && i < requestedFlags.size) {
                    (requestedFlags[i] and PackageManager.PERMISSION_GRANTED) != 0
                } else {
                    // fallback: checkPermission
                    pm.checkPermission(perm, pkg) == PackageManager.PERMISSION_GRANTED
                }

                if (isGranted) {
                    grantedDangerous.add(dangerous)
                }
            }

            if (grantedDangerous.isEmpty()) continue

            val appLabel = try {
                pm.getApplicationLabel(
                    pm.getApplicationInfo(pkg, 0),
                ).toString()
            } catch (e: Exception) {
                pkg.substringAfterLast(".")
            }

            val installTime = packageInfo.firstInstallTime
            val daysSinceInstall = ((System.currentTimeMillis() - installTime) /
                (24 * 60 * 60 * 1000L)).toInt().coerceAtLeast(0)

            val isKnownSafe = pkg in KNOWN_SAFE_APPS
            val riskScore = calculateRiskScore(grantedDangerous, isKnownSafe, daysSinceInstall)

            results.add(
                GrantedPermissionInfo(
                    packageName = pkg,
                    appLabel = appLabel,
                    grantedPermissions = grantedDangerous,
                    daysSinceInstall = daysSinceInstall,
                    isKnownSafe = isKnownSafe,
                    isSystemApp = isSystem,
                    riskScore = riskScore,
                ),
            )
        }

        Log.i(TAG, "Collected ${results.size} apps with dangerous permissions")
        return results.sortedByDescending { it.riskScore }
    }

    /**
     * 특정 권한 그룹으로 필터링.
     *
     * @param groupKey DangerousPermission.groupKey (예: "camera", "location")
     */
    fun collectByGroup(groupKey: String): List<GrantedPermissionInfo> {
        return collectAll().filter { info ->
            info.grantedPermissions.any { it.groupKey == groupKey }
        }
    }

    /**
     * 위험도 점수 계산.
     *
     * 점수 체계:
     * - 기본: 허용된 위험 권한 수 × 10
     * - 카메라 + 마이크 동시 보유: +15
     * - 위치 + 연락처 동시 보유: +10
     * - SMS + 통화기록 동시 보유: +10
     * - 알려진 안전 앱: -20
     * - 설치 365일 이상: -5
     * - 범위: 0~100
     */
    private fun calculateRiskScore(
        permissions: List<DangerousPermission>,
        isKnownSafe: Boolean,
        daysSinceInstall: Int,
    ): Int {
        var score = permissions.size * 10

        val groups = permissions.map { it.groupKey }.toSet()

        // 위험 조합 가중
        if ("camera" in groups && "microphone" in groups) score += 15
        if ("location" in groups && "contacts" in groups) score += 10
        if ("sms" in groups && "call_log" in groups) score += 10

        // 감경
        if (isKnownSafe) score -= 20
        if (daysSinceInstall > 365) score -= 5

        return score.coerceIn(0, 100)
    }
}
