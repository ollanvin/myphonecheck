# 종합 보고서: 헌법군 + 신규 정보 합성 (Pre-Stage 1)

**문서 ID**: synthesis_2026-04-22_pre-stage1
**작성일**: 2026-04-22
**작성자**: 코웍 (Claude Cowork)
**검수 대상**: 비전 (Claude Opus 4.7)
**최종 소비자**: 대표님 (founder@idolab.ai)
**리포 HEAD**: 96471c6 (`chore(stage0-cleanup): preserve stage1 research + ignore hprof`)
**법적 지위**: 차기 헌법 패치 또는 코어 재구축 스펙 v3의 근거 자료

---

## § 1. 문서 목적

본 보고서는 MyPhoneCheck 리포에 실재하는 **6종 헌법군 문서**를 전수 정독하고, 대표님이 2026-04-22 신규 지시한 **2건의 축**(검색 3대 축, Mic/Camera 침해 이벤트 정기 검색)을 기존 헌법 체계 위에 반영하기 위한 **의사결정 자료**이다.

본 문서의 범위는 다음과 같다.

- 헌법군 6종의 현재 선언 요약 및 상호 참조
- 헌법 간 충돌·모호점 식별
- 신규 축 2건의 헌법 반영 설계안
- 기존 conformance 보고서 갭 5건의 우선순위 재평가
- Stage 1 범위 재정의 초안
- 대표님 의사결정 필요 항목 정리

---

## § 2. 헌법군 인벤토리

### 2-1. Base Architecture v1

**파일**: `docs/01_architecture/myphonecheck_base_architecture_v1.md`
**핵심 선언**: MyPhoneCheck는 4개 체크 유닛(Call/Message/Mic/Camera)으로 구성된 결정 지원 앱이다. 4층 레이어(Presentation / Decision·Domain / Data / Device Integration)를 기준 축으로 선언한다. 190국+ 동시 론칭, 글로벌 단일 요금(~1.5 USD), 첫 1개월 무료, 31일째 과금을 명시한다. §13에 제로 비용·제로 저장 원칙과 네오 중심 운영의 정의 정합을 후속 과제로 남겨둔다.
**상위 참고**: `global-single-core-system.md`, `PRD_CALLCHECK_V1.md`를 명시적으로 참조한다.

### 2-2. Global Single Core System

**파일**: `docs/01_architecture/global-single-core-system.md`
**핵심 선언**: 3계층 구조(Global Core / Country Policy Layer / Presentation Layer)를 고정한다. CallCheck Core Engine이 **유일한 판정 코어**이며, MessageCheck는 동일 코어의 확장 경로이다. **PushCheck는 disabled** 상태로 명시되어 있고, 국가별 코어 분기를 금지한다.
**상위 관계**: Base Architecture §9와 병립하며, Base는 4층, 본 문서는 3층으로 기술 축이 다르다. Base §9 본문에서 "충돌하지 않는다"고 명시적으로 선언한다 (`myphonecheck_base_architecture_v1.md` L63-64).

### 2-3. PRD CallCheck V1

**파일**: `docs/02_product/specs/PRD_CALLCHECK_V1.md`
**핵심 선언**: "Decide before you answer"를 제품 약속으로 고정한다. 3-Stage Decision Flow(Saved Check → Device Evidence → Search Enrichment)를 정의한다. 가격 USD 1/month (Base의 ~1.5 USD와 상이). 비교부적 규칙 13개(Non-Negotiable Rules)를 선언한다. Kotlin 데이터 모델(`DeviceEvidence`, `SearchEvidence`, `DecisionResult`)과 판정 규칙(KNOWN_CONTACT ~ INSUFFICIENT_EVIDENCE)을 포함한다.
**상위 관계**: Base Architecture의 하위 제품 스펙으로 위치하며, Base §12가 PRD를 참고 대상으로 지정한다.

### 2-4. Project Governance

