package app.callcheck.mobile.feature.countryconfig

import android.content.Context
import app.callcheck.mobile.R

/**
 * 프라이버시 신뢰 메시지 — Android 리소스 기반.
 * 디바이스 Locale을 자동 추종한다.
 *
 * 사용 위치 3곳:
 *  1. 온보딩 (앱 최초 실행)
 *  2. 설정 화면 (프라이버시 섹션)
 *  3. 결제 직전 (구독 확인 화면)
 */
class PrivacyTrustMessages(private val context: Context) {

    // ── 온보딩 ──
    val onboardingTagline: String get() = context.getString(R.string.privacy_onboarding_tagline)
    val onboardingPrivacyCore: String get() = context.getString(R.string.privacy_onboarding_core)
    val onboardingPrivacyDetail: String get() = context.getString(R.string.privacy_onboarding_detail)
    val onboardingNoServerPledge: String get() = context.getString(R.string.privacy_onboarding_no_server_pledge)

    // ── 설정 화면 ──
    val settingsPrivacyTitle: String get() = context.getString(R.string.privacy_settings_title)
    val settingsPrivacyDescription: String get() = context.getString(R.string.privacy_settings_description)
    val settingsDataHandling: String get() = context.getString(R.string.privacy_settings_data_handling)

    // ── 결제 직전 ──
    val purchasePrivacyGuarantee: String get() = context.getString(R.string.privacy_purchase_guarantee)
    val purchaseValueProposition: String get() = context.getString(R.string.privacy_purchase_value_proposition)
}
