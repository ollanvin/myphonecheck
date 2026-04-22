package app.myphonecheck.mobile.feature.pushtrash.repository

import app.myphonecheck.mobile.data.localcache.dao.BlockedAppDao
import app.myphonecheck.mobile.data.localcache.dao.BlockedChannelDao
import app.myphonecheck.mobile.data.localcache.dao.PushNotificationObservationDao
import app.myphonecheck.mobile.data.localcache.dao.TrashedNotificationDao
import app.myphonecheck.mobile.data.localcache.entity.BlockedAppEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class PushTrashRepositoryTest {

    private lateinit var blockedChannelDao: BlockedChannelDao
    private lateinit var blockedAppDao: BlockedAppDao
    private lateinit var trashedNotificationDao: TrashedNotificationDao
    private lateinit var observationDao: PushNotificationObservationDao
    private lateinit var repository: PushTrashRepository

    @Before
    fun setup() {
        blockedChannelDao = mockk(relaxed = true)
        blockedAppDao = mockk(relaxed = true)
        trashedNotificationDao = mockk(relaxed = true)
        observationDao = mockk(relaxed = true)
        repository = PushTrashRepository(
            blockedChannelDao,
            blockedAppDao,
            trashedNotificationDao,
            observationDao,
        )
    }

    @Test
    fun decide_allBlocked_returnsBlock() = runTest {
        coEvery { blockedAppDao.find("com.example.app") } returns BlockedAppEntity(
            packageName = "com.example.app",
            mode = PushTrashRepository.MODE_ALL_BLOCKED,
            blockedAt = 1L,
        )
        assertEquals(PushTrashRepository.Decision.Block, repository.decide("com.example.app", "ch1"))
    }

    @Test
    fun decide_allAllowed_returnsAllow_evenIfChannelBlocked() = runTest {
        coEvery { blockedAppDao.find("com.example.app") } returns BlockedAppEntity(
            packageName = "com.example.app",
            mode = PushTrashRepository.MODE_ALL_ALLOWED,
            blockedAt = 1L,
        )
        coEvery { blockedChannelDao.isBlocked("com.example.app", "ch1") } returns true
        assertEquals(PushTrashRepository.Decision.Allow, repository.decide("com.example.app", "ch1"))
    }

    @Test
    fun decide_channelBlocked_returnsBlock() = runTest {
        coEvery { blockedAppDao.find("com.example.app") } returns null
        coEvery { blockedChannelDao.isBlocked("com.example.app", "promo") } returns true
        assertEquals(PushTrashRepository.Decision.Block, repository.decide("com.example.app", "promo"))
    }

    @Test
    fun decide_noRule_returnsAllow() = runTest {
        coEvery { blockedAppDao.find("com.example.app") } returns null
        coEvery { blockedChannelDao.isBlocked("com.example.app", "order") } returns false
        assertEquals(PushTrashRepository.Decision.Allow, repository.decide("com.example.app", "order"))
    }

    @Test
    fun decide_blankChannel_skipsChannelLookup() = runTest {
        coEvery { blockedAppDao.find("com.example.app") } returns null
        assertEquals(PushTrashRepository.Decision.Allow, repository.decide("com.example.app", ""))
        coVerify(exactly = 0) { blockedChannelDao.isBlocked(any(), any()) }
    }
}
