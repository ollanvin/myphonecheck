package app.myphonecheck.mobile.feature.settings.v2.repository

import app.myphonecheck.mobile.core.globalengine.search.publicfeed.FeedOptInProvider
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * UserPreferenceRepository의 publicFeedOptInFlow를 코어 FeedOptInProvider 인터페이스에 어댑팅.
 * Architecture v2.1.0 §30-4 Layer 3 — 코어 ↔ Settings 계층 분리.
 */
@Singleton
class PreferenceFeedOptInProvider @Inject constructor(
    private val userPrefs: UserPreferenceRepository,
) : FeedOptInProvider {
    override suspend fun optedInIds(): Set<String> = userPrefs.publicFeedOptInFlow.first()
}
