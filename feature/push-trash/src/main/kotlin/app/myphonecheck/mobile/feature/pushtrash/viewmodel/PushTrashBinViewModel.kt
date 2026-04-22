package app.myphonecheck.mobile.feature.pushtrash.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.myphonecheck.mobile.data.localcache.entity.TrashedNotificationEntity
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
) : ViewModel() {

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
