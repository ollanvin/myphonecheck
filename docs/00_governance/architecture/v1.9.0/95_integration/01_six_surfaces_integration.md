# 36. Six Surfaces 통합 원칙 (Patch 22 + v1.9.0 Patch 39·40)

**원본 출처**: v1.7.1 §36 (4103–4249)
**v1.9.0 Layer**: Integration
**의존**: `00_core/01_primary.md`
**변경 이력**: 본 파일은 v1.7.1 §36 원본 전문 이관본. v1.9.0에서 Six Surfaces 명문화로 헤더·§36-3·§36-4 토큰 갱신 (PushCheck 정식 + CardCheck 신설 반영).
**파일 경로**: `docs/00_governance/architecture/v1.9.0/95_integration/01_six_surfaces_integration.md`

---

# 36. Six Surfaces 통합 원칙 (Patch 22 + v1.9.0 Patch 39·40)

**본 장은 §17 One Engine 원칙의 기술 구현 요약이다.** v1.6.0 Patch 22로 4 Surface 통합 신설, v1.6.1 재작성에서 **논리적 위치에 정확히 배치** (말미 추가 아님 — Claude Code 통합본의 구조 오류 시정), v1.9.0 Patch 39·40에서 PushCheck 정식 승격 + CardCheck 신설 반영.

> **v1.9.0 Surface 정의 정정 (§17-1 참조)**: Surface는 **사용자 가치 추출 단위 (Value Extraction Layer)**이며 Decision Engine 공유는 위협 평가 Surface (CallCheck / MessageCheck / PushCheck) 한정. MicCheck / CameraCheck (PackageManager 직접) 및 CardCheck (SMS/Push 재활용)는 자체 로직.

## 36-1. 통합 엔진 (One Engine)

위협 평가 Surface는 **단일 DecisionEngineContract**를 공유한다. MicCheck·CameraCheck·CardCheck는 같은 헌법을 따르되 별도 로컬 로직으로 가치를 추출한다.

```kotlin
// feature/call
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

// feature/message
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

// feature/push
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

**엔진 분기 없음**. 확장은 `IdentifierType` sealed class에 새 분기 추가 + 각 위협 평가 Surface의 Checker 구현으로 처리한다. MicCheck·CameraCheck는 PackageManager/UsageStatsManager 직접 스캔, CardCheck는 SMS/Push 소비자 파서로 작동한다.

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

Stage 1-001 cursor 구현 (`feature/push-trash`)으로 위 1~9 단계 모두 충족. 단 **PushCheck는 위협 평가뿐 아니라 휴지통 격리 모델**도 제공 (§26 본문 참조). NotificationListenerService + Room DB v12 + Compose UI.

### 36-3-B. CardCheck 신설 사례 (v1.9.0 Patch 40)

CardCheck는 **위협 평가 Surface가 아니다**. SMS/Push **재활용 거래 추출 Surface (Producer/Consumer 모델, §27 본문 참조)**. 따라서 위 일반 워크플로우 중:

- 1~5 단계: 미적용 (IdentifierType / Decision Engine 미사용)
- 6 단계: `FourAttributeCard` 셸 대신 카드사별 월 사용액 카드뷰 중심 UI
- 7 단계: 새 권한 0 (MessageCheck/PushCheck가 이미 보유한 권한 재활용)
- 8~9 단계: SmokeRun 시나리오 추가, Patch 40 기록

CardCheck 사례를 통해 Surface는 반드시 Decision Engine을 공유하지 않는다는 것이 명시됨 (§17-1 v1.9.0 정의 정정).

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

본 v1.7.0 재작성본에 대한 자체 grep 검증 결과는 §Z-5에 기록.
