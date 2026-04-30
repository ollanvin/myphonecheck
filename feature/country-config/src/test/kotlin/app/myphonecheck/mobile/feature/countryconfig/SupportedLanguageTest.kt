package app.myphonecheck.mobile.feature.countryconfig

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * SupportedLanguage enum 단위 테스트 — EN 단일 (헌법 §9-1).
 *
 * 검증:
 *  1. 단일 entry (EN) 무결성
 *  2. fromCode("en") 대소문자 무관 매칭
 *  3. fromCode(타 언어) → null
 *  4. fromCodeOrDefault(타 언어) → EN
 *  5. EN 속성 무결성
 */
class SupportedLanguageTest {

    @Test
    fun `supported languages contain exactly 1 entry (EN)`() {
        assertEquals(1, SupportedLanguage.entries.size)
        assertEquals(SupportedLanguage.EN, SupportedLanguage.entries.single())
    }

    @Test
    fun `only EN code is registered`() {
        val codes = SupportedLanguage.entries.map { it.code }.toSet()
        assertEquals(setOf("en"), codes)
    }

    @Test
    fun `fromCode with lowercase en returns EN`() {
        assertEquals(SupportedLanguage.EN, SupportedLanguage.fromCode("en"))
    }

    @Test
    fun `fromCode with uppercase EN returns EN`() {
        assertEquals(SupportedLanguage.EN, SupportedLanguage.fromCode("EN"))
    }

    @Test
    fun `fromCode with mixed case En returns EN`() {
        assertEquals(SupportedLanguage.EN, SupportedLanguage.fromCode("En"))
    }

    @Test
    fun `fromCode with other languages returns null`() {
        assertNull(SupportedLanguage.fromCode("ko"))
        assertNull(SupportedLanguage.fromCode("ja"))
        assertNull(SupportedLanguage.fromCode("zh"))
        assertNull(SupportedLanguage.fromCode("ru"))
        assertNull(SupportedLanguage.fromCode("es"))
        assertNull(SupportedLanguage.fromCode("ar"))
        assertNull(SupportedLanguage.fromCode("fr"))
        assertNull(SupportedLanguage.fromCode("de"))
        assertNull(SupportedLanguage.fromCode("xx"))
    }

    @Test
    fun `fromCode with empty string returns null`() {
        assertNull(SupportedLanguage.fromCode(""))
    }

    @Test
    fun `fromCodeOrDefault with en returns EN`() {
        assertEquals(SupportedLanguage.EN, SupportedLanguage.fromCodeOrDefault("en"))
    }

    @Test
    fun `fromCodeOrDefault with non-en falls back to EN`() {
        assertEquals(SupportedLanguage.EN, SupportedLanguage.fromCodeOrDefault("ko"))
        assertEquals(SupportedLanguage.EN, SupportedLanguage.fromCodeOrDefault("fr"))
        assertEquals(SupportedLanguage.EN, SupportedLanguage.fromCodeOrDefault("de"))
        assertEquals(SupportedLanguage.EN, SupportedLanguage.fromCodeOrDefault(""))
    }

    @Test
    fun `EN has correct properties`() {
        val lang = SupportedLanguage.EN
        assertEquals("en", lang.code)
        assertEquals("English", lang.nativeName)
        assertEquals("English", lang.englishName)
    }
}
