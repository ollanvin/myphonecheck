package app.myphonecheck.mobile.feature.decisionui.ring

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
import androidx.compose.ui.res.stringResource
import app.myphonecheck.mobile.feature.decisionui.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.myphonecheck.mobile.feature.decisionui.theme.MyPhoneCheckTheme

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
    MyPhoneCheckTheme {
        Column(
            modifier = Modifier
                .background(MyPhoneCheckTheme.colors.darkBackground)
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
                        color = MyPhoneCheckTheme.colors.textPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = stringResource(R.string.decision_analyzing_dots),
                        color = MyPhoneCheckTheme.colors.textSecondary,
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
    MyPhoneCheckTheme {
        Column(
            modifier = Modifier
                .background(MyPhoneCheckTheme.colors.darkBackground)
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
                        color = MyPhoneCheckTheme.colors.textPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = stringResource(R.string.decision_safe_estimate),
                        color = MyPhoneCheckTheme.colors.riskSafe,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = stringResource(R.string.decision_saved_contact),
                        color = MyPhoneCheckTheme.colors.textSecondary,
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
    MyPhoneCheckTheme {
        Column(
            modifier = Modifier
                .background(MyPhoneCheckTheme.colors.darkBackground)
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
                        color = MyPhoneCheckTheme.colors.textPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = stringResource(R.string.decision_scam_suspect),
                        color = MyPhoneCheckTheme.colors.riskCritical,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = stringResource(R.string.decision_phishing_report),
                        color = MyPhoneCheckTheme.colors.textSecondary,
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
    MyPhoneCheckTheme {
        Row(
            modifier = Modifier
                .background(MyPhoneCheckTheme.colors.darkBackground)
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
                        color = MyPhoneCheckTheme.colors.textSecondary,
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
    MyPhoneCheckTheme {
        Column(
            modifier = Modifier
                .background(MyPhoneCheckTheme.colors.darkBackground)
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
                        text = stringResource(R.string.decision_protecting),
                        color = MyPhoneCheckTheme.colors.textPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}
