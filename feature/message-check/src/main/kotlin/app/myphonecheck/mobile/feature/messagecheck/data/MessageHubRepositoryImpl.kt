package app.myphonecheck.mobile.feature.messagecheck.data

import app.myphonecheck.mobile.core.util.PhoneNumberNormalizer
import app.myphonecheck.mobile.data.contacts.ContactsDataSource
import app.myphonecheck.mobile.data.localcache.dao.HubMessageDao
import app.myphonecheck.mobile.data.localcache.entity.HubMessageEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageHubRepositoryImpl @Inject constructor(
    private val hubMessageDao: HubMessageDao,
    private val contactsDataSource: ContactsDataSource,
) : MessageHubRepository {

    private val urlRegex = Regex("""https?://[\w.-]+(?:/\S*)?""")
    private val phoneChunkRegex = Regex("""\d{2,4}-\d{3,4}-\d{4}""")
    private val mobileRegex = Regex("""010\d{8}""")

    override suspend fun saveSms(senderNumber: String, content: String, receivedAt: Long) {
        val urls = extractUrls(content)
        val nums = extractPhoneNumbers(content)
        val category = classifyCategory(content, senderNumber)
        hubMessageDao.insert(
            HubMessageEntity(
                senderIdentifier = senderNumber.trim(),
                senderLabel = null,
                content = content,
                hasLink = urls.isNotEmpty(),
                extractedUrls = jsonArray(urls),
                extractedNumbers = jsonArray(nums),
                category = category.name,
                receivedAt = receivedAt,
                source = MessageSource.SMS.name,
            ),
        )
    }

    override suspend fun saveNotification(
        packageName: String,
        senderLabel: String?,
        content: String,
        receivedAt: Long,
    ) {
        val urls = extractUrls(content)
        val nums = extractPhoneNumbers(content)
        val category = classifyCategory(content, senderIdentifierForContact = null)
        hubMessageDao.insert(
            HubMessageEntity(
                senderIdentifier = packageName,
                senderLabel = senderLabel,
                content = content,
                hasLink = urls.isNotEmpty(),
                extractedUrls = jsonArray(urls),
                extractedNumbers = jsonArray(nums),
                category = category.name,
                receivedAt = receivedAt,
                source = MessageSource.NOTIFICATION.name,
            ),
        )
    }

    override fun observeRecent(limit: Int): Flow<List<MessageHubItem>> =
        hubMessageDao.observeRecent(limit).map { rows -> rows.map(::toItem) }

    override fun observeBySender(sender: String): Flow<List<MessageHubItem>> =
        hubMessageDao.observeBySender(sender).map { rows -> rows.map(::toItem) }

    override fun observeWeeklySenderProfiles(): Flow<List<SenderWeeklyProfile>> {
        val weekMs = 7L * 24 * 60 * 60 * 1000
        val sinceMs = System.currentTimeMillis() - weekMs
        return hubMessageDao.observeSenderProfiles(sinceMs).map { rows ->
            rows.map {
                SenderWeeklyProfile(
                    senderIdentifier = it.sender_identifier,
                    senderLabel = it.sender_label,
                    count = it.count,
                    lastAt = it.last_at,
                    linkCount = it.link_count,
                )
            }
        }
    }

    override suspend fun cleanupOldRecords(retainDays: Int): Int {
        val cutoff = System.currentTimeMillis() - retainDays * 24L * 60 * 60 * 1000
        return hubMessageDao.deleteOlderThan(cutoff)
    }

    private fun toItem(e: HubMessageEntity): MessageHubItem = MessageHubItem(
        id = e.id,
        senderIdentifier = e.senderIdentifier,
        senderLabel = e.senderLabel,
        content = e.content,
        hasLink = e.hasLink,
        extractedUrls = parseJsonArray(e.extractedUrls),
        extractedNumbers = parseJsonArray(e.extractedNumbers),
        category = MessageCategory.valueOf(e.category),
        receivedAt = e.receivedAt,
        source = MessageSource.valueOf(e.source),
    )

    /**
     * WO-DATA-MSG-001 keyword tiers (ASCII matcher avoids IDE UTF-16 BOM issues on Windows).
     * Same semantics as vision WO examples (promotion / transaction / public tiers).
     */
    private suspend fun classifyCategory(content: String, senderIdentifierForContact: String?): MessageCategory {
        val cc = Locale.getDefault().country.ifBlank { "KR" }
        if (senderIdentifierForContact != null) {
            val normalized = PhoneNumberNormalizer.normalize(senderIdentifierForContact.trim(), cc)?.e164
            if (normalized != null && contactsDataSource.isContactSaved(normalized)) {
                return MessageCategory.PERSONAL
            }
        }
        val hay = content.lowercase(Locale.getDefault())
        return when {
            listOf("discount", "coupon", "event", "sale", "benefit").any { hay.contains(it) } ->
                MessageCategory.PROMOTION
            listOf("payment", "deposit", "withdraw", "delivery", "confirmation").any { hay.contains(it) } ->
                MessageCategory.TRANSACTION
            listOf("hospital", "school", "government", "health insurance").any { hay.contains(it) } ->
                MessageCategory.PUBLIC
            else -> MessageCategory.UNKNOWN
        }
    }

    internal fun extractUrls(content: String): List<String> =
        urlRegex.findAll(content).map { it.value }.distinct().toList()

    internal fun extractPhoneNumbers(content: String): List<String> {
        val out = linkedSetOf<String>()
        phoneChunkRegex.findAll(content).forEach { out.add(it.value) }
        mobileRegex.findAll(content).forEach { out.add(it.value) }
        return out.toList()
    }

    private fun jsonArray(items: List<String>): String =
        buildString {
            append('[')
            items.forEachIndexed { index, raw ->
                if (index > 0) append(',')
                append('"')
                val s = raw ?: ""
                for (ch in s) {
                    when (ch) {
                        '\\', '"' -> {
                            append('\\')
                            append(ch)
                        }
                        '\n' -> append("\\n")
                        '\r' -> append("\\r")
                        '\t' -> append("\\t")
                        else -> append(ch)
                    }
                }
                append('"')
            }
            append(']')
        }

    private fun parseJsonArray(json: String): List<String> {
        if (json.isBlank() || json == "[]") return emptyList()
        return try {
            val ja = JSONArray(json)
            List(ja.length()) { ja.getString(it) }
        } catch (_: Exception) {
            emptyList()
        }
    }
}
