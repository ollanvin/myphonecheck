# 5. 시스템 아키텍처 (디바이스 = 모든 것)

**원본 출처**: v1.7.1 §5 + §18 CallCheck (599–700 + 1847–1896)
**v1.9.0 Layer**: Feature
**의존**: `00_core/01_primary.md`
**변경 이력**: v1.9.0 Six Surfaces 구조 및 용어 정정 반영.
**파일 경로**: `docs/00_governance/architecture/v1.9.0/20_features/21_call.md`

---

# 5. 시스템 아키텍처 (디바이스 = 모든 것)

## 5-1. 3-Layer Knowledge Sourcing (3계층 지식 소싱)

디바이스가 판단에 사용하는 지식은 **3개 계층**에서 온다. 헌법 제3조 "결정권 중앙집중 금지"의 구체 구현.

| 계층 | 이름 | 소스 | 지연 | L3 가용 |
|---|---|---|---|---|
| Layer 1 | **내부축** | 온디바이스 NKB + 통화·문자 이력 + 사용자 태그 | ≤ 5ms | 항상 |
| Layer 2 | **외부축** | 일반 검색 엔진 (Google Programmable Search·Custom Tab) | 수백 ms ~ 수 초 | 네트워크 필요 |
| Layer 3 | **오픈소스축** | 공공 공신력 DB (KISA·경찰청·금감원·NVD·CISA·Have I Been Pwned) | 수백 ms ~ 수 초 | 네트워크 필요 |

