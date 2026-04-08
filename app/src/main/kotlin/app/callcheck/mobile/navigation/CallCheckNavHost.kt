package app.callcheck.mobile.navigation

import android.Manifest
import android.app.AppOpsManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import app.callcheck.mobile.R as AppR
import app.callcheck.mobile.core.model.InterceptEventType
import app.callcheck.mobile.core.model.RiskLevel
import app.callcheck.mobile.core.model.UserCallAction
import app.callcheck.mobile.core.model.UserCallTag
import app.callcheck.mobile.data.localcache.entity.MessageHubEntity
import app.callcheck.mobile.data.localcache.entity.PrivacyHistoryEntity
import app.callcheck.mobile.data.localcache.entity.UserCallRecord
import app.callcheck.mobile.feature.countryconfig.*
import app.callcheck.mobile.feature.pushintercept.PushInterceptService
import app.callcheck.mobile.ui.backup.BackupScreen
import app.callcheck.mobile.viewmodel.CallHistoryViewModel
import app.callcheck.mobile.viewmodel.MessageHubViewModel
import app.callcheck.mobile.viewmodel.PrivacyHistoryViewModel
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

// ═══════════════════════════════════════════════════════════
// Bottom Navigation — 3탭: 홈 / 기록 / 설정
// ═══════════════════════════════════════════════════════════

private enum class BottomTab(
    val route: String,
    val icon: ImageVector,
    val labelResId: Int,
) {
    HOME("home", Icons.Filled.Home, AppR.string.nav_home),
    TIMELINE("timeline", Icons.Filled.Timeline, AppR.string.nav_timeline),
    SETTINGS("settings", Icons.Filled.Settings, AppR.string.nav_settings),
}

/** 바텀 네비게이션이 보이는 route 목록 */
private val BOTTOM_NAV_ROUTES = BottomTab.entries.map { it.route }.toSet()

@Composable
fun CallCheckNavHost(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val languageProvider = remember(configuration) {
        LanguageContextProviderImpl(context)
    }

    // ViewModel — Hilt DI, Room DB 연동
    val historyViewModel: CallHistoryViewModel = hiltViewModel()

    // 최초 실행 시 데모 데이터 삽입
    LaunchedEffect(Unit) {
        historyViewModel.insertDemoDataIfEmpty()
    }

    // 현재 route 추적
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        containerColor = Color(0xFF0D1B2A),
        bottomBar = {
            if (currentRoute in BOTTOM_NAV_ROUTES) {
                NavigationBar(
                    containerColor = Color(0xFF0A1628),
                    contentColor = Color.White,
                    tonalElevation = 0.dp,
                ) {
                    BottomTab.entries.forEach { tab ->
                        val selected = currentRoute == tab.route
                        val tabLabel = stringResource(id = tab.labelResId)
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (!selected) {
                                    navController.navigate(tab.route) {
                                        // 탭 전환 시 백스택 정리 — home을 root로 유지
                                        popUpTo("home") { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tabLabel,
                                )
                            },
                            label = { Text(tabLabel, fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF4FC3F7),
                                selectedTextColor = Color(0xFF4FC3F7),
                                unselectedIconColor = Color(0xFF607D8B),
                                unselectedTextColor = Color(0xFF607D8B),
                                indicatorColor = Color(0xFF1E3A5F),
                            ),
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "onboarding",
            modifier = Modifier.padding(innerPadding),
        ) {
            composable("onboarding") {
                OnboardingScreen(
                    languageProvider = languageProvider,
                    onContinue = {
                        navController.navigate("home") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    }
                )
            }
            composable("home") {
                HomeScreen(
                    languageProvider = languageProvider,
                    onPurchaseClick = { navController.navigate("purchase") },
                    viewModel = historyViewModel,
                    onEngineClick = { route -> navController.navigate(route) },
                )
            }
            composable("message-hub") {
                val messageHubViewModel: MessageHubViewModel = hiltViewModel()
                MessageHubScreen(
                    viewModel = messageHubViewModel,
                    onBack = { navController.popBackStack() },
                )
            }
            composable("privacy-history") {
                val privacyHistoryViewModel: PrivacyHistoryViewModel = hiltViewModel()
                PrivacyHistoryScreen(
                    viewModel = privacyHistoryViewModel,
                    onBack = { navController.popBackStack() },
                )
            }
            composable("engine/call") {
                EngineDetailScreen(
                    engineName = context.getString(AppR.string.engine_name_callcheck),
                    engineNameKo = context.getString(AppR.string.engine_title_call),
                    eventType = InterceptEventType.CALL,
                    color = Color(0xFF4FC3F7),
                    icon = Icons.Filled.Phone,
                    languageProvider = languageProvider,
                    onBack = { navController.popBackStack() },
                )
            }
            composable("engine/push") {
                EngineDetailScreen(
                    engineName = context.getString(AppR.string.engine_name_pushcheck),
                    engineNameKo = context.getString(AppR.string.engine_title_push),
                    eventType = InterceptEventType.PUSH,
                    color = Color(0xFFFFB74D),
                    icon = Icons.Filled.Notifications,
                    languageProvider = languageProvider,
                    onBack = { navController.popBackStack() },
                )
            }
            composable("engine/message") {
                EngineDetailScreen(
                    engineName = context.getString(AppR.string.engine_name_messagecheck),
                    engineNameKo = context.getString(AppR.string.engine_title_message),
                    eventType = InterceptEventType.MESSAGE,
                    color = Color(0xFF81C784),
                    icon = Icons.Filled.Message,
                    languageProvider = languageProvider,
                    onBack = { navController.popBackStack() },
                )
            }
            composable("engine/privacy") {
                EngineDetailScreen(
                    engineName = context.getString(AppR.string.engine_name_privacycheck),
                    engineNameKo = context.getString(AppR.string.engine_title_privacy),
                    eventType = InterceptEventType.PRIVACY,
                    color = Color(0xFFE57373),
                    icon = Icons.Filled.Security,
                    languageProvider = languageProvider,
                    onBack = { navController.popBackStack() },
                )
            }
            composable("timeline") {
                TimelineScreen(
                    languageProvider = languageProvider,
                    viewModel = historyViewModel,
                    onBack = { navController.popBackStack() },
                    onRecordClick = { canonicalNumber ->
                        val safe = canonicalNumber.replace("+", "PLUS")
                        navController.navigate("call-detail/$safe")
                    },
                )
            }
            composable(
                route = "call-detail/{number}",
                arguments = listOf(navArgument("number") { type = NavType.StringType }),
            ) { backStackEntry ->
                val raw = backStackEntry.arguments?.getString("number") ?: ""
                val canonicalNumber = raw.replace("PLUS", "+")
                CallDetailScreen(
                    canonicalNumber = canonicalNumber,
                    languageProvider = languageProvider,
                    viewModel = historyViewModel,
                    onBack = { navController.popBackStack() },
                )
            }
            composable("settings") {
                SettingsScreen(
                    languageProvider = languageProvider,
                    onBack = { navController.popBackStack() },
                    onNavigateToBackup = { navController.navigate("backup") },
                )
            }
            composable("purchase") {
                PurchaseScreen(
                    languageProvider = languageProvider,
                    onBack = { navController.popBackStack() }
                )
            }
            composable("backup") {
                BackupScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// 온보딩 5장 — 보호영역 / 4엔진 / 권한안내 / 보안선언 / 권한요청
// ═══════════════════════════════════════════════════════════

private const val ONBOARDING_PAGE_COUNT = 5

private fun Context.hasDrawOverlayPermission(): Boolean =
    Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this)

private fun Context.isNotificationListenerEnabled(): Boolean {
    val cn = ComponentName(this, PushInterceptService::class.java)
    val flat = Settings.Secure.getString(contentResolver, "enabled_notification_listeners") ?: return false
    return flat.contains(cn.flattenToString(), ignoreCase = false)
}

private fun Context.hasUsageStatsPermission(): Boolean {
    val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        appOps.unsafeCheckOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            packageName,
        )
    } else {
        @Suppress("DEPRECATION")
        appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            packageName,
        )
    }
    return mode == AppOpsManager.MODE_ALLOWED
}

private fun Context.hasReadPhoneStatePermission(): Boolean =
    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) ==
        PackageManager.PERMISSION_GRANTED

@Composable
private fun OnboardingScreen(
    languageProvider: LanguageContextProvider,
    onContinue: () -> Unit,
) {
    languageProvider.resolveLanguage()
    var currentPage by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1B2A)),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            when (currentPage) {
                0 -> OnboardingPage1()
                1 -> OnboardingPage2()
                2 -> OnboardingPage3()
                3 -> OnboardingPage4()
                4 -> OnboardingPage5()
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            repeat(ONBOARDING_PAGE_COUNT) { index ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (index == currentPage) 10.dp else 8.dp)
                        .background(
                            color = if (index == currentPage) Color(0xFF4FC3F7) else Color(0xFF455A64),
                            shape = RoundedCornerShape(50),
                        ),
                )
            }
        }

        if (currentPage < 4) {
            Button(
                onClick = { currentPage++ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4FC3F7)),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(
                    text = stringResource(AppR.string.btn_next),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0D1B2A),
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(
                    onClick = onContinue,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF4FC3F7)),
                ) {
                    Text(
                        stringResource(AppR.string.onboarding_permissions_later),
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Button(
                    onClick = onContinue,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4FC3F7)),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(
                        stringResource(AppR.string.onboarding_permissions_continue_home),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0D1B2A),
                    )
                }
            }
        }
    }
}

