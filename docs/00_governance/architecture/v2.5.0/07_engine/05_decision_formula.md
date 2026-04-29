# 10. Decision Engine 수식 (Softmax + 사용자 Override)

**원본 출처**: v1.7.1 §10 (1322–1468)
**v1.8.0 Layer**: Engine
**의존**: `00_core/01_primary.md`
**변경 이력**: 본 파일은 v1.7.1 §10 원본 전문 이관본. 텍스트 변경 없음.
**파일 경로**: `docs/00_governance/architecture/v1.8.0_cursor/07_engine/05_decision_formula.md`

---

# 10. Decision Engine 수식 (Softmax + 사용자 Override)

3계층 신호를 통합하여 `RiskKnowledge` 4속성을 산출.

## 10-1. Decision Engine 계약

```kotlin
interface DecisionEngine {
    /**
     * 입력 식별자(번호·URL·앱)에 대한 판단 산출.
     *
     * @param query IdentifierType (phoneNumber·smsMessage·appReputation)
     * @return RiskKnowledge (4속성 + STALE_KNOWLEDGE 플래그)
     */
    suspend fun evaluate(query: IdentifierType): RiskKnowledge

    /**
     * 백그라운드 재검증 큐 등록 (Stale 상태인 엔트리용).
     */
    fun enqueueRefresh(query: IdentifierType)

    /**
     * 사용자 조치 반영 (UserAction 저장 + NKB 재계산 트리거).
     */
    suspend fun applyUserAction(query: IdentifierType, action: UserActionType)
}
```

## 10-2. 통합 알고리즘 (8단계)

```
Input: IdentifierType query

1. NKB Lookup
   hit = nkb.find(query)
   if hit != null && !hit.isStale:
       return toRiskKnowledge(hit)  // L3 경로, 즉시 반환
   if hit != null && hit.isStale:
       background: enqueueRefresh(query)
       return toRiskKnowledge(hit).withFlag(STALE_KNOWLEDGE)

2. Probe Layer 3 (공공 공신력 API, 우선)
   publicResults = publicApiClient.query(query, timeout=1s)

3. Probe Layer 2 (일반 검색, 쿼터 남은 경우)
   if quotaMgr.hasQuota():
       searchResults = searchMesh.query(query, timeout=1s)

4. SearchResultAnalyzer
   signals = [analyzer.analyze(r, tier=tierOf(r)) for r in publicResults + searchResults]

5. Tier 가중치 합산
   weightedCounts = mergeFeatureCounts(signals, weights={T1:0.3, T2:0.5, T3:0.8, T4:1.0})

6. 카테고리 점수 계산 (룰 기반)
   rawScores = ruleEngine.score(weightedCounts)  // {SCAM: 2.4, SPAM: 1.2, SAFE: 0.8, ...}

7. Softmax 정규화 + ConflictResolver
   probs = softmax(rawScores)
   topCategory = argmax(probs)
   topConfidence = max(probs)
   isAmbiguous = (probs.sorted()[0] - probs.sorted()[1]) < 0.15

8. NKB Write (원문 없이)
   nkb.upsert(NumberKnowledge(
       numberE164 = query.value,
       categoryDistribution = probs,
       topCategory = topCategory,
       topConfidence = topConfidence,
       isAmbiguous = isAmbiguous,
       riskLevel = mapToRiskLevel(topCategory, topConfidence),
       signalSummary = summarize(signals, weightedCounts),
       ...
   ))

Output: RiskKnowledge (4속성)
```

## 10-3. Softmax 정규화

```kotlin
fun softmax(scores: Map<ConclusionCategory, Float>): Map<ConclusionCategory, Float> {
    val maxScore = scores.values.max()
    val expScores = scores.mapValues { exp(it.value - maxScore) }  // overflow 방지
    val sum = expScores.values.sum()
    return expScores.mapValues { it.value / sum }
}
```

## 10-4. RiskLevel 매핑

