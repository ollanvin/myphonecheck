package app.myphonecheck.mobile.feature.decisionui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.myphonecheck.mobile.core.model.DecisionResult
import app.myphonecheck.mobile.core.util.DecisionReasoningFormatter
import app.myphonecheck.mobile.core.util.R as UtilR
import app.myphonecheck.mobile.feature.decisionui.theme.MyPhoneCheckTheme

/**
 * 엔진·증거 전체를 접지 않고 노출 (오버레이와 동등한 정보 축).
 */
@Composable
fun FullEngineReasoningSection(
    result: DecisionResult,
    searchPending: Boolean,
    modifier: Modifier = Modifier,
) {
    val resources = LocalContext.current.resources

    val phaseLine = if (searchPending) {
        stringResource(UtilR.string.reasoning_overlay_phase_pending)
    } else {
        stringResource(UtilR.string.reasoning_overlay_phase_complete)
    }
    val immediateLine = stringResource(UtilR.string.reasoning_overlay_immediate)

    val riskLabel = DecisionReasoningFormatter.riskTriLabel(resources, result.riskLevel)
    val pct = DecisionReasoningFormatter.confidencePercent(result.confidence)
    val riskConfidenceLine = stringResource(UtilR.string.reasoning_overlay_risk_confidence, riskLabel, pct)

    val sectionTitles = listOf(
        stringResource(UtilR.string.reasoning_overlay_section_report),
        stringResource(UtilR.string.reasoning_overlay_section_pattern),
        stringResource(UtilR.string.reasoning_overlay_section_behavior),
        stringResource(UtilR.string.reasoning_overlay_section_search),
    )
    val bodies = DecisionReasoningFormatter.sectionBodiesInOrder(resources, result)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
    ) {
        Text(
            text = immediateLine,
            color = MyPhoneCheckTheme.colors.textSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = phaseLine,
            color = MyPhoneCheckTheme.colors.textPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = riskConfidenceLine,
            color = MyPhoneCheckTheme.colors.textPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(10.dp))

        sectionTitles.zip(bodies).forEach { (title, body) ->
            section(title, body)
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = stringResource(UtilR.string.reasoning_overlay_judgment_basis),
            color = MyPhoneCheckTheme.colors.textPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = DecisionReasoningFormatter.judgmentBasisMultiline(resources, result),
            color = MyPhoneCheckTheme.colors.textSecondary,
            fontSize = 11.sp,
            lineHeight = 16.sp,
        )

        if (DecisionReasoningFormatter.useGlobalDataBanner(result)) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(UtilR.string.reasoning_overlay_global_banner),
                color = MyPhoneCheckTheme.colors.textTertiary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun section(title: String, body: String) {
    Text(
        text = title,
        color = MyPhoneCheckTheme.colors.textPrimary,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(top = 8.dp),
    )
    Text(
        text = body,
        color = MyPhoneCheckTheme.colors.textSecondary,
        fontSize = 11.sp,
        lineHeight = 15.sp,
    )
}
