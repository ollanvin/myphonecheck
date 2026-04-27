# 18-6. MicCheck 본문 (Patch 30 — 단순 관리자 축소)

**원본 출처**: v1.7.1 §18-6 전문
**v1.8.0 Layer**: Feature
**의존**: `06_product_design/04_system_arch.md`
**변경 이력**: 본 파일은 v1.7.1 원본 전문 이관본. 텍스트 변경 없음.
**파일 경로**: `docs/00_governance/architecture/v1.8.0_code/20_features/23_mic.md`

---

## 18-6. MicCheck 본문 (Patch 30 — 단순 관리자 축소)

### 18-6-0. 재정의 배경 (Patch 30)

v1.6.0 ~ v1.6.1-patch 시점에는 `AppPermissionRisk`·`JustificationStatus`·`CveHistory`·`BreachHistory`·Decision Engine 평판 평가까지 포함된 **감시 엔진** 구조였다. 2026-04-24 대표님 지시로 **세 가지 단순 기능**으로 축소:

1. **권한 있는 앱 리스트 정리** — PackageManager 스캔, 정렬만
2. **최근 사용한 기록 정리** — UsageStatsManager 조회, 표시만
3. **사용자가 언제든 권한 회수할 수 있는 버튼** — 시스템 설정으로 원터치

축소 사유:
- 대표님 정의 기능 범위를 넘어선 "평판 감시 엔진" 설계는 설계자 과잉
- CVE·침해 이력 감시는 별도 Surface `AppSecurityWatch`(§17-3 후행)로 이관 (Patch 31)
- Play 심사 정합성 유지 (QUERY_ALL_PACKAGES 대안 §24-6과 결합)

### 18-6-1. 범위 및 헌법 정합

**MicCheck는 RECORD_AUDIO 권한을 요청하지 않는다.** PackageManager의 `getPackagesHoldingPermissions` 또는 `queryIntentActivities` 기반으로 "누가 권한을 갖고 있는가"만 조회한다.

**R5 네트워크 경계 (헐크 Lane 3 Top 1 반영)**: MicCheck Surface Layer는 **직접 네트워크 호출을 하지 않는다**. 본 Surface 단순 관리자 축소판은 Decision Engine·외부 API 조회도 없고, PackageManager + UsageStatsManager + Intent 호출만 수행한다 (§6-2 R5 준수).

권한 해제 UX는 `ACTION_APPLICATION_DETAILS_SETTINGS` 인텐트 원터치로 시스템 설정 화면 진입.

### 18-6-2. 데이터 모델 (단순화)

```kotlin
/**
 * MicCheck의 단일 항목.
 * Decision Engine · Risk · CVE · Breach · Justification 필드 모두 삭제됨 (Patch 30).
 * 이 구조체는 RiskKnowledge를 구현하지 않는다 (평판 판정 대상이 아님).
 */
data class MicPermissionEntry(
    val packageName: String,
    val appName: String,
    val appIconUri: String?,       // 로컬 PackageManager.getApplicationIcon 결과 캐시
    val installedFromStore: Boolean,
    val lastUsedAt: Long?,          // UsageStatsManager.queryUsageStats 최근 값 (null 가능)
    val isCurrentlyForeground: Boolean
)
```

삭제된 것:
- ~~`AppPermissionRisk`~~ (Patch 30 삭제)
- ~~`JustificationStatus`~~ (Patch 30 삭제, 분류 판정 자체 제거)
- ~~`CveEntry` / `BreachEntry`~~ (AppSecurityWatch Surface로 이관)
- ~~`AppReputation`~~ (Decision Engine 호출 없음)

### 18-6-3. 구현

