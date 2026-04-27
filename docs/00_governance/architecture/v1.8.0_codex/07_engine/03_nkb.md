# 8. NKB 데이터 설계 (Number Knowledge Base)

**원본 출처**: v1.7.1 §8 (314줄)
**v1.8.0 Layer**: Engine
**의존**: `07_engine/02_self_discovery.md` + `60_implementation/04_memory_budget.md`
**변경 이력**: 본 파일은 v1.7.1 §8 (314줄) 원본 전문 이관본. 텍스트 변경 없음.
**파일 경로**: `docs/00_governance/architecture/v1.8.0_codex/07_engine/03_nkb.md`

---


NKB는 디바이스 내 Local Room DB로, **Tier C 내부 지식만 저장**한다. Tier A 외부 원문은 절대 저장되지 않는다(제2조). v1.3의 모든 Entity 정의를 100% 계승한다.

## 8-1. NumberKnowledge Entity

```kotlin
@Entity(tableName = "number_knowledge")
data class NumberKnowledge(
    @PrimaryKey
    val numberE164: String,           // E.164 국제 포맷 (예: "+821012345678")

    // === 분류 (Softmax 분포) ===
    val categoryDistribution: Map<ConclusionCategory, Float>,
    val topCategory: ConclusionCategory,
    val topConfidence: Float,
    val isAmbiguous: Boolean,         // gap < 0.15 → true
    val riskLevel: RiskLevel,

    // === 신호 요약 (rawSnippet 절대 없음) ===
    val signalSummary: SignalSummary,
    val tierContributions: List<TierContribution>,

    // === 메타 ===
    val firstSeenAt: Long,
    val lastUpdatedAt: Long,
    val isStale: Boolean,
    val sourceCount: Int,             // 누적 신호 출처 수

    // === Cluster (Self-Discovery 결과) ===
    val discoveredClusterId: String,  // "auto_xxx"

    // === 사용자 행동 참조 (별도 테이블 FK) ===
    val userActionCount: Int
)

enum class ConclusionCategory {
    SAFE,           // 안심
    AD_LEGITIMATE,  // 합법 광고
    AD_AGGRESSIVE,  // 공격적 광고
    SPAM,           // 스팸
    SCAM,           // 사기/피싱
    UNKNOWN         // 정보 부족
}

enum class RiskLevel {
    NONE,           // 위험 없음
    LOW,            // 낮음
    MEDIUM,         // 중간
    HIGH,           // 높음
    CRITICAL        // 즉시 차단 권고
}

data class SignalSummary(
    val totalSignals: Int,
    val tier1Count: Int,  // 커뮤니티 (가중치 0.3)
    val tier2Count: Int,  // 일반 사이트 (가중치 0.5)
    val tier3Count: Int,  // 정부/공식 (가중치 0.8)
    val tier4Count: Int,  // 회사 공식 (가중치 1.0)
    val featureCounts: Map<FeatureType, Int>
)

data class TierContribution(
    val tier: Int,
    val weightedScore: Float,
    val signalCount: Int,
    val lastUpdatedAt: Long
)
```

## 8-2. UserAction Entity (Patch 35 — DO_NOT_MISS 서브타입 추가, 코웍 87a9a3 §17-6-5 흡수)

사용자 조치(스팸 신고·차단·안심 표시·**DO_NOT_MISS 지정** 등)를 기록. 본사 전송 없음, 디바이스 내부만.

**Patch 35 변경 사항**: Lane 1 D05가 지적한 "v1.0 §4.1에서 E2E 완성 기능이었던 DO_NOT_MISS가 v1.6.1에서 실종"을 복원한다. 코웍 87a9a3 §17-6-5의 처분 정책을 본 §8-2 + §3-4 + §21로 흡수한다.

### 8-2-1. Entity 정의

```kotlin
@Entity(tableName = "user_action")
data class UserActionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val identifierType: String,    // PhoneNumber / UrlDomain / AppReputation 직렬화
    val identifierValue: String,
    val actionType: String,         // sealed class 직렬화 (kind tag)
    val payloadJson: String?,       // 서브타입별 추가 데이터 (DO_NOT_MISS 메모 등)
    val createdAt: Long
)
```

### 8-2-2. UserAction sealed class

