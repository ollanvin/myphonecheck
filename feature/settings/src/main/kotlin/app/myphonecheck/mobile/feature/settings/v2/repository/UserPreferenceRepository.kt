package app.myphonecheck.mobile.feature.settings.v2.repository

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 사용자 선호 영구 저장 (Architecture v2.0.0 §29).
 *
 * DataStore Preferences — UI 언어 선호, 공개 피드 옵트인 출처 ID 집합.
 *
 * 헌법 §3 결정권 중앙집중 금지: 본 저장소는 사용자 선택만 보존, 자동 결정 0.
 */
@Singleton
class UserPreferenceRepository @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val dataStore = context.userPreferencesDataStore

    val uiLanguagePreferenceFlow: Flow<UiLanguagePreference> = dataStore.data.map { prefs ->
        val raw = prefs[KEY_UI_LANGUAGE]
        runCatching { UiLanguagePreference.valueOf(raw ?: "") }
            .getOrDefault(UiLanguagePreference.SIM_BASED)
    }

    suspend fun setUiLanguagePreference(pref: UiLanguagePreference) {
        dataStore.edit { it[KEY_UI_LANGUAGE] = pref.name }
    }

    val publicFeedOptInFlow: Flow<Set<String>> = dataStore.data.map { prefs ->
        prefs[KEY_PUBLIC_FEED_OPT_IN].orEmpty()
    }

    suspend fun setPublicFeedOptIn(sourceId: String, optIn: Boolean) {
        dataStore.edit { prefs ->
            val current = prefs[KEY_PUBLIC_FEED_OPT_IN].orEmpty().toMutableSet()
            if (optIn) current += sourceId else current -= sourceId
            prefs[KEY_PUBLIC_FEED_OPT_IN] = current
        }
    }

    companion object {
        const val DATASTORE_NAME = "user_preferences_v2"
        val KEY_UI_LANGUAGE: Preferences.Key<String> = stringPreferencesKey("ui_language_preference")
        val KEY_PUBLIC_FEED_OPT_IN: Preferences.Key<Set<String>> =
            stringSetPreferencesKey("public_feed_opt_in")
    }
}

private val Context.userPreferencesDataStore by preferencesDataStore(
    name = UserPreferenceRepository.DATASTORE_NAME,
)
