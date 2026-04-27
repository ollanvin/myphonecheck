package app.myphonecheck.mobile.feature.settings.v2

import app.myphonecheck.mobile.core.globalengine.simcontext.SimContext
import app.myphonecheck.mobile.data.localcache.dao.SimContextSnapshotDao
import app.myphonecheck.mobile.data.localcache.entity.SimContextSnapshotEntity
import app.myphonecheck.mobile.feature.settings.v2.repository.RoomSimContextStorage
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Currency
import java.util.TimeZone

class RoomSimContextStorageTest {

    private val dao = mockk<SimContextSnapshotDao>(relaxUnitFun = true)
    private val storage = RoomSimContextStorage(dao)

    @Test
    fun `loadPrevious returns null when DAO empty`() = runTest {
        coEvery { dao.get(any()) } returns null
        assertNull(storage.loadPrevious())
    }

    @Test
    fun `loadPrevious converts entity to SimContext`() = runTest {
        coEvery { dao.get(any()) } returns SimContextSnapshotEntity(
            id = SimContextSnapshotEntity.SINGLETON_ID,
            mcc = "262", mnc = "01", countryIso = "DE", operatorName = "Telekom",
            currencyCode = "EUR", phoneRegion = "DE", timezoneId = "Europe/Berlin",
            capturedAtMillis = 0L,
        )
        val ctx = storage.loadPrevious()!!
        assertEquals("DE", ctx.countryIso)
        assertEquals("EUR", ctx.currency.currencyCode)
        assertEquals("DE", ctx.phoneRegion)
        assertEquals("Europe/Berlin", ctx.timezone.id)
    }

    @Test
    fun `saveCurrent persists ISO 4217 currency code`() = runTest {
        val captured = slot<SimContextSnapshotEntity>()
        coEvery { dao.upsert(capture(captured)) } returns Unit

        storage.saveCurrent(
            SimContext(
                mcc = "450", mnc = "08", countryIso = "KR", operatorName = "SK",
                currency = Currency.getInstance("KRW"), phoneRegion = "KR",
                timezone = TimeZone.getTimeZone("Asia/Seoul"),
            ),
        )

        assertEquals(SimContextSnapshotEntity.SINGLETON_ID, captured.captured.id)
        assertEquals("KRW", captured.captured.currencyCode)
        assertEquals("KR", captured.captured.countryIso)
    }

    @Test
    fun `clear delegates to DAO`() = runTest {
        storage.clear()
        coVerify { dao.clear() }
    }
}
