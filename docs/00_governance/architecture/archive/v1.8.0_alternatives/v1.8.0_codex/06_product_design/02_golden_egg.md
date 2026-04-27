# 3. 황금알 정의 (Golden Egg Definition)

**원본 출처**: v1.7.1 §3 (77줄)
**v1.8.0 Layer**: Feature
**의존**: `05_constitution.md` + `06_product_design/01_goose_vs_egg.md`
**변경 이력**: 본 파일은 v1.7.1 §3 (77줄) 원본 전문 이관본. 텍스트 변경 없음.
**파일 경로**: `docs/00_governance/architecture/v1.8.0_codex/06_product_design/02_golden_egg.md`

---


"거위가 황금알을 낳는다"는 비유의 **황금알 자체**를 정의한다. 사용자 입장에서 "무엇이 황금알인가"의 긍정적 정의가 없으면 결제 이유 설계가 불가능하다.

## 3-1. 황금알 자격 4조건

황금알이 되려면 다음 4조건을 모두 만족해야 한다.

| # | 조건 | 정의 | 검증 기준 |
|---|---|---|---|
| 1 | **즉시성** | 통화 수신 시각 ≤ 1초 이내 표시 | NKB Hit p95 ≤ 100ms + UI 렌더 ≤ 500ms |
| 2 | **신뢰성** | 근거 없는 추정 금지, 출처 표기 | Tier 1~4 신뢰도 가중합 + ambiguous 플래그 |
| 3 | **실행 가능성** | 차단·신고·안심 표시 3종 즉시 수행 가능 | 오버레이에 3버튼 상시 노출 |
| 4 | **정직성** | "확실하지 않음"을 숨기지 않음 | topConfidence 표시 + isAmbiguous 플래그 |

## 3-2. 황금알 = 4속성 출력

황금알의 실체는 다음 4속성이다. 이 4속성이 **모든 Surface에서 동일 규격으로 출력**된다.

| 속성 | 설명 | 예시 |
|---|---|---|
| 위험도 | 5단계 RiskLevel | `HIGH` (빨강) |
| 예상 손해 | 위험도 + 카테고리 기반 손해 유형 | "금전 피해 가능 (평균 120만원)" |
| 손해 유형 | 카테고리 정적 매핑 | "금융사기 / 대포폰 / 보이스피싱" |
| 이유 설명 | 신호 카운트 기반 한 줄 요약 | "정부 신고 12건, 사용자 신고 8건" |

4속성 모두 **NKB Hit만으로 산출 가능** → L3 호환 100%.

## 3-3. One Engine, N Surfaces 원칙

황금알은 Surface가 달라도 **동일 엔진·동일 규격**으로 산출된다. 이는 v1.6에서 **Four Surfaces**로 확장되었으며, 이후 Surface 추가 시에도 동일 원칙을 유지한다.

| Surface | 입력 | 엔진 호출 | 출력 |
|---|---|---|---|
| CallCheck | 전화번호 (E.164) | `decisionEngine.evaluate(phoneQuery)` | CallRisk (4속성) |
| MessageCheck | 발신번호 + URL + 기관명 | 3중 `evaluate` 호출 후 결합 | MessageRisk (4속성) |
| MicCheck | RECORD_AUDIO 보유 앱 목록 | PackageManager 스캔 (엔진 미사용, Patch 30·37) | `List<MicPermissionEntry>` |
| CameraCheck | CAMERA 보유 앱 목록 | PackageManager 스캔 (엔진 미사용, Patch 30·37) | `List<CameraPermissionEntry>` |

**핵심**: 엔진은 하나, Surface는 넷. 단 Patch 30 이후 MicCheck/CameraCheck는 **단순 관리자**로 축소되어 DecisionEngine 호출 없이 로컬 PackageManager + UsageStatsManager만 사용한다. 앱 평판·CVE 감시는 후행 Surface `AppSecurityWatch`로 이관(Patch 31, §17-3). 엔진 분기는 여전히 없음 — CallCheck·MessageCheck·AppSecurityWatch가 동일 엔진 계약(§33-1-4 `DecisionEngineContract`)을 통과한다.

**Patch 37 반영 (P0-2)**: v1.6.2까지 본 표에 `List<AppPermissionRisk>` 출력 + `evaluate(appRepQuery)` 호출이 잔존했으나 Patch 30으로 폐기된 구조. 7-워커 평가(Cursor·코웍 2자 합의)로 지적. 본 v1.7.0에서 `MicPermissionEntry`/`CameraPermissionEntry` 단순 데이터 클래스로 정정.

## 3-4. 4속성의 UX 표현 규격

| 속성 | UI 컴포넌트 | 규격 |
|---|---|---|
| 위험도 | RiskBadge | 색상(초록/노랑/주황/빨강/검정) + 라벨 |
| 예상 손해 | DamageEstimate | "평균 ~원 / 중앙값 ~원 / 신뢰도 ~%" |
| 손해 유형 | DamageTypeChip | 최대 2개 칩 (예: "금융사기 + 대포폰") |
| 이유 설명 | ReasonExplainText | 한 줄, strings.xml 다국어 템플릿 |

단일 카드 `FourAttributeCard`에 4속성을 모두 담아 **Surface 간 일관된 UX**를 보장한다.

### 3-4-1. DO_NOT_MISS 강조 규칙 (Patch 35 cross-ref)

`UserAction.DoNotMiss`(§8-2-2)로 지정된 식별자에 대해서는 4속성 카드 위에 **고정 강조 띠**를 표시:

- 띠 색상: 노랑(#FFD54F) + 별 아이콘
- 메시지: "DO_NOT_MISS — {userMemo 있으면 표시}"
- 알림 채널: High-Priority Notification Channel 강제 사용
- 오버레이 dismiss 시간: 5초 → 15초 (사용자가 충분히 인지하도록)
- `riskLevel == LOW`여도 **반드시 노출** (DecisionEngine 판정 우선순위 역전)

자세한 처분 정책은 §8-2-4 (Phase별 적용 표), §21-1 (v1.7.0 Open Issues #13 근거), §21-1-1 (데이터 수명) 참조.

## 3-5. 황금알이 아닌 것 (반례)

다음은 황금알로 오인하기 쉬우나 **자격 미달**이다.

- "스팸 통계만 표시" (메모리 #12 반쪽 기능) → 실행 가능성 0
- "데이터 다운로드 버튼" → 즉시성 없음
- "광고 차단 기능" → 본 앱의 정체성과 무관
- "포인트 적립 게이미피케이션" → 정직성 저해

---

