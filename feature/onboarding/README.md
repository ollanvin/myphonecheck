# MyPhoneCheck — feature:onboarding

Five-step onboarding flow extracted from `MyPhoneCheckNavHost.kt` into this library module (SRP, §16 parity with sibling features).

## Contents

- `OnboardingScreen` + `OnboardingPage1`–`OnboardingPage5`
- `OnboardingPermissionRow` (shared with Settings permissions UI in app)
- `OnboardingViewModel` + `OnboardingPermissionState` (Page 5 permission snapshot)
- English default strings in `src/main/res/values/strings.xml` (§9-1)

## Documentation

| Document | Purpose |
|----------|---------|
| [QUICK_REFERENCE.md](QUICK_REFERENCE.md) | Integration checklist |
| [IMPLEMENTATION.md](IMPLEMENTATION.md) | Structure and DI |
| [DELIVERABLES.md](DELIVERABLES.md) | Files and verification |

## Dependency

App module: `implementation(project(":feature:onboarding"))`

