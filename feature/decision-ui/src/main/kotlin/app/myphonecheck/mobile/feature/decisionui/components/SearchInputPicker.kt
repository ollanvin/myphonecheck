package app.myphonecheck.mobile.feature.decisionui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import app.myphonecheck.mobile.core.globalengine.search.SearchInput

/**
 * 다중 SearchInput 후보 중 사용자 선택 (v2.5.0 §direct-search 정합).
 *
 * MessageCheck / PushCheck 등 본문 파싱으로 다중 후보가 있는 Surface에서
 * SimBasedAiMenu 진입 전에 표시.
 */
@Composable
fun SearchInputPicker(
    candidates: List<SearchInput>,
    onSelect: (SearchInput) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Search what?") },
        text = {
            Column {
                candidates.forEach { input ->
                    InputItem(input, onClick = { onSelect(input) })
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}

@Composable
private fun InputItem(input: SearchInput, onClick: () -> Unit) {
    val (icon, label) = iconAndLabelFor(input)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null)
        Spacer(Modifier.width(16.dp))
        Text(label, style = MaterialTheme.typography.bodyLarge)
    }
}

// Material Icons Extended 미포함 — Search 단일 아이콘 사용 (Stage 3-005-REV).
private fun iconAndLabelFor(input: SearchInput): Pair<ImageVector, String> = when (input) {
    is SearchInput.PhoneNumber -> Icons.Default.Search to "Number: ${input.value}"
    is SearchInput.Url -> Icons.Default.Search to "URL: ${input.value.take(40)}"
    is SearchInput.MessageBody -> Icons.Default.Search to "Body: ${input.text.take(40)}"
    is SearchInput.AppPackage -> Icons.Default.Search to "App: ${input.packageName}"
}
