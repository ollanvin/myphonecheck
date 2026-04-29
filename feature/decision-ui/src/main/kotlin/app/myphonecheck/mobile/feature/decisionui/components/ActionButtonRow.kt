package app.myphonecheck.mobile.feature.decisionui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.myphonecheck.mobile.core.globalengine.search.SearchInput
import app.myphonecheck.mobile.feature.decisionui.R

/**
 * 3액션 버튼 row (v2.6.0 §11 정합).
 *
 * - [차단 / 해제 toggle] — Block 액션. 사용자 명시 결정만 (자동 차단 영구 미포함).
 * - [태그] — Tag 액션. TagInputDialog로 자유 텍스트 라벨.
 * - [🔍 검색] — Direct Search 액션. SimBasedAiMenu trigger.
 *
 * Material Icons Extended 미포함 — Search 단일 아이콘 사용 (Block/Tag는 텍스트 라벨).
 */
@Composable
fun ActionButtonRow(
    @Suppress("UNUSED_PARAMETER") input: SearchInput,
    isBlocked: Boolean,
    currentTag: String?,
    onBlockToggle: (Boolean) -> Unit,
    onTagSet: (String) -> Unit,
    onDirectSearchTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showTagDialog by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth().padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        // [차단 / 해제] toggle
        OutlinedButton(
            onClick = { onBlockToggle(!isBlocked) },
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = if (isBlocked) MaterialTheme.colorScheme.error
                               else MaterialTheme.colorScheme.onSurface,
            ),
        ) {
            Text(
                text = if (isBlocked) stringResource(R.string.action_unblock)
                       else stringResource(R.string.action_block),
            )
        }

        // [태그]
        OutlinedButton(onClick = { showTagDialog = true }) {
            Text(
                text = if (currentTag != null) stringResource(R.string.action_tag) + ": $currentTag"
                       else stringResource(R.string.action_tag),
            )
        }

        // [🔍 검색]
        OutlinedButton(onClick = onDirectSearchTap) {
            Icon(Icons.Default.Search, contentDescription = null)
            Spacer(Modifier.width(4.dp))
            Text(stringResource(R.string.action_search))
        }
    }

    if (showTagDialog) {
        TagInputDialog(
            initialValue = currentTag.orEmpty(),
            onConfirm = { tag ->
                onTagSet(tag)
                showTagDialog = false
            },
            onDismiss = { showTagDialog = false },
        )
    }
}

@Composable
private fun TagInputDialog(
    initialValue: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var text by remember { mutableStateOf(initialValue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.tag_dialog_title)) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text(stringResource(R.string.tag_dialog_label)) },
                singleLine = true,
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(text.trim()) },
                enabled = text.isNotBlank(),
            ) { Text(stringResource(R.string.tag_dialog_save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.tag_dialog_cancel)) }
        },
    )
}
