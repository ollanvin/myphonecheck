# 30. :core:global-engine — 글로벌 단일 코어 엔진

> **신규 모듈 (v2.0.0)**: 모든 Surface가 사용하는 단일 글로벌 엔진.
> **One Engine, Six Surfaces** 헌법 정합 (v1.9.0 비전 모호 해석 정정).

---

## 30-1. 정의

**`:core:global-engine`**은 6 Surface 모두가 공유하는 단일 엔진. 카드사·국가·언어 분기 코드 0.

핵심 가치:
- **One Engine** — 모든 Surface가 동일 코어 사용 (자체 파서·매핑 금지)
- **Global Core** — 190국 단일 코어, 국가별 분기 0
- **검색 3대 축 통합** — 내부·외부·공개 피드 모두 코어 통합
- **SIM-Oriented** — 헌법 8조 정합, SimContext 단일 진실원

## 30-2. 구조

```
:core:global-engine/
├── sim-context/                    # §29 SIM 단일 진실원
│   ├── SimContext.kt
│   ├── SimContextProvider.kt
│   ├── UiLanguageResolver.kt
│   ├── SimChangeDetector.kt
│   └── CountryCurrencyMapper.kt
│
├── parsing/                        # 파싱 통합
│   ├── phone/
│   │   ├── PhoneNumberParser.kt    # libphonenumber 기반
│   │   └── PhoneNumberFormatter.kt
│   ├── message/
│   │   └── SmsPatternExtractor.kt  # 발신자·본문 패턴
│   ├── notification/
│   │   └── NotificationParser.kt   # NLS package + 채널
│   ├── currency/
│   │   ├── CurrencyAmountParser.kt # ICU NumberFormat (Stage 1-002 마이그레이션 대상)
│   │   └── CurrencyValidator.kt    # ISO 4217
│   └── timestamp/
│       └── TimestampParser.kt      # ICU DateFormat
│
├── search/                         # 검색 3대 축 (메모리 #5 정합)
│   ├── internal/
│   │   └── OnDeviceHistorySearch.kt    # 온디바이스 이력
│   ├── external/
│   │   └── CustomTabExternalSearch.kt  # Custom Tab (사용자 trigger)
│   └── public/
│       └── PublicFeedAggregator.kt     # 정부·통신사·보안사 공개 피드
│
└── decision/
    └── InputAggregator.kt          # 3대 축 → Surface별 결정 입력
```

## 30-3. Producer/Consumer 모델 (확장)

기존 v1.9.0 Producer/Consumer (데이터 소스 측면) + v2.0.0 코어 엔진 측면:

| Producer | 제공물 | Consumer |
|---|---|---|
| 데이터 소스 (Android OS) | SMS, CallLog, NLS, PackageManager | `:core:global-engine` (입력) |
| `:core:global-engine` | SimContext, parsing 결과, 검색 결과 | 6 Surface (모두) |
| 6 Surface | UI 가치, 사용자 알림 | 사용자 |

## 30-3-A. 데이터 계층 4-Layer 모델 (v2.1.0 신규)

CardCheck·CallCheck 등 모든 Surface가 활용하는 데이터는 4-Layer로 분리:

| Layer | 출처 | 영구 저장 | 외부 통신 | 헌법 정합 |
|---|---|---|---|---|
| **Layer 1** | OS 자원 (CallLog, SMS, Notification, PackageManager, Contacts) | 안 함 (OS가 보관) | 0 | §1·§2 |
| **Layer 2** | MyPhoneCheck 자체 (라벨·태그·차단·휴지통·카드) | Room DB v15 | 0 | §1·§2 |
| **Layer 3** | 외부 공개 피드 캐시 (옵트인 후) | Room DB 또는 파일 캐시 | 옵트인 다운로드만 | §1 옵트인 정합 |
| **Layer 4** | 외부 검색 (사용자 trigger) | 0 | Custom Tab만 | §1 사용자 trigger 정합 |

### 30-3-A-1. Layer 우선순위 조회 흐름

수신 이벤트 또는 Surface 조회 시:

```
1. Layer 2 (즉시, 사용자 본인 자료 = 최우선)
2. Layer 1 (OS 자원, 즉시)
3. Layer 3 (캐시, 옵트인 출처)
4. Layer 4 (필요 시 사용자 trigger)
```

`InputAggregator`는 Layer 2·1·3을 병렬 통합. Layer 4는 별도 인텐트.

### 30-3-A-2. Layer 3 하위 분류

Layer 3는 출처 성격에 따라 4유형으로 세분 (§30-4 참조):

