package app.myphonecheck.mobile.feature.billing

import app.myphonecheck.mobile.data.localcache.dao.MessageHubDao
import javax.inject.Inject
import javax.inject.Singleton
import java.util.concurrent.TimeUnit

@Singleton
class SubscriptionValueAnchorRepository @Inject constructor(
    private val messageHubDao: MessageHubDao,
) {
    suspend fun load(
        nowMillis: Long = System.currentTimeMillis(),
    ): SubscriptionValueAnchorState? {
        val candidates = listOf(
            PeriodCandidate("오늘", TimeUnit.DAYS.toMillis(1), nowMillis),
            PeriodCandidate("최근 7일", TimeUnit.DAYS.toMillis(7), nowMillis),
            PeriodCandidate("최근 30일", TimeUnit.DAYS.toMillis(30), nowMillis),
            PeriodCandidate("누계", null, nowMillis),
        )

        for (candidate in candidates) {
            val suspiciousCallsCount = candidate.countSuspiciousCalls()
            val riskyLinkMessagesCount = candidate.countRiskyLinkMessages()
            if ((suspiciousCallsCount ?: 0) > 0 || (riskyLinkMessagesCount ?: 0) > 0) {
                return SubscriptionValueAnchorState(
                    selectedPeriodLabel = candidate.label,
                    suspiciousCallsCount = suspiciousCallsCount,
                    riskyLinkMessagesCount = riskyLinkMessagesCount?.takeIf { it > 0 },
                    visible = true,
                )
            }
        }

        return null
    }

    suspend fun countSuspiciousCallsDaily(): Int? = null

    suspend fun countSuspiciousCallsWeekly(): Int? = null

    suspend fun countSuspiciousCallsMonthly(): Int? = null

    suspend fun countSuspiciousCallsCumulative(): Int? = null

    suspend fun countRiskyLinkMessagesDaily(nowMillis: Long = System.currentTimeMillis()): Int =
        messageHubDao.countRiskyLinkMessagesSince(nowMillis - TimeUnit.DAYS.toMillis(1))

    suspend fun countRiskyLinkMessagesWeekly(nowMillis: Long = System.currentTimeMillis()): Int =
        messageHubDao.countRiskyLinkMessagesSince(nowMillis - TimeUnit.DAYS.toMillis(7))

    suspend fun countRiskyLinkMessagesMonthly(nowMillis: Long = System.currentTimeMillis()): Int =
        messageHubDao.countRiskyLinkMessagesSince(nowMillis - TimeUnit.DAYS.toMillis(30))

    suspend fun countRiskyLinkMessagesCumulative(): Int =
        messageHubDao.countRiskyLinkMessagesCumulative()

    private inner class PeriodCandidate(
        val label: String,
        private val durationMillis: Long?,
        private val nowMillis: Long,
    ) {
        suspend fun countSuspiciousCalls(): Int? = when (label) {
            "오늘" -> countSuspiciousCallsDaily()
            "최근 7일" -> countSuspiciousCallsWeekly()
            "최근 30일" -> countSuspiciousCallsMonthly()
            else -> countSuspiciousCallsCumulative()
        }?.takeIf { it > 0 }

        suspend fun countRiskyLinkMessages(): Int? {
            val value = when (durationMillis) {
                null -> countRiskyLinkMessagesCumulative()
                else -> messageHubDao.countRiskyLinkMessagesSince(nowMillis - durationMillis)
            }
            return value.takeIf { it > 0 }
        }
    }
}
