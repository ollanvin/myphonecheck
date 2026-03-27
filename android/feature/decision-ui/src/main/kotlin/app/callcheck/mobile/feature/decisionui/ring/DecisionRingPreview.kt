package app.callcheck.mobile.feature.decisionui.ring

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.callcheck.mobile.feature.decisionui.theme.CallCheckTheme

/**
 * Decision Ring Preview 모음.
 * IDE에서 즉시 확인 가능한 전체 상태 프리뷰.
 */

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0F0F0F,
    widthDp = 320,
    heightDp = 400,
)
@Composable
private fun DecisionRingLoadingPreview() {
    CallCheckTheme {
        Column(
            modifier = Modifier
                .background(CallCheckTheme.colors.darkBackground)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            DecisionRing(
                state = RingState.LOADING,
                size = DecisionRingDefaults.DECISION_CARD_SIZE,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "010-1234-5678",
                        color = CallCheckTheme.colors.textPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = "분석 중...",
                        color = CallCheckTheme.colors.textSecondary,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0F0F0F,
    widthDp = 320,
    heightDp = 400,
)
@Composable
private fun DecisionRingSafePreview() {
    CallCheckTheme {
        Column(
            modifier = Modifier
                .background(CallCheckTheme.colors.darkBackground)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            DecisionRing(
                state = RingState.SAFE,
                size = DecisionRingDefaults.DECISION_CARD_SIZE,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "010-1234-5678",
                        color = CallCheckTheme.colors.textPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "안전 추정",
                        color = CallCheckTheme.colors.riskSafe,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "연락처에 등록된 번호",
                        color = CallCheckTheme.colors.textSecondary,
                        fontSize = 12.sp,
                    )
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0F0F0F,
    widthDp = 320,
    heightDp = 400,
)
@Composable
private fun DecisionRingDangerPreview() {
    CallCheckTheme {
        Column(
            modifier = Modifier
                .background(CallCheckTheme.colors.darkBackground)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            DecisionRing(
                state = RingState.DANGER,
                size = DecisionRingDefaults.DECISION_CARD_SIZE,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "02-555-0199",
                        color = CallCheckTheme.colors.textPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "스캠 의심",
                        color = CallCheckTheme.colors.riskCritical,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "보이스피싱 신고 이력",
                        color = CallCheckTheme.colors.textSecondary,
                        fontSize = 12.sp,
                    )
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0F0F0F,
    widthDp = 360,
    heightDp = 200,
    name = "All States Row",
)
@Composable
private fun DecisionRingAllStatesPreview() {
    CallCheckTheme {
        Row(
            modifier = Modifier
                .background(CallCheckTheme.colors.darkBackground)
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RingState.entries.forEach { state ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    DecisionRing(
                        state = state,
                        size = 56.dp,
                    )
                    Text(
                        text = state.name,
                        color = CallCheckTheme.colors.textSecondary,
                        fontSize = 9.sp,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0F0F0F,
    widthDp = 240,
    heightDp = 300,
    name = "Home Dashboard Mini Ring",
)
@Composable
private fun DecisionRingHomeDashboardPreview() {
    CallCheckTheme {
        Column(
            modifier = Modifier
                .background(CallCheckTheme.colors.darkBackground)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            DecisionRing(
                state = RingState.LOADING,
                size = DecisionRingDefaults.HOME_DASHBOARD_SIZE,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "보호 중",
                        color = CallCheckTheme.colors.textPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}