**파일**: `docs/00_governance/project-governance.md`
**핵심 선언**: 프로젝트 거버넌스 규칙을 고정한다. Fixed Project Principles 12개항을 선언한다. 문서 표준 폴더 구조(`docs/00~06`)를 정의한다. In-repo architecture charter에서 v1.5.1 ~ v1.6.1 빌드 이력과 Stage 0 완료 사실을 기록한다. `PushCheck remains disabled` 재확인.
**상위 관계**: 외부 헌법 저장소(`ollanvin/web` CONSTITUTION.md)를 상위로 둔다. 본 리포 문서들의 운영 규칙을 관장한다.

### 2-5. SPEC v2.1 Core Rebuild (수익 설계 보강)

**파일**: `docs/03_engineering/integration/SPEC_2026-04-14_core_rebuild_v2.md`
**핵심 선언**: v1 기능 완성 위에 "왜 결제해야 하는지"를 내장하는 4건 보강(P1 전환 앵커, P5 위험 패턴, P6 PushCheck CTA, P2 i18n T1 우선)을 확정한다. v2.1에서 D-day 카운트 추가. 실측 4종 지표만 허용하고, 금액 표현을 금지한다.
**상위 관계**: PRD v1을 기준선으로 하여 수익 보강을 덧붙인 문서이다. v1의 어떤 내용도 삭제하지 않는다고 명시한다.

### 2-6. Conformance Report (코웍 작성)

**파일**: `docs/05_quality/reports/architecture_conformance_myphonecheck_base_architecture_v1.md`
**핵심 선언**: Base Architecture v1 대비 현재 코드 적합성을 판정한 보고서이다. 총평: "4유닛·오버레이·판단 파이프라인·태그·글로벌 검색 골격은 대체로 Base 방향과 맞으나, 초기 스캔 범위, 통화/문자 권한 정책, 구독 수치·온보딩 서사에서 명시적 간극이 있다." 갭 5건, 리스크 5건을 식별했다.
**상위 관계**: Base Architecture v1을 기준 문서로 사용하며, 코드 변경 없이 분석만 수행한 Read-Only 산출물이다.

---

## § 3. 기존 헌법 간 충돌·모호점

### 충돌 #1 (기확인): PushCheck disabled vs SPEC v2.1 §3.3 P6 PushCheck CTA

- **`global-single-core-system.md`**: "PushCheck remains disabled in this architecture state. It is isolated from active navigation and active background behavior until re-evaluated later."
- **`project-governance.md`** Fixed Project Principles: "PushCheck remains disabled."
- **SPEC v2.1 §3.3**: P6 PushCheck에 대해 "각 앱 카드 하단에 '이 앱 알림 차단' 버튼 상시 노출" — `PushStatsAppCard`, `PushStatsScreen`, `PushListenerService` 등 구체적 Composable과 DAO 스펙을 정의한다.

**코웍 판단**: 이것은 **시점 차이**이다. `global-single-core-system.md`와 `project-governance.md`는 2026-04-15 Branchpoint 시점 기준 선언이다 (`2026-04-15-global-core-branchpoint.md`에서 확인). SPEC v2.1은 그 이전 2026-04-14 작성이나, 비전·자비스·대표님 승인을 거친 수익 보강 스펙이다. 두 문서의 "PushCheck" 정의가 같은 범위를 가리키는지도 모호하다 — SPEC v2.1의 P6은 **알림 통계 + 차단 CTA**이고, Global Single Core의 PushCheck는 **active navigation과 active background behavior** disabled로 기술한다. 즉, SPEC의 P6이 "PushCheck 유닛 전체 부활"인지 "알림 통계 기능만 CTA 추가"인지 명확하지 않다.

**결론**: 대표님 결정 필요 (§ 7 Q1).

### 충돌 #2 (신규 식별): 가격 — PRD USD 1 vs Base ~1.5 USD

- **PRD §13**: "USD 1/month. Global uniform pricing. Single plan."
- **Base Architecture §11**: "가격대는 약 1.5달러 수준을 선호한다."
- **코드**: `BillingManager.kt`의 `SUBSCRIPTION_PRODUCT_ID = "myphonecheck_monthly"` — 가격은 Play Console에서 설정되므로 코드에 고정 USD 없음.
- **project-governance.md**: 가격에 대한 명시적 선언 없음.

