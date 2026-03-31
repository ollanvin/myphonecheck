package app.callcheck.mobile.data.localcache.di

import android.content.Context
import androidx.room.Room
import app.callcheck.mobile.data.localcache.dao.PreJudgeCacheDao
import app.callcheck.mobile.data.localcache.dao.UserCallRecordDao
import app.callcheck.mobile.data.localcache.db.CallCheckDatabase
import app.callcheck.mobile.data.localcache.repository.PreJudgeCacheRepository
import app.callcheck.mobile.data.localcache.repository.UserCallRecordRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 로컬 캐시 Hilt 모듈.
 *
 * Room Database + DAO + Repository 제공.
 * 앱 전체 수명 동안 단일 인스턴스 보장 (Singleton).
 */
@Module
@InstallIn(SingletonComponent::class)
object LocalCacheModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): CallCheckDatabase {
        return Room.databaseBuilder(
            context,
            CallCheckDatabase::class.java,
            CallCheckDatabase.DATABASE_NAME,
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun providePreJudgeCacheDao(
        database: CallCheckDatabase,
    ): PreJudgeCacheDao {
        return database.preJudgeCacheDao()
    }

    @Provides
    @Singleton
    fun providePreJudgeCacheRepository(
        dao: PreJudgeCacheDao,
    ): PreJudgeCacheRepository {
        return PreJudgeCacheRepository(dao)
    }

    @Provides
    @Singleton
    fun provideUserCallRecordDao(
        database: CallCheckDatabase,
    ): UserCallRecordDao {
        return database.userCallRecordDao()
    }

    @Provides
    @Singleton
    fun provideUserCallRecordRepository(
        dao: UserCallRecordDao,
    ): UserCallRecordRepository {
        return UserCallRecordRepository(dao)
    }
}
