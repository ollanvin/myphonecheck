# MyPhoneCheck Architecture

> 역설계 기준일: 2026-04-21
> 작성: 비전 (코드베이스 전수 스캔 기반)

---

## 1. 제품 정의

MyPhoneCheck는 **100% 온디바이스 전화·문자·푸시·프라이버시 방어 앱**이다.

핵심 원칙:
- 서버 전송 없음 — 모든 판단·학습·캐시가 기기 내부에서 완결
- 전화번호 SHA-256 해시 — PII 로그 기록 금지
- 2-Phase UX — Phase 1(0~5ms 캐시) 즉시 표시, Phase 2(150~4500ms 네트워크) 점진 갱신
- 190개국 정책 — 국가별 포맷·긴급번호·위험 가중치·검색 엔진 라우팅
- Ring 디자인 시스템 — 단일 시각 메타포(원형 링 색상/애니메이션)로 판단 결과 표현

4개 방어 엔진:

| 엔진 | 진입점 | 판단 대상 |
|------|--------|-----------|
| CALL | CallScreeningService | 수신 전화 위험도 |
| MESSAGE | BroadcastReceiver (SMS) | 수신 문자 피싱/스미싱 |
| PUSH | NotificationListenerService | 앱 푸시 스팸/프로모션 |
| PRIVACY | ForegroundService + AppOps | 카메라/마이크 무단 접근 |

---

## 2. 모듈 맵 (19개)

```
┌─────────────────────────────────────────────────────────────┐
│                         app                                  │
│  Application · MainActivity · NavHost · ViewModels          │
│  Backup/Restore · WeeklyReportWorker                         │
├──────────┬──────────┬──────────┬──────────┬─────────────────┤
│          │          │          │          │                  │
│  feature/│  feature/│  feature/│  feature/│  feature/        │
│  call-   │  message-│  push-   │  privacy-│  decision-       │
│  intercept│ intercept│ intercept│  check   │  engine          │
│  (40 cls)│  (2 cls) │  (2 cls) │  (16 cls)│  (7 cls)         │
│          │          │          │          │                  │
├──────────┴──────────┴──────────┴──────────┤                  │
│                                           │  feature/        │
│  feature/decision-ui (23 cls)             │  search-         │
│  Ring · Overlay · Compose screens         │  enrichment      │
│                                           │  (EMPTY)         │
├───────────────────────┬───────────────────┤                  │
│  feature/settings     │  feature/billing  ├──────────────────┤
│  (5 cls)              │  (7 cls)          │  feature/        │
│                       │                   │  country-config  │
│  feature/device-      │                   │  (11 cls)        │
│  evidence (2 cls)     │                   │                  │
├───────────────────────┴───────────────────┴──────────────────┤
│                       data layer                              │
│  local-cache (28)  calllog (4)  contacts (4)  sms (4)        │
│  search (20+)                                                 │
├──────────────────────────────────────────────────────────────┤
│                       core layer                              │
│  model (40)         util (13)         security (6)           │
└──────────────────────────────────────────────────────────────┘
```

### 모듈 상세

