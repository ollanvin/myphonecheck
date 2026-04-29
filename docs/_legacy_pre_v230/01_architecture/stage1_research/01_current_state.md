# Stage 1 사전 조사 — 현재 상태 (`01_current_state.md`)

인코딩: UTF-8 (BOM 없음), 줄바꿈 LF.  
근거: `settings.gradle.kts`, `core/common/src/main/kotlin/**`, `core/model/src/main/kotlin/**`, `docs/00_governance/project-governance.md`, `docs/02_product/specs/PRD_CALLCHECK_V1.md`. Git 미사용.

---

## 1. `settings.gradle.kts` 모듈 인벤토리

`rootProject.name = "MyPhoneCheck"`. `include`된 Gradle path와 대응 `build.gradle.kts` 위치:

| Gradle path | 빌드 스크립트 |
|-------------|----------------|
| `:app` | `app/build.gradle.kts` |
| `:core:common` | `core/common/build.gradle.kts` |
| `:core:model` | `core/model/build.gradle.kts` |
| `:core:util` | `core/util/build.gradle.kts` |
| `:core:security` | `core/security/build.gradle.kts` |
| `:feature:call-intercept` | `feature/call-intercept/build.gradle.kts` |
| `:feature:device-evidence` | `feature/device-evidence/build.gradle.kts` |
| `:feature:search-enrichment` | `feature/search-enrichment/build.gradle.kts` |
| `:feature:decision-engine` | `feature/decision-engine/build.gradle.kts` |
| `:feature:decision-ui` | `feature/decision-ui/build.gradle.kts` |
| `:feature:settings` | `feature/settings/build.gradle.kts` |
| `:feature:billing` | `feature/billing/build.gradle.kts` |
| `:feature:country-config` | `feature/country-config/build.gradle.kts` |
| `:feature:message-intercept` | `feature/message-intercept/build.gradle.kts` |
| `:feature:privacy-check` | `feature/privacy-check/build.gradle.kts` |
| `:data:contacts` | `data/contacts/build.gradle.kts` |
| `:data:calllog` | `data/calllog/build.gradle.kts` |
| `:data:sms` | `data/sms/build.gradle.kts` |
| `:data:search` | `data/search/build.gradle.kts` |
| `:data:local-cache` | `data/local-cache/build.gradle.kts` |

주석: `feature:push-intercept`는 v1.1 아키텍처에 따라 제거되었다고 명시되어 있으며, 본 `include` 목록에는 없음. `pluginManagement`에 `includeBuild("build-logic")` 있음.

---

## 2. `core:common` 패키지·계약 인벤토리

패키지 루트: `app.myphonecheck.core.common.*` (JVM 모듈, `kotlin-jvm` + `jvmToolchain(17)`).

| 소스 경로 (대표) | 선언 종류 | 식별자 |
|------------------|-----------|--------|
| `checker/Checker.kt` | `interface` | `Checker<IN, OUT>` — `suspend fun check(input: IN): OUT` |
| `checker/Checker.kt` | `class` | `CheckerException(message, reason, cause?)` — 내부 `enum Reason` (`NETWORK_TIMEOUT`, `PERMISSION_DENIED`, `INVALID_INPUT`, `NKB_READ_FAILED`, `UNKNOWN`) |
| `engine/DecisionEngineContract.kt` | `interface` | `DecisionEngineContract` — `suspend fun sourceEvidence(identifier: IdentifierType): List<SearchEvidence>`; `suspend fun search(query: String): List<SearchEvidence>`; `suspend fun synthesize(identifier: IdentifierType, evidence: List<SearchEvidence>): RiskKnowledge` |
| `identifier/IdentifierType.kt` | `sealed class` | `IdentifierType` — `PhoneNumber`, `SmsMessage`, `AppPackage` |
| `risk/RiskKnowledge.kt` | `interface` | `RiskKnowledge` — `identifier`, `riskScore`, `riskLevel`(기본 getter), `expectedDamage`, `damageType`, `reasoning`, `evidence`(기본 `emptyList()`), `analyzedAt` |
| `risk/RiskLevel.kt` | `enum class` | `RiskLevel` — `SAFE`, `SAFE_UNKNOWN`, `UNKNOWN`, `CAUTION`, `DANGER` (각 `score: Float`); `fromScore(Float)` |
| `risk/SearchEvidence.kt` | `data class` | `SearchEvidence(source, summary, timestamp)` — 내부 `enum Layer` (`L1_NKB`, `L2_SEARCH`, `L3_PUBLIC_DB`) |
| `risk/DamageType.kt` | `enum class` | `DamageType` (다중 값; FREEZE: MINOR로 enum 추가) |
| `risk/DamageEstimate.kt` | `data class` | `DamageEstimate` |

