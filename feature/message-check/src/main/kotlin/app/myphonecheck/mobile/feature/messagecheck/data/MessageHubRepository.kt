package app.myphonecheck.mobile.feature.messagecheck.data

import kotlinx.coroutines.flow.Flow

interface MessageHubRepository {
    suspend fun saveSms(senderNumber: String, content: String, receivedAt: Long)
    suspend fun saveNotification(packageName: String, senderLabel: String?, content: String, receivedAt: Long)
    fun observeRecent(limit: Int = 200): Flow<List<MessageHubItem>>
    fun observeBySender(sender: String): Flow<List<MessageHubItem>>
    fun observeWeeklySenderProfiles(): Flow<List<SenderWeeklyProfile>>
    suspend fun cleanupOldRecords(retainDays: Int = 90): Int
}

data class MessageHubItem(
    val id: Long,
    val senderIdentifier: String,
    val senderLabel: String?,
    val content: String,
    val hasLink: Boolean,
    val extractedUrls: List<String>,
    val extractedNumbers: List<String>,
    val category: MessageCategory,
    val receivedAt: Long,
    val source: MessageSource,
)

enum class MessageCategory {
    PROMOTION,
    TRANSACTION,
    PUBLIC,
    PERSONAL,
    UNKNOWN,
}

enum class MessageSource {
    SMS,
    NOTIFICATION,
}

data class SenderWeeklyProfile(
    val senderIdentifier: String,
    val senderLabel: String?,
    val count: Int,
    val lastAt: Long,
    val linkCount: Int,
)
