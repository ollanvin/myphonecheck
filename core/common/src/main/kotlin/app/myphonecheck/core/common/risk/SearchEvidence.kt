package app.myphonecheck.core.common.risk

/**
 * Summarized evidence from three-layer sourcing for UI. Do not persist raw URLs in NKB.
 */
data class SearchEvidence(
    val source: Layer,
    val summary: String,
    val timestamp: Long,
) {
    enum class Layer { L1_NKB, L2_SEARCH, L3_PUBLIC_DB }
}
