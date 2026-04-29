package app.myphonecheck.mobile.data.localcache.db

import androidx.room.Database
import androidx.room.RoomDatabase
import app.myphonecheck.mobile.data.localcache.dao.BackupMetadataDao
import app.myphonecheck.mobile.data.localcache.dao.BlockedAppDao
import app.myphonecheck.mobile.data.localcache.dao.BlockedChannelDao
import app.myphonecheck.mobile.data.localcache.dao.BlockedIdentifierDao
import app.myphonecheck.mobile.data.localcache.dao.CallBaseDao
import app.myphonecheck.mobile.data.localcache.dao.CardSourceLabelDao
import app.myphonecheck.mobile.data.localcache.dao.CardTransactionDao
import app.myphonecheck.mobile.data.localcache.dao.DetailTagDao
import app.myphonecheck.mobile.data.localcache.dao.FeedEntryDao
import app.myphonecheck.mobile.data.localcache.dao.HubMessageDao
import app.myphonecheck.mobile.data.localcache.dao.InitialScanMetaDao
import app.myphonecheck.mobile.data.localcache.dao.MessageHubDao
import app.myphonecheck.mobile.data.localcache.dao.NumberProfileDao
import app.myphonecheck.mobile.data.localcache.dao.PackageBaseDao
import app.myphonecheck.mobile.data.localcache.dao.PhoneTagDao
import app.myphonecheck.mobile.data.localcache.dao.PreJudgeCacheDao
import app.myphonecheck.mobile.data.localcache.dao.PrivacyHistoryDao
import app.myphonecheck.mobile.data.localcache.dao.PushNotificationObservationDao
import app.myphonecheck.mobile.data.localcache.dao.PushStatsDao
import app.myphonecheck.mobile.data.localcache.dao.SensorScanResultDao
import app.myphonecheck.mobile.data.localcache.dao.SimContextSnapshotDao
import app.myphonecheck.mobile.data.localcache.dao.SmsBaseDao
import app.myphonecheck.mobile.data.localcache.dao.TrashedNotificationDao
import app.myphonecheck.mobile.data.localcache.dao.UserCallRecordDao
import app.myphonecheck.mobile.data.localcache.entity.BackupMetadataEntity
import app.myphonecheck.mobile.data.localcache.entity.BlockedAppEntity
import app.myphonecheck.mobile.data.localcache.entity.BlockedChannelEntity
import app.myphonecheck.mobile.data.localcache.entity.BlockedIdentifierEntity
import app.myphonecheck.mobile.data.localcache.entity.CallBaseEntity
import app.myphonecheck.mobile.data.localcache.entity.CardSourceLabelEntity
import app.myphonecheck.mobile.data.localcache.entity.CardTransactionEntity
import app.myphonecheck.mobile.data.localcache.entity.DetailTagEntity
import app.myphonecheck.mobile.data.localcache.entity.FeedEntryEntity
import app.myphonecheck.mobile.data.localcache.entity.HubMessageEntity
import app.myphonecheck.mobile.data.localcache.entity.InitialScanMetaEntity
import app.myphonecheck.mobile.data.localcache.entity.MessageHubEntity
import app.myphonecheck.mobile.data.localcache.entity.NumberProfileEntity
import app.myphonecheck.mobile.data.localcache.entity.PackageBaseEntity
import app.myphonecheck.mobile.data.localcache.entity.PhoneTagEntity
import app.myphonecheck.mobile.data.localcache.entity.PreJudgeCacheEntry
import app.myphonecheck.mobile.data.localcache.entity.PrivacyHistoryEntity
import app.myphonecheck.mobile.data.localcache.entity.PushNotificationObservationEntity
import app.myphonecheck.mobile.data.localcache.entity.PushStatsEntity
import app.myphonecheck.mobile.data.localcache.entity.SensorScanResultEntity
import app.myphonecheck.mobile.data.localcache.entity.SimContextSnapshotEntity
import app.myphonecheck.mobile.data.localcache.entity.SmsBaseEntity
import app.myphonecheck.mobile.data.localcache.entity.TrashedNotificationEntity
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
 *  - BlockedChannelEntity / BlockedAppEntity / TrashedNotificationEntity / PushNotificationObservationEntity: 푸시 휴지통(Stage 1)
 *  - CardTransactionEntity / CardSourceLabelEntity: CardCheck Stage 1-002 (글로벌 파싱 엔진)
 *
 * 서버 동기화: 없음 (온디바이스 전용)
 *
 * 버전 이력:
 *  - v12: 푸시 휴지통 4 entity (Stage 1-001)
 *  - v13 (2026-04-27): CardTransaction + CardSourceLabel 추가 (Stage 1-002, Architecture v1.9.0 §27)
 *  - v14: Initial Scan 베이스 4 entity 추가 (Architecture v2.0.0 §28).
 *          call_base_entry, sms_base_entry, package_base_entry, sim_context_snapshot.
 *  - v15: Real-time Action 차단 목록 1 entity 추가 (Architecture v2.1.0 §31).
 *          blocked_identifier (PHONE_E164 / SMS_SENDER / NOTIFICATION_PACKAGE).
 *  - v16: Tag System 휘발성 메모 1 entity 추가 (Architecture v2.1.0 §32).
 *          phone_tag (REMIND_ME / PENDING / SUSPICIOUS / ARCHIVE).
 *  - v17: 공개 피드 캐시 1 entity 추가 (Architecture v2.1.0 §30-4 Layer 3).
 *          feed_entry (4 FeedType 공통 캐시).
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
        BlockedChannelEntity::class,
        BlockedAppEntity::class,
        TrashedNotificationEntity::class,
        PushNotificationObservationEntity::class,
        CardTransactionEntity::class,
        CardSourceLabelEntity::class,
        CallBaseEntity::class,
        SmsBaseEntity::class,
        PackageBaseEntity::class,
        SimContextSnapshotEntity::class,
        BlockedIdentifierEntity::class,
        PhoneTagEntity::class,
        FeedEntryEntity::class,
        HubMessageEntity::class,
    ],
    version = 18,
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
    abstract fun blockedChannelDao(): BlockedChannelDao
    abstract fun blockedAppDao(): BlockedAppDao
    abstract fun trashedNotificationDao(): TrashedNotificationDao
    abstract fun pushNotificationObservationDao(): PushNotificationObservationDao
    abstract fun cardTransactionDao(): CardTransactionDao
    abstract fun cardSourceLabelDao(): CardSourceLabelDao
    abstract fun callBaseDao(): CallBaseDao
    abstract fun smsBaseDao(): SmsBaseDao
    abstract fun packageBaseDao(): PackageBaseDao
    abstract fun simContextSnapshotDao(): SimContextSnapshotDao
    abstract fun blockedIdentifierDao(): BlockedIdentifierDao
    abstract fun phoneTagDao(): PhoneTagDao
    abstract fun feedEntryDao(): FeedEntryDao
    abstract fun hubMessageDao(): HubMessageDao

    companion object {
        const val DATABASE_NAME = "myphonecheck.db"
    }
}