동결 선언 요약은 `core/common/FREEZE.md`에도 열거됨 (`IdentifierType`, `RiskKnowledge`, `Checker`, `DecisionEngineContract`, `RiskLevel`, `DamageEstimate`, `DamageType`, `SearchEvidence`).

---

## 3. `core:model` 동명·유사 타입 인벤토리

패키지 루트: `app.myphonecheck.mobile.core.model` (Android Library).

### 3-1. `RiskLevel` (4단계)

파일: `core/model/src/main/kotlin/app/myphonecheck/mobile/core/model/RiskLevel.kt`

```kotlin
package app.myphonecheck.mobile.core.model

enum class RiskLevel(val displayNameEn: String, val displayNameKo: String) {
    HIGH("High Risk", "위험 높음"),
    MEDIUM("Caution", "주의"),
    LOW("Low Risk", "위험 낮음"),
    UNKNOWN("Unknown", "불명"),
}
```

### 3-2. `SearchEvidence` (의사결정용 검색 신호 모델)

파일: `core/model/src/main/kotlin/app/myphonecheck/mobile/core/model/SearchEvidence.kt`

- `data class SearchEvidence` — 필드 예: `recent30dSearchIntensity`, `recent90dSearchIntensity`, `searchTrend`, `keywordClusters`, `repeatedEntities`, `sourceTypes`, `topSnippets`, `signalSummaries`, `adjacentNumberHint` 등 (동반 타입: `SignalSummary`, `AdjacentNumberHint`, `SearchTrend`).

`core:model`에는 `RiskKnowledge`, `DecisionEngineContract`, `Checker` 동명 선언 **없음** (소스 트리 `grep` 기준).

---

## 4. 두 타입 계열의 구체적 차이

### 4-1. `RiskLevel`

| 항목 | `app.myphonecheck.core.common.risk.RiskLevel` | `app.myphonecheck.mobile.core.model.RiskLevel` |
|------|-----------------------------------------------|------------------------------------------------|
| **import** | `app.myphonecheck.core.common.risk.RiskLevel` | `app.myphonecheck.mobile.core.model.RiskLevel` |
| **종류** | `enum` + 부동소수 `score` + `fromScore` | `enum` + `displayNameEn` / `displayNameKo` |
| **값 개수** | 5: `SAFE`, `SAFE_UNKNOWN`, `UNKNOWN`, `CAUTION`, `DANGER` | 4: `HIGH`, `MEDIUM`, `LOW`, `UNKNOWN` |
| **의미 축** | 0~1 스코어 구간으로 이산화 | UI/제품 카피 축 (영·한 표시명) |

동일 식별자 `UNKNOWN`이 양쪽에 존재하나, **주변 값 집합·매핑 규칙이 다름** (5단계 vs 4단계).

### 4-2. `SearchEvidence`

| 항목 | `core.common.risk.SearchEvidence` | `core.model.SearchEvidence` |
|------|-------------------------------------|-----------------------------|
| **import** | `app.myphonecheck.core.common.risk.SearchEvidence` | `app.myphonecheck.mobile.core.model.SearchEvidence` |
| **역할** | 3계층 소싱 요약 한 건 (`Layer` + `summary` + `timestamp`) | 검색 강도·키워드·엔티티·스니펫·신호 요약 등 **풍부한 의사결정 입력** |
| **핵심 필드** | `source: Layer`, `summary: String`, `timestamp: Long` | `recent30dSearchIntensity`, `keywordClusters`, `repeatedEntities`, … (다수) |
| **중첩 enum** | `Layer { L1_NKB, L2_SEARCH, L3_PUBLIC_DB }` | 별도; `SearchTrend` 등 다른 타입 사용 |

