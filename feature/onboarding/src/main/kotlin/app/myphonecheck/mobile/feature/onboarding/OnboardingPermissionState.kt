package app.myphonecheck.mobile.feature.onboarding

/**
 * Snapshot of runtime / special permissions checked on onboarding Page 5.
 */
data class OnboardingPermissionState(
    val overlayGranted: Boolean,
    val usageStatsGranted: Boolean,
    val phoneStateGranted: Boolean,
)
