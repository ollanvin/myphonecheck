package app.myphonecheck.mobile.feature.tagsystem.repository

import app.myphonecheck.mobile.core.globalengine.decision.IdentifierType
import app.myphonecheck.mobile.core.globalengine.decision.TagPriority
import app.myphonecheck.mobile.core.globalengine.decision.TagRecord
import app.myphonecheck.mobile.core.globalengine.decision.TagRepository
import app.myphonecheck.mobile.data.localcache.dao.PhoneTagDao
import app.myphonecheck.mobile.data.localcache.entity.PhoneTagEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * TagRepository Room 기반 구현 (Architecture v2.1.0 §32 Tag System).
 *
 * 의존: :data:local-cache (PhoneTagDao) + :core:global-engine (TagRepository interface).
 * PR #27 RealTimeActionEngine이 TagRepository를 inject받음 — 본 PR에서 NoopTagRepository 대체.
 *
 * 헌법 정합:
 *  - §1 Out-Bound Zero: 디바이스 로컬만.
 *  - §2 In-Bound Zero: 사용자 입력 태그만 저장.
 *  - §3 결정권 중앙집중 금지: 사용자 부여·해제, 자동 채우기 0.
 */
@Singleton
class RoomTagRepository @Inject constructor(
    private val dao: PhoneTagDao,
) : TagRepository {

    override suspend fun findByKey(key: String, type: IdentifierType): TagRecord? {
        val entity = dao.findByKey(key, type.name) ?: return null
        return entity.toTagRecord()
    }

    /**
     * v2.6.0 §11 액션 2 (Tag) — 사용자 명시 라벨링. priority = REMIND_ME.
     * 본 메서드는 TagRepository 인터페이스 default override.
     */
    override suspend fun setTag(key: String, type: IdentifierType, tagText: String) {
        upsert(key, type, tagText, TagPriority.REMIND_ME)
    }

    suspend fun upsert(
        key: String,
        type: IdentifierType,
        tagText: String,
        priority: TagPriority,
    ) {
        dao.upsert(
            PhoneTagEntity(
                identifierKey = key,
                identifierType = type.name,
                tagText = tagText,
                priority = priority.name,
                createdAtMillis = System.currentTimeMillis(),
                lastSeenAtMillis = null,
                seenCount = 0,
            ),
        )
    }

    suspend fun delete(key: String, type: IdentifierType) {
        dao.delete(key, type.name)
    }

    suspend fun recordSeen(key: String, type: IdentifierType, atMillis: Long = System.currentTimeMillis()) {
        dao.recordSeen(key, type.name, atMillis)
    }

    fun observeAll(): Flow<List<TagRecord>> = dao.observeAll().map { list -> list.map { it.toTagRecord() } }

    suspend fun pendingReminders(thresholdMillis: Long): List<TagRecord> =
        dao.pendingReminders(thresholdMillis).map { it.toTagRecord() }

    suspend fun count(): Int = dao.count()

    private fun PhoneTagEntity.toTagRecord(): TagRecord = TagRecord(
        key = identifierKey,
        type = runCatching { IdentifierType.valueOf(identifierType) }
            .getOrDefault(IdentifierType.PHONE_E164),
        tagText = tagText,
        priority = runCatching { TagPriority.valueOf(priority) }
            .getOrDefault(TagPriority.REMIND_ME),
        lastSeenMillis = lastSeenAtMillis,
    )
}
