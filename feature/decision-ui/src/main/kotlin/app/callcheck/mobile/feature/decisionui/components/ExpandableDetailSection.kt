package app.callcheck.mobile.feature.decisionui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.callcheck.mobile.core.model.DeviceEvidence
import app.callcheck.mobile.core.model.SearchEvidence
import app.callcheck.mobile.feature.decisionui.preview.PreviewData
import app.callcheck.mobile.feature.decisionui.theme.CallCheckTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Collapsible section showing granular device + search evidence breakdown.
 */
@Composable
fun ExpandableDetailSection(
    deviceEvidence: DeviceEvidence?,
    searchEvidence: SearchEvidence?,
    searchPending: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val isExpanded = remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = CallCheckTheme.colors.border,
                shape = RoundedCornerShape(8.dp),
            ),
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded.value = !isExpanded.value }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "상세 정보",
                color = CallCheckTheme.colors.textPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
            )
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = "Toggle expand",
                tint = CallCheckTheme.colors.textSecondary,
                modifier = Modifier.rotate(if (isExpanded.value) 180f else 0f),
            )
        }

        // Expandable content
        AnimatedVisibility(
            visible = isExpanded.value,
            enter = expandVertically(),
            exit = shrinkVertically(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CallCheckTheme.colors.cardBackground)
                    .padding(12.dp),
            ) {
                // Device evidence section
                DeviceEvidenceDetail(deviceEvidence)

                // Search evidence section
                if (searchEvidence != null && !searchEvidence.isEmpty) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = CallCheckTheme.colors.divider)
                    Spacer(modifier = Modifier.height(12.dp))
                    SearchEvidenceDetail(searchEvidence)
                }

                if (searchPending) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "웹 검색 진행 중...",
                        color = CallCheckTheme.colors.textSecondary,
                        fontSize = 11.sp,
                        fontStyle = FontStyle.Italic,
                    )
                }
            }
        }
    }
}

@Composable
private fun DeviceEvidenceDetail(device: DeviceEvidence?) {
    Text(
        text = "디바이스 이력",
        color = CallCheckTheme.colors.textPrimary,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 8.dp),
    )

    if (device == null) {
        DetailRow("상태", "기기 기록 없음")
        return
    }

    DetailRow(
        "저장 여부",
        if (device.isSavedContact) {
            device.contactName?.let { "저장됨 ($it)" } ?: "저장됨"
        } else {
            "저장되지 않음"
        },
    )

    if (device.outgoingCount > 0) {
        DetailRow("발신", "${device.outgoingCount}회${device.lastOutgoingAt?.let { " (마지막: ${formatTimestamp(it)})" } ?: ""}")
    }
    if (device.incomingCount > 0) {
        DetailRow("수신", "${device.incomingCount}회${device.lastIncomingAt?.let { " (마지막: ${formatTimestamp(it)})" } ?: ""}")
    }
    if (device.missedCount > 0) {
        DetailRow("부재중", "${device.missedCount}회${device.lastMissedAt?.let { " (마지막: ${formatTimestamp(it)})" } ?: ""}")
    }
    if (device.rejectedCount > 0) {
        DetailRow("거절", "${device.rejectedCount}회${device.lastRejectedAt?.let { " (마지막: ${formatTimestamp(it)})" } ?: ""}")
    }
    if (device.connectedCount > 0) {
        DetailRow("실제 통화", "${device.connectedCount}회")
        DetailRow("총 통화시간", "${device.totalDurationSec}초")
        DetailRow("평균 통화시간", "${device.avgDurationSec}초")
    }
    if (device.shortCallCount > 0) {
        DetailRow("짧은 통화 (<10s)", "${device.shortCallCount}회")
    }
    if (device.longCallCount > 0) {
        DetailRow("긴 통화 (>60s)", "${device.longCallCount}회")
    }
    if (device.smsExists) {
        DetailRow("문자", "있음${device.smsLastAt?.let { " (마지막: ${formatTimestamp(it)})" } ?: ""}")
    }
    device.recentDaysContact?.let {
        DetailRow("마지막 연락", "${it}일 전")
    }
}

@Composable
private fun SearchEvidenceDetail(search: SearchEvidence) {
    Text(
        text = "웹 검색 결과",
        color = CallCheckTheme.colors.textPrimary,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 8.dp),
    )

    search.recent30dSearchIntensity?.let {
        DetailRow("30일 검색량", "${it}건")
    }
    search.recent90dSearchIntensity?.let {
        DetailRow("90일 검색량", "${it}건")
    }
    DetailRow("검색 추세", search.searchTrend.name)

    if (search.keywordClusters.isNotEmpty()) {
        DetailRow("키워드", search.keywordClusters.take(5).joinToString(", "))
    }
    if (search.repeatedEntities.isNotEmpty()) {
        DetailRow("반복 엔터티", search.repeatedEntities.take(3).joinToString(", "))
    }
    if (search.topSnippets.isNotEmpty()) {
        DetailRow("검색 스니펫", search.topSnippets.first())
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            color = CallCheckTheme.colors.textSecondary,
            fontSize = 11.sp,
            modifier = Modifier.width(100.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            color = CallCheckTheme.colors.textTertiary,
            fontSize = 11.sp,
            modifier = Modifier.weight(1f),
        )
    }
}

private fun formatTimestamp(timestamp: Long): String {
    return try {
        val date = Date(timestamp)
        val formatter = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.KOREA)
        formatter.format(date)
    } catch (e: Exception) {
        "알 수 없음"
    }
}

@Preview(showBackground = true)
@Composable
private fun ExpandableDetailSectionPreview() {
    CallCheckTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ExpandableDetailSection(
                deviceEvidence = PreviewData.deliveryDevice,
                searchEvidence = PreviewData.deliverySearch,
            )
        }
    }
}
