package app.myphonecheck.mobile.core.globalengine.search.publicfeed

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CountryScopeTest {

    @Test
    fun `GLOBAL matches any iso`() {
        assertTrue(CountryScope.GLOBAL.matches("KR"))
        assertTrue(CountryScope.GLOBAL.matches("US"))
        assertTrue(CountryScope.GLOBAL.matches(""))
    }

    @Test
    fun `COUNTRY matches exact case-insensitive`() {
        val kr = CountryScope.COUNTRY("KR")
        assertTrue(kr.matches("KR"))
        assertTrue(kr.matches("kr"))
        assertFalse(kr.matches("US"))
        assertFalse(kr.matches(""))
    }

    @Test
    fun `REGION matches any in list case-insensitive`() {
        val asia = CountryScope.REGION(listOf("KR", "JP", "TW"))
        assertTrue(asia.matches("KR"))
        assertTrue(asia.matches("jp"))
        assertTrue(asia.matches("TW"))
        assertFalse(asia.matches("US"))
    }
}
