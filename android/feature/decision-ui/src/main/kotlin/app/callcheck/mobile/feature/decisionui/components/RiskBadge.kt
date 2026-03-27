package app.callcheck.mobile.feature.decisionui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.callcheck.mobile.core.model.RiskLevel
import app.callcheck.mobile.feature.decisionui.theme.CallCheckTheme

/**
 * Colored badge showing risk level.
 * PRD 4 levels: LOW / MEDIUM / HIGH / UNKNOWN
 */
@Composable
fun RiskBadge(
    riskLevel: RiskLevel,
    modifier: Modifier = Modifier,
) {
    val (backgroundColor, textColor, displayText) = when (riskLevel) {
        RiskLevel.LOW -> Triple(
            CallCheckTheme.colors.riskLow,
            Color(0xFF1B5E20),
            riskLevel.displayNameKo,
        )

        RiskLevel.MEDIUM -> Triple(
            CallCheckTheme.colors.riskMedium,
            Color(0xFF3E2723),
            riskLevel.displayNameKo,
        )

        RiskLevel.HIGH -> Triple(
            CallCheckTheme.colors.riskHigh,
            Color.White,
            riskLevel.displayNameKo,
        )

        RiskLevel.UNKNOWN -> Triple(
            CallCheckTheme.colors.border,
            CallCheckTheme.colors.textSecondary,
            riskLevel.displayNameKo,
        )
    }

    Box(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(20.dp),
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = displayText,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RiskBadgeLowPreview() {
    CallCheckTheme { RiskBadge(riskLevel = RiskLevel.LOW) }
}

@Preview(showBackground = true)
@Composable
private fun RiskBadgeMediumPreview() {
    CallCheckTheme { RiskBadge(riskLevel = RiskLevel.MEDIUM) }
}

@Preview(showBackground = true)
@Composable
private fun RiskBadgeHighPreview() {
    CallCheckTheme { RiskBadge(riskLevel = RiskLevel.HIGH) }
}

@Preview(showBackground = true)
@Composable
private fun RiskBadgeUnknownPreview() {
    CallCheckTheme { RiskBadge(riskLevel = RiskLevel.UNKNOWN) }
}
