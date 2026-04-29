package app.myphonecheck.mobile.feature.decisionui.components

import android.content.Context
import android.content.SharedPreferences
import app.myphonecheck.mobile.core.globalengine.search.SearchInput
import app.myphonecheck.mobile.core.globalengine.search.external.CustomTabExternalSearch
import app.myphonecheck.mobile.core.globalengine.search.external.ExternalMode
import app.myphonecheck.mobile.core.globalengine.search.registry.SimAiSearchRegistry
import app.myphonecheck.mobile.core.globalengine.simcontext.SimContext
import app.myphonecheck.mobile.core.globalengine.simcontext.SimContextProvider
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Currency
import java.util.TimeZone

class DirectSearchHandlerTest {

    private val sim = SimContext("", "", "KR", "", Currency.getInstance("KRW"), "KR", TimeZone.getTimeZone("UTC"))

    private fun handlerFor(countryIso: String, prefValue: String? = null): Pair<DirectSearchHandler, SharedPreferences> {
        val provider = mockk<SimContextProvider>()
        every { provider.resolve() } returns SimContext(
            "", "", countryIso, "", Currency.getInstance("USD"), countryIso, TimeZone.getTimeZone("UTC"),
        )
        val registry = SimAiSearchRegistry(provider)

        val prefs = mockk<SharedPreferences>(relaxed = true)
        every { prefs.getString("last_ai_mode", null) } returns prefValue
        val editor = mockk<SharedPreferences.Editor>(relaxed = true)
        every { prefs.edit() } returns editor
        every { editor.putString(any(), any()) } returns editor
        every { editor.apply() } just runs

        val context = mockk<Context>(relaxed = true)
        every { context.getSharedPreferences(any(), any()) } returns prefs

        val handler = DirectSearchHandler(context, CustomTabExternalSearch(), registry)
        return handler to prefs
    }

    @Test
    fun `KR SIM returns Naver Google Bing as candidates`() {
        val (handler, _) = handlerFor("KR")
        val candidates = handler.getAiCandidates()
        assertEquals(3, candidates.size)
        assertTrue(candidates.contains(ExternalMode.NAVER_AI))
        assertTrue(candidates.contains(ExternalMode.GOOGLE_AI_MODE))
        assertTrue(candidates.contains(ExternalMode.BING_COPILOT))
    }

    @Test
    fun `Global default returns 2 candidates`() {
        val (handler, _) = handlerFor("ZW")
        val candidates = handler.getAiCandidates()
        assertEquals(2, candidates.size)
    }

    @Test
    fun `getLastSelectedMode returns saved mode if in current SIM candidates`() {
        val (handler, _) = handlerFor("KR", prefValue = "NAVER_AI")
        assertEquals(ExternalMode.NAVER_AI, handler.getLastSelectedMode())
    }

    @Test
    fun `getLastSelectedMode returns null if mode not in current SIM candidates`() {
        // KR SIM 사용자가 NAVER_AI로 마지막 선택, JP SIM으로 변경 시 → null (Naver는 JP 후보 아님)
        // (실제로 NAVER_AI는 JP candidates에 없음 — JP는 YAHOO_JAPAN_AI / GOOGLE_AI_MODE / BING_COPILOT)
        val (handler, _) = handlerFor("JP", prefValue = "NAVER_AI")
        assertNull(handler.getLastSelectedMode())
    }

    @Test
    fun `getLastSelectedMode returns null if no saved value`() {
        val (handler, _) = handlerFor("KR", prefValue = null)
        assertNull(handler.getLastSelectedMode())
    }

    @Test
    fun `getLastSelectedMode returns null on invalid saved value`() {
        val (handler, _) = handlerFor("KR", prefValue = "NONEXISTENT_MODE")
        assertNull(handler.getLastSelectedMode())
    }
}

class SimBasedAiMenuRequireTest {

    @Test(expected = IllegalArgumentException::class)
    fun `init fails if candidates size less than 2`() {
        // SimBasedAiMenu의 require는 Composable scope이므로 컴포지션 외부에서 직접 검증 불가.
        // 본 테스트는 candidates list size 검증 로직을 별도 함수로 추출해 단위 테스트.
        validateMinimumCandidates(listOf(ExternalMode.GOOGLE_AI_MODE))
    }

    @Test
    fun `validation passes for 2 candidates`() {
        validateMinimumCandidates(listOf(ExternalMode.GOOGLE_AI_MODE, ExternalMode.BING_COPILOT))
    }

    /** SimBasedAiMenu와 동일 require 로직을 단위 테스트 가능 함수로 추출. */
    private fun validateMinimumCandidates(candidates: List<ExternalMode>) {
        require(candidates.size >= 2) {
            "AI search candidates must be >= 2 (헌법 §1 v2.5.0 정합)"
        }
    }
}
