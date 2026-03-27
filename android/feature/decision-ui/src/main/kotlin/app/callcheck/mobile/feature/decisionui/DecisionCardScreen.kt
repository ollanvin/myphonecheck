package app.callcheck.mobile.feature.decisionui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.callcheck.mobile.core.model.DecisionResult
import app.callcheck.mobile.feature.decisionui.components.ActionButtonRow
import app.callcheck.mobile.feature.decisionui.components.ConclusionText
import app.callcheck.mobile.feature.decisionui.components.DisclaimerText
import app.callcheck.mobile.feature.decisionui.components.ExpandableDetailSection
import app.callcheck.mobile.feature.decisionui.components.PhoneNumberHeader
import app.callcheck.mobile.feature.decisionui.components.ReasonsList
import app.callcheck.mobile.feature.decisionui.components.RiskBadge
import app.callcheck.mobile.feature.decisionui.preview.PreviewData
import app.callcheck.mobile.feature.decisionui.theme.CallCheckTheme

@Composable
fun DecisionCardScreen(
    state: DecisionUiState,
    onAnswer: () -> Unit = {},
    onReject: () -> Unit = {},
    onBlock: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(CallCheckTheme.colors.darkOverlay),
        contentAlignment = Alignment.Center,
    ) {
        when (state) {
            is DecisionUiState.Loading -> {
                LoadingState(phoneNumber = state.phoneNumber)
            }

            is DecisionUiState.PartialResult -> {
                DecisionCardContent(
                    result = state.result,
                    phoneNumber = state.phoneNumber,
                    searchPending = state.searchPending,
                    onAnswer = onAnswer,
                    onReject = onReject,
                    onBlock = onBlock,
                )
            }

            is DecisionUiState.Complete -> {
                DecisionCardContent(
                    result = state.result,
                    phoneNumber = state.phoneNumber,
                    searchPending = false,
                    onAnswer = onAnswer,
                    onReject = onReject,
                    onBlock = onBlock,
                )
            }

            is DecisionUiState.Error -> {
                ErrorState(message = state.message)
            }
        }
    }
}

@Composable
private fun LoadingState(phoneNumber: String) {
    Box(
        modifier = Modifier
            .background(
                color = CallCheckTheme.colors.cardBackground,
                shape = RoundedCornerShape(16.dp),
            )
            .padding(32.dp)
            .fillMaxWidth(0.9f),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(),
        ) {
            CircularProgressIndicator(
                color = CallCheckTheme.colors.primary,
                modifier = Modifier.padding(bottom = 16.dp),
            )
            Text(
                text = phoneNumber,
                color = CallCheckTheme.colors.textPrimary,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "분석 중...",
                color = CallCheckTheme.colors.textSecondary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun ErrorState(message: String) {
    Box(
        modifier = Modifier
            .background(
                color = CallCheckTheme.colors.cardBackground,
                shape = RoundedCornerShape(16.dp),
            )
            .padding(24.dp)
            .fillMaxWidth(0.9f),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = message,
            color = CallCheckTheme.colors.error,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun DecisionCardContent(
    result: DecisionResult,
    phoneNumber: String,
    searchPending: Boolean,
    onAnswer: () -> Unit,
    onReject: () -> Unit,
    onBlock: () -> Unit,
) {
    Box(
        modifier = Modifier
            .background(
                color = CallCheckTheme.colors.cardBackground,
                shape = RoundedCornerShape(16.dp),
            )
            .fillMaxWidth(0.95f),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            // Phone number header
            PhoneNumberHeader(
                phoneNumber = phoneNumber,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Risk badge
            RiskBadge(
                riskLevel = result.riskLevel,
                modifier = Modifier.align(Alignment.Start),
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Conclusion text (largest, one-line summary)
            ConclusionText(
                text = result.summary,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Supporting reasons (max 3, from DecisionResult.reasons)
            ReasonsList(
                reasons = result.reasons,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Action buttons
            ActionButtonRow(
                recommendation = result.action,
                onAnswer = onAnswer,
                onReject = onReject,
                onBlock = onBlock,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Expandable detail section
            ExpandableDetailSection(
                deviceEvidence = result.deviceEvidence,
                searchEvidence = result.searchEvidence,
                searchPending = searchPending,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Disclaimer
            DisclaimerText(
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DecisionCardScreenLoadingPreview() {
    CallCheckTheme {
        DecisionCardScreen(
            state = DecisionUiState.Loading("010-1234-5678"),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DecisionCardScreenCompletePreview() {
    CallCheckTheme {
        DecisionCardScreen(
            state = DecisionUiState.Complete(
                result = PreviewData.scamResult,
                phoneNumber = "010-4567-8901",
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DecisionCardScreenErrorPreview() {
    CallCheckTheme {
        DecisionCardScreen(
            state = DecisionUiState.Error("분석 중 오류가 발생했습니다."),
        )
    }
}
