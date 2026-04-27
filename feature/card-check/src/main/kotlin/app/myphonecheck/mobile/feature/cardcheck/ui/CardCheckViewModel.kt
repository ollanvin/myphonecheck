package app.myphonecheck.mobile.feature.cardcheck.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.myphonecheck.mobile.data.localcache.dao.CardTransactionMonthlyTotal
import app.myphonecheck.mobile.data.localcache.entity.CardTransactionEntity
import app.myphonecheck.mobile.core.globalengine.parsing.currency.learning.LabelingService
import app.myphonecheck.mobile.feature.cardcheck.repository.CardTransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone
import javax.inject.Inject

/**
 * CardCheck 메인 ViewModel (Architecture v1.9.0 §27).
 *
 * 월별·통화별·소스별 집계 노출. 사용자 라벨링 결과 반영.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CardCheckViewModel @Inject constructor(
    private val repository: CardTransactionRepository,
    private val labeling: LabelingService,
) : ViewModel() {

    private val _selectedMonth = MutableStateFlow(MonthOffset.CURRENT)
    val selectedMonth: StateFlow<MonthOffset> = _selectedMonth.asStateFlow()

    private val _includeLow = MutableStateFlow(false)
    val includeLow: StateFlow<Boolean> = _includeLow.asStateFlow()

    val monthlyTotals: StateFlow<List<CardTransactionMonthlyTotal>> =
        _selectedMonth.flatMapLatest { offset ->
            val (start, end) = monthBounds(offset)
            repository.observeMonthlyTotals(start, end, _includeLow.value)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val transactions: StateFlow<List<CardTransactionEntity>> =
        _selectedMonth.flatMapLatest { offset ->
            val (start, end) = monthBounds(offset)
            repository.observeInRange(start, end)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun selectMonth(offset: MonthOffset) {
        _selectedMonth.value = offset
    }

    fun toggleIncludeLow() {
        _includeLow.value = !_includeLow.value
    }

    fun confirmAsCard(sourceId: String, label: String) {
        viewModelScope.launch {
            labeling.confirmAsCard(sourceId, label)
        }
    }

    fun denyAsCard(sourceId: String) {
        viewModelScope.launch {
            labeling.denyAsCard(sourceId)
        }
    }

    private fun monthBounds(offset: MonthOffset): Pair<Long, Long> {
        val cal = Calendar.getInstance(TimeZone.getDefault())
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        when (offset) {
            MonthOffset.CURRENT -> {} // 이번 달 1일
            MonthOffset.PREVIOUS -> cal.add(Calendar.MONTH, -1)
        }
        val start = cal.timeInMillis
        cal.add(Calendar.MONTH, 1)
        val end = cal.timeInMillis
        return start to end
    }
}

enum class MonthOffset { CURRENT, PREVIOUS }