**코웍 판단**: PRD가 먼저(2026-03-24), Base가 나중에 작성되었다. Base가 상위 문서이므로 ~1.5 USD가 최신 의도로 보이나, 두 문서 모두 현행 유효하므로 **단정 불가**.

### 충돌 #3 (신규 식별): RiskLevel 이중 정의

- **`core:common` (Stage 0 동결)**: `RiskLevel` — 5단계(`SAFE`, `SAFE_UNKNOWN`, `UNKNOWN`, `CAUTION`, `DANGER`) + float score
- **`core:model` (PRD 기반)**: `RiskLevel` — 4단계(`HIGH`, `MEDIUM`, `LOW`, `UNKNOWN`) + displayName
- `01_current_state.md` L67-80에서 이 차이를 명시적으로 식별하고 "직접 치환·묵시적 캐스팅 불가"로 판정한다.

**코웍 판단**: 이것은 Stage 0와 PRD-era 코드 간의 **타입 분리**이며, Stage 1에서 매퍼를 구현하거나 하나로 통합해야 한다. 현재 `core:common`은 FREEZE 상태(MINOR만 허용)이므로, 통합 방향은 `core:model`이 `core:common`에 맞추거나 매퍼를 두는 두 가지뿐이다.

### 충돌 #4 (신규 식별): SearchEvidence 이중 정의

- **`core:common`**: `SearchEvidence(source: Layer, summary: String, timestamp: Long)` — 3계층 소싱 요약, Layer enum(`L1_NKB`, `L2_SEARCH`, `L3_PUBLIC_DB`)
- **`core:model`**: `SearchEvidence` — `recent30dSearchIntensity`, `keywordClusters`, `repeatedEntities` 등 다수 필드, 풍부한 의사결정 입력

**코웍 판단**: RiskLevel과 동일한 구조적 문제. 두 타입의 역할이 근본적으로 다르므로 이름 통일보다는 역할 분리를 명확히 하는 것이 정석이다.

### 모호점 #1: 4층 vs 3층 레이어

- **Base Architecture §9**: Presentation / Decision·Domain / Data / Device Integration (4층)
- **Global Single Core System**: Global Core / Country Policy Layer / Presentation Layer (3층)
- Base §9 본문에서 "충돌하지 않는다"고 선언하나, 구체적 매핑(어떤 4층이 어떤 3층에 대응하는지)은 기술되어 있지 않다.

### 모호점 #2: 초기 스캔 범위

- **Base §8**: "통화 이력, 문자 이력, 마이크 권한 및 사용 기록, 카메라 권한 및 사용 기록"
- **코드 `InitialScanOrchestrator.kt`**: DeviceBaseline + Camera + Mic만 수행, 통화/문자 이력 벌크 스캔은 포함되지 않음 (conformance 보고서 §7.2에서 "불일치" 판정)
- `READ_CALL_LOG`/`READ_SMS` 권한이 Manifest에서 DENY 처리되어 있어, Base의 기대와 코드 현실이 괴리한다.

---

## § 4. 신규 축 2건의 헌법 반영 설계

### § 4-1. 검색 3대 축 (내부 / 외부 / 오픈소스)

#### 현재 헌법 상태

기존 헌법에서 **내부**와 **외부** 2축은 이미 정의되어 있다.

- **내부**: Base §6 "내부 통화·문자 이력", PRD §4 Stage 2 "Device Evidence Analysis", `core:common`의 `SearchEvidence.Layer.L1_NKB`
- **외부**: Base §6 "외부 검색 결과", PRD §4 Stage 3 "Search Platform Enrichment", SPEC v2.1 "Chrome Custom Tab 전용 1차", `SearchEvidence.Layer.L2_SEARCH`

