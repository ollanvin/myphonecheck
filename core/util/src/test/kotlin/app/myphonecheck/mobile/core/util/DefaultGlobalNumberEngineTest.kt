package app.myphonecheck.mobile.core.util

import app.myphonecheck.mobile.core.model.DeviceNumberScanSnapshot
import app.myphonecheck.mobile.core.model.DevicePatternProfile
import app.myphonecheck.mobile.core.model.NumberParseInput
import app.myphonecheck.mobile.core.model.NumberResolutionStage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultGlobalNumberEngineTest {

    private val engine = DefaultGlobalNumberEngine()

    @Test
    fun `alternate KR forms resolve to same canonical key`() {
        val local = engine.resolve(
            NumberParseInput(
                rawNumber = "010-1234-5678",
                defaultCountryCode = "KR",
            ),
        )
        val international = engine.resolve(
            NumberParseInput(
                rawNumber = "+82 10 1234 5678",
                defaultCountryCode = "ZZ",
            ),
        )

        assertTrue(local.isValid)
        assertEquals("+821012345678", local.canonicalNumber)
        assertEquals(local.canonicalKey, international.canonicalKey)
    }

    @Test
    fun `device profile resolves ambiguous local number`() {
        val result = engine.resolve(
            NumberParseInput(
                rawNumber = "01012345678",
                defaultCountryCode = null,
                devicePatternProfile = DevicePatternProfile(
                    primaryCountryCode = "KR",
                    preferredCountryCodes = listOf("KR", "JP"),
                    dominantNumberLengths = listOf(11),
                    usesNationalTrunkPrefix = true,
                    sampleSize = 20,
                ),
            ),
        )

        assertTrue(result.isValid)
        assertEquals(NumberResolutionStage.DEVICE_PROFILE, result.resolutionStage)
        assertTrue(result.inferredFromDeviceProfile)
        assertEquals("+821012345678", result.canonicalNumber)
    }

    @Test
    fun `scanner derives primary country and formatting habits`() {
        val scanner = DefaultDevicePatternProfileScanner(engine)
        val profile = kotlinx.coroutines.runBlocking {
            scanner.scan(
                DeviceNumberScanSnapshot(
                    callHistoryNumbers = listOf("010-1234-5678", "02-555-0199"),
                    smsSenderNumbers = listOf("+82 10 9999 1111"),
                    contactNumbers = listOf("01012345678"),
                    defaultCountryCode = "KR",
                ),
            )
        }

        assertEquals("KR", profile.primaryCountryCode)
        assertTrue(profile.preferredCountryCodes.contains("KR"))
        assertTrue(profile.usesSeparators)
        assertFalse(profile.commonPrefixes.isEmpty())
    }
}
