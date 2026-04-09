package app.myphonecheck.mobile.feature.decisionui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.myphonecheck.mobile.core.model.ActionRecommendation
import app.myphonecheck.mobile.feature.decisionui.theme.MyPhoneCheckTheme

/**
 * 사용자 액션 버튼 행: 거절 / 차단 / 자세히 보기
 *
 * "수신(Answer)" 버튼 의도적 제외.
 * 수신 행동은 시스템 콜 UI가 담당합니다.
 * MyPhoneCheck는 판단 재료를 제공할 뿐, 행동을 대행하지 않습니다.
 */
@Composable
fun ActionButtonRow(
    recommendation: ActionRecommendation,
    onReject: () -> Unit = {},
    onBlock: () -> Unit = {},
    onDetail: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ActionButton(
            label = "거절",
            icon = {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Reject call",
                    tint = Color.White,
                )
            },
            backgroundColor = MyPhoneCheckTheme.colors.buttonReject,
            onClick = onReject,
            modifier = Modifier.weight(1f),
        )

        ActionButton(
            label = "차단",
            icon = {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Block number",
                    tint = Color.White,
                )
            },
            backgroundColor = MyPhoneCheckTheme.colors.buttonBlock,
            onClick = onBlock,
            modifier = Modifier.weight(1f),
        )

        ActionButton(
            label = "자세히",
            icon = {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "View details",
                    tint = Color.White,
                )
            },
            backgroundColor = MyPhoneCheckTheme.colors.primary,
            onClick = onDetail,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ActionButton(
    label: String,
    icon: @Composable () -> Unit,
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = Color.White,
        ),
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(),
        ) {
            icon()
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ActionButtonRowPreview() {
    MyPhoneCheckTheme {
        ActionButtonRow(recommendation = ActionRecommendation.REJECT)
    }
}
