package app.myphonecheck.mobile.viewmodel

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.myphonecheck.mobile.data.localcache.dao.MessageHubDao
import app.myphonecheck.mobile.data.localcache.entity.MessageHubEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageHubViewModel @Inject constructor(
    private val messageHubDao: MessageHubDao,
    @ApplicationContext private val appContext: Context,
) : ViewModel() {

    val messages: StateFlow<List<MessageHubEntity>> = messageHubDao.getAllFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    fun deleteMessage(id: Long) {
        viewModelScope.launch {
            messageHubDao.deleteById(id)
        }
    }

    fun blockSender(packageName: String) {
        viewModelScope.launch {
            messageHubDao.blockSender(packageName)
            cancelNotificationsForPackage(packageName)
        }
    }

    fun keepMessage(id: Long, markerText: String) {
        viewModelScope.launch {
            val existing = messageHubDao.findById(id) ?: return@launch
            val memo = existing.userMemo
            val next = when {
                memo.isNullOrBlank() -> markerText
                memo.contains(markerText) -> memo
                else -> "$memo\n$markerText"
            }
            messageHubDao.updateMemo(id, next)
        }
    }

    private fun cancelNotificationsForPackage(packageName: String) {
        val nm = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            nm.activeNotifications?.forEach { sn ->
                if (sn.packageName == packageName) {
                    nm.cancel(sn.tag, sn.id)
                }
            }
        }
    }
}
