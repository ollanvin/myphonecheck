package app.myphonecheck.mobile.viewmodel

import androidx.lifecycle.ViewModel
import app.myphonecheck.mobile.feature.privacycheck.GuardResult
import app.myphonecheck.mobile.feature.privacycheck.InitialScanGuard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import androidx.lifecycle.viewModelScope
import javax.inject.Inject

/**
 * InitialScanGuard ViewModel — Guard 결과를 UI에 실시간 제공.
 *
 * NavHost 레벨에서 hiltViewModel()로 생성.
 * guardFlow()를 StateFlow로 변환하여 HomeScreen/상세화면에서 구독.
 *
 * Guard FAIL 시: 모든 카드/상세 완료 UI 차단.
 * Guard PASS 시: 정상 UI 허용.
 */
@HiltViewModel
class InitialScanGuardViewModel @Inject constructor(
    initialScanGuard: InitialScanGuard,
) : ViewModel() {

    val guardResult: StateFlow<GuardResult> = initialScanGuard.guardFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = GuardResult.FAIL(listOf("initializing")),
        )
}
