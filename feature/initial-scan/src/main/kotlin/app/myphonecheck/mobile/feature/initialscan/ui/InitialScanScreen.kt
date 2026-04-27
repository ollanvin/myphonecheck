package app.myphonecheck.mobile.feature.initialscan.ui

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import app.myphonecheck.mobile.feature.initialscan.R
import app.myphonecheck.mobile.feature.initialscan.service.ScanResult

private val ScreenBg = Color(0xFF0D1B2A)
private val CardBg = Color(0xFF1B2838)
private val Accent = Color(0xFF4FC3F7)
private val TextSubtle = Color(0xFFB0BEC5)

@Composable
fun InitialScanRoute(
    onComplete: () -> Unit,
    onSkip: () -> Unit,
    viewModel: InitialScanViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.checkAlreadyCompleted(onAlready = onComplete)
    }

    InitialScanScreen(
        state = state,
        onStartScan = viewModel::startScan,
        onContinue = onComplete,
        onSkip = onSkip,
    )
}

@Composable
private fun InitialScanScreen(
    state: InitialScanUiState,
    onStartScan: () -> Unit,
    onContinue: () -> Unit,
    onSkip: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize().background(ScreenBg)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stringResource(R.string.initial_scan_title),
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(16.dp))

            when (state) {
                is InitialScanUiState.PermissionConsent ->
                    PermissionConsentView(onStartScan = onStartScan, onSkip = onSkip)
                is InitialScanUiState.InProgress -> ScanProgressView()
                is InitialScanUiState.Completed -> ScanCompleteView(state.result, onContinue)
            }
        }
    }
}

@Composable
private fun PermissionConsentView(onStartScan: () -> Unit, onSkip: () -> Unit) {
    val phoneLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { /* user proceeds via Start Scan; scanners handle missing permissions gracefully */ }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = stringResource(R.string.initial_scan_consent_title),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.initial_scan_consent_desc),
                color = TextSubtle,
                fontSize = 13.sp,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    phoneLauncher.launch(
                        arrayOf(
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.READ_CALL_LOG,
                            Manifest.permission.READ_SMS,
                        )
                    )
                    onStartScan()
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.initial_scan_start))
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = onSkip,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.initial_scan_skip))
            }
        }
    }
}

@Composable
private fun ScanProgressView() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = Accent)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.initial_scan_progress_title),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(12.dp))
            ProgressLine(stringResource(R.string.initial_scan_progress_sim))
            ProgressLine(stringResource(R.string.initial_scan_progress_calls))
            ProgressLine(stringResource(R.string.initial_scan_progress_sms))
            ProgressLine(stringResource(R.string.initial_scan_progress_packages))
        }
    }
}

@Composable
private fun ProgressLine(label: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = "·", color = Accent, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(0.dp))
        Text(
            text = "  $label",
            color = TextSubtle,
            fontSize = 13.sp,
        )
    }
}

@Composable
private fun ScanCompleteView(result: ScanResult, onContinue: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = stringResource(R.string.initial_scan_complete_title),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.initial_scan_complete_desc),
                color = TextSubtle,
                fontSize = 13.sp,
            )
            Spacer(modifier = Modifier.height(16.dp))
            ResultLine(stringResource(R.string.initial_scan_progress_sim), result.simContext.countryIso + " · " + result.simContext.operatorName.ifEmpty { result.simContext.phoneRegion })
            ResultLine(stringResource(R.string.initial_scan_progress_calls), stringResource(R.string.initial_scan_count_format, result.callCount))
            ResultLine(stringResource(R.string.initial_scan_progress_sms), stringResource(R.string.initial_scan_count_format, result.smsCount))
            ResultLine(stringResource(R.string.initial_scan_progress_packages), stringResource(R.string.initial_scan_count_format, result.packageCount))
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.initial_scan_continue))
            }
        }
    }
}

@Composable
private fun ResultLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = label, color = TextSubtle, fontSize = 13.sp)
        Text(text = value, color = Accent, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}
