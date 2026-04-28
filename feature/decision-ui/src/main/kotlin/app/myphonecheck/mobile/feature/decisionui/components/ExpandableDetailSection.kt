package app.myphonecheck.mobile.feature.decisionui.components

import android.content.res.Resources
import android.os.Build
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.myphonecheck.mobile.core.model.DeviceEvidence
import app.myphonecheck.mobile.core.model.SearchEvidence
import app.myphonecheck.mobile.feature.decisionui.R
import app.myphonecheck.mobile.feature.decisionui.preview.PreviewData
import app.myphonecheck.mobile.feature.decisionui.theme.MyPhoneCheckTheme
import java.text.SimpleDateFormat
import java.util.Date

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
                color = MyPhoneCheckTheme.colors.border,
                shape = RoundedCornerShape(8.dp),
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded.value = !isExpanded.value }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.decision_detail_header),
                color = MyPhoneCheckTheme.colors.textPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
            )
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = "Toggle expand",
                tint = MyPhoneCheckTheme.colors.textSecondary,
                modifier = Modifier.rotate(if (isExpanded.value) 180f else 0f),
            )
        }

        AnimatedVisibility(
            visible = isExpanded.value,
            enter = expandVertically(),
            exit = shrinkVertically(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MyPhoneCheckTheme.colors.cardBackground)
                    .padding(12.dp),
            ) {
                DeviceEvidenceDetail(deviceEvidence)

                if (searchEvidence != null && !searchEvidence.isEmpty) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = MyPhoneCheckTheme.colors.divider)
                    Spacer(modifier = Modifier.height(12.dp))
                    SearchEvidenceDetail(searchEvidence)
                }

                if (searchPending) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.decision_web_search_pending),
                        color = MyPhoneCheckTheme.colors.textSecondary,
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
    val resources = LocalContext.current.resources

    Text(
        text = stringResource(R.string.decision_device_history),
        color = MyPhoneCheckTheme.colors.textPrimary,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 8.dp),
    )

    if (device == null) {
        DetailRow(
            stringResource(R.string.detail_label_status),
            stringResource(R.string.detail_no_device_history),
        )
        return
    }

    val savedValue = if (device.isSavedContact) {
        device.contactName?.let { stringResource(R.string.detail_saved_named, it) }
            ?: stringResource(R.string.detail_saved)
    } else {
        stringResource(R.string.detail_not_saved)
    }
    DetailRow(stringResource(R.string.detail_label_contact_saved), savedValue)

    if (device.outgoingCount > 0) {
        DetailRow(
            stringResource(R.string.detail_label_outgoing),
            formatCallsRow(resources, device.outgoingCount, device.lastOutgoingAt),
        )
    }
    if (device.incomingCount > 0) {
        DetailRow(
            stringResource(R.string.detail_label_incoming),
            formatCallsRow(resources, device.incomingCount, device.lastIncomingAt),
        )
    }
    if (device.missedCount > 0) {
        DetailRow(
            stringResource(R.string.detail_label_missed),
            formatCallsRow(resources, device.missedCount, device.lastMissedAt),
        )
    }
    if (device.rejectedCount > 0) {
        DetailRow(
            stringResource(R.string.detail_label_rejected),
            formatCallsRow(resources, device.rejectedCount, device.lastRejectedAt),
        )
    }
    if (device.connectedCount > 0) {
        DetailRow(stringResource(R.string.detail_label_connected), stringResource(R.string.detail_calls_fmt, device.connectedCount))
        DetailRow(stringResource(R.string.detail_total_duration_sec), stringResource(R.string.detail_sec_fmt, device.totalDurationSec))
        DetailRow(stringResource(R.string.detail_avg_duration_sec), stringResource(R.string.detail_sec_fmt, device.avgDurationSec))
    }
    if (device.shortCallCount > 0) {
        DetailRow(stringResource(R.string.detail_short_calls), stringResource(R.string.detail_calls_fmt, device.shortCallCount))
    }
    if (device.longCallCount > 0) {
        DetailRow(stringResource(R.string.detail_long_calls), stringResource(R.string.detail_calls_fmt, device.longCallCount))
    }
    if (device.smsExists) {
        val smsVal = device.smsLastAt?.let {
            stringResource(R.string.detail_sms_yes_last, formatTimestamp(resources, it))
        } ?: stringResource(R.string.detail_sms_yes)
        DetailRow(stringResource(R.string.detail_sms), smsVal)
    }
    device.recentDaysContact?.let {
        DetailRow(
            stringResource(R.string.detail_last_contact_days),
            stringResource(R.string.detail_days_ago_fmt, it),
        )
    }
}

