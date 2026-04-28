## 23-4. FREEZE.md 내용 (요약)

**원본 출처**: v1.7.1 §23-4,5 + §33-1 (2701–2722 + 3750–3947)
**v1.8.0 Layer**: Implementation
**의존**: `00_core/01_primary.md`
**변경 이력**: 본 파일은 v1.7.1 §23-4,5 + §33-1 원본 전문 이관본. 텍스트 변경 없음.
**파일 경로**: `docs/00_governance/architecture/v1.8.0_cursor/60_implementation/02_stage0_freeze.md`

---

## 23-4. FREEZE.md 내용 (요약)

```markdown
# core/common FREEZE 선언

- Frozen 시점: 2026-04-22 Stage 0 Contracts 완료
- 변경 금지: 파일·클래스·메서드 시그니처·이름·가시성
- 변경 필요 시: MAJOR 버전 (v2.0.0)에서만 + 대표님 승인
- 검증: FreezeMarkerTest 22개 (CI에서 매 PR 실행, Patch 37 통일)
- CI 강제: PR의 core/common 변경 시 FreezeMarkerTest 실패 → 머지 차단
```

## 23-5. 기타 폴더 README 철칙 (메모리)

메모리 철칙에 따라 모든 폴더는 README.md 필수. 4종 항목:
1. 목적 (이 폴더의 존재 이유)
2. 책임 범위 (어떤 코드·문서가 들어가는가)
3. 외부 인터페이스 (다른 폴더에서 어떻게 사용하는가)
4. 내부 파일 안내 (각 파일의 역할)

빈 README·폴더명만 적힌 README는 위반.

# 33. Store Policy 대응 + Stage 0 Contracts

## 33-1. Stage 0 Contracts 전문 (Kotlin 소스)

Stage 0에서 FREEZE된 4 계약의 Kotlin 소스 전문. **변경 금지 (§23-4 FREEZE.md)**.

### 33-1-0. FREEZE 대상 명세 (헐크 Lane 3 P1-2 해결)

본 표는 Stage 0 Contracts FREEZE의 정확한 경계를 규정한다. `FreezeMarkerTest`가 검증하는 대상 일체이다.