```kotlin
class MicCheckEngine(
    private val context: Context,
    private val packageManager: PackageManager,
    private val usageStats: UsageStatsManager
) : Checker<Unit, List<MicPermissionEntry>> {

    override suspend fun check(input: Unit): List<MicPermissionEntry> =
        withContext(Dispatchers.IO) {
            val apps = packageManager.getInstalledApplications(
                PackageManager.GET_PERMISSIONS
            ).filter { appInfo ->
                val info = packageManager.getPackageInfo(
                    appInfo.packageName,
                    PackageManager.GET_PERMISSIONS
                )
                info.requestedPermissions?.contains(
                    Manifest.permission.RECORD_AUDIO
                ) == true
            }

            val foregroundPkg = detectForegroundPackage()
            apps.map { appInfo ->
                MicPermissionEntry(
                    packageName = appInfo.packageName,
                    appName = packageManager.getApplicationLabel(appInfo).toString(),
                    appIconUri = null,  // 아이콘은 UI에서 직접 로드
                    installedFromStore = isInstalledFromStore(appInfo.packageName),
                    lastUsedAt = usageStats.lastUsedMillis(appInfo.packageName),
                    isCurrentlyForeground = appInfo.packageName == foregroundPkg
                )
            }.sortedByDescending { it.lastUsedAt ?: 0L }
        }
}
```

### 18-6-4. Cold Start 및 스케줄 (코웍 87a9a3 §17-5-3a 흡수)

MicCheck는 CallCheck/MessageCheck와 달리 실시간 이벤트(수신 전화/문자)가 없으므로 별도 트리거 정책이 필요하다.

**트리거 조건 (OR)**:
- **앱 최초 실행**: Surface 등록 직후 1회 전체 스캔
- **주기적 스캔**: WorkManager `PeriodicWorkRequest`, 최소 간격 15분 (Android 제약), 권장 간격 6시간
- **앱 설치·업데이트 감지**: `ACTION_PACKAGE_ADDED` / `ACTION_PACKAGE_CHANGED` BroadcastReceiver
- **사용자 수동 새로고침**: UI 풀-투-리프레시 제스처

**Cold Start 흐름**:
```
앱 시작 → WorkManager 등록 → 즉시 1회 check(Unit) 실행
       → RECORD_AUDIO 보유 앱 목록 수집 (로컬, 네트워크 없음)
       → UsageStatsManager 최근 사용 조회 (로컬)
       → MicPermissionEntry 리스트 생성 → UI 갱신
```

**배터리·성능 제약**:
- `Constraints.Builder().setRequiresBatteryNotLow(true)` 적용
- 백그라운드 스캔은 로컬 조회만, **네트워크 호출 0**
- 결과 메모리 캐시 + 세션 내 유효 (NKB 저장 불필요 — Risk 정보 아님)
- 스캔 시간 중위값 < 200ms 목표 (앱 30개 기준)

### 18-6-5. UX 화면

**MicCheckScreen**: 마이크 권한 보유 앱 목록
- 각 카드: 앱 아이콘·이름·마지막 사용 시각·전경 표시 뱃지
- 탭: 권한 해제 인텐트 직접 실행 (상세 화면 없음, 단순화)
- 상단: "N개 앱이 마이크 권한 보유 중" 요약
- 정렬: 최근 사용 순 (내림차순), 사용 기록 없음은 하단

**MicPermissionActionSheet** (탭 시 하단 시트):
```
[앱 이름]
마지막 사용: 3일 전 (또는 "사용 기록 없음")

[ 🛑 권한 해제 ]   ← ACTION_APPLICATION_DETAILS_SETTINGS
[ 🗑️ 앱 삭제 ]     ← ACTION_UNINSTALL_PACKAGE
[ 취소 ]
```

평판·Justification·CVE 표시 없음. 사용자가 직접 판단.

### 18-6-6. 이관 기록 — AppSecurityWatch (§17-3 후행)

v1.6.1-patch 시점에 MicCheck/CameraCheck에 포함되었던 다음 기능은 **별도 Surface `AppSecurityWatch`**로 이관되었다 (Patch 31):

- 신규 앱 설치 시 과거 보안 사고 이력 자동 검색·경고 (메모리 #13-1)
- 기존 앱 신규 CVE/침해 사고 실시간 감지·알림 (메모리 #13-2)
- NVD CVE API / CISA KEV / Have I Been Pwned 조회
- Decision Engine 기반 앱 평판 판정

본 후행 Surface는 §17-3에 Placeholder로 등록, 별도 스펙 워크오더로 진입 예정.
