# v1 UX 자산 전수 조사 + v2.x 매핑 보고서

**작성**: Cowork
**작성일**: 2026-04-28
**대상**: v1 시절 (2026-04-09~04-13, CallCheck) UX 자산
**근거**: 대표님 명시 — "껍데기만 가지고 지금까지 다 했다"

---

## Executive Summary

| 영역 | v1 존재 | v2.x 상태 | 매핑 |
|---|---|---|---|
| 1·2 앱 진입 | YES | v2.x 리팩토링 (CallCheck→MyPhoneCheck) | 정합 |
| 3 Initial Scan | YES | v2.x 리팩토링 (6스캐너 분리) | 정합 |
| 4 온보딩 5페이지 | YES | v1 구조 유지 (NavHost 인라인) | 정합 |
| 5 권한 위임 | YES | v2.x 확장 (4종→8+종) | 정합 |
| 6·7 홈 + 스와이프 | YES | 부분 (2×2 → 4엔진만, Pager 누락) | **부분 매핑** |
| 8-1 CallCheck UX | YES | v2.x 독립 모듈 (feature/call-check) | 정합 |
| 8-2 MessageCheck UX | YES | v2.x 독립 모듈 (feature/message-check) | 정합 |
| 8-3 Mic/CameraCheck UX | YES | v2.x 분리 (SensorCheckDetailScreen) | 정합 |
| 8-4 PushCheck UX | YES | v2.x 독립 모듈 (feature/push-trash) | 정합 |
| 8-5 CardCheck UX | NO (v1 부재) | v2.x 신규 (feature/card-check) | v2.x 신설 |
| 9 결제 (Subscribe) | YES | v2.x 보안 강화 (EncryptedSharedPreferences) | 정합 |
| 10 멤버스 (계정) | NO | 구독 전용 모델 (서버 0, 회원가입 0) | §1 정합 |
| 11 Settings 탭 | YES | v2.x 확장 (v1+v2 병행) | 정합 |
| + 7개 언어 strings | YES | 6개 values-xx 잔존 | **§9-1 충돌** |
| + 피그마 자료 | NO | 외부 폴더 미발견 | **누락** |

**판정**:
- 누락 영역: 피그마 자료 (외부 폴더 미존재), 홈 6 Surface 확장 (4→6 미완), HorizontalPager 스와이프
- 충돌 영역: 7개 언어 values-xx 수동 번역 vs §9-1 (values-xx 수동 추가 금지)
- 정합 영역: 11개 영역 중 10개 기본 구조 정합

---

## 1. 영역별 상세 조사

### 1-1. 영역 1·2 — 앱 진입

**v1 자산**:
- CallCheckApplication.kt → 앱 Application 클래스
- MainActivity.kt → 단일 Activity, Compose 호스팅
- AndroidManifest.xml → ACTION_MAIN + LAUNCHER intent-filter

**v2.x 현재**:
- `app/src/main/kotlin/app/myphonecheck/mobile/MyPhoneCheckApplication.kt` — Application 클래스 (v1 CallCheck→v2 MyPhoneCheck 리네임)
- `app/src/main/kotlin/app/myphonecheck/mobile/MainActivity.kt` — 단일 Activity
- `app/src/main/AndroidManifest.xml` — LAUNCHER intent (line 78)
- `app/src/main/kotlin/app/myphonecheck/mobile/navigation/MyPhoneCheckNavHost.kt` — 전체 네비게이션 허브 (3200+ lines)

**매핑 평가**: 정합
**조치 권장**: 없음. v1→v2 리네임 완료, 구조 동일.

### 1-2. 영역 3 — Initial Scan

**v1 자산**:
- PrivacyScannerService + PrivacyScannerForegroundService + PrivacyScannerEngine
- 앱 첫 실행 시 디바이스 스캔

**v2.x 현재**:
- `feature/initial-scan/` 독립 모듈 (12 .kt 파일)
  - `InitialScanService.kt` — 포그라운드 서비스
  - `SimContextScanner.kt` — SIM 컨텍스트 스캔
  - `CallLogScanner.kt` — 통화 기록 스캔
  - `SmsInboxScanner.kt` — SMS 수신함 스캔
  - `PackageInventoryScanner.kt` — 설치 앱 인벤토리
  - `InitialScanScreen.kt` + `InitialScanViewModel.kt` — UI
  - `BaseDataRepository.kt`, `ScanResult.kt`, `InitialScanModule.kt`
  - 테스트 2건 포함