| # | 모듈 | 클래스 수 | 역할 |
|---|------|----------|------|
| 1 | **app** | 24+ | Application, Activity, NavHost, ViewModels, Backup/Restore, WeeklyReport |
| 2 | **core:model** | 40 | 전체 도메인 모델 (enum, data class, interface) |
| 3 | **core:util** | 13 | 전화번호 파싱(libphonenumber), 정규화, 변환 생성, URL 위험 채점, 신뢰도 계산 |
| 4 | **core:security** | 6 | Keystore AES-256, SQLCipher 키 관리, 루트/후킹/리패키징 탐지 |
| 5 | **data:local-cache** | 28 | Room DB (SQLCipher 암호화), 11 Entity, 10 DAO, 3 Repository |
| 6 | **data:calllog** | 4 | Android CallLog ContentProvider 래퍼 |
| 7 | **data:contacts** | 4 | Android Contacts ContentProvider 래퍼 |
| 8 | **data:sms** | 4 | Android SMS ContentProvider 래퍼 |
| 9 | **data:search** | 20+ | 8개 웹 스크레이핑 검색 제공자 (Google/Naver/Baidu/Yahoo JP/Yandex/DDG 등) |
| 10 | **feature:call-intercept** | 40 | 전화 수신 파이프라인 전체 (2-Phase, 4-Route, 오버레이, 알림) |
| 11 | **feature:decision-engine** | 7 | 4축 평가 엔진 (디바이스+검색+학습+행동) → DecisionResult |
| 12 | **feature:decision-ui** | 23 | Ring 컴포넌트, Compose UI, 프로그레시브 렌더링 |
| 13 | **feature:push-intercept** | 2 | NotificationListenerService 기반 푸시 분석 |
| 14 | **feature:message-intercept** | 2 | SMS BroadcastReceiver 기반 문자 분석 |
| 15 | **feature:privacy-check** | 16 | 카메라/마이크 권한 감시, AppOps 리스너, 이상 탐지 |
| 16 | **feature:settings** | 5 | SharedPreferences 기반 설정 (언어/국가/표시 수준) |
| 17 | **feature:billing** | 7 | Google Play Billing, 구독 상태, 탬퍼 체크 연동 |
| 18 | **feature:country-config** | 11 | 190개국 설정, 다국어 키워드, 가격 정책, 신호 요약 번역 |
| 19 | **feature:search-enrichment** | 0 | **빈 모듈** — 디렉토리 구조만 존재 |

---

## 3. 데이터 흐름

### 3.1 전화 수신 흐름

```
Android 전화 시스템
    │
    ▼
MyPhoneCheckScreeningService.onScreenCall(callDetails)
    │
    ├─ 1. 전화번호 추출 + E.164 정규화 (PhoneNumberNormalizer)
    ├─ 2. 국가 감지 (SIM → Network → Locale → "ZZ" fallback)
    ├─ 3. 주소록 조회 (ContactsDataSource)
    │
    ▼
CallInterceptRepositoryImpl.analyzeIdentifierTwoPhase(input)
    │
    ├─ InterceptPriorityRouter.route(input)
    │   ├─ SKIP: 긴급번호/자기번호 → 즉시 ALLOW (0ms)
    │   ├─ INSTANT: 저장된 연락처 → 캐시 전용 (5ms)
    │   ├─ LIGHT: 디바이스 증거만 (200ms)
    │   └─ FULL: 전체 4축 파이프라인 (4500ms 하드 데드라인)
    │
    ├─ Phase 1: PreJudgeCacheRepository 조회 (0~5ms)
    │   └─ Tier 0 캐시 히트 → 즉시 Phase1Result 반환
    │
    └─ Phase 2: 병렬 증거 수집
        ├─ DeviceEvidenceProvider (CallLog + Contacts + SMS)
        ├─ SearchEvidenceProvider (8개 웹 검색 엔진, 국가별 라우팅)
        ├─ LocalLearningProvider (사용자 과거 행동 이력)
        └─ BehaviorPatternSignal (시간대/빈도/VoIP 패턴)
            │
            ▼
        DecisionEngine.evaluate(device, search, learning, behavior)
            ├─ 관계 점수 계산 (저장 연락처, 발신 이력, 통화 빈도)
            ├─ 위험 점수 계산 (검색 신호, 짧은 통화, 거부 이력)
            ├─ 7-카테고리 우선순위 트리:
            │   KNOWN_CONTACT → SCAM_RISK_HIGH → SALES_SPAM
            │   → DELIVERY → INSTITUTION → BUSINESS → INSUFFICIENT_EVIDENCE
            ├─ 신뢰도 계산 (카테고리별 하한선 적용)
            └─ 중요도 판정 (DO_NOT_MISS / IMPORTANT / NORMAL / UNKNOWN)
                │
                ▼
            DecisionResult {category, riskLevel, action, confidence, reasons}
                │
                ├─ PreJudgeCacheRepository에 Tier 0 저장
                ├─ NumberProfileRepository에 프로필 갱신
                │
                ▼
        OverlayPresenter.present(result)
            ├─ CallerIdOverlayManager.showOverlay()
            │   └─ WindowManager에 오버레이 뷰 추가
            │       ├─ 전화번호 + 국가 플래그
            │       ├─ Ring 색상 (SAFE/CAUTION/DANGER/UNKNOWN)
            │       ├─ 판단 문장 + 이유 3줄
            │       └─ 액션 버튼 (응답/거부/차단/중요)
            │
            └─ DecisionNotificationManager.showDecisionNotification()
                └─ BigTextStyle 알림 (Ring 색상 매핑)

사용자 액션
    │
    ▼
CallActionReceiver.onReceive()
    ├─ UserCallRecordRepository에 행동 기록 (학습 데이터)
    ├─ NumberProfileRepository 차단 상태 갱신
    ├─ BlocklistRepository 업데이트
    └─ OverlayDismissReceiver → 오버레이 제거
```

