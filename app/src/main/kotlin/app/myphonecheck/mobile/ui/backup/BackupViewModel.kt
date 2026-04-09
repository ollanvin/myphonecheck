package app.myphonecheck.mobile.ui.backup

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.myphonecheck.mobile.backup.BackupCore
import app.myphonecheck.mobile.backup.BackupFileManager
import app.myphonecheck.mobile.backup.BackupPreferences
import app.myphonecheck.mobile.backup.RestoreCore
import app.myphonecheck.mobile.data.localcache.dao.BackupMetadataDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class BackupViewModel @Inject constructor(
    private val backupCore: BackupCore,
    private val restoreCore: RestoreCore,
    private val backupFileManager: BackupFileManager,
    private val backupMetadataDao: BackupMetadataDao,
    private val backupPreferences: BackupPreferences,
) : ViewModel() {

    // ─── 백업 목록 ───

    private val _backups = MutableStateFlow<List<BackupItem>>(emptyList())
    val backups: StateFlow<List<BackupItem>> = _backups.asStateFlow()

    // ─── 진행 상태 ───

    sealed class OperationState {
        data object Idle : OperationState()
        data class InProgress(val message: String) : OperationState()
        data class Success(val message: String) : OperationState()
        data class Error(val message: String) : OperationState()
    }

    private val _operationState = MutableStateFlow<OperationState>(OperationState.Idle)
    val operationState: StateFlow<OperationState> = _operationState.asStateFlow()

    // ─── 전체 백업 용량 ───

    private val _totalSize = MutableStateFlow(0L)
    val totalSize: StateFlow<Long> = _totalSize.asStateFlow()

    // ─── SAF 클라우드 백업 URI ───

    /** 유저가 선택한 SAF 트리 URI. null이면 디바이스 전용 백업. */
    val safTreeUri: StateFlow<String?> = backupPreferences.safTreeUri
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        loadBackups()
    }

    /** 백업 파일 목록을 갱신한다. */
    fun loadBackups() {
        viewModelScope.launch {
            val files = backupFileManager.listBackups()
            _backups.value = files.map { file ->
                BackupItem(
                    file = file,
                    name = file.name,
                    sizeBytes = file.length(),
                    lastModified = file.lastModified(),
                )
            }
            _totalSize.value = backupFileManager.getTotalBackupSize()
        }
    }

    /** SAF 트리 URI를 저장한다 (ACTION_OPEN_DOCUMENT_TREE 결과). */
    fun setSafTreeUri(uri: Uri) {
        viewModelScope.launch {
            backupPreferences.setSafTreeUri(uri.toString())
        }
    }

    /** SAF 트리 URI를 초기화한다 (디바이스 전용 백업으로 전환). */
    fun clearSafTreeUri() {
        viewModelScope.launch {
            backupPreferences.clearSafTreeUri()
        }
    }

    /** 즉시 백업을 생성한다. SAF URI가 설정되어 있으면 클라우드에도 동시 저장. */
    fun createBackup(passphrase: CharSequence) {
        viewModelScope.launch {
            _operationState.value = OperationState.InProgress("백업 생성 중...")
            try {
                val currentSafUri = backupPreferences.safTreeUri.first()
                val safUri = currentSafUri?.let { Uri.parse(it) }
                val metadata = backupCore.createBackup(safTreeUri = safUri, passphrase = passphrase)
                val cloudMsg = if (safUri != null) " (클라우드 동시 저장)" else ""
                _operationState.value = OperationState.Success(
                    "백업 완료 (${formatFileSize(metadata.sizeBytes)})$cloudMsg"
                )
                loadBackups()
            } catch (e: Exception) {
                _operationState.value = OperationState.Error("백업 실패: ${e.message}")
            }
        }
    }

    /** 선택한 백업 파일에서 복원한다. */
    fun restoreFromFile(file: File, passphrase: CharSequence) {
        viewModelScope.launch {
            _operationState.value = OperationState.InProgress("복원 중...")
            try {
                val result = restoreCore.restore(file, passphrase)
                _operationState.value = OperationState.Success(
                    "복원 완료: 통화기록 ${result.userCallRecords}건, " +
                            "판단캐시 ${result.preJudgeCacheEntries}건"
                )
            } catch (e: Exception) {
                _operationState.value = OperationState.Error("복원 실패: ${e.message}")
            }
        }
    }

    /** 최신 백업에서 복원한다. */
    fun restoreFromLatest(passphrase: CharSequence) {
        val latest = _backups.value.firstOrNull()
        if (latest == null) {
            _operationState.value = OperationState.Error("복원할 백업이 없습니다")
            return
        }
        restoreFromFile(latest.file, passphrase)
    }

    /** 특정 백업 파일을 삭제한다. */
    fun deleteBackup(item: BackupItem) {
        viewModelScope.launch {
            backupFileManager.deleteBackup(item.file)
            loadBackups()
        }
    }

    /** 상태 메시지를 초기화한다. */
    fun clearOperationState() {
        _operationState.value = OperationState.Idle
    }

    /** 파일 크기를 읽기 쉬운 형식으로 변환한다. */
    companion object {
        fun formatFileSize(bytes: Long): String {
            return when {
                bytes < 1024 -> "${bytes}B"
                bytes < 1024 * 1024 -> "${bytes / 1024}KB"
                else -> String.format(Locale.US, "%.1fMB", bytes / (1024.0 * 1024.0))
            }
        }

        fun formatDate(timestamp: Long): String {
            return SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREA).format(Date(timestamp))
        }
    }

    data class BackupItem(
        val file: File,
        val name: String,
        val sizeBytes: Long,
        val lastModified: Long,
    )
}
