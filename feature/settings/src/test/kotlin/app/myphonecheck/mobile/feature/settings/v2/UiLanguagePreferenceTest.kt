package app.myphonecheck.mobile.feature.settings.v2

import app.myphonecheck.mobile.feature.settings.v2.repository.UiLanguagePreference
import org.junit.Assert.assertEquals
import org.junit.Test

class UiLanguagePreferenceTest {

    @Test
    fun `enum has three values matching three-tier fallback`() {
        assertEquals(3, UiLanguagePreference.values().size)
    }

    @Test
    fun `valueOf round trip preserves value`() {
        UiLanguagePreference.values().forEach {
            assertEquals(it, UiLanguagePreference.valueOf(it.name))
        }
    }

    @Test
    fun `SIM_BASED is first option (1순위 fallback)`() {
        assertEquals(UiLanguagePreference.SIM_BASED, UiLanguagePreference.values()[0])
    }

    @Test
    fun `ENGLISH is final fallback option`() {
        assertEquals(UiLanguagePreference.ENGLISH, UiLanguagePreference.values().last())
    }
}
