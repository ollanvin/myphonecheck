package app.myphonecheck.mobile.core.globalengine.search.publicfeed

import javax.inject.Inject
import javax.inject.Singleton

/**
 * 공개 피드 옵트인 출처 ID 조회 (Architecture v2.1.0 §30 Layer 3).
 *
 * 본 인터페이스는 코어가 사용자 선호 저장소(:feature:settings DataStore)에 직접 의존하지 않게 추상화.
 * 실제 구현은 :feature:settings v2 (PreferenceFeedOptInProvider, UserPreferenceRepository wrapper).
 *
 * 헌법 §3 결정 중앙집중 금지: 옵트인 결정은 사용자.
 */
interface FeedOptInProvider {
    suspend fun optedInIds(): Set<String>
    suspend fun isOptedIn(sourceId: String): Boolean = sourceId in optedInIds()
}

/**
 * NoOp 기본 — 옵트인 0. :feature:settings 모듈 활성 시 PreferenceFeedOptInProvider로 override.
 */
@Singleton
class NoopFeedOptInProvider @Inject constructor() : FeedOptInProvider {
    override suspend fun optedInIds(): Set<String> = emptySet()
}