/** 온보딩 1장: 질문 + 위협 인식 */
@Composable
private fun OnboardingPage1() {
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.verticalScroll(rememberScrollState()),
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // 위협 질문 — 큰 글씨
        Text(
            text = context.getString(AppR.string.onboarding_page1_title),
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
            lineHeight = 36.sp,
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 위협 상황 묘사
        Text(
            text = context.getString(AppR.string.onboarding_page1_threat),
            fontSize = 16.sp,
            color = Color(0xFFE57373),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Medium,
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 해결 제시
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2A3A)),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = context.getString(AppR.string.onboarding_page1_card_title),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4FC3F7),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = context.getString(AppR.string.onboarding_page1_card_desc),
                    fontSize = 14.sp,
                    color = Color(0xFFB0BEC5),
                    lineHeight = 20.sp,
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 온디바이스 신뢰
        Text(
            text = context.getString(AppR.string.onboarding_on_device_only),
            fontSize = 12.sp,
            color = Color(0xFF455A64),
        )
    }
}

/** 온보딩 2장: 4곳 공격 포인트 */
@Composable
private fun OnboardingPage2() {
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.verticalScroll(rememberScrollState()),
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = context.getString(AppR.string.onboarding_page2_title),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFE57373),
            textAlign = TextAlign.Center,
            lineHeight = 32.sp,
        )

        Spacer(modifier = Modifier.height(24.dp))

        data class ThreatInfo(
            val icon: ImageVector,
            val threat: String,
            val attack: String,
            val color: Color,
        )
        val threats = listOf(
            ThreatInfo(
                Icons.Filled.Phone,
                context.getString(AppR.string.threat_call_title),
                context.getString(AppR.string.threat_call_desc),
                Color(0xFF4FC3F7)
            ),
            ThreatInfo(
                Icons.Filled.Notifications,
                context.getString(AppR.string.threat_push_title),
                context.getString(AppR.string.threat_push_desc),
                Color(0xFFFFB74D)
            ),
            ThreatInfo(
                Icons.Filled.Message,
                context.getString(AppR.string.threat_message_title),
                context.getString(AppR.string.threat_message_desc),
                Color(0xFF81C784)
            ),
            ThreatInfo(
                Icons.Filled.Security,
                context.getString(AppR.string.threat_privacy_title),
                context.getString(AppR.string.threat_privacy_desc),
                Color(0xFFE57373)
            ),
        )

        for (threat in threats) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2A1A1A)),
                shape = RoundedCornerShape(12.dp),
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(threat.icon, null, tint = threat.color, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = threat.threat,
                            fontSize = 16.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = threat.attack,
                            fontSize = 13.sp,
                            color = Color(0xFFEF9A9A),
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = context.getString(AppR.string.onboarding_page2_footer),
            fontSize = 15.sp,
            color = Color(0xFF4FC3F7),
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
    }
}

/** 온보딩 3장: 접근 필요 이유 + 신뢰 확정 */
@Composable
private fun OnboardingPage3() {
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.verticalScroll(rememberScrollState()),
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = context.getString(AppR.string.onboarding_page3_title),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
            lineHeight = 32.sp,
        )

        Spacer(modifier = Modifier.height(24.dp))

        data class AccessInfo(
            val target: String,
            val why: String,
            val icon: ImageVector,
        )
        val accesses = listOf(
            AccessInfo(
                context.getString(AppR.string.access_phone_target),
                context.getString(AppR.string.access_phone_why),
                Icons.Filled.Phone
            ),
            AccessInfo(
                context.getString(AppR.string.access_push_target),
                context.getString(AppR.string.access_push_why),
                Icons.Filled.Notifications
            ),
            AccessInfo(
                context.getString(AppR.string.access_sms_target),
                context.getString(AppR.string.access_sms_why),
                Icons.Filled.Message
            ),
            AccessInfo(
                context.getString(AppR.string.access_usage_target),
                context.getString(AppR.string.access_usage_why),
                Icons.Filled.Security
            ),
        )

        for (access in accesses) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2A3A)),
                shape = RoundedCornerShape(12.dp),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(access.icon, null, tint = Color(0xFF4FC3F7), modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = access.why,
                            fontSize = 14.sp,
                            color = Color(0xFFB0BEC5),
                        )
                        Text(
                            text = access.target,
                            fontSize = 13.sp,
                            color = Color(0xFF607D8B),
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 신뢰 확정 — 강한 톤
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A3A2A)),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = context.getString(AppR.string.onboarding_privacy_title),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF81C784),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = context.getString(AppR.string.onboarding_privacy_desc),
                    fontSize = 13.sp,
                    color = Color(0xFF81C784).copy(alpha = 0.7f),
                )
            }
        }
    }
}

/** 온보딩 4장: 보안 선언 */
@Composable
private fun OnboardingPage4() {
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.verticalScroll(rememberScrollState()),
    ) {
        Icon(
            imageVector = Icons.Filled.Shield,
            contentDescription = null,
            tint = Color(0xFF4FC3F7),
            modifier = Modifier.size(72.dp),
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = context.getString(AppR.string.onboarding_p4_hero),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
            lineHeight = 32.sp,
        )
        Spacer(modifier = Modifier.height(28.dp))
        val declares = listOf(
            context.getString(AppR.string.onboarding_declare_no_server),
            context.getString(AppR.string.onboarding_declare_no_collection),
            context.getString(AppR.string.onboarding_declare_no_transfer),
        )
        for (line in declares) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2A3A)),
                shape = RoundedCornerShape(12.dp),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = null,
                        tint = Color(0xFF4FC3F7),
                        modifier = Modifier.size(22.dp),
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = line,
                        fontSize = 15.sp,
                        color = Color.White,
                        lineHeight = 22.sp,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = context.getString(AppR.string.onboarding_aes_footer),
            fontSize = 12.sp,
            color = Color(0xFF607D8B),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun OnboardingPermissionRow(
    icon: ImageVector,
    title: String,
    description: String,
    granted: Boolean,
    onAllow: () -> Unit,
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2A3A)),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = Color(0xFF4FC3F7), modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(description, fontSize = 13.sp, color = Color(0xFFB0BEC5), lineHeight = 18.sp)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            if (granted) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF81C784),
                        modifier = Modifier.size(22.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        context.getString(AppR.string.perm_status_granted),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF81C784),
                    )
                }
            } else {
                Button(
                    onClick = onAllow,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4FC3F7)),
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Text(
                        context.getString(AppR.string.perm_action_allow),
                        color = Color(0xFF0D1B2A),
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

/** 온보딩 5장: 권한 요청 */
@Composable
private fun OnboardingPage5() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var refreshTrigger by remember { mutableIntStateOf(0) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                refreshTrigger++
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val overlayOk = remember(refreshTrigger) { context.hasDrawOverlayPermission() }
    val notifOk = remember(refreshTrigger) { context.isNotificationListenerEnabled() }
    val usageOk = remember(refreshTrigger) { context.hasUsageStatsPermission() }

    val phoneLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { refreshTrigger++ }

    val phoneOk = remember(refreshTrigger) { context.hasReadPhoneStatePermission() }

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = stringResource(AppR.string.onboarding_p5_intro),
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
            title = context.getString(AppR.string.perm_overlay_title),
            description = context.getString(AppR.string.perm_overlay_desc),
            granted = overlayOk,
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
            icon = Icons.Filled.Notifications,
            title = context.getString(AppR.string.perm_notification_listener_title),
            description = context.getString(AppR.string.perm_notification_listener_desc),
            granted = notifOk,
            onAllow = {
                context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
            },
        )
        OnboardingPermissionRow(
            icon = Icons.Filled.Security,
            title = context.getString(AppR.string.perm_usage_stats_title),
            description = context.getString(AppR.string.perm_usage_stats_desc),
            granted = usageOk,
            onAllow = {
                context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            },
        )
        OnboardingPermissionRow(
            icon = Icons.Filled.Phone,
            title = context.getString(AppR.string.perm_phone_state_title),
            description = context.getString(AppR.string.perm_phone_state_desc),
            granted = phoneOk,
            onAllow = { phoneLauncher.launch(Manifest.permission.READ_PHONE_STATE) },
        )
    }
}

