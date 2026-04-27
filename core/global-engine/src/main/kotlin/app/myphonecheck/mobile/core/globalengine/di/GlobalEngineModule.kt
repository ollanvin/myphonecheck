package app.myphonecheck.mobile.core.globalengine.di

import app.myphonecheck.mobile.core.globalengine.search.internal.HistoryRepository
import app.myphonecheck.mobile.core.globalengine.search.internal.NoopHistoryRepository
import app.myphonecheck.mobile.core.globalengine.search.publicfeed.PublicFeedSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * :core:global-engine Hilt 모듈 (Architecture v2.0.0 §30).
 *
 * 대부분 클래스는 @Singleton + @Inject constructor 패턴 — 명시적 @Provides 불필요:
 *  - parsing/currency/, parsing/phone/, parsing/message/, parsing/notification/ 모두 자동 디스커버리.
 *  - simcontext/SimContextProvider, SimChangeDetector 자동 디스커버리.
 *
 * 명시 바인딩 (Stage 2-005 신설):
 *  - HistoryRepository → NoopHistoryRepository (후속 PR에서 :data:local-cache 실 구현으로 교체).
 *  - PublicFeedSource 리스트 → emptyList (옵트인 출처는 후속 PR에서 추가).
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class GlobalEngineModule {

    @Binds
    @Singleton
    abstract fun bindHistoryRepository(impl: NoopHistoryRepository): HistoryRepository

    companion object {
        @Provides
        @Singleton
        fun providePublicFeedSources(): List<@JvmSuppressWildcards PublicFeedSource> = emptyList()
    }
}
