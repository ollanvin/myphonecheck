package app.myphonecheck.mobile.feature.initialscan

import app.myphonecheck.mobile.core.globalengine.simcontext.SimContext
import app.myphonecheck.mobile.data.localcache.dao.CallBaseDao
import app.myphonecheck.mobile.data.localcache.dao.PackageBaseDao
import app.myphonecheck.mobile.data.localcache.dao.SimContextSnapshotDao
import app.myphonecheck.mobile.data.localcache.dao.SmsBaseDao
import app.myphonecheck.mobile.data.localcache.entity.SimContextSnapshotEntity
import app.myphonecheck.mobile.feature.initialscan.repository.BaseDataRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Currency
import java.util.TimeZone

class BaseDataRepositoryTest {

    private val callBase = mockk<CallBaseDao>(relaxUnitFun = true)
    private val smsBase = mockk<SmsBaseDao>(relaxUnitFun = true)
    private val packageBase = mockk<PackageBaseDao>(relaxUnitFun = true)
    private val simSnapshot = mockk<SimContextSnapshotDao>(relaxUnitFun = true)

    private val repo = BaseDataRepository(callBase, smsBase, packageBase, simSnapshot)

    @Test
    fun `saveSimContext writes singleton entity with derived currency code`() = runTest {
        val sim = SimContext(
            mcc = "262", mnc = "01", countryIso = "DE", operatorName = "Telekom",
            currency = Currency.getInstance("EUR"), phoneRegion = "DE",
            timezone = TimeZone.getTimeZone("Europe/Berlin"),
        )
        val captured = slot<SimContextSnapshotEntity>()
        coEvery { simSnapshot.upsert(capture(captured)) } returns Unit

        repo.saveSimContext(sim)

        assertEquals(SimContextSnapshotEntity.SINGLETON_ID, captured.captured.id)
        assertEquals("EUR", captured.captured.currencyCode)
        assertEquals("DE", captured.captured.countryIso)
        assertEquals("Europe/Berlin", captured.captured.timezoneId)
    }

    @Test
    fun `isInitialScanCompleted true when snapshot exists`() = runTest {
        coEvery { simSnapshot.get(any()) } returns SimContextSnapshotEntity(
            id = SimContextSnapshotEntity.SINGLETON_ID,
            mcc = "", mnc = "", countryIso = "KR", operatorName = "",
            currencyCode = "KRW", phoneRegion = "KR", timezoneId = "UTC",
            capturedAtMillis = 0L,
        )
        assertTrue(repo.isInitialScanCompleted())
    }

    @Test
    fun `isInitialScanCompleted false when snapshot null`() = runTest {
        coEvery { simSnapshot.get(any()) } returns null
        assertFalse(repo.isInitialScanCompleted())
    }

    @Test
    fun `clear cascades to all four DAOs`() = runTest {
        repo.clear()
        coVerify { callBase.clear() }
        coVerify { smsBase.clear() }
        coVerify { packageBase.clear() }
        coVerify { simSnapshot.clear() }
    }
}