// ═══════════════════════════════════════════════════════════
// 홈 화면 — 오버레이 데모 트리거
// ═══════════════════════════════════════════════════════════

@Composable
private fun HomeScreen(
    languageProvider: LanguageContextProvider,
    onPurchaseClick: () -> Unit,
    viewModel: CallHistoryViewModel,
    onEngineClick: (String) -> Unit = {},
) {
    languageProvider.resolveLanguage()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1B2A))
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Title
        Text(
            text = context.getString(AppR.string.app_name),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
        )

        Text(
            text = context.getString(AppR.string.app_version_fmt, "1.0.0"),
            fontSize = 14.sp,
            color = Color(0xFF607D8B),
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 2x2 Grid of Engine Cards
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Row 1: CallCheck + PushCheck
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                EngineCard(
                    question = stringResource(AppR.string.engine_call_question),
                    description = stringResource(AppR.string.engine_call_desc),
                    icon = Icons.Filled.Phone,
                    color = Color(0xFF4FC3F7),
                    modifier = Modifier.weight(1f),
                    onClick = { onEngineClick("engine/call") },
                )
                EngineCard(
                    question = stringResource(AppR.string.engine_push_question),
                    description = stringResource(AppR.string.engine_push_desc),
                    icon = Icons.Filled.Notifications,
                    color = Color(0xFFFFB74D),
                    modifier = Modifier.weight(1f),
                    onClick = { onEngineClick("engine/push") },
                )
            }

            // Row 2: MessageCheck + PrivacyCheck
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                EngineCard(
                    question = stringResource(AppR.string.engine_message_question),
                    description = stringResource(AppR.string.engine_message_desc),
                    icon = Icons.Filled.Message,
                    color = Color(0xFF81C784),
                    modifier = Modifier.weight(1f),
                    onClick = { onEngineClick("message-hub") },
                )
                EngineCard(
                    question = stringResource(AppR.string.engine_privacy_question),
                    description = stringResource(AppR.string.engine_privacy_desc),
                    icon = Icons.Filled.Security,
                    color = Color(0xFFE57373),
                    modifier = Modifier.weight(1f),
                    onClick = { onEngineClick("privacy-history") },
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = stringResource(AppR.string.home_security_trust_badge),
            fontSize = 11.sp,
            color = Color(0xFF4FC3F7),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            lineHeight = 15.sp,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Subscribe button
        Button(
            onClick = onPurchaseClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4FC3F7)),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text(
                stringResource(AppR.string.btn_subscribe),
                color = Color(0xFF0D1B2A),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun EngineCard(
    question: String,
    description: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    val context = LocalContext.current
    Card(
        modifier = modifier
            .height(180.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2A3A)),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            // 상단: 아이콘
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(32.dp),
            )

            // 중앙: 질문형 문장 (메인)
            Text(
                text = question,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                lineHeight = 22.sp,
            )

            // 하단: 1줄 설명 + 상태
            Column {
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = Color(0xFF78909C),
                    lineHeight = 14.sp,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(color, shape = RoundedCornerShape(3.dp)),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = context.getString(AppR.string.status_active),
                        fontSize = 10.sp,
                        color = color.copy(alpha = 0.7f),
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// 엔진 상세 화면 — 4개 엔진 공통 레이아웃
// ═══════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EngineDetailScreen(
    engineName: String,
    engineNameKo: String,
    eventType: InterceptEventType,
    color: Color,
    icon: ImageVector,
    languageProvider: LanguageContextProvider,
    onBack: () -> Unit,
) {
    val language = languageProvider.resolveLanguage()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(engineNameKo, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = context.getString(AppR.string.common_back), tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D1B2A)),
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0D1B2A))
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Ring UI — 원형 상태 표시기
            Box(
                modifier = Modifier.size(160.dp),
                contentAlignment = Alignment.Center,
            ) {
                // 배경 원
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(
                            color = Color(0xFF1A2A3A),
                            shape = RoundedCornerShape(60.dp),
                        )
                        .border(
                            width = 3.dp,
                            color = color,
                            shape = RoundedCornerShape(60.dp),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(48.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Status text
            Text(
                text = context.getString(AppR.string.status_running),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = color,
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Recent events header
            Text(
                text = context.getString(AppR.string.recent_events_title),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start,
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Sample events
            val sampleEvents = getSampleEventsForEngine(eventType)
            for (event in sampleEvents) {
                EventListItem(
                    time = event.time,
                    status = event.status,
                    reason = event.reason,
                    statusColor = event.statusColor,
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun EventListItem(
    time: String,
    status: String,
    reason: String,
    statusColor: Color,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2A3A)),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = time,
                    fontSize = 13.sp,
                    color = Color(0xFF607D8B),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = reason,
                    fontSize = 14.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = statusColor.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(4.dp),
                ) {
                    Text(
                        text = status,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }

                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = null,
                    tint = Color(0xFF455A64),
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

private data class SampleEvent(
    val time: String,
    val status: String,
    val reason: String,
    val statusColor: Color,
)

private fun getSampleEventsForEngine(eventType: InterceptEventType): List<SampleEvent> {
    return when (eventType) {
        InterceptEventType.CALL -> listOf(
            SampleEvent("2:15 PM", "Blocked", "Spam - Known phishing number", Color(0xFFD32F2F)),
            SampleEvent("1:42 PM", "Allowed", "Contact in phone", Color(0xFF2E7D32)),
            SampleEvent("12:08 PM", "Blocked", "Scam pattern detected", Color(0xFFD32F2F)),
        )
        InterceptEventType.PUSH -> listOf(
            SampleEvent("3:45 PM", "Blocked", "Suspicious notification", Color(0xFFD32F2F)),
            SampleEvent("2:30 PM", "Allowed", "Trusted app notification", Color(0xFF2E7D32)),
            SampleEvent("1:15 PM", "Blocked", "Malware signature match", Color(0xFFD32F2F)),
        )
        InterceptEventType.MESSAGE -> listOf(
            SampleEvent("4:20 PM", "Blocked", "Phishing SMS detected", Color(0xFFD32F2F)),
            SampleEvent("3:00 PM", "Allowed", "Normal message", Color(0xFF2E7D32)),
            SampleEvent("1:50 PM", "Blocked", "Suspicious link detected", Color(0xFFD32F2F)),
        )
        InterceptEventType.PRIVACY -> listOf(
            SampleEvent("5:10 PM", "Blocked", "Location access denied", Color(0xFFD32F2F)),
            SampleEvent("4:00 PM", "Allowed", "Camera access - TrustApp", Color(0xFF2E7D32)),
            SampleEvent("2:45 PM", "Blocked", "Microphone access denied", Color(0xFFD32F2F)),
        )
    }
}

// ═══════════════════════════════════════════════════════════
// 통합 타임라인 화면 — 4개 엔진 통합 뷰
// ═══════════════════════════════════════════════════════════

private fun getTimelineLabel(context: Context): String = context.getString(AppR.string.timeline_title)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimelineScreen(
    languageProvider: LanguageContextProvider,
    viewModel: CallHistoryViewModel,
    onBack: () -> Unit,
    onRecordClick: (String) -> Unit,
) {
    val language = languageProvider.resolveLanguage()
    val context = LocalContext.current
    val records by viewModel.allRecords.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(getTimelineLabel(context), color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = context.getString(AppR.string.common_back), tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D1B2A)),
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0D1B2A))
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            if (records.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = context.getString(AppR.string.no_events_yet),
                        fontSize = 14.sp,
                        color = Color(0xFF607D8B),
                    )
                }
            } else {
                // Display records as timeline
                for (record in records.sortedByDescending { it.id }) {
                    TimelineRecordItem(
                        record = record,
                        language = language,
                        onClick = { onRecordClick(record.canonicalNumber) },
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun TimelineRecordItem(
    record: UserCallRecord,
    language: SupportedLanguage,
    onClick: () -> Unit,
) {
    val context = LocalContext.current
    val engineColor = Color(0xFF4FC3F7) // Default to CallCheck color
    val engineIcon = Icons.Filled.Phone // Default to Phone icon

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2838)),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(
                    imageVector = engineIcon,
                    contentDescription = null,
                    tint = engineColor,
                    modifier = Modifier.size(24.dp),
                )

                Column {
                    Text(
                        text = record.displayNumber,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                    )
                    Text(
                        text = context.getString(AppR.string.calls_count_fmt, record.callCount),
                        fontSize = 12.sp,
                        color = Color(0xFF607D8B),
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(engineColor, shape = RoundedCornerShape(4.dp)),
                )
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = null,
                    tint = Color(0xFF455A64),
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@Composable
private fun PrivacyDeclarationItem(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A3A2A)),
        shape = RoundedCornerShape(12.dp),
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF81C784),
            modifier = Modifier.padding(16.dp),
            lineHeight = 20.sp,
        )
    }
}

