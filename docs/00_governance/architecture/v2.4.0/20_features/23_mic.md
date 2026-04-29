## 18-6. MicCheck 본문 (Patch 30 — 단순 관리자 축소)

**원본 출처**: v1.7.1 §18-6 (2205–2343)
**v1.8.0 Layer**: Feature
**의존**: `00_core/01_primary.md`
**변경 이력**: 본 파일은 v1.7.1 §18-6 원본 전문 이관본. 텍스트 변경 없음.
**파일 경로**: `docs/00_governance/architecture/v1.8.0_cursor/20_features/23_mic.md`

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

---

## §direct-search. "직접 검색" 버튼 spec (v2.4.0 신설, 2026-04-29 대표님 결정)

### 목적

Risk Tier "Unknown" 영역 사용자 의사결정 보조. 옛 MyPhoneCheck v1에서 "확실하게 피싱·스팸이라고 판단하기 어려운 애매한 번호"가 사용자에게 가장 답답한 영역이었던 점 정정.

본 Surface에서 사용자가 수신/거절/차단 외 **"직접 확인" 옵션**을 행사할 수 있도록 "🔍 직접 검색" 버튼을 prominent 배치한다.

### 동작

```
[🔍 직접 검색] 탭
  ↓
4축 메뉴 시트 표시 (사용자 첫 사용 시 전체 메뉴, 이후 마지막 선택 default)
  ① 🌐 AI 검색 (Google AI Mode)
       → https://www.google.com/search?q={번호}&udm=50
       → Custom Tab 진입
  ② 🛡 KISA 보이스피싱 신고 DB
       → 공공 API 결과 페이지
  ③ 📞 경쟁사 (Truecaller / Whoscall / Hiya)
       → 사용자 마지막 선택 기억
       → Custom Tab 진입
  ④ 🔎 일반 검색 (Google / Bing / Naver)
       → 사용자 default 검색엔진
       → Custom Tab 진입
  ↓
사용자가 검색 결과 보고 본인 판단
  ↓
사용자 행동:
  - 수신
  - 거절
  - 차단
  - 태그 추가 (이 입력은 다음 동일 번호 수신 시 결정 엔진 score 갱신, 헌법 §1·§2 정합 = 온디바이스만)
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
- 4축 메뉴 시트: BottomSheet 또는 Dialog (Material 3)
- 마지막 선택 기억: SharedPreferences 로컬 저장 (헌법 §1·§2 정합)

### §direct-search-mic. MicCheck 배치 위치

**권한 침해 알림 시**:
- 신규 앱 설치 + 마이크 권한 요청 시 즉시 외부 검색 (앱 패키지명 + CVE 키워드)
- 위치: 권한 침해 알림 카드에 "🔍 이 앱 검색" 버튼
- 검색 대상: 앱 패키지명 → Google AI Mode / 경쟁사 reverse는 부적합 (앱 검색 전용)
- 4축 중 활성: ① AI 검색, ④ 일반 검색만 (경쟁사·KISA는 폰 번호 전용)

**모듈**: `:feature:privacy-check` (기존)
