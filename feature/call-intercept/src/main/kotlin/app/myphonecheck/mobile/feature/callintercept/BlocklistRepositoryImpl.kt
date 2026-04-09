package app.myphonecheck.mobile.feature.callintercept

import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * In-memory implementation of BlocklistRepository.
 *
 * TODO: Replace with Room-backed implementation for persistence across app restarts.
 */
@Singleton
class BlocklistRepositoryImpl @Inject constructor() : BlocklistRepository {

    private val blocklist = ConcurrentHashMap<String, BlockEntry>()

    override suspend fun addToBlocklist(
        phoneNumber: String,
        reason: String,
        timestamp: Long,
    ) {
        blocklist[phoneNumber] = BlockEntry(phoneNumber, reason, timestamp)
    }

    override suspend fun isBlocked(phoneNumber: String): Boolean {
        return blocklist.containsKey(phoneNumber)
    }

    override suspend fun removeFromBlocklist(phoneNumber: String) {
        blocklist.remove(phoneNumber)
    }

    override suspend fun getBlockedNumbers(): List<String> {
        return blocklist.keys.toList()
    }

    private data class BlockEntry(
        val phoneNumber: String,
        val reason: String,
        val timestamp: Long,
    )
}
