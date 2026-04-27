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
- `architecture/v1.8.0/` — Current Working Canonical
- Defines: constitution 7 principles, Four Surfaces, NKB schema, Decision Engine, Day-by-Day implementation guide

### Infrastructure (Operations)

- `infrastructure/v1.0/` — Paired with Architecture v1.7.1
- `infrastructure/v1.1/` — Current Working Canonical
- Defines: toolmap (Cursor, Codex CLI, Claude Code, Cowork, Vision), build pipelines, SOPs, secrets management

### Pair Relationship

- Architecture and Infrastructure are **paired documents**
- Each has its own Working Canonical (independent versioning)
- Conflict resolution: **Architecture takes precedence** (per Infrastructure v1.1 §0.2 Rule 4)
- Cross-references: Infrastructure §0.2 -> Architecture v1.8.0 5 paths; Architecture §35-6 -> Infrastructure v1.1

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

Last refactor: 2026-04-27 (WO-V180-CLEANUP-009-E)