### 3.2 문자 수신 흐름

```
Android SMS 시스템
    │
    ▼
SmsInterceptReceiver.onReceive(SMS_RECEIVED_ACTION)
    │
    ├─ 1. SmsMessage 파싱 (PDU → sender + body)
    ├─ 2. 중복 필터 (SHA-256 해시, 5초 윈도우)
    ├─ 3. 연락처 조회 (ContactsDataSource)
    │
    ▼
MessageTextAnalyzer.analyze(body, sender, isContact)
    ├─ URL 추출 + 단축 링크 탐지
    ├─ 다국어 키워드 매칭:
    │   ├─ 사칭 키워드 (은행, 정부기관, 택배)
    │   ├─ 피싱 키워드 (인증, 비밀번호, 계좌)
    │   ├─ 긴급 키워드 (즉시, 마감, 정지)
    │   └─ 금융 키워드 (송금, 대출, 투자)
    └─ → MessageEvidence
        │
        ▼
CallInterceptRepository.analyzeIdentifier(input)
    └─ DecisionEngine.evaluate() → DecisionResult
        │
        ▼
MessageHubDao에 구조화 저장 (JSON 배열: 링크, 키워드)
    └─ 위험 문자 → 알림 발송
```

### 3.3 푸시 수신 흐름

```
Android 알림 시스템
    │
    ▼
PushInterceptService.onNotificationPosted(sbn)
    │
    ├─ 1. 패키지명 + 알림 텍스트 추출
    ├─ 2. 앱별 통계 집계 (24h / 7d 빈도)
    │
    ▼
PushCheckEngine.evaluate(PushEvidence)
    ├─ 빈도 점수 (24h > 10건, 7d > 50건)
    ├─ 프로모션 키워드 매칭
    ├─ 심야 알림 탐지 (22시~06시)
    ├─ 상호작용률 분석 (무시율 높으면 위험 가산)
    └─ → DecisionResult
        │
        ├─ PushStatsDao에 통계 저장
        ├─ MessageHubDao에 이벤트 저장
        └─ 스팸 판정 시 → 알림 자동 취소 (cancelNotification)
```

### 3.4 프라이버시 감지 흐름