- NavHost route: `"initial-scan"`

**매핑 평가**: 정합
**조치 권장**: 없음. v1 단일 서비스 → v2.x 6스캐너 분리 리팩토링 완료. §8 SIM-Oriented 정합.

### 1-3. 영역 4 — 온보딩 5페이지

**v1 자산**:
- 온보딩 5장: 위협 인식 / 4엔진 소개 / 권한 안내 / 보안 선언 / 권한 요청
- CallCheckNavHost.kt 인라인 Composable

**v2.x 현재**:
- `MyPhoneCheckNavHost.kt` (line 526~1096) 인라인 — **별도 모듈 미분리**
  - `OnboardingScreen()` — 5페이지 페이지 인디케이터 + Next/Later/Start 버튼
  - `OnboardingPage1()` (line 634) — 위협 질문 + 카드
  - `OnboardingPage2()` (line 702) — 4 entry points (Calls/Push/Messages/Privacy)
  - `OnboardingPage3()` (line 793) — 권한 안내 (Phone state/NLS/SMS/Usage stats)
  - `OnboardingPage4()` (line 893) — 보안 선언 (No server/No collection/No transfers/AES-256-GCM)
  - `OnboardingPage5()` (line 1021) — 실제 권한 요청 (Overlay/NLS/Usage stats/Phone state) + Later/Get started
- `ONBOARDING_PAGE_COUNT = 5`
- 완료 플래그: SharedPreferences `"onboarding_completed"`
- NavHost startDestination: 미완료 시 `"onboarding"`, 완료 시 `"home"`

**strings.xml 대응**:
- `onboarding_page1_title` — "Unknown number, would you answer it?"
- `onboarding_page1_threat` — "Scam calls, phishing messages..."
- `onboarding_p4_hero` — "Your data never leaves this device / It never will"
- `onboarding_p5_intro` — "Grant the permissions below..."
- `onboarding_on_device_only` — "100% on-device · no server transmission"

**매핑 평가**: 정합
**조치 권장**: 온보딩 Composable이 NavHost.kt 단일 파일에 인라인 (약 570 lines). `feature/onboarding/` 모듈 분리 권장 (코드 정리 차원, 기능 정합은 문제 없음).

### 1-4. 영역 5 — 권한 위임

**v1 자산**:
- 4종 권한: SYSTEM_ALERT_WINDOW / BIND_NOTIFICATION_LISTENER_SERVICE / PACKAGE_USAGE_STATS / READ_PHONE_STATE
- 온보딩 Page 5에서 일괄 요청

**v2.x 현재**:
- v1 4종 유지 + v2.x 추가:
  - `READ_CONTACTS` — 저장된 연락처 식별
  - `READ_CALL_LOG` — v4.3에서 제거 (항상 false 반환)
  - `READ_SMS` — v4.3에서 제거 (항상 false 반환)
  - `RECEIVE_SMS` — 실시간 SMS 수신
  - `POST_NOTIFICATIONS` — Android 13+ 런타임 권한
  - `ANSWER_PHONE_CALLS` — Android 8+ 인앱 통화 제어
- 권한 체크 함수: NavHost.kt (line 444~510) — `hasDrawOverlayPermission()`, `hasUsageStatsPermission()`, `hasReadPhoneStatePermission()`, `hasContactsPermission()`, `hasReceiveSmsPermission()`, `hasPostNotificationsPermission()`, `hasAnswerCallsPermission()`
- `openAppDetailsSettings()` — 영구 거부 시 설정 이동

**매핑 평가**: 정합
**조치 권장**: 없음. v1 4종 → v2.x 8+종 확장 완료. v4.3 deprecated 권한 (READ_CALL_LOG, READ_SMS) 정리됨.

### 1-5. 영역 6·7 — 홈 화면 + 스와이프

