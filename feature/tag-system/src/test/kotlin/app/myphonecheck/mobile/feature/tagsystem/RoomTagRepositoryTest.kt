package app.myphonecheck.mobile.feature.tagsystem

import app.myphonecheck.mobile.core.globalengine.decision.IdentifierType
import app.myphonecheck.mobile.core.globalengine.decision.TagPriority
import app.myphonecheck.mobile.data.localcache.dao.PhoneTagDao
import app.myphonecheck.mobile.data.localcache.entity.PhoneTagEntity
import app.myphonecheck.mobile.feature.tagsystem.repository.RoomTagRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RoomTagRepositoryTest {

    private val dao = mockk<PhoneTagDao>(relaxUnitFun = true)
    private val repo = RoomTagRepository(dao)

    @Test
    fun `findByKey returns null when DAO empty`() = runTest {
        coEvery { dao.findByKey("k", "PHONE_E164") } returns null
        assertNull(repo.findByKey("k", IdentifierType.PHONE_E164))
    }

    @Test
    fun `findByKey converts entity to TagRecord with enums`() = runTest {
        coEvery { dao.findByKey("+821012345678", "PHONE_E164") } returns PhoneTagEntity(
            identifierKey = "+821012345678",
            identifierType = "PHONE_E164",
            tagText = "조심",
            priority = "SUSPICIOUS",
            createdAtMillis = 0L,
            lastSeenAtMillis = 100L,
            seenCount = 3,
        )
        val record = repo.findByKey("+821012345678", IdentifierType.PHONE_E164)!!
        assertEquals("+821012345678", record.key)
        assertEquals(IdentifierType.PHONE_E164, record.type)
        assertEquals("조심", record.tagText)
        assertEquals(TagPriority.SUSPICIOUS, record.priority)
        assertEquals(100L, record.lastSeenMillis)
    }

    @Test
    fun `findByKey falls back to PHONE_E164 enum on garbage type`() = runTest {
        coEvery { dao.findByKey("k", "SMS_SENDER") } returns PhoneTagEntity(
            "k", "GARBAGE_TYPE", "x", "REMIND_ME", 0L, null, 0,
        )
        val record = repo.findByKey("k", IdentifierType.SMS_SENDER)!!
        assertEquals(IdentifierType.PHONE_E164, record.type)
    }

    @Test
    fun `upsert maps enum names to entity`() = runTest {
        val captured = slot<PhoneTagEntity>()
        coEvery { dao.upsert(capture(captured)) } returns Unit

        repo.upsert("1588", IdentifierType.SMS_SENDER, "확인필요", TagPriority.PENDING)

        assertEquals("1588", captured.captured.identifierKey)
        assertEquals("SMS_SENDER", captured.captured.identifierType)
        assertEquals("PENDING", captured.captured.priority)
        assertEquals(0, captured.captured.seenCount)
    }

    @Test
    fun `delete delegates to DAO with type enum name`() = runTest {
        repo.delete("k", IdentifierType.NOTIFICATION_PACKAGE)
        coVerify { dao.delete("k", "NOTIFICATION_PACKAGE") }
    }

    @Test
    fun `recordSeen passes through to DAO`() = runTest {
        repo.recordSeen("+821012345678", IdentifierType.PHONE_E164, atMillis = 999L)
        coVerify { dao.recordSeen("+821012345678", "PHONE_E164", 999L) }
    }

    @Test
    fun `pendingReminders maps results`() = runTest {
        coEvery { dao.pendingReminders(any()) } returns listOf(
            PhoneTagEntity("a", "PHONE_E164", "리마인드1", "REMIND_ME", 0L, null, 0),
            PhoneTagEntity("b", "PHONE_E164", "리마인드2", "REMIND_ME", 0L, 100L, 2),
        )
        val list = repo.pendingReminders(System.currentTimeMillis())
        assertEquals(2, list.size)
        assertEquals(TagPriority.REMIND_ME, list[0].priority)
    }
}
