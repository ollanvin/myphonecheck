package app.myphonecheck.mobile.feature.initialscan.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * InitialScan Hilt 모듈 — 모든 클래스 @Singleton @Inject constructor 자동 디스커버리.
 */
@Module
@InstallIn(SingletonComponent::class)
object InitialScanModule
