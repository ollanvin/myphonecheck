package app.myphonecheck.mobile.feature.pushtrash.ui

import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import app.myphonecheck.mobile.data.localcache.entity.TrashedNotificationEntity
import app.myphonecheck.mobile.feature.pushtrash.R
import app.myphonecheck.mobile.feature.pushtrash.mapping.ChannelLabelMapper
import app.myphonecheck.mobile.feature.pushtrash.repository.PushTrashRepository
import app.myphonecheck.mobile.feature.pushtrash.viewmodel.AppBlockSettingsViewModel
import app.myphonecheck.mobile.feature.pushtrash.viewmodel.PushTrashAppsViewModel
import app.myphonecheck.mobile.feature.pushtrash.viewmodel.PushTrashBinViewModel
import app.myphonecheck.mobile.feature.pushtrash.viewmodel.PushTrashViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

private val ScreenBg = Color(0xFF0D1B2A)
private val CardBg = Color(0xFF1B2838)
private val Accent = Color(0xFF4FC3F7)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PushTrashMainRoute(
    onBack: () -> Unit,
    onOpenBin: () -> Unit,
    onOpenApps: () -> Unit,
) {
    val vm: PushTrashViewModel = hiltViewModel()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val listenerOk by vm.listenerEnabled.collectAsState()
    val trashed7d by vm.trashedCount7d.collectAsState()
    val rules by vm.ruleCount.collectAsState()

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                vm.refreshListenerState()
                vm.refreshRuleCount()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.push_trash_title), color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.push_trash_back), tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ScreenBg),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ScreenBg)
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            if (!listenerOk) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF3E2723)),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            stringResource(R.string.push_trash_permission_banner),
                            color = Color.White,
                            fontSize = 14.sp,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = {
                                context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                            },
                        ) {
                            Text(stringResource(R.string.push_trash_open_listener_settings))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Text(
                stringResource(R.string.push_trash_collected_7d_fmt, trashed7d),
                color = Color(0xFFB0BEC5),
                fontSize = 15.sp,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                stringResource(R.string.push_trash_rules_fmt, rules),
                color = Color(0xFFB0BEC5),
                fontSize = 15.sp,
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenBin() },
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(16.dp),
            ) {
                Text(
                    stringResource(R.string.push_trash_view_bin),
                    modifier = Modifier.padding(20.dp),
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenApps() },
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(16.dp),
            ) {
                Text(
                    stringResource(R.string.push_trash_app_settings),
                    modifier = Modifier.padding(20.dp),
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PushTrashBinRoute(onBack: () -> Unit) {
    val vm: PushTrashBinViewModel = hiltViewModel()
    val context = LocalContext.current
    val items by vm.items.collectAsState()

    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.push_trash_bin_title_fmt, items.size),
                        color = Color.White,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.push_trash_back), tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ScreenBg),
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(ScreenBg)
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(items, key = { it.id }) { entry ->
                TrashedNotificationCard(
                    entry = entry,
                    onRestore = {
                        vm.restore(entry) { key ->
                            val msg = when (key) {
                                "restore" -> context.getString(R.string.push_trash_restore_done)
                                else -> key
                            }
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }
                    },
                    onDelete = {
                        vm.delete(entry) { key ->
                            val msg = when (key) {
                                "delete" -> context.getString(R.string.push_trash_deleted)
                                else -> key
                            }
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun TrashedNotificationCard(
    entry: TrashedNotificationEntity,
    onRestore: () -> Unit,
    onDelete: () -> Unit,
) {
    val context = LocalContext.current
    val timeStr = formatPostedAt(entry.postedAt)
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                entry.title?.takeIf { it.isNotBlank() } ?: entry.packageName,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
            )
            entry.text?.takeIf { it.isNotBlank() }?.let { body ->
                Spacer(modifier = Modifier.height(6.dp))
                Text(body, color = Color(0xFFB0BEC5), fontSize = 13.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "${ChannelLabelMapper.displayAppLabel(context, entry.packageName)} 뿯½ $timeStr",
                color = Color(0xFF607D8B),
                fontSize = 12.sp,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onRestore) {
                    Text(stringResource(R.string.push_trash_restore))
                }
                OutlinedButton(onClick = onDelete) {
                    Text(stringResource(R.string.push_trash_delete))
                }
            }
        }
    }
}

private val timeFormatter: DateTimeFormatter =
    DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withZone(ZoneId.systemDefault())

private fun formatPostedAt(postedAt: Long): String =
    timeFormatter.format(Instant.ofEpochMilli(postedAt))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PushTrashAppsRoute(
    onBack: () -> Unit,
    onOpenApp: (String) -> Unit,
) {
    val vm: PushTrashAppsViewModel = hiltViewModel()
    val apps by vm.apps.collectAsState()
    val context = LocalContext.current

    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.push_trash_apps_title), color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.push_trash_back), tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ScreenBg),
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(ScreenBg)
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(apps, key = { it.packageName }) { row ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenApp(row.packageName) },
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            ChannelLabelMapper.displayAppLabel(context, row.packageName),
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
                        )
                        Text(
                            stringResource(R.string.push_trash_notifications_7d_fmt, row.notificationCount),
                            color = Accent,
                            fontSize = 13.sp,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBlockSettingsRoute(onBack: () -> Unit) {
    val vm: AppBlockSettingsViewModel = hiltViewModel()
    val context = LocalContext.current
    val rows by vm.channelRows.collectAsState()
    val appMode by vm.appMode.collectAsState()
    val total by vm.totalNotifications.collectAsState()

    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        ChannelLabelMapper.displayAppLabel(context, vm.packageName),
                        color = Color.White,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.push_trash_back), tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ScreenBg),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ScreenBg)
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Text(
                stringResource(R.string.push_trash_notifications_7d_fmt, total),
                color = Color(0xFFB0BEC5),
                fontSize = 14.sp,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                stringResource(R.string.push_trash_app_settings_title),
                color = Accent,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ModeChip(
                    label = stringResource(R.string.push_trash_mode_per_channel),
                    selected = appMode == null,
                    onClick = { vm.clearAppMode() },
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ModeChip(
                    label = stringResource(R.string.push_trash_mode_all_allowed),
                    selected = appMode == PushTrashRepository.MODE_ALL_ALLOWED,
                    onClick = { vm.setAppModeAllAllowed() },
                )
                ModeChip(
                    label = stringResource(R.string.push_trash_mode_all_blocked),
                    selected = appMode == PushTrashRepository.MODE_ALL_BLOCKED,
                    onClick = { vm.setAppModeAllBlocked() },
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = Color(0xFF37474F))
            Spacer(modifier = Modifier.height(16.dp))

            if (rows.isEmpty()) {
                Text(
                    stringResource(R.string.push_trash_no_channels_hint),
                    color = Color(0xFF90A4AE),
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                )
            } else {
                for (row in rows) {
                    val label = ChannelLabelMapper.label(context, vm.packageName, row.channelId)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(label, color = Color.White, fontSize = 14.sp)
                            Text(
                                stringResource(R.string.push_trash_notifications_7d_fmt, row.notificationCount),
                                color = Color(0xFF607D8B),
                                fontSize = 12.sp,
                            )
                        }
                        Switch(
                            checked = row.blocked,
                            onCheckedChange = { vm.setChannelBlocked(row.channelId, it) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ModeChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val bg = if (selected) Accent.copy(alpha = 0.25f) else CardBg
    Text(
        text = label,
        modifier = Modifier
            .background(bg, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        color = if (selected) Accent else Color.White,
        fontSize = 13.sp,
        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
    )
}
