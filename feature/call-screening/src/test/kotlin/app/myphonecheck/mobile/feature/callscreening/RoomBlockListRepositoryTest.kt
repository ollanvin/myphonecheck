package app.myphonecheck.mobile.feature.callscreening

import app.myphonecheck.mobile.core.globalengine.decision.IdentifierType
import app.myphonecheck.mobile.data.localcache.dao.BlockedIdentifierDao
import app.myphonecheck.mobile.data.localcache.entity.BlockedIdentifierEntity
import app.myphonecheck.mobile.feature.callscreening.repository.RoomBlockListRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RoomBlockListRepositoryTest {

    private val dao = mockk<BlockedIdentifierDao>(relaxUnitFun = true)
    private val repo = RoomBlockListRepository(dao)

    @Test
    fun `isBlocked maps IdentifierType enum name to type column`() = runTest {
        coEvery { dao.isBlocked("+821012345678", "PHONE_E164") } returns true
        assertTrue(repo.isBlocked("+821012345678", IdentifierType.PHONE_E164))
        coEvery { dao.isBlocked("foo", "SMS_SENDER") } returns false
        assertFalse(repo.isBlocked("foo", IdentifierType.SMS_SENDER))
    }

    @Test
    fun `add upserts entity with type enum name and given source`() = runTest {
        val captured = slot<BlockedIdentifierEntity>()
        coEvery { dao.upsert(capture(captured)) } returns Unit

        repo.add("com.spam.app", IdentifierType.NOTIFICATION_PACKAGE, source = "user")

        assertEquals("com.spam.app", captured.captured.key)
        assertEquals("NOTIFICATION_PACKAGE", captured.captured.type)
        assertEquals("user", captured.captured.source)
        assertTrue(captured.captured.addedAtMillis > 0)
    }

    @Test
    fun `remove delegates to dao with type enum name`() = runTest {
        repo.remove("k", IdentifierType.PHONE_E164)
        coVerify { dao.delete("k", "PHONE_E164") }
    }

    @Test
    fun `listAll converts entities back to domain`() = runTest {
        coEvery { dao.listAll() } returns listOf(
            BlockedIdentifierEntity("k1", "PHONE_E164", 100L, "user"),
            BlockedIdentifierEntity("k2", "SMS_SENDER", 200L, "user"),
        )
        val list = repo.listAll()
        assertEquals(2, list.size)
        assertEquals(IdentifierType.PHONE_E164, list[0].type)
        assertEquals(IdentifierType.SMS_SENDER, list[1].type)
    }

    @Test
    fun `listAll falls back to PHONE_E164 for unknown type strings`() = runTest {
        coEvery { dao.listAll() } returns listOf(
            BlockedIdentifierEntity("k", "GARBAGE", 0L, "user"),
        )
        val list = repo.listAll()
        assertEquals(IdentifierType.PHONE_E164, list[0].type)
    }
}
