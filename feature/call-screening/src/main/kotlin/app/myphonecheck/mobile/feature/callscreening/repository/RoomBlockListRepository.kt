package app.myphonecheck.mobile.feature.callscreening.repository

import app.myphonecheck.mobile.core.globalengine.decision.BlockListRepository
import app.myphonecheck.mobile.core.globalengine.decision.BlockedIdentifier
import app.myphonecheck.mobile.core.globalengine.decision.IdentifierType
import app.myphonecheck.mobile.data.localcache.dao.BlockedIdentifierDao
import app.myphonecheck.mobile.data.localcache.entity.BlockedIdentifierEntity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * BlockListRepository Room 기반 구현 (Architecture v2.1.0 §31).
 *
 * 모듈 위치: :feature:call-screening (data → core 의존 방향 회피).
 * :data:local-cache는 코어 인터페이스를 모름 — 본 모듈에서 두 영역 묶어 바인딩.
 */
@Singleton
class RoomBlockListRepository @Inject constructor(
    private val dao: BlockedIdentifierDao,
) : BlockListRepository {

    override suspend fun isBlocked(key: String, type: IdentifierType): Boolean =
        dao.isBlocked(key, type.name)

    override suspend fun add(key: String, type: IdentifierType, source: String) {
        dao.upsert(
            BlockedIdentifierEntity(
                key = key,
                type = type.name,
                addedAtMillis = System.currentTimeMillis(),
                source = source,
            ),
        )
    }

    override suspend fun remove(key: String, type: IdentifierType) {
        dao.delete(key, type.name)
    }

    override suspend fun listAll(): List<BlockedIdentifier> = dao.listAll().map { entity ->
        BlockedIdentifier(
            key = entity.key,
            type = runCatching { IdentifierType.valueOf(entity.type) }
                .getOrDefault(IdentifierType.PHONE_E164),
            addedAtMillis = entity.addedAtMillis,
            source = entity.source,
        )
    }
}
