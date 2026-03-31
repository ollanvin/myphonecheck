package app.callcheck.mobile.navigation

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import app.callcheck.mobile.core.model.InterceptEventType
import app.callcheck.mobile.core.model.RiskLevel
import app.callcheck.mobile.core.model.UserCallAction
import app.callcheck.mobile.core.model.UserCallTag
import app.callcheck.mobile.data.localcache.entity.UserCallRecord
import app.callcheck.mobile.feature.countryconfig.*
import app.callcheck.mobile.viewmodel.CallHistoryViewModel
import java.net.URLDecoder
import java.net.URLEncoder

// ═══════════════════════════════════════════════════════════
// Bottom Navigation — 3탭: 홈 / 기록 / 설정
// ═══════════════════════════════════════════════════════════

private enum class BottomTab(
    val route: String,
    val icon: ImageVector,
    val label: String,
) {
    HOME("home", Icons.Filled.Home, "홈"),
    TIMELINE("timeline", Icons.Filled.Timeline, "타임라인"),
    SETTINGS("settings", Icons.Filled.Settings, "설정"),
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
                                    contentDescription = tab.label,
                                )
                            },
                            label = { Text(tab.label, fontSize = 11.sp) },
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
                    context = context,
                    viewModel = historyViewModel,
                    onEngineClick = { route -> navController.navigate(route) },
                )
            }
            composable("engine/call") {
                EngineDetailScreen(
                    engineName = "CallCheck",
                    engineNameKo = "전화 보호",
                    eventType = InterceptEventType.CALL,
                    color = Color(0xFF4FC3F7),
                    icon = Icons.Filled.Phone,
                    languageProvider = languageProvider,
                    onBack = { navController.popBackStack() },
                )
            }
            composable("engine/push") {
                EngineDetailScreen(
                    engineName = "PushCheck",
                    engineNameKo = "알림 감시",
                    eventType = InterceptEventType.PUSH,
                    color = Color(0xFFFFB74D),
                    icon = Icons.Filled.Notifications,
                    languageProvider = languageProvider,
                    onBack = { navController.popBackStack() },
                )
            }
            composable("engine/message") {
                EngineDetailScreen(
                    engineName = "MessageCheck",
                    engineNameKo = "메시지 방어",
                    eventType = InterceptEventType.MESSAGE,
                    color = Color(0xFF81C784),
                    icon = Icons.Filled.Message,
                    languageProvider = languageProvider,
                    onBack = { navController.popBackStack() },
                )
            }
            composable("engine/privacy") {
                EngineDetailScreen(
                    engineName = "PrivacyCheck",
                    engineNameKo = "프라이버시 감시",
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
                    onBack = { navController.popBackStack() }
                )
            }
            composable("purchase") {
                PurchaseScreen(
                    languageProvider = languageProvider,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// 온보딩 3장 — 보호영역 / 4엔진 / 권한안내
// ═══════════════════════════════════════════════════════════

@Composable
private fun OnboardingScreen(
    languageProvider: LanguageContextProvider,
    onContinue: () -> Unit,
) {
    var currentPage by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1B2A)),
    ) {
        // Content area
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
            }
        }

        // Page indicator dots
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            repeat(3) { index ->
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

        // Navigation button
        Button(
            onClick = {
                if (currentPage < 2) {
                    currentPage++
                } else {
                    onContinue()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4FC3F7)),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text(
                text = if (currentPage < 2) "Next" else "Start",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0D1B2A),
            )
        }
    }
}

/** 온보딩 1장: 질문 + 위협 인식 */
@Composable
private fun OnboardingPage1() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.verticalScroll(rememberScrollState()),
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // 위협 질문 — 큰 글씨
        Text(
            text = "모르는 번호,\n그냥 받으시겠습니까?",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
            lineHeight = 36.sp,
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 위협 상황 묘사
        Text(
            text = "사기 전화, 피싱 문자, 쓸데없는 알림\n이미 당신을 계속 노리고 있습니다.",
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
                    text = "받기 전에 끝냅니다.",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4FC3F7),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "실시간 인터셉트로\n위험 여부를 즉시 판단합니다.",
                    fontSize = 14.sp,
                    color = Color(0xFFB0BEC5),
                    lineHeight = 20.sp,
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 온디바이스 신뢰
        Text(
            text = "100% 온디바이스 · 서버 전송 없음",
            fontSize = 12.sp,
            color = Color(0xFF455A64),
        )
    }
}

