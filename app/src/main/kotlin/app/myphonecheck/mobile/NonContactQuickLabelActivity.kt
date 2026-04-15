package app.myphonecheck.mobile

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import app.myphonecheck.mobile.core.model.ActionState
import app.myphonecheck.mobile.core.model.ProductStageFlags
import app.myphonecheck.mobile.core.model.displayLabelKo
import app.myphonecheck.mobile.data.localcache.entity.DetailTagSource
import app.myphonecheck.mobile.data.localcache.entity.NumberProfileBlockState
import app.myphonecheck.mobile.data.localcache.entity.QuickLabel
import app.myphonecheck.mobile.data.localcache.repository.NumberProfileRepository
import app.myphonecheck.mobile.ui.theme.MyPhoneCheckTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "QuickLabelSheet"

@AndroidEntryPoint
class NonContactQuickLabelActivity : ComponentActivity() {

    @Inject
    lateinit var numberProfileRepository: NumberProfileRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val normalizedNumber = intent.getStringExtra(EXTRA_NORMALIZED_NUMBER).orEmpty()
        if (normalizedNumber.isBlank()) {
            finish()
            return
        }

        setContent {
            MyPhoneCheckTheme {
                Surface(color = Color.Transparent) {
                    val snapshot by numberProfileRepository.observeSnapshot(normalizedNumber)
                        .collectAsState(initial = null)
                    val summary = intent.getStringExtra(EXTRA_SUMMARY)
                    val searchStatus = intent.getStringExtra(EXTRA_SEARCH_STATUS)
                    val renderStartedAtMs = remember { System.currentTimeMillis() }

                    QuickLabelBottomSheet(
                        normalizedNumber = normalizedNumber,
                        summary = summary,
                        searchStatus = searchStatus,
                        selectedLabels = snapshot?.quickLabels.orEmpty(),
                        actionState = snapshot?.actionState,
                        renderStartedAtMs = renderStartedAtMs,
                        onDismiss = { finish() },
                        onSelectQuickLabel = { label ->
                            lifecycleScope.launch {
                                Log.i(
                                    TAG,
                                    "quick_label_action latencyMs=${System.currentTimeMillis() - renderStartedAtMs} " +
                                        "action=${label.name} actionStateReused=${snapshot?.actionState != ActionState.NONE}",
                                )
                                val wasSelected = snapshot?.quickLabels?.contains(label) == true
                                numberProfileRepository.toggleQuickLabel(normalizedNumber, label)
                                if (label == QuickLabel.DO_NOT_BLOCK) {
                                    numberProfileRepository.setBlockState(
                                        normalizedNumber,
                                        if (wasSelected) {
                                            NumberProfileBlockState.NONE
                                        } else {
                                            NumberProfileBlockState.DO_NOT_BLOCK
                                        },
                                    )
                                }
                                finish()
                            }
                        },
                        onAddDetailTag = { tagName ->
                            lifecycleScope.launch {
                                Log.i(
                                    TAG,
                                    "detail_tag_action latencyMs=${System.currentTimeMillis() - renderStartedAtMs} " +
                                        "actionStateReused=${snapshot?.actionState != ActionState.NONE}",
                                )
                                numberProfileRepository.addDetailTag(
                                    normalizedNumber = normalizedNumber,
                                    tagName = tagName,
                                    source = DetailTagSource.USER,
                                )
                                finish()
                            }
                        },
                    )
                }
            }
        }
    }

    companion object {
        const val EXTRA_NORMALIZED_NUMBER = "extra_normalized_number"
        const val EXTRA_SUMMARY = "extra_summary"
        const val EXTRA_SEARCH_STATUS = "extra_search_status"
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun QuickLabelBottomSheet(
    normalizedNumber: String,
    summary: String?,
    searchStatus: String?,
    selectedLabels: Set<QuickLabel>,
    actionState: ActionState?,
    renderStartedAtMs: Long,
    onDismiss: () -> Unit,
    onSelectQuickLabel: (QuickLabel) -> Unit,
    onAddDetailTag: (String) -> Unit,
) {
    var showTagInput by remember { mutableStateOf(false) }
    var detailTagText by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF102033),
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF16293B)),
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Remember this number",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = normalizedNumber,
                    fontSize = 13.sp,
                    color = Color(0xFF90A4AE),
                )
                summary?.takeIf { it.isNotBlank() }?.let {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Summary  $it",
                        fontSize = 12.sp,
                        color = Color(0xFFFFCC80),
                    )
                }
                searchStatus?.takeIf { it.isNotBlank() }?.let {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Search status  $it",
                        fontSize = 12.sp,
                        color = Color(0xFF81D4FA),
                    )
                }
                actionState?.displayLabelKo()?.let {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Action state  $it",
                        fontSize = 12.sp,
                        color = Color(0xFFA5D6A7),
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    QuickLabel.entries.forEach { label ->
                        FilterChip(
                            selected = selectedLabels.contains(label),
                            onClick = { onSelectQuickLabel(label) },
                            label = { Text(label.displayName) },
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                TextButton(onClick = { showTagInput = !showTagInput }) {
                    Text("Add detail tag", color = Color(0xFF81D4FA))
                }
                if (showTagInput) {
                    OutlinedTextField(
                        value = detailTagText,
                        onValueChange = { detailTagText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("Example: client, family, delivery")
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF4FC3F7),
                            unfocusedBorderColor = Color(0xFF455A64),
                        ),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            Log.i(
                                TAG,
                                "detail_tag_button latencyMs=${System.currentTimeMillis() - renderStartedAtMs} " +
                                    "actionStateReused=${actionState != ActionState.NONE}",
                            )
                            onAddDetailTag(detailTagText)
                        },
                        enabled = detailTagText.isNotBlank(),
                    ) {
                        Text("Save tag")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Free: ${ProductStageFlags.SIMPLE_LABEL.name}  Premium: ${ProductStageFlags.DO_NOT_MISS_BEHAVIOR.name}",
                    fontSize = 11.sp,
                    color = Color(0xFF78909C),
                )
            }
        }
    }
}
