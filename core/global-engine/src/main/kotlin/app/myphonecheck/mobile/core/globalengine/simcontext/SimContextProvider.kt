package app.myphonecheck.mobile.core.globalengine.simcontext

import android.content.Context
import android.telephony.TelephonyManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

/**
 * SIM 정보 → SimContext 변환 (Architecture v2.0.0 §29 + 헌법 §8조).
 *
 * 헌법 §8조 핵심:
 *  - SIM이 단일 진실원
 *  - Locale.getDefault() 직접 사용 금지 (UI 언어 resolve 영역 외)
 *  - 모든 국가·통화·전화번호 양식이 본 클래스 출력 SimContext 기반
 *
 * SIM 부재 시 fallback (WiFi-only 태블릿) — 디바이스 시스템 Locale country.
 */
@Singleton
class SimContextProvider @Inject constructor(
    @ApplicationContext private val appContext: Context,
) {

    fun resolve(): SimContext {
        val telephony = appContext.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
            ?: return fallbackFromSystem()

        val simCountryIso = telephony.simCountryIso?.uppercase()?.takeIf { it.isNotEmpty() }
        val simOperator = telephony.simOperator?.takeIf { it.isNotEmpty() && it.length >= 4 }
        val operatorName = telephony.simOperatorName ?: ""

        // SIM 부재 fallback (헌법 §8-5)
        if (simCountryIso == null || simOperator == null) {
            return fallbackFromSystem()
        }

        val mcc = simOperator.substring(0, 3)
        val mnc = simOperator.substring(3)
        val currency = CountryCurrencyMapper.resolve(simCountryIso)

        return SimContext(
            mcc = mcc,
            mnc = mnc,
            countryIso = simCountryIso,
            operatorName = operatorName,
            currency = currency,
            phoneRegion = simCountryIso,
            timezone = TimeZone.getDefault(),  // SIM에서 timezone 추출 어려우므로 디바이스 그대로
        )
    }

    private fun fallbackFromSystem(): SimContext {
        // 헌법 §8-5: SIM 부재 시 디바이스 시스템 Locale country fallback (사용자 명시 필수)
        val systemCountry = Locale.getDefault().country
        return SimContext.fallback(systemCountry)
    }
}
