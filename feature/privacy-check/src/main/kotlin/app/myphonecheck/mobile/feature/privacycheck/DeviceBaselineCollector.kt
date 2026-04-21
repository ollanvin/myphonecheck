package app.myphonecheck.mobile.feature.privacycheck

import android.content.Context
import android.telephony.TelephonyManager
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 디바이스 온보드 컨텍스트 baseline 수집.
 *
 * 원글로벌코어에 주입 가능한 디바이스 현지화 baseline 제공.
 * 하드코딩 국가값 금지 — 실제 디바이스 상태에서만 수집.
 *
 * 수집 항목:
 * 1) country: SIM → Network → Locale 우선순위
 * 2) language: System language
 * 3) timezone: System timezone (IANA)
 * 4) numberRegion: country와 동일 (PhoneNumberNormalizer 기본 region)
 */
@Singleton
class DeviceBaselineCollector @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    /**
     * 디바이스 baseline을 수집합니다.
     *
     * @return DeviceBaseline — 모든 필드가 non-null (fallback 포함)
     */
    fun collect(): DeviceBaseline {
        val country = detectCountry()
        val language = detectLanguage()
        val timezone = detectTimezone()
        val numberRegion = country // PhoneNumberNormalizer와 동일 region
        val generatedAt = System.currentTimeMillis()

        Log.i(
            TAG,
            "BASELINE_COLLECTED" +
                " country=$country" +
                " language=$language" +
                " timezone=$timezone" +
                " numberRegion=$numberRegion",
        )

        return DeviceBaseline(
            country = country,
            language = language,
            timezone = timezone,
            numberRegion = numberRegion,
            generatedAt = generatedAt,
        )
    }

    /**
     * 국가 감지 — 우선순위:
     * 1. SIM country (TelephonyManager.simCountryIso)
     * 2. Network country (TelephonyManager.networkCountryIso)
     * 3. Locale country (Locale.getDefault().country)
     *
     * 모든 소스 실패 시 빈 문자열 → guard fail로 이어짐.
     * 하드코딩 fallback 없음.
     */
    private fun detectCountry(): String {
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager

        // 1. SIM country
        val simCountry = tm?.simCountryIso?.uppercase()?.takeIf { it.length == 2 }
        if (!simCountry.isNullOrBlank()) {
            Log.d(TAG, "COUNTRY_SOURCE=SIM value=$simCountry")
            return simCountry
        }

        // 2. Network country
        val networkCountry = tm?.networkCountryIso?.uppercase()?.takeIf { it.length == 2 }
        if (!networkCountry.isNullOrBlank()) {
            Log.d(TAG, "COUNTRY_SOURCE=NETWORK value=$networkCountry")
            return networkCountry
        }

        // 3. Locale country
        val localeCountry = Locale.getDefault().country.uppercase().takeIf { it.length == 2 }
        if (!localeCountry.isNullOrBlank()) {
            Log.d(TAG, "COUNTRY_SOURCE=LOCALE value=$localeCountry")
            return localeCountry
        }

        Log.w(TAG, "COUNTRY_SOURCE=NONE — all sources empty")
        return ""
    }

    /**
     * 언어 감지 — System default locale language.
     */
    private fun detectLanguage(): String {
        val lang = Locale.getDefault().language.lowercase()
        Log.d(TAG, "LANGUAGE=$lang")
        return lang
    }

    /**
     * 타임존 감지 — System default timezone (IANA ID).
     */
    private fun detectTimezone(): String {
        val tz = TimeZone.getDefault().id
        Log.d(TAG, "TIMEZONE=$tz")
        return tz
    }

    private companion object {
        const val TAG = "MPC_DEVICE_BASELINE"
    }
}

/**
 * 디바이스 온보드 컨텍스트 baseline.
 *
 * 원글로벌코어 주입용 — 모든 필드 non-null.
 */
data class DeviceBaseline(
    /** ISO 3166-1 alpha-2 (예: "KR", "US") — 빈 문자열이면 guard fail */
    val country: String,
    /** ISO 639-1 (예: "ko", "en") */
    val language: String,
    /** IANA timezone (예: "Asia/Seoul") */
    val timezone: String,
    /** PhoneNumberNormalizer 기본 region (예: "KR") */
    val numberRegion: String,
    /** 수집 시각 (epoch ms) */
    val generatedAt: Long,
)
