# Project Governance

## Scope

MyPhoneCheck is a project governed by the shared constitution repository and by this project-level applied governance.

This repository stores:

- applied project rules
- project architecture records
- implementation and validation documents
- project history

It does not store the cross-project constitution itself.

## Constitution Source

- Constitution repository local path: `C:\Users\user\Dev\ollanvin\web`
- Constitution repository remote: `https://github.com/ollanvin/web`
- Constitution document baseline: `CONSTITUTION.md` (OllanVin 조직 인프라 헌법, 8조 체계)
- Constitution version baseline: **10-article (Architecture v2.2.0 ~ v2.3.0)** — v2.2.0 §9 빅테크 정공법(7절) + §10 비전 자율 결정(5절) MAJOR 신설, **v2.3.0 §10-6 자체 머지 의무 신설(§10 5절 → 6절) MINOR**
- Architecture canonical: **v2.3.0** (Working Canonical)

MyPhoneCheck product 헌법 (Out-Bound Zero ~ §10-6 자체 머지 의무 10조)은 Architecture v2.3.0 `05_constitution.md`에 위치한다 (web 레포 OllanVin 인프라 헌법과는 별도). 두 헌법은 다른 영역을 다루며, MyPhoneCheck는 둘 다 정합한다.

## Fixed Project Principles

- MyPhoneCheck is a 190-country shared single-core system.
- Country-specific feature branching is prohibited in the global core.
- The fixed layers are `Global Core`, `Country Policy Layer`, and `Presentation Layer`.
- `CallCheck Core Engine` is the only judgment core.
- `MessageCheck` is an extension path into the same core, not an independent engine.
- `PushCheck` operates as a push trash (notification quarantine) Surface — promoted to formal Surface in Architecture v1.9.0.
- `CardCheck` (v1.9.0 신설) provides month-by-month card spend management as a Surface that reuses SMS/Push data only (no new permissions, no new outbound traffic).
- `Initial Scan` (v2.0.0 신설): 최초 론칭 후 디바이스 스캔으로 6 Surface 베이스데이터·베이스양식을 일괄 구축한다 (§28).
- `:core:global-engine` (v2.0.0 신설): 모든 Surface가 사용하는 단일 코어 엔진. **Surface별 자체 파서·매핑 코드 금지** (§30).
- **SIM-Oriented Single Core (헌법 8조, v2.0.0)**: 국가·통화·전화번호 양식 단일 진실원 = SIM (MCC/MNC). UI 언어만 사용자 선택 가능 (3단 fallback: SIM → 디바이스 시스템 → English).
- **4-Layer Data Model (§30-3-A, v2.1.0)**: OS / MyPhoneCheck / 외부 캐시 / 외부 검색 분리. Layer 우선순위 = 2·1·3·4. FeedType 4유형 (SecurityIntelligence / GovernmentPublic / CompetitorApp / TelcoBlocklist).
- **Real-time Action (§31, v2.1.0)**: 수신 거절 즉시 종료, SMS abortBroadcast, 밀리초 단위 조치 (50ms 응답). `CallScreeningService` 등록 권장.
- **Tag System (§32, v2.1.0)**: 휘발성 메모, 연락처 저장과 별개. REMIND_ME / PENDING / SUSPICIOUS / ARCHIVE.
- **Competitor Feeds (§30-4-4, v2.1.0)**: 경쟁 앱 공개 데이터 활용 (라이선스·robots.txt 정합 필수). 사용자 옵트인 강조 ("타사 데이터 활용").
- **빅테크 정공법 (헌법 §9, v2.2.0)**: 시장 분리·번역 작업·외부 계약·iOS 분리·검증 매트릭스 부재 6종 위반 영구 차단. §9-6 검증 매트릭스 (SIM 11개국 × Locale 11개 × 디바이스 4종, 핵심 40~60 케이스, PR 회귀 게이트).
- **비전 자율 결정 (헌법 §10, v2.2.0)**: 사지선다·확인 받기 금지. 비전이 메모리·헌법 정합 후 단독 결정. 외부 검증자 권고도 비전 발행 거쳐야 실행.
- **자체 머지 의무 (헌법 §10-6, v2.3.0 신설)**: 비전 워크오더 = 워커가 한 사이클(분기 → 작성 → push+PR → CI PASS → squash merge + delete branch → 완료 보고) 자체 수행. 명령: `gh pr merge <PR> --squash --delete-branch --auto`. 대표님 머지 개입 = §10-2 비전 책임 영역 위반. 예외: 외부 의사결정·NOT-OK·명시적 대표님 결정 요청.
- The app does not make the final user judgment. It shows evidence.
- The user remains the final decision-maker.
- Contact saving and relationship management remain separate.
- Unsaved numbers must still support local relationship profiles.
- Only measured on-device data may be shown.
- Search-result status must use direct fixed wording only.

