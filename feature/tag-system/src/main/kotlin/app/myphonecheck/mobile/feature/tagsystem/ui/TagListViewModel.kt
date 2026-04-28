package app.myphonecheck.mobile.feature.tagsystem.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.myphonecheck.mobile.core.globalengine.decision.IdentifierType
import app.myphonecheck.mobile.core.globalengine.decision.TagPriority
import app.myphonecheck.mobile.core.globalengine.decision.TagRecord
import app.myphonecheck.mobile.feature.tagsystem.repository.RoomTagRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TagListViewModel @Inject constructor(
    private val tagRepository: RoomTagRepository,
) : ViewModel() {

    val tags: StateFlow<List<TagRecord>> = tagRepository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun upsert(key: String, type: IdentifierType, tagText: String, priority: TagPriority) {
        viewModelScope.launch { tagRepository.upsert(key, type, tagText, priority) }
    }

    fun delete(record: TagRecord) {
        viewModelScope.launch { tagRepository.delete(record.key, record.type) }
    }
}
