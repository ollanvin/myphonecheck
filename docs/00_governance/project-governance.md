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
- Constitution document baseline: `CONSTITUTION.md`

MyPhoneCheck must align to the constitution repository, but project-specific implementation documents remain inside this repository only.

## Fixed Project Principles

- MyPhoneCheck is a 190-country shared single-core system.
- Country-specific feature branching is prohibited in the global core.
- The fixed layers are `Global Core`, `Country Policy Layer`, and `Presentation Layer`.
- `CallCheck Core Engine` is the only judgment core.
- `MessageCheck` is an extension path into the same core, not an independent engine.
- `PushCheck` remains disabled.
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
| `architecture/v1.8.0/` | Architecture current Working Canonical |
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