```
앱 시작 (Application.onCreate)
    │
    ▼
InitialScanOrchestrator.runInitialScan()
    │
    ├─ InitialScanGuard.check()
    │   └─ 6개 조건 검증:
    │       ├─ 기준 국가 존재 여부
    │       ├─ 기준 언어 존재 여부
    │       ├─ 기준 시간대 존재 여부
    │       ├─ 카메라 기준선 존재 여부
    │       ├─ 마이크 기준선 존재 여부
    │       └─ 센서 스캔 유효기간
    │   → GuardResult (PASS / FAIL + reasons)
    │
    ├─ shouldRun() == true 이면:
    │
    ▼
병렬 스캔 실행
    ├─ CameraCheckScanner.scan()
    │   ├─ PermissionGrantedCollector.collectByGroup(CAMERA)
    │   ├─ PrivacyScannerEngine.scanPermissionHolders()
    │   ├─ PrivacyScannerEngine.queryAllowedApps()
    │   └─ → SensorCheckState (전체 앱 목록 + 위험 앱 표시)
    │
    └─ MicCheckScanner.scan()
        └─ (동일 구조, RECORD_AUDIO 대상)
            │
            ▼
        DeviceBaselineCollector.collect()
            └─ 기기 컨텍스트: 국가, 언어, 시간대, 설치 앱 수
                │
                ▼
        SensorScanResultDao에 결과 저장
        InitialScanMetaDao에 스캔 메타 저장

Android 12+ 실시간 감시 (별도 경로):
    │
    ▼
PrivacyScannerForegroundService
    ├─ AppOpsManager.startWatchingActive(CAMERA, RECORD_AUDIO)
    └─ 접근 감지 시:
        ├─ PrivacyCheckCollector.collect()
        ├─ PrivacyCheckEngine.evaluate(PrivacyEvidence)
        │   ├─ 이상 탐지 5유형:
        │   │   ├─ 백그라운드 접근
        │   │   ├─ 심야 접근 (23시~05시)
        │   │   ├─ 신규 앱 (7일 미만)
        │   │   ├─ 최초 접근
        │   │   └─ 비정상 빈도
        │   └─ 위험 권한 조합 가중 (카메라+마이크, 위치+연락처)
        └─ 위험 판정 시:
            ├─ PrivacyHistoryDao에 이력 저장
            └─ 알림 발송 (확인/설정 버튼)
                └─ PrivacyConfirmReceiver → 사용자 확인 처리
```

---

## 4. 기능 현황표

| # | 기능 | 모듈 | 상태 | 근거 |
|---|------|------|------|------|
| 1 | 전화 수신 판단 (2-Phase 파이프라인) | call-intercept | **DONE** | CallInterceptRepositoryImpl 4-Route 분기, Phase 1/2 전체 구현 |
| 2 | CallScreeningService 연동 | call-intercept | **DONE** | MyPhoneCheckScreeningService.onScreenCall() 완전 구현 |
| 3 | 발신자 오버레이 표시 | call-intercept | **DONE** | CallerIdOverlayManager + WindowManager 완전 구현 |
| 4 | 판단 알림 (BigText) | call-intercept | **DONE** | DecisionNotificationManager RingSystem 색상 매핑 |
| 5 | 사용자 액션 처리 (응답/거부/차단) | call-intercept | **DONE** | CallActionReceiver 브로드캐스트 체인 |
| 6 | 디바이스 증거 수집 | device-evidence | **DONE** | 병렬 async (CallLog + Contacts + SMS) |
| 7 | 4축 판단 엔진 | decision-engine | **DONE** | 7-카테고리 트리, 5-액션 매핑, 신뢰도 계산 |
| 8 | Ring 비주얼 시스템 | decision-ui | **DONE** | 4-상태 애니메이션 (LOADING/SAFE/CAUTION/DANGER) |
| 9 | Compose 판단 카드 UI | decision-ui | **DONE** | 프로그레시브 렌더링, 이유 3줄, 면책 문구 |
| 10 | 문자 수신 분석 | message-intercept | **DONE** | 다국어 키워드 매칭, URL 추출, 중복 필터 |
| 11 | 푸시 알림 분석 | push-intercept | **DONE** | NotificationListenerService, 빈도/프로모션/심야 점수 |
| 12 | 카메라/마이크 감시 | privacy-check | **DONE** | 초기 스캔 + AppOps 실시간 리스너 (API 29+) |
| 13 | 프라이버시 이상 탐지 | privacy-check | **DONE** | 5-유형 이상 + 위험 권한 조합 가중 |
| 14 | 190개국 설정 | country-config | **DONE** | 국가별 포맷, 긴급번호, 검색 라우팅, 가격 정책 |
| 15 | 전화번호 정규화 (libphonenumber) | core:util | **DONE** | 다단계 해석 (정적 글로벌 → 기기 프로필 → fallback) |
| 16 | 검색 변환 생성 (8개 포맷) | core:util | **DONE** | E.164, 국가, 국제, 숫자만, 구분자 등 |
| 17 | 웹 검색 (8개 제공자) | data:search | **PARTIAL** | 8개 스크레이퍼 구현됨. SearchResultAnalyzer 키워드 추출 **STUB** ("TBD: NLP pipeline") |
| 18 | Room DB (SQLCipher 암호화) | data:local-cache | **DONE** | 11 Entity, 10 DAO, 3 Repository |
| 19 | Tier 0 캐시 (PreJudge) | data:local-cache | **DONE** | 7일/30일 soft-expire 감쇠 로직 |
| 20 | 보안 탐지 (루트/후킹/리패키징) | core:security | **DONE** | 3-축 탐지 + 랜덤 지연 (50~300ms) |
| 21 | Keystore + SQLCipher 키 관리 | core:security | **DONE** | AES-256-GCM, Base64 직렬화 |
| 22 | Google Play 결제 | billing | **DONE** | BillingClient 연동, 탬퍼 체크, 암호화 캐시 |
| 23 | 설정 화면 | settings | **DONE** | 언어/국가/표시수준, Flow 반응형 |
| 24 | 백업/복원 (AES-256-GCM) | app | **DONE** | PBKDF2 키 파생, CSB v2 포맷 |
| 25 | 주간 리포트 | app | **DONE** | WorkManager 매주 월요일 09:00 |
| 26 | 네비게이션 | app | **DONE** | 16개 Compose route (onboarding → home → 각 엔진 상세) |
| 27 | 차단 목록 영속화 | call-intercept | **PARTIAL** | BlocklistRepositoryImpl — 인메모리만. Room 연동 TODO |
| 28 | 검색 강화 모듈 | search-enrichment | **EMPTY** | 디렉토리 구조만 존재, .kt 파일 0개 |
| 29 | First-Run 온보딩 | decision-ui | **PARTIAL** | 기본 골격만 존재 |
| 30 | 5개 locale 번역 (ar/es/ja/ru/zh) | 전체 | **PARTIAL** | 한국어+영어만 완성. 나머지 550건 미번역 (lint warning 강등 처리) |