| # | 파일 | 요소 | 종류 | FREEZE 시점 | 위반 시 영향 |
|---|---|---|---|---|---|
| 1 | `IdentifierType.kt` | `sealed class IdentifierType` | 타입 계층 구조 | 2026-04-22 | 모든 Surface 엔진 진입점 깨짐 |
| 1-a | `IdentifierType.kt` | `data class PhoneNumber(val value: String)` | 서브타입 + 필드명 | 2026-04-22 | CallCheck·MessageCheck 호출 깨짐 |
| 1-b | `IdentifierType.kt` | `data class UrlDomain(val value: String)` | 서브타입 + 필드명 | 2026-04-22 | MessageCheck URL 평가 깨짐 |
| 1-c | `IdentifierType.kt` | `data class AppReputation(val value: String)` | 서브타입 + 필드명 | 2026-04-22 | AppSecurityWatch(후행) 진입점 깨짐 |
| 2 | `RiskKnowledge.kt` | `interface RiskKnowledge` | 인터페이스 | 2026-04-22 | 4속성 출력 규격 깨짐 |
| 2-a | `RiskKnowledge.kt` | `val identifier: IdentifierType` | 프로퍼티 시그니처 | 2026-04-22 | 식별자 추적 불가 |
| 2-b | `RiskKnowledge.kt` | `val riskLevel: RiskLevel` | 프로퍼티 시그니처 | 2026-04-22 | 위험도 표시 깨짐 |
| 2-c | `RiskKnowledge.kt` | `val expectedDamage: DamageEstimate` | 프로퍼티 시그니처 | 2026-04-22 | 손해 표시 깨짐 |
| 2-d | `RiskKnowledge.kt` | `val damageTypes: List<DamageType>` | 프로퍼티 시그니처 | 2026-04-22 | 손해 유형 칩 깨짐 |
| 2-e | `RiskKnowledge.kt` | `val reasonSummary: String` | 프로퍼티 시그니처 | 2026-04-22 | 이유 설명 깨짐 |
| 2-f | `RiskKnowledge.kt` | `val computedAt: Long` | 프로퍼티 시그니처 | 2026-04-22 | Stale 판정 깨짐 |
| 2-g | `RiskKnowledge.kt` | `val stalenessFlag: StalenessFlag` | 프로퍼티 시그니처 | 2026-04-22 | L3 경로 표시 깨짐 |
| 2-h | `RiskKnowledge.kt` | `enum RiskLevel { NONE, LOW, MEDIUM, HIGH, CRITICAL }` | enum 값 + 순서 | 2026-04-22 | RiskBadge·`mapToRiskLevel` 깨짐 |
| 2-i | `RiskKnowledge.kt` | `data class DamageEstimate` | 클래스 + 필드 | 2026-04-22 | DamageEstimate 표시 깨짐 |
| 2-j | `RiskKnowledge.kt` | `enum DamageType` | enum 값 | 2026-04-22 | DamageTypeChip 깨짐 |
| 2-k | `RiskKnowledge.kt` | `enum StalenessFlag { FRESH, STALE_KNOWLEDGE, STALE_OFFLINE }` | enum 값 + 순서 | 2026-04-22 | SLA Detector·UI 분기 깨짐 |
| 3 | `Checker.kt` | `fun interface Checker<IN, OUT : RiskKnowledge>` | 함수형 인터페이스 + 제네릭 경계 | 2026-04-22 | 모든 Surface Checker 깨짐 |
| 3-a | `Checker.kt` | `suspend fun check(input: IN): OUT` | 메서드 시그니처 | 2026-04-22 | Surface 호출 컨벤션 깨짐 |
| 4 | `DecisionEngineContract.kt` | `interface DecisionEngineContract` | 인터페이스 | 2026-04-22 | 엔진 진입점 깨짐 |
| 4-a | `DecisionEngineContract.kt` | `suspend fun evaluate(query: IdentifierType): RiskKnowledge` | 메서드 시그니처 | 2026-04-22 | 모든 Surface 평가 호출 깨짐 |
| 4-b | `DecisionEngineContract.kt` | `fun enqueueRefresh(query: IdentifierType)` | 메서드 시그니처 | 2026-04-22 | Stale 재검증 큐 깨짐 |
| 4-c | `DecisionEngineContract.kt` | `suspend fun applyUserAction(query, action: UserActionType)` | 메서드 시그니처 | 2026-04-22 | UserAction → NKB 재계산 깨짐 |
| 4-d | `DecisionEngineContract.kt` | `enum UserActionType` | enum 값 | 2026-04-22 | 사용자 조치 분류 깨짐 |
| 5 | `FreezeMarker.kt` | `annotation class FreezeMarker(val frozenSince: String)` | 어노테이션 + 파라미터 | 2026-04-22 | FreezeMarkerTest 자체 깨짐 |

**총 FREEZE 항목**: 5개 파일, 22개 시그니처. `FreezeMarkerTest`는 위 22개를 모두 reflection으로 검증.

**변경 절차** (FREEZE 해제):
1. MAJOR 버전 업 (v2.0.0+) 시점에만 가능
2. 대표님 명시 승인 필수
3. 변경 사유 + Migration 계획 + 영향 받는 모듈 전수 조사 첨부
4. `FreezeMarkerTest` 갱신 + 모든 테스트 그린 확인
5. 본 §33-1-0 표 갱신 + §0-A-1 헌법 변경 추적과 별도 추적 로그 작성

### 33-1-1. IdentifierType.kt

```kotlin
package com.myphonecheck.core.common

/**
 * 디바이스가 판단하는 식별자 유형.
 *
 * One Engine, N Surfaces 원칙: 엔진은 IdentifierType으로 분기하되,
 * 분기별로 별도 엔진을 만들지 않는다.
 *
 * FREEZE 시점: 2026-04-22 Stage 0 Contracts 완료
 * FREEZE 이유: Decision Engine이 모든 Surface에서 공유하는 공통 입력 타입.
 *              변경 시 모든 Surface의 엔진 호출 경로가 영향받음.
 */
sealed class IdentifierType {

    /** E.164 국제 포맷 전화번호 (예: "+821012345678") */
    data class PhoneNumber(val value: String) : IdentifierType()

    /** URL 도메인 (예: "coupang.com", "coupang-delivery.xyz") */
    data class UrlDomain(val value: String) : IdentifierType()

    /** Android 앱 패키지명 (예: "com.example.app") */
    data class AppReputation(val value: String) : IdentifierType()
}
```

