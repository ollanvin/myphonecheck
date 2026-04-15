package app.myphonecheck.mobile.core.util

import app.myphonecheck.mobile.core.model.NumberParseInput
import app.myphonecheck.mobile.core.model.NumberSourceContext
import app.myphonecheck.mobile.core.model.PhoneNumberContext

class PhoneNumberContextBuilder(
    private val globalNumberEngine: DefaultGlobalNumberEngine = DefaultGlobalNumberEngine(),
) {

    fun build(
        rawNumber: String,
        deviceCountryCode: String?,
        sourceContext: NumberSourceContext,
    ): PhoneNumberContext {
        val trimmedRaw = rawNumber.trim()
        if (trimmedRaw.isEmpty()) {
            return PhoneNumberContext(
                rawNumber = trimmedRaw,
                deviceCanonicalNumber = "",
                searchVariants = emptyList(),
                deviceCountryCode = deviceCountryCode,
                sourceContext = sourceContext,
                isParseable = false,
            )
        }

        val result = globalNumberEngine.resolve(
            NumberParseInput(
                rawNumber = trimmedRaw,
                defaultCountryCode = deviceCountryCode,
                sourceContext = sourceContext,
                devicePatternProfile = GlobalNumberEngineProfileStore.current(),
            ),
        )

        return PhoneNumberContext(
            rawNumber = trimmedRaw,
            deviceCanonicalNumber = result.canonicalNumber,
            searchVariants = result.searchVariants,
            deviceCountryCode = deviceCountryCode,
            sourceContext = sourceContext,
            isParseable = result.isValid,
        )
    }
}