**오픈소스(Authoritative Open Data) 축은 헌법군 어디에도 없다.** 단, `core:common`의 `SearchEvidence.Layer` enum에 `L3_PUBLIC_DB`가 이미 Stage 0 동결 시점에 포함되어 있다 (`01_current_state.md` L29 확인). 이는 설계 시점에 3축을 예견한 것으로 판단된다.

#### 반영 설계안

**안 A (권장): `DecisionEngineContract` 확장 — 기존 `search` 메서드 내 Layer 기반 분기**

`DecisionEngineContract.sourceEvidence(identifier)` 메서드가 `List<SearchEvidence>`를 반환하며, 각 `SearchEvidence`에 `Layer` enum이 있다. 오픈소스 축은 `L3_PUBLIC_DB` 레이어로 자연스럽게 편입된다. 새 계약을 만들 필요 없이 기존 `sourceEvidence` 구현체가 L3 소싱 로직을 추가하면 된다.

**안 B: 별도 `PublicDataSourceContract` 신설**

오픈소스 데이터 특성(배치 갱신, 오프라인 캐시, 국가별 소스 차이)이 실시간 검색과 근본적으로 다르므로 별도 계약으로 분리한다. 이 경우 `DecisionEngineContract.synthesize`가 두 계약의 결과를 합성하는 구조가 된다.

**코웍 권고**: 안 A. 이유 — `L3_PUBLIC_DB`가 이미 동결된 enum에 있으므로 계약 변경 없이 구현 가능하다. FREEZE 정책(MINOR만 허용)을 위반하지 않는다.

#### 오픈소스 데이터 소스 후보 매트릭스

| 국가/지역 | 1순위 후보 | 2순위 후보 | 적용 유닛 |
|-----------|-----------|-----------|----------|
| 한국 | KISA 스팸 신고 DB (공공데이터포털) | 경찰청 보이스피싱 DB | Call/Message |
| 미국 | FTC Complaint DB (data.gov) | FCC Robocall Mitigation DB | Call/Message |
| EU (범역) | ENISA Threat Intelligence | 각국 CERT 공개 피드 | Call/Message/Mic/Camera |
| 글로벌 공통 | NVD/CVE (NIST) | Google Play Transparency Report | Mic/Camera |
| 글로벌 공통 | PhishTank (오픈 피싱 DB) | URLhaus (abuse.ch) | Message (링크 검증) |

### § 4-2. Mic/Camera 침해 이벤트 정기 검색

#### 현재 헌법 상태

Base §6.3-6.4는 "권한 보유 앱 목록 + 최근 사용 기록 + 차단 기능"만 정의한다. 코드(`MicCheckScanner.kt`, `CameraCheckScanner.kt`)도 `PackageManager`/`AppOpsManager`/`UsageStatsManager` 기반 정적 스캔만 수행한다. **앱이 침해 사고에 연루되었는지 능동 검색하는 기능은 헌법군에도 코드에도 없다.**

#### 검색 주기 비교

| 옵션 | 장점 | 단점 | 배터리/네트워크 |
|------|------|------|----------------|
| 일 1회 (새벽 배치) | 최신성 높음, 위협 탐지 빠름 | 배터리 소비, 데이터 소비 | WorkManager `PeriodicWorkRequest` 24h, `Constraints(networkType=CONNECTED, batteryNotLow=true)` |
| 주 1회 | 배터리·데이터 절약 | 위협 탐지 최대 7일 지연 | 동일 + 7일 주기 |
| 화면 진입시 | 사용자 의도에 맞춤 | 비활성 사용자 보호 불가, 캐시 없으면 진입 지연 | `OneTimeWorkRequest` on screen entry, 캐시 TTL 필요 |

**코웍 권고**: 일 1회 + 화면 진입시 캐시 갱신 병행. 이유 — 보안 앱의 핵심 가치는 능동 보호이므로 주 1회는 너무 느리다. 화면 진입시만으로는 비활성 사용자를 보호할 수 없다. 일 1회 배치가 배터리 제약 조건 하에서 실측 가능한 최소 주기이다.

