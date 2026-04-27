# 10. Decision Engine 수식 (Softmax + 사용자 Override)

**원본 출처**: v1.7.1 §10 (148줄)
**v1.8.0 Layer**: Engine
**의존**: `07_engine/03_nkb.md` + `60_implementation/02_stage0_freeze.md`
**변경 이력**: 본 파일은 v1.7.1 §10 (148줄) 원본 전문 이관본. 텍스트 변경 없음.
**파일 경로**: `docs/00_governance/architecture/v1.8.0_codex/07_engine/05_decision_formula.md`

---


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

