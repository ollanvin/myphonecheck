# Onboarding — Quick reference

## Gradle

- `settings.gradle.kts`: `include(":feature:onboarding")`
- `app/build.gradle.kts`: `implementation(project(":feature:onboarding"))`

## Navigation

- Route `"onboarding"` uses `OnboardingScreen(languageProvider, onContinue)`.

## Strings

- Onboarding copy lives in `:feature:onboarding` `values/strings.xml` (EN default).

## Shared UI

- Import `OnboardingPermissionRow` for permission rows; pass `actionAllowLabel` / `statusGrantedLabel` from app `strings.xml` when used outside onboarding.

