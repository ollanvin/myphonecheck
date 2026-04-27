# Governance

Project governance area. Contains constitution references, architecture/infrastructure SSOT, and historical archive.

## Contents

| Path | Purpose |
|---|---|
| `architecture/` | Architecture SSOT (product design) |
| `infrastructure/` | Infrastructure SSOT (operations) |
| `archive/` | Historical preservation |
| `project-governance.md` | Project-level governance rules |
| `README.md` | This file |

---

## SSOT 2-axis

This governance area follows the **2-axis Single Source of Truth** model:

### Architecture (Product Design)

- `architecture/v1.7.1/` — Frozen reference (canonical)
- `architecture/v1.8.0/` — Frozen (4 Surface 시점)
- `architecture/v1.9.0/` — Frozen (Six Surfaces 정식)
- `architecture/v2.0.0/` — Frozen (One Core Engine + SIM-Oriented + Initial Scan, MAJOR)
- `architecture/v2.1.0/` — Current Working Canonical (4-Layer + Real-time + Tag + Competitor Feeds, MINOR)
- Defines: constitution 8 principles (v2.0.0 §8조 SIM-Oriented 신설, v2.1.0 무변경), Six Surfaces (CallCheck/MessageCheck/MicCheck/CameraCheck/PushCheck/CardCheck), Initial Scan, `:core:global-engine` (모든 Surface 단일 코어), NKB schema, Day-by-Day implementation guide
- **One Core Engine**: All Surfaces share single `:core:global-engine`. Self parser/mapping prohibited.
- **SIM-Oriented Single Core (헌법 8조)**: 국가·통화·전화번호 양식 단일 진실원 = SIM. UI 언어 3단 fallback (SIM → 시스템 → English).
- **4-Layer Data Model (§30-3-A, v2.1.0)**: OS / MyPhoneCheck / 외부 캐시 / 외부 검색 분리. Layer 우선순위 = 2·1·3·4.
- **Real-time Action Engine (§31, v2.1.0)**: CallScreeningService + SMS abortBroadcast + Push cancelNotification, 50ms 응답.
- **Tag System (§32, v2.1.0)**: 휘발성 메모, REMIND_ME/PENDING/SUSPICIOUS/ARCHIVE priority. 연락처 저장과 별개.
- **Search 4축 (§30-4)**: Internal · External(Custom Tab) · Public Government·Telco · Public Competitor (더콜·후후·Whoscall).

### Infrastructure (Operations)

- `infrastructure/v1.0/` — Paired with Architecture v1.7.1
- `infrastructure/v1.1/` — Current Working Canonical
- Defines: toolmap (Cursor, Codex CLI, Claude Code, Cowork, Vision), build pipelines, SOPs, secrets management

### Pair Relationship

- Architecture and Infrastructure are **paired documents**
- Each has its own Working Canonical (independent versioning)
- 현재 페어: **Architecture v2.1.0 ↔ Infrastructure v1.1** (cross-ref 갱신, Infrastructure v1.2 승격은 후속 WO에서 검토)
- Conflict resolution: **Architecture takes precedence** (per Infrastructure v1.1 §0.2 Rule 4)
- Cross-references: Infrastructure §0.2 -> Architecture v2.1.0 (4-Layer + Real-time + Tag + Competitor, 11 paths = 6 Surfaces + Initial Scan + SIM-Oriented Core + `:core:global-engine` + Real-time Action + Tag System); Architecture §35-6 -> Infrastructure v1.1

---

## Archive Policy

The `archive/` directory preserves obsolete or replaced materials:

| Subfolder | Purpose |
|---|---|
| `workorders/` | Completed one-off work orders |
| `patches/` | Legacy patch bundles (integrated into Architecture Appendix B) |
| `legacy_docx/` | Deprecated docx builds |
| `legacy_docs/` | Legacy integrated documents |
| `temp/` | Temporary work folders (tmp_files, eval, scope-test) |

**Policy**:
- Direct reference prohibited (canonical sources are in `architecture/` and `infrastructure/`)
- No deletion allowed (preservation of decision records)
- Only addition allowed (new archives over time)
- See `archive/README.md` for details

---

## 4-Worker Structure

This project uses a 4-worker development model defined in Infrastructure v1.1 §0.4:

| Worker | Role | Tool |
|---|---|---|
| Cursor | Primary implementer (general coding) | Cursor IDE |
| Claude Code | Auditor, long-running automation, Fastlane | Claude Code CLI |
| Cowork | Communication hub, GitHub ops, remote SSH | Claude Cowork |
| Codex CLI | Secondary CLI, cross-checks, backup | ChatGPT Codex CLI |
| **Vision** | Design, judgment, work order issuance | Claude.ai |

The 4-worker structure operates under "2 execution + 2 audit" pattern with Vision as integrator.

Worker handoff materials live in `docs/07_relay/`.

---

## Governance History

For project governance evolution and refactor records, see:
- `project-governance.md` (current rules)
- `archive/legacy_docs/project-governance-original.md` (pre-2026-04-27 original)

Last refactor: 2026-04-28 (WO-V180-CLEANUP-009-E + WO-V190-GOVERNANCE-PATCH-003 v1.9.0 sync + WO-V200-GOVERNANCE-PATCH v2.0.0 sync + WO-V210-GOVERNANCE-PATCH v2.1.0 sync)
