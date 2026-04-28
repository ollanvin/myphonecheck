package app.myphonecheck.mobile.feature.callscreening.di

import app.myphonecheck.mobile.core.globalengine.decision.BlockListRepository
import app.myphonecheck.mobile.core.globalengine.decision.NoopTagRepository
import app.myphonecheck.mobile.core.globalengine.decision.TagRepository
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
 * TagRepository → NoopTagRepository (Tag System §32 코드 구현은 후속 PR, 본 PR은 NoOp).
 *
 * 의존 방향: :feature:call-screening → :data:local-cache + :core:global-engine.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class CallScreeningModule {

    @Binds
    @Singleton
    abstract fun bindBlockListRepository(impl: RoomBlockListRepository): BlockListRepository

    @Binds
    @Singleton
    abstract fun bindTagRepository(impl: NoopTagRepository): TagRepository
}
