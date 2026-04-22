package app.myphonecheck.mobile.feature.pushtrash.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.myphonecheck.mobile.feature.pushtrash.repository.PushTrashRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ObservedAppRow(
    val packageName: String,
    val notificationCount: Int,
)

@HiltViewModel
class PushTrashAppsViewModel @Inject constructor(
    private val repository: PushTrashRepository,
) : ViewModel() {

    private val _apps = MutableStateFlow<List<ObservedAppRow>>(emptyList())
    val apps: StateFlow<List<ObservedAppRow>> = _apps.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val rows = repository.observedAppsLast7Days()
                .filter { it.notificationCount > 0 }
                .map {
                    ObservedAppRow(
                        packageName = it.packageName,
                        notificationCount = it.notificationCount,
                    )
                }
                .sortedByDescending { it.notificationCount }
            _apps.value = rows
        }
    }
}
