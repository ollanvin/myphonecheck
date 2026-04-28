package app.myphonecheck.mobile.data.localcache.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import app.myphonecheck.mobile.core.security.DatabaseKeyProvider
import app.myphonecheck.mobile.data.localcache.dao.BackupMetadataDao
import app.myphonecheck.mobile.data.localcache.dao.BlockedAppDao
import app.myphonecheck.mobile.data.localcache.dao.BlockedChannelDao
import app.myphonecheck.mobile.data.localcache.dao.CardSourceLabelDao
import app.myphonecheck.mobile.data.localcache.dao.CardTransactionDao
import app.myphonecheck.mobile.data.localcache.dao.DetailTagDao
import app.myphonecheck.mobile.data.localcache.dao.MessageHubDao
import app.myphonecheck.mobile.data.localcache.dao.InitialScanMetaDao
import app.myphonecheck.mobile.data.localcache.dao.NumberProfileDao
import app.myphonecheck.mobile.data.localcache.dao.PreJudgeCacheDao
import app.myphonecheck.mobile.data.localcache.dao.PrivacyHistoryDao
import app.myphonecheck.mobile.data.localcache.dao.PushNotificationObservationDao
import app.myphonecheck.mobile.data.localcache.dao.PushStatsDao
import app.myphonecheck.mobile.data.localcache.dao.SensorScanResultDao
import app.myphonecheck.mobile.data.localcache.dao.TrashedNotificationDao
import app.myphonecheck.mobile.data.localcache.dao.UserCallRecordDao
import app.myphonecheck.mobile.data.localcache.dao.BlockedIdentifierDao
import app.myphonecheck.mobile.data.localcache.dao.CallBaseDao
import app.myphonecheck.mobile.data.localcache.dao.PackageBaseDao
import app.myphonecheck.mobile.data.localcache.dao.SimContextSnapshotDao
import app.myphonecheck.mobile.data.localcache.dao.SmsBaseDao
import app.myphonecheck.mobile.data.localcache.db.Migration12To13
import app.myphonecheck.mobile.data.localcache.db.Migration13To14
import app.myphonecheck.mobile.data.localcache.db.Migration14To15
import app.myphonecheck.mobile.data.localcache.db.MyPhoneCheckDatabase
import app.myphonecheck.mobile.data.localcache.repository.NumberProfileRepository
import app.myphonecheck.mobile.data.localcache.repository.PreJudgeCacheRepository
import app.myphonecheck.mobile.data.localcache.repository.UserCallRecordRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import javax.inject.Singleton

/**
 * 로컬 캐시 Hilt 모듈.
 *
 * Room Database + DAO + Repository 제공.
 * SQLCipher로 DB 전체 암호화 — Android Keystore 기반 키 관리.
 * 앱 전체 수명 동안 단일 인스턴스 보장 (Singleton).
 */
