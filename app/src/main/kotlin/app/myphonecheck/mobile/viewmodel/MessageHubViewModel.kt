package app.myphonecheck.mobile.viewmodel

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.myphonecheck.mobile.data.localcache.dao.MessageHubDao
import app.myphonecheck.mobile.data.localcache.entity.DetailTagSource
import app.myphonecheck.mobile.data.localcache.entity.MessageHubEntity
import app.myphonecheck.mobile.data.localcache.entity.NumberProfileBlockState
import app.myphonecheck.mobile.data.localcache.entity.QuickLabel
import app.myphonecheck.mobile.data.localcache.repository.NumberProfileRepository
import app.myphonecheck.mobile.data.localcache.repository.NumberProfileSnapshot
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
    private val numberProfileRepository: NumberProfileRepository,
    @ApplicationContext private val appContext: Context,
) : ViewModel() {

    val messages: StateFlow<List<MessageHubEntity>> = messageHubDao.getAllFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    val numberProfiles: StateFlow<Map<String, NumberProfileSnapshot>> =
        numberProfileRepository.observeAllSnapshots()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyMap(),
            )

    fun deleteMessage(id: Long) {
        viewModelScope.launch {
            messageHubDao.deleteById(id)
        }
    }

    fun blockSender(packageName: String) {
        viewModelScope.launch {
            messageHubDao.blockSender(packageName)
            numberProfileRepository.setBlockState(packageName, NumberProfileBlockState.BLOCKED)
            cancelNotificationsForPackage(packageName)
        }
    }

    fun blockAndDelete(id: Long, packageName: String) {
        viewModelScope.launch {
            messageHubDao.blockSender(packageName)
            messageHubDao.deleteById(id)
            numberProfileRepository.setBlockState(packageName, NumberProfileBlockState.BLOCKED)
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

    fun toggleQuickLabel(number: String, label: QuickLabel) {
        viewModelScope.launch {
            numberProfileRepository.toggleQuickLabel(number, label)
        }
    }

    fun addDetailTag(number: String, tagName: String) {
        viewModelScope.launch {
            numberProfileRepository.addDetailTag(number, tagName, DetailTagSource.USER)
        }
    }

    fun removeDetailTag(number: String, tagName: String) {
        viewModelScope.launch {
            numberProfileRepository.removeDetailTag(number, tagName)
        }
    }

    fun saveShortMemo(number: String, memo: String) {
        viewModelScope.launch {
            numberProfileRepository.updateShortMemo(number, memo)
        }
    }

    fun setDoNotBlock(number: String) {
        viewModelScope.launch {
            numberProfileRepository.setBlockState(number, NumberProfileBlockState.DO_NOT_BLOCK)
            val snapshot = numberProfiles.value[number]
            if (snapshot?.quickLabels?.contains(QuickLabel.DO_NOT_BLOCK) != true) {
                numberProfileRepository.toggleQuickLabel(number, QuickLabel.DO_NOT_BLOCK)
            }
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