@Composable
private fun VerdictDemoButton(
    verdict: String,
    subtitle: String,
    riskLevel: RiskLevel,
    phoneNumber: String,
    category: String,
    confidence: Int,
    language: SupportedLanguage,
    context: Context,
    onAction: (String, UserCallAction) -> Unit = { _, _ -> },
) {
    val bgColor = when (riskLevel) {
        RiskLevel.HIGH -> Color(0xFFD32F2F)
        RiskLevel.MEDIUM -> Color(0xFFF57F17)
        RiskLevel.LOW -> Color(0xFF2E7D32)
        RiskLevel.UNKNOWN -> Color(0xFF455A64)
    }

    Button(
        onClick = {
            showDemoOverlay(context, riskLevel, phoneNumber, category, confidence, language, onAction)
        },
        modifier = Modifier.fillMaxWidth().height(72.dp),
        colors = ButtonDefaults.buttonColors(containerColor = bgColor),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = verdict,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color(0xDDFFFFFF),
            )
        }
    }
}

/**
 * 실제 SYSTEM_ALERT_WINDOW 오버레이를 표시한다.
 * CallerIdOverlayManager.buildOverlayView()와 동일 구조:
 *   HERO: 24sp Bold 한 단어 verdict
 *   INFO: 카테고리 · 번호 · 신뢰도%
 *   REASONS: 최대 2줄
 *   BUTTONS: 수신/거절
 */
private fun showDemoOverlay(
    context: Context,
    riskLevel: RiskLevel,
    phoneNumber: String,
    category: String,
    confidence: Int,
    language: SupportedLanguage,
    onAction: (String, UserCallAction) -> Unit = { _, _ -> },
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
        return
    }

    val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    val localizer = SignalSummaryLocalizer()
    val categoryText = localizer.localizeCategory(category, context)

    // Verdict word from OverlayUiText
    val verdictWord = getOneWordVerdict(riskLevel, context)

    // Background color
    val bgColor = when (riskLevel) {
        RiskLevel.HIGH -> 0xFFD32F2F.toInt()
        RiskLevel.MEDIUM -> 0xFFF57F17.toInt()
        RiskLevel.LOW -> 0xFF2E7D32.toInt()
        RiskLevel.UNKNOWN -> 0xFF455A64.toInt()
    }

    // Reasons (demo — max 2)
    val reasons = getDemoReasons(riskLevel, context)

    // Build overlay view
    val container = LinearLayout(context)
    container.orientation = LinearLayout.VERTICAL
    container.setBackgroundColor(bgColor)
    container.setPadding(48, 40, 48, 40)
    container.gravity = Gravity.CENTER_HORIZONTAL

    // HERO: 24sp one word verdict
    container.addView(TextView(context).apply {
        text = verdictWord
        textSize = 24f
        setTextColor(0xFFFFFFFF.toInt())
        gravity = Gravity.CENTER
        setTypeface(typeface, android.graphics.Typeface.BOLD)
    })

    // INFO: category · phone · confidence%
    container.addView(TextView(context).apply {
        text = context.getString(AppR.string.overlay_info_fmt, categoryText, phoneNumber, confidence)
        textSize = 12f
        setTextColor(0xCCFFFFFF.toInt())
        gravity = Gravity.CENTER
        setPadding(0, 16, 0, 16)
    })

    // Divider
    container.addView(android.view.View(context).apply {
        setBackgroundColor(0x33FFFFFF)
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 1
        ).apply { setMargins(0, 8, 0, 8) }
    })

    // REASONS: max 2
    for (reason in reasons.take(2)) {
        container.addView(TextView(context).apply {
            text = "· $reason"
            textSize = 13f
            setTextColor(0xDDFFFFFF.toInt())
            setPadding(0, 6, 0, 6)
        })
    }

    // Divider
    container.addView(android.view.View(context).apply {
        setBackgroundColor(0x33FFFFFF)
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 1
        ).apply { setMargins(0, 12, 0, 12) }
    })

    // 행동 버튼: 수신 / 거절 / 차단
    val actionRow = LinearLayout(context)
    actionRow.orientation = LinearLayout.HORIZONTAL
    actionRow.gravity = Gravity.CENTER
    actionRow.setPadding(0, 12, 0, 8)

    val actionLabels = getOverlayActionLabels(context)
    val actionColors = intArrayOf(0xFF2E7D32.toInt(), 0xFFF57F17.toInt(), 0xFFD32F2F.toInt())
    val actionTypes = arrayOf(UserCallAction.ANSWERED, UserCallAction.REJECTED, UserCallAction.BLOCKED)

    for (i in actionLabels.indices) {
        val btn = TextView(context)
        btn.text = actionLabels[i]
        btn.textSize = 13f
        btn.setTextColor(0xFFFFFFFF.toInt())
        btn.gravity = Gravity.CENTER
        btn.setPadding(24, 12, 24, 12)
        btn.setBackgroundColor(actionColors[i])
        btn.setOnClickListener {
            // 기록 자동 저장 — 오버레이 액션 시 UserCallRecord에 기록
            onAction(phoneNumber, actionTypes[i])
            try { wm.removeView(container) } catch (_: Exception) {}
        }
        val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        params.setMargins(4, 0, 4, 0)
        actionRow.addView(btn, params)
    }
    container.addView(actionRow)

    // DISMISS button — 언어별 로컬라이즈
    val dismissText = getDismissText(context)
    container.addView(TextView(context).apply {
        text = context.getString(AppR.string.dismiss_with_icon_fmt, dismissText)
        textSize = 12f
        setTextColor(0x99FFFFFF.toInt())
        gravity = Gravity.CENTER
        setPadding(0, 8, 0, 4)
        setOnClickListener {
            try { wm.removeView(container) } catch (_: Exception) {}
        }
    })

    val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
    } else {
        @Suppress("DEPRECATION")
        WindowManager.LayoutParams.TYPE_PHONE
    }

    val params = WindowManager.LayoutParams(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        layoutType,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
        PixelFormat.TRANSLUCENT,
    ).apply {
        gravity = Gravity.TOP
    }

    try {
        wm.addView(container, params)
    } catch (_: Exception) {
    }
}

