## 18-7. CameraCheck 본문 (Patch 30 — 단순 관리자 축소, MicCheck 병렬 구조)

**원본 출처**: v1.7.1 §18-7 (2345–2416)
**v1.8.0 Layer**: Feature
**의존**: `00_core/01_primary.md`
**변경 이력**: 본 파일은 v1.7.1 §18-7 원본 전문 이관본. 텍스트 변경 없음.
**파일 경로**: `docs/00_governance/architecture/v1.8.0_cursor/20_features/24_camera.md`

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

---

## §direct-search. "직접 검색" 버튼 spec (v2.4.0 신설, 2026-04-29 대표님 결정)

### 목적

Risk Tier "Unknown" 영역 사용자 의사결정 보조. 옛 MyPhoneCheck v1에서 "확실하게 피싱·스팸이라고 판단하기 어려운 애매한 번호"가 사용자에게 가장 답답한 영역이었던 점 정정.

본 Surface에서 사용자가 수신/거절/차단 외 **"직접 확인" 옵션**을 행사할 수 있도록 "🔍 직접 검색" 버튼을 prominent 배치한다.

### 동작

```
[🔍 직접 검색] 탭
  ↓
SIM 기준 AI 검색 후보 메뉴 (최소 2개) 표시
  - SearchInput.AppPackage 입력 (앱 검색 전용)
  - SimAiSearchRegistry가 SIM countryIso 기반 AI 후보군 추출
  - 예) KR SIM: Naver AI / Google AI Mode / Bing Copilot
  - 예) Global default: Google AI Mode / Bing Copilot
  - 사용자 자율 결정, 고정 우선순위 없음, 마지막 선택 기억
  - 모두 Custom Tab 직접 진입 (헌법 §1 정합)
  ↓
사용자가 검색 결과 보고 본인 판단 (AI 모드가 보안 사고 이력 / CVE / 사용자 신고 자체 통합)
  ↓
사용자 행동:
  - 수신
  - 거절
  - 차단
  - 태그 추가 (다음 동일 query 시 NKB로 흡수 → score 갱신, 헌법 §1·§2 정합 = 온디바이스만)
```

### 헌법 정합

- §1 Out-Bound Zero: Custom Tab 사용자 직접 진입 = 사용자 본인 의지 외부 검색 (우리 송신 0)
- §3 결정권 중앙집중 금지: 우리는 검색 채널 제공만, 행동 결정은 사용자
- §5 정직성: Tier Unknown을 정직히 표시 + 직접 검색 채널 제공

### UX 가이드

- 버튼 색상: Tier 색상과 대비되는 중립색 (회색 또는 파랑)
- 버튼 위치: Tier 표시 바로 옆 또는 아래 (사용자 시선 자연 흐름)
- 버튼 크기: 최소 44dp (Material Design 터치 타겟)
- 버튼 레이블: "🔍 직접 검색" (다국어 strings.xml)
- SIM 기준 AI 후보 메뉴 시트: BottomSheet 또는 Dialog (Material 3)
- 마지막 선택 기억: SharedPreferences 로컬 저장 (헌법 §1·§2 정합)

### §direct-search-camera. CameraCheck 배치 위치

**권한 침해 알림 시**:
- MicCheck와 동일 패턴
- 검색 대상: 앱 패키지명 (SearchInput.AppPackage) → AI 검색 모드가 보안 사고 이력 / CVE / 사용자 신고 자체 통합
- 후보군: SIM 기준 AI 검색 후보 (예 Google AI Mode / Bing Copilot) — 경쟁사 reverse는 부적합 (폰 번호 전용)

**모듈**: `:feature:privacy-check` (기존)
