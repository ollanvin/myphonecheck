package app.myphonecheck.mobile.feature.countryconfig

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * 언어 해상도 로직 테스트 — EN 단일 정책 (헌법 §9-1).
 *
 * 헌법 §9-1: SupportedLanguage 는 EN 단일.
 * Priority chain / App Setting 오버라이드 / OS Locale 분기 영구 폐기.
 * fromCodeOrDefault 는 항상 EN 을 반환한다 (다국어 표시는 OS Locale + ICU 가 처리).
 */
class LanguageResolutionLogicTest {

    @Test
    fun `fromCodeOrDefault with en returns EN`() {
        assertEquals(SupportedLanguage.EN, SupportedLanguage.fromCodeOrDefault("en"))
    }

    @Test
    fun `fromCodeOrDefault with ko falls back to EN`() {
        assertEquals(SupportedLanguage.EN, SupportedLanguage.fromCodeOrDefault("ko"))
    }

    @Test
    fun `fromCodeOrDefault with ja falls back to EN`() {
        assertEquals(SupportedLanguage.EN, SupportedLanguage.fromCodeOrDefault("ja"))
    }

    @Test
    fun `fromCodeOrDefault with zh falls back to EN`() {
        assertEquals(SupportedLanguage.EN, SupportedLanguage.fromCodeOrDefault("zh"))
    }

    @Test
    fun `fromCodeOrDefault with ru falls back to EN`() {
        assertEquals(SupportedLanguage.EN, SupportedLanguage.fromCodeOrDefault("ru"))
    }

    @Test
    fun `fromCodeOrDefault with es falls back to EN`() {
        assertEquals(SupportedLanguage.EN, SupportedLanguage.fromCodeOrDefault("es"))
    }

    @Test
    fun `fromCodeOrDefault with ar falls back to EN`() {
        assertEquals(SupportedLanguage.EN, SupportedLanguage.fromCodeOrDefault("ar"))
    }

    @Test
    fun `fromCodeOrDefault with unsupported language falls back to EN`() {
        assertEquals(SupportedLanguage.EN, SupportedLanguage.fromCodeOrDefault("fr"))
        assertEquals(SupportedLanguage.EN, SupportedLanguage.fromCodeOrDefault("de"))
        assertEquals(SupportedLanguage.EN, SupportedLanguage.fromCodeOrDefault("pt"))
    }

    @Test
    fun `fromCodeOrDefault with empty string returns EN`() {
        assertEquals(SupportedLanguage.EN, SupportedLanguage.fromCodeOrDefault(""))
    }

    @Test
    fun `entries iteration produces only EN`() {
        for (lang in SupportedLanguage.entries) {
            assertEquals(SupportedLanguage.EN, lang)
        }
    }
}
