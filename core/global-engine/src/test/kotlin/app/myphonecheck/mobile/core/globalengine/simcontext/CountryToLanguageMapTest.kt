package app.myphonecheck.mobile.core.globalengine.simcontext

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CountryToLanguageMapTest {

    @Test
    fun `KR maps to ko`() {
        assertEquals("ko", CountryToLanguageMap.resolve("KR"))
    }

    @Test
    fun `Case insensitive lookup`() {
        assertEquals("ko", CountryToLanguageMap.resolve("kr"))
        assertEquals("ja", CountryToLanguageMap.resolve("jp"))
    }

    @Test
    fun `Unknown country returns null`() {
        assertNull(CountryToLanguageMap.resolve("ZZ"))
        assertNull(CountryToLanguageMap.resolve(""))
    }

    @Test
    fun `Major regions covered`() {
        // 동아시아
        assertNotNull(CountryToLanguageMap.resolve("KR"))
        assertNotNull(CountryToLanguageMap.resolve("JP"))
        assertNotNull(CountryToLanguageMap.resolve("CN"))
        assertNotNull(CountryToLanguageMap.resolve("TW"))
        // 영어권 5개국
        assertNotNull(CountryToLanguageMap.resolve("US"))
        assertNotNull(CountryToLanguageMap.resolve("GB"))
        assertNotNull(CountryToLanguageMap.resolve("AU"))
        assertNotNull(CountryToLanguageMap.resolve("CA"))
        assertNotNull(CountryToLanguageMap.resolve("NZ"))
        // 유럽 주요
        assertNotNull(CountryToLanguageMap.resolve("DE"))
        assertNotNull(CountryToLanguageMap.resolve("FR"))
        assertNotNull(CountryToLanguageMap.resolve("ES"))
        assertNotNull(CountryToLanguageMap.resolve("IT"))
        assertNotNull(CountryToLanguageMap.resolve("RU"))
        assertNotNull(CountryToLanguageMap.resolve("PT"))
        // 기타
        assertNotNull(CountryToLanguageMap.resolve("BR"))
        assertNotNull(CountryToLanguageMap.resolve("IN"))
        assertNotNull(CountryToLanguageMap.resolve("TR"))
        assertNotNull(CountryToLanguageMap.resolve("SA"))
    }

    @Test
    fun `BCP-47 region tags preserved for English variants`() {
        assertEquals("en-US", CountryToLanguageMap.resolve("US"))
        assertEquals("en-GB", CountryToLanguageMap.resolve("GB"))
    }

    @Test
    fun `Chinese variants distinguished by region`() {
        assertEquals("zh-CN", CountryToLanguageMap.resolve("CN"))
        assertEquals("zh-TW", CountryToLanguageMap.resolve("TW"))
        assertEquals("zh-HK", CountryToLanguageMap.resolve("HK"))
    }

    @Test
    fun `Portuguese variants distinguished`() {
        assertEquals("pt-PT", CountryToLanguageMap.resolve("PT"))
        assertEquals("pt-BR", CountryToLanguageMap.resolve("BR"))
    }

    @Test
    fun `Total mapping count is at least 27`() {
        // WO 명시 27개국 이상 (확장 매핑 정합).
        assertTrue("size=${CountryToLanguageMap.size()}", CountryToLanguageMap.size() >= 27)
    }
}
