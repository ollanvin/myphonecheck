package app.myphonecheck.mobile.feature.pushtrash.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.myphonecheck.mobile.feature.pushtrash.repository.PushTrashRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject

data class ChannelRuleRow(
    val channelId: String,
    val notificationCount: Int,
    val blocked: Boolean,
)

@HiltViewModel
class AppBlockSettingsViewModel @Inject constructor(
    private val repository: PushTrashRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val packageName: String = URLDecoder.decode(
        checkNotNull(savedStateHandle["packageName"]),
        StandardCharsets.UTF_8.name(),
    )

    private val _rows = MutableStateFlow<List<ChannelRuleRow>>(emptyList())
    val channelRows: StateFlow<List<ChannelRuleRow>> = _rows.asStateFlow()

    private val _appMode = MutableStateFlow<String?>(null)
    val appMode: StateFlow<String?> = _appMode.asStateFlow()

    private val _totalNotifications = MutableStateFlow(0)
    val totalNotifications: StateFlow<Int> = _totalNotifications.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val app = repository.getAppBlock(packageName)
            _appMode.value = app?.mode

            val channels = repository.observedChannelsLast7Days()
                .filter { it.packageName == packageName }

            val total = channels.sumOf { it.notificationCount }
            _totalNotifications.value = total

            val grouped = channels
                .filter { it.channelId.isNotBlank() }
                .groupBy { it.channelId }
                .mapValues { (_, v) -> v.sumOf { it.notificationCount } }

            if (grouped.isEmpty()) {
                _rows.value = emptyList()
                return@launch
            }

            val rows = mutableListOf<ChannelRuleRow>()
            for ((chId, cnt) in grouped.toList().sortedByDescending { it.second }) {
                val blocked = repository.isChannelBlocked(packageName, chId)
                rows.add(
                    ChannelRuleRow(
                        channelId = chId,
                        notificationCount = cnt,
                        blocked = blocked,
                    ),
                )
            }
            _rows.value = rows
        }
    }

    fun setChannelBlocked(channelId: String, blocked: Boolean) {
        viewModelScope.launch {
            repository.setChannelBlocked(packageName, channelId, blocked)
            refresh()
        }
    }

    fun setAppModeAllAllowed() {
        viewModelScope.launch {
            repository.setAppMode(packageName, PushTrashRepository.MODE_ALL_ALLOWED)
            _appMode.value = PushTrashRepository.MODE_ALL_ALLOWED
        }
    }

    fun setAppModeAllBlocked() {
        viewModelScope.launch {
            repository.setAppMode(packageName, PushTrashRepository.MODE_ALL_BLOCKED)
            _appMode.value = PushTrashRepository.MODE_ALL_BLOCKED
        }
    }

    fun clearAppMode() {
        viewModelScope.launch {
            repository.clearAppMode(packageName)
            _appMode.value = null
        }
    }
}
