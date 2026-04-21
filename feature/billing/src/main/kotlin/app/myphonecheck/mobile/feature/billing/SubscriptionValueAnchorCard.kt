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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.myphonecheck.mobile.feature.billing.R

@Composable
fun SubscriptionValueAnchorCard(
    state: SubscriptionValueAnchorState?,
    modifier: Modifier = Modifier,
) {
    if (state == null || !state.visible) return

    val suspiciousCallsText = state.suspiciousCallsCount?.let {
        stringResource(R.string.value_anchor_suspicious_calls_fmt, it)
    }
    val riskyMessagesText = state.riskyLinkMessagesCount?.let {
        stringResource(R.string.value_anchor_risky_messages_fmt, it)
    }

    val metricLines = buildList {
        suspiciousCallsText?.let { add(it) }
        riskyMessagesText?.let { add(it) }
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
            text = stringResource(R.string.value_anchor_period_fmt, state.selectedPeriodLabel),
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
            text = stringResource(R.string.value_anchor_retention_note),
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
