package app.myphonecheck.mobile.navigation

import android.Manifest
import android.app.AppOpsManager
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
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Mic
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
import androidx.lifecycle.ViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import app.myphonecheck.mobile.R as AppR
import app.myphonecheck.mobile.core.model.ActionState
import app.myphonecheck.mobile.core.model.InterceptEventType
import app.myphonecheck.mobile.core.model.ImportanceLevel
import app.myphonecheck.mobile.core.model.RiskLevel
import app.myphonecheck.mobile.core.model.SearchStatus
import app.myphonecheck.mobile.core.model.UserCallAction
import app.myphonecheck.mobile.core.model.UserCallTag
import app.myphonecheck.mobile.core.model.displayLabelKo
import app.myphonecheck.mobile.data.localcache.entity.MessageHubEntity
import app.myphonecheck.mobile.data.localcache.entity.PrivacyHistoryEntity
import app.myphonecheck.mobile.data.localcache.entity.QuickLabel
import app.myphonecheck.mobile.data.localcache.entity.UserCallRecord
import app.myphonecheck.mobile.data.localcache.repository.NumberProfileSnapshot
import app.myphonecheck.mobile.feature.countryconfig.*
import app.myphonecheck.mobile.core.util.DecisionReasoningFormatter
import app.myphonecheck.mobile.core.util.DecisionReasoningFormatter.Lang
import app.myphonecheck.mobile.feature.decisionui.components.FullEngineReasoningSection
import app.myphonecheck.mobile.feature.privacycheck.R as PrivacyR
import app.myphonecheck.mobile.feature.cardcheck.R as CardCheckR
import app.myphonecheck.mobile.feature.cardcheck.ui.CardCheckRoute
import app.myphonecheck.mobile.feature.callcheck.R as CallCheckR
import app.myphonecheck.mobile.feature.callcheck.ui.CallCheckRoute
import app.myphonecheck.mobile.feature.messagecheck.R as MessageCheckR
import app.myphonecheck.mobile.feature.messagecheck.ui.MessageCheckRoute
import app.myphonecheck.mobile.feature.initialscan.R as InitialScanR
import app.myphonecheck.mobile.feature.initialscan.ui.InitialScanRoute
import app.myphonecheck.mobile.feature.pushtrash.R as PushTrashR
import app.myphonecheck.mobile.feature.pushtrash.ui.AppBlockSettingsRoute
import app.myphonecheck.mobile.feature.pushtrash.ui.PushTrashAppsRoute
import app.myphonecheck.mobile.feature.pushtrash.ui.PushTrashBinRoute
import app.myphonecheck.mobile.feature.pushtrash.ui.PushTrashMainRoute
import app.myphonecheck.mobile.ui.backup.BackupScreen
import app.myphonecheck.mobile.viewmodel.CallHistoryViewModel
import app.myphonecheck.mobile.viewmodel.MessageHubViewModel
import app.myphonecheck.mobile.viewmodel.CameraCheckViewModel
import app.myphonecheck.mobile.viewmodel.InitialScanGuardViewModel
import app.myphonecheck.mobile.viewmodel.MicCheckViewModel
import app.myphonecheck.mobile.viewmodel.PrivacyHistoryViewModel
import app.myphonecheck.mobile.feature.privacycheck.GuardResult
import app.myphonecheck.mobile.feature.privacycheck.InitialScanGuard
import app.myphonecheck.mobile.feature.privacycheck.ScanStatus
import app.myphonecheck.mobile.feature.privacycheck.SensorAppInfo
import app.myphonecheck.mobile.feature.privacycheck.SensorCheckState
import app.myphonecheck.mobile.feature.privacycheck.StatusLevel
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
private const val PREFS_NAME = "myphonecheck_app_prefs"
private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"

