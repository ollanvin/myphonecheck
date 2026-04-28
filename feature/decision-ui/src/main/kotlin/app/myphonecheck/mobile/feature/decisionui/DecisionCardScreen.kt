package app.myphonecheck.mobile.feature.decisionui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import app.myphonecheck.mobile.feature.decisionui.R
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.myphonecheck.mobile.core.model.DecisionResult
import app.myphonecheck.mobile.core.util.DecisionReasoningFormatter
import app.myphonecheck.mobile.core.util.R as UtilR
import app.myphonecheck.mobile.feature.decisionui.components.ActionButtonRow
import app.myphonecheck.mobile.feature.decisionui.components.DisclaimerText
import app.myphonecheck.mobile.feature.decisionui.components.ExpandableDetailSection
import app.myphonecheck.mobile.feature.decisionui.components.FullEngineReasoningSection
import app.myphonecheck.mobile.feature.decisionui.components.PhoneNumberHeader
import app.myphonecheck.mobile.feature.decisionui.preview.PreviewData
import app.myphonecheck.mobile.feature.decisionui.ring.DecisionRing
import app.myphonecheck.mobile.feature.decisionui.ring.DecisionRingDefaults
import app.myphonecheck.mobile.feature.decisionui.ring.RingState
import app.myphonecheck.mobile.feature.decisionui.theme.MyPhoneCheckTheme

/**
 * Decision Screen — Ring 중심 레이아웃.
 *
 * 구조 (위→아래):
 *   1. 전화번호 + 국가 플래그
 *   2. Decision Ring (중앙, 핵심)
 *      └─ 내부: 판단 요약 + 이유 (최대 3개)
 *   3. 액션 버튼 [거절] [차단] [상세]
 *   4. 상세 정보 (접이식)
 *   5. 면책 문구
 *
 * 기존 카드형 UI 완전 제거.
 * Ring이 화면의 시각적·구조적 중심.
 */
@Composable
fun DecisionCardScreen(
    state: DecisionUiState,
    onReject: () -> Unit = {},
    onBlock: () -> Unit = {},
    onDetail: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MyPhoneCheckTheme.colors.darkBackground),
        contentAlignment = Alignment.Center,
    ) {
        when (state) {
            is DecisionUiState.Loading -> {
                RingLoadingState(phoneNumber = state.phoneNumber)
            }

            is DecisionUiState.PartialResult -> {
                RingDecisionContent(
                    result = state.result,
                    phoneNumber = state.phoneNumber,
                    searchPending = state.searchPending,
                    onReject = onReject,
                    onBlock = onBlock,
                    onDetail = onDetail,
                )
            }

            is DecisionUiState.Complete -> {
                RingDecisionContent(
                    result = state.result,
                    phoneNumber = state.phoneNumber,
                    searchPending = false,
                    onReject = onReject,
                    onBlock = onBlock,
                    onDetail = onDetail,
                )
            }

            is DecisionUiState.Error -> {
                RingErrorState(message = state.message)
            }
        }
    }
}

// ============================================================
// Loading: Ring이 3색 그래디언트로 회전하는 상태
// ============================================================

@Composable
private fun RingLoadingState(phoneNumber: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // 상단: 전화번호
        PhoneNumberHeader(
            phoneNumber = phoneNumber,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 중앙: Decision Ring (LOADING — 3색 그래디언트 회전)
        DecisionRing(
            state = RingState.LOADING,
            size = DecisionRingDefaults.DECISION_CARD_SIZE,
        ) {
            // 링 내부: 분석 중 메시지
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.decision_analyzing),
                    color = MyPhoneCheckTheme.colors.textPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.decision_collecting_evidence),
                    color = MyPhoneCheckTheme.colors.textSecondary,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

// ============================================================
// Error: Ring이 UNKNOWN 상태, 에러 메시지 표시
// ============================================================

@Composable
private fun RingErrorState(message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DecisionRing(
            state = RingState.UNKNOWN,
            size = DecisionRingDefaults.DECISION_CARD_SIZE,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.decision_error),
                    color = MyPhoneCheckTheme.colors.error,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = message,
                    color = MyPhoneCheckTheme.colors.textSecondary,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                )
            }
        }
    }
}

// ============================================================
// 판단 결과: Ring 중심 전체 레이아웃
// ============================================================

