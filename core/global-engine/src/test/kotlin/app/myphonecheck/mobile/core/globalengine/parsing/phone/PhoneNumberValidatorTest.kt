package app.myphonecheck.mobile.core.globalengine.parsing.phone

import com.google.i18n.phonenumbers.PhoneNumberUtil
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PhoneNumberValidatorTest {

    private val validator = PhoneNumberValidator()

    @Test
    fun `valid KR mobile recognized as valid and possible`() {
        assertTrue(validator.isValid("010-1234-5678", "KR"))
        assertTrue(validator.isPossible("010-1234-5678", "KR"))
    }

    @Test
    fun `garbage input invalid and not possible`() {
        assertFalse(validator.isValid("xxx", "KR"))
        assertFalse(validator.isPossible("xxx", "KR"))
    }

    @Test
    fun `KR mobile classified as MOBILE`() {
        val type = validator.classify("010-1234-5678", "KR")
        assertEquals(PhoneNumberUtil.PhoneNumberType.MOBILE, type)
    }
}