## Documentation Standard

Project documents must live under `docs/` and be classified by purpose.

### Standard Folders

| Path | Purpose |
|---|---|
| `docs/00_governance/` | Constitution refs, architecture/infrastructure SSOT, archive |
| `docs/01_architecture/` | Architecture working notes |
| `docs/02_product/` | Product specs |
| `docs/03_engineering/` | Engineering guides |
| `docs/04_operations/` | Operations records |
| `docs/05_quality/` | Quality assurance |
| `docs/06_history/` | Project history (incl. `discovery/` subfolder) |
| `docs/07_relay/` | Worker handoff materials (4-worker structure) |

### Governance Subfolder Structure

`docs/00_governance/` contains:

| Path | Purpose |
|---|---|
| `architecture/v1.7.1/` | Architecture canonical (frozen reference) |
| `architecture/v1.8.0/` | Architecture frozen (4 Surface 시점) |
| `architecture/v1.9.0/` | Architecture frozen (Six Surfaces 정식) |
| `architecture/v2.0.0/` | Architecture frozen (One Core Engine + SIM-Oriented + Initial Scan, MAJOR) |
| `architecture/v2.1.0/` | Architecture frozen (4-Layer + Real-time + Tag + Competitor Feeds, MINOR 시점) |
| `architecture/v2.2.0/` | Architecture frozen (헌법 §9 빅테크 정공법 + §10 비전 자율 결정, 8조 → 10조 MAJOR) |
| `architecture/v2.3.0/` | Architecture current Working Canonical (헌법 §10-6 자체 머지 의무 신설, MINOR) |
| `infrastructure/v1.0/` | Infrastructure original (paired with Architecture v1.7.1, frozen) |
| `infrastructure/v1.1/` | Infrastructure frozen (Architecture v1.8.0~v2.1.0 cross-ref 패치 시점) |
| `infrastructure/v1.2/` | Infrastructure frozen (Architecture v2.1.0 정합 MAJOR 시점) |
| `infrastructure/v1.3/` | Infrastructure current Working Canonical (Architecture v2.3.0 페어 정합 MAJOR — 헌법 §10-6 + Toolmap §4.0.9 + SOP-V13-001) |
| `archive/` | Historical preservation (workorders, patches, legacy_docx, legacy_docs, temp) |
| `project-governance.md` | This file |
| `README.md` | Governance area guide |

### Rules

- Do not leave implementation notes scattered in the repository root or feature folders.
- Do not mix constitution documents with project implementation documents.
- Keep work instructions, architecture, validation, and history as separate records.
- Keep temporary local notes and duplicate floating artifacts out of the project tree.
- Use `archive/` for historical preservation, never delete.
- Maintain SSOT alignment between Architecture and Infrastructure documents.

## Refactor Note

This document was refactored on 2026-04-27 (WO-V180-CLEANUP-009-E):

- Removed v1.5.x build script history (Korean encoding-corrupted lines)
- Added `docs/07_relay/` to standard folders (4-worker structure formal recognition)
- Added Governance Subfolder Structure table (SSOT 2-axis: architecture + infrastructure)
- Added archive policy reference

Original backed up at: `archive/legacy_docs/project-governance-original.md`

### Updates 2026-04-27 (WO-V190-GOVERNANCE-PATCH-003)

