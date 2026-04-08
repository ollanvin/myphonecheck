package app.callcheck.mobile.ui.backup

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.documentfile.provider.DocumentFile
import androidx.hilt.navigation.compose.hiltViewModel
import java.io.File

// CallCheck 다크테마 색상
private val BackgroundDark = Color(0xFF0D1B2A)
private val CardDark = Color(0xFF1B2838)
private val Primary = Color(0xFF4FC3F7)
private val TextPrimary = Color(0xFFFFFFFF)
private val TextSecondary = Color(0xFFB3B3B3)
private val TextTertiary = Color(0xFF808080)
private val Green = Color(0xFF66BB6A)
private val Red = Color(0xFFEF5350)
private val Blue = Color(0xFF42A5F5)
private val BlueDark = Color(0xFF1565C0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupScreen(
    onNavigateBack: () -> Unit,
    viewModel: BackupViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val backups by viewModel.backups.collectAsState()
    val operationState by viewModel.operationState.collectAsState()
    val totalSize by viewModel.totalSize.collectAsState()
    val safTreeUri by viewModel.safTreeUri.collectAsState()

    var deleteTarget by remember { mutableStateOf<BackupViewModel.BackupItem?>(null) }
    var restoreTarget by remember { mutableStateOf<BackupViewModel.BackupItem?>(null) }
    var showBackupPassphraseDialog by remember { mutableStateOf(false) }
    var showRestorePassphraseDialog by remember { mutableStateOf(false) }
    var pendingRestoreFile by remember { mutableStateOf<File?>(null) }

    // SAF 파일 선택 (복원용)
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                if (inputStream != null) {
                    val tempFile = File(context.cacheDir, "restore_temp.csb")
                    inputStream.use { input ->
                        tempFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    pendingRestoreFile = tempFile
                    showRestorePassphraseDialog = true
                }
            } catch (_: Exception) {
            }
        }
    }

    // SAF 트리 선택 (클라우드 백업 위치)
    val treePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri?.let {
            val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            context.contentResolver.takePersistableUriPermission(it, takeFlags)
            viewModel.setSafTreeUri(it)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("백업 및 복원", fontWeight = FontWeight.Bold, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "뒤로", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark),
            )
        },
    ) { scaffoldPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = scaffoldPadding.calculateTopPadding())
                .background(BackgroundDark),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // ─── 상태 메시지 ───
            item {
                AnimatedVisibility(
                    visible = operationState !is BackupViewModel.OperationState.Idle,
                    enter = expandVertically(),
                    exit = shrinkVertically(),
                ) {
                    val (bgColor, textColor, message) = when (operationState) {
                        is BackupViewModel.OperationState.InProgress -> Triple(
                            Color(0xFF1A237E), Blue,
                            (operationState as BackupViewModel.OperationState.InProgress).message,
                        )
                        is BackupViewModel.OperationState.Success -> Triple(
                            Color(0xFF1B5E20), Green,
                            (operationState as BackupViewModel.OperationState.Success).message,
                        )
                        is BackupViewModel.OperationState.Error -> Triple(
                            Color(0xFF7F0000), Red,
                            (operationState as BackupViewModel.OperationState.Error).message,
                        )
                        else -> Triple(CardDark, TextTertiary, "")
                    }
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = bgColor),
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (operationState is BackupViewModel.OperationState.InProgress) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = Blue,
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                            }
                            Text(
                                text = message,
                                fontSize = 13.sp,
                                color = textColor,
                                modifier = Modifier.weight(1f),
                            )
                            if (operationState !is BackupViewModel.OperationState.InProgress) {
                                TextButton(onClick = { viewModel.clearOperationState() }) {
                                    Text("닫기", fontSize = 12.sp, color = TextSecondary)
                                }
                            }
                        }
                    }
                }
            }

            // ─── 클라우드 백업 설정 ───
            item {
                SectionHeader("클라우드 백업 위치")
            }
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardDark),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (safTreeUri != null) {
                                Icon(
                                    Icons.Default.CloudDone,
                                    contentDescription = null,
                                    tint = Green,
                                    modifier = Modifier.size(20.dp),
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "디바이스 + 클라우드 동시 저장",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Green,
                                )
                            } else {
                                Icon(
                                    Icons.Default.CloudOff,
                                    contentDescription = null,
                                    tint = TextTertiary,
                                    modifier = Modifier.size(20.dp),
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "디바이스에만 저장",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextSecondary,
                                )
                            }
                        }

                        if (safTreeUri != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            val displayPath = remember(safTreeUri) {
                                try {
                                    val uri = Uri.parse(safTreeUri)
                                    val docFile = DocumentFile.fromTreeUri(context, uri)
                                    docFile?.name ?: uri.lastPathSegment ?: "선택된 위치"
                                } catch (_: Exception) {
                                    "선택된 위치"
                                }
                            }
                            Text("위치: $displayPath", fontSize = 12.sp, color = TextTertiary)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Google Drive, OneDrive 등\nSAF를 지원하는 클라우드 저장소에\n백업을 동시 저장할 수 있습니다.",
                            fontSize = 12.sp,
                            color = TextTertiary,
                            lineHeight = 17.sp,
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = { treePickerLauncher.launch(null) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Primary),
                        ) {
                            Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                if (safTreeUri != null) "클라우드 위치 변경" else "클라우드 백업 위치 선택",
                                fontSize = 14.sp,
                            )
                        }

                        if (safTreeUri != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(
                                onClick = { viewModel.clearSafTreeUri() },
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text(
                                    "클라우드 백업 해제 (디바이스만 사용)",
                                    fontSize = 13.sp,
                                    color = TextTertiary,
                                )
                            }
                        }
                    }
                }
            }

            // ─── 백업 생성 ───
            item { SectionHeader("새 백업 생성") }
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardDark),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "현재 데이터를 AES-256-GCM으로 암호화하여 백업합니다.",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            lineHeight = 18.sp,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            if (safTreeUri != null)
                                "저장 위치: Documents/CallCheck/ + 클라우드"
                            else
                                "저장 위치: Documents/CallCheck/",
                            fontSize = 12.sp,
                            color = TextTertiary,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { showBackupPassphraseDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Primary),
                            enabled = operationState !is BackupViewModel.OperationState.InProgress,
                        ) {
                            Text("지금 백업", fontSize = 14.sp, color = Color(0xFF0D1B2A))
                        }
                    }
                }
            }

            // ─── 파일에서 복원 ───
            item { SectionHeader("파일에서 복원") }
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardDark),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "백업 파일(.csb)을 선택하여 복원합니다.\n" +
                                "복원 시 현재 데이터는 모두 삭제됩니다.",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            lineHeight = 18.sp,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = { filePickerLauncher.launch(arrayOf("*/*")) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Primary),
                            enabled = operationState !is BackupViewModel.OperationState.InProgress,
                        ) {
                            Text("파일 선택하여 복원", fontSize = 14.sp)
                        }
                    }
                }
            }

            // ─── 백업 목록 ───
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SectionHeader("백업 목록")
                    if (totalSize > 0) {
                        Text(
                            "총 ${BackupViewModel.formatFileSize(totalSize)}",
                            fontSize = 12.sp,
                            color = TextTertiary,
                        )
                    }
                }
            }

            if (backups.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = CardDark),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("백업 파일이 없습니다", fontSize = 14.sp, color = TextTertiary)
                        }
                    }
                }
            } else {
                items(backups, key = { it.name }) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = CardDark),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { restoreTarget = item }
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    item.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary,
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    "${BackupViewModel.formatDate(item.lastModified)} · ${BackupViewModel.formatFileSize(item.sizeBytes)}",
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                )
                            }
                            Row {
                                IconButton(onClick = { restoreTarget = item }) {
                                    Icon(
                                        Icons.Default.Refresh,
                                        contentDescription = "복원",
                                        tint = Blue,
                                        modifier = Modifier.size(20.dp),
                                    )
                                }
                                IconButton(onClick = { deleteTarget = item }) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "삭제",
                                        tint = Red,
                                        modifier = Modifier.size(20.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ─── 안내 ───
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "백업은 복구 비밀번호와 PBKDF2·AES-256-GCM으로 암호화된 .csb(v2) 파일로 저장됩니다.\n" +
                        "동일한 복구 비밀번호로 다른 기기에서도 복원할 수 있습니다.\n" +
                        "최대 5개의 백업이 보관되며, 초과 시 오래된 백업이 자동 삭제됩니다.",
                    fontSize = 12.sp,
                    color = TextTertiary,
                    lineHeight = 17.sp,
                )
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }

    if (showBackupPassphraseDialog) {
        BackupPassphraseDialog(
            onDismiss = { showBackupPassphraseDialog = false },
            onConfirm = { passphrase ->
                showBackupPassphraseDialog = false
                viewModel.createBackup(passphrase)
            },
        )
    }

    if (showRestorePassphraseDialog && pendingRestoreFile != null) {
        BackupPassphraseDialog(
            onDismiss = {
                showRestorePassphraseDialog = false
                pendingRestoreFile = null
            },
            onConfirm = { passphrase ->
                val file = pendingRestoreFile!!
                showRestorePassphraseDialog = false
                pendingRestoreFile = null
                viewModel.restoreFromFile(file, passphrase)
            },
            confirmButtonLabel = "복원",
        )
    }

    // ─── 삭제 확인 다이얼로그 ───
    if (deleteTarget != null) {
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("백업 삭제") },
            text = {
                Text("${deleteTarget!!.name}\n이 백업을 삭제하시겠습니까?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteBackup(deleteTarget!!)
                        deleteTarget = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Red),
                ) {
                    Text("삭제")
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) {
                    Text("취소")
                }
            },
        )
    }

    // ─── 복원 확인 다이얼로그 ───
    if (restoreTarget != null) {
        AlertDialog(
            onDismissRequest = { restoreTarget = null },
            title = { Text("백업 복원") },
            text = {
                Text(
                    "${restoreTarget!!.name}\n\n" +
                        "이 백업에서 복원하시겠습니까?\n" +
                        "현재 데이터는 모두 삭제되고 백업 데이터로 교체됩니다."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        pendingRestoreFile = restoreTarget!!.file
                        restoreTarget = null
                        showRestorePassphraseDialog = true
                    },
                ) {
                    Text("복원")
                }
            },
            dismissButton = {
                TextButton(onClick = { restoreTarget = null }) {
                    Text("취소")
                }
            },
        )
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary,
    )
}