#### 검색 소스 후보

| 소스 | 유형 | 접근 방식 | 국가 |
|------|------|----------|------|
| NVD/CVE (NIST) | 공식 DB | REST API (무료, rate-limited) | 글로벌 |
| KISA 보안공지 | 정부 CERT | RSS/API | 한국 |
| Google Play Transparency Report | 플랫폼 | 웹 (비API, 배치 파싱 필요) | 글로벌 |
| 보안업체 블로그 RSS (Kaspersky, ESET, Avast) | 업계 | RSS 피드 | 글로벌 |
| MobileIron/Lookout 위협 인텔리전스 | 업계 | API (유료 가능) | 글로벌 |

#### UI 표시 UX 초안

1. **앱 카드 내 배지**: 침해 이력 발견 시 앱 카드 우측 상단에 경고 배지(빨간 점 + 건수). 탭하면 상세.
2. **전용 경고 카드**: 임계값(예: CVE severity HIGH 이상) 초과 시 Mic/Camera 유닛 화면 최상단에 전용 경고 카드 고정 표시.
3. **알림**: 임계값 초과 시 `NotificationCompat` 알림. 사용자가 알림 빈도 설정 가능(Settings).

#### WorkManager 잡 스펙 초안

```
Worker: PrivacyThreatScanWorker
주기: PeriodicWorkRequest(24, TimeUnit.HOURS)
제약: Constraints.Builder()
    .setRequiredNetworkType(NetworkType.CONNECTED)
    .setRequiresBatteryNotLow(true)
    .build()
입력: 설치된 앱 패키지 목록 (PackageManager)
처리: 각 패키지명으로 NVD/KISA 등 조회 → 매칭 CVE/보안공지 필터 → Room 저장
출력: 갱신된 침해 이력 DB + (임계 초과 시) 알림 발송
```

---

## § 5. Conformance 보고서 갭 5건 우선순위 재평가

### 갭 1: 초기 스캔 범위 불일치

- **기존**: Base §8 vs InitialScanOrchestrator (Camera+Mic만)
- **신규 축 영향**: 검색 3대 축 추가로 초기 스캔 시 L3_PUBLIC_DB 조회도 고려 대상이 됨. 범위가 더 넓어지므로 **우선순위 상승**.
- **재평가**: ★★★★★ (최우선) — 초기 스캔 범위를 Base + 신규 축 기준으로 재정의해야 Stage 1 범위가 확정된다.

### 갭 2: READ_CALL_LOG / READ_SMS 권한 정책 정렬

- **기존**: Manifest DENY vs DeviceEvidenceRepository의 CallLogDataSource/SmsMetadataDataSource 사용
- **신규 축 영향**: 검색 3대 축 중 내부 축이 통화/문자 이력에 의존하므로, 이 권한이 확보되지 않으면 내부 축의 증거 품질이 심각하게 저하된다. **우선순위 상승**.
- **재평가**: ★★★★★ (최우선) — Google Play 정책 승인 전략(Call Screening 앱 예외 또는 Default Dialer 전략)을 확정해야 한다.

### 갭 3: 온보딩 4위협 vs 4유닛 카피

- **기존**: strings.xml에 `threat_push_title` 잔존, Base의 4유닛(Call/Message/Mic/Camera)과 불일치
- **신규 축 영향**: Mic/Camera 유닛이 침해 이벤트 정기 검색으로 기능 확장되면, 온보딩 카피가 "4위협"이 아니라 "4방패" 또는 "4가드" 등으로 재정의될 수 있다. **우선순위 유지** (Stage 1 이후 UX 개편 시 처리 가능).
- **재평가**: ★★★☆☆ (중간) — 기능 확장 후 카피를 맞추는 것이 효율적.

### 갭 4: 구독 1.5 USD / 31일 vs 30일 상수