/**
 * OverlayUiText.oneWordVerdict() 로직 복제 — 7개 언어 × 4 RiskLevel
 */
private fun getOneWordVerdict(risk: RiskLevel, context: Context): String = when (risk) {
    RiskLevel.HIGH -> context.getString(AppR.string.overlay_verdict_high)
    RiskLevel.MEDIUM -> context.getString(AppR.string.overlay_verdict_medium)
    RiskLevel.LOW -> context.getString(AppR.string.overlay_verdict_low)
    RiskLevel.UNKNOWN -> context.getString(AppR.string.overlay_verdict_unknown)
}

/**
 * 데모용 근거 2개 — 실 서비스에서는 SearchResultAnalyzer 결과에서 추출
 */
private fun getDemoReasons(risk: RiskLevel, context: Context): List<String> = when (risk) {
    RiskLevel.HIGH -> listOf(
        context.getString(AppR.string.demo_reason_high_1),
        context.getString(AppR.string.demo_reason_high_2),
    )
    RiskLevel.MEDIUM -> listOf(
        context.getString(AppR.string.demo_reason_medium_1),
        context.getString(AppR.string.demo_reason_medium_2),
    )
    RiskLevel.LOW -> listOf(
        context.getString(AppR.string.demo_reason_low_1),
        context.getString(AppR.string.demo_reason_low_2),
    )
    RiskLevel.UNKNOWN -> listOf(
        context.getString(AppR.string.demo_reason_unknown_1),
        context.getString(AppR.string.demo_reason_unknown_2),
    )
}

/**
 * 오버레이 닫기 버튼 텍스트 — 7개 언어
 */
private fun getDismissText(context: Context): String = context.getString(AppR.string.common_close)

/**
 * 오버레이 행동 버튼 라벨 — [수신, 거절, 차단]
 */
private fun getOverlayActionLabels(context: Context): List<String> = listOf(
    context.getString(AppR.string.overlay_action_answer),
    context.getString(AppR.string.overlay_action_reject),
    context.getString(AppR.string.overlay_action_block),
)

/**
 * 홈 화면 버튼 subtitle — 행동 유도형 (설명 → 액션)
 */
private fun getActionSubtitle(risk: RiskLevel, context: Context): String = when (risk) {
    RiskLevel.HIGH -> context.getString(AppR.string.action_subtitle_high)
    RiskLevel.MEDIUM -> context.getString(AppR.string.action_subtitle_medium)
    RiskLevel.LOW -> context.getString(AppR.string.action_subtitle_low)
    RiskLevel.UNKNOWN -> context.getString(AppR.string.action_subtitle_unknown)
}

// ═══════════════════════════════════════════════════════════
// 설정 화면 — 증거 5: PrivacyTrustMessages 설정 삽입
// ═══════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen(
    languageProvider: LanguageContextProvider,
    onBack: () -> Unit,
    onNavigateToBackup: () -> Unit = {},
) {
    val language = languageProvider.resolveLanguage()
    val context = LocalContext.current
    val msg = PrivacyTrustMessages(context)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(msg.settingsPrivacyTitle, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = context.getString(AppR.string.common_back), tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D1B2A)),
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0D1B2A))
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A3A2A)),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Shield,
                            contentDescription = null,
                            tint = Color(0xFF81C784),
                            modifier = Modifier.size(36.dp),
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = context.getString(AppR.string.settings_security_card_title),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF81C784),
                        )
                    }
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 14.dp),
                        color = Color(0xFF81C784).copy(alpha = 0.35f),
                    )
                    val securityBullets = listOf(
                        context.getString(AppR.string.settings_security_bullet_no_server),
                        context.getString(AppR.string.settings_security_bullet_no_transfer),
                        context.getString(AppR.string.settings_security_bullet_aes),
                        context.getString(AppR.string.settings_security_bullet_on_device),
                    )
                    for (line in securityBullets) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                Icons.Filled.Check,
                                contentDescription = null,
                                tint = Color(0xFF81C784),
                                modifier = Modifier.size(20.dp),
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = line,
                                fontSize = 15.sp,
                                color = Color(0xFF81C784),
                                lineHeight = 22.sp,
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Privacy section — 자비스 구조: 신뢰 확정형
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2838)),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        Icons.Filled.Shield,
                        contentDescription = null,
                        tint = Color(0xFF4FC3F7),
                        modifier = Modifier.size(40.dp),
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // HERO headline — 강력 선언
                    Text(
                        text = msg.onboardingPrivacyCore,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4FC3F7),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // 3줄 확정 선언 — ✓ 체크 아이콘 + 정보 카드 (클릭 불가)
                    val pledgeLines = msg.onboardingNoServerPledge.split("\n")
                    for (line in pledgeLines) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1B2A)),
                            shape = RoundedCornerShape(8.dp),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = Color(0xFF4FC3F7),
                                    modifier = Modifier.size(20.dp),
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = line,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White,
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 보조 설명 — 작게
                    Text(
                        text = msg.onboardingPrivacyDetail,
                        fontSize = 12.sp,
                        color = Color(0xFF607D8B),
                        lineHeight = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 녹색 박스 — 강력 선언형
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A3A2A)),
                shape = RoundedCornerShape(16.dp),
            ) {
                Text(
                    text = msg.settingsDataHandling,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF81C784),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    lineHeight = 24.sp,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Language info
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2838)),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = context.getString(AppR.string.language_label),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = context.getString(AppR.string.language_current_fmt, language.code.uppercase()),
                        fontSize = 14.sp,
                        color = Color(0xFFB0BEC5),
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 백업 및 복원 카드
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToBackup() },
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2838)),
                shape = RoundedCornerShape(16.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            Icons.Filled.Security,
                            contentDescription = null,
                            tint = Color(0xFF4FC3F7),
                            modifier = Modifier.size(24.dp),
                        )
                        Column {
                            Text(
                                text = context.getString(AppR.string.backup_restore_title),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = context.getString(AppR.string.backup_restore_desc),
                                fontSize = 12.sp,
                                color = Color(0xFFB0BEC5),
                            )
                        }
                    }
                    Icon(
                        Icons.Filled.ChevronRight,
                        contentDescription = null,
                        tint = Color(0xFFB0BEC5),
                        modifier = Modifier.size(24.dp),
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// 결제 직전 화면 — 증거 5 + 증거 6: Privacy + Pricing 연결
// ═══════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PurchaseScreen(
    languageProvider: LanguageContextProvider,
    onBack: () -> Unit,
) {
    val language = languageProvider.resolveLanguage()
    val context = LocalContext.current
    val msg = PrivacyTrustMessages(context)
    val countryCode = CountryConfigProviderImpl().detectCountry(context)
    val tier = CountryPricingMapper.getTier(countryCode)
    val pricingMsg = PricingUiMessages.forLanguage(language)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CallCheck Premium", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = context.getString(AppR.string.common_back), tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D1B2A)),
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0D1B2A))
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // ═══════════════════════════════════════════════════════
            // VISUAL HIERARCHY (자비스 Stage 19):
            //   1. 가치 제안 — 왜 돈을 내야 하는가 (불안 제거)
            //   2. 가격 + 무료 체험 — 핵심 정보
            //   3. 체험 해지 안내 — 경계심 해소
            //   4. 구매 CTA — 최강 시각 우선순위
            //   5. 지역 가격 설명 — "왜 이 가격?" 해소
            //   6. 프라이버시 보장 — 신뢰 연출
            //   7. 해지 버튼 — 보이되 CTA보다 약하게
            //   8. 리펀드/해지 정책 — 맨 아래 투명 공개
            // ═══════════════════════════════════════════════════════

            // ── 1. 핵심 가치 제안 (불안 제거 결제) ──
            Text(
                text = pricingMsg.valueProposition,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
            )

            Spacer(modifier = Modifier.height(28.dp))

            // ── 2. 가격 + 무료 체험 카드 ──
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B3548)),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4FC3F7)),
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    // 무료 체험
                    Text(
                        text = pricingMsg.formatFreeTrial(tier.freeTrialDays),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4FC3F7),
                        textAlign = TextAlign.Center,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 월간 가격 — 단일 플랜
                    Text(
                        text = pricingMsg.formatMonthlyPrice(tier.monthlyPriceUsd),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── 3. 체험 중 해지 가능 안내 — 경계심 해소 ──
            Text(
                text = pricingMsg.trialCancelNote,
                fontSize = 13.sp,
                color = Color(0xFF81C784),
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── 4. 구매 CTA — 최강 시각 우선순위 ──
            Button(
                onClick = { /* billing flow */ },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4FC3F7)),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(
                    text = pricingMsg.subscribeButton,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0D1B2A),
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── 5. 지역별 가격 차이 설명 ──
            Text(
                text = pricingMsg.regionalPricingNote,
                fontSize = 12.sp,
                color = Color(0xFF78909C),
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── 6. 프라이버시 보장 ──
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A3A2A)),
                shape = RoundedCornerShape(12.dp),
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        Icons.Filled.Shield,
                        contentDescription = null,
                        tint = Color(0xFF81C784),
                        modifier = Modifier.size(24.dp),
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = msg.purchasePrivacyGuarantee,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF81C784),
                        lineHeight = 18.sp,
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── 7. 해지 버튼 — 보이되 CTA보다 약하게 ──
            OutlinedButton(
                onClick = { /* cancel subscription flow via Play Billing */ },
                modifier = Modifier.fillMaxWidth().height(44.dp),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF546E7A)),
            ) {
                Text(
                    text = pricingMsg.cancelSubscriptionButton,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF90A4AE),
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── 8. 리펀드 불가 + 서비스 유지 안내 ──
            Text(
                text = pricingMsg.noRefundNotice,
                fontSize = 12.sp,
                color = Color(0xFF607D8B),
                textAlign = TextAlign.Center,
                lineHeight = 16.sp,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = pricingMsg.cancellationNote,
                fontSize = 11.sp,
                color = Color(0xFF546E7A),
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tier & country info (디버그용)
            Text(
                text = context.getString(
                    AppR.string.country_tier_debug_fmt,
                    countryCode ?: context.getString(AppR.string.common_unknown),
                    tier.tierId,
                    language.code.uppercase()
                ),
                fontSize = 11.sp,
                color = Color(0xFF37474F),
            )
        }
    }
}

