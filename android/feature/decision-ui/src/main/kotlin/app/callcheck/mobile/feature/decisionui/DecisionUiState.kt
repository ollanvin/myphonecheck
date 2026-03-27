package app.callcheck.mobile.feature.decisionui

import app.callcheck.mobile.core.model.DecisionResult

sealed interface DecisionUiState {
    data class Loading(val phoneNumber: String) : DecisionUiState

    data class PartialResult(
        val result: DecisionResult,
        val phoneNumber: String,
        val searchPending: Boolean = true,
    ) : DecisionUiState

    data class Complete(
        val result: DecisionResult,
        val phoneNumber: String,
    ) : DecisionUiState

    data class Error(val message: String) : DecisionUiState
}
