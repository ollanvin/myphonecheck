# 36. Four Surfaces 통합 원칙 (Patch 22)

**원본 출처**: v1.7.1 §36 (149줄)
**v1.8.0 Layer**: Integration
**의존**: `20_features/21_call.md` + `20_features/22_message.md` + `20_features/23_mic.md` + `20_features/24_camera.md`
**변경 이력**: 본 파일은 v1.7.1 §36 (149줄) 원본 전문 이관본. 텍스트 변경 없음.
**파일 경로**: `docs/00_governance/architecture/v1.8.0_codex/95_integration/01_four_surfaces_integration.md`

---


**본 장은 §17 One Engine 원칙의 기술 구현 요약이다.** v1.6.0 Patch 22로 신설, v1.6.1 재작성에서 **논리적 위치에 정확히 배치** (말미 추가 아님 — Claude Code 통합본의 구조 오류 시정).

## 36-1. 통합 엔진 (One Engine)

모든 Surface는 **단일 DecisionEngineContract**를 공유한다.

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

// feature/mic, feature/camera도 동일 패턴
```

**엔진 분기 없음**. 확장은 `IdentifierType` sealed class에 새 분기 추가 + 각 Surface의 Checker 구현으로 처리.

## 36-2. 4속성 공통 렌더링

모든 Surface 결과가 **`FourAttributeCard`** 공통 UI 컴포넌트로 렌더링된다.

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

Surface별로 이 카드 **위·아래에 Surface 고유 정보**를 덧붙일 뿐 (예: MessageCheck의 URL 목록, AppSecurityWatch의 CVE 이력 — MicCheck/CameraCheck는 Patch 30으로 단순 관리자로 축소되어 CVE 이력 표시 없음). 공통 4속성 영역은 불변.

## 36-3. Surface 추가 워크플로우 (향후 PushCheck 등)

1. `IdentifierType` sealed class에 새 분기 추가 (예: `NotificationSource`)
2. Decision Engine의 `evaluate()` switch에 처리 로직 추가
3. 새 feature 모듈 생성 (`feature/push`)
4. `Checker<NotificationPayload, NotificationRisk>` 구현
5. `NotificationRisk : RiskKnowledge` 구현
6. UI에서 `FourAttributeCard(risk)` 호출
7. 권한 매트릭스(§34-1) 행 추가
8. SmokeRun 시나리오 추가
9. 본 문서 Patch 넘버 부여 + §0-B-2 기록

엔진·NKB·SearchResultAnalyzer **변경 없음**. 새 Surface는 얇은 레이어만 추가.

## 36-4. 빌드 필수 토큰 42개 (v1.6.1-patch §9-1)

본 문서가 v1.6.1로 인정받으려면 다음 42개 토큰이 본문에 **모두 존재**해야 한다.

```
1.  CallCheck
2.  MessageCheck
3.  MicCheck
4.  CameraCheck
5.  One Engine
6.  Four Surfaces
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

---

