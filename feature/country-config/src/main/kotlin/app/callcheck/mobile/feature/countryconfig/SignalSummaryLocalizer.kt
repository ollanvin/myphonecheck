package app.callcheck.mobile.feature.countryconfig

import android.content.Context
import app.callcheck.mobile.feature.countryconfig.R

/**
 * SignalSummary 로컬라이저 — Android 리소스 기반 다국어 지원.
 *
 * ┌──────────────────────────────────────────────────────────────┐
 * │ 핵심 원칙                                                     │
 * ├──────────────────────────────────────────────────────────────┤
 * │ 1. SearchResultAnalyzer는 언어 중립을 유지한다               │
 * │    → intensity 상수 + category enum을 반환                    │
 * │ 2. SignalSummaryLocalizer는 그 결과를 받아                    │
 * │    → Android 시스템 로케일에 맞는 문자열 리소스 로드         │
 * │ 3. "번역"이 아니라 "리소스 선택" — 각 언어는 자체 values 폴더│
 * │ 4. 새 언어 추가 = values-xx/ 폴더 추가 (기존 코드 수정 없음)│
 * └──────────────────────────────────────────────────────────────┘
 *
 * 데이터 흐름:
 * ```
 * SearchResultAnalyzer
 *   → intensity: "SAFE" | "REFERENCE" | "CAUTION_LIGHT" | ...
 *   → category: ConclusionCategory enum
 *        │
 *        ▼
 * SignalSummaryLocalizer.localize(intensity, category, context, entityName?)
 *   → Android 로케일 기반 로컬라이즈 텍스트
 * ```
 */
class SignalSummaryLocalizer {

    /**
     * 위험도 수준(intensity)을 Android 로케일에 맞게 로컬라이즈한다.
     *
     * @param intensityKey INTENSITY_* 상수의 키 (예: "SAFE", "DANGER")
     * @param context Android Context (문자열 리소스 로드용)
     * @return 로컬라이즈된 위험도 텍스트. 리소스 미존재 시 키 반환.
     */
    fun localizeIntensity(
        intensityKey: String,
        context: Context,
    ): String {
        return when (intensityKey) {
            KEY_SAFE -> context.getString(R.string.signal_intensity_safe)
            KEY_REFERENCE -> context.getString(R.string.signal_intensity_reference)
            KEY_CAUTION_LIGHT -> context.getString(R.string.signal_intensity_caution_light)
            KEY_CAUTION -> context.getString(R.string.signal_intensity_caution)
            KEY_DANGER -> context.getString(R.string.signal_intensity_danger)
            KEY_REJECT -> context.getString(R.string.signal_intensity_reject)
            KEY_VERIFY -> context.getString(R.string.signal_intensity_verify)
            else -> intensityKey
        }
    }

    /**
     * 카테고리별 문장을 Android 로케일에 맞게 로컬라이즈한다.
     *
     * @param categoryKey ConclusionCategory enum의 name (예: "SCAM_RISK_HIGH")
     * @param context Android Context (문자열 리소스 로드용)
     * @param entityName 엔티티명 (검색에서 발견된 업체/기관명). null이면 일반형 사용.
     * @return 로컬라이즈된 문장. 리소스 미존재 시 키 반환.
     */
    fun localizeCategory(
        categoryKey: String,
        context: Context,
        entityName: String? = null,
    ): String {
        val template = when (categoryKey) {
            "KNOWN_CONTACT" -> context.getString(R.string.signal_category_known_contact)
            "BUSINESS_LIKELY" -> context.getString(R.string.signal_category_business_likely)
            "DELIVERY_LIKELY" -> context.getString(R.string.signal_category_delivery_likely)
            "INSTITUTION_LIKELY" -> context.getString(R.string.signal_category_institution_likely)
            "SALES_SPAM_SUSPECTED" -> context.getString(R.string.signal_category_sales_spam_suspected)
            "SCAM_RISK_HIGH" -> context.getString(R.string.signal_category_scam_risk_high)
            "INSUFFICIENT_EVIDENCE" -> context.getString(R.string.signal_category_insufficient_evidence)
            else -> return categoryKey
        }

        return if (entityName != null) {
            template.replace("{entity}", entityName)
        } else {
            template.replace("{entity} ", "").replace("{entity}", "")
        }
    }

    /**
     * 전체 SignalSummary 텍스트를 로컬라이즈한다.
     * intensity + category를 조합하여 최종 사용자 대면 텍스트를 생성.
     *
     * @param intensityKey INTENSITY_* 상수 키
     * @param categoryKey ConclusionCategory enum name
     * @param context Android Context (문자열 리소스 로드용)
     * @param entityName 엔티티명 (선택)
     * @return "위험도 — 카테고리 설명" 형태의 로컬라이즈된 문장
     */
    fun localize(
        intensityKey: String,
        categoryKey: String,
        context: Context,
        entityName: String? = null,
    ): String {
        val intensity = localizeIntensity(intensityKey, context)
        val category = localizeCategory(categoryKey, context, entityName)
        return "$category — $intensity"
    }

    companion object {

        // ═══════════════════════════════════════════════════════
        // Intensity 키 상수 (SearchResultAnalyzer와 동기화)
        // ═══════════════════════════════════════════════════════

        const val KEY_SAFE = "SAFE"
        const val KEY_REFERENCE = "REFERENCE"
        const val KEY_CAUTION_LIGHT = "CAUTION_LIGHT"
        const val KEY_CAUTION = "CAUTION"
        const val KEY_DANGER = "DANGER"
        const val KEY_REJECT = "REJECT"
        const val KEY_VERIFY = "VERIFY"
    }
}