- **기존**: TrialCountdown.kt `DEFAULT_TRIAL_DAYS = 30` vs Base "31일째 과금"
- **신규 축 영향**: 직접 관련 없음. **우선순위 유지**.
- **재평가**: ★★☆☆☆ (낮음) — Play Console 설정 사항이며, 코드 상수 1일 차이는 billingPeriod 파싱으로 해결 가능.

### 갭 5: 유닛별 타임라인 vs 통합 Timeline

- **기존**: Base IA는 유닛별 전용 타임라인, 코드는 통합 Timeline
- **신규 축 영향**: Mic/Camera 침해 이벤트가 추가되면 유닛별 타임라인의 필요성이 높아짐 (Call/Message 이벤트와 Mic/Camera 침해 이벤트는 성격이 다름). **우선순위 소폭 상승**.
- **재평가**: ★★★☆☆ (중간) — Stage 1에서 구조만 결정하고 구현은 Stage 2 이후.

---

## § 6. Stage 1 범위 재정의 초안

비전 채팅이 앞서 권고한 "RiskLevel 매퍼 + push-intercept 삭제"는 신규 축 2건과 기존 갭 5건을 반영하면 **협소**하다. 아래 5개 후보를 제시한다.

### 후보 1: DecisionEngineContract 첫 소비 + RiskLevel/SearchEvidence 매퍼

- **내용**: `:feature:decision-engine`이 `:core:common`을 `implementation`으로 의존. `core.common.risk.RiskLevel` ↔ `core.model.RiskLevel` 매퍼 구현. `core.common.risk.SearchEvidence` ↔ `core.model.SearchEvidence` 매퍼 구현.
- **완료 기준**: `DecisionEngineImpl`이 `DecisionEngineContract`를 구현하고, 기존 테스트(`DecisionEngineImplTest`) + 매퍼 단위 테스트 통과.
- **근거**: `02_module_candidates.md`에서 `:feature:decision-engine`을 Stage 1 첫 번째 모듈로 권고.

### 후보 2: L3_PUBLIC_DB 소싱 인프라 (오픈소스 축)

- **내용**: `sourceEvidence` 구현체에 L3 소싱 경로 추가. NVD REST API 연동 모듈(`:data:search` 확장 또는 신규 `:data:public-db`). 국가별 소스 레지스트리(`CountrySearchConfig` 확장).
- **완료 기준**: L3 소싱 경로가 1개 이상의 실제 API와 연동되고, `SearchEvidence(source=L3_PUBLIC_DB, ...)` 반환 테스트 통과.
- **근거**: 대표님 신규 지시 "검색 3대 축".

### 후보 3: PrivacyThreatScanWorker (Mic/Camera 침해 정기 검색)

- **내용**: WorkManager 기반 24시간 주기 백그라운드 잡. NVD/KISA 등 외부 소스에서 설치 앱 패키지 매칭. Room 테이블 신설(`PrivacyThreatEntity`). Mic/Camera 유닛 화면에 경고 배지/카드 표시.
- **완료 기준**: Worker가 실제 API 1개 이상 조회하고 결과를 Room에 저장. 유닛 화면에 배지 표시. 배터리 제약 조건 준수.
- **근거**: 대표님 신규 지시 "Mic/Camera 권한 침해 이벤트 정기 검색".

### 후보 4: READ_CALL_LOG / READ_SMS 권한 전략 확정 + 초기 스캔 범위 통합

- **내용**: Google Play 정책 경로 확정(Call Screening 앱 예외 신청 또는 대안). 확정된 권한 기준으로 `InitialScanOrchestrator` 범위를 Base §8에 맞춤. DeviceEvidenceRepository의 입력 계약 고정.
- **완료 기준**: 권한 전략 문서화 + InitialScanOrchestrator에 Call/SMS 스캔 경로 추가(권한 있을 때) 또는 Base §8 개정(권한 없을 때).
- **근거**: 갭 1, 갭 2 (최우선).

### 후보 5: PushCheck 위상 확정

