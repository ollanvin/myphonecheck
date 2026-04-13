package app.myphonecheck.mobile.core.security

import android.content.Context
import app.myphonecheck.mobile.core.security.tamper.HookDetector
import app.myphonecheck.mobile.core.security.tamper.RepackageDetector
import app.myphonecheck.mobile.core.security.tamper.RootDetector
import app.myphonecheck.mobile.core.security.tamper.TamperChecker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 올랑방 앱팩토리 보안 Hilt 모듈.
 *
 * DatabaseKeyProvider + TamperChecker를 전체 앱에 제공.
 * CardSpend, ItsallDone, MeetLog, BlackDiary에 그대로 이식 가능.
 */
@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {

    @Provides
    @Singleton
    fun provideDatabaseKeyProvider(
        @ApplicationContext context: Context,
    ): DatabaseKeyProvider {
        return DatabaseKeyProvider(context)
    }

    @Provides
    @Singleton
    fun provideRootDetector(): RootDetector {
        return RootDetector()
    }

    @Provides
    @Singleton
    fun provideHookDetector(): HookDetector {
        return HookDetector()
    }

    @Provides
    @Singleton
    fun provideRepackageDetector(
        @ApplicationContext context: Context,
    ): RepackageDetector {
        return RepackageDetector(context)
    }

    @Provides
    @Singleton
    fun provideTamperChecker(
        rootDetector: RootDetector,
        hookDetector: HookDetector,
        repackageDetector: RepackageDetector,
    ): TamperChecker {
        return TamperChecker(rootDetector, hookDetector, repackageDetector)
    }
}
