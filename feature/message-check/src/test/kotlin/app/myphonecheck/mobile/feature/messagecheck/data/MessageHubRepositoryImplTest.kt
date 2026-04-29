package app.myphonecheck.mobile.feature.messagecheck.data

import app.myphonecheck.mobile.core.model.DevicePatternProfile
import app.myphonecheck.mobile.core.util.GlobalNumberEngineProfileStore
import app.myphonecheck.mobile.data.contacts.ContactsDataSource
import app.myphonecheck.mobile.data.localcache.dao.HubMessageDao
import app.myphonecheck.mobile.data.localcache.dao.SenderProfileRow
import app.myphonecheck.mobile.data.localcache.entity.HubMessageEntity
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MessageHubRepositoryImplTest {

    private lateinit var dao: FakeHubMessageDao
    private lateinit var contacts: ContactsDataSource
    private lateinit var repo: MessageHubRepositoryImpl

    @Before
    fun setup() {
        GlobalNumberEngineProfileStore.update(
            DevicePatternProfile(
                primaryCountryCode = "KR",
                preferredCountryCodes = listOf("KR"),
            ),
        )
        dao = FakeHubMessageDao()
        contacts = mockk(relaxed = true)
        coEvery { contacts.isContactSaved(any()) } returns false
        repo = MessageHubRepositoryImpl(dao, contacts)
    }

    @Test
    fun `saveSms persists with SMS source extracts matching URL`() = runBlocking {
        repo.saveSms(
            "01012345678",
            "click https://example.com path promo",
            1000L,
        )
        val saved = dao.inserted.first()
        assertEquals(MessageSource.SMS.name, saved.source)
        assertEquals(true, saved.hasLink)
        assertEquals("""["https://example.com"]""", saved.extractedUrls)
    }

    @Test
    fun `classify PROMOTION when coupon keyword present`() = runBlocking {
        repo.saveSms("01099998888", "coupon inside message", 100L)
        assertEquals(MessageCategory.PROMOTION.name, dao.inserted.last().category)
    }

    @Test
    fun `classify TRANSACTION when payment keyword present`() = runBlocking {
        repo.saveSms("01099998888", "payment completed", 100L)
        assertEquals(MessageCategory.TRANSACTION.name, dao.inserted.last().category)
    }

    @Test
    fun `classify UNKNOWN when no keywords and no saved contact`() = runBlocking {
        repo.saveSms("01099998888", "plain notification text", 100L)
        assertEquals(MessageCategory.UNKNOWN.name, dao.inserted.last().category)
    }

    @Test
    fun `classify PUBLIC when institution keyword present`() = runBlocking {
        repo.saveSms("01099998888", "hospital appointment reminder", 100L)
        assertEquals(MessageCategory.PUBLIC.name, dao.inserted.last().category)
    }

    @Test
    fun `observeWeeklySenderProfiles aggregates counts and link totals`() = runBlocking {
        val now = System.currentTimeMillis()
        dao.forceSeed(
            listOf(
                stub(senderIdentifier = "A", hasLink = true, receivedAt = now - 1000L),
                stub(senderIdentifier = "A", hasLink = false, receivedAt = now),
            ),
        )
        val profiles = repo.observeWeeklySenderProfiles().first()
        assertEquals(1, profiles.size)
        assertEquals("A", profiles.first().senderIdentifier)
        assertEquals(2, profiles.first().count)
        assertEquals(now, profiles.first().lastAt)
        assertEquals(1, profiles.first().linkCount)
    }

    @Test
    fun `cleanupOldRecords removes stale rows`() = runBlocking {
        dao.forceSeed(listOf(stub(senderIdentifier = "x", receivedAt = 0L)))
        val removed = repo.cleanupOldRecords(retainDays = 90)
        assertTrue(removed >= 1)
        assertTrue(dao.rows.value.isEmpty())
    }

    @Test
    fun `extractUrls helper finds http URL`() {
        val urls = repo.extractUrls("see http://x.com/y")
        assertEquals(listOf("http://x.com/y"), urls)
    }

    @Test
    fun `saveNotification sets NOTIFICATION source`() = runBlocking {
        repo.saveNotification("com.example.app", "App", "body", 1L)
        assertEquals(MessageSource.NOTIFICATION.name, dao.inserted.last().source)
    }

    private fun stub(
        senderIdentifier: String,
        senderLabel: String? = null,
        content: String = "c",
        hasLink: Boolean = false,
        receivedAt: Long,
    ): HubMessageEntity = HubMessageEntity(
        id = 0,
        senderIdentifier = senderIdentifier,
        senderLabel = senderLabel,
        content = content,
        hasLink = hasLink,
        extractedUrls = "[]",
        extractedNumbers = "[]",
        category = MessageCategory.UNKNOWN.name,
        receivedAt = receivedAt,
        source = MessageSource.SMS.name,
    )

    /** Minimal DAO backing store for repository tests */
    private class FakeHubMessageDao : HubMessageDao {

        val inserted = mutableListOf<HubMessageEntity>()
        val rows = MutableStateFlow<List<HubMessageEntity>>(emptyList())

        fun forceSeed(rowsToUse: List<HubMessageEntity>) {
            rows.value = rowsToUse.mapIndexed { idx, e ->
                e.copy(id = (idx + 1).toLong())
            }
        }

        override suspend fun insert(entity: HubMessageEntity): Long {
            val id = (rows.value.maxOfOrNull { it.id } ?: 0L) + 1L
            val withId = entity.copy(id = id)
            inserted += withId
            rows.value = rows.value + withId
            return id
        }

        override fun observeRecent(limit: Int): Flow<List<HubMessageEntity>> =
            rows.map { list ->
                list.sortedByDescending { it.receivedAt }.take(limit)
            }

        override fun observeBySender(sender: String): Flow<List<HubMessageEntity>> =
            rows.map { list ->
                list.filter { it.senderIdentifier == sender }.sortedByDescending { it.receivedAt }
            }

        override fun observeSenderProfiles(sinceMs: Long): Flow<List<SenderProfileRow>> =
            rows.map { list ->
                val filtered = list.filter { it.receivedAt >= sinceMs }
                filtered.groupBy { it.senderIdentifier to it.senderLabel }.map { (key, group) ->
                    SenderProfileRow(
                        sender_identifier = key.first,
                        sender_label = key.second,
                        count = group.size,
                        last_at = group.maxOf { it.receivedAt },
                        link_count = group.count { it.hasLink },
                    )
                }.sortedByDescending { it.count }
            }

        override suspend fun deleteOlderThan(beforeMs: Long): Int {
            val before = rows.value.size
            val kept = rows.value.filter { it.receivedAt >= beforeMs }
            val removed = before - kept.size
            rows.value = kept
            return removed
        }

        override suspend fun count(): Int = rows.value.size
    }
}
