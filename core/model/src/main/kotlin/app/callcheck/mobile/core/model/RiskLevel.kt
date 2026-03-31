package app.callcheck.mobile.core.model

enum class RiskLevel(val displayNameEn: String, val displayNameKo: String) {
    HIGH("High Risk", "위험 높음"),
    MEDIUM("Caution", "주의"),
    LOW("Low Risk", "위험 낮음"),
    UNKNOWN("Unknown", "불명"),
}
