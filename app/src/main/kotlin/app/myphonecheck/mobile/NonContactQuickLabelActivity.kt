package app.myphonecheck.mobile

import android.os.Bundle
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
import androidx.compose.material3.MaterialTheme
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
import app.myphonecheck.mobile.data.localcache.entity.DetailTagSource
import app.myphonecheck.mobile.data.localcache.entity.NumberProfileBlockState
import app.myphonecheck.mobile.data.localcache.entity.QuickLabel
import app.myphonecheck.mobile.data.localcache.repository.NumberProfileRepository
import app.myphonecheck.mobile.ui.theme.MyPhoneCheckTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

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
                    QuickLabelBottomSheet(
                        normalizedNumber = normalizedNumber,
                        selectedLabels = snapshot?.quickLabels.orEmpty(),
                        onDismiss = { finish() },
                        onSelectQuickLabel = { label ->
                            lifecycleScope.launch {
                                numberProfileRepository.toggleQuickLabel(normalizedNumber, label)
                                if (label == QuickLabel.DO_NOT_BLOCK) {
                                    numberProfileRepository.setBlockState(
                                        normalizedNumber,
                                        NumberProfileBlockState.DO_NOT_BLOCK,
                                    )
                                }
                                finish()
                            }
                        },
                        onAddDetailTag = { tagName ->
                            lifecycleScope.launch {
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
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun QuickLabelBottomSheet(
    normalizedNumber: String,
    selectedLabels: Set<QuickLabel>,
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
                    text = "저장 없이 기억하기",
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
                Spacer(modifier = Modifier.height(16.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    QuickLabel.entries.forEach { label ->
                        FilterChip(
                            selected = selectedLabels.contains(label),
                            onClick = { onSelectQuickLabel(label) },
                            label = {
                                Text(label.displayName)
                            },
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                TextButton(onClick = { showTagInput = !showTagInput }) {
                    Text("상세 태그 추가", color = Color(0xFF81D4FA))
                }
                if (showTagInput) {
                    OutlinedTextField(
                        value = detailTagText,
                        onValueChange = { detailTagText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("예: 발주, 재확인, 저녁 콜백")
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
                        onClick = { onAddDetailTag(detailTagText) },
                        enabled = detailTagText.isNotBlank(),
                    ) {
                        Text("태그 저장")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
