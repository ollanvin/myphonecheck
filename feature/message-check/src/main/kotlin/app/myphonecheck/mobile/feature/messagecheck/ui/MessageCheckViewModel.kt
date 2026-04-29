package app.myphonecheck.mobile.feature.messagecheck.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.myphonecheck.mobile.core.globalengine.parsing.message.SenderProfile
import app.myphonecheck.mobile.core.globalengine.simcontext.SimContextProvider
import app.myphonecheck.mobile.feature.decisionui.components.DirectSearchHandler
import app.myphonecheck.mobile.feature.messagecheck.repository.MessageEntry
import app.myphonecheck.mobile.feature.messagecheck.repository.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * MessageCheck 메인 ViewModel (Architecture v2.0.0 §22).
 *
 * 발신자 인벤토리 + 최근 메시지 분류 결과 노출. 영구 저장 0 (헌법 §2 In-Bound Zero).
 */
@HiltViewModel
class MessageCheckViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
    private val simContextProvider: SimContextProvider,
    val directSearchHandler: DirectSearchHandler,
) : ViewModel() {

    fun simContext() = simContextProvider.resolve()

    private val _uiState = MutableStateFlow<MessageCheckUiState>(MessageCheckUiState.Loading)
    val uiState: StateFlow<MessageCheckUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            if (!messageRepository.hasPermission()) {
                _uiState.value = MessageCheckUiState.PermissionRequired
                return@launch
            }
            val (entries, inventory) = withContext(Dispatchers.IO) {
                val list = messageRepository.readRecentMessages(limit = 200)
                list to messageRepository.senderInventory()
            }
            _uiState.value = MessageCheckUiState.Loaded(entries, inventory)
        }
    }
}

sealed class MessageCheckUiState {
    object Loading : MessageCheckUiState()
    object PermissionRequired : MessageCheckUiState()
    data class Loaded(
        val entries: List<MessageEntry>,
        val senderInventory: List<SenderProfile>,
    ) : MessageCheckUiState()
}