@Composable
fun MyPhoneCheckNavHost(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val appPrefs = remember(context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    val startDestination = remember(appPrefs) {
        if (appPrefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)) "home" else "onboarding"
    }
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
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable("onboarding") {
                OnboardingScreen(
                    languageProvider = languageProvider,
                    onContinue = {
                        appPrefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, true).apply()
                        navController.navigate("home") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    }
                )
            }
            composable("home") {
                val cameraCheckViewModel: CameraCheckViewModel = hiltViewModel()
                val micCheckViewModel: MicCheckViewModel = hiltViewModel()
                val guardViewModel: InitialScanGuardViewModel = hiltViewModel()
                HomeScreen(
                    languageProvider = languageProvider,
                    onPurchaseClick = { navController.navigate("purchase") },
                    viewModel = historyViewModel,
                    cameraCheckViewModel = cameraCheckViewModel,
                    micCheckViewModel = micCheckViewModel,
                    guardViewModel = guardViewModel,
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
            composable("camera-check") {
                val cameraVm: CameraCheckViewModel = hiltViewModel()
                val guardVm: InitialScanGuardViewModel = hiltViewModel()
                SensorCheckDetailScreen(
                    title = stringResource(AppR.string.camera_check_title),
                    icon = Icons.Filled.Videocam,
                    color = Color(0xFFE57373),
                    viewModel = cameraVm,
                    guardViewModel = guardVm,
                    onBack = { navController.popBackStack() },
                )
            }
            composable("mic-check") {
                val micVm: MicCheckViewModel = hiltViewModel()
                val guardVm: InitialScanGuardViewModel = hiltViewModel()
                SensorCheckDetailScreen(
                    title = stringResource(AppR.string.mic_check_title),
                    icon = Icons.Filled.Mic,
                    color = Color(0xFFFFB74D),
                    viewModel = micVm,
                    guardViewModel = guardVm,
                    onBack = { navController.popBackStack() },
                )
            }
            composable("engine/call") {
                EngineDetailScreen(
                    engineName = context.getString(AppR.string.engine_name_myphonecheck),
                    engineNameKo = context.getString(AppR.string.engine_title_call),
                    eventType = InterceptEventType.CALL,
                    color = Color(0xFF4FC3F7),
                    icon = Icons.Filled.Phone,
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
                    onNavigateToPushTrash = { navController.navigate("push-trash") },
                    onNavigateToCardCheck = { navController.navigate("card-check") },
                    onNavigateToCallCheck = { navController.navigate("call-check") },
                    onNavigateToMessageCheck = { navController.navigate("message-check") },
                    onNavigateToInitialScan = { navController.navigate("initial-scan") },
                    onRestartOnboarding = {
                        // 온보딩 완료 플래그 동기 제거 → 네비게이션 → 뒤로가기 스택 초기화
                        appPrefs.edit()
                            .putBoolean(KEY_ONBOARDING_COMPLETED, false)
                            .commit()
                        navController.navigate("onboarding") {
                            popUpTo(navController.graph.id) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
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
            composable("push-trash") {
                PushTrashMainRoute(
                    onBack = { navController.popBackStack() },
                    onOpenBin = { navController.navigate("push-trash/bin") },
                    onOpenApps = { navController.navigate("push-trash/apps") },
                )
            }
            composable("push-trash/bin") {
                PushTrashBinRoute(onBack = { navController.popBackStack() })
            }
            composable("push-trash/apps") {
                PushTrashAppsRoute(
                    onBack = { navController.popBackStack() },
                    onOpenApp = { pkg ->
                        val enc = URLEncoder.encode(pkg, StandardCharsets.UTF_8)
                        navController.navigate("push-trash/app/$enc")
                    },
                )
            }
            composable(
                route = "push-trash/app/{packageName}",
                arguments = listOf(navArgument("packageName") { type = NavType.StringType }),
            ) {
                AppBlockSettingsRoute(onBack = { navController.popBackStack() })
            }
            composable("card-check") {
                CardCheckRoute(onBack = { navController.popBackStack() })
            }
            composable("call-check") {
                CallCheckRoute(onBack = { navController.popBackStack() })
            }
            composable("message-check") {
                MessageCheckRoute(onBack = { navController.popBackStack() })
            }
            composable("initial-scan") {
                InitialScanRoute(
                    onComplete = { navController.popBackStack() },
                    onSkip = { navController.popBackStack() },
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

// ─────────────────────────────────────────────────────────────
// 추가 런타임 권한 체크 — Settings 화면에서 표시/변경
// ─────────────────────────────────────────────────────────────

private fun Context.hasContactsPermission(): Boolean =
    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) ==
        PackageManager.PERMISSION_GRANTED

/** v4.3: READ_CALL_LOG removed — always returns false */
private fun Context.hasCallLogPermission(): Boolean = false

/** v4.3: READ_SMS removed — always returns false */
private fun Context.hasReadSmsPermission(): Boolean = false

private fun Context.hasReceiveSmsPermission(): Boolean =
    ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) ==
        PackageManager.PERMISSION_GRANTED

/**
 * POST_NOTIFICATIONS 은 Android 13 (API 33) 이상에서만 런타임 권한.
 * 이하 버전에서는 항상 true (시스템이 자동 허용).
 */
private fun Context.hasPostNotificationsPermission(): Boolean =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
    } else {
        true
    }

/**
 * ANSWER_PHONE_CALLS 은 Android 8 (API 26) 이상에서만 런타임 권한.
 * 이하 버전에서는 해당 기능 미지원 — 항상 false 반환하여 UI에서 숨김 처리 가능.
 */
private fun Context.hasAnswerCallsPermission(): Boolean =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        ContextCompat.checkSelfPermission(this, Manifest.permission.ANSWER_PHONE_CALLS) ==
            PackageManager.PERMISSION_GRANTED
    } else {
        false
    }

/**
 * 앱 세부정보 설정 화면으로 이동.
 * Android 11+ 런타임 권한 거부가 영구화된 경우 설정에서만 재허용 가능.
 */
private fun Context.openAppDetailsSettings() {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.parse("package:$packageName"),
    ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
    startActivity(intent)
}

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
    cameraCheckViewModel: CameraCheckViewModel,
    micCheckViewModel: MicCheckViewModel,
    guardViewModel: InitialScanGuardViewModel,
    onEngineClick: (String) -> Unit = {},
) {
    languageProvider.resolveLanguage()
    val context = LocalContext.current

    // Guard: baseline 유효성 확인 — FAIL이면 완료 UI 차단
    val guardResult by guardViewModel.guardResult.collectAsState()
    val guardPassed = guardResult.passed

    // ViewModel 상태 수집
    val cameraState by cameraCheckViewModel.state.collectAsState()
    val micState by micCheckViewModel.state.collectAsState()

    // Guard PASS이고 데이터가 STALE이면 백그라운드 refresh
    // Guard FAIL이면 scan 호출하지 않음 (Application.onCreate에서 처리)
    LaunchedEffect(guardPassed) {
        if (guardPassed) {
            cameraCheckViewModel.scan()
            micCheckViewModel.scan()
        }
    }

    // Guard 적용된 상태 — FAIL이면 강제 NOT_SCANNED/SCANNING 표시
    val displayCameraState = if (guardPassed) cameraState else {
        if (cameraState.scanStatus == ScanStatus.SCANNING) cameraState
        else SensorCheckState(scanStatus = ScanStatus.NOT_SCANNED)
    }
    val displayMicState = if (guardPassed) micState else {
        if (micState.scanStatus == ScanStatus.SCANNING) micState
        else SensorCheckState(scanStatus = ScanStatus.NOT_SCANNED)
    }

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
            // Row 1: CallCheck + Message intelligence
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
                    question = stringResource(AppR.string.engine_message_question),
                    description = stringResource(AppR.string.engine_message_desc),
                    icon = Icons.Filled.Message,
                    color = Color(0xFF81C784),
                    modifier = Modifier.weight(1f),
                    onClick = { onEngineClick("message-hub") },
                )
            }

            // Row 2: CameraCheck + MicCheck
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                SensorEngineCard(
                    question = stringResource(AppR.string.engine_camera_question),
                    description = stringResource(AppR.string.engine_camera_desc),
                    icon = Icons.Filled.Videocam,
                    color = Color(0xFFE57373),
                    scanState = displayCameraState,
                    modifier = Modifier.weight(1f),
                    onClick = { onEngineClick("camera-check") },
                )
                SensorEngineCard(
                    question = stringResource(AppR.string.engine_mic_question),
                    description = stringResource(AppR.string.engine_mic_desc),
                    icon = Icons.Filled.Mic,
                    color = Color(0xFFFFB74D),
                    scanState = displayMicState,
                    modifier = Modifier.weight(1f),
                    onClick = { onEngineClick("mic-check") },
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

/**
 * SensorEngineCard — Camera/Mic 전용 엔진 카드.
 *
 * EngineCard와 동일한 180dp 카드 레이아웃에
 * ScanStatus 기반 실시간 상태 표시 추가.
 *
 * 상태 표시:
 * - NOT_SCANNED → 회색 점 + "미스캔"
 * - SCANNING → 노란색 점 + "스캔중"
 * - SCANNED → 색상 점 + "N개 앱 허용" (grantedAppCount 기반)
 */
@Composable
private fun SensorEngineCard(
    question: String,
    description: String,
    icon: ImageVector,
    color: Color,
    scanState: SensorCheckState,
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
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(32.dp),
            )

            Text(
                text = question,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                lineHeight = 22.sp,
            )

            Column {
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = Color(0xFF78909C),
                    lineHeight = 14.sp,
                )
                Spacer(modifier = Modifier.height(6.dp))

                val (dotColor, statusText) = when (scanState.scanStatus) {
                    ScanStatus.NOT_SCANNED -> Color(0xFF607D8B) to
                        context.getString(AppR.string.scan_status_not_scanned)
                    ScanStatus.SCANNING -> Color(0xFFFFD54F) to
                        context.getString(AppR.string.scan_status_scanning)
                    ScanStatus.SCANNED -> color to
                        context.getString(
                            AppR.string.scan_status_granted_count,
                            scanState.grantedAppCount,
                        )
                    ScanStatus.STALE -> color.copy(alpha = 0.6f) to
                        context.getString(
                            AppR.string.scan_status_granted_count,
                            scanState.grantedAppCount,
                        )
                    ScanStatus.FAILED -> Color(0xFFE57373) to
                        context.getString(AppR.string.scan_status_failed)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(dotColor, shape = RoundedCornerShape(3.dp)),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = statusText,
                        fontSize = 10.sp,
                        color = dotColor.copy(alpha = 0.7f),
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// 엔진 상세 화면 — 4개 엔진 공통 레이아웃
// ═══════════════════════════════════════════════════════════

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MessageHubProfileItem(
    item: MessageHubEntity,
    numberProfile: NumberProfileSnapshot?,
    onLinkClick: (String) -> Unit,
    onDelete: () -> Unit,
    onBlock: () -> Unit,
    onDeleteAndBlock: () -> Unit,
    onQuickLabelToggle: (QuickLabel) -> Unit,
    onAddDetailTag: (String) -> Unit,
    onRemoveDetailTag: (String) -> Unit,
    onSaveShortMemo: (String) -> Unit,
    onDoNotBlock: () -> Unit,
) {
    val link = remember(item.detectedLinks) { item.firstDetectedLink() }
    val timeText = remember(item.receivedAt) { formatMessageHubTime(item.receivedAt) }
    val meta = remember(item.reasons) { parseMessageHubMeta(item.reasons) }
    var detailTagInput by remember(item.id) { mutableStateOf("") }
    var memoInput by remember(numberProfile?.userMemoShort) { mutableStateOf(numberProfile?.userMemoShort.orEmpty()) }
    val searchStatus = remember(item.summary, meta.searchSummary) {
        SearchStatus.fromStoredMessage(
            searchSummary = meta.searchSummary,
            fallbackSummary = item.summary,
        ).labelKo
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2A3A)),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val context = LocalContext.current
            Text("${context.getString(AppR.string.detail_label_number)}  ${item.title ?: item.appLabel}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(6.dp))
            Text("${context.getString(AppR.string.detail_label_time)}  $timeText", fontSize = 13.sp, color = Color(0xFFB0BEC5))
            Spacer(modifier = Modifier.height(6.dp))
            Text("${context.getString(AppR.string.detail_label_search_summary)}  ${meta.searchSummary ?: item.summary}", fontSize = 13.sp, color = Color(0xFFFFCC80), maxLines = 1)
            Spacer(modifier = Modifier.height(6.dp))
            Text("${context.getString(AppR.string.detail_label_similar_number)}  ${meta.similarNumberLabel ?: context.getString(AppR.string.detail_label_not_confirmed)}", fontSize = 13.sp, color = Color(0xFFB0BEC5), maxLines = 1)
            Spacer(modifier = Modifier.height(6.dp))
            Text(searchStatus, fontSize = 13.sp, color = Color(0xFF80CBC4))
            val importance = runCatching { ImportanceLevel.valueOf(item.importanceLevel) }
                .getOrDefault(ImportanceLevel.UNKNOWN)
            if (importance != ImportanceLevel.UNKNOWN) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "${context.getString(AppR.string.detail_label_importance)}  ${importance.name} (${item.importanceReason ?: "-"})",
                    fontSize = 13.sp,
                    color = Color(0xFFB39DDB),
                    maxLines = 1,
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            numberProfile?.actionState?.displayLabelKo()?.let { actionStateLabel ->
                Text(actionStateLabel, fontSize = 13.sp, color = Color(0xFFA5D6A7))
                Spacer(modifier = Modifier.height(6.dp))
            }
            if (item.linkCount > 0 && link != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onLinkClick(link) }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        Icons.Filled.Warning,
                        contentDescription = null,
                        tint = Color(0xFFFFB74D),
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("${context.getString(AppR.string.detail_label_link_warning)}  ${meta.linkWarning ?: context.getString(AppR.string.detail_label_link_exists)}", fontSize = 13.sp, color = Color(0xFFFFB74D), maxLines = 1)
                }
            } else {
                Text("${context.getString(AppR.string.detail_label_link_warning)}  ${context.getString(AppR.string.detail_label_link_none)}", fontSize = 13.sp, color = Color(0xFFB0BEC5))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(context.getString(AppR.string.detail_action_remember), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF81D4FA))
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                QuickLabel.entries.forEach { label ->
                    FilterChip(
                        selected = numberProfile?.quickLabels?.contains(label) == true,
                        onClick = { onQuickLabelToggle(label) },
                        label = { Text(label.displayName) },
                    )
                }
            }
            val summaryChips = remember(numberProfile) {
                buildList {
                    numberProfile?.quickLabels?.forEach { add(it.displayName) }
                    numberProfile?.detailTags?.forEach { add(it.tagName) }
                    if (false) when (numberProfile?.actionState) {
                        ActionState.BLOCKED -> add(context.getString(AppR.string.detail_state_blocked))
                        ActionState.DO_NOT_BLOCK -> add(context.getString(AppR.string.detail_state_do_not_block))
                        else -> Unit
                    }
                }
            }
            if (summaryChips.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    summaryChips.forEach { chip ->
                        AssistChip(onClick = {}, label = { Text(chip) })
                    }
                }
            }
            if (!numberProfile?.detailTags.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    numberProfile!!.detailTags.forEach { tag ->
                        InputChip(
                            selected = false,
                            onClick = { onRemoveDetailTag(tag.tagName) },
                            label = { Text(tag.tagName) },
                            trailingIcon = {
                                Icon(Icons.Filled.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                            },
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = detailTagInput,
                onValueChange = { detailTagInput = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(context.getString(AppR.string.detail_tag_placeholder)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF4FC3F7),
                    unfocusedBorderColor = Color(0xFF455A64),
                ),
                singleLine = true,
            )
            Row {
                TextButton(
                    onClick = {
                        onAddDetailTag(detailTagInput)
                        detailTagInput = ""
                    },
                    enabled = detailTagInput.isNotBlank(),
                ) {
                    Text(context.getString(AppR.string.detail_tag_add), color = Color(0xFF81C784))
                }
                TextButton(onClick = onDoNotBlock) {
                    Text(context.getString(AppR.string.detail_state_do_not_block), color = Color(0xFF64B5F6))
                }
            }
            OutlinedTextField(
                value = memoInput,
                onValueChange = { memoInput = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(context.getString(AppR.string.detail_memo_placeholder)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF4FC3F7),
                    unfocusedBorderColor = Color(0xFF455A64),
                ),
                maxLines = 2,
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = { onSaveShortMemo(memoInput) }) {
                    Text(context.getString(AppR.string.detail_memo_save), color = Color(0xFF81D4FA))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = onDelete) { Text(context.getString(AppR.string.detail_action_delete), color = Color(0xFFE57373)) }
                TextButton(onClick = onBlock) { Text(context.getString(AppR.string.detail_action_block), color = Color(0xFFFFB74D)) }
                TextButton(onClick = onDeleteAndBlock) { Text(context.getString(AppR.string.detail_action_both), color = Color(0xFF81C784)) }
            }
        }
    }
}

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
    val engineColor = Color(0xFF4FC3F7) // Default to MyPhoneCheck color
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
    onNavigateToPushTrash: () -> Unit = {},
    onNavigateToCardCheck: () -> Unit = {},
    onNavigateToCallCheck: () -> Unit = {},
    onNavigateToMessageCheck: () -> Unit = {},
    onNavigateToInitialScan: () -> Unit = {},
    onRestartOnboarding: () -> Unit = {},
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

            // ═══════════════════════════════════════════════════════
            // 권한 관리 — 온보딩에서 설정한 항목을 여기서 보고 변경
            // ═══════════════════════════════════════════════════════
            SettingsPermissionsSection()

            Spacer(modifier = Modifier.height(16.dp))

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

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToPushTrash() },
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
                            Icons.Filled.Notifications,
                            contentDescription = null,
                            tint = Color(0xFF4FC3F7),
                            modifier = Modifier.size(24.dp),
                        )
                        Column {
                            Text(
                                text = context.getString(PushTrashR.string.push_trash_settings_entry_title),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = context.getString(PushTrashR.string.push_trash_settings_entry_desc),
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

            Spacer(modifier = Modifier.height(16.dp))

            // CardCheck 진입 카드 (Architecture v1.9.0 §27 Stage 1-002)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToCardCheck() },
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
                            Icons.Filled.CreditCard,
                            contentDescription = null,
                            tint = Color(0xFF4FC3F7),
                            modifier = Modifier.size(24.dp),
                        )
                        Column {
                            Text(
                                text = context.getString(CardCheckR.string.card_check_settings_entry_title),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = context.getString(CardCheckR.string.card_check_settings_entry_desc),
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

            Spacer(modifier = Modifier.height(16.dp))

            // CallCheck 진입 카드 (Architecture v2.0.0 §21 Stage 2-002)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToCallCheck() },
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
                            Icons.Filled.Phone,
                            contentDescription = null,
                            tint = Color(0xFF4FC3F7),
                            modifier = Modifier.size(24.dp),
                        )
                        Column {
                            Text(
                                text = context.getString(CallCheckR.string.call_check_settings_entry_title),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = context.getString(CallCheckR.string.call_check_settings_entry_desc),
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

            Spacer(modifier = Modifier.height(16.dp))

            // MessageCheck 진입 카드 (Architecture v2.0.0 §22 Stage 2-003)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToMessageCheck() },
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
                            Icons.Filled.Message,
                            contentDescription = null,
                            tint = Color(0xFF4FC3F7),
                            modifier = Modifier.size(24.dp),
                        )
                        Column {
                            Text(
                                text = context.getString(MessageCheckR.string.message_check_settings_entry_title),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = context.getString(MessageCheckR.string.message_check_settings_entry_desc),
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

            Spacer(modifier = Modifier.height(16.dp))

            // Initial Scan 진입 카드 (Architecture v2.0.0 §28)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToInitialScan() },
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
                            Icons.Filled.Refresh,
                            contentDescription = null,
                            tint = Color(0xFF4FC3F7),
                            modifier = Modifier.size(24.dp),
                        )
                        Column {
                            Text(
                                text = context.getString(InitialScanR.string.initial_scan_settings_entry_title),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = context.getString(InitialScanR.string.initial_scan_settings_entry_desc),
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

            Spacer(modifier = Modifier.height(16.dp))

            // ═══════════════════════════════════════════════════════
            // 온보딩 다시 보기 — 대표님 지시: 온보딩에서 설정한 것을
            // 설정 화면에서 보고 변경할 수 있게. 이 카드는 "재진입" 경로.
            // ═══════════════════════════════════════════════════════
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onRestartOnboarding() },
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
                            Icons.Filled.Shield,
                            contentDescription = null,
                            tint = Color(0xFF4FC3F7),
                            modifier = Modifier.size(24.dp),
                        )
                        Column {
                            Text(
                                text = context.getString(AppR.string.settings_restart_onboarding_title),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = context.getString(AppR.string.settings_restart_onboarding_desc),
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
// Settings > 권한 관리 섹션
// — 접근성(3) · 통화·메시지(6) · 알림(1) 총 10종
// — Lifecycle.ON_RESUME 시 자동 새로고침 (시스템 설정에서 변경 반영)
// — 온보딩 Page 5 의 OnboardingPermissionRow 재사용 (DRY)
// ═══════════════════════════════════════════════════════════

@Composable
private fun SettingsPermissionsSection() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var refreshTrigger by remember { mutableIntStateOf(0) }

    // 포그라운드 복귀 시 자동 새로고침 (온보딩 Page 5 와 동일 패턴)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                refreshTrigger++
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // ── 접근성 그룹 (특수 권한 — 시스템 설정으로 직행) ──
    val overlayOk = remember(refreshTrigger) { context.hasDrawOverlayPermission() }
    val usageStatsOk = remember(refreshTrigger) { context.hasUsageStatsPermission() }

    // ── 통화·메시지 그룹 (런타임 권한) ──
    val phoneStateOk = remember(refreshTrigger) { context.hasReadPhoneStatePermission() }
    val contactsOk = remember(refreshTrigger) { context.hasContactsPermission() }
    // v4.3: callLogOk, readSmsOk removed — READ_CALL_LOG / READ_SMS denied per PERMISSION POLICY
    val receiveSmsOk = remember(refreshTrigger) { context.hasReceiveSmsPermission() }
    val answerCallsOk = remember(refreshTrigger) { context.hasAnswerCallsPermission() }

    // ── 알림 그룹 ──
    val postNotifOk = remember(refreshTrigger) { context.hasPostNotificationsPermission() }

    // 런타임 권한 요청 런처 — Manifest permission 이름을 직접 전달
    val phoneStateLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { refreshTrigger++ }
    val contactsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { refreshTrigger++ }
    // v4.3: callLogLauncher, readSmsLauncher removed — READ_CALL_LOG / READ_SMS denied per PERMISSION POLICY
    val receiveSmsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { refreshTrigger++ }
    val answerCallsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { refreshTrigger++ }
    val postNotifLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { refreshTrigger++ }

    // 섹션 헤더
    Text(
        text = context.getString(AppR.string.settings_permissions_header),
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = Modifier.padding(bottom = 12.dp),
    )

    // ─── 접근성 그룹 ───────────────────────────────────
    PermissionGroupHeader(
        text = context.getString(AppR.string.settings_perm_group_accessibility),
    )
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
                ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                context.startActivity(intent)
            }
        },
    )
    OnboardingPermissionRow(
        icon = Icons.Filled.Security,
        title = context.getString(AppR.string.perm_usage_stats_title),
        description = context.getString(AppR.string.perm_usage_stats_desc),
        granted = usageStatsOk,
        onAllow = {
            context.startActivity(
                Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                    .apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) },
            )
        },
    )

    Spacer(modifier = Modifier.height(16.dp))

    // ─── 통화·메시지 그룹 ──────────────────────────────
    PermissionGroupHeader(
        text = context.getString(AppR.string.settings_perm_group_phone_sms),
    )
    OnboardingPermissionRow(
        icon = Icons.Filled.Phone,
        title = context.getString(AppR.string.perm_phone_state_title),
        description = context.getString(AppR.string.perm_phone_state_desc),
        granted = phoneStateOk,
        onAllow = {
            if (phoneStateOk) {
                context.openAppDetailsSettings()
            } else {
                phoneStateLauncher.launch(Manifest.permission.READ_PHONE_STATE)
            }
        },
    )
    OnboardingPermissionRow(
        icon = Icons.Filled.Phone,
        title = context.getString(AppR.string.perm_contacts_title),
        description = context.getString(AppR.string.perm_contacts_desc),
        granted = contactsOk,
        onAllow = {
            if (contactsOk) {
                context.openAppDetailsSettings()
            } else {
                contactsLauncher.launch(Manifest.permission.READ_CONTACTS)
            }
        },
    )
    // v4.3: OnboardingPermissionRow for READ_CALL_LOG and READ_SMS removed — denied per PERMISSION POLICY
    OnboardingPermissionRow(
        icon = Icons.Filled.Message,
        title = context.getString(AppR.string.perm_sms_receive_title),
        description = context.getString(AppR.string.perm_sms_receive_desc),
        granted = receiveSmsOk,
        onAllow = {
            if (receiveSmsOk) {
                context.openAppDetailsSettings()
            } else {
                receiveSmsLauncher.launch(Manifest.permission.RECEIVE_SMS)
            }
        },
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        OnboardingPermissionRow(
            icon = Icons.Filled.Phone,
            title = context.getString(AppR.string.perm_answer_calls_title),
            description = context.getString(AppR.string.perm_answer_calls_desc),
            granted = answerCallsOk,
            onAllow = {
                if (answerCallsOk) {
                    context.openAppDetailsSettings()
                } else {
                    answerCallsLauncher.launch(Manifest.permission.ANSWER_PHONE_CALLS)
                }
            },
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    // ─── 알림 그룹 ─────────────────────────────────────
    PermissionGroupHeader(
        text = context.getString(AppR.string.settings_perm_group_notification),
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        OnboardingPermissionRow(
            icon = Icons.Filled.Notifications,
            title = context.getString(AppR.string.perm_post_notifications_title),
            description = context.getString(AppR.string.perm_post_notifications_desc),
            granted = postNotifOk,
            onAllow = {
                if (postNotifOk) {
                    context.openAppDetailsSettings()
                } else {
                    postNotifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            },
        )
    }
}

@Composable
private fun PermissionGroupHeader(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFF4FC3F7),
        modifier = Modifier.padding(top = 4.dp, bottom = 8.dp, start = 4.dp),
    )
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
                title = { Text("MyPhoneCheck Premium", color = Color.White) },
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
    allLinkTokens().firstOrNull {
        it.startsWith("http://", ignoreCase = true) || it.startsWith("https://", ignoreCase = true)
    }

private fun MessageHubEntity.allLinkTokens(): List<String> =
    detectedLinks
        ?.removePrefix("[")
        ?.removeSuffix("]")
        ?.split(',')
        ?.map { it.trim().trim('"') }
        ?.filter { it.isNotEmpty() }
        ?: emptyList()

private data class MessageHubMeta(
    val searchSummary: String?,
    val similarNumberLabel: String?,
    val linkWarning: String?,
)

private fun parseMessageHubMeta(reasons: String?): MessageHubMeta {
    if (reasons.isNullOrBlank()) return MessageHubMeta(null, null, null)

    var searchSummary: String? = null
    var similarNumberLabel: String? = null
    var linkWarning: String? = null

    reasons.split('|').forEach { token ->
        when {
            token.startsWith("SEARCH=") -> searchSummary = token.removePrefix("SEARCH=")
            token.startsWith("SIMILAR=") -> similarNumberLabel = token.removePrefix("SIMILAR=")
            token.startsWith("LINK=") -> linkWarning = token.removePrefix("LINK=")
        }
    }

    return MessageHubMeta(searchSummary, similarNumberLabel, linkWarning)
}

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
    val numberProfiles by viewModel.numberProfiles.collectAsState()
    val context = LocalContext.current
    var linkDialogUrl by remember { mutableStateOf<String?>(null) }

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
                    MessageHubProfileItem(
                        item = item,
                        numberProfile = numberProfiles[item.packageName],
                        onLinkClick = { url -> linkDialogUrl = url },
                        onDelete = {
                            viewModel.deleteMessage(item.id)
                        },
                        onBlock = {
                            viewModel.blockSender(item.packageName)
                        },
                        onDeleteAndBlock = {
                            viewModel.blockAndDelete(item.id, item.packageName)
                        },
                        onQuickLabelToggle = { label ->
                            viewModel.toggleQuickLabel(item.packageName, label)
                        },
                        onAddDetailTag = { tagName ->
                            viewModel.addDetailTag(item.packageName, tagName)
                        },
                        onRemoveDetailTag = { tagName ->
                            viewModel.removeDetailTag(item.packageName, tagName)
                        },
                        onSaveShortMemo = { memo ->
                            viewModel.saveShortMemo(item.packageName, memo)
                        },
                        onDoNotBlock = {
                            viewModel.setDoNotBlock(item.packageName)
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
    numberProfile: NumberProfileSnapshot?,
    onLinkClick: (String) -> Unit,
    onDelete: () -> Unit,
    onBlock: () -> Unit,
    onDeleteAndBlock: () -> Unit,
    onQuickLabelToggle: (QuickLabel) -> Unit,
    onAddDetailTag: (String) -> Unit,
    onRemoveDetailTag: (String) -> Unit,
    onSaveShortMemo: (String) -> Unit,
    onDoNotBlock: () -> Unit,
) {
    val link = remember(item.detectedLinks) { item.firstDetectedLink() }
    val timeText = remember(item.receivedAt) { formatMessageHubTime(item.receivedAt) }
    val meta = remember(item.reasons) { parseMessageHubMeta(item.reasons) }
    var detailTagInput by remember(item.id) { mutableStateOf("") }
    var memoInput by remember(numberProfile?.userMemoShort) { mutableStateOf(numberProfile?.userMemoShort.orEmpty()) }
    val searchStatus = remember(item.summary, meta.searchSummary) {
        SearchStatus.fromStoredMessage(
            searchSummary = meta.searchSummary,
            fallbackSummary = item.summary,
        ).labelKo
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2A3A)),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val context = LocalContext.current
            Text(
                text = "${context.getString(AppR.string.detail_label_number)}  ${item.title ?: item.appLabel}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${context.getString(AppR.string.detail_label_time)}  $timeText",
                fontSize = 13.sp,
                color = Color(0xFFB0BEC5),
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${context.getString(AppR.string.detail_label_search_summary)}  ${meta.searchSummary ?: item.summary}",
                fontSize = 13.sp,
                color = Color(0xFFFFCC80),
                maxLines = 1,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${context.getString(AppR.string.detail_label_similar_number)}  ${meta.similarNumberLabel ?: context.getString(AppR.string.detail_label_not_confirmed)}",
                fontSize = 13.sp,
                color = Color(0xFFB0BEC5),
                maxLines = 1,
            )
            Spacer(modifier = Modifier.height(6.dp))
            val importance = runCatching { ImportanceLevel.valueOf(item.importanceLevel) }
                .getOrDefault(ImportanceLevel.UNKNOWN)
            if (importance != ImportanceLevel.UNKNOWN) {
                Text(
                    text = "${context.getString(AppR.string.detail_label_importance)}  ${importance.name} (${item.importanceReason ?: "-"})",
                    fontSize = 13.sp,
                    color = Color(0xFFB39DDB),
                    maxLines = 1,
                )
                Spacer(modifier = Modifier.height(6.dp))
            }
            if (item.linkCount > 0 && link != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onLinkClick(link) }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        Icons.Filled.Warning,
                        contentDescription = null,
                        tint = Color(0xFFFFB74D),
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${context.getString(AppR.string.detail_label_link_warning)}  ${meta.linkWarning ?: context.getString(AppR.string.detail_label_link_exists)}",
                        fontSize = 13.sp,
                        color = Color(0xFFFFB74D),
                        maxLines = 1,
                    )
                }
            } else {
                Text(
                    text = "${context.getString(AppR.string.detail_label_link_warning)}  ${context.getString(AppR.string.detail_label_link_none)}",
                    fontSize = 13.sp,
                    color = Color(0xFFB0BEC5),
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                TextButton(onClick = onDelete) {
                    Text(context.getString(AppR.string.detail_action_delete), color = Color(0xFFE57373))
                }
                TextButton(onClick = onBlock) {
                    Text(context.getString(AppR.string.detail_action_block), color = Color(0xFFFFB74D))
                }
                TextButton(onClick = onDeleteAndBlock) {
                    Text(context.getString(AppR.string.detail_action_both), color = Color(0xFF81C784))
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// ═══════════════════════════════════════════════════════════
// CameraCheck / MicCheck 상세 화면
// ═══════════════════════════════════════════════════════════

/**
 * SensorCheckDetailScreen — Camera/Mic 공용 상세 화면.
 *
 * 구성:
 * - 헤더: 아이콘 + 타이틀 + 스캔 상태 요약
 * - 권한 보유 앱 목록 (grantedApps)
 * - 최근 사용 앱 목록 (recentApps)
 * - 마지막 사용 시각
 *
 * ViewModel은 CameraCheckViewModel 또는 MicCheckViewModel 중 하나를 전달받음.
 * 둘 다 StateFlow<SensorCheckState>를 노출하므로 동일 UI로 처리.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SensorCheckDetailScreen(
    title: String,
    icon: ImageVector,
    color: Color,
    viewModel: ViewModel,
    guardViewModel: InitialScanGuardViewModel,
    onBack: () -> Unit,
) {
    val rawState by when (viewModel) {
        is CameraCheckViewModel -> viewModel.state.collectAsState()
        is MicCheckViewModel -> viewModel.state.collectAsState()
        else -> remember { mutableStateOf(SensorCheckState()) }
    }

    // Guard: baseline 유효성 확인
    val guardResult by guardViewModel.guardResult.collectAsState()
    val guardPassed = guardResult.passed

    // Guard FAIL이면 완료 데이터 표시 차단
    val state = if (guardPassed) rawState else {
        when (rawState.scanStatus) {
            ScanStatus.SCANNING -> rawState.copy(
                grantedApps = emptyList(),
                recentApps = emptyList(),
            )
            else -> SensorCheckState(scanStatus = ScanStatus.NOT_SCANNED)
        }
    }

    // Guard PASS이고 미스캔/STALE이면 백그라운드 refresh
    LaunchedEffect(guardPassed) {
        if (guardPassed && (rawState.scanStatus == ScanStatus.NOT_SCANNED || rawState.scanStatus == ScanStatus.STALE)) {
            when (viewModel) {
                is CameraCheckViewModel -> viewModel.scan()
                is MicCheckViewModel -> viewModel.scan()
            }
        }
    }

    val context = LocalContext.current
    val dateFormatter = remember {
        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
            .withZone(ZoneId.systemDefault())
    }

    Scaffold(
        containerColor = Color(0xFF0D1B2A),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier.size(24.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(title, color = Color.White)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0A1628)),
            )
        },
    ) { innerPadding ->
        when (state.scanStatus) {
            ScanStatus.NOT_SCANNED -> {
                // Guard FAIL 또는 baseline 미존재 — 초기화 필요 안내
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = Color(0xFF607D8B),
                            modifier = Modifier.size(48.dp),
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = context.getString(AppR.string.scan_status_init_required),
                            color = Color(0xFF78909C),
                            fontSize = 14.sp,
                        )
                    }
                }
            }

            ScanStatus.SCANNING -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = color)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = context.getString(AppR.string.scan_status_scanning),
                            color = Color(0xFF78909C),
                            fontSize = 14.sp,
                        )
                    }
                }
            }

            ScanStatus.FAILED -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = null,
                            tint = Color(0xFFE57373),
                            modifier = Modifier.size(48.dp),
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = context.getString(AppR.string.scan_status_failed),
                            color = Color(0xFFE57373),
                            fontSize = 14.sp,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        TextButton(onClick = {
                            when (viewModel) {
                                is CameraCheckViewModel -> viewModel.scan()
                                is MicCheckViewModel -> viewModel.scan()
                            }
                        }) {
                            Text(
                                text = context.getString(AppR.string.scan_retry),
                                color = color,
                            )
                        }
                    }
                }
            }

            ScanStatus.SCANNED,
            ScanStatus.STALE -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                ) {
                    // 요약 헤더
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        SensorSummaryCard(
                            state = state,
                            color = color,
                            dateFormatter = dateFormatter,
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // 권한 보유 앱 섹션
                    item {
                        Text(
                            text = context.getString(AppR.string.sensor_granted_apps_header),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    if (state.grantedApps.isEmpty()) {
                        item {
                            Text(
                                text = context.getString(AppR.string.sensor_no_granted_apps),
                                fontSize = 13.sp,
                                color = Color(0xFF607D8B),
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    } else {
                        items(state.grantedApps.size) { index ->
                            SensorAppItem(
                                app = state.grantedApps[index],
                                color = color,
                                dateFormatter = dateFormatter,
                            )
                        }
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }

                    // 최근 사용 앱 섹션
                    item {
                        Text(
                            text = context.getString(AppR.string.sensor_recent_apps_header),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    if (state.recentApps.isEmpty()) {
                        item {
                            Text(
                                text = context.getString(AppR.string.sensor_no_recent_apps),
                                fontSize = 13.sp,
                                color = Color(0xFF607D8B),
                            )
                        }
                    } else {
                        items(state.recentApps.size) { index ->
                            SensorAppItem(
                                app = state.recentApps[index],
                                color = color,
                                dateFormatter = dateFormatter,
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(32.dp)) }
                }
            }
        }
    }
}

@Composable
private fun SensorSummaryCard(
    state: SensorCheckState,
    color: Color,
    dateFormatter: DateTimeFormatter,
) {
    val context = LocalContext.current
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2A3A)),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(
                            when (state.statusLevel) {
                                StatusLevel.NORMAL -> Color(0xFF4FC3F7)
                                StatusLevel.CAUTION -> Color(0xFFE57373)
                                StatusLevel.UNKNOWN -> Color(0xFF607D8B)
                            },
                            shape = RoundedCornerShape(5.dp),
                        ),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = context.getString(
                        AppR.string.scan_status_granted_count,
                        state.grantedAppCount,
                    ),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }

            if (state.recentAppCount > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${context.getString(AppR.string.sensor_recent_apps_header)}: ${state.recentAppCount}",
                    fontSize = 14.sp,
                    color = color,
                )
            }

            if (state.lastUsedAt != null) {
                Spacer(modifier = Modifier.height(4.dp))
                val formatted = dateFormatter.format(Instant.ofEpochMilli(state.lastUsedAt!!))
                Text(
                    text = "${context.getString(AppR.string.sensor_last_used_label)}: $formatted",
                    fontSize = 12.sp,
                    color = Color(0xFF78909C),
                )
            }
        }
    }
}

@Composable
private fun SensorAppItem(
    app: SensorAppInfo,
    color: Color,
    dateFormatter: DateTimeFormatter,
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (app.isKnownSafe) Color(0xFF1A2A3A) else Color(0xFF2A1A1A),
        ),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = app.appLabel,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = app.packageName,
                    fontSize = 11.sp,
                    color = Color(0xFF607D8B),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Text(
                        text = context.getString(AppR.string.sensor_days_installed, app.daysSinceInstall),
                        fontSize = 11.sp,
                        color = Color(0xFF78909C),
                    )
                    if (app.lastUsedAt != null) {
                        Spacer(modifier = Modifier.width(12.dp))
                        val formatted = dateFormatter.format(Instant.ofEpochMilli(app.lastUsedAt!!))
                        Text(
                            text = "${context.getString(AppR.string.sensor_last_used_label)}: $formatted",
                            fontSize = 11.sp,
                            color = Color(0xFF78909C),
                        )
                    }
                }
            }

            if (app.isKnownSafe) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = context.getString(AppR.string.sensor_known_safe_badge),
                    tint = Color(0xFF4FC3F7),
                    modifier = Modifier.size(20.dp),
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = null,
                    tint = Color(0xFFE57373),
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

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
                if (item.isAnomaly) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        stringResource(PrivacyR.string.privacy_anomaly_reason_label),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFCC80),
                    )
                    val legacyText = stringResource(PrivacyR.string.privacy_anomaly_reason_legacy)
                    val anomalyBody = remember(item.anomalyReasons, legacyText) {
                        DecisionReasoningFormatter.privacyAnomalyDisplayText(item.anomalyReasons, legacyText)
                    }
                    Text(
                        text = anomalyBody,
                        fontSize = 11.sp,
                        color = Color(0xFFE0E0E0),
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
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