/** 온보딩 2장: 4곳 공격 포인트 */
@Composable
private fun OnboardingPage2() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.verticalScroll(rememberScrollState()),
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "당신의 폰은 이미\n4곳에서 공격받고 있습니다",
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
            ThreatInfo(Icons.Filled.Phone, "전화", "사기 전화 · 보이스피싱", Color(0xFF4FC3F7)),
            ThreatInfo(Icons.Filled.Notifications, "알림", "광고 폭탄 · 유도 클릭", Color(0xFFFFB74D)),
            ThreatInfo(Icons.Filled.Message, "문자", "피싱 링크 · 기관 사칭", Color(0xFF81C784)),
            ThreatInfo(Icons.Filled.Security, "프라이버시", "몰래 카메라 · 마이크 접근", Color(0xFFE57373)),
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
            text = "CallCheck이 4곳 전부 실시간으로 잡습니다.",
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
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.verticalScroll(rememberScrollState()),
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "이걸 막으려면,\n접근이 필요합니다",
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
            AccessInfo("전화 상태", "사기 전화를 받기 전에 잡으려면", Icons.Filled.Phone),
            AccessInfo("알림 읽기", "광고/소음 알림을 걸러내려면", Icons.Filled.Notifications),
            AccessInfo("문자 수신", "피싱 링크를 열기 전에 잡으려면", Icons.Filled.Message),
            AccessInfo("앱 사용 통계", "몰래 카메라 접근을 감지하려면", Icons.Filled.Security),
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
                    text = "당신의 데이터는 밖으로 나가지 않습니다.",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF81C784),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "모든 분석은 이 기기 안에서만 처리됩니다.",
                    fontSize = 13.sp,
                    color = Color(0xFF81C784).copy(alpha = 0.7f),
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// 홈 화면 — 오버레이 데모 트리거
// ═══════════════════════════════════════════════════════════

