package app.myphonecheck.mobile.feature.settings

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface SettingsRepository {
    val settings: Flow<AppSettings>

    suspend fun updateLanguage(language: String)
    suspend fun updateCountryOverride(country: String?)
    suspend fun updateEvidenceDisplayLevel(level: String)
}

class SettingsRepositoryImpl(
    context: Context,
) : SettingsRepository {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    override val settings: Flow<AppSettings> = _settings.asStateFlow()

    override suspend fun updateLanguage(language: String) {
        sharedPreferences.edit().putString(KEY_LANGUAGE, language).apply()
        _settings.update { it.copy(language = language) }
    }

    override suspend fun updateCountryOverride(country: String?) {
        if (country == null) {
            sharedPreferences.edit().remove(KEY_COUNTRY_OVERRIDE).apply()
        } else {
            sharedPreferences.edit().putString(KEY_COUNTRY_OVERRIDE, country).apply()
        }
        _settings.update { it.copy(countryOverride = country) }
    }

    override suspend fun updateEvidenceDisplayLevel(level: String) {
        sharedPreferences.edit().putString(KEY_EVIDENCE_DISPLAY_LEVEL, level).apply()
        _settings.update { it.copy(evidenceDisplayLevel = level) }
    }

    private fun loadSettings(): AppSettings {
        return AppSettings(
            language = sharedPreferences.getString(KEY_LANGUAGE, "auto") ?: "auto",
            countryOverride = sharedPreferences.getString(KEY_COUNTRY_OVERRIDE, null),
            evidenceDisplayLevel = sharedPreferences.getString(KEY_EVIDENCE_DISPLAY_LEVEL, "normal") ?: "normal",
        )
    }

    companion object {
        private const val PREFERENCES_NAME = "myphonecheck_settings"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_COUNTRY_OVERRIDE = "country_override"
        private const val KEY_EVIDENCE_DISPLAY_LEVEL = "evidence_display_level"
    }
}
