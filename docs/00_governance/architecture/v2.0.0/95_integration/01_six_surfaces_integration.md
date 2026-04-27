# 36. Six Surfaces 통합 원칙 (Patch 22 + v1.9.0 Patch 39·40 + v2.0.0 Patch 41~45)

**원본 출처**: v1.7.1 §36 (4103–4249)
**v2.0.0 Layer**: Integration
**의존**: `00_core/01_primary.md`
**변경 이력**: v1.7.1 §36 원본 이관본을 v1.9.0 Patch 39·40 (Six Surfaces, PushCheck 정식, CardCheck 신설) 반영 후, v2.0.0 Patch 41~45에서 **One Core Engine** 정확화 + Initial Scan / SIM-Oriented / `:core:global-engine` 통합 다이어그램 갱신.
**파일 경로**: `docs/00_governance/architecture/v2.0.0/95_integration/01_six_surfaces_integration.md`

---

# 36. Six Surfaces 통합 원칙 (Patch 22 + v1.9.0 Patch 39·40 + v2.0.0 Patch 41~45)

**본 장은 §17 One Core Engine 원칙의 기술 구현 요약이다.** v1.6.0 Patch 22로 4 Surface 통합 신설, v1.6.1 재작성에서 **논리적 위치에 정확히 배치** (말미 추가 아님 — Claude Code 통합본의 구조 오류 시정), v1.9.0 Patch 39·40에서 PushCheck 정식 승격 + CardCheck 신설 반영, **v2.0.0 Patch 41~45에서 One Core Engine 정확화 + Initial Scan / SIM-Oriented Single Core / `:core:global-engine` 통합**.

> **v2.0.0 정정 (헌법 §3 강화 + §8조 신설)**: Surface는 **사용자 가치 추출 단위 (Value Extraction Layer)**이며 모든 Surface는 단일 코어 `:core:global-engine`을 사용한다. v1.9.0 §17-1의 "위협 평가 Surface 한정 Decision Engine 공유" 해석은 v2.0.0에서 정정 — 모든 Surface가 동일 코어 사용. SimContext (헌법 §8조) 단일 진실원으로 동작.

## 36-1. 통합 엔진 (One Core Engine)

모든 Surface는 단일 코어 `:core:global-engine`을 사용한다 (§30 본문). 자체 파서·매핑 코드 금지.

### 36-1-A. v2.0.0 코어 다이어그램

```
                    [SimContext §29]  (헌법 §8조 단일 진실원)
                          │
                          ▼
              ┌───────────────────────┐
              │ :core:global-engine   │
              ├───────────────────────┤
              │ sim-context/          │  ← §29
              │ parsing/              │  ← phone/message/notification/currency/timestamp
              │ search/               │  ← internal/external/public (§30-4 검색 3대 축)
              │ decision/             │  ← InputAggregator
              └───────────────────────┘
                          │
                          ▼ (모든 Surface 활용)
       ┌──────────┬──────────┬──────────┬──────────┬──────────┬──────────┐
       │CallCheck │MessageChk│MicCheck  │CameraChk │PushCheck │CardCheck │
       │ §18-3    │ §18-4    │ §18-6    │ §18-7    │ §26      │ §27      │
       └──────────┴──────────┴──────────┴──────────┴──────────┴──────────┘
                          │
                          ▼
                       [사용자]
```

> **목표 스케치 — Stage 2+ 후속 구현 대상**: v2.0.0 시점 코어 모듈은 **명문화만**. 실제 코드 마이그레이션은 Stage 2-001 ~ 2-005 후속 PR (§30-8 표 참조).

### 36-1-B. v1.9.0 → v2.0.0 변경 정정

v1.9.0의 분산된 Surface별 자체 파서/매핑은 v2.0.0에서 코어로 점진 추출:

