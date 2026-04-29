# 27. CardCheck — 카드 결제 글로벌 파싱 엔진

> **신규 Surface (v1.9.0)**: 카드 결제 SMS·알림 데이터를 글로벌 파싱 엔진으로 처리, 월별·카드별 사용액을 표시.
> 카드스펜드 별도 앱 폐기 결정에 따라 MyPhoneCheck CardCheck로 통합.
> 기준 헌법: `docs/00_governance/architecture/v2.0.0/05_constitution.md` (v2.0.0에서 제8조 SIM-Oriented Single Core 신설 반영)
>
> **v2.0.0 변경 안내**: Stage 1-002 (PR #14)에서 `:feature:card-check/parser/` 자체 모듈로 구현됨.
> Stage 2-001 후속 PR에서 `:core:global-engine/parsing/currency/`로 코어 마이그레이션 예정 (§30-8 표 참조).
> 기능 동일, 위치만 변경. 통화 default는 SimContext 활용 (헌법 §8조).

---

## 27-1. 정의

**CardCheck**는 사용자의 카드 결제 SMS 또는 Push 알림에서 결제 정보(통화·금액·시각·카드 식별자·가맹점)를 **글로벌 파싱 엔진**으로 추출하여 월별·카드별 합계를 관리하는 Surface.

핵심 가치: **190국·모든 언어·모든 카드사에 동작하는 단일 코어 파싱 엔진**.

원칙:
- 카드사·국가별 분기 금지 (Global Core 원칙)
- 시드 데이터 0 (사용자 주도 학습)
- 통화·언어·시각 형식은 ICU 표준으로 처리
- "결제 의심" 검출 + 사용자 확인 = 학습

## 27-2. 데이터 소스 — Producer/Consumer 모델

CardCheck는 **순수 소비자 (Pure Consumer)**:
- 자체 데이터 소스 없음
- 새 권한 요청 0 (MessageCheck/PushCheck 권한 활용)
- 새 외부 통신 0

```
SMS Repository (Android OS) ──┬──→ MessageCheck (자기 파서, 스팸 검증)
                              └──→ CardCheck (자기 파서, 거래 추출)

Notification (NLS, PushCheck) ──→ CardCheck (카드사 앱 알림 후보)
```

CardCheck는 MessageCheck 파서를 호출하지 않음. 동일 SMS를 두 Surface가 각자 자기 관점으로 파싱.

## 27-3. 글로벌 파싱 엔진 — 3단계 파이프라인

### 27-3-1. 1단계: Source Detection (소스 식별)

**자동 감지 + 사용자 학습**:
- SMS 발신자 또는 Notification package를 키로 결제 패턴 강도 점수화
- 점수 임계값 초과 시 "결제 의심" 분류
- 사용자 확인(Y/N) → 발신자별 라벨 학습 (디바이스 로컬 캐시)
- 한 번 라벨링된 발신자는 자동 처리

**시드 데이터 0**: 카드사 목록 사전 정의 안 함. 모든 발신자 동등 취급, 사용자가 시드 형성.

### 27-3-2. 2단계: Pattern Extraction (패턴 추출)

ICU·Unicode 표준 기반 (Android 기본 라이브러리):

| 추출 필드 | 방법 |
|---|---|
| 통화 + 금액 | ICU NumberFormat + 통화 기호 (`\p{Sc}`) + 정규식 |
| 거래 시각 | ICU DateFormat (LocaleAware) + 본문 timestamp 또는 SMS 수신 시각 |
| 카드 식별자 | 발신자 + 본문 토큰 (카드명, 카드 끝자리 4자리, 발신자 ID) |
| 가맹점명 | 대문자 토큰, 따옴표 안 텍스트, 키워드 후 토큰 |

**언어 무관**:
- 통화: ISO 4217 (USD, EUR, JPY, KRW, GBP, INR, CNY, ...) — 모든 통화 자동 인식
- 정규식: Unicode 카테고리 (`\p{Sc}` = 통화 기호, `\p{N}` = 숫자) 기반
- 본문 언어 감지 불필요 (패턴이 언어 무관)

### 27-3-3. 3단계: Validation & Normalization (검증·정규화)

- 통화 정규화: ISO 4217 코드로 변환
- 금액 정수화: 통화별 소수 자릿수 처리 (JPY=0, USD=2, BHD=3, ...)
- timezone: 디바이스 시스템 timezone 적용
- 신뢰도 점수: 어떤 필드 추출됐는지 (전체 추출=high, 부분=medium, 일부=low)
- low 점수는 사용자에게 추가 확인 요청

## 27-4. 저장 (Room DB)

**신규 entity**: `CardTransactionEntity`

```kotlin
@Entity(tableName = "card_transaction")
data class CardTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sourceId: String,            // SMS 발신자 ID 또는 Notification package
    val sourceLabel: String,         // 사용자 정의 라벨 (예: "Visa Card", "Hana Bank")
    val cardIdentifier: String?,     // 카드 끝자리 또는 카드명 (nullable)
    val amount: Long,                // 통화별 최소 단위 정수 (cents, won, ...)
    val currencyCode: String,        // ISO 4217 (USD, EUR, KRW, ...)
    val timestamp: Long,             // epoch ms
    val merchantName: String?,       // 가맹점명 (nullable)
    val source: String,              // "SMS" | "NOTIFICATION"
    val confidence: String           // "HIGH" | "MEDIUM" | "LOW"
)
```

도메인 용어: **CardTransaction**. Kotlin 식별자: **CardTransactionEntity**.

**헌법 2조 정합**: 원문 SMS/Push 폐기, 추출 필드만 영구 저장.

## 27-5. 집계 — 월 단위, 통화별 분리

- **월 경계**: 매월 1일 00:00:00 ~ 말일 23:59:59
- **타임존**: 디바이스 시스템 timezone 적용
- **통화별 분리**: 멀티 통화 사용자 (해외 결제 포함) 대응. USD·KRW·JPY 동시 표시 가능
- **카드(소스)별 분리**: sourceId 단위 합계
- **신뢰도 필터**: low confidence는 사용자에게 별도 표시 (집계 포함 여부 사용자 선택)

## 27-6. UI

- 통화별 카드뷰: 통화 → 카드별 → 월 사용액
- 월 선택: 이번 달 / 지난 달 / 임의 월 (시스템 timezone)
- 결제 내역 리스트: 시간순, 가맹점·통화·금액·일시·신뢰도
- 신뢰도 표시: HIGH (자동 인식), MEDIUM (부분 추출), LOW (사용자 확인 필요)
- 사용자 학습 화면: "이 발신자는 카드사인가" 확인, 라벨 직접 편집

**헌법 6조 정합**: 측정값 그대로, 가공·예측·환산 없음. 환율 변환은 사용자 디바이스 설정 통화로 표시할 때만 옵션.

## 27-7. Stage 1-002 범위

1. SMS Repository 접근 (READ_SMS, MessageCheck 권한 재활용)
2. NotificationListener 활용 (PushCheck NLS 재활용)
3. 글로벌 파싱 엔진 (3단계 파이프라인)
4. Room DB v13 승격 (CardTransactionEntity 추가)
5. 사용자 학습 UI (발신자 라벨링)
6. 월별·통화별·카드별 UI

## 27-8. Stage 2+ 비범위

- 예산 설정·알림
- 카테고리 자동 분류 (식비/교통 등)
- 통계 그래프
- 환율 변환 (사용자 통화 단일화 옵션)
- 머신러닝 기반 패턴 학습 강화

## 27-9. 모듈 매핑

- `:feature:card-check` (신규)
  - `parser/` — 글로벌 파싱 엔진 (Source Detection, Pattern Extraction, Validation)
  - `ui/` — Compose UI (월별·통화별·카드별)
  - `learning/` — 사용자 라벨링 학습 캐시
- `:data:local-cache` (확장)
  - CardTransactionEntity, CardSourceLabelEntity 추가
  - DB v13 승격
- 의존: `:data:sms` (기존 SMS Repository), `:feature:push-trash` (기존 NotificationListener)

## 27-10. 헌법 정합성 (기준 헌법: docs/00_governance/architecture/v2.0.0/05_constitution.md)

| 조 | 명칭 | 정합 사유 |
|---|---|---|
| 1 | Out-Bound Zero | 새 외부 통신 0. 학습도 디바이스 로컬 |
| 2 | In-Bound Zero | SMS/Push 원문 폐기, 추출 필드만 저장 |
| 3 | 결정권 중앙집중 금지 | 디바이스 로컬 파싱·집계, 사용자 라벨링 주도 |
| 4 | 자가 작동 (Self-Operation) | 네트워크 단절 시에도 작동 |
| 5 | 정직성 (Honesty) | 측정값 그대로, "결제 의심" 표현 (단정 X) |
| 6 | 가격 정직성 (Pricing Honesty) | 측정 금액 그대로, 가공·예측·환산 없음. 영수증·SMS와 1:1 일치 |
| 7 | Device-Oriented Goose | 모든 처리 온디바이스, ICU 표준 활용 |
| 8 | SIM-Oriented Single Core | 통화 default 추론에 `SimContext.currency` 활용 (멀티 통화 사용자 정합 유지). 카드사·국가별 분기 코드 0 |

## 27-11. 사용자 대면 약속

CardCheck 화면 또는 onboarding에 명시 필수:

> **"모든 카드 데이터는 디바이스 내에서만 처리되며, 어떤 외부 서버로도 전송되지 않습니다.**
> **SMS·알림은 OS가 이미 받은 것을 읽기만 하며, 원문은 가공 후 즉시 폐기됩니다.**
> **카드사 정보는 사용자가 직접 라벨링하며, 시스템이 사전에 분류하지 않습니다."**

## 27-12. 글로벌 적용 보증

- 카드사·국가별 분기 코드 0 (Global Core 원칙)
- 시드 데이터 0 (한국·미국·일본 등 특정 시장 가정 없음)
- ICU 표준으로 모든 언어·통화·날짜 형식 자동 처리
- 사용자 학습 = 190국 어디서든 동일 UX

## 27-13. cross-ref

- §17 One Core Engine, Six Surfaces (제품 전략, v2.0.0 정확화)
- §28 Initial Scan (CardCheck 베이스 후보 = SMS Inbox 발신자)
- §29 SIM-Oriented Single Core (currency default = SimContext.currency)
- §30 :core:global-engine (Stage 2-001 마이그레이션 대상)
- §31 Real-time Action Engine (v2.1.0, 카드 SMS Layer 2 차단·태그 즉시 적용)
- §32 Tag System (v2.1.0, 카드 SMS 발신자 태그 부여)
- §36 Six Surfaces 통합 원칙 (코어 다이어그램)
- 헌법 §8조 SIM-Oriented Single Core (통화 단일 진실원)
- 카드스펜드 폐기 결정: 메모리 #2 (figma 자료 보존, 코드 폐기 별도 작업)
- Stage 1-002 워크오더 (PR #14, `:feature:card-check`)
- Stage 2-001 워크오더 (예정, 코어 마이그레이션)

## 27-14. v2.0.0 코어 마이그레이션 (예정)

Stage 2-001 후속 PR에서:

| 현재 (Stage 1-002) | 마이그레이션 후 (Stage 2-001) |
|---|---|
| `:feature:card-check/parser/PatternExtractor.kt` | `:core:global-engine/parsing/currency/CurrencyAmountParser.kt` |
| `:feature:card-check/parser/SourceDetector.kt` | (`:feature:card-check`에 잔류, 코어 활용) |
| `:feature:card-check/parser/Validator.kt` | (`:feature:card-check`에 잔류) |
| `:feature:card-check/learning/` | (잔류, 사용자 라벨링 = Surface 영역) |
| `:feature:card-check/repository/` | (잔류, 코어 결과 저장) |

기능 동일. 사용자 영향 0. 단위 테스트 그대로 코어 모듈로 이전.

**SimContext 활용 추가**:
- 통화 default 추론: `SimContext.currency` 활용 (현재는 본문 통화만 추출)
- 사용자 SIM이 KR이면 KRW가 default 통화 (CardCheck UI hint)
- 멀티 통화 지원 유지 (헌법 §6 가격 정직성)

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
  - SearchInput.PhoneNumber 입력 (카드 알림 발신 번호)
  - SimAiSearchRegistry가 SIM countryIso 기반 AI 후보군 추출
  - 예) KR SIM: Naver AI / Google AI Mode (udm=50) / Bing Copilot
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

### §direct-search-card. CardCheck 배치 위치

**카드 알림 발신자 번호**:
- 위치: 카드 알림 UI에 발신 번호 + "🔍 직접 검색" 버튼 (피싱 의심 시 prominent)
- 검색 대상: 카드사 사칭 의심 번호 (SearchInput.PhoneNumber) → AI 검색 모드가 (구) 공공 신고 DB / 경쟁사 reverse 자체 통합
- 카드 정상 알림 시: Tier=Safe로 버튼 보조 표시 (사용자 의심 시 사용 가능)

**모듈**: `:feature:card-check` (Stage 1-002 완료)
