package app.myphonecheck.mobile.core.model

enum class ProductAccessTier {
    FREE,
    PREMIUM,
}

object ProductStageFlags {
    val BASIC_RISK_DISPLAY = ProductAccessTier.FREE
    val SEARCH_STATUS = ProductAccessTier.FREE
    val SIMPLE_LABEL = ProductAccessTier.FREE
    val RELATIONSHIP_MEMORY = ProductAccessTier.PREMIUM
    val ADVANCED_LABEL_TAG_COMBINATIONS = ProductAccessTier.PREMIUM
    val INTERACTION_HISTORY_REUSE = ProductAccessTier.PREMIUM
    val DO_NOT_MISS_BEHAVIOR = ProductAccessTier.PREMIUM
}

fun ActionState.displayLabelKo(): String? = when (this) {
    ActionState.NONE -> null
    ActionState.BLOCKED -> "\uCC28\uB2E8 \uC0C1\uD0DC"
    ActionState.DO_NOT_BLOCK -> "\uCC28\uB2E8 \uAE08\uC9C0"
}
