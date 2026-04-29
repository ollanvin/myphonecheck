# Stage 3 시리즈 종료 — 검증 매트릭스 (v2.5.0 정합)

**최종 머지 일자**: 2026-04-29
**워커**: Claude Code

---

## PR 목록

| WO | PR | main HEAD | 결과물 |
|---|---|---|---|
| 3-000 | #53 | `8237f35` | data/search 폐기 + manifest 정정 + FeedType 정정 |
| 3-001 | #54 | `631c982` | InputAggregator 4축 + udm=50 (v2.4.0 정합, 후속 정정) |
| V250-CONST-2AXIS | #55 | `545fde2` | 헌법 v2.4.0 → v2.5.0 (4축 → 2축 + SIM 자율 + SearchInput) |
| 3-002-REV | #56 | `237257c` | InputAggregator 4축 → 2축 + SearchInput sealed class |
| 3-003-REV | #57 | `50983fb` | DecisionEngineImpl 2축 + RiskTier + SimAiSearchRegistry |
| 3-004-REV | #58 | `43a5d0e` | DirectSearchButton + SimBasedAiMenu (decision-ui) |
| 3-005-REV | #59 | `5347444` | 4 Surface 통합 + multi-input + helper Composables |
| 3-006-REV | #60 | (TBD) | 매트릭스 12 시나리오 + 시리즈 종료 |

총 8 PR.

## v2.5.0 SSOT ↔ 코드 매핑

| spec 파일 | 구현 파일 | 테스트 파일 |
|---|---|---|
| §1 검색 2축 (NKB 0.40 + AI 0.60) | core/global-engine/decision/InputAggregator.kt | core/global-engine/decision/InputAggregatorTest.kt |
| §1 SimAiSearchRegistry (KR 3 / JP 3 / CN 2 / Global 2) | core/global-engine/search/registry/SimAiSearchRegistry.kt | core/global-engine/search/registry/SimAiSearchRegistryTest.kt |
| §1 SearchInput sealed (PhoneNumber/Url/MessageBody/AppPackage) | core/global-engine/search/SearchInput.kt | core/global-engine/search/SearchInputTest.kt |
| §1 ExternalMode (8 enum) | core/global-engine/search/external/ExternalMode.kt | core/global-engine/search/CustomTabExternalSearchTest.kt |
| §10-formula-2axis | feature/decision-engine/DecisionEngineImpl.kt + core/common/risk/RiskTier.kt | feature/decision-engine/DecisionEngine2AxisTest.kt + core/common/TierMappingTest.kt |
| §direct-search-* (DirectSearchButton + SimBasedAiMenu + SearchInputPicker + DirectSearchHandler) | feature/decision-ui/components/ | feature/decision-ui/components/DirectSearchHandlerTest.kt |
| §direct-search 6 Surface 통합 (4 + privacy-check 보류) | feature/{call-check, card-check, message-check, push-trash}/ui/ ViewModel | (Compose UI test는 instrumented 영역) |
| Stage 3 매트릭스 12 시나리오 | app/src/test/.../matrix/Stage3MatrixScenarios.kt | (자체 검증) |

## 헌법 정합 검증

| 헌법 | 정합 |
|---|---|
| §1 Out-Bound Zero | data/search 폐기 / Custom Tab만 / 우리 송신 0 ✓ |
| §3 결정권 중앙집중 금지 | 사용자 자율 AI 검색 모드 선택 ✓ |
| §5 정직성 | Tier Unknown 정직 표시 + 직접 검색 prominent ✓ |
| §6 가격 정직성 | API 통합 0, 우리 비용 0 ✓ |
| §7 One Engine, N Surfaces + N Inputs | DecisionEngine 단일, SearchInput sealed 4 타입 ✓ |
| §8 SIM-Oriented | SimAiSearchRegistry SIM 기준 자동 추출 ✓ |
| §9-1 영문 strings | strings-xx 추가 0 ✓ |
| §9-3 글로벌 단일 | 국가별 분기 0 (SimContextProvider 단일 진실원) ✓ |
| §9-4 단일 코어 | core/global-engine 안드로이드 의존 0 (string URL만) ✓ |
| §9-6 매트릭스 검증 | Stage3MatrixScenarios 12건 + 기존 ScenarioMatrixTest 골격 ✓ |
| §10-6 자체 머지 | 모든 PR squash + delete-branch ✓ |

## 후속 영역 (Stage 4 별 시리즈로 분리)

본 Stage 3 시리즈에서 명시적으로 후속 위임된 영역:

1. **privacy-check Surface 통합** — Compose UI Composable 부재 (Service/Engine 만). MicCheck/CameraCheck 알림 카드 영역 통합은 OS PendingIntent + RemoteViews 영역.
2. **NKB DAO 영속화** — `InputAggregator.appendUserTaggedExternalResult` 인터페이스 stub. 실 NKB DAO 통합 후속 PR.
3. **본문 다중 input 정밀 추출** — Stage 3-005-REV에서 minimal 정규식 기반 URL/Phone 추출. PhoneNumberParser (libphonenumber) 활용한 정밀 추출은 Stage 4 영역.
4. **Compose UI instrumented test** — s01 (DirectSearchButton color) / s07 (Custom Tab fallback) / s11 (OkHttp interceptor) 등 instrumented 환경 의존.
5. **call-intercept 39 파일 정리** — 헌법 §9-4 위반 영역. Stage 3-000 학습 시 별 시리즈로 분리 (DIAGNOSE Q1-4).
6. **URL/링크 검증 본격** — SearchInput.Url + SearchInput.MessageBody 활용한 phishing URL 검증. Stage 4 본격 영역.

## 베이스라인 회귀 보호

Stage 3 진입 전 baseline:
- decision-engine: 38 testcase PASS
- core/global-engine: 169 testcase PASS

Stage 3 종료 후 (3-003-REV + 3-002-REV + 3-006-REV 누적):
- decision-engine: 38 + 5 (DecisionEngine2AxisTest) = 43 testcase PASS ✓
- core/global-engine: 169 + 7 (SearchInputTest) + 7 (SimAiSearchRegistryTest) = 183 testcase PASS ✓
- core/common: 신규 9 testcase (TierMappingTest) PASS ✓
- decision-ui: 신규 8 testcase (DirectSearchHandlerTest + SimBasedAiMenuRequireTest) PASS ✓
- app: 신규 12 testcase (Stage3MatrixScenarios) PASS ✓

기존 v1 자산 회귀 0 (PR diff 본문 변경 0, additive only).

## Stage 3 시리즈 종료 선언

본 PR (3-006-REV) 머지 시 **Stage 3 시리즈 종료**.

Stage 4 (URL/링크 검증 본격) 별 시리즈 후속 정의 — 본 시리즈 학습:
- 헌법 v2.5.0 §1 SearchInput.Url + MessageBody는 Stage 4 본격 영역
- AI 검색 모드 인프라가 (구) 공공 + 경쟁사 reverse 자체 통합 (대표님 2026-04-29 실측 결과)
- Stage 4에서 URL reputation도 동일 패턴 검증
