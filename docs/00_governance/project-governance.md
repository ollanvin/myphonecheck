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
- Constitution version baseline: **8-article (Architecture v2.0.0)** — 8조 SIM-Oriented Single Core 신설 (PATCH-41)
- Architecture canonical: **v2.0.0** (Working Canonical)

MyPhoneCheck product 헌법 (Out-Bound Zero ~ SIM-Oriented Single Core 8조)은 Architecture v2.0.0 `05_constitution.md`에 위치한다 (web 레포 OllanVin 인프라 헌법과는 별도). 두 헌법은 다른 영역을 다루며, MyPhoneCheck는 둘 다 정합한다.

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
| `architecture/v2.0.0/` | Architecture current Working Canonical (One Core Engine + SIM-Oriented + Initial Scan, MAJOR 승격) |
| `infrastructure/v1.0/` | Infrastructure original (paired) |
| `infrastructure/v1.1/` | Infrastructure current Working Canonical |
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
