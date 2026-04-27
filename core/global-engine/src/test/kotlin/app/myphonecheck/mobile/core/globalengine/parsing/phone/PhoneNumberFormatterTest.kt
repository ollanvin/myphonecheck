package app.myphonecheck.mobile.core.globalengine.parsing.phone

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PhoneNumberFormatterTest {

    private val formatter = PhoneNumberFormatter()

    @Test
    fun `same region E164 displayed in NATIONAL format`() {
        val display = formatter.formatForDisplay("+821012345678", "KR")
        // NATIONAL 양식에는 0이 포함되어야 함
        assertTrue(display.startsWith("010"))
    }

    @Test
    fun `cross region E164 displayed in INTERNATIONAL format`() {
        val display = formatter.formatForDisplay("+12025550181", "KR")
        assertTrue(display.startsWith("+1"))
    }

    @Test
    fun `unparseable input returns input as fallback`() {
        val display = formatter.formatForDisplay("garbage", "KR")
        assertEquals("garbage", display)
    }
}
