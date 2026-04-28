package app.myphonecheck.mobile.feature.settings.v2.di

import app.myphonecheck.mobile.core.globalengine.search.publicfeed.FeedOptInProvider
import app.myphonecheck.mobile.core.globalengine.simcontext.SimContextStorage
import app.myphonecheck.mobile.feature.settings.v2.repository.PreferenceFeedOptInProvider
import app.myphonecheck.mobile.feature.settings.v2.repository.RoomSimContextStorage
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Settings v2 Hilt 모듈 (Architecture v2.0.0 §29 + v2.1.0 §30-4).
 *
 * 바인딩:
 *  - SimContextStorage interface → RoomSimContextStorage (Room v14 sim_context_snapshot).
 *  - FeedOptInProvider interface → PreferenceFeedOptInProvider (DataStore publicFeedOptInFlow wrapper).
 *
 * 의존 방향: :feature:settings → :data:local-cache + :core:global-engine.
 *
 * Hilt 단일 @Binds 정합: 코어 GlobalEngineModule에서 NoopFeedOptInProvider @Binds 두지 않고
 * 본 모듈에서만 바인딩 (Stage 2-008 PR #29).
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsV2Module {

    @Binds
    @Singleton
    abstract fun bindSimContextStorage(impl: RoomSimContextStorage): SimContextStorage

    @Binds
    @Singleton
    abstract fun bindFeedOptInProvider(impl: PreferenceFeedOptInProvider): FeedOptInProvider
}