@Composable
private fun SearchEvidenceDetail(search: SearchEvidence) {
    Text(
        text = stringResource(R.string.decision_web_search_result),
        color = MyPhoneCheckTheme.colors.textPrimary,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 8.dp),
    )

    search.recent30dSearchIntensity?.let {
        DetailRow("", stringResource(R.string.detail_search_30d_fmt, it))
    }
    search.recent90dSearchIntensity?.let {
        DetailRow("", stringResource(R.string.detail_search_90d_fmt, it))
    }
    DetailRow(stringResource(R.string.detail_search_trend), search.searchTrend.name)

    if (search.signalSummaries.isNotEmpty()) {
        Text(
            text = stringResource(R.string.decision_signal_summary),
            color = MyPhoneCheckTheme.colors.textPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
        )
        search.signalSummaries.forEach { s ->
            val countPart = if (s.resultCount > 0) {
                stringResource(R.string.detail_signal_count_fmt, s.resultCount)
            } else {
                ""
            }
            val typePart = s.signalType.orEmpty()
            val tail = listOf(countPart, typePart)
                .filter { it.isNotBlank() }
                .joinToString(" · ")
                .ifEmpty { stringResource(R.string.detail_em_dash) }
            DetailRow(s.signalDescription, tail)
        }
    }

    if (search.keywordClusters.isNotEmpty()) {
        DetailRow(
            stringResource(R.string.detail_keyword_clusters),
            search.keywordClusters.take(5).joinToString(", "),
        )
    }
    if (search.repeatedEntities.isNotEmpty()) {
        DetailRow(
            stringResource(R.string.detail_repeated_entities),
            search.repeatedEntities.take(3).joinToString(", "),
        )
    }
    if (search.topSnippets.isNotEmpty()) {
        DetailRow(stringResource(R.string.detail_search_snippet), search.topSnippets.first())
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
            color = MyPhoneCheckTheme.colors.textSecondary,
            fontSize = 11.sp,
            modifier = Modifier.width(100.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            color = MyPhoneCheckTheme.colors.textTertiary,
            fontSize = 11.sp,
            modifier = Modifier.weight(1f),
        )
    }
}

/** Uses configuration locale (no manual ko/en branching). */
private fun formatTimestamp(resources: Resources, timestamp: Long): String {
    return try {
        val date = Date(timestamp)
        val locale = if (Build.VERSION.SDK_INT >= 24) {
            resources.configuration.locales[0]
        } else {
            @Suppress("DEPRECATION")
            resources.configuration.locale
        }
        SimpleDateFormat("yyyy.MM.dd HH:mm", locale).format(date)
    } catch (_: Exception) {
        resources.getString(app.myphonecheck.mobile.core.util.R.string.reasoning_timestamp_unknown)
    }
}

@Composable
private fun formatCallsRow(resources: Resources, count: Int, lastAt: Long?): String {
    val last = lastAt?.let { formatTimestamp(resources, it) }
    return if (last != null) {
        stringResource(R.string.detail_calls_last_fmt, count, last)
    } else {
        stringResource(R.string.detail_calls_fmt, count)
    }
}

@Preview(showBackground = true)
@Composable
private fun ExpandableDetailSectionPreview() {
    MyPhoneCheckTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ExpandableDetailSection(
                deviceEvidence = PreviewData.deliveryDevice,
                searchEvidence = PreviewData.deliverySearch,
            )
        }
    }
}