PR #11 (Architecture v1.9.0 MAJOR 머지, squash `0a62b91`) 후속 거버넌스 동기화:

- `architecture/v1.9.0/` 행 추가 (Six Surfaces 정식 Working Canonical)
- `architecture/v1.8.0/` 행은 frozen으로 강등 (4 Surface 시점 이전 Canonical)
- Fixed Project Principles에서 "PushCheck remains disabled" 제거 — v1.9.0에서 push trash 정식 Surface로 승격된 사실 반영
- Fixed Project Principles에 CardCheck 신설 명시 (월별 카드 사용액 관리, SMS/Push 재활용)
- Infrastructure v1.1 cross-ref는 v1.9.0 기준 (현재형)으로 갱신, v1.2 승격은 별도 후속 WO
- 거버넌스 본문은 영문 정책 + 한글 주석 혼합 유지 (텍스트 변경 최소화)

### Updates 2026-04-27 (WO-V200-GOVERNANCE-PATCH)

PR #15 (Architecture v2.0.0 MAJOR 머지, squash `2dd2bc6`) 후속 거버넌스 동기화:

- `architecture/v2.0.0/` 행 추가 (One Core Engine + SIM-Oriented + Initial Scan, MAJOR 승격)
- `architecture/v1.9.0/` 행은 frozen으로 강등 (Six Surfaces 시점 이전 Canonical)
- Fixed Project Principles에 v2.0.0 신설 사항 추가:
  · `Initial Scan` (§28): 최초 론칭 후 디바이스 스캔, 6 Surface 베이스데이터 일괄 구축
  · `:core:global-engine` (§30): 모든 Surface 단일 코어 엔진, Surface별 자체 파서·매핑 금지
  · **SIM-Oriented Single Core (헌법 8조)**: 국가·통화·전화번호 양식 단일 진실원 = SIM
  · UI 언어 3단 fallback (SIM → 시스템 → English)
- Constitution baseline: 7조 → **8조** (PATCH-41 SIM-Oriented Single Core 신설)
- Architecture canonical: **v2.0.0**
- Infrastructure v1.1 cross-ref: v2.0.0 기준 (9 paths = 6 Surfaces + Initial Scan + SIM Core + Global Engine)
- v1.2 승격은 별도 후속 WO (toolmap·SOPs 변화 검토 필요)

### Updates 2026-04-28 (WO-V210-GOVERNANCE-PATCH)

PR #25 (Architecture v2.1.0 MINOR 머지, squash `80c10b7`) 후속 거버넌스 동기화:

- `architecture/v2.1.0/` 행 추가 (4-Layer + Real-time + Tag + Competitor Feeds, MINOR 승격)
- `architecture/v2.0.0/` 행은 frozen으로 강등 (One Core Engine + SIM-Oriented 시점 이전 Canonical)
- Fixed Project Principles에 v2.1.0 신설 사항 추가:
  · **4-Layer Data Model** (§30-3-A): OS / MyPhoneCheck / 외부 캐시 / 외부 검색 분리, Layer 우선순위 2·1·3·4
  · **Real-time Action** (§31): CallScreeningService + SMS abortBroadcast + Push cancel, 50ms 응답
  · **Tag System** (§32): 휘발성 메모, REMIND_ME / PENDING / SUSPICIOUS / ARCHIVE priority
  · **Competitor Feeds** (§30-4-4): 더콜·후후·뭐야이번호·Whoscall 등 경쟁 앱 공개 데이터, 옵트인 다운로드, 라이선스·robots.txt 정합 필수
- Constitution baseline: 8조 (v2.0.0 ~ v2.1.0, 변경 없음 → MINOR)
- Architecture canonical: **v2.1.0**
- Infrastructure v1.1 cross-ref: v2.1.0 기준 (9 paths → **11 paths** = 기존 9 + Real-time Action + Tag System)
- v1.2 승격은 별도 후속 WO (Real-time Action·Tag·Competitor Feeds 통합 검토 필요)

### Updates 2026-04-28 (WO-INFRA-V12-MAJOR — Infrastructure v1.2 정식 승격)