---

## 5. 현재 문제점 및 미완성 항목

### 5.1 코드 레벨 문제

| # | 심각도 | 모듈 | 문제 | 상세 |
|---|--------|------|------|------|
| 1 | **HIGH** | data:search | SearchResultAnalyzer 키워드/엔티티 추출 STUB | 주석 "TBD: NLP pipeline" — 검색 결과를 파싱해도 구조화된 신호로 변환하는 핵심 로직 미구현 |
| 2 | **HIGH** | feature:search-enrichment | 모듈 전체 EMPTY | .kt 파일 0개. data:search의 SearchEnrichmentRepository가 직접 사용되고 있어 당장은 동작하지만, 설계상 이 모듈이 중간 계층 역할을 해야 함 |
| 3 | **MEDIUM** | feature:call-intercept | BlocklistRepositoryImpl 인메모리 전용 | 앱 재시작 시 차단 목록 소실. Room Entity 필요 |
| 4 | **MEDIUM** | feature:decision-ui | FirstRunScreen 골격만 존재 | 온보딩 UX 미완성 |
| 5 | **MEDIUM** | 전체 | 5개 locale 550건 미번역 | ar/es/ja/ru/zh-rCN strings.xml — lint warning으로 강등 처리되어 빌드는 통과하나 실사용 불가 |
| 6 | **LOW** | core:security | RepackageDetector 서명 해시 플레이스홀더 | `EXPECTED_SIGNATURE_HASH = "PLACEHOLDER_REPLACE_WITH_ACTUAL_RELEASE_SIGNING_HASH"` — 릴리스 키스토어 해시로 교체 필요 |
| 7 | **LOW** | feature:call-intercept | 실험적 파일 다수 (10+) | CircuitBreakerRehearsalRunner, SlaStressTester 등 — POC/벤치마크 코드가 프로덕션 모듈에 혼재 |

### 5.2 아키텍처 레벨 문제

| # | 문제 | 설명 |
|---|------|------|
| 1 | search-enrichment 모듈 역할 부재 | call-intercept가 data:search를 직접 참조. 설계상 feature:search-enrichment가 중간 계층이어야 하나 빈 상태 |
| 2 | 실험 코드와 프로덕션 코드 미분리 | call-intercept에 벤치마크/시뮬레이션/SLA 테스터가 프로덕션 소스에 포함 |
| 3 | consumer-rules.pro 미적용 | core:security만 consumer-rules.pro 있음. 다른 library 모듈은 app R8에 keep 규칙 전달 안 됨 |

