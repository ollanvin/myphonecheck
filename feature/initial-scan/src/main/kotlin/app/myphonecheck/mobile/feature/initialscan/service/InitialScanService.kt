package app.myphonecheck.mobile.feature.initialscan.service

import app.myphonecheck.mobile.feature.initialscan.repository.BaseDataRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Initial Scan orchestrator (Architecture v2.0.0 §28).
 *
 * 흐름:
 *  1. SIM 컨텍스트 확정 (먼저 — phone parser 등이 의존).
 *  2. CallLog/SMS/Package 병렬 스캔.
 *  3. 베이스데이터 영구 저장 (Room v14).
 *
 * 헌법 정합:
 *  - §1 Out-Bound Zero: 모두 디바이스 로컬.
 *  - §2 In-Bound Zero: 본문/녹음 0, 메타데이터/정규화 결과만.
 *  - §3 결정 중앙집중 금지: 사용자 동의 후 호출.
 *  - §8 SIM-Oriented Single Core: SimContextScanner가 첫 단계.
 */
@Singleton
class InitialScanService @Inject constructor(
    private val simScanner: SimContextScanner,
    private val callLogScanner: CallLogScanner,
    private val smsInboxScanner: SmsInboxScanner,
    private val packageScanner: PackageInventoryScanner,
    private val baseDataRepository: BaseDataRepository,
) {

    suspend fun execute(): ScanResult = coroutineScope {
        val simContext = simScanner.scan()
        baseDataRepository.saveSimContext(simContext)

        val callJob = async { callLogScanner.scan(simContext) }
        val smsJob = async { smsInboxScanner.scan(simContext) }
        val packageJob = async { packageScanner.scan() }

        val callBase = callJob.await()
        val smsBase = smsJob.await()
        val packageBase = packageJob.await()

        baseDataRepository.saveCallBase(callBase)
        baseDataRepository.saveSmsBase(smsBase)
        baseDataRepository.savePackageBase(packageBase)

        ScanResult(
            simContext = simContext,
            callCount = callBase.size,
            smsCount = smsBase.size,
            packageCount = packageBase.size,
            scannedAtMillis = System.currentTimeMillis(),
        )
    }
}
