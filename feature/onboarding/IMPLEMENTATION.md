# Onboarding — Implementation

## Packages

- `app.myphonecheck.mobile.feature.onboarding` — VM, permission probes, state
- `...ui` — `OnboardingScreen`, `OnboardingPermissionRow`
- `...ui.pages` — page composables
- `...di` — `OnboardingModule` (Hilt marker)

## ViewModel

`OnboardingViewModel` exposes `StateFlow<OnboardingPermissionState>` refreshed on lifecycle resume and after runtime permission result.

## Permissions

Page 5 uses overlay, usage stats, and `READ_PHONE_STATE` flow identical to pre-refactor behavior.

