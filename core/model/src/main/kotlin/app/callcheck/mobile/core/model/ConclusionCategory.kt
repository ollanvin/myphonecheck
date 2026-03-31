package app.callcheck.mobile.core.model

enum class ConclusionCategory(
    val summaryEn: String,
    val summaryKo: String,
) {
    KNOWN_CONTACT(
        summaryEn = "Known contact",
        summaryKo = "저장된 연락처",
    ),
    BUSINESS_LIKELY(
        summaryEn = "Business contact likely",
        summaryKo = "거래처/업무 번호 가능성 높음",
    ),
    DELIVERY_LIKELY(
        summaryEn = "Delivery/courier likely",
        summaryKo = "택배/배송 가능성 높음",
    ),
    INSTITUTION_LIKELY(
        summaryEn = "Institution likely",
        summaryKo = "기관/병원 가능성 높음",
    ),
    SALES_SPAM_SUSPECTED(
        summaryEn = "Sales/advertising suspected",
        summaryKo = "광고/영업 의심",
    ),
    SCAM_RISK_HIGH(
        summaryEn = "Scam/phishing risk high",
        summaryKo = "스팸/사기 위험 높음",
    ),
    INSUFFICIENT_EVIDENCE(
        summaryEn = "Insufficient evidence",
        summaryKo = "판단 근거 부족",
    ),
}
