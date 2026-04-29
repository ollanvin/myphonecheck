# 2026-04-15 Global Core Branchpoint

## Why This Is a Branchpoint

This date marks the point where MyPhoneCheck is explicitly fixed as a global single-core system rather than a feature-led local app structure.

The architecture direction is now locked to:

- one shared global core
- country variance through policy only
- presentation variance through display only

## Before

Before this branchpoint, the project still carried traces of feature-led separation:

- call flow as the main core
- message flow as a partially separate concern
- push flow existing as a feature track even after de-prioritization
- project documentation spread across mixed locations and formats

## After

After this branchpoint:

- `CallCheck Core Engine` is the single judgment core
- `MessageCheck` is explicitly positioned as a shared-core extension layer
- `PushCheck` remains disabled
- unsaved-number relationship memory is part of the core model
- search status and user labels/tags are explicitly separated
- project documents are organized under the standard `docs/` structure

## Key Decisions Recorded Today

- Fixed the three-layer structure: `Global Core`, `Country Policy Layer`, `Presentation Layer`
- Recorded project governance under `docs/00_governance/`
- Recorded architecture under `docs/01_architecture/`
- Recorded number-profile relationship design under `docs/03_engineering/`
- Recorded operational instructions and quality checklist
- Kept constitution material outside the project repo and referenced it instead

## Build And SCM Status

- Build status: completed on 2026-04-15
- Commit status: completed on 2026-04-15
- Push status: completed on 2026-04-15

This file records the branchpoint itself, not a claim of final product completion.