이름만 같고 **데이터 모델이 완전히 다르며**, 직접 치환·묵시적 캐스팅 불가.

---

## 5. `project-governance.md` — "Stage" 정의 위치 및 인용

**위치:** `docs/00_governance/project-governance.md`, 섹션 `## In-repo architecture charter`, 마지막 불릿(약 62행).

**본문 인용 (원문 그대로):**

> **Stage 0 — Common contracts (Coding WO f1a85c + hotfix e3b05e Java 17):** 순수 JVM 모듈 `:core:common` (`app.myphonecheck.core.common.*`) — `IdentifierType`, `RiskKnowledge` 계약군, `Checker`, `DecisionEngineContract`, `FREEZE.md`, `FreezeMarkerTest`, 워크플로 `.github/workflows/contract-freeze-check.yml`. 리포 전역 **JDK 17** (`jvmToolchain(17)`, `gradle.properties`의 `org.gradle.java.installations.auto-download`, 루트·`build-logic` `settings.gradle.kts`의 **Foojay toolchain resolver** `0.9.0`). 검증: `./gradlew :core:common:test`. 런타임 classpath에 `android*` 의존성 없음.

동일 파일의 앞부분에서 **코딩 "Stage 1"**을 별도 문단으로 정의한 항목은 확인되지 않음(본 조사 범위).

---

## 6. `PRD_CALLCHECK_V1.md` — "Stage" 정의 위치 및 인용

**위치:** `docs/02_product/specs/PRD_CALLCHECK_V1.md`, `## 4. Decision Flow (3-Stage Engine)` 하위.

**본문 인용 (원문 그대로):**

> ## 4. Decision Flow (3-Stage Engine)
>
> ### Stage 1: Saved Check
> - If number is saved in contacts → show known contact name → recommend Answer
>
> ### Stage 2: Device Evidence Analysis
> - If NOT saved but device history exists → "Relationship Recovery Mode"
> - Analyze:
>   - Outgoing call attempts by user
>   - Incoming call attempts by other party
>   - Answered incoming calls
>   - Rejected calls
>   - Missed calls
>   - Successfully connected calls (with duration)
>   - Short calls (< 10s)
>   - Long meaningful calls (> 60s)
>   - Total/average duration
>   - Last interaction timestamps (by type)
>   - SMS existence and last timestamp
>
> ### Stage 3: Search Platform Enrichment
> - If NO device history or evidence is weak → "External Inference Mode"
> - Search signals:
>   - Recent search intensity (30d / 90d)
>   - Keyword clustering (delivery, hospital, company, loan, spam, scam)
>   - Repeated entity names (company, brand, courier)
>   - Source types (official site, community, blog, news, spam-report)

### 6-1. 제품 Stage vs 코딩 Stage 구분

- **제품 Stage (PRD):** 수신 통화 의사결정 **플로우 단계** — 연락처 저장 여부(1) → 온디바이스 증거(2) → 검색 보강(3). 번호 체계는 PRD §4.
- **코딩 Stage (`project-governance.md`):** **공통 계약 모듈 완료 단계**로서 현재 문서에 명시된 것은 **Stage 0** (`:core:common` 동결 등) 뿐. PRD의 "Stage 1: Saved Check"와 **같은 번호 체계·같은 정의가 아님**.

---

## 7. 미해결 질문 (본 파일 범위)

1. filter-repo 이후 Architecture v1.6.1 draft/바이너리의 리포 내 실제 경로.
2. `:core:common` 첫 소비 모듈을 `feature:decision-engine`로 둘지, 신규 구현 모듈을 둘지.

(외부 `CONSTITUTION.md`의 코딩 Stage 정의는 본 워크오더에서 제외.)