- **내용**: PushCheck를 disabled 유지할 것인지, SPEC v2.1 P6 범위(알림 통계 + CTA)만 활성화할 것인지 확정. 확정 결과를 `global-single-core-system.md`, `project-governance.md`에 반영.
- **완료 기준**: 헌법 문서 간 PushCheck 선언이 단일 진실로 통일.
- **근거**: 충돌 #1.

---

## § 7. 대표님 의사결정 필요 항목

### Q1: PushCheck — disabled 유지인가, SPEC v2.1 P6 범위(알림 통계 CTA)만 살릴 것인가?

**코웍 권고**: SPEC v2.1 P6 범위만 활성화. 이유 — P6의 알림 통계 + 차단 CTA는 사용자 가치가 명확하고(수익 전환 앵커), PushCheck "유닛"을 전면 부활시키는 것과는 다르다. `global-single-core-system.md`의 "disabled"는 "active navigation과 active background behavior" 기준이므로, 통계 표시 + 1클릭 차단은 이 정의에 반드시 저촉되지는 않는다. 단, 헌법 문서에 이 구분을 명시해야 한다.

### Q2: 오픈소스 축 — 우선 적용 유닛은?

**코웍 권고**: Call Check 유닛 우선. 이유 — 스팸/사기 전화 신고 DB(FTC, KISA)가 가장 성숙하고 API 접근이 용이하다. Mic/Camera 유닛의 NVD 연동은 패키지명 매칭 정밀도가 낮아 2순위가 적절하다.

### Q3: Mic/Camera 정기 검색 주기는?

**코웍 권고**: 일 1회 (WorkManager 24h + 화면 진입시 캐시 갱신). 이유 — § 4-2 참조. 보안 앱의 가치는 능동 보호이며, 주 1회는 위협 탐지 지연이 너무 크다.

### Q4: RiskLevel 통합 방향은?

**코웍 권고**: `core:model.RiskLevel`(4단계)을 `core:common.risk.RiskLevel`(5단계)로 매핑하는 단방향 매퍼를 `:feature:decision-engine`에 둔다. `core:common`은 FREEZE 상태이므로 수정하지 않고, `core:model`도 PRD 기반 UI 타입으로 유지한다. 매핑 규칙:

| core:common (5단계) | core:model (4단계) |
|----|-----|
| SAFE | LOW |
| SAFE_UNKNOWN | LOW |
| UNKNOWN | UNKNOWN |
| CAUTION | MEDIUM |
| DANGER | HIGH |

### Q5: Stage 1 범위 — 후보 5개 중 어디까지?

**코웍 권고**: 후보 1 + 후보 4를 필수로 하고, 후보 2와 3을 병렬 트랙으로 진행. 후보 5는 Q1 결정 후 즉시 문서 패치. 이유 — 후보 1은 `core:common` 계약의 첫 실소비이므로 Stage 0의 자연스러운 후속이다. 후보 4는 갭 1·2 해소 없이 나머지가 불안정하다.

### Q6: 가격 — USD 1 vs ~1.5 USD?

**코웍 권고**: 코웍이 단정할 사안이 아니다. PRD와 Base Architecture 중 어느 것이 최신 의도인지 대표님이 확정하고, 확정값을 하나의 헌법 문서에 고정해야 한다.

---

## § 8. 다음 단계 권고

1. **본 보고서 → 비전 검수** — 비전이 본 보고서의 사실 관계, 충돌 판단, 설계안을 검증한다.
2. **대표님 Q1~Q6 결정** — 비전이 대표님에게 의사결정 항목을 제시하고 확정받는다.
3. **헌법 패치 발행** — Q1~Q6 확정값을 `global-single-core-system.md`, `project-governance.md`, Base Architecture에 반영하는 패치 워크오더를 비전이 작성한다.
4. **Stage 1 본 워크오더 발행** — 확정된 범위로 Stage 1 코딩 워크오더를 발행한다.
5. **소요 일정 추정** — 헌법 패치(1일) → Stage 1 워크오더 작성(1일) → Stage 1 구현(후보 1+4 기준 3~5일, 후보 2+3 병렬 +3~5일).

---

*End of synthesis report.*
