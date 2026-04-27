# 시스템 아키텍처 (디바이스 = 모든 것)

**원본 출처**: v1.7.1 §5 (104줄)
**v1.8.0 Layer**: Product Design
**의존**: `00_core/01_primary.md` + `05_constitution.md`
**변경 이력**: 본 파일은 v1.7.1 §5 원본 전문 이관본. 텍스트 변경 없음.
**파일 경로**: `docs/00_governance/architecture/v1.8.0_cowork/06_product_design/04_system_arch.md`

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
│  ┌─────────┐ ┌────────────┐ ┌────────┐ ┌──────────┐    │
│  │CallCheck│ │MessageCheck│ │MicCheck│ │CameraCheck│    │
│  └────┬────┘ └─────┬──────┘ └───┬────┘ └─────┬────┘    │
│       │            │            │            │          │
│       └────────────┼────────────┼────────────┘          │
│                    ▼            ▼                         │
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
│                    │                                       │
│                    ▼                                       │
│  ┌───────────────────────────────────────────────────────┐ │
│  │ Self-Discovery (§7) + Cold Start (§11)                │ │
│  │ + Self-Evolution (§12) + SLA Level Detector (§14)     │ │
│  └───────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────┘
```

Decision Engine은 단 하나이며 4 Surface가 공유. 엔진 분기 금지 (§17 One Engine).

## 5-4. 데이터 흐름 (Dataflow) 불변 규칙

| 규칙 | 정의 | 헌법 조항 |
|---|---|---|
| R1 | 외부 원문은 Decision Engine 메모리를 벗어나지 않는다 | 제2조 |
| R2 | NKB에는 featureCounts·카테고리·시간 스탬프만 저장 | 제2조 |
| R3 | Decision Engine 출력(RiskKnowledge)만 Surface Layer로 전달 | 제7조 |
| R4 | Surface Layer에서 수집한 사용자 조치는 UserAction으로 NKB에 저장 | 제1·7조 |
| R5 | 외부 네트워크는 Layer 2·3 내부 클라이언트만 수행, Surface에서 직접 호출 금지 | 제1·3조 |
| R6 | 결제·구독 데이터는 Billing Module이 단일 진입점, Decision Engine 우회 | 제1조 |

---

