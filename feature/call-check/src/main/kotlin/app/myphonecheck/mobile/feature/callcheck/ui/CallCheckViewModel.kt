package app.myphonecheck.mobile.feature.callcheck.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.myphonecheck.mobile.core.globalengine.simcontext.SimContextProvider
import app.myphonecheck.mobile.feature.callcheck.repository.CallEntry
import app.myphonecheck.mobile.feature.callcheck.repository.CallLogRepository
import app.myphonecheck.mobile.feature.callcheck.service.CallVerificationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * CallCheck 메인 ViewModel (Architecture v2.0.0 §21).
 *
 * 권한 상태 + 최근 통화 리스트 노출. 영구 저장 0 (헌법 §2 In-Bound Zero).
 */
@HiltViewModel
class CallCheckViewModel @Inject constructor(
    private val callLogRepository: CallLogRepository,
    private val verificationService: CallVerificationService,
    private val simContextProvider: SimContextProvider,
) : ViewModel() {

    private val _uiState = MutableStateFlow<CallCheckUiState>(CallCheckUiState.Loading)
    val uiState: StateFlow<CallCheckUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            if (!callLogRepository.hasPermission()) {
                _uiState.value = CallCheckUiState.PermissionRequired
                return@launch
            }
            val entries = withContext(Dispatchers.IO) {
                callLogRepository.readRecentCalls(limit = 100)
            }
            val region = simContextProvider.resolve().phoneRegion
            _uiState.value = CallCheckUiState.Loaded(entries, simRegion = region)
        }
    }
}

sealed class CallCheckUiState {
    object Loading : CallCheckUiState()
    object PermissionRequired : CallCheckUiState()
    data class Loaded(
        val entries: List<CallEntry>,
        val simRegion: String,
    ) : CallCheckUiState()
}
