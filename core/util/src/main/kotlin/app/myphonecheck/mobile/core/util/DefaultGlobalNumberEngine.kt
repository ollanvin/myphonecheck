package app.myphonecheck.mobile.core.util

import app.myphonecheck.mobile.core.model.DeviceNumberScanSnapshot
import app.myphonecheck.mobile.core.model.DevicePatternProfile
import app.myphonecheck.mobile.core.model.DevicePatternProfileScanner
import app.myphonecheck.mobile.core.model.GlobalNumberEngine
import app.myphonecheck.mobile.core.model.NumberCluster
import app.myphonecheck.mobile.core.model.NumberParseInput
import app.myphonecheck.mobile.core.model.NumberParseResult
import app.myphonecheck.mobile.core.model.NumberResolutionStage
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber

class DefaultGlobalNumberEngine(
    private val phoneUtil: PhoneNumberUtil = PhoneNumberUtil.getInstance(),
) : GlobalNumberEngine {

    override fun parse(input: NumberParseInput): NumberParseResult {
        val rawNumber = input.rawNumber.trim()
        if (rawNumber.isEmpty()) {
            return fallbackResult(rawNumber, input, isAmbiguous = false)
        }

        val directRegion = normalizeRegion(input.defaultCountryCode)
        val directCandidate = directRegion?.let { tryParse(rawNumber, it) }
            ?: if (rawNumber.startsWith("+")) tryParse(rawNumber, UNKNOWN_REGION) else null
        if (directCandidate != null) {
            return buildResolvedResult(
                rawNumber = rawNumber,
                candidate = directCandidate,
                stage = NumberResolutionStage.STATIC_GLOBAL,
                inferredFromDeviceProfile = false,
                candidateCountryCodes = listOfNotNull(directCandidate.regionCode),
            )
        }

        val candidates = findStaticCandidates(rawNumber, directRegion)
        return when (candidates.size) {
            1 -> buildResolvedResult(
                rawNumber = rawNumber,
                candidate = candidates.first(),
                stage = NumberResolutionStage.STATIC_GLOBAL,
                inferredFromDeviceProfile = false,
                candidateCountryCodes = candidates.mapNotNull { it.regionCode },
            )
            else -> fallbackResult(
                rawNumber = rawNumber,
                input = input,
                isAmbiguous = candidates.isNotEmpty() || shouldTreatAsAmbiguous(rawNumber),
                candidateCountryCodes = candidates.mapNotNull { it.regionCode },
            )
        }
    }

    override fun resolve(input: NumberParseInput): NumberParseResult {
        val staticResult = parse(input)
        if (staticResult.isValid || !staticResult.isAmbiguous) {
            return staticResult
        }

        val profile = input.devicePatternProfile ?: return staticResult
        val candidateCountries = buildAdaptiveCountryOrder(
            profile = profile,
            staticCandidateCountryCodes = staticResult.candidateCountryCodes,
            defaultCountryCode = input.defaultCountryCode,
        )

        for (countryCode in candidateCountries) {
            val candidate = tryParse(input.rawNumber.trim(), countryCode) ?: continue
            return buildResolvedResult(
                rawNumber = input.rawNumber.trim(),
                candidate = candidate,
                stage = NumberResolutionStage.DEVICE_PROFILE,
                inferredFromDeviceProfile = true,
                candidateCountryCodes = candidateCountries,
            )
        }

        return staticResult
    }

    override fun canonicalKey(
        rawNumber: String,
        defaultCountryCode: String?,
        devicePatternProfile: DevicePatternProfile?,
    ): String {
        return resolve(
            NumberParseInput(
                rawNumber = rawNumber,
                defaultCountryCode = defaultCountryCode,
                devicePatternProfile = devicePatternProfile,
            ),
        ).canonicalKey
    }

    private fun findStaticCandidates(
        rawNumber: String,
        directRegion: String?,
    ): List<ParsedCandidate> {
        val candidateRegions = buildList {
            if (rawNumber.startsWith("+")) {
                add(UNKNOWN_REGION)
            } else {
                directRegion?.let(::add)
                addAll(phoneUtil.supportedRegions.sorted())
            }
        }

        return candidateRegions
            .asSequence()
            .mapNotNull { region ->
                tryParse(rawNumber, region)
            }
            .distinctBy { it.e164 }
            .toList()
    }

    private fun buildAdaptiveCountryOrder(
        profile: DevicePatternProfile,
        staticCandidateCountryCodes: List<String>,
        defaultCountryCode: String?,
    ): List<String> {
        return buildList {
            normalizeRegion(defaultCountryCode)?.let(::add)
            profile.primaryCountryCode?.let(::add)
            addAll(profile.preferredCountryCodes.mapNotNull(::normalizeRegion))
            addAll(staticCandidateCountryCodes.mapNotNull(::normalizeRegion))
        }.distinct()
    }

    private fun buildResolvedResult(
        rawNumber: String,
        candidate: ParsedCandidate,
        stage: NumberResolutionStage,
        inferredFromDeviceProfile: Boolean,
        candidateCountryCodes: List<String>,
    ): NumberParseResult {
        val searchVariants = buildSearchVariants(rawNumber, candidate)
        val canonicalKey = canonicalKeyFor(candidate.e164)
        val cluster = NumberCluster(
            canonicalKey = canonicalKey,
            canonicalNumber = candidate.e164,
            alternateForms = searchVariants,
            resolvedCountryCode = candidate.regionCode,
        )
        return NumberParseResult(
            rawNumber = rawNumber,
            canonicalNumber = candidate.e164,
            canonicalKey = canonicalKey,
            normalizedDigits = digitsOnly(candidate.e164),
            resolvedCountryCode = candidate.regionCode,
            nationalFormat = candidate.nationalFormat,
            internationalFormat = candidate.internationalFormat,
            searchVariants = searchVariants,
            isValid = true,
            isAmbiguous = false,
            resolutionStage = stage,
            inferredFromDeviceProfile = inferredFromDeviceProfile,
            candidateCountryCodes = candidateCountryCodes,
            numberCluster = cluster,
        )
    }

    private fun fallbackResult(
        rawNumber: String,
        input: NumberParseInput,
        isAmbiguous: Boolean,
        candidateCountryCodes: List<String> = emptyList(),
    ): NumberParseResult {
        val digits = digitsOnly(rawNumber)
        val canonicalNumber = if (digits.isNotEmpty()) digits else rawNumber
        val canonicalKey = canonicalKeyFor(canonicalNumber)
        val searchVariants = listOf(rawNumber, digits, canonicalNumber)
            .filter { it.isNotBlank() }
            .distinct()
        val cluster = NumberCluster(
            canonicalKey = canonicalKey,
            canonicalNumber = canonicalNumber,
            alternateForms = searchVariants,
            resolvedCountryCode = input.devicePatternProfile?.primaryCountryCode
                ?: normalizeRegion(input.defaultCountryCode),
        )
        return NumberParseResult(
            rawNumber = rawNumber,
            canonicalNumber = canonicalNumber,
            canonicalKey = canonicalKey,
            normalizedDigits = digits,
            resolvedCountryCode = cluster.resolvedCountryCode,
            nationalFormat = null,
            internationalFormat = null,
            searchVariants = searchVariants,
            isValid = false,
            isAmbiguous = isAmbiguous,
            resolutionStage = NumberResolutionStage.FALLBACK_DIGITS,
            inferredFromDeviceProfile = false,
            candidateCountryCodes = candidateCountryCodes,
            numberCluster = cluster,
        )
    }

    private fun buildSearchVariants(
        rawNumber: String,
        candidate: ParsedCandidate,
    ): List<String> {
        return listOf(
            rawNumber,
            digitsOnly(rawNumber),
            candidate.nationalFormat,
            candidate.internationalFormat,
            candidate.e164,
            digitsOnly(candidate.e164),
            candidate.parsed.nationalNumber.toString(),
        ).filter { it.isNotBlank() }.distinct()
    }

    private fun tryParse(
        rawNumber: String,
        regionCode: String,
    ): ParsedCandidate? {
        return try {
            val parsed = phoneUtil.parse(rawNumber, regionCode)
            if (!phoneUtil.isValidNumber(parsed)) {
                return null
            }
            parsedCandidate(parsed)
        } catch (_: NumberParseException) {
            val cleaned = preservePlusDigits(rawNumber)
            if (cleaned == rawNumber) {
                null
            } else {
                try {
                    val parsed = phoneUtil.parse(cleaned, regionCode)
                    if (!phoneUtil.isValidNumber(parsed)) {
                        null
                    } else {
                        parsedCandidate(parsed)
                    }
                } catch (_: Exception) {
                    null
                }
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun parsedCandidate(parsed: Phonenumber.PhoneNumber): ParsedCandidate {
        val e164 = phoneUtil.format(parsed, PhoneNumberUtil.PhoneNumberFormat.E164)
        val regionCode = phoneUtil.getRegionCodeForNumber(parsed)
            ?.takeUnless { it == UNKNOWN_REGION || it == GLOBAL_REGION }
        return ParsedCandidate(
            parsed = parsed,
            e164 = e164,
            nationalFormat = phoneUtil.format(parsed, PhoneNumberUtil.PhoneNumberFormat.NATIONAL),
            internationalFormat = phoneUtil.format(parsed, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL),
            regionCode = regionCode,
        )
    }

    private fun shouldTreatAsAmbiguous(rawNumber: String): Boolean {
        val digits = digitsOnly(rawNumber)
        return !rawNumber.startsWith("+") && digits.length in 7..15
    }

    private fun preservePlusDigits(rawNumber: String): String {
        val hasPlus = rawNumber.trim().startsWith("+")
        val digits = digitsOnly(rawNumber)
        return if (hasPlus && digits.isNotEmpty()) "+$digits" else digits
    }

    private fun digitsOnly(rawNumber: String): String {
        return rawNumber.replace(Regex("[^\\d]"), "")
    }

    private fun canonicalKeyFor(canonicalNumber: String): String {
        return if (canonicalNumber.startsWith("+")) {
            "e164:$canonicalNumber"
        } else {
            "digits:${digitsOnly(canonicalNumber)}"
        }
    }

    private fun normalizeRegion(countryCode: String?): String? {
        val region = countryCode?.trim()?.uppercase().orEmpty()
        if (region.isBlank() || region == UNKNOWN_REGION) {
            return null
        }
        return region
    }

    private data class ParsedCandidate(
        val parsed: Phonenumber.PhoneNumber,
        val e164: String,
        val nationalFormat: String,
        val internationalFormat: String,
        val regionCode: String?,
    )

    private companion object {
        const val UNKNOWN_REGION = "ZZ"
        const val GLOBAL_REGION = "001"
    }
}

class DefaultDevicePatternProfileScanner(
    private val numberEngine: GlobalNumberEngine = DefaultGlobalNumberEngine(),
    private val phoneUtil: PhoneNumberUtil = PhoneNumberUtil.getInstance(),
) : DevicePatternProfileScanner {

    override suspend fun scan(snapshot: DeviceNumberScanSnapshot): DevicePatternProfile {
        val observedNumbers = snapshot.callHistoryNumbers +
            snapshot.smsSenderNumbers +
            snapshot.contactNumbers
        if (observedNumbers.isEmpty()) {
            return DevicePatternProfile(primaryCountryCode = snapshot.defaultCountryCode)
        }

        val resolvedResults = observedNumbers.map { raw ->
            numberEngine.resolve(
                NumberParseInput(
                    rawNumber = raw,
                    defaultCountryCode = snapshot.defaultCountryCode,
                ),
            )
        }

        val regionCounts = resolvedResults
            .mapNotNull { it.resolvedCountryCode }
            .groupingBy { it }
            .eachCount()
            .entries
            .sortedByDescending { it.value }

        val canonicalNumbers = resolvedResults.map { it.canonicalNumber }
        val dominantLengths = canonicalNumbers
            .map { it.replace(Regex("[^\\d]"), "").length }
            .filter { it > 0 }
            .groupingBy { it }
            .eachCount()
            .entries
            .sortedByDescending { it.value }
            .map { it.key }
            .take(5)

        val commonPrefixes = regionCounts
            .mapNotNull { entry ->
                val code = phoneUtil.getCountryCodeForRegion(entry.key)
                if (code > 0) "+$code" else null
            }
            .distinct()
            .take(5)

        return DevicePatternProfile(
            primaryCountryCode = regionCounts.firstOrNull()?.key ?: snapshot.defaultCountryCode,
            preferredCountryCodes = regionCounts.map { it.key },
            commonPrefixes = commonPrefixes,
            dominantNumberLengths = dominantLengths,
            usesInternationalPrefix = observedNumbers.count { it.trim().startsWith("+") || it.trim().startsWith("00") } * 2 >= observedNumbers.size,
            usesNationalTrunkPrefix = observedNumbers.count {
                val trimmed = it.trim()
                trimmed.startsWith("0") && !trimmed.startsWith("00") && !trimmed.startsWith("+")
            } * 2 >= observedNumbers.size,
            usesSeparators = observedNumbers.count { it.any { ch -> ch == ' ' || ch == '-' || ch == '(' || ch == ')' } } * 2 >= observedNumbers.size,
            sampleSize = observedNumbers.size,
        )
    }
}
