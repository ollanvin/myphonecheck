# MyPhoneCheck 1.0 — 코드 검증 보고서
**검증일:** 2026-03-24
**검증자:** 비전

---

## 총평

프로젝트 뼈대와 코드량은 충분하지만, PRD와 실제 코드 사이에 **심각한 모델 불일치**가 존재합니다.
특히 대표님이 가장 강조한 **"통화 이력 세분화"**가 core 모델에서 누락됐습니다.
data 레이어에서는 세분화 데이터를 수집하지만, core 모델로 올라가면서 단일 숫자로 압축됩니다.

**판정: 빌드 불가 상태. 모델 재정렬 필수.**

---

## 1. 치명적 문제 (반드시 수정)

### 문제 1: DeviceEvidence 모델 — PRD와 완전 불일치

**PRD 정의:**
```
outgoingCount, incomingCount, answeredCount, rejectedCount, missedCount, connectedCount
totalDurationSec, avgDurationSec, shortCallCount, longCallCount
lastOutgoingAt, lastIncomingAt, lastConnectedAt, lastRejectedAt, lastMissedAt
smsExists, smsLastAt, localTag, localMemo
```

**실제 코드:**
```
inContactList, callFrequency, firstSeenTimestamp, lastSeenTimestamp, frequencyScore
isBlocked, isMarkedAsSpam, messageCount, isVerified, verificationProvider
```

**핵심 위반:** 대표님이 "통화라고 퉁치지 말고 세분화" 지시를 내렸으나, core 모델은 모든 통화를 `callFrequency` 하나로 압축함. data 레이어의 `CallHistoryDetail`에는 세분화 필드가 다 있지만, `DeviceEvidenceRepositoryImpl`에서 변환 시 손실됨.

**영향:** 앱의 핵심 차별점인 "거래처 vs 스팸 판별"이 불가능해짐.

---

### 문제 2: ConclusionCategory — PRD 핵심 카테고리 누락

**PRD 정의:**
```
KNOWN_CONTACT, BUSINESS_LIKELY, DELIVERY_LIKELY, INSTITUTION_LIKELY,
SALES_SPAM_SUSPECTED, SCAM_RISK_HIGH, INSUFFICIENT_EVIDENCE
```

**실제 코드:**
```
SAFE_KNOWN, SAFE_BUSINESS, SAFE_ORGANIZATION, SUSPICIOUS_UNKNOWN,
SPAM_LIKELY, SCAM_ALERT, FRAUD_WARNING, HARASSMENT, TELEMARKETING,
UNVERIFIABLE, UNKNOWN
```

**누락:** `DELIVERY_LIKELY`, `INSTITUTION_LIKELY` 없음. 이 두 개는 대표님 서비스의 핵심 차별 카테고리임 (택배, 병원/학교 판별).

---

### 문제 3: SearchEvidence 모델 — 완전히 다른 구조

**PRD 정의:**
```
recent30dSearchIntensity, recent90dSearchIntensity, searchTrend
keywordClusters, repeatedEntities, sourceTypes, topSnippets
```

**실제 코드:**
```
searchResults, aggregatedTrends, overallSentiment, threatIndicators, enrichmentScore
```

**영향:** PRD의 "검색 강도 + 키워드 클러스터 + 반복 엔티티" 접근법이 구현되지 않음.

---

### 문제 4: RiskLevel — 값 불일치

**PRD:** HIGH, MEDIUM, LOW, UNKNOWN
**실제:** SAFE, LOW, MEDIUM, HIGH, CRITICAL

DecisionEngine과 UI가 이 enum에 의존하므로 전체 흐름에 영향.

---

### 문제 5: ActionRecommendation — 구조 불일치

**PRD:** 단순 enum (ANSWER, ANSWER_WITH_CAUTION, REJECT, BLOCK_REVIEW, HOLD)
**실제:** data class with primaryAction, secondaryActions, rationale, confidenceScore + UserAction enum

PRD의 핵심인 `ANSWER_WITH_CAUTION`과 `HOLD`가 없음.

---

### 문제 6: DecisionResult — summary/reasons 누락

**PRD:** `summary: String` (한 줄 결론), `reasons: List<String>` (근거 3개)
**실제:** summary는 자동 생성 getter (`"$category - $riskLevel"`), reasons 필드 자체 없음

