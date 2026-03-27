package app.callcheck.mobile.feature.decisionui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import app.callcheck.mobile.core.model.DeviceEvidence
import app.callcheck.mobile.core.model.SearchEvidence
import app.callcheck.mobile.feature.decisionengine.DecisionEngine

/**
 * ViewModel for the Decision Card UI.
 *
 * Progressive rendering flow:
 * 1. Loading (phone number displayed, spinner)
 * 2. PartialResult (device evidence only, search pending)
 * 3. Complete (device + search evidence)
 */
@HiltViewModel
class DecisionViewModel @Inject constructor(
    private val decisionEngine: DecisionEngine,
) : ViewModel() {

    private val _uiState = MutableStateFlow<DecisionUiState>(
        DecisionUiState.Loading("")
    )
    val uiState: StateFlow<DecisionUiState> = _uiState.asStateFlow()

    /**
     * Evaluate with current evidence. Called twice:
     * first with device-only, then with device+search.
     */
    fun evaluateCall(
        phoneNumber: String,
        deviceEvidence: DeviceEvidence? = null,
        searchEvidence: SearchEvidence? = null,
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = DecisionUiState.Loading(phoneNumber)

                val result = decisionEngine.evaluate(
                    deviceEvidence = deviceEvidence,
                    searchEvidence = searchEvidence,
                )

                val searchPending = searchEvidence == null

                if (searchPending) {
                    _uiState.value = DecisionUiState.PartialResult(
                        result = result,
                        phoneNumber = phoneNumber,
                        searchPending = true,
                    )
                } else {
                    _uiState.value = DecisionUiState.Complete(
                        result = result,
                        phoneNumber = phoneNumber,
                    )
                }
            } catch (e: Exception) {
                _uiState.value = DecisionUiState.Error(
                    "분석 중 오류가 발생했습니다: ${e.message}"
                )
            }
        }
    }

    /**
     * Re-evaluate when search enrichment completes.
     */
    fun updateWithSearchResults(
        phoneNumber: String,
        deviceEvidence: DeviceEvidence? = null,
        searchEvidence: SearchEvidence?,
    ) {
        viewModelScope.launch {
            try {
                val result = decisionEngine.evaluate(
                    deviceEvidence = deviceEvidence,
                    searchEvidence = searchEvidence,
                )
                _uiState.value = DecisionUiState.Complete(
                    result = result,
                    phoneNumber = phoneNumber,
                )
            } catch (e: Exception) {
                _uiState.value = DecisionUiState.Error(
                    "결과 업데이트 중 오류가 발생했습니다: ${e.message}"
                )
            }
        }
    }

    fun onAnswer() { handleUserAction("answered") }
    fun onReject() { handleUserAction("rejected") }
    fun onBlock() { handleUserAction("blocked") }

    private fun handleUserAction(action: String) {
        // Log action for analytics, close decision card
    }

    fun reset() {
        _uiState.value = DecisionUiState.Loading("")
    }
}
