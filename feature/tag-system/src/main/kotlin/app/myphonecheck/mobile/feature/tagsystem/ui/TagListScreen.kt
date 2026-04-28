package app.myphonecheck.mobile.feature.tagsystem.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import app.myphonecheck.mobile.core.globalengine.decision.TagPriority
import app.myphonecheck.mobile.core.globalengine.decision.TagRecord
import app.myphonecheck.mobile.feature.tagsystem.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val ScreenBg = Color(0xFF0D1B2A)
private val CardBg = Color(0xFF1B2838)
private val Accent = Color(0xFF4FC3F7)
private val TextSubtle = Color(0xFFB0BEC5)
private val Suspicious = Color(0xFFEF5350)
private val Pending = Color(0xFFFFB74D)
private val RemindMe = Color(0xFFFFEE58)
private val Archive = Color(0xFF9E9E9E)

@Composable
fun TagListRoute(
    onBack: () -> Unit,
    viewModel: TagListViewModel = hiltViewModel(),
) {
    val tags by viewModel.tags.collectAsState()
    TagListScreen(tags = tags, onBack = onBack, onDelete = viewModel::delete)
}

@Composable
private fun TagListScreen(
    tags: List<TagRecord>,
    onBack: () -> Unit,
    onDelete: (TagRecord) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize().background(ScreenBg)) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                }
                Text(
                    text = stringResource(R.string.tag_list_title),
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            PromiseCard()

            Spacer(modifier = Modifier.height(8.dp))

            if (tags.isEmpty()) {
                EmptyState()
            } else {
                TagGroupedList(tags, onDelete)
            }
        }
    }
}

@Composable
private fun PromiseCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF22364A)),
        shape = RoundedCornerShape(12.dp),
    ) {
        Text(
            text = stringResource(R.string.tag_user_promise),
            color = Color.White,
            fontSize = 11.sp,
            modifier = Modifier.padding(12.dp),
        )
    }
}

@Composable
private fun EmptyState() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = stringResource(R.string.tag_empty_title),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.tag_empty_desc),
                color = TextSubtle,
                fontSize = 12.sp,
            )
        }
    }
}

@Composable
private fun TagGroupedList(tags: List<TagRecord>, onDelete: (TagRecord) -> Unit) {
    val grouped = tags.groupBy { it.priority }
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        TagPriority.values().forEach { priority ->
            val list = grouped[priority].orEmpty()
            if (list.isEmpty()) return@forEach
            item(key = "header-${priority.name}") {
                SectionHeader(priority = priority, count = list.size)
            }
            items(list, key = { it.type.name + "|" + it.key }) { record ->
                TagRow(record, onDelete)
            }
        }
    }
}

@Composable
private fun SectionHeader(priority: TagPriority, count: Int) {
    val (label, color) = labelAndColor(priority)
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label, color = color, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Text(text = stringResource(R.string.tag_seen_count, count), color = TextSubtle, fontSize = 11.sp)
    }
}

@Composable
private fun TagRow(record: TagRecord, onDelete: (TagRecord) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = record.key,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = record.tagText,
                    color = Accent,
                    fontSize = 12.sp,
                )
                Text(
                    text = lastSeenLabel(record),
                    color = TextSubtle,
                    fontSize = 10.sp,
                )
            }
            TextButton(onClick = { onDelete(record) }) {
                Text(stringResource(R.string.tag_delete), color = Color(0xFFEF5350))
            }
        }
    }
}

@Composable
private fun lastSeenLabel(record: TagRecord): String {
    val millis = record.lastSeenMillis ?: return stringResource(R.string.tag_never_seen)
    val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return stringResource(R.string.tag_last_seen, fmt.format(Date(millis)))
}

@Composable
private fun labelAndColor(priority: TagPriority): Pair<String, Color> = when (priority) {
    TagPriority.SUSPICIOUS -> stringResource(R.string.tag_section_suspicious) to Suspicious
    TagPriority.PENDING -> stringResource(R.string.tag_section_pending) to Pending
    TagPriority.REMIND_ME -> stringResource(R.string.tag_section_remind_me) to RemindMe
    TagPriority.ARCHIVE -> stringResource(R.string.tag_section_archive) to Archive
}