- v1.9.0 `:feature:card-check/parser/PatternExtractor.kt` (Stage 1-002 PR #14) → v2.0.0 `:core:global-engine/parsing/currency/CurrencyAmountParser.kt` (Stage 2-001 후속)
- 미구현 CallCheck → v2.0.0 `:core:global-engine/parsing/phone/` (Stage 2-002 후속)
- 미구현 MessageCheck → v2.0.0 `:core:global-engine/parsing/message/` (Stage 2-003 후속)
- v1.9.0 `:feature:push-trash/util/` → v2.0.0 `:core:global-engine/parsing/notification/` (Stage 2-004 후속)
- 미구현 검색 → v2.0.0 `:core:global-engine/search/` (Stage 2-005 후속)

기존 Decision Engine 코드 예시 (v1.9.0 시점 잔존, Stage 2+ 코어 마이그레이션 대상):

> **목표 스케치 — 후속 구현 대상 (Stage 2+)**: 아래 코드 블록은 PushCheck DE 통합 시점의 **목표 형태**이다. v1.9.0 시점 PushCheck는 `feature/push-trash` 모듈로 규칙 기반 휴지통이 구현되어 있으며, `IdentifierType.NotificationSource`·`PushCheckEngine`·`PushRisk : RiskKnowledge`는 **미구현 미래 타입**이다. 독자가 "이미 구현됨"으로 오인하지 않도록 라벨로 명시한다.

```kotlin
// feature/call (v1.9.0 시점 구현 형태)
class CallCheckEngine(
    private val decisionEngine: DecisionEngineContract
) : Checker<PhoneQuery, CallRisk> {
    override suspend fun check(input: PhoneQuery): CallRisk {
        val knowledge = decisionEngine.evaluate(
            IdentifierType.PhoneNumber(input.phoneE164)
        )
        return CallRisk.from(knowledge)
    }
}

// feature/message (v1.9.0 시점 구현 형태)
class MessageCheckEngine(
    private val decisionEngine: DecisionEngineContract,
    // ... 추가 의존성
) : Checker<IncomingSms, MessageRisk> {
    override suspend fun check(input: IncomingSms): MessageRisk {
        // 3중 평가 모두 동일 엔진 호출
        val senderKnowledge = decisionEngine.evaluate(
            IdentifierType.PhoneNumber(input.senderE164)
        )
        // ...
    }
}

// feature/push (목표 스케치 — Stage 2+ 후속 구현 대상; v1.9.0 시점 미구현)
// IdentifierType.NotificationSource·PushCheckEngine·PushRisk 모두 v1.9.0 시점에 존재하지 않음.
// v1.9.0 PushCheck는 feature/push-trash 모듈로 규칙 기반 휴지통이 구현되어 있다 (§26 본문).
class PushCheckEngine(
    private val decisionEngine: DecisionEngineContract
) : Checker<NotificationSource, PushRisk> {
    override suspend fun check(input: NotificationSource): PushRisk {
        val knowledge = decisionEngine.evaluate(
            IdentifierType.NotificationSource(input.packageName, input.channelId)
        )
        return PushRisk.from(knowledge)
    }
}
```

**엔진 분기 없음**. 확장은 `IdentifierType` sealed class에 새 분기 추가 + 각 위협 평가 Surface의 Checker 구현으로 처리한다. PushCheck DE 통합도 본 패턴을 따를 예정 (Stage 2+). MicCheck·CameraCheck는 PackageManager/UsageStatsManager 직접 스캔, CardCheck는 SMS/Push 소비자 파서로 작동한다.

## 36-2. 4속성 공통 렌더링

모든 Surface 결과는 **`FourAttributeCard`** 공통 UI 셸 또는 그에 준하는 4속성 프레임을 사용한다.

```kotlin
@Composable
fun FourAttributeCard(risk: RiskKnowledge) {
    Card {
        RiskBadge(level = risk.riskLevel)
        DamageEstimateBlock(estimate = risk.expectedDamage)
        DamageTypeChips(types = risk.damageTypes)
        ReasonExplainText(text = risk.reasonSummary)
        StalenessIndicator(flag = risk.stalenessFlag)
    }
}
```

Surface별로 이 카드 **위·아래에 Surface 고유 정보**를 덧붙일 뿐이다. 예를 들어 MessageCheck는 URL 목록, PushCheck는 휴지통 상태, CardCheck는 월별 카드 집계를 추가한다. MicCheck·CameraCheck는 단순 관리자이므로 권한 리스트·최근 사용 시각·회수 버튼이 핵심이며, 공통 4속성 셸은 불변이다.

## 36-3. Surface 확장 정책 (v1.9.0 PushCheck 정식 + CardCheck 사례)

위협 평가 Surface 추가 시 일반 워크플로우 (CallCheck → MessageCheck → PushCheck 패턴):

1. `IdentifierType` sealed class에 새 분기 추가 (예: `NotificationSource`)
2. Decision Engine의 `evaluate()` switch에 처리 로직 추가
3. 새 feature 모듈 생성 (`feature/push`)
4. `Checker<NotificationPayload, NotificationRisk>` 구현
5. `NotificationRisk : RiskKnowledge` 구현
6. UI에서 `FourAttributeCard(risk)` 호출
7. 권한 매트릭스(§34-1) 행 추가
8. SmokeRun 시나리오 추가
9. 본 문서 Patch 넘버 부여 + §0-B-2 기록

엔진·NKB·SearchResultAnalyzer **변경 없음**. 새 Surface는 얇은 레이어만 추가한다.

### 36-3-A. PushCheck 정식 사례 (v1.9.0 Patch 39)

v1.9.0 시점 PushCheck = **규칙 기반 휴지통** (앱/채널 차단 규칙 매칭으로 격리 여부 결정). Stage 1-001 cursor 구현 (`feature/push-trash`)에서 NotificationListenerService + Room DB v12 + Compose UI 휴지통이 완료됨 (§26 본문 참조).

**Decision Engine 통합은 Stage 2+ 후속 작업**으로 위임 (위 §36-1 목표 스케치 참조). 따라서 v1.9.0 시점 §36-3 1~9 단계 워크플로우 중:

- 1·2·5 단계 (`IdentifierType.NotificationSource`·`evaluate()` 분기·`PushRisk : RiskKnowledge`): **미적용** (Stage 2+에 도입 예정)
- 3·4 단계 (feature 모듈 + Checker 구현): 휴지통 모듈 형태로 충족
- 6 단계 (UI): `FourAttributeCard` 대신 휴지통 화면이 핵심 (위협 4속성 표시는 Stage 2+)
- 7·8·9 단계: 권한 매트릭스 NLS 행 추가, SmokeRun 시나리오 보강, Patch 39 기록 — 모두 충족

### 36-3-B. CardCheck 신설 사례 (v1.9.0 Patch 40)

CardCheck는 **위협 평가 Surface가 아니다**. SMS/Push **재활용 거래 추출 Surface (Producer/Consumer 모델, §27 본문 참조)**. 따라서 위 일반 워크플로우 중:

- 1~5 단계: 미적용 (IdentifierType / Decision Engine 미사용)
- 6 단계: `FourAttributeCard` 셸 대신 카드사별 월 사용액 카드뷰 중심 UI
- 7 단계: 새 권한 0 (MessageCheck/PushCheck가 이미 보유한 권한 재활용)
- 8~9 단계: SmokeRun 시나리오 추가, Patch 40 기록

CardCheck 사례를 통해 Surface는 반드시 Decision Engine을 공유하지 않는다는 것이 명시됨 (§17-1 v1.9.0 정의 정정).

### 36-3-C. Initial Scan 흐름 (v2.0.0 §28)

```
[앱 최초 론칭]
   ↓
[권한 동의 UI]
   ↓
[SimContext 확정 — :core:global-engine/sim-context/ 활용]  ← 헌법 §8조
   ↓
[병렬 스캔 — :core:global-engine/parsing/ 활용]
   ├─ CallLog → CallCheck 베이스
   ├─ SMS Inbox → MessageCheck 베이스 + CardCheck 후보
   ├─ PackageManager → MicCheck/CameraCheck 인벤토리
   └─ NLS 등록 → PushCheck 활성화
   ↓
[Room DB 베이스데이터 영구 저장]
   ↓
[6 Surface 활성화]
```

상세는 §28 본문 참조.

### 36-3-D. SIM-Oriented Single Core 흐름 (v2.0.0 §29 + 헌법 §8조)

```
[디바이스 부팅 / SIM 변경 감지]
   ↓
[:core:global-engine/sim-context/SimContextProvider]
   ↓
[SimContext = (mcc, mnc, countryIso, operatorName, currency, phoneRegion, timezone)]
   ↓
[모든 Surface가 SimContext 활용 — 자체 매핑 코드 0]
   ↓
[UI 언어만 사용자 선택 가능 (3단 fallback)]
   ↓
[SimContext 변경 시 사용자 3-옵션 (적용 / 유지 / 초기화)]
```

UI 언어 3단:
- 1순위 (default): SIM 기반 언어
- 2순위: 디바이스 시스템 언어
- 3순위: English

상세는 §29 본문 + 헌법 §8조 참조.

## 36-4. 빌드 필수 토큰 42개 (v1.6.1-patch §9-1)

본 문서가 v1.6.1로 인정받으려면 다음 42개 토큰이 본문에 **모두 존재**해야 한다.

```
1.  CallCheck
2.  MessageCheck
3.  MicCheck
4.  CameraCheck
5.  One Engine
6.  Six Surfaces (v1.9.0; v1.6.1 시점 "Four Surfaces" 토큰 확장)
7.  DecisionEngineContract
8.  IdentifierType
9.  RiskKnowledge
10. RiskLevel
11. DamageEstimate
12. DamageType
13. StalenessFlag
14. FreezeMarker
15. Checker
16. ExtractedSignal
17. NumberKnowledge
18. UserAction
19. ClusterProfile
20. SignalSummary
21. FeatureType
22. ConclusionCategory
23. Softmax
24. Tier
25. Cold Start
26. Self-Discovery
27. NKB
28. L3
29. SLA
30. AppPermissionRisk
31. JustificationStatus
32. MessageRisk
33. CallRisk
34. FourAttributeCard
35. QUERY_ALL_PACKAGES
36. PACKAGE_USAGE_STATS
37. 디바이스 오리엔티드
38. 헌법
39. Out-Bound Zero
40. In-Bound Zero
41. Working Canonical
42. 2.49
```

## 36-5. 금지 토큰 9개 (v1.6.1-patch §9-2)

다음 9개 토큰은 본문에 **등장하지 않아야** 한다 (Patch 17·20·23·24·Infra FINAL).

```
1.  BROADCAST_SMS           (Patch 17, Play 정책 위반)
2.  PrivacyCheck            (Patch 21, 폐기됨)
3.  RECORD_AUDIO            (Patch 23, 권한 요청 없음 — 단, 34-1 "요청 안 함" 표기는 예외)
4.  RevenueCat              (메모리 #20, 미채택)
5.  AWS Lambda              (Infra v1.0 FINAL, 폐기)
6.  API Gateway             (Infra v1.0 FINAL, 폐기)
7.  DynamoDB                (Infra v1.0 FINAL, 폐기)
8.  자체 영수증 검증 서버    (헌법 1·7조 위반)
9.  본사 큐레이션            (헌법 3조 위반)
```

금지 토큰은 본 문서에 **다음 맥락에서만 등장 허용**:
- "금지 토큰" 자체를 설명하는 §36-5 (본 섹션)
- 폐기 이력 §2-2, §18-5 (역사 기록)
- Patch 감사 로그 §0-B-2 (과거 변경 추적)

## 36-6. 자체 검증 결과

본 v1.9.0에 대한 자체 grep 검증 결과는 §Z-5 (v1.7.0 시점 검증) + §Z-11 (v1.9.0 MAJOR 승격 사유)에 기록. 향후 `scripts/verify-tokens.sh`가 본 §36-4 토큰 표 (v1.9.0 보강 포함)를 자동 검증 예정.
