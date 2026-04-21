package app.myphonecheck.mobile.feature.decisionui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import app.myphonecheck.mobile.feature.decisionui.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import app.myphonecheck.mobile.feature.decisionui.theme.MyPhoneCheckTheme

/**
 * Displays the conclusion summary as large, bold text.
 * This is the most prominent text in the decision card.
 */
@Composable
fun ConclusionText(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        modifier = modifier,
        color = MyPhoneCheckTheme.colors.textPrimary,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        lineHeight = 32.sp,
    )
}

@Preview(showBackground = true)
@Composable
private fun ConclusionTextPreview() {
    MyPhoneCheckTheme {
        ConclusionText(text = stringResource(R.string.preview_delivery_likely))
    }
}

@Preview(showBackground = true)
@Composable
private fun ConclusionTextLongPreview() {
    MyPhoneCheckTheme {
        ConclusionText(text = stringResource(R.string.preview_scam_likely))
    }
}
