package app.callcheck.mobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.callcheck.mobile.core.model.UserCallAction
import app.callcheck.mobile.core.model.UserCallTag
import app.callcheck.mobile.data.localcache.entity.UserCallRecord
import app.callcheck.mobile.data.localcache.repository.UserCallRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 통화 기록 ViewModel.
 *
 * Room DB Flow를 관찰하여 UI에 실시간 반영.
 * 메모/태그/행동 CRUD 전담.
 *
 * 서버 동기화: 없음 (온디바이스 전용)
 */
@HiltViewModel
class CallHistoryViewModel @Inject constructor(
    private val repository: UserCallRecordRepository,
) : ViewModel() {

    // ── 전체 기록 (실시간 관찰) ──

    val allRecords: StateFlow<List<UserCallRecord>> = repository.observeAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    // ── 현재 선택된 기록 (상세 화면용) ──

    private val _selectedRecord = MutableStateFlow<UserCallRecord?>(null)
    val selectedRecord: StateFlow<UserCallRecord?> = _selectedRecord.asStateFlow()

    // ── 필터 상태 ──

    private val _activeFilter = MutableStateFlow<String?>(null)
    val activeFilter: StateFlow<String?> = _activeFilter.asStateFlow()

    /** 번호로 상세 기록 로드 */
    fun loadRecord(canonicalNumber: String) {
        viewModelScope.launch {
            _selectedRecord.value = repository.findByNumber(canonicalNumber)
        }
    }

    /** 번호로 즉시 로드 + 실시간 관찰 시작 */
    fun observeRecord(canonicalNumber: String) {
        viewModelScope.launch {
            // 즉시 로드 (스피너 최소화)
            _selectedRecord.value = repository.findByNumber(canonicalNumber)
            // 이후 실시간 관찰
            repository.observeByNumber(canonicalNumber).collect { record ->
                _selectedRecord.value = record
            }
        }
    }

    // ── CRUD 작업 ──

    /** 메모 저장 */
    fun saveMemo(canonicalNumber: String, memo: String) {
        viewModelScope.launch {
            repository.saveMemo(canonicalNumber, memo)
            // 갱신
            _selectedRecord.value = repository.findByNumber(canonicalNumber)
        }
    }

    /** 태그 저장 */
    fun saveTag(canonicalNumber: String, tag: UserCallTag) {
        viewModelScope.launch {
            repository.saveTag(canonicalNumber, tag)
            _selectedRecord.value = repository.findByNumber(canonicalNumber)
        }
    }

    /** 차단 토글 */
    fun toggleBlock(canonicalNumber: String, currentlyBlocked: Boolean) {
        viewModelScope.launch {
            if (currentlyBlocked) {
                // 차단 해제 → 마지막 행동을 answered로 변경
                val record = repository.findByNumber(canonicalNumber) ?: return@launch
                repository.recordCall(
                    canonicalNumber = record.canonicalNumber,
                    displayNumber = record.displayNumber,
                    action = UserCallAction.ANSWERED,
                    aiRiskLevel = record.aiRiskLevel,
                    aiCategory = record.aiCategory,
                )
            } else {
                repository.blockNumber(canonicalNumber)
            }
            _selectedRecord.value = repository.findByNumber(canonicalNumber)
        }
    }

    /** 통화 기록 저장 (오버레이 액션에서 호출) */
    fun recordCallAction(
        canonicalNumber: String,
        displayNumber: String,
        action: UserCallAction,
        aiRiskLevel: String? = null,
        aiCategory: String? = null,
    ) {
        viewModelScope.launch {
            repository.recordCall(
                canonicalNumber = canonicalNumber,
                displayNumber = displayNumber,
                action = action,
                aiRiskLevel = aiRiskLevel,
                aiCategory = aiCategory,
            )
        }
    }

    /** 기록 삭제 */
    fun deleteRecord(canonicalNumber: String) {
        viewModelScope.launch {
            repository.deleteRecord(canonicalNumber)
            _selectedRecord.value = null
        }
    }

    /** 데모 데이터 삽입 (최초 실행 시 빈 화면 방지) */
    fun insertDemoDataIfEmpty() {
        viewModelScope.launch {
            val count = repository.getRecordCount()
            if (count > 0) return@launch

            repository.recordCall("+821012345678", "010-1234-5678", UserCallAction.REJECTED, "HIGH", "SCAM_RISK_HIGH")
            repository.saveMemo("+821012345678", "보험 영업 전화")
            repository.saveTag("+821012345678", UserCallTag.SPAM)

            repository.recordCall("+12028003000", "(202) 800-3000", UserCallAction.ANSWERED, "LOW", "BUSINESS_LIKELY")
            repository.saveMemo("+12028003000", "거래처 담당자")
            repository.saveTag("+12028003000", UserCallTag.BUSINESS)
            // 12번 통화 시뮬레이션
            repeat(11) {
                repository.recordCall("+12028003000", "(202) 800-3000", UserCallAction.ANSWERED, "LOW", "BUSINESS_LIKELY")
            }

            repository.recordCall("+810120444113", "0120-444-113", UserCallAction.ANSWERED, "LOW", "BUSINESS_LIKELY")
            repository.saveTag("+810120444113", UserCallTag.SAFE)
            repeat(4) {
                repository.recordCall("+810120444113", "0120-444-113", UserCallAction.ANSWERED, "LOW", "BUSINESS_LIKELY")
            }

            repository.recordCall("+821098765432", "010-9876-5432", UserCallAction.BLOCKED, "HIGH", "SCAM_CONFIRMED")
            repository.saveMemo("+821098765432", "사기였음 — 다시 받지 말 것")
        }
    }
}