(메모리 #8 확정)

### 5-1-1. Layer 1 (내부축) 구성

- **NKB (Number Knowledge Base)**: 번호별 4속성 캐시 (§8)
- **통화 이력**: `CallLog.Calls` ContentResolver (READ_CALL_LOG 권한)
- **문자 이력**: `Telephony.Sms` ContentResolver (READ_SMS 권한, Default SMS Handler 불필요)
- **사용자 태그**: `UserAction` Entity (§8-2)

### 5-1-2. Layer 2 (외부축) 구성

- **Primary**: Google Programmable Search API (키는 온디바이스 앱 서명 기반 제한)
- **Backup**: Bing Search API (대표님 판단 필요 — 메모리에 기재)
- **UX 1차 진입**: Custom Tabs (Android) / SFSafariViewController (iOS) — 사용자가 브라우저 열기
- **쿼터 제한**: 1사용자 / 월 100회 기본 (Google PSE 무료 한도 100/일 공유)
- **결과 처리**: 메모리만 통과 → SearchResultAnalyzer에서 featureCounts 추출 → 원문 폐기 (제2조)

### 5-1-3. Layer 3 (오픈소스축) 구성

- **KISA 스팸 공개 데이터**: 조사 필요 (메모리 #8)
- **경찰청 사이버범죄 공개 데이터**: 보이스피싱 번호
- **금융감독원 전기통신금융사기**: 금융 사기 번호
- **각국 Do-Not-Call 레지스트리**: 글로벌 확장용 (후행)
- **NVD CVE API**: 앱 CVE 감지 (후행 Surface `AppSecurityWatch`, §17-3 · Patch 31로 MicCheck/CameraCheck에서 분리 이관)
- **CISA KEV Catalog**: 실제 악용 취약점
- **Have I Been Pwned API**: 데이터 침해 사고 (있으면 좋음)

오픈소스축 **데이터 수집 파이프라인은 별도 스펙 작성 필요** (메모리 #8 명시).

## 5-2. 본사가 하지 않는 것 / 디바이스가 하는 것

| # | 본사 (0) | 디바이스 (1) |
|---|---|---|
| 1 | 사용자 번호 DB 운영 | NKB Local Room DB |
| 2 | 사용자 행동 로그 수집 | UserAction Entity (디바이스 내) |
| 3 | 사기 번호 중앙 매핑 | 디바이스 probe + 3계층 결과 통합 |
| 4 | 사용자별 모델 재학습 | 디바이스 Softmax 가중치 + 로컬 규칙 |
| 5 | 영수증 검증 서버 | Google Play Billing / StoreKit 2 온디바이스 서명 검증 |
| 6 | 결제 상태 중앙 DB | 디바이스 Purchase Token + 스토어 API |
| 7 | 고객지원 백엔드 | 이메일 기반 (지원 이메일, 메모리 #4 인프라) |
| 8 | 국가별 큐레이션 콘텐츠 | 디바이스 Self-Discovery (§7) |

## 5-3. 디바이스 내부 구조도 (개념)

```
┌─────────────────────────────────────────────────────────┐
│  Surface Layer (UI)                                      │
│  ┌─────────┐ ┌────────────┐ ┌─────────┐                │
│  │CallCheck│ │MessageCheck│ │PushCheck│                │
│  └────┬────┘ └─────┬──────┘ └────┬────┘                │
│       │            │             │                     │
│       └────────────┼─────────────┘                     │
│                    ▼                                   │
│  ┌─────────────────────────────────────────────────────┐ │
│  │ Decision Engine (Single)                            │ │
│  │   - evaluate(IdentifierType): RiskKnowledge         │ │
│  │   - ConflictResolver (Softmax + Tier 가중치)        │ │
│  │   - StaleDetector (Tier별 maxAge)                   │ │
│  └─────┬────────────┬──────────────┬────────────────────┘ │
│        │            │              │                       │
│        ▼            ▼              ▼                       │
│  ┌──────────┐ ┌──────────┐ ┌──────────────┐             │
│  │ Layer 1  │ │ Layer 2  │ │ Layer 3      │             │
│  │ NKB DB   │ │ Search   │ │ Public APIs  │             │
│  │ (Room)   │ │ Mesh     │ │ (KISA·CVE 등)│             │
│  └────┬─────┘ └────┬─────┘ └──────┬───────┘             │
│       │            │              │                       │
│       └────────────┴──────────────┘                       │
│                                                           │
│  ┌──────────┐  ┌──────────────┐  ┌───────────┐           │
│  │MicCheck  │  │CameraCheck   │  │CardCheck  │           │
│  │(Pkg Scan)│  │(Pkg Scan)    │  │(SMS/Push) │           │
│  └──────────┘  └──────────────┘  └───────────┘           │
│                    │                                       │
│                    ▼                                       │
│  ┌───────────────────────────────────────────────────────┐ │
│  │ Self-Discovery (§7) + Cold Start (§11)                │ │
│  │ + Self-Evolution (§12) + SLA Level Detector (§14)     │ │
│  └───────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────┘
```

Decision Engine은 단 하나이며 위협 평가 3 Surface(CallCheck·MessageCheck·PushCheck)가 공유한다. MicCheck·CameraCheck·CardCheck는 각자 로컬 로직으로 가치를 추출한다.

## 5-4. 데이터 흐름 (Dataflow) 불변 규칙

| 규칙 | 정의 | 헌법 조항 |
|---|---|---|
| R1 | 외부 원문은 Decision Engine 메모리를 벗어나지 않는다 | 제2조 |
| R2 | NKB에는 featureCounts·카테고리·시간 스탬프만 저장 | 제2조 |
| R3 | Decision Engine 출력(RiskKnowledge)만 Surface Layer로 전달 | 제7조 |
| R4 | Surface Layer에서 수집한 사용자 조치는 UserAction으로 NKB에 저장 | 제1·7조 |
| R5 | 외부 네트워크는 Layer 2·3 내부 클라이언트만 수행, Surface에서 직접 호출 금지 | 제1·3조 |
| R6 | 결제·구독 데이터는 Billing Module이 단일 진입점, Decision Engine 우회 | 제1조 |

# 18. 스모크런 시나리오 + Six Surfaces 본문

스모크런(Smoke Run)은 **구현 완료 즉시 수행**하는 제품 작동 검증. v1.5.2에서 11개 시나리오 정의, v1.6.0에서 Four Surfaces 관련 시나리오 확장, v1.9.0에서 Six Surfaces 기준으로 해석을 갱신한다.

## 18-1. 스모크런 11개 시나리오

| # | 이름 | 목적 | Surface | SLA 레벨 |
|---|---|---|---|---|
| SmokeRun01 | 기본 Cold Start | 설치 직후 Day 0 부트 | 공통 | L1 |
| SmokeRun02 | 착신 오버레이 | 전화 수신 시 4속성 표시 | CallCheck | L1 |
| SmokeRun03 | Softmax 분포 | 신호 100개에서 topConfidence 산출 | 엔진 | L1 |
| SmokeRun04 | 사용자 Override | "안심 표시" 후 NKB 재계산 | 엔진 | L1 |
| SmokeRun05 | 연락처 상호작용 | 연락처 등록 번호는 SAFE 초기값 | Cold Start | L1 |
| SmokeRun06 | MessageCheck 3중 평가 | 발신번호 + URL + 기관명 평가 결합 | MessageCheck | L1 |
| SmokeRun07 | MessageCheck 시나리오 | "쿠팡 배송 알림" 사칭 SMS 검출 | MessageCheck | L1 |
| SmokeRun08 | MicCheck 기본 | RECORD_AUDIO 보유 앱 스캔 + 평가 | MicCheck | L1 |
| SmokeRun09 | CameraCheck 기본 | CAMERA 보유 앱 스캔 + 평가 | CameraCheck | L1 |
| SmokeRun10 | Billing 주기 | 구독 구매 → 상태 업데이트 → 만료 감지 | Billing | L1 |
| SmokeRun11 | L3 Offline 기준선 | 비행기 모드 + NKB 200건에서 p95 ≤ 5ms | 공통 | **L3** |

## 18-2. SmokeRun01: 기본 Cold Start

**조건**: 신규 설치, 모든 권한 허용, 네트워크 연결
**절차**:
1. 앱 최초 실행
2. 온보딩 슬라이드 4개 → "시작하기"
3. 권한 순차 요청 (READ_PHONE_STATE·READ_CALL_LOG·READ_SMS·READ_CONTACTS)
4. Cold Start 6단계 실행 (§11)
5. Self-Discovery 실행 → ClusterProfile 생성 확인
6. 메인 화면 진입

**검증 포인트**:
- NKB에 초기 엔트리 500~1000건 생성 (통화·문자·연락처 기반)
- ClusterProfile 1건 저장, discoveredEngines 비어있지 않음
- 전체 Cold Start 소요 시간 ≤ 30초

## 18-3. SmokeRun02: 착신 오버레이

**조건**: L1, NKB 캐시 있는 번호 "+821012345678" (MEDIUM Risk)
**절차**:
1. 에뮬레이터 또는 실기기에서 해당 번호로 통화 수신
2. 오버레이 렌더 시점 측정
3. FourAttributeCard 내용 확인
4. "차단 / 신고 / 안심" 3버튼 탭 → UserAction 기록 확인

**검증 포인트**:
- 수신 감지 → 오버레이 렌더 ≤ 500ms (p95)
- 4속성 모두 표시 (null·plaintext 없음)
- 버튼 탭 시 NKB 재계산 트리거

---

## §direct-search. "직접 검색" 버튼 spec (v2.4.0 신설, 2026-04-29 대표님 결정)

### 목적

Risk Tier "Unknown" 영역 사용자 의사결정 보조. 옛 MyPhoneCheck v1에서 "확실하게 피싱·스팸이라고 판단하기 어려운 애매한 번호"가 사용자에게 가장 답답한 영역이었던 점 정정.

본 Surface에서 사용자가 3액션 (차단/태그/검색) 외 **"직접 확인" 옵션**을 행사할 수 있도록 "🔍 직접 검색" 버튼을 prominent 배치한다.

### 동작

```
[🔍 직접 검색] 탭
  ↓
SIM 기준 AI 검색 후보 메뉴 (최소 2개) 표시
  - SimAiSearchRegistry가 SIM countryIso 기반 후보군 추출
  - 예) KR SIM: Naver AI / Google AI Mode (udm=50) / Bing Copilot
  - 예) JP SIM: Yahoo Japan AI / Google AI Mode / Bing Copilot
  - 예) Global default: Google AI Mode / Bing Copilot
  - 사용자 자율 결정, 고정 우선순위 없음, 마지막 선택 기억
  - 모두 Custom Tab 직접 진입 (헌법 §1 정합)
  ↓
사용자가 검색 결과 보고 본인 판단 (AI 모드가 공공 + 경쟁사 reverse 자체 통합)
  ↓
사용자 행동:
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

### §direct-search-call. CallCheck 배치 위치

**수신 중 (CallScreeningService UI)**:
- Android API: `CallScreeningService.onScreenCall()` 5초 안에 자체 UI 표시 (caller ID 기능)
- 위치: 발신 번호 + Tier 표시 옆에 "🔍 직접 검색" 버튼
- 제약: 5초 안에 `respondToCall()` 호출해야 하므로 직접 검색 진입은 사용자 탭 시 별도 액티비티로 분리

**통화 종료 후 (오버레이)**:
- Android 권한: `SYSTEM_ALERT_WINDOW` (사용자 명시 허용)
- 위치: 통화 종료 직후 floating card에 "🔍 직접 검색" 버튼
- 대안: SAW 권한 거부 시 알림 카드 (Notification with action)

**모듈**: `:feature:call-intercept` (기존)

---

### CallCheck 3액션 (v2.6.0 정합)

CallCheck는 헌법 §11 정합 — 다음 3액션만 노출:

1. **[차단]** — 발신 번호 영원히 차단. CallScreeningService `setDisallowCall(true)` + skip log/notification.
2. **[태그]** — 발신 번호에 라벨 부여 (자유 텍스트). NKB DAO 영속화.
3. **[🔍 직접 검색]** — SIM 기준 AI 검색 후보 메뉴 → Custom Tab.

**미포함 액션** (시스템 dialer 책임, 우리 미간섭):
- 수신 / 받기
- 거절 / 받지 않기
- 자동 차단 / 자동 무음

**시스템 dialer 미간섭 정공법**:
- 사용자 콜 풀스크린 UI = 시스템 dialer (구글 폰 / 삼성 통화 / 통신사 dialer)
- 우리 오버레이 = 정보 카드 + 3액션 버튼 (TYPE_APPLICATION_OVERLAY)
- 사용자가 콜 받기·거절은 시스템 dialer에서 직접
- 우리 [차단] 누르면 다음 동일 번호 수신 시점에 CallScreeningService가 시스템에 차단 명령

**CallScreeningService 활성 조건**:
- 기본 전화 앱 권한 미필요 (API 29+)
- 사용자가 시스템 설정에서 "스팸 차단 앱" 으로 우리 앱 선택 시 활성
- 거절 시 우리 차단 작동 안 함, 시스템 dialer 단독 작동
- `RoleManager.ROLE_DIALER` / `ACTION_CHANGE_DEFAULT_DIALER` 영구 미사용 (헌법 §3 정합)