```kotlin
sealed class FeedType {
    object SecurityIntelligence : FeedType()  // A. 글로벌 보안 (Abuse.ch, PhishTank)
    object GovernmentPublic : FeedType()      // B. 정부·공공기관 (KISA, FBI IC3)
    object CompetitorApp : FeedType()         // C. 경쟁 앱 데이터 (더콜·후후·Whoscall)
    object TelcoBlocklist : FeedType()        // D. 통신사 공개 차단
}
```

### 30-3-A-3. CountryScope

피드 출처는 적용 범위(국가/지역/글로벌)를 명시:

```kotlin
sealed class CountryScope {
    object GLOBAL : CountryScope()
    data class COUNTRY(val iso: String) : CountryScope()
    data class REGION(val isoList: List<String>) : CountryScope()
}
```

SIM countryIso 기반 자동 추천 시 CountryScope.COUNTRY 매칭이 우선.

## 검색 4대 축 (2026-04-29 v2.3.1 갱신)

본 4대 축은 결정 엔진(`:core:global-engine`)의 입력으로 통합된다. 사용자에게는 검색 결과를 직접 노출하지 않고, **Risk Tier + 근거 카드**만 표시한다 (헌법 §3 결정권 중앙집중 금지 + §5 정직성 정합).

### 축 1: 내부 (온디바이스 NKB)
- 사용자 본인 통화/문자/태그 이력
- 헌법 §1·§2 정합 (외부 송신 0)
- 가중치: 높음 (사용자 자신의 이력)

### 축 2: 공공 공신력 (Ground Truth)
- KISA 보이스피싱 신고 DB
- 경찰청 사이버범죄 신고
- 금감원 금융사기 신고
- 통신사 공개 차단번호 리스트
- 가중치: **최고** (ground truth)
- 헌법 §1 허용 명시

### 축 3: 외부 AI 검색 (사용자 직접 트리거)
- **1차**: Google AI Mode (`https://www.google.com/search?q={번호}&udm=50`)
- **Fallback**: Bing Copilot (`https://www.bing.com/search?q={번호}&showconv=1`)
- 한국 SIM: Naver Cue (별건 검증 후 추가)
- **방식**: Custom Tab 사용자 직접 진입. 우리 API 통합 0.
- 가중치: 중간 (사용자 직접 검색 시 입력으로 합산)
- 헌법 §1 정합 (사용자 본인 의지로 외부 검색)

### 축 4: 경쟁사 Reverse Lookup (사용자 직접 트리거)
- Truecaller 웹: `https://www.truecaller.com/search/{country}/{number}`
- Whoscall 웹: `https://whoscall.com/search/{number}`
- Hiya 웹: `https://hiya.com/phone-lookup/{number}`
- Should I Answer, Sync.ME 등 추가 가능
- **방식**: Custom Tab 사용자 직접 진입. 우리 비공식 API/scraper 통합 절대 금지.
- 가중치: 중간 (사용자 직접 검색 시 입력으로 합산)
- 헌법 §1 정합 (공개 웹 페이지 사용자 본인 진입 = 일반 검색과 동일 패턴)

### 4축 통합 → 결정 엔진
모든 축의 결과는 `DecisionEngine`이 가중치 합산 후 Risk Tier (Safe / Caution / Danger / Unknown) 출력. 사용자에게는 Tier + 근거 카드만 표시.

### "직접 검색" 버튼 (Risk Tier Unknown 영역 보조)
6 Surface 모두에 "🔍 직접 검색" 버튼 배치. 사용자가 결과 부족한 케이스에서 직접 4축 검색 진입 가능. 본인 판단으로 행동 결정 (수신/거절/차단 외 "직접 확인" 옵션).

배치:
- CallCheck 수신 화면 (CallScreeningService UI)
- CallCheck 통화 종료 오버레이 (SAW 또는 알림 카드)
- MessageCheck 문자 수신 알림 + 자체 UI 카드
- PushCheck 휴지통 항목별
- CardCheck 카드 알림 발신자 번호
- MicCheck/CameraCheck 권한 침해 알림 발신 앱

상세는 `20_features/21~32` 각 Surface 문서 (별건 WO-V230-SURFACES-DIRECT-SEARCH).

## 30-5. Decision Input Aggregator

각 Surface별 결정 입력 통합:

```kotlin
class InputAggregator(
    private val internal: OnDeviceHistorySearch,
    private val publicFeed: PublicFeedAggregator,
    private val external: CustomTabExternalSearch
) {
    fun aggregate(query: SurfaceQuery): SurfaceInput {
        // 1. 내부 검색 (필수, 즉시)
        // 2. 공개 피드 (옵트인됐으면)
        // 3. 외부 검색 (사용자 trigger 시에만)
        // 통합 결과 → Surface가 decision 또는 표시
    }
}
```

