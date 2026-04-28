package app.myphonecheck.mobile.feature.onboarding.ui.pages

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import app.myphonecheck.mobile.feature.onboarding.OnboardingViewModel
import app.myphonecheck.mobile.feature.onboarding.R
import app.myphonecheck.mobile.feature.onboarding.ui.OnboardingPermissionRow

/** 온보딩 5장: 권한 요청 */
@Composable
fun OnboardingPage5(viewModel: OnboardingViewModel) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshPermissionSnapshot()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    var state by remember { mutableStateOf(viewModel.permissionState.value) }
    LaunchedEffect(viewModel) {
        viewModel.permissionState.collect { state = it }
    }

    val phoneLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) {
        viewModel.refreshPermissionSnapshot()
    }

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = stringResource(R.string.onboarding_p5_intro),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            lineHeight = 24.sp,
        )
        Spacer(modifier = Modifier.height(16.dp))
        OnboardingPermissionRow(
            icon = Icons.Filled.Layers,
            title = context.getString(R.string.onboarding_perm_overlay_title),
            description = context.getString(R.string.onboarding_perm_overlay_desc),
            granted = state.overlayGranted,
            actionAllowLabel = stringResource(R.string.onboarding_perm_action_allow),
            statusGrantedLabel = stringResource(R.string.onboarding_perm_status_granted),
            onAllow = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:${context.packageName}"),
                    )
                    context.startActivity(intent)
                }
            },
        )
        OnboardingPermissionRow(
            icon = Icons.Filled.Security,
            title = context.getString(R.string.onboarding_perm_usage_stats_title),
            description = context.getString(R.string.onboarding_perm_usage_stats_desc),
            granted = state.usageStatsGranted,
            actionAllowLabel = stringResource(R.string.onboarding_perm_action_allow),
            statusGrantedLabel = stringResource(R.string.onboarding_perm_status_granted),
            onAllow = {
                context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            },
        )
        OnboardingPermissionRow(
            icon = Icons.Filled.Phone,
            title = context.getString(R.string.onboarding_perm_phone_state_title),
            description = context.getString(R.string.onboarding_perm_phone_state_desc),
            granted = state.phoneStateGranted,
            actionAllowLabel = stringResource(R.string.onboarding_perm_action_allow),
            statusGrantedLabel = stringResource(R.string.onboarding_perm_status_granted),
            onAllow = { phoneLauncher.launch(Manifest.permission.READ_PHONE_STATE) },
        )
    }
}