// PricingCard 제거됨 — 단일 월간 구독 전환 (2026-03-31)

// ═══════════════════════════════════════════════════════════
// 통화 기록 화면 — 사용자 기록 레이어
// ═══════════════════════════════════════════════════════════

/**
 * 통화 기록 라벨 — 7개 언어
 */
private fun getCallHistoryLabel(context: Context): String = context.getString(AppR.string.call_history_title)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CallHistoryScreen(
    languageProvider: LanguageContextProvider,
    viewModel: CallHistoryViewModel,
    onBack: () -> Unit,
    onRecordClick: (String) -> Unit,
) {
    val language = languageProvider.resolveLanguage()
    val context = LocalContext.current
    val records by viewModel.allRecords.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(getCallHistoryLabel(context), color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = context.getString(AppR.string.common_back), tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D1B2A)),
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0D1B2A))
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 요약 헤더
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2838)),
                shape = RoundedCornerShape(12.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    HistoryStatItem("${records.size}", getRecordCountLabel(context))
                    HistoryStatItem(
                        "${records.count { it.lastAction == "blocked" }}",
                        getBlockedLabel(context),
                    )
                    HistoryStatItem(
                        "${records.count { it.memo?.isNotBlank() == true }}",
                        getMemoCountLabel(context),
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (records.isEmpty()) {
                // 빈 상태
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = getEmptyHistoryLabel(context),
                        fontSize = 14.sp,
                        color = Color(0xFF607D8B),
                    )
                }
            } else {
                // 기록 리스트 — Room DB Flow 실시간 반영
                for (record in records) {
                    CallRecordItem(
                        record = record,
                        language = language,
                        onClick = { onRecordClick(record.canonicalNumber) },
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ═══════════════════════════════════════════════════════════
// 번호별 상세 화면 — AI 판단 + 사용자 기록(메모/태그/행동)
// ═══════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun CallDetailScreen(
    canonicalNumber: String,
    languageProvider: LanguageContextProvider,
    viewModel: CallHistoryViewModel,
    onBack: () -> Unit,
) {
    val language = languageProvider.resolveLanguage()
    val context = LocalContext.current

    // allRecords에서 직접 필터링 — 가장 확실한 방식
    val allRecords by viewModel.allRecords.collectAsState()
    val record = allRecords.find { it.canonicalNumber == canonicalNumber }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(getDetailLabel(context), color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = context.getString(AppR.string.common_back), tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D1B2A)),
            )
        }
    ) { padding ->
        if (record == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF0D1B2A))
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = Color(0xFF4FC3F7))
            }
            return@Scaffold
        }

        val rec = record!!

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0D1B2A))
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // ── 번호 헤더 ──
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2838)),
                shape = RoundedCornerShape(12.dp),
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = rec.displayNumber,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = context.getString(
                            AppR.string.call_count_action_fmt,
                            rec.callCount,
                            getActionText(rec.lastAction, context),
                        ),
                        fontSize = 14.sp,
                        color = Color(0xFF607D8B),
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── AI 판단 섹션 ──
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2838)),
                shape = RoundedCornerShape(12.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = getAiJudgmentLabel(context),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4FC3F7),
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val riskColor = when (rec.aiRiskLevel) {
                        "HIGH" -> Color(0xFFD32F2F)
                        "MEDIUM" -> Color(0xFFF57F17)
                        "LOW" -> Color(0xFF2E7D32)
                        else -> Color(0xFF455A64)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "●", fontSize = 14.sp, color = riskColor)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = context.getString(
                                AppR.string.risk_with_category_fmt,
                                getRiskLabel(rec.aiRiskLevel, context),
                                rec.aiCategory ?: context.getString(AppR.string.common_na),
                            ),
                            fontSize = 14.sp,
                            color = Color.White,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── 태그 선택 섹션 ──
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2838)),
                shape = RoundedCornerShape(12.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = getTagSectionLabel(context),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4FC3F7),
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // 태그 칩 행
                    val allTags = UserCallTag.values()
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        for (tag in allTags) {
                            val isSelected = rec.tag == tag.displayKey
                            val chipColor = getTagColor(tag.displayKey)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    viewModel.saveTag(canonicalNumber, tag)
                                },
                                label = {
                                    Text(
                                        text = getTagDisplayName(tag, context),
                                        fontSize = 12.sp,
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = chipColor.copy(alpha = 0.3f),
                                    selectedLabelColor = chipColor,
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = if (isSelected) chipColor else Color(0xFF455A64),
                                    selectedBorderColor = chipColor,
                                    enabled = true,
                                    selected = isSelected,
                                ),
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── 메모 섹션 ──
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2838)),
                shape = RoundedCornerShape(12.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = getMemoSectionLabel(context),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4FC3F7),
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    var memoText by remember(rec.memo) {
                        mutableStateOf(rec.memo ?: "")
                    }
                    var isEditing by remember { mutableStateOf(false) }

                    if (isEditing) {
                        OutlinedTextField(
                            value = memoText,
                            onValueChange = { memoText = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text(getMemoPlaceholder(context), color = Color(0xFF607D8B)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color(0xFF4FC3F7),
                                focusedBorderColor = Color(0xFF4FC3F7),
                                unfocusedBorderColor = Color(0xFF455A64),
                            ),
                            maxLines = 4,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                        ) {
                            TextButton(onClick = {
                                memoText = rec.memo ?: ""
                                isEditing = false
                            }) {
                                Text(getCancelLabel(context), color = Color(0xFF607D8B))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    viewModel.saveMemo(canonicalNumber, memoText)
                                    isEditing = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4FC3F7)),
                            ) {
                                Text(getSaveLabel(context), color = Color(0xFF0D1B2A))
                            }
                        }
                    } else {
                        if (rec.memo.isNullOrBlank()) {
                            Text(
                                text = getMemoPlaceholder(context),
                                fontSize = 13.sp,
                                color = Color(0xFF607D8B),
                            )
                        } else {
                            Text(
                                text = rec.memo!!,
                                fontSize = 14.sp,
                                color = Color(0xFFB0BEC5),
                                lineHeight = 20.sp,
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = { isEditing = true },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF4FC3F7)),
                        ) {
                            Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(getEditMemoLabel(context), fontSize = 13.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── 행동 버튼 ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                val isBlocked = rec.lastAction == "blocked"

                // 차단/해제 버튼
                Button(
                    onClick = { viewModel.toggleBlock(canonicalNumber, isBlocked) },
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isBlocked) Color(0xFF2E7D32) else Color(0xFFD32F2F),
                    ),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Icon(
                        if (isBlocked) Icons.Filled.CheckCircle else Icons.Filled.Block,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isBlocked) getUnblockLabel(context) else getBlockLabel(context),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                // 삭제 버튼
                OutlinedButton(
                    onClick = {
                        viewModel.deleteRecord(canonicalNumber)
                        onBack()
                    },
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(getDeleteLabel(context), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ── 통화 기록 리스트 아이템 (Room DB entity 사용) ──

@Composable
private fun HistoryStatItem(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4FC3F7),
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF607D8B),
        )
    }
}

