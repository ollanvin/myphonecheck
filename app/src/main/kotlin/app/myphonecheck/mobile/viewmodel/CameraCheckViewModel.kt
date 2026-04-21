package app.myphonecheck.mobile.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.myphonecheck.mobile.data.localcache.dao.SensorScanResultDao
import app.myphonecheck.mobile.feature.privacycheck.CameraCheckScanner
import app.myphonecheck.mobile.feature.privacycheck.InitialScanOrchestrator
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
 * CameraCheck ViewModel — 카메라 센서 스캔 상태 관리.
 *
 * 동작 흐름:
 * 1. init: Room DB에서 저장값 즉시 복원 → STALE 상태
 * 2. scan(): 백그라운드 스캔 → SCANNED 상태 + Room upsert
 *
 * Guard 연동:
 * - 저장값 없으면 NOT_SCANNED 유지 (Guard fail → 완료 UI 차단)
 * - 저장값 있으면 STALE (유효 데이터 + 갱신 권장)
 * - scan() 완료 후 SCANNED
 * - scan() 실패 시 FAILED
 *
 * 금지:
 * - 저장값 없는데 SCANNED 진입
 * - null 값인데 정상 상태 표기
 */
@HiltViewModel
class CameraCheckViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sensorScanResultDao: SensorScanResultDao,
) : ViewModel() {

    private val scanner = CameraCheckScanner(context)

    private val _state = MutableStateFlow(SensorCheckState())
    val state: StateFlow<SensorCheckState> = _state.asStateFlow()

    init {
        // Room DB에서 저장값 즉시 복원 → STALE 상태
        viewModelScope.launch(Dispatchers.IO) {
            val saved = sensorScanResultDao.getResult(InitialScanOrchestrator.SENSOR_CAMERA)
            if (saved != null) {
                val restored = SensorScanResultMapper.toState(saved)
                // STALE로 설정 — 유효하지만 최신이 아닐 수 있음
                _state.value = restored.copy(scanStatus = ScanStatus.STALE)
                Log.i(TAG, "RESTORE status=STALE" +
                    " granted=${restored.grantedAppCount}" +
                    " scannedAt=${saved.scannedAt}")
            } else {
                Log.i(TAG, "RESTORE status=NOT_SCANNED — no saved baseline")
                // NOT_SCANNED 유지 — Guard fail 상태
            }
        }
    }

    /**
     * 카메라 센서 스캔 (증분 refresh).
     *
     * Guard가 PASS인 상태에서만 SCANNED로 전환.
     * 저장값이 없는 상태에서는 SCANNING → SCANNED 또는 FAILED.
     */
    fun scan() {
        if (_state.value.scanStatus == ScanStatus.SCANNING) return

        // STALE이면 기존 데이터 유지하면서 백그라운드 갱신
        // NOT_SCANNED이면 SCANNING 표시
        if (_state.value.scanStatus == ScanStatus.NOT_SCANNED) {
            _state.value = SensorCheckState(scanStatus = ScanStatus.SCANNING)
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.i(TAG, "VM_SCAN_START sensor=CAMERA")
                val result = scanner.scan()
                _state.value = result

                sensorScanResultDao.upsert(
                    SensorScanResultMapper.toEntity(InitialScanOrchestrator.SENSOR_CAMERA, result),
                )

                Log.i(TAG, "VM_SCAN_DONE sensor=CAMERA" +
                    " status=${result.scanStatus}" +
                    " granted=${result.grantedAppCount}" +
                    " recent=${result.recentAppCount}" +
                    " persisted=true")
            } catch (e: Exception) {
                Log.e(TAG, "VM_SCAN_FAILED sensor=CAMERA", e)
                // 기존 STALE 데이터가 있으면 유지, 없으면 FAILED
                if (!_state.value.hasValidData) {
                    _state.value = SensorCheckState(scanStatus = ScanStatus.FAILED)
                }
            }
        }
    }

    private companion object {
        const val TAG = "MPC_CAMERA_VM"
    }
}
