package app.myphonecheck.mobile.data.localcache.di

import android.content.Context
import androidx.room.Room
import app.myphonecheck.mobile.data.localcache.dao.BackupMetadataDao
import app.myphonecheck.mobile.data.localcache.dao.MessageHubDao
import app.myphonecheck.mobile.data.localcache.dao.PreJudgeCacheDao
import app.myphonecheck.mobile.data.localcache.dao.PrivacyHistoryDao
import app.myphonecheck.mobile.data.localcache.dao.UserCallRecordDao
import app.myphonecheck.mobile.data.localcache.db.MyPhoneCheckDatabase
import app.myphonecheck.mobile.data.localcache.repository.PreJudgeCacheRepository
import app.myphonecheck.mobile.data.localcache.repository.UserCallRecordRepository
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
    ): MyPhoneCheckDatabase {
        return Room.databaseBuilder(
            context,
            MyPhoneCheckDatabase::class.java,
            MyPhoneCheckDatabase.DATABASE_NAME,
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun providePreJudgeCacheDao(
        database: MyPhoneCheckDatabase,
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
    fun provideBackupMetadataDao(
        database: MyPhoneCheckDatabase,
    ): BackupMetadataDao {
        return database.backupMetadataDao()
    }

    @Provides
    @Singleton
    fun provideMessageHubDao(
        database: MyPhoneCheckDatabase,
    ): MessageHubDao {
        return database.messageHubDao()
    }

    @Provides
    @Singleton
    fun providePrivacyHistoryDao(
        database: MyPhoneCheckDatabase,
    ): PrivacyHistoryDao {
        return database.privacyHistoryDao()
    }

    @Provides
    @Singleton
    fun provideUserCallRecordDao(
        database: MyPhoneCheckDatabase,
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
