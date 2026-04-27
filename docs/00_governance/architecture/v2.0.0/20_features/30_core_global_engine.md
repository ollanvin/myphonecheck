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

## 30-4. 검색 3대 축 (메모리 #5 정합, 헌법 1조 강제)

### 30-4-1. 내부 검색 (Internal)

- 온디바이스 이력 (CallLog, SMS, 사용자 라벨링 캐시)
- 외부 통신 0
- 즉시 응답 (디스크·메모리)

### 30-4-2. 외부 검색 (External)

- 일반 검색엔진 (Google, Naver 등)
- **Custom Tab 사용자 trigger 방식** (헌법 1조 정합)
- 사용자가 명시적으로 "검색 열기" 버튼 클릭 → 브라우저 트리거
- 본 앱은 검색 자체 수행 안 함, 결과 받지 않음

### 30-4-3. 공개 피드 (Public)

- 정부 신고 DB (예: KISA 스미싱 신고, FBI IC3, ACMA Do-Not-Call)
- 통신사 공개 차단번호
- 보안업체 공개 위협 인텔리전스 (예: Abuse.ch, PhishTank, OpenPhish)
- **사용자 옵트인 다운로드 방식** (헌법 1조 정합)
- 본 앱이 다운로드해서 디바이스 캐싱 → 이후 오프라인 활용
- 외부 통신은 사용자 동의 후 정해진 피드만

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
