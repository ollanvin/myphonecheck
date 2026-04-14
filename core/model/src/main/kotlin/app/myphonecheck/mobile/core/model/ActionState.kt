package app.myphonecheck.mobile.core.model

/**
 * Persistent user action memory for a number profile.
 *
 * This state is reused on the next call or SMS and remains separate from search evidence.
 */
enum class ActionState {
    NONE,
    BLOCKED,
    DO_NOT_BLOCK,
}