**영향:** UI에서 "한 줄 결론 + 근거 3개" 표시가 불가능.

---

## 2. 중요 문제

### 빈 모듈 2개
- `feature/search-enrichment/` — 소스 파일 0개
- `data/local-cache/` — 소스 파일 0개

### OkHttp 의존성 누락
`GenericWebSearchProvider`가 OkHttp를 사용하지만 `libs.versions.toml`에 OkHttp 없음.

### Kotlin 2.0.0 + Compose Compiler 1.5.11 호환성
Kotlin 2.0은 Compose Compiler 2.0+을 요구할 수 있음. 1.5.11은 Kotlin 1.9.x 대응.

### Manifest 문제
- `MyPhoneCheckContentProvider` 선언되어 있으나 해당 클래스 파일 없음
- `CallActionReceiver`의 intent-filter에 custom action 사용 — exported=true는 보안 리스크

### connectedCount 계산 오류
`CallLogDataSourceImpl` 142번 줄:
```kotlin
val connectedCount = (0..outgoingCount).intersect((0..incomingCount).toList()).size + answeredCount
```
이 로직은 의미 없음. connectedCount는 duration > 0인 통화 수여야 함.

160번 줄:
```kotlin
connectedCount = outgoingCount + incomingCount,
```
connectedCount가 "연결된 통화"가 아니라 "총 통화 시도"로 잘못 계산됨.

---

## 3. 긍정적 발견

### 잘 된 부분
- **프로젝트 구조**: 멀티모듈 깔끔함 (settings.gradle.kts 적절)
- **Gradle 설정**: version catalog, convention plugin 구조 양호
- **CallLogDataSourceImpl**: 발신/수신/응답/거절/부재중 세분화 쿼리 구현됨 (data 레이어까지는 정확)
- **MyPhoneCheckScreeningService**: 기본 흐름 적절 (인터셉트 → 정규화 → 판단 → 응답)
- **DecisionEngineImpl**: 3축 스코어링 구조 적절 (relationship/risk/category)
- **GenericWebSearchProvider**: 실제 HTTP 호출 구조 있음 (단, API endpoint 미정)
- **빌드 설정**: Hilt, Compose, Room 의존성 적절히 설정됨

---

## 4. 수정 우선순위

### P0 (즉시 수정 — 이것 없이는 앱 의미 없음)
1. `DeviceEvidence` core 모델을 PRD 기준으로 재작성
2. `DeviceEvidenceRepositoryImpl`이 세분화 데이터를 유지하도록 수정
3. `ConclusionCategory`에 DELIVERY_LIKELY, INSTITUTION_LIKELY 추가
4. `DecisionResult`에 summary, reasons 필드 추가
5. `RiskLevel`을 PRD 기준으로 통일
6. `ActionRecommendation`을 PRD 기준 enum으로 단순화

### P1 (빌드 가능 상태 달성)
7. OkHttp 의존성 추가
8. Kotlin/Compose compiler 호환성 수정
9. Manifest에서 없는 ContentProvider 제거
10. connectedCount 계산 로직 수정
11. feature/search-enrichment 모듈 구현 또는 data/search로 통합
12. SearchEvidence 모델을 PRD 기준으로 수정

### P2 (품질 향상)
13. CallActionReceiver 보안 수정 (exported=false + explicit intent)
14. data/local-cache 구현 (성능 최적화)
15. 에러 핸들링 강화
16. 단위 테스트 추가

---

## 5. 최종 판정

| 항목 | 판정 |
|------|------|
| 파일 존재 | ✅ 96개 .kt 파일 확인 |
| 프로젝트 구조 | ✅ 멀티모듈 적절 |
| PRD 모델 일치 | ❌ 치명적 불일치 6건 |
| 빌드 가능 | ❌ 미검증 (호환성 문제 예상) |
| 핵심 기능 완성도 | ⚠️ 60% — 뼈대 있으나 모델 불일치로 조립 불가 |
| production-ready | ❌ 아님 |

**한 줄 결론: 코드량은 충분하지만, PRD와 모델이 안 맞아서 지금 상태로는 조립이 안 됨. P0 6건 수정이 선행되어야 함.**

---

*이 보고서는 실제 파일 내용을 직접 읽고 검증한 결과입니다.*
