# MyPhoneCheck

A multi-module Android application for intelligent call verification and decision making.

**Tech Stack**: Kotlin, Jetpack Compose, Hilt DI

**Pricing**: `$2.49/month` single-tier global pricing (190 countries)

---

## Core Principles

MyPhoneCheck operates under 7 constitutional principles (see `docs/00_governance/architecture/v1.8.0/05_constitution.md` for full text):

1. **Out-Bound Zero**: No user data transmitted to operator-controlled or third-party servers
2. **In-Bound Zero**: External raw text never persisted; only feature counts stored in NKB
3. **Decision Centralization Prohibited**: Device is the primary decision maker, not central servers
4. **Self-Operation**: System operates without central servers (L3 baseline SLA)
5. **Honesty**: No false claims; transparent evidence presentation
6. **User Trust**: User is the final decision maker; app shows evidence only
7. **Device-Oriented Goose**: All processing on-device; aligned with Android platform standards

---

## Project Structure

### Core Modules
- `core:model` - Domain models and data classes
- `core:util` - Utility functions (phone number normalization, time utilities, Result wrapper)

### Feature Modules
- `feature:call-intercept` - Call interception and screening logic
- `feature:device-evidence` - Device-based evidence collection
- `feature:search-enrichment` - Search-based enrichment logic
- `feature:decision-engine` - Core decision making engine
- `feature:decision-ui` - UI for displaying decisions
- `feature:settings` - User settings screens
- `feature:billing` - In-app billing and subscription management
- `feature:country-config` - Country-specific configurations

### Data Modules
- `data:contacts` - Contact data access
- `data:calllog` - Call log data access
- `data:sms` - SMS data access
- `data:search` - Search results data access
- `data:local-cache` - Local database caching with Room

### Build Logic
- `build-logic:convention` - Gradle convention plugins for consistent configuration

---

## Documentation

All project documentation lives under `docs/`:

| Path | Purpose |
|---|---|
| `docs/00_governance/` | Governance & architecture SSOT |
| `docs/01_architecture/` | Architecture working notes |
| `docs/02_product/` | Product specs |
| `docs/03_engineering/` | Engineering guides |
| `docs/04_operations/` | Operations records |
| `docs/05_quality/` | Quality assurance |
| `docs/06_history/` | Project history |
| `docs/07_relay/` | Worker handoff materials |

### Governance SSOT (2-axis)

Project governance is split into two paired Working Canonicals:

- **Architecture**: `docs/00_governance/architecture/v1.8.0/` — Product design (constitution, Four Surfaces, NKB, Decision Engine)
- **Infrastructure**: `docs/00_governance/infrastructure/v1.1/` — Operations (toolmap, build pipelines, SOPs)

For detailed governance rules, see `docs/00_governance/project-governance.md`.

### Constitution Source

The cross-project constitution is governed in a separate repository:
- Repository: `https://github.com/ollanvin/web`
- Document: `CONSTITUTION.md`

---

## Build

Built with Gradle and JDK 17. Run:

```bash
./gradlew build
```

For Fastlane Mac builds, see `docs/00_governance/infrastructure/v1.1/MyPhoneCheck_Infra_Ops_v1.1.md`.

---

## Project Status

- Stage 0-hotfix: Complete (Java 17 migration)
- Stage 0 core/common: Complete (FROZEN)
- Stage 1: Preparation
- Architecture v1.8.0: Working Canonical
- Infrastructure v1.1: Working Canonical

---

## License

Proprietary. All rights reserved by Ollanvin.
