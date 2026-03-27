package app.callcheck.mobile.feature.decisionui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Close
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
import app.callcheck.mobile.core.model.ActionRecommendation
import app.callcheck.mobile.feature.decisionui.theme.CallCheckTheme

/**
 * Row of 3 action buttons: Answer (green), Reject (orange), Block (red).
 *
 * The recommended action is visually emphasized.
 */
@Composable
fun ActionButtonRow(
    recommendation: ActionRecommendation,
    onAnswer: () -> Unit = {},
    onReject: () -> Unit = {},
    onBlock: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ActionButton(
            label = "응답",
            icon = {
                Icon(
                    imageVector = Icons.Filled.Call,
                    contentDescription = "Answer call",
                    tint = Color.White,
                )
            },
            backgroundColor = CallCheckTheme.colors.buttonAnswer,
            onClick = onAnswer,
            modifier = Modifier.weight(1f),
        )

        ActionButton(
            label = "거절",
            icon = {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Reject call",
                    tint = Color.White,
                )
            },
            backgroundColor = CallCheckTheme.colors.buttonReject,
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
            backgroundColor = CallCheckTheme.colors.buttonBlock,
            onClick = onBlock,
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
    CallCheckTheme {
        ActionButtonRow(recommendation = ActionRecommendation.BLOCK_REVIEW)
    }
}
