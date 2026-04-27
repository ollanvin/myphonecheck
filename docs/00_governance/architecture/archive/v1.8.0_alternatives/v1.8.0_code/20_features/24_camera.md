# 18-7. CameraCheck 본문 (Patch 30 — 단순 관리자 축소, MicCheck 병렬 구조)

**원본 출처**: v1.7.1 §18-7 전문
**v1.8.0 Layer**: Feature
**의존**: `06_product_design/04_system_arch.md`
**변경 이력**: 본 파일은 v1.7.1 원본 전문 이관본. 텍스트 변경 없음.
**파일 경로**: `docs/00_governance/architecture/v1.8.0_code/20_features/24_camera.md`

---

## 18-7. CameraCheck 본문 (Patch 30 — 단순 관리자 축소, MicCheck 병렬 구조)

### 18-7-1. 재정의

CameraCheck는 MicCheck와 **동일한 단순 관리자 구조**를 가지며, `PermissionScope`만 `CAMERA`로 다르다. 설계 중복 회피를 위해 본 섹션은 차이점만 기술.

**CameraCheck는 CAMERA 권한을 요청하지 않는다** (Patch 23 유지).

**R5 네트워크 경계**: MicCheck §18-6-1과 동일. 직접 네트워크 호출 없음.

### 18-7-2. 데이터 모델

```kotlin
data class CameraPermissionEntry(
    val packageName: String,
    val appName: String,
    val appIconUri: String?,
    val installedFromStore: Boolean,
    val lastUsedAt: Long?,
    val isCurrentlyForeground: Boolean
)
```

구조 완전 동일 (이름만 `Camera*`). 공통화 가능하지만 Surface 분리 원칙(§17-1)에 따라 각 Surface가 자기 타입을 가진다.

### 18-7-3. 구현

```kotlin
class CameraCheckEngine(
    private val context: Context,
    private val packageManager: PackageManager,
    private val usageStats: UsageStatsManager
) : Checker<Unit, List<CameraPermissionEntry>> {

    override suspend fun check(input: Unit): List<CameraPermissionEntry> =
        withContext(Dispatchers.IO) {
            val apps = packageManager.getInstalledApplications(
                PackageManager.GET_PERMISSIONS
            ).filter { appInfo ->
                val info = packageManager.getPackageInfo(
                    appInfo.packageName,
                    PackageManager.GET_PERMISSIONS
                )
                info.requestedPermissions?.contains(
                    Manifest.permission.CAMERA
                ) == true
            }

            val foregroundPkg = detectForegroundPackage()
            apps.map { appInfo ->
                CameraPermissionEntry(
                    packageName = appInfo.packageName,
                    appName = packageManager.getApplicationLabel(appInfo).toString(),
                    appIconUri = null,
                    installedFromStore = isInstalledFromStore(appInfo.packageName),
                    lastUsedAt = usageStats.lastUsedMillis(appInfo.packageName),
                    isCurrentlyForeground = appInfo.packageName == foregroundPkg
                )
            }.sortedByDescending { it.lastUsedAt ?: 0L }
        }
}
```

### 18-7-4. Cold Start·스케줄·UX

MicCheck §18-6-4, §18-6-5와 완전 동일 원칙. WorkManager·BroadcastReceiver·사용자 새로고침 트리거를 공유할 수 있도록 `feature/permission-scan` 공통 모듈로 구현 권장.

화면 이름만 `CameraCheckScreen` / `CameraPermissionActionSheet`.

### 18-7-5. 이관 기록

CameraCheck의 감시·CVE·침해 기능도 Patch 31로 `AppSecurityWatch`에 이관 (§18-6-6과 동일).
