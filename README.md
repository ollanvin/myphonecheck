# MyPhoneCheck

A multi-module Android application for intelligent call verification and decision making.

**Tech Stack**: Kotlin, Jetpack Compose, Hilt DI

**Pricing**: `$2.49/month` single-tier global pricing (190 countries)

---

## Core Principles

MyPhoneCheck operates under 7 constitutional principles (see `docs/00_governance/architecture/v2.7.0/05_constitution.md` for full text):

1. **Out-Bound Zero**: No user data transmitted to operator-controlled or third-party servers
2. **In-Bound Zero**: External raw text never persisted; only feature counts stored in NKB
3. **Decision Centralization Prohibited**: Device is the primary decision maker, not central servers
4. **Self-Operation**: System operates without central servers (L3 baseline SLA)
5. **Honesty**: No false claims; transparent evidence presentation; user is the final decision maker — app shows evidence only
6. **Pricing Honesty**: Single $2.49/month global pricing, transparent value; net ARPU measured/disclosed (gross × (1 − 0.30 store) × (1 − 0.10 VAT) × (1 − 0.05 refund) ≈ $1.49)
7. **Device-Oriented Goose**: All processing on-device; aligned with Android platform standards

---

## Project Structure

### Core Modules
- `core:common` - Shared output contracts (RiskKnowledge), damage taxonomy, identifier sealed hierarchy
- `core:model` - Domain models and data classes
- `core:util` - Utility functions (phone number normalization, time utilities, Result wrapper)
- `core:security` - Security primitives
- `core:global-engine` - SIM-Oriented single core (Decision Engine, NKB, currency parser, search aggregator)

### Feature Modules — Six Surfaces (Call/Message/Mic/Camera/Push/Card)
- `feature:call-check` - CallCheck Surface (사용자 가치 추출)
- `feature:message-check` - MessageCheck Surface
- `feature:privacy-check` - MicCheck + CameraCheck (통합 호칭 PrivacyCheck)
- `feature:push-trash` - PushCheck Surface (푸시 휴지통)
- `feature:card-check` - CardCheck Surface (월별 카드 사용액)

### Feature Modules — System Integration / Ingestion
- `feature:call-intercept` - Call interception logic
- `feature:call-screening` - CallScreeningService 시스템 통합
- `feature:message-intercept` - SMS/Push 인입 라우터 (IngestRouter → MessageHub + CardTransaction)
- `feature:sms-block` - SMS 차단 통합

### Feature Modules — Engine / UI / Decision
- `feature:decision-engine` - Decision making (코어 위임)
- `feature:decision-ui` - 4-attribute card / SurfaceContext 6 enum 단일 SOT
- `feature:initial-scan` - Initial Scan (최초 론칭 6 Surface 베이스데이터)
- `feature:tag-system` - Tag System (휘발성 메모)
- `feature:device-evidence` - Device-based evidence collection

### Feature Modules — Operations
- `feature:onboarding` - Onboarding flow
- `feature:settings` - User settings screens
- `feature:billing` - In-app billing and subscription management
- `feature:country-config` - Country-specific configurations

### Data Modules
- `data:contacts` - Contact data access
- `data:calllog` - Call log data access
- `data:sms` - SMS data access
- `data:local-cache` - Room database (24 entities, version 19)

### Build Logic
- `build-logic` - Gradle convention plugins (composite build via `includeBuild`)

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

- **Architecture**: `docs/00_governance/architecture/v2.7.0/` — Product design (constitution, Six Surfaces, NKB, Decision Engine)
- **Infrastructure**: `docs/00_governance/infrastructure/v1.3/` — Operations (toolmap, build pipelines, SOPs)

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

For Fastlane Mac builds, see `docs/00_governance/infrastructure/v1.3/MyPhoneCheck_Infra_Ops_v1.3.md`.

---

## Project Status

- Stage 0-hotfix: Complete (Java 17 migration)
- Stage 0 core/common: Complete (FROZEN)
- Stage 1: Preparation
- Architecture v2.7.0: Working Canonical
- Infrastructure v1.3: Working Canonical

---

## License

Proprietary. All rights reserved by Ollanvin.