---

## 6. 다음 작업 우선순위

| 순위 | 작업 | 근거 |
|------|------|------|
| **P0** | 실기기 검증 (스크린샷 5종 + logcat 8태그) | assembleDebug + check 통과 확인됨. 실기기 구동 증거 미확보 |
| **P1** | SearchResultAnalyzer 키워드 추출 구현 | 검색 엔진이 HTML을 가져오지만 구조화 신호 변환이 STUB — FULL Route 판단 품질에 직접 영향 |
| **P2** | BlocklistRepositoryImpl Room 영속화 | 앱 재시작 시 차단 목록 소실 — 사용자 경험 직접 타격 |
| **P3** | 5개 locale 번역 550건 | 글로벌 출시 전 필수. 현재 lint warning 강등으로 빌드만 통과 |
| **P4** | search-enrichment 모듈 구현 또는 제거 | 빈 모듈이 의존성 그래프에 존재 — 설계 의도 확정 필요 |
| **P5** | 실험 코드 분리 (call-intercept → benchmark 모듈) | 프로덕션 모듈 경량화 |
| **P6** | RepackageDetector 서명 해시 교체 | 릴리스 빌드 전 필수 |
| **P7** | FirstRunScreen 온보딩 완성 | 신규 사용자 전환율 영향 |

---

## 부록 A. 네비게이션 라우트 맵

```
onboarding → home
home → message-hub
home → privacy-history
home → camera-check
home → mic-check
home → engine/call
home → engine/message
home → engine/privacy
home → purchase
home → timeline → call-detail/{number}
home → settings → backup
settings → onboarding (리셋)
```

## 부록 B. Hilt DI 모듈 목록

| DI Module | Scope | 제공 대상 |
|-----------|-------|-----------|
| SecurityModule | Singleton | DatabaseKeyProvider, TamperChecker, RootDetector, HookDetector, RepackageDetector |
| CallLogModule | Singleton | CallLogDataSourceImpl |
| ContactsModule | Singleton | ContactsDataSourceImpl |
| SmsModule | Singleton | SmsMetadataDataSourceImpl |
| SearchModule | Singleton | SearchEnrichmentRepositoryImpl, SearchProviderRegistry |
| LocalCacheModule | Singleton | MyPhoneCheckDatabase, 10 DAOs, 3 Repositories |
| DecisionEngineModule | Singleton | DecisionEngineImpl, ActionMapper, RiskBadgeMapper, SummaryGenerator |
| DecisionUiModule | Singleton | DecisionViewModel 바인딩 |
| BillingModule | Singleton | BillingManager (TamperChecker 주입) |
| CountryConfigModule | Singleton | CountryConfigProviderImpl, LanguageContextProviderImpl |
| SettingsModule | Singleton | SettingsRepositoryImpl |

## 부록 C. 주요 데이터 클래스 관계

```
IdentifierAnalysisInput
    ├─ phoneNumber: String (raw)
    ├─ channel: IdentifierChannel (CALL / SMS / PUSH)
    └─ actionState: UserCallAction?

        ↓ 평가

DecisionResult
    ├─ category: ConclusionCategory (28종)
    ├─ riskLevel: RiskLevel (HIGH / MEDIUM / LOW / UNKNOWN)
    ├─ action: ActionRecommendation (5종)
    ├─ confidence: Float (0.0 ~ 1.0)
    ├─ reasons: List<String> (최대 3개)
    ├─ importanceLevel: ImportanceLevel
    └─ summary: String

        ↓ 2-Phase 래핑

TwoPhaseDecision
    ├─ phase1: DecisionResult (캐시/정책, 0~5ms)
    ├─ phase2: DecisionResult? (전체 파이프라인, 150~4500ms)
    ├─ metrics: InterceptMetrics
    └─ phaseConflict: Boolean (Phase 1↔2 불일치 감지)
```
