package app.myphonecheck.mobile.feature.billing

import app.myphonecheck.mobile.data.localcache.dao.MessageHubDao
import app.myphonecheck.mobile.data.localcache.dao.UserCallRecordDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubscriptionValueAnchorRepository @Inject constructor(
    private val messageHubDao: MessageHubDao,
    private val userCallRecordDao: UserCallRecordDao,
) {
    suspend fun load(
        nowMillis: Long = System.currentTimeMillis(),
    ): SubscriptionValueAnchorState? {
        for (windowDays in SubscriptionValueAnchorState.WINDOW_DAYS_LADDER) {
            val sinceMillis = SubscriptionValueAnchorState.sinceMillisFor(windowDays, nowMillis)
            val state = SubscriptionValueAnchorState(
                blockedCalls = userCallRecordDao.countBlockedCallsSince(sinceMillis),
                riskyLinks = messageHubDao.countRiskyLinkMessagesSince(sinceMillis),
                windowDays = windowDays,
            )
            if (!state.shouldHide) return state
        }
        return null
    }
}
