package app.myphonecheck.mobile.feature.privacycheck

import android.util.Log
import app.myphonecheck.mobile.data.localcache.dao.InitialScanMetaDao
import app.myphonecheck.mobile.data.localcache.dao.SensorScanResultDao
import app.myphonecheck.mobile.data.localcache.entity.InitialScanMetaEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Initial Scan Orchestrator — 앱 첫 실행 시 전체 baseline 생성.
 *
 * ┌──────────────────────────────────────────────────────┐
 * │ 동작 흐름:                                            │
 * │  1. shouldRun() — 재실행 규칙 평가                     │
 * │  2. DeviceBaseline 수집 (국가/언어/시간대/번호region)   │
 * │  3. Camera + Mic 병렬 스캔                             │
 * │  4. 결과 → SensorScanResult → Room DB upsert          │
 * │  5. InitialScanMeta → completed=true + baseline 저장  │
 * │  6. Guard 자동 PASS 전환                               │
 * └──────────────────────────────────────────────────────┘
 *
 * 재실행 규칙:
 * - 첫 설치 후 첫 실행 → 실행
 * - scanVersion 변경 → 실행
 * - 사용자 수동 재초기화 → 실행
 * - baseline 필수값 누락 → 실행
 * - 그 외 → 스킵 (Home 진입 시 증분 refresh만)
 */
@Singleton
class InitialScanOrchestrator @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sensorScanResultDao: SensorScanResultDao,
    private val initialScanMetaDao: InitialScanMetaDao,
    private val deviceBaselineCollector: DeviceBaselineCollector,
    private val initialScanGuard: InitialScanGuard,
) {
    private val cameraScanner = CameraCheckScanner(context)
    private val micScanner = MicCheckScanner(context)

    /**
     * Initial Scan 재실행 필요 여부를 판단합니다.
     *
     * true를 반환하는 조건:
     * 1) meta 없음 (첫 설치)
     * 2) scanVersion != CURRENT_SCAN_VERSION
     * 3) completed == false (이전 실행 실패/중단)
     * 4) baseline 필수값 누락 (guard fail)
     */
    suspend fun shouldRun(): Boolean {
        val meta = initialScanMetaDao.getMeta()

        // 조건 1: meta 없음
        if (meta == null) {
            Log.i(TAG, "SHOULD_RUN=true reason=first_install")
            return true
        }

        // 조건 2: 버전 변경
        if (meta.scanVersion != CURRENT_SCAN_VERSION) {
            Log.i(TAG, "SHOULD_RUN=true reason=version_changed saved=${meta.scanVersion} current=$CURRENT_SCAN_VERSION")
            return true
        }

        // 조건 3: 미완료
        if (!meta.completed) {
            Log.i(TAG, "SHOULD_RUN=true reason=incomplete")
            return true
        }

        // 조건 4: guard fail (baseline 필수값 누락)
        val guardResult = initialScanGuard.check()
        if (!guardResult.passed) {
            Log.i(TAG, "SHOULD_RUN=true reason=guard_fail")
            return true
        }

        Log.i(TAG, "SHOULD_RUN=false — baseline valid")
        return false
    }

    /**
     * 전체 Initial Scan을 실행합니다.
     *
     * 1. 디바이스 baseline 수집
     * 2. Camera + Mic 병렬 스캔 + Room 저장
     * 3. Meta 완료 플래그 저장
     */
    suspend fun runInitialScan(): Pair<SensorCheckState, SensorCheckState> =
        withContext(Dispatchers.IO) {
            Log.i(TAG, "INIT_SCAN_START version=$CURRENT_SCAN_VERSION")
            val startMs = System.currentTimeMillis()

            // 1. 디바이스 baseline 수집
            val baseline = deviceBaselineCollector.collect()

            // 2. Camera + Mic 병렬 스캔
            val cameraDeferred = async { cameraScanner.scan() }
            val micDeferred = async { micScanner.scan() }

            val cameraResult = cameraDeferred.await()
            val micResult = micDeferred.await()

            // 3. 스캔 결과 Room DB 저장
            sensorScanResultDao.upsert(
                SensorScanResultMapper.toEntity(SENSOR_CAMERA, cameraResult),
            )
            sensorScanResultDao.upsert(
                SensorScanResultMapper.toEntity(SENSOR_MICROPHONE, micResult),
            )

            // 4. 전체 성공 검증 — Camera/Mic 둘 다 SCANNED여야 완료
            val allScanned = cameraResult.scanStatus == ScanStatus.SCANNED &&
                micResult.scanStatus == ScanStatus.SCANNED
            val baselineValid = baseline.country.isNotBlank() &&
                baseline.language.isNotBlank() &&
                baseline.timezone.isNotBlank()
            val completed = allScanned && baselineValid

            // 5. Meta 저장
            val meta = InitialScanMetaEntity(
                id = 1,
                completed = completed,
                scanVersion = CURRENT_SCAN_VERSION,
                completedAt = if (completed) System.currentTimeMillis() else 0L,
                baselineCountry = baseline.country.ifBlank { null },
                baselineLanguage = baseline.language.ifBlank { null },
                baselineTimezone = baseline.timezone.ifBlank { null },
                baselineNumberRegion = baseline.numberRegion.ifBlank { null },
                baselineGeneratedAt = baseline.generatedAt,
            )
            initialScanMetaDao.upsert(meta)

            val elapsedMs = System.currentTimeMillis() - startMs
            Log.i(
                TAG, "INIT_SCAN_COMPLETE" +
                    " completed=$completed" +
                    " camera_granted=${cameraResult.grantedAppCount}" +
                    " mic_granted=${micResult.grantedAppCount}" +
                    " country=${baseline.country}" +
                    " language=${baseline.language}" +
                    " timezone=${baseline.timezone}" +
                    " elapsed=${elapsedMs}ms",
            )

            Log.i(META_TAG, "META_SAVED" +
                " completed=$completed" +
                " version=$CURRENT_SCAN_VERSION" +
                " country=${baseline.country}" +
                " language=${baseline.language}" +
                " timezone=${baseline.timezone}" +
                " numberRegion=${baseline.numberRegion}",
            )

            cameraResult to micResult
        }

    /**
     * 수동 재초기화 — 사용자 명시적 요청 시에만 호출.
     *
     * meta + baseline 전체 삭제 후 runInitialScan() 재실행.
     */
    suspend fun forceReinitialize(): Pair<SensorCheckState, SensorCheckState> {
        Log.i(TAG, "FORCE_REINITIALIZE — clearing meta and baselines")
        initialScanMetaDao.deleteAll()
        sensorScanResultDao.deleteAll()
        return runInitialScan()
    }

    companion object {
        private const val TAG = "MPC_INIT_SCAN"
        private const val META_TAG = "MPC_INIT_META"

        /** Initial Scan 스키마 버전 — 변경 시 자동 재스캔 */
        const val CURRENT_SCAN_VERSION = 1

        const val SENSOR_CAMERA = "CAMERA"
        const val SENSOR_MICROPHONE = "MICROPHONE"
    }
}
