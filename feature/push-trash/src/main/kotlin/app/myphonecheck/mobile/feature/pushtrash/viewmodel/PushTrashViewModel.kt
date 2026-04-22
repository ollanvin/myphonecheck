package app.myphonecheck.mobile.feature.pushtrash.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.myphonecheck.mobile.feature.pushtrash.repository.PushTrashRepository
import app.myphonecheck.mobile.feature.pushtrash.util.NotificationListenerState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PushTrashViewModel @Inject constructor(
    private val repository: PushTrashRepository,
    @ApplicationContext context: Context,
) : ViewModel() {

    private val appContext = context.applicationContext

    private val since7d: Long
        get() = System.currentTimeMillis() - SEVEN_DAYS_MS

    private val _listenerEnabled = MutableStateFlow(NotificationListenerState.isEnabled(appContext))
    val listenerEnabled: StateFlow<Boolean> = _listenerEnabled.asStateFlow()

    val trashedCount7d: StateFlow<Int> = repository
        .trashedCountSince(since7d)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    private val _ruleCount = MutableStateFlow(0)
    val ruleCount: StateFlow<Int> = _ruleCount.asStateFlow()

    init {
        refreshRuleCount()
    }

    fun refreshListenerState() {
        _listenerEnabled.value = NotificationListenerState.isEnabled(appContext)
    }

    fun refreshRuleCount() {
        viewModelScope.launch {
            _ruleCount.value = repository.ruleCount()
        }
    }

    companion object {
        private const val SEVEN_DAYS_MS = 7L * 24L * 60L * 60L * 1000L
    }
}
