package app.myphonecheck.mobile.feature.countryconfig

/**
 * SignalSummary 로컬라이저 — 영문 단일 (헌법 §9-1).
 *
 * 빅테크 정공법: values-{locale} / R.string lookup 영구 금지.
 * 모든 표시는 영문 hardcoded String 직접 반환.
 *
 * 데이터 흐름:
 * ```
 * SearchResultAnalyzer
 *   → intensity: "SAFE" | "REFERENCE" | "CAUTION_LIGHT" | ...
 *   → category: ConclusionCategory enum
 *        │
 *        ▼
 * SignalSummaryLocalizer.localize(intensity, category, entityName?)
 *   → 영문 단일 텍스트
 * ```
 */
class SignalSummaryLocalizer {

    /**
     * 위험도 수준(intensity)을 영문 텍스트로 반환한다.
     *
     * @param intensityKey INTENSITY_* 상수의 키 (예: "SAFE", "DANGER")
     * @return 영문 위험도 텍스트. 미지원 키 시 키 그대로 반환.
     */
    fun localizeIntensity(intensityKey: String): String {
        return when (intensityKey) {
            KEY_SAFE -> "Safe to Answer"
            KEY_REFERENCE -> "For Reference"
            KEY_CAUTION_LIGHT -> "Light Caution"
            KEY_CAUTION -> "Be Cautious"
            KEY_DANGER -> "High Risk"
            KEY_REJECT -> "Reject Recommended"
            KEY_VERIFY -> "Verify Recommended"
            else -> intensityKey
        }
    }

    /**
     * 카테고리별 문장을 영문 텍스트로 반환한다.
     *
     * @param categoryKey ConclusionCategory enum의 name (예: "SCAM_RISK_HIGH")
     * @param entityName 엔티티명 (검색에서 발견된 업체/기관명). null이면 일반형 사용.
     * @return 영문 문장. 미지원 키 시 키 그대로 반환.
     */
    fun localizeCategory(
        categoryKey: String,
        entityName: String? = null,
    ): String {
        val template = when (categoryKey) {
            "KNOWN_CONTACT" -> "{entity} Known Contact"
            "BUSINESS_LIKELY" -> "{entity} Business Call"
            "DELIVERY_LIKELY" -> "{entity} Delivery Call"
            "INSTITUTION_LIKELY" -> "{entity} Institution Call"
            "SALES_SPAM_SUSPECTED" -> "Suspected Spam/Sales"
            "SCAM_RISK_HIGH" -> "Scam/Phishing Risk"
            "INSUFFICIENT_EVIDENCE" -> "Insufficient Evidence"
            else -> return categoryKey
        }

        return if (entityName != null) {
            template.replace("{entity}", entityName)
        } else {
            template.replace("{entity} ", "").replace("{entity}", "")
        }
    }

    /**
     * 전체 SignalSummary 텍스트 생성. intensity + category 결합.
     *
     * @return "카테고리 — 위험도" 형태 영문 단일 문장.
     */
    fun localize(
        intensityKey: String,
        categoryKey: String,
        entityName: String? = null,
    ): String {
        val intensity = localizeIntensity(intensityKey)
        val category = localizeCategory(categoryKey, entityName)
        return "$category — $intensity"
    }

    companion object {

        const val KEY_SAFE = "SAFE"
        const val KEY_REFERENCE = "REFERENCE"
        const val KEY_CAUTION_LIGHT = "CAUTION_LIGHT"
        const val KEY_CAUTION = "CAUTION"
        const val KEY_DANGER = "DANGER"
        const val KEY_REJECT = "REJECT"
        const val KEY_VERIFY = "VERIFY"
    }
}