@Composable
private fun RingDecisionContent(
    result: DecisionResult,
    phoneNumber: String,
    searchPending: Boolean,
    onReject: () -> Unit,
    onBlock: () -> Unit,
    onDetail: () -> Unit,
) {
    // ActionRecommendation → RingState 매핑 (전 접점 통일)
    val ringState = RingState.fromAction(result.action)
    val ringColor = ringStateDisplayColor(ringState)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // ── 1. 상단: 전화번호 + 국가 플래그 ──
        PhoneNumberHeader(
            phoneNumber = phoneNumber,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ── 2. 중앙: Decision Ring ──
        DecisionRing(
            state = ringState,
            size = DecisionRingDefaults.DECISION_CARD_SIZE,
        ) {
            RingInnerContent(
                result = result,
                ringColor = ringColor,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        FullEngineReasoningSection(
            result = result,
            searchPending = searchPending,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ── 3. 하단: 액션 버튼 [거절] [차단] [상세] ──
        ActionButtonRow(
            recommendation = result.action,
            onReject = onReject,
            onBlock = onBlock,
            onDetail = onDetail,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ── 4. 상세 정보 (접이식) ──
        ExpandableDetailSection(
            deviceEvidence = result.deviceEvidence,
            searchEvidence = result.searchEvidence,
            searchPending = searchPending,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ── 5. 면책 문구 ──
        DisclaimerText(
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

// ============================================================
// Ring 내부 컨텐츠: 판단 요약 + 이유
// ============================================================

/**
 * Ring 내부: 요약 + SAFE/WARNING/DANGER + 신뢰도 + 엔진 근거(전부, 최대 3줄은 엔진 계약).
 */
@Composable
private fun RingInnerContent(
    result: DecisionResult,
    ringColor: androidx.compose.ui.graphics.Color,
) {
    val resources = LocalContext.current.resources
    val riskLabel = DecisionReasoningFormatter.riskTriLabel(resources, result.riskLevel)
    val pct = DecisionReasoningFormatter.confidencePercent(result.confidence)
    val confidenceLine = stringResource(UtilR.string.reasoning_overlay_risk_confidence, riskLabel, pct)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 12.dp),
    ) {
        Text(
            text = result.summary,
            color = ringColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 26.sp,
            maxLines = 2,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = confidenceLine,
            color = ringColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
        if (result.reasons.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            result.reasons.forEach { reason ->
                Text(
                    text = "· $reason",
                    color = MyPhoneCheckTheme.colors.textSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 4,
                    modifier = Modifier.padding(vertical = 2.dp),
                )
            }
        }
    }
}

// ============================================================
// 상태별 디스플레이 색상
// ============================================================

/**
 * RingState에 따른 텍스트 표시 색상.
 * 링 내부 요약 텍스트에 사용.
 */
@Composable
private fun ringStateDisplayColor(state: RingState): androidx.compose.ui.graphics.Color {
    return when (state) {
        RingState.LOADING -> MyPhoneCheckTheme.colors.textPrimary
        RingState.SAFE -> MyPhoneCheckTheme.colors.riskSafe
        RingState.CAUTION -> MyPhoneCheckTheme.colors.riskMedium
        RingState.DANGER -> MyPhoneCheckTheme.colors.riskCritical
        RingState.UNKNOWN -> MyPhoneCheckTheme.colors.textTertiary
    }
}

// ============================================================
// Preview
// ============================================================

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0F0F0F,
    widthDp = 380,
    heightDp = 700,
    name = "Ring UI - Loading",
)
@Composable
private fun DecisionCardScreenLoadingPreview() {
    MyPhoneCheckTheme {
        DecisionCardScreen(
            state = DecisionUiState.Loading("010-1234-5678"),
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0F0F0F,
    widthDp = 380,
    heightDp = 700,
    name = "Ring UI - Safe",
)
@Composable
private fun DecisionCardScreenSafePreview() {
    MyPhoneCheckTheme {
        DecisionCardScreen(
            state = DecisionUiState.Complete(
                result = PreviewData.knownResult,
                phoneNumber = "010-1234-5678",
            ),
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0F0F0F,
    widthDp = 380,
    heightDp = 700,
    name = "Ring UI - Danger (Scam)",
)
@Composable
private fun DecisionCardScreenDangerPreview() {
    MyPhoneCheckTheme {
        DecisionCardScreen(
            state = DecisionUiState.Complete(
                result = PreviewData.scamResult,
                phoneNumber = "02-555-0199",
            ),
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0F0F0F,
    widthDp = 380,
    heightDp = 700,
    name = "Ring UI - Caution (Spam)",
)
@Composable
private fun DecisionCardScreenCautionPreview() {
    MyPhoneCheckTheme {
        DecisionCardScreen(
            state = DecisionUiState.Complete(
                result = PreviewData.spamResult,
                phoneNumber = "02-1234-5678",
            ),
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0F0F0F,
    widthDp = 380,
    heightDp = 700,
    name = "Ring UI - Safe (Delivery)",
)
@Composable
private fun DecisionCardScreenDeliveryPreview() {
    MyPhoneCheckTheme {
        DecisionCardScreen(
            state = DecisionUiState.Complete(
                result = PreviewData.deliveryResult,
                phoneNumber = "1588-1234",
            ),
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0F0F0F,
    widthDp = 380,
    heightDp = 700,
    name = "Ring UI - Error",
)
@Composable
private fun DecisionCardScreenErrorPreview() {
    MyPhoneCheckTheme {
        DecisionCardScreen(
            state = DecisionUiState.Error("분석 중 오류가 발생했습니다."),
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0F0F0F,
    widthDp = 380,
    heightDp = 700,
    name = "Ring UI - Partial Result",
)
@Composable
private fun DecisionCardScreenPartialPreview() {
    MyPhoneCheckTheme {
        DecisionCardScreen(
            state = DecisionUiState.PartialResult(
                result = PreviewData.unknownResult,
                phoneNumber = "070-8888-9999",
                searchPending = true,
            ),
        )
    }
}
