package app.myphonecheck.mobile.feature.billing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
    if (state == null || state.shouldHide) return

    Column(
        modifier = modifier
            .background(
                color = Color(0xFF1A1A1A),
                shape = RoundedCornerShape(20.dp),
            )
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "최근 ${state.windowDays}일 동안",
            color = Color(0xFFB3B3B3),
            fontSize = 13.sp,
        )

        state.metrics.take(2).forEach { metric ->
            Text(
                text = metric.toDisplayText(),
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

private fun SubscriptionValueAnchorState.ValueMetric.toDisplayText(): String = when (this) {
    is SubscriptionValueAnchorState.ValueMetric.BlockedCalls -> "의심 전화 ${count}건 탐지"
    is SubscriptionValueAnchorState.ValueMetric.RiskyLinks -> "위험 링크 메시지 ${count}건 확인"
}

@Preview(showBackground = true, backgroundColor = 0xFF101010, widthDp = 360)
@Composable
private fun SubscriptionValueAnchorCardPreview_WithData() {
    SubscriptionValueAnchorCard(
        state = SubscriptionValueAnchorState(
            blockedCalls = 12,
            riskyLinks = 4,
            windowDays = 30,
        ),
        modifier = Modifier.padding(16.dp),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF101010, widthDp = 360, heightDp = 120)
@Composable
private fun SubscriptionValueAnchorCardPreview_WithoutData() {
    SubscriptionValueAnchorCard(
        state = SubscriptionValueAnchorState(
            blockedCalls = 0,
            riskyLinks = 0,
            windowDays = 90,
        ),
        modifier = Modifier.padding(16.dp),
    )
}
