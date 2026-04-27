package app.myphonecheck.mobile.feature.initialscan.repository

import app.myphonecheck.mobile.core.globalengine.simcontext.SimContext
import app.myphonecheck.mobile.data.localcache.dao.CallBaseDao
import app.myphonecheck.mobile.data.localcache.dao.PackageBaseDao
import app.myphonecheck.mobile.data.localcache.dao.SimContextSnapshotDao
import app.myphonecheck.mobile.data.localcache.dao.SmsBaseDao
import app.myphonecheck.mobile.data.localcache.entity.CallBaseEntity
import app.myphonecheck.mobile.data.localcache.entity.PackageBaseEntity
import app.myphonecheck.mobile.data.localcache.entity.SimContextSnapshotEntity
import app.myphonecheck.mobile.data.localcache.entity.SmsBaseEntity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Initial Scan 베이스데이터 영구 저장 (Architecture v2.0.0 §28, Room v14).
 */
@Singleton
class BaseDataRepository @Inject constructor(
    private val callBaseDao: CallBaseDao,
    private val smsBaseDao: SmsBaseDao,
    private val packageBaseDao: PackageBaseDao,
    private val simContextSnapshotDao: SimContextSnapshotDao,
) {

    suspend fun saveSimContext(simContext: SimContext) {
        simContextSnapshotDao.upsert(
            SimContextSnapshotEntity(
                id = SimContextSnapshotEntity.SINGLETON_ID,
                mcc = simContext.mcc,
                mnc = simContext.mnc,
                countryIso = simContext.countryIso,
                operatorName = simContext.operatorName,
                currencyCode = simContext.currency.currencyCode,
                phoneRegion = simContext.phoneRegion,
                timezoneId = simContext.timezone.id,
                capturedAtMillis = System.currentTimeMillis(),
            ),
        )
    }

    suspend fun saveCallBase(entries: List<CallBaseEntity>) {
        callBaseDao.upsertAll(entries)
    }

    suspend fun saveSmsBase(entries: List<SmsBaseEntity>) {
        smsBaseDao.upsertAll(entries)
    }

    suspend fun savePackageBase(entries: List<PackageBaseEntity>) {
        packageBaseDao.upsertAll(entries)
    }

    suspend fun callCount(): Int = callBaseDao.count()
    suspend fun smsCount(): Int = smsBaseDao.count()
    suspend fun packageCount(): Int = packageBaseDao.count()

    suspend fun lastSimSnapshot(): SimContextSnapshotEntity? = simContextSnapshotDao.get()

    suspend fun isInitialScanCompleted(): Boolean = lastSimSnapshot() != null

    suspend fun clear() {
        callBaseDao.clear()
        smsBaseDao.clear()
        packageBaseDao.clear()
        simContextSnapshotDao.clear()
    }
}
