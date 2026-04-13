package app.myphonecheck.mobile.feature.decisionui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.myphonecheck.mobile.core.model.DecisionResult
import app.myphonecheck.mobile.core.util.DecisionReasoningFormatter
import app.myphonecheck.mobile.core.util.DecisionReasoningFormatter.Lang
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
    val lang = when (LocalConfiguration.current.locales[0]?.language) {
        "ko" -> Lang.KO
        else -> Lang.EN
    }
    val phaseLine = when {
        searchPending && lang == Lang.KO -> "확정 분석 중 (웹·글로벌 데이터 반영 대기)"
        searchPending -> "Final analysis in progress (web/global data pending)"
        lang == Lang.KO -> "확정 완료"
        else -> "Final analysis complete"
    }
    val immediateLine = when (lang) {
        Lang.KO -> "즉시 판단: 아래 요약·근거는 현재 수집된 증거 기준입니다."
        Lang.EN -> "Immediate view: based on evidence collected so far."
    }

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
            text = when (lang) {
                Lang.KO -> "위험 라벨: ${DecisionReasoningFormatter.riskTriLabel(result.riskLevel, lang)} · 신뢰도 ${DecisionReasoningFormatter.confidencePercent(result.confidence)}%"
                Lang.EN -> "Risk: ${DecisionReasoningFormatter.riskTriLabel(result.riskLevel, lang)} · ${DecisionReasoningFormatter.confidencePercent(result.confidence)}% confidence"
            },
            color = MyPhoneCheckTheme.colors.textPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(10.dp))

        val sectionTitles = when (lang) {
            Lang.KO -> listOf("신고 이력", "패턴 분석 결과", "사용자 행동 이력", "검색 결과 요약")
            Lang.EN -> listOf(
                "Report / complaint signals",
                "Pattern analysis",
                "User behavior on device",
                "Search summary",
            )
        }
        val bodies = DecisionReasoningFormatter.sectionBodiesInOrder(result, lang)
        sectionTitles.zip(bodies).forEach { (title, body) ->
            section(title, body)
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = when (lang) {
                Lang.KO -> "판단 근거"
                Lang.EN -> "Judgment basis"
            },
            color = MyPhoneCheckTheme.colors.textPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = DecisionReasoningFormatter.judgmentBasisMultiline(result, lang),
            color = MyPhoneCheckTheme.colors.textSecondary,
            fontSize = 11.sp,
            lineHeight = 16.sp,
        )

        if (DecisionReasoningFormatter.useGlobalDataBanner(result)) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = when (lang) {
                    Lang.KO -> "글로벌 데이터 기반 분석"
                    else -> "Analysis uses global web/community data"
                },
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
