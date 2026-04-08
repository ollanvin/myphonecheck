package app.callcheck.mobile.feature.countryconfig

import android.content.Context
import android.content.res.Configuration
import androidx.test.core.app.ApplicationProvider
import java.util.Locale

internal fun contextForLanguage(lang: SupportedLanguage): Context {
    val app = ApplicationProvider.getApplicationContext<Context>()
    val locale = when (lang) {
        SupportedLanguage.ZH -> Locale.forLanguageTag("zh-CN")
        else -> Locale.forLanguageTag(lang.code)
    }
    val config = Configuration(app.resources.configuration)
    config.setLocale(locale)
    return app.createConfigurationContext(config)
}
