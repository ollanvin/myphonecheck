package app.myphonecheck.mobile.feature.privacycheck

import android.util.Log
import app.myphonecheck.mobile.data.localcache.dao.InitialScanMetaDao
import app.myphonecheck.mobile.data.localcache.dao.SensorScanResultDao
import app.myphonecheck.mobile.data.localcache.entity.InitialScanMetaEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Initial Scan Guard — baseline 유효성 검증.
 *
 * 6개 조건을 모두 만족해야 PASS:
 * 1) initialScanCompleted == true
 * 2) baselineCountry 존재 (non-null, non-blank)
 * 3) baselineLanguage 존재
 * 4) baselineTimezone 존재
 * 5) Camera baseline 저장 존재
 * 6) Mic baseline 저장 존재
 *
 * Guard 결과:
 * - PASS → 정상 홈/상세 진입 허용
 * - FAIL → 완료 UI 차단, 미스캔/초기화필요 상태 강제
 *
 * 금지:
 * - Guard fail인데 "N개 앱 허용" 표시
 * - Guard fail인데 일반 완료 UI 노출
 */
@Singleton
class InitialScanGuard @Inject constructor(
    private val initialScanMetaDao: InitialScanMetaDao,
    private val sensorScanResultDao: SensorScanResultDao,
) {
    /**
     * Guard 결과를 실시간 Flow로 제공합니다.
     *
     * HomeScreen / 상세 화면에서 collectAsState()로 구독.
     * InitialScan 완료 시 자동으로 PASS로 전환.
     */
    fun guardFlow(): Flow<GuardResult> = combine(
        initialScanMetaDao.getMetaFlow(),
        sensorScanResultDao.getResultFlow(InitialScanOrchestrator.SENSOR_CAMERA),
        sensorScanResultDao.getResultFlow(InitialScanOrchestrator.SENSOR_MICROPHONE),
    ) { meta, cameraResult, micResult ->
        evaluate(meta, cameraResult != null, micResult != null)
    }

    /**
     * Guard 1회 평가 (suspend).
     */
    suspend fun check(): GuardResult {
        val meta = initialScanMetaDao.getMeta()
        val hasCamera = sensorScanResultDao.getResult(InitialScanOrchestrator.SENSOR_CAMERA) != null
        val hasMic = sensorScanResultDao.getResult(InitialScanOrchestrator.SENSOR_MICROPHONE) != null
        return evaluate(meta, hasCamera, hasMic)
    }

    private fun evaluate(
        meta: InitialScanMetaEntity?,
        hasCameraBaseline: Boolean,
        hasMicBaseline: Boolean,
    ): GuardResult {
        val failures = mutableListOf<String>()

        // 조건 1: completed
        if (meta == null || !meta.completed) {
            failures.add("initialScanCompleted=false")
        }

        // 조건 2: baselineCountry
        if (meta?.baselineCountry.isNullOrBlank()) {
            failures.add("baselineCountry=null")
        }

        // 조건 3: baselineLanguage
        if (meta?.baselineLanguage.isNullOrBlank()) {
            failures.add("baselineLanguage=null")
        }

        // 조건 4: baselineTimezone
        if (meta?.baselineTimezone.isNullOrBlank()) {
            failures.add("baselineTimezone=null")
        }

        // 조건 5: Camera baseline
        if (!hasCameraBaseline) {
            failures.add("cameraBaseline=missing")
        }

        // 조건 6: Mic baseline
        if (!hasMicBaseline) {
            failures.add("micBaseline=missing")
        }

        val result = if (failures.isEmpty()) {
            GuardResult.PASS
        } else {
            GuardResult.FAIL(failures)
        }

        Log.i(TAG, "GUARD_CHECK result=${result.name} failures=${failures.joinToString(",").ifEmpty { "none" }}")
        return result
    }

    private companion object {
        const val TAG = "MPC_INIT_GUARD"
    }
}

/**
 * Guard 결과.
 *
 * PASS: 모든 조건 만족 — 정상 UI 허용
 * FAIL: 하나 이상 실패 — 완료 UI 차단
 */
sealed class GuardResult {
    abstract val name: String
    abstract val passed: Boolean

    data object PASS : GuardResult() {
        override val name = "PASS"
        override val passed = true
    }

    data class FAIL(val reasons: List<String>) : GuardResult() {
        override val name = "FAIL"
        override val passed = false
    }
}
