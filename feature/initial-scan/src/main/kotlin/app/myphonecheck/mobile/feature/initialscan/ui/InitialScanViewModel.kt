package app.myphonecheck.mobile.feature.initialscan.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.myphonecheck.mobile.feature.initialscan.repository.BaseDataRepository
import app.myphonecheck.mobile.feature.initialscan.service.InitialScanService
import app.myphonecheck.mobile.feature.initialscan.service.ScanResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Initial Scan ViewModel (Architecture v2.0.0 §28).
 *
 * 흐름: PermissionConsent → InProgress → Completed → main 진입.
 */
@HiltViewModel
class InitialScanViewModel @Inject constructor(
    private val service: InitialScanService,
    private val baseDataRepository: BaseDataRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<InitialScanUiState>(InitialScanUiState.PermissionConsent)
    val state: StateFlow<InitialScanUiState> = _state.asStateFlow()

    fun checkAlreadyCompleted(onAlready: () -> Unit) {
        viewModelScope.launch {
            if (baseDataRepository.isInitialScanCompleted()) onAlready()
        }
    }

    fun startScan() {
        if (_state.value is InitialScanUiState.InProgress) return
        _state.value = InitialScanUiState.InProgress
        viewModelScope.launch {
            val result = service.execute()
            _state.value = InitialScanUiState.Completed(result)
        }
    }

    fun reset() {
        _state.value = InitialScanUiState.PermissionConsent
    }
}

sealed class InitialScanUiState {
    object PermissionConsent : InitialScanUiState()
    object InProgress : InitialScanUiState()
    data class Completed(val result: ScanResult) : InitialScanUiState()
}
