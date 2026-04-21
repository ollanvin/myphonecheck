package app.myphonecheck.mobile.feature.decisionui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import app.myphonecheck.mobile.feature.decisionui.R
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.myphonecheck.mobile.core.model.RingSystem
import app.myphonecheck.mobile.core.model.ActionRecommendation
import app.myphonecheck.mobile.core.model.RiskLevel
import app.myphonecheck.mobile.feature.decisionui.ring.DecisionRing
import app.myphonecheck.mobile.feature.decisionui.ring.DecisionRingDefaults
import app.myphonecheck.mobile.feature.decisionui.ring.RingState
import app.myphonecheck.mobile.feature.decisionui.theme.MyPhoneCheckTheme

/**
 * 위젯 컨텐츠 — 최근 판단 요약.
 *
 * 설계 원칙:
 * - "정보 앱 금지 → 판단 요약만"
 * - 미니 Ring (24dp) + 번호 + 결과
 * - RingSystem 색상 체계 동일 적용
 *
 * 구성:
 * ┌──────────────────────────┐
 * │ MyPhoneCheck                │
 * │ ○ 02-555-0199  위험 높음  │
 * │ ○ 010-1234     안전 추정  │
 * │ ○ 1588-1234    주의      │
 * │                          │
 * │ 판단을 돕습니다           │
 * └──────────────────────────┘
 *
 * 이 Composable은 Glance 위젯의 내부 컨텐츠로도,
 * 앱 내부 미니 위젯 카드로도 재사용 가능.
 *
 * @param items 최근 판단 아이템 리스트
 */
@Composable
fun RecentDecisionWidgetContent(
    items: List<WidgetDecisionItem>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MyPhoneCheckTheme.colors.cardBackground,
                shape = RoundedCornerShape(12.dp),
            )
            .padding(12.dp),
    ) {
        // 헤더
        Text(
            text = "MyPhoneCheck",
            color = MyPhoneCheckTheme.colors.textPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (items.isEmpty()) {
            // 판단 이력 없음
            Text(
                text = stringResource(R.string.decision_no_history),
                color = MyPhoneCheckTheme.colors.textTertiary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
            )
        } else {
            // 최근 판단 리스트 (최대 5개)
            items.take(5).forEach { item ->
                WidgetDecisionRow(item = item)
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // 면책 문구
        Text(
            text = RingSystem.DISCLAIMER_KO,
            color = MyPhoneCheckTheme.colors.textTertiary,
            fontSize = 8.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/**
 * 위젯 내 개별 판단 행.
 * 미니 Ring(24dp) + 번호 + 상태 라벨.
 */
@Composable
private fun WidgetDecisionRow(
    item: WidgetDecisionItem,
    modifier: Modifier = Modifier,
) {
    val ringState = RingState.fromAction(item.action)
    val stateColor = Color(RingSystem.color(item.action))

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
    ) {
        // 미니 Ring (24dp — History 사이즈)
        DecisionRing(
            state = ringState,
            size = DecisionRingDefaults.HISTORY_ITEM_SIZE,
        )

        Spacer(modifier = Modifier.width(8.dp))

        // 전화번호
        Text(
            text = item.phoneNumber,
            color = MyPhoneCheckTheme.colors.textPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )

        Spacer(modifier = Modifier.width(8.dp))

        // 상태 라벨 (RingSystem 색상)
        Text(
            text = RingSystem.labelKo(item.action),
            color = stateColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

/**
 * 위젯에 표시할 판단 아이템.
 */
data class WidgetDecisionItem(
    val phoneNumber: String,
    val riskLevel: RiskLevel,
    val action: ActionRecommendation,
    val summary: String,
    val timestampMs: Long,
)

// ============================================================
// Preview
// ============================================================

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0F0F0F,
    widthDp = 300,
    heightDp = 250,
    name = "Widget - Recent Decisions",
)
@Composable
private fun RecentDecisionWidgetPreview() {
    MyPhoneCheckTheme {
        RecentDecisionWidgetContent(
            items = listOf(
                WidgetDecisionItem("02-555-0199", RiskLevel.HIGH, ActionRecommendation.REJECT, "보이스피싱 의심", 0L),
                WidgetDecisionItem("010-1234-5678", RiskLevel.LOW, ActionRecommendation.ANSWER, "저장된 연락처", 0L),
                WidgetDecisionItem("1588-1234", RiskLevel.MEDIUM, ActionRecommendation.ANSWER_WITH_CAUTION, "광고/영업 의심", 0L),
                WidgetDecisionItem("070-8888-9999", RiskLevel.UNKNOWN, ActionRecommendation.HOLD, "판단 근거 부족", 0L),
            ),
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF0F0F0F,
    widthDp = 300,
    heightDp = 180,
    name = "Widget - Empty",
)
@Composable
private fun RecentDecisionWidgetEmptyPreview() {
    MyPhoneCheckTheme {
        RecentDecisionWidgetContent(items = emptyList())
    }
}
