package app.myphonecheck.mobile.feature.callscreening.di

import app.myphonecheck.mobile.core.globalengine.decision.BlockListRepository
import app.myphonecheck.mobile.feature.callscreening.repository.RoomBlockListRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Real-time Action 바인딩 (Architecture v2.1.0 §31).
 *
 * BlockListRepository → RoomBlockListRepository (Room v15 blocked_identifier).
 * TagRepository 바인딩은 PR #27 시점 NoopTagRepository → Stage 2-007(PR #28)에서
 * :feature:tag-system/TagSystemModule의 RoomTagRepository로 이전 (Hilt 단일 @Binds 정합).
 *
 * 의존 방향: :feature:call-screening → :data:local-cache + :core:global-engine.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class CallScreeningModule {

    @Binds
    @Singleton
    abstract fun bindBlockListRepository(impl: RoomBlockListRepository): BlockListRepository
}
