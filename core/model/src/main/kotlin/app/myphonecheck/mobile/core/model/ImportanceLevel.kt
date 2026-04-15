package app.myphonecheck.mobile.core.model

/**
 * Importance axis for identifier decisions.
 *
 * This is independent from risk:
 * - risk: how dangerous
 * - importance: how much user should not miss the interaction
 */
enum class ImportanceLevel {
    UNKNOWN,
    NORMAL,
    IMPORTANT,
    DO_NOT_MISS,
}