```kotlin
fun mapToRiskLevel(category: ConclusionCategory, confidence: Float): RiskLevel = when {
    category == ConclusionCategory.SCAM && confidence >= 0.8f -> RiskLevel.CRITICAL
    category == ConclusionCategory.SCAM && confidence >= 0.6f -> RiskLevel.HIGH
    category == ConclusionCategory.SPAM && confidence >= 0.7f -> RiskLevel.HIGH
    category == ConclusionCategory.SPAM -> RiskLevel.MEDIUM
    category == ConclusionCategory.AD_AGGRESSIVE -> RiskLevel.MEDIUM
    category == ConclusionCategory.AD_LEGITIMATE -> RiskLevel.LOW
    category == ConclusionCategory.SAFE -> RiskLevel.NONE
    else -> RiskLevel.LOW  // UNKNOWN
}
```

## 10-5. 사용자 Override (Patch 37 타입 통일)

사용자가 "안심 표시" 또는 "스팸 신고"를 하면 Decision Engine이 NKB를 재계산.

**타입 정합 주의 (Patch 37)**: 본 메서드의 `action` 매개변수 타입은 Stage 0 FREEZE(§33-1-4)에서 `UserActionType` enum으로 확정. v1.6.2까지 본 섹션에 `ActionType`으로 표기되어 FREEZE 시그니처와 충돌했으나, 7-워커 평가(Codex CLI 단독 지적)로 발견. v1.7.0에서 `UserActionType`으로 통일.

```kotlin
suspend fun applyUserAction(query: IdentifierType, action: UserActionType) {
    // 1. UserAction 저장 (도메인 모델 UserAction sealed class는 §8-2-2 참조)
    userActionDao.insert(
        UserAction.fromType(action, query).toEntity()
    )

    // 2. NKB 재계산
    val hit = nkb.find(query) ?: return
    val updatedSignals = adjustForUserAction(hit.signalSummary, action)
    val updated = recomputeWithUserBias(hit, updatedSignals)
    nkb.upsert(updated)
}
```

사용자가 "안심 표시"한 번호는 **사용자 자산**이므로 이후 외부 신호와 상관없이 SAFE로 유지 (단, 심각한 공공 신고 신호 등장 시 재검토 알림).

## 10-X. Decision Contract (v1.5.2 Patch 10)

```kotlin
// Frozen: v1.5.2부터 변경 금지
data class Decision(
    val query: IdentifierType,
    val result: RiskKnowledge,
    val stalenessFlag: StalenessFlag,
    val computedAt: Long,
    val engineVersion: String = "v1.7.0"
)

enum class StalenessFlag {
    FRESH,              // 방금 계산
    STALE_KNOWLEDGE,    // NKB Stale, 재검증 예정
    STALE_OFFLINE       // 네트워크 단절, 기존 값 사용
}
```

---

## §10-formula-2axis. 검색 2축 가중치 합산 (v2.5.0 정정, 4축 → 2축)

### 입력 (2축, 헌법 §1 v2.5.0)

| 축 | 입력 형태 | 가중치 W | 신뢰도 C |
|---|---|---|---|
| 1. 내부 NKB | featureCounts (호출/문자 빈도, 마지막 접촉, 태그) | 0.40 | 사용자 자신 이력 = 매우 높음 |
| 2. 외부 AI 검색 (사용자 직접) | 사용자 검색 후 태그 추가 시 입력 | 0.60 | 사용자 직접 검증 + AI 인프라 통합 |

**중요**: 축 2는 customTabOnly = true → query() empty 반환. 사용자 검색 후 태그 추가 시점에만 비-empty result.

