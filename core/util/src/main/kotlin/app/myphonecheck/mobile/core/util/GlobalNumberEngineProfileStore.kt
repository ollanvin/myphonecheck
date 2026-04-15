package app.myphonecheck.mobile.core.util

import android.content.Context
import app.myphonecheck.mobile.core.model.DevicePatternProfile
import org.json.JSONArray
import org.json.JSONObject

object GlobalNumberEngineProfileStore {
    private const val PREFS_NAME = "global_number_engine_profile"
    private const val KEY_PROFILE_JSON = "device_pattern_profile_json"
    private const val KEY_PENDING_REFRESH_REASONS = "pending_refresh_reasons"
    private const val PROFILE_STALE_MS = 7L * 24L * 60L * 60L * 1000L

    @Volatile
    private var currentProfile: DevicePatternProfile? = null

    @Volatile
    private var appContext: Context? = null

    @Volatile
    private var pendingRefreshReasons: List<String> = emptyList()

    fun initialize(context: Context) {
        appContext = context.applicationContext
        restoreIfNeeded()
        evaluateRefreshPolicy()
    }

    fun update(profile: DevicePatternProfile) {
        currentProfile = profile
        pendingRefreshReasons = emptyList()
        persist(profile)
        persistRefreshReasons(emptyList())
    }

    fun current(): DevicePatternProfile? {
        restoreIfNeeded()
        return currentProfile
    }

    fun needsRefresh(): Boolean {
        restoreIfNeeded()
        return pendingRefreshReasons.isNotEmpty()
    }

    fun refreshReasons(): List<String> {
        restoreIfNeeded()
        return pendingRefreshReasons
    }

    fun requestManualRefresh(reason: String = "manual_refresh") {
        val reasons = (pendingRefreshReasons + reason).distinct()
        pendingRefreshReasons = reasons
        persistRefreshReasons(reasons)
    }

    private fun restoreIfNeeded() {
        val context = appContext ?: return
        if (currentProfile == null) {
            val raw = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString(KEY_PROFILE_JSON, null)
            currentProfile = raw?.let(::decode)
        }
        if (pendingRefreshReasons.isEmpty()) {
            val rawReasons = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString(KEY_PENDING_REFRESH_REASONS, null)
            pendingRefreshReasons = decodeStringList(rawReasons)
        }
    }

    private fun evaluateRefreshPolicy() {
        val context = appContext ?: return
        val profile = currentProfile
        val source = AndroidDeviceNumberScanSource(context)
        val reasons = buildList {
            if (profile == null) {
                add("missing_profile")
            } else {
                val scannedAt = profile.lastScannedAt ?: 0L
                if (scannedAt <= 0L || System.currentTimeMillis() - scannedAt >= PROFILE_STALE_MS) {
                    add("startup_age_threshold")
                }
                val expectedCallLog = profile.activeSources.contains("call_log")
                val expectedContacts = profile.activeSources.contains("contacts")
                val expectedSms = profile.activeSources.contains("sms")
                if (expectedCallLog != source.hasCallLogAccess()) add("permission_change:READ_CALL_LOG")
                if (expectedContacts != source.hasContactsAccess()) add("permission_change:READ_CONTACTS")
                if (expectedSms != source.hasSmsAccess()) add("permission_change:READ_SMS")
            }
            addAll(pendingRefreshReasons)
        }.distinct()
        pendingRefreshReasons = reasons
        persistRefreshReasons(reasons)
    }

    private fun persist(profile: DevicePatternProfile) {
        val context = appContext ?: return
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_PROFILE_JSON, encode(profile).toString())
            .apply()
    }

    private fun persistRefreshReasons(reasons: List<String>) {
        val context = appContext ?: return
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_PENDING_REFRESH_REASONS, JSONArray(reasons).toString())
            .apply()
    }

    private fun encode(profile: DevicePatternProfile): JSONObject {
        return JSONObject().apply {
            put("primaryCountryCode", profile.primaryCountryCode)
            put("preferredCountryCodes", JSONArray(profile.preferredCountryCodes))
            put("commonPrefixes", JSONArray(profile.commonPrefixes))
            put("dominantNumberLengths", JSONArray(profile.dominantNumberLengths))
            put("usesInternationalPrefix", profile.usesInternationalPrefix)
            put("usesNationalTrunkPrefix", profile.usesNationalTrunkPrefix)
            put("usesSeparators", profile.usesSeparators)
            put("sampleSize", profile.sampleSize)
            put("activeSources", JSONArray(profile.activeSources))
            put("fallbackReasons", JSONArray(profile.fallbackReasons))
            put("lastScannedAt", profile.lastScannedAt ?: JSONObject.NULL)
        }
    }

    private fun decode(raw: String): DevicePatternProfile? {
        return try {
            val json = JSONObject(raw)
            DevicePatternProfile(
                primaryCountryCode = json.optString("primaryCountryCode").ifBlank { null },
                preferredCountryCodes = json.optJSONArray("preferredCountryCodes").toStringList(),
                commonPrefixes = json.optJSONArray("commonPrefixes").toStringList(),
                dominantNumberLengths = json.optJSONArray("dominantNumberLengths").toIntList(),
                usesInternationalPrefix = json.optBoolean("usesInternationalPrefix"),
                usesNationalTrunkPrefix = json.optBoolean("usesNationalTrunkPrefix"),
                usesSeparators = json.optBoolean("usesSeparators"),
                sampleSize = json.optInt("sampleSize"),
                activeSources = json.optJSONArray("activeSources").toStringList(),
                fallbackReasons = json.optJSONArray("fallbackReasons").toStringList(),
                lastScannedAt = if (json.isNull("lastScannedAt")) null else json.optLong("lastScannedAt"),
            )
        } catch (_: Exception) {
            null
        }
    }

    private fun decodeStringList(raw: String?): List<String> {
        if (raw.isNullOrBlank()) return emptyList()
        return try {
            val array = JSONArray(raw)
            buildList {
                for (i in 0 until array.length()) {
                    val value = array.optString(i)
                    if (value.isNotBlank()) add(value)
                }
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun JSONArray?.toStringList(): List<String> {
        if (this == null) return emptyList()
        return buildList {
            for (i in 0 until length()) {
                val value = optString(i)
                if (value.isNotBlank()) add(value)
            }
        }
    }

    private fun JSONArray?.toIntList(): List<Int> {
        if (this == null) return emptyList()
        return buildList {
            for (i in 0 until length()) {
                add(optInt(i))
            }
        }
    }
}
