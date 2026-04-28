package app.myphonecheck.mobile.core.globalengine.decision

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Tag 저장소 (Architecture v2.1.0 §32, Layer 2).
 *
 * 본 PR에서는 인터페이스만 정의 + Noop 기본 구현.
 * Tag System §32 코드 구현(Room v16 tag entity + UI)은 후속 PR.
 */
interface TagRepository {
    suspend fun findByKey(key: String, type: IdentifierType): TagRecord?
}

data class TagRecord(
    val key: String,
    val type: IdentifierType,
    val tagText: String,
    val priority: TagPriority,
    val lastSeenMillis: Long?,
)

enum class TagPriority {
    REMIND_ME,
    PENDING,
    SUSPICIOUS,
    ARCHIVE,
}

/**
 * Tag System §32 후속 PR 전 NoOp 기본. 모든 lookup 결과 = null.
 */
@Singleton
class NoopTagRepository @Inject constructor() : TagRepository {
    override suspend fun findByKey(key: String, type: IdentifierType): TagRecord? = null
}
