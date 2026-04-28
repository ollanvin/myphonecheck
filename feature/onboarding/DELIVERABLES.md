# Onboarding — Deliverables

## Kotlin

| Area | Files |
|------|--------|
| UI | `ui/OnboardingScreen.kt`, `ui/OnboardingPermissionRow.kt`, `ui/pages/OnboardingPage1.kt` … `OnboardingPage5.kt` |
| State | `OnboardingViewModel.kt`, `OnboardingPermissionState.kt`, `PermissionContext.kt` |
| DI | `di/OnboardingModule.kt` |

## Resources

- `src/main/res/values/strings.xml` — onboarding EN strings

## App integration

- `MyPhoneCheckNavHost.kt`: imports onboarding UI; Settings uses `OnboardingPermissionRow` with app string labels for Allow/Granted.

## Verification

- `./gradlew :feature:onboarding:compileDebugKotlin :app:compileDebugKotlin`

