package app.myphonecheck.mobile.core.globalengine.simcontext

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

/**
 * UI 언어 3단 fallback 검증 (Architecture v2.0.0 §29-3 + 헌법 §8-2).
 *
 * 시나리오:
 *  1순위 (default): SIM 기반 언어 (예: KR → ko)
 *  2순위: 디바이스 시스템 언어 (Locale.getDefault())
 *  3순위: English (만국 공통)
 */
class UiLanguageResolverTest {

    @Test
    fun `SIM_BASED KR → ko_KR`() {
        val ctx = SimContext.fallback("KR")
        val resolver = UiLanguageResolver(ctx, UiLanguagePreference.SIM_BASED)
        val locale = resolver.resolveLocale()
        assertEquals("ko", locale.language)
        assertEquals("KR", locale.country)
    }

    @Test
    fun `SIM_BASED JP → ja_JP`() {
        val ctx = SimContext.fallback("JP")
        val resolver = UiLanguageResolver(ctx, UiLanguagePreference.SIM_BASED)
        val locale = resolver.resolveLocale()
        assertEquals("ja", locale.language)
    }

    @Test
    fun `SIM_BASED US → en_US`() {
        val ctx = SimContext.fallback("US")
        val resolver = UiLanguageResolver(ctx, UiLanguagePreference.SIM_BASED)
        val locale = resolver.resolveLocale()
        assertEquals("en", locale.language)
    }

    @Test
    fun `SIM_BASED 매핑 안 된 country → country 그대로`() {
        val ctx = SimContext.fallback("XX")
        val resolver = UiLanguageResolver(ctx, UiLanguagePreference.SIM_BASED)
        val locale = resolver.resolveLocale()
        // 매핑 안 된 country는 country code만 (시스템 fallback)
        assertEquals("XX", locale.country)
    }

    @Test
    fun `DEVICE_SYSTEM → 시스템 default Locale`() {
        val ctx = SimContext.fallback("KR")
        val resolver = UiLanguageResolver(ctx, UiLanguagePreference.DEVICE_SYSTEM)
        val locale = resolver.resolveLocale()
        assertEquals(Locale.getDefault(), locale)
    }

    @Test
    fun `ENGLISH → Locale_ENGLISH`() {
        val ctx = SimContext.fallback("KR")
        val resolver = UiLanguageResolver(ctx, UiLanguagePreference.ENGLISH)
        val locale = resolver.resolveLocale()
        assertEquals(Locale.ENGLISH, locale)
    }

    @Test
    fun `한국인 해외 SIM (US) - DEVICE_SYSTEM 선택 시나리오`() {
        // 한국인이 미국 출장 중 US SIM 사용. DEVICE_SYSTEM (Locale.getDefault) 선택.
        // 실제 시나리오: 디바이스 시스템 Locale은 ko로 유지된 상태.
        val ctx = SimContext.fallback("US")
        val resolver = UiLanguageResolver(ctx, UiLanguagePreference.DEVICE_SYSTEM)
        val locale = resolver.resolveLocale()
        // 본 테스트는 시스템 default 그대로 — 실제 사용자 시나리오는 시스템에 따라 다름.
        assertEquals(Locale.getDefault(), locale)
    }

    @Test
    fun `다국적 사용자 - ENGLISH 선택 시나리오`() {
        // 사용자가 SIM·시스템 무관하게 English 선택.
        val ctx = SimContext.fallback("DE")
        val resolver = UiLanguageResolver(ctx, UiLanguagePreference.ENGLISH)
        val locale = resolver.resolveLocale()
        assertEquals(Locale.ENGLISH, locale)
    }
}
