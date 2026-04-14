package app.myphonecheck.mobile.feature.billing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SubscriptionValueAnchorCard(
    state: SubscriptionValueAnchorState?,
    modifier: Modifier = Modifier,
) {
    if (state == null || !state.visible) return

    val metricLines = buildList {
        state.suspiciousCallsCount?.let {
            add("검색 근거가 있는 의심 전화 ${it}건 확인")
        }
        state.riskyLinkMessagesCount?.let {
            add("위험 링크 메시지 ${it}건 확인")
        }
    }.take(2)

    if (metricLines.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFF1A1A1A),
                shape = RoundedCornerShape(20.dp),
            )
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "${state.selectedPeriodLabel} 동안",
            color = Color(0xFFB3B3B3),
            fontSize = 13.sp,
        )

        metricLines.forEach { line ->
            Text(
                text = line,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Text(
            text = "이 기능은 구독 시 유지됩니다",
            color = Color(0xFFB3B3B3),
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF101010, widthDp = 360)
@Composable
private fun SubscriptionValueAnchorCardPreview_WithMeasuredState() {
    SubscriptionValueAnchorCard(
        state = SubscriptionValueAnchorState(
            selectedPeriodLabel = "최근 7일",
            suspiciousCallsCount = 2,
            riskyLinkMessagesCount = 1,
            visible = true,
        ),
        modifier = Modifier.padding(16.dp),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF101010, widthDp = 360, heightDp = 120)
@Composable
private fun SubscriptionValueAnchorCardPreview_HiddenState() {
    SubscriptionValueAnchorCard(
        state = SubscriptionValueAnchorState(
            selectedPeriodLabel = "누계",
            suspiciousCallsCount = null,
            riskyLinkMessagesCount = null,
            visible = false,
        ),
        modifier = Modifier.padding(16.dp),
    )
}
