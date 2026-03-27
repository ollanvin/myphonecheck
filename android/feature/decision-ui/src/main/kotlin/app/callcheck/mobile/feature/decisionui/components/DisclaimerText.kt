package app.callcheck.mobile.feature.decisionui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.callcheck.mobile.feature.decisionui.theme.CallCheckTheme

/**
 * Disclaimer text about the accuracy and limitations of the decision.
 */
@Composable
fun DisclaimerText(
    modifier: Modifier = Modifier,
) {
    Text(
        text = "이 결과는 디바이스 이력 및 웹 검색 기반 요약이며 정확성을 보장하지 않습니다.",
        modifier = modifier.padding(top = 4.dp),
        color = CallCheckTheme.colors.textTertiary,
        fontSize = 10.sp,
        textAlign = TextAlign.Center,
        lineHeight = 14.sp,
    )
}

@Preview(showBackground = true)
@Composable
private fun DisclaimerTextPreview() {
    CallCheckTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            DisclaimerText()
        }
    }
}
