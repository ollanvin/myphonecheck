package app.callcheck.mobile.feature.countryconfig

import android.content.Context

interface CountryConfigProvider {
    fun getConfig(countryCode: String): CountryConfig
    fun getDefaultConfig(): CountryConfig
    fun detectCountry(context: Context): String
}
