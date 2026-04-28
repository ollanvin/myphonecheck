package app.myphonecheck.mobile.core.globalengine.di

import app.myphonecheck.mobile.core.globalengine.search.internal.HistoryRepository
import app.myphonecheck.mobile.core.globalengine.search.internal.NoopHistoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * :core:global-engine Hilt 모듈 (Architecture v2.1.0 §30).
 *
 * 대부분 클래스는 @Singleton + @Inject constructor 패턴 — 명시적 @Provides 불필요:
 *  - parsing/currency/, parsing/phone/, parsing/message/, parsing/notification/ 모두 자동 디스커버리.
 *  - simcontext/SimContextProvider, SimChangeDetector 자동 디스커버리.
 *  - decision/RealTimeActionEngine, search/internal/OnDeviceHistorySearch 등.
 *  - search/publicfeed/FeedRegistry, PublicFeedAggregator, PublicFeedCache 자동 디스커버리.
 *
 * 명시 바인딩:
 *  - HistoryRepository → NoopHistoryRepository (후속 PR에서 :data:local-cache 실 구현으로 교체).
 *
 * 변경 이력:
 *  - Stage 2-005 (PR #22): PublicFeedSource interface + emptyList @Provides.
 *  - Stage 2-008 (PR #29): PublicFeedSource → data class + FeedRegistry. emptyList @Provides 제거.
 *    FeedOptInProvider 바인딩은 :feature:settings/SettingsV2Module의 PreferenceFeedOptInProvider 단일 @Binds (Hilt 단일 @Binds 정합).
 *    NoopFeedOptInProvider는 코어 클래스로만 남고 default 바인딩 안 함.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class GlobalEngineModule {

    @Binds
    @Singleton
    abstract fun bindHistoryRepository(impl: NoopHistoryRepository): HistoryRepository
}
