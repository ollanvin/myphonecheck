package app.myphonecheck.mobile.feature.messagecheck.ui

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import app.myphonecheck.core.common.risk.RiskTier
import app.myphonecheck.mobile.core.globalengine.parsing.message.MessageCategory
import app.myphonecheck.mobile.core.globalengine.parsing.message.SenderProfile
import app.myphonecheck.mobile.core.globalengine.search.SearchInput
import app.myphonecheck.mobile.core.globalengine.simcontext.SimContext
import app.myphonecheck.mobile.feature.decisionui.components.DirectSearchAddon
import app.myphonecheck.mobile.feature.decisionui.components.DirectSearchHandler
import app.myphonecheck.mobile.feature.decisionui.components.MultiInputDirectSearchAddon
import app.myphonecheck.mobile.feature.decisionui.components.SearchInputExtractor
import app.myphonecheck.mobile.feature.decisionui.components.SurfaceContext
import app.myphonecheck.mobile.feature.messagecheck.R
import app.myphonecheck.mobile.feature.messagecheck.repository.MessageEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val ScreenBg = Color(0xFF0D1B2A)
private val CardBg = Color(0xFF1B2838)
private val Accent = Color(0xFF4FC3F7)
private val Spam = Color(0xFFEF5350)
private val Payment = Color(0xFF66BB6A)
private val TextSubtle = Color(0xFFB0BEC5)

@Composable
fun MessageCheckRoute(
    onBack: () -> Unit,
    viewModel: MessageCheckViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) viewModel.refresh()
    }

    MessageCheckScreen(
        state = state,
        onBack = onBack,
        onRequestPermission = {
            permissionLauncher.launch(Manifest.permission.READ_SMS)
        },
        directSearchHandler = viewModel.directSearchHandler,
        simContext = viewModel.simContext(),
    )
}

@Composable
private fun MessageCheckScreen(
    state: MessageCheckUiState,
    onBack: () -> Unit,
    onRequestPermission: () -> Unit,
    directSearchHandler: DirectSearchHandler? = null,
    simContext: SimContext? = null,
) {
    Box(modifier = Modifier.fillMaxSize().background(ScreenBg)) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                }
                Text(
                    text = stringResource(R.string.message_check_title),
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            when (state) {
                MessageCheckUiState.Loading -> Text(
                    text = stringResource(R.string.message_check_recent_messages),
                    color = TextSubtle,
                    fontSize = 12.sp,
                )
                MessageCheckUiState.PermissionRequired -> PermissionCard(onRequestPermission)
                is MessageCheckUiState.Loaded -> {
                    LoadedContent(state)
                    // v2.5.0 §direct-search-message: 첫 메시지 → 본문 파싱 다중 input
                    val firstEntry = state.entries.firstOrNull()
                    if (firstEntry != null && directSearchHandler != null && simContext != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        val candidates = SearchInputExtractor.fromMessage(
                            senderPhoneNumber = firstEntry.sender,
                            body = firstEntry.bodySnippet,
                            simContext = simContext,
                            surfaceContextLabel = "MESSAGE",
                        )
                        if (candidates.size > 1) {
                            MultiInputDirectSearchAddon(
                                candidates = candidates,
                                tier = RiskTier.Unknown,
                                surfaceContext = SurfaceContext.MESSAGE,
                                handler = directSearchHandler,
                            )
                        } else if (candidates.size == 1) {
                            DirectSearchAddon(
                                input = candidates.first(),
                                tier = RiskTier.Unknown,
                                surfaceContext = SurfaceContext.MESSAGE,
                                handler = directSearchHandler,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PermissionCard(onRequest: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = stringResource(R.string.message_check_permission_required),
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = onRequest) {
                Text(stringResource(R.string.message_check_grant_permission))
            }
        }
    }
}

@Composable
private fun LoadedContent(state: MessageCheckUiState.Loaded) {
    if (state.entries.isEmpty() && state.senderInventory.isEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text(
                text = stringResource(R.string.message_check_no_messages),
                color = Color.White,
                modifier = Modifier.padding(20.dp),
                fontSize = 14.sp,
            )
        }
        return
    }
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            Text(
                text = stringResource(R.string.message_check_sender_inventory),
                color = TextSubtle,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
            )
        }
        items(state.senderInventory, key = { it.sender }) { profile ->
            SenderRow(profile)
        }
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.message_check_recent_messages),
                color = TextSubtle,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
            )
        }
        items(state.entries, key = { it.timestampMillis.toString() + it.sender }) { entry ->
            MessageRow(entry)
        }
    }
}

@Composable
private fun SenderRow(profile: SenderProfile) {
    val dominant = profile.dominantCategory()
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
                    text = profile.sender,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = stringResource(R.string.message_check_count_format, profile.count) +
                        if (profile.isShortSender) " · " + stringResource(R.string.message_check_short_sender) else "",
                    color = TextSubtle,
                    fontSize = 11.sp,
                )
            }
            CategoryChip(dominant)
        }
    }
}

@Composable
private fun MessageRow(entry: MessageEntry) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = entry.sender,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                CategoryChip(entry.category)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = entry.bodySnippet,
                color = TextSubtle,
                fontSize = 12.sp,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = formatTimestamp(entry.timestampMillis),
                color = TextSubtle,
                fontSize = 10.sp,
            )
        }
    }
}

@Composable
private fun CategoryChip(category: MessageCategory) {
    val (label, color) = when (category) {
        MessageCategory.PAYMENT_CANDIDATE -> stringResource(R.string.message_check_category_payment) to Payment
        MessageCategory.SPAM_CANDIDATE -> stringResource(R.string.message_check_category_spam) to Spam
        MessageCategory.NOTIFICATION -> stringResource(R.string.message_check_category_notification) to Accent
        MessageCategory.NORMAL -> stringResource(R.string.message_check_category_normal) to TextSubtle
    }
    Text(
        text = label,
        color = color,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
    )
}

private fun formatTimestamp(millis: Long): String {
    val fmt = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault())
    return fmt.format(Date(millis))
}