```kotlin
/**
 * 사용자 조치 도메인 모델.
 * v1.5.2 Patch 08에서 IdentifierType 호환으로 확장.
 * v1.6.1 Patch 35에서 DoNotMiss 서브타입 추가.
 */
sealed class UserAction {
    abstract val identifier: IdentifierType
    abstract val createdAt: Long

    data class SpamReport(
        override val identifier: IdentifierType,
        override val createdAt: Long
    ) : UserAction()

    data class Blocked(
        override val identifier: IdentifierType,
        override val createdAt: Long
    ) : UserAction()

    data class AddedToContacts(
        override val identifier: IdentifierType,
        override val createdAt: Long
    ) : UserAction()

    data class MarkedSafe(
        override val identifier: IdentifierType,
        override val createdAt: Long
    ) : UserAction()

    data class MarkedAd(
        override val identifier: IdentifierType,
        override val createdAt: Long
    ) : UserAction()

    data class Unblocked(
        override val identifier: IdentifierType,
        override val createdAt: Long
    ) : UserAction()

    /**
     * Patch 35 신설 (Lane 1 D05 복원).
     * 사용자가 특정 식별자에 대해 "이건 절대 놓치지 마"라고 지정한 우선순위 플래그.
     *
     * 효과 (4 Surface 공통):
     * 1. DecisionEngine이 riskLevel = LOW로 판정해도 사용자에게 반드시 노출
     * 2. 알림은 일반 채널이 아닌 High-Priority Notification Channel 사용
     * 3. CallCheck 오버레이는 dismiss 타임아웃을 평소보다 길게 (5초 → 15초)
     * 4. NKB 캐시 우선순위 상위 (Stale 갱신 시 먼저 재검증)
     */
    data class DoNotMiss(
        override val identifier: IdentifierType,
        override val createdAt: Long,
        val userMemo: String? = null   // 사용자가 직접 입력한 메모 (예: "엄마 병원 번호")
    ) : UserAction()
}
```

### 8-2-3. 도메인 ↔ Entity 변환

```kotlin
fun UserAction.toEntity(): UserActionEntity = UserActionEntity(
    identifierType = identifier::class.simpleName ?: "Unknown",
    identifierValue = when (val id = identifier) {
        is IdentifierType.PhoneNumber -> id.value
        is IdentifierType.UrlDomain -> id.value
        is IdentifierType.AppReputation -> id.value
    },
    actionType = this::class.simpleName ?: "Unknown",
    payloadJson = if (this is UserAction.DoNotMiss) {
        Json.encodeToString(mapOf("memo" to userMemo))
    } else null,
    createdAt = createdAt
)

fun UserActionEntity.toDomain(): UserAction { /* ... 역변환 ... */ }
```

### 8-2-4. Phase 별 적용 범위

코웍 87a9a3 §17-6-5의 단계적 적용 정책 계승:

| Phase | DO_NOT_MISS 적용 Surface |
|---|---|
| Phase 1 | CallCheck (착신 오버레이 dismiss 시간 연장 + High-Priority Channel) |
| Phase 2 | MessageCheck Mode A·B (HIGH 알림 채널 분기) |
| Phase 3 | MicCheck·CameraCheck (특정 앱 권한 변동 시 강조 알림) |
| Phase 후행 | PushCheck·AppSecurityWatch도 동일 규칙 적용 |

### 8-2-5. UX 진입 경로

- CallCheck PostCallScreen 4버튼 옆 메뉴 → "DO_NOT_MISS로 지정" + 메모 입력
- 설정 → "DO_NOT_MISS 목록 관리" → 추가·삭제·메모 수정
- 4속성 카드 우상단 메뉴에서도 토글 가능

## 8-3. ExtractedSignal (rawSnippet 완전 제거)

```kotlin
// v1.2 → v1.3 변경 핵심: rawSnippet 완전 제거
// v1.5 patch 03: numberE164 필드 추가 (자비스 라운드 2 지적 반영)
// v1.6.1 유지
data class ExtractedSignal(
    val numberE164: String,                       // 어느 번호의 신호인지 식별
    val signalType: SignalType,
    val sourceTier: Int,                          // 1~4
    val featureCounts: Map<FeatureType, Int>,     // 예: {SCAM_KEYWORD: 12, AD_KEYWORD: 3}
    val extractedAt: Long
    // ❌ rawSnippet: String (200자) — v1.3에서 완전 제거, 이후 유지
    // ❌ sourceProvider: String — v1.3에서 제거 (Tier 정보로 충분)
)

// v1.6.1 헌법 정합:
// - 제2조 (In-Bound Zero): numberE164는 외부 원문이 아닌 식별자이므로 정합 ✓
// - 제1조 (Out-Bound Zero): numberE164는 디바이스 내부 처리만, 외부 전송 0 ✓

enum class FeatureType {
    SCAM_KEYWORD,            // 사기 관련 키워드 출현 횟수
    AD_KEYWORD,              // 광고 관련 키워드 출현 횟수
    OFFICIAL_DOMAIN_HIT,     // 정부/공식 도메인 매칭 횟수
    URL_RISK_INDICATOR,      // 의심 URL 패턴 횟수
    USER_REVIEW_NEGATIVE,    // 사용자 부정 리뷰 횟수
    USER_REVIEW_POSITIVE,    // 사용자 긍정 리뷰 횟수
    PHONE_FORMAT_SUSPICIOUS  // 의심 전화 포맷 횟수
}

enum class SignalType {
    SCAM_INDICATOR,
    AD_INDICATOR,
    OFFICIAL_RECOGNITION,
    USER_FEEDBACK_NEGATIVE,
    USER_FEEDBACK_POSITIVE,
    NEUTRAL_INFORMATION
}
```

