package app.myphonecheck.mobile.feature.tagsystem.di

import app.myphonecheck.mobile.core.globalengine.decision.TagRepository
import app.myphonecheck.mobile.feature.tagsystem.repository.RoomTagRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Tag System Hilt 모듈 (Architecture v2.1.0 §32).
 *
 * TagRepository interface(:core:global-engine)를 RoomTagRepository(:feature:tag-system)에 바인딩.
 * 본 모듈 등록 시 PR #27 CallScreeningModule의 NoopTagRepository @Binds는 제거되어야 함 (Hilt 충돌 회피).
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class TagSystemModule {

    @Binds
    @Singleton
    abstract fun bindTagRepository(impl: RoomTagRepository): TagRepository
}
