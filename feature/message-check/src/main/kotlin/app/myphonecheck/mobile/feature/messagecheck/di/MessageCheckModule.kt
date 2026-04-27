package app.myphonecheck.mobile.feature.messagecheck.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * MessageCheck Hilt 모듈 (Architecture v2.0.0 §22).
 *
 * Repository/Service는 @Singleton @Inject constructor 자동 디스커버리 — provide 없음.
 */
@Module
@InstallIn(SingletonComponent::class)
object MessageCheckModule