@Composable
private fun HomeScreen(
    languageProvider: LanguageContextProvider,
    onPurchaseClick: () -> Unit,
    context: Context,
    viewModel: CallHistoryViewModel,
    onEngineClick: (String) -> Unit = {},
) {
    val language = languageProvider.resolveLanguage()

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
            text = "CallCheck",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
        )

        Text(
            text = "v1.0.0",
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
                    question = "이 전화,\n받아도 되는가",
                    description = "사기/스팸 여부를 실시간 판단",
                    icon = Icons.Filled.Phone,
                    color = Color(0xFF4FC3F7),
                    modifier = Modifier.weight(1f),
                    onClick = { onEngineClick("engine/call") },
                )
                EngineCard(
                    question = "이 알림,\n무시해도 되는가",
                    description = "소음/프로모션 알림을 즉시 분류",
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
                    question = "이 메시지,\n믿어도 되는가",
                    description = "피싱/사칭 문자를 열기 전에 감지",
                    icon = Icons.Filled.Message,
                    color = Color(0xFF81C784),
                    modifier = Modifier.weight(1f),
                    onClick = { onEngineClick("engine/message") },
                )
                EngineCard(
                    question = "지금 내 폰,\n안전한가",
                    description = "카메라/마이크 몰래 접근 감시",
                    icon = Icons.Filled.Security,
                    color = Color(0xFFE57373),
                    modifier = Modifier.weight(1f),
                    onClick = { onEngineClick("engine/privacy") },
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

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
                "Subscribe",
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
                        text = "Active",
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(engineNameKo, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
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
                text = "Running",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = color,
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Recent events header
            Text(
                text = "최근 이벤트",
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

private fun getTimelineLabel(lang: SupportedLanguage): String {
    return when (lang) {
        SupportedLanguage.KO -> "통합 타임라인"
        SupportedLanguage.JA -> "統合タイムライン"
        SupportedLanguage.ZH -> "统一时间线"
        SupportedLanguage.RU -> "Объединённая шкала времени"
        SupportedLanguage.ES -> "Cronología unificada"
        SupportedLanguage.AR -> "الخط الزمني الموحد"
        else -> "Unified Timeline"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimelineScreen(
    languageProvider: LanguageContextProvider,
    viewModel: CallHistoryViewModel,
    onBack: () -> Unit,
    onRecordClick: (String) -> Unit,
) {
    val language = languageProvider.resolveLanguage()
    val records by viewModel.allRecords.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(getTimelineLabel(language), color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
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
                        text = "No events yet",
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
                        text = "${record.callCount}x calls",
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
    val categoryText = localizer.localizeCategory(category, language)

    // Verdict word from OverlayUiText
    val verdictWord = getOneWordVerdict(riskLevel, language)

    // Background color
    val bgColor = when (riskLevel) {
        RiskLevel.HIGH -> 0xFFD32F2F.toInt()
        RiskLevel.MEDIUM -> 0xFFF57F17.toInt()
        RiskLevel.LOW -> 0xFF2E7D32.toInt()
        RiskLevel.UNKNOWN -> 0xFF455A64.toInt()
    }

    // Reasons (demo — max 2)
    val reasons = getDemoReasons(riskLevel, language)

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
        text = "$categoryText · $phoneNumber · ${confidence}%"
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

    val actionLabels = getOverlayActionLabels(language)
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
    val dismissText = getDismissText(language)
    container.addView(TextView(context).apply {
        text = "✕ $dismissText"
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
private fun getOneWordVerdict(risk: RiskLevel, lang: SupportedLanguage): String {
    return when (lang) {
        SupportedLanguage.KO -> when (risk) {
            RiskLevel.HIGH -> "위험"
            RiskLevel.MEDIUM -> "주의"
            RiskLevel.LOW -> "안전"
            RiskLevel.UNKNOWN -> "확인중"
        }
        SupportedLanguage.JA -> when (risk) {
            RiskLevel.HIGH -> "危険"
            RiskLevel.MEDIUM -> "注意"
            RiskLevel.LOW -> "安全"
            RiskLevel.UNKNOWN -> "確認中"
        }
        SupportedLanguage.ZH -> when (risk) {
            RiskLevel.HIGH -> "危险"
            RiskLevel.MEDIUM -> "注意"
            RiskLevel.LOW -> "安全"
            RiskLevel.UNKNOWN -> "检查中"
        }
        SupportedLanguage.RU -> when (risk) {
            RiskLevel.HIGH -> "Опасно"
            RiskLevel.MEDIUM -> "Внимание"
            RiskLevel.LOW -> "Безопасно"
            RiskLevel.UNKNOWN -> "Проверка"
        }
        SupportedLanguage.ES -> when (risk) {
            RiskLevel.HIGH -> "Peligro"
            RiskLevel.MEDIUM -> "Precaución"
            RiskLevel.LOW -> "Seguro"
            RiskLevel.UNKNOWN -> "Verificando"
        }
        SupportedLanguage.AR -> when (risk) {
            RiskLevel.HIGH -> "خطر"
            RiskLevel.MEDIUM -> "تنبيه"
            RiskLevel.LOW -> "آمن"
            RiskLevel.UNKNOWN -> "جاري التحقق"
        }
        else -> when (risk) {
            RiskLevel.HIGH -> "Danger"
            RiskLevel.MEDIUM -> "Caution"
            RiskLevel.LOW -> "Safe"
            RiskLevel.UNKNOWN -> "Checking"
        }
    }
}

/**
 * 데모용 근거 2개 — 실 서비스에서는 SearchResultAnalyzer 결과에서 추출
 */
private fun getDemoReasons(risk: RiskLevel, lang: SupportedLanguage): List<String> {
    return when (lang) {
        SupportedLanguage.KO -> when (risk) {
            RiskLevel.HIGH -> listOf("다수의 피싱 신고 이력", "정부 사칭 패턴 일치")
            RiskLevel.MEDIUM -> listOf("광고성 전화 신고 있음", "업체 인증 미확인")
            RiskLevel.LOW -> listOf("공식 사업자 번호 확인", "부정 신고 없음")
            RiskLevel.UNKNOWN -> listOf("정보 수집 중", "잠시 후 결과 표시")
        }
        SupportedLanguage.JA -> when (risk) {
            RiskLevel.HIGH -> listOf("複数の詐欺報告あり", "政府機関なりすまし")
            RiskLevel.MEDIUM -> listOf("迷惑電話の報告あり", "企業認証未確認")
            RiskLevel.LOW -> listOf("公式事業者番号確認済み", "否定的な報告なし")
            RiskLevel.UNKNOWN -> listOf("情報収集中", "結果をまもなく表示")
        }
        else -> when (risk) {
            RiskLevel.HIGH -> listOf("Multiple phishing reports found", "Government impersonation pattern")
            RiskLevel.MEDIUM -> listOf("Spam call reports exist", "Business not verified")
            RiskLevel.LOW -> listOf("Official business number verified", "No negative reports found")
            RiskLevel.UNKNOWN -> listOf("Gathering information", "Results coming soon")
        }
    }
}

/**
 * 오버레이 닫기 버튼 텍스트 — 7개 언어
 */
private fun getDismissText(lang: SupportedLanguage): String {
    return when (lang) {
        SupportedLanguage.KO -> "닫기"
        SupportedLanguage.JA -> "閉じる"
        SupportedLanguage.ZH -> "关闭"
        SupportedLanguage.RU -> "Закрыть"
        SupportedLanguage.ES -> "Cerrar"
        SupportedLanguage.AR -> "إغلاق"
        else -> "Close"
    }
}

/**
 * 오버레이 행동 버튼 라벨 — [수신, 거절, 차단]
 */
private fun getOverlayActionLabels(lang: SupportedLanguage): List<String> {
    return when (lang) {
        SupportedLanguage.KO -> listOf("수신", "거절", "차단")
        SupportedLanguage.JA -> listOf("応答", "拒否", "ブロック")
        SupportedLanguage.ZH -> listOf("接听", "拒绝", "拉黑")
        SupportedLanguage.RU -> listOf("Ответить", "Отклонить", "Блокировать")
        SupportedLanguage.ES -> listOf("Aceptar", "Rechazar", "Bloquear")
        SupportedLanguage.AR -> listOf("رد", "رفض", "حظر")
        else -> listOf("Answer", "Reject", "Block")
    }
}

/**
 * 홈 화면 버튼 subtitle — 행동 유도형 (설명 → 액션)
 */
private fun getActionSubtitle(risk: RiskLevel, lang: SupportedLanguage): String {
    return when (lang) {
        SupportedLanguage.KO -> when (risk) {
            RiskLevel.HIGH -> "응답하지 마세요"
            RiskLevel.MEDIUM -> "주의해서 응답하세요"
            RiskLevel.LOW -> "안심하고 받으세요"
            RiskLevel.UNKNOWN -> "확인 중..."
        }
        SupportedLanguage.JA -> when (risk) {
            RiskLevel.HIGH -> "応答しないでください"
            RiskLevel.MEDIUM -> "注意して応答してください"
            RiskLevel.LOW -> "安心してお受けください"
            RiskLevel.UNKNOWN -> "確認中..."
        }
        SupportedLanguage.ZH -> when (risk) {
            RiskLevel.HIGH -> "请勿接听"
            RiskLevel.MEDIUM -> "请谨慎接听"
            RiskLevel.LOW -> "可放心接听"
            RiskLevel.UNKNOWN -> "确认中..."
        }
        SupportedLanguage.RU -> when (risk) {
            RiskLevel.HIGH -> "Не отвечайте"
            RiskLevel.MEDIUM -> "Будьте осторожны"
            RiskLevel.LOW -> "Можно ответить"
            RiskLevel.UNKNOWN -> "Проверка..."
        }
        SupportedLanguage.ES -> when (risk) {
            RiskLevel.HIGH -> "No contestes"
            RiskLevel.MEDIUM -> "Contesta con precaución"
            RiskLevel.LOW -> "Puedes contestar"
            RiskLevel.UNKNOWN -> "Verificando..."
        }
        SupportedLanguage.AR -> when (risk) {
            RiskLevel.HIGH -> "لا ترد"
            RiskLevel.MEDIUM -> "رد بحذر"
            RiskLevel.LOW -> "يمكنك الرد بأمان"
            RiskLevel.UNKNOWN -> "جاري التحقق..."
        }
        else -> when (risk) {
            RiskLevel.HIGH -> "Do not answer"
            RiskLevel.MEDIUM -> "Answer with caution"
            RiskLevel.LOW -> "Safe to answer"
            RiskLevel.UNKNOWN -> "Checking..."
        }
    }
}

// ═══════════════════════════════════════════════════════════
// 설정 화면 — 증거 5: PrivacyTrustMessages 설정 삽입
// ═══════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen(
    languageProvider: LanguageContextProvider,
    onBack: () -> Unit,
) {
    val language = languageProvider.resolveLanguage()
    val msg = PrivacyTrustMessages.forLanguage(language)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(msg.settingsPrivacyTitle, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
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
                        text = "Language",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Current: ${language.code.uppercase()} (auto-detected from device)",
                        fontSize = 14.sp,
                        color = Color(0xFFB0BEC5),
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
    val msg = PrivacyTrustMessages.forLanguage(language)
    val countryCode = CountryConfigProviderImpl().detectCountry(LocalContext.current)
    val tier = CountryPricingMapper.getTier(countryCode)
    val pricingMsg = PricingUiMessages.forLanguage(language)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CallCheck Premium", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
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
            // Privacy guarantee — THE KEY MESSAGE
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A3A2A)),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        Icons.Filled.Shield,
                        contentDescription = null,
                        tint = Color(0xFF81C784),
                        modifier = Modifier.size(32.dp),
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = msg.purchasePrivacyGuarantee,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF81C784),
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp,
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Value proposition
            Text(
                text = msg.purchaseValueProposition,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF4FC3F7),
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Pricing cards
            // Monthly
            PricingCard(
                title = "Monthly",
                price = tier.monthlyPriceUsd + "/mo",
                isSelected = false,
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Yearly
            PricingCard(
                title = "Yearly",
                price = tier.yearlyPriceUsd + "/yr",
                isSelected = true,
                badge = pricingMsg.yearlySavingsMessage,
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Free trial message
            Text(
                text = pricingMsg.formatFreeTrial(tier.freeTrialDays),
                fontSize = 14.sp,
                color = Color(0xFFB0BEC5),
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Subscribe button
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

            Spacer(modifier = Modifier.height(12.dp))

            // Cancellation note
            Text(
                text = pricingMsg.cancellationNote,
                fontSize = 12.sp,
                color = Color(0xFF607D8B),
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tier & country info
            Text(
                text = "Country: ${countryCode ?: "unknown"} · Tier ${tier.tierId} · ${language.code.uppercase()}",
                fontSize = 11.sp,
                color = Color(0xFF455A64),
            )
        }
    }
}

@Composable
private fun PricingCard(
    title: String,
    price: String,
    isSelected: Boolean,
    badge: String? = null,
) {
    val borderColor = if (isSelected) Color(0xFF4FC3F7) else Color(0xFF37474F)
    val bgColor = if (isSelected) Color(0xFF1B3548) else Color(0xFF1B2838)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = borderColor,
        ),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                )
                Text(
                    text = price,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color(0xFF4FC3F7) else Color.White,
                )
            }
            if (badge != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = badge,
                    fontSize = 12.sp,
                    color = Color(0xFF81C784),
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// 통화 기록 화면 — 사용자 기록 레이어
// ═══════════════════════════════════════════════════════════

/**
 * 통화 기록 라벨 — 7개 언어
 */
private fun getCallHistoryLabel(lang: SupportedLanguage): String {
    return when (lang) {
        SupportedLanguage.KO -> "통화 기록"
        SupportedLanguage.JA -> "通話記録"
        SupportedLanguage.ZH -> "通话记录"
        SupportedLanguage.RU -> "Журнал звонков"
        SupportedLanguage.ES -> "Historial"
        SupportedLanguage.AR -> "سجل المكالمات"
        else -> "Call History"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CallHistoryScreen(
    languageProvider: LanguageContextProvider,
    viewModel: CallHistoryViewModel,
    onBack: () -> Unit,
    onRecordClick: (String) -> Unit,
) {
    val language = languageProvider.resolveLanguage()
    val records by viewModel.allRecords.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(getCallHistoryLabel(language), color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
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
                    HistoryStatItem("${records.size}", getRecordCountLabel(language))
                    HistoryStatItem(
                        "${records.count { it.lastAction == "blocked" }}",
                        getBlockedLabel(language),
                    )
                    HistoryStatItem(
                        "${records.count { it.memo?.isNotBlank() == true }}",
                        getMemoCountLabel(language),
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
                        text = getEmptyHistoryLabel(language),
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

    // allRecords에서 직접 필터링 — 가장 확실한 방식
    val allRecords by viewModel.allRecords.collectAsState()
    val record = allRecords.find { it.canonicalNumber == canonicalNumber }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(getDetailLabel(language), color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
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
                        text = "${rec.callCount}x · ${getActionText(rec.lastAction, language)}",
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
                        text = getAiJudgmentLabel(language),
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
                            text = "${getRiskLabel(rec.aiRiskLevel, language)} — ${rec.aiCategory ?: "N/A"}",
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
                        text = getTagSectionLabel(language),
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
                                        text = getTagDisplayName(tag, language),
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
                        text = getMemoSectionLabel(language),
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
                            placeholder = { Text(getMemoPlaceholder(language), color = Color(0xFF607D8B)) },
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
                                Text(getCancelLabel(language), color = Color(0xFF607D8B))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    viewModel.saveMemo(canonicalNumber, memoText)
                                    isEditing = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4FC3F7)),
                            ) {
                                Text(getSaveLabel(language), color = Color(0xFF0D1B2A))
                            }
                        }
                    } else {
                        if (rec.memo.isNullOrBlank()) {
                            Text(
                                text = getMemoPlaceholder(language),
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
                            Text(getEditMemoLabel(language), fontSize = 13.sp)
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
                        text = if (isBlocked) getUnblockLabel(language) else getBlockLabel(language),
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
                    Text(getDeleteLabel(language), fontSize = 14.sp, fontWeight = FontWeight.Bold)
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
                        text = "${record.callCount}x · $actionIcon",
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

private fun getRecordCountLabel(lang: SupportedLanguage): String {
    return when (lang) {
        SupportedLanguage.KO -> "전체 기록"
        SupportedLanguage.JA -> "全記録"
        SupportedLanguage.ZH -> "全部记录"
        else -> "Records"
    }
}

private fun getBlockedLabel(lang: SupportedLanguage): String {
    return when (lang) {
        SupportedLanguage.KO -> "차단됨"
        SupportedLanguage.JA -> "ブロック"
        SupportedLanguage.ZH -> "已拉黑"
        else -> "Blocked"
    }
}

private fun getMemoCountLabel(lang: SupportedLanguage): String {
    return when (lang) {
        SupportedLanguage.KO -> "메모"
        SupportedLanguage.JA -> "メモ"
        SupportedLanguage.ZH -> "备注"
        else -> "Memos"
    }
}

private fun getEmptyHistoryLabel(lang: SupportedLanguage): String {
    return when (lang) {
        SupportedLanguage.KO -> "통화 기록이 없습니다"
        SupportedLanguage.JA -> "通話記録がありません"
        SupportedLanguage.ZH -> "暂无通话记录"
        else -> "No call records yet"
    }
}

// ── 상세 화면 로컬라이즈 ──

private fun getDetailLabel(lang: SupportedLanguage): String {
    return when (lang) {
        SupportedLanguage.KO -> "상세 정보"
        SupportedLanguage.JA -> "詳細情報"
        SupportedLanguage.ZH -> "详细信息"
        SupportedLanguage.RU -> "Подробности"
        SupportedLanguage.ES -> "Detalles"
        SupportedLanguage.AR -> "التفاصيل"
        else -> "Details"
    }
}

private fun getAiJudgmentLabel(lang: SupportedLanguage): String {
    return when (lang) {
        SupportedLanguage.KO -> "AI 판단"
        SupportedLanguage.JA -> "AI判定"
        SupportedLanguage.ZH -> "AI判断"
        else -> "AI Judgment"
    }
}

private fun getRiskLabel(risk: String?, lang: SupportedLanguage): String {
    return when (lang) {
        SupportedLanguage.KO -> when (risk) {
            "HIGH" -> "위험"
            "MEDIUM" -> "주의"
            "LOW" -> "안전"
            else -> "미확인"
        }
        SupportedLanguage.JA -> when (risk) {
            "HIGH" -> "危険"
            "MEDIUM" -> "注意"
            "LOW" -> "安全"
            else -> "未確認"
        }
        else -> when (risk) {
            "HIGH" -> "High Risk"
            "MEDIUM" -> "Medium Risk"
            "LOW" -> "Low Risk"
            else -> "Unknown"
        }
    }
}

private fun getActionText(action: String?, lang: SupportedLanguage): String {
    return when (lang) {
        SupportedLanguage.KO -> when (action) {
            "answered" -> "수신"
            "rejected" -> "거절"
            "blocked" -> "차단"
            "missed" -> "부재중"
            else -> "기록 없음"
        }
        SupportedLanguage.JA -> when (action) {
            "answered" -> "応答"
            "rejected" -> "拒否"
            "blocked" -> "ブロック"
            "missed" -> "不在着信"
            else -> "記録なし"
        }
        else -> when (action) {
            "answered" -> "Answered"
            "rejected" -> "Rejected"
            "blocked" -> "Blocked"
            "missed" -> "Missed"
            else -> "No record"
        }
    }
}

private fun getTagSectionLabel(lang: SupportedLanguage): String {
    return when (lang) {
        SupportedLanguage.KO -> "태그"
        SupportedLanguage.JA -> "タグ"
        SupportedLanguage.ZH -> "标签"
        else -> "Tag"
    }
}

private fun getTagDisplayName(tag: UserCallTag, lang: SupportedLanguage): String {
    return when (lang) {
        SupportedLanguage.KO -> when (tag) {
            UserCallTag.SAFE -> "안전"
            UserCallTag.SPAM -> "스팸"
            UserCallTag.BUSINESS -> "업무"
            UserCallTag.PERSONAL -> "개인"
            UserCallTag.DELIVERY -> "배달"
            UserCallTag.CUSTOM -> "기타"
        }
        SupportedLanguage.JA -> when (tag) {
            UserCallTag.SAFE -> "安全"
            UserCallTag.SPAM -> "スパム"
            UserCallTag.BUSINESS -> "ビジネス"
            UserCallTag.PERSONAL -> "個人"
            UserCallTag.DELIVERY -> "配達"
            UserCallTag.CUSTOM -> "その他"
        }
        SupportedLanguage.ZH -> when (tag) {
            UserCallTag.SAFE -> "安全"
            UserCallTag.SPAM -> "垃圾"
            UserCallTag.BUSINESS -> "商务"
            UserCallTag.PERSONAL -> "个人"
            UserCallTag.DELIVERY -> "快递"
            UserCallTag.CUSTOM -> "其他"
        }
        else -> when (tag) {
            UserCallTag.SAFE -> "Safe"
            UserCallTag.SPAM -> "Spam"
            UserCallTag.BUSINESS -> "Business"
            UserCallTag.PERSONAL -> "Personal"
            UserCallTag.DELIVERY -> "Delivery"
            UserCallTag.CUSTOM -> "Custom"
        }
    }
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

private fun getMemoSectionLabel(lang: SupportedLanguage): String {
    return when (lang) {
        SupportedLanguage.KO -> "메모"
        SupportedLanguage.JA -> "メモ"
        SupportedLanguage.ZH -> "备注"
        else -> "Memo"
    }
}

private fun getMemoPlaceholder(lang: SupportedLanguage): String {
    return when (lang) {
        SupportedLanguage.KO -> "이 번호에 대한 메모를 작성하세요"
        SupportedLanguage.JA -> "この番号についてメモを書いてください"
        SupportedLanguage.ZH -> "为这个号码添加备注"
        else -> "Add a note about this number"
    }
}

private fun getEditMemoLabel(lang: SupportedLanguage): String {
    return when (lang) {
        SupportedLanguage.KO -> "메모 수정"
        SupportedLanguage.JA -> "メモ編集"
        SupportedLanguage.ZH -> "编辑备注"
        else -> "Edit Memo"
    }
}

private fun getSaveLabel(lang: SupportedLanguage): String {
    return when (lang) {
        SupportedLanguage.KO -> "저장"
        SupportedLanguage.JA -> "保存"
        SupportedLanguage.ZH -> "保存"
        else -> "Save"
    }
}

private fun getCancelLabel(lang: SupportedLanguage): String {
    return when (lang) {
        SupportedLanguage.KO -> "취소"
        SupportedLanguage.JA -> "キャンセル"
        SupportedLanguage.ZH -> "取消"
        else -> "Cancel"
    }
}

private fun getBlockLabel(lang: SupportedLanguage): String {
    return when (lang) {
        SupportedLanguage.KO -> "차단"
        SupportedLanguage.JA -> "ブロック"
        SupportedLanguage.ZH -> "拉黑"
        else -> "Block"
    }
}

private fun getUnblockLabel(lang: SupportedLanguage): String {
    return when (lang) {
        SupportedLanguage.KO -> "차단 해제"
        SupportedLanguage.JA -> "解除"
        SupportedLanguage.ZH -> "取消拉黑"
        else -> "Unblock"
    }
}

private fun getDeleteLabel(lang: SupportedLanguage): String {
    return when (lang) {
        SupportedLanguage.KO -> "삭제"
        SupportedLanguage.JA -> "削除"
        SupportedLanguage.ZH -> "删除"
        else -> "Delete"
    }
}
