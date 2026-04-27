package app.myphonecheck.mobile.feature.settings.v2.di

import app.myphonecheck.mobile.core.globalengine.simcontext.SimContextStorage
import app.myphonecheck.mobile.feature.settings.v2.repository.RoomSimContextStorage
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Settings v2 Hilt 모듈 (Architecture v2.0.0 §29).
 *
 * SimContextStorage interface(:core:global-engine)를 RoomSimContextStorage(:feature:settings)에 바인딩 —
 * 의존 방향: :feature:settings → :data:local-cache + :core:global-engine.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsV2Module {

    @Binds
    @Singleton
    abstract fun bindSimContextStorage(impl: RoomSimContextStorage): SimContextStorage
}
