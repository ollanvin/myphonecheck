# Stage 2-001 — CardCheck → :core:global-engine 마이그레이션 Manual Test

**Architecture**: v2.0.0 §27-14 + §29 + §30
**WO**: WO-V200-STAGE2-001
**Module**: `:core:global-engine` (신규) + `:feature:card-check` (의존 변경)
**Selection**: 회귀 0 + SimContext 신규 검증

---

## Prerequisites

- Debug build (`./gradlew assembleDebug`) installed on device or emulator (Android 8+, API 26+).
- READ_SMS permission granted (`:data:sms` 모듈 파이프라인).
- Notification listener access granted (`:feature:push-trash`).
- Database v13 그대로 (Stage 2-001은 entity·migration 변경 없음).

---

## Procedure

### 1. 회귀 검증 — CardCheck UI 동작 동일

PR #14 (Stage 1-002) 시점과 **동일 동작** 확인:

1. Settings → CardCheck 카드 진입 → empty state 화면 (변동 없음)
2. 카드 결제 SMS 시뮬레이션 (예: `"VISA $25.50 Starbucks ending in 1234"`)
3. SourceDetector가 Suspect 분류 → 라벨링 prompt
4. 라벨 입력 → 자동 분류
5. 통화별·카드별 월 사용액 표시 — Stage 1-002와 동일

### 2. 코어 엔진 활용 검증

Logcat 또는 디버거로 확인:
- `app.myphonecheck.mobile.core.globalengine.parsing.currency.CurrencyAmountParser` 호출
- `:feature:card-check`에는 `parser/`, `learning/` 디렉토리 부재 (코어로 이전)

### 3. 다양성 7 SMS 다시 검증 (PR #14 패턴 그대로)

| # | SMS body | Expected currencyCode |
|---|---|---|
| 1 | `"신한카드 끝자리 1234 50,000원 GS25"` | `KRW` |
| 2 | `"VISA $25.50 ending in 5678 Starbucks"` | `USD` |
| 3 | `"BHD 12.500 LULU"` | `BHD` |
| 4 | `"Sparkasse 15,75 € REWE"` | `EUR` |
| 5 | `"Payment 100.00 USD at Amazon"` | `USD` |
| 6 | `"招商银行 1,234元 京东购物"` | `CNY` |
| 7 | `"Bank Hapoalim 250.00 ₪ at SHUFERSAL"` | `ILS` |

7건 모두 PR #14 시점과 동일 결과 (회귀 0).

### 4. SimContext 신규 검증

본 Stage 2-001에서 신규 추가된 SimContext 기능 검증.

#### 4-1. SIM 장착 시 (한국 SIM 가정)

- `SimContextProvider.resolve()` 호출 결과:
  - `countryIso = "KR"`
  - `currency.currencyCode = "KRW"`
  - `phoneRegion = "KR"`
  - `mcc`/`mnc` 통신사별 (예: 450/05 SK Telecom)

#### 4-2. SIM 부재 시 (WiFi-only 태블릿 또는 SIM 미장착)

- `fallbackFromSystem()` 경로:
  - `countryIso = Locale.getDefault().country` (예: "KR")
  - `currency.currencyCode = "KRW"` (CountryCurrencyMapper fallback)
  - 사용자에게 명시 (Settings 배너): "SIM 미감지. 디바이스 설정 사용."

#### 4-3. UI 언어 3단 fallback (헌법 §8-2)

- 현재 Stage 2-001은 인프라만 구현. UI 적용은 후속 Stage.
- 단위 테스트로 검증 완료 (UiLanguageResolverTest 8 PASS).

### 5. 헌법 정합 검증

- **§1 Out-Bound Zero**: 코어 모듈에 외부 통신 코드 0
- **§2 In-Bound Zero**: 원문 폐기, 추출 필드만 저장 (Stage 1-002 시점 동일)
- **§3 결정권 중앙집중 금지**: 코어 엔진 = 본 조 비대상 (v2.0.0 §3 강화 주석)
- **§8 SIM-Oriented Single Core**: SimContext 단일 진실원 활용

---

## Record

각 device test session에 capture:

- Device model, Android version, build flavor
- CardCheck UI 회귀 검증 결과 (Stage 1-002 vs Stage 2-001 동일 여부)
- 7 다양성 SMS 결과 (7/7 PASS)
- SimContext.resolve() 출력 로그 (countryIso, currency, mcc, mnc)
- 단위 테스트 PASS 로그 (`:core:global-engine:testDebugUnitTest`)

증거: `docs/05_quality/evidence/stage2_001/<YYYYMMDD>/`

---

## Test Results (자동 단위 테스트, 2026-04-27)

`:core:global-engine:testDebugUnitTest` — **37 PASS** (회귀 17 + 신규 20):

- `CurrencyAmountParserTest`: 12 PASS (구 PatternExtractorTest 회귀)
- `CurrencyValidatorTest`: 5 PASS (구 ValidatorTest 회귀)
- `CountryCurrencyMapperTest`: 7 PASS (신규)
- `SimContextTest`: 5 PASS (신규)
- `UiLanguageResolverTest`: 8 PASS (신규)

`:feature:card-check:testDebugUnitTest`: PASS (자체 테스트는 코어로 이전, 모듈에는 비-parser 테스트만 잔존 — 0 또는 적은 수)

`assembleDebug`: PASS (33s, 회귀 0)

---

## Known Limitations (Stage 2-001)

- SimContextStorage (이전 SimContext 영구 저장) 미구현. SimChangeDetector는 첫 스캔 + 비교만.
- UiLanguageResolver 통합 (Settings UI 3단 라디오) 후속.
- CardCheck Repository는 코어 의존만 — UI 가시 변화 0 (회귀 0).
- 코드 영역만 변경. v2.0.0 명세 (docs/00_governance/architecture/v2.0.0/) 변경 0.