`InputAggregator`는 v1.9.0 §17 "Decision Engine"의 v2.0.0 정확화 형태. 위협 평가 한정이 아닌 **모든 Surface 활용 가능한 입력 통합기**.

## 30-6. 헌법 정합

| 조 | 정합 |
|---|---|
| 1 Out-Bound Zero | 외부 검색 = Custom Tab (사용자 trigger), 공개 피드 = 사용자 옵트인 |
| 2 In-Bound Zero | 외부 검색 결과 미수신, 공개 피드는 캐싱 후 가공·요약 |
| 3 결정 중앙집중 금지 | 코어 엔진은 디바이스 로컬, 외부 결정 없음 (v2.0.0 §3 강화 주석) |
| 4 자가 작동 | 내부 검색 + 공개 피드 캐시 = 오프라인 작동 |
| 5 정직성 | 검색·피드 결과 가공 없이 원본 표시 |
| 6 가격 정직성 | (코어 엔진은 가격 표시 영역 외, §16 Billing + §31 Pricing에서 측정. 통화 default 추론은 SimContext 활용 — 헌법 §8조 정합) |
| 7 Device-Oriented Goose | 모든 핵심 처리 온디바이스 |
| **8 SIM-Oriented Single Core** | SimContext = 코어 단일 진실원 |

## 30-7. 사용자 대면 약속

> **"외부 검색은 당신이 직접 버튼을 눌렀을 때만 실행되며, 결과는 우리가 받지 않습니다.**
> **공개 피드 다운로드는 당신의 동의 후에만 진행됩니다.**
> **모든 핵심 처리는 디바이스 안에서 이루어집니다."**

## 30-8. 마이그레이션 (v1.9.0 → v2.0.0)

기존 v1.9.0 Surface별 자체 파서·매핑 코드는 **점진 추출** (v2.0.0 PR은 명문화만):

| v1.9.0 위치 | v2.0.0 코어 위치 | Stage | WO |
|---|---|---|---|
| `:feature:card-check/parser/PatternExtractor.kt` (Stage 1-002 PR #14) | `:core:global-engine/parsing/currency/CurrencyAmountParser.kt` | Stage 2-001 | WO-V200-STAGE2-001 |
| (CallCheck 미구현) | `:core:global-engine/parsing/phone/` | Stage 2-002 | WO-V200-STAGE2-002 |
| (MessageCheck 미구현) | `:core:global-engine/parsing/message/` | Stage 2-003 | WO-V200-STAGE2-003 |
| `:feature:push-trash/util/` | `:core:global-engine/parsing/notification/` | Stage 2-004 | WO-V200-STAGE2-004 |
| (검색 3대 축 미구현) | `:core:global-engine/search/` | Stage 2-005 | WO-V200-STAGE2-005 |

각 Stage는 별도 PR. 본 v2.0.0 PR은 **명문화만**, 코드 마이그레이션은 후속.

**마이그레이션 원칙**:
- 기능 동일 (사용자 영향 0)
- 위치만 이동 (Surface 모듈 → 코어 모듈)
- 테스트 그대로 이전 (코어 모듈 단위 테스트로 재구성)
- Surface 모듈은 코어 모듈 의존하는 얇은 layer로 축소

## 30-9. v1.9.0 비전 모호 해석 정정

v1.9.0 §17·§36에서 비전이 "Decision Engine 공유"를 위협 평가 Surface 한정으로 해석한 것을 **v2.0.0에서 정정**:

- v1.9.0 해석: "위협 평가 Surface (CallCheck/MessageCheck/PushCheck)만 Decision Engine 공유"
- v2.0.0 정정: "모든 Surface가 `:core:global-engine` 단일 코어 사용. Decision Engine은 코어 내부 모듈 (`decision/InputAggregator.kt`)"

이유:
- "One Engine" 본질 = 모든 Surface가 동일 코어 사용 = 일관성 보장
- 위협 평가·가치 추출·인벤토리 구분 없이 모두 동일 코어 사용 가능
- Surface별 자체 파서 허용은 카드사·국가 분기 코드 발생 위험 (헌법 8조 위반 가능성)

## 30-10. cross-ref

- §28 Initial Scan (코어 활용 입력 단계)
- §29 SIM-Oriented Single Core (코어 sim-context/ 모듈)
- §95 Six Surfaces Integration (코어 엔진 다이어그램 갱신)
- 헌법 §3 (v2.0.0 강화 주석) + 헌법 §8 (신설)
- 메모리 #5 (검색 3대 축)
