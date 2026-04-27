package app.myphonecheck.mobile.feature.settings.v2.repository

import app.myphonecheck.mobile.core.globalengine.simcontext.CountryCurrencyMapper
import app.myphonecheck.mobile.core.globalengine.simcontext.SimContext
import app.myphonecheck.mobile.core.globalengine.simcontext.SimContextStorage
import app.myphonecheck.mobile.data.localcache.dao.SimContextSnapshotDao
import app.myphonecheck.mobile.data.localcache.entity.SimContextSnapshotEntity
import java.util.Currency
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

/**
 * SimContextStorage Room 기반 구현 (Architecture v2.0.0 §29).
 *
 * Initial Scan(PR #23) sim_context_snapshot 테이블 재활용 — 단일 진실원.
 *
 * 모듈 위치: :feature:settings (data → core 의존 방향 유지를 위해 feature 레이어에 배치).
 * :data:local-cache는 코어 인터페이스를 모름 — :feature:settings가 두 모듈을 묶어 바인딩.
 */
@Singleton
class RoomSimContextStorage @Inject constructor(
    private val dao: SimContextSnapshotDao,
) : SimContextStorage {

    override suspend fun loadPrevious(): SimContext? {
        val entity = dao.get() ?: return null
        return SimContext(
            mcc = entity.mcc,
            mnc = entity.mnc,
            countryIso = entity.countryIso,
            operatorName = entity.operatorName,
            currency = runCatching { Currency.getInstance(entity.currencyCode) }
                .getOrElse { CountryCurrencyMapper.resolve(entity.countryIso) },
            phoneRegion = entity.phoneRegion,
            timezone = TimeZone.getTimeZone(entity.timezoneId),
        )
    }

    override suspend fun saveCurrent(context: SimContext) {
        dao.upsert(
            SimContextSnapshotEntity(
                id = SimContextSnapshotEntity.SINGLETON_ID,
                mcc = context.mcc,
                mnc = context.mnc,
                countryIso = context.countryIso,
                operatorName = context.operatorName,
                currencyCode = context.currency.currencyCode,
                phoneRegion = context.phoneRegion,
                timezoneId = context.timezone.id,
                capturedAtMillis = System.currentTimeMillis(),
            ),
        )
    }

    override suspend fun clear() {
        dao.clear()
    }
}
