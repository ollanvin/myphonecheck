package app.myphonecheck.mobile.feature.onboarding

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    application: Application,
) : AndroidViewModel(application) {

    private val _permissionState = MutableStateFlow(computeSnapshot())

    val permissionState: StateFlow<OnboardingPermissionState> = _permissionState.asStateFlow()

    fun refreshPermissionSnapshot() {
        _permissionState.value = computeSnapshot()
    }

    private fun computeSnapshot(): OnboardingPermissionState {
        val ctx = getApplication<Application>()
        return OnboardingPermissionState(
            overlayGranted = ctx.hasDrawOverlayPermission(),
            usageStatsGranted = ctx.hasUsageStatsPermission(),
            phoneStateGranted = ctx.hasReadPhoneStatePermission(),
        )
    }
}
