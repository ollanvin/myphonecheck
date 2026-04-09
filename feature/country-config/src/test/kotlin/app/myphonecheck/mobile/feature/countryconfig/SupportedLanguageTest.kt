package app.myphonecheck.mobile.feature.countryconfig

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * SupportedLanguage enum 단위 테스트.
 *
 * ┌──────────────────────────────────────────────────────────────┐
 * │ 검증 대상                                                     │
 * ├──────────────────────────────────────────────────────────────┤
 * │ 1. 7개 언어 entries 무결성                                    │
 * │ 2. fromCode() 대소문자 무관 매칭                              │
 * │ 3. fromCode() 미지원 코드 → null                             │
 * │ 4. fromCodeOrDefault() 미지원 코드 → EN                      │
 * │ 5. 각 언어의 code/nativeName/englishName 무결성              │
 * └──────────────────────────────────────────────────────────────┘
 */
class SupportedLanguageTest {

    // ═══════════════════════════════════════════════════════════
    // 1. 7개 언어 entries 무결성
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `supported languages contain exactly 7 entries`() {
        assertEquals(7, SupportedLanguage.entries.size)
    }

    @Test
    fun `all expected languages are present`() {
        val codes = SupportedLanguage.entries.map { it.code }.toSet()
        assertEquals(setOf("ko", "en", "ja", "zh", "ru", "es", "ar"), codes)
    }

    // ═══════════════════════════════════════════════════════════
    // 2. fromCode() 대소문자 무관 매칭
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `fromCode with lowercase ko returns KO`() {
        assertEquals(SupportedLanguage.KO, SupportedLanguage.fromCode("ko"))
    }

    @Test
    fun `fromCode with uppercase KO returns KO`() {
        assertEquals(SupportedLanguage.KO, SupportedLanguage.fromCode("KO"))
    }

    @Test
    fun `fromCode with mixed case En returns EN`() {
        assertEquals(SupportedLanguage.EN, SupportedLanguage.fromCode("En"))
    }

    @Test
    fun `fromCode with all supported codes`() {
        assertEquals(SupportedLanguage.KO, SupportedLanguage.fromCode("ko"))
        assertEquals(SupportedLanguage.EN, SupportedLanguage.fromCode("en"))
        assertEquals(SupportedLanguage.JA, SupportedLanguage.fromCode("ja"))
        assertEquals(SupportedLanguage.ZH, SupportedLanguage.fromCode("zh"))
        assertEquals(SupportedLanguage.RU, SupportedLanguage.fromCode("ru"))
        assertEquals(SupportedLanguage.ES, SupportedLanguage.fromCode("es"))
        assertEquals(SupportedLanguage.AR, SupportedLanguage.fromCode("ar"))
    }

    // ═══════════════════════════════════════════════════════════
    // 3. fromCode() 미지원 코드 → null
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `fromCode with unsupported language returns null`() {
        assertNull(SupportedLanguage.fromCode("fr"))
        assertNull(SupportedLanguage.fromCode("de"))
        assertNull(SupportedLanguage.fromCode("pt"))
        assertNull(SupportedLanguage.fromCode("xx"))
    }

    @Test
    fun `fromCode with empty string returns null`() {
        assertNull(SupportedLanguage.fromCode(""))
    }

    // ═══════════════════════════════════════════════════════════
    // 4. fromCodeOrDefault() 미지원 코드 → EN
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `fromCodeOrDefault with supported code returns that language`() {
        assertEquals(SupportedLanguage.KO, SupportedLanguage.fromCodeOrDefault("ko"))
        assertEquals(SupportedLanguage.JA, SupportedLanguage.fromCodeOrDefault("ja"))
    }

    @Test
    fun `fromCodeOrDefault with unsupported code returns EN`() {
        assertEquals(SupportedLanguage.EN, SupportedLanguage.fromCodeOrDefault("fr"))
        assertEquals(SupportedLanguage.EN, SupportedLanguage.fromCodeOrDefault("de"))
        assertEquals(SupportedLanguage.EN, SupportedLanguage.fromCodeOrDefault(""))
    }

    // ═══════════════════════════════════════════════════════════
    // 5. 각 언어 속성 무결성
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `KO has correct properties`() {
        val lang = SupportedLanguage.KO
        assertEquals("ko", lang.code)
        assertEquals("한국어", lang.nativeName)
        assertEquals("Korean", lang.englishName)
    }

    @Test
    fun `EN has correct properties`() {
        val lang = SupportedLanguage.EN
        assertEquals("en", lang.code)
        assertEquals("English", lang.nativeName)
        assertEquals("English", lang.englishName)
    }

    @Test
    fun `JA has correct properties`() {
        val lang = SupportedLanguage.JA
        assertEquals("ja", lang.code)
        assertEquals("日本語", lang.nativeName)
        assertEquals("Japanese", lang.englishName)
    }

    @Test
    fun `ZH has correct properties`() {
        val lang = SupportedLanguage.ZH
        assertEquals("zh", lang.code)
        assertEquals("中文", lang.nativeName)
        assertEquals("Chinese", lang.englishName)
    }

    @Test
    fun `RU has correct properties`() {
        val lang = SupportedLanguage.RU
        assertEquals("ru", lang.code)
        assertEquals("Русский", lang.nativeName)
        assertEquals("Russian", lang.englishName)
    }

    @Test
    fun `ES has correct properties`() {
        val lang = SupportedLanguage.ES
        assertEquals("es", lang.code)
        assertEquals("Español", lang.nativeName)
        assertEquals("Spanish", lang.englishName)
    }

    @Test
    fun `AR has correct properties`() {
        val lang = SupportedLanguage.AR
        assertEquals("ar", lang.code)
        assertEquals("العربية", lang.nativeName)
        assertEquals("Arabic", lang.englishName)
    }

    // ═══════════════════════════════════════════════════════════
    // 6. 언어 코드 유일성 보장
    // ═══════════════════════════════════════════════════════════

    @Test
    fun `all language codes are unique`() {
        val codes = SupportedLanguage.entries.map { it.code }
        assertEquals(codes.size, codes.distinct().size)
    }
}
