package app.myphonecheck.mobile.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.myphonecheck.mobile.data.localcache.dao.SensorScanResultDao
import app.myphonecheck.mobile.feature.privacycheck.InitialScanOrchestrator
import app.myphonecheck.mobile.feature.privacycheck.MicCheckScanner
import app.myphonecheck.mobile.feature.privacycheck.ScanStatus
import app.myphonecheck.mobile.feature.privacycheck.SensorCheckState
import app.myphonecheck.mobile.feature.privacycheck.SensorScanResultMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * MicCheck ViewModel — 마이크 센서 스캔 상태 관리.
 *
 * CameraCheckViewModel과 동일한 Guard 연동 구조.
 * 상태 흐름: NOT_SCANNED → (Room 복원) STALE → (scan()) SCANNED
 */
@HiltViewModel
class MicCheckViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sensorScanResultDao: SensorScanResultDao,
) : ViewModel() {

    private val scanner = MicCheckScanner(context)

    private val _state = MutableStateFlow(SensorCheckState())
    val state: StateFlow<SensorCheckState> = _state.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val saved = sensorScanResultDao.getResult(InitialScanOrchestrator.SENSOR_MICROPHONE)
            if (saved != null) {
                val restored = SensorScanResultMapper.toState(saved)
                _state.value = restored.copy(scanStatus = ScanStatus.STALE)
                Log.i(TAG, "RESTORE status=STALE" +
                    " granted=${restored.grantedAppCount}" +
                    " scannedAt=${saved.scannedAt}")
            } else {
                Log.i(TAG, "RESTORE status=NOT_SCANNED — no saved baseline")
            }
        }
    }

    fun scan() {
        if (_state.value.scanStatus == ScanStatus.SCANNING) return

        if (_state.value.scanStatus == ScanStatus.NOT_SCANNED) {
            _state.value = SensorCheckState(scanStatus = ScanStatus.SCANNING)
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.i(TAG, "VM_SCAN_START sensor=MICROPHONE")
                val result = scanner.scan()
                _state.value = result

                sensorScanResultDao.upsert(
                    SensorScanResultMapper.toEntity(InitialScanOrchestrator.SENSOR_MICROPHONE, result),
                )

                Log.i(TAG, "VM_SCAN_DONE sensor=MICROPHONE" +
                    " status=${result.scanStatus}" +
                    " granted=${result.grantedAppCount}" +
                    " recent=${result.recentAppCount}" +
                    " persisted=true")
            } catch (e: Exception) {
                Log.e(TAG, "VM_SCAN_FAILED sensor=MICROPHONE", e)
                if (!_state.value.hasValidData) {
                    _state.value = SensorCheckState(scanStatus = ScanStatus.FAILED)
                }
            }
        }
    }

    private companion object {
        const val TAG = "MPC_MIC_VM"
    }
}
