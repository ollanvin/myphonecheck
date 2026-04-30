package app.myphonecheck.mobile.feature.countryconfig

import android.content.Context

/**
 * 프라이버시 신뢰 메시지 — 영문 단일 (헌법 §9-1).
 *
 * 사용 위치 3곳:
 *  1. 온보딩 (앱 최초 실행)
 *  2. 설정 화면 (프라이버시 섹션)
 *  3. 결제 직전 (구독 확인 화면)
 *
 * R.string lookup / values-{locale} 영구 금지.
 * Context 파라미터는 호출자 시그니처 보존을 위해 유지하되 사용하지 않는다.
 */
@Suppress("UNUSED_PARAMETER")
class PrivacyTrustMessages(context: Context) {

    // ── 온보딩 ──
    val onboardingTagline: String = "Privacy first. Verdicts on-device."
    val onboardingPrivacyCore: String = "Your data never leaves the device."
    val onboardingPrivacyDetail: String = "MyPhoneCheck never sends your contacts, calls, or messages to any server."
    val onboardingNoServerPledge: String = "No servers. No accounts. No tracking."

    // ── 설정 화면 ──
    val settingsPrivacyTitle: String = "Privacy"
    val settingsPrivacyDescription: String = "All decisions are made on your device. Nothing is uploaded."
    val settingsDataHandling: String = "We do not collect, store, or transmit any personal data."

    // ── 결제 직전 ──
    val purchasePrivacyGuarantee: String = "Premium keeps the same privacy guarantee — everything stays on your device."
    val purchaseValueProposition: String = "Calls, notifications, messages, and privacy — one app judges all four threats."
}
