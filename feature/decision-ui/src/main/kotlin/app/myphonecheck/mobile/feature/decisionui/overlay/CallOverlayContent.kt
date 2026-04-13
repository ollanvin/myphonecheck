package app.myphonecheck.mobile.feature.decisionui.overlay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.myphonecheck.mobile.core.model.DecisionResult
import app.myphonecheck.mobile.core.model.RingSystem
import app.myphonecheck.mobile.core.model.RiskLevel
import app.myphonecheck.mobile.feature.decisionui.components.FullEngineReasoningSection
import app.myphonecheck.mobile.feature.decisionui.ring.DecisionRing
import app.myphonecheck.mobile.feature.decisionui.ring.DecisionRingDefaults
import app.myphonecheck.mobile.feature.decisionui.ring.RingState
import app.myphonecheck.mobile.feature.decisionui.theme.MyPhoneCheckTheme

/**
 * 전화 수신 시 화면 상단에 표시되는 오버레이 컨텐츠.
 *
 * 설계 원칙:
 * - 미니 Ring UI (48dp) + 번호 + 요약 1줄
 * - 풀 UI 금지 → 즉시 판단만 제공
 * - FirstRun 경험과 동일한 색상 체계 (RingSystem 참조)
 * - "아까 봤던 그 화면이 실제로 뜨네" 느낌 유지
 *
 * 구성:
 * ┌──────────────────────────────┐
 * │ ○ 02-555-0199                │
 * │   🔴 보이스피싱 의심           │
 * │              [확인]           │
 * └──────────────────────────────┘
 *
 * @param result 판단 결과
 * @param phoneNumber 전화번호
 * @param isVisible 오버레이 노출 여부
 * @param onDismiss 확인 버튼 콜백
 */
@Composable
fun CallOverlayContent(
    result: DecisionResult?,
    phoneNumber: String,
    isVisible: Boolean,
    onDismiss: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            animationSpec = tween(300),
            initialOffsetY = { -it },
        ) + fadeIn(animationSpec = tween(300)),
        exit = slideOutVertically(
            animationSpec = tween(200),
            targetOffsetY = { -it },
        ) + fadeOut(animationSpec = tween(200)),
    ) {
        OverlayCard(
            result = result,
            phoneNumber = phoneNumber,
            onDismiss = onDismiss,
            modifier = modifier,
        )
    }
}

@Composable
private fun OverlayCard(
    result: DecisionResult?,
    phoneNumber: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val ringState = result?.let { RingState.fromAction(it.action) } ?: RingState.LOADING
    val stateColor = result?.let { Color(RingSystem.color(it.action)) } ?: MyPhoneCheckTheme.colors.primary

    val scroll = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scroll)
            .background(
                color = MyPhoneCheckTheme.colors.cardBackground,
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
            )
            .padding(16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            // 미니 Ring (48dp)
            DecisionRing(
                state = ringState,
                size = DecisionRingDefaults.WIDGET_SIZE,
            )

            Spacer(modifier = Modifier.width(12.dp))

            // 번호 + 요약
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = phoneNumber,
                    color = MyPhoneCheckTheme.colors.textPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                if (result != null) {
                    Text(
                        text = result.summary,
                        color = stateColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 3,
                    )
                } else {
                    Text(
                        text = "분석 중...",
                        color = MyPhoneCheckTheme.colors.textSecondary,
                        fontSize = 14.sp,
                    )
                }
            }
        }

        // 면책 + 확인 버튼
        if (result != null) {
            Spacer(modifier = Modifier.height(8.dp))

            FullEngineReasoningSection(
                result = result,
                searchPending = false,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = RingSystem.DISCLAIMER_KO,
                color = MyPhoneCheckTheme.colors.textTertiary,
                fontSize = 9.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(
                    containerColor = stateColor,
                    contentColor = Color.White,
                ),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(
                    text = "확인",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

/**
 * RiskLevel → Compose Color 매핑.
 * RingSystem ARGB Int 값을 Compose Color로 변환.
 */
@Composable
private fun ringStateColor(riskLevel: RiskLevel): Color {
    return Color(RingSystem.color(riskLevel))
}

// ============================================================
// Preview
// ============================================================

@Preview(
    showBackground = true,
    backgroundColor = 0xFF000000,
    widthDp = 380,
    heightDp = 200,
    name = "Overlay - Danger",
)
@Composable
private fun CallOverlayDangerPreview() {
    MyPhoneCheckTheme {
        CallOverlayContent(
            result = DecisionResult(
                riskLevel = RiskLevel.HIGH,
                category = app.myphonecheck.mobile.core.model.ConclusionCategory.SCAM_RISK_HIGH,
                action = app.myphonecheck.mobile.core.model.ActionRecommendation.REJECT,
                confidence = 0.89f,
                summary = "보이스피싱 의심",
                reasons = listOf("사기 신고 이력 다수"),
                deviceEvidence = null,
                searchEvidence = null,
            ),
            phoneNumber = "02-555-0199",
            isVisible = true,
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF000000,
    widthDp = 380,
    heightDp = 150,
    name = "Overlay - Loading",
)
@Composable
private fun CallOverlayLoadingPreview() {
    MyPhoneCheckTheme {
        CallOverlayContent(
            result = null,
            phoneNumber = "010-9876-5432",
            isVisible = true,
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF000000,
    widthDp = 380,
    heightDp = 200,
    name = "Overlay - Safe",
)
@Composable
private fun CallOverlaySafePreview() {
    MyPhoneCheckTheme {
        CallOverlayContent(
            result = DecisionResult(
                riskLevel = RiskLevel.LOW,
                category = app.myphonecheck.mobile.core.model.ConclusionCategory.KNOWN_CONTACT,
                action = app.myphonecheck.mobile.core.model.ActionRecommendation.ANSWER,
                confidence = 0.95f,
                summary = "저장된 연락처",
                reasons = listOf("연락처에 등록된 번호"),
                deviceEvidence = null,
                searchEvidence = null,
            ),
            phoneNumber = "010-1234-5678",
            isVisible = true,
        )
    }
}
