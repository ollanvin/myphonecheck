package app.callcheck.mobile.core.model

/**
 * On-device evidence gathered from contacts, call log, and SMS metadata.
 *
 * CRITICAL: Every interaction type MUST remain separate.
 * Do NOT collapse into a single "callFrequency" or "totalCount".
 * The decision engine relies on granular breakdown to distinguish
 * business contacts from spam.
 */
data class DeviceEvidence(
    // Contact status
    val isSavedContact: Boolean,
    val contactName: String?,

    // Outgoing (user-initiated) calls
    val outgoingCount: Int,
    val lastOutgoingAt: Long?,

    // Incoming (other-party-initiated) calls
    val incomingCount: Int,
    val lastIncomingAt: Long?,

    // Answered incoming calls (user picked up)
    val answeredCount: Int,

    // Rejected calls (user explicitly declined)
    val rejectedCount: Int,
    val lastRejectedAt: Long?,

    // Missed calls (rang but user didn't answer)
    val missedCount: Int,
    val lastMissedAt: Long?,

    // Successfully connected calls (duration > 0)
    val connectedCount: Int,
    val lastConnectedAt: Long?,

    // Duration metrics (seconds)
    val totalDurationSec: Long,
    val avgDurationSec: Long,
    val shortCallCount: Int,   // duration < 10s
    val longCallCount: Int,    // duration > 60s

    // Recent activity
    val recentDaysContact: Int?, // days since last any interaction

    // SMS metadata
    val smsExists: Boolean,
    val smsLastAt: Long?,

    // User-applied labels
    val localTag: String?,
    val localMemo: String?,
) {
    companion object {
        fun empty() = DeviceEvidence(
            isSavedContact = false,
            contactName = null,
            outgoingCount = 0,
            lastOutgoingAt = null,
            incomingCount = 0,
            lastIncomingAt = null,
            answeredCount = 0,
            rejectedCount = 0,
            lastRejectedAt = null,
            missedCount = 0,
            lastMissedAt = null,
            connectedCount = 0,
            lastConnectedAt = null,
            totalDurationSec = 0,
            avgDurationSec = 0,
            shortCallCount = 0,
            longCallCount = 0,
            recentDaysContact = null,
            smsExists = false,
            smsLastAt = null,
            localTag = null,
            localMemo = null,
        )
    }

    /** True if any on-device history exists at all */
    val hasAnyHistory: Boolean
        get() = outgoingCount > 0 || incomingCount > 0 || missedCount > 0 ||
                rejectedCount > 0 || smsExists

    /** True if user initiated contact (strong trust signal) */
    val userInitiated: Boolean
        get() = outgoingCount > 0

    /** True if meaningful conversations happened */
    val hasMeaningfulCalls: Boolean
        get() = longCallCount > 0
}