@Composable
private fun CallRecordItem(
    record: UserCallRecord,
    language: SupportedLanguage,
    onClick: () -> Unit,
) {
    val context = LocalContext.current
    val tagColor = getTagColor(record.tag)

    val actionIcon = when (record.lastAction) {
        "answered" -> "✓"
        "rejected" -> "✕"
        "blocked" -> "⊘"
        else -> "?"
    }

    val riskColor = when (record.aiRiskLevel) {
        "HIGH" -> Color(0xFFD32F2F)
        "MEDIUM" -> Color(0xFFF57F17)
        "LOW" -> Color(0xFF2E7D32)
        else -> Color(0xFF455A64)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2838)),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = record.displayNumber,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                    Text(
                        text = context.getString(AppR.string.call_count_icon_fmt, record.callCount, actionIcon),
                        fontSize = 12.sp,
                        color = Color(0xFF607D8B),
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "●", fontSize = 10.sp, color = riskColor)
                    Spacer(modifier = Modifier.width(8.dp))
                    record.tag?.let { tagValue ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = tagColor.copy(alpha = 0.2f)),
                            shape = RoundedCornerShape(4.dp),
                        ) {
                            Text(
                                text = tagValue,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = tagColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            )
                        }
                    }
                }
            }

            record.memo?.takeIf { it.isNotBlank() }?.let { memoValue ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = memoValue,
                    fontSize = 13.sp,
                    color = Color(0xFFB0BEC5),
                    lineHeight = 18.sp,
                    maxLines = 2,
                )
            }

            // 탭하여 상세보기 힌트
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "›",
                fontSize = 16.sp,
                color = Color(0xFF455A64),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
            )
        }
    }
}

private fun getRecordCountLabel(context: Context): String = context.getString(AppR.string.call_record_count)
private fun getBlockedLabel(context: Context): String = context.getString(AppR.string.call_blocked_count)
private fun getMemoCountLabel(context: Context): String = context.getString(AppR.string.call_memo_count)
private fun getEmptyHistoryLabel(context: Context): String = context.getString(AppR.string.call_history_empty)

// ── 상세 화면 로컬라이즈 ──

private fun getDetailLabel(context: Context): String = context.getString(AppR.string.call_detail_title)
private fun getAiJudgmentLabel(context: Context): String = context.getString(AppR.string.call_ai_judgment)
private fun getRiskLabel(risk: String?, context: Context): String = when (risk) {
    "HIGH" -> context.getString(AppR.string.risk_high)
    "MEDIUM" -> context.getString(AppR.string.risk_medium)
    "LOW" -> context.getString(AppR.string.risk_low)
    else -> context.getString(AppR.string.risk_unknown)
}
private fun getActionText(action: String?, context: Context): String = when (action) {
    "answered" -> context.getString(AppR.string.action_answered)
    "rejected" -> context.getString(AppR.string.action_rejected)
    "blocked" -> context.getString(AppR.string.action_blocked)
    "missed" -> context.getString(AppR.string.action_missed)
    else -> context.getString(AppR.string.action_no_record)
}
private fun getTagSectionLabel(context: Context): String = context.getString(AppR.string.call_tag_section)
private fun getTagDisplayName(tag: UserCallTag, context: Context): String = when (tag) {
    UserCallTag.SAFE -> context.getString(AppR.string.tag_safe)
    UserCallTag.SPAM -> context.getString(AppR.string.tag_spam)
    UserCallTag.BUSINESS -> context.getString(AppR.string.tag_business)
    UserCallTag.PERSONAL -> context.getString(AppR.string.tag_personal)
    UserCallTag.DELIVERY -> context.getString(AppR.string.tag_delivery)
    UserCallTag.CUSTOM -> context.getString(AppR.string.tag_custom)
}

private fun getTagColor(tag: String?): Color {
    return when (tag) {
        "spam" -> Color(0xFFD32F2F)
        "safe" -> Color(0xFF2E7D32)
        "business" -> Color(0xFF1565C0)
        "personal" -> Color(0xFF7B1FA2)
        "delivery" -> Color(0xFFFF8F00)
        else -> Color(0xFF455A64)
    }
}

private fun getMemoSectionLabel(context: Context): String = context.getString(AppR.string.call_memo_section)
private fun getMemoPlaceholder(context: Context): String = context.getString(AppR.string.call_memo_placeholder)
private fun getEditMemoLabel(context: Context): String = context.getString(AppR.string.call_edit_memo)
private fun getSaveLabel(context: Context): String = context.getString(AppR.string.common_save)
private fun getCancelLabel(context: Context): String = context.getString(AppR.string.common_cancel)
private fun getBlockLabel(context: Context): String = context.getString(AppR.string.common_block)
private fun getUnblockLabel(context: Context): String = context.getString(AppR.string.common_unblock)
private fun getDeleteLabel(context: Context): String = context.getString(AppR.string.common_delete)

// ═══════════════════════════════════════════════════════════
// MessageCheck 허브 (작업8)
// ═══════════════════════════════════════════════════════════

private fun MessageHubEntity.firstDetectedLink(): String? =
    detectedLinks
        ?.split(',')
        ?.map { it.trim() }
        ?.firstOrNull { it.startsWith("http://", ignoreCase = true) || it.startsWith("https://", ignoreCase = true) }

private fun MessageHubEntity.previewText(maxLen: Int = 50): String {
    val raw = listOfNotNull(title, text).joinToString(" ").trim()
    return if (raw.length <= maxLen) raw else raw.take(maxLen) + "…"
}

private fun formatMessageHubTime(epochMillis: Long): String {
    val zoned = Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault())
    val fmt = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withZone(ZoneId.systemDefault())
    return fmt.format(zoned)
}

private fun formatPrivacyUsedAt(epochMillis: Long): String {
    val zoned = Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault())
    val fmt = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withZone(ZoneId.systemDefault())
    return fmt.format(zoned)
}