## 8-4. ClusterProfile Entity

Self-Discovery 결과 저장. L3에서 probe 실패해도 기존 값 사용.

```kotlin
@Entity(tableName = "cluster_profile")
data class ClusterProfile(
    @PrimaryKey val clusterId: String,            // "auto_xxx"
    val discoveredEngines: List<SearchEngineRef>,
    val discoveredOfficialDomains: List<DomainRef>,
    val simMcc: String?,
    val simMnc: String?,
    val networkCountryIso: String?,
    val locale: String,
    val timeZone: String,
    val discoveredAt: Long,
    val lastVerifiedAt: Long
)

data class SearchEngineRef(
    val domain: String,
    val responseTimeMs: Long
)

data class DomainRef(
    val tld: String,
    val sample: String
)
```

## 8-5. Stale 정책 (Tier별 maxAge)

§6-4 참조. Stale은 삭제 트리거가 아니라 신뢰도 하락 플래그.

## 8-6. 4속성과 NKB 매핑

헌법이 요구하는 4속성 출력이 NKB 필드에서 어떻게 산출되는지 정합 검증.

| 출력 속성 | 산출 NKB 필드 | 산출 방식 |
|---|---|---|
| 위험도 | NumberKnowledge.riskLevel + topConfidence | 직접 매핑 |
| 예상 손해 | RiskLevel → 정적 매핑 (디바이스 내) | `RiskLevel.HIGH` → "금전 피해 가능", `MEDIUM` → "시간 손해 가능" 등 strings.xml 다국어 |
| 손해 유형 | NumberKnowledge.topCategory | `ConclusionCategory.SCAM` → "금융사기", `AD_AGGRESSIVE` → "광고" 등 strings.xml |
| 이유 설명 | NumberKnowledge.signalSummary 기반 | `tier3Count > 0` → "정부 신고 이력", `userActionCount > 5` → "사용자 신고 다수" 등 룰 기반 한 줄 자동 생성 |

**4속성 모두 NKB Hit만으로 산출 가능** → L3 호환 100%.

## 8-X. Data Model Freeze Declaration (v1.5.2 Patch 11)

### 8-X-1. Frozen 모델 선언

v1.5.2 시점부터 다음 데이터 모델을 **Frozen** 상태로 선언한다. v1.6.1에서도 계승.

| 모델 | Frozen 시점 | 비고 |
|---|---|---|
| ExtractedSignal | v1.5.2 (Patch 03 적용본) | numberE164 필드 포함 |
| NumberKnowledge | v1.5.2 (Patch 08 RiskKnowledge 호환) | identifierType/Value 필드 포함 |
| UserAction | v1.5.2 (Patch 08 마이그레이션 후) | identifierType/Value 필드 포함 |
| Decision | v1.5.2 (Patch 10 Contract 반영) | STALE_KNOWLEDGE 플래그 포함 |

### 8-X-2. 변경 절차

- Frozen 모델의 필드 변경·삭제는 **메이저 버전 (v2.0.0+)**에서만 허용
- 패치 버전 (v1.5.x ~ v1.6.x)에서는 **추가 필드만 허용**, 기존 필드 변경 금지
- 변경 시 Room Migration 코드 동시 작성 의무
- 0-B 정직성 감사 로그에 변경 사유 기록

### 8-X-3. 모델 호환성 테스트 (Day 6 신규)

```kotlin
class MigrationCompatTest {
    @Test
    fun `v1_to_v2 migration preserves all non-deprecated fields`() {
        // 실행 시 v1 DB → v2 DB 마이그레이션 후
        // 모든 기존 필드가 동일하게 보존되는지 검증
    }

    @Test
    fun `FreezeMarkerTest all Frozen fields still exist`() {
        // ExtractedSignal·NumberKnowledge·UserAction·Decision
        // 각각의 필수 필드 존재를 리플렉션으로 검증
    }
}
```

---

