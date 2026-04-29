package app.myphonecheck.mobile.feature.callcheck.ui

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
import app.myphonecheck.mobile.core.globalengine.search.SearchInput
import app.myphonecheck.mobile.feature.callcheck.R
import app.myphonecheck.mobile.feature.callcheck.repository.CallDirection
import app.myphonecheck.mobile.feature.callcheck.repository.CallEntry
import app.myphonecheck.mobile.feature.decisionui.components.DirectSearchAddon
import app.myphonecheck.mobile.feature.decisionui.components.DirectSearchHandler
import app.myphonecheck.mobile.feature.decisionui.components.SurfaceContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val ScreenBg = Color(0xFF0D1B2A)
private val CardBg = Color(0xFF1B2838)
private val Accent = Color(0xFF4FC3F7)
private val TextSubtle = Color(0xFFB0BEC5)

@Composable
fun CallCheckRoute(
    onBack: () -> Unit,
    viewModel: CallCheckViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) viewModel.refresh()
    }

    CallCheckScreen(
        state = state,
        onBack = onBack,
        onRequestPermission = {
            permissionLauncher.launch(Manifest.permission.READ_CALL_LOG)
        },
        directSearchHandler = viewModel.directSearchHandler,
        simRegion = (state as? CallCheckUiState.Loaded)?.simRegion ?: "",
    )
}

@Composable
private fun CallCheckScreen(
    state: CallCheckUiState,
    onBack: () -> Unit,
    onRequestPermission: () -> Unit,
    directSearchHandler: DirectSearchHandler? = null,
    simRegion: String = "",
) {
    Box(modifier = Modifier.fillMaxSize().background(ScreenBg)) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                }
                Text(
                    text = stringResource(R.string.call_check_title),
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            when (state) {
                is CallCheckUiState.Loading -> {
                    Text(
                        text = stringResource(R.string.call_check_recent_calls),
                        color = TextSubtle,
                        fontSize = 12.sp,
                    )
                }
                is CallCheckUiState.PermissionRequired -> PermissionCard(onRequestPermission)
                is CallCheckUiState.Loaded -> {
                    EntryList(state.entries)
                    // v2.5.0 §direct-search: 최근 entry의 PhoneNumber로 SIM 기준 AI 검색.
                    val firstNumber = state.entries.firstOrNull()?.e164
                    if (firstNumber != null && directSearchHandler != null && simRegion.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        DirectSearchAddon(
                            input = SearchInput.PhoneNumber(firstNumber, callCheckSimContext(simRegion)),
                            tier = RiskTier.Unknown,
                            surfaceContext = SurfaceContext.CALL,
                            handler = directSearchHandler,
                        )
                    }
                }
            }
        }
    }
}

private fun callCheckSimContext(region: String) = app.myphonecheck.mobile.core.globalengine.simcontext.SimContext(
    mcc = "", mnc = "", countryIso = region, operatorName = "",
    currency = java.util.Currency.getInstance("USD"),
    phoneRegion = region, timezone = java.util.TimeZone.getDefault(),
)

@Composable
private fun PermissionCard(onRequest: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = stringResource(R.string.call_check_permission_required),
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = onRequest) {
                Text(stringResource(R.string.call_check_grant_permission))
            }
        }
    }
}

@Composable
private fun EntryList(entries: List<CallEntry>) {
    if (entries.isEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = stringResource(R.string.call_check_empty_title),
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.call_check_empty_desc),
                    color = TextSubtle,
                    fontSize = 12.sp,
                )
            }
        }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(entries, key = { it.timestampMillis.toString() + it.rawNumber }) { entry ->
                CallEntryRow(entry)
            }
        }
    }
}

@Composable
private fun CallEntryRow(entry: CallEntry) {
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
                    text = if (entry.isValid) entry.displayNumber else stringResource(R.string.call_check_invalid_number),
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = formatTimestamp(entry.timestampMillis) + " · " +
                        directionLabel(entry.direction) +
                        if (entry.regionCode.isNotEmpty()) " · " + entry.regionCode else "",
                    color = TextSubtle,
                    fontSize = 11.sp,
                )
            }
            Text(
                text = formatDuration(entry.durationSeconds),
                color = Accent,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun directionLabel(direction: CallDirection): String = when (direction) {
    CallDirection.INCOMING -> stringResource(R.string.call_check_type_incoming)
    CallDirection.OUTGOING -> stringResource(R.string.call_check_type_outgoing)
    CallDirection.MISSED -> stringResource(R.string.call_check_type_missed)
    CallDirection.REJECTED -> stringResource(R.string.call_check_type_rejected)
    CallDirection.BLOCKED -> stringResource(R.string.call_check_type_blocked)
    CallDirection.VOICEMAIL -> stringResource(R.string.call_check_type_voicemail)
    CallDirection.UNKNOWN -> stringResource(R.string.call_check_type_unknown)
}

private fun formatTimestamp(millis: Long): String {
    val fmt = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault())
    return fmt.format(Date(millis))
}

private fun formatDuration(seconds: Long): String {
    if (seconds <= 0) return "—"
    val m = seconds / 60
    val s = seconds % 60
    return "%d:%02d".format(m, s)
}