private fun openSearchUrl(context: Context, baseQueryUrl: String, rawQuery: String) {
    val enc = URLEncoder.encode(rawQuery, StandardCharsets.UTF_8.toString())
    val uri = Uri.parse(baseQueryUrl + enc)
    context.startActivity(
        Intent(Intent.ACTION_VIEW, uri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun MessageHubScreen(
    viewModel: MessageHubViewModel,
    onBack: () -> Unit,
) {
    val messages by viewModel.messages.collectAsState()
    val context = LocalContext.current
    var linkDialogUrl by remember { mutableStateOf<String?>(null) }
    val keptMarker = stringResource(AppR.string.message_hub_kept_marker)

    linkDialogUrl?.let { url ->
        AlertDialog(
            onDismissRequest = { linkDialogUrl = null },
            containerColor = Color(0xFF1A2A3A),
            titleContentColor = Color.White,
            textContentColor = Color(0xFFB0BEC5),
            title = { Text(stringResource(AppR.string.message_hub_pick_search)) },
            text = {
                Column {
                    TextButton(
                        onClick = {
                            openSearchUrl(
                                context,
                                "https://search.naver.com/search.naver?query=",
                                url,
                            )
                            linkDialogUrl = null
                        },
                    ) {
                        Text(stringResource(AppR.string.message_hub_search_naver), color = Color(0xFF4FC3F7))
                    }
                    TextButton(
                        onClick = {
                            openSearchUrl(
                                context,
                                "https://www.google.com/search?q=",
                                url,
                            )
                            linkDialogUrl = null
                        },
                    ) {
                        Text(stringResource(AppR.string.message_hub_search_google), color = Color(0xFF4FC3F7))
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { linkDialogUrl = null }) {
                    Text(stringResource(AppR.string.common_close), color = Color(0xFF607D8B))
                }
            },
        )
    }

    Scaffold(
        containerColor = Color(0xFF0D1B2A),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(AppR.string.message_hub_title), color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = stringResource(AppR.string.common_back),
                            tint = Color.White,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D1B2A)),
            )
        },
    ) { padding ->
        if (messages.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    stringResource(AppR.string.message_hub_empty),
                    color = Color(0xFF607D8B),
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(messages, key = { it.id }) { item ->
                    MessageHubListItem(
                        item = item,
                        onLinkClick = { url -> linkDialogUrl = url },
                        onKeep = {
                            viewModel.keepMessage(item.id, keptMarker)
                        },
                        onDelete = {
                            viewModel.deleteMessage(item.id)
                        },
                        onBlock = {
                            viewModel.blockSender(item.packageName)
                        },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MessageHubListItem(
    item: MessageHubEntity,
    onLinkClick: (String) -> Unit,
    onKeep: () -> Unit,
    onDelete: () -> Unit,
    onBlock: () -> Unit,
) {
    val link = remember(item.detectedLinks) { item.firstDetectedLink() }
    val timeText = remember(item.receivedAt) { formatMessageHubTime(item.receivedAt) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2A3A)),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = item.appLabel,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.previewText(),
                fontSize = 13.sp,
                color = Color(0xFFB0BEC5),
                maxLines = 3,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = timeText,
                fontSize = 11.sp,
                color = Color(0xFF607D8B),
            )
            if (item.linkCount > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                val linkRowModifier = if (link != null) {
                    Modifier
                        .fillMaxWidth()
                        .clickable { onLinkClick(link) }
                        .padding(vertical = 4.dp)
                } else {
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                }
                Row(
                    modifier = linkRowModifier,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        Icons.Filled.Warning,
                        contentDescription = stringResource(AppR.string.message_hub_link_warning),
                        tint = Color(0xFFFFB74D),
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        stringResource(AppR.string.message_hub_link_warning),
                        fontSize = 12.sp,
                        color = Color(0xFFFFB74D),
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                TextButton(onClick = onKeep) {
                    Text(stringResource(AppR.string.message_hub_action_keep), color = Color(0xFF81C784))
                }
                TextButton(onClick = onDelete) {
                    Text(stringResource(AppR.string.message_hub_action_delete), color = Color(0xFFE57373))
                }
                TextButton(onClick = onBlock) {
                    Text(stringResource(AppR.string.message_hub_action_block), color = Color(0xFFFFB74D))
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// PrivacyCheck 히스토리 (작업9)
// ═══════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PrivacyHistoryScreen(
    viewModel: PrivacyHistoryViewModel,
    onBack: () -> Unit,
) {
    val history by viewModel.history.collectAsState()
    val context = LocalContext.current
    var tabIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = Color(0xFF0D1B2A),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(AppR.string.privacy_history_title), color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = stringResource(AppR.string.common_back),
                            tint = Color.White,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D1B2A)),
            )
        },
    ) { padding ->
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(AppR.string.privacy_requires_android12),
                    color = Color(0xFF607D8B),
                    textAlign = TextAlign.Center,
                )
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            TabRow(
                selectedTabIndex = tabIndex,
                containerColor = Color(0xFF0A1628),
                contentColor = Color(0xFF4FC3F7),
            ) {
                Tab(
                    selected = tabIndex == 0,
                    onClick = { tabIndex = 0 },
                    text = {
                        Text(
                            stringResource(AppR.string.privacy_tab_camera),
                            color = if (tabIndex == 0) Color.White else Color(0xFF607D8B),
                        )
                    },
                )
                Tab(
                    selected = tabIndex == 1,
                    onClick = { tabIndex = 1 },
                    text = {
                        Text(
                            stringResource(AppR.string.privacy_tab_microphone),
                            color = if (tabIndex == 1) Color.White else Color(0xFF607D8B),
                        )
                    },
                )
            }

            val filtered = remember(history, tabIndex) {
                if (tabIndex == 0) {
                    history.filter { it.permissionType == "CAMERA" }
                } else {
                    history.filter { it.permissionType == "MICROPHONE" }
                }
            }

            if (filtered.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        stringResource(AppR.string.privacy_history_empty),
                        color = Color(0xFF607D8B),
                        textAlign = TextAlign.Center,
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(filtered, key = { it.id }) { row ->
                        PrivacyHistoryListItem(
                            item = row,
                            onConfirm = {
                                viewModel.updateVerified(row.id, "CONFIRMED")
                            },
                            onNotMe = {
                                viewModel.updateVerified(row.id, "DENIED")
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", row.appPackage, null)
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                context.startActivity(intent)
                            },
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PrivacyHistoryListItem(
    item: PrivacyHistoryEntity,
    onConfirm: () -> Unit,
    onNotMe: () -> Unit,
) {
    val context = LocalContext.current
    val pm = context.packageManager
    val bitmap = remember(item.appPackage) {
        try {
            pm.getApplicationIcon(item.appPackage).toBitmap()
        } catch (_: Exception) {
            null
        }
    }
    val usedAtText = remember(item.usedAt) { formatPrivacyUsedAt(item.usedAt) }
    val durationMin = (item.durationSec / 60).toInt()
    val durationSecRem = (item.durationSec % 60).toInt()
    val durationText = stringResource(
        AppR.string.privacy_duration_fmt,
        durationMin,
        durationSecRem,
    )
    val showAnomaly = item.isAnomaly && item.userVerified == "UNVERIFIED"
    val borderColor = if (showAnomaly) Color(0xFFE57373) else Color(0xFF2A3F54)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2A3A)),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = item.appLabel,
                    modifier = Modifier.size(40.dp),
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF2A3F54), RoundedCornerShape(8.dp)),
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.appLabel, color = Color.White, fontWeight = FontWeight.SemiBold)
                Text(usedAtText, fontSize = 11.sp, color = Color(0xFF607D8B))
                Text(durationText, fontSize = 12.sp, color = Color(0xFFB0BEC5))
                if (showAnomaly) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Warning,
                            contentDescription = null,
                            tint = Color(0xFFE57373),
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            stringResource(
                                when (item.permissionType) {
                                    "CAMERA" -> AppR.string.privacy_permission_camera
                                    else -> AppR.string.privacy_permission_microphone
                                },
                            ),
                            fontSize = 12.sp,
                            color = Color(0xFFE57373),
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        TextButton(onClick = onConfirm) {
                            Text(
                                stringResource(AppR.string.privacy_anomaly_confirm),
                                color = Color(0xFF81C784),
                            )
                        }
                        TextButton(onClick = onNotMe) {
                            Text(
                                stringResource(AppR.string.privacy_anomaly_denied),
                                color = Color(0xFFFFB74D),
                            )
                        }
                    }
                }
            }
        }
    }
}
