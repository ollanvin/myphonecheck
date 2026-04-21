package app.myphonecheck.mobile.data.localcache.db

import androidx.room.Database
import androidx.room.RoomDatabase
import app.myphonecheck.mobile.data.localcache.dao.BackupMetadataDao
import app.myphonecheck.mobile.data.localcache.dao.DetailTagDao
import app.myphonecheck.mobile.data.localcache.dao.InitialScanMetaDao
import app.myphonecheck.mobile.data.localcache.dao.MessageHubDao
import app.myphonecheck.mobile.data.localcache.dao.NumberProfileDao
import app.myphonecheck.mobile.data.localcache.dao.PreJudgeCacheDao
import app.myphonecheck.mobile.data.localcache.dao.PrivacyHistoryDao
import app.myphonecheck.mobile.data.localcache.dao.PushStatsDao
import app.myphonecheck.mobile.data.localcache.dao.SensorScanResultDao
import app.myphonecheck.mobile.data.localcache.dao.UserCallRecordDao
import app.myphonecheck.mobile.data.localcache.entity.BackupMetadataEntity
import app.myphonecheck.mobile.data.localcache.entity.DetailTagEntity
import app.myphonecheck.mobile.data.localcache.entity.InitialScanMetaEntity
import app.myphonecheck.mobile.data.localcache.entity.MessageHubEntity
import app.myphonecheck.mobile.data.localcache.entity.NumberProfileEntity
import app.myphonecheck.mobile.data.localcache.entity.PreJudgeCacheEntry
import app.myphonecheck.mobile.data.localcache.entity.PrivacyHistoryEntity
import app.myphonecheck.mobile.data.localcache.entity.PushStatsEntity
import app.myphonecheck.mobile.data.localcache.entity.SensorScanResultEntity
import app.myphonecheck.mobile.data.localcache.entity.UserCallRecord

/**
 * MyPhoneCheck 로컬 데이터베이스.
 *
 * 저장 대상:
 *  - UserCallRecord: 사용자 메모, 태그, 행동 기록 (영구 저장)
 *  - PreJudgeCacheEntry: Tier 0 사전 판단 영속 캐시 (0ms 판단용)
 *  - BackupMetadataEntity: 백업 이력 메타데이터
 *  - MessageHubEntity: MessageCheck 허브 메시지 기록
 *  - PrivacyHistoryEntity: PrivacyCheck 카메라/마이크 접근 히스토리
 *  - PushStatsEntity: PushCheck 앱별 일간 알림 통계
 *
 * 서버 동기화: 없음 (온디바이스 전용)
 */
@Database(
    entities = [
        UserCallRecord::class,
        PreJudgeCacheEntry::class,
        BackupMetadataEntity::class,
        NumberProfileEntity::class,
        DetailTagEntity::class,
        MessageHubEntity::class,
        PrivacyHistoryEntity::class,
        PushStatsEntity::class,
        SensorScanResultEntity::class,
        InitialScanMetaEntity::class,
    ],
    version = 11,
    exportSchema = true,
)
abstract class MyPhoneCheckDatabase : RoomDatabase() {
    abstract fun userCallRecordDao(): UserCallRecordDao
    abstract fun preJudgeCacheDao(): PreJudgeCacheDao
    abstract fun backupMetadataDao(): BackupMetadataDao
    abstract fun numberProfileDao(): NumberProfileDao
    abstract fun detailTagDao(): DetailTagDao
    abstract fun messageHubDao(): MessageHubDao
    abstract fun privacyHistoryDao(): PrivacyHistoryDao
    abstract fun pushStatsDao(): PushStatsDao
    abstract fun sensorScanResultDao(): SensorScanResultDao
    abstract fun initialScanMetaDao(): InitialScanMetaDao

    companion object {
        const val DATABASE_NAME = "myphonecheck.db"
    }
}
