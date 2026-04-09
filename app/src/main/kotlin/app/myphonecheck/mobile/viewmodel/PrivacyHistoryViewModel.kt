package app.myphonecheck.mobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.myphonecheck.mobile.data.localcache.dao.PrivacyHistoryDao
import app.myphonecheck.mobile.data.localcache.entity.PrivacyHistoryEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrivacyHistoryViewModel @Inject constructor(
    private val privacyHistoryDao: PrivacyHistoryDao,
) : ViewModel() {

    val history: StateFlow<List<PrivacyHistoryEntity>> = privacyHistoryDao.getAllFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    fun updateVerified(id: Long, verified: String) {
        viewModelScope.launch {
            privacyHistoryDao.updateVerified(id, verified)
        }
    }
}
