package app.callcheck.mobile.feature.decisionui

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.callcheck.mobile.core.model.DecisionResult
import app.callcheck.mobile.core.model.RiskLevel
import app.callcheck.mobile.feature.decisionui.components.ActionButtonRow
import app.callcheck.mobile.feature.decisionui.components.DisclaimerText
import app.callcheck.mobile.feature.decisionui.components.ExpandableDetailSection
import app.callcheck.mobile.feature.decisionui.components.PhoneNumberHeader
import app.callcheck.mobile.feature.decisionui.preview.PreviewData
import app.callcheck.mobile.feature.decisionui.ring.DecisionRing
import app.callcheck.mobile.feature.decisionui.ring.DecisionRingDefaults
import app.callcheck.mobile.feature.decisionui.ring.RingState
import app.callcheck.mobile.feature.decisionui.theme.CallCheckTheme

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
            .background(CallCheckTheme.colors.darkBackground),
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
                    text = "분석 중",
                    color = CallCheckTheme.colors.textPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "판단 근거 수집 중...",
                    color = CallCheckTheme.colors.textSecondary,
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
                    text = "오류 발생",
                    color = CallCheckTheme.colors.error,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = message,
                    color = CallCheckTheme.colors.textSecondary,
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
            // 링 내부 컨텐츠
            RingInnerContent(
                summary = result.summary,
                reasons = result.reasons,
                ringColor = ringColor,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

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
 * Ring 내부에 표시되는 컨텐츠.
 * - 판단 요약 (큰 텍스트, 상태색)
 * - 이유 최대 3개 (작은 텍스트)
 */
@Composable
private fun RingInnerContent(
    summary: String,
    reasons: List<String>,
    ringColor: androidx.compose.ui.graphics.Color,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 12.dp),
    ) {
        // 판단 요약 — Ring의 핵심 메시지
        Text(
            text = summary,
            color = ringColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 26.sp,
            maxLines = 2,
        )

        // 이유 리스트 (최대 3개, 링 내부에 적합한 컴팩트 사이즈)
        if (reasons.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))

            reasons.take(3).forEach { reason ->
                Text(
                    text = "· $reason",
                    color = CallCheckTheme.colors.textSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
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
        RingState.LOADING -> CallCheckTheme.colors.textPrimary
        RingState.SAFE -> CallCheckTheme.colors.riskSafe
        RingState.CAUTION -> CallCheckTheme.colors.riskMedium
        RingState.DANGER -> CallCheckTheme.colors.riskCritical
        RingState.UNKNOWN -> CallCheckTheme.colors.textTertiary
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
    CallCheckTheme {
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
    CallCheckTheme {
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
    CallCheckTheme {
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
    CallCheckTheme {
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
    CallCheckTheme {
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
    CallCheckTheme {
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
    CallCheckTheme {
        DecisionCardScreen(
            state = DecisionUiState.PartialResult(
                result = PreviewData.unknownResult,
                phoneNumber = "070-8888-9999",
                searchPending = true,
            ),
        )
    }
}