@Module
@InstallIn(SingletonComponent::class)
object LocalCacheModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        databaseKeyProvider: DatabaseKeyProvider,
    ): MyPhoneCheckDatabase {
        // sqlcipher-android 4.6.1 (공식 현행 라이브러리, 기존 android-database-sqlcipher는 deprecated).
        // Raw 32-byte 키를 SQLCipher raw-key format "x'<64 hex chars>'" 로 변환하여 전달.
        //
        // 이유 (SQLCipher 공식 권장):
        // 1) raw 바이너리 ByteArray 전달 시 내부 C-string 처리 과정에서 0x00 바이트 truncation
        //    가능성이 있으며, 32바이트 랜덤 키에서 0x00 포함 확률 ≈ 11.8%.
        // 2) "x'...'" 포맷은 PBKDF2를 우회하고 raw 256-bit AES 키를 그대로 사용.
        //    https://www.zetetic.net/sqlcipher/sqlcipher-api/#key
        // 3) clearPassphrase=false — Room connection pool이 동일 ByteArray를 여러
        //    connection에서 재사용하므로 zero-out 방지 필수.
        val rawKey = databaseKeyProvider.getOrCreateDatabaseKey()
        val hex = rawKey.joinToString("") { "%02x".format(it) }
        val passphrase = "x'$hex'".toByteArray(Charsets.UTF_8)
        Log.i(
            "LocalCacheModule",
            "SQLCipher(sqlcipher-android 4.6.1) raw-key: rawKeyLen=${rawKey.size}, " +
                "passphraseLen=${passphrase.size}, " +
                "prefix=${String(passphrase.copyOfRange(0, 4), Charsets.UTF_8)}, " +
                "suffix=${String(passphrase.copyOfRange(passphrase.size - 2, passphrase.size), Charsets.UTF_8)}",
        )
        val factory = SupportOpenHelperFactory(passphrase, null, false)
        Log.i("LocalCacheModule", "SupportOpenHelperFactory created — clearPassphrase=false")

        return Room.databaseBuilder(
            context,
            MyPhoneCheckDatabase::class.java,
            MyPhoneCheckDatabase.DATABASE_NAME,
        )
            .openHelperFactory(factory)
            .addMigrations(Migration12To13, Migration13To14, Migration14To15)
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
    fun provideNumberProfileDao(
        database: MyPhoneCheckDatabase,
    ): NumberProfileDao {
        return database.numberProfileDao()
    }

    @Provides
    @Singleton
    fun provideDetailTagDao(
        database: MyPhoneCheckDatabase,
    ): DetailTagDao {
        return database.detailTagDao()
    }

    @Provides
    @Singleton
    fun provideNumberProfileRepository(
        numberProfileDao: NumberProfileDao,
        detailTagDao: DetailTagDao,
    ): NumberProfileRepository {
        return NumberProfileRepository(numberProfileDao, detailTagDao)
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
    fun providePushStatsDao(
        database: MyPhoneCheckDatabase,
    ): PushStatsDao {
        return database.pushStatsDao()
    }

    @Provides
    @Singleton
    fun provideSensorScanResultDao(
        database: MyPhoneCheckDatabase,
    ): SensorScanResultDao {
        return database.sensorScanResultDao()
    }

    @Provides
    @Singleton
    fun provideInitialScanMetaDao(
        database: MyPhoneCheckDatabase,
    ): InitialScanMetaDao {
        return database.initialScanMetaDao()
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

    @Provides
    @Singleton
    fun provideBlockedChannelDao(database: MyPhoneCheckDatabase): BlockedChannelDao =
        database.blockedChannelDao()

    @Provides
    @Singleton
    fun provideBlockedAppDao(database: MyPhoneCheckDatabase): BlockedAppDao =
        database.blockedAppDao()

    @Provides
    @Singleton
    fun provideTrashedNotificationDao(database: MyPhoneCheckDatabase): TrashedNotificationDao =
        database.trashedNotificationDao()

    @Provides
    @Singleton
    fun providePushNotificationObservationDao(
        database: MyPhoneCheckDatabase,
    ): PushNotificationObservationDao = database.pushNotificationObservationDao()

    // CardCheck (Stage 1-002, Architecture v1.9.0 §27)

    @Provides
    @Singleton
    fun provideCardTransactionDao(
        database: MyPhoneCheckDatabase,
    ): CardTransactionDao = database.cardTransactionDao()

    @Provides
    @Singleton
    fun provideCardSourceLabelDao(
        database: MyPhoneCheckDatabase,
    ): CardSourceLabelDao = database.cardSourceLabelDao()

    // Initial Scan 베이스 (Architecture v2.0.0 §28, Room v14)

    @Provides
    @Singleton
    fun provideCallBaseDao(database: MyPhoneCheckDatabase): CallBaseDao =
        database.callBaseDao()

    @Provides
    @Singleton
    fun provideSmsBaseDao(database: MyPhoneCheckDatabase): SmsBaseDao =
        database.smsBaseDao()

    @Provides
    @Singleton
    fun providePackageBaseDao(database: MyPhoneCheckDatabase): PackageBaseDao =
        database.packageBaseDao()

    @Provides
    @Singleton
    fun provideSimContextSnapshotDao(database: MyPhoneCheckDatabase): SimContextSnapshotDao =
        database.simContextSnapshotDao()

    // Real-time Action (Architecture v2.1.0 §31, Room v15)

    @Provides
    @Singleton
    fun provideBlockedIdentifierDao(database: MyPhoneCheckDatabase): BlockedIdentifierDao =
        database.blockedIdentifierDao()
}
