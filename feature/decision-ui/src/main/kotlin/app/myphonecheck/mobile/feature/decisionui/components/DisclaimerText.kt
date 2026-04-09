package app.myphonecheck.mobile.feature.decisionui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.myphonecheck.mobile.core.model.RingSystem
import app.myphonecheck.mobile.feature.decisionui.theme.MyPhoneCheckTheme

/**
 * 면책 문구.
 *
 * RingSystem.DISCLAIMER_DETAIL_KO를 단일 소스로 참조.
 * 앱 내부, 오버레이, 위젯 모든 접점에서 동일 문구.
 *
 * "MyPhoneCheck는 판단 보조 도구이며, 최종 결정은 사용자에게 있습니다."
 */
@Composable
fun DisclaimerText(
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    Text(
        text = if (compact) RingSystem.DISCLAIMER_KO else RingSystem.DISCLAIMER_DETAIL_KO,
        modifier = modifier.padding(top = 4.dp),
        color = MyPhoneCheckTheme.colors.textTertiary,
        fontSize = if (compact) 9.sp else 10.sp,
        textAlign = TextAlign.Center,
        lineHeight = if (compact) 12.sp else 14.sp,
    )
}

@Preview(showBackground = true)
@Composable
private fun DisclaimerTextPreview() {
    MyPhoneCheckTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            DisclaimerText()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DisclaimerTextCompactPreview() {
    MyPhoneCheckTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            DisclaimerText(compact = true)
        }
    }
}
