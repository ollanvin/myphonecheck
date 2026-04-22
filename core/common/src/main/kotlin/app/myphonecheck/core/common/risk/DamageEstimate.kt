package app.myphonecheck.core.common.risk

/**
 * Estimated damage in local currency. No central server lookup (constitution art.3).
 */
data class DamageEstimate(
    val amountLocal: Long,
    val currencyCode: String,
    val confidence: Float,
) {
    init {
        require(amountLocal >= 0) { "amount must be >= 0: $amountLocal" }
        require(currencyCode.length == 3) {
            "ISO 4217 currency must be 3 chars: $currencyCode"
        }
        require(confidence in 0f..1f) {
            "confidence must be 0..1: $confidence"
        }
    }

    companion object {
        val UNKNOWN = DamageEstimate(0L, "XXX", 0.0f)
    }
}