PR #31 (Stage 2-010 통합 정리 머지, squash `81c83cd`) 후속, Architecture v2.1.0 ↔ Infrastructure v1.2 정식 페어 확정:

- `infrastructure/v1.2/` 행 추가 (Architecture v2.1.0 정합 MAJOR 승격, toolmap·SOPs 정식)
- `infrastructure/v1.1/` 행은 frozen으로 강등 (Architecture v1.8.0~v2.1.0 cross-ref 패치 누적 시점 이전 Canonical)
- v1.2 신설 항목:
  · **Toolmap §4.0 정식**: 메모리 #6 권한 범위 기반 정책 — Claude Code 메인 구현 + 감사 + 시스템 스캔 (PowerShell 네이티브 전면 권한)
  · **§4.0.7 Codex CLI 환경 이슈 메모**: v2.0.0~v2.1.0 시리즈 Claude Code 단독 진행 사실 명문화
  · **§4.0.5 Layer별 차등 정식**: Layer 1~4별 주력 워커 + 크로스 체크 명시
  · **§5-bis SOPs 신설**:
    - SOP-V12-001 Real-time Action 운영 (CallScreening + SMS abort + Push cancel, 50ms timeout)
    - SOP-V12-002 Tag System 운영 (4 priority + 일일 리마인드)
    - SOP-V12-003 Public Feed 운영 (4유형 출처 + 옵트인 + 라이선스 검토 강제)
  · **§7 STEP 0-bis 12항목 정식**: §31 Real-time + §32 Tag 추가 (PR #26 패치 정리)
  · **부록 A v1.2 변경 이력 표**: v1.1 → v1.2 항목별 변경 매트릭스
- Constitution baseline: 8조 (v2.0.0 ~ v2.1.0, 변경 없음)
- Architecture canonical: **v2.1.0** (변경 없음, 페어만 정식 승격)
- Infrastructure canonical: **v1.2** (frozen 전환된 v1.1 → v1.2 MAJOR)
- v1.0 / v1.1 frozen 보존 (FROZEN, 이력 보존)
- 다음 페어 변경: Architecture v2.2.0 또는 v3.0.0 시 재검토

### Updates 2026-04-28 (WO-V210-AUDIT-CORRECTIONS — 3 워커 통합 감사 후속 정정)

PR #32 (Infrastructure v1.2 머지, squash `6c7c149`) 후속 3 워커 (Claude Code + Cursor + Cowork) 통합 감사 발견 사항 정정:

- **Cowork Major-1 (Subfolder Structure 테이블 v2.0.0·v2.1.0 행 누락)**: 검증 결과 **NO-OP**.
  · PR #32 (Infrastructure v1.2 정식 승격) 시 project-governance.md sync 작업에서 이미 v2.0.0/v2.1.0 행 추가 + Infrastructure v1.0~v1.2 행 추가 완료.
  · Cowork 감사가 PR #32 머지 이전 시점을 본 것으로 추정 — 현재 main 시점에서는 정합 상태.
- **Cowork Minor-2 (v2.1.0 INDEX.md 트리맵 루트 v2.0.0/ 오표기)**: 정정 완료.
  · `docs/00_governance/architecture/v2.1.0/INDEX.md` line 17 `v2.0.0/` → `v2.1.0/`.
  · 트리맵 루트만 정정. 본문 cross-ref·역사 표기 무손상.
- **Cursor Minor 3건**: 본 PR 영역 외 처리.
  · Cursor 감사 보고서 미접수 (docs/07_relay/done/에 v2.1.0 감사 보고서 없음).
  · WO STEP 6 명시 절차 따라 별도 후속 WO에서 처리 예정.
- **Cowork Suggestion-1 (Infrastructure v1.2 승격)**: 별도 처리 완료 (PR #32).
- **Cowork Minor-1 (14 PR 라벨 미부착)**: GitHub UI 작업, 본 PR 영역 외.

영역 한정:
- `docs/00_governance/project-governance.md` (본 Refactor Note 추가)
- `docs/00_governance/architecture/v2.1.0/INDEX.md` (트리맵 루트 정정)

보호 영역 무손상:
- v1.7.1 / v1.8.0 / v1.9.0 / v2.0.0 / Infrastructure v1.0~v1.2.

### Updates 2026-04-28 (WO-V220-MAJOR-001 — Architecture v2.2.0 헌법 10조 MAJOR 승격)

PR #34 / PR #35 (정정본 §9-6 검증 매트릭스 추가) 머지 후속 거버넌스 동기화:

- `architecture/v2.2.0/` 행 추가 (헌법 §9 빅테크 정공법 + §10 비전 자율 결정, 8조 → 10조 MAJOR)
- `architecture/v2.1.0/` 행은 frozen으로 강등
- Constitution baseline: 8조 → **10조** (§9 7절 + §10 5절 신설; §9-6 검증 매트릭스 정정본 신설)
- Architecture canonical: **v2.2.0** (이후 v2.3.0 MINOR 승격 후 frozen 전환)
- Fixed Project Principles에 §9 빅테크 정공법 + §10 비전 자율 결정 항목 추가
- 비전 누적 위반 6건 영구 차단 (시장 분리·번역·계약·iOS 분리·사지선다·검증 매트릭스 부재)

### Updates 2026-04-28 (WO-V220-GOVERNANCE-INFRA-V13-INTEGRATED — v2.3.0 + v1.3 페어 승격)

PR #35 (Architecture v2.2.0 정정본 머지, main 6db52a7) 후속 페어 정식 승격:

- `architecture/v2.3.0/` 행 추가 (헌법 §10-6 자체 머지 의무 신설, §10 5절 → 6절 MINOR)
- `architecture/v2.2.0/` 행은 frozen으로 강등
- `infrastructure/v1.3/` 행 추가 (Architecture v2.3.0 페어 정합 MAJOR — 헌법 §10-6 + Toolmap §4.0.9 자체 머지 정공법 + SOP-V13-001)
- `infrastructure/v1.2/` 행은 frozen으로 강등
- Constitution baseline: 10조 + **§10-6 자체 머지 의무** (§10 6번째 절 신설)
- Architecture canonical: **v2.3.0**
- Infrastructure canonical: **v1.3**
- Fixed Project Principles에 §10-6 자체 머지 의무 항목 추가
- v1.3 신설 항목:
  · **§4.0.9 자체 머지 정공법**: 한 사이클 6단계 + `gh pr merge <PR> --squash --delete-branch --auto`
  · **SOP-V13-001 워크오더 발행 + 자체 머지**: 비전 발행권 + 워커 자체 머지 + 영역 한정 + CI 분류 + §11 양식
  · **§7 STEP 0-bis 13항목**: §10-6 자체 머지 게이트 추가 (12 → 13)
  · **§0.2 Cross-references 12 paths**: §9-6 검증 매트릭스 명시 추가
  · **부록 A v1.3 변경 이력 표**: v1.2 → v1.3 항목별 변경 매트릭스
- 비전 누적 잘못 7번째(자체 머지 단계 누락) 영구 차단
- 페어 검증: Architecture v2.3.0 ↔ Infrastructure v1.3 정식 페어 확정
- 자체 적용 (정합 검증): 본 PR은 §10-6 정합으로 워커가 직접 `gh pr merge --squash --delete-branch --auto` 수행

영역 한정:
- `docs/00_governance/architecture/v2.3.0/` (cp 후 §10-6 + 부록 B/D/E + INDEX 갱신)
- `docs/00_governance/infrastructure/v1.3/MyPhoneCheck_Infra_Ops_v1.3.md` (cp 후 본문 정정)
- `docs/00_governance/README.md` (페어 v2.3.0 ↔ v1.3 갱신)
- `docs/00_governance/project-governance.md` (본 Refactor Note 추가)

보호 영역 무손상:
- v1.7.1 / v1.8.0 / v1.9.0 / v2.0.0 / v2.1.0 / v2.2.0 / Infrastructure v1.0~v1.2.