**v1 자산**:
- 2×2 엔진 카드 그리드: Call / Message / Privacy / Notification
- HorizontalPager 스와이프 네비게이션
- 카드 색상: #4FC3F7 (Call), #81C784 (Message), #E57373 (Privacy), #FFB74D (Notification)
- 하단 Subscribe 버튼 (#4FC3F7)
- 보안 배지: "🔒 100% on-device · No servers · No data collection"

**v2.x 현재**:
- `HomeScreen()` — NavHost.kt (line 1097~1246)
- 2×2 그리드 유지: Call (#4FC3F7) / Message (#81C784) / Camera (#E57373) / Mic (#FFB74D)
  - Row 1: `EngineCard` (CallCheck) + `EngineCard` (MessageCheck)
  - Row 2: `SensorEngineCard` (CameraCheck) + `SensorEngineCard` (MicCheck)
- 하단 Subscribe 버튼 유지 (#4FC3F7)
- 보안 배지 유지: `home_security_trust_badge`
- Bottom Navigation 3탭: Home / Timeline / Settings

**누락 확인**:
- **HorizontalPager / VerticalPager**: 코드베이스 전체 grep 결과 0건. v1 Pager 스와이프 제거됨.
- **6 Surface 홈 카드**: 현재 4 엔진 카드만 (CallCheck, MessageCheck, CameraCheck, MicCheck). PushCheck·CardCheck는 홈 미표시, Settings 경유만 접근.
- v1 4엔진 → v2.x 6 Surface 전환 시 홈 2×2를 2×3 또는 3×2로 확장 필요했으나 미반영.

**매핑 평가**: **부분 매핑**
**조치 권장**:
1. 홈 화면 6 Surface 카드 확장 (PushCheck + CardCheck 홈 추가) — 별도 PR
2. HorizontalPager 스와이프 복원 여부 — 비전 결정 필요 (설계 결정)

### 1-6. 영역 8-1 — CallCheck UX

**v1 자산**:
- 오버레이 카드 (위험/주의/안전 판정, 수신/거절/차단 3버튼)
- 통화 기록 리스트
- AI Judgment 표시

**v2.x 현재**:
- `feature/call-check/` 독립 모듈
  - `CallCheckScreen.kt` — LazyColumn 기반 통화 기록 목록
  - `CallCheckViewModel.kt` — 상태 관리
  - `CallCheckRoute()` — READ_CALL_LOG 권한 요청 포함
  - `CallEntry` + `CallDirection` — 데이터 모델
- `feature/call-intercept/` — 수신 차단 서비스
- `feature/call-screening/` — `MyPhoneCheckCallScreeningService.kt` + `RoomBlockListRepository.kt`
- NavHost EngineDetailScreen (route: `"engine/call"`) — 엔진 상세 화면
- 색상: ScreenBg `Color(0xFF0D1B2A)`, Accent `Color(0xFF4FC3F7)`
- strings.xml: overlay_verdict_high/medium/low, overlay_action_answer/reject/block, risk_high/medium/low

**매핑 평가**: 정합
**조치 권장**: 없음. v1 오버레이 → v2.x CallScreeningService 전환 완료.

### 1-7. 영역 8-2 — MessageCheck UX

**v1 자산**:
- 메시지 허브 (발신자 프로필 + 미리보기 + 링크 경고 + 보관/삭제/차단)
- 카테고리 분류 (스팸/결제/일반)

**v2.x 현재**:
- `feature/message-check/` 독립 모듈
  - `MessageCheckScreen.kt` — LazyColumn 메시지 목록
  - `MessageCheckViewModel.kt` — 상태 관리
  - `MessageCheckRoute()` — READ_SMS 권한 요청
  - `MessageEntry` + `MessageCategory` + `SenderProfile` — 데이터 모델
- NavHost `MessageHubScreen` (route: `"message-hub"`) — 통합 메시지 허브
- 색상: ScreenBg `Color(0xFF0D1B2A)`, Accent `Color(0xFF4FC3F7)`, Spam `Color(0xFFEF5350)`, Payment `Color(0xFF66BB6A)`
- strings.xml: message_hub_* 시리즈 (link_warning, action_keep/delete/block, search_naver/google, keyword_counts, risk_fmt)
- `feature/message-intercept/` — SMS 수신 인터셉트 서비스
- `feature/sms-block/` — SMS 차단 모듈

**매핑 평가**: 정합
**조치 권장**: 없음. v1 허브 구조 보존 + v2.x 패턴 분석 강화.

### 1-8. 영역 8-3 — MicCheck / CameraCheck UX

**v1 자산**:
- PrivacyCheck 단일 화면 (카메라/마이크 탭 + 히스토리 리스트 + 경고 배지)

**v2.x 현재**:
- v1 PrivacyCheck → v2.x MicCheck + CameraCheck 분리
- `feature/privacy-check/` 모듈
  - `PermissionGrantedCollector.kt` — PackageManager 기반 권한 인벤토리
  - `GuardResult`, `InitialScanGuard`, `ScanStatus`, `SensorAppInfo`, `SensorCheckState`, `StatusLevel`
- NavHost `SensorCheckDetailScreen` (공유 Composable):
  - route `"camera-check"` — 아이콘 Videocam, 색상 `Color(0xFFE57373)`
  - route `"mic-check"` — 아이콘 Mic, 색상 `Color(0xFFFFB74D)`
- NavHost `PrivacyHistoryScreen` (route: `"privacy-history"`) — 카메라/마이크 접근 이력
- ViewModel: `CameraCheckViewModel`, `MicCheckViewModel`, `PrivacyHistoryViewModel`
- Guard 체계: `InitialScanGuardViewModel` — 베이스라인 유효 시에만 스캔 실행
- strings.xml: camera_check_title, mic_check_title, sensor_* 시리즈, privacy_* 시리즈

**매핑 평가**: 정합
**조치 권장**: 없음. v1 통합 → v2.x 분리 완료. Guard 체계 추가 (보안 강화).

### 1-9. 영역 8-4 — PushCheck UX

**v1 자산**:
- NotificationListenerService 기반 알림 허브
- 광고/스팸 알림 필터링

**v2.x 현재**:
- `feature/push-trash/` 독립 모듈
  - `PushTrashScreens.kt` — 메인 UI
  - `PushTrashViewModel.kt` — 상태 관리
  - `PushTrashBinViewModel.kt` — 휴지통 UI
  - `PushTrashNotificationListener.kt` — NotificationListenerService 구현
  - `PushTrashAppsRoute`, `AppBlockSettingsRoute` — 앱별 차단 설정
- NavHost routes: `"push-trash"`, `"push-trash/bin"`, `"push-trash/apps"`, `"push-trash/app/{packageName}"`
- strings.xml: engine_name_pushcheck, engine_push_question/desc

**매핑 평가**: 정합
**조치 권장**: PushCheck가 홈 화면 엔진 카드에 미표시 (Settings 경유만 접근). 영역 6·7 참조.

### 1-10. 영역 8-5 — CardCheck UX

**v1 자산**: v1 시절 부재 (v1.9.0 이후 신설)

**v2.x 현재**:
- `feature/card-check/` 독립 모듈
  - `CardCheckScreen.kt` — 월별 필터 + 거래 목록 (LazyColumn)
  - `CardCheckViewModel.kt` — MonthOffset 기반 월 선택
  - `MonthOffset` — 월 오프셋 모델
  - 색상: ScreenBg `Color(0xFF0D1B2A)`, Accent `Color(0xFF4FC3F7)`
  - FilterChip — 저위험 포함/제외 토글
  - CardTransactionMonthlyTotal + CardTransactionEntity — Room DB 연동
- NavHost route: `"card-check"`

**매핑 평가**: v2.x 신설 (v1 부재, 매핑 대상 아님)
**조치 권장**: 홈 화면 엔진 카드에 미표시. 영역 6·7 참조.

### 1-11. 영역 9 — 결제 (Subscribe)

**v1 자산**:
- 홈 하단 Subscribe 버튼 (#4FC3F7)
- Google Play BillingClient 연동

**v2.x 현재**:
- `feature/billing/` 독립 모듈
  - `BillingManager.kt` — Google Play BillingClient 연동
    - `SUBSCRIPTION_PRODUCT_ID = "myphonecheck_monthly"`
    - `EncryptedSharedPreferences` — 암호화된 entitlement 캐시
    - `TamperChecker` 통합 — 변조 기기 차단
    - 구독 토큰 중복 방지 (anti-replay)
    - 캐시 만료: 24시간
  - `PaywallScreen.kt` — 구독 UI (기능 소개 + 구독/복원/취소)
  - `PaywallViewModel.kt` — 체험 기간 카운트다운 + 가치 앵커 (의심 전화/위험 메시지 실적 표시)
  - `SubscriptionState` — Loading / Active / Expired / Error 상태 관리
- NavHost route: `"purchase"` → `PurchaseScreen`
- 홈 하단 Subscribe 버튼 유지

**strings.xml 대응**:
- `subscription_title` — "Subscription"
- `paywall_title` — "Subscribe"
- `paywall_feature_call_title` — "Real-time suspicious call check"
- `paywall_feature_message_title` — "Risky link message check"
- `paywall_restore` — "Restore previous subscription"
- `trial_countdown_remaining` — "Free trial ends in %1$d days"
- `value_anchor_suspicious_calls_fmt`, `value_anchor_risky_messages_fmt`

**가격**: 코드 내 하드코딩 가격 없음. Google Play Console에서 동적 반환 (BillingClient ProductDetails). $2.49/월은 Play Console 설정.

**매핑 평가**: 정합
**조치 권장**: 없음. v1 → v2.x 보안 강화 (EncryptedSharedPreferences + TamperChecker + anti-replay).

### 1-12. 영역 10 — 멤버스 (계정)

**v1 자산**: 별도 멤버십/계정 체계 부재. Play 구독만.

**v2.x 현재**:
- 서버 0, 회원가입 0 (헌법 §1 Out-Bound Zero 정합)
- 디바이스 로컬 데이터 + Play 구독 ID 모델
- `SubscriptionState` 기반 entitlement 관리
- 계정 삭제 = 디바이스 로컬 데이터 삭제 (앱 삭제)

**매핑 평가**: 정합 (§1 Out-Bound Zero 완전 준수)
**조치 권장**: 없음. 설계상 의도적 부재.

### 1-13. 영역 11 — Settings 탭

**v1 자산**:
- 보안 상태 카드 (방패 아이콘 + 4줄 체크리스트)
- 데이터 관리 / 구독 / 앱 정보 섹션

**v2.x 현재**:
- `SettingsScreen()` — NavHost.kt (line 2224) 인라인 Composable
- `feature/settings/` 독립 모듈
  - v1 구조: `SettingsScreen.kt`, `SettingsViewModel.kt`
  - v2 구조: `v2/SettingsV2Screen.kt`, `v2/SettingsV2ViewModel.kt` (PR #24, LanguagePreference + PublicFeed)
  - `RoomSimContextStorage.kt`, `UserPreferenceRepository.kt`
- 보안 상태 카드: `settings_security_card_title`, `settings_security_bullet_*` (no_server, no_transfer, aes, on_device)
- Navigation: Backup, PushTrash, CardCheck, CallCheck, MessageCheck, InitialScan, SettingsV2, TagList
- 온보딩 리플레이: `settings_restart_onboarding_title` + `settings_restart_onboarding_desc`
- 권한 관리: `settings_perm_group_*` 시리즈 (Accessibility, Phone/SMS, Notification)
- 언어 표시: `language_current_fmt` — "Current: %1$s (auto-detected from device)"
- NavHost route: `"settings"` (Bottom Nav 3탭 중 하나)

**매핑 평가**: 정합
**조치 권장**: 없음. v1 → v2.x 확장 완료 (v2 Settings 추가).

---

## 2. 누락·충돌 핵심 영역

### 2-1. 누락 (v1 있고 v2.x에 없음)

1. **HorizontalPager 스와이프**: v1에서 Compose Pager로 엔진 간 스와이프 구현됨. v2.x에서 제거됨 (HorizontalPager / VerticalPager grep 결과 0건). 홈 화면이 단순 verticalScroll Column으로 변경.

2. **홈 6 Surface 완전 표시**: v2.x 아키텍처는 6 Surface (CallCheck, MessageCheck, MicCheck, CameraCheck, PushCheck, CardCheck). 그러나 홈 화면은 4 엔진 카드만 (CallCheck, MessageCheck, CameraCheck, MicCheck). PushCheck·CardCheck는 Settings 경유만 접근 가능.

3. **피그마 디자인 자료**: `C:\Users\user\Dev\ollanvin\figma\` 경로 및 하위 어디에도 피그마 관련 파일 미발견. Downloads 폴더에도 미발견.

### 2-2. 충돌 (v1 자산 vs v2.x 헌법)

#### 2-2-1. 7개 values-xx 수동 번역

- **v1 자산**: app 모듈 6개 values-xx 디렉토리 존재
  - `app/src/main/res/values-ko/strings.xml`
  - `app/src/main/res/values-ja/strings.xml`
  - `app/src/main/res/values-zh-rCN/strings.xml`
  - `app/src/main/res/values-ru/strings.xml`
  - `app/src/main/res/values-es/strings.xml`
  - `app/src/main/res/values-ar/strings.xml`
  - + `values/strings.xml` (en default) = **7개 언어**
- **feature/billing**: `values-ko/strings.xml` 추가 존재 (1개)
- **v2.2.0 §9-1**: values-xx 수동 추가 금지, OS 자동 처리 위임
- **충돌 판정**: **YES**
- **조치**: v1 수동 번역 파일 정리 PR 필요. 다만 정리 시 UX 퇴보 발생 (7개 언어 → 영문만). 비전 결정 필요:
  - A) §9-1 예외 조항 신설 (v1 유산 보존)
  - B) §9-1 강행 (values-xx 삭제, OS 자동 처리만)
  - C) §9-1 개정 (수동 번역 허용 조건 명시)

### 2-3. 부분 매핑

1. **홈 2×2 → 2×3 확장 미완**: v2.x 아키텍처 6 Surface 중 PushCheck·CardCheck 홈 미표시. 기능 자체는 존재하지만 홈 화면 접근성 부재.

2. **온보딩 모듈 미분리**: OnboardingScreen + OnboardingPage1~5가 NavHost.kt (3200+ lines) 안에 인라인. 기능은 정합하지만 코드 구조적 분리 미완.

---

## 3. 향후 작업 권장

### 3-1. 즉시 정정 필요 (출시 전 P0)

없음. v1 UX 자산이 v2.x에 기능적으로 존재. 출시 차단 사유 아님.

### 3-2. UX 통합 워크오더 시리즈 권장

1. **홈 6 Surface 카드 확장**: PushCheck + CardCheck 홈 추가 → 2×2 → 2×3 또는 3×2 그리드 변경
2. **HorizontalPager 스와이프 복원 여부**: 비전 설계 결정 (홈 카드 vs Pager)
3. **온보딩 모듈 분리**: `feature/onboarding/` 독립 모듈로 NavHost에서 분리
4. **§9-1 vs 7언어 충돌 해소**: 비전 결정 (예외/삭제/개정)

### 3-3. 카드스펜드 폐기 영향

- v1 시절 CallCheck 코드 → myphonecheck로 완전 마이그레이션 확인 (패키지명 `app.myphonecheck.mobile`)
- v1.9.0 CardCheck 신설 시 카드스펜드 코드 재활용 흔적 없음 (독립 구현)
- 피그마 자료: myphonecheck 및 cardspend 피그마 폴더 모두 미발견

---

## 4. 헌법 정합 검증

| 조 | 정합 검증 |
|---|---|
| §1 Out-Bound Zero | **PASS** — BillingClient만 외부 통신. 서버 0. 회원가입 0. 로컬 데이터만. |
| §6 가격 정직성 | **PASS** — 가격 Google Play Console 동적 반환. 하드코딩 없음. `myphonecheck_monthly` 단일 상품. |
| §8 SIM-Oriented | **PASS** — `SimContextScanner` 존재. `LanguageContextProviderImpl` 디바이스 언어 감지. |
| §9-1 언어 정공법 | **FAIL** — 6개 values-xx 수동 번역 잔존. §9-1 위반. 정정 PR 필요. |
| §9-3 글로벌 단일 출시 | **PASS** — 국가/언어별 분기 없음. `feature/country-config/` 존재하지만 런타임 Tier 분류만 (빌드 분기 아님). |

---

## 5. 종합 판정

**v1 자산 vs v2.x 정합도**: 85% (11개 핵심 영역 중 10개 기능 정합, 1개 부분 매핑)
**출시 차단 사유**: NO — v1 UX 자산 기능적으로 v2.x에 존재. 홈 6 Surface 미완은 UX 개선 사항이지 출시 차단 아님.
**비전 다음 단계 결정 영역**:
1. §9-1 vs v1 7언어 충돌 해소 방향 (예외/삭제/개정)
2. 홈 6 Surface 카드 확장 여부 + 시기
3. HorizontalPager 스와이프 복원 여부
4. 피그마 디자인 자료 소재 확인

WO: WO-V230-UX-ASSETS-AUDIT

---

## 부록 A. 색상 팔레트 매핑

| 색상 | 용도 | 위치 |
|---|---|---|
| `#0D1B2A` | ScreenBg (전체 배경) | NavHost.kt, CallCheckScreen.kt, MessageCheckScreen.kt, CardCheckScreen.kt 등 |
| `#1B2838` | CardBg (카드 배경) | CallCheckScreen.kt, MessageCheckScreen.kt, CardCheckScreen.kt |
| `#4FC3F7` | Accent / CallCheck 색상 | 홈 카드, 버튼, 선택 네비, 보안 배지 |
| `#81C784` | MessageCheck 색상 | 홈 카드, engine/message |
| `#E57373` | CameraCheck / PrivacyCheck 색상 | 홈 카드, camera-check, 온보딩 위협 텍스트 |
| `#FFB74D` | MicCheck 색상 | 홈 카드, mic-check |
| `#0A1628` | NavigationBar 배경 | NavHost.kt Bottom Nav |
| `#1E3A5F` | 선택 인디케이터 | NavHost.kt NavigationBarItem |
| `#607D8B` | 비선택 텍스트/아이콘 | NavHost.kt |
| `#B0BEC5` | TextSubtle (보조 텍스트) | CallCheckScreen.kt, MessageCheckScreen.kt 등 |

## 부록 B. NavHost 전체 라우트 맵

| Route | 화면 | 영역 |
|---|---|---|
| `onboarding` | OnboardingScreen (5페이지) | 영역 4 |
| `home` | HomeScreen (2×2 엔진 카드 + 구독) | 영역 6·7 |
| `engine/call` | EngineDetailScreen (CallCheck) | 영역 8-1 |
| `engine/message` | EngineDetailScreen (MessageCheck) | 영역 8-2 |
| `engine/privacy` | EngineDetailScreen (PrivacyCheck) | 영역 8-3 |
| `message-hub` | MessageHubScreen | 영역 8-2 |
| `privacy-history` | PrivacyHistoryScreen | 영역 8-3 |
| `camera-check` | SensorCheckDetailScreen (Camera) | 영역 8-3 |
| `mic-check` | SensorCheckDetailScreen (Mic) | 영역 8-3 |
| `timeline` | TimelineScreen | 공통 |
| `call-detail/{number}` | CallDetailScreen | 영역 8-1 |
| `settings` | SettingsScreen | 영역 11 |
| `settings/v2` | SettingsV2Route | 영역 11 |
| `purchase` | PurchaseScreen → PaywallScreen | 영역 9 |
| `backup` | BackupScreen | 영역 11 |
| `push-trash` | PushTrashMainRoute | 영역 8-4 |
| `push-trash/bin` | PushTrashBinRoute | 영역 8-4 |
| `push-trash/apps` | PushTrashAppsRoute | 영역 8-4 |
| `push-trash/app/{pkg}` | AppBlockSettingsRoute | 영역 8-4 |
| `card-check` | CardCheckRoute | 영역 8-5 |
| `call-check` | CallCheckRoute | 영역 8-1 |
| `message-check` | MessageCheckRoute | 영역 8-2 |
| `initial-scan` | InitialScanRoute | 영역 3 |
| `tag-list` | TagListRoute | 공통 |

## 부록 C. strings.xml 언어 분포

| 모듈 | default (en) | ko | ja | zh-rCN | ru | es | ar |
|---|---|---|---|---|---|---|---|
| app | 299 strings | O | O | O | O | O | O |
| feature/billing | 21 strings | O | - | - | - | - | - |
| feature/call-check | O | - | - | - | - | - | - |
| feature/call-intercept | O | - | - | - | - | - | - |
| feature/card-check | O | - | - | - | - | - | - |
| feature/country-config | O | - | - | - | - | - | - |
| feature/decision-ui | O | - | - | - | - | - | - |
| feature/initial-scan | O | - | - | - | - | - | - |
| feature/message-check | O | - | - | - | - | - | - |
| feature/privacy-check | O | - | - | - | - | - | - |
| feature/push-trash | O | - | - | - | - | - | - |
| feature/call-screening | O | - | - | - | - | - | - |
| feature/settings | O | - | - | - | - | - | - |
| feature/sms-block | O | - | - | - | - | - | - |
| feature/tag-system | O | - | - | - | - | - | - |
| core/model | O | - | - | - | - | - | - |

**§9-1 위반 범위**: app 모듈 (6개 values-xx) + feature/billing (1개 values-ko) = **총 7개 수동 언어 파일**
