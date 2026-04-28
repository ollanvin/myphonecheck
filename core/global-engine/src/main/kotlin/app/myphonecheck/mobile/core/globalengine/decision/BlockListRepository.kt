package app.myphonecheck.mobile.core.globalengine.decision

/**
 * 차단 목록 저장소 (Architecture v2.1.0 §31, Layer 2).
 *
 * 사용자 직접 차단 등록 — 헌법 §3 결정권 중앙집중 금지 정합.
 * 실 구현은 :feature:call-screening (RoomBlockListRepository, Room v15 blocked_identifier).
 */
interface BlockListRepository {
    suspend fun isBlocked(key: String, type: IdentifierType): Boolean
    suspend fun add(key: String, type: IdentifierType, source: String = "user")
    suspend fun remove(key: String, type: IdentifierType)
    suspend fun listAll(): List<BlockedIdentifier>
}

data class BlockedIdentifier(
    val key: String,
    val type: IdentifierType,
    val addedAtMillis: Long,
    val source: String,
)
