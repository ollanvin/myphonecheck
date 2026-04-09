package app.myphonecheck.mobile.feature.decisionui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.myphonecheck.mobile.feature.decisionui.theme.MyPhoneCheckTheme

/**
 * Single reason item with bullet point and text.
 */
@Composable
fun ReasonItem(
    reason: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top,
    ) {
        // Bullet point
        Box(
            modifier = Modifier
                .padding(top = 7.dp)
                .size(6.dp)
                .background(
                    color = MyPhoneCheckTheme.colors.primary,
                    shape = CircleShape,
                ),
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Reason text
        Text(
            text = reason,
            color = MyPhoneCheckTheme.colors.textSecondary,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            modifier = Modifier.weight(1f),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ReasonItemPreview() {
    MyPhoneCheckTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ReasonItem(reason = "디바이스에 저장된 연락처가 아닙니다")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReasonItemLongPreview() {
    MyPhoneCheckTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ReasonItem(reason = "최근 웹 검색 결과에서 사기 관련 키워드가 자주 언급되었습니다")
        }
    }
}
