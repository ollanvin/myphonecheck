package app.myphonecheck.mobile.feature.billing

import java.util.concurrent.TimeUnit

data class SubscriptionValueAnchorState(
    val blockedCalls: Int,
    val riskyLinks: Int,
    val windowDays: Int,
) {
    val shouldHide: Boolean
        get() = blockedCalls <= 0 && riskyLinks <= 0

    val metrics: List<ValueMetric>
        get() = buildList(capacity = 2) {
            if (blockedCalls > 0) add(ValueMetric.BlockedCalls(blockedCalls))
            if (riskyLinks > 0) add(ValueMetric.RiskyLinks(riskyLinks))
        }

    sealed interface ValueMetric {
        val count: Int

        data class BlockedCalls(override val count: Int) : ValueMetric
        data class RiskyLinks(override val count: Int) : ValueMetric
    }

    companion object {
        val WINDOW_DAYS_LADDER: List<Int> = listOf(7, 30, 90)

        fun sinceMillisFor(windowDays: Int, nowMillis: Long = System.currentTimeMillis()): Long =
            nowMillis - TimeUnit.DAYS.toMillis(windowDays.toLong())
    }
}