### 33-1-2. RiskKnowledge.kt

```kotlin
package com.myphonecheck.core.common

/**
 * 엔진이 반환하는 판단 결과의 공통 계약.
 *
 * 4속성 출력: riskLevel, expectedDamage, damageTypes, reasonSummary
 *
 * FREEZE 시점: 2026-04-22 Stage 0 Contracts 완료
 * FREEZE 이유: 모든 Surface가 이 규격으로 렌더링.
 *              필드 추가·삭제 시 UI 렌더링 전체 깨짐.
 */
interface RiskKnowledge {
    val identifier: IdentifierType
    val riskLevel: RiskLevel
    val expectedDamage: DamageEstimate
    val damageTypes: List<DamageType>
    val reasonSummary: String
    val computedAt: Long
    val stalenessFlag: StalenessFlag
}

enum class RiskLevel {
    NONE, LOW, MEDIUM, HIGH, CRITICAL
}

data class DamageEstimate(
    val averageAmount: Long?,    // 평균 손해 금액 (현지 통화, null 허용)
    val medianAmount: Long?,     // 중앙값
    val confidence: Float        // 추정 신뢰도 0.0 ~ 1.0
)

enum class DamageType {
    FINANCIAL_SCAM,      // 금융사기
    IDENTITY_THEFT,      // 개인정보 유출
    HARASSMENT,          // 괴롭힘
    UNWANTED_AD,         // 원치 않는 광고
    TIME_WASTE,          // 시간 손해
    PRIVACY_BREACH,      // 프라이버시 침해
    SECURITY_VULNERABILITY  // 보안 취약점 (앱)
}

enum class StalenessFlag {
    FRESH,
    STALE_KNOWLEDGE,
    STALE_OFFLINE
}
```

### 33-1-3. Checker.kt

```kotlin
package com.myphonecheck.core.common

/**
 * 범용 Checker 계약.
 *
 * 모든 Surface의 엔진 진입점이 이 인터페이스를 구현한다.
 * IN은 Surface별 입력 타입, OUT은 4속성을 담은 결과 타입.
 *
 * FREEZE 시점: 2026-04-22 Stage 0 Contracts 완료
 * FREEZE 이유: Surface 추가 시에도 동일 계약으로 통합.
 */
fun interface Checker<IN, OUT : RiskKnowledge> {
    suspend fun check(input: IN): OUT
}
```

### 33-1-4. DecisionEngineContract.kt

```kotlin
package com.myphonecheck.core.common

/**
 * Decision Engine의 외부 계약.
 *
 * 내부 구현은 engine/decision 모듈에 있으며,
 * feature/* 는 이 계약만 본다.
 *
 * FREEZE 시점: 2026-04-22 Stage 0 Contracts 완료
 * FREEZE 이유: 모든 Surface의 엔진 호출 경로 공통 진입점.
 */
interface DecisionEngineContract {
    /** 단일 식별자 평가. 메인 API. */
    suspend fun evaluate(query: IdentifierType): RiskKnowledge

    /** 백그라운드 재검증 큐 등록 (Stale 엔트리용) */
    fun enqueueRefresh(query: IdentifierType)

    /** 사용자 조치 반영 + NKB 재계산 */
    suspend fun applyUserAction(query: IdentifierType, action: UserActionType)
}

enum class UserActionType {
    SPAM_REPORT,
    BLOCKED,
    ADDED_TO_CONTACTS,
    MARKED_SAFE,
    MARKED_AD,
    UNBLOCKED
}
```

### 33-1-5. FreezeMarker.kt

```kotlin
package com.myphonecheck.core.common

/**
 * FREEZE 선언 마커.
 * 이 어노테이션이 붙은 요소는 시그니처 변경 금지.
 * FreezeMarkerTest가 CI에서 검증.
 */
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD
)
@Retention(AnnotationRetention.RUNTIME)
annotation class FreezeMarker(val frozenSince: String)
```