**v2.4.0 → v2.5.0 단순화 근거** (대표님 2026-04-29 실측):
- 번호 0322379987 (인천 032 보험 광고 의심) Google AI Mode + Naver AI 검색
- 두 AI 검색 모두 TheCall / JUNKCALL.org / 더콜 / 레드루팅 등 (구) 4축 중 공공 + 경쟁사 자체 통합
- AI 검색 인프라가 RAG (web crawl + LLM synthesis) 보유 → 4축 분리 가중치 합산보다 정확
- 결정: 2축 단순화 (NKB 0.40 + AI 검색 0.60)

### 합산 공식

```
RiskScore = Σ (W_i × C_i × signal_i)

where:
  signal_i ∈ [-1, 1]
  -1 = strong safe (예: 사용자 빈번 통화 + 태그=가족)
   0 = unknown
  +1 = strong danger (예: 사용자 차단 + AI 검색 결과 보이스피싱 신고 다수)
```

### Tier 매핑 (변경 없음)

| RiskScore 범위 | Tier | 색상 | 사용자 추천 행동 |
|---|---|---|---|
| `score >= +0.5` | **Danger** | 빨강 | 거절 + 차단 + 신고 |
| `+0.2 ≤ score < +0.5` | **Caution** | 주황 | 거절 권장 + 직접 검색 |
| `-0.2 < score < +0.2` AND 신뢰도 합산 < 0.4 | **Unknown** | 회색 | **직접 검색 강력 권장** |
| `-0.2 < score < +0.2` AND 신뢰도 합산 ≥ 0.4 | **Caution** | 노랑 | 사용자 판단 |
| `score ≤ -0.2` | **Safe** | 초록 | 정상 수신 |

### Unknown Tier 특별 처리 (변경 없음)

Unknown Tier = "결정 엔진이 충분한 정보를 못 받음". 옛 MyPhoneCheck v1에서 이 영역이 사용자에게 가장 답답한 영역이었음.

대응:
1. **명시 표시**: "정보 부족. 직접 확인이 필요합니다" 메시지
2. **직접 검색 버튼 강조**: "🔍 직접 검색" 버튼을 Tier 표시 옆에 prominent 배치
3. **SIM 기준 AI 검색 후보 메뉴 (최소 2개) 자동 표시** — 사용자가 검색 진입 시 SIM 기준 AI 검색 후보 노출, 사용자 자율 결정 + 마지막 선택 기억
4. **사용자 판단 후 입력**: 사용자가 검색 결과 보고 태그 추가 → NKB 영속화 → 다음 동일 query Tier 갱신

### 6 Surface 적용 (변경 없음)

본 공식은 6 Surface 모두 동일 적용 (헌법 §7 One Engine, N Surfaces).

| Surface | 입력 채널 | Tier 표시 위치 |
|---|---|---|
| CallCheck (수신) | 발신 번호 | CallScreeningService UI |
| CallCheck (종료 후) | 발신 번호 | 오버레이 카드 (또는 알림) |
| MessageCheck | 발신 번호 + 본문 키워드 | 알림 + 자체 UI 카드 |
| PushCheck | 알림 발신 앱 패키지 | 휴지통 항목 |
| CardCheck | 카드 알림 발신 번호 | 카드 알림 UI |
| MicCheck/CameraCheck | 권한 침해 발신 앱 | 권한 침해 알림 |

추가 — **N Inputs 정합** (v2.5.0 신설):
입력 타입(폰 번호 / URL / 메시지 본문 / 앱 패키지명) 무관 동일 2축 패턴. SearchInput sealed class 단일 인터페이스.

### 헌법 정합

- §3 결정권 중앙집중 금지: 우리는 Tier만 제시, 행동 결정은 사용자
- §5 정직성: Unknown은 정직히 Unknown 표시 + 직접 검색 채널 제공
- §1 Out-Bound Zero: 축 2는 사용자 직접 트리거만, 우리 송신 0
- §6 가격 정직성: 2축 모두 우리 비용 0 ($2.49/월 net ARPU $1.49 유지)
- §7 One Engine, N Surfaces / N Inputs: 6 Surface × 4 input type 동일 공식
