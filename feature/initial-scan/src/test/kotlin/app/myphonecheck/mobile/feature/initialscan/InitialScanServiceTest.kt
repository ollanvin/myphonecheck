package app.myphonecheck.mobile.feature.initialscan

import app.myphonecheck.mobile.core.globalengine.simcontext.SimContext
import app.myphonecheck.mobile.data.localcache.entity.CallBaseEntity
import app.myphonecheck.mobile.data.localcache.entity.PackageBaseEntity
import app.myphonecheck.mobile.data.localcache.entity.SmsBaseEntity
import app.myphonecheck.mobile.feature.initialscan.repository.BaseDataRepository
import app.myphonecheck.mobile.feature.initialscan.service.CallLogScanner
import app.myphonecheck.mobile.feature.initialscan.service.InitialScanService
import app.myphonecheck.mobile.feature.initialscan.service.PackageInventoryScanner
import app.myphonecheck.mobile.feature.initialscan.service.SimContextScanner
import app.myphonecheck.mobile.feature.initialscan.service.SmsInboxScanner
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Currency
import java.util.TimeZone

class InitialScanServiceTest {

    private val sim = SimContext(
        mcc = "450", mnc = "08", countryIso = "KR", operatorName = "SK Telecom",
        currency = Currency.getInstance("KRW"), phoneRegion = "KR",
        timezone = TimeZone.getTimeZone("Asia/Seoul"),
    )

    private val simScanner = mockk<SimContextScanner>()
    private val callLogScanner = mockk<CallLogScanner>()
    private val smsInboxScanner = mockk<SmsInboxScanner>()
    private val packageScanner = mockk<PackageInventoryScanner>()
    private val baseDataRepository = mockk<BaseDataRepository>(relaxUnitFun = true)

    private val service = InitialScanService(
        simScanner, callLogScanner, smsInboxScanner, packageScanner, baseDataRepository,
    )

    @Test
    fun `execute scans SIM first then parallel scans others and persists`() = runTest {
        every { simScanner.scan() } returns sim
        coEvery { callLogScanner.scan(sim) } returns listOf(
            CallBaseEntity("+821012345678", "KR", 3, 1L, "MOBILE", 1L),
        )
        coEvery { smsInboxScanner.scan(sim) } returns listOf(
            SmsBaseEntity("1588", true, 5, 2L, "NOTIFICATION"),
        )
        coEvery { packageScanner.scan() } returns listOf(
            PackageBaseEntity("com.x", "X", "android.permission.RECORD_AUDIO", 0L, 0L),
        )

        val result = service.execute()

        assertEquals(1, result.callCount)
        assertEquals(1, result.smsCount)
        assertEquals(1, result.packageCount)
        assertEquals(sim, result.simContext)

        coVerifyOrder {
            simScanner.scan()
            baseDataRepository.saveSimContext(sim)
        }
        coVerify { callLogScanner.scan(sim) }
        coVerify { smsInboxScanner.scan(sim) }
        coVerify { packageScanner.scan() }
        coVerify { baseDataRepository.saveCallBase(any()) }
        coVerify { baseDataRepository.saveSmsBase(any()) }
        coVerify { baseDataRepository.savePackageBase(any()) }
    }

    @Test
    fun `execute returns counts equal to scanner output sizes`() = runTest {
        every { simScanner.scan() } returns sim
        coEvery { callLogScanner.scan(sim) } returns List(7) {
            CallBaseEntity("+821000000$it", "KR", 1, 0L, "MOBILE", 0L)
        }
        coEvery { smsInboxScanner.scan(sim) } returns List(11) {
            SmsBaseEntity("s$it", false, 1, 0L, "NORMAL")
        }
        coEvery { packageScanner.scan() } returns List(3) {
            PackageBaseEntity("p$it", "label", "perm", 0L, 0L)
        }

        val result = service.execute()
        assertEquals(7, result.callCount)
        assertEquals(11, result.smsCount)
        assertEquals(3, result.packageCount)
    }

    @Test
    fun `execute handles empty scanner outputs`() = runTest {
        every { simScanner.scan() } returns sim
        coEvery { callLogScanner.scan(sim) } returns emptyList()
        coEvery { smsInboxScanner.scan(sim) } returns emptyList()
        coEvery { packageScanner.scan() } returns emptyList()

        val result = service.execute()
        assertEquals(0, result.callCount)
        assertEquals(0, result.smsCount)
        assertEquals(0, result.packageCount)
    }
}
