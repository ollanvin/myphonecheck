package app.myphonecheck.mobile.feature.messagecheck.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.myphonecheck.mobile.core.globalengine.decision.BlockListRepository
import app.myphonecheck.mobile.core.globalengine.decision.TagRepository
import app.myphonecheck.mobile.core.globalengine.decision.addBlock
import app.myphonecheck.mobile.core.globalengine.decision.findTagFor
import app.myphonecheck.mobile.core.globalengine.decision.isBlocked
import app.myphonecheck.mobile.core.globalengine.decision.removeBlock
import app.myphonecheck.mobile.core.globalengine.decision.setTagFor
import app.myphonecheck.mobile.core.globalengine.parsing.message.SenderProfile
import app.myphonecheck.mobile.core.globalengine.search.SearchInput
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
    private val blockListRepository: BlockListRepository,
    private val tagRepository: TagRepository,
    val directSearchHandler: DirectSearchHandler,
) : ViewModel() {

    fun simContext() = simContextProvider.resolve()

    /** v2.6.0 §11 액션 1 (Block toggle). */
    fun toggleBlock(input: SearchInput, newState: Boolean) {
        viewModelScope.launch {
            if (newState) blockListRepository.addBlock(input)
            else blockListRepository.removeBlock(input)
        }
    }

    /** v2.6.0 §11 액션 2 (Tag set). */
    fun setTag(input: SearchInput, tagText: String) {
        viewModelScope.launch {
            tagRepository.setTagFor(input, tagText)
        }
    }

    suspend fun isBlocked(input: SearchInput): Boolean = blockListRepository.isBlocked(input)
    suspend fun currentTag(input: SearchInput): String? = tagRepository.findTagFor(input)?.tagText

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
