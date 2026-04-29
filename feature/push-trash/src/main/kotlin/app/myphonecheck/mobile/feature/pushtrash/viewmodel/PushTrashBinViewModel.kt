package app.myphonecheck.mobile.feature.pushtrash.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.myphonecheck.mobile.core.globalengine.decision.BlockListRepository
import app.myphonecheck.mobile.core.globalengine.decision.TagRepository
import app.myphonecheck.mobile.core.globalengine.decision.addBlock
import app.myphonecheck.mobile.core.globalengine.decision.findTagFor
import app.myphonecheck.mobile.core.globalengine.decision.isBlocked
import app.myphonecheck.mobile.core.globalengine.decision.removeBlock
import app.myphonecheck.mobile.core.globalengine.decision.setTagFor
import app.myphonecheck.mobile.core.globalengine.search.SearchInput
import app.myphonecheck.mobile.core.globalengine.simcontext.SimContextProvider
import app.myphonecheck.mobile.data.localcache.entity.TrashedNotificationEntity
import app.myphonecheck.mobile.feature.decisionui.components.DirectSearchHandler
import app.myphonecheck.mobile.feature.pushtrash.repository.PushTrashRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PushTrashBinViewModel @Inject constructor(
    private val repository: PushTrashRepository,
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

    val items: StateFlow<List<TrashedNotificationEntity>> = repository.trashedItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun restore(entry: TrashedNotificationEntity, onDone: (String) -> Unit) {
        viewModelScope.launch {
            repository.restoreTrashed(entry)
            onDone("restore")
        }
    }

    fun delete(entry: TrashedNotificationEntity, onDone: (String) -> Unit) {
        viewModelScope.launch {
            repository.deleteTrashed(entry)
            onDone("delete")
        }
    }
}
