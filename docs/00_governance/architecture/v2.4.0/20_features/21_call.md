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
