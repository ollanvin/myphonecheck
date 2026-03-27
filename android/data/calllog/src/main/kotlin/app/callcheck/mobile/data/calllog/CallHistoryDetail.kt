package app.callcheck.mobile.data.calllog

data class CallHistoryDetail(
    // Call counts
    val outgoingCount: Int = 0,
    val incomingCount: Int = 0,
    val answeredCount: Int = 0,
    val rejectedCount: Int = 0,
    val missedCount: Int = 0,
    val connectedCount: Int = 0,
    // Duration metrics
    val totalDurationSec: Long = 0,
    val avgDurationSec: Long = 0,
    val shortCallCount: Int = 0,
    val longCallCount: Int = 0,
    // Timestamps
    val lastOutgoingAt: Long? = null,
    val lastIncomingAt: Long? = null,
    val lastConnectedAt: Long? = null,
    val lastRejectedAt: Long? = null,
    val lastMissedAt: Long? = null,
    // Recent contact info
    val recentDaysContact: Int? = null,
)
