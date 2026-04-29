package app.myphonecheck.mobile.feature.decisionui.components

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import app.myphonecheck.mobile.core.globalengine.search.SearchInput
import app.myphonecheck.mobile.core.globalengine.search.external.CustomTabExternalSearch
import app.myphonecheck.mobile.core.globalengine.search.external.ExternalMode
import app.myphonecheck.mobile.core.globalengine.search.registry.SimAiSearchRegistry
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 직접 검색 핸들러 (v2.5.0 §direct-search 정합).
 *
 * 책임:
 *  - SimAiSearchRegistry로 SIM 기준 AI 검색 후보군 노출
 *  - 사용자 선택 → Custom Tab launch (헌법 §1 정합, 우리 송신 0)
 *  - 마지막 선택 SharedPreferences 영속화 (현재 SIM 후보 안에서만 default 인정)
 */
@Singleton
class DirectSearchHandler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val externalSearch: CustomTabExternalSearch,
    private val simAiRegistry: SimAiSearchRegistry,
) {

    /** SIM 기준 AI 검색 후보군 (최소 2개 보장). */
    fun getAiCandidates(): List<ExternalMode> = simAiRegistry.getCandidates()

    /** 마지막 선택 mode 조회. SIM 후보 안에 있을 때만 default 인정 (SIM 변경 시 무효). */
    fun getLastSelectedMode(): ExternalMode? {
        val saved = prefs().getString(KEY_LAST_MODE, null)?.let {
            runCatching { ExternalMode.valueOf(it) }.getOrNull()
        }
        return saved?.takeIf { it in getAiCandidates() }
    }

    /** 사용자 선택 → Custom Tab launch + 마지막 선택 영속화. */
    fun launch(action: DirectSearchAction, input: SearchInput) {
        val intent = when (action) {
            is DirectSearchAction.AiSearch -> {
                saveLastMode(action.mode)
                Intent(Intent.ACTION_VIEW, Uri.parse(externalSearch.buildUrl(input, action.mode)))
            }
            DirectSearchAction.GenericFallback -> {
                val plain = aiToPlain(getAiCandidates().firstOrNull() ?: ExternalMode.GOOGLE_AI_MODE)
                Intent(Intent.ACTION_VIEW, Uri.parse(externalSearch.buildUrl(input, plain)))
            }
            DirectSearchAction.Cancel -> return
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    private fun aiToPlain(ai: ExternalMode): ExternalMode = when (ai) {
        ExternalMode.GOOGLE_AI_MODE -> ExternalMode.GOOGLE_PLAIN
        ExternalMode.BING_COPILOT -> ExternalMode.BING_PLAIN
        ExternalMode.NAVER_AI -> ExternalMode.NAVER_PLAIN
        else -> ExternalMode.GOOGLE_PLAIN
    }

    private fun saveLastMode(mode: ExternalMode) {
        prefs().edit().putString(KEY_LAST_MODE, mode.name).apply()
    }

    private fun prefs(): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "direct_search_v250"
        private const val KEY_LAST_MODE = "last_ai_mode"
    }
}
