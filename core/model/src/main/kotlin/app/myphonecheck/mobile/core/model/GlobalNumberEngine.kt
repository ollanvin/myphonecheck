package app.myphonecheck.mobile.core.model

data class NumberParseInput(
    val rawNumber: String,
    val defaultCountryCode: String? = null,
    val sourceContext: NumberSourceContext = NumberSourceContext.INCOMING_CALL,
    val devicePatternProfile: DevicePatternProfile? = null,
)

enum class NumberResolutionStage {
    STATIC_GLOBAL,
    DEVICE_PROFILE,
    FALLBACK_DIGITS,
}

data class NumberCluster(
    val canonicalKey: String,
    val canonicalNumber: String,
    val alternateForms: List<String>,
    val resolvedCountryCode: String?,
)

data class NumberParseResult(
    val rawNumber: String,
    val canonicalNumber: String,
    val canonicalKey: String,
    val normalizedDigits: String,
    val resolvedCountryCode: String?,
    val nationalFormat: String?,
    val internationalFormat: String?,
    val searchVariants: List<String>,
    val isValid: Boolean,
    val isAmbiguous: Boolean,
    val resolutionStage: NumberResolutionStage,
    val inferredFromDeviceProfile: Boolean,
    val candidateCountryCodes: List<String> = emptyList(),
    val numberCluster: NumberCluster,
)

data class DevicePatternProfile(
    val primaryCountryCode: String?,
    val preferredCountryCodes: List<String> = emptyList(),
    val commonPrefixes: List<String> = emptyList(),
    val dominantNumberLengths: List<Int> = emptyList(),
    val usesInternationalPrefix: Boolean = false,
    val usesNationalTrunkPrefix: Boolean = false,
    val usesSeparators: Boolean = false,
    val sampleSize: Int = 0,
    val activeSources: List<String> = emptyList(),
    val fallbackReasons: List<String> = emptyList(),
    val lastScannedAt: Long? = null,
)

data class DeviceNumberScanSnapshot(
    val callHistoryNumbers: List<String> = emptyList(),
    val smsSenderNumbers: List<String> = emptyList(),
    val contactNumbers: List<String> = emptyList(),
    val defaultCountryCode: String? = null,
)

interface GlobalNumberEngine {
    fun parse(input: NumberParseInput): NumberParseResult

    fun resolve(input: NumberParseInput): NumberParseResult

    fun canonicalKey(
        rawNumber: String,
        defaultCountryCode: String? = null,
        devicePatternProfile: DevicePatternProfile? = null,
    ): String
}

interface DevicePatternProfileScanner {
    suspend fun scan(snapshot: DeviceNumberScanSnapshot): DevicePatternProfile
}

interface DeviceNumberScanSource {
    suspend fun recentCallHistoryNumbers(limit: Int = 200): List<String>

    suspend fun recentSmsSenderNumbers(limit: Int = 200): List<String>

    suspend fun contactNumbers(limit: Int = 500): List<String>
}

object StubDeviceNumberScanSource : DeviceNumberScanSource {
    override suspend fun recentCallHistoryNumbers(limit: Int): List<String> = emptyList()

    override suspend fun recentSmsSenderNumbers(limit: Int): List<String> = emptyList()

    override suspend fun contactNumbers(limit: Int): List<String> = emptyList()
}
